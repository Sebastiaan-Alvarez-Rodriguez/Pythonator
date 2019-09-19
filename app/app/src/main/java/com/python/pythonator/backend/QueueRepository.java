package com.python.pythonator.backend;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.python.pythonator.structures.Image;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class QueueRepository {

    private MutableLiveData<List<Image>> queue;

    public QueueRepository() {
        queue = new MutableLiveData<>();
    }

    public LiveData<List<Image>> getQueue() {
        return queue;
    }

    public void addToQueue(@NonNull Image image) {
        Executor myExecutor = Executors.newSingleThreadExecutor();
        myExecutor.execute(() -> {
            List<Image> list = queue.getValue();
            if (list == null)
                list = new ArrayList<>();
            list.add(image);
            queue.postValue(list);
        });
    }
}
