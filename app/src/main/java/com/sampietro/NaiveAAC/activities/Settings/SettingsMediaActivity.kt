package com.sampietro.NaiveAAC.activities.Settings

import com.sampietro.NaiveAAC.activities.Settings.Utils.AccountActivityAbstractClass
import com.sampietro.NaiveAAC.activities.Graphics.ImagesAdapter.ImagesAdapterInterface
import com.sampietro.NaiveAAC.activities.Graphics.VideosAdapter.VideosAdapterInterface
import com.sampietro.NaiveAAC.activities.Graphics.SoundsAdapter.SoundsAdapterInterface
import com.sampietro.NaiveAAC.activities.Settings.Utils.SettingsFragmentAbstractClass.onFragmentEventListenerSettings
import android.widget.TextView
import android.os.Bundle
import com.sampietro.NaiveAAC.R
import com.sampietro.NaiveAAC.activities.Game.Utils.ActionbarFragment
import android.content.Intent
import android.widget.EditText
import com.sampietro.NaiveAAC.activities.Graphics.Videos
import android.media.MediaPlayer
import android.net.Uri
import android.view.View
import androidx.fragment.app.FragmentManager
import com.sampietro.NaiveAAC.activities.Graphics.Images
import com.sampietro.NaiveAAC.activities.Graphics.Sounds
import io.realm.Realm
import java.lang.Exception
import java.net.URISyntaxException

/**
 * <h1>SettingsMediaActivity</h1>
 *
 * **SettingsMediaActivity** app media settings.
 *
 * @version     4.0, 09/09/2023
 * @see AccountActivityAbstractClass
 *
 * @see com.sampietro.NaiveAAC.activities.Settings.Utils.SettingsFragmentAbstractClass
 *
 * @see com.sampietro.NaiveAAC.activities.Graphics.ImagesAdapter
 *
 * @see com.sampietro.NaiveAAC.activities.Graphics.VideosAdapter
 *
 * @see com.sampietro.NaiveAAC.activities.Graphics.SoundsAdapter
 */
