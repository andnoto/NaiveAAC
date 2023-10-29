package com.sampietro.NaiveAAC.activities.Settings

import android.content.Context
import com.sampietro.NaiveAAC.activities.Settings.Utils.SettingsFragmentAbstractClass
import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.ViewGroup
import android.os.Bundle
import android.view.View
import com.sampietro.NaiveAAC.R
import android.widget.EditText

/**
 * <h1>AccountFragment</h1>
 *
 * **AccountFragment** UI to register the user
 *
 *
 * @version     4.0, 09/09/2023
 * @see com.sampietro.NaiveAAC.activities.Settings.Utils.SettingsFragmentAbstractClass
 *
 * @see com.sampietro.NaiveAAC.activities.Settings.SettingsActivity
 *
 * @see com.sampietro.NaiveAAC.activities.Account.AccountActivity
 */
class AccountFragment : SettingsFragmentAbstractClass() {
    lateinit var  sharedPref: SharedPreferences

    /**
     * prepares the ui and makes the callback to the activity
     *
     * @see androidx.fragment.app.Fragment.onCreateView
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        rootView = inflater.inflate(R.layout.activity_settings_account, container, false)
        // logic of fragment
        sharedPref = ctext.getSharedPreferences(
            getString(R.string.preference_file_key), Context.MODE_PRIVATE
        )
        val preference_LastPlayer =
            sharedPref.getString(getString(R.string.preference_LastPlayer), "")
        val editTextTextAccount = rootView.findViewById<View>(R.id.editTextTextAccount) as EditText
        editTextTextAccount.setText(preference_LastPlayer)
        //
        listener.receiveResultSettings(rootView)
        //
        return rootView
    }
}