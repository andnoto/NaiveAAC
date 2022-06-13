package com.example.NaiveAAC.activities.Game.Game2;

import static com.example.NaiveAAC.activities.Game.Utils.HistoryRegistrationHelper.historyAdd;
import static com.example.NaiveAAC.activities.Graphics.ImageSearchHelper.searchId;
import static com.example.NaiveAAC.activities.Graphics.ImageSearchHelper.searchUri;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
// import android.util.Log;
import android.widget.Toast;

import androidx.fragment.app.FragmentTransaction;

import com.example.NaiveAAC.R;
import com.example.NaiveAAC.activities.Game.Utils.ActionbarFragment;
import com.example.NaiveAAC.activities.Game.Utils.GameActivityAbstractClass;
import com.example.NaiveAAC.activities.Game.Utils.GameFragmentHear;
import com.example.NaiveAAC.activities.Grammar.GrammarHelper;
import com.example.NaiveAAC.activities.Graphics.ImageSearchHelper;
import com.example.NaiveAAC.activities.Phrases.Phrases;
import com.example.NaiveAAC.activities.history.ToBeRecordedInHistory;
import com.example.NaiveAAC.activities.history.ToBeRecordedInHistoryImpl;
import com.example.NaiveAAC.activities.history.VoiceToBeRecordedInHistory;
import com.example.voicerecognitionlibrary.AndroidPermission;
import com.example.voicerecognitionlibrary.SpeechRecognizerManagement;

import java.util.Calendar;
import java.util.List;

import io.realm.Realm;

/**
 * <h1>Game2Activity</h1>
 * <p><b>Game2Activity</b> displays images (uploaded by the user or Arasaac pictograms) of the words
 * spoken after pressing the listen button
 * </p>
 * Refer to <a href="https://www.raywenderlich.com/8192680-viewpager2-in-android-getting-started">raywenderlich.com</a>
 * By <a href="https://www.raywenderlich.com/u/rajdeep1008">Rajdeep Singh</a>
 *
 * @version     1.4, 19/05/22
 * @see GameActivityAbstractClass
 */
