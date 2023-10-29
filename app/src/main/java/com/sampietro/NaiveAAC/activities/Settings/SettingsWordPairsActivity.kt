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
import com.sampietro.NaiveAAC.activities.Graphics.ResponseImageSearch
import com.sampietro.NaiveAAC.activities.WordPairs.WordPairs
import com.sampietro.NaiveAAC.activities.Graphics.Videos
import io.realm.Realm

/**
 * <h1>SettingsWordPairsActivity</h1>
 *
 * **SettingsWordPairsActivity** word pairs settings.
 * Called when the user taps the word pairs button from the contents settings menu
 *
 * @version     4.0, 09/09/2023
 * @see AccountActivityAbstractClass
 *
 * @see com.sampietro.NaiveAAC.activities.Settings.Utils.SettingsFragmentAbstractClass
 *
 * @see com.sampietro.NaiveAAC.activities.WordPairs.WordPairsAdapter
 */
class SettingsWordPairsActivity : AccountActivityAbstractClass(), WordPairsAdapterInterface,
    onFragmentEventListenerSettings {
    //
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
     * @see com.sampietro.NaiveAAC.activities.Settings.Utils.SettingsFragmentAbstractClass
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
        val textWord4 = findViewById<View>(R.id.ismenuitem) as EditText
        val textWord5 = findViewById<View>(R.id.awardtype) as EditText
        val textWord6 = findViewById<View>(R.id.uripremiumvideo) as TextView
        val textWord7 = findViewById<View>(R.id.linkyoutube) as EditText
        // check if the images of word1 and word 2 exist
        val image1: ResponseImageSearch?
        val image2: ResponseImageSearch?
        image1 = imageSearch(realm, textWord1.text.toString())
        image2 = imageSearch(realm, textWord2.text.toString())
        if (image1 != null && image2 != null) {
            // Note that the realm object was generated with the createObject method
            // and not with the new operator.
            // The modification operations will be performed within a Transaction.
            realm.beginTransaction()
            val wordPairs = realm.createObject(WordPairs::class.java)
            // set the fields here
            wordPairs.word1 = textWord1.text.toString()
            wordPairs.word2 = textWord2.text.toString()
            wordPairs.complement = textWord3.text.toString()
            wordPairs.isMenuItem = textWord4.text.toString()
            wordPairs.awardType = textWord5.text.toString()
            // if textword5 = V and textword6 = no video -> uripremiumvideo = prize otherwise see below
            if (textWord5.text.toString() == getString(R.string.character_v) && textWord6.text.toString() == getString(
                    R.string.nessun_video
                )
            ) {
                wordPairs.uriPremiumVideo = getString(R.string.prize)
            } else {
                // if textword5 = Y -> uripremiumvideo = linkyoutube otherwise see below
                if (textWord5.text.toString() == getString(R.string.character_y)) {
                    wordPairs.uriPremiumVideo = textWord7.text.toString()
                } else {
                    wordPairs.uriPremiumVideo = textWord6.text.toString()
                }
            }
            wordPairs.fromAssets = ""
            realm.commitTransaction()
            // if textword5 = V and textword6 different from no video record video on realm
            if (textWord5.text.toString() == "V" && textWord6.text.toString() != getString(R.string.nessun_video)) {
                // Note that the realm object was generated with the createObject method
                // and not with the new operator.
                // The modification operations will be performed within a Transaction.
                realm.beginTransaction()
                val videos = realm.createObject(Videos::class.java)
                videos.descrizione = textWord6.text.toString()
                videos.uri = filePath
                realm.commitTransaction()
            }
            //
        }
        // view the word pairs settings fragment initializing WordPairsFragment (FragmentTransaction
        // switch between Fragments).
        val frag = WordPairsFragment()
        val ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.settings_container, frag)
        ft.addToBackStack(null)
        ft.commit()
    }

    /**
     * on callback from WordPairsAdapter to this Activity
     *
     * after deleting a word pairs the activity is notified to view the word pairs settings
     *
     *
     * @see com.sampietro.NaiveAAC.activities.WordPairs.WordPairsAdapter
     *
     * @see WordPairsFragment
     */
    override fun reloadWordPairsFragment() {
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