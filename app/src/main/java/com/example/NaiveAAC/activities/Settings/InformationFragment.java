package com.example.NaiveAAC.activities.Settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.NaiveAAC.R;
import com.example.NaiveAAC.activities.Settings.Utils.SettingsFragmentAbstractClass;

/**
 * <h1>InformationFragment</h1>
 * <p><b>InformationFragment</b> UI for app information
 * </p>
 *
 * @version     1.1, 04/22/22
 * @see com.example.NaiveAAC.activities.Settings.Utils.SettingsFragmentAbstractClass
 * @see com.example.NaiveAAC.activities.Settings.SettingsActivity
 */
public class InformationFragment extends SettingsFragmentAbstractClass {
    /**
     * prepares the ui
     *
     * @see androidx.fragment.app.Fragment#onCreateView
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        rootView = inflater.inflate(R.layout.activity_settings_information, container, false);
        // logic of fragment
        return rootView;
    }
}

