package com.python.pythonator.ui.queue;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
import com.python.pythonator.ui.queue.model.QueueViewModel;
import com.python.pythonator.ui.queue.view.QueueAdapter;
import com.python.pythonator.ui.templates.ActionListener;

public class QueueFragment extends Fragment implements ActionListener {
    private static final int
            REQUEST_CAMERA_PERMISSION = 0,
            REQUEST_IMAGE_CAPTURE = 1,
            REQUEST_IMAGE_GALLERY = 2;

    private QueueViewModel model;
    private QueueAdapter adapter;

    private RecyclerView queue_list;
    private FloatingActionButton queue_add, queue_camera, queue_gallery;
    private boolean add_menu_expanded = false;

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
    }

    private void findGlobalViews(View view) {
        queue_list = view.findViewById(R.id.fragment_queue_list);
        queue_add = view.findViewById(R.id.fragment_queue_add);
        queue_camera = view.findViewById(R.id.fragment_queue_camera);
        queue_gallery = view.findViewById(R.id.fragment_queue_gallery);
    }

    private void setupButtons() {
        queue_add.setOnClickListener(v -> {
            if (adapter.isActionMode()) {
                //delete action is here
                //TODO: Make delete thing

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

    private void setupList() {
        adapter = new QueueAdapter(this);
        model.getQueue().observe(this, adapter);
        queue_list.setLayoutManager(new LinearLayoutManager(getContext()));
        queue_list.setAdapter(adapter);
        queue_list.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));
    }

    private void setupAdd(View view, boolean actionMode) {
        if (actionMode) {
            queue_add.setImageResource(R.drawable.ic_delete);
            queue_camera.hide();
            queue_gallery.hide();
        } else {
            queue_add.setImageResource(R.drawable.ic_add);
        }

    }

    private void capture() {
        if (!checkPermission()) {
            askPermission();
            return;
        }

        Intent ext_photo_intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Intent chooserIntent = Intent.createChooser(ext_photo_intent, "Take a picture");
        startActivityForResult(chooserIntent, REQUEST_IMAGE_CAPTURE);
    }

    private void gallery() {
        Intent intent=new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
//        String[] mimeTypes = {"image/jpeg", "image/png"};
        String[] mimeTypes = {"image/png"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES,mimeTypes);
        startActivityForResult(intent,REQUEST_IMAGE_GALLERY);
    }

    @SuppressWarnings("ConstantConditions")
    private boolean checkPermission() {
        return ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }

    private void askPermission() {
        requestPermissions(new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case REQUEST_IMAGE_CAPTURE:
                    if (data.hasExtra("data")) {
                        Bitmap photo = (Bitmap) data.getExtras().get("data");
                        Image image = new Image(photo);
                        //TODO: do something intelligent with captured image
                        //bitmap.compress(Bitmap.CompressFormat.PNG, quality, outStream);
                    }
                    break;
                case REQUEST_IMAGE_GALLERY:
                    Uri uri = data.getData();
                    try {
                        Bitmap photo = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri);
                    } catch (Exception ignored) {
                        Snackbar.make(getView(), "Error retrieving file", Snackbar.LENGTH_LONG).show();
                    }
            }
        }
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults[0] == PackageManager.PERMISSION_DENIED)
                Snackbar.make(getView(), "Cannot open camera without permissions", Snackbar.LENGTH_LONG).show();
            else
                capture();
        }
    }

    @Override
    public void onClick(View view, int pos) {
        if (!adapter.isActionMode()) {
            Image clicked = adapter.get(pos);
        }
    }

    @Override
    public boolean onLongClick(View view, int pos) {
        return true;
    }

    @Override
    public void onActionModeChange(boolean actionMode) {
        setupAdd(getView(), actionMode);
    }
}
