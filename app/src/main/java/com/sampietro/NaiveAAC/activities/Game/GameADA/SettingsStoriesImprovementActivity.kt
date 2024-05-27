package com.sampietro.NaiveAAC.activities.Game.GameADA

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.view.inputmethod.InputMethodManager.HIDE_IMPLICIT_ONLY
import android.view.inputmethod.InputMethodManager.SHOW_IMPLICIT
import android.widget.EditText
import android.widget.ImageButton
import com.sampietro.NaiveAAC.R
import com.sampietro.NaiveAAC.activities.Game.ChoiseOfGame.ChoiseOfGameActivity
import com.sampietro.NaiveAAC.activities.Game.Game2.SettingsStoriesRegistrationActivity
import com.sampietro.NaiveAAC.activities.Game.Utils.ActionbarFragment
import com.sampietro.NaiveAAC.activities.Game.Utils.GameFragmentHear
import com.sampietro.NaiveAAC.activities.Game.Utils.GameHelper.historyAdd
import com.sampietro.NaiveAAC.activities.Game.Utils.PrizeFragment
import com.sampietro.NaiveAAC.activities.Game.Utils.YoutubePrizeFragment
import com.sampietro.NaiveAAC.activities.Grammar.GrammarHelper.splitString
import com.sampietro.NaiveAAC.activities.Graphics.GraphicsAndPrintingHelper.printImage
import com.sampietro.NaiveAAC.activities.Stories.Stories
import com.sampietro.NaiveAAC.activities.Stories.StoriesHelper
import com.sampietro.NaiveAAC.activities.VoiceRecognition.AndroidPermission
import com.sampietro.NaiveAAC.activities.VoiceRecognition.SpeechRecognizerManagement
import com.sampietro.NaiveAAC.activities.history.VoiceToBeRecordedInHistory
import io.realm.Realm
import io.realm.RealmResults
import java.util.*

/**
 * <h1>SettingsStoriesImprovementActivity</h1>
 *
 * **SettingsStoriesImprovementActivity** displays images (uploaded by the user or Arasaac pictograms) of the
 * phrases of a story
 *
 *
 * @version     5.0, 01/04/2024
 * @see GameADAActivityAbstractClass
 */
class SettingsStoriesImprovementActivity : GameADAActivityAbstractClass() {
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
        setContentView(R.layout.activity_settings_stories_improvement)
        //
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .add(ActionbarFragment(), getString(R.string.actionbar_fragment)).commit()
        }
        //
        AndroidPermission.checkPermission(this)
        //
        SpeechRecognizerManagement.prepareSpeechRecognizer(this)
        //
        realm = Realm.getDefaultInstance()
        // SEARCH HISTORY TO BE DISPLAYED
        val intent = intent
        if (intent.hasExtra(getString(R.string.story))) {
            sharedStory = intent.getStringExtra(getString(R.string.story))
            phraseToDisplayIndex = intent.getIntExtra(getString(R.string.phrase_number), 1)
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
        super.onResume()
        continueGameAda()
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
        //
        val d = Dialog(this)
        // Setting dialogview
        val window = d.window
        val wlp = window!!.attributes
        wlp.gravity = Gravity.BOTTOM
        wlp.flags = wlp.flags and WindowManager.LayoutParams.FLAG_DIM_BEHIND.inv()
        window.attributes = wlp
        //
        d.setTitle("frase da cercare")
        d.setCancelable(false)
        d.setContentView(R.layout.activity_game_ada_write_dialog)
        //
        val submitResponseOrRequestButton =
            d.findViewById<ImageButton>(R.id.submitResponseOrRequestButton)
        val cancelTheAnswerOrRequestButton =
            d.findViewById<ImageButton>(R.id.cancelTheAnswerOrRequestButton)
        val responseOrRequest = d.findViewById<View>(R.id.responseOrRequest) as EditText
        (d.findViewById<View>(R.id.responseOrRequest) as EditText).setHint("frase da cercare")
        responseOrRequest.inputType = InputType.TYPE_CLASS_TEXT
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
            extemporaneousInteractionWithVoice(eText)
            continueGameAda()
        }
        cancelTheAnswerOrRequestButton.setOnClickListener { //
            imm.hideSoftInputFromWindow(responseOrRequest.windowToken, 0)
            //
            d.cancel()
        }
        //
        d.show()
    }
    /**
     * initiate Fragment transaction.
     *
     *
     *
     * @see GameADAFragment
     */
    override fun fragmentTransactionStart() {
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
                SettingsStoriesImprovementViewPagerActivity::class.java
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
     * @see extemporaneousInteractionWithVoice
     * @see continueGameAda
     */
    override fun onResult(editText: String?) {
        // convert uppercase letter to lowercase
        var eText = editText
        eText = eText!!.lowercase(Locale.getDefault())
        //
        extemporaneousInteractionWithVoice(eText)
        continueGameAda()
    }
    //
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
        // checks if the eText is contained in the story
        val value0 = 0
        val wordsToLookFor = eText.lowercase(Locale.getDefault())
        val results: RealmResults<Stories> = realm.where(Stories::class.java)
            .beginGroup()
            .equalTo(getString(R.string.story), sharedStory)
            .equalTo(getString(R.string.wordnumberint), value0)
            .contains("word", wordsToLookFor)
            .endGroup()
            .findAll()
        if (results.size != 0) {
            phraseToDisplayIndex = results[0]!!.phraseNumberInt
        }
    }
    /**
     * Called when the user taps insert a sentence before this one button.
     *
     * @param view view of tapped picture
     *
     * @see SettingsStoriesRegistrationActivity
     */
    fun insertsASentenceBeforeThisOneButton(view: View?) {
        val intent: Intent?
        intent = Intent(
            this,
            SettingsStoriesRegistrationActivity::class.java
        )
        intent.putExtra(getString(R.string.story), sharedStory)
        intent.putExtra(getString(R.string.phrase_number), phraseToDisplayIndex)
        startActivity(intent)
    }
    /**
     * Called when the user taps insert a sentence after this button.
     *
     * @param view view of tapped picture
     *
     * @see SettingsStoriesRegistrationActivity
     */
    fun insertsASentenceAfterThisButton(view: View?) {
        val intent: Intent?
        intent = Intent(
            this,
            SettingsStoriesRegistrationActivity::class.java
        )
        intent.putExtra(getString(R.string.story), sharedStory)
        intent.putExtra(getString(R.string.phrase_number), phraseToDisplayIndex+1)
        startActivity(intent)
    }
    /**
     * Called when the user taps remove sentence button.
     *
     * @param view view of tapped picture
     *
     * @see SettingsStoriesImprovementActivity
     */
    fun deleteTheSentenceButton(view: View?) {
        realm = Realm.getDefaultInstance()
        val daCancellare =
            realm.where(Stories::class.java)
                .equalTo(getString(R.string.story), sharedStory)
                .equalTo(getString(R.string.phrasenumberint), phraseToDisplayIndex)
                .findAll()
        // delete
        realm.beginTransaction()
        daCancellare.deleteAllFromRealm()
        realm.commitTransaction()
        //
        StoriesHelper.renumberThePhrasesOfAStory(realm, sharedStory)
        //
        StoriesHelper.renumberAStory(realm, sharedStory)
        //
        continueGameAda()
    }
}