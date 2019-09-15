package com.python.pythonator.ui.pick;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.Snackbar;
import com.python.pythonator.R;
import com.python.pythonator.ui.capture.Capture;

public class PickFragment extends Fragment {
    private static final int
            REQUEST_CAMERA_PERMISSION = 0,
            REQUEST_IMAGE_CAPTURE = 1,
            REQUEST_IMAGE_GALLERY = 2;

    private ImageView capture_button, gallery_button;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        findGlobalViews(view);
        setupButtons();
    }

    private void findGlobalViews(final View view) {
        capture_button = view.findViewById(R.id.main_capture);
        gallery_button = view.findViewById(R.id.main_gallery);
    }

    private void setupButtons() {
        capture_button.setOnClickListener(v -> capture());
        gallery_button.setOnClickListener(v -> gallery());
    }

    private void capture() {
        if (!checkPermission()) {
            askPermission();
            return;
        }

        Intent ext_photo_intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Intent slf_photo_intent = new Intent(getContext(), Capture.class);

        Intent chooserIntent = Intent.createChooser(ext_photo_intent, "Take a picture");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{slf_photo_intent});

        startActivityForResult(chooserIntent, REQUEST_IMAGE_CAPTURE);
    }

    private void gallery() {
        Intent pickIntent = new Intent(Intent.ACTION_GET_CONTENT);
        startActivityForResult(pickIntent, REQUEST_IMAGE_GALLERY);
    }

    @SuppressWarnings("ConstantConditions")
    private boolean checkPermission() {
        return ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }

    @SuppressWarnings("ConstantConditions")
    private void askPermission() {
        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case REQUEST_IMAGE_CAPTURE:
                    if (data.hasExtra("data")) {
                        Bitmap photo = (Bitmap) data.getExtras().get("data");
                        //TODO: do something intelligent with captured photo
                    }
                    break;
                case REQUEST_IMAGE_GALLERY:
                    //TODO: Get image from intent
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
}
