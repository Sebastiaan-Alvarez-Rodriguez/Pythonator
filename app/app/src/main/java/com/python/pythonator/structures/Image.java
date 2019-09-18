package com.python.pythonator.structures;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.annotation.CheckResult;
import androidx.annotation.NonNull;

import java.io.ByteArrayOutputStream;

public class Image {
    private Bitmap bitmap;
    private String name, date;


    private Bitmap.CompressFormat format;
    public Image(@NonNull Bitmap bitmap) {
        this(bitmap, "Untitled", "just now");
    }

    public Image(@NonNull Bitmap bitmap, @NonNull String name, @NonNull String date) {
        this.bitmap = bitmap;
        this.name = name;
        this.date = date;
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
    public @NonNull Bitmap getBitmap(int quality) {
        codec(format, quality);
        return bitmap;
    }

    /**
     * Returns a thumbnail of this image with resolution as close as possible to given resolution
     * @param requested_width Requested width for result image
     * @param requested_height Requested height for result image
     * @return a bitmap thumbnail
     */
    @CheckResult
    public @NonNull Bitmap getThumbnail(int requested_width, int requested_height) {
        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, requested_width, requested_height);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        bitmap.compress(format, 100, os);
        byte[] array = os.toByteArray();

        return BitmapFactory.decodeByteArray(array, 0, array.length, options);
    }

    @CheckResult
    public @NonNull String getName() {
        return name;
    }

    @CheckResult
    public @NonNull String getDate() {
        return date;
    }

    /**
     * Encode #bitmap to a given format.
     * @see Bitmap.CompressFormat for formats
     * @param format The format to encode to
     * @param quality Quality indicator for compressor (if format supports), between 1-100.
     */
    private void codec(Bitmap.CompressFormat format, int quality) {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        bitmap.compress(format, quality, os);

        byte[] array = os.toByteArray();
        bitmap = BitmapFactory.decodeByteArray(array, 0, array.length);
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

            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

}
