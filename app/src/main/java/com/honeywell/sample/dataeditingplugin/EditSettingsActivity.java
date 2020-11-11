package com.honeywell.sample.dataeditingplugin;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import static com.honeywell.sample.dataeditingplugin.Common.PREF_KEY_SCAN_MODE;
import static com.honeywell.sample.dataeditingplugin.Common.PREF_NAME;
import static com.honeywell.sample.dataeditingplugin.Common.SCAN_MODE_NORMAL;

public class EditSettingsActivity extends Activity implements Common{
    private static final String TAG = "DataEditingSettings";
    private static final String INTENT_ACTION_EDIT_SETTINGS = "com.honeywell.decode.intent.action.EDIT_SETTINGS";
    /* activity will be searched
    01-31 09:53:35.804 14248 14248 D DataEditingPlugin: Got intent to activate data edit plugin
    01-31 09:53:53.701   517  1060 I ActivityManager: START u0 {act=com.honeywell.decode.intent.action.EDIT_SETTINGS flg=0x10000000
    cmp=com.honeywell.sample.dataeditingplugin/.MainActivity (has extras)} from uid 1000 on display 0
    */

    private static final String SCANNER_NAME = "dcs.scanner.imager";
    private static final String PROFILE_NAME = "DEFAULT";

    Button btnToggle;
    TextView txtMode;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_settings);
        Log.d(TAG, "EditDataPlugin Settings launched");

        txtMode = (TextView)findViewById(R.id.txtCurrentMode);
        txtMode.setText(getMode());

        btnToggle=(Button)findViewById(R.id.btnToggle);
        btnToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                toggleScanMode();
                txtMode.setText(getMode());
            }
        });
    }

    private String getMode(){
        // get current scan mode
        SharedPreferences prefs = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        String currentMode = prefs.getString(PREF_KEY_SCAN_MODE, "");
        String mode="unknown";

        switch (currentMode){
            case SCAN_MODE_NORMAL:
                mode="normal";
                break;
            case SCAN_MODE_AUTOMATIC:
                mode="automatic";
                break;
        }
        return mode;
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