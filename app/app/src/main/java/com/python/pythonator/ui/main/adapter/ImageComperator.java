package com.python.pythonator.ui.main.adapter;

import androidx.annotation.NonNull;

import com.python.pythonator.structures.ImageQueueItem;
import com.python.pythonator.ui.templates.adapter.Adapter;
import com.python.pythonator.ui.templates.adapter.Comperator;

public class ImageComperator extends Comperator<ImageQueueItem> {

    public ImageComperator(@NonNull Adapter<ImageQueueItem> adapter) {
        super(adapter);
    }

    @Override
    public int compare(ImageQueueItem o1, ImageQueueItem o2) {
        return Boolean.compare(o1.isSent(), o2.isSent());
    }

    @Override
    public boolean areContentsTheSame(ImageQueueItem oldItem, ImageQueueItem newItem) {
        return oldItem.get().getPath().equals(newItem.get().getPath()) && oldItem.isSent() == newItem.isSent();
    }

    @Override
    public boolean areItemsTheSame(ImageQueueItem item1, ImageQueueItem item2) {
        return item1 == item2;
    }
}
