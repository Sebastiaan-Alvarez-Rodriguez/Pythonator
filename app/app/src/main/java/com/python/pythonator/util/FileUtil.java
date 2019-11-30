package com.python.pythonator.util;

import android.content.Context;
import android.database.Cursor;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import androidx.annotation.CheckResult;
import androidx.annotation.NonNull;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Utility functions for file manipulations
 */
public class FileUtil {

    /**
     * Converts a uri to a filepath, in a very dirty manner
     * @param context Context of application
     * @param uri Uri to convert
     * @return filepath matching uri
     */
    @CheckResult
    public static String getPath(@NonNull Context context, @NonNull Uri uri ) {
        String result = null;
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = context.getContentResolver( ).query( uri, proj, null, null, null );
        if(cursor != null){
            if ( cursor.moveToFirst( ) ) {
                int column_index = cursor.getColumnIndexOrThrow( proj[0] );
                result = cursor.getString( column_index );
            }
            cursor.close( );
        }
        if(result == null) {
            result = "Not found";
        }
        return result;
    }

    /**
     * @param path Path of the file to return size for
     * @return the file size of a given file
     */
    @CheckResult
    public static String getFileSize(@NonNull String path) {
        File file = new File(path);
        String modifiedFileSize;
        double fileSize;
        if (file.isFile()) {
            fileSize = (double) file.length();//in Bytes

            if (fileSize < 1024) {
                modifiedFileSize = String.valueOf(fileSize).concat("B");
            } else if (fileSize > 1024 && fileSize < (1024 * 1024)) {
                modifiedFileSize = String.valueOf(Math.round((fileSize / 1024 * 100.0)) / 100.0).concat("KB");
            } else {
                modifiedFileSize = String.valueOf(Math.round((fileSize / (1024 * 1204) * 100.0)) / 100.0).concat("MB");
            }
        } else {
            modifiedFileSize = "Unknown";
        }

        return modifiedFileSize;
    }

    /**
     * @param context Application context (needed to make file)
     * @return A newly created file in the global pictures environment
     */
    @CheckResult
    public static @NonNull File createImageFile(@NonNull Context context) {
        String time_stamp = SimpleDateFormat.getDateTimeInstance().format(new Date());
        String image_file_name = "JPEG_" + time_stamp + "_";
        File storage_dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File app_dir = new File(storage_dir, "Pythonator");
        //noinspection ResultOfMethodCallIgnored
        app_dir.mkdirs();

        File file = new File(app_dir, image_file_name+".jpg");
        MediaScannerConnection.scanFile(context, new String[] {file.getAbsolutePath()}, null, null);
        return file;
    }
}
