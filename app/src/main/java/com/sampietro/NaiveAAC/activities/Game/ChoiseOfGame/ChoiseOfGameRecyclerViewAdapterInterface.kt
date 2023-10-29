package com.sampietro.NaiveAAC.activities.Game.ChoiseOfGame

import android.view.View

/**
 * <h1>ChoiseOfGameRecyclerViewAdapterInterface</h1>
 *
 * **ChoiseOfGameRecyclerViewAdapterInterface**
 * interface used to refer to the Activity without having to explicitly use its class
 *
 *
 *
 * @see ChoiseOfGameActivity
 */
interface ChoiseOfGameRecyclerViewAdapterInterface {
    /**
     * called when a game list item is clicked.
     *
     * launch the corresponding game or the corresponding info.
     *
     * @param view view of clicked item
     * @param i int the position of the item within the adapter's data set
     * @see ChoiseOfGameActivity
     */
    fun onItemClick(view: View?, i: Int)
}