package com.sampietro.NaiveAAC.activities.Settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sampietro.NaiveAAC.R
import com.sampietro.NaiveAAC.activities.BaseAndAbstractClass.FragmentAbstractClassWithoutConstructor

/**
 * <h1>GrammaticalExceptionsFragment</h1>
 *
 * **GrammaticalExceptionsFragment** UI for grammatical exceptions settings
 *
 *
 * @version     5.0, 01/04/2024
 * @see SettingsActivity
 */
class GrammaticalExceptionsFragment() : FragmentAbstractClassWithoutConstructor() {
    /**
     * prepares the ui
     *
     * @see androidx.fragment.app.Fragment.onCreateView
     *
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
        rootView =
            inflater.inflate(R.layout.activity_settings_grammatical_exceptions, container, false)
        // logic of fragment
        return rootView
    }
}