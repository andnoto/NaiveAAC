package com.sampietro.NaiveAAC.activities.Info;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.sampietro.NaiveAAC.R;

/**
 * <h1>TerminiFragment</h1>
 * <p><b>TerminiFragment</b> UI for app information
 * </p>
 *
 * @version     1.1, 04/22/22
 * @see RequestConsentToTheProcessingOfPersonalDataActivity
 */
public class TerminiFragment extends Fragment {
    public View rootView;
    /**
     * prepares the ui
     *
     * @see Fragment#onCreateView
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        rootView = inflater.inflate(R.layout.activity_eula_information, container, false);
        // logic of fragment
        return rootView;
    }
}

