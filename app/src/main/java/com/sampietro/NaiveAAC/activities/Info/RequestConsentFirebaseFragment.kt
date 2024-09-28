package com.sampietro.NaiveAAC.activities.Info

import android.view.LayoutInflater
import android.view.ViewGroup
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.sampietro.NaiveAAC.R
import com.sampietro.NaiveAAC.activities.BaseAndAbstractClass.FragmentAbstractClassWithoutConstructor

/**
 * <h1>RequestConsentFirebaseFragment</h1>
 *
 * **RequestConsentFirebaseFragment** UI to requires consent Firebase Analytics and Crashlytics
 *
 *
 * @version     4.0, 09/09/2023
 * @see RequestConsentFirebaseActivity
 */
class RequestConsentFirebaseFragment : FragmentAbstractClassWithoutConstructor() {
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
        rootView = inflater.inflate(R.layout.activity_requestconsentfirebase, container, false)
        // logic of fragment
        return rootView
    }
}