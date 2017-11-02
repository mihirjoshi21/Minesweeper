package com.conem.app.assignment2.screens;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.conem.app.assignment2.R;


public class SettingsActivity extends AppCompatActivity implements
        SharedPreferences.OnSharedPreferenceChangeListener {

    public static final String PREF_DISCLAIMER = "pref_disclaimer";
    public static final String PREF_OPEN_SOURCE = "pref_open_source";
    public static final String PREF_SOUND = "pref_sound";
    public static final String PREF_VIBRATE = "pref_vibrate";
    public static final String PREF_DIFFICULTY = "pref_difficulty";
    public static final String DIFFICULTY_CHANGED = "Difficulty Changed";
    private Activity mActivity;
    private SharedPreferences mPrefs;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mActivity = SettingsActivity.this;
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_settings);

        // Set SharedPreference Listener
        mPrefs = PreferenceManager.getDefaultSharedPreferences(mActivity);
        mPrefs.registerOnSharedPreferenceChangeListener(this);

        // Display the fragment as the main content.
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new PrefsFragment()).commit();
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPrefs.unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

        switch (key) {
            case PREF_DIFFICULTY:
                Intent intent = new Intent();
                intent.putExtra(DIFFICULTY_CHANGED, true);
                setResult(RESULT_OK, intent);
                break;
        }

    }

    public static class PrefsFragment extends PreferenceFragment {

        private static final String LICENSE_DIALOG_FRAGMENT = "License dialog fragment";
        private static final String ABOUT_DIALOG_FRAGMENT = "About dialog fragment";
        Activity mActivity;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            // Load the preferences from an XML resource
            addPreferencesFromResource(R.xml.pref_general);


            findPreference(PREF_DISCLAIMER).
                    setOnPreferenceClickListener(preference -> {
                        DialogFragmentAbout dialogFragmentAbout = new DialogFragmentAbout();
                        dialogFragmentAbout.show(getFragmentManager(), ABOUT_DIALOG_FRAGMENT);
                        return true;
                    });

            findPreference(PREF_OPEN_SOURCE).
                    setOnPreferenceClickListener(preference -> {
                        DialogFragmentLicense dialogFragmentLicense = new DialogFragmentLicense();
                        dialogFragmentLicense.show(getFragmentManager(), LICENSE_DIALOG_FRAGMENT);
                        return true;
                    });
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            mActivity = activity;
        }

        @Override
        public void onAttach(Context context) {
            super.onAttach(context);
            mActivity = (Activity) context;
        }
    }
}