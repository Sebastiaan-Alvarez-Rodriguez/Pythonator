package com.python.pythonator.ui.pick;

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
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.Snackbar;
import com.python.pythonator.R;

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
        return inflater.inflate(R.layout.backup, container, false);
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

    //https://stackoverflow.com/questions/2708128/single-intent-to-let-user-take-picture-or-pick-image-from-gallery-in-android
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

    @SuppressWarnings("ConstantConditions")
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
                        //TODO: do something intelligent with captured photo
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
}
