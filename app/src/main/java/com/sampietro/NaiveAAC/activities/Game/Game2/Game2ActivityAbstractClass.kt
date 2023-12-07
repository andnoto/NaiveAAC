package com.sampietro.NaiveAAC.activities.Game.Game2

import com.sampietro.NaiveAAC.activities.Game.Utils.GameActivityAbstractClass
import android.speech.tts.TextToSpeech
import com.sampietro.NaiveAAC.R
import com.sampietro.NaiveAAC.activities.history.VoiceToBeRecordedInHistory
import com.sampietro.NaiveAAC.activities.Grammar.GrammarHelper
import com.sampietro.NaiveAAC.activities.history.ToBeRecordedInHistoryImpl
import com.sampietro.NaiveAAC.activities.Graphics.ImageSearchHelper
import com.sampietro.NaiveAAC.activities.VoiceRecognition.SpeechRecognizerManagement.destroyRecognizer
import io.realm.Realm
import java.util.*

/**
 * <h1>Game2ActivityAbstractClass</h1>
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
abstract class Game2ActivityAbstractClass : GameActivityAbstractClass() {
    // lines inserted to remedy the incorrect double onresults that occurs with android 11
    var previouseText = ""

    // TTS
    var tTS1: TextToSpeech? = null
    var toSpeak: String? = null
    var reminderPhraseCounter = 0

    /**
     * destroy SpeechRecognizer, TTS shutdown
     *
     * @see android.app.Activity.onDestroy
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
    //
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
    fun gettoBeRecordedInHistory(realm: Realm, eText: String?): ToBeRecordedInHistoryImpl {
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
        editor.putInt(getString(R.string.preference_last_phrase_number), sharedLastPhraseNumber)
        editor.apply()
        //
        val currentTime = Calendar.getInstance().time
        //
        voiceToBeRecordedInHistory = VoiceToBeRecordedInHistory(
            sharedLastSession, sharedLastPhraseNumber, currentTime,
            0, " ", eText!!, " ", " ", " "
        )
        toBeRecordedInHistory.add(voiceToBeRecordedInHistory)
        // SEARCH THE IMAGES OF THE WORDS
        val arrWordsLength = arrWords.size
        var i = 0
        while (i < arrWordsLength) {
            // INTERNAL MEMORY IMAGE SEARCH
            val uriToSearch = ImageSearchHelper.searchUri(context, realm, arrWords[i])
            if (uriToSearch != getString(R.string.non_trovata)) {
                voiceToBeRecordedInHistory = VoiceToBeRecordedInHistory(
                    sharedLastSession,
                    sharedLastPhraseNumber, currentTime,
                    i + 1, " ", arrWords[i], " ", "S", uriToSearch
                )
                toBeRecordedInHistory.add(voiceToBeRecordedInHistory)
            } else {
                // SEARCH VERBS WITH REALM
                val verbToSearch = GrammarHelper.searchVerb(context, arrWords[i], realm)
                var idToSearch: String
                idToSearch = if (verbToSearch != getString(R.string.non_trovato)) {
                    ImageSearchHelper.searchId(context, realm, verbToSearch)
                } else {
                    ImageSearchHelper.searchId(context, realm, arrWords[i])
                }
                // IMAGE SEARCH ON ARASAAC
                if (idToSearch != getString(R.string.non_trovata)) {
                    //  SEARCH TYPE OF WORD
                    val typeToSearch = GrammarHelper.searchType(context, idToSearch, realm)
                    // SEARCH IF IT IS PLURAL
                    val pluralToSearch = GrammarHelper.searchPlural(context, idToSearch, arrWords[i], realm)
                    //
                    val url = REMOTE_ADDR_PICTOGRAM + idToSearch + getString(R.string.download_false)
                    voiceToBeRecordedInHistory = VoiceToBeRecordedInHistory(
                        sharedLastSession, sharedLastPhraseNumber,
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
    }
    companion object {
        //
        private const val REMOTE_ADDR_PICTOGRAM = "https://api.arasaac.org/api/pictograms/"
    }
}