package com.sampietro.NaiveAAC.activities.Game.Utils

import android.view.View

/**
 * <h1>SomeCustomListenerGame</h1>
 *
 * **SomeCustomListenerGame**
 * interface used by game fragments to refer to the game Activities without having to explicitly
 * use its class
 *
 *
 *
 */
interface OnFragmentEventListenerGame {
    /**
     * used for game activities callbacks insert here any references to the game activity
     *
     * @param v view of tapped button
     */
    fun receiveResultGameFragment(v: View?)
}