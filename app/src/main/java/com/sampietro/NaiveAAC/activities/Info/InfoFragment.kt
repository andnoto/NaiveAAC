package com.sampietro.NaiveAAC.activities.Info

import com.sampietro.NaiveAAC.activities.Info.Utils.InfoFragmentAbstractClass
import android.view.LayoutInflater
import android.view.ViewGroup
import android.os.Bundle
import android.view.View
import com.sampietro.NaiveAAC.R
import android.widget.TextView
import com.sampietro.NaiveAAC.BuildConfig

/**
 * <h1>InfoFragment</h1>
 *
 * **InfoFragment** UI for app information
 *
 *
 * @version     4.0, 09/09/2023
 * @see InfoFragmentAbstractClass
 *
 * @see InfoActivity
 */
class InfoFragment : InfoFragmentAbstractClass() {
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