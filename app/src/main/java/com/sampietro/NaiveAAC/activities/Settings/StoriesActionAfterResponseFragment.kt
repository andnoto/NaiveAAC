package com.sampietro.NaiveAAC.activities.Settings

import android.os.Bundle
import android.view.View
import android.widget.ListView
import com.sampietro.NaiveAAC.R
import com.sampietro.NaiveAAC.activities.BaseAndAbstractClass.FragmentAbstractClass
import com.sampietro.NaiveAAC.activities.Graphics.Videos
import io.realm.Realm

/**
 * <h1>StoriesActionAfterResponseFragment</h1>
 *
 * **StoriesActionAfterResponseFragment** UI for action after response settings
 *
 *
 * @version     4.0, 09/09/2023
 * @see FragmentAbstractClass
 *
 * @see SettingsStoriesActivity
 */
class StoriesActionAfterResponseFragment(contentLayoutId: Int) : FragmentAbstractClass(contentLayoutId) {
    private lateinit var realm: Realm

    //
    private lateinit var listView: ListView
    private var adapter: StoriesVideosSearchAdapter? = null

    /**
     * prepares the ui also using a listview
     *
     * @see androidx.fragment.app.Fragment.onViewCreated
     *
     * @see Videos
     *
     * @see StoriesVideosSearchAdapter
     */
    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        // logic of fragment
        realm = Realm.getDefaultInstance()
        // ListView
        // 1) we get a reference to the data structure through the RealmResults class which constitutes
        // the query result set and is an iterable collection accessible with Java constructs:
        // for loop, basic access to position and Iterator.
        // The approach for realm queries is object-oriented.
        // The where method will retrieve the objects from the specified class and the result will
        // be treated with filtrate by other specific methods.
        // At the end of the selection configuration, the findAll method will be invoked to retrieve
        // all the corresponding results.
        // 2) we instantiate an Adapter by assigning it, the collection and the layout related to
        // each single row
        // 3) we retrieve the ListView prepared in the layout and assign it the reference to the adapter
        // which will be your View "supplier".
        val results = realm.where(Videos::class.java).findAll()
        //
        listView = view.findViewById<View>(R.id.listview) as ListView
        //
        adapter = StoriesVideosSearchAdapter(ctext, results, listView, "ActionAfterResponse")
        //
        listView.adapter = adapter
    }
}