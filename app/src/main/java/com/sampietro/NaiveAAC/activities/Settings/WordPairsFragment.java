package com.sampietro.NaiveAAC.activities.Settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sampietro.NaiveAAC.R;
import com.sampietro.NaiveAAC.activities.Settings.Utils.SettingsFragmentAbstractClass;
import com.sampietro.NaiveAAC.activities.WordPairs.WordPairs;
import com.sampietro.NaiveAAC.activities.WordPairs.WordPairsAdapter;

/**
 * <h1>WordPairsFragment</h1>
 * <p><b>WordPairsFragment</b> UI for word pairs settings
 * </p>
 *
 * @version     1.1, 04/22/22
 * @see SettingsFragmentAbstractClass
 * @see SettingsActivity
 */
public class WordPairsFragment extends SettingsFragmentAbstractClass {
    /**
     * prepares the ui and makes the callback to the activity
     *
     * @see androidx.fragment.app.Fragment#onCreateView
     * @see WordPairs
     * @see WordPairsAdapter
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        rootView = inflater.inflate(R.layout.activity_settings_wordpairs, container, false);
        // logic of fragment
        listener.receiveResultSettings(rootView);
        //
        return rootView;
    }
}

