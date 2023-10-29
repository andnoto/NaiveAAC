package com.sampietro.NaiveAAC.activities.Info

import android.view.LayoutInflater
import android.view.ViewGroup
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.sampietro.NaiveAAC.R

/**
 * <h1>TerminiFragment</h1>
 *
 * **TerminiFragment** UI for app information
 *
 *
 * @version     4.0, 09/09/2023
 * @see RequestConsentToTheProcessingOfPersonalDataActivity
 */
class TerminiFragment : Fragment() {
    var rootView: View? = null

    /**
     * prepares the ui
     *
     * @see Fragment.onCreateView
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        rootView = inflater.inflate(R.layout.activity_eula_information, container, false)
        // logic of fragment
        return rootView
    }
}