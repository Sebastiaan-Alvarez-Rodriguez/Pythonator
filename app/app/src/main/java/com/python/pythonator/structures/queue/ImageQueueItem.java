package com.python.pythonator.structures.queue;

import com.python.pythonator.structures.Image;

public class ImageQueueItem extends QueueItem<Image> {
    public ImageQueueItem(Image item) {
        super(item);
    }

    public ImageQueueItem(Image item, ImageState sent) {
        super(item, sent);
    }

    public ImageQueueItem(String path) {
        super(new Image(path));
    }
}
