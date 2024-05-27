package com.sampietro.NaiveAAC.activities.Info

import android.os.Bundle
import android.view.View
import android.widget.TextView
import com.sampietro.NaiveAAC.BuildConfig
import com.sampietro.NaiveAAC.R
import com.sampietro.NaiveAAC.activities.BaseAndAbstractClass.FragmentAbstractClass

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
class InfoFragment(contentLayoutId: Int) : FragmentAbstractClass(contentLayoutId) {
    /**
     * prepares the ui
     *
     * @see androidx.fragment.app.Fragment.onViewCreated
     */
    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        // logic of fragment
        val versionName = BuildConfig.VERSION_NAME
        val textInformation = view.findViewById<View>(R.id.information) as TextView
        textInformation.text =
            "NaiveAAC version " + versionName + getString(R.string.information_this_app_was_created_by) + getString(
                R.string.copyright_information
            )
    }
}