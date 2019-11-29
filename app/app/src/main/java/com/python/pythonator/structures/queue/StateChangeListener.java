package com.python.pythonator.structures.queue;

import com.python.pythonator.backend.bluetooth.sender.SendListener;

public interface StateChangeListener {
    void onChanged(SendListener.SendState new_state);
}
