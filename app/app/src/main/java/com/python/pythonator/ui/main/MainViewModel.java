package com.python.pythonator.ui.main;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.python.pythonator.backend.QueueRepository;
import com.python.pythonator.backend.bluetooth.SendListener;
import com.python.pythonator.structures.Image;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class MainViewModel extends AndroidViewModel {
    private LiveData<List<Image>> cache = null;
    private QueueRepository repository;

    public MainViewModel(@NonNull Application application) {
        super(application);
        repository = new QueueRepository(application.getApplicationContext());
    }

    public LiveData<List<Image>> getQueue() {
        if (cache == null)
            cache = repository.getQueue();
        return cache;
    }

    public void addToQueue(@NonNull Image image) {
        repository.addToQueue(Collections.singletonList(image));
    }

    public void addToQueue(@NonNull Collection<Image> image) {
        repository.addToQueue(image);
    }

    public void removeFromQueue(@NonNull Image image) {
        repository.removeFromQueue(Collections.singletonList(image));
    }

    public void removeFromQueue(@NonNull Collection<Image> images) {
        repository.removeFromQueue(images);
    }

    public void replaceQueueItem(@NonNull Image oldImage, @NonNull Image newImage) {
            repository.replaceQueueItem(oldImage, newImage);
    }

    public void trySendImage(@NonNull Image image, int retries, SendListener listener) {
        repository.trySendImage(image, retries, listener);
    }
}
