package com.example.money.ui.settings;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.example.money.Models.MoneyController;
import com.example.money.R;

public class SettingsFragment extends PreferenceFragmentCompat {


    public static SettingsFragment newInstance() {
        return new SettingsFragment();
    }
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.settings_fragment, rootKey);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Preference button = findPreference(getString(R.string.ClearHistoryButton));
        assert button != null;
        button.setOnPreferenceClickListener(preference -> {
            preference.setEnabled(false);
            MoneyController.getInstance().dropDataBase();
            return true;
        });
        if (MoneyController.getInstance().isDroppedDatabase()) button.setEnabled(false);
    }
}