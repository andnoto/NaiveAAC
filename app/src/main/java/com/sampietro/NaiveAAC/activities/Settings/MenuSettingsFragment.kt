package com.sampietro.NaiveAAC.activities.Settings

import android.view.LayoutInflater
import android.view.ViewGroup
import android.os.Bundle
import android.view.View
import com.sampietro.NaiveAAC.R
import com.sampietro.NaiveAAC.activities.BaseAndAbstractClass.FragmentAbstractClassWithoutConstructor

/**
 * <h1>MenuSettingsFragment</h1>
 *
 * **MenuSettingsFragment** UI for settings menu
 *
 *
 * @version     5.0, 01/04/2024
 * @see FragmentAbstractClassWithoutConstructor
 *
 * @see SettingsActivity
 */
class MenuSettingsFragment : FragmentAbstractClassWithoutConstructor() {
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
        rootView = inflater.inflate(R.layout.activity_settings_menu, container, false)
        // logic of fragment
        return rootView
    }
}