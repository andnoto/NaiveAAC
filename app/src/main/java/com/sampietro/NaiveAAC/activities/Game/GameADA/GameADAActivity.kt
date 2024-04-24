package com.sampietro.NaiveAAC.activities.Game.GameADA

import android.app.Activity
import android.app.Dialog
import com.sampietro.NaiveAAC.activities.Game.Utils.GameHelper.historyAdd
import com.sampietro.NaiveAAC.activities.Game.Utils.PrizeFragment.onFragmentEventListenerPrize
import com.sampietro.NaiveAAC.activities.Game.Utils.YoutubePrizeFragment.onFragmentEventListenerYoutubePrize
import io.realm.RealmResults
import com.sampietro.NaiveAAC.activities.Stories.Stories
import android.speech.tts.TextToSpeech
import android.os.Bundle
import com.sampietro.NaiveAAC.R
import android.content.Intent
import com.sampietro.NaiveAAC.activities.Game.ChoiseOfGame.ChoiseOfGameActivity
import com.sampietro.NaiveAAC.activities.history.VoiceToBeRecordedInHistory
import com.sampietro.NaiveAAC.activities.Settings.VerifyActivity
import android.widget.ImageButton
import android.widget.EditText
import android.text.InputType
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.view.inputmethod.InputMethodManager.HIDE_IMPLICIT_ONLY
import android.view.inputmethod.InputMethodManager.SHOW_IMPLICIT
import com.sampietro.NaiveAAC.activities.Game.Utils.GameFragmentHear
import android.widget.Toast
import com.sampietro.NaiveAAC.activities.BaseAndAbstractClass.GameActivityAbstractClassWithRecognizerCallback
import com.sampietro.NaiveAAC.activities.Game.Utils.PrizeFragment
import com.sampietro.NaiveAAC.activities.Game.Utils.YoutubePrizeFragment
import com.sampietro.NaiveAAC.activities.Game.Balloon.BalloonGameplayActivity
import com.sampietro.NaiveAAC.activities.Grammar.GrammarHelper.searchVerb
import com.sampietro.NaiveAAC.activities.Grammar.GrammarHelper.splitString
import com.sampietro.NaiveAAC.activities.Grammar.GrammarHelper.thereIsACorrespondenceWithAnAllowedMarginOfError
import com.sampietro.NaiveAAC.activities.Graphics.GraphicsAndPrintingHelper.printImage
import com.sampietro.NaiveAAC.activities.Graphics.GraphicsAndPrintingHelper.printPhrase
import com.sampietro.NaiveAAC.activities.Graphics.GraphicsAndPrintingHelper.setToFullScreen
import com.sampietro.NaiveAAC.activities.Graphics.ImageSearchHelper.imageSearch
import com.sampietro.NaiveAAC.activities.history.ToBeRecordedInHistory
import com.sampietro.NaiveAAC.activities.history.ToBeRecordedInHistoryImpl
import com.sampietro.NaiveAAC.activities.Graphics.ResponseImageSearch
import com.sampietro.NaiveAAC.activities.VoiceRecognition.AndroidPermission
import com.sampietro.NaiveAAC.activities.VoiceRecognition.SpeechRecognizerManagement
import com.sampietro.NaiveAAC.activities.history.History
import io.realm.Realm
import java.util.*

/**
 * <h1>GameADAActivity</h1>
 *
 * **GameADAActivity** displays images (uploaded by the user or Arasaac pictograms) of the
 * phrases of a story
 *
 *
 * @version     5.0, 01/04/2024
 * @see GameActivityAbstractClass
 */
