package com.sampietro.NaiveAAC.activities.Settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import com.sampietro.NaiveAAC.R
import com.sampietro.NaiveAAC.activities.Arasaac.PictogramsAllToModify
import com.sampietro.NaiveAAC.activities.Arasaac.PictogramsAllToModifyAdapter
import com.sampietro.NaiveAAC.activities.BaseAndAbstractClass.FragmentAbstractClassWithoutConstructor
import io.realm.Realm

/**
 * <h1>PictogramsToModifyFragment</h1>
 *
 * **PictogramsToModifyFragment** UI for pictograms to modify settings
 *
 *
 * @version     5.0, 01/04/2024
 * @see SettingsActivity
 */
class PictogramsToModifyFragment() : FragmentAbstractClassWithoutConstructor()  {
    /**
     * prepares the ui
     *
     * @see androidx.fragment.app.Fragment.onCreateView
     *
     * @see PictogramsAllToModify
     *
     * @see PictogramsAllToModifyAdapter
     */
//   override fun onViewCreated(
//        view: View,
//        savedInstanceState: Bundle?
//    ) {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        rootView =
            inflater.inflate(R.layout.activity_settings_pictograms_to_modify, container, false)
        // logic of fragment
        return rootView
    }
}