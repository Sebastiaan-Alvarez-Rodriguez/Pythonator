package com.python.pythonator.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.python.pythonator.R;
import com.python.pythonator.backend.bluetooth.BluetoothServer;
import com.python.pythonator.backend.bluetooth.connectListener;
import com.python.pythonator.ui.main.view.SectionsPagerAdapter;

public class MainActivity extends AppCompatActivity implements connectListener {
    private View view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        view = findViewById(R.id.main_layout);

        prepareViewPager();

        BluetoothServer.getServer().activate(this);
    }

    private void prepareViewPager() {
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == BluetoothServer.REQUEST_ENABLE_BLUETOOTH) {
            if (resultCode != RESULT_OK) {
                Snackbar s = Snackbar.make(view, "Bluetooth is required to communicate with the server", Snackbar.LENGTH_INDEFINITE);
                s.setAction("Try again", v -> BluetoothServer.getServer().activate(this));
                s.show();
            } else {
                BluetoothServer.getServer().connect(this);
            }
        }
    }

    @Override
    public void noBluetooth() {

    }

    @Override
    public void isConnected() {

    }

    @Override
    public void isPending() {

    }

    @Override
    public void notFound() {

    }
}