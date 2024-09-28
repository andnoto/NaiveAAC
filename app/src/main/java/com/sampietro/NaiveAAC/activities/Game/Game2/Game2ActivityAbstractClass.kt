package com.sampietro.NaiveAAC.activities.Game.Game2

import android.speech.tts.TextToSpeech
import com.sampietro.NaiveAAC.R
import com.sampietro.NaiveAAC.activities.BaseAndAbstractClass.GameActivityAbstractClassWithRecognizerCallback
import com.sampietro.NaiveAAC.activities.Game.GameADA.GameADAArrayList
import com.sampietro.NaiveAAC.activities.Grammar.GrammarHelper.searchVerb
import com.sampietro.NaiveAAC.activities.Grammar.GrammarHelper.splitString
import com.sampietro.NaiveAAC.activities.Graphics.ImageSearchHelper.searchId
import com.sampietro.NaiveAAC.activities.Graphics.ImageSearchHelper.searchUri
import com.sampietro.NaiveAAC.activities.history.ToBeRecordedInHistoryImpl
import com.sampietro.NaiveAAC.activities.history.VoiceToBeRecordedInHistory
import io.realm.Realm
import java.util.Calendar

/**
 * <h1>Game2ActivityAbstractClass</h1>
 *
 * **Game2Activity** displays images (uploaded by the user or Arasaac pictograms) of the words
 * spoken after pressing the listen button
 *
 * Refer to [raywenderlich.com](https://www.raywenderlich.com/8192680-viewpager2-in-android-getting-started)
 * By [Rajdeep Singh](https://www.raywenderlich.com/u/rajdeep1008)
 *
 * @version     5.0, 01/04/2024
 * @see GameActivityAbstractClassWithRecognizerCallback
 */
abstract class Game2ActivityAbstractClass : GameActivityAbstractClassWithRecognizerCallback() {
    // lines inserted to remedy the incorrect double onresults that occurs with android 11
    var previouseText = ""

    // TTS
    var tTS1: TextToSpeech? = null
    var toSpeak: String? = null
    var reminderPhraseCounter = 0

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
     * @see GameActivityAbstractClassWithRecognizerCallback
     *
     * @see com.sampietro.NaiveAAC.activities.VoiceRecognition.RecognizerCallback
     */
    override fun onEndOfSpeech(editText: String?) {
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
     * @see searchUri
     * @see searchId
    */
    //    public ToBeRecordedInHistory<VoiceToBeRecordedInHistory> gettoBeRecordedInHistory(Realm realm, String eText)
    fun gettoBeRecordedInHistory(realm: Realm, eText: String?): ToBeRecordedInHistoryImpl {
        // decomposes EditText
        val arrWords = splitString(eText!!)
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
            0, " ", eText, " ", " ", " "
        )
        toBeRecordedInHistory.add(voiceToBeRecordedInHistory)
        // SEARCH THE IMAGES OF THE WORDS
        val arrWordsLength = arrWords.size
        val galleryList = ArrayList<GameADAArrayList>()
        //
        for (i in 0 until arrWordsLength) {
            val createList = GameADAArrayList()
            createList.image_title = arrWords[i]
            createList.urlType = getString(R.string.non_trovato)
            createList.url = getString(R.string.non_trovato)
            createList.video = ""
            createList.sound = ""
            createList.soundReplacesTTS = ""
            createList.soundAssociatedWithThePhraseReplacesTheOtherSounds = ""
            //
            // INTERNAL MEMORY IMAGE SEARCH
            val uriToSearch = searchUri(context, realm, arrWords[i])
            if (uriToSearch != getString(R.string.non_trovata)) {
                createList.urlType = "S"
                createList.url = uriToSearch
            } else {
                // SEARCH VERBS WITH REALM
                val verbToSearch = searchVerb(context, arrWords[i], realm)
                var idToSearch: String
                idToSearch = if (verbToSearch != getString(R.string.non_trovato)) {
                    searchId(context, realm, verbToSearch)
                } else {
                    searchId(context, realm,arrWords[i])
                }
                // IMAGE SEARCH ON ARASAAC
                if (idToSearch != getString(R.string.non_trovata)) {
                    val url = REMOTE_ADDR_PICTOGRAM + idToSearch + getString(R.string.download_false)
                    createList.urlType = "A"
                    createList.url = url
                }
            }
            //
            galleryList.add(createList)
        }
        // sentence arrangement
        var i = 0
        while (i < arrWordsLength) {
            if (galleryList[i].urlType == getString(R.string.non_trovato)
                && i+1 < arrWordsLength) {
                galleryList[i+1].image_title = galleryList[i].image_title + " " + galleryList[i+1].image_title
            }
            //
            i++
        }
        i = arrWordsLength - 1
        while (i > 0) {
            if (galleryList[i].urlType == getString(R.string.non_trovato)) {
                    galleryList[i-1].image_title = galleryList[i-1].image_title + " " + galleryList[arrWordsLength - 1].image_title
             }
            else {
                break
            }
            //
            i--
        }
        //
        i = 0
        while (i < arrWordsLength) {
            // history recording
            if (galleryList[i].urlType != getString(R.string.non_trovato)) {
                voiceToBeRecordedInHistory = VoiceToBeRecordedInHistory(
                    sharedLastSession,
                    sharedLastPhraseNumber, currentTime,
                    i + 1, " ",
                    galleryList[i].image_title!!," ", galleryList[i].urlType!!, galleryList[i].url!!
                )
                toBeRecordedInHistory.add(voiceToBeRecordedInHistory)
            }
            //
            i++
        }
        return toBeRecordedInHistory
    }
    companion object {
        //
        private const val REMOTE_ADDR_PICTOGRAM = "https://api.arasaac.org/api/pictograms/"
    }
}