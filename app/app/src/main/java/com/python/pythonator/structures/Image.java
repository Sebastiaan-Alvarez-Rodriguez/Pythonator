package com.python.pythonator.structures;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.annotation.CheckResult;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.WorkerThread;

import com.python.pythonator.ui.templates.ResultCallback;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.concurrent.Executors;

public class Image {
    private String abs_path;

    private Bitmap.CompressFormat format;

    public Image(@NonNull String abs_path) {
        this.abs_path = abs_path;

        this.format = Bitmap.CompressFormat.PNG;
//        rotateImage();
    }

    @CheckResult
    public @NonNull Image setPNG() {
        format = Bitmap.CompressFormat.PNG;
        return this;
    }

    @CheckResult
    public @NonNull Image setJPEG() {
        format = Bitmap.CompressFormat.JPEG;
        return this;
    }

    @CheckResult
    public @NonNull Image setWEBP() {
        format = Bitmap.CompressFormat.WEBP;
        return this;
    }

    @CheckResult
    @WorkerThread
    public @NonNull Bitmap getBitmap(int quality) {
        Bitmap bitmap = BitmapFactory.decodeFile(abs_path);
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        bitmap.compress(format, quality, os);
        return bitmap;
    }

    @CheckResult
    @WorkerThread
    public byte[] getBitmapBytes() {
        Bitmap bitmap = BitmapFactory.decodeFile(abs_path);
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        bitmap.compress(format, 100, os);
        byte[] array = os.toByteArray();
        try {
            os.close();
        } catch (IOException ignored) {}
        return array;
    }

    @CheckResult
    public int getWidth() {
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(abs_path, o);
        return o.outWidth;
    }

    @CheckResult
    public int getHeight() {
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(abs_path, o);
        return o.outHeight;
    }

    /**
     * Returns a thumbnail of this image in callback, with resolution as close as possible to given resolution
     * @param requested_width Requested width for result image
     * @param requested_height Requested height for result image
     * @param callback The callback to receive when an item is loaded
     * @return a bitmap thumbnail
     */
    public void getThumbnail(int requested_width, int requested_height, ResultCallback<Bitmap> callback) {
        Executors.newSingleThreadExecutor().execute(() -> {
            // First decode with inJustDecodeBounds=true to check dimensions
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;

            // Calculate inSampleSize
            options.inSampleSize = calculateInSampleSize(options, requested_width, requested_height);

            // Decode bitmap with inSampleSize set
            options.inJustDecodeBounds = false;
            callback.onResult(BitmapFactory.decodeFile(abs_path, options));
        });
    }

//    /**
//     * Rotates the image to always be in 'landscape' mode
//     */
//    private void rotateImage() {
//        if (bitmap.getHeight() > bitmap.getWidth()) {
//            Matrix matrix = new Matrix();
//            matrix.postRotate(270);
//            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
//        }
//    }

    /**
     * Calculates samplesize for image downscaling/thumbnailing
     * @param options Input bitmap
     * @param reqWidth Requested image width
     * @param reqHeight Requested image height
     * @return the computed samplesize
     */
    @CheckResult
    private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            while ((halfHeight / inSampleSize) >= reqHeight && (halfWidth / inSampleSize) >= reqWidth)
                inSampleSize *= 2;
        }
        return inSampleSize;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (!(obj instanceof Image))
            return false;
        return this.abs_path.equals(((Image) obj).abs_path);
    }
}
