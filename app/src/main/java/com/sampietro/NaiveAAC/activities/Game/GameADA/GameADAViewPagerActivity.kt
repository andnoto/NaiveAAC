package com.sampietro.NaiveAAC.activities.Game.GameADA

import com.sampietro.NaiveAAC.activities.VoiceRecognition.AndroidPermission.checkPermission
import com.sampietro.NaiveAAC.activities.Game.Utils.GameActivityAbstractClass
import android.speech.tts.TextToSpeech
import android.media.MediaPlayer
import android.view.ViewGroup
import androidx.viewpager2.widget.ViewPager2
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import android.os.Bundle
import io.realm.RealmResults
import com.sampietro.NaiveAAC.activities.Stories.Stories
import com.sampietro.NaiveAAC.R
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.content.Intent
import com.sampietro.NaiveAAC.activities.Game.ChoiseOfGame.ChoiseOfGameActivity
import com.sampietro.NaiveAAC.activities.Settings.VerifyActivity
import android.view.View
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
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
 * @version     4.0, 09/09/2023
 * @see GameActivityAbstractClass
 *
 * @see GameADAViewPagerAdapter
 */
class GameADAViewPagerActivity : GameActivityAbstractClass(),
    GameADAViewPagerOnFragmentEventListener, GameADAViewPagerOnFragmentSoundMediaPlayerListener,
    GameADAViewPagerMediaContainerOnClickListener {
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
//            wordToDisplayInTheStoryIndex = position;
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
            sharedStory = savedInstanceState.getString("STORY TO DISPLAY")
            phraseToDisplay = savedInstanceState.getInt("PHRASE TO DISPLAY")
            wordToDisplay = savedInstanceState.getInt("WORD TO DISPLAY")
            gameUseVideoAndSound = savedInstanceState.getString("GAME USE VIDEO AND SOUND")
            //
            val resultsStories = realm.where(Stories::class.java)
                .beginGroup()
                .equalTo("story", sharedStory)
                .equalTo("phraseNumberInt", phraseToDisplay)
                .equalTo("wordNumberInt", wordToDisplay)
                .endGroup()
                .findAll()
            val storiesSize = resultsStories.size
            if (storiesSize > 0) {
                assert(resultsStories[0] != null)
                wordToDisplayInTheStory = resultsStories[0]!!.wordNumberIntInTheStory
            }
            //
        }
        //
        if (savedInstanceState == null) {
            val extras = intent.extras
            if (extras != null) {
                sharedStory = extras.getString("STORY TO DISPLAY")
                phraseToDisplay = extras.getInt("PHRASE TO DISPLAY")
                wordToDisplay = extras.getInt("WORD TO DISPLAY")
                gameUseVideoAndSound = extras.getString("GAME USE VIDEO AND SOUND")
                //
                val resultsStories = realm.where(Stories::class.java)
                    .beginGroup()
                    .equalTo("story", sharedStory)
                    .equalTo("phraseNumberInt", phraseToDisplay)
                    .equalTo("wordNumberInt", wordToDisplay)
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
        /*
        USED FOR FULL SCREEN
         */
        val mContentView:ViewGroup = findViewById(R.id.activity_game_ada_viewpager_id)
        setToFullScreen()
        val viewTreeObserver = mContentView.getViewTreeObserver()
        if (viewTreeObserver.isAlive) {
            viewTreeObserver.addOnGlobalLayoutListener(object : OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    mContentView.getViewTreeObserver().removeOnGlobalLayoutListener(this)
                }
            })
        }
        mContentView.setOnClickListener(View.OnClickListener { view: View? -> setToFullScreen() })
        /*

         */
        //
        checkPermission(this)
        //
        realm = Realm.getDefaultInstance()
        //
        val resultsStories: RealmResults<Stories> = realm.where(Stories::class.java)
            .beginGroup()
            .equalTo("story", sharedStory)
//            .notEqualTo("wordNumberInt", 0)
            .greaterThan("wordNumberInt", 0)
            .lessThan("wordNumberInt", 99)
            .endGroup()
            .findAll()
        //
        context = this
        // viewpager
        // Wire Adapter with ViewPager2
        mViewPager = findViewById<View>(R.id.pager) as ViewPager2
//        val lifecycle: Lifecycle = getLifecycle()
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
        setToFullScreen()
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
        savedInstanceState.putString("STORY TO DISPLAY", sharedStory)
        savedInstanceState.putInt("PHRASE TO DISPLAY", phraseToDisplay)
        savedInstanceState.putInt("WORD TO DISPLAY", wordToDisplay)
        savedInstanceState.putString("GAME USE VIDEO AND SOUND", gameUseVideoAndSound)
        //
        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState)
    }

    /**
     * This method is responsible to transfer into fullscreen mode.
     */
    private fun setToFullScreen() {
        WindowCompat.setDecorFitsSystemWindows(window, false)

        val insetsController = WindowCompat.getInsetsController(window, window.decorView)
        insetsController.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        insetsController.hide(WindowInsetsCompat.Type.statusBars())
        insetsController.hide(WindowInsetsCompat.Type.navigationBars())
//        findViewById<View>(R.id.activity_game_ada_viewpager_id).systemUiVisibility =
//            View.SYSTEM_UI_FLAG_LOW_PROFILE or View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
    }
    //
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
            .equalTo("story", sharedStory)
            .equalTo("wordNumberIntInTheStory", wordToDisplayInTheStory)
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
        intent.putExtra("STORY TO DISPLAY", sharedStory)
        intent.putExtra("PHRASE TO DISPLAY INDEX", phraseToDisplay)
        intent.putExtra("WORD TO DISPLAY INDEX", wordToDisplay - 1)
        intent.putExtra("GAME USE VIDEO AND SOUND", gameUseVideoAndSound)
        startActivity(intent)
    }

    /**
     * Called on result of speech.
     *
     * @param editText string result from SpeechRecognizerManagement
     * @see com.sampietro.NaiveAAC.activities.VoiceRecognition.RecognizerCallback
     */
//    @RequiresApi(api = Build.VERSION_CODES.N)
    override fun onResult(editText: String?) {
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