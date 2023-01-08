package com.sampietro.NaiveAAC.activities.Game.GameADA;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.sampietro.NaiveAAC.activities.Stories.Stories;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * <h1>GameADAViewPagerAdapter</h1>
 * <p><b>GameADAViewPagerAdapter</b> adapter for game1
 * </p>
 * Refer to <a href="https://www.raywenderlich.com/8192680-viewpager2-in-android-getting-started">raywenderlich.com</a>
 * By <a href="https://www.raywenderlich.com/u/rajdeep1008">Rajdeep Singh</a>
 *
 * @version     1.3, 05/05/22
 * @see FragmentStateAdapter
 */
public class GameADAViewPagerAdapter extends FragmentStateAdapter {
    private Context context;
    private Realm realm;
    //
    public String sharedStory;
    public RealmResults<Stories> resultsStories;
    public int phraseToDisplayIndex;
    /**
     * Game1ViewPagerAdapter constructor.
     * context annotation
     *
     * @param fragmentManager fragmentManager
     * @param lifecycle lifecycle
     * @param context context
     * @param realm realm
     */
    public GameADAViewPagerAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle, Context context, Realm realm,
                                   String sharedStory, int phraseToDisplayIndex ) {
        super(fragmentManager, lifecycle);
        this.context = context;
        this.realm = realm;
        this.sharedStory = sharedStory;
        this.phraseToDisplayIndex = phraseToDisplayIndex;
    }
    /**
     * returns a fragment instance for the given position to display
     *
     * @param position int with the position of the item within the phrase
     * @return fragment instance
     * @see GameADAViewPagerFragment
     * @see Stories
     */
    @NonNull
    @Override
    public Fragment createFragment(int position) {
        Bundle bundle = new Bundle();
        GameADAViewPagerFragment fragment=null;
        //
        bundle.putString("STORY TO DISPLAY", sharedStory);
        bundle.putInt("PHRASE TO DISPLAY INDEX", phraseToDisplayIndex);
        bundle.putInt("WORD TO DISPLAY INDEX", position);
        //
        fragment=new GameADAViewPagerFragment();
        fragment.setArguments(bundle);
        //
        return fragment;
    }
    /**
     * returns the number of word items in the stories table
     *
     * @return int with the number of top-level menu items in the wordpairs table
     * @see Stories
     */
    @Override
    public int getItemCount() {
        resultsStories =
                realm.where(Stories.class)
                        .beginGroup()
                        .equalTo("story", sharedStory)
                        .equalTo("phraseNumberInt", phraseToDisplayIndex)
                        .notEqualTo("wordNumberInt", 0)
                        .lessThan("wordNumberInt", 99)
                        .endGroup()
                        .findAll();
        return resultsStories.size();
    }
}
