package com.sampietro.NaiveAAC.activities.Info

import com.sampietro.NaiveAAC.activities.Graphics.VideosCopyrightAdapter
import android.os.Bundle
import android.view.View
import android.widget.ListView
import com.sampietro.NaiveAAC.R
import android.widget.TextView
import io.realm.RealmResults
import com.sampietro.NaiveAAC.activities.Graphics.Videos
import com.sampietro.NaiveAAC.activities.BaseAndAbstractClass.FragmentAbstractClass
import io.realm.Realm
import io.realm.Sort

/**
 * <h1>InfoVideosCopyrightListFragment</h1>
 *
 * **InfoVideosCopyrightListFragment** UI for Videos copyright list
 *
 *
 * @version     5.0, 01/04/2024
 *
 * @see InfoActivity
 */
class InfoVideosCopyrightListFragment(contentLayoutId: Int) : FragmentAbstractClass(contentLayoutId) {
    private lateinit var realm: Realm
    //
    private lateinit var listView: ListView
    private lateinit var adapter: VideosCopyrightAdapter

    /**
     * prepares the ui also using a listview
     *
     * @see androidx.fragment.app.Fragment.onViewCreated
     *
     * @see Videos
     *
     * @see VideosCopyrightAdapter
     */
    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        // logic of fragment
        val myTextView = view.findViewById<View>(R.id.textviewcopyright) as TextView
        myTextView.text = "COPYRIGHT VIDEO"
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
        //
        realm = Realm.getDefaultInstance()
        var results: RealmResults<Videos>
        results = realm.where(Videos::class.java).findAll()
        //
        val mStrings1 = arrayOf(ctext.getString(R.string.copyright))
        val mStrings2 = arrayOf(Sort.ASCENDING)
        results = results.sort(mStrings1, mStrings2)
        //
        listView = view.findViewById<View>(R.id.listview) as ListView
        //
        adapter = VideosCopyrightAdapter(ctext, results, listView)
        //
        listView.adapter = adapter
    }
}