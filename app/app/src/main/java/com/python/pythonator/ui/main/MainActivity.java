package com.python.pythonator.ui.main;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.python.pythonator.R;
import com.python.pythonator.backend.bluetooth.BluetoothServer;
import com.python.pythonator.backend.bluetooth.ConnectListener;
import com.python.pythonator.backend.bluetooth.connector.BluetoothConnectState;
import com.python.pythonator.ui.main.view.SectionsPagerAdapter;
import com.python.pythonator.ui.settings.SettingsActivity;

public class MainActivity extends AppCompatActivity implements TabLayout.OnTabSelectedListener, ConnectListener {
    private static final int REQUEST_BLUETOOTH_PERMISSION = 1;

    private View view;
    private ViewPager view_pager;
    private TabLayout tab_layout;
    private MenuItem bluetooth_search;

    private BluetoothServer server;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        server = BluetoothServer.getServer(getApplicationContext());
        setContentView(R.layout.activity_main);
        findGlobalViews();
        setupActionBar();
        setupViewPager();

        if (!checkPermission())
            askPermission();
        server.activate(this);
    }

    private void findGlobalViews() {
        view_pager = findViewById(R.id.view_pager);
        view = findViewById(R.id.main_layout);
        tab_layout = findViewById(R.id.tabs);
    }

    private void setupViewPager() {
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        view_pager.setAdapter(sectionsPagerAdapter);
        tab_layout.setupWithViewPager(view_pager);
        tab_layout.addOnTabSelectedListener(this);
    }

    private void setupActionBar() {
        Toolbar myToolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(myToolbar);

        ActionBar actionbar = getSupportActionBar();
        if (actionbar != null)
            actionbar.setTitle("Pythonator");
    }

    private boolean checkPermission() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_ADMIN) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private void askPermission() {
        requestPermissions(new String[]{Manifest.permission.BLUETOOTH,
                Manifest.permission.BLUETOOTH_ADMIN,
                Manifest.permission.ACCESS_COARSE_LOCATION},
                REQUEST_BLUETOOTH_PERMISSION);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.main_menu_settings:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                break;
            case R.id.main_menu_bluetooth:
                if (!server.isConnected()) {
                    Log.e("Retry", "Retrying to establish server connection");
                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
                    server.connect(preferences.getString("bluetooth_host", "Pythonator"), this);
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        bluetooth_search = menu.findItem(R.id.main_menu_bluetooth);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == BluetoothServer.REQUEST_ENABLE_BLUETOOTH) {
            if (resultCode != RESULT_OK) {
                Snackbar s = Snackbar.make(view, "Bluetooth is required to communicate with the server", Snackbar.LENGTH_INDEFINITE);
                s.setAction("Try again", v -> BluetoothServer.getServer(getApplicationContext()).activate(this));
                s.show();
            } else {
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
                server.connect(preferences.getString("bluetooth_host", "Pythonator"), this);
            }
        }
    }

    @Override
    public void onChangeState(BluetoothConnectState state) {
        switch (state) {
            case PENDING:
                runOnUiThread(() -> bluetooth_search.setIcon(R.drawable.ic_bluetooth_searching));
                break;
            case CONNECTED:
                runOnUiThread(() -> bluetooth_search.setIcon(R.drawable.ic_bluetooth_connected));
                break;
            case NOT_FOUND:
                runOnUiThread(() -> bluetooth_search.setIcon(R.drawable.ic_out_of_range));
                break;
            case NO_LOCATION:
                runOnUiThread(() -> bluetooth_search.setIcon(R.drawable.ic_location_disabled));
            case NO_BLUETOOTH:
            case FAILED:
                runOnUiThread(() -> bluetooth_search.setIcon(R.drawable.ic_bluetooth_disabled));
                break;
        }
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }
}