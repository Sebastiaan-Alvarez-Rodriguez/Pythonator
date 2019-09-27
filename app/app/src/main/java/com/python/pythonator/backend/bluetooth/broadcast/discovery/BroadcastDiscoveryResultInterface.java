package com.python.pythonator.backend.bluetooth.broadcast.discovery;

import android.bluetooth.BluetoothDevice;

import androidx.annotation.NonNull;

public interface BroadcastDiscoveryResultInterface {
    void onDeviceFound(@NonNull BluetoothDevice device);
    void onSearchFinished(int amount_found);
}
