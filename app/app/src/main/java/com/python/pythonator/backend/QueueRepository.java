package com.python.pythonator.backend;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.python.pythonator.backend.bluetooth.BluetoothServer;
import com.python.pythonator.structures.Image;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class QueueRepository {

    private MutableLiveData<List<Image>> queue;
    private BluetoothServer server;

    public QueueRepository(Context application_context) {
        queue = new MutableLiveData<>();
        server = BluetoothServer.getServer(application_context);
    }

    public LiveData<List<Image>> getQueue() {
        return queue;
    }

    public void addToQueue(@NonNull Image image) {
        Executor executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            List<Image> list = queue.getValue();
            if (list == null)
                list = new ArrayList<>();
            list.add(image);
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
}
