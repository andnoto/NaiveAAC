package com.sampietro.NaiveAAC.activities.Settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.RadioButton
import com.sampietro.NaiveAAC.R
import com.sampietro.NaiveAAC.activities.BaseAndAbstractClass.FragmentAbstractClassWithoutConstructor
import com.sampietro.NaiveAAC.activities.Graphics.GraphicsAndPrintingHelper.addFileImageUsingPicasso
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
class GameParametersSettingsFragment() : FragmentAbstractClassWithoutConstructor() {
    /**
     * prepares the ui
     *
     * @see androidx.fragment.app.Fragment.onCreateView
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
        rootView = inflater.inflate(R.layout.activity_settings_game_parameters, container, false)
        // logic of fragment
        val bundle = this.arguments
        //
        if (bundle != null) {
            val gameDescription = rootView.findViewById<View>(R.id.gameDescription) as EditText
            val radio_active = rootView.findViewById<View>(R.id.radio_active) as RadioButton
            val radio_not_active = rootView.findViewById<View>(R.id.radio_not_active) as RadioButton
            val gamejavaclass = rootView.findViewById<View>(R.id.gamejavaclass) as EditText
            val gameparameter = rootView.findViewById<View>(R.id.gameparameter) as EditText
            val radioUseVideoAndSound = rootView.findViewById<View>(R.id.radio_usevideoandsound) as RadioButton
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
        return rootView
    }
}