package com.python.pythonator.ui.edit;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.provider.MediaStore;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;

import com.python.pythonator.structures.ImageQueueItem;

import java.io.File;
import java.util.List;

import static com.python.pythonator.util.FileUtil.createImageFile;

public class ImageEditHandler {

    public static final int REQUEST_IMAGE_EDIT = 500;

    private @NonNull Context context;
    private String filepath;
    private ImageQueueItem image;

    public ImageEditHandler(@NonNull Context context, @NonNull ImageQueueItem image) {
        this.context = context;
        this.image = image;
        filepath = null;
    }

    public final @NonNull ImageQueueItem getImageQueueItem() {
        return image;
    }

    public final @Nullable String getEditedPath() {
        return filepath;
    }

    /**
     * Start edit intent
     * {@link #REQUEST_IMAGE_EDIT} will be the request code for the intent
     * @param activity The calling activity
     */
    public void edit(@NonNull Activity activity) {
        File file = new File(image.get().getPath());
        final Intent intent = new Intent(Intent.ACTION_EDIT);
        File photo_file = createImageFile(context);
        filepath = photo_file.getAbsolutePath();
        Uri photoURI = FileProvider.getUriForFile(context,"com.python.pythonator.fileprovider", photo_file);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
        intent.setDataAndType(FileProvider.getUriForFile(context,"com.python.pythonator.fileprovider", file),"image/*")
                .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                .addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

        grantPermissions(intent, photoURI);

        activity.startActivityForResult(Intent.createChooser(intent, null),REQUEST_IMAGE_EDIT);
    }

    /**
     * Grant read/write permissions for given URI, to all apps that can handle image editing. Handle with great care
     * @param intent The intent which is setup for requesting external image editors
     * @param uri The uri to the file
     */
    private void grantPermissions(@NonNull Intent intent, @NonNull Uri uri) {
        List<ResolveInfo> resInfoList = context.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        for (ResolveInfo resolveInfo : resInfoList) {
            String packageName = resolveInfo.activityInfo.packageName;
            context.grantUriPermission(packageName, uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }
    }
}
