package com.sampietro.NaiveAAC.activities.Settings

import com.sampietro.NaiveAAC.activities.Graphics.ImageSearchHelper.imageSearch
import com.sampietro.NaiveAAC.activities.Settings.Utils.AccountActivityAbstractClass
import com.sampietro.NaiveAAC.activities.WordPairs.WordPairsAdapter.WordPairsAdapterInterface
import com.sampietro.NaiveAAC.activities.Settings.Utils.SettingsFragmentAbstractClass.onFragmentEventListenerSettings
import android.os.Bundle
import com.sampietro.NaiveAAC.R
import com.sampietro.NaiveAAC.activities.Game.Utils.ActionbarFragment
import android.content.Intent
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import com.sampietro.NaiveAAC.activities.Graphics.ResponseImageSearch
import com.sampietro.NaiveAAC.activities.WordPairs.WordPairs
import com.sampietro.NaiveAAC.activities.Graphics.Videos
import com.sampietro.NaiveAAC.activities.WordPairs.VoiceToBeRecordedInWordPairs
import com.sampietro.NaiveAAC.activities.WordPairs.VoiceToBeRecordedInWordPairsViewModel
import io.realm.Realm
import io.realm.RealmResults
import io.realm.Sort

/**
 * <h1>SettingsWordPairsActivity</h1>
 *
 * **SettingsWordPairsActivity** word pairs settings.
 * Called when the user taps the word pairs button from the contents settings menu
 *
 * @version     5.0, 01/04/2024
 * @see AccountActivityAbstractClass
 *
 * @see com.sampietro.simsimtest.activities.Settings.Utils.SettingsFragmentAbstractClass
 *
 * @see com.sampietro.simsimtest.activities.WordPairs.WordPairsAdapter
 */
