package com.sampietro.NaiveAAC.activities.Game.Game2

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import com.sampietro.NaiveAAC.R
import com.sampietro.NaiveAAC.activities.BaseAndAbstractClass.GameActivityAbstractClassWithRecognizerCallback
import com.sampietro.NaiveAAC.activities.Game.GameADA.SettingsStoriesImprovementActivity
import com.sampietro.NaiveAAC.activities.Game.Utils.ActionbarFragment
import com.sampietro.NaiveAAC.activities.Settings.SettingsStoriesActivity
import com.sampietro.NaiveAAC.activities.Stories.VoiceToBeRecordedInStories
import com.sampietro.NaiveAAC.activities.Stories.VoiceToBeRecordedInStoriesViewModel
import com.sampietro.NaiveAAC.activities.VoiceRecognition.AndroidPermission
import com.sampietro.NaiveAAC.activities.VoiceRecognition.SpeechRecognizerManagement
import io.realm.Realm
import java.util.Locale

/**
 * <h1>SettingsStoriesRegistrationActivity</h1>
 *
 * **SettingsStoriesRegistrationActivity** displays images (uploaded by the user or Arasaac pictograms) of the words
 * spoken after pressing the listen button
 *
 *
 * @version     5.0, 01/04/2024
 * @see GameActivityAbstractClassWithRecognizerCallback
 * @see Game2ActivityAbstractClass
 */
open class SettingsStoriesRegistrationActivity : SettingsStoriesRegistrationActivityAbstractClass() {
    //
    private var voiceToBeRecordedInStories: VoiceToBeRecordedInStories? = null
    private lateinit var viewModel: VoiceToBeRecordedInStoriesViewModel
    //
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
        /*
        Both your fragment and its host activity can retrieve a shared instance of a ViewModel with activity scope by passing the activity into the ViewModelProvider
        constructor.
        The ViewModelProvider handles instantiating the ViewModel or retrieving it if it already exists. Both components can observe and modify this data
        */
        // In the Activity#onCreate make the only setItem
        voiceToBeRecordedInStories = VoiceToBeRecordedInStories()
        viewModel = ViewModelProvider(this).get(
            VoiceToBeRecordedInStoriesViewModel::class.java
        )
        viewModel.setItem(voiceToBeRecordedInStories!!)
//        voiceToBeRecordedInStories!!.story = "Image exchange via Bluetooth"
//        voiceToBeRecordedInStories!!.phraseNumberInt = 1
        clearFieldsOfViewmodelDataClass()
        // SEARCH STORY TO RECORD
        val intent = intent
        if (intent.hasExtra(getString(R.string.story))) {
//            keywordStoryToAdd = intent.getStringExtra(getString(R.string.story))!!
            voiceToBeRecordedInStories!!.story = intent.getStringExtra(getString(R.string.story))!!
        }
//        phraseNumberToAdd = intent.getIntExtra(getString(R.string.phrase_number), 1).toString()
        voiceToBeRecordedInStories!!.phraseNumberInt = intent.getIntExtra(getString(R.string.phrase_number), 1)
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
        // Check whether we're recreating a previously destroyed instance
        if (savedInstanceState != null) {
            // The onSaveInstanceState method is called before an activity may be killed
            // (for Screen Rotation Handling) so that
            // when it comes back it can restore its state.
            voiceToBeRecordedInStories!!.story = savedInstanceState.getString(getString(R.string.story))
            voiceToBeRecordedInStories!!.phraseNumberInt = savedInstanceState.getInt(getString(R.string.phrase_number))
        } else {
            fragmentTransactionStart("")
        }
    }
    /**
     * destroy SpeechRecognizer, TTS shutdown
     *
     * @see android.app.Activity.onDestroy
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
     * This method is called before an activity may be killed so that when it comes back some time in the future it can restore its state..
     *
     *
     * it stores the last phrase number
     *
     * @param savedInstanceState Define potentially saved parameters due to configurations changes.
     */
    public override fun onSaveInstanceState(savedInstanceState: Bundle) {
        //
        savedInstanceState.putString(getString(R.string.story), voiceToBeRecordedInStories!!.story)
        savedInstanceState.putInt(getString(R.string.phrase_number), voiceToBeRecordedInStories!!.phraseNumberInt)
        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState)
    }
    /**
     * Called when the user taps the back button.
     *
     * @param v view of tapped button
     * @see fragmentTransactionStart
     */
    fun onClickGoBackFromRegistration(v: View?) {
        val textWord1 = findViewById<View>(R.id.keywordstorytoadd) as TextView
        val textWord2 = findViewById<View>(R.id.phrasenumbertoadd) as TextView
        if (textWord2.text.toString().toInt() == 1)
            {
            /*
                    navigate to settings stories screen
            */
            val intent = Intent(context, SettingsStoriesActivity::class.java)
            startActivity(intent)
            }
            else
            {
            /*
            navigate to settings stories improvement activity
            */
            val intent = Intent(context, SettingsStoriesImprovementActivity::class.java)
            intent.putExtra(getString(R.string.story), textWord1.text.toString().lowercase(Locale.getDefault()))
            startActivity(intent)
            }
    }
    /**
     * clear fields of viewmodel data class
     *
     *
     * @see VoiceToBeRecordedInStories
     */
    fun clearFieldsOfViewmodelDataClass() {
        voiceToBeRecordedInStories!!.wordNumberInt = 0
        voiceToBeRecordedInStories!!.word = ""
        voiceToBeRecordedInStories!!.uriType = ""
        voiceToBeRecordedInStories!!.uri = ""
        voiceToBeRecordedInStories!!.answerActionType = ""
        voiceToBeRecordedInStories!!.answerAction = ""
        voiceToBeRecordedInStories!!.video = ""
        voiceToBeRecordedInStories!!.sound = ""
        voiceToBeRecordedInStories!!.soundReplacesTTS = ""
    }
}