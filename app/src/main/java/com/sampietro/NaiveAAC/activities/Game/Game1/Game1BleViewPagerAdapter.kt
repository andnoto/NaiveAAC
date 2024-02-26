package com.sampietro.NaiveAAC.activities.Game.Game1

import android.content.Context
import androidx.viewpager2.adapter.FragmentStateAdapter
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.sampietro.NaiveAAC.activities.WordPairs.WordPairs
import io.realm.Realm

/**
 * <h1>Game1BleViewPagerAdapter</h1>
 *
 * **Game1BleViewPagerAdapter** adapter for game1
 *
 * Refer to [raywenderlich.com](https://www.raywenderlich.com/8192680-viewpager2-in-android-getting-started)
 * By [Rajdeep Singh](https://www.raywenderlich.com/u/rajdeep1008)
 *
 * @version     4.0, 09/09/2023
 * @see FragmentStateAdapter
 */
class Game1BleViewPagerAdapter
/**
 * Game1ViewPagerAdapter constructor.
 * context annotation
 *
 * @param fragmentManager fragmentManager
 * @param context context
 * @param realm realm
 */(
    fragmentManager: Game1BleActivity,
    private val context: Context,
    private val realm: Realm
    ) : FragmentStateAdapter(fragmentManager) {
    /**
     * returns a fragment instance for the given position to display the first level menu
     * the search for words takes place via a two-level menu
     * the first level contains the main research classes such as games, food, family, animals
     * the second level contains the subclasses of the first level for example
     * for the game class subclasses ball, tablet, running, etc.
     *
     * @param position int with the position of the item within the wordpairs table
     * @return fragment instance
     * @see Game1FirstLevelFragment
     *
     * @see WordPairs
     */
    override fun createFragment(position: Int): Fragment {
        val bundle = Bundle()
        var fragment: Game1FirstLevelFragment? = null
        val wordToSearchSecondLevelMenu: String?
        //
        val resultsWordPairs = realm.where(WordPairs::class.java)
            .beginGroup()
            .equalTo("isMenuItem", "TLM")
            .endGroup()
            .findAll()
        val resultsWordPairsSize = resultsWordPairs.size
        if (resultsWordPairsSize != 0) {
            val result = resultsWordPairs[position]!!
            wordToSearchSecondLevelMenu = result.word1
            //
            bundle.putString("WORD TO SEARCH SECOND LEVEL MENU", wordToSearchSecondLevelMenu)
            //
            if (position != 0) {
                bundle.putString("LEFT ARROW", "Y")
            } else {
                bundle.putString("LEFT ARROW", "N")
            }
            if (resultsWordPairsSize > position + 1) {
                bundle.putString("RIGHT ARROW", "Y")
            } else {
                bundle.putString("RIGHT ARROW", "N")
            }
            //
            fragment = Game1FirstLevelFragment()
            fragment.arguments = bundle
        }
        assert(fragment != null)
        return fragment!!
    }

    /**
     * returns the number of top-level menu items in the wordpairs table
     *
     * @return int with the number of top-level menu items in the wordpairs table
     * @see WordPairs
     */
    override fun getItemCount(): Int {
        val resultsWordPairs = realm.where(WordPairs::class.java)
            .beginGroup()
            .equalTo("isMenuItem", "TLM")
            .endGroup()
            .findAll()
        return resultsWordPairs.size
    }
}