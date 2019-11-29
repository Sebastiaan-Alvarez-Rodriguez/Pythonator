package com.python.pythonator.ui.main;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.python.pythonator.R;
import com.python.pythonator.backend.bluetooth.BluetoothClient;
import com.python.pythonator.backend.bluetooth.ConnectListener;
import com.python.pythonator.backend.bluetooth.connector.BluetoothConnectState;
import com.python.pythonator.backend.bluetooth.sender.SendListener;
import com.python.pythonator.structures.queue.ImageQueueItem;
import com.python.pythonator.ui.camera.CameraHandler;
import com.python.pythonator.ui.edit.ImageEditHandler;
import com.python.pythonator.ui.main.adapter.QueueAdapter;
import com.python.pythonator.ui.main.adapter.QueueImageClickListener;
import com.python.pythonator.ui.settings.SettingsActivity;
import com.python.pythonator.ui.templates.adapter.listener.AdapterListener;
import com.python.pythonator.util.FileUtil;

import java.util.Collections;

public class MainActivity extends AppCompatActivity implements ConnectListener, AdapterListener, QueueImageClickListener {
    private static final int
            REQUEST_BLUETOOTH_PERMISSION = 0,
            REQUEST_CAMERA_PERMISSION = 1,
            REQUEST_GALLERY_PERMISSION = 2,
            REQUEST_IMAGE_GALLERY = 3;

    private View view;
    private Snackbar snackbar;

    private MenuItem bluetooth_search;

    private BluetoothClient client;

    private MainViewModel model;
    private QueueAdapter adapter;

    private RecyclerView queue_list;
    private FloatingActionButton queue_add, queue_camera, queue_gallery;

    private boolean add_menu_expanded = false;

    private CameraHandler camera_handler;
    private ImageEditHandler image_edit_handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        model = ViewModelProviders.of(this).get(MainViewModel.class);

        client = BluetoothClient.getClient(getApplicationContext());
        setContentView(R.layout.activity_main);
        findGlobalViews();
        setupButtons();
        setupList();
        setupActionBar();
        setupSnackBar();
        if (!checkPermissionBluetooth())
            askPermissionBluetooth();

