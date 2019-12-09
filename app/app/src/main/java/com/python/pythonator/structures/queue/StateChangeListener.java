package com.python.pythonator.structures.queue;


/**
 * Interface to send state updates
 */
public interface StateChangeListener {
    /**
     * Send state updates
     * @param new_state
     */
    void onChanged(ImageState new_state);
}
