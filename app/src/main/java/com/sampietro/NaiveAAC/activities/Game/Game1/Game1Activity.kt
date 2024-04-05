package com.sampietro.NaiveAAC.activities.Game.Game1

import com.sampietro.NaiveAAC.activities.Game.Utils.GameActivityAbstractClass
import com.sampietro.NaiveAAC.activities.Game.Utils.PrizeFragment.onFragmentEventListenerPrize
import com.sampietro.NaiveAAC.activities.Game.Utils.YoutubePrizeFragment.onFragmentEventListenerYoutubePrize
import android.os.Bundle
import android.speech.tts.TextToSpeech
import com.sampietro.NaiveAAC.activities.WordPairs.WordPairs
import android.graphics.Bitmap
import com.squareup.picasso.Picasso.LoadedFrom
import android.graphics.drawable.Drawable
import androidx.viewpager2.widget.ViewPager2
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.sampietro.NaiveAAC.R
import com.sampietro.NaiveAAC.activities.Game.Utils.PrizeFragment
import com.sampietro.NaiveAAC.activities.Game.Utils.YoutubePrizeFragment
import android.content.Intent
import com.sampietro.NaiveAAC.activities.Game.ChoiseOfGame.ChoiseOfGameActivity
import com.sampietro.NaiveAAC.activities.Settings.VerifyActivity
import com.sampietro.NaiveAAC.activities.Game.Utils.GameFragmentHear
import android.os.Handler
import android.os.Looper
import com.sampietro.NaiveAAC.activities.Game.Utils.GameHelper
import com.sampietro.NaiveAAC.activities.Grammar.GrammarHelper
import android.view.View
import android.widget.Toast
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.print.PrintHelper
import com.sampietro.NaiveAAC.activities.Phrases.Phrases
import com.sampietro.NaiveAAC.activities.Game.Balloon.BalloonGameplayActivity
import com.sampietro.NaiveAAC.activities.Grammar.ComposesASentenceResults
import com.sampietro.NaiveAAC.activities.Grammar.ListsOfNames
import com.sampietro.NaiveAAC.activities.Graphics.GraphicsHelper.getTargetBitmapFromFileUsingPicasso
import com.sampietro.NaiveAAC.activities.Graphics.GraphicsHelper.getTargetBitmapFromUrlUsingPicasso
import com.sampietro.NaiveAAC.activities.VoiceRecognition.AndroidPermission
import com.sampietro.NaiveAAC.activities.VoiceRecognition.SpeechRecognizerManagement
import com.sampietro.NaiveAAC.activities.VoiceRecognition.SpeechRecognizerManagement.destroyRecognizer
import com.sampietro.NaiveAAC.activities.VoiceRecognition.SpeechRecognizerManagement.prepareSpeechRecognizer
import com.sampietro.NaiveAAC.activities.WordPairs.WordPairs.Companion.searchAwardType
import com.sampietro.NaiveAAC.activities.WordPairs.WordPairs.Companion.searchUriPremiumVideo
import com.squareup.picasso.Target
import io.realm.Realm
import java.io.File
import java.lang.Exception
import java.util.*

/**
 * <h1>Game1Activity</h1>
 *
 * **Game1Activity** displays collections of word images that you can select to form simple sentences
 *
 * the search for words takes place via a two-level menu
 * the first level contains the main research classes such as games, food, family, animals
 * the second level contains the subclasses of the first level for example
 * for the game class subclasses ball, tablet, running, etc.
 * the sentences are formed by coupling the words of the subclasses with the words contained in the wordpairs table
 * Refer to [raywenderlich.com](https://www.raywenderlich.com/8192680-viewpager2-in-android-getting-started)
 * By [Rajdeep Singh](https://www.raywenderlich.com/u/rajdeep1008)
 *
 * @version     5.0, 01/04/2024
 * @see GameActivityAbstractClass
 *
 * @see Game1RecyclerViewAdapterInterface
 *
 * @see PrizeFragment.onFragmentEventListenerPrize
 *
 * @see YoutubePrizeFragment.onFragmentEventListenerYoutubePrize
 */
