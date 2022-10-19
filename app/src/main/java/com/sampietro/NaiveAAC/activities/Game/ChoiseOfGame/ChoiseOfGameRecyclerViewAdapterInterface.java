package com.sampietro.NaiveAAC.activities.Game.ChoiseOfGame;

import android.view.View;

/**
 * <h1>ChoiseOfGameRecyclerViewAdapterInterface</h1>
 * <p><b>ChoiseOfGameRecyclerViewAdapterInterface</b>
 * interface used to refer to the Activity without having to explicitly use its class
 * <p>
 *
 * @see ChoiseOfGameActivity
 */
public interface ChoiseOfGameRecyclerViewAdapterInterface {
    /**
     * called when a game list item is clicked.
     * </p>
     * launch the corresponding game or the corresponding info.</p>
     *
     * @param view view of clicked item
     * @param i int the position of the item within the adapter's data set
     * @see ChoiseOfGameActivity
     */
    public void onItemClick(View view, int i);
}