class SettingsWordPairsActivity : AccountActivityAbstractClass(), WordPairsAdapterInterface,
    onFragmentEventListenerSettings {
    /*
    used for viewmodel
     */
    private var voiceToBeRecordedInWordPairs: VoiceToBeRecordedInWordPairs? = null
    private lateinit var viewModel: VoiceToBeRecordedInWordPairsViewModel
    /*

     */
    var fragmentManager: FragmentManager? = null

    /**
     * configurations of settings start screen.
     *
     * @param savedInstanceState Define potentially saved parameters due to configurations changes.
     * @see .setActivityResultLauncher
     *
     * @see ActionbarFragment
     *
     * @see WordPairsFragment
     *
     * @see android.app.Activity.onCreate
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        //
        context = this
        /*
        Both your fragment and its host activity can retrieve a shared instance of a ViewModel with activity scope by passing the activity into the ViewModelProvider
        constructor.
        The ViewModelProvider handles instantiating the ViewModel or retrieving it if it already exists. Both components can observe and modify this data
        */
        // In the Activity#onCreate make the only setItem
        voiceToBeRecordedInWordPairs = VoiceToBeRecordedInWordPairs()
        viewModel = ViewModelProvider(this).get(
            VoiceToBeRecordedInWordPairsViewModel::class.java
        )
        viewModel.setItem(voiceToBeRecordedInWordPairs!!)
        clearFieldsOfViewmodelDataClass()
        //
        setActivityResultLauncher()
        //
        if (savedInstanceState == null) {
            fragmentManager = supportFragmentManager
            fragmentManager!!.beginTransaction()
                .add(ActionbarFragment(), getString(R.string.actionbar_fragment))
                .add(R.id.settings_container, WordPairsFragment(), "WordPairsFragment")
                .commit()
        }
        // The MainActivity class provides an instance of Realm wherever needed in the application.
        // To use it in the Activity, a reference must be obtained to a Realm object in the onCreate method.
        // The Realm object must be closed in the onDestroy method.
        realm = Realm.getDefaultInstance()
    }

    /**
     * receives calls from fragment listeners.
     *
     * @param v view of calling fragment
     * @see com.sampietro.simsimtest.activities.Settings.Utils.SettingsFragmentAbstractClass
     */
    override fun receiveResultSettings(v: View?) {
        rootViewFragment = v
    }

    /**
     * Called when the user taps the search video button from the word pairs settings.
     *
     *
     * @param v view of tapped button
     * @see WordPairsFragment
     *
     * @see .setActivityResultLauncher
     */
    fun videoSearchWordPairs(v: View?) {
        // video search
        // ACTION_OPEN_DOCUMENT is the intent to choose a file via the system's file
        // browser.
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        // Filter to only show results that can be "opened", such as a
        // file (as opposed to a list of contacts or timezones)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        // Filter to show only images, using the image MIME data type.
        // If one wanted to search for ogg vorbis files, the type would be "audio/ogg".
        // To search for all documents available via installed storage providers,
        // it would be "*/*".
        intent.type = "video/*"
        videoSearchWordPairsActivityResultLauncher!!.launch(intent)
    }

    /**
     * Called when the user taps the show list button from the word pairs settings.
     *
     * the activity is notified to view the word pairs list.
     *
     *
     * @param view view of tapped button
     * @see WordPairsFragment
     *
     * @see WordPairsListFragment
     */
    fun wordsToMatchShowList(view: View?) {
        // view the word pairs list fragment initializing WordPairsListFragment (FragmentTransaction
        // switch between Fragments).
        val frag = WordPairsListFragment()
        val ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.settings_container, frag)
        ft.addToBackStack(null)
        ft.commit()
    }

    /**
     * Called when the user taps the save button from word pairs settings.
     *
     * after the checks it adds the word pair on realm
     *
     * and the activity is notified to view the word pairs settings.
     *
     * @param v view of tapped button
     * @see WordPairsFragment
     *
     * @see WordPairs
     */
    fun wordsToMatchSave(v: View?) {
        realm = Realm.getDefaultInstance()
        //
        val textWord1 = findViewById<View>(R.id.firstword) as EditText
        val textWord2 = findViewById<View>(R.id.secondword) as EditText
        val textWord3 = findViewById<View>(R.id.complement) as EditText
        val textWord4 = findViewById<View>(R.id.awardtype) as EditText
        val textWord5 = findViewById<View>(R.id.uripremiumvideo) as TextView
        val textWord6 = findViewById<View>(R.id.linkyoutube) as EditText
        // check if the images of word1 and word 2 exist
        val image1: ResponseImageSearch?
        val image2: ResponseImageSearch?
        image1 = imageSearch(context, realm, textWord1.text.toString())
        image2 = imageSearch(context, realm, textWord2.text.toString())
        if (image1 != null && image2 != null) {
            // cancello il vecchio item
            val resultsWordPairs = realm.where(WordPairs::class.java)
                .equalTo(
                    "word1",
                    textWord1.text.toString()
                )
                .equalTo("word2", textWord2.text.toString())
                .findAll()
            val resultsWordPairsSize = resultsWordPairs.size
            //
            if (resultsWordPairsSize > 0) {
                realm.beginTransaction()
                resultsWordPairs.deleteAllFromRealm()
                realm.commitTransaction()
            }
            // Note that the realm object was generated with the createObject method
            // and not with the new operator.
            // The modification operations will be performed within a Transaction.
            realm.beginTransaction()
            val wordPairs = realm.createObject(WordPairs::class.java)
            // set the fields here
            wordPairs.word1 = textWord1.text.toString()
            wordPairs.word2 = textWord2.text.toString()
            wordPairs.complement = textWord3.text.toString()
            wordPairs.awardType = textWord4.text.toString()
             if (textWord4.text.toString() == getString(R.string.character_v) && textWord5.text.toString() == getString(
                    R.string.nessun_video
                )
            ) {
                wordPairs.uriPremiumVideo = getString(R.string.prize)
            } else {
                // if textword4 = Y -> uripremiumvideo = linkyoutube otherwise see below
                if (textWord4.text.toString() == getString(R.string.character_y)) {
                    wordPairs.uriPremiumVideo = textWord6.text.toString()
                } else {
                    wordPairs.uriPremiumVideo = textWord5.text.toString()
                }
            }
            wordPairs.fromAssets = ""
            realm.commitTransaction()
            // if textword4 = V and textword5 different from no video record video on realm
            if (textWord4.text.toString() == "V" && textWord5.text.toString() != getString(R.string.nessun_video)) {
                // Note that the realm object was generated with the createObject method
                // and not with the new operator.
                // The modification operations will be performed within a Transaction.
                realm.beginTransaction()
                val videos = realm.createObject(Videos::class.java)
                videos.descrizione = textWord5.text.toString()
                videos.uri = filePath
                realm.commitTransaction()
            }
            //
        }
        //
        clearFieldsOfViewmodelDataClass()
        //
        reloadWordPairsFragment()
    }
    /**
     * clear fields of viewmodel data class
     *
     *
     * @see VoiceToBeRecordedInWordPairs
     */
    fun clearFieldsOfViewmodelDataClass() {
        voiceToBeRecordedInWordPairs!!.word1 = ""
        voiceToBeRecordedInWordPairs!!.word2 = ""
        voiceToBeRecordedInWordPairs!!.complement = ""
        voiceToBeRecordedInWordPairs!!.awardType = ""
        voiceToBeRecordedInWordPairs!!.uriPremiumVideo = ""
        voiceToBeRecordedInWordPairs!!.fromAssets = ""
    }
    override fun reloadWordPairsFragmentForDeletion(position: Int) {
        // delete
        realm = Realm.getDefaultInstance()
        var results = realm.where(WordPairs::class.java).findAll()
        val mStrings1 = arrayOf("word1", "word2")
        val mStrings2 = arrayOf(Sort.ASCENDING, Sort.ASCENDING)
        results = results.sort(mStrings1, mStrings2)
        //
        realm.beginTransaction()
        val daCancellare = results[position]
        daCancellare!!.deleteFromRealm()
        realm.commitTransaction()
        //
        wordsToMatchShowList(null)
        //
    }

    override fun reloadWordPairsFragmentForInsertion(position: Int) {
        // Viewmodel
        // In the activity, sometimes it is called observe, other times it is limited to performing set directly
        // (maybe it is not necessary to call observe)
        viewModel.getSelectedItem()
            .observe(this) { voiceToBeRecordedInWordPairs: VoiceToBeRecordedInWordPairs ->
                realm = Realm.getDefaultInstance()
                var results: RealmResults<WordPairs>
                results = realm.where(WordPairs::class.java).findAll()
                val mStrings1 = arrayOf("word1", "word2")
                val mStrings2 = arrayOf(Sort.ASCENDING, Sort.ASCENDING)
                results = results.sort(mStrings1, mStrings2)
                val daInserire = results[position]!!
                voiceToBeRecordedInWordPairs.word1 = daInserire.word1
                reloadWordPairsFragment()
            }
    }

    override fun reloadWordPairsFragmentForEditing(position: Int) {
        // Viewmodel
        // In the activity, sometimes it is called observe, other times it is limited to performing set directly
        // (maybe it is not necessary to call observe)
        viewModel.getSelectedItem()
            .observe(this) { voiceToBeRecordedInWordPairs: VoiceToBeRecordedInWordPairs ->
                realm = Realm.getDefaultInstance()
                var results: RealmResults<WordPairs>
                results = realm.where(WordPairs::class.java).findAll()
                val mStrings1 = arrayOf("word1", "word2")
                val mStrings2 = arrayOf(Sort.ASCENDING, Sort.ASCENDING)
                results = results.sort(mStrings1, mStrings2)
                val daModificare = results[position]!!
                voiceToBeRecordedInWordPairs.word1 = daModificare.word1
                voiceToBeRecordedInWordPairs.word2 = daModificare.word2
                voiceToBeRecordedInWordPairs.complement = daModificare.complement
                voiceToBeRecordedInWordPairs.awardType = daModificare.awardType
                voiceToBeRecordedInWordPairs.uriPremiumVideo = daModificare.uriPremiumVideo
                 voiceToBeRecordedInWordPairs.fromAssets = daModificare.fromAssets
                //
                reloadWordPairsFragment()
            }
    }
    /**
     * on callback from WordPairsAdapter to this Activity
     *
     * after deleting a word pairs the activity is notified to view the word pairs settings
     *
     *
     * @see com.sampietro.simsimtest.activities.WordPairs.WordPairsAdapter
     *
     * @see WordPairsFragment
     */
    fun reloadWordPairsFragment() {
        // view the word pairs settings fragment initializing WordPairsFragment (FragmentTransaction
        // switch between Fragments).
        val ft: FragmentTransaction
        val wordPairsFragment = WordPairsFragment()
        ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.settings_container, wordPairsFragment)
        ft.addToBackStack(null)
        ft.commit()
    } //
}