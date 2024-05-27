package com.sampietro.NaiveAAC.activities.Game.GameADA

import android.speech.tts.TextToSpeech
import android.view.View

/**
 * <h1>GameADAOnFragmentEventListener</h1>
 *
 * **GameADAOnFragmentEventListener**
 * interface used by game fragments to refer to the game Activities without having to explicitly
 * use its class
 *
 *
 *
 */
interface GameADAOnFragmentEventListener {
    /**
     * used for game activities callbacks insert here any references to the game activity
     *
     * @param v view of tapped button
     * @param t TextToSpeech
     * @param galleryList ArrayList<GameADAArrayList>
    </GameADAArrayList> */
    fun receiveResultGameFragment(
        v: View?,
        t: TextToSpeech?,
        galleryList: ArrayList<GameADAArrayList>
    )
}