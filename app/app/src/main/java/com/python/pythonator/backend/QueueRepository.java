package com.python.pythonator.backend;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.WorkerThread;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.python.pythonator.backend.bluetooth.BluetoothClient;
import com.python.pythonator.backend.bluetooth.SendListener;
import com.python.pythonator.structures.Image;
import com.python.pythonator.util.ThreadUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class QueueRepository {

    private MutableLiveData<List<Image>> queue;
    private boolean is_sending;
    private BluetoothClient client;

    public QueueRepository(Context application_context) {
        queue = new MutableLiveData<>();
        is_sending = false;
        client = BluetoothClient.getClient(application_context);
    }

    public LiveData<List<Image>> getQueue() {
        return queue;
    }

    public void addToQueue(@NonNull Collection<Image> images) {
        Executor executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            List<Image> list = queue.getValue();
            if (list == null)
                list = new ArrayList<>();
            list.addAll(images);
            queue.postValue(list);
        });
    }

    public void removeFromQueue(@NonNull Collection<Image> images) {
        Executor executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            List<Image> list = queue.getValue();
            if (list == null)
                return;
            for (Image image : images)
                list.remove(image);
            queue.postValue(list);
        });
    }

    public void replaceQueueItem(@NonNull Image imageOld, @NonNull Image imageNew) {
        Executor executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            List<Image> list = queue.getValue();
            if (list == null)
                return;

           int index = list.indexOf(imageOld);
           if (index == -1)
               return;

           list.set(index, imageNew);
           queue.postValue(list);
        });
    }

    public void trySendImage(@NonNull Image image, int retries, SendListener listener) {
        Executors.newSingleThreadExecutor().execute(() -> {
            if (!is_sending && client.isConnected()) {
                is_sending = true;
                SendListener.SendState state = trySendImageInternal(image, retries);
                is_sending = false;
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
