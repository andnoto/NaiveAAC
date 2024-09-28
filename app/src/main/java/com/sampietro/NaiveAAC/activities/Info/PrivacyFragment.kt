package com.sampietro.NaiveAAC.activities.Info

import android.view.LayoutInflater
import android.view.ViewGroup
import android.os.Bundle
import android.view.View
import com.sampietro.NaiveAAC.R
import com.sampietro.NaiveAAC.activities.BaseAndAbstractClass.FragmentAbstractClassWithoutConstructor

/**
 * <h1>PrivacyFragment</h1>
 *
 * **PrivacyFragment** UI for Privacy information
 *
 *
 * @version     4.0, 09/09/2023
 * @see FragmentAbstractClassWithoutConstructor
 *
 * @see PrivacyActivity
 */
class PrivacyFragment : FragmentAbstractClassWithoutConstructor() {
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