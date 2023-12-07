package com.sampietro.NaiveAAC.activities.Settings

import com.sampietro.NaiveAAC.activities.Graphics.GraphicsHelper.addFileImageUsingPicasso
import com.sampietro.NaiveAAC.activities.Settings.Utils.SettingsFragmentAbstractClass
import com.sampietro.NaiveAAC.activities.Game.GameParameters.GameParametersAdapter
import android.view.LayoutInflater
import android.view.ViewGroup
import android.os.Bundle
import android.view.View
import com.sampietro.NaiveAAC.R
import com.sampietro.NaiveAAC.activities.Game.GameParameters.GameParameters
import android.widget.EditText
import android.widget.ImageView
import android.widget.ListView
import android.widget.RadioButton
import io.realm.Realm
import java.io.File

/**
 * <h1>GameParametersSettingsFragment</h1>
 *
 * **GameParametersSettingsFragment** UI for game parameters settings
 *
 *
 * @version     4.0, 09/09/2023
 * @see com.sampietro.NaiveAAC.activities.Settings.Utils.SettingsFragmentAbstractClass
 *
 * @see com.sampietro.NaiveAAC.activities.Settings.SettingsActivity
 */
class GameParametersSettingsFragment : SettingsFragmentAbstractClass() {
    private lateinit var realm: Realm

    //
    private lateinit var listView: ListView
    private var adapter: GameParametersAdapter? = null

    /**
     * prepares the ui also using a listview and makes the callback to the activity
     *
     * @see androidx.fragment.app.Fragment.onCreateView
     *
     * @see com.sampietro.NaiveAAC.activities.Game.GameParameters.GameParameters
     *
     * @see com.sampietro.NaiveAAC.activities.Game.GameParameters.GameParametersAdapter
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        rootView = inflater.inflate(R.layout.activity_settings_game_parameters, container, false)
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
        val bundle = this.arguments
        //
        if (bundle != null) {
            val gameDescription = rootView.findViewById<View>(R.id.gameDescription) as EditText
            val radio_active = rootView.findViewById<View>(R.id.radio_active) as RadioButton
            val radio_not_active = rootView.findViewById<View>(R.id.radio_not_active) as RadioButton
            val gamejavaclass = rootView.findViewById<View>(R.id.gamejavaclass) as EditText
            val gameparameter = rootView.findViewById<View>(R.id.gameparameter) as EditText
            val radioUseVideoAndSound = rootView.findViewById<View>(R.id.radio_yes) as RadioButton
            val radioDoesNotUseVideoAndSound =
                rootView.findViewById<View>(R.id.radio_no) as RadioButton
            val gameinfo = rootView.findViewById<View>(R.id.gameinfo) as EditText
            val imageviewgameicon = rootView.findViewById<View>(R.id.imageviewgameicon) as ImageView
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
//            if (bundle.getString("GameUseVideoAndSound").equals("Y")) {
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
        //
        listener.receiveResultSettings(rootView)
        //
        return rootView
    }
}