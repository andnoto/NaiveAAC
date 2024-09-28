package com.sampietro.NaiveAAC.activities.Info

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.TextView
import com.sampietro.NaiveAAC.R
import com.sampietro.NaiveAAC.activities.BaseAndAbstractClass.FragmentAbstractClassWithoutConstructor
import com.sampietro.NaiveAAC.activities.Graphics.Sounds
import com.sampietro.NaiveAAC.activities.Graphics.SoundsCopyrightAdapter
import io.realm.Realm
import io.realm.RealmResults
import io.realm.Sort

/**
 * <h1>InfoSoundsCopyrightListFragment</h1>
 *
 * **InfoSoundsCopyrightListFragment** UI for Sounds copyright list
 *
 *
 * @version     5.0, 01/04/2024
 *
 * @see InfoActivity
 */
class InfoSoundsCopyrightListFragment() : FragmentAbstractClassWithoutConstructor() {
    private lateinit var realm: Realm

    //
    private lateinit var listView: ListView
    private lateinit var adapter: SoundsCopyrightAdapter

    /**
     * prepares the ui also using a listview
     *
     * @see androidx.fragment.app.Fragment.onCreateView
     *
     * @see Sounds
     *
     * @see SoundsCopyrightAdapter
     */
//    override fun onViewCreated(
//        view: View,
//        savedInstanceState: Bundle?
//    ) {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        rootView = inflater.inflate(R.layout.activity_settings_copyright_list, container, false)
        // logic of fragment
        val myTextView = rootView.findViewById<View>(R.id.textviewcopyright) as TextView
        myTextView.text = "COPYRIGHT SUONI"
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
        var results: RealmResults<Sounds>
        results = realm.where(Sounds::class.java).isNotNull("copyright").findAll()
        //
        val mStrings1 = arrayOf(ctext.getString(R.string.copyright))
        val mStrings2 = arrayOf(Sort.ASCENDING)
        results = results.sort(mStrings1, mStrings2)
        //
        listView = rootView.findViewById<View>(R.id.listview) as ListView
        //
        adapter = SoundsCopyrightAdapter(ctext, results, listView)
        //
        listView.adapter = adapter
        //
        return rootView
    }
}