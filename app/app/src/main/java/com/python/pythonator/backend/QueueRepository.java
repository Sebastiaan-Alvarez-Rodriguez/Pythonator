package com.python.pythonator.backend;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.WorkerThread;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.python.pythonator.backend.bluetooth.BluetoothClient;
import com.python.pythonator.backend.bluetooth.SendListener;
import com.python.pythonator.structures.Image;
import com.python.pythonator.structures.ImageQueueItem;
import com.python.pythonator.util.ThreadUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class QueueRepository {

    private MutableLiveData<List<ImageQueueItem>> queue;
    private boolean is_sending;
    private BluetoothClient client;

    public QueueRepository(Context application_context) {
        queue = new MutableLiveData<>();
        is_sending = false;
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

    public void trySendImage(@NonNull ImageQueueItem image, int retries, SendListener listener) {
        Executors.newSingleThreadExecutor().execute(() -> {
            if (image.isSent()) {
                listener.onResult(SendListener.SendState.ALREADY_SENT);
            } else if (!is_sending && client.isConnected()) {
                is_sending = true;
                SendListener.SendState state = trySendImageInternal(image.get(), retries);
                is_sending = false;
                image.setSent(state == SendListener.SendState.SENT);
                listener.onResult(state);
            } else if (is_sending) {
                listener.onResult(SendListener.SendState.BUSY);
            } else {
                listener.onResult(SendListener.SendState.FAILED);
            }
        });
    }

    @WorkerThread
    private SendListener.SendState trySendImageInternal(@NonNull Image image, int retries) {
        if (retries == 0)
            return SendListener.SendState.FAILED;
        if (client.sendImage(image)) {
            return SendListener.SendState.SENT;
        } else {
            ThreadUtil.sleep(2000);
            return trySendImageInternal(image, retries-1);
        }
    }
}
