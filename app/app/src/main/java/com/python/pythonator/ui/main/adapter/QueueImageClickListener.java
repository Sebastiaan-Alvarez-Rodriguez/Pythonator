package com.python.pythonator.ui.main.adapter;

/**
 * Simple interface to send click events to UI
 */
public interface QueueImageClickListener {
    /**
     * Called when thumbnail is clicked
     * @param pos Position of {@link com.python.pythonator.structures.queue.ImageQueueItem} which thumbnail was clicked
     */
    void onThumbnailClick(int pos);

    /**
     * Called when sendbutton is clicked
     * @param pos Position of {@link com.python.pythonator.structures.queue.ImageQueueItem} which sendbutton was clicked
     */
    void onSendClicked(int pos);
}