class GameADAActivity : GameActivityAbstractClassWithRecognizerCallback(), GameADARecyclerViewAdapterInterface,
    onFragmentEventListenerPrize, GameADAOnFragmentEventListener,
    onFragmentEventListenerYoutubePrize {
    // USED FOR FULL SCREEN
    lateinit var mywindow: Window
    //
    var sharedStory: String? = null
    var resultsStories: RealmResults<Stories>? = null
    var sharedPhraseSize = 0

    //
    var phraseToDisplayIndex = 0
    var phraseToDisplay: Stories? = null

    //
    var preference_PrintPermissions: String? = null
    var preference_AllowedMarginOfError = 0

    //
    var wordToDisplayIndex = 0
    var ttsEnabled = true

    //
    lateinit var galleryList: ArrayList<GameADAArrayList>
    /**
     * used for TTS
     */
    var tTS1: TextToSpeech? = null

    //
    private var gameUseVideoAndSound: String? = null

    /**
     * configurations of gameADA start screen.
     *
     *
     *
     * @param savedInstanceState Define potentially saved parameters due to configurations changes.
     * @see SpeechRecognizerManagement.prepareSpeechRecognizer
     *
     * @see android.app.Activity.onCreate
     * @see fragmentTransactionStart
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //
        setContentView(R.layout.activity_game_ada)
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
        AndroidPermission.checkPermission(this)
        //
        SpeechRecognizerManagement.prepareSpeechRecognizer(this)
        //
        realm = Realm.getDefaultInstance()
        // SEARCH HISTORY TO BE DISPLAYED
        val intent = intent
        if (intent.hasExtra(ChoiseOfGameActivity.EXTRA_MESSAGE_GAME_PARAMETER)) {
            sharedStory = intent.getStringExtra(ChoiseOfGameActivity.EXTRA_MESSAGE_GAME_PARAMETER)
            phraseToDisplayIndex = 1
        }
        // SEARCH IF GAME USE VIDEO AND SOUND
        if (intent.hasExtra(ChoiseOfGameActivity.EXTRA_MESSAGE_GAME_USE_VIDEO_AND_SOUND)) {
            gameUseVideoAndSound =
                intent.getStringExtra(ChoiseOfGameActivity.EXTRA_MESSAGE_GAME_USE_VIDEO_AND_SOUND)
        }
        // if intent is from GameADAViewPagerActivity
        if (intent.hasExtra(getString(R.string.story_to_display))) {
            sharedStory = intent.getStringExtra(getString(R.string.story_to_display))
            phraseToDisplayIndex = intent.getIntExtra(getString(R.string.phrase_to_display_index), 1)
            wordToDisplayIndex = intent.getIntExtra(getString(R.string.word_to_display_index), 0)
            gameUseVideoAndSound = intent.getStringExtra(getString(R.string.game_use_video_and_sound))
            ttsEnabled = false
        }
        //
        context = this
        //
        sharedPref = context.getSharedPreferences(
            getString(R.string.preference_file_key), MODE_PRIVATE
        )
        //
        if (sharedStory == null) {
            sharedStory = sharedPref.getString(getString(R.string.preference_story), getString(R.string.default_string))
        }
        // resumes the story from the last sentence displayed
        if (intent.hasExtra(ChoiseOfGameActivity.EXTRA_MESSAGE_GAME_PARAMETER)) {
            val hasPhraseToDisplayIndex =
                sharedPref.contains(sharedStory + getString(R.string.preference_phrasetodisplayindex))
            if (hasPhraseToDisplayIndex) {
                phraseToDisplayIndex =
                    sharedPref.getInt(sharedStory + getString(R.string.preference_phrasetodisplayindex), 1)
            }
        }
        //
        val hasLastPhraseNumber = sharedPref.contains(getString(R.string.preference_last_phrase_number))
        if (hasLastPhraseNumber) {
            sharedLastPhraseNumber = sharedPref.getInt(getString(R.string.preference_last_phrase_number), 1)
        }
        // if is print permitted then preference_PrintPermissions = Y
        preference_PrintPermissions = sharedPref.getString(
            context.getString(R.string.preference_print_permissions),
            getString(R.string.default_string)
        )
        //
        preference_AllowedMarginOfError =
            sharedPref.getInt(getString(R.string.preference_allowed_margin_of_error), 20)
        //
        // SEARCH FIRST PHRASE TO DISPLAY
        // Check whether we're recreating a previously destroyed instance
        if (savedInstanceState != null) {
            // The onSaveInstanceState method is called before an activity may be killed
            // (for Screen Rotation Handling) so that
            // when it comes back it can restore its state.
            // it display the last phrase
            // else display the first phrase
            phraseToDisplayIndex = savedInstanceState.getInt(getString(R.string.phrase_to_display_index))
            sharedLastPhraseNumber = savedInstanceState.getInt(getString(R.string.last_phrase_number))
            fragmentTransactionStart()
        } else {
            resultsStories = realm.where(Stories::class.java)
                .beginGroup()
                .equalTo(getString(R.string.story), sharedStory)
                .equalTo(getString(R.string.phrasenumberint), phraseToDisplayIndex)
                .endGroup()
                .findAll()
            sharedPhraseSize = resultsStories!!.size
            //
            resultsStories = resultsStories!!.sort(getString(R.string.wordnumberint))
            //
            if (sharedPhraseSize != 0) {
                val editor = sharedPref.edit()
                editor.putInt(sharedStory + getString(R.string.preference_phrasetodisplayindex), phraseToDisplayIndex)
                editor.apply()
                //
                val toBeRecordedInHistory = gettoBeRecordedInHistory()
                // REALM SESSION REGISTRATION
                val voicesToBeRecordedInHistory: MutableList<VoiceToBeRecordedInHistory?> =
                    toBeRecordedInHistory.getListVoicesToBeRecordedInHistory()
                //
                val debugUrlNumber =
                    toBeRecordedInHistory.getNumberOfVoicesToBeRecordedInHistory()
                //
                historyAdd(realm, debugUrlNumber, voicesToBeRecordedInHistory)
                //
                fragmentTransactionStart()
            }
        }
    }
    //
    /**
     * Hide the Navigation Bar and go to the sentence
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
        USED FOR FULL SCREEN
         */
        continueGameAda()
    }
    //
    /**
     * destroy SpeechRecognizer
     *
     * @see android.app.Activity.onDestroy
     */
    override fun onDestroy() {
        super.onDestroy()
        SpeechRecognizerManagement.destroyRecognizer()
    }
    //
    /**
     * This method is called before an activity may be killed so that when it comes back some time in the future it can restore its state..
     *
     *
     * it stores the last phrase number
     *
     * @param savedInstanceState Define potentially saved parameters due to configurations changes.
     */
    public override fun onSaveInstanceState(savedInstanceState: Bundle) {
        //
        savedInstanceState.putInt(getString(R.string.phrase_to_display_index), phraseToDisplayIndex)
        savedInstanceState.putInt(getString(R.string.word_to_display_index), wordToDisplayIndex)
        savedInstanceState.putInt(getString(R.string.last_phrase_number), sharedLastPhraseNumber)
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
    //
    /**
     * Called when the user taps the print button.
     *
     *
     * @param v view of tapped button
     * @see .printPhraseCacheImages
     *
     * @see .printPhraseCreateMergedImages
     */
    fun printSentenceOfAStory(v: View?) {
        printPhrase(context, realm, galleryList)
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
                navigate to settings screen
    */
        val intent = Intent(this, VerifyActivity::class.java)
        startActivity(intent)
    }

    /**
     * Called when the user taps the start write button.
     * Refer to [stackoverflow](https://stackoverflow.com/questions/9467026/changing-position-of-the-dialog-on-screen-android)
     * answer of [Aleks G](https://stackoverflow.com/users/717214/aleks-g)
     * Refer to [stackoverflow](https://stackoverflow.com/questions/8991522/how-can-i-set-the-focus-and-display-the-keyboard-on-my-edittext-programmatical)
     * answer of [ungalcrys](https://stackoverflow.com/users/443427/ungalcrys)
     *
     *
     * @param v view of tapped button
     */
    fun startWriteGameADA(v: View?) {
        val correspondingWord = searchAnswer()
        val correspondingWordIsNumeric = isNumeric(correspondingWord)
        //
        val d = Dialog(this)
        // Setting dialogview
        val window = d.window
        val wlp = window!!.attributes
        wlp.gravity = Gravity.BOTTOM
        wlp.flags = wlp.flags and WindowManager.LayoutParams.FLAG_DIM_BEHIND.inv()
        window.attributes = wlp
        //
        d.setTitle("inserisci la risposta o la richiesta")
        d.setCancelable(false)
        d.setContentView(R.layout.activity_game_ada_write_dialog)
        //
        val submitResponseOrRequestButton =
            d.findViewById<ImageButton>(R.id.submitResponseOrRequestButton)
        val cancelTheAnswerOrRequestButton =
            d.findViewById<ImageButton>(R.id.cancelTheAnswerOrRequestButton)
        val responseOrRequest = d.findViewById<View>(R.id.responseOrRequest) as EditText
        if (correspondingWordIsNumeric) {
            responseOrRequest.inputType = InputType.TYPE_CLASS_NUMBER
        } else {
            responseOrRequest.inputType = InputType.TYPE_CLASS_TEXT
        }
        //
        responseOrRequest.requestFocus()
        val imm = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(d.findViewById<View>(R.id.responseOrRequest),SHOW_IMPLICIT)
        imm.hideSoftInputFromWindow(d.findViewById<View>(R.id.responseOrRequest).getWindowToken(),HIDE_IMPLICIT_ONLY)
        //
        submitResponseOrRequestButton.setOnClickListener { //
            var eText = responseOrRequest.text.toString()
            eText = eText.lowercase(Locale.getDefault())
            //
            imm.hideSoftInputFromWindow(responseOrRequest.windowToken, 0)
            //
            d.cancel()
            //
            mywindow = getWindow()
            setToFullScreen(mywindow)
            //
            checkAnswer(eText)
        }
        cancelTheAnswerOrRequestButton.setOnClickListener { //
            imm.hideSoftInputFromWindow(responseOrRequest.windowToken, 0)
            //
            d.cancel()
            //
            mywindow = getWindow()
            setToFullScreen(mywindow)
        }
        //
        d.show()
    }

    /**
     * If exist return answer related to the phrase.
     *
     *
     * @return string answer related to the phrase
     */
    fun searchAnswer(): String {
        //
        context = this
        sharedPref = context.getSharedPreferences(
            getString(R.string.preference_file_key), MODE_PRIVATE
        )
        val hasLastPhraseNumber = sharedPref.contains(getString(R.string.preference_last_phrase_number))
        if (hasLastPhraseNumber) {
            sharedLastPhraseNumber = sharedPref.getInt(getString(R.string.preference_last_phrase_number), 1)
        }
        // if wordNumber = 999 the word contain the answer related to the phrase
        var correspondingWord = getString(R.string.non_trovata)
        val results = realm.where(
            History::class.java
        )
            .beginGroup()
            .equalTo(getString(R.string.phrase_number), sharedLastPhraseNumber.toString())
            .and()
            .equalTo(getString(R.string.wordnumber), 999.toString())
            .endGroup()
            .findAll()
        // hystory also contains the article that I have to exclude from the comparison
        val count = results.size
        if (count != 0) {
            val result = results[0]
            if (result != null) {
                val resultHistoryCorrespondingWord = result.word
                val arrResultHistoryCorrespondingWords =
                    splitString(resultHistoryCorrespondingWord!!)
                val arrCorrespondingWordsLength = arrResultHistoryCorrespondingWords.size
                correspondingWord = if (arrCorrespondingWordsLength == 2) {
                    arrResultHistoryCorrespondingWords[1]
                } else {
                    arrResultHistoryCorrespondingWords[0]
                }
            }
        }
        return correspondingWord
    }

    /**
     * Called when the user taps the start speech button.
     * replace with hear fragment
     *
     * Refer to [stackoverflow](https://stackoverflow.com/questions/43520688/findfragmentbyid-and-findfragmentbytag)
     * answer of [Ricardo](https://stackoverflow.com/users/4266957/ricardo)
     *
     * @param v view of tapped button
     * @see SpeechRecognizerManagement.startSpeech
     */
    fun startSpeechGameADA(v: View?) {
        SpeechRecognizerManagement.startSpeech()
        //
        val frag = GameFragmentHear()
        val ft = supportFragmentManager.beginTransaction()
        val fragmentgotinstance =
            supportFragmentManager.findFragmentByTag(getString(R.string.gamefragmentgameada)) as GameADAFragment?
        val hearfragmentgotinstance =
            supportFragmentManager.findFragmentByTag(getString(R.string.game_fragment_hear)) as GameFragmentHear?
        if (fragmentgotinstance != null || hearfragmentgotinstance != null) {
            ft.replace(R.id.game_container, frag, getString(R.string.game_fragment_hear))
            // ok, we got the fragment instance, but should we manipulate its view?
        } else {
            ft.add(R.id.game_container, frag, getString(R.string.game_fragment_hear))
        }
        ft.addToBackStack(null)
        ft.commit()
    }

    /**
     * Called when the user taps the first page button.
     * go to the first sentence
     * @param v view of tapped button
     * @see .continueGameAda
     */
    fun firstPageGameAdaButton(v: View?) {
        if (tTS1 != null) {
            if (!tTS1!!.isSpeaking) {
                phraseToDisplayIndex = 1
                wordToDisplayIndex = 0
                continueGameAda()
            } else {
                Toast.makeText(
                    context,
                    getString(R.string.tts_sta_parlando_riprovare_pi_tardi),
                    Toast.LENGTH_SHORT
                ).show()
            }
        } else {
            phraseToDisplayIndex = 1
            wordToDisplayIndex = 0
            continueGameAda()
        }
    }

    /**
     * Called when the user taps the last page button.
     * go to the last sentence
     * @param v view of tapped button
     * @see .continueGameAda
     */
    fun lastPageGameAdaButton(v: View?) {
        // VIEW THE LAST PHRASE
        resultsStories = realm.where(Stories::class.java)
            .beginGroup()
            .equalTo(getString(R.string.story), sharedStory)
            .endGroup()
            .findAll()
        val resultsStoriesSize = (resultsStories)!!.size
        //
        resultsStories = (resultsStories)!!.sort(getString(R.string.phrasenumberint))
        //
        val resultStories = (resultsStories)!!.get(resultsStoriesSize - 1)!!
        if (tTS1 != null) {
            if (!tTS1!!.isSpeaking) {
                phraseToDisplayIndex = resultStories.phraseNumberInt
                wordToDisplayIndex = 0
                continueGameAda()
            } else {
                Toast.makeText(
                    context,
                    getString(R.string.tts_sta_parlando_riprovare_pi_tardi),
                    Toast.LENGTH_SHORT
                ).show()
            }
        } else {
            phraseToDisplayIndex = resultStories.phraseNumberInt
            wordToDisplayIndex = 0
            continueGameAda()
        }
    }

    /**
     * Called when the user taps the return button.
     * go to the previous sentence
     * @param v view of tapped button
     * @see .continueGameAda
     */
    fun returnGameAdaButton(v: View?) {
        if (tTS1 != null) {
            if (!tTS1!!.isSpeaking) {
                if (phraseToDisplayIndex > 1) {
                    phraseToDisplayIndex--
                    wordToDisplayIndex = 0
                }
                continueGameAda()
            } else {
                Toast.makeText(
                    context,
                    getString(R.string.tts_sta_parlando_riprovare_pi_tardi),
                    Toast.LENGTH_SHORT
                ).show()
            }
        } else {
            if (phraseToDisplayIndex > 1) {
                phraseToDisplayIndex--
                wordToDisplayIndex = 0
            }
            continueGameAda()
        }
    }
    //
    /**
     * Called when the user taps the continue button.
     * go to the next sentence
     * Refer to [stackoverflow](https://stackoverflow.com/questions/9702216/get-the-latest-fragment-in-backstack)
     * answer of [Deepak Goel](https://stackoverflow.com/users/628291/deepak-goel)
     * @param v view of tapped button
     * @see .continueGameAda
     */
    fun continueGameAdaButton(v: View?) {
        val fragmentBackStackIndex = supportFragmentManager.backStackEntryCount - 1
        val fragmentBackEntry = supportFragmentManager.getBackStackEntryAt(fragmentBackStackIndex)
        val fragmenttag = fragmentBackEntry.name
        if (tTS1 != null) {
            if (!tTS1!!.isSpeaking) {
                // it is necessary to increase the phraseToDisplayIndex by 1 only if a prize video is not being viewed
                if (fragmenttag != getString(R.string.prize_fragment) && fragmenttag != getString(R.string.youtube_prize_fragment)) {
                    phraseToDisplayIndex++
                    wordToDisplayIndex = 0
                }
                continueGameAda()
            } else {
                Toast.makeText(
                    context,
                    getString(R.string.tts_sta_parlando_riprovare_pi_tardi),
                    Toast.LENGTH_SHORT
                ).show()
            }
        } else {
            // it is necessary to increase the phraseToDisplayIndex by 1 only if a prize video is not being viewed
            if (fragmenttag != getString(R.string.prize_fragment) && fragmenttag != getString(R.string.youtube_prize_fragment)) {
                phraseToDisplayIndex++
                wordToDisplayIndex = 0
            }
            continueGameAda()
        }
    }
    //
    /**
     * View the next phrase.
     *
     * @see ToBeRecordedInHistory
     *
     * @see VoiceToBeRecordedInHistory
     *
     * @see com.sampietro.NaiveAAC.activities.Game.Utils.GameHelper.historyAdd
     *
     * @see .gettoBeRecordedInHistory
     *
     * @see .fragmentTransactionStart
     */
    fun continueGameAda() {
        // VIEW THE NEXT PHRASE
        resultsStories = realm.where(Stories::class.java)
            .beginGroup()
            .equalTo(getString(R.string.story), sharedStory)
            .equalTo(
                getString(R.string.phrasenumberint),
                phraseToDisplayIndex
            )
            .endGroup()
            .findAll()
        sharedPhraseSize = (resultsStories)!!.size
        //
        resultsStories = (resultsStories)!!.sort(getString(R.string.wordnumberint))
        // if there are no subsequent sentences, I start again from the first sentence
        if (sharedPhraseSize == 0) {
            phraseToDisplayIndex = 1
            resultsStories = realm.where(Stories::class.java)
                .beginGroup()
                .equalTo(getString(R.string.story), sharedStory)
                .equalTo(
                    getString(R.string.phrasenumberint),
                    phraseToDisplayIndex
                )
                .endGroup()
                .findAll()
            sharedPhraseSize = (resultsStories)!!.size
            //
            resultsStories = (resultsStories)!!.sort(getString(R.string.wordnumberint))
        }
        //
        if (sharedPhraseSize != 0) {
            val editor = sharedPref.edit()
            editor.putInt(sharedStory + getString(R.string.preference_phrasetodisplayindex), phraseToDisplayIndex)
            editor.apply()
            //
            val toBeRecordedInHistory = gettoBeRecordedInHistory()
            // REALM SESSION REGISTRATION
            val voicesToBeRecordedInHistory: MutableList<VoiceToBeRecordedInHistory?> =
                toBeRecordedInHistory.getListVoicesToBeRecordedInHistory()
            //
            val debugUrlNumber = toBeRecordedInHistory.getNumberOfVoicesToBeRecordedInHistory()
            //
            historyAdd(realm, debugUrlNumber, voicesToBeRecordedInHistory)
            //
            fragmentTransactionStart()
        }
    }
    //
    /**
     * initiate Fragment transaction.
     *
     *
     *
     * @see GameADAFragment
     */
    fun fragmentTransactionStart() {
        mywindow = getWindow()
        setToFullScreen(mywindow)
        //
        val frag = GameADAFragment(R.layout.activity_game_ada_recycler_view)
        val bundle = Bundle()
        bundle.putInt(getString(R.string.last_phrase_number), sharedLastPhraseNumber)
        bundle.putInt(getString(R.string.word_to_display_index), wordToDisplayIndex)
        bundle.putBoolean(getString(R.string.tts_enabled), ttsEnabled)
        ttsEnabled = true
        bundle.putString(getString(R.string.game_use_video_and_sound), gameUseVideoAndSound)
        frag.arguments = bundle
        val ft = supportFragmentManager.beginTransaction()
        val fragmentgotinstance =
            supportFragmentManager.findFragmentByTag(getString(R.string.gamefragmentgameada)) as GameADAFragment?
        val prizefragmentgotinstance =
            supportFragmentManager.findFragmentByTag(getString(R.string.prize_fragment)) as PrizeFragment?
        val youtubeprizefragmentgotinstance =
            supportFragmentManager.findFragmentByTag(getString(R.string.youtube_prize_fragment)) as YoutubePrizeFragment?
        val hearfragmentgotinstance =
            supportFragmentManager.findFragmentByTag(getString(R.string.gamefragmentgame1hear)) as GameFragmentHear?
        if (fragmentgotinstance != null || prizefragmentgotinstance != null || youtubeprizefragmentgotinstance != null || hearfragmentgotinstance != null) {
            ft.replace(R.id.game_container, frag, getString(R.string.gamefragmentgameada))
        } else {
            ft.add(R.id.game_container, frag, getString(R.string.gamefragmentgameada))
        }
        ft.addToBackStack(null)
        ft.commit()
    }

    /**
     * Called when the user taps a picture of the story.
     * print or switch to viewpager
     * @param view view of tapped picture
     * @param i int index of tapped picture
     * @param galleryList ArrayList<GameADAArrayList> list for choice of words
     * @see GameADAArrayList
     *
     * @see .getTargetBitmapFromUrlUsingPicasso
     *
     * @see .getTargetBitmapFromFileUsingPicasso
     *
     * @see GameADAViewPagerActivity
    */
    override fun onItemClick(view: View?, i: Int, galleryList: ArrayList<GameADAArrayList>) {
        if (preference_PrintPermissions == "Y") {
            printImage(
                context,
                galleryList[i].urlType,
                galleryList[i].url,
                200,
                200
            )
        } else {
            val intent: Intent?
            intent = Intent(
                this,
                GameADAViewPagerActivity::class.java
            )
            intent.putExtra(getString(R.string.story_to_display), sharedStory)
            intent.putExtra(getString(R.string.phrase_to_display), phraseToDisplayIndex)
            intent.putExtra(getString(R.string.word_to_display), i + 1)
            intent.putExtra(getString(R.string.game_use_video_and_sound), gameUseVideoAndSound)
            startActivity(intent)
            //
        }
    }
    //
    /**
     * Called on result of speech.
     *
     * @param editText string result from SpeechRecognizerManagement
     * @see com.sampietro.NaiveAAC.activities.VoiceRecognition.RecognizerCallback
     *
     * @see .checkAnswer
     */
    override fun onResult(editText: String?) {
        checkAnswer(editText!!)
    }
    //
    /**
     * Called on error from SpeechRecognizerManagement.
     *
     * @param errorCode int error code from SpeechRecognizerManagement
     * @see com.sampietro.NaiveAAC.activities.VoiceRecognition.RecognizerCallback
     *
     * @see .fragmentTransactionStart
     */
    override fun onError(errorCode: Int) {
        fragmentTransactionStart()
    }
    //
    /**
     * Called on beginning of speech.
     *
     * @param eText string message from SpeechRecognizerManagement
     * @see com.sampietro.NaiveAAC.activities.VoiceRecognition.RecognizerCallback
     */
    override fun onBeginningOfSpeech(eText: String?) {}
    //
    /**
     * Called on end of speech.
     * overrides the method on GameActivityAbstractClass
     *
     * @param editText string message from SpeechRecognizerManagement
     * @see GameActivityAbstractClass
     *
     * @see com.sampietro.NaiveAAC.activities.VoiceRecognition.RecognizerCallback
     */
    override fun onEndOfSpeech(editText: String?) {}
    //
    /**
     * on callback from PrizeFragment to this Activity
     *
     *
     * @param v view inflated inside PrizeFragment
     * @see PrizeFragment
     */
    override fun receiveResultImagesFromPrizeFragment(v: View?) {
        rootViewPrizeFragment = v
    }

    /**
     * callback from PrizeFragment to this Activity on completation video
     *
     * go to the next sentence
     *
     * @param v view inflated inside PrizeFragment
     * @see .continueGameAdaButton
     *
     * @see PrizeFragment
     */
    override fun receiveResultOnCompletatioVideoFromPrizeFragment(v: View?) {
        continueGameAdaButton(v)
    }
    //
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
     * @see .continueGameAdaButton
     *
     * @see YoutubePrizeFragment
     */
    override fun receiveResultOnCompletatioVideoFromYoutubePrizeFragment(v: View?) {
        continueGameAdaButton(v)
    }
    //
    /**
     * Called on result of speech.
     * check the speech results
     *
     * @param editText string result from SpeechRecognizerManagement
     * @see com.sampietro.NaiveAAC.activities.VoiceRecognition.RecognizerCallback
     *
     * @see splitString
     *
     * @see searchVerb
     *
     * @see .extemporaneousInteractionWithVoice
     *
     * @see .continueGameAda
     */
    fun checkAnswer(editText: String) {
        // convert uppercase letter to lowercase
        var eText = editText
        eText = eText.lowercase(Locale.getDefault())
        // break down EditText
        val arrWords = splitString(eText)
        val arrWordsLength = arrWords.size
        //
        context = this
        sharedPref = context.getSharedPreferences(
            getString(R.string.preference_file_key), MODE_PRIVATE
        )
        val hasLastPhraseNumber = sharedPref.contains(getString(R.string.preference_last_phrase_number))
        if (hasLastPhraseNumber) {
            sharedLastPhraseNumber = sharedPref.getInt(getString(R.string.preference_last_phrase_number), 1)
        }
        // if wordNumber = 999 the word contain the answer related to the phrase
        var correspondingWord = getString(R.string.non_trovata)
        val results = realm.where(
            History::class.java
        )
            .beginGroup()
            .equalTo(getString(R.string.phrase_number), sharedLastPhraseNumber.toString())
            .and()
            .equalTo(getString(R.string.wordnumber), 999.toString())
            .endGroup()
            .findAll()
        // hystory also contains the article that I have to exclude from the comparison
        val count = results.size
        if (count != 0) {
            val result = results[0]
            if (result != null) {
                val resultHistoryCorrespondingWord = result.word
                val arrResultHistoryCorrespondingWords =
                    splitString(resultHistoryCorrespondingWord!!)
                val arrCorrespondingWordsLength = arrResultHistoryCorrespondingWords.size
                correspondingWord = if (arrCorrespondingWordsLength == 2) {
                    arrResultHistoryCorrespondingWords[1]
                } else {
                    arrResultHistoryCorrespondingWords[0]
                }
            }
        }
        // I look for the presence of the corresponding word in eText
        var theWordMatches = false
        //
        var i = 0
        while (i < arrWordsLength) {
            if (thereIsACorrespondenceWithAnAllowedMarginOfError(
                    arrWords[i],
                    correspondingWord,
                    preference_AllowedMarginOfError
                )
            )
            {
                theWordMatches = true
                break
            }
            // check if the word in eText is a conjugation of the corresponding word
            val verbToSearch = searchVerb(context, arrWords[i], realm)
            if (verbToSearch == correspondingWord) {
                theWordMatches = true
                break
            }
            //
            i++
        }
        // if the corresponding word is present in eText, I give the prize,
        // otherwise I answer "no, try again"
        if (!theWordMatches) {
            // treatment of impromptu responses
            //(go to the next sentence, go back to the previous sentence, go to topic)
            extemporaneousInteractionWithVoice(eText)
            continueGameAda()
        } else {
            // riattivo il bottone forward
            val forwardImageButton = findViewById<View>(R.id.continuegameadabutton) as ImageButton
            forwardImageButton.visibility = View.VISIBLE
            //
            val value999 = 999
            resultsStories = realm.where(Stories::class.java)
                .beginGroup()
                .equalTo(getString(R.string.story), sharedStory)
                .equalTo(
                    getString(R.string.phrasenumberint),
                    phraseToDisplayIndex
                )
                .equalTo(getString(R.string.wordnumberint), value999)
                .endGroup()
                .findAll()
            val answerSize = resultsStories!!.size
            if (answerSize > 0) {
                val resultStories = resultsStories!![0]!!
                val answerActionType = resultStories.answerActionType
                if (answerActionType == null) {
                    phraseToDisplayIndex++
                    wordToDisplayIndex = 0
                    // charge the activity balloon as a reward
                    // a simple balloon game
                    val intent = Intent(applicationContext, BalloonGameplayActivity::class.java)
                    // ad uso futuro
                    intent.putExtra(EXTRA_MESSAGE_BALLOON, getString(R.string.a_da))
                    startActivity(intent)
                } else {
                    val answerAction: String?
                    when (answerActionType) {
                        "V" -> {
                            // upload a video as a reward
                            answerAction = resultStories.answerAction
                            uploadAVideoAsAReward(answerAction)
                            //
                            phraseToDisplayIndex++
                            wordToDisplayIndex = 0
                        }
                        "Y" -> {
                            // upload a Youtube video as a reward
                            answerAction = resultStories.answerAction
                            uploadAYoutubeVideoAsAReward(answerAction)
                            //
                            phraseToDisplayIndex++
                            wordToDisplayIndex = 0
                        }
                        else -> {
                            phraseToDisplayIndex++
                            wordToDisplayIndex = 0
                            // charge the activity balloon as a reward
                            // a simple balloon game
                            val intent =
                                Intent(applicationContext, BalloonGameplayActivity::class.java)
                            // ad uso futuro
                            intent.putExtra(EXTRA_MESSAGE_BALLOON, getString(R.string.a_da))
                            startActivity(intent)
                        }
                    }
                }
            }
        }
    }

    /**
     * upload a video as a reward
     *
     *
     *
     * @see PrizeFragment
     */
    fun uploadAVideoAsAReward(uriPremiumVideo: String?) {
        mywindow = getWindow()
        setToFullScreen(mywindow)
        // upload the award video
        val frag = PrizeFragment()
        val bundle = Bundle()
        bundle.putString(getString(R.string.award_type), "V")
        bundle.putString(getString(R.string.uri_premium_video), uriPremiumVideo)
        frag.arguments = bundle
        val ft = supportFragmentManager.beginTransaction()
        val fragmentgotinstance =
            supportFragmentManager.findFragmentByTag(getString(R.string.gamefragmentgameada)) as GameADAFragment?
        if (fragmentgotinstance != null) {
            ft.replace(R.id.game_container, frag, getString(R.string.prize_fragment))
        } else {
            ft.add(R.id.game_container, frag, getString(R.string.prize_fragment))
        }
        ft.addToBackStack(getString(R.string.prize_fragment))
        ft.commit()
    }

    /**
     * upload a Youtube video as a reward
     *
     *
     *
     * @see YoutubePrizeFragment
     */
    fun uploadAYoutubeVideoAsAReward(uriPremiumVideo: String?) {
        mywindow = getWindow()
        setToFullScreen(mywindow)
        // upload a Youtube video as a reward
        val yfrag = YoutubePrizeFragment()
        val ybundle = Bundle()
        ybundle.putString(getString(R.string.award_type), "Y")
        ybundle.putString(getString(R.string.uri_premium_video), uriPremiumVideo)
        yfrag.arguments = ybundle
        val yft = supportFragmentManager.beginTransaction()
        val yfragmentgotinstance =
            supportFragmentManager.findFragmentByTag(getString(R.string.gamefragmentgameada)) as GameADAFragment?
        if (yfragmentgotinstance != null) {
            yft.replace(R.id.game_container, yfrag, getString(R.string.youtube_prize_fragment))
        } else {
            yft.add(R.id.game_container, yfrag, getString(R.string.youtube_prize_fragment))
        }
        yft.addToBackStack(getString(R.string.youtube_prize_fragment))
        yft.commit()
    }
    //
    /**
     * Called on result of speech.
     * treatment of impromptu responses
     * (go to the next sentence, go back to the previous sentence, go to topic)
     *
     * @param eText string result from SpeechRecognizerManagement
     * @see com.sampietro.NaiveAAC.activities.VoiceRecognition.RecognizerCallback
     *
     * @see splitString
     */
    fun extemporaneousInteractionWithVoice(eText: String) {
        // break down EditText
        val arrWords = splitString(eText)
        val arrWordsLength = arrWords.size
        // search for the presence in eText of the words forward (to the next sentence),
        // backwards (to the previous sentence), (go to) topic
        var i = 0
        val value999 = 999
        while (i < arrWordsLength) {
            if (arrWords[i] == "avanti") {
                // go to the next sentence only if this sentence does not contain a question
                val results: RealmResults<Stories> = realm.where(Stories::class.java)
                    .beginGroup()
                    .equalTo(getString(R.string.story), sharedStory)
                    .equalTo(
                        getString(R.string.phrasenumberint),
                        phraseToDisplayIndex
                    )
                    .equalTo(
                        getString(R.string.wordnumberint),
                        value999
                    )
                    .endGroup()
                    .findAll()
                val count = results.size
                if (count == 0) {
                    phraseToDisplayIndex++
                }
                break
            }
            if (arrWords[i] == "indietro") {
                if (phraseToDisplayIndex > 1) {
                    phraseToDisplayIndex--
                }
                break
            }
            //
            i++
        }
        // check if a story topic is in eText
        // (if wordNumber = 9999 the word contain the topic of the sentence)
        val value9999 = 9999
        val results: RealmResults<Stories> = realm.where(Stories::class.java)
            .beginGroup()
            .equalTo(getString(R.string.story), sharedStory)
            .equalTo(getString(R.string.wordnumberint), value9999)
            .endGroup()
            .findAll()
        val count = results.size
        if (count != 0) {
            i = 0
            while (i < count) {
                val result = results[i]!!
                // if the argument is in eText then that is the sentence to display
                if (eText.lowercase(Locale.getDefault())
                        .contains(result.word!!.lowercase(Locale.getDefault()))
                ) {
                    phraseToDisplayIndex = result.phraseNumberInt
                    break
                }
                //
                i++
            }
        }
    }

    /**
     * prepares the list of items to be registered on History
     *
     * @return ToBeRecordedInHistory<VoiceToBeRecordedInHistory> list of items to be registered on History
     * @see VoiceToBeRecordedInHistory
     *
     * @see ToBeRecordedInHistory
     *
     * @see imageSearch
    */
    fun gettoBeRecordedInHistory(): ToBeRecordedInHistoryImpl {
        // initializes the list of items to be registered on History
        val toBeRecordedInHistory: ToBeRecordedInHistoryImpl
        toBeRecordedInHistory = ToBeRecordedInHistoryImpl()
        // adds the first entry to be recorded on History to the list
        sharedPref = context.getSharedPreferences(
            getString(R.string.preference_file_key), MODE_PRIVATE
        )
        sharedLastSession = sharedPref.getInt(getString(R.string.preference_LastSession), 1)
        //
        val editor = sharedPref.edit()
        val hasLastPhraseNumber = sharedPref.contains(getString(R.string.preference_last_phrase_number))
        sharedLastPhraseNumber = if (!hasLastPhraseNumber) {
            // is the first recorded sentence and LastPhraseNumber log on sharedpref
            // with the number 1
            1
        } else {
            // it is not the first sentence recorded and I add 1 to LastPhraseNumber on sharedprefs
            sharedPref.getInt(getString(R.string.preference_last_phrase_number), 1) + 1
        }
        editor.putInt(getString(R.string.preference_last_phrase_number), sharedLastPhraseNumber)
        editor.apply()
        //
        val currentTime = Calendar.getInstance().time
        // SEARCH THE IMAGES OF THE WORDS
        var i = 0
        while (i < sharedPhraseSize) {
            phraseToDisplay = resultsStories!![i]
            assert(phraseToDisplay != null)
            val phraseToDisplayWordNumber = phraseToDisplay!!.wordNumberInt
            when (phraseToDisplayWordNumber) {
                0 -> {
                    voiceToBeRecordedInHistory = VoiceToBeRecordedInHistory(
                        sharedLastSession,
                        sharedLastPhraseNumber, currentTime,
                        0, " ", phraseToDisplay!!.word!!,
                        " ", " ", " ", " ", phraseToDisplay!!.sound,
                        " "
                    )
                    toBeRecordedInHistory.add(voiceToBeRecordedInHistory)
                }
                99 -> {
                    voiceToBeRecordedInHistory = VoiceToBeRecordedInHistory(
                        sharedLastSession,
                        sharedLastPhraseNumber, currentTime,
                        99, " ", phraseToDisplay!!.word!!,
                        " ", " ", " "
                    )
                    toBeRecordedInHistory.add(voiceToBeRecordedInHistory)
                }
                999 -> {
                    voiceToBeRecordedInHistory = VoiceToBeRecordedInHistory(
                        sharedLastSession,
                        sharedLastPhraseNumber, currentTime,
                        999, " ", phraseToDisplay!!.word!!,
                        " ", " ", " "
                    )
                    toBeRecordedInHistory.add(voiceToBeRecordedInHistory)
                }
                9999 -> {
                    voiceToBeRecordedInHistory = VoiceToBeRecordedInHistory(
                        sharedLastSession,
                        sharedLastPhraseNumber, currentTime,
                        9999, " ", phraseToDisplay!!.word!!,
                        " ", " ", " "
                    )
                    toBeRecordedInHistory.add(voiceToBeRecordedInHistory)
                }
                else -> {
                    // image search
                    var image: ResponseImageSearch?
                    val phraseToDisplayWord = phraseToDisplay!!.word
                    val phraseToDisplayUriType = phraseToDisplay!!.uriType
                    if (phraseToDisplayUriType != null) {
                        if (phraseToDisplayUriType == " " || phraseToDisplayUriType == "") {
                            image = imageSearch(context, realm, phraseToDisplayWord)
                            if (image != null) {
                                voiceToBeRecordedInHistory = VoiceToBeRecordedInHistory(
                                    sharedLastSession,
                                    sharedLastPhraseNumber, currentTime,
                                    1, " ", phraseToDisplayWord!!,
                                    " ", image.uriType, image.uriToSearch,
                                    phraseToDisplay!!.video, phraseToDisplay!!.sound,
                                    phraseToDisplay!!.soundReplacesTTS
                                )
                                toBeRecordedInHistory.add(voiceToBeRecordedInHistory)
                            }
                        } else {
                            voiceToBeRecordedInHistory = VoiceToBeRecordedInHistory(
                                sharedLastSession,
                                sharedLastPhraseNumber, currentTime,
                                1, " ", phraseToDisplayWord!!,
                                " ", phraseToDisplayUriType,
                                phraseToDisplay!!.uri!!,
                                phraseToDisplay!!.video, phraseToDisplay!!.sound,
                                phraseToDisplay!!.soundReplacesTTS
                            )
                            toBeRecordedInHistory.add(voiceToBeRecordedInHistory)
                        }
                    }
                }
            }
            //
            i++
        }
        return toBeRecordedInHistory
    }

    /**
     * on callback from GameFragment to this Activity
     *
     * @param v view root fragment view
     * @param t TextToSpeech
     * @param gL ArrayList<GameADAArrayList>
    </GameADAArrayList> */
    override fun receiveResultGameFragment(
        v: View?,
        t: TextToSpeech?,
        gL: ArrayList<GameADAArrayList>
    ) {
        rootViewImageFragment = v
        tTS1 = t
        galleryList = gL
    }
    //

    companion object {
        const val EXTRA_MESSAGE_BALLOON = "GameJavaClass"

        /**
         * return if a string is numeric.
         * Refer to [stackoverflow](https://stackoverflow.com/questions/1102891/how-to-check-if-a-string-is-numeric-in-java)
         * answer of [CraigTP](https://stackoverflow.com/users/57477/craigtp)
         * Refer to [Medium](https://betulnecanli.medium.com/regular-expressions-regex-in-kotlin-a2eaeb2cd113)
         * answer of [Betul Necanli](https://betulnecanli.medium.com/)
         *
         *
         * @param str string to verify
         * @return boolean true if the string is numeric
         */
        fun isNumeric(str: String): Boolean {
            return Regex("-?\\d+(\\.\\d+)?").matches(str)
        }
    }
}