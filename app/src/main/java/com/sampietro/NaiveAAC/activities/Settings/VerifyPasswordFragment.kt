package com.sampietro.NaiveAAC.activities.Settings

import android.view.LayoutInflater
import android.view.ViewGroup
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.sampietro.NaiveAAC.R

/**
 * <h1>VerifyPasswordFragment</h1>
 *
 * **VerifyPasswordFragment** UI for check the right to access the settings
 *
 *
 * @version     4.0, 09/09/2023
 * @see SettingsActivity
 */
class VerifyPasswordFragment : Fragment() {
    var rootView: View? = null

    /**
     * prepares the ui.
     *
     *
     * @see Fragment.onCreateView
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        rootView = inflater.inflate(R.layout.activity_settings_verify_password, container, false)
        // logic of fragment
        //
        return rootView
    }
}