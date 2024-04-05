package com.sampietro.NaiveAAC.activities.Settings.Utils

import android.view.View

/**
 * <h1>StoriesImageSearchArasaacRecyclerViewAdapterInterface</h1>
 *
 * **StoriesImageSearchArasaacRecyclerViewAdapterInterface**
 * interface used to refer to the Activity without having to explicitly use its class
 *
 *
 *
 * @see SettingsActivity
 *
 * @see ImageSearchArasaacRecyclerViewAdapter
 */
interface ImageSearchArasaacRecyclerViewAdapterInterface {
    /**
     * called when a word list item is clicked.
     *
     *
     * @param view view of clicked item
     * @param word string whit image title
     * @param url string image url
     * @see
     */
    fun onItemClick(view: View?, word: String?, url: String?)
}