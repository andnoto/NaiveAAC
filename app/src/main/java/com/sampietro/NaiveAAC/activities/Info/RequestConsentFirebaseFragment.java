package com.sampietro.NaiveAAC.activities.Info;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.sampietro.NaiveAAC.R;

/**
 * <h1>RequestConsentFirebaseFragment</h1>
 * <p><b>RequestConsentFirebaseFragment</b> UI to requires consent Firebase Analytics and Crashlytics
 * </p>
 *
 * @version     1.1, 04/22/22
 * @see RequestConsentFirebaseActivity
 */
public class RequestConsentFirebaseFragment extends Fragment {
    public View rootView;
    public TextView textView;
    public Context ctext;
    /**
     * context annotation
     *
     * @see Fragment#onAttach
     */
    @Override
    public void onAttach (Context context)
    {
        super.onAttach(context);
        //
        ctext = context;
    }
    /**
     * prepares the ui
     *
     * @see Fragment#onCreateView
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        rootView = inflater.inflate(R.layout.activity_requestconsentfirebase, container, false);
        // logic of fragment
        return rootView;
    }
}

