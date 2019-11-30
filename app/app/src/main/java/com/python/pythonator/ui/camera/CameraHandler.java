package com.python.pythonator.ui.camera;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import androidx.annotation.CheckResult;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.python.pythonator.util.FileUtil.createImageFile;

/**
 * Class to handle taking pictures with the camera
 */
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
            activity.startActivityForResult(Intent.createChooser(intent, null), REQUEST_CAPTURE);
    }

    /**
     * @return Path to the image file
     */
    public String getFilepath() {
        return filepath;
    }

    /**
     * @return Intent which, if sent, will open a frame where user may choose camera app, takes picture, stores it in gallery
     */
    private @Nullable Intent createCaptureIntent() {
        Intent ext_photo_intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File photo_file = createImageFile(context);
        filepath = photo_file.getAbsolutePath();
        Uri photoURI = FileProvider.getUriForFile(context,"com.python.pythonator.fileprovider", photo_file);
        ext_photo_intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);

        return Intent.createChooser(ext_photo_intent, "Take a picture");
    }

    /**
     * Makes new picture accessible by the gallery (and other apps).
     * We do this, since people might want to share their created photos
     */
    public void addPictureToGallery() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(filepath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        context.sendBroadcast(mediaScanIntent);
    }
}
