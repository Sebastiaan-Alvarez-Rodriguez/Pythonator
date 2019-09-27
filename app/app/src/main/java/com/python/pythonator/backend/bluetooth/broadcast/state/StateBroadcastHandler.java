package com.python.pythonator.backend.bluetooth.broadcast.state;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.IntentFilter;
import android.util.Log;

import androidx.annotation.NonNull;

public class StateBroadcastHandler {
    private BluetoothStateBroadcast broadcast;
    private Context application_context;

    public StateBroadcastHandler(@NonNull Context application_context, @NonNull StateChangeInterface listen_interface) {
        this.application_context = application_context;
        broadcast = new BluetoothStateBroadcast(listen_interface);
    }

    public void startBroadcast() {
        Log.e("StateHandle", "Starting broadcast");
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED);
        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);

        application_context.registerReceiver(broadcast, filter);
        Log.e("StateHandle", "Started broadcast");
    }

    public void stopBroadcast() {
        application_context.unregisterReceiver(broadcast);
        Log.e("StateHandle", "Stopped broadcast");
    }
}