class Game1Activity : GameActivityAbstractClass(), Game1RecyclerViewAdapterInterface,
    onFragmentEventListenerPrize, onFragmentEventListenerYoutubePrize {
    var onCreateSavedInstanceState: Bundle? = null

    // TTS
    var tTS1: TextToSpeech? = null
    var toSpeak: String? = null
    //
    var listOfWordsLeft = arrayListOf<String>()
    var listOfWordsCenter = arrayListOf<String>()
    var listOfWordsRight = arrayListOf<String>()

    //

    // at the fragment I pass three strings with, for each column,
    // the possible content of the chosen words
    // and three numbers which, if applicable, indicate the choice menus for each column
    // identified by PhraseNumber on History
    var leftColumnContent: String? = null
    var leftColumnContentUrlType: String? = null
    var leftColumnContentUrl: String? = null
    var middleColumnContent: String? = null
    var middleColumnContentUrlType: String? = null
    var middleColumnContentUrl: String? = null
    var rightColumnContent: String? = null
    var rightColumnContentUrlType: String? = null
    var rightColumnContentUrl: String? = null

    //
    var leftColumnMenuPhraseNumber: Int? = null
    var middleColumnMenuPhraseNumber: Int? = null
    var rightColumnMenuPhraseNumber: Int? = null

    //
    var rightColumnAwardType: String? = null
    var rightColumnUriPremiumVideo: String? = null

    //
    var middleColumnContentVerbInTheInfinitiveForm: String? = null

    //
    var numberOfWordsChosen = 0

    //
    var sharedLastPlayer: String? = null

    //
    var wordToSearchSecondLevelMenu: String? = null

    //
    var preference_PrintPermissions: String? = null
    var preference_AllowedMarginOfError = 0

    /**
     * used for printing
     */
    var bitmap1: Bitmap? = null

    /**
     * used for printing
     */
    var target1: Target = object : Target {
        override fun onBitmapLoaded(bitmap: Bitmap, from: LoadedFrom) {
            bitmap1 = bitmap
        }

        override fun onBitmapFailed(e: Exception, errorDrawable: Drawable) {}
        override fun onPrepareLoad(placeHolderDrawable: Drawable?) {}
    }

    //  ViewPager2
    // - 1) Create the views (activity_game_1_viewpager_content.xml)
    // - 2) Create the fragment (Game1ViewPagerFirstLevelFragment and Game1ViewPagerSecondLevelFragment)
    // - 3) Add a ViewPager2
    // - 4) Create a FragmentStateAdapter who will use the fragments in step 2
    // - 5) Create a layout that contains a ViewPager2 object: (activity_game_1_viewpager.xml)
    // - 6) This activity must do the following things:
    //   - a) Sets the content view to be the layout with the ViewPager2.
    //   - b) Hooks up the FragmentStateAdapter to the ViewPager2 objects.
    //
    private lateinit var mViewPager: ViewPager2
    private lateinit var mAdapter: Game1ViewPagerAdapter
    var callbackViewPager2: OnPageChangeCallback = object : OnPageChangeCallback() {
        /**
         * Define page change callback
         *
         * update activity variables and display second level menu.
         *
         * @param position int with position index of the new selected page
         * @see Game1Activity.displaySecondLevelMenu
         *
         * @see WordPairs
         *
         * @see OnPageChangeCallback.onPageSelected
         */
        override fun onPageSelected(position: Int) {
            super.onPageSelected(position)
            //  update activity variables
            val resultsListsOfNames = realm.where(ListsOfNames::class.java)
                .beginGroup()
                .equalTo("isMenuItem", "F")
                .equalTo("elementActive", "A")
                .endGroup()
                .findAll()
            val resultsListsOfNamesSize = resultsListsOfNames.size
            if (resultsListsOfNamesSize != 0) {
                val result = resultsListsOfNames[position]!!
                wordToSearchSecondLevelMenu = result.keyword

                //
                // aggiungere ripristino video prize ?
                if (onCreateSavedInstanceState != null) {
                    // The onSaveInstanceState method is called before an activity may be killed
                    // (for Screen Rotation Handling) so that
                    // when it comes back it can restore its state.
                    // if this activity was playing an award video, it restores the award video
                    // else it restores second level menu
                    wordToSearchSecondLevelMenu = onCreateSavedInstanceState!!.getString(
                        getString(R.string.word_to_search_second_level_menu),
                        getString(R.string.non_trovato)
                    )
                    //
                    rightColumnAwardType = onCreateSavedInstanceState!!.getString(
                        getString(R.string.award_type),
                        getString(R.string.nessuno)
                    )
                    rightColumnUriPremiumVideo = onCreateSavedInstanceState!!.getString(
                        getString(R.string.uri_premium_video),
                        getString(R.string.non_trovato)
                    )
                    if (rightColumnAwardType != getString(R.string.non_trovato)) {
                        when (rightColumnAwardType) {
                            "V" ->                                 // upload a video as a reward
                                uploadAVideoAsAReward()
                            "Y" ->                                 // upload a Youtube video as a reward
                                uploadAYoutubeVideoAsAReward()
                            else -> {}
                        }
                    } else {
                        displaySecondLevelMenu()
                    }
                    onCreateSavedInstanceState = null
                } else {
                    displaySecondLevelMenu()
                }
            }
        }
    }

    /**
     * configurations of game1 start screen.
     *
     *
     *
     * @param savedInstanceState Define potentially saved parameters due to configurations changes.
     * @see SpeechRecognizerManagement.prepareSpeechRecognizer
     * // * @see ActionbarFragment
     *
     * @see welcomeSpeech
     *
     * @see Game1ViewPagerAdapter
     *
     * @see android.app.Activity.onCreate
     * @see OnPageChangeCallback.onPageSelected
     *
     * @see uploadAVideoAsAReward
     *
     * @see uploadAYoutubeVideoAsAReward
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Check whether we're recreating a previously destroyed instance
        if (savedInstanceState != null) {
            // The onSaveInstanceState method is called before an activity may be killed
            // (for Screen Rotation Handling) so that
            // when it comes back it can restore its state.
            // if this activity was playing an award video, it restores the award video
            // (in callbackViewPager2)
            onCreateSavedInstanceState = savedInstanceState
        }
        // viewpager
        setContentView(R.layout.activity_game_1_viewpager)
        //
        if (supportActionBar != null) {
            supportActionBar!!.hide()
        }
        /*
        USED FOR FULL SCREEN
        */
        setToFullScreen()
        /*

        */
        AndroidPermission.checkPermission(this)
        //
        prepareSpeechRecognizer(this)
        //
        realm = Realm.getDefaultInstance()
        //
        context = this
        //
        sharedPref = getSharedPreferences(
            getString(R.string.preference_file_key), MODE_PRIVATE
        )
        //
        sharedLastPlayer =
            sharedPref.getString(getString(R.string.preference_LastPlayer), "DEFAULT")
        //
        preference_PrintPermissions =
            sharedPref.getString(getString(R.string.preference_print_permissions), "DEFAULT")
        preference_AllowedMarginOfError =
            sharedPref.getInt(getString(R.string.preference_allowed_margin_of_error), 20)
        // TTS
        if (savedInstanceState == null) welcomeSpeech()
        // viewpager
        // Wire Adapter with ViewPager2
        mViewPager = findViewById<View>(R.id.pager) as ViewPager2
        mAdapter = Game1ViewPagerAdapter(this, this, realm)
        mViewPager.adapter = mAdapter
        // Register page change callback
        mViewPager.registerOnPageChangeCallback(callbackViewPager2)
    }

    /**
     * Hide the Navigation Bar
     *
     * @see android.app.Activity.onResume
     */
    override fun onResume() {
        super.onResume()
        setToFullScreen()
    }
    //
    /**
     * destroy SpeechRecognizer, TTS shutdown and more
     *
     * @see androidx.fragment.app.Fragment.onDestroy
     */
    override fun onDestroy() {
        super.onDestroy()
        destroyRecognizer()
        // TTS
        if (tTS1 != null) {
            tTS1!!.stop()
            tTS1!!.shutdown()
        }
        // Unregister page change callback
        mViewPager.unregisterOnPageChangeCallback(callbackViewPager2)
    }

    /**
     * This method is called before an activity may be killed so that when it comes back some time in the future it can restore its state..
     *
     *
     * if it is playing an award video, it stores the relative references
     *
     * @param savedInstanceState Define potentially saved parameters due to configurations changes.
     * @see PrizeFragment
     *
     * @see YoutubePrizeFragment
     */
    public override fun onSaveInstanceState(savedInstanceState: Bundle) {
        //
        savedInstanceState.putString(
            getString(R.string.word_to_search_second_level_menu),
            wordToSearchSecondLevelMenu
        )
        //
        val prizefragmentgotinstance =
            supportFragmentManager.findFragmentByTag(getString(R.string.prize_fragment)) as PrizeFragment?
        val yprizefragmentgotinstance =
            supportFragmentManager.findFragmentByTag(getString(R.string.youtube_prize_fragment)) as YoutubePrizeFragment?
        if (prizefragmentgotinstance != null || yprizefragmentgotinstance != null) {
            savedInstanceState.putString(getString(R.string.award_type), rightColumnAwardType)
            savedInstanceState.putString(
                getString(R.string.uri_premium_video),
                rightColumnUriPremiumVideo
            )
        }
        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState)
    }

    /**
     * This method is responsible to transfer MainActivity into fullscreen mode.
     */
    private fun setToFullScreen() {
        WindowCompat.setDecorFitsSystemWindows(window, false)

        val insetsController = WindowCompat.getInsetsController(window, window.decorView)
        insetsController.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        insetsController.hide(WindowInsetsCompat.Type.statusBars())
        insetsController.hide(WindowInsetsCompat.Type.navigationBars())
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
     * @see ChoiseOfGameActivity
     */
    fun returnSettings(v: View?) {
    /*
                navigate to settings screen (ChoiseOfGameActivity)
    */
        val intent = Intent(this, VerifyActivity::class.java)
        startActivity(intent)
    }

    /**
     * Called when the user taps the start speech button.
     *
     * @param v view of tapped button
     * @see SpeechRecognizerManagement.startSpeech
     */
    fun startSpeechGame1(v: View?) {
        SpeechRecognizerManagement.startSpeech()
        //
        val frag = GameFragmentHear()
        val ft = supportFragmentManager.beginTransaction()
        val fragmentgotinstance =
            supportFragmentManager.findFragmentByTag(getString(R.string.game1_second_level_fragment)) as Game1SecondLevelFragment?
        val hearfragmentgotinstance =
            supportFragmentManager.findFragmentByTag(getString(R.string.game_fragment_hear)) as GameFragmentHear?
        if (fragmentgotinstance != null || hearfragmentgotinstance != null) {
            ft.replace(R.id.game_container_game1, frag, getString(R.string.game_fragment_hear))
        } else {
            ft.add(R.id.game_container_game1, frag, getString(R.string.game_fragment_hear))
        }
        ft.addToBackStack(null)
        ft.commitAllowingStateLoss()
    }

    /**
     * Called when the user taps the listen again button.
     *
     * re-reads the text of the sentence just composed
     *
     * REFER to [stackoverflow](https://stackoverflow.com/questions/24745546/android-change-activity-after-few-seconds)
     * answer of [Chefes](https://stackoverflow.com/users/3586222/chefes)
     *
     * @param v view of tapped button
     * @see readingOfTheText
     *
     * @see startSpeechGame1
     */
    fun listenAgainButton(v: View?) {
        readingOfTheText()
        // Time to launch the another activity
        val TIME_OUT = 4000
        Handler(Looper.getMainLooper()).postDelayed({ startSpeechGame1(rootViewImageFragment) }, TIME_OUT.toLong())
    }

    /**
     * Called when the user taps the continue game button.
     *
     * display second level menu
     *
     * @param v view of tapped button
     * @see .displaySecondLevelMenu
     */
    fun continueGameButton(v: View?) {
        displaySecondLevelMenu()
    }

    /**
     * Called on result of speech.
     *
     * @param eText string result from SpeechRecognizerManagement
     * @see com.sampietro.NaiveAAC.activities.VoiceRecognition.RecognizerCallback
     */
    override fun onResult(eText: String?) {
        checkAnswer(eText!!)
    }

    /**
     * Called on end of speech.
     * replace with second level fragment
     *
     * @param editText string message from SpeechRecognizerManagement
     * @see fragmentTransactionStart
     *
     * @see com.sampietro.NaiveAAC.activities.VoiceRecognition.RecognizerCallback
     */
    override fun onEndOfSpeech(editText: String?) {
        fragmentTransactionStart()
    }

    /**
     * Called on error from SpeechRecognizerManagement.
     *
     * @param errorCode int error code from SpeechRecognizerManagement
     * @see com.sampietro.NaiveAAC.activities.VoiceRecognition.RecognizerCallback
     *
     * @see fragmentTransactionStart
     */
    override fun onError(errorCode: Int) {
        fragmentTransactionStart()
    }

    /**
     * on callback from PrizeFragment to this Activity
     *
     *
     * @param v view inflated inside PrizeFragment
     * @see PrizeFragment
     */
    override fun receiveResultImagesFromPrizeFragment(v: View?) {}

    /**
     * callback from PrizeFragment to this Activity on completatio video
     *
     * display second level menu
     *
     * @param v view inflated inside PrizeFragment
     * @see displaySecondLevelMenu
     *
     * @see PrizeFragment
     */
    override fun receiveResultOnCompletatioVideoFromPrizeFragment(v: View?) {
        displaySecondLevelMenu()
    }

    /**
     * on callback from YoutubePrizeFragment to this Activity
     *
     *
     * @param v view inflated inside YoutubePrizeFragment
     * @see YoutubePrizeFragment
     */
    override fun receiveResultImagesFromYoutubePrizeFragment(v: View?) {}

    /**
     * callback from YoutubePrizeFragment to this Activity on completatio video
     *
     * display second level menu
     *
     * @param v view inflated inside YoutubePrizeFragment
     * @see displaySecondLevelMenu
     *
     * @see YoutubePrizeFragment
     */
    override fun receiveResultOnCompletatioVideoFromYoutubePrizeFragment(v: View?) {
        displaySecondLevelMenu()
    }

    /**
     * display second level menu.
     *
     *
     * the second level contains the subclasses of the first level for example
     * for the game class subclasses ball, tablet, running, etc.
     *
     *
     * 1) search for the second level menu words in the wordpairs table,
     * 2) convert RealmResults<Model> to ArrayList<Model>
     * 3) record History and initiate Fragment transaction
     *
     * @see ComposesASentenceResults
     * @see prepareTheFragmentTransaction
     * @see fragmentTransactionStart
     */
    fun displaySecondLevelMenu() {
        numberOfWordsChosen = 0
        leftColumnContent = getString(R.string.nessuno)
        middleColumnContent = getString(R.string.nessuno)
        rightColumnContent = getString(R.string.nessuno)
        listOfWordsLeft.clear()
        listOfWordsCenter.clear()
        listOfWordsRight.clear()
        // search for the second level menu words in the listsofnames table
        val resultsListsOfNames = realm.where(ListsOfNames::class.java)
            .beginGroup()
            .equalTo("keyword", wordToSearchSecondLevelMenu)
            .equalTo("isMenuItem", "S")
            .endGroup()
            .findAll()
        val resultsListsOfNamesSize = resultsListsOfNames!!.size
        var resultsListsOfNamesIndex = 0
        while (resultsListsOfNamesSize > resultsListsOfNamesIndex) {
            listOfWordsCenter.add(resultsListsOfNames[resultsListsOfNamesIndex]!!.word!!)
            resultsListsOfNamesIndex++
        }
        val composesASentenceResults: ComposesASentenceResults = GrammarHelper.composesASentence(
            context,
            realm,
            numberOfWordsChosen,
            "none",
            leftColumnContent!!,
            middleColumnContent!!,
            rightColumnContent!!,
            listOfWordsLeft,
            listOfWordsCenter,
            listOfWordsRight,
            sharedLastPlayer!!
        )
        //
        numberOfWordsChosen = composesASentenceResults.numberOfWordsChosen
        leftColumnContent = composesASentenceResults.leftColumnContent
        middleColumnContent = composesASentenceResults.middleColumnContent
        rightColumnContent = composesASentenceResults.rightColumnContent
        listOfWordsLeft.clear()
        listOfWordsLeft.addAll(composesASentenceResults.listOfWordsLeft)
        listOfWordsCenter.clear()
        listOfWordsCenter.addAll(composesASentenceResults.listOfWordsCenter)
        listOfWordsRight.clear()
        listOfWordsRight.addAll(composesASentenceResults.listOfWordsRight)
        //
        if (numberOfWordsChosen == 3)
        {
            // sentence text reading and search award
            sentenceReadingOfTheTextAndStartSpeech()
            //
            rightColumnAwardType = getString(R.string.nessuno)
            if (middleColumnContent != getString(R.string.nessuno)
                && rightColumnContent != getString(R.string.nessuno))
                {
                rightColumnAwardType = searchAwardType(context, listOfWordsCenter[0], listOfWordsRight[0], realm )
                if (rightColumnAwardType != getString(R.string.nessuno))
                    {
                        rightColumnUriPremiumVideo = searchUriPremiumVideo(context, listOfWordsCenter[0], listOfWordsRight[0], realm )
                    }
                }
        }
        prepareTheFragmentTransaction()
        fragmentTransactionStart()
    }
    /**
     * prepare fragment transaction:
     * record History
     *
     * @see GameHelper.historyRegistration
     */
    fun prepareTheFragmentTransaction() {
        //
        if (listOfWordsLeft.size != 0) {
            // History registration
            GameHelper.historyRegistration(
                context, realm,
                leftColumnContent!!,
                listOfWordsLeft, listOfWordsLeft.size
            )
            sharedLastPhraseNumber =
                sharedPref.getInt(getString(R.string.preference_last_phrase_number), 1)
            leftColumnMenuPhraseNumber = sharedLastPhraseNumber
        } else { leftColumnMenuPhraseNumber = 0 }
        if (listOfWordsCenter.size != 0) {
            // record History and initiate Fragment transaction
            GameHelper.historyRegistration(
                context, realm,
                middleColumnContent!!,
                listOfWordsCenter, listOfWordsCenter.size
            )
            sharedLastPhraseNumber =
                sharedPref.getInt(getString(R.string.preference_last_phrase_number), 1)
            middleColumnMenuPhraseNumber = sharedLastPhraseNumber
        } else { middleColumnMenuPhraseNumber = 0 }
        if (listOfWordsRight.size != 0) {
            // History registration
            GameHelper.historyRegistration(
                context, realm,
                rightColumnContent!!,
                listOfWordsRight, listOfWordsRight.size
            )
            sharedLastPhraseNumber =
                sharedPref.getInt(getString(R.string.preference_last_phrase_number), 1)
            rightColumnMenuPhraseNumber = sharedLastPhraseNumber
        } else { rightColumnMenuPhraseNumber = 0 }
    }

    /**
     * initiate Fragment transaction.
     *
     *
     *
     * @see Game1SecondLevelFragment
     */
    fun fragmentTransactionStart() {
        val frag = Game1SecondLevelFragment()
        val bundle = Bundle()
        bundle.putString(getString(R.string.left_column_content), leftColumnContent)
        bundle.putString(getString(R.string.left_column_content_url_type), leftColumnContentUrlType)
        bundle.putString(getString(R.string.left_column_content_url), leftColumnContentUrl)
        bundle.putString(getString(R.string.middle_column_content), middleColumnContent)
        bundle.putString(
            getString(R.string.middle_column_content_url_type),
            middleColumnContentUrlType
        )
        bundle.putString(getString(R.string.middle_column_content_url), middleColumnContentUrl)
        bundle.putString(getString(R.string.right_column_content), rightColumnContent)
        bundle.putString(
            getString(R.string.right_column_content_url_type),
            rightColumnContentUrlType
        )
        bundle.putString(getString(R.string.right_column_content_url), rightColumnContentUrl)
        bundle.putInt(
            getString(R.string.left_column_menu_phrase_number),
            leftColumnMenuPhraseNumber!!
        )
        bundle.putInt(
            getString(R.string.middle_column_menu_phrase_number),
            middleColumnMenuPhraseNumber!!
        )
        bundle.putInt(
            getString(R.string.right_column_menu_phrase_number),
            rightColumnMenuPhraseNumber!!
        )
        //
        if (numberOfWordsChosen == 3)
        { bundle.putBoolean("THE SENTENCE IS COMPLETED", true) }
        else
        { bundle.putBoolean("THE SENTENCE IS COMPLETED", false) }
        //
        frag.arguments = bundle
        val ft = supportFragmentManager.beginTransaction()
        val fragmentgotinstance =
            supportFragmentManager.findFragmentByTag(getString(R.string.game1_second_level_fragment)) as Game1SecondLevelFragment?
        val prizefragmentgotinstance =
            supportFragmentManager.findFragmentByTag(getString(R.string.prize_fragment)) as PrizeFragment?
        if (fragmentgotinstance != null || prizefragmentgotinstance != null) {
            ft.replace(
                R.id.game_container_game1,
                frag,
                getString(R.string.game1_second_level_fragment)
            )
        } else {
            ft.add(R.id.game_container_game1, frag, getString(R.string.game1_second_level_fragment))
        }
        ft.addToBackStack(null)
        ft.commitAllowingStateLoss()
    }

    /**
     * called when a word list item is clicked.
     *
     * 1) prepare the ui through the history table
     * 2) launch the fragment for displaying the ui
     * 3) in case of completion of the choice of words,
     * carries out the grammatical arrangement and the reading of the text,
     * 4) chosen the 3rd and last word of the sentence
     * the following clicks on the words will start the tts
     *
     * @param view view of clicked item
     * @param i int the position of the item within the adapter's data set
     * @see GrammarHelper.ComposesASentenceResults
     * @see sentenceReadingOfTheTextAndStartSpeech
     * @see sendMessage
     * @see prepareTheFragmentTransaction
     * @see fragmentTransactionStart
     */
    override fun onItemClick(view: View?, i: Int) {
        when (numberOfWordsChosen) {
            0 -> {
                val chosenWordCenter = listOfWordsCenter[i]
                listOfWordsCenter.clear()
                listOfWordsCenter.add(chosenWordCenter)
                val composesASentenceResults: ComposesASentenceResults =
                    GrammarHelper.composesASentence(
                        context,
                        realm,
                        numberOfWordsChosen,
                        "center",
                        leftColumnContent!!,
                        middleColumnContent!!,
                        rightColumnContent!!,
                        listOfWordsLeft,
                        listOfWordsCenter,
                        listOfWordsRight,
                        sharedLastPlayer!!
                    )
                //
                numberOfWordsChosen = composesASentenceResults.numberOfWordsChosen
                leftColumnContent = composesASentenceResults.leftColumnContent
                middleColumnContent = composesASentenceResults.middleColumnContent
                rightColumnContent = composesASentenceResults.rightColumnContent
                listOfWordsLeft.clear()
                listOfWordsLeft.addAll(composesASentenceResults.listOfWordsLeft)
                listOfWordsCenter.clear()
                listOfWordsCenter.addAll(composesASentenceResults.listOfWordsCenter)
                listOfWordsRight.clear()
                listOfWordsRight.addAll(composesASentenceResults.listOfWordsRight)
                // the first word of the sentence was chosen
                // (in the case of noun and verb only possible choice,
                // the second word of the sentence is also considered to be chosen -
                // in the case of verb and nouns the only possible choice is considered
                // also chosen the second or second and third word of the sentence)
                if (numberOfWordsChosen == 3)
                {
                    // sentence text reading and search award
                    sentenceReadingOfTheTextAndStartSpeech()
                    //
                    rightColumnAwardType = getString(R.string.nessuno)
                    if (middleColumnContent != getString(R.string.nessuno)
                        && rightColumnContent != getString(R.string.nessuno))
                    {
                        rightColumnAwardType = searchAwardType(context, listOfWordsCenter[0], listOfWordsRight[0], realm )
                        if (rightColumnAwardType != getString(R.string.nessuno))
                        {
                            rightColumnUriPremiumVideo = searchUriPremiumVideo(context, listOfWordsCenter[0], listOfWordsRight[0], realm )
                        }
                    }
                }
                // start of transaction Fragment
                prepareTheFragmentTransaction()
                fragmentTransactionStart()
                return
            }
            1 -> {
                // the second word of the sentence was chosen
                // 1) if the chosen word (central) is a verb (type = 3 verbs)
                // 1a) if the chosen word is on the left, I register the choice and keep the options
                // list in the recycler view on the right
                // 1b) if the chosen word is in the center the choice is not allowed
                // (because it has already been chosen) i keep both option lists in the recycler views
                // both on the right and on the left
                // 1c) if the word chosen is on the right i register the choice and
                // keep the options list in the recycler view on the left
                // 2) if the chosen (middle) word is not a verb
                // 2a) if the chosen word is on the left, i register the choice,
                // moving the central column to the right and the left column (that of the choice)
                // to the center and proposing in the left column the choices compatible
                // with the choice (it should be a verb)
                // 2b) if the chosen word is in the center the choice is not allowed (because it has
                // already been chosen) i keep both option lists in the recycler views
                // both on the right and on the left
                // 2c) if the word chosen is on the right, i register the choice moving the central
                // column to the left and the right column (that of the choice) to the center and
                // proposing in the right column the choices compatible with the choice (it should be a verb)
                when (view!!.id) {
                    R.id.img1 -> {
                        val chosenWordLeft = listOfWordsLeft[i]
                        listOfWordsLeft.clear()
                        listOfWordsLeft.add(chosenWordLeft)
                        val composesASentenceResults: ComposesASentenceResults =
                            GrammarHelper.composesASentence(
                                context,
                                realm,
                                numberOfWordsChosen,
                                "left",
                                leftColumnContent!!,
                                middleColumnContent!!,
                                rightColumnContent!!,
                                listOfWordsLeft,
                                listOfWordsCenter,
                                listOfWordsRight,
                                sharedLastPlayer!!
                            )
                        //
                        numberOfWordsChosen = composesASentenceResults.numberOfWordsChosen
                        leftColumnContent = composesASentenceResults.leftColumnContent
                        middleColumnContent = composesASentenceResults.middleColumnContent
                        rightColumnContent = composesASentenceResults.rightColumnContent
                        listOfWordsLeft.clear()
                        listOfWordsLeft.addAll(composesASentenceResults.listOfWordsLeft)
                        listOfWordsCenter.clear()
                        listOfWordsCenter.addAll(composesASentenceResults.listOfWordsCenter)
                        listOfWordsRight.clear()
                        listOfWordsRight.addAll(composesASentenceResults.listOfWordsRight)
                    }
                    R.id.img2 -> {
                        val chosenWordCenter = listOfWordsCenter[i]
                        listOfWordsCenter.clear()
                        listOfWordsCenter.add(chosenWordCenter)
                        val composesASentenceResults: ComposesASentenceResults =
                            GrammarHelper.composesASentence(
                                context,
                                realm,
                                numberOfWordsChosen,
                                "center",
                                leftColumnContent!!,
                                middleColumnContent!!,
                                rightColumnContent!!,
                                listOfWordsLeft,
                                listOfWordsCenter,
                                listOfWordsRight,
                                sharedLastPlayer!!
                            )
                        //
                        numberOfWordsChosen = composesASentenceResults.numberOfWordsChosen
                        leftColumnContent = composesASentenceResults.leftColumnContent
                        middleColumnContent = composesASentenceResults.middleColumnContent
                        rightColumnContent = composesASentenceResults.rightColumnContent
                        listOfWordsLeft.clear()
                        listOfWordsLeft.addAll(composesASentenceResults.listOfWordsLeft)
                        listOfWordsCenter.clear()
                        listOfWordsCenter.addAll(composesASentenceResults.listOfWordsCenter)
                        listOfWordsRight.clear()
                        listOfWordsRight.addAll(composesASentenceResults.listOfWordsRight)
                    }
                    R.id.img3 -> {
                        val chosenWordRight = listOfWordsRight[i]
                        listOfWordsRight.clear()
                        listOfWordsRight.add(chosenWordRight)
                        val composesASentenceResults: ComposesASentenceResults =
                            GrammarHelper.composesASentence(
                                context,
                                realm,
                                numberOfWordsChosen,
                                "right",
                                leftColumnContent!!,
                                middleColumnContent!!,
                                rightColumnContent!!,
                                listOfWordsLeft,
                                listOfWordsCenter,
                                listOfWordsRight,
                                sharedLastPlayer!!
                            )
                        //
                        numberOfWordsChosen = composesASentenceResults.numberOfWordsChosen
                        leftColumnContent = composesASentenceResults.leftColumnContent
                        middleColumnContent = composesASentenceResults.middleColumnContent
                        rightColumnContent = composesASentenceResults.rightColumnContent
                        listOfWordsLeft.clear()
                        listOfWordsLeft.addAll(composesASentenceResults.listOfWordsLeft)
                        listOfWordsCenter.clear()
                        listOfWordsCenter.addAll(composesASentenceResults.listOfWordsCenter)
                        listOfWordsRight.clear()
                        listOfWordsRight.addAll(composesASentenceResults.listOfWordsRight)
                        listOfWordsRight = composesASentenceResults.listOfWordsRight
                    }
                }
                //
                if (numberOfWordsChosen == 3)
                {
                    // sentence text reading and search award
                    sentenceReadingOfTheTextAndStartSpeech()
                    //
                    rightColumnAwardType = getString(R.string.nessuno)
                    if (middleColumnContent != getString(R.string.nessuno)
                        && rightColumnContent != getString(R.string.nessuno))
                    {
                        rightColumnAwardType = searchAwardType(context, listOfWordsCenter[0], listOfWordsRight[0], realm )
                        if (rightColumnAwardType != getString(R.string.nessuno))
                        {
                            rightColumnUriPremiumVideo = searchUriPremiumVideo(context, listOfWordsCenter[0], listOfWordsRight[0], realm )
                        }
                    }
                }
                prepareTheFragmentTransaction()
                fragmentTransactionStart()
            }
            2 -> {
                // chosen the 3rd and last word of the sentence (to be checked if the sentence completes)
                // the following clicks on the words (case 3) will start the tts
                // 1a) if the word chosen is on the left, register the choice (if it has not already been made)
                // 1b) if the chosen word is in the center, the choice is not allowed
                // (because it has already been chosen)
                // 1c) if the word chosen is on the right I register the choice
                // (if it has not already been made)
                when (view!!.id) {
                    R.id.img1 -> {
                        val chosenWordLeft = listOfWordsLeft[i]
                        listOfWordsLeft.clear()
                        listOfWordsLeft.add(chosenWordLeft)
                        val composesASentenceResults: ComposesASentenceResults =
                            GrammarHelper.composesASentence(
                                context,
                                realm,
                                numberOfWordsChosen,
                                "left",
                                leftColumnContent!!,
                                middleColumnContent!!,
                                rightColumnContent!!,
                                listOfWordsLeft,
                                listOfWordsCenter,
                                listOfWordsRight,
                                sharedLastPlayer!!
                            )
                        //
                        numberOfWordsChosen = composesASentenceResults.numberOfWordsChosen
                        leftColumnContent = composesASentenceResults.leftColumnContent
                        middleColumnContent = composesASentenceResults.middleColumnContent
                        rightColumnContent = composesASentenceResults.rightColumnContent
                        listOfWordsLeft.clear()
                        listOfWordsLeft.addAll(composesASentenceResults.listOfWordsLeft)
                        listOfWordsCenter.clear()
                        listOfWordsCenter.addAll(composesASentenceResults.listOfWordsCenter)
                        listOfWordsRight.clear()
                        listOfWordsRight.addAll(composesASentenceResults.listOfWordsRight)
                    }
                    R.id.img2 -> {
                    }
                    R.id.img3 -> {
                        val chosenWordRight = listOfWordsRight[i]
                        listOfWordsRight.clear()
                        listOfWordsRight.add(chosenWordRight)
                        val composesASentenceResults: ComposesASentenceResults =
                            GrammarHelper.composesASentence(
                                context,
                                realm,
                                numberOfWordsChosen,
                                "right",
                                leftColumnContent!!,
                                middleColumnContent!!,
                                rightColumnContent!!,
                                listOfWordsLeft,
                                listOfWordsCenter,
                                listOfWordsRight,
                                sharedLastPlayer!!
                            )
                        //
                        numberOfWordsChosen = composesASentenceResults.numberOfWordsChosen
                        leftColumnContent = composesASentenceResults.leftColumnContent
                        middleColumnContent = composesASentenceResults.middleColumnContent
                        rightColumnContent = composesASentenceResults.rightColumnContent
                        listOfWordsLeft.clear()
                        listOfWordsLeft.addAll(composesASentenceResults.listOfWordsLeft)
                        listOfWordsCenter.clear()
                        listOfWordsCenter.addAll(composesASentenceResults.listOfWordsCenter)
                        listOfWordsRight.clear()
                        listOfWordsRight.addAll(composesASentenceResults.listOfWordsRight)
                    }
                }
                //
                if (numberOfWordsChosen == 3)
                {
                    // sentence text reading and search award
                    sentenceReadingOfTheTextAndStartSpeech()
                    //
                    rightColumnAwardType = getString(R.string.nessuno)
                    if (middleColumnContent != getString(R.string.nessuno)
                        && rightColumnContent != getString(R.string.nessuno))
                    {
                        rightColumnAwardType = searchAwardType(context, listOfWordsCenter[0], listOfWordsRight[0], realm )
                        if (rightColumnAwardType != getString(R.string.nessuno))
                        {
                            rightColumnUriPremiumVideo = searchUriPremiumVideo(context, listOfWordsCenter[0], listOfWordsRight[0], realm )
                        }
                    }
                }
                prepareTheFragmentTransaction()
                fragmentTransactionStart()
            }
            3 -> when (view!!.id) {
                R.id.img1 -> {
                    // TTS
                    tTS1 = TextToSpeech(context) { status ->
                        if (status != TextToSpeech.ERROR) {
                            tTS1!!.speak(
                                leftColumnContent,
                                TextToSpeech.QUEUE_FLUSH,
                                null,
                                getString(R.string.prova_tts)
                            )
                        } else {
                            Toast.makeText(context, status, Toast.LENGTH_SHORT).show()
                        }
                    }
                    //
                    if (preference_PrintPermissions == getString(R.string.character_y)) {
                        if (leftColumnContentUrlType == getString(R.string.character_a)) {
                            getTargetBitmapFromUrlUsingPicasso(leftColumnContentUrl, target1)
                        } else {
                            val f = File(leftColumnContentUrl!!)
                            getTargetBitmapFromFileUsingPicasso(f, target1)
                        }
                        val photoPrinter = PrintHelper(context)
                        photoPrinter.scaleMode = PrintHelper.SCALE_MODE_FIT
                        photoPrinter.printBitmap(getString(R.string.stampa_immagine1), bitmap1!!)
                    }
                }
                R.id.img2 -> {
                    // TTS
                    tTS1 = TextToSpeech(context) { status ->
                        if (status != TextToSpeech.ERROR) {
                            tTS1!!.speak(
                                middleColumnContent,
                                TextToSpeech.QUEUE_FLUSH,
                                null,
                                getString(R.string.prova_tts)
                            )
                        } else {
                            Toast.makeText(context, status, Toast.LENGTH_SHORT).show()
                        }
                    }
                    //
                    if (preference_PrintPermissions == getString(R.string.character_y)) {
                        if (middleColumnContentUrlType == getString(R.string.character_a)) {
                            getTargetBitmapFromUrlUsingPicasso(middleColumnContentUrl, target1)
                        } else {
                            val f = File(middleColumnContentUrl!!)
                            getTargetBitmapFromFileUsingPicasso(f, target1)
                        }
                        val photoPrinter = PrintHelper(context)
                        photoPrinter.scaleMode = PrintHelper.SCALE_MODE_FIT
                        photoPrinter.printBitmap(getString(R.string.stampa_immagine1), bitmap1!!)
                    }
                }
                R.id.img3 -> {
                    // TTS
                    tTS1 = TextToSpeech(context) { status ->
                        if (status != TextToSpeech.ERROR) {
                            tTS1!!.speak(
                                rightColumnContent,
                                TextToSpeech.QUEUE_FLUSH,
                                null,
                                getString(R.string.prova_tts)
                            )
                        } else {
                            Toast.makeText(context, status, Toast.LENGTH_SHORT).show()
                        }
                    }
                    //
                    if (preference_PrintPermissions == "Y") {
                        if (rightColumnContentUrlType == "A") {
                            getTargetBitmapFromUrlUsingPicasso(rightColumnContentUrl, target1)
                        } else {
                            val f = File(rightColumnContentUrl!!)
                            getTargetBitmapFromFileUsingPicasso(f, target1)
                        }
                        val photoPrinter = PrintHelper(context)
                        photoPrinter.scaleMode = PrintHelper.SCALE_MODE_FIT
                        photoPrinter.printBitmap(getString(R.string.stampa_immagine1), bitmap1!!)
                    }
                }
            }
            else -> {}
        }
    }

    /**
     * reads the previously completed sentence
     *
     *
     */
    fun readingOfTheText() {
        // text reading
        var leftColumnToSpeak: String
        var middleColumnToSpeak: String
        var rightColumnToSpeak: String
        if (leftColumnContent == getString(R.string.nessuno))
        { leftColumnToSpeak = " " } else { leftColumnToSpeak = leftColumnContent!! }
        if (rightColumnContent == getString(R.string.nessuno))
        { rightColumnToSpeak = " " } else { rightColumnToSpeak = rightColumnContent!! }
        toSpeak = (leftColumnToSpeak + " " + middleColumnContent
                + " " + rightColumnToSpeak)
        tTS1 = TextToSpeech(this) { status ->
            if (status != TextToSpeech.ERROR) {
                tTS1!!.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null, getString(R.string.prova_tts))
            } else {
                Toast.makeText(applicationContext, status, Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * text reading and start speech.
     *
     * @see readingOfTheText
     *
     * @see startSpeechGame1
     */
    fun sentenceReadingOfTheTextAndStartSpeech() {
            // text reading
            readingOfTheText()
            //
            val TIME_OUT = 2000
            Handler(Looper.getMainLooper()).postDelayed({ startSpeechGame1(rootViewImageFragment) }, TIME_OUT.toLong())
    }

     /**
     * welcome speech
     *
     *
     *
     * @see Phrases
     */
    fun welcomeSpeech() {
        // TTS
        val phraseToSearch1 = realm.where(Phrases::class.java)
            .equalTo(getString(R.string.tipo), getString(R.string.welcome_phrase_first_part))
            .findFirst()
        sharedLastPlayer =
            sharedPref.getString(getString(R.string.preference_LastPlayer), "DEFAULT")
        val phraseToSearch2 = realm.where(Phrases::class.java)
            .equalTo(getString(R.string.tipo), getString(R.string.welcome_phrase_second_part))
            .findFirst()
        if (phraseToSearch1 != null && phraseToSearch2 != null) {
            toSpeak = (phraseToSearch1.descrizione + " " + sharedLastPlayer
                    + " " + phraseToSearch2.descrizione)
            tTS1 = TextToSpeech(this) { status ->
                if (status != TextToSpeech.ERROR) {
                    tTS1!!.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null, "prova tts")
                } else {
                    Toast.makeText(applicationContext, status, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    /**
     * check the results of speech.
     * with an allowed margin of error, if there is a correspondence
     * upload a video as a reward or charge the activity balloon , a simple balloon game, as a reward
     *
     *
     *
     * @param eTextResultOfSpeech string result from SpeechRecognizerManagement
     * @see GrammarHelper.thereIsACorrespondenceWithAnAllowedMarginOfError
     *
     * @see uploadAVideoAsAReward
     *
     * @see uploadAYoutubeVideoAsAReward
     *
     * @see BalloonGameplayActivity
     *
     * @see fragmentTransactionStart
     */
    fun checkAnswer(eTextResultOfSpeech: String) {
        // convert uppercase letter to lowercase
        val eText = eTextResultOfSpeech.lowercase(Locale.getDefault())
        val leftColumnContentToCompare: String
        if (leftColumnContent == getString(R.string.nessuno)) { leftColumnContentToCompare = "" }
            else { leftColumnContentToCompare = leftColumnContent!! }
        val middleColumnContentToCompare: String
        if (middleColumnContent == getString(R.string.nessuno)) { middleColumnContentToCompare = "" }
            else { middleColumnContentToCompare = middleColumnContent!! }
        val rightColumnContentToCompare: String
        if (rightColumnContent == getString(R.string.nessuno)) { rightColumnContentToCompare = "" }
            else { rightColumnContentToCompare = rightColumnContent!! }
        if (GrammarHelper.thereIsACorrespondenceWithAnAllowedMarginOfError(
                eText, leftColumnContentToCompare + " " + middleColumnContentToCompare
                        + " " + rightColumnContentToCompare, preference_AllowedMarginOfError
            )
        ) {
            if (rightColumnAwardType == null || rightColumnAwardType == getString(R.string.nessuno)) {
                // charge the activity balloon as a reward
                // a simple balloon game
                val intent = Intent(applicationContext, BalloonGameplayActivity::class.java)
                // ad uso futuro
                intent.putExtra(EXTRA_MESSAGE_BALLOON, getString(R.string.pensieri_e_parole))
                startActivity(intent)
            } else {
                when (rightColumnAwardType) {
                    "V" ->                         // upload a video as a reward
                        uploadAVideoAsAReward()
                    "Y" ->                         // upload a Youtube video as a reward
                        uploadAYoutubeVideoAsAReward()
                    else -> {
                        // charge the activity balloon as a reward
                        // a simple balloon game
                        val intent = Intent(applicationContext, BalloonGameplayActivity::class.java)
                        // ad uso futuro
                        intent.putExtra(
                            EXTRA_MESSAGE_BALLOON,
                            getString(R.string.pensieri_e_parole)
                        )
                        startActivity(intent)
                    }
                }
            }
        } else {
            fragmentTransactionStart()
        }
    }

    /**
     * upload a video as a reward
     *
     *
     *
     * @see PrizeFragment
     */
    fun uploadAVideoAsAReward() {
        // upload a video as a reward
        val frag = PrizeFragment()
        val bundle = Bundle()
        bundle.putString(getString(R.string.award_type), rightColumnAwardType)
        bundle.putString(getString(R.string.uri_premium_video), rightColumnUriPremiumVideo)
        frag.arguments = bundle
        val ft = supportFragmentManager.beginTransaction()
        val fragmentgotinstance =
            supportFragmentManager.findFragmentByTag(getString(R.string.game1_second_level_fragment)) as Game1SecondLevelFragment?
        val prizefragmentgotinstance =
            supportFragmentManager.findFragmentByTag(getString(R.string.prize_fragment)) as PrizeFragment?
        if (fragmentgotinstance != null || prizefragmentgotinstance != null) {
            ft.replace(R.id.game_container_game1, frag, getString(R.string.prize_fragment))
        } else {
            ft.add(R.id.game_container_game1, frag, getString(R.string.prize_fragment))
        }
        ft.addToBackStack(null)
        ft.commit()
    }

    /**
     * upload a Youtube video as a reward
     *
     *
     *
     * @see YoutubePrizeFragment
     */
    fun uploadAYoutubeVideoAsAReward() {
        // upload a Youtube video as a reward
        val yfrag = YoutubePrizeFragment()
        val ybundle = Bundle()
        ybundle.putString(getString(R.string.award_type), rightColumnAwardType)
        ybundle.putString(getString(R.string.uri_premium_video), rightColumnUriPremiumVideo)
        yfrag.arguments = ybundle
        val yft = supportFragmentManager.beginTransaction()
        val yfragmentgotinstance =
            supportFragmentManager.findFragmentByTag(getString(R.string.game1_second_level_fragment)) as Game1SecondLevelFragment?
        val yprizefragmentgotinstance =
            supportFragmentManager.findFragmentByTag(getString(R.string.prize_fragment)) as PrizeFragment?
        if (yfragmentgotinstance != null || yprizefragmentgotinstance != null) {
            yft.replace(
                R.id.game_container_game1,
                yfrag,
                getString(R.string.youtube_prize_fragment)
            )
        } else {
            yft.add(R.id.game_container_game1, yfrag, getString(R.string.youtube_prize_fragment))
        }
        yft.addToBackStack(null)
        yft.commit()
    }

    companion object {
        //
        const val EXTRA_MESSAGE_BALLOON = "GameJavaClass"
    }
}