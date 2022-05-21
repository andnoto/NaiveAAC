package com.example.NaiveAAC.activities.Game.Game1;

import android.view.View;


/**
 * <h1>Game1RecyclerViewAdapterInterface</h1>
 * <p><b>Game1RecyclerViewAdapterInterface</b>
 * interface used to refer to the Activity without having to explicitly use its class
 * <p>
 *
 * @see Game1Activity
 */
public interface Game1RecyclerViewAdapterInterface {
    /**
     * called when a word list item is clicked.
     * </p>
     * 1) prepare the ui through the history table
     * 2) launch the fragment for displaying the ui
     * 3) in case of completion of the choice of words,
     * carries out the grammatical arrangement and the reading of the text,
     * 4) chosen the 3rd and last word of the sentence
     * the following clicks on the words will start the tts</p>
     *
     * @param view view of clicked item
     * @param i int the position of the item within the adapter's data set
     * @see Game1Activity
     */
    public void onItemClick(View view, int i);
}
