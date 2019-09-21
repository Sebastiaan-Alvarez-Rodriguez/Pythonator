package com.python.pythonator.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.python.pythonator.R;
import com.python.pythonator.backend.bluetooth.BluetoothServer;
import com.python.pythonator.backend.bluetooth.connectListener;
import com.python.pythonator.ui.main.view.SectionsPagerAdapter;

public class MainActivity extends AppCompatActivity implements TabLayout.OnTabSelectedListener, connectListener {
    private View view;
    private ViewPager view_pager;
    private TabLayout tab_layout;
    private MenuItem bluetooth_search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findGlobalViews();
        setupActionBar();
        setupViewPager();

        BluetoothServer.getServer().activate(this);
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.main_menu_settings:
//                intent = new Intent(this, SettingsActivity.class);
//                startActivity(intent);
                break;
            case R.id.main_menu_bluetooth:
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
                s.setAction("Try again", v -> BluetoothServer.getServer().activate(this));
                s.show();
            } else {
                BluetoothServer.getServer().connect(this);
            }
        }
    }

    @Override
    public void noBluetooth() {
        runOnUiThread(() -> bluetooth_search.setIcon(R.drawable.ic_bluetooth_disabled));
    }

    @Override
    public void isConnected() {
        runOnUiThread(() -> bluetooth_search.setIcon(R.drawable.ic_bluetooth_connected));
    }

    @Override
    public void isPending() {
        runOnUiThread(() -> bluetooth_search.setIcon(R.drawable.ic_bluetooth_searching));
    }

    @Override
    public void notConnected() {
        runOnUiThread(() -> bluetooth_search.setIcon(R.drawable.ic_bluetooth_disabled));
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