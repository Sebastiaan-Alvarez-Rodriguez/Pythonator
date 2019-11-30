package com.python.pythonator.backend;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.python.pythonator.structures.queue.ImageQueueItem;
import com.python.pythonator.structures.queue.ImageState;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class QueueRepository {

    private MutableLiveData<List<ImageQueueItem>> queue;

    public QueueRepository() {
        queue = new MutableLiveData<>();
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
            if (list == null || imageOld.getState() != ImageState.NOT_SENT)
                return;

           int index = list.indexOf(imageOld);
           if (index == -1)
               return;

           list.set(index, imageNew);
           queue.postValue(list);
        });
    }
}
