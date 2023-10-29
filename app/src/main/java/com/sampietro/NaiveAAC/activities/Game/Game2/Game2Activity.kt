package com.sampietro.NaiveAAC.activities.Game.Game2

import android.content.Intent
import com.sampietro.NaiveAAC.activities.Game.Utils.HistoryRegistrationHelper.historyAdd
import com.sampietro.NaiveAAC.activities.Game.Utils.GameActivityAbstractClass
import android.speech.tts.TextToSpeech
import android.os.Bundle
import com.sampietro.NaiveAAC.R
import com.sampietro.NaiveAAC.activities.Game.Utils.ActionbarFragment
import com.sampietro.NaiveAAC.activities.Phrases.Phrases
import android.widget.Toast
import com.sampietro.NaiveAAC.activities.Game.Utils.GameFragmentHear
import android.widget.EditText
import com.sampietro.NaiveAAC.activities.history.VoiceToBeRecordedInHistory
import android.speech.SpeechRecognizer
import com.sampietro.NaiveAAC.activities.Grammar.GrammarHelper
import com.sampietro.NaiveAAC.activities.history.ToBeRecordedInHistoryImpl
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.sampietro.NaiveAAC.activities.Game.ChoiseOfGame.ChoiseOfGameActivity
import com.sampietro.NaiveAAC.activities.Graphics.ImageSearchHelper
import com.sampietro.NaiveAAC.activities.Settings.VerifyActivity
import com.sampietro.NaiveAAC.activities.VoiceRecognition.AndroidPermission
import com.sampietro.NaiveAAC.activities.VoiceRecognition.SpeechRecognizerManagement
import com.sampietro.NaiveAAC.activities.VoiceRecognition.SpeechRecognizerManagement.destroyRecognizer
import com.sampietro.NaiveAAC.activities.VoiceRecognition.SpeechRecognizerManagement.prepareSpeechRecognizer
import io.realm.Realm
import java.util.*

/**
 * <h1>Game2Activity</h1>
 *
 * **Game2Activity** displays images (uploaded by the user or Arasaac pictograms) of the words
 * spoken after pressing the listen button
 *
 * Refer to [raywenderlich.com](https://www.raywenderlich.com/8192680-viewpager2-in-android-getting-started)
 * By [Rajdeep Singh](https://www.raywenderlich.com/u/rajdeep1008)
 *
 * @version     4.0, 09/09/2023
 * @see GameActivityAbstractClass
 */
class Game2Activity : GameActivityAbstractClass() {
    // lines inserted to remedy the incorrect double onresults that occurs with android 11
    var previouseText = ""

    // TTS
    var tTS1: TextToSpeech? = null
    var toSpeak: String? = null
    var reminderPhraseCounter = 0

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
     * @see Phrases
     *
     * @see android.app.Activity.onCreate
     * @see fragmentTransactionStart
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)
        /*
USED FOR FULL SCREEN
*/
        val mContentView: ViewGroup = findViewById(R.id.activity_game_2_id)
        setToFullScreen()
        val viewTreeObserver = mContentView.getViewTreeObserver()
        //
        if (viewTreeObserver.isAlive) {
            viewTreeObserver.addOnGlobalLayoutListener(object :
                ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    mContentView.getViewTreeObserver().removeOnGlobalLayoutListener(this)
                }
            })
        }
        //
        mContentView.setOnClickListener(View.OnClickListener { view: View? -> setToFullScreen() })
        /*

        */

        //
        AndroidPermission.checkPermission(this)
        //
        prepareSpeechRecognizer(this)
        //
