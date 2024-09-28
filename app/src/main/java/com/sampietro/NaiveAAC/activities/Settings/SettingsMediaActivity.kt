package com.sampietro.NaiveAAC.activities.Settings

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.FragmentManager
import com.sampietro.NaiveAAC.R
import com.sampietro.NaiveAAC.activities.BaseAndAbstractClass.AccountActivityAbstractClass
import com.sampietro.NaiveAAC.activities.BaseAndAbstractClass.ActivityAbstractClass
import com.sampietro.NaiveAAC.activities.DataStorage.DataStorageHelper.copyFileFromSharedToInternalStorageAndGetPath
import com.sampietro.NaiveAAC.activities.Game.Utils.ActionbarFragment
import com.sampietro.NaiveAAC.activities.Grammar.ListsOfNames
import com.sampietro.NaiveAAC.activities.Graphics.GraphicsAndPrintingHelper.showImage
import com.sampietro.NaiveAAC.activities.Graphics.Images
import com.sampietro.NaiveAAC.activities.Graphics.ImagesAdapter.ImagesAdapterInterface
import com.sampietro.NaiveAAC.activities.Graphics.Sounds
import com.sampietro.NaiveAAC.activities.Graphics.SoundsAdapter.SoundsAdapterInterface
import com.sampietro.NaiveAAC.activities.Graphics.Videos
import com.sampietro.NaiveAAC.activities.Graphics.VideosAdapter.VideosAdapterInterface
import io.realm.Realm
import java.io.ByteArrayOutputStream
import java.net.URISyntaxException
import java.util.Objects

/**
 * <h1>SettingsMediaActivity</h1>
 *
 * **SettingsMediaActivity** app media settings.
 *
 * @version     5.0, 01/04/2024
 * @see AccountActivityAbstractClass
 *
 * @see com.sampietro.NaiveAAC.activities.Graphics.ImagesAdapter
 *
 * @see com.sampietro.NaiveAAC.activities.Graphics.VideosAdapter
 *
 * @see com.sampietro.NaiveAAC.activities.Graphics.SoundsAdapter
 */
