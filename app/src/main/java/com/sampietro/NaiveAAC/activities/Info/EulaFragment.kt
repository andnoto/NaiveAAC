package com.sampietro.NaiveAAC.activities.Info

import com.sampietro.NaiveAAC.activities.Info.Utils.InfoFragmentAbstractClass
import android.view.LayoutInflater
import android.view.ViewGroup
import android.os.Bundle
import android.view.View
import com.sampietro.NaiveAAC.R

/**
 * <h1>EulaFragment</h1>
 *
 * **EulaFragment** UI for app information
 *
 *
 * @version     4.0, 09/09/2023
 * @see InfoFragmentAbstractClass
 *
 * @see EulaActivity
 */
class EulaFragment : InfoFragmentAbstractClass() {
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
        rootView = inflater.inflate(R.layout.activity_eula_information, container, false)
        // logic of fragment
        return rootView
    }
}