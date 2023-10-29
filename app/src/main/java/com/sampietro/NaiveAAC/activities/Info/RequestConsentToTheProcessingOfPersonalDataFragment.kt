package com.sampietro.NaiveAAC.activities.Info

import android.content.Context
import android.widget.TextView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.sampietro.NaiveAAC.R

/**
 * <h1>RequestConsentToTheProcessingOfPersonalDataFragment</h1>
 *
 * **RequestConsentToTheProcessingOfPersonalDataFragment** UI to requires consent to the processing of personal data
 *
 *
 * @version     4.0, 09/09/2023
 * @see RequestConsentToTheProcessingOfPersonalDataActivity
 */
class RequestConsentToTheProcessingOfPersonalDataFragment : Fragment() {
    var rootView: View? = null
    var textView: TextView? = null
    var ctext: Context? = null

    /**
     * context annotation
     *
     * @see Fragment.onAttach
     */
    override fun onAttach(context: Context) {
        super.onAttach(context)
        //
        ctext = context
    }

    /**
     * prepares the ui
     *
     * @see androidx.fragment.app.Fragment.onCreateView
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        rootView = inflater.inflate(
            R.layout.activity_requestconsenttotheprocessingofpersonaldata,
            container,
            false
        )
        // logic of fragment
        return rootView
    }
}