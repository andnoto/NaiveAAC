package com.sampietro.NaiveAAC.activities.Settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sampietro.NaiveAAC.R;
import com.sampietro.NaiveAAC.activities.Settings.Utils.SettingsFragmentAbstractClass;

/**
 * <h1>ChoiseOfMediaToSetFragment</h1>
 * <p><b>ChoiseOfMediaToSetFragment</b> UI for choise of media to set
 * </p>
 *
 * @version     1.1, 04/22/22
 * @see SettingsFragmentAbstractClass
 * @see SettingsActivity
 */
public class ChoiseOfMediaToSetFragment extends SettingsFragmentAbstractClass {
    /**
     * prepares the ui
     *
     * @see androidx.fragment.app.Fragment#onCreateView
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        rootView = inflater.inflate(R.layout.activity_settings_media_menu, container, false);
        // logic of fragment
        return rootView;
    }
}

