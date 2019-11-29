package com.python.pythonator.structures;

public class QueueItem<T> {
    private T item;
    private boolean sent;

    public QueueItem(T item) {
        this.item = item;
    }

    public QueueItem(T item, boolean sent) {
        this.item = item;
        this.sent = sent;
    }

    public boolean isSent() {
        return sent;
    }

    public void setSent(boolean value) {
        sent = value;
    }

    public T get() {
        return item;
    }
}
