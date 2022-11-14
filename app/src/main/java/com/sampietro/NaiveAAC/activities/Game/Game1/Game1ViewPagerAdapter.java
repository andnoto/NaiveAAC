package com.sampietro.NaiveAAC.activities.Game.Game1;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.sampietro.NaiveAAC.activities.WordPairs.WordPairs;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * <h1>Game1ViewPagerAdapter</h1>
 * <p><b>Game1ViewPagerAdapter</b> adapter for game1
 * </p>
 * Refer to <a href="https://www.raywenderlich.com/8192680-viewpager2-in-android-getting-started">raywenderlich.com</a>
 * By <a href="https://www.raywenderlich.com/u/rajdeep1008">Rajdeep Singh</a>
 *
 * @version     1.3, 05/05/22
 * @see FragmentStateAdapter
 */
public class Game1ViewPagerAdapter extends FragmentStateAdapter {
    private Context context;
    private Realm realm;
    /**
     * Game1ViewPagerAdapter constructor.
     * context annotation
     *
     * @param fragmentManager fragmentManager
     * @param lifecycle lifecycle
     * @param context context
     * @param realm realm
     */
    public Game1ViewPagerAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle, Context context, Realm realm) {
        super(fragmentManager, lifecycle);
        this.context = context;
        this.realm = realm;
    }
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
     * @see WordPairs
     */
    @NonNull
    @Override
    public Fragment createFragment(int position) {
        Bundle bundle = new Bundle();
        Game1FirstLevelFragment fragment=null;
        String wordToSearchSecondLevelMenu = null;
        //
        RealmResults<WordPairs> resultsWordPairs =
                realm.where(WordPairs.class)
                        .beginGroup()
                        .equalTo("isMenuItem", "TLM")
                        .endGroup()
                        .findAll();
        int resultsWordPairsSize = resultsWordPairs.size();
        if (resultsWordPairsSize != 0) {
            WordPairs result = resultsWordPairs.get(position);
            assert result != null;
            wordToSearchSecondLevelMenu = result.getWord1();
            //
            bundle.putString("WORD TO SEARCH SECOND LEVEL MENU", wordToSearchSecondLevelMenu);
            //
            if (!(position == 0))
                { bundle.putString("LEFT ARROW", "Y"); }
                else
                { bundle.putString("LEFT ARROW", "N"); }
            if (resultsWordPairsSize > (position + 1))
                { bundle.putString("RIGHT ARROW", "Y"); }
                else
                { bundle.putString("RIGHT ARROW", "N"); }
            //
            fragment=new Game1FirstLevelFragment();
            fragment.setArguments(bundle);
        }
        //
        assert fragment != null;
        return fragment;
    }
    /**
     * returns the number of top-level menu items in the wordpairs table
     *
     * @return int with the number of top-level menu items in the wordpairs table
     * @see WordPairs
     */
    @Override
    public int getItemCount() {
        RealmResults<WordPairs> resultsWordPairs =
                realm.where(WordPairs.class)
                        .beginGroup()
                        .equalTo("isMenuItem", "TLM")
                        .endGroup()
                        .findAll();
        return resultsWordPairs.size();
    }
}
