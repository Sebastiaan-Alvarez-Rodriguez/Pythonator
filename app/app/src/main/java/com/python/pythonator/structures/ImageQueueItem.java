package com.python.pythonator.structures;

public class ImageQueueItem extends QueueItem<Image> {
    public ImageQueueItem(Image item) {
        super(item);
    }

    public ImageQueueItem(Image item, boolean sent) {
        super(item, sent);
    }

    public ImageQueueItem(String path) {
        super(new Image(path));
    }
}
