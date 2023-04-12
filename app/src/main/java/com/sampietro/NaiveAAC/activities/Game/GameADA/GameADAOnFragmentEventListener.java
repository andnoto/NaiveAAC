package com.sampietro.NaiveAAC.activities.Game.GameADA;


import android.speech.tts.TextToSpeech;
import android.view.View;

import java.util.ArrayList;

/**
 * <h1>GameADAOnFragmentEventListener</h1>
 * <p><b>GameADAOnFragmentEventListener</b>
 * interface used by game fragments to refer to the game Activities without having to explicitly
 * use its class
 * <p>
 *
 */
public interface GameADAOnFragmentEventListener
{
    /**
     * used for game activities callbacks insert here any references to the game activity
     *
     * @param v view of tapped button
     * @param t TextToSpeech
     * @param galleryList ArrayList<GameADAArrayList>
     */
    public void receiveResultGameFragment(View v, TextToSpeech t, ArrayList<GameADAArrayList> galleryList);
}
