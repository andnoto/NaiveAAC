package com.sampietro.NaiveAAC.activities.Game.Game2

import com.sampietro.NaiveAAC.activities.Game.Utils.HistoryRegistrationHelper.historyAdd
import com.sampietro.NaiveAAC.activities.Stories.StoriesHelper.renumberAStory
import com.sampietro.NaiveAAC.activities.Game.Utils.GameActivityAbstractClass
import android.speech.tts.TextToSpeech
import android.os.Bundle
import com.sampietro.NaiveAAC.R
import com.sampietro.NaiveAAC.activities.Game.Utils.ActionbarFragment
import com.sampietro.NaiveAAC.activities.Game.Utils.GameFragmentHear
import android.widget.EditText
import com.sampietro.NaiveAAC.activities.history.VoiceToBeRecordedInHistory
import android.speech.SpeechRecognizer
import com.sampietro.NaiveAAC.activities.Grammar.GrammarHelper
import android.widget.Toast
import com.sampietro.NaiveAAC.activities.Stories.Stories
import com.sampietro.NaiveAAC.activities.Game.GameParameters.GameParameters
import com.sampietro.NaiveAAC.activities.Stories.StoriesComparator
import com.sampietro.NaiveAAC.activities.history.ToBeRecordedInHistory
import com.sampietro.NaiveAAC.activities.history.ToBeRecordedInHistoryImpl
import android.view.View
import com.sampietro.NaiveAAC.activities.Graphics.ImageSearchHelper
import com.sampietro.NaiveAAC.activities.VoiceRecognition.AndroidPermission
import com.sampietro.NaiveAAC.activities.VoiceRecognition.SpeechRecognizerManagement
import com.sampietro.NaiveAAC.activities.history.History
import io.realm.Realm
import java.util.*

/**
 * <h1>SettingsStoriesQuickRegistrationActivity</h1>
 *
 * **SettingsStoriesQuickRegistrationActivity** displays images (uploaded by the user or Arasaac pictograms) of the words
 * spoken after pressing the listen button
 *
 *
 * @version     4.0, 09/09/2023
 * @see GameActivityAbstractClass
 */
class SettingsStoriesQuickRegistrationActivity : GameActivityAbstractClass() {
    // lines inserted to remedy the incorrect double onresults that occurs with android 11
    var previouseText = ""

    // TTS
    var tTS1: TextToSpeech? = null
    var toSpeak: String? = null
    var reminderPhraseCounter = 0

    //
    var keywordStoryToAdd = ""
    var phraseNumberToAdd = ""
    var wordToAdd = ""

