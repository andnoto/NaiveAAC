package com.sampietro.NaiveAAC.activities.Settings

import com.sampietro.NaiveAAC.activities.Settings.Utils.SettingsFragmentAbstractClass
import com.sampietro.NaiveAAC.activities.Graphics.ImagesAdapter
import android.view.LayoutInflater
import android.view.ViewGroup
import android.os.Bundle
import android.view.View
import android.widget.ListView
import com.sampietro.NaiveAAC.R
import com.sampietro.NaiveAAC.activities.Graphics.Images
import io.realm.Realm

/**
 * <h1>ImagesFragment</h1>
 *
 * **ImagesFragment** UI for images settings
 *
 *
 * @version     4.0, 09/09/2023
 * @see SettingsFragmentAbstractClass
 *
 * @see SettingsActivity
 */
class ImagesFragment : SettingsFragmentAbstractClass() {
    //
    private lateinit var realm: Realm

    //
    private var listView: ListView? = null
    private var adapter: ImagesAdapter? = null

    /**
     * prepares the ui also using a listview and makes the callback to the activity
     *
     * @see androidx.fragment.app.Fragment.onCreateView
     *
     * @see Images
     *
     * @see ImagesAdapter
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        rootView = inflater.inflate(R.layout.activity_settings_images, container, false)
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
        val results = realm.where(
            Images::class.java
        ).findAll()
        //
        listView = rootView.findViewById<View>(R.id.listview) as ListView
        //
        adapter = ImagesAdapter(ctext, results, listView!!)
        //
        listView!!.adapter = adapter
        //
        listener.receiveResultSettings(rootView)
        //
        return rootView
    }
}