public class Game2Activity extends GameActivityAbstractClass {
    // TTS
    public TextToSpeech tTS1;
    public String toSpeak;
    public Integer reminderPhraseCounter = 0;
    //
    private static final String REMOTE_ADDR_PICTOGRAM=
    "https://api.arasaac.org/api/pictograms/";
    /**
     * configurations of game2 start screen.
     * <p>
     *
     * @param savedInstanceState Define potentially saved parameters due to configurations changes.
     * @see SpeechRecognizerManagement#prepareSpeechRecognizer
     * @see ActionbarFragment
     * @see Phrases
     * @see android.app.Activity#onCreate(Bundle)
     * @see #fragmentTransactionStart
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        //
        AndroidPermission.checkPermission(this);
        //
        SpeechRecognizerManagement.prepareSpeechRecognizer(this);
        //
        if (savedInstanceState == null)
        {
            getSupportFragmentManager().beginTransaction()
                    .add(new ActionbarFragment(), getString(R.string.actionbar_fragment)).commit();
        }
        //
        realm= Realm.getDefaultInstance();
        // TTS
        Phrases phraseToSearch1 = realm.where(Phrases.class)
                .equalTo(getString(R.string.tipo), getString(R.string.welcome_phrase_first_part))
                .findFirst();
        sharedPref = this.getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        String sharedLastPlayer =
                sharedPref.getString (getString(R.string.preference_LastPlayer), "DEFAULT");
        Phrases phraseToSearch2 = realm.where(Phrases.class)
                .equalTo(getString(R.string.tipo), getString(R.string.welcome_phrase_second_part))
                .findFirst();
        if ((phraseToSearch1 != null) && (phraseToSearch2 != null))  {
            toSpeak = phraseToSearch1.getDescrizione() + " " + sharedLastPlayer
                    + " " + phraseToSearch2.getDescrizione();
            if (savedInstanceState == null)
            {
                tTS1=new TextToSpeech(this, new TextToSpeech.OnInitListener() {
                    @Override
                    public void onInit(int status) {
                        if(status != TextToSpeech.ERROR) {
                        tTS1.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null, "prova tts");
                        }
                        else
                        {
                        Toast.makeText(getApplicationContext(), status,Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }
        //
        context = this;
        //
        sharedPref = context.getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        sharedLastSession =
                sharedPref.getInt (getString(R.string.preference_LastSession), 1);
//
        boolean hasLastPhraseNumber = sharedPref.contains(getString(R.string.preference_last_phrase_number));
        if (!hasLastPhraseNumber) {
            // no sentences have been recorded yet
            sharedLastPhraseNumber = 0;
        }
        else
        {
            // it is not the first recorded sentence
            sharedLastPhraseNumber =
                    sharedPref.getInt (getString(R.string.preference_last_phrase_number), 1);
        }
        //
        fragmentTransactionStart();
    }
    /**
     * destroy SpeechRecognizer, TTS shutdown
     *
     * @see androidx.fragment.app.Fragment#onDestroy
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        SpeechRecognizerManagement.destroyRecognizer();
        // TTS
        if (tTS1 != null) {
            tTS1.stop();
            tTS1.shutdown();
        }
    }
    /**
     * initiate Fragment transaction.
     * <p>
     *
     * @see Game2Fragment
     */
    public void fragmentTransactionStart()
    {
        Game2Fragment frag= new Game2Fragment();
        Bundle bundle = new Bundle();
        bundle.putInt(getString(R.string.last_phrase_number), sharedLastPhraseNumber);
        frag.setArguments(bundle);
        FragmentTransaction ft=getSupportFragmentManager().beginTransaction();
        Game2Fragment fragmentgotinstance =
                (Game2Fragment)
                        getSupportFragmentManager().findFragmentByTag(getString(R.string.game2_fragment));
        GameFragmentHear hearfragmentgotinstance =
                (GameFragmentHear)
                        getSupportFragmentManager().findFragmentByTag(getString(R.string.game_fragment_hear));
         if ((fragmentgotinstance != null) || (hearfragmentgotinstance != null))
        {
            ft.replace(R.id.game_container, frag, getString(R.string.game2_fragment));
        }
        else
        {
            ft.add(R.id.game_container, frag, getString(R.string.game2_fragment));
        }
        ft.addToBackStack(null);
        ft.commit();
    }
    /**
     * Called on result of speech.
     *
     * @param eText string result from SpeechRecognizerManagement
     * @see com.example.voicerecognitionlibrary.RecognizerCallback
     * @see ToBeRecordedInHistory
     * @see VoiceToBeRecordedInHistory
     * @see com.example.NaiveAAC.activities.Game.Utils.HistoryRegistrationHelper#historyAdd
     * @see #fragmentTransactionStart
     */
    @Override
    public void onResult(String eText) {
        // convert uppercase letter to lowercase
        eText = eText.toLowerCase();
        //
        toBeRecordedInHistory=gettoBeRecordedInHistory(realm, eText);
        // REALM SESSION REGISTRATION
        List<VoiceToBeRecordedInHistory> voicesToBeRecordedInHistory =
                toBeRecordedInHistory.getVoicesToBeRecordedInHistory();
        //
        int debugUrlNumber = toBeRecordedInHistory.getNumberOfVoicesToBeRecordedInHistory();
        //
        historyAdd(realm, debugUrlNumber, voicesToBeRecordedInHistory);
        //
        fragmentTransactionStart();
        //
        reminderPhraseCounter = 0;
}
    /**
     * Called on error from SpeechRecognizerManagement.
     * after 4 unsuccessful requests to speak, the app formulates a sentence / question
     * based on the user's last recorded sentence
     *
     * @param errorCode int error code from SpeechRecognizerManagement
     * @see com.example.voicerecognitionlibrary.RecognizerCallback
     * @see GrammarHelper#lookForTheAnswerToLastPieceOfTheSentence(int, Realm)
     * @see #fragmentTransactionStart
     */
    @Override
    public void onError(int errorCode) {
        if ((errorCode == SpeechRecognizer.ERROR_SPEECH_TIMEOUT) ||
            (errorCode == SpeechRecognizer.ERROR_NO_MATCH))
        {
            // TTS
            reminderPhraseCounter++;
            if (reminderPhraseCounter > 4)
            {
            sharedPref = this.getSharedPreferences(
                    getString(R.string.preference_file_key), Context.MODE_PRIVATE);
            //
            boolean hasLastPhraseNumber = sharedPref.contains(getString(R.string.preference_last_phrase_number));
            if (!hasLastPhraseNumber) {
                    // no sentences have been recorded yet
                    sharedLastPhraseNumber = 0;
                }
                else
                {
                    // phrases have already been recorded
                    sharedLastPhraseNumber =
                    sharedPref.getInt (getString(R.string.preference_last_phrase_number), 1);
                }
            String answerToLastPieceOfTheSentence =
                    GrammarHelper.lookForTheAnswerToLastPieceOfTheSentence(sharedLastPhraseNumber, realm);
            //
            String sharedLastPlayer =
                    sharedPref.getString (getString(R.string.preference_LastPlayer), "DEFAULT");
            if (!answerToLastPieceOfTheSentence.equals(" "))   {
                toSpeak = sharedLastPlayer + " " + answerToLastPieceOfTheSentence;
                tTS1=new TextToSpeech(this, new TextToSpeech.OnInitListener() {
                        @Override
                        public void onInit(int status) {
                            if(status != TextToSpeech.ERROR) {
                                // tTS1.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);
                                tTS1.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null, "prova tts");
                            }
                            else
                            {
                                Toast.makeText(getApplicationContext(), status,Toast.LENGTH_SHORT).show();
                            }
                        }
                        });
            }
            reminderPhraseCounter = 0;
            }
        }
        //
        fragmentTransactionStart();
    }
    /**
     * Called on beginning of speech.
     * replace with hear fragment
     * </p>
     * Refer to <a href="https://stackoverflow.com/questions/43520688/findfragmentbyid-and-findfragmentbytag">stackoverflow</a>
     * answer of <a href="https://stackoverflow.com/users/4266957/ricardo">Ricardo</a>
     *
     * @param eText string message from SpeechRecognizerManagement
     * @see com.example.voicerecognitionlibrary.RecognizerCallback
     * @see GameFragmentHear
     */
    @Override
    public void onBeginningOfSpeech(String eText) {
        GameFragmentHear frag= new GameFragmentHear();
        FragmentTransaction ft=getSupportFragmentManager().beginTransaction();
        Game2Fragment fragmentgotinstance =
                (Game2Fragment)
                        getSupportFragmentManager().findFragmentByTag(getString(R.string.game2_fragment));
        GameFragmentHear hearfragmentgotinstance =
                (GameFragmentHear)
                        getSupportFragmentManager().findFragmentByTag(getString(R.string.game_fragment_hear));
        if ((fragmentgotinstance != null) || (hearfragmentgotinstance != null))
        {
            ft.replace(R.id.game_container, frag, getString(R.string.game_fragment_hear));
            // ok, we got the fragment instance, but should we manipulate its view?
        }
        else
        {
            ft.add(R.id.game_container, frag, getString(R.string.game_fragment_hear));
        }
        ft.addToBackStack(null);
        ft.commit();
    }
    /**
     * Called on end of speech.
     * overrides the method on GameActivityAbstractClass
     *
     * @param editText string message from SpeechRecognizerManagement
     * @see GameActivityAbstractClass
     * @see com.example.voicerecognitionlibrary.RecognizerCallback
     */
    @Override
    public void onEndOfSpeech(String editText) {
//
    }
    /**
     * prepares the list of items to be registered on History
     *
     * @param realm realm obtained from the activity by Realm#getDefaultInstance
     * @param eText string to be registered on History
     * @return ToBeRecordedInHistory<VoiceToBeRecordedInHistory> list of items to be registered on History
     * @see VoiceToBeRecordedInHistory
     * @see ToBeRecordedInHistory
     * @see ImageSearchHelper#searchUri(Realm, String)
     * @see ImageSearchHelper#searchId(Realm, String)
     * @see GrammarHelper#searchType(String, Realm)
     * @see GrammarHelper#searchPlural
     */
    public ToBeRecordedInHistory<VoiceToBeRecordedInHistory> gettoBeRecordedInHistory(Realm realm, String eText)
    {
        // decomposes EditText
        String[] arrWords = GrammarHelper.splitString(eText);
        // initializes the list of items to be registered on History
        toBeRecordedInHistory= new ToBeRecordedInHistoryImpl();
        // adds the first entry to be recorded on History to the list
        SharedPreferences.Editor editor = sharedPref.edit();
        boolean hasLastPhraseNumber = sharedPref.contains(getString(R.string.preference_last_phrase_number));
        if (!hasLastPhraseNumber) {
            // is the first recorded sentence and LastPhraseNumber log on sharedpref
            // with the number 1
            sharedLastPhraseNumber = 1;
        }
        else
        {
            // it is not the first sentence recorded and I add 1 to LastPhraseNumber on sharedprefs
            sharedLastPhraseNumber =
                    sharedPref.getInt (getString(R.string.preference_last_phrase_number), 1)+1;
        }
        editor.putInt(getString(R.string.preference_last_phrase_number), sharedLastPhraseNumber);
        editor.apply();
        //
        currentTime = Calendar.getInstance().getTime();
        //
        voiceToBeRecordedInHistory =
                new VoiceToBeRecordedInHistory(sharedLastSession, sharedLastPhraseNumber, currentTime,
                        0, " ", eText, " "," ", " ");
        toBeRecordedInHistory.add(voiceToBeRecordedInHistory);
        // SEARCH THE IMAGES OF THE WORDS
        int arrWordsLength=arrWords.length;
        int i=0;
        while(i < arrWordsLength) {
            // INTERNAL MEMORY IMAGE SEARCH
            String uriToSearch = searchUri(realm, arrWords[i]);
            if (!uriToSearch.equals(getString(R.string.non_trovata))) {
                voiceToBeRecordedInHistory =
                        new VoiceToBeRecordedInHistory(sharedLastSession,
                                sharedLastPhraseNumber, currentTime,
                                i+1, " ", arrWords[i],  " ", "S", uriToSearch);
                toBeRecordedInHistory.add(voiceToBeRecordedInHistory);
                }
                else
                {
                // SEARCH VERBS WITH REALM
                String verbToSearch =
                        GrammarHelper.searchVerb(arrWords[i], realm);
                String idToSearch;
                if (!verbToSearch.equals(getString(R.string.non_trovato))) {
                        idToSearch = searchId(realm, verbToSearch);
                    }
                    else
                    {
                        idToSearch = searchId(realm, arrWords[i]);
                    };
                    // IMAGE SEARCH ON ARASAAC
                    if (!idToSearch.equals(getString(R.string.non_trovata))) {
                        //  SEARCH TYPE OF WORD
                        String typeToSearch = GrammarHelper.searchType(idToSearch, realm);
                        // SEARCH IF IT IS PLURAL
                        String pluralToSearch = GrammarHelper.searchPlural(idToSearch, arrWords[i], realm);
                        //
                        String url = REMOTE_ADDR_PICTOGRAM + idToSearch +"?download=false";
                        voiceToBeRecordedInHistory =
                                new VoiceToBeRecordedInHistory(sharedLastSession, sharedLastPhraseNumber,
                                        currentTime,
                                        i+1, typeToSearch,  arrWords[i], pluralToSearch, "A", url);
                        toBeRecordedInHistory.add(voiceToBeRecordedInHistory);
                    }
                }
            //
            i++;
            // Log.d("TAG" + ": ", "some on result Response : " );
        }
        return toBeRecordedInHistory;
    }
    //
}