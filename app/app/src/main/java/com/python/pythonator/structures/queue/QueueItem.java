package com.python.pythonator.structures.queue;

import androidx.annotation.NonNull;


public class QueueItem<T> {
    private T item;
    private @NonNull ImageState state;

    private StateChangeListener listener;

    public QueueItem(T item) {
        this.item = item;
        this.listener = null;
        this.state = ImageState.NOT_SENT;
    }

    public QueueItem(T item, @NonNull ImageState state) {
        this.item = item;
        this.state = state;
        this.listener = null;
    }

    public T get() {
        return item;
    }

    public @NonNull ImageState getState() {
        return state;
    }

    public void setState(@NonNull ImageState state) {
        if (this.state == state)
            return;
        this.state = state;
        if (listener != null)
            listener.onChanged(state);
    }

    public void setListener(@NonNull StateChangeListener listener) {
        this.listener = listener;
    }
}
