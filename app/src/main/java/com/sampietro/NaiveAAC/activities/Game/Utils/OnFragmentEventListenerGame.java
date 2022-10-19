package com.sampietro.NaiveAAC.activities.Game.Utils;


import android.view.View;

/**
 * <h1>SomeCustomListenerGame</h1>
 * <p><b>SomeCustomListenerGame</b>
 * interface used by game fragments to refer to the game Activities without having to explicitly
 * use its class
 * <p>
 *
 */
public interface OnFragmentEventListenerGame
{
    /**
     * used for game activities callbacks insert here any references to the game activity
     *
     * @param v view of tapped button
     */
    public void receiveResultGameFragment(View v);
}