//        if (savedInstanceState == null) {
//            supportFragmentManager.beginTransaction()
//                .add(ActionbarFragment(), getString(R.string.actionbar_fragment)).commit()
//        }
        //
        realm = Realm.getDefaultInstance()
        // TTS
        val phraseToSearch1 = realm.where(Phrases::class.java)
            .equalTo(getString(R.string.tipo), getString(R.string.welcome_phrase_first_part))
            .findFirst()
        sharedPref = getSharedPreferences(
            getString(R.string.preference_file_key), MODE_PRIVATE
        )
        val sharedLastPlayer =
            sharedPref.getString(getString(R.string.preference_LastPlayer), "DEFAULT")
        val phraseToSearch2 = realm.where(Phrases::class.java)
            .equalTo(getString(R.string.tipo), getString(R.string.welcome_phrase_second_part))
            .findFirst()
        if (phraseToSearch1 != null && phraseToSearch2 != null) {
            toSpeak = (phraseToSearch1.descrizione + " " + sharedLastPlayer
                    + " " + phraseToSearch2.descrizione)
            if (savedInstanceState == null) {
                tTS1 = TextToSpeech(this) { status ->
                    if (status != TextToSpeech.ERROR) {
                        tTS1!!.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null, "prova tts")
                    } else {
                        Toast.makeText(applicationContext, status, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
        //
        context = this
        //
        sharedPref = context.getSharedPreferences(
            getString(R.string.preference_file_key), MODE_PRIVATE
        )
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
        //
        fragmentTransactionStart("")
    }

    /**
     * destroy SpeechRecognizer, TTS shutdown
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
//        findViewById<View>(R.id.activity_game_1_viewpager_id).systemUiVisibility =
//            View.SYSTEM_UI_FLAG_LOW_PROFILE or View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
    }
    //
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
    //
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
     * @see com.sampietro.NaiveAAC.activities.Game.Utils.HistoryRegistrationHelper.historyAdd
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
        val frag = Game2Fragment()
        val bundle = Bundle()
        bundle.putInt(getString(R.string.last_phrase_number), sharedLastPhraseNumber!!)
        bundle.putString("eText", eText)
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
     * @see com.sampietro.NaiveAAC.activities.Game.Utils.HistoryRegistrationHelper.historyAdd
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
     * @see GrammarHelper.lookForTheAnswerToLastPieceOfTheSentence
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
                    GrammarHelper.lookForTheAnswerToLastPieceOfTheSentence(
                        sharedLastPhraseNumber!!, realm
                    )
                //
                val sharedLastPlayer =
                    sharedPref.getString(getString(R.string.preference_LastPlayer), "DEFAULT")
                if (answerToLastPieceOfTheSentence != " ") {
                    toSpeak = "$sharedLastPlayer $answerToLastPieceOfTheSentence"
                    tTS1 = TextToSpeech(this) { status ->
                        if (status != TextToSpeech.ERROR) {
                            // tTS1.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);
                            tTS1!!.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null, "prova tts")
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

    /**
     * Called on beginning of speech.
     *
     * @param eText string message from SpeechRecognizerManagement
     * @see com.sampietro.NaiveAAC.activities.VoiceRecognition.RecognizerCallback
     *
     */
    override fun onBeginningOfSpeech(eText: String?) {}

    /**
     * Called on end of speech.
     * overrides the method on GameActivityAbstractClass
     *
     * @param editText string message from SpeechRecognizerManagement
     * @see GameActivityAbstractClass
     *
     * @see com.sampietro.NaiveAAC.activities.VoiceRecognition.RecognizerCallback
     */
    override fun onEndOfSpeech(editText: String?) {
//
    }

    /**
     * prepares the list of items to be registered on History
     *
     * @param realm realm obtained from the activity by Realm#getDefaultInstance
     * @param eText string to be registered on History
     * @return ToBeRecordedInHistory<VoiceToBeRecordedInHistory> list of items to be registered on History
     * @see VoiceToBeRecordedInHistory
     *
     * @see com.sampietro.NaiveAAC.activities.history.ToBeRecordedInHistory
     *
     * @see ImageSearchHelper.searchUri
     * @see ImageSearchHelper.searchId
     * @see GrammarHelper.searchType
     * @see GrammarHelper.searchPlural
    </VoiceToBeRecordedInHistory> */
    //    public ToBeRecordedInHistory<VoiceToBeRecordedInHistory> gettoBeRecordedInHistory(Realm realm, String eText)
    fun gettoBeRecordedInHistory(realm: Realm?, eText: String?): ToBeRecordedInHistoryImpl {
        // decomposes EditText
        val arrWords = GrammarHelper.splitString(eText!!)
        // initializes the list of items to be registered on History
        val toBeRecordedInHistory: ToBeRecordedInHistoryImpl
        toBeRecordedInHistory = ToBeRecordedInHistoryImpl()
        // adds the first entry to be recorded on History to the list
        val editor = sharedPref.edit()
        val hasLastPhraseNumber =
            sharedPref.contains(getString(R.string.preference_last_phrase_number))
        sharedLastPhraseNumber = if (!hasLastPhraseNumber) {
            // is the first recorded sentence and LastPhraseNumber log on sharedpref
            // with the number 1
            1
        } else {
            // it is not the first sentence recorded and I add 1 to LastPhraseNumber on sharedprefs
            sharedPref.getInt(getString(R.string.preference_last_phrase_number), 1) + 1
        }
        editor.putInt(getString(R.string.preference_last_phrase_number), sharedLastPhraseNumber!!)
        editor.apply()
        //
        val currentTime = Calendar.getInstance().time
        //
        voiceToBeRecordedInHistory = VoiceToBeRecordedInHistory(
            sharedLastSession!!, sharedLastPhraseNumber!!, currentTime,
            0, " ", eText!!, " ", " ", " "
        )
        toBeRecordedInHistory.add(voiceToBeRecordedInHistory)
        // SEARCH THE IMAGES OF THE WORDS
        val arrWordsLength = arrWords.size
        var i = 0
        while (i < arrWordsLength) {
            // INTERNAL MEMORY IMAGE SEARCH
            val uriToSearch = ImageSearchHelper.searchUri(realm!!, arrWords[i])
            if (uriToSearch != getString(R.string.non_trovata)) {
                voiceToBeRecordedInHistory = VoiceToBeRecordedInHistory(
                    sharedLastSession!!,
                    sharedLastPhraseNumber!!, currentTime,
                    i + 1, " ", arrWords[i], " ", "S", uriToSearch
                )
                toBeRecordedInHistory.add(voiceToBeRecordedInHistory)
            } else {
                // SEARCH VERBS WITH REALM
                val verbToSearch = GrammarHelper.searchVerb(arrWords[i], realm)
                var idToSearch: String
                idToSearch = if (verbToSearch != getString(R.string.non_trovato)) {
                    ImageSearchHelper.searchId(realm, verbToSearch)
                } else {
                    ImageSearchHelper.searchId(realm, arrWords[i])
                }
                // IMAGE SEARCH ON ARASAAC
                if (idToSearch != getString(R.string.non_trovata)) {
                    //  SEARCH TYPE OF WORD
                    val typeToSearch = GrammarHelper.searchType(idToSearch, realm)
                    // SEARCH IF IT IS PLURAL
                    val pluralToSearch = GrammarHelper.searchPlural(idToSearch, arrWords[i], realm)
                    //
                    val url = REMOTE_ADDR_PICTOGRAM + idToSearch + "?download=false"
                    voiceToBeRecordedInHistory = VoiceToBeRecordedInHistory(
                        sharedLastSession!!, sharedLastPhraseNumber!!,
                        currentTime,
                        i + 1, typeToSearch, arrWords[i], pluralToSearch, "A", url
                    )
                    toBeRecordedInHistory.add(voiceToBeRecordedInHistory)
                }
            }
            //
            i++
            // Log.d("TAG" + ": ", "some on result Response : " );
        }
        return toBeRecordedInHistory
    } //

    companion object {
        //
        private const val REMOTE_ADDR_PICTOGRAM = "https://api.arasaac.org/api/pictograms/"
    }
}