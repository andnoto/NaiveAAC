package com.sampietro.NaiveAAC.activities.Info

import com.sampietro.NaiveAAC.activities.Info.Utils.InfoFragmentAbstractClass
import android.view.LayoutInflater
import android.view.ViewGroup
import android.os.Bundle
import android.view.View
import com.sampietro.NaiveAAC.R

/**
 * <h1>PrivacyFragment</h1>
 *
 * **PrivacyFragment** UI for Privacy information
 *
 *
 * @version     4.0, 09/09/2023
 * @see InfoFragmentAbstractClass
 *
 * @see PrivacyActivity
 */
class PrivacyFragment : InfoFragmentAbstractClass() {
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
        rootView = inflater.inflate(R.layout.activity_privacy_information, container, false)
        // logic of fragment
        return rootView
    }
}