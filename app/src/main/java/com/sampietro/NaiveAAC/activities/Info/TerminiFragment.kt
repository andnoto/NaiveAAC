package com.sampietro.NaiveAAC.activities.Info

import android.view.LayoutInflater
import android.view.ViewGroup
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.sampietro.NaiveAAC.R
import com.sampietro.NaiveAAC.activities.BaseAndAbstractClass.FragmentAbstractClassWithoutConstructor

/**
 * <h1>TerminiFragment</h1>
 *
 * **TerminiFragment** UI for app information
 *
 *
 * @version     5.0, 01/04/2024
 * @see RequestConsentToTheProcessingOfPersonalDataActivity
 */
class TerminiFragment : FragmentAbstractClassWithoutConstructor() {
    /**
     * prepares the ui
     *
     * @see Fragment.onCreateView
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        rootView = inflater.inflate(R.layout.activity_eula_information, container, false)
        // logic of fragment
        return rootView
    }
}