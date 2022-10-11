package com.example.NaiveAAC.activities.Info;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.NaiveAAC.R;
import com.example.NaiveAAC.activities.Info.Utils.InfoFragmentAbstractClass;

/**
 * <h1>EulaFragment</h1>
 * <p><b>EulaFragment</b> UI for app information
 * </p>
 *
 * @version     1.1, 04/22/22
 * @see InfoFragmentAbstractClass
 * @see EulaActivity
 */
public class EulaFragment extends InfoFragmentAbstractClass {
    /**
     * prepares the ui
     *
     * @see androidx.fragment.app.Fragment#onCreateView
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        rootView = inflater.inflate(R.layout.activity_eula_information, container, false);
        // logic of fragment
        return rootView;
    }
}
