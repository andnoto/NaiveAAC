package com.sampietro.NaiveAAC.activities.Game.Game2;

import static com.sampietro.NaiveAAC.activities.Game.Utils.HistoryRegistrationHelper.historyAdd;
import static com.sampietro.NaiveAAC.activities.Graphics.ImageSearchHelper.searchId;
import static com.sampietro.NaiveAAC.activities.Graphics.ImageSearchHelper.searchUri;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.FragmentTransaction;

import com.sampietro.NaiveAAC.R;
import com.sampietro.NaiveAAC.activities.Game.GameParameters.GameParameters;
import com.sampietro.NaiveAAC.activities.Game.Utils.ActionbarFragment;
import com.sampietro.NaiveAAC.activities.Game.Utils.GameActivityAbstractClass;
import com.sampietro.NaiveAAC.activities.Game.Utils.GameFragmentHear;
import com.sampietro.NaiveAAC.activities.Grammar.GrammarHelper;
import com.sampietro.NaiveAAC.activities.Graphics.ImageSearchHelper;
import com.sampietro.NaiveAAC.activities.Phrases.Phrases;
import com.sampietro.NaiveAAC.activities.Stories.Stories;
import com.sampietro.NaiveAAC.activities.Stories.StoriesComparator;
import com.sampietro.NaiveAAC.activities.history.History;
import com.sampietro.NaiveAAC.activities.history.ToBeRecordedInHistory;
import com.sampietro.NaiveAAC.activities.history.ToBeRecordedInHistoryImpl;
import com.sampietro.NaiveAAC.activities.history.VoiceToBeRecordedInHistory;
import com.example.voicerecognitionlibrary.AndroidPermission;
import com.example.voicerecognitionlibrary.SpeechRecognizerManagement;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * <h1>SettingsStoriesQuickRegistrationActivity</h1>
 * <p><b>SettingsStoriesQuickRegistrationActivity</b> displays images (uploaded by the user or Arasaac pictograms) of the words
 * spoken after pressing the listen button
 * </p>
 *
 * @version     1.4, 19/05/22
 * @see GameActivityAbstractClass
 */
