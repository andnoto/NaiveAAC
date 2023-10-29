package com.sampietro.NaiveAAC.activities.Game.GameADA

import android.media.MediaPlayer
import android.view.View

/**
 * <h1>GameADAViewPagerOnFragmentSoundMediaPlayerListener</h1>
 *
 * **GameADAViewPagerOnFragmentSoundMediaPlayerListener**
 * interface used by game fragments to refer to the game Activities without having to explicitly
 * use its class
 *
 *
 * THIS CLASS WAS CREATED TO ASSOCIATE VIDEOS AND SOUNDS WITH WORDS (AS WELL AS IMAGES AND TTS)
 *
 */
interface GameADAViewPagerOnFragmentSoundMediaPlayerListener {
    /**
     * used for game activities callbacks insert here any references to the game activity
     *
     * @param v view
     * @param soundMediaPlayer MediaPlayer
     */
    fun receiveResultGameFragment(v: View?, soundMediaPlayer: MediaPlayer?)
}