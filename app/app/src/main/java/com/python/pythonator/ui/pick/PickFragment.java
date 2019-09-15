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

public class PickFragment extends Fragment {
    private static final int REQUEST_CAMERA_PERMISSION = 0, REQUEST_IMAGE_CAPTURE = 1;

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
        gallery_button.setOnClickListener(v -> {

        });
    }

    private void capture() {
        if (!checkPermission()) {
            askPermission();
            return;
        }
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (getActivity() != null && intent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
        } else {
            //TODO: User has no camera app. Handle camera myself
        }
    }

    private boolean checkPermission() {
        return ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }

    private void askPermission() {
        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK && data.hasExtra("data")) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            //TODO: do something intelligent with photo
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults[0] == PackageManager.PERMISSION_DENIED)
                Snackbar.make(getView(), "Cannot open camera without permissions", Snackbar.LENGTH_LONG).show();
            else
                capture();
        }
    }
}
