package com.sampietro.NaiveAAC.activities.Game.Utils

import android.content.Context
import com.sampietro.NaiveAAC.R
import com.sampietro.NaiveAAC.activities.Graphics.ImageSearchHelper
import com.sampietro.NaiveAAC.activities.Graphics.ResponseImageSearch
import com.sampietro.NaiveAAC.activities.WordPairs.WordPairs
import com.sampietro.NaiveAAC.activities.history.History
import com.sampietro.NaiveAAC.activities.history.ToBeRecordedInHistory
import com.sampietro.NaiveAAC.activities.history.ToBeRecordedInHistoryImpl
import com.sampietro.NaiveAAC.activities.history.VoiceToBeRecordedInHistory
import io.realm.Realm
import java.text.SimpleDateFormat
import java.util.*

/**
 * <h1>HistoryRegistrationHelper</h1>
 *
 *
 * **HistoryRegistrationHelper** utility class for History Registration.
 */
object HistoryRegistrationHelper {
    /**
     * history registration
     *
     * @param context context
     * @param realm realm obtained from the activity by Realm#getDefaultInstance
     * @param resultsWordPairsList List<WordPairs> to be registered on History
     * @param resultsWordPairsSize int with List<WordPairs> size
     * @see WordPairs
     *
     * @see VoiceToBeRecordedInHistory
     *
     * @see ToBeRecordedInHistory
     *
     * @see gettoBeRecordedInHistory
     *
     * @see historyAdd
    </WordPairs></WordPairs> */
    fun historyRegistration(
        context: Context,
        realm: Realm,
        resultsWordPairsList: MutableList<WordPairs>?,
        resultsWordPairsSize: Int,
        word1ToBeRecordedInHistory: Boolean
    ) {
//        val toBeRecordedInHistory: ToBeRecordedInHistory<VoiceToBeRecordedInHistory>
        var toBeRecordedInHistory = ToBeRecordedInHistoryImpl()
        toBeRecordedInHistory = gettoBeRecordedInHistory(
            context,
            realm,
            resultsWordPairsList,
            resultsWordPairsSize,
            word1ToBeRecordedInHistory
        )
        //
        val voicesToBeRecordedInHistory = toBeRecordedInHistory.voicesToBeRecordedInHistory
        //
        val numberOfVoicesToBeRecordedInHistory =
            toBeRecordedInHistory.getNumberOfVoicesToBeRecordedInHistory()
        //
        historyAdd(realm, numberOfVoicesToBeRecordedInHistory, voicesToBeRecordedInHistory!!)
    }

