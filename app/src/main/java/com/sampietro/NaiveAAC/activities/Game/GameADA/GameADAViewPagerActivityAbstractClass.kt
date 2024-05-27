package com.sampietro.NaiveAAC.activities.Game.GameADA

import android.media.MediaPlayer
import android.speech.tts.TextToSpeech
import android.view.View
import androidx.viewpager2.widget.ViewPager2
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.sampietro.NaiveAAC.activities.BaseAndAbstractClass.GameActivityAbstractClass

/**
 * <h1>GameAdaViewPagerActivity</h1>
 *
 * **GameAdaViewPagerActivity** displays images (uploaded by the user or Arasaac pictograms) of the
 * * phrases of a story
 *
 * Refer to [raywenderlich.com](https://www.raywenderlich.com/8192680-viewpager2-in-android-getting-started)
 * By [Rajdeep Singh](https://www.raywenderlich.com/u/rajdeep1008)
 *
 * @version     5.0, 01/04/2024
 * @see GameActivityAbstractClass
 *
 * @see GameADAViewPagerAdapter
 */
open class GameADAViewPagerActivityAbstractClass : GameActivityAbstractClass(),
    GameADAViewPagerOnFragmentEventListener, GameADAViewPagerOnFragmentSoundMediaPlayerListener,
    GameADAViewPagerMediaContainerOnClickListener {
    var sharedStory: String? = null
    var phraseToDisplay = 0
    var wordToDisplay = 0
    var wordToDisplayInTheStory = 0
    // TTS
    var tTS1: TextToSpeech? = null
    //
    private var soundMediaPlayer: MediaPlayer? = null
    //
    private var isTheFirstPageSelected = true
    //  ViewPager2
    // - 1) Create the views (activity_game_ada_viewpager_content.xml)
    // - 2) Create the fragment (GameADAViewPagerFragment)
    // - 3) Add a ViewPager2
    // - 4) Create a FragmentStateAdapter who will use the fragments in step 2
    // - 5) Create a layout that contains a ViewPager2 object: (activity_game_ada_viewpager.xml)
    // - 6) This activity must do the following things:
    //   - a) Sets the content view to be the layout with the ViewPager2.
    //   - b) Hooks up the FragmentStateAdapter to the ViewPager2 objects.
    //
    var mViewPager: ViewPager2? = null
//    private var mAdapter: GameADAViewPagerAdapter? = null
    var callbackViewPager2: OnPageChangeCallback = object : OnPageChangeCallback() {
        /**
         * Define page change callback
         *
         *
         * @param position int with position index of the new selected page
         * @see OnPageChangeCallback.onPageSelected
         */
        override fun onPageSelected(position: Int) {
            super.onPageSelected(position)
            //  update activity variables
            /*
            ADAPTED FOR VIDEO AND SOUND
            */
            // release sound mediaplayer
            if (isTheFirstPageSelected) {
                isTheFirstPageSelected = false
            } else {
                wordToDisplayInTheStory = position + 1
                if (soundMediaPlayer != null) {
                    if (soundMediaPlayer!!.isPlaying) {
                        soundMediaPlayer!!.stop()
                    }
                    soundMediaPlayer!!.release()
                    soundMediaPlayer = null
                }
            }
            /*

            */
        }
    }
    /**
     * destroy SpeechRecognizer, TTS shutdown and more
     *
     * @see androidx.fragment.app.Fragment.onDestroy
     */
    override fun onDestroy() {
        super.onDestroy()
        // TTS
        if (tTS1 != null) {
            tTS1!!.stop()
            tTS1!!.shutdown()
        }
        // Unregister page change callback
        mViewPager!!.unregisterOnPageChangeCallback(callbackViewPager2)
    }
    /**
     * Called when the user taps the image.
     * return to GameADAActivity
     * @param v view of tapped button
     * @see GameADAActivity
     */
    open fun onClickGameImage(v: View?) {
    }
    /**
     * on callback from GameADAViewPagerFragment to this Activity
     *
     * @param v view root fragment view
     * @param wordToDisplayInTheStory word to display in the story
     */
    override fun receiveWordToDisplayIndexGameFragment(v: View?, wordToDisplayInTheStory: Int) {
        this.wordToDisplayInTheStory = wordToDisplayInTheStory
    }
    /**
     * on callback from GameADAViewPagerFragment to this Activity
     *
     * @param v view root fragment view
     * @param soundMediaPlayer MediaPlayer
     */
    override fun receiveResultGameFragment(v: View?, soundMediaPlayer: MediaPlayer?) {
        this.soundMediaPlayer = soundMediaPlayer
    }

    /**
     * on callback from GameADAViewPagerFragment to this Activity
     *
     * @param v view root fragment view
     */
    override fun receiveOnClickGameImage(v: View?) {
        onClickGameImage(v)
    }
}