    /**
     * configurations of game start screen.
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
        AndroidPermission.checkPermission(this)
        //
        SpeechRecognizerManagement.prepareSpeechRecognizer(this)
        //
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .add(ActionbarFragment(), getString(R.string.actionbar_fragment)).commit()
        }
        //
        realm = Realm.getDefaultInstance()
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
        SpeechRecognizerManagement.destroyRecognizer()
        // TTS
        if (tTS1 != null) {
            tTS1!!.stop()
            tTS1!!.shutdown()
        }
    }

    /**
     * Called when the user taps the start speech button.
     *
     * @param v view of tapped button
     * @see SpeechRecognizerManagement.startSpeech
     */
    fun startSpeechSettingsStoriesQuickRegistration(v: View?) {
        SpeechRecognizerManagement.startSpeech()
        //
        val frag = GameFragmentHear()
        val ft = supportFragmentManager.beginTransaction()
        val fragmentgotinstance =
            supportFragmentManager.findFragmentByTag(getString(R.string.game2_fragment)) as SettingsStoriesQuickRegistrationFragment?
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
     * @see ToBeRecordedInHistory
     *
     * @see VoiceToBeRecordedInHistory
     *
     * @see com.sampietro.NaiveAAC.activities.Game.Utils.HistoryRegistrationHelper.historyAdd
     *
     * @see fragmentTransactionStart
     */
    fun startPreview(v: View?) {
        //
        val textWord1 =
            rootViewImageFragment!!.findViewById<View>(R.id.keywordstorytoadd) as EditText
        val textWord2 =
            rootViewImageFragment!!.findViewById<View>(R.id.phrasenumbertoadd) as EditText
        keywordStoryToAdd = textWord1.text.toString()
        phraseNumberToAdd = textWord2.text.toString()
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
     * @see SettingsStoriesQuickRegistrationFragment
     */
    fun fragmentTransactionStart(eText: String?) {
        val frag = SettingsStoriesQuickRegistrationFragment()
        val bundle = Bundle()
        bundle.putInt(getString(R.string.last_phrase_number), sharedLastPhraseNumber!!)
        bundle.putString("eText", eText)
        bundle.putString("keywordStoryToAdd", keywordStoryToAdd)
        bundle.putString("phraseNumberToAdd", phraseNumberToAdd)
        bundle.putString("wordToAdd", wordToAdd)
        frag.arguments = bundle
        val ft = supportFragmentManager.beginTransaction()
        val fragmentgotinstance =
            supportFragmentManager.findFragmentByTag(getString(R.string.game2_fragment)) as SettingsStoriesQuickRegistrationFragment?
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
     * @see ToBeRecordedInHistory
     *
     * @see VoiceToBeRecordedInHistory
     *
     * @see com.sampietro.NaiveAAC.activities.Game.Utils.HistoryRegistrationHelper.historyAdd
     *
     * @see fragmentTransactionStart
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
            val textWord1 =
                rootViewImageFragment!!.findViewById<View>(R.id.keywordstorytoadd) as EditText
            val textWord2 =
                rootViewImageFragment!!.findViewById<View>(R.id.phrasenumbertoadd) as EditText
            keywordStoryToAdd = textWord1.text.toString()
            phraseNumberToAdd = textWord2.text.toString()
            //
            fragmentTransactionStart(eText)
            //
            reminderPhraseCounter = 0
        }
    }

    /**
     * Called on error from SpeechRecognizerManagement.
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
        val textWord1 =
            rootViewImageFragment!!.findViewById<View>(R.id.keywordstorytoadd) as EditText
        val textWord2 =
            rootViewImageFragment!!.findViewById<View>(R.id.phrasenumbertoadd) as EditText
        keywordStoryToAdd = textWord1.text.toString()
        phraseNumberToAdd = textWord2.text.toString()
        //
        fragmentTransactionStart("")
    }

    /**
     * Called on beginning of speech.
     * replace with hear fragment
     *
     *
     * @param eText string message from SpeechRecognizerManagement
     * @see com.sampietro.NaiveAAC.activities.VoiceRecognition.RecognizerCallback
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
     * Called when the user taps the preview button.
     *
     * @param v view of tapped button
     * @see ToBeRecordedInHistory
     *
     * @see VoiceToBeRecordedInHistory
     *
     * @see com.sampietro.NaiveAAC.activities.Game.Utils.HistoryRegistrationHelper.historyAdd
     *
     * @see fragmentTransactionStart
     */
//    @RequiresApi(api = Build.VERSION_CODES.N)
    fun saveStories(v: View?) {
        //
        val textWord1 = findViewById<View>(R.id.keywordstorytoadd) as EditText
        val textWord2 = findViewById<View>(R.id.phrasenumbertoadd) as EditText
        val textWord3 = findViewById<View>(R.id.sentencetoadd) as EditText
//        if (textWord3 != null) {
            // convert uppercase letter to lowercase
            val keywordstorytoadd = textWord3.text.toString().lowercase(Locale.getDefault())
            //
            val toBeRecordedInHistory = gettoBeRecordedInHistory(realm, keywordstorytoadd)
            // REALM SESSION REGISTRATION
            val voicesToBeRecordedInHistory: MutableList<VoiceToBeRecordedInHistory?> =
                toBeRecordedInHistory.getListVoicesToBeRecordedInHistory()
            //
            val debugUrlNumber = toBeRecordedInHistory.getNumberOfVoicesToBeRecordedInHistory()
            //
            historyAdd(realm, debugUrlNumber, voicesToBeRecordedInHistory)
            //
//        }
        // registrazione stories vedi 805-840 settingsactivity
//        if (textWord1 != null && textWord2 != null
//            && textWord3 != null
//        ) {
            // se textword2 = "" allora inserisco alla fine
            // se textword2 = numero inserisco prima della frase numero aumentando il numero della frase per quelle successive
            val resultsStories = realm.where(Stories::class.java)
                .equalTo("story", textWord1.text.toString().lowercase(Locale.getDefault()))
                .findAll()
            val storiesSize = resultsStories.size
            //
            if (storiesSize == 0) {
                // registro nuova storia su GameParameters
                // Note that the realm object was generated with the createObject method
                // and not with the new operator.
                // The modification operations will be performed within a Transaction.
                realm.beginTransaction()
                val gp = realm.createObject(GameParameters::class.java)
                gp.gameName = textWord1.text.toString().lowercase(Locale.getDefault())
                gp.gameActive = "A"
                gp.gameInfo = textWord1.text.toString().lowercase(Locale.getDefault())
                gp.gameJavaClass = "A/DA"
                gp.gameParameter = textWord1.text.toString().lowercase(Locale.getDefault())
                gp.gameIconType = "AS"
                gp.gameIconPath = "images/racconto.png"
                realm.commitTransaction()
                // registro nuova riga per nuova storia
                val createLists1 = prepareData1()
                // Note that the realm object was generated with the createObject method
                // and not with the new operator.
                // The modification operations will be performed within a Transaction.
                realm.beginTransaction()
                val stories = realm.createObject(Stories::class.java)
                // set the fields here
                stories.story = textWord1.text.toString().lowercase(Locale.getDefault())
                stories.phraseNumberInt = 1
                stories.wordNumberInt = 0
                stories.word = textWord3.text.toString().lowercase(Locale.getDefault())
                realm.commitTransaction()
                // scorre array list per registrare le singole parole
                var wordNumber = 0
                for (currentGame2ArrayList in createLists1) {
                    // Do something with the value
                    realm.beginTransaction()
                    val storiesFromGame2ArrayList = realm.createObject(Stories::class.java)
                    // set the fields here
                    storiesFromGame2ArrayList.story =
                        textWord1.text.toString().lowercase(Locale.getDefault())
                    storiesFromGame2ArrayList.phraseNumberInt = 1
                    wordNumber++
                    storiesFromGame2ArrayList.wordNumberInt = wordNumber
                    storiesFromGame2ArrayList.word = currentGame2ArrayList.image_title
                    storiesFromGame2ArrayList.uriType = currentGame2ArrayList.urlType
                    storiesFromGame2ArrayList.uri = currentGame2ArrayList.url
                    realm.commitTransaction()
                }
            } else {
                val startPhraseNumber: Int
                startPhraseNumber = if (textWord2.text.toString() == "") {
                    9999
                } else {
                    textWord2.text.toString().toInt()
                }
                // cancello storia vecchia
                // copio la storia vecchia fino a startPhraseNumber
                // inserisco la nuova frase
                // copio la storia vecchia sommando 1 a phrasenumber
                //
                // clear the table
                // cancello storia vecchia dopo averne salvato copia
                val resultsStoriesList = realm.copyFromRealm(resultsStories)
                //
                Collections.sort(resultsStoriesList, StoriesComparator())
                //
                realm.beginTransaction()
                resultsStories.deleteAllFromRealm()
                realm.commitTransaction()
                //
                // copio la storia vecchia fino a startPhraseNumber
                var phraseNumber = 0
                var irrh = 0
                while (irrh < storiesSize) {
                    val resultStories = resultsStoriesList[irrh]!!
                    val currentPhraseNumber = resultStories.phraseNumberInt
                    if (currentPhraseNumber < startPhraseNumber) {
                        phraseNumber = currentPhraseNumber
                        realm.beginTransaction()
                        realm.copyToRealm(resultStories)
                        realm.commitTransaction()
                    } else {
                        break
                    }
                    irrh++
                }
                // inserisco la nuova frase
                // registro nuova riga
                phraseNumber++
                val createLists1 = prepareData1()
                // Note that the realm object was generated with the createObject method
                // and not with the new operator.
                // The modification operations will be performed within a Transaction.
                realm.beginTransaction()
                val stories = realm.createObject(Stories::class.java)
                // set the fields here
                stories.story = textWord1.text.toString().lowercase(Locale.getDefault())
                stories.phraseNumberInt = phraseNumber
                stories.wordNumberInt = 0
                stories.word = textWord3.text.toString().lowercase(Locale.getDefault())
                realm.commitTransaction()
                // scorre array list per registrare le singole parole
                var wordNumber = 0
                for (currentGame2ArrayList in createLists1) {
                    // Do something with the value
                    realm.beginTransaction()
                    val storiesFromGame2ArrayList = realm.createObject(Stories::class.java)
                    // set the fields here
                    storiesFromGame2ArrayList.story =
                        textWord1.text.toString().lowercase(Locale.getDefault())
                    storiesFromGame2ArrayList.phraseNumberInt = phraseNumber
                    wordNumber++
                    storiesFromGame2ArrayList.wordNumberInt = wordNumber
                    storiesFromGame2ArrayList.word = currentGame2ArrayList.image_title
                    storiesFromGame2ArrayList.uriType = currentGame2ArrayList.urlType
                    storiesFromGame2ArrayList.uri = currentGame2ArrayList.url
                    realm.commitTransaction()
                }
                // copio la storia vecchia sommando 1 a phrasenumber
                while (irrh < storiesSize) {
                    val resultStories = resultsStoriesList[irrh]!!
                    var oldPhraseNumber = resultStories.phraseNumberInt
                    resultStories.phraseNumberInt = ++oldPhraseNumber
                    realm.beginTransaction()
                    realm.copyToRealm(resultStories)
                    realm.commitTransaction()
                    irrh++
                }
            }
            renumberAStory(realm, textWord1.text.toString().lowercase(Locale.getDefault()))
//        }
        //
        fragmentTransactionStart("")
        //
        reminderPhraseCounter = 0
    }

    /**
     * prepares the list of items to be registered on History
     *
     * @param realm realm obtained from the activity by Realm#getDefaultInstance
     * @param eText string to be registered on History
     * @return ToBeRecordedInHistory<VoiceToBeRecordedInHistory> list of items to be registered on History
     * @see VoiceToBeRecordedInHistory
     *
     * @see ToBeRecordedInHistory
     *
     * @see ImageSearchHelper.searchUri
     * @see ImageSearchHelper.searchId
     * @see GrammarHelper.searchType
     * @see GrammarHelper.searchPlural
    </VoiceToBeRecordedInHistory> */
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
    }
    //
    /**
     * prepare data using data from the history table
     *
     * @return theimage arraylist<Game2ArrayList> data for recyclerview
     * @see Game2ArrayList
     *
     * @see History
    </Game2ArrayList> */
    private fun prepareData1(): ArrayList<Game2ArrayList> {
        val results = realm.where(
            History::class.java
        ).equalTo(getString(R.string.phrase_number), sharedLastPhraseNumber.toString()).findAll()
        val count = results.size
        val theimage = ArrayList<Game2ArrayList>()
        if (count != 0) {
            var irrh = 0
            while (irrh < count) {
                val result = results[irrh]!!
                val wordNumber = Objects.requireNonNull(result.wordNumber)!!.toInt()
                //
                if (wordNumber != 0) {
                    val createList = Game2ArrayList()
                    createList.image_title = result.word
                    createList.urlType = result.uriType
                    createList.url = result.uri
                    theimage.add(createList)
                }
                irrh++
            }
        }
        //
        return theimage
    } //

    companion object {
        //
        private const val REMOTE_ADDR_PICTOGRAM = "https://api.arasaac.org/api/pictograms/"
    }
}