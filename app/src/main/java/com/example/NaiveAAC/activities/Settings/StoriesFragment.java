package com.example.NaiveAAC.activities.Settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.NaiveAAC.R;
import com.example.NaiveAAC.activities.Settings.Utils.SettingsFragmentAbstractClass;

/**
 * <h1>StoriesFragment</h1>
 * <p><b>StoriesFragment</b> UI for stories settings
 * </p>
 *
 * @version     1.1, 04/22/22
 * @see com.example.NaiveAAC.activities.Settings.Utils.SettingsFragmentAbstractClass
 * @see com.example.NaiveAAC.activities.Settings.SettingsActivity
 */
public class StoriesFragment extends SettingsFragmentAbstractClass {
    /**
     * prepares the ui and makes the callback to the activity
     *
     * @see androidx.fragment.app.Fragment#onCreateView
     * @see com.example.NaiveAAC.activities.Stories.Stories
     * @see com.example.NaiveAAC.activities.Game.GameParameters.GameParametersAdapter
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

