package com.honeywell.sample.dataeditingplugin;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements Common {
    private static final String TAG = "MainActivity";

    private static final String SCANNER_NAME = "dcs.scanner.imager";
    private static final String PROFILE_NAME = "DEFAULT";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        toggleScanMode();
        finish();
    }

    private void toggleScanMode() {
        Log.d(TAG, "Toggling scan mode");
        Bundle properties = new Bundle();

        // get current scan mode
        SharedPreferences prefs = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        String currentMode = prefs.getString(PREF_KEY_SCAN_MODE, "");

        // set new scan mode
        if (SCAN_MODE_NORMAL.equals(currentMode)) {
            notifyMessage(getString(R.string.notify_scan_mode_continuous));
            prefs.edit().putString(PREF_KEY_SCAN_MODE, SCAN_MODE_AUTOMATIC).commit();
            currentMode = SCAN_MODE_AUTOMATIC;
            properties.putString("TRIG_CONTROL_MODE", "clientControl");
            properties.putString("TRIG_SCAN_MODE", "continuous");
        } else {
            notifyMessage(getString(R.string.notify_scan_mode_normal));
            prefs.edit().putString(PREF_KEY_SCAN_MODE, SCAN_MODE_NORMAL).commit();
            currentMode = SCAN_MODE_NORMAL;
        }

        // claim scanner, load profile, and apply properties
        Scanner scanner = new Scanner(this);

        // make sure scanner is off before claiming to ensure it doesn't read a barcode
        // it is expected to fail the first time since we haven't claimed the scanner before
        scanner.scan(false);

        scanner.claim(SCANNER_NAME, PROFILE_NAME, properties);

        // make sure scanner is off
        scanner.scan(false);

        // if automatic mode, make sure scanner is on
        if (SCAN_MODE_AUTOMATIC.equals(currentMode)) {
            scanner.scan(true);
        }
    }

    private void notifyMessage(String message) {
        Log.d(TAG, message);
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
