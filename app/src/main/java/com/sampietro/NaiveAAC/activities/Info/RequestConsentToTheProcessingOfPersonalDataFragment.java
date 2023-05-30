package com.sampietro.NaiveAAC.activities.Info;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.sampietro.NaiveAAC.R;
import com.sampietro.NaiveAAC.activities.Account.AccountActivity;
import com.sampietro.NaiveAAC.activities.Settings.SettingsActivity;
import com.sampietro.NaiveAAC.activities.Settings.Utils.SettingsFragmentAbstractClass;

/**
 * <h1>RequestConsentToTheProcessingOfPersonalDataFragment</h1>
 * <p><b>RequestConsentToTheProcessingOfPersonalDataFragment</b> UI to requires consent to the processing of personal data
 * </p>
 *
 * @version     1.1, 04/22/22
 * @see RequestConsentToTheProcessingOfPersonalDataActivity
 */
public class RequestConsentToTheProcessingOfPersonalDataFragment extends Fragment {
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
     * @see androidx.fragment.app.Fragment#onCreateView
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        rootView = inflater.inflate(R.layout.activity_requestconsenttotheprocessingofpersonaldata, container, false);
        // logic of fragment
        return rootView;
    }
}

