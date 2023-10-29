package com.sampietro.NaiveAAC.activities.Settings

import com.sampietro.NaiveAAC.activities.Settings.Utils.SettingsFragmentAbstractClass
import android.view.LayoutInflater
import android.view.ViewGroup
import android.os.Bundle
import android.view.View
import com.sampietro.NaiveAAC.R

/**
 * <h1>GeneralSettingsFragment</h1>
 *
 * **GeneralSettingsFragment** UI for general settings
 *
 *
 * @version     4.0, 09/09/2023
 * @see SettingsFragmentAbstractClass
 *
 * @see SettingsActivity
 */
class GeneralSettingsFragment : SettingsFragmentAbstractClass() {
    /**
     * prepares the ui and makes the callback to the activity
     *
     * @see androidx.fragment.app.Fragment.onCreateView
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        rootView = inflater.inflate(R.layout.activity_settings_general_settings, container, false)
        // logic of fragment
        listener.receiveResultSettings(rootView)
        //
        return rootView
    }
}