package com.sampietro.NaiveAAC.activities.Game.Game1

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import com.sampietro.NaiveAAC.R
import android.widget.TextView
import androidx.annotation.LayoutRes
import com.sampietro.NaiveAAC.activities.BaseAndAbstractClass.GameFragmentAbstractClass
import com.sampietro.NaiveAAC.activities.Graphics.GraphicsAndPrintingHelper.addImage
import com.sampietro.NaiveAAC.activities.Graphics.ImageSearchHelper.imageSearch
import com.sampietro.NaiveAAC.activities.Graphics.ResponseImageSearch
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
class Game1FirstLevelFragment(@LayoutRes contentLayoutId : Int = 0) : GameFragmentAbstractClass(contentLayoutId) {
    var wordToSearchSecondLevelMenu: String? = null
    var leftArrow: String? = null
    var rightArrow: String? = null

    /**
     * prepares the ui
     *
     *
     * Refer to [raywenderlich.com](https://www.raywenderlich.com/8192680-viewpager2-in-android-getting-started)
     * By [Rajdeep Singh](https://www.raywenderlich.com/u/rajdeep1008)
     *
     * @see androidx.fragment.app.Fragment.onViewCreated
     *
     * @see ImageSearchHelper.imageSearch
     *
     * @see addImage
     */
    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
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
                view.findViewById<View>(R.id.titlefirstlevelmenu) as TextView
            textFirstLevelMenuView.text =
                wordToSearchSecondLevelMenu!!.uppercase(Locale.getDefault())
            // ricerca immagine
            val image: ResponseImageSearch?
            image = imageSearch(ctext, realm, wordToSearchSecondLevelMenu)
            val imageFirstLevelMenuView = view.findViewById<ImageView>(R.id.imagefirstlevelmenu)
            imageFirstLevelMenuView.contentDescription = wordToSearchSecondLevelMenu!!.uppercase(
                Locale.getDefault()
            )
            addImage(image!!.uriType, image.uriToSearch, imageFirstLevelMenuView, 200, 200)
            // arrows
            val leftArrowFirstLevelMenu =
                view.findViewById<ImageView>(R.id.leftarrowfirstlevelmenu)
            val rightArrowFirstLevelMenu =
                view.findViewById<ImageView>(R.id.rightarrowfirstlevelmenu)
            if (leftArrow == "N") leftArrowFirstLevelMenu.visibility = View.INVISIBLE
            if (rightArrow == "N") rightArrowFirstLevelMenu.visibility = View.INVISIBLE
        }
        //
//        listener.receiveResultGameFragment(rootView)
//        return rootView
    } //
}