package com.sampietro.NaiveAAC.activities.Game.Game2

import android.content.Intent
import android.os.Bundle
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.sampietro.NaiveAAC.R
import com.sampietro.NaiveAAC.activities.BaseAndAbstractClass.GameActivityAbstractClassWithRecognizerCallback
import com.sampietro.NaiveAAC.activities.Game.Game1.Game1ArrayList
import com.sampietro.NaiveAAC.activities.Game.GameADA.SettingsStoriesImprovementActivity
import com.sampietro.NaiveAAC.activities.Game.Utils.GameFragmentHear
import com.sampietro.NaiveAAC.activities.Game.Utils.GameHelper.historyAdd
import com.sampietro.NaiveAAC.activities.Grammar.GrammarHelper.lookForTheAnswerToLastPieceOfTheSentence
import com.sampietro.NaiveAAC.activities.Stories.Stories
import com.sampietro.NaiveAAC.activities.Stories.StoriesComparator
import com.sampietro.NaiveAAC.activities.Stories.StoriesHelper.renumberAStory
import com.sampietro.NaiveAAC.activities.VoiceRecognition.SpeechRecognizerManagement
import com.sampietro.NaiveAAC.activities.history.History
import com.sampietro.NaiveAAC.activities.history.ToBeRecordedInHistory
import com.sampietro.NaiveAAC.activities.history.VoiceToBeRecordedInHistory
import java.util.*

/**
 * <h1>SettingsStoriesRegistrationActivityAbstractClass</h1>
 *
 * **SettingsStoriesRegistrationActivity** displays images (uploaded by the user or Arasaac pictograms) of the words
 * spoken after pressing the listen button
 *
 *
 * @version     5.0, 01/04/2024
 * @see GameActivityAbstractClassWithRecognizerCallback
 * @see Game2ActivityAbstractClass
 */
abstract class SettingsStoriesRegistrationActivityAbstractClass : Game2ActivityAbstractClass() {
    //
    var wordToAdd = ""
    //
    var phraseNumberToPutInTheBundle = 0

    /**
     * Called when the user taps the start speech button.
     *
     * @param v view of tapped button
     * @see SpeechRecognizerManagement.startSpeech
     */
    open fun startSpeechSettingsStoriesQuickRegistration(v: View?) {
        SpeechRecognizerManagement.startSpeech()
        //
        val frag = GameFragmentHear()
        val ft = supportFragmentManager.beginTransaction()
        val fragmentgotinstance =
            supportFragmentManager.findFragmentByTag(getString(R.string.game2_fragment)) as SettingsStoriesRegistrationFragment?
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
     * @see historyAdd
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
     * @see fragmentTransactionStart
     */
    fun deleteSentence(v: View?) {
        fragmentTransactionStart("")
    }
    /**
     * initiate Fragment transaction.
     *
     *
     *
     * @see SettingsStoriesRegistrationFragment
     */
    open fun fragmentTransactionStart(eText: String?) {
        val frag = SettingsStoriesRegistrationFragment()
        val bundle = Bundle()
        bundle.putInt(getString(R.string.last_phrase_number), sharedLastPhraseNumber)
        bundle.putString(getString(R.string.etext), eText)
        bundle.putString(getString(R.string.wordtoadd), wordToAdd)
        frag.arguments = bundle
        val ft = supportFragmentManager.beginTransaction()
        val fragmentgotinstance =
            supportFragmentManager.findFragmentByTag(getString(R.string.game2_fragment)) as SettingsStoriesRegistrationFragment?
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
     * @see historyAdd
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
     * Called when the user taps the preview button.
     *
     * @param v view of tapped button
     * @see ToBeRecordedInHistory
     *
     * @see VoiceToBeRecordedInHistory
     *
     * @see historyAdd
     *
     * @see fragmentTransactionStart
     */
    open fun saveStories(v: View?) {
        //
        val textWord1 = findViewById<View>(R.id.keywordstorytoadd) as TextView
        val textWord2 = findViewById<View>(R.id.phrasenumbertoadd) as TextView
        val textWord3 = findViewById<View>(R.id.sentencetoadd) as EditText
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
            // se textword2 = "" allora inserisco alla fine
            // se textword2 = numero inserisco prima della frase numero aumentando il numero della frase per quelle successive
            val resultsStories = realm.where(Stories::class.java)
                .equalTo(getString(R.string.story), textWord1.text.toString().lowercase(Locale.getDefault()))
                .findAll()
            val storiesSize = resultsStories.size
            //
            if (storiesSize == 0) {
                // registro nuova storia su GameParameters
                // Note that the realm object was generated with the createObject method
                // and not with the new operator.
                // The modification operations will be performed within a Transaction.
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
                phraseNumberToPutInTheBundle = phraseNumber
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
        //
        val editor = sharedPref.edit()
        editor.putInt(textWord1.text.toString().lowercase(Locale.getDefault()) + getString(R.string.preference_phrasetodisplayindex), phraseNumberToPutInTheBundle)
        editor.apply()
        /*
        navigate to settings stories improvement activity
        */
        val intent = Intent(context, SettingsStoriesImprovementActivity::class.java)
        intent.putExtra(getString(R.string.story), textWord1.text.toString().lowercase(Locale.getDefault()))
        intent.putExtra(getString(R.string.phrase_number), phraseNumberToPutInTheBundle)
        startActivity(intent)
    }
    /**
     * prepare data using data from the history table
     *
     * @return theimage arraylist<Game2ArrayList> data for recyclerview
     * @see Game1ArrayList
     *
     * @see History
     */
    fun prepareData1(): ArrayList<Game1ArrayList> {
        val results = realm.where(
            History::class.java
        ).equalTo(getString(R.string.phrase_number), sharedLastPhraseNumber.toString()).findAll()
        val count = results.size
        val theimage = ArrayList<Game1ArrayList>()
        if (count != 0) {
            var irrh = 0
            while (irrh < count) {
                val result = results[irrh]!!
                val wordNumber = Objects.requireNonNull(result.wordNumber)!!.toInt()
                //
                if (wordNumber != 0) {
                    val createList = Game1ArrayList()
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
}