package com.sampietro.NaiveAAC.activities.Settings;

import android.view.View;


/**
 * <h1>StoriesImageSearchArasaacRecyclerViewAdapterInterface</h1>
 * <p><b>StoriesImageSearchArasaacRecyclerViewAdapterInterface</b>
 * interface used to refer to the Activity without having to explicitly use its class
 * <p>
 *
 * @see SettingsActivity
 * @see StoriesImageSearchArasaacRecyclerViewAdapter
 */
public interface StoriesImageSearchArasaacRecyclerViewAdapterInterface {
    /**
     * called when a word list item is clicked.
     * </p>
     *
     * @param view view of clicked item
     * @param word string whit image title
     * @param url string image url
     * @see
     */
    public void onItemClick(View view, String word, String url);
}
