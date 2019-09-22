package com.python.pythonator.backend.bluetooth.broadcast;

import android.bluetooth.BluetoothDevice;

import androidx.annotation.NonNull;

public interface BroadcastResultInterface {
    void onDeviceFound(@NonNull BluetoothDevice device);
    void onSearchFinished(int amount_found);
}
