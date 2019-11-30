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

public class MainViewModel extends AndroidViewModel {
    private LiveData<List<ImageQueueItem>> cache = null;
    private QueueRepository repository;

    public MainViewModel(@NonNull Application application) {
        super(application);
        repository = new QueueRepository();
    }

    public LiveData<List<ImageQueueItem>> getQueue() {
        if (cache == null)
            cache = repository.getQueue();
        return cache;
    }

    public void addToQueue(@NonNull ImageQueueItem image) {
        repository.addToQueue(Collections.singletonList(image));
    }

    public void addToQueue(@NonNull Collection<ImageQueueItem> image) {
        repository.addToQueue(image);
    }

    public void removeFromQueue(@NonNull ImageQueueItem image) {
        repository.removeFromQueue(Collections.singletonList(image));
    }

    public void removeFromQueue(@NonNull Collection<ImageQueueItem> images) {
        repository.removeFromQueue(images);
    }

    public void replaceQueueItem(@NonNull ImageQueueItem oldImage, @NonNull ImageQueueItem newImage) {
            repository.replaceQueueItem(oldImage, newImage);
    }
}
