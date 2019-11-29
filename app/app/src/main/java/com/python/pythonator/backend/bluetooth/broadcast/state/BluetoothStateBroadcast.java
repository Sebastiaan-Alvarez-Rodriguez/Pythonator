package com.python.pythonator.backend.bluetooth.broadcast.state;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;

import com.python.pythonator.backend.bluetooth.connector.BluetoothConnectState;

public class BluetoothStateBroadcast extends BroadcastReceiver {
    private StateChangeInterface listen_interface;

    public BluetoothStateBroadcast(@NonNull StateChangeInterface listen_interface) {
        this.listen_interface = listen_interface;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)) {
            listen_interface.onStateChange(BluetoothConnectState.CONNECTED);
        } else if (BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED.equals(action)) {
            //Device is about to disconnect
        } else if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) {
            listen_interface.onStateChange(BluetoothConnectState.NOT_CONNECTED);
        }
    }
}
