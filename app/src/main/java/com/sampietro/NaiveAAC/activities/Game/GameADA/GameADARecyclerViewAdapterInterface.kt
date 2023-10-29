package com.sampietro.NaiveAAC.activities.Game.GameADA

import android.view.View
import java.util.ArrayList

/**
 * <h1>GameAdaRecyclerViewAdapterInterface</h1>
 *
 * **GameAdaRecyclerViewAdapterInterface**
 * interface used to refer to the Activity without having to explicitly use its class
 *
 *
 *
 * @see GameADAViewPagerActivity
 */
interface GameADARecyclerViewAdapterInterface {
    /**
     * called when a game list item is clicked.
     *
     * launch the corresponding page.
     *
     * @param view view of clicked item
     * @param i int the position of the item within the adapter's data set
     * @param galleryList ArrayList<GameADAArrayList> represents the arraylist object to initialize the recyclerview
     * @see GameADAViewPagerActivity
     */
    fun onItemClick(view: View?, i: Int, galleryList: ArrayList<GameADAArrayList>)
}