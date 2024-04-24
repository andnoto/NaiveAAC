package com.sampietro.NaiveAAC.activities.Settings

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import com.sampietro.NaiveAAC.R
import android.widget.EditText
import com.sampietro.NaiveAAC.activities.BaseAndAbstractClass.FragmentAbstractClass

/**
 * <h1>AccountFragment</h1>
 *
 * **AccountFragment** UI to register the user
 *
 *
 * @version     5.0, 01/04/2024
 * @see com.sampietro.NaiveAAC.activities.Settings.Utils.SettingsFragmentAbstractClass
 *
 * @see com.sampietro.NaiveAAC.activities.Settings.SettingsActivity
 *
 * @see com.sampietro.NaiveAAC.activities.Account.AccountActivity
 */
class AccountFragment(contentLayoutId: Int) : FragmentAbstractClass(contentLayoutId) {
    lateinit var  sharedPref: SharedPreferences

    /**
     * prepares the ui and makes the callback to the activity
     *
     * @see androidx.fragment.app.Fragment.onCreateView
     */
    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        // logic of fragment
        sharedPref = ctext.getSharedPreferences(
            getString(R.string.preference_file_key), Context.MODE_PRIVATE
        )
        val preference_LastPlayer =
            sharedPref.getString(getString(R.string.preference_LastPlayer), "")
        val editTextTextAccount = view.findViewById<View>(R.id.editTextTextAccount) as EditText
        editTextTextAccount.setText(preference_LastPlayer)
    }
}