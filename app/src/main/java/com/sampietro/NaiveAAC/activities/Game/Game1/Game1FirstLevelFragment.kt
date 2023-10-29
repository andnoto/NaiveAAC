package com.sampietro.NaiveAAC.activities.Game.Game1

import com.sampietro.NaiveAAC.activities.Game.Utils.GameFragmentAbstractClass
import android.view.LayoutInflater
import android.view.ViewGroup
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import com.sampietro.NaiveAAC.R
import android.widget.TextView
import com.sampietro.NaiveAAC.activities.Graphics.ResponseImageSearch
import com.sampietro.NaiveAAC.activities.Graphics.ImageSearchHelper
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
 * @version     4.0, 09/09/2023
 * @see GameFragmentAbstractClass
 *
 * @see Game1Activity
 */
class Game1FirstLevelFragment : GameFragmentAbstractClass() {
    var wordToSearchSecondLevelMenu: String? = null
    var leftArrow: String? = null
    var rightArrow: String? = null

    /**
     * prepares the ui and makes the callback to the activity
     *
     *
     * Refer to [raywenderlich.com](https://www.raywenderlich.com/8192680-viewpager2-in-android-getting-started)
     * By [Rajdeep Singh](https://www.raywenderlich.com/u/rajdeep1008)
     *
     * @see androidx.fragment.app.Fragment.onCreateView
     *
     * @see ImageSearchHelper.imageSearch
     *
     * @see addImage
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        rootView = inflater.inflate(R.layout.activity_game_1_viewpager_content, container, false)
        //
        val bundle = this.arguments
        if (bundle != null) {
            wordToSearchSecondLevelMenu =
                bundle.getString(getString(R.string.word_to_search_second_level_menu))
            leftArrow = bundle.getString("LEFT ARROW")
            rightArrow = bundle.getString("RIGHT ARROW")
            //
            val textFirstLevelMenuView =
                rootView.findViewById<View>(R.id.titlefirstlevelmenu) as TextView
            textFirstLevelMenuView.text =
                wordToSearchSecondLevelMenu!!.uppercase(Locale.getDefault())
            // ricerca immagine
            val image: ResponseImageSearch?
            image = ImageSearchHelper.imageSearch(realm, wordToSearchSecondLevelMenu)
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
        listener.receiveResultGameFragment(rootView)
        return rootView
    } //
}