package com.sampietro.NaiveAAC.activities.Settings

import android.os.Bundle
import android.view.*
import android.widget.ListView
import com.sampietro.NaiveAAC.R
import com.sampietro.NaiveAAC.activities.BaseAndAbstractClass.FragmentAbstractClassWithoutConstructor
import com.sampietro.NaiveAAC.activities.Graphics.Videos
import com.sampietro.NaiveAAC.activities.Graphics.VideosAdapter
import io.realm.Realm

/**
 * <h1>VideosListFragment</h1>
 *
 * **VideosFragment** UI for videos settings
 *
 *
 * @version     5.0, 01/04/2024
 * @see FragmentAbstractClassWithoutConstructor
 *
 * @see SettingsActivity
 */
class VideosListFragment() : FragmentAbstractClassWithoutConstructor()
    {
    private lateinit var realm: Realm

    //
    private lateinit var listView: ListView
    private var adapter: VideosAdapter? = null

    /**
     * prepares the ui also using a listview
     *
     * @see androidx.fragment.app.Fragment.onCreateView
     *
     * @see com.sampietro.NaiveAAC.activities.Graphics.Videos
     *
     * @see com.sampietro.NaiveAAC.activities.Graphics.VideosAdapter
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        rootView = inflater.inflate(R.layout.activity_settings_videos_list, container, false)
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
        var results = realm.where(Videos::class.java).findAll()
        //
        results = results.sort("descrizione")
        //
        listView = rootView.findViewById<View>(R.id.listview) as ListView
        //
        adapter = VideosAdapter(ctext, results, listView)
        //
        listView.adapter = adapter
        //
        return rootView
    }
}