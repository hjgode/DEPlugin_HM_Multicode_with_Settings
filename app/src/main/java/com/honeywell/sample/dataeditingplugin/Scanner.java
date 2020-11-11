package com.honeywell.sample.dataeditingplugin;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class Scanner {
    private static final String TAG = "Scanner";

    /**
     * Honeywell DataCollection Intent API
     * Claim scanner
     * Permissions:
     * "com.honeywell.decode.permission.DECODE"
     */
    private static final String ACTION_CLAIM_SCANNER = "com.honeywell.aidc.action.ACTION_CLAIM_SCANNER";

    /**
     * Honeywell DataCollection Intent API
     * Release scanner claim
     * Permissions:
     * "com.honeywell.decode.permission.DECODE"
     */
    private static final String ACTION_RELEASE_SCANNER = "com.honeywell.aidc.action.ACTION_RELEASE_SCANNER";

    /**
     * Honeywell DataCollection Intent API
     * Control scanner
     * Permissions:
     * "com.honeywell.decode.permission.DECODE"
     */
    private static final String ACTION_CONTROL_SCANNER = "com.honeywell.aidc.action.ACTION_CONTROL_SCANNER";

    /**
     * Honeywell DataCollection Intent API
     * Optional. Sets the scanner to claim. If scanner is not available or if extra is not used,
     * DataCollection will choose an available scanner.
     * Values : String
     * "dcs.scanner.imager" : Uses the internal scanner
     * "dcs.scanner.ring" : Uses the external ring scanner
     */
    private static final String EXTRA_SCANNER = "com.honeywell.aidc.extra.EXTRA_SCANNER";

    /**
     * Honeywell DataCollection Intent API
     * Optional. Sets the profile to use. If profile is not available or if extra is not used,
     * the scanner will use factory default properties (not "DEFAULT" profile properties).
     * Values : String
     */
    private static final String EXTRA_PROFILE = "com.honeywell.aidc.extra.EXTRA_PROFILE";

    /**
     * Honeywell DataCollection Intent API
     * Optional. Overrides the profile properties (non-persistent) until the next scanner claim.
     * Values : Bundle
     */
    private static final String EXTRA_PROPERTIES = "com.honeywell.aidc.extra.EXTRA_PROPERTIES";

    /**
     * Honeywell DataCollection Intent API
     * Set the scanner control.
     * Values : Bundle
     */
    private static final String EXTRA_SCAN = "com.honeywell.aidc.extra.EXTRA_SCAN";

    /**
     * Honeywell DataCollection property
     * Enable data intent (boolean)
     */
    public static final String PROPERTY_DATA_INTENT = "DPR_DATA_INTENT";

    /**
     * Honeywell DataCollection property
     * Set data intent action (String)
     */
    public static final String PROPERTY_DATA_INTENT_ACTION = "DPR_DATA_INTENT_ACTION";

    /**
     * Honeywell DataCollection property
     * Set data intent category (String)
     */
    public static final String PROPERTY_DATA_INTENT_CATEGORY = "DPR_DATA_INTENT_CATEGORY";

    /**
     * Honeywell DataCollection property
     * Set data intent package name (String)
     */
    public static final String PROPERTY_DATA_INTENT_PACKAGE_NAME = "DPR_DATA_INTENT_PACKAGE_NAME";

    /**
     * Honeywell DataCollection property
     * Set data intent class name (String)
     */
    public static final String PROPERTY_DATA_INTENT_CLASS_NAME = "DPR_DATA_INTENT_CLASS_NAME";

    private Context mContext = null;

    public Scanner(Context context) {
        mContext = context;
    }

    public void claim(String scannerName, String profileName, Bundle properties) {
        if (mContext == null) {
            Log.e(TAG, "claim, no context");
            return;
        }

        Intent intent = new Intent(ACTION_CLAIM_SCANNER);

        if (scannerName != null) {
            intent.putExtra(EXTRA_SCANNER, scannerName);
        }

        if (profileName != null) {
            intent.putExtra(EXTRA_PROFILE, profileName);
        }

        if (properties != null) {
            intent.putExtra(EXTRA_PROPERTIES, properties);
        }

        mContext.sendBroadcast(intent);
    }

    public void release() {
        if (mContext == null) {
            Log.e(TAG, "release, no context");
            return;
        }

        mContext.sendBroadcast(new Intent(ACTION_RELEASE_SCANNER));
    }

    public void scan(boolean scan) {
        if (mContext == null) {
            Log.e(TAG, "scan, no context");
            return;
        }

        mContext.sendBroadcast(new Intent(ACTION_CONTROL_SCANNER)
                .putExtra(EXTRA_SCAN, scan));
    }
}
