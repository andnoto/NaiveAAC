package com.sampietro.NaiveAAC.activities.Settings

import com.sampietro.NaiveAAC.activities.Settings.Utils.SettingsFragmentAbstractClass
import android.view.LayoutInflater
import android.view.ViewGroup
import android.os.Bundle
import android.view.View
import android.widget.ListView
import com.sampietro.NaiveAAC.R
import com.sampietro.NaiveAAC.activities.Graphics.Videos
import io.realm.Realm

/**
 * <h1>StoriesActionAfterResponseFragment</h1>
 *
 * **StoriesActionAfterResponseFragment** UI for action after response settings
 *
 *
 * @version     4.0, 09/09/2023
 * @see SettingsFragmentAbstractClass
 *
 * @see SettingsStoriesActivity
 */
class StoriesActionAfterResponseFragment : SettingsFragmentAbstractClass() {
    private lateinit var realm: Realm

    //
    private var listView: ListView? = null
    private var adapter: StoriesVideosSearchAdapter? = null

    /**
     * prepares the ui also using a listview and makes the callback to the activity
     *
     * @see androidx.fragment.app.Fragment.onCreateView
     *
     * @see Videos
     *
     * @see StoriesVideosSearchAdapter
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        rootView = inflater.inflate(
            R.layout.activity_settings_stories_action_after_response,
            container,
            false
        )
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
        listView = rootView.findViewById<View>(R.id.listview) as ListView
        //
        adapter = StoriesVideosSearchAdapter(ctext, results, listView!!, "ActionAfterResponse")
        //
        listView!!.adapter = adapter
        //
        listener.receiveResultSettings(rootView)
        //
        return rootView
    }
}