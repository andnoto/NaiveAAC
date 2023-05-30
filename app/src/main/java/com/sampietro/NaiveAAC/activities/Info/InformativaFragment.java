package com.sampietro.NaiveAAC.activities.Info;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.sampietro.NaiveAAC.R;
import com.sampietro.NaiveAAC.activities.Info.Utils.InfoFragmentAbstractClass;
import com.sampietro.NaiveAAC.activities.Info.Utils.OnFragmentEventListenerInfo;

/**
 * <h1>InformativaFragment</h1>
 * <p><b>InformativaFragment</b> UI for Privacy information
 * </p>
 *
 * @version     1.1, 04/22/22
 * @see RequestConsentToTheProcessingOfPersonalDataActivity
 */
public class InformativaFragment extends Fragment {
    public View rootView;
    /**
     * prepares the ui
     *
     * @see androidx.fragment.app.Fragment#onCreateView
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        rootView = inflater.inflate(R.layout.activity_privacy_information, container, false);
        // logic of fragment
        return rootView;
    }
}

