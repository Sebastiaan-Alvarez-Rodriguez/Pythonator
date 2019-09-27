package com.python.pythonator.backend.bluetooth.broadcast.state;

import com.python.pythonator.backend.bluetooth.connector.BluetoothConnectState;

public interface StateChangeInterface {
    void onStateChange(BluetoothConnectState new_state);
}
