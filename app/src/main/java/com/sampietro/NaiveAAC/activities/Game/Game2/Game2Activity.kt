package com.sampietro.NaiveAAC.activities.Game.Game2

import android.content.Intent
import android.os.Bundle
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.view.View
import android.view.Window
import android.widget.EditText
import android.widget.Toast
import com.sampietro.NaiveAAC.R
import com.sampietro.NaiveAAC.activities.Game.ChoiseOfGame.ChoiseOfGameActivity
import com.sampietro.NaiveAAC.activities.Game.Utils.ActionbarFragment
import com.sampietro.NaiveAAC.activities.Game.Utils.GameFragmentHear
import com.sampietro.NaiveAAC.activities.Game.Utils.GameHelper.historyAdd
import com.sampietro.NaiveAAC.activities.Game.Utils.GameHelper.welcomeSpeech
import com.sampietro.NaiveAAC.activities.Grammar.GrammarHelper.lookForTheAnswerToLastPieceOfTheSentence
import com.sampietro.NaiveAAC.activities.Graphics.GraphicsAndPrintingHelper.setToFullScreen
import com.sampietro.NaiveAAC.activities.Settings.VerifyActivity
import com.sampietro.NaiveAAC.activities.VoiceRecognition.AndroidPermission
import com.sampietro.NaiveAAC.activities.VoiceRecognition.SpeechRecognizerManagement
import com.sampietro.NaiveAAC.activities.VoiceRecognition.SpeechRecognizerManagement.prepareSpeechRecognizer
import com.sampietro.NaiveAAC.activities.history.VoiceToBeRecordedInHistory
import io.realm.Realm
import java.util.Locale

/**
 * <h1>Game2Activity</h1>
 *
 * **Game2Activity** displays images (uploaded by the user or Arasaac pictograms) of the words
 * spoken after pressing the listen button
 *
 * Refer to [raywenderlich.com](https://www.raywenderlich.com/8192680-viewpager2-in-android-getting-started)
 * By [Rajdeep Singh](https://www.raywenderlich.com/u/rajdeep1008)
 *
 * @version     5.0, 01/04/2024
 * @see Game2ActivityAbstractClass
 */
