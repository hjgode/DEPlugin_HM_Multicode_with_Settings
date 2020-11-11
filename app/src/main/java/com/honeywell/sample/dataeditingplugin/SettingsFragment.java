package com.honeywell.sample.dataeditingplugin;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.support.v4.app.Fragment;

import android.support.v7.preference.PreferenceFragmentCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends PreferenceFragmentCompat implements Common {
    static String TAG="SettingsFragment";

    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState,
                                    String rootKey) {
        Log.d(TAG, "SettingsFragment onCreatePreferences");
        setPreferencesFromResource(R.xml.preferences, rootKey);
/*
        //load
        SharedPreferences prefs = getActivity().getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        String currentMode = prefs.getString(PREF_KEY_SCAN_MODE, "");
*/
    }

    public static SettingsFragment newInstance() {
        Log.d(TAG, "SettingsFragment newInstance");
        SettingsFragment fragment = new SettingsFragment();
        return fragment;
    }
}
