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
 * <h1>VerifyFragment</h1>
 * <p><b>VerifyFragment</b> UI for check the right to access the settings
 * </p>
 * sets up an addition or subtraction with random numbers and calculates the result
 *
 * @version     1.1, 04/22/22
 * @see SettingsActivity
 */
public class VerifyFragment extends Fragment {
    public View rootView;
    public TextView textView;
    public int resultToVerify;
    /**
     * <h1>onFragmentEventListenerVerify</h1>
     * <p><b>onFragmentEventListenerVerify</b>
     * interface used to refer to the Activity without having to explicitly use its class
     * <p>
     *
     * @see #onAttach
     */
    interface onFragmentEventListenerVerify
    {
        //  insert here any references to the Settings Activity
        void receiveResultToVerify(View v, int r);
    }
    //
    private onFragmentEventListenerVerify listener=null;
    /**
     * listener setting for settings activity callbacks and context annotation
     *
     * @see Fragment#onAttach
     */
    @Override
    public void onAttach (Context context)
    {
        super.onAttach(context);
        Activity activity = (Activity) context;
        listener=(onFragmentEventListenerVerify) activity;
    }
    /**
     * prepares the ui and makes the callback to the activity.
     * </p>
     * sets up an addition or subtraction with random numbers and calculates the result
     *
     * @see Fragment#onCreateView
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        rootView = inflater.inflate(R.layout.activity_settings_verify, container, false);
        // logic of fragment
        // if you need to generate numbers from min to max (including both),
        // you write random.nextInt(max - min + 1) + min
        Random rn = new Random();
        int rn1 = rn.nextInt(10) + 1;
        int rn2 = rn.nextInt(rn1) + 1;
        int rn3 = rn.nextInt(2) + 1;
        //
        if (rn3 == 1)
            { resultToVerify = rn1+rn2; }
            else
            { resultToVerify = rn1-rn2; }
        //
        textView = rootView.findViewById(R.id.verifyCalculation);
        if (rn3 == 1)
            { textView.setText(rn1 + " + " + rn2); }
            else
            { textView.setText(rn1 + " - " + rn2); }
        //
        listener.receiveResultToVerify(rootView, resultToVerify);
        //
        return rootView;
    }
}