class SettingsMediaActivity : ActivityAbstractClass(), ImagesAdapterInterface,
    VideosAdapterInterface, SoundsAdapterInterface {
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
        if (savedInstanceState != null) {
            // The onSaveInstanceState method is called before an activity may be killed
            // (for Screen Rotation Handling) so that
            // when it comes back it can restore its state.
            filePath = savedInstanceState.getString("IMAGE VIDEO OR SOUND FILE PATH")
        }
        else
        {
            filePath = getString(R.string.non_trovato)
//        if (savedInstanceState == null) {
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
    /**
     * This method is called before an activity may be killed so that when it comes back some time in the future it can restore its state..
     *
     *
     * it stores the limage video or sound file path
     *
     * @param savedInstanceState Define potentially saved parameters due to configurations changes.
     */
    public override fun onSaveInstanceState(savedInstanceState: Bundle) {
        //
        savedInstanceState.putString("IMAGE VIDEO OR SOUND FILE PATH", filePath)
        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState)
    }
    /**
     * Called when the user taps the video button from the media settings menu.
     *
     * the activity is notified to view the videos settings.
     *
     *
     * @param view view of tapped button
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
//        realm = Realm.getDefaultInstance()
        //
        val vidD = findViewById<View>(R.id.videoDescription) as EditText
//        if (vidD.length() > 0 && stringUri != null) {
        if (vidD.length() > 0 && filePath != getString(R.string.non_trovato)) {
//            uri = Uri.parse(stringUri)
//            try {
//                filePath = copyFileFromSharedToInternalStorageAndGetPath(this, uri!!)
//            } catch (e: URISyntaxException) {
//                e.printStackTrace()
//            }
                // cancello il vecchio item
                val results = realm.where(Videos::class.java)
                    .equalTo(
                        "descrizione",
                        vidD.text.toString()
                    )
                    .findAll()
                val resultsSize = results.size
                //
                if (resultsSize > 0) {
                    realm.beginTransaction()
                    results.deleteAllFromRealm()
                    realm.commitTransaction()
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
     * Called when the user taps the show list button .
     *
     * the activity is notified to view the videos list.
     *
     *
     * @param view view of tapped button
     * @see VideosFragment
     *
     * @see VideosListFragment
     */
    fun videosList(view: View?) {
        // view the videos list fragment initializing VideosListFragment (FragmentTransaction
        // switch between Fragments).
        val frag = VideosListFragment()
        val ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.settings_container, frag)
        ft.addToBackStack(null)
        ft.commit()
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
//        realm = Realm.getDefaultInstance()
        //
        val immD = findViewById<View>(R.id.imageDescription) as EditText
        if (immD.length() > 0 && filePath != getString(R.string.non_trovato)) {
            // cancello il vecchio item
            val results = realm.where(Images::class.java)
                .equalTo(
                    "descrizione",
                    immD.text.toString()
                )
                .findAll()
            val resultsSize = results.size
            //
            if (resultsSize > 0) {
                realm.beginTransaction()
                results.deleteAllFromRealm()
                realm.commitTransaction()
            }
            // Note that the realm object was generated with the createObject method
            // and not with the new operator.
            // The modification operations will be performed within a Transaction.
            realm.beginTransaction()
            val i = realm.createObject(
                Images::class.java
            )
            i.descrizione = immD.text.toString()
            i.uri = filePath
            i.fromAssets = "N"
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
     * Called when the user taps the show list button .
     *
     * the activity is notified to view the images list.
     *
     *
     * @param view view of tapped button
     * @see ImagesFragment
     *
     * @see ImagesListFragment
     */
    fun imagesList(view: View?) {
        // view the images list fragment initializing ImagesListFragment (FragmentTransaction
        // switch between Fragments).
        val frag = ImagesListFragment()
        val ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.settings_container, frag)
        ft.addToBackStack(null)
        ft.commit()
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
//        realm = Realm.getDefaultInstance()
        //
        val souD = findViewById<View>(R.id.soundDescription) as EditText
        if (souD.length() > 0 && filePath != getString(R.string.non_trovato)) {
            // cancello il vecchio item
            val results = realm.where(Sounds::class.java)
                .equalTo(
                    "descrizione",
                    souD.text.toString()
                )
                .findAll()
            val resultsSize = results.size
            //
            if (resultsSize > 0) {
                realm.beginTransaction()
                results.deleteAllFromRealm()
                realm.commitTransaction()
            }
            // Note that the realm object was generated with the createObject method
            // and not with the new operator.
            // The modification operations will be performed within a Transaction.
            realm.beginTransaction()
            val s = realm.createObject(Sounds::class.java)
            s.descrizione = souD.text.toString()
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
     * Called when the user taps the show list button .
     *
     * the activity is notified to view the sounds list.
     *
     *
     * @param view view of tapped button
     * @see SoundsFragment
     *
     * @see SoundsListFragment
     */
    fun soundsList(view: View?) {
        // view the sounds list fragment initializing SoundsListFragment (FragmentTransaction
        // switch between Fragments).
        val frag = SoundsListFragment()
        val ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.settings_container, frag)
        ft.addToBackStack(null)
        ft.commit()
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
    // ActivityResultLauncher
    var imageSearchActivityResultLauncher: ActivityResultLauncher<Intent>? = null
    @JvmField
    var videoSearchActivityResultLauncher: ActivityResultLauncher<Intent>? = null
    @JvmField
    var soundSearchActivityResultLauncher: ActivityResultLauncher<Intent>? = null
//    @JvmField
//    var uri: Uri? = null
//    @JvmField
//    var stringUri: String? = null
    @JvmField
    var filePath: String? = null
//    var fileName: String? = null
//    lateinit var byteArray: ByteArray
    //
    /**
     * setting callbacks to search for images and videos via ACTION_OPEN_DOCUMENT which is
     * the intent to choose a file via the system's file browser
     *
     *
     * Refer to [stackoverflow](https://stackoverflow.com/questions/62671106/onactivityresult-method-is-deprecated-what-is-the-alternative)
     * answer of [Muntashir Akon](https://stackoverflow.com/users/4147849/muntashir-akon)
     * and
     * Refer to [stackoverflow](https://stackoverflow.com/questions/56651444/deprecated-getbitmap-with-api-29-any-alternative-codes)
     * answer of [Ally](https://stackoverflow.com/users/6258197/ally)
     *
     * @see copyFileFromSharedToInternalStorageAndGetPath
     *
     * @see showImage
     */
    fun setActivityResultLauncher() {
        // You can do the assignment inside onAttach or onCreate, i.e, before the activity is displayed
        imageSearchActivityResultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult(),
            object : ActivityResultCallback<ActivityResult?> {
                override fun onActivityResult(result: ActivityResult?) {
                    if (result!!.resultCode == RESULT_OK) {
                        // There are no request codes
                        val resultData = result.data
                        // doSomeOperations();
                        var uri: Uri? = null
                        var stringUri: String? = null
                        filePath = getString(R.string.non_trovato)
                        //
                        val immD = findViewById<View>(R.id.imageDescription) as EditText
                        //
                        if (resultData != null) {
                            uri = Objects.requireNonNull(resultData).data
                            //
                            try {
                                filePath = copyFileFromSharedToInternalStorageAndGetPath(context, uri!!)
                            } catch (e: URISyntaxException) {
                                e.printStackTrace()
                            }
                            //
                            val takeFlags =
                                resultData.flags and Intent.FLAG_GRANT_READ_URI_PERMISSION
                            context.contentResolver.takePersistableUriPermission(
                                uri!!,
                                takeFlags
                            )
                            //
                            stringUri = uri.toString()
                            //
//                            var bitmap: Bitmap? = null
                            //
//                            try {
//                                val source = ImageDecoder.createSource(
//                                    context.contentResolver,
//                                    uri
//                                )
//                                bitmap = ImageDecoder.decodeBitmap(source) { decoder, _, _ ->
//                                    decoder.setTargetSampleSize(1) // shrinking by
//                                    decoder.isMutableRequired = true // this resolve the hardware type of bitmap problem
//                                }
//                            } catch (e: Exception) {
//                                e.printStackTrace()
//                            }
                            //
//                            val stream = ByteArrayOutputStream()
//                            bitmap!!.compress(Bitmap.CompressFormat.PNG, 100, stream)
//                            byteArray = stream.toByteArray()
                            //
                            val frag = ImagesFragment()
                            val bundle = Bundle()
                            //
                            if (immD.length() > 0) {
                                bundle.putString(
                                    getString(R.string.descrizione),
                                    immD.text.toString()
                                )
                            } else {
                                bundle.putString(
                                    getString(R.string.descrizione),
                                    getString(R.string.nessuna)
                                )
                            }
                            //
                            bundle.putString(getString(R.string.uri), stringUri)
                            frag.arguments = bundle
                            val ft = supportFragmentManager.beginTransaction()
                            ft.replace(R.id.settings_container, frag)
                            ft.addToBackStack(null)
                            ft.commit()
                            //
//                            val myImage: ImageView
//                            myImage = findViewById<View>(R.id.imageviewTest) as ImageView
//                            showImage(context, uri, myImage)
                        }
                    }
                }
            })
        //
        videoSearchActivityResultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult(),
            object : ActivityResultCallback<ActivityResult?> {
                override fun onActivityResult(result: ActivityResult?) {
                    if (result!!.resultCode == RESULT_OK) {
                        // There are no request codes
                        val resultData = result.data
                        // doSomeOperations();
                        var uri: Uri? = null
                        var stringUri: String? = null
                        //
                        val vidD = findViewById<View>(R.id.videoDescription) as EditText
                        //
                        if (resultData != null) {
                            uri = Objects.requireNonNull(resultData).data
                            //
                            val takeFlags =
                                resultData.flags and Intent.FLAG_GRANT_READ_URI_PERMISSION
                            context.contentResolver.takePersistableUriPermission(
                                uri!!,
                                takeFlags
                            )
                            //
                            stringUri = uri.toString()
                            //
                            try {
                                filePath = copyFileFromSharedToInternalStorageAndGetPath(context, uri!!)
//                                assert(filePath != null)
//                                val cut = filePath!!.lastIndexOf('/')
//                                if (cut != -1) {
//                                    fileName = filePath!!.substring(cut + 1)
//                                }
                                //
                            } catch (e: URISyntaxException) {
                                e.printStackTrace()
                            }
                            //
                            val frag = VideosFragment()
                            val bundle = Bundle()
                            //
                            if (vidD.length() > 0) {
                                bundle.putString(
                                    getString(R.string.descrizione),
                                    vidD.text.toString()
                                )
                            } else {
                                bundle.putString(
                                    getString(R.string.descrizione),
                                    getString(R.string.nessuna)
                                )
                            }
                            //
                            bundle.putString(getString(R.string.uri), stringUri)
                            frag.arguments = bundle
                            val ft = supportFragmentManager.beginTransaction()
                            ft.replace(R.id.settings_container, frag)
                            ft.addToBackStack(null)
                            ft.commit()
                            //
                        }
                    }
                }
            })
        soundSearchActivityResultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult(),
            object : ActivityResultCallback<ActivityResult?> {
                override fun onActivityResult(result: ActivityResult?) {
                    if (result!!.resultCode == RESULT_OK) {
                        // There are no request codes
                        val resultData = result.data
                        // doSomeOperations();
                        var uri: Uri? = null
//                        stringUri = null
                        //
                        if (resultData != null) {
                            uri = Objects.requireNonNull(resultData).data
                            //
                            val takeFlags =
                                resultData.flags and Intent.FLAG_GRANT_READ_URI_PERMISSION
                            context.contentResolver.takePersistableUriPermission(
                                uri!!,
                                takeFlags
                            )
                            //
//                            stringUri = uri.toString()
                            //
                            try {
                                filePath = copyFileFromSharedToInternalStorageAndGetPath(context, uri!!)
//                                assert(filePath != null)
//                                val cut = filePath!!.lastIndexOf('/')
//                                if (cut != -1) {
//                                    fileName = filePath!!.substring(cut + 1)
//                                }
                                //
                            } catch (e: URISyntaxException) {
                                e.printStackTrace()
                            }
                        }
                    }
                }
            })
    }
    /**
     * Called when the user taps the image search button.
     *
     * @param v view of tapped button
     */
    fun imageSearch(v: View) {
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
        intent.type = "image/*"
        /*  the instructions of the button */
        imageSearchActivityResultLauncher!!.launch(intent)
    }
    companion object {
    }
}