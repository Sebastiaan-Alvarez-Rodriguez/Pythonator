package com.python.pythonator.backend.transfer;

import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.preference.PreferenceManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.python.pythonator.structures.Image;
import com.python.pythonator.structures.queue.ImageQueueItem;
import com.python.pythonator.structures.queue.ImageState;

import java.io.DataOutputStream;
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

    public BtSender(Context application_context) {
        queue = new ConcurrentLinkedQueue<>();
        orderqueue = new ConcurrentLinkedQueue<>();
        service = Executors.newSingleThreadScheduledExecutor();
        this.application_context = application_context;
        
    }

    /**
     * Start the consumer thread, which handles sending
     */
    public synchronized void start(@NonNull BluetoothSocket socket) {
        service.scheduleAtFixedRate(() -> {
            while (!queue.isEmpty()) {
                ImageQueueItem item = queue.poll();
                Log.i("BtS", "Found an image in queue!");
                int retries = PreferenceManager.getDefaultSharedPreferences(application_context).getInt("retries", 4);
                for (int i = 0; i < retries; ++i) {
                    try {
                        Log.i("BtS", "Sending image... retry "+(i+1)+"/"+retries);
                        Image image = item.get();
                        DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                        byte[] to_send = image.getBitmapBytes();
                        out.writeLong((long) to_send.length);
                        out.write(image.getBitmapBytes());
                        item.setState(ImageState.SENT);
                        orderqueue.add(item);
                        return;
                    } catch (Exception ignored) {}
                }
                Log.i("BtS", "Failed to send image, with "+retries+ " retries");
                item.setState(ImageState.NOT_SENT);
            }
        }, 0, 5, TimeUnit.SECONDS);
    }

    /**
     * Stop the comsumer thread for sending (Call this when bluetooth is turned off, per example)
     */
    public synchronized void stop() {
        service.shutdown();
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
