package com.sampietro.NaiveAAC.activities.Settings

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.ListView
import android.widget.RadioButton
import com.sampietro.NaiveAAC.R
import com.sampietro.NaiveAAC.activities.BaseAndAbstractClass.FragmentAbstractClass
import com.sampietro.NaiveAAC.activities.Game.GameParameters.GameParameters
import com.sampietro.NaiveAAC.activities.Game.GameParameters.GameParametersAdapter
import com.sampietro.NaiveAAC.activities.Graphics.GraphicsAndPrintingHelper.addFileImageUsingPicasso
import io.realm.Realm
import java.io.File

/**
 * <h1>GameParametersSettingsFragment</h1>
 *
 * **GameParametersSettingsFragment** UI for game parameters settings
 *
 *
 * @version     5.0, 01/04/2024
 * @see com.sampietro.NaiveAAC.activities.Settings.SettingsActivity
 */
class GameParametersSettingsFragment(contentLayoutId: Int) : FragmentAbstractClass(contentLayoutId) {
    private lateinit var realm: Realm

    //
    private lateinit var listView: ListView
    private var adapter: GameParametersAdapter? = null

    /**
     * prepares the ui
     *
     * @see androidx.fragment.app.Fragment.onCreateView
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
        var results = realm.where(GameParameters::class.java).findAll()
        //
        results = results.sort("gameName")
        //
        listView = view.findViewById<View>(R.id.listview) as ListView
        //
        adapter = GameParametersAdapter(ctext, results, listView)
        //
        listView.adapter = adapter
        //
        val bundle = this.arguments
        //
        if (bundle != null) {
            val gameDescription = view.findViewById<View>(R.id.gameDescription) as EditText
            val radio_active = view.findViewById<View>(R.id.radio_active) as RadioButton
            val radio_not_active = view.findViewById<View>(R.id.radio_not_active) as RadioButton
            val gamejavaclass = view.findViewById<View>(R.id.gamejavaclass) as EditText
            val gameparameter = view.findViewById<View>(R.id.gameparameter) as EditText
            val radioUseVideoAndSound = view.findViewById<View>(R.id.radio_yes) as RadioButton
            val radioDoesNotUseVideoAndSound =
                view.findViewById<View>(R.id.radio_no) as RadioButton
            val gameinfo = view.findViewById<View>(R.id.gameinfo) as EditText
            val imageviewgameicon = view.findViewById<View>(R.id.imageviewgameicon) as ImageView
            //
            gameDescription.setText(bundle.getString(getString(R.string.gamename)))
            if (bundle.getString(getString(R.string.gameactive)) == "A") {
                radio_active.isChecked = true
                radio_not_active.isChecked = false
            } else {
                radio_active.isChecked = false
                radio_not_active.isChecked = true // default
            }
            gamejavaclass.setText(bundle.getString(getString(R.string.gamejavaclass)))
            gameparameter.setText(bundle.getString(getString(R.string.gameparameter)))
            //
            radioUseVideoAndSound.isChecked = false
            radioDoesNotUseVideoAndSound.isChecked = true // default
            if (bundle.getString(getString(R.string.gameusevideoandsound)) == "Y") {
                radioUseVideoAndSound.isChecked = true
                radioDoesNotUseVideoAndSound.isChecked = false
            }
            gameinfo.setText(bundle.getString(getString(R.string.gameinfo)))
            val gameIconType = bundle.getString(getString(R.string.gameicontype))
            val gameIconPath = bundle.getString(getString(R.string.gameiconpath))
            //
            val f = File(gameIconPath!!)
            addFileImageUsingPicasso(f, imageviewgameicon, 200, 200)
            //
        }
    }
}