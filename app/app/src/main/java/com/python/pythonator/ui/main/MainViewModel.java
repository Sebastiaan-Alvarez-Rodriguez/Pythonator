package com.python.pythonator.ui.main;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.python.pythonator.backend.QueueRepository;
import com.python.pythonator.structures.queue.ImageQueueItem;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Handles retaining important information between lifecycle events, and to expose our repository
 * @see AndroidViewModel
 */
public class MainViewModel extends AndroidViewModel {
    // Cached variant of repository images
    private LiveData<List<ImageQueueItem>> cache = null;
    // Our repository
    private QueueRepository repository;

    public MainViewModel(@NonNull Application application) {
        super(application);
        repository = new QueueRepository();
    }

    /**
     * @return live data of selected images
     */
    public LiveData<List<ImageQueueItem>> getQueue() {
        if (cache == null)
            cache = repository.getQueue();
        return cache;
    }

    /**
     * Adds an item to the queue
     * @param image Image to add
     */
    public void addToQueue(@NonNull ImageQueueItem image) {
        repository.addToQueue(Collections.singletonList(image));
    }

    /**
     * Adds a collection of items to the queue
     * @param images Images to add
     */
    public void addToQueue(@NonNull Collection<ImageQueueItem> images) {
        repository.addToQueue(images);
    }

    /**
     * Removes an item from the queue
     * @param image Image to remove
     */
    public void removeFromQueue(@NonNull ImageQueueItem image) {
        repository.removeFromQueue(Collections.singletonList(image));
    }

    /**
     * Removes all items from the queue
     * @param images Images to remove
     */
    public void removeFromQueue(@NonNull Collection<ImageQueueItem> images) {
        repository.removeFromQueue(images);
    }

    /**
     * Replace one item of the queue by another
     * @param oldImage Image to remove
     * @param newImage Image to place on old position
     */
    public void replaceQueueItem(@NonNull ImageQueueItem oldImage, @NonNull ImageQueueItem newImage) {
            repository.replaceQueueItem(oldImage, newImage);
    }
}
