package com.python.pythonator.backend.bluetooth.connector;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.python.pythonator.backend.bluetooth.broadcast.discovery.BroadcastDiscoveryResultInterface;
import com.python.pythonator.backend.bluetooth.broadcast.discovery.DiscoveryBroadcastHandler;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.UUID;
import java.util.concurrent.Executors;

public class BluetoothConnector implements BroadcastDiscoveryResultInterface {

    private BluetoothAdapter adapter;
    private DiscoveryBroadcastHandler broadcast_handler;
    private final Context application_context;

    private volatile boolean is_searching;
    private volatile String server_name;

    private BluetoothConnectorInterface connector_interface;

    //For board, use well known: 00001101-0000-1000-8000-00805F9B34FB?
    private static final UUID uuid = UUID.fromString("E1D75160-DBE1-11E9-AAEF-0800200C9A66");

    public BluetoothConnector(BluetoothAdapter adapter, Context application_context) {
        this.adapter = adapter;
        this.application_context = application_context;
        is_searching = false;
    }

    @Override
    public void onDeviceFound(@NonNull BluetoothDevice device) {
        if (device.getName() == null)
            return;
        String name = device.getName();
        String address = device.getAddress();
        Log.e("Connector", "Name: "+name+". MAC: "+address);
        if (name.equals(server_name)) {
            stop_search();
            broadcast_handler.stopBroadcast();
            BluetoothSocket bluetooth_socket;
            try {
                 bluetooth_socket = device.createRfcommSocketToServiceRecord(uuid);
                device.createBond();
                bluetooth_socket.connect();
                connector_interface.onConnectResult(BluetoothConnectState.CONNECTED, bluetooth_socket);
            } catch (IOException initial) {
                Log.e("Connector", "Normal instantiation failed. Trying hack...");
                try {
                    Class<?> clazz = device.getClass();
                    Class<?>[] paramTypes = new Class<?>[]{Integer.TYPE};
                    Method m = clazz.getMethod("createRfcommSocket", paramTypes);
                    Object[] params = new Object[]{1};
                    bluetooth_socket = (BluetoothSocket) m.invoke((device), params);
                    device.createBond();
                    bluetooth_socket.connect();
                    Log.e("Connector", "Hack worked just fine");
                    connector_interface.onConnectResult(BluetoothConnectState.CONNECTED, bluetooth_socket);
                } catch (Exception second) {
                    Log.e("Connector", "Reflection hack failed!");
                    connector_interface.onConnectResult(BluetoothConnectState.NOT_CONNECTED, null);
                }
            }
        }
    }

    @Override
    public void onSearchFinished(int amount_found) {
        if (amount_found == 0)
            connector_interface.onConnectResult(BluetoothConnectState.NO_LOCATION, null);
        else
            connector_interface.onConnectResult(BluetoothConnectState.NOT_FOUND, null);
        stop_search();
    }

    public void search(@NonNull String server_name, BluetoothConnectorInterface connector_interface) {
        if (!adapter.isEnabled()) {
            Log.e("OOF", "Bluetooth is off");
            connector_interface.onConnectResult(BluetoothConnectState.NO_BLUETOOTH, null);
            return;
        }

        this.server_name = server_name;
        this.connector_interface = connector_interface;
        is_searching = true;
        broadcast_handler = new DiscoveryBroadcastHandler(application_context, this);
        Executors.newSingleThreadExecutor().execute(() -> broadcast_handler.startBroadcast());
        if (adapter.isDiscovering())
            adapter.cancelDiscovery();
        adapter.startDiscovery();
    }

    public void stop_search() {
        if (is_searching) {
            if (adapter.isDiscovering())
                adapter.cancelDiscovery();
            is_searching = false;
        }
    }
}
