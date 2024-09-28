package com.sampietro.NaiveAAC.activities.Info

import android.view.LayoutInflater
import android.view.ViewGroup
import android.os.Bundle
import android.view.View
import com.sampietro.NaiveAAC.R
import com.sampietro.NaiveAAC.activities.BaseAndAbstractClass.FragmentAbstractClassWithoutConstructor

/**
 * <h1>RequestConsentToTheProcessingOfPersonalDataFragment</h1>
 *
 * **RequestConsentToTheProcessingOfPersonalDataFragment** UI to requires consent to the processing of personal data
 *
 *
 * @version     5.0, 01/04/2024
 * @see RequestConsentToTheProcessingOfPersonalDataActivity
 */
class RequestConsentToTheProcessingOfPersonalDataFragment : FragmentAbstractClassWithoutConstructor() {
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
        rootView = inflater.inflate(
            R.layout.activity_requestconsenttotheprocessingofpersonaldata,
            container,
            false
        )
        // logic of fragment
        return rootView
    }
}