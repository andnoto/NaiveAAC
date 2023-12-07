package com.sampietro.NaiveAAC.activities.Game.Utils

import android.widget.TextView
import android.content.SharedPreferences
import android.graphics.Bitmap
import com.squareup.picasso.Picasso.LoadedFrom
import android.graphics.drawable.Drawable
import android.speech.tts.TextToSpeech
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import android.app.Activity
import android.content.Context
import android.view.View
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.sampietro.NaiveAAC.R
import com.sampietro.NaiveAAC.activities.Graphics.GraphicsHelper
import com.squareup.picasso.Target
import io.realm.Realm
import java.io.File
import java.lang.Exception

/**
 * <h1>GameFragmentAbstractClass</h1>
 *
 * **GameFragmentAbstractClass**
 * abstract class containing common methods that is extended by game fragments
 *
 * 1) GameFragmentChoiseOfGameMediaPlayer
 * 2) Game1ViewPagerFirstLevelFragment
 * 3) Game1ViewPagerSecondLevelFragment
 * 4) GameFragmentGame2
 *
 * @version     4.0, 09/09/2023
 * @see com.sampietro.NaiveAAC.activities.Game.ChoiseOfGame.ChoiseOfGameFragment
 *
 * @see com.sampietro.NaiveAAC.activities.Game.Game1.Game1FirstLevelFragment
 *
 * @see com.sampietro.NaiveAAC.activities.Game.Game1.Game1SecondLevelFragment
 *
 * @see com.sampietro.NaiveAAC.activities.Game.Game2.Game2Fragment
 */
abstract class GameFragmentAbstractClass : Fragment() {
//    @JvmField
    lateinit var realm: Realm
//    @JvmField
    lateinit var rootView: View
    var textView: TextView? = null
//    @JvmField
    lateinit var ctext: Context
//    @JvmField
    lateinit var sharedPref: SharedPreferences
//    @JvmField
    lateinit var preference_PrintPermissions: String
    //
//    @JvmField
    lateinit var preference_TitleWritingType: String

    /**
     * used for printing
     */
    var bitmap1: Bitmap? = null

    /**
     * used for printing
     */
    var target1: Target = object : Target {
        override fun onBitmapLoaded(bitmap: Bitmap, from: LoadedFrom) {
            bitmap1 = bitmap
        }

        override fun onBitmapFailed(e: Exception, errorDrawable: Drawable) {}
        override fun onPrepareLoad(placeHolderDrawable: Drawable) {}
    }

    /**
     * used for TTS
     */
    @JvmField
    var tTS1: TextToSpeech? = null
    var toSpeak: String? = null

    //
//    @JvmField
    lateinit var listener: OnFragmentEventListenerGame
    //
    /**
     * override the default Back button behavior
     *
     * @see Fragment.onCreate
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // This callback will only be called when MyFragment is at least Started.
        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true /* enabled by default */) {
                override fun handleOnBackPressed() {
                    // Handle the back button event
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)

        // The callback can be enabled or disabled here or in handleOnBackPressed()
    }

    /**
     * listener setting for game activities callbacks , context annotation, realm get default instance
     * and get print permissions
     *
     * @see Fragment.onAttach
     */
    override fun onAttach(context: Context) {
        super.onAttach(context)
        val activity = context as Activity
        listener = activity as OnFragmentEventListenerGame
        //
        ctext = context
        // REALM
        realm = Realm.getDefaultInstance()
        // if is print permitted then preference_PrintPermissions = Y
        sharedPref = ctext.getSharedPreferences(
            getString(R.string.preference_file_key), Context.MODE_PRIVATE
        )
        preference_PrintPermissions =
            sharedPref.getString(getString(R.string.preference_print_permissions), getString(R.string.default_string)).toString()
        preference_TitleWritingType =
            sharedPref.getString(getString(R.string.preference_title_writing_type), getString(R.string.uppercase)).toString()
    }

    /**
     * TTS shutdown
     *
     * @see Fragment.onDestroy
     */
    override fun onDestroy() {
        if (tTS1 != null) {
            tTS1!!.stop()
            tTS1!!.shutdown()
        }
        //
        super.onDestroy()
    }

    /**
     * load an image in imageview from a url or from a file
     *
     * @param urlType if string equal to "A" the image is loaded from a url otherwise it is loaded from a file
     * @param url string with url or file path of origin
     * @param img target imageview
     * @param width int with the width of the target imageview
     * @param height int with the height of the target imageview
     * @see GraphicsHelper.addImageUsingPicasso
     */
    open fun addImage(urlType: String, url: String?, img: ImageView?, width: Int, height: Int) {
        if (urlType == "A") {
            GraphicsHelper.addImageUsingPicasso(url, img, width, height)
        } else {
            val f = File(url!!)
            GraphicsHelper.addFileImageUsingPicasso(f, img, width, height)
        }
    }
}