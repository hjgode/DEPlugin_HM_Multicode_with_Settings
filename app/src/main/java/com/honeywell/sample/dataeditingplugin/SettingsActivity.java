package com.honeywell.sample.dataeditingplugin;

import android.app.Activity;
import android.app.Fragment;
import android.preference.PreferenceActivity;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.util.Log;


public class SettingsActivity extends AppCompatActivity {

    String TAG="SettingsActivity";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //addPreferencesFromResource(R.xml.preferences);
        Log.d(TAG,"SettingsActivity onCreate");
        SettingsFragment settingsFragment=new SettingsFragment().newInstance();
        FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction()
                .replace(android.R.id.content, new com.honeywell.sample.dataeditingplugin.SettingsFragment().newInstance())
                .commit();
    }
/*
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        // Load the preferences from an XML resource
        setPreferencesFromResource(R.xml.preferences, rootKey);
    }
*/
}