class SettingsMediaActivity : AccountActivityAbstractClass(), ImagesAdapterInterface,
    VideosAdapterInterface, SoundsAdapterInterface, onFragmentEventListenerSettings {
    var message = "messaggio non formato"
    var textView: TextView? = null

    //
    var fragmentManager: FragmentManager? = null

    /**
     * configurations of settings start screen.
     *
     * @param savedInstanceState Define potentially saved parameters due to configurations changes.
     * @see setActivityResultLauncher
     *
     * @see ActionbarFragment
     *
     * @see VerifyFragment
     *
     * @see androidx.fragment.app.Fragment.onCreate
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
                .add(
                    R.id.settings_container,
                    ChoiseOfMediaToSetFragment(),
                    "ChoiseOfMediaToSetFragment"
                )
                .commit()
        }
        // The MainActivity class provides an instance of Realm wherever needed in the application.
        // To use it in the Activity, a reference must be obtained to a Realm object in the onCreate method.
        // The Realm object must be closed in the onDestroy method.
        realm = Realm.getDefaultInstance()
    }
    //
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
     * Called when the user taps the video button from the media settings menu.
     *
     * the activity is notified to view the videos settings.
     *
     *
     * @param view view of tapped button
     * @see ChoiseOfMediaToSetFragment
     *
     * @see VideosFragment
     */
    fun submitVideo(view: View?) {
        // view the videos settings fragment initializing VideosFragment (FragmentTransaction
        // switch between Fragments).
        val frag = VideosFragment()
        val bundle = Bundle()
        bundle.putString(getString(R.string.uri), getString(R.string.none))
        frag.arguments = bundle
        val ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.settings_container, frag)
        ft.addToBackStack(null)
        ft.commit()
    }

    /**
     * Called when the user taps the search video button from the video settings menu.
     *
     *
     * @param v view of tapped button
     * @see VideosFragment
     *
     * @see setActivityResultLauncher
     */
    fun videoSearch(v: View?) {
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
        videoSearchActivityResultLauncher!!.launch(intent)
    }

    /**
     * Called when the user taps the add video button from the video settings.
     *
     * after the checks it adds the references of the video on realm
     *
     * and the activity is notified to view the updated video settings.
     *
     * @param v view of tapped button
     * @see VideosFragment
     *
     * @see Videos
     */
    fun videoAdd(v: View?) {
        realm = Realm.getDefaultInstance()
        //
        val vidD = findViewById<View>(R.id.videoDescription) as EditText
        if (vidD.length() > 0 && stringUri != null) {
            uri = Uri.parse(stringUri)
            try {
                filePath = getFilePath(this, uri)
            } catch (e: URISyntaxException) {
                e.printStackTrace()
            }
            // Note that the realm object was generated with the createObject method
            // and not with the new operator.
            // The modification operations will be performed within a Transaction.
            realm.beginTransaction()
            val videos = realm.createObject(Videos::class.java)
            videos.descrizione = vidD.text.toString()
            videos.uri = filePath
            videos.fromAssets = ""
            realm.commitTransaction()
            // view the videos settings fragment initializing VideosFragment (FragmentTransaction
            // switch between Fragments).
            val frag = VideosFragment()
            val bundle = Bundle()
            bundle.putString(getString(R.string.uri), getString(R.string.none))
            frag.arguments = bundle
            val ft = supportFragmentManager.beginTransaction()
            ft.replace(R.id.settings_container, frag)
            ft.addToBackStack(null)
            ft.commit()
        }
    }

    /**
     * on callback from VideosAdapter to this Activity
     *
     * after deleting references to a video the activity is notified to view the updated video settings
     *
     *
     * @see com.sampietro.NaiveAAC.activities.Graphics.VideosAdapter
     *
     * @see VideosFragment
     */
    override fun reloadVideosFragment() {
        // view the videos settings fragment initializing VideosFragment (FragmentTransaction
        // switch between Fragments).
        val frag = VideosFragment()
        val bundle = Bundle()
        bundle.putString(getString(R.string.uri), getString(R.string.none))
        frag.arguments = bundle
        val ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.settings_container, frag)
        ft.addToBackStack(null)
        ft.commit()
    }

    /**
     * Called when the user taps the images button from the media settings menu.
     *
     * the activity is notified to view the images settings.
     *
     *
     * @param view view of tapped button
     * @see ChoiseOfMediaToSetFragment
     *
     * @see ImagesFragment
     */
    fun submitImages(view: View?) {
        // view the images settings fragment initializing ImagesFragment (FragmentTransaction
        // switch between Fragments).
        val frag = ImagesFragment()
        val ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.settings_container, frag)
        ft.addToBackStack(null)
        ft.commit()
    }

    /**
     * Called when the user taps the add image button from the image settings.
     *
     * after the checks it adds the references of the image on realm
     *
     * and the activity is notified to view the updated image settings.
     *
     * @param v view of tapped button
     * @see ImagesFragment
     *
     * @see Images
     */
    fun imageAdd(v: View?) {
        realm = Realm.getDefaultInstance()
        //
        val immD = findViewById<View>(R.id.imageDescription) as EditText
        if (immD.length() > 0 && filePath != getString(R.string.non_trovato)) {
            // Note that the realm object was generated with the createObject method
            // and not with the new operator.
            // The modification operations will be performed within a Transaction.
            realm.beginTransaction()
            val i = realm.createObject(
                Images::class.java
            )
            i.descrizione = immD.text.toString()
            i.uri = filePath
            i.fromAssets = ""
            realm.commitTransaction()
            // view the images settings fragment initializing ImagesFragment (FragmentTransaction
            // switch between Fragments).
            val frag = ImagesFragment()
            val ft = supportFragmentManager.beginTransaction()
            ft.replace(R.id.settings_container, frag)
            ft.addToBackStack(null)
            ft.commit()
        }
    }

    /**
     * on callback from ImagesAdapter to this Activity
     *
     * after deleting references to a image the activity is notified to view the updated image settings
     *
     *
     * @see com.sampietro.NaiveAAC.activities.Graphics.ImagesAdapter
     *
     * @see ImagesFragment
     */
    override fun reloadImagesFragment() {
        // view the images settings fragment initializing ImagesFragment (FragmentTransaction
        // switch between Fragments).
        val frag = ImagesFragment()
        val ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.settings_container, frag)
        ft.addToBackStack(null)
        ft.commit()
    }

    /**
     * Called when the user taps the sounds button from the media settings menu.
     *
     * the activity is notified to view the sounds settings.
     *
     *
     * @param view view of tapped button
     * @see ChoiseOfMediaToSetFragment
     *
     * @see SoundsFragment
     */
    fun submitSounds(view: View?) {
        // view the sounds settings fragment initializing SoundsFragment (FragmentTransaction
        // switch between Fragments).
        val frag = SoundsFragment()
        val ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.settings_container, frag)
        ft.addToBackStack(null)
        ft.commit()
    }

    /**
     * Called when the user taps the search sound button from the sound settings menu.
     *
     *
     * @param v view of tapped button
     * @see SoundsFragment
     *
     * @see setActivityResultLauncher
     */
    fun soundSearch(v: View?) {
        // sound search
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
        intent.type = "audio/*"
        soundSearchActivityResultLauncher!!.launch(intent)
    }

    /**
     * Called when the user taps the listen sound button from the sound settings menu.
     *
     * Refer to [stackoverflow](https://stackoverflow.com/questions/24104506/android-code-to-select-audio-mp3-and-play-it)
     * answer of [Ali Sherafat](https://stackoverflow.com/users/5160077/ali-sherafat)
     *
     * @param v view of tapped button
     * @see SoundsFragment
     */
    fun listenSoundButton(v: View?) {
        //
        try {
            // play audio file using MediaPlayer
            val mediaPlayer = MediaPlayer()
            mediaPlayer.setDataSource(filePath)
            mediaPlayer.prepare()
            mediaPlayer.start()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Called when the user taps the add sound button from the sounds settings.
     *
     * after the checks it adds the references of the sound on realm
     *
     * and the activity is notified to view the updated sound settings.
     *
     * @param v view of tapped button
     * @see SoundsFragment
     *
     * @see Sounds
     */
    fun soundAdd(v: View?) {
        realm = Realm.getDefaultInstance()
        //
        val immD = findViewById<View>(R.id.soundDescription) as EditText
        if (immD.length() > 0 && filePath != getString(R.string.non_trovato)) {
            // Note that the realm object was generated with the createObject method
            // and not with the new operator.
            // The modification operations will be performed within a Transaction.
            realm.beginTransaction()
            val s = realm.createObject(Sounds::class.java)
            s.descrizione = immD.text.toString()
            s.uri = filePath
            s.fromAssets = ""
            realm.commitTransaction()
            // view the sounds settings fragment initializing SoundsFragment (FragmentTransaction
            // switch between Fragments).
            val frag = SoundsFragment()
            val ft = supportFragmentManager.beginTransaction()
            ft.replace(R.id.settings_container, frag)
            ft.addToBackStack(null)
            ft.commit()
        }
    }

    /**
     * on callback from SoundsAdapter to this Activity
     *
     * after deleting references to a sound the activity is notified to view the updated sound settings
     *
     *
     * @see com.sampietro.NaiveAAC.activities.Graphics.SoundsAdapter
     *
     * @see SoundsFragment
     */
    override fun reloadSoundsFragment() {
        // view the images settings fragment initializing ImagesFragment (FragmentTransaction
        // switch between Fragments).
        val frag = SoundsFragment()
        val ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.settings_container, frag)
        ft.addToBackStack(null)
        ft.commit()
    }

    companion object {
        //
//        private const val TAGPERMISSION = "Permission"
    }
}