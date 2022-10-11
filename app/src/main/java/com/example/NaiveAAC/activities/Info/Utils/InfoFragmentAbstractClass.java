package com.example.NaiveAAC.activities.Info.Utils;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

/**
 * <h1>InfoFragmentAbstractClass</h1>
 * <p><b>InfoFragmentAbstractClass</b>
 * abstract class containing common methods that is extended by Info Fragment
 *
 * @version     1.1, 04/22/22
 */
public abstract class InfoFragmentAbstractClass extends Fragment {
    public View rootView;
    public TextView textView;
    public Context ctext;
    //
    public OnFragmentEventListenerInfo listener=null;
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
        listener=(OnFragmentEventListenerInfo) activity;
        //
        ctext = context;
    }
}

