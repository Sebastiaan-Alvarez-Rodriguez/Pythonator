package com.python.pythonator.backend.bluetooth.broadcast;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BluetoothBroadcast extends BroadcastReceiver {
    BroadcastResultInterface result_interface;

    public BluetoothBroadcast(BroadcastResultInterface result_interface) {
        this.result_interface = result_interface;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Log.e("Broad", "action received: " + action);

        if (BluetoothDevice.ACTION_FOUND.equals(action)) {
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            Log.e("Broad", "found device: " + device.getName());
            result_interface.onDeviceFound(device);
        } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
            Log.e("Broad", "discovery complete");
            result_interface.onDeviceFound(null);
        }
    }
}
