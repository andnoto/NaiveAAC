package com.sampietro.NaiveAAC.activities.Settings

import android.view.LayoutInflater
import android.view.ViewGroup
import android.os.Bundle
import android.view.View
import com.sampietro.NaiveAAC.R
import com.sampietro.NaiveAAC.activities.BaseAndAbstractClass.FragmentAbstractClassWithoutConstructor

/**
 * <h1>DataImportSettingsFragment</h1>
 *
 * **DataImportSettingsFragment** UI for data import settings
 *
 *
 * @version     5.0, 01/04/2024
 * @see FragmentAbstractClassWithoutConstructor
 *
 * @see SettingsActivity
 */
class DataImportSettingsFragment : FragmentAbstractClassWithoutConstructor() {
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
        rootView = inflater.inflate(R.layout.activity_settings_data_import, container, false)
        // logic of fragment
        return rootView
    }
}