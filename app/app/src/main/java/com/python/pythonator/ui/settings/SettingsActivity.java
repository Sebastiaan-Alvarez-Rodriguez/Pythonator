package com.python.pythonator.ui.settings;

import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.python.pythonator.R;

public class SettingsActivity extends AppCompatActivity {
    private EditText bluetooth_host, bluetooth_search;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        findGlobalViews();
        setupActionBar();
        setValues();
    }

    private void findGlobalViews() {
        bluetooth_host = findViewById(R.id.settings_bluetooth_host);
        bluetooth_search = findViewById(R.id.settings_bluetooth_retry);
    }

    private void setValues() {
        preferences = PreferenceManager.getDefaultSharedPreferences(this);

        String bluetooth_hostname = preferences.getString("bluetooth_host", "Pythonator");
        bluetooth_host.setText(bluetooth_hostname);
        int bluetooth_search_secs = preferences.getInt("retries", 4);
        bluetooth_search.setText(String.valueOf(bluetooth_search_secs));
    }

    private void setupActionBar() {
        Toolbar myToolbar = findViewById(R.id.settings_toolbar);
        setSupportActionBar(myToolbar);

        ActionBar actionbar = getSupportActionBar();
        if (actionbar != null) {
            actionbar.setTitle("Settings");
            actionbar.setDisplayHomeAsUpEnabled(true);
            Drawable icon = myToolbar.getNavigationIcon();
            if (icon != null) {
                icon.setColorFilter(getResources().getColor(R.color.colorWindowBackground, null), PorterDuff.Mode.SRC_IN);
                myToolbar.setNavigationIcon(icon);
            }
        }
    }

    private boolean checkValues() {
        String new_host = bluetooth_host.getText().toString();
        int new_time = Integer.parseInt(bluetooth_search.getText().toString());

        if (new_host.length() == 0)
            return false;

        return new_time >= 2;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.menu_ok:
                if (checkValues()) {
                    String new_host = bluetooth_host.getText().toString();
                    int new_time = Integer.parseInt(bluetooth_search.getText().toString());

                    SharedPreferences.Editor editor = preferences.edit();

                    editor.putString("bluetooth_host", new_host);
                    editor.putInt("retries", new_time);
                    editor.apply();
                    finish();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_ok, menu);
        return true;
    }
}
