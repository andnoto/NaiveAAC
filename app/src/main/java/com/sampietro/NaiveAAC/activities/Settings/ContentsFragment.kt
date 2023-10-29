package com.sampietro.NaiveAAC.activities.Settings

import com.sampietro.NaiveAAC.activities.Settings.Utils.SettingsFragmentAbstractClass
import android.view.LayoutInflater
import android.view.ViewGroup
import android.os.Bundle
import android.view.View
import com.sampietro.NaiveAAC.R

/**
 * <h1>ContentsFragment</h1>
 *
 * **ContentsFragment** UI for choise of contents to set
 *
 *
 * @version     4.0, 09/09/2023
 * @see SettingsFragmentAbstractClass
 *
 * @see SettingsActivity
 */
class ContentsFragment : SettingsFragmentAbstractClass() {
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
        rootView = inflater.inflate(R.layout.activity_settings_contents_menu, container, false)
        // logic of fragment
        return rootView
    }
}