package com.sampietro.NaiveAAC.activities.Info.Utils

import android.view.View

/**
 * <h1>OnFragmentEventListenerInfo</h1>
 *
 * **OnFragmentEventListenerInfo**
 * interface used by Info fragments to refer to the Info Activities without having to explicitly
 * use its class
 *
 *
 *
 */
interface OnFragmentEventListenerInfo {
    /**
     * used for Info activities callbacks insert here any references to the Info activity
     *
     * @param v view of tapped button
     */
    fun receiveResultInfoFragment(v: View?)
}