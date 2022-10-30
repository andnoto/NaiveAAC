package com.sampietro.NaiveAAC.activities.Settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sampietro.NaiveAAC.R;
import com.sampietro.NaiveAAC.activities.Settings.Utils.SettingsFragmentAbstractClass;

/**
 * <h1>DataImportSettingsFragment</h1>
 * <p><b>DataImportSettingsFragment</b> UI for data import settings
 * </p>
 *
 * @version     1.1, 04/22/22
 * @see SettingsFragmentAbstractClass
 * @see SettingsActivity
 */
public class DataImportSettingsFragment extends SettingsFragmentAbstractClass {
    /**
     * prepares the ui and makes the callback to the activity
     *
     * @see androidx.fragment.app.Fragment#onCreateView
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        rootView = inflater.inflate(R.layout.activity_settings_data_import, container, false);
        // logic of fragment
        listener.receiveResultSettings(rootView);
        //
        return rootView;
    }
}