public class SettingsStoriesQuickRegistrationActivity extends GameActivityAbstractClass {
    // lines inserted to remedy the incorrect double onresults that occurs with android 11
    public String previouseText = "";
    // TTS
    public TextToSpeech tTS1;
    public String toSpeak;
    public Integer reminderPhraseCounter = 0;
    //
    private static final String REMOTE_ADDR_PICTOGRAM=
    "https://api.arasaac.org/api/pictograms/";
    //
    public String keywordStoryToAdd = "";
    public String phraseNumberToAdd = "";
    public String wordToAdd = "";
    /**
     * configurations of game start screen.
     * <p>
     *
     * @param savedInstanceState Define potentially saved parameters due to configurations changes.
     * @see SpeechRecognizerManagement#prepareSpeechRecognizer
     * @see ActionbarFragment
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
        fragmentTransactionStart("");
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
     * Called when the user taps the start speech button.
     *
     * @param v view of tapped button
     * @see SpeechRecognizerManagement#startSpeech()
     */
    public void startSpeechSettingsStoriesQuickRegistration(View v)
    {
        SpeechRecognizerManagement.startSpeech();
        //
        GameFragmentHear frag= new GameFragmentHear();
        FragmentTransaction ft=getSupportFragmentManager().beginTransaction();
        SettingsStoriesQuickRegistrationFragment fragmentgotinstance =
                (SettingsStoriesQuickRegistrationFragment)
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
     * Called when the user taps the preview button.
     *
     * @param v view of tapped button
     * @see ToBeRecordedInHistory
     * @see VoiceToBeRecordedInHistory
     * @see com.sampietro.NaiveAAC.activities.Game.Utils.HistoryRegistrationHelper#historyAdd
     * @see #fragmentTransactionStart
     */
    public void startPreview(View v) {
        //
        EditText textWord1=(EditText) rootViewImageFragment.findViewById(R.id.keywordstorytoadd);
        EditText textWord2=(EditText) rootViewImageFragment.findViewById(R.id.phrasenumbertoadd);
        keywordStoryToAdd = textWord1.getText().toString();
        phraseNumberToAdd = textWord2.getText().toString();
        //
        EditText sentenceToAdd = (EditText)findViewById(R.id.sentencetoadd);
        String eText = sentenceToAdd.getText().toString();
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
        fragmentTransactionStart(eText);
    }
    /**
     * Called when the user taps the delete sentence button.
     *
     * @param v view of tapped button
     * @see #fragmentTransactionStart
     */
    public void deleteSentence(View v) {
        fragmentTransactionStart("");
    }
    /**
     * initiate Fragment transaction.
     * <p>
     *
     * @see SettingsStoriesQuickRegistrationFragment
     */
    public void fragmentTransactionStart(String eText)
    {
        SettingsStoriesQuickRegistrationFragment frag= new SettingsStoriesQuickRegistrationFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(getString(R.string.last_phrase_number), sharedLastPhraseNumber);
        bundle.putString("eText", eText);
        bundle.putString("keywordStoryToAdd", keywordStoryToAdd);
        bundle.putString("phraseNumberToAdd", phraseNumberToAdd);
        bundle.putString("wordToAdd", wordToAdd);
        frag.setArguments(bundle);
        FragmentTransaction ft=getSupportFragmentManager().beginTransaction();
        SettingsStoriesQuickRegistrationFragment fragmentgotinstance =
                (SettingsStoriesQuickRegistrationFragment)
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
        ft.commitAllowingStateLoss();
    }
    /**
     * Called on result of speech.
     *
     * @param eText string result from SpeechRecognizerManagement
     * @see com.example.voicerecognitionlibrary.RecognizerCallback
     * @see ToBeRecordedInHistory
     * @see VoiceToBeRecordedInHistory
     * @see com.sampietro.NaiveAAC.activities.Game.Utils.HistoryRegistrationHelper#historyAdd
     * @see #fragmentTransactionStart
     */
    @Override
    public void onResult(String eText) {
        // lines inserted to remedy the incorrect double onresults that occurs with android 11
        if (!eText.equals(previouseText))
        {
            previouseText = eText;
            // end lines inserted to remedy the incorrect double onresults that occurs with android 11
            // convert uppercase letter to lowercase
            eText = eText.toLowerCase();
            //
            EditText textWord1=(EditText) rootViewImageFragment.findViewById(R.id.keywordstorytoadd);
            EditText textWord2=(EditText) rootViewImageFragment.findViewById(R.id.phrasenumbertoadd);
            keywordStoryToAdd = textWord1.getText().toString();
            phraseNumberToAdd = textWord2.getText().toString();
            //
            fragmentTransactionStart(eText);
            //
            reminderPhraseCounter = 0;
        }
}
    /**
     * Called on error from SpeechRecognizerManagement.
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
        EditText textWord1=(EditText) rootViewImageFragment.findViewById(R.id.keywordstorytoadd);
        EditText textWord2=(EditText) rootViewImageFragment.findViewById(R.id.phrasenumbertoadd);
        keywordStoryToAdd = textWord1.getText().toString();
        phraseNumberToAdd = textWord2.getText().toString();
        //
        fragmentTransactionStart("");
    }
    /**
     * Called on beginning of speech.
     * replace with hear fragment
     * </p>
     *
     * @param eText string message from SpeechRecognizerManagement
     * @see com.example.voicerecognitionlibrary.RecognizerCallback
     */
    @Override
    public void onBeginningOfSpeech(String eText) {

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
     * Called when the user taps the preview button.
     *
     * @param v view of tapped button
     * @see ToBeRecordedInHistory
     * @see VoiceToBeRecordedInHistory
     * @see com.sampietro.NaiveAAC.activities.Game.Utils.HistoryRegistrationHelper#historyAdd
     * @see #fragmentTransactionStart
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void saveStories(View v) {
        //
        EditText textWord1=(EditText) findViewById(R.id.keywordstorytoadd);
        EditText textWord2=(EditText) findViewById(R.id.phrasenumbertoadd);
        EditText textWord3=(EditText) findViewById(R.id.sentencetoadd);
        if ((textWord3 != null) )
        {
            // convert uppercase letter to lowercase
            String keywordstorytoadd = textWord3.getText().toString().toLowerCase();
            //
            toBeRecordedInHistory=gettoBeRecordedInHistory(realm, keywordstorytoadd);
            // REALM SESSION REGISTRATION
            List<VoiceToBeRecordedInHistory> voicesToBeRecordedInHistory =
                    toBeRecordedInHistory.getVoicesToBeRecordedInHistory();
            //
            int debugUrlNumber = toBeRecordedInHistory.getNumberOfVoicesToBeRecordedInHistory();
            //
            historyAdd(realm, debugUrlNumber, voicesToBeRecordedInHistory);
            //
        }
        // registrazione stories vedi 805-840 settingsactivity
        if ((textWord1 != null) && (textWord2 != null)
                && (textWord3 != null) )
        {
            // se textword2 = "" allora inserisco alla fine
            // se textword2 = numero inserisco prima della frase numero aumentando il numero della frase per quelle successive
            RealmResults<Stories> resultsStories =
                    realm.where(Stories.class)
                            .equalTo("story", textWord1.getText().toString().toLowerCase())
                            .findAll();
            int storiesSize = resultsStories.size();
            //
            if (storiesSize == 0) {
                // registro nuova storia su GameParameters
                // Note that the realm object was generated with the createObject method
                // and not with the new operator.
                // The modification operations will be performed within a Transaction.
                realm.beginTransaction();
                GameParameters gp = realm.createObject(GameParameters.class);
                gp.setGameName(textWord1.getText().toString().toLowerCase());
                gp.setGameActive("A");
                gp.setGameInfo(textWord1.getText().toString().toLowerCase());
                gp.setGameJavaClass("A/DA");
                gp.setGameParameter(textWord1.getText().toString().toLowerCase());
                gp.setGameIconType("AS");
                gp.setGameIconPath("images/racconto.png");
                realm.commitTransaction();
                // registro nuova riga per nuova storia
                ArrayList<Game2ArrayList> createLists1 = prepareData1();
                // Note that the realm object was generated with the createObject method
                // and not with the new operator.
                // The modification operations will be performed within a Transaction.
                realm.beginTransaction();
                Stories stories = realm.createObject(Stories.class);
                // set the fields here
                stories.setStory(textWord1.getText().toString().toLowerCase());
                stories.setPhraseNumber(1);
                stories.setWordNumber(0);
                stories.setWord(textWord3.getText().toString().toLowerCase());
                realm.commitTransaction();
                // scorre array list per registrare le singole parole
                int wordNumber = 0;
                for(Game2ArrayList currentGame2ArrayList : createLists1) {
                    // Do something with the value
                    realm.beginTransaction();
                    Stories storiesFromGame2ArrayList = realm.createObject(Stories.class);
                    // set the fields here
                    storiesFromGame2ArrayList.setStory(textWord1.getText().toString().toLowerCase());
                    storiesFromGame2ArrayList.setPhraseNumber(1);
                    wordNumber++;
                    storiesFromGame2ArrayList.setWordNumber(wordNumber);
                    storiesFromGame2ArrayList.setWord(currentGame2ArrayList.getImage_title());
                    storiesFromGame2ArrayList.setUriType(currentGame2ArrayList.getUrlType());
                    storiesFromGame2ArrayList.setUri(currentGame2ArrayList.getUrl());
                    realm.commitTransaction();
                }
            }
            else
            {
                int startPhraseNumber;
                if ((textWord2.getText().toString().equals("")))
                {
                    startPhraseNumber = 9999;
                }
                else
                {
                    startPhraseNumber = Integer.parseInt(textWord2.getText().toString());
                }
                // cancello storia vecchia
                // copio la storia vecchia fino a startPhraseNumber
                // inserisco la nuova frase
                // copio la storia vecchia sommando 1 a phrasenumber
                //
                // clear the table
                // cancello storia vecchia dopo averne salvato copia
                List<Stories> resultsStoriesList = realm.copyFromRealm(resultsStories);
                //
                Collections.sort(resultsStoriesList,new StoriesComparator());
                //
                realm.beginTransaction();
                resultsStories.deleteAllFromRealm();
                realm.commitTransaction();
                //
                // copio la storia vecchia fino a startPhraseNumber
                int phraseNumber=0;
                int irrh=0;
                while(irrh < storiesSize)   {
                    Stories resultStories = resultsStoriesList.get(irrh);
                    assert resultStories != null;
                    int currentPhraseNumber = resultStories.getPhraseNumber();
                    if (currentPhraseNumber < startPhraseNumber)
                    {
                        phraseNumber = currentPhraseNumber;
                        realm.beginTransaction();
                        realm.copyToRealm(resultStories);
                        realm.commitTransaction();
                    }
                    else
                    {
                         break;
                    }
                    irrh++;
                }
                // inserisco la nuova frase
                // registro nuova riga
                phraseNumber++;
                ArrayList<Game2ArrayList> createLists1 = prepareData1();
                // Note that the realm object was generated with the createObject method
                // and not with the new operator.
                // The modification operations will be performed within a Transaction.
                realm.beginTransaction();
                Stories stories = realm.createObject(Stories.class);
                // set the fields here
                stories.setStory(textWord1.getText().toString().toLowerCase());
                stories.setPhraseNumber(phraseNumber);
                stories.setWordNumber(0);
                stories.setWord(textWord3.getText().toString().toLowerCase());
                realm.commitTransaction();
                // scorre array list per registrare le singole parole
                int wordNumber = 0;
                for(Game2ArrayList currentGame2ArrayList : createLists1) {
                    // Do something with the value
                    realm.beginTransaction();
                    Stories storiesFromGame2ArrayList = realm.createObject(Stories.class);
                    // set the fields here
                    storiesFromGame2ArrayList.setStory(textWord1.getText().toString().toLowerCase());
                    storiesFromGame2ArrayList.setPhraseNumber(phraseNumber);
                    wordNumber++;
                    storiesFromGame2ArrayList.setWordNumber(wordNumber);
                    storiesFromGame2ArrayList.setWord(currentGame2ArrayList.getImage_title());
                    storiesFromGame2ArrayList.setUriType(currentGame2ArrayList.getUrlType());
                    storiesFromGame2ArrayList.setUri(currentGame2ArrayList.getUrl());
                    realm.commitTransaction();
                }
                // copio la storia vecchia sommando 1 a phrasenumber
                while(irrh < storiesSize)   {
                    Stories resultStories = resultsStoriesList.get(irrh);
                    assert resultStories != null;
                    int oldPhraseNumber = resultStories.getPhraseNumber();
                    resultStories.setPhraseNumber(++oldPhraseNumber);
                    realm.beginTransaction();
                    realm.copyToRealm(resultStories);
                    realm.commitTransaction();
                    irrh++;
                }
            }
        }
        //
        fragmentTransactionStart("");
        //
        reminderPhraseCounter = 0;
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
    /**
     * prepare data using data from the history table
     *
     * @return theimage arraylist<Game2ArrayList> data for recyclerview
     * @see Game2ArrayList
     * @see History
     */
    private ArrayList<Game2ArrayList> prepareData1(){
        RealmResults<History> results =
                realm.where(History.class).equalTo(getString(R.string.phrase_number), String.valueOf(sharedLastPhraseNumber)).findAll();
        int count = results.size();
        ArrayList<Game2ArrayList> theimage = new ArrayList<>();
        if (count != 0) {
            int irrh=0;
            while(irrh < count) {
                History result = results.get(irrh);
                assert result != null;
                int wordNumber = result.getWordNumber();
                //
                if (wordNumber != 0) {
                    Game2ArrayList createList = new Game2ArrayList();
                    createList.setImage_title(result.getWord());
                    createList.setUrlType(result.getUriType());
                    createList.setUrl(result.getUri());
                    theimage.add(createList);
                }
                irrh++;
            }
        }
        //
        return theimage;
    }
    //
}