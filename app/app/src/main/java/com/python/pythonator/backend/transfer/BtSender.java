package com.python.pythonator.backend.transfer;

import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.preference.PreferenceManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.common.io.LittleEndianDataOutputStream;
import com.python.pythonator.structures.Image;
import com.python.pythonator.structures.queue.ImageQueueItem;
import com.python.pythonator.structures.queue.ImageState;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Class to handle sending images to the server, and to keep track of ordering of sent items
 * Follows producer-consumer principles
 */
public class BtSender {

    // In the queue below, we store all images we want to send
    private Queue<ImageQueueItem> queue;

    // In this queue, we store all sent images, to know which image is drawn when we get a signal
    private Queue<ImageQueueItem> orderqueue;

    // This single-threaded service looks at the queue every 5 seconds, and sends the first item (and pops it)
    private ScheduledExecutorService service;

    // We need this context to check out how many retries we have, each time we send an item
    private Context application_context;

    private BluetoothSocket socket;

    public BtSender(Context application_context) {
        queue = new ConcurrentLinkedQueue<>();
        orderqueue = new ConcurrentLinkedQueue<>();
        service = Executors.newSingleThreadScheduledExecutor();
        this.application_context = application_context;
        socket = null;
        start();
    }

    public synchronized void updateSocket(@NonNull BluetoothSocket socket) {
        this.socket = socket;
    }
    /**
     * Start the consumer thread, which handles sending
     */
    private void start() {
        service.scheduleAtFixedRate(() -> {
            while (socket != null && socket.isConnected() && !queue.isEmpty()) {
                ImageQueueItem item = queue.poll();
                Log.i("BtS", "Found an image in queue!");
                int retries = PreferenceManager.getDefaultSharedPreferences(application_context).getInt("retries", 4);
                Image image = item.get();
                LittleEndianDataOutputStream out = null;
                for (int i = 0; i < retries; ++i) {
                    try {
                        out = new LittleEndianDataOutputStream(socket.getOutputStream());
                    } catch (Exception ignored) {
                        try {
                            Thread.sleep(1000);
                        } catch (Exception ignored2){}
                    }
                }
                if (out == null) {
                    Log.i("BtS", "Failed to send image, no connection ("+retries+" retries");
                    item.setState(ImageState.NOT_SENT);
                    return;
                }
                byte[] to_send = image.getBitmapBytes();

                for (int i = 0; i < retries && socket.isConnected(); ++i) {
                    try {
                        Log.i("BtS", "Sending image... retry "+(i+1)+"/"+retries);

                        Log.i("BtS", "I will send: "+((long)to_send.length)+ " bytes!");
                        out.writeLong((long) to_send.length);
                        out.write(image.getBitmapBytes());
                        item.setState(ImageState.SENT);
                        Log.i("BtS", "Image sent! (success on retry "+(i+1)+"/"+retries+")");
                        orderqueue.add(item);
                        return;
                    } catch (Exception ignored) {}
                }
                Log.i("BtS", "Failed to send image, with "+retries+ " retries");
                item.setState(ImageState.NOT_SENT);
            }
        }, 0, 5, TimeUnit.SECONDS);
        Log.i("BtS", "Bluetooth sender started");
    }

    /**
     * Stop the comsumer thread for sending (Call this when bluetooth is turned off, per example)
     */
    public synchronized void stop() {
        service.shutdown();
        Log.i("BtS", "Bluetooth sender stopped");
    }

    /**
     * Send an image to the server
     * @param item the image to send
     */
    public synchronized void sendImage(@NonNull ImageQueueItem item) {
        queue.add(item);
        item.setState(ImageState.SENDING);
    }

    /**
     * Pops the first item out of the sent-queue and returns it
     * @return the first item sent away that has not received a DRAWN confirm yet
     */
    public synchronized @Nullable ImageQueueItem getFirstSent() {
        return orderqueue.poll();
    }
}