class Game2Activity : Game2ActivityAbstractClass() {
    // USED FOR FULL SCREEN
    lateinit var mywindow: Window
    //
    var sharedLastPlayer: String? = null
    /**
     * configurations of game2 start screen.
     *
     *
     *
     * @param savedInstanceState Define potentially saved parameters due to configurations changes.
     * @see SpeechRecognizerManagement.prepareSpeechRecognizer
     *
     * @see ActionbarFragment
     *
     * @see android.app.Activity.onCreate
     * @see fragmentTransactionStart
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)
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
        AndroidPermission.checkPermission(this)
        //
        prepareSpeechRecognizer(this)
        //
        realm = Realm.getDefaultInstance()
        //
        sharedPref = getSharedPreferences(
            getString(R.string.preference_file_key), MODE_PRIVATE
        )
        //
        sharedLastPlayer = sharedPref.getString(getString(R.string.preference_LastPlayer), "DEFAULT")
        //
        context = this
        //
        sharedLastSession = sharedPref.getInt(getString(R.string.preference_LastSession), 1)
        //
        val hasLastPhraseNumber =
            sharedPref.contains(getString(R.string.preference_last_phrase_number))
        sharedLastPhraseNumber = if (!hasLastPhraseNumber) {
            // no sentences have been recorded yet
            0
        } else {
            // it is not the first recorded sentence
            sharedPref.getInt(getString(R.string.preference_last_phrase_number), 1)
        }
        // TTS
        if (savedInstanceState == null)
        {
            tTS1 = TextToSpeech(context) { status ->
                if (status != TextToSpeech.ERROR) {
                    welcomeSpeech(context, realm, sharedLastPlayer!!, tTS1!!)
                } else {
                    Toast.makeText(context.applicationContext, status, Toast.LENGTH_SHORT).show()
                }
            }
        }
        //
        fragmentTransactionStart("")
    }
    /**
     * set to full screen
     *
     * @see android.app.Activity.onResume
     */
    override fun onResume() {
        super.onResume()
        mywindow = getWindow()
        setToFullScreen(mywindow)
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
    fun startSpeechGame2(v: View?) {
        SpeechRecognizerManagement.startSpeech()
        //
        val frag = GameFragmentHear()
        val ft = supportFragmentManager.beginTransaction()
        val fragmentgotinstance =
            supportFragmentManager.findFragmentByTag(getString(R.string.game2_fragment)) as Game2Fragment?
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
     * Called when the user taps the preview button.
     *
     * @param v view of tapped button
     * @see com.sampietro.NaiveAAC.activities.history.ToBeRecordedInHistory
     *
     * @see com.sampietro.NaiveAAC.activities.history.VoiceToBeRecordedInHistory
     *
     * @see com.sampietro.NaiveAAC.activities.Game.Utils.GameHelper.historyAdd
     *
     * @see fragmentTransactionStart
     */
    fun startPreview(v: View?) {
        //
        val sentenceToAdd = findViewById<View>(R.id.sentencetoadd) as EditText
        var eText = sentenceToAdd.text.toString()
        eText = eText.lowercase(Locale.getDefault())
        //
        val toBeRecordedInHistory = gettoBeRecordedInHistory(realm, eText)
        // REALM SESSION REGISTRATION
        val voicesToBeRecordedInHistory: MutableList<VoiceToBeRecordedInHistory?> =
            toBeRecordedInHistory.getListVoicesToBeRecordedInHistory()
        //
        val debugUrlNumber = toBeRecordedInHistory.getNumberOfVoicesToBeRecordedInHistory()
        //
        historyAdd(realm, debugUrlNumber, voicesToBeRecordedInHistory)
        //
        fragmentTransactionStart(eText)
    }

    /**
     * Called when the user taps the delete sentence button.
     *
     * @param v view of tapped button
     * @see .fragmentTransactionStart
     */
    fun deleteSentence(v: View?) {
        fragmentTransactionStart("")
    }

    /**
     * initiate Fragment transaction.
     *
     *
     *
     * @see Game2Fragment
     */
    fun fragmentTransactionStart(eText: String?) {
        val frag = Game2Fragment(R.layout.activity_game_2)
        val bundle = Bundle()
        bundle.putInt(getString(R.string.last_phrase_number), sharedLastPhraseNumber)
        bundle.putString(getString(R.string.etext), eText)
        frag.arguments = bundle
        val ft = supportFragmentManager.beginTransaction()
        val fragmentgotinstance =
            supportFragmentManager.findFragmentByTag(getString(R.string.game2_fragment)) as Game2Fragment?
        val hearfragmentgotinstance =
            supportFragmentManager.findFragmentByTag(getString(R.string.game_fragment_hear)) as GameFragmentHear?
        if (fragmentgotinstance != null || hearfragmentgotinstance != null) {
            ft.replace(R.id.game_container, frag, getString(R.string.game2_fragment))
        } else {
            ft.add(R.id.game_container, frag, getString(R.string.game2_fragment))
        }
        ft.addToBackStack(null)
        ft.commitAllowingStateLoss()
    }

    /**
     * Called on result of speech.
     *
     * @param eTextOnResult string result from SpeechRecognizerManagement
     * @see com.sampietro.NaiveAAC.activities.VoiceRecognition.RecognizerCallback
     *
     * @see com.sampietro.NaiveAAC.activities.history.ToBeRecordedInHistory
     *
     * @see VoiceToBeRecordedInHistory
     *
     * @see com.sampietro.NaiveAAC.activities.Game.Utils.GameHelper.historyAdd
     *
     * @see .fragmentTransactionStart
     */
    override fun onResult(eTextOnResult: String?) {
        // lines inserted to remedy the incorrect double onresults that occurs with android 11
        var eText = eTextOnResult
        if (eText != previouseText) {
            previouseText = eText!!
            // end lines inserted to remedy the incorrect double onresults that occurs with android 11
            // convert uppercase letter to lowercase
            eText = eText.lowercase(Locale.getDefault())
            //
            fragmentTransactionStart(eText)
            //
            reminderPhraseCounter = 0
        }
    }

    /**
     * Called on error from SpeechRecognizerManagement.
     * after 4 unsuccessful requests to speak, the app formulates a sentence / question
     * based on the user's last recorded sentence
     *
     * @param errorCode int error code from SpeechRecognizerManagement
     * @see com.sampietro.NaiveAAC.activities.VoiceRecognition.RecognizerCallback
     *
     * @see lookForTheAnswerToLastPieceOfTheSentence
     * @see fragmentTransactionStart
     */
    override fun onError(errorCode: Int) {
        if (errorCode == SpeechRecognizer.ERROR_SPEECH_TIMEOUT ||
            errorCode == SpeechRecognizer.ERROR_NO_MATCH
        ) {
            // TTS
            reminderPhraseCounter++
            if (reminderPhraseCounter > 4) {
                sharedPref = getSharedPreferences(
                    getString(R.string.preference_file_key), MODE_PRIVATE
                )
                //
                val hasLastPhraseNumber =
                    sharedPref.contains(getString(R.string.preference_last_phrase_number))
                sharedLastPhraseNumber = if (!hasLastPhraseNumber) {
                    // no sentences have been recorded yet
                    0
                } else {
                    // phrases have already been recorded
                    sharedPref.getInt(getString(R.string.preference_last_phrase_number), 1)
                }
                val answerToLastPieceOfTheSentence =
                    lookForTheAnswerToLastPieceOfTheSentence(
                        context, sharedLastPhraseNumber, realm
                    )
                //
                val sharedLastPlayer =
                    sharedPref.getString(getString(R.string.preference_LastPlayer), getString(R.string.default_string))
                if (answerToLastPieceOfTheSentence != " ") {
                    toSpeak = "$sharedLastPlayer $answerToLastPieceOfTheSentence"
                    tTS1 = TextToSpeech(this) { status ->
                        if (status != TextToSpeech.ERROR) {
                            // tTS1.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);
                            tTS1!!.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null, getString(R.string.prova_tts))
                        } else {
                            Toast.makeText(applicationContext, status, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                reminderPhraseCounter = 0
            }
        }
        //
        fragmentTransactionStart("")
    }
}