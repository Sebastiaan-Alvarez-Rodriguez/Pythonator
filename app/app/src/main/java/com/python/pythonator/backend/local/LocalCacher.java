package com.python.pythonator.backend.local;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.os.Environment;
import android.util.Log;

import androidx.annotation.Nullable;

import com.python.pythonator.structures.Image;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.Executors;

public class LocalCacher {

    public static void store(Image image, Context context) {
        try {
            FileOutputStream f_out = context.openFileOutput(image.getName(), Context.MODE_PRIVATE);
            DataOutputStream d_out = new DataOutputStream(f_out);
            d_out.writeInt(image.getWidth());
            d_out.writeInt(image.getHeight());

            d_out.writeInt(image.getName().length());
            d_out.writeChars(image.getName());

            d_out.writeInt(image.getDate().length());
            d_out.writeChars(image.getDate());
            d_out.write(image.getBitmapBytes());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
/*

    public static void load(String filename, Context context, LocalResultInterface localResultInterface) {
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                FileInputStream f_in = new FileInputStream(filename);
                DataInputStream d_in = new DataInputStream(f_in);
                int w = d_in.readInt();
                int h = d_in.readInt();


//                byte[] bitmap_bytes = new byte[((int) bitmap_length)];
//                d_in.read(bitmap_bytes, 0, (int) bitmap_length);
//
//                Bitmap b = BitmapFactory.decodeByteArray(bitmap_bytes, 0, (int) bitmap_length);
                int name_length = d_in.readInt();
                StringBuilder name_builder = new StringBuilder();
                for (int i = 0; i < name_length; i++)
                    name_builder.append(d_in.readChar());
                String name = name_builder.toString();


                int date_length = d_in.readInt();
                StringBuilder date_builder = new StringBuilder();
                for (int i = 0; i < date_length; i++)
                    date_builder.append(d_in.readChar());
                String date = date_builder.toString();

                Log.e("LOCAL", "w: "+w);
                Log.e("LOCAL", "h: "+h);
                Log.e("LOCAL", "name: "+name);
                Log.e("LOCAL", "date: "+date);
                Bitmap b = BitmapFactory.decodeStream(d_in, new Rect(0,0,w,h), null);
                Log.e("LOCAL", "image is null? "+(b == null));
                localResultInterface.onResult(new Image(b, name, date));
            } catch (IOException e) {
                Log.e("LOCAL", "Error: "+e);
            }
            localResultInterface.onResult(null);
        });
    }
 */
    public static void load(String filename, Context context, LocalResultInterface localResultInterface) {
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                FileInputStream f_in = new FileInputStream(filename);


                Bitmap b = BitmapFactory.decodeStream(f_in);
//                Log.e("LOCAL", "w: "+w);
//                Log.e("LOCAL", "h: "+h);
//                Log.e("LOCAL", "name: "+name);
//                Log.e("LOCAL", "date: "+date);
                Log.e("LOCAL", "image is null? "+(b == null));
//                localResultInterface.onResult(new Image(b, filename, "just now"));
            } catch (IOException e) {
                Log.e("LOCAL", "Error: "+e);
            }
            localResultInterface.onResult(null);
        });
    }
}
