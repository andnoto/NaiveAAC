package com.sampietro.NaiveAAC.activities.Info;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sampietro.NaiveAAC.R;
import com.sampietro.NaiveAAC.activities.Info.Utils.InfoFragmentAbstractClass;

/**
 * <h1>PrivacyFragment</h1>
 * <p><b>PrivacyFragment</b> UI for Privacy information
 * </p>
 *
 * @version     1.1, 04/22/22
 * @see InfoFragmentAbstractClass
 * @see PrivacyActivity
 */
public class PrivacyFragment extends InfoFragmentAbstractClass {
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

