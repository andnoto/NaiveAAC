package com.sampietro.NaiveAAC.activities.Game.GameADA;


import android.media.MediaPlayer;
import android.view.View;

/**
 * <h1>GameADAViewPagerOnFragmentSoundMediaPlayerListener</h1>
 * <p><b>GameADAViewPagerOnFragmentSoundMediaPlayerListener</b>
 * interface used by game fragments to refer to the game Activities without having to explicitly
 * use its class
 * <p>
 * THIS CLASS WAS CREATED TO ASSOCIATE VIDEOS AND SOUNDS WITH WORDS (AS WELL AS IMAGES AND TTS)
 *
 */
public interface GameADAViewPagerOnFragmentSoundMediaPlayerListener
{
    /**
     * used for game activities callbacks insert here any references to the game activity
     *
     * @param v view
     * @param soundMediaPlayer MediaPlayer
     */
    public void receiveResultGameFragment(View v, MediaPlayer soundMediaPlayer);
}
