package com.example.NaiveAAC.activities.Info.Utils;


import android.view.View;

/**
 * <h1>OnFragmentEventListenerInfo</h1>
 * <p><b>OnFragmentEventListenerInfo</b>
 * interface used by Info fragments to refer to the Info Activities without having to explicitly
 * use its class
 * <p>
 *
 */
public interface OnFragmentEventListenerInfo
{
    /**
     * used for Info activities callbacks insert here any references to the Info activity
     *
     * @param v view of tapped button
     */
    public void receiveResultInfoFragment(View v);
}
