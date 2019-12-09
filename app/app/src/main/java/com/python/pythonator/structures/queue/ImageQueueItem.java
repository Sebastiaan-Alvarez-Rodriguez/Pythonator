package com.python.pythonator.structures.queue;

import com.python.pythonator.structures.Image;

/**
 * A minimal implementation of a {@link QueueItem} with type {@link Image}.
 * Needed, because Java otherwise cannot handle our templated classes,
 * such as {@link com.python.pythonator.ui.templates.adapter.Adapter}
 */
public class ImageQueueItem extends QueueItem<Image> {
    public ImageQueueItem(Image item) {
        super(item);
    }

    public ImageQueueItem(Image item, ImageState sent) {
        super(item, sent);
    }

    /**
     * Construct an ImageQueueItem from a path to an image
     * @param path Path to image
     */
    public ImageQueueItem(String path) {
        super(new Image(path));
    }
}
