package com.sampietro.NaiveAAC.activities.Game.GameADA

import android.view.View

/**
 * <h1>GameADAViewPagerCenterVideoViewOnClickListener</h1>
 *
 * **GameADAViewPagerCenterVideoViewOnClickListener**
 * interface used by game fragments to refer to the game Activities without having to explicitly
 * use its class
 *
 *
 * THIS CLASS WAS CREATED TO ASSOCIATE VIDEOS AND SOUNDS WITH WORDS (AS WELL AS IMAGES AND TTS)
 *
 */
interface GameADAViewPagerMediaContainerOnClickListener {
    /**
     * used for game activities callbacks insert here any references to the game activity
     *
     * @param v view
     */
    fun receiveOnClickGameImage(v: View?)
}