package com.honeywell.sample.dataeditingplugin;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import static android.content.Context.MODE_PRIVATE;

/**
 * This handles the Data Editing intent
 */
public class DataEditingPlugin extends BroadcastReceiver implements Common {
    private static final String TAG = "DataEditingPlugin";

    private static final String INTENT_ACTION_ACTIVATE_DATA_EDIT_PLUGIN =
            "com.honeywell.decode.intent.action.ACTIVATE_DATA_EDIT_PLUGIN";

    private static final String INTENT_ACTION_EDIT_DATA =
            "com.honeywell.decode.intent.action.EDIT_DATA";
    private static final String INTENT_ACTION_EDIT_SETTINGS = "com.honeywell.decode.intent.action.EDIT_SETTINGS";

    // Result success, continue further processing, wedge
    private static final int DATA_EDIT_RESULT_SUCCESS = 0;

    // Result continue, continue further processing, wedge
    private static final int DATA_EDIT_RESULT_CONTINUE = 1;

    // Result handled, stop further processing, no wedge
    private static final int DATA_EDIT_RESULT_HANDLED = 2;

    // Result error, stop further processing and bad read notification, no wedge
    private static final int DATA_EDIT_RESULT_ERROR = 3;

    private static final String CODEID_I2OF5 = "e";

    @Override
    public void onReceive(final Context context, Intent intent) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        String currentMode = prefs.getString(PREF_KEY_SCAN_MODE, SCAN_MODE_NORMAL);
        boolean bAutomode_on = prefs.getBoolean(PREF_KEY_AUTOMODE_ON, false);

