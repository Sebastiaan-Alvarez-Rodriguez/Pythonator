package com.python.pythonator.ui.camera;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import androidx.annotation.CheckResult;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CameraHandler {
    public static final int REQUEST_CAPTURE = 300;

    private @NonNull Context context;
    private String filepath;

    public CameraHandler(@NonNull Context context) {
        this.context = context;
        filepath = null;
    }

    /**
     * Take a picture. Should have permission to take pictures before calling this function
     * {@link #REQUEST_CAPTURE} will be the request code for the intent
     * @param activity The calling activity
     */
    public void capture(@NonNull Activity activity) {
        Intent intent = createCaptureIntent();
        if (intent != null)
            activity.startActivityForResult(intent, REQUEST_CAPTURE);
    }

    /**
     * @see #capture(Activity)
     * Same function, but for fragment callers
     */
    public void capture(@NonNull Fragment fragment) {
        Intent intent = createCaptureIntent();
        if (intent != null)
            fragment.startActivityForResult(intent, REQUEST_CAPTURE);
    }

    public String getFilepath() {
        return filepath;
    }

    private @Nullable Intent createCaptureIntent() {
        Intent ext_photo_intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File photo_file = createImageFile();
        if (photo_file == null)
            return null;
        Uri photoURI = FileProvider.getUriForFile(context,"com.python.pythonator.fileprovider", photo_file);
        ext_photo_intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);

        return Intent.createChooser(ext_photo_intent, "Take a picture");
    }

    @CheckResult
    private @Nullable File createImageFile() {
        String time_stamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String image_file_name = "JPEG_" + time_stamp + "_";
        File storage_dir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        try {
            File file = File.createTempFile(image_file_name,".jpg", storage_dir);
            filepath = file.getAbsolutePath();
            return file;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void addPictureToGallery() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(filepath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        context.sendBroadcast(mediaScanIntent);
    }
}
