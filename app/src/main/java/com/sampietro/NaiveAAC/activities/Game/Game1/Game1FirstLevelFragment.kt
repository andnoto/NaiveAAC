package com.sampietro.NaiveAAC.activities.Game.Game1

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import com.sampietro.NaiveAAC.R
import com.sampietro.NaiveAAC.activities.BaseAndAbstractClass.GameFragmentAbstractClass
import com.sampietro.NaiveAAC.activities.Graphics.GraphicsAndPrintingHelper.addImage
import com.sampietro.NaiveAAC.activities.Graphics.ImageSearchHelper.imageSearch
import com.sampietro.NaiveAAC.activities.Graphics.ResponseImageSearch
import io.realm.Realm
import java.util.*

/**
 * <h1>Game1FirstLevelFragment</h1>
 *
 * **Game1FirstLevelFragment** UI for game1
 * display first level menu
 * the search for words takes place via a two-level menu
 * the first level contains the main research classes such as games, food, family, animals
 * the second level contains the subclasses of the first level for example
 * for the game class subclasses ball, tablet, running, etc.
 *
 *
 * @version     5.0, 01/04/2024
 *
 * @see Game1Activity
 */
class Game1FirstLevelFragment() : Fragment() {
    lateinit var realm: Realm
    //
    var textView: TextView? = null
    //
    lateinit var ctext: Context
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
    //
    var wordToSearchSecondLevelMenu: String? = null
    var leftArrow: String? = null
    var rightArrow: String? = null

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
     * prepares the ui
     *
     *
     * Refer to [raywenderlich.com](https://www.raywenderlich.com/8192680-viewpager2-in-android-getting-started)
     * By [Rajdeep Singh](https://www.raywenderlich.com/u/rajdeep1008)
     *
     * @see androidx.fragment.app.Fragment.onViewCreated
     *
     * @see imageSearch
     *
     * @see addImage
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val rootView = inflater.inflate(R.layout.activity_game_1_viewpager_content, container, false)
//    override fun onViewCreated(
//        view: View,
//        savedInstanceState: Bundle?
//    ) {
//        rootView = inflater.inflate(R.layout.activity_game_1_viewpager_content, container, false)
        //
        val bundle = this.arguments
        if (bundle != null) {
            wordToSearchSecondLevelMenu =
                bundle.getString(getString(R.string.word_to_search_second_level_menu))
            leftArrow = bundle.getString(getString(R.string.left_arrow))
            rightArrow = bundle.getString(getString(R.string.right_arrow))
            //
            val textFirstLevelMenuView =
                rootView.findViewById<View>(R.id.titlefirstlevelmenu) as TextView
            textFirstLevelMenuView.text =
                wordToSearchSecondLevelMenu!!.uppercase(Locale.getDefault())
            // ricerca immagine
            val image: ResponseImageSearch?
            image = imageSearch(ctext, realm, wordToSearchSecondLevelMenu)
            val imageFirstLevelMenuView = rootView.findViewById<ImageView>(R.id.imagefirstlevelmenu)
            imageFirstLevelMenuView.contentDescription = wordToSearchSecondLevelMenu!!.uppercase(
                Locale.getDefault()
            )
            addImage(image!!.uriType, image.uriToSearch, imageFirstLevelMenuView, 200, 200)
            // arrows
            val leftArrowFirstLevelMenu =
                rootView.findViewById<ImageView>(R.id.leftarrowfirstlevelmenu)
            val rightArrowFirstLevelMenu =
                rootView.findViewById<ImageView>(R.id.rightarrowfirstlevelmenu)
            if (leftArrow == "N") leftArrowFirstLevelMenu.visibility = View.INVISIBLE
            if (rightArrow == "N") rightArrowFirstLevelMenu.visibility = View.INVISIBLE
        }
        //
//        listener.receiveResultGameFragment(rootView)
        return rootView
    } //
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