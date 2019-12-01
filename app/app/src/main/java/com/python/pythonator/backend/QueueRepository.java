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

/**
 * Repository class for our data.
 * Officially, we should keep the bluetooth client here, and make calls from the repository to it.
 * However, this would result in a lot of call-through functions, because handling bluetooth connections
 * is handled in UI only.
 * Therefore, we migrated bluetooth connection handling to a separate object,
 * with which the UI can interact directly
 */
public class QueueRepository {

    // The interesting data of this application: The images a user selected
    private MutableLiveData<List<ImageQueueItem>> queue;

    public QueueRepository() {
        queue = new MutableLiveData<>();
    }

    /**
     * @return Livedata of current queue
     */
    public LiveData<List<ImageQueueItem>> getQueue() {
        return queue;
    }

    /**
     * Add a collection of items to the queue
     * @param images Collection to add
     */
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

    /**
     * Removes a collection of items from the queue. Non-existing items are ignored.
     * @param images Collection to remove
     */
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

    /**
     * Replaces one item in the queue with another one.
     * This function is useful if you want to replace, but do not want the new item to appear at queue end
     * @param imageOld Old image to have replaced
     * @param imageNew New image to replace the old one with
     */
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
