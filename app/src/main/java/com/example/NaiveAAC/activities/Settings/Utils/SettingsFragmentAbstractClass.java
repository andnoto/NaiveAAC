package com.example.NaiveAAC.activities.Settings.Utils;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

/**
 * <h1>SettingsFragmentAbstractClass</h1>
 * <p><b>SettingsFragmentAbstractClass</b>
 * abstract class containing common methods that is extended by Settings Fragment
 *
 * @version     1.1, 04/22/22
 */
public abstract class SettingsFragmentAbstractClass extends Fragment {
    public View rootView;
    public TextView textView;
    public Context ctext;
    /**
     * <h1>onFragmentEventListenerSettings</h1>
     * <p><b>onFragmentEventListenerSettings</b>
     * interface used to refer to the Activity without having to explicitly use its class
     * <p>
     *
     * @see #onAttach
     */
    public interface onFragmentEventListenerSettings
    {
    //  insert here any references to the Settings Activity
        void receiveResultSettings(View v);
    }
    //
    public onFragmentEventListenerSettings listener=null;
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
        //
        listener=(onFragmentEventListenerSettings) activity;
        //
        ctext = context;
    }
}

