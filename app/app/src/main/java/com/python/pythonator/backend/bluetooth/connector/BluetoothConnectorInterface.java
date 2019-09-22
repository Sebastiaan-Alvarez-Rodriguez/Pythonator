package com.python.pythonator.backend.bluetooth.connector;

import android.bluetooth.BluetoothSocket;

import androidx.annotation.Nullable;

public interface BluetoothConnectorInterface {
    void onConnectResult(BluetoothConnectState state, @Nullable BluetoothSocket socket);
}
