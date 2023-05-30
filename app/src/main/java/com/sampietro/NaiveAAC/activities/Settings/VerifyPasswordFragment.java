package com.sampietro.NaiveAAC.activities.Settings;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.sampietro.NaiveAAC.R;

import java.util.Random;

/**
 * <h1>VerifyPasswordFragment</h1>
 * <p><b>VerifyPasswordFragment</b> UI for check the right to access the settings
 * </p>
 *
 * @version     1.1, 04/22/22
 * @see SettingsActivity
 */
public class VerifyPasswordFragment extends Fragment {
    public View rootView;
    /**
     * prepares the ui.
     * </p>
     *
     * @see Fragment#onCreateView
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        rootView = inflater.inflate(R.layout.activity_settings_verify_password, container, false);
        // logic of fragment
        //
        return rootView;
    }
}

