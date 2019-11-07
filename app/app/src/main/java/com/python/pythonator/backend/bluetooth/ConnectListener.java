package com.python.pythonator.backend.bluetooth;

import com.python.pythonator.backend.bluetooth.connector.BluetoothConnectState;

public interface ConnectListener {
    void onChangeState(BluetoothConnectState state);
}
