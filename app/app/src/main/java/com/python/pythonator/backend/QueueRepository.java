package com.python.pythonator.backend;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.python.pythonator.backend.bluetooth.BluetoothClient;
import com.python.pythonator.backend.bluetooth.receiver.ReceivedListener;
import com.python.pythonator.backend.bluetooth.sender.SendListener;
import com.python.pythonator.structures.queue.ImageQueueItem;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class QueueRepository {

    private MutableLiveData<List<ImageQueueItem>> queue;
    private BluetoothClient client;

    public QueueRepository(Context application_context) {
        queue = new MutableLiveData<>();
        client = BluetoothClient.getClient(application_context);
    }

    public LiveData<List<ImageQueueItem>> getQueue() {
        return queue;
    }

    public void addToQueue(@NonNull Collection<ImageQueueItem> images) {
        Executor executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            List<ImageQueueItem> list = queue.getValue();
            if (list == null)
                list = new ArrayList<>();
            list.addAll(images);
            queue.postValue(list);
        });
    }

    public void removeFromQueue(@NonNull Collection<ImageQueueItem> images) {
        Executor executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            List<ImageQueueItem> list = queue.getValue();
            if (list == null)
                return;
            list.removeAll(images);
            queue.postValue(list);
        });
    }

    public void replaceQueueItem(@NonNull ImageQueueItem imageOld, @NonNull ImageQueueItem imageNew) {
        Executor executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            List<ImageQueueItem> list = queue.getValue();
            if (list == null || imageOld.isSent())
                return;

           int index = list.indexOf(imageOld);
           if (index == -1)
               return;

           list.set(index, imageNew);
           queue.postValue(list);
        });
    }

    public synchronized void sendImage(@NonNull ImageQueueItem image, @NonNull SendListener listener, int retries) {
        if (image.isSent() || image.getState() == SendListener.SendState.SENDING) {
            return;
        } else if (client.isConnected()) {
            Log.i("QueueRepo", "Backend ready to send image, with retries = "+retries);
            image.setState(SendListener.SendState.SENDING);
            Executors.newSingleThreadExecutor().execute(() ->
                    client.sendImage(image.get(), result -> {
                    image.setState(result);
                    listener.onResult(result);
                    }, retries));
        } else {
            listener.onResult(SendListener.SendState.FAILED);
        }
    }

    public synchronized void receiveConfirm(@NonNull ReceivedListener listener, int retries) {
        client.receiveConfirm(listener, retries);
    }
}
