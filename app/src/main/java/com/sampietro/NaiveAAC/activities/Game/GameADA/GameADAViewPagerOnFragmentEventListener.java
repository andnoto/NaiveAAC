package com.sampietro.NaiveAAC.activities.Game.GameADA;


import android.media.MediaPlayer;
import android.view.View;

/**
 * <h1>GameADAViewPagerOnFragmentEventListener</h1>
 * <p><b>GameADAViewPagerOnFragmentEventListener</b>
 * interface used by game fragments to refer to the game Activities without having to explicitly
 * use its class
 * <p>
 * THIS CLASS WAS CREATED TO ASSOCIATE VIDEOS AND SOUNDS WITH WORDS (AS WELL AS IMAGES AND TTS)
 *
 */
public interface GameADAViewPagerOnFragmentEventListener
{
    /**
     * used for game activities callbacks insert here any references to the game activity
     *
     * @param v view
     * @param wordToDisplayIndex word to display index in the story
     */
    public void receiveWordToDisplayIndexGameFragment(View v, int wordToDisplayIndex);
}