    /**
     * prepares the list of items to be registered on History
     *
     * @param context context
     * @param realm realm obtained from the activity by Realm#getDefaultInstance
     * @param resultsWordPairsList List<WordPairs> to be registered on History
     * @param resultsWordPairsSize int with size of List<WordPairs> to be registered on History
     * @param word1ToBeRecordedInHistory boolean true if word1 in List <WordPairs> must be recorded in the History
     * @return ToBeRecordedInHistory<VoiceToBeRecordedInHistory> list of items to be registered on History
     * @see VoiceToBeRecordedInHistory
     *
     * @see ToBeRecordedInHistory
     *
     * @see ToBeRecordedInHistoryImpl
     *
     * @see WordPairs
     *
     * @see ResponseImageSearch
     *
     * @see ImageSearchHelper.imageSearch
    </VoiceToBeRecordedInHistory></WordPairs></WordPairs></WordPairs> */
    fun gettoBeRecordedInHistory(
        context: Context,
        realm: Realm?,
        resultsWordPairsList: MutableList<WordPairs>?,
        resultsWordPairsSize: Int,
        word1ToBeRecordedInHistory: Boolean
    ): ToBeRecordedInHistoryImpl {
        // initializes the list of items to be registered on History
//        val toBeRecordedInHistory: ToBeRecordedInHistory<VoiceToBeRecordedInHistory>
        val toBeRecordedInHistory = ToBeRecordedInHistoryImpl()
        // adds the first entry to be recorded on History to the list
        val sharedPref = context.getSharedPreferences(
            context.getString(R.string.preference_file_key), Context.MODE_PRIVATE
        )
        val sharedLastSession =
            sharedPref.getInt(context.getString(R.string.preference_LastSession), 1)
        //
        val editor = sharedPref.edit()
        val hasLastPhraseNumber = sharedPref.contains("preference_LastPhraseNumber")
        val sharedLastPhraseNumber: Int
        sharedLastPhraseNumber = if (!hasLastPhraseNumber) {
            // is the first recorded sentence and LastPhraseNumber log on sharedpref
            //with the number 1
            1
        } else {
            // it is not the first sentence recorded and I add 1 to LastPhraseNumber on sharedprefs
            sharedPref.getInt("preference_LastPhraseNumber", 1) + 1
        }
        editor.putInt("preference_LastPhraseNumber", sharedLastPhraseNumber)
        editor.apply()
        //
        val currentTime = Calendar.getInstance().time
        //
        var voiceToBeRecordedInHistory = VoiceToBeRecordedInHistory(
            sharedLastSession, sharedLastPhraseNumber, currentTime,
            0, " ", "menu", " ", " ", " "
        )
        toBeRecordedInHistory.add(voiceToBeRecordedInHistory)
        // SEARCH THE IMAGES OF THE WORDS
        var resultWordPairs: WordPairs?
        var i = 0
        while (i < resultsWordPairsSize) {
            resultWordPairs = resultsWordPairsList!![i]
            // image search
            var image: ResponseImageSearch? = null
            image = if (word1ToBeRecordedInHistory) {
                ImageSearchHelper.imageSearch(realm!!, resultWordPairs.word1)
            } else {
                ImageSearchHelper.imageSearch(realm!!, resultWordPairs.word2)
            }
            if (image != null) {
                voiceToBeRecordedInHistory = if (word1ToBeRecordedInHistory) {
                    VoiceToBeRecordedInHistory(
                        sharedLastSession,
                        sharedLastPhraseNumber, currentTime,
                        i + 1, " ", resultWordPairs.word1!!,
                        " ", image.uriType, image.uriToSearch
                    )
                } else {
                    VoiceToBeRecordedInHistory(
                        sharedLastSession,
                        sharedLastPhraseNumber, currentTime,
                        i + 1, " ", resultWordPairs.word2!!,
                        " ", image.uriType, image.uriToSearch
                    )
                }
                toBeRecordedInHistory.add(voiceToBeRecordedInHistory)
            }
            //
            i++
        }
        return toBeRecordedInHistory
    }

    /**
     * registers items in History table
     *
     * @param realm realm obtained from the activity by Realm#getDefaultInstance
     * @param numberOfVoicesToBeRecordedInHistory int with number of voices to be recorded in History
     * @param voicesToBeRecordedInHistory List<VoiceToBeRecordedInHistory> list of items to be registered on History
     * @see VoiceToBeRecordedInHistory
     *
     * @see History
    </VoiceToBeRecordedInHistory> */
    @JvmStatic
    fun historyAdd(
        realm: Realm, numberOfVoicesToBeRecordedInHistory: Int,
        voicesToBeRecordedInHistory: MutableList<VoiceToBeRecordedInHistory?>
//        voicesToBeRecordedInHistory: ToBeRecordedInHistoryImpl?
    ) {
        var voiceToBeRecordedInHistory: VoiceToBeRecordedInHistory
        var irm = 0
        while (irm < numberOfVoicesToBeRecordedInHistory) {
            voiceToBeRecordedInHistory = voicesToBeRecordedInHistory[irm]!!
            realm.beginTransaction()
            val h = realm.createObject(
                History::class.java
            )
            //
            val session = voiceToBeRecordedInHistory.session
            val phraseNumber = voiceToBeRecordedInHistory.phraseNumber
            val date = voiceToBeRecordedInHistory.date
            val wordNumber = voiceToBeRecordedInHistory.wordNumber
            val type = voiceToBeRecordedInHistory.type
            val word = voiceToBeRecordedInHistory.word
            val plural = voiceToBeRecordedInHistory.plural
            val uriType = voiceToBeRecordedInHistory.uriType
            val uri = voiceToBeRecordedInHistory.uri
            val video = voiceToBeRecordedInHistory.video
            val sound = voiceToBeRecordedInHistory.sound
            val soundReplacesTTS = voiceToBeRecordedInHistory.soundReplacesTTS
            h.session = session.toString()
            h.phraseNumber = phraseNumber.toString()
            val simpleDate = SimpleDateFormat("dd/MM/yyyy")
            h.date = simpleDate.format(date)
            h.wordNumber = wordNumber.toString()
            h.type = type
            h.word = word
            h.plural = plural
            h.uriType = uriType
            h.uri = uri
            h.video = video
            h.sound = sound
            h.soundReplacesTTS = soundReplacesTTS
            realm.commitTransaction()
            //
            irm++
        }
    }
}