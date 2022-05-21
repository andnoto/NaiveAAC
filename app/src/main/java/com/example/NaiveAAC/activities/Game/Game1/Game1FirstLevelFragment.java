package com.example.NaiveAAC.activities.Game.Game1;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.NaiveAAC.R;
import com.example.NaiveAAC.activities.Game.Utils.GameFragmentAbstractClass;
import com.example.NaiveAAC.activities.Graphics.ImageSearchHelper;
import com.example.NaiveAAC.activities.Graphics.ResponseImageSearch;

import java.util.Locale;

/**
 * <h1>Game1FirstLevelFragment</h1>
 * <p><b>Game1FirstLevelFragment</b> UI for game1
 * display first level menu
 * the search for words takes place via a two-level menu
 * the first level contains the main research classes such as games, food, family, animals
 * the second level contains the subclasses of the first level for example
 * for the game class subclasses ball, tablet, running, etc.
 * </p>
 *
 * @version     1.3, 05/05/22
 * @see GameFragmentAbstractClass
 * @see Game1Activity
 */
public class Game1FirstLevelFragment extends GameFragmentAbstractClass {
    public String wordToSearchSecondLevelMenu;
    /**
     * prepares the ui and makes the callback to the activity
     * <p>
     * Refer to <a href="https://www.raywenderlich.com/8192680-viewpager2-in-android-getting-started">raywenderlich.com</a>
     * By <a href="https://www.raywenderlich.com/u/rajdeep1008">Rajdeep Singh</a>
     *
     * @see androidx.fragment.app.Fragment#onCreateView
     * @see ImageSearchHelper#imageSearch
     * @see #addImage
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        rootView = inflater.inflate(R.layout.activity_game_1_viewpager_content, container, false);
        //
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            wordToSearchSecondLevelMenu = bundle.getString(getString(R.string.word_to_search_second_level_menu));
            //
            TextView textFirstLevelMenuView = (TextView) rootView.findViewById(R.id.titlefirstlevelmenu);
            textFirstLevelMenuView.setText(wordToSearchSecondLevelMenu.toUpperCase(Locale.getDefault()));
            // ricerca immagine
            ResponseImageSearch image = null;
            image = ImageSearchHelper.imageSearch(realm, wordToSearchSecondLevelMenu);
            ImageView imageFirstLevelMenuView = rootView.findViewById(R.id.imagefirstlevelmenu);
            addImage(image.getUriType(),image.getUriToSearch(),imageFirstLevelMenuView, 200, 200);
        }
        //
        listener.receiveResultGameFragment(rootView);
        return rootView;
    }
    //
}

