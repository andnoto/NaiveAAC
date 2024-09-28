package com.sampietro.NaiveAAC.activities.Settings

import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import com.sampietro.NaiveAAC.R
import com.sampietro.NaiveAAC.activities.BaseAndAbstractClass.FragmentAbstractClassWithoutConstructor
import com.sampietro.NaiveAAC.activities.Graphics.GraphicsAndPrintingHelper

/**
 * <h1>AccountFragment</h1>
 *
 * **AccountFragment** UI to register the user
 *
 *
 * @version     5.0, 01/04/2024
 * @see com.sampietro.NaiveAAC.activities.Settings.SettingsActivity
 *
 * @see com.sampietro.NaiveAAC.activities.Account.AccountActivity
 */
class AccountFragment() : FragmentAbstractClassWithoutConstructor() {
    lateinit var  sharedPref: SharedPreferences

    /**
     * prepares the ui and makes the callback to the activity
     *
     * @see androidx.fragment.app.Fragment.onCreateView
     */
//    override fun onViewCreated(
//        view: View,
//        savedInstanceState: Bundle?
//    ) {
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
        val bundle = this.arguments
        val stringUri: String?
        //
        val descrizione: String?
        //
        if (bundle != null) {
            //
            descrizione = bundle.getString("descrizione")
            val accD = rootView.findViewById<View>(R.id.editTextTextAccount) as EditText
            accD.setText(descrizione)
            //
            stringUri = bundle.getString("URI")
//            if (stringUri != "none") {
                val uri = Uri.parse(stringUri)
                //
                val myImage = rootView.findViewById<View>(R.id.imageviewaccounticon) as ImageView
                GraphicsAndPrintingHelper.showImage(ctext, uri, myImage)
//            }
        }
        //
        return rootView
    }
}