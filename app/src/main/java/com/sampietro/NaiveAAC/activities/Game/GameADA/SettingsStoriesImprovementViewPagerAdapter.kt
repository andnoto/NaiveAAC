package com.sampietro.NaiveAAC.activities.Game.GameADA

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.sampietro.NaiveAAC.R
import com.sampietro.NaiveAAC.activities.Stories.Stories
import io.realm.Realm
import io.realm.RealmResults

/**
 * <h1>SettingsStoriesImprovementViewPagerAdapter</h1>
 *
 * **SettingsStoriesImprovementViewPagerAdapter** adapter for gameADA
 *
 * Refer to [raywenderlich.com](https://www.raywenderlich.com/8192680-viewpager2-in-android-getting-started)
 * By [Rajdeep Singh](https://www.raywenderlich.com/u/rajdeep1008)
 *
 * @version     5.0, 01/04/2024
 * @see FragmentStateAdapter
 */
class SettingsStoriesImprovementViewPagerAdapter
/**
 * Game1ViewPagerAdapter constructor.
 * context annotation
 *
 * @param fragmentManager fragmentManager
 * @param lifecycle lifecycle
 * @param context context
 * @param realm realm
 */(
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle,
    private val context: Context,
    private val realm: Realm,
    //
    var sharedStory: String,
    var wordToDisplayInTheStoryIndex: Int,
    //
    private val gameUseVideoAndSound: String,
    var resultsStories: RealmResults<Stories>
) : FragmentStateAdapter(fragmentManager, lifecycle) {
    /**
     * returns a fragment instance for the given position to display
     *
     * @param position int with the position of the item within the phrase
     * @return fragment instance
     * @see GameADAViewPagerFragment
     *
     * @see Stories
     */
    override fun createFragment(position: Int): Fragment {
        val bundle = Bundle()
        val fragment: SettingsStoriesImprovementViewPagerFragment?
        //
        bundle.putString(context.getString(R.string.story_to_display), sharedStory)
        //        bundle.putInt("PHRASE TO DISPLAY INDEX", phraseToDisplayIndex);
        bundle.putInt(context.getString(R.string.word_to_display_index), position)
        bundle.putString(context.getString(R.string.game_use_video_and_sound), gameUseVideoAndSound)
        //
//        fragment = GameADAViewPagerFragment(R.layout.activity_settings_stories_improvement_viewpager_content)
        fragment = SettingsStoriesImprovementViewPagerFragment()
        fragment.arguments = bundle
        //
        return fragment
    }

    /**
     * returns the number of word items in the stories table
     *
     * @return int with the number of top-level menu items in the wordpairs table
     * @see Stories
     */
    override fun getItemCount(): Int {
        return resultsStories.size
    }
}