package com.python.pythonator.backend.bluetooth.broadcast.discovery;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BluetoothDiscoveryBroadcast extends BroadcastReceiver {
    private BroadcastDiscoveryResultInterface result_interface;
    private int found;

    public BluetoothDiscoveryBroadcast(BroadcastDiscoveryResultInterface result_interface) {
        this.result_interface = result_interface;
        found = 0;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Log.e("Broad", "action received: " + action);

        if (BluetoothDevice.ACTION_FOUND.equals(action)) {
            found += 1;
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            Log.e("Broad", "found device: " + device.getName());
            result_interface.onDeviceFound(device);
        } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
            result_interface.onSearchFinished(found);
        }
    }
}
