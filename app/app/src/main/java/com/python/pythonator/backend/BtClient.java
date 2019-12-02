package com.python.pythonator.backend;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.python.pythonator.backend.connection.BluetoothListener;
import com.python.pythonator.backend.connection.ConnectListener;
import com.python.pythonator.backend.connection.ConnectState;
import com.python.pythonator.backend.transfer.BtSender;
import com.python.pythonator.backend.transfer.ErrorListener;
import com.python.pythonator.backend.transfer.ErrorType;
import com.python.pythonator.structures.queue.ImageQueueItem;
import com.python.pythonator.structures.queue.ImageState;

import me.aflak.bluetooth.Bluetooth;
import me.aflak.bluetooth.interfaces.BluetoothCallback;
import me.aflak.bluetooth.interfaces.DeviceCallback;
import me.aflak.bluetooth.interfaces.DiscoveryCallback;

/**
 * Client to handle bluetooth connections
 */
@SuppressWarnings("WeakerAccess")
public class BtClient implements DeviceCallback, BluetoothCallback {
    // Bluetooth main interacter
    private Bluetooth bluetooth;
    // Connection listener, to receive connection state update
    private ConnectListener c_listener;
    // Bluetooth listener, to receive bluetooth state updates
    private BluetoothListener bt_listener;
    // Error listener, to receive updates when the server sends error code responses
    private ErrorListener er_listener;

    // Sender object, to handle sending our items
    private BtSender bt_sender;

    public BtClient(@NonNull Context application_context) {
        this.bluetooth = new Bluetooth(application_context);
        bt_sender = new BtSender(application_context);
        bluetooth.setDeviceCallback(this);
        bluetooth.setBluetoothCallback(this);
        c_listener = null;
        bt_listener = null;
        er_listener = null;
    }


    /**
     * Sets a {@link ConnectListener} to receive connection state updates
     * @param listener Listener to receive connection state updates
     */
    public void setConnectListener(ConnectListener listener) {
        c_listener = listener;
    }

    /**
     * Sets a {@link BluetoothListener} to receive bluetooth state updates
     * @param listener Listener to receive bluetooth state updates
     */
    public void setBluetoothListener(BluetoothListener listener) {
        bt_listener = listener;
    }

    public void setErrorListener(ErrorListener listener) {
        er_listener = listener;
    }
    /**
     * Try to connect to a given device
     * @param devicename Devicename to connect to
     */
    public synchronized void connect(String devicename) {
        if (bluetooth.isConnected())
            bluetooth.disconnect();

        bluetooth.setDiscoveryCallback(new DiscoveryCallback() {
            @Override
            public void onDiscoveryStarted() {
                if (c_listener != null)
                    c_listener.onConnectStateChange(ConnectState.CONNECTING);
            }

            @Override
            public void onDiscoveryFinished() {
                if (c_listener != null)
                    c_listener.onConnectStateChange(ConnectState.NOT_FOUND);
            }

            @Override
            public void onDeviceFound(BluetoothDevice device) {
                if (device == null || device.getName() == null)
                    return;
                Log.i("BtC", "Found device: "+device.getName());
                if (devicename.equals(device.getName())) {
                    bluetooth.stopScanning();
                    Log.i("BtC", "Target found!");
                    bluetooth.connectToDevice(device, true, true);
                }
            }

            @Override
            public void onError(int errorCode) {
                if (c_listener != null)
                    c_listener.onConnectStateChange(ConnectState.ERROR);
            }

            @Override
            public void onDevicePaired(BluetoothDevice device) {}
            @Override
            public void onDeviceUnpaired(BluetoothDevice device) {}
        });
        bluetooth.startScanning();
    }

    /**
     * @return <code>true</code> if bluetooth is enabled, <code>false</code> otherwise
     */
    public boolean isBluetoothEnabled() {
        return bluetooth.isEnabled();
    }

    /**
     * Requests the user to enable bluetooth. Let the caller Activity send its onActivityResult calls
     * to {@link #enableBluetoothResult(int, int)} for further processing
     * @param caller Activity to dispaly request to the user
     */
    public synchronized void enableBluetooth(Activity caller) {
        bluetooth.showEnableDialog(caller);
    }

    /**
     * Send Activity onActivityResult calls from any Activity calling {@link #enableBluetooth(Activity)}
     * to this place, to handle result from {@link #enableBluetooth(Activity)} calls
     * @param req Requestcode as received by onActivityResult
     * @param res Resultcode as received by onActivityResult
     */
    public void enableBluetoothResult(int req, int res) {
        bluetooth.onActivityResult(req, res);
    }

    /**
     * Let UI onStart function call this function
     */
    public void onStart() {
        bluetooth.onStart();
    }

    /**
     * Let UI onStop function call this function
     */
    public void onStop() {
        bluetooth.onStop();
    }

    /**
     * Let UI onDestroy function call this function, to properly stop Bluetooth transmissions
     */
    public void onDestroy() {
        bt_sender.stop();
    }

    /**
     * @return <code>true</code> if we are currently connected to a device, <code>false</code> otherwise
     */
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean isConnected() {
        return bluetooth.isConnected();
    }

    /**
     * Send an image over bluetooth to the connected server
     * @param item Image to send to server
     * @return <code>false</code> if we are not connected to a device, <code>true</code> otherwise
     */
    public synchronized boolean sendImage(@NonNull ImageQueueItem item) {
        if (!isConnected())
            return false;
        bt_sender.sendImage(item);
        return true;
    }

    @Override
    public void onDeviceConnected(BluetoothDevice device) {
        Log.i("BtC", "Connection established");
        if (c_listener != null)
            c_listener.onConnectStateChange(ConnectState.CONNECTED);
        bt_sender.updateSocket(bluetooth.getSocket());
    }

    @Override
    public void onDeviceDisconnected(BluetoothDevice device, String message) {
        Log.i("BtC", "Disconnected!");
    }

    @Override
    public void onMessage(byte[] message) {
        Log.i("BtC", "We received a message...");
        ImageQueueItem first_sent = bt_sender.getFirstSent();
        if (message[0] == 0) {
            Log.i("BtC", "\tAnd it was from the server, since message[0] == 0x00");
            if (first_sent != null)
                first_sent.setState(ImageState.DRAWN);
        } else {
            if (first_sent != null)
                first_sent.setState(ImageState.NOT_SENT);
            if (er_listener != null)
                switch (message[0]) {
                    case 1: er_listener.onError(ErrorType.OUT_OF_BOUNDS); break;
                    case 2: er_listener.onError(ErrorType.UNKNOWN_COMMAND); break;
                    case 3: default:
                    er_listener.onError(ErrorType.IO_ERROR); break;
                }

            Log.w("BtC", "\tServer failure (message != 0x00");
        }
    }

    @Override
    public void onError(int errorCode) {}

    @Override
    public void onConnectError(BluetoothDevice device, String message) {}

    @Override
    public void onBluetoothTurningOn() {}

    @Override
    public void onBluetoothOn() {
        if (bt_listener != null)
            bt_listener.onBluetoothOn();
    }

    @Override
    public void onBluetoothTurningOff() {
        bt_sender.stop();
        if (c_listener != null)
            c_listener.onConnectStateChange(ConnectState.DISCONNECTED);
    }

    @Override
    public void onBluetoothOff() {}

    @Override
    public void onUserDeniedActivation() {
        if (bt_listener != null)
            bt_listener.onUserDeniedActivation();
    }
}