        if (INTENT_ACTION_EDIT_DATA.equals(intent.getAction())) {
            Log.d(TAG, "Got intent to edit data");

            int version = intent.getIntExtra("version", 0);
            Log.d(TAG, "Version: " + Integer.toString(version));

            String edittedData = "";
            int resultCode = DATA_EDIT_RESULT_SUCCESS;

            if(bAutomode_on){
                edittedData = intent.getStringExtra("data");
                resultCode = DATA_EDIT_RESULT_SUCCESS;
                // Return editted data with result code.
                Bundle resultBundle = new Bundle();
                resultBundle.putString("data", edittedData);

                final PendingResult result = goAsync();
                result.setResultExtras(resultBundle);
                result.setResultCode(resultCode);
                result.finish();
                return;
            }
            if (version > 0) {
                /**
                 *  These extras are available:
                 *  "version" = Data Editing Intent version (int)
                 *  "aimId" = The AIM Identifier (String)
                 *  "charset" = The character set used to convert "dataBytes" to "data" (String)
                 *  "codeId" = The Honeywell Symbology Identifier (String)
                 *  "data" = The barcode data as a string (String)
                 *  "dataBytes" = The barcode data as a byte array (byte[])
                 *  "timestamp" = The barcode timestamp (String)
                 */

                String codeId = intent.getStringExtra("codeId");
                //Log.d(TAG, "CodeId: " + codeId);

                String data = intent.getStringExtra("data");
                //Log.d(TAG, "Data: " + data);

                // Only do data editing in NORMAL mode
                if (SCAN_MODE_NORMAL.equals(currentMode)) {
                    // Edit the barcode data based on Symbology
                    if (codeId.equals(CODEID_I2OF5)) {
                        /**
                         * This concatenates three I2of5 barcodes for wedging where the first
                         * barcode starts with '3' with 12 length, second barcode starts with
                         * '4' with 12 length, and third barcode starts with '5' or '7' with
                         * length 8. Whitespace is also stripped from the data. And then, a
                         * '\n' newline is appended to the concatenated barcode data. If a barcode
                         * is read that does not match this criteria, the data will be wedge as is.
                         */
                        resultCode = DATA_EDIT_RESULT_HANDLED;

                        String barcode1 = prefs.getString(PREF_KEY_BARCODE_1, "");
                        String barcode2 = prefs.getString(PREF_KEY_BARCODE_2, "");
                        String barcode3 = prefs.getString(PREF_KEY_BARCODE_3, "");

                        String multicodeData = data.trim();

                        if (multicodeData.length() == 12 && multicodeData.startsWith("3")) {
                            barcode1 = multicodeData;
                            prefs.edit().putString(PREF_KEY_BARCODE_1, barcode1).commit();
                        } else if (multicodeData.length() == 12 && multicodeData.startsWith("4")) {
                            barcode2 = multicodeData;
                            prefs.edit().putString(PREF_KEY_BARCODE_2, barcode2).commit();
                        } else if (multicodeData.length() == 8 && (multicodeData.startsWith("5") || multicodeData.startsWith("7"))) {
                            barcode3 = multicodeData;
                            prefs.edit().putString(PREF_KEY_BARCODE_3, barcode3).commit();
                        } else {
                            // did not scan an expected I2of5 barcode
                            // pass current barcode data through
                            resetMulticodePrefs(prefs);
                            edittedData = data;
                            resultCode = DATA_EDIT_RESULT_SUCCESS;
                        }

                        if (resultCode == DATA_EDIT_RESULT_HANDLED &&
                                barcode1 != null && barcode1.length() > 0 &&
                                barcode2 != null && barcode2.length() > 0 &&
                                barcode3 != null && barcode3.length() > 0) {
                            // pass the completed concatenated data set
                            resetMulticodePrefs(prefs);
                            edittedData = barcode1 + barcode2 + barcode3 + '\n';
                            resultCode = DATA_EDIT_RESULT_SUCCESS;
                        }
                    } else {
                        // did not scan an I2of5 barcode
                        // pass current barcode data through
                        resetMulticodePrefs(prefs);
                        edittedData = data;
                        resultCode = DATA_EDIT_RESULT_SUCCESS;
                    }
                } else {
                    resetMulticodePrefs(prefs);
                    edittedData = data;
                    resultCode = DATA_EDIT_RESULT_SUCCESS;
                }
            } else {
                Log.e(TAG, "Unsupported data editing version. Require version 1 or higher.");
                resultCode = DATA_EDIT_RESULT_ERROR;
            }

            // Return editted data with result code.
            Bundle resultBundle = new Bundle();
            resultBundle.putString("data", edittedData);

            final PendingResult result = goAsync();
            result.setResultExtras(resultBundle);
            result.setResultCode(resultCode);
            result.finish();
        } else if (INTENT_ACTION_ACTIVATE_DATA_EDIT_PLUGIN.equals(intent.getAction())) {
            Log.d(TAG, "Got intent to activate data edit plugin");
            resetMulticodePrefs(prefs);
        }else if(INTENT_ACTION_EDIT_SETTINGS.equals(intent.getAction())){
            Log.d(TAG, "Got intent to show data edit plugin settings");

        }
    }

    private void resetMulticodePrefs(SharedPreferences prefs) {
        if (prefs == null) {
            Log.e(TAG, "resetMulticodePrefs, prefs is null");
            return;
        }

        String data = prefs.getString(PREF_KEY_BARCODE_1, "") +
                prefs.getString(PREF_KEY_BARCODE_2, "") +
                prefs.getString(PREF_KEY_BARCODE_3, "");

        if (data.length() > 0) {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString(PREF_KEY_BARCODE_1, "");
            editor.putString(PREF_KEY_BARCODE_2, "");
            editor.putString(PREF_KEY_BARCODE_3, "");
            editor.commit();
        }
    }

    private String bytesToHexString(byte[] arr) {
        if (arr == null) {
            return "[]";
        }

        String s = "";
        for (int i = 0; i < arr.length; i++) {
            s += "0x" + Integer.toHexString(arr[i]) + " ";
        }
        s = "[" + s.trim() + "]";
        return s;
    }
}
