package com.python.pythonator.ui.queue.model;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.python.pythonator.backend.QueueRepository;
import com.python.pythonator.structures.Image;

import java.util.Collection;
import java.util.List;

public class QueueViewModel extends AndroidViewModel {
    private LiveData<List<Image>> cache = null;
    private QueueRepository repository;

    public QueueViewModel(@NonNull Application application) {
        super(application);
        repository = new QueueRepository(application.getApplicationContext());
    }

    public LiveData<List<Image>> getQueue() {
        if (cache == null)
            cache = repository.getQueue();
        return cache;
    }

    public void addToQueue(@NonNull Image image) {
        repository.addToQueue(image);
    }

    public void removeFromQueue(@NonNull Collection<Image> images) {
        repository.removeFromQueue(images);
    }
}
