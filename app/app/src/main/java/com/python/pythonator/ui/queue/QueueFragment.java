package com.python.pythonator.ui.queue;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.python.pythonator.R;
import com.python.pythonator.structures.Image;
import com.python.pythonator.ui.camera.CameraHandler;
import com.python.pythonator.ui.queue.model.QueueViewModel;
import com.python.pythonator.ui.queue.view.QueueAdapter;
import com.python.pythonator.ui.templates.adapter.listener.ActionListener;
import com.python.pythonator.util.UriUtil;

public class QueueFragment extends Fragment implements ActionListener {
    private static final int
            REQUEST_CAMERA_PERMISSION = 0,
            REQUEST_GALLERY_PERMISSION = 1,
            REQUEST_IMAGE_GALLERY = 2;

    private QueueViewModel model;
    private QueueAdapter adapter;

    private RecyclerView queue_list;
    private FloatingActionButton queue_add, queue_camera, queue_gallery;
    private TextView queue_nothing_drawn;

    private boolean add_menu_expanded = false;

    private CameraHandler camera_handler;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        model = ViewModelProviders.of(this).get(QueueViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_queue, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        findGlobalViews(view);
        setupButtons();
        setupList();
        setupTexts();
    }

    @SuppressWarnings("ConstantConditions")
    private void findGlobalViews(@NonNull View view) {
        queue_list = view.findViewById(R.id.fragment_local_queue_list);
        queue_add = getActivity().findViewById(R.id.main_fab_add);
        queue_camera = getActivity().findViewById(R.id.main_fab_camera);
        queue_gallery = getActivity().findViewById(R.id.main_fab_gallery);
        queue_nothing_drawn = view.findViewById(R.id.fragment_queue_nothing_drawn);
    }

    private void setupButtons() {
        queue_add.setOnClickListener(v -> {
            if (adapter.isActionMode()) {
                //delete action is here
                model.removeFromQueue(adapter.getSelected());
                adapter.deactivateActionMode();

                add_menu_expanded = false;
                queue_add.setImageResource(R.drawable.ic_add);
            } else if (add_menu_expanded) {
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

    @SuppressWarnings("ConstantConditions")
    private void setupList() {
        adapter = new QueueAdapter(this);
        model.getQueue().observe(this, adapter);
        queue_list.setLayoutManager(new LinearLayoutManager(getContext()));
        queue_list.setAdapter(adapter);
        queue_list.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));
    }

    private void setupAdd(boolean actionMode) {
        if (actionMode) {
            queue_add.setImageResource(R.drawable.ic_delete);
            queue_camera.hide();
            queue_gallery.hide();
        } else {
            queue_add.setImageResource(R.drawable.ic_add);
        }
    }

    private void setupTexts() {
        queue_nothing_drawn.setVisibility(View.VISIBLE);
    }

    @SuppressWarnings("ConstantConditions")
    private void capture() {
        if (!checkPermissionCamera()) {
            askPermissionCamera();
            return;
        }
        camera_handler = new CameraHandler(getContext());
        camera_handler.capture(this);
    }

    private void gallery() {
        if (!checkPermissionGallery()) {
            askPermissionGallery();
            return;
        }

        Intent intent=new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
//        String[] mimeTypes = {"image/jpeg", "image/png"};
        String[] mimeTypes = {"image/png"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES,mimeTypes);
        startActivityForResult(intent,REQUEST_IMAGE_GALLERY);
    }

    @SuppressWarnings("ConstantConditions")
    private boolean checkPermissionCamera() {
        return ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    private void askPermissionCamera() {
        requestPermissions(new String[]{Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
        }, REQUEST_CAMERA_PERMISSION);
    }

    @SuppressWarnings("ConstantConditions")
    private boolean checkPermissionGallery() {
        return ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    private void askPermissionGallery() {
        requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                REQUEST_GALLERY_PERMISSION);
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case CameraHandler.REQUEST_CAPTURE:
                    Log.e("QUEUE", "Received photo");
                    camera_handler.addPictureToGallery();
                    model.addToQueue(new Image(camera_handler.getFilepath()));

                    break;
                case REQUEST_IMAGE_GALLERY:
                    Uri uri = data.getData();
                    try {
                        Log.e("QUEUE", "Received gallery");
                        Image image = new Image(UriUtil.getPath(getContext(), uri));
                        model.addToQueue(image);
                    } catch (Exception ignored) {
                        Snackbar.make(getView(), "Error retrieving file", Snackbar.LENGTH_LONG).show();
                    }
                    break;
            }
        } else {
            Log.e("QUEUE", "Received canceled");
        }
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode) {
            case REQUEST_CAMERA_PERMISSION:
                if (grantResults[0] == PackageManager.PERMISSION_DENIED || grantResults[1] == PackageManager.PERMISSION_DENIED
                        || grantResults[2] == PackageManager.PERMISSION_DENIED)
                    Snackbar.make(getView(), "Cannot open camera and store picture without permissions", Snackbar.LENGTH_LONG).show();
                else
                    capture();
                break;
            case REQUEST_GALLERY_PERMISSION:
                if (grantResults[0] == PackageManager.PERMISSION_DENIED)
                    Snackbar.make(getView(), "Cannot open gallery without permissions", Snackbar.LENGTH_LONG).show();
                else
                    gallery();
                break;
        }
    }

    @Override
    public void onClick(View view, int pos) {
        if (!adapter.isActionMode()) {
            Image clicked = adapter.get(pos);
            model.sendImage(clicked);
        }
    }

    @Override
    public boolean onLongClick(View view, int pos) {
        return true;
    }

    @Override
    public void onActionModeChange(boolean actionMode) {
        Log.e("Actionmode", "Actionmode changed to: "+actionMode);
        setupAdd(actionMode);
    }
}
