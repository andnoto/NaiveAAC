package com.sampietro.NaiveAAC.activities.Info

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.sampietro.NaiveAAC.BuildConfig
import com.sampietro.NaiveAAC.R
import com.sampietro.NaiveAAC.activities.BaseAndAbstractClass.FragmentAbstractClassWithoutConstructor

/**
 * <h1>InfoFragment</h1>
 *
 * **InfoFragment** UI for app information
 *
 *
 * @version     5.0, 01/04/2024
 *
 * @see InfoActivity
 */
class InfoFragment() : FragmentAbstractClassWithoutConstructor() {
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
            rootView = inflater.inflate(R.layout.activity_settings_information, container, false)
        // logic of fragment
        val versionName = BuildConfig.VERSION_NAME
        val textInformation = rootView.findViewById<View>(R.id.information) as TextView
        textInformation.text =
            "NaiveAAC version " + versionName + getString(R.string.information_this_app_was_created_by) + getString(
                R.string.copyright_information
            )
        //
        return rootView
        }
}