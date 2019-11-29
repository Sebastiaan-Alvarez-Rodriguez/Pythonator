package com.python.pythonator.structures.queue;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.python.pythonator.backend.bluetooth.sender.SendListener;

public class QueueItem<T> {
    private T item;
    private SendListener.SendState state;

    private StateChangeListener listener;

    public QueueItem(T item) {
        this.item = item;
        this.listener = null;
        this.state = null;
    }

    public QueueItem(T item, SendListener.SendState state) {
        this.item = item;
        this.state = state;
        this.listener = null;
    }

    public T get() {
        return item;
    }

    public @Nullable SendListener.SendState getState() {
        return state;
    }

    public void setState(SendListener.SendState state) {
        this.state = state;
        if (listener != null)
            listener.onChanged(state);
    }

    public boolean isSent() {
        return state == SendListener.SendState.SENT;
    }


    public void setListener(@NonNull StateChangeListener listener) {
        this.listener = listener;
    }
}
