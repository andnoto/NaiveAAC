package com.sampietro.NaiveAAC.activities.Settings

import android.view.LayoutInflater
import android.view.ViewGroup
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.RadioButton
import androidx.appcompat.app.AppCompatActivity
import com.sampietro.NaiveAAC.R
import com.sampietro.NaiveAAC.activities.BaseAndAbstractClass.FragmentAbstractClassWithoutConstructor

/**
 * <h1>GeneralSettingsFragment</h1>
 *
 * **GeneralSettingsFragment** UI for general settings
 *
 *
 * @version     5.0, 01/04/2024
 * @see FragmentAbstractClassWithoutConstructor
 *
 * @see SettingsActivity
 */
class GeneralSettingsFragment : FragmentAbstractClassWithoutConstructor() {
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
        rootView = inflater.inflate(R.layout.activity_settings_general_settings, container, false)
        // logic of fragment
        val enableprinting = rootView.findViewById<View>(R.id.enableprinting) as RadioButton
        val disableprinting = rootView.findViewById<View>(R.id.disableprinting) as RadioButton
        val uppercase = rootView.findViewById<View>(R.id.uppercase) as RadioButton
        val lowercase = rootView.findViewById<View>(R.id.lowercase) as RadioButton
        val disablelistmode = rootView.findViewById<View>(R.id.disablelistmode) as RadioButton
        val enablesortedlistmode = rootView.findViewById<View>(R.id.enablesortedlistmode) as RadioButton
        val enablerandomlistmode = rootView.findViewById<View>(R.id.enablerandomlistmode) as RadioButton
        val allowedmarginoferror = rootView.findViewById<View>(R.id.allowedmarginoferror) as EditText
        val serverbluetoothmode = rootView.findViewById<View>(R.id.serverbluetoothmode) as RadioButton
        val clientbluetoothmode = rootView.findViewById<View>(R.id.clientbluetoothmode) as RadioButton
        val defaultbluetoothmode = rootView.findViewById<View>(R.id.defaultbluetoothmode) as RadioButton
        //
        val sharedPref = ctext.getSharedPreferences(getString(R.string.preference_file_key), AppCompatActivity.MODE_PRIVATE)
        val preference_PrintPermissions = sharedPref.getString(
            getString(R.string.preference_print_permissions),
            "DEFAULT"
        )
        if (preference_PrintPermissions == "Y") {
            enableprinting.isChecked = true
            disableprinting.isChecked = false
        } else {
            enableprinting.isChecked = false
            disableprinting.isChecked = true // default
        }
        //
        val preference_TitleWritingType = sharedPref.getString(
            getString(R.string.preference_title_writing_type),
            getString(R.string.uppercase)
        ).toString()
        if (preference_TitleWritingType == getString(R.string.lowercase)) {
            lowercase.isChecked = true
            uppercase.isChecked = false
        } else {
            lowercase.isChecked = false
            uppercase.isChecked = true // default
        }
        //
        val preference_List_Mode = sharedPref.getString(
            getString(R.string.preference_list_mode),
            getString(R.string.sorted)
        ).toString()
        if (preference_List_Mode == getString(R.string.disabled)) {
            disablelistmode.isChecked = true
            enablesortedlistmode.isChecked = false
            enablerandomlistmode.isChecked = false
        } else {
            if (preference_List_Mode == getString(R.string.random)) {
                disablelistmode.isChecked = false
                enablesortedlistmode.isChecked = false
                enablerandomlistmode.isChecked = true
            } else {
                disablelistmode.isChecked = false
                enablesortedlistmode.isChecked = true // default
                enablerandomlistmode.isChecked = false
            }
        }
        //
        val preference_AllowedMarginOfError =
            sharedPref.getInt(getString(R.string.preference_allowed_margin_of_error), 20)
        allowedmarginoferror.setText(preference_AllowedMarginOfError.toString())
        //
        val preference_BluetoothMode =
            sharedPref.getString(getString(R.string.preference_bluetoothmode), "DEFAULT")
        if (preference_BluetoothMode == "Server") {
            serverbluetoothmode.isChecked = true
            clientbluetoothmode.isChecked = false
            defaultbluetoothmode.isChecked = false
        } else {
            if (preference_BluetoothMode == "Client") {
                serverbluetoothmode.isChecked = false
                clientbluetoothmode.isChecked = true
                defaultbluetoothmode.isChecked = false
            } else {
                serverbluetoothmode.isChecked = false
                clientbluetoothmode.isChecked = false
                defaultbluetoothmode.isChecked = true // default
            }
        }
        return rootView
    }
}