package com.python.pythonator.structures;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.annotation.CheckResult;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.WorkerThread;

import com.python.pythonator.ui.templates.ResultCallback;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.concurrent.Executors;

import static com.python.pythonator.util.FileUtil.getFileSize;

/**
 * One of the most important classes of this project: Exposes (potentially huge) images to our application
 */
@SuppressWarnings("unused")
public class Image {
    private String abs_path;
    private String size;
    private Bitmap.CompressFormat format;

    public Image(@NonNull String abs_path) {
        this.abs_path = abs_path;
        this.size = getFileSize(abs_path);
        this.format = Bitmap.CompressFormat.JPEG;
    }

    /**
     * Sets conversion format to PNG
     * @return Self
     */
    @CheckResult
    public @NonNull Image setPNG() {
        format = Bitmap.CompressFormat.PNG;
        return this;
    }

    /**
     * Sets conversion format to JPEG
     * @return Self
     */
    @CheckResult
    public @NonNull Image setJPEG() {
        format = Bitmap.CompressFormat.JPEG;
        return this;
    }

    /**
     * Sets conversion format to WEBP
     * @return Self
     */
    @CheckResult
    public @NonNull Image setWEBP() {
        format = Bitmap.CompressFormat.WEBP;
        return this;
    }

    /**
     * Be aware when calling this function: An image may be 4K, resulting in hanging UI's
     * @param quality The quality of the bitmap to return. Format PNG ignores this value
     * @return the bitmap of this image, with given quality
     */
    @CheckResult
    @WorkerThread
    public @NonNull Bitmap getBitmap(int quality) {
        Bitmap bitmap = BitmapFactory.decodeFile(abs_path);
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        bitmap.compress(format, quality, os);
        return bitmap;
    }

    /**
     * @return Human readable size of this image (e.g. 2.5MB)
     */
    @CheckResult
    public String getSize() {
        return size;
    }

    /**
     * @return Path to the image
     */
    @CheckResult
    public String getPath() {
        return abs_path;
    }

    /**
     * Execute this function in a thread: Image reading and oompression will otherwise hang UI
     * @return bytes of the bitmap
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    @CheckResult
    @WorkerThread
    public byte[] getBitmapBytes() {
        Bitmap bitmap = BitmapFactory.decodeFile(abs_path);
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        byte[] array = null;
        if (format == Bitmap.CompressFormat.JPEG && (abs_path.endsWith(".jpg") || abs_path.endsWith(".jpeg")) ||
                format == Bitmap.CompressFormat.PNG && (abs_path.endsWith(".png"))) { //No compress if we already have the right type
            try {
                File f = new File(abs_path);
                array = new byte[(int)f.length()];
                FileInputStream fin = new FileInputStream(f);
                fin.read(array);
                fin.close();
            } catch (Exception ignored) {}
        } else {
            bitmap.compress(format, 100, os);
            array = os.toByteArray();
        }
        try {
            os.close();
        } catch (IOException ignored) {}
        return array;
    }

    /**
     * @return width of image
     */
    @CheckResult
    public int getWidth() {
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(abs_path, o);
        return o.outWidth;
    }

    /**
     * @return height of image
     */
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

            // Calculate inSampleSize (10 * insamplesize for extra low quality)
            options.inSampleSize = 10 * calculateInSampleSize(options, requested_width, requested_height);

            // Decode bitmap with inSampleSize set
            options.inJustDecodeBounds = false;
            callback.onResult(BitmapFactory.decodeFile(abs_path, options));
        });
    }

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
