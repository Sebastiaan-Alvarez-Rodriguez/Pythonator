package com.python.pythonator.backend;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.python.pythonator.backend.bluetooth.BluetoothServer;
import com.python.pythonator.structures.Image;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class QueueRepository {

    private MutableLiveData<List<Image>> local_queue, server_queue;
    private BluetoothServer server;

    public QueueRepository(Context application_context) {
        local_queue = new MutableLiveData<>();
        server_queue = new MutableLiveData<>();
        server = BluetoothServer.getServer(application_context);
    }

    public LiveData<List<Image>> getLocalQueue() {
        return local_queue;
    }

    public void addToLocalQueue(@NonNull Image image) {
        Executor executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            List<Image> list = local_queue.getValue();
            if (list == null)
                list = new ArrayList<>();
            list.add(image);
            local_queue.postValue(list);

        });
    }

    public void removeFromLocalQueue(@NonNull Collection<Image> images) {
        Executor executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            List<Image> list = local_queue.getValue();
            if (list == null)
                return;
            for (Image image : images)
                list.remove(image);
            local_queue.postValue(list);
        });
    }

    public void sendImage(@NonNull Image image) {
        server.sendImage(image, sent -> {
            if (sent) {
                removeFromLocalQueue(Collections.singletonList(image));
                addToServerQueue(image);
            }
        });
    }

    public void addToServerQueue(@NonNull Image image) {
        Executor executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            List<Image> list = server_queue.getValue();
            if (list == null)
                list = new ArrayList<>();
            list.add(image);
            server_queue.postValue(list);

        });
    }
}
