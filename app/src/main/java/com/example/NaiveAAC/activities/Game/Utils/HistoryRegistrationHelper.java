package com.example.NaiveAAC.activities.Game.Utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.NaiveAAC.R;
import com.example.NaiveAAC.activities.Graphics.ImageSearchHelper;
import com.example.NaiveAAC.activities.Graphics.ResponseImageSearch;
import com.example.NaiveAAC.activities.WordPairs.WordPairs;
import com.example.NaiveAAC.activities.history.History;
import com.example.NaiveAAC.activities.history.ToBeRecordedInHistory;
import com.example.NaiveAAC.activities.history.ToBeRecordedInHistoryImpl;
import com.example.NaiveAAC.activities.history.VoiceToBeRecordedInHistory;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import io.realm.Realm;

/**
 * <h1>HistoryRegistrationHelper</h1>
 *
 * <p><b>HistoryRegistrationHelper</b> utility class for History Registration.</p>
 */
public class HistoryRegistrationHelper {
    /**
     * history registration
     *
     * @param context context
     * @param realm realm obtained from the activity by Realm#getDefaultInstance
     * @param resultsWordPairsList List<WordPairs> to be registered on History
     * @param resultsWordPairsSize int with List<WordPairs> size
     * @see WordPairs
     * @see VoiceToBeRecordedInHistory
     * @see ToBeRecordedInHistory
     * @see #gettoBeRecordedInHistory
     * @see #historyAdd
     */
    public static void historyRegistration
    (Context context, Realm realm, List<WordPairs> resultsWordPairsList, int resultsWordPairsSize,
     boolean word1ToBeRecordedInHistory)
    {
        ToBeRecordedInHistory<VoiceToBeRecordedInHistory> toBeRecordedInHistory;
        toBeRecordedInHistory=gettoBeRecordedInHistory
                (context, realm, resultsWordPairsList,resultsWordPairsSize, word1ToBeRecordedInHistory);
        //
        List<VoiceToBeRecordedInHistory> voicesToBeRecordedInHistory =
                toBeRecordedInHistory.getVoicesToBeRecordedInHistory();
        //
        int numberOfVoicesToBeRecordedInHistory = toBeRecordedInHistory.getNumberOfVoicesToBeRecordedInHistory();
        //
        historyAdd(realm, numberOfVoicesToBeRecordedInHistory, voicesToBeRecordedInHistory);
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
     * @see ToBeRecordedInHistory
     * @see ToBeRecordedInHistoryImpl
     * @see WordPairs
     * @see ResponseImageSearch
     * @see ImageSearchHelper#imageSearch(Realm, String)
     */
    public static ToBeRecordedInHistory<VoiceToBeRecordedInHistory>
    gettoBeRecordedInHistory
    (Context context, Realm realm, List<WordPairs> resultsWordPairsList, int resultsWordPairsSize,
     boolean word1ToBeRecordedInHistory)
    {
        // initializes the list of items to be registered on History
        ToBeRecordedInHistory<VoiceToBeRecordedInHistory> toBeRecordedInHistory;
        toBeRecordedInHistory= new ToBeRecordedInHistoryImpl();
        // adds the first entry to be recorded on History to the list
        SharedPreferences sharedPref = context.getSharedPreferences(
                context.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        int sharedLastSession =
                sharedPref.getInt (context.getString(R.string.preference_LastSession), 1);
//
        SharedPreferences.Editor editor = sharedPref.edit();
        boolean hasLastPhraseNumber = sharedPref.contains("preference_LastPhraseNumber");
        int sharedLastPhraseNumber;
        if (!hasLastPhraseNumber) {
            // is the first recorded sentence and LastPhraseNumber log on sharedpref
            //with the number 1
            sharedLastPhraseNumber = 1;
        }
        else
        {
            // it is not the first sentence recorded and I add 1 to LastPhraseNumber on sharedprefs
            sharedLastPhraseNumber =
                    sharedPref.getInt ("preference_LastPhraseNumber", 1)+1;
        }
        editor.putInt("preference_LastPhraseNumber", sharedLastPhraseNumber);
        editor.apply();
        //
        Date currentTime = Calendar.getInstance().getTime();
        //
        VoiceToBeRecordedInHistory voiceToBeRecordedInHistory =
                new VoiceToBeRecordedInHistory(sharedLastSession, sharedLastPhraseNumber, currentTime,
                        0, " ", "menu", " "," ", " ");
        toBeRecordedInHistory.add(voiceToBeRecordedInHistory);
        // SEARCH THE IMAGES OF THE WORDS
        WordPairs resultWordPairs;
        int i=0;
        while(i < resultsWordPairsSize) {
            resultWordPairs = resultsWordPairsList.get(i);
            // image search
            ResponseImageSearch image = null;
            //
            assert resultWordPairs != null;
            if (word1ToBeRecordedInHistory) {
                image = ImageSearchHelper.imageSearch(realm, resultWordPairs.getWord1());
            } else
            {
                image = ImageSearchHelper.imageSearch(realm, resultWordPairs.getWord2());
            }
            if (image!=null) {
                if (word1ToBeRecordedInHistory) {
                    voiceToBeRecordedInHistory =
                            new VoiceToBeRecordedInHistory(sharedLastSession,
                                    sharedLastPhraseNumber, currentTime,
                                    i+1, " ", resultWordPairs.getWord1(),
                                    " ", image.getUriType(), image.getUriToSearch());
                } else
                {
                    voiceToBeRecordedInHistory =
                            new VoiceToBeRecordedInHistory(sharedLastSession,
                                    sharedLastPhraseNumber, currentTime,
                                    i+1, " ", resultWordPairs.getWord2(),
                                    " ", image.getUriType(), image.getUriToSearch());
                }
                toBeRecordedInHistory.add(voiceToBeRecordedInHistory);
            }
            //
            i++;
        }
        return toBeRecordedInHistory;
    }
    /**
     * registers items in History table
     *
     * @param realm realm obtained from the activity by Realm#getDefaultInstance
     * @param numberOfVoicesToBeRecordedInHistory int with number of voices to be recorded in History
     * @param voicesToBeRecordedInHistory List<VoiceToBeRecordedInHistory> list of items to be registered on History
     * @see VoiceToBeRecordedInHistory
     * @see History
     */
    public static void historyAdd(Realm realm, int numberOfVoicesToBeRecordedInHistory,
                                  List<VoiceToBeRecordedInHistory> voicesToBeRecordedInHistory)
    {
        VoiceToBeRecordedInHistory voiceToBeRecordedInHistory;
        int irm=0;
        while(irm < numberOfVoicesToBeRecordedInHistory ) {
            voiceToBeRecordedInHistory =
                    voicesToBeRecordedInHistory.get(irm);

            realm.beginTransaction();
            History h = realm.createObject(History.class);
            //
            Integer session = voiceToBeRecordedInHistory.getSession();
            Integer phraseNumber = voiceToBeRecordedInHistory.getPhraseNumber();
            Date date = voiceToBeRecordedInHistory.getDate();
            Integer wordNumber = voiceToBeRecordedInHistory.getWordNumber();
            String type = voiceToBeRecordedInHistory.getType();
            String word = voiceToBeRecordedInHistory.getWord();
            String plural = voiceToBeRecordedInHistory.getPlural();
            String uriType = voiceToBeRecordedInHistory.getUriType();
            String uri = voiceToBeRecordedInHistory.getUri();
            h.setSession(session);
            h.setPhraseNumber(phraseNumber);
            h.setDate(date);
            h.setWordNumber(wordNumber);
            h.setType(type);
            h.setWord(word);
            h.setPlural(plural);
            h.setUriType(uriType);
            h.setUri(uri);
            realm.commitTransaction();
            //
            irm++;
        }
    }
}
