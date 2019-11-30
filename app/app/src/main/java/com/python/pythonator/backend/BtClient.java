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
import com.python.pythonator.structures.queue.ImageQueueItem;
import com.python.pythonator.structures.queue.ImageState;

import me.aflak.bluetooth.Bluetooth;
import me.aflak.bluetooth.interfaces.BluetoothCallback;
import me.aflak.bluetooth.interfaces.DeviceCallback;
import me.aflak.bluetooth.interfaces.DiscoveryCallback;

public class BtClient implements DeviceCallback, BluetoothCallback {

    private static volatile BtClient INSTANCE;

    public static BtClient getClient(Context application_context) {
        if (INSTANCE == null) {
            synchronized (BtClient.class) {
                if (INSTANCE == null)
                    INSTANCE = new BtClient(application_context);
            }
        }
        return INSTANCE;
    }

    private Bluetooth bluetooth;
    private ConnectListener c_listener;
    private BluetoothListener bt_listener;
    private BtSender bt_sender;

    private BtClient(Context application_context) {
        this.bluetooth = new Bluetooth(application_context);
        bt_sender = new BtSender(application_context);
        bluetooth.setDeviceCallback(this);
        bluetooth.setBluetoothCallback(this);
    }


    public void setConnectListener(ConnectListener listener) {
        c_listener = listener;
    }

    public void setBluetoothListener(BluetoothListener listener) {
        bt_listener = listener;
    }

    public synchronized boolean connect(String devicename) {
        if (!bluetooth.isEnabled())
            return false;
        if (bluetooth.isConnected())
            bluetooth.disconnect();

        bluetooth.setDiscoveryCallback(new DiscoveryCallback() {
            @Override
            public void onDiscoveryStarted() {
                if (c_listener != null)
                    c_listener.connectStatus(ConnectState.CONNECTING);
            }

            @Override
            public void onDiscoveryFinished() {
                if (c_listener != null)
                    c_listener.connectStatus(ConnectState.NOT_FOUND);
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
                    c_listener.connectStatus(ConnectState.ERROR);
            }

            @Override
            public void onDevicePaired(BluetoothDevice device) {}
            @Override
            public void onDeviceUnpaired(BluetoothDevice device) {}
        });
        bluetooth.startScanning();
        return true;
    }

    public boolean isBluetoothEnabled() {
        return bluetooth.isEnabled();
    }

    public synchronized void enableBt(Activity caller) {
        bluetooth.showEnableDialog(caller);
    }
    public void enableBtResult(int req, int res) {
        bluetooth.onActivityResult(req, res);
    }
    public void onStart() {
        bluetooth.onStart();
    }
    public void onStop() {
        bluetooth.onStop();
    }
    public boolean isConnected() {
        return bluetooth.isConnected();
    }

    @Override
    public void onDeviceConnected(BluetoothDevice device) {
        Log.i("BtC", "Connection established");
        if (c_listener != null)
            c_listener.connectStatus(ConnectState.CONNECTED);
        bt_sender.start(bluetooth.getSocket());
    }

    @Override
    public void onDeviceDisconnected(BluetoothDevice device, String message) {
        Log.i("BtC", "Disconnected!");
        bt_sender.stop();
    }

    @Override
    public void onMessage(byte[] message) {
        Log.i("BtC", "We received a message...");
        if (message[0] == 0) {
            Log.i("BtC", "\tAnd it was from the server, since message[0] == 0x00");
            ImageQueueItem first_sent = bt_sender.getFirstSent();
            if (first_sent != null)
                first_sent.setState(ImageState.DRAWN);
        } else {
            Log.w("BtC", "\tBut it was not from the server, since message[0] != 0x00");
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
            c_listener.connectStatus(ConnectState.DISCONNECTED);
    }

    @Override
    public void onBluetoothOff() {}

    @Override
    public void onUserDeniedActivation() {
        if (bt_listener != null)
            bt_listener.onUserDeniedActivation();
    }

//    public static byte[] longToBytes(long l) {
//        byte[] result = new byte[8];
//        for (int i = 7; i >= 0; i--) {
//            result[i] = (byte)(l & 0xFF);
//            l >>= 8;
//        }
//        return result;
//    }
//    public static long bytesToLong(byte[] b) {
//        long result = 0;
//        for (int i = 0; i < 8; i++) {
//            result <<= 8;
//            result |= (b[i] & 0xFF);
//        }
//        return result;
//    }

    public synchronized boolean sendImage(@NonNull ImageQueueItem item) {
        if (!isConnected())
            return false;
        bt_sender.sendImage(item);
        return true;
    }
//    private Notification createNotification() {
//        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//        assert notificationManager != null;
//        String channel_id = "Python channel";
//        NotificationChannel channel = new NotificationChannel(channel_id, "Python channel", NotificationManager.IMPORTANCE_HIGH);
//            channel.setDescription("Python service running");
//            channel.enableLights(true);
//            channel.setLightColor(android.graphics.Color.rgb(102, 245, 66));
//            channel.enableVibration(true);
//            channel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
//
//        notificationManager.createNotificationChannel(channel);
//
//        Intent notificationIntent = new Intent(this, MainActivity.class);
//        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//
//
//        Notification.Builder b =  new Notification.Builder(this, channel_id);
//        return b.setContentTitle("Python service running")
//                .setContentText("Keeping bluetooth active")
//                .setContentIntent(pendingIntent)
//                .setSmallIcon(R.mipmap.ic_launcher)
//                .setTicker("Ticker text")
//                .build();
//    }
}
