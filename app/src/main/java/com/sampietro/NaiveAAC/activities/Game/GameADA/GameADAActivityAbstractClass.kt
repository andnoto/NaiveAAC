package com.sampietro.NaiveAAC.activities.Game.GameADA

import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.view.View
import android.widget.ImageButton
import android.widget.Toast
import com.sampietro.NaiveAAC.R
import com.sampietro.NaiveAAC.activities.BaseAndAbstractClass.GameActivityAbstractClassWithRecognizerCallback
import com.sampietro.NaiveAAC.activities.Game.Utils.GameFragmentHear
import com.sampietro.NaiveAAC.activities.Game.Utils.GameHelper.historyAdd
import com.sampietro.NaiveAAC.activities.Game.Utils.PrizeFragment
import com.sampietro.NaiveAAC.activities.Game.Utils.YoutubePrizeFragment
import com.sampietro.NaiveAAC.activities.Graphics.GraphicsAndPrintingHelper.printPhrase
import com.sampietro.NaiveAAC.activities.Graphics.GraphicsAndPrintingHelper.setToFullScreen
import com.sampietro.NaiveAAC.activities.Graphics.ImageSearchHelper.imageSearch
import com.sampietro.NaiveAAC.activities.Graphics.ResponseImageSearch
import com.sampietro.NaiveAAC.activities.Stories.Stories
import com.sampietro.NaiveAAC.activities.VoiceRecognition.SpeechRecognizerManagement
import com.sampietro.NaiveAAC.activities.history.ToBeRecordedInHistory
import com.sampietro.NaiveAAC.activities.history.ToBeRecordedInHistoryImpl
import com.sampietro.NaiveAAC.activities.history.VoiceToBeRecordedInHistory
import io.realm.RealmResults
import java.util.Calendar

/**
 * <h1>GameADAActivity</h1>
 *
 * **GameADAActivity** displays images (uploaded by the user or Arasaac pictograms) of the
 * phrases of a story
 *
 *
 * @version     5.0, 01/04/2024
 * @see GameActivityAbstractClassWithRecognizerCallback
 */
abstract class GameADAActivityAbstractClass : GameActivityAbstractClassWithRecognizerCallback(), GameADARecyclerViewAdapterInterface,
    GameADAOnFragmentEventListener {
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
    var gameUseVideoAndSound: String? = null
    /**
     * destroy SpeechRecognizer
     *
     * @see android.app.Activity.onDestroy
     */
    override fun onDestroy() {
        super.onDestroy()
        SpeechRecognizerManagement.destroyRecognizer()
    }
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
        // riattivo i bottoni forward e last page
        val forwardImageButton = findViewById<View>(R.id.continuegameadabutton) as ImageButton
        val lastPageImageButton =
            findViewById<View>(R.id.lastpagegameadabutton) as ImageButton
        forwardImageButton.visibility = View.VISIBLE
        lastPageImageButton.visibility = View.VISIBLE
        //
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
        // riattivo i bottoni forward e last page
        val forwardImageButton = findViewById<View>(R.id.continuegameadabutton) as ImageButton
        val lastPageImageButton =
            findViewById<View>(R.id.lastpagegameadabutton) as ImageButton
        forwardImageButton.visibility = View.VISIBLE
        lastPageImageButton.visibility = View.VISIBLE
        //
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
    /**
     * View the next phrase.
     *
     * @see ToBeRecordedInHistory
     *
     * @see VoiceToBeRecordedInHistory
     *
     * @see historyAdd
     *
     * @see gettoBeRecordedInHistory
     *
     * @see fragmentTransactionStart
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
    /**
     * initiate Fragment transaction.
     *
     *
     *
     * @see GameADAFragment
     */
    open fun fragmentTransactionStart() {
        val mywindow = getWindow()
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
     * @see GameActivityAbstractClassWithRecognizerCallback
     * @see com.sampietro.NaiveAAC.activities.VoiceRecognition.RecognizerCallback
     */
    override fun onEndOfSpeech(editText: String?) {}
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
//                99 -> {
//                    voiceToBeRecordedInHistory = VoiceToBeRecordedInHistory(
//                        sharedLastSession,
//                        sharedLastPhraseNumber, currentTime,
//                        99, " ", phraseToDisplay!!.word!!,
//                        " ", " ", " "
//                    )
//                    toBeRecordedInHistory.add(voiceToBeRecordedInHistory)
//                }
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
//        rootViewImageFragment = v
        tTS1 = t
        galleryList = gL
    }
}