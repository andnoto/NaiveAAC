package com.sampietro.NaiveAAC.activities.Info;

import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sampietro.NaiveAAC.R;
import com.sampietro.NaiveAAC.activities.Info.Utils.InfoFragmentAbstractClass;

/**
 * <h1>InfoFragment</h1>
 * <p><b>InfoFragment</b> UI for app information
 * </p>
 *
 * @version     1.1, 04/22/22
 * @see InfoFragmentAbstractClass
 * @see InfoActivity
 */
public class InfoFragment extends InfoFragmentAbstractClass {
    /**
     * prepares the ui
     *
     * @see androidx.fragment.app.Fragment#onCreateView
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        rootView = inflater.inflate(R.layout.activity_settings_information, container, false);
        // logic of fragment
        TextView textInformation = (TextView) rootView.findViewById(R.id.information);
        textInformation.setMovementMethod(new ScrollingMovementMethod());
        TextView textCopyright = (TextView) rootView.findViewById(R.id.copyright);
        textCopyright.setMovementMethod(new ScrollingMovementMethod());
        //
        return rootView;
    }
}

