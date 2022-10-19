package com.sampietro.NaiveAAC.activities.Settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sampietro.NaiveAAC.R;
import com.sampietro.NaiveAAC.activities.Settings.Utils.SettingsFragmentAbstractClass;
import com.sampietro.NaiveAAC.activities.Game.GameParameters.GameParametersAdapter;
import com.sampietro.NaiveAAC.activities.Stories.Stories;

/**
 * <h1>StoriesFragment</h1>
 * <p><b>StoriesFragment</b> UI for stories settings
 * </p>
 *
 * @version     1.1, 04/22/22
 * @see SettingsFragmentAbstractClass
 * @see SettingsActivity
 */
public class StoriesFragment extends SettingsFragmentAbstractClass {
    /**
     * prepares the ui and makes the callback to the activity
     *
     * @see androidx.fragment.app.Fragment#onCreateView
     * @see Stories
     * @see GameParametersAdapter
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        rootView = inflater.inflate(R.layout.activity_settings_stories, container, false);
        // logic of fragment
        listener.receiveResultSettings(rootView);
        //
        return rootView;
    }
}

