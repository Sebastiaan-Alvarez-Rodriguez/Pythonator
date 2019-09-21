package com.python.pythonator.backend.bluetooth.broadcast;

import android.bluetooth.BluetoothDevice;

public interface BroadcastResultInterface {
    void onDeviceFound(BluetoothDevice device);
}
