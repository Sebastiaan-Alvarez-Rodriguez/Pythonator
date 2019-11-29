package com.python.pythonator.structures.queue;

import com.python.pythonator.backend.bluetooth.sender.SendListener;
import com.python.pythonator.structures.Image;

public class ImageQueueItem extends QueueItem<Image> {
    public ImageQueueItem(Image item) {
        super(item);
    }

    public ImageQueueItem(Image item, SendListener.SendState sent) {
        super(item, sent);
    }

    public ImageQueueItem(String path) {
        super(new Image(path));
    }
}
