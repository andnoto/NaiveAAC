package com.sampietro.NaiveAAC.activities.BaseAndAbstractClass

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.view.View
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import com.sampietro.NaiveAAC.R
import io.realm.Realm

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
 * @version     5.0, 01/04/2024
 * @see com.sampietro.NaiveAAC.activities.Game.ChoiseOfGame.ChoiseOfGameFragment
 *
 * @see com.sampietro.NaiveAAC.activities.Game.Game1.Game1FirstLevelFragment
 *
 * @see com.sampietro.NaiveAAC.activities.Game.Game1.Game1SecondLevelFragment
 *
 * @see com.sampietro.NaiveAAC.activities.Game.Game2.Game2Fragment
 */
abstract class GameFragmentAbstractClass() : Fragment() {
    lateinit var realm: Realm
//
    var textView: TextView? = null
//
    lateinit var ctext: Context
    lateinit var rootView: View
//
    lateinit var sharedPref: SharedPreferences
//
    lateinit var preference_PrintPermissions: String
//
    lateinit var preference_TitleWritingType: String

    /**
     * used for TTS
     */
    @JvmField
    var tTS1: TextToSpeech? = null
    var toSpeak: String? = null

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

}