package com.python.pythonator.backend.bluetooth.broadcast.discovery;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.IntentFilter;
import android.util.Log;

import androidx.annotation.NonNull;

public class DiscoveryBroadcastHandler implements BroadcastDiscoveryResultInterface {
    private Context application_context;
    private BluetoothDiscoveryBroadcast broadcast;
    private BroadcastDiscoveryResultInterface result_interface;

    public DiscoveryBroadcastHandler(@NonNull Context application_context, BroadcastDiscoveryResultInterface result_interface) {
        this.application_context = application_context;
        this.result_interface = result_interface;
        broadcast = new BluetoothDiscoveryBroadcast(this);
    }

    public void startBroadcast() {
        Log.e("DiscoveryHandle", "Starting broadcast");
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        application_context.registerReceiver(broadcast, filter);
        Log.e("DiscoveryHandle", "Started broadcast");
    }

    public void stopBroadcast() {
        application_context.unregisterReceiver(broadcast);
        Log.e("DiscoveryHandle", "Stopped broadcast");
    }

    @Override
    public void onDeviceFound(@NonNull BluetoothDevice device) {
        result_interface.onDeviceFound(device);
    }

    @Override
    public void onSearchFinished(int amount_found) {
        result_interface.onSearchFinished(amount_found);
        stopBroadcast();
    }
}