        client.activateBluetooth(this);
    }

    private void findGlobalViews() {
        view = findViewById(R.id.main_layout);
        queue_list = view.findViewById(R.id.main_queue_list);
        queue_add = findViewById(R.id.main_fab_add);
        queue_camera = findViewById(R.id.main_fab_camera);
        queue_gallery = findViewById(R.id.main_fab_gallery);
    }

    private void setupButtons() {
        queue_add.setOnClickListener(v -> {
            if (add_menu_expanded) {
                //back action is here
                add_menu_expanded = false;
                queue_add.setImageResource(R.drawable.ic_add);
                queue_camera.hide();
                queue_gallery.hide();
            } else {
                //expand action is here
                add_menu_expanded = true;
                queue_add.setImageResource(R.drawable.ic_back);
                queue_gallery.show();
                queue_camera.show();
            }
        });
        queue_camera.setOnClickListener(v -> capture());
        queue_gallery.setOnClickListener(v -> gallery());
        queue_camera.hide();
        queue_gallery.hide();
    }

    private void setupList() {
        adapter = new QueueAdapter(this, this);
        model.getQueue().observe(this, adapter);
        queue_list.setLayoutManager(new LinearLayoutManager(this));
        queue_list.setAdapter(adapter);
        queue_list.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
    }

    private void setupActionBar() {
        Toolbar myToolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(myToolbar);

        ActionBar actionbar = getSupportActionBar();
        if (actionbar != null)
            actionbar.setTitle("Pythonator");
    }

    private void setupSnackBar() {
        snackbar = Snackbar.make(view, "Bluetooth is required to communicate with the client", Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction("Try again", v -> client.activateBluetooth(this));
    }

    private void sendImage(@NonNull ImageQueueItem image) {
        if (image.isSent()) {
            Snackbar.make(view, "Item is already sent to the server", Snackbar.LENGTH_LONG).show();
            return;
        }

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        int retries = preferences.getInt("retries", 4);
        model.sendImage(image, state -> {
            switch (state) {
                case SENT:
                    runOnUiThread(() -> Snackbar.make(view, "A new image has been sent to the server", Snackbar.LENGTH_LONG).show());
                    break;
                case FAILED:
                    runOnUiThread(() -> Snackbar.make(view, "Could not send image to the server", Snackbar.LENGTH_SHORT).show());
                    break;
            }
        }, retries);
    }

    private void capture() {
        if (!checkPermissionCamera()) {
            askPermissionCamera();
            return;
        }
        camera_handler = new CameraHandler(this);
        camera_handler.capture(this);
    }

    private void gallery() {
        if (!checkPermissionGallery()) {
            askPermissionGallery();
            return;
        }

        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        String[] mimeTypes = {"image/png"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES,mimeTypes);
        startActivityForResult(intent,REQUEST_IMAGE_GALLERY);
    }

    private boolean checkPermissionBluetooth() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_ADMIN) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private void askPermissionBluetooth() {
        requestPermissions(new String[]{Manifest.permission.BLUETOOTH,
                Manifest.permission.BLUETOOTH_ADMIN,
                Manifest.permission.ACCESS_COARSE_LOCATION},
                REQUEST_BLUETOOTH_PERMISSION);
    }

    private boolean checkPermissionCamera() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    private void askPermissionCamera() {
        requestPermissions(new String[]{Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
        }, REQUEST_CAMERA_PERMISSION);
    }

    private boolean checkPermissionGallery() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    private void askPermissionGallery() {
        requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                REQUEST_GALLERY_PERMISSION);
    }

    public void startConnect() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String server_name = preferences.getString("bluetooth_host", "Pythonator");
        int retries = preferences.getInt("retries", 4);
        client.connect(server_name, this, retries);
    }

    @Override
    public void onChangeState(BluetoothConnectState state) {
        if (bluetooth_search == null)
            return;
        switch (state) {
            case PENDING:
                runOnUiThread(() -> bluetooth_search.setIcon(R.drawable.ic_bluetooth_searching));
                break;
            case CONNECTED:
                runOnUiThread(() -> bluetooth_search.setIcon(R.drawable.ic_bluetooth_connected));
                break;
            case NOT_FOUND:
                runOnUiThread(() -> bluetooth_search.setIcon(R.drawable.ic_out_of_range));
                break;
            case NO_LOCATION:
                runOnUiThread(() -> bluetooth_search.setIcon(R.drawable.ic_location_disabled));
            case NO_BLUETOOTH:
            case NOT_CONNECTED:
                runOnUiThread(() -> bluetooth_search.setIcon(R.drawable.ic_bluetooth_disabled));
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.main_menu_settings:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                break;
            case R.id.main_menu_bluetooth:
                if (!client.isConnected()) {
                    if (!client.isBluetoothEnabled()) {
                        client.activateBluetooth(this);
                        snackbar.dismiss();
                    } else {
                        Log.i("Retry", "Retrying to establish client connection");
                        startConnect();
                    }
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        bluetooth_search = menu.findItem(R.id.main_menu_bluetooth);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        boolean result_ok = resultCode == RESULT_OK;

        switch (requestCode) {
            case BluetoothClient.REQUEST_ENABLE_BLUETOOTH:
                if (result_ok)
                    startConnect();
                else
                    snackbar.show();
                break;
            case CameraHandler.REQUEST_CAPTURE:
                if (result_ok) {
                    camera_handler.addPictureToGallery();
                    model.addToQueue(new ImageQueueItem(camera_handler.getFilepath()));
                }
                break;
            case REQUEST_IMAGE_GALLERY:
                if (result_ok) {
                    if (data != null) {
                        Uri uri = data.getData();
                        if (uri != null) {
                            model.addToQueue(new ImageQueueItem(FileUtil.getPath(this, uri)));
                            break;
                        }
                    }
                    Snackbar.make(view, "Error retrieving picture", Snackbar.LENGTH_LONG).show();
                }
                break;
            case ImageEditHandler.REQUEST_IMAGE_EDIT:
                if (result_ok) {
                    String output_file = image_edit_handler.getEditedPath();
                    if (output_file != null)
                        model.replaceQueueItem(image_edit_handler.getImageQueueItem(), new ImageQueueItem(output_file));
                }
                image_edit_handler = null;
                break;
        }
    }

    @Override
    protected void onDestroy() {
        //imagine I got list of images here
        super.onDestroy();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode) {
            case REQUEST_CAMERA_PERMISSION:
                if (grantResults[0] == PackageManager.PERMISSION_DENIED || grantResults[1] == PackageManager.PERMISSION_DENIED
                        || grantResults[2] == PackageManager.PERMISSION_DENIED)
                    Snackbar.make(view, "Cannot open camera and store picture without permissions", Snackbar.LENGTH_LONG).show();
                else
                    capture();
                break;
            case REQUEST_GALLERY_PERMISSION:
                if (grantResults[0] == PackageManager.PERMISSION_DENIED)
                    Snackbar.make(view, "Cannot open gallery without permissions", Snackbar.LENGTH_LONG).show();
                else
                    gallery();
                break;
        }
    }

    @Override
    public void onClick(View view, int pos) {
    }

    @Override
    public boolean onLongClick(View view, int pos) {
        return true;
    }

    @Override
    public void onSwiped(int pos) {
        model.removeFromQueue(Collections.singletonList(adapter.get(pos)));
    }

    @Override
    public void onThumbnailClick(int pos) {
        ImageQueueItem clicked = adapter.get(pos);
        if (clicked.getState() != SendListener.SendState.SENDING && clicked.getState() != SendListener.SendState.SENT) {
            image_edit_handler = new ImageEditHandler(this, clicked);
            image_edit_handler.edit(this);
        }
    }

    @Override
    public void onSendClicked(int pos) {
        ImageQueueItem item = adapter.get(pos);
        if (!item.isSent())
        sendImage(item);
    }
}