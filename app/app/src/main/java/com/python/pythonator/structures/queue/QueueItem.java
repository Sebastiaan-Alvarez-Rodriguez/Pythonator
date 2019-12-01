package com.python.pythonator.structures.queue;

import androidx.annotation.NonNull;


/**
 * Template for an item that can be sent to a server
 * @param <T> Type of item which can be sent to a server
 */
public class QueueItem<T> {
    // The item instance to send
    private T item;
    // State of the item
    private @NonNull ImageState state;

    // StateListener for this item
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

    /**
     * @return The instance which will be sent or is sent
     */
    public T get() {
        return item;
    }

    /**
     * @return The current send state of the object
     */
    public @NonNull ImageState getState() {
        return state;
    }

    /**
     * Updates the current state of the object. Calls listener only if state is different than before
     * @param state New state of object
     */
    public void setState(@NonNull ImageState state) {
        if (this.state == state)
            return;
        this.state = state;
        if (listener != null)
            listener.onChanged(state);
    }

    /**
     * Sets a listener for the send state, to receive state updates.
     * @see ImageState
     * @param listener The listener to send updates to
     */
    public void setListener(@NonNull StateChangeListener listener) {
        this.listener = listener;
    }
}
