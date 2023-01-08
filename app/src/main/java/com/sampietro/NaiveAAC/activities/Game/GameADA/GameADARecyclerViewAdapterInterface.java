package com.sampietro.NaiveAAC.activities.Game.GameADA;

import android.view.View;

import com.sampietro.NaiveAAC.activities.Game.Game2.Game2ArrayList;

import java.util.ArrayList;

/**
 * <h1>GameAdaRecyclerViewAdapterInterface</h1>
 * <p><b>GameAdaRecyclerViewAdapterInterface</b>
 * interface used to refer to the Activity without having to explicitly use its class
 * <p>
 *
 * @see GameADAViewPagerActivity
 */
public interface GameADARecyclerViewAdapterInterface {
    /**
     * called when a game list item is clicked.
     * </p>
     * launch the corresponding page.</p>
     *
     * @param view view of clicked item
     * @param i int the position of the item within the adapter's data set
     * @see GameADAViewPagerActivity
     */
    public void onItemClick(View view, int i, ArrayList<Game2ArrayList> galleryList);
}
