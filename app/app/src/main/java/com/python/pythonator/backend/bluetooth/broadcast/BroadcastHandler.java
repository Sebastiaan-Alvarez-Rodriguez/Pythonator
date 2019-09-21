package com.python.pythonator.backend.bluetooth.broadcast;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.IntentFilter;
import android.util.Log;

import androidx.annotation.NonNull;

public class BroadcastHandler {
    private Context application_context;
    private BluetoothBroadcast instance;

    public BroadcastHandler(@NonNull Context application_context) {
        this.application_context =application_context;
        instance = null;
    }

    public boolean startBroadcast(BroadcastResultInterface result_interface) {
        Log.e("Handle", "Starting broadcast");
        if (instance != null)
            return false;

        instance = new BluetoothBroadcast(result_interface);
        IntentFilter eventFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        // this is not strictly necessary, but you may wish to know when the discovery cycle is done as well
        eventFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        application_context.registerReceiver(instance, eventFilter);
        Log.e("Handle", "Started broadcast");
        return true;
    }

    public void stopBroadcast() {
        application_context.unregisterReceiver(instance);
        Log.e("Handle", "Stopped broadcast");
    }
}
