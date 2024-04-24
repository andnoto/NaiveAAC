package com.sampietro.NaiveAAC.activities.Game.GameADA

import com.sampietro.NaiveAAC.activities.VoiceRecognition.AndroidPermission.checkPermission
import android.speech.tts.TextToSpeech
import android.media.MediaPlayer
import androidx.viewpager2.widget.ViewPager2
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import android.os.Bundle
import io.realm.RealmResults
import com.sampietro.NaiveAAC.activities.Stories.Stories
import com.sampietro.NaiveAAC.R
import android.content.Intent
import com.sampietro.NaiveAAC.activities.Game.ChoiseOfGame.ChoiseOfGameActivity
import com.sampietro.NaiveAAC.activities.Settings.VerifyActivity
import android.view.View
import android.view.Window
import com.sampietro.NaiveAAC.activities.BaseAndAbstractClass.GameActivityAbstractClass
import com.sampietro.NaiveAAC.activities.Graphics.GraphicsAndPrintingHelper.setToFullScreen
import io.realm.Realm

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
class GameADAViewPagerActivity : GameActivityAbstractClass(),
    GameADAViewPagerOnFragmentEventListener, GameADAViewPagerOnFragmentSoundMediaPlayerListener,
    GameADAViewPagerMediaContainerOnClickListener {
    // USED FOR FULL SCREEN
    lateinit var mywindow: Window
    //
    var sharedStory: String? = null
    var phraseToDisplay = 0
    var wordToDisplay = 0
    var wordToDisplayInTheStory = 0

    // TTS
    var tTS1: TextToSpeech? = null
//    var toSpeak: String? = null

    //
    private var soundMediaPlayer: MediaPlayer? = null

    //
//    private var mContentView: ViewGroup? = null

    //
    private var gameUseVideoAndSound: String? = null

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
    private var mViewPager: ViewPager2? = null
    private var mAdapter: GameADAViewPagerAdapter? = null
//    private override val lifecycle: Any? = null
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
     * configurations of gameadaviewpager start screen.
     *
     *
     *
     * @param savedInstanceState Define potentially saved parameters due to configurations changes.
     * //     * @see prepareSpeechRecognizer
     * @see com.sampietro.NaiveAAC.activities.Game.Game1.Game1ViewPagerAdapter
     *
     * @see android.app.Activity.onCreate
     * @see OnPageChangeCallback.onPageSelected
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //
        realm = Realm.getDefaultInstance()
        // Check whether we're recreating a previously destroyed instance
        if (savedInstanceState != null) {
            // The onSaveInstanceState method is called before an activity may be killed
            // (for Screen Rotation Handling) so that
            // when it comes back it can restore its state.
            // it display the last word
            // else display the word passed by GameADAActivity
            sharedStory = savedInstanceState.getString(getString(R.string.story_to_display))
            phraseToDisplay = savedInstanceState.getInt(getString(R.string.phrase_to_display))
            wordToDisplay = savedInstanceState.getInt(getString(R.string.word_to_display))
            gameUseVideoAndSound = savedInstanceState.getString(getString(R.string.game_use_video_and_sound))
            //
            val resultsStories = realm.where(Stories::class.java)
                .beginGroup()
                .equalTo(getString(R.string.story), sharedStory)
                .equalTo(getString(R.string.phrasenumberint), phraseToDisplay)
                .equalTo(getString(R.string.wordnumberint), wordToDisplay)
                .endGroup()
                .findAll()
            val storiesSize = resultsStories.size
            if (storiesSize > 0) {
                assert(resultsStories[0] != null)
                wordToDisplayInTheStory = resultsStories[0]!!.wordNumberIntInTheStory
            }
        }
        //
        if (savedInstanceState == null) {
            val extras = intent.extras
            if (extras != null) {
                sharedStory = extras.getString(getString(R.string.story_to_display))
                phraseToDisplay = extras.getInt(getString(R.string.phrase_to_display))
                wordToDisplay = extras.getInt(getString(R.string.word_to_display))
                gameUseVideoAndSound = extras.getString(getString(R.string.game_use_video_and_sound))
                //
                val resultsStories = realm.where(Stories::class.java)
                    .beginGroup()
                    .equalTo(getString(R.string.story), sharedStory)
                    .equalTo(getString(R.string.phrasenumberint), phraseToDisplay)
                    .equalTo(getString(R.string.wordnumberint), wordToDisplay)
                    .endGroup()
                    .findAll()
                val storiesSize = resultsStories.size
                if (storiesSize > 0) {
                    assert(resultsStories[0] != null)
                    wordToDisplayInTheStory = resultsStories[0]!!.wordNumberIntInTheStory
                }
                //
            }
        }
        // viewpager
        setContentView(R.layout.activity_game_ada_viewpager)
        //
        if (supportActionBar != null) {
            supportActionBar!!.hide()
        }
        /*
        USED FOR FULL SCREEN
         */
        mywindow = getWindow()
        setToFullScreen(mywindow)
        /*

         */
        //
        checkPermission(this)
        //
        realm = Realm.getDefaultInstance()
        //
        val resultsStories: RealmResults<Stories> = realm.where(Stories::class.java)
            .beginGroup()
            .equalTo(getString(R.string.story), sharedStory)
            .greaterThan(getString(R.string.wordnumberint), 0)
            .lessThan(getString(R.string.wordnumberint), 99)
            .endGroup()
            .findAll()
        //
        context = this
        // viewpager
        // Wire Adapter with ViewPager2
        mViewPager = findViewById<View>(R.id.pager) as ViewPager2
        mAdapter = GameADAViewPagerAdapter(
            supportFragmentManager, lifecycle, context, realm,
            sharedStory!!, wordToDisplayInTheStory - 1, gameUseVideoAndSound!!, resultsStories
        )
        mViewPager!!.adapter = mAdapter
        // Register page change callback
        mViewPager!!.registerOnPageChangeCallback(callbackViewPager2)
        // Set the currently selected page
        mViewPager!!.setCurrentItem(wordToDisplayInTheStory - 1, false)
    }

    /**
     * Hide the Navigation Bar
     *
     * @see android.app.Activity.onResume
     */
    override fun onResume() {
        /*
        USED FOR FULL SCREEN
         */
        super.onResume()
        mywindow = getWindow()
        setToFullScreen(mywindow)
        /*

         */
    }
    //
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
     * This method is called before an activity may be killed so that when it comes back some time in the future it can restore its state..
     *
     *
     *
     * @param savedInstanceState Define potentially saved parameters due to configurations changes.
     */
    public override fun onSaveInstanceState(savedInstanceState: Bundle) {
        //
        savedInstanceState.putString(getString(R.string.story_to_display), sharedStory)
        savedInstanceState.putInt(getString(R.string.phrase_to_display), phraseToDisplay)
        savedInstanceState.putInt(getString(R.string.word_to_display), wordToDisplay)
        savedInstanceState.putString(getString(R.string.game_use_video_and_sound), gameUseVideoAndSound)
        //
        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState)
    }

    /**
     * Called when the user taps the home button.
     *
     *
     * @param v view of tapped button
     * @see ChoiseOfGameActivity
     */
    fun returnHome(v: View?) {
    /*
                navigate to home screen (ChoiseOfGameActivity)
    */
        val intent = Intent(this, ChoiseOfGameActivity::class.java)
        startActivity(intent)
    }

    /**
     * Called when the user taps the settings button.
     * replace with hear fragment
     *
     *
     * @param v view of tapped button
     * @see VerifyActivity
     */
    fun returnSettings(v: View?) {
    /*
                navigate to settings screen (ChoiseOfGameActivity)
    */
        val intent = Intent(this, VerifyActivity::class.java)
        startActivity(intent)
    }

    /**
     * Called when the user taps the image.
     * return to GameADAActivity
     * @param v view of tapped button
     * @see GameADAActivity
     */
    fun onClickGameImage(v: View?) {
        //
        val resultsStories = realm.where(Stories::class.java)
            .beginGroup()
            .equalTo(getString(R.string.story), sharedStory)
            .equalTo(getString(R.string.wordnumberintinthestory), wordToDisplayInTheStory)
            .endGroup()
            .findAll()
        val storiesSize = resultsStories.size
        if (storiesSize > 0) {
            assert(resultsStories[0] != null)
            phraseToDisplay = resultsStories[0]!!.phraseNumberInt
            wordToDisplay = resultsStories[0]!!.wordNumberInt
        }
        val intent: Intent
        intent = Intent(
            this,
            GameADAActivity::class.java
        )
        intent.putExtra(getString(R.string.story_to_display), sharedStory)
        intent.putExtra(getString(R.string.phrase_to_display_index), phraseToDisplay)
        intent.putExtra(getString(R.string.word_to_display_index), wordToDisplay - 1)
        intent.putExtra(getString(R.string.game_use_video_and_sound), gameUseVideoAndSound)
        startActivity(intent)
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
        rootViewImageFragment = v
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