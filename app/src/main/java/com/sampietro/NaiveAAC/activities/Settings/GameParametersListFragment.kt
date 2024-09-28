package com.sampietro.NaiveAAC.activities.Settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.ListView
import android.widget.RadioButton
import com.sampietro.NaiveAAC.R
import com.sampietro.NaiveAAC.activities.BaseAndAbstractClass.FragmentAbstractClassWithoutConstructor
import com.sampietro.NaiveAAC.activities.Game.GameParameters.GameParameters
import com.sampietro.NaiveAAC.activities.Game.GameParameters.GameParametersAdapter
import com.sampietro.NaiveAAC.activities.Graphics.GraphicsAndPrintingHelper.addFileImageUsingPicasso
import io.realm.Realm
import java.io.File

/**
 * <h1>GameParametersListFragment</h1>
 *
 * **GameParametersSettingsFragment** UI for game parameters settings
 *
 *
 * @version     5.0, 01/04/2024
 * @see com.sampietro.NaiveAAC.activities.Settings.SettingsActivity
 */
class GameParametersListFragment() : FragmentAbstractClassWithoutConstructor() {
    private lateinit var realm: Realm

    //
    private lateinit var listView: ListView
    private var adapter: GameParametersAdapter? = null

    /**
     * prepares the ui
     *
     * @see androidx.fragment.app.Fragment.onCreateView
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        rootView = inflater.inflate(R.layout.activity_settings_game_parameters_list, container, false)
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
        var results = realm.where(GameParameters::class.java).findAll()
        //
        results = results.sort("gameName")
        //
        listView = rootView.findViewById<View>(R.id.listview) as ListView
        //
        adapter = GameParametersAdapter(ctext, results, listView)
        //
        listView.adapter = adapter
        //
        return rootView
    }
}