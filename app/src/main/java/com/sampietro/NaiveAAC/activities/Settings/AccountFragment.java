package com.sampietro.NaiveAAC.activities.Settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.sampietro.NaiveAAC.R;
import com.sampietro.NaiveAAC.activities.Account.AccountActivity;
import com.sampietro.NaiveAAC.activities.Settings.Utils.SettingsFragmentAbstractClass;

/**
 * <h1>AccountFragment</h1>
 * <p><b>AccountFragment</b> UI to register the user
 * </p>
 *
 * @version     1.1, 04/22/22
 * @see SettingsFragmentAbstractClass
 * @see SettingsActivity
 * @see AccountActivity
 */
public class AccountFragment extends SettingsFragmentAbstractClass {

    public SharedPreferences sharedPref;
    /**
     * prepares the ui and makes the callback to the activity
     *
     * @see androidx.fragment.app.Fragment#onCreateView
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        rootView = inflater.inflate(R.layout.activity_settings_account, container, false);
        // logic of fragment
        sharedPref = ctext.getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        String preference_LastPlayer =
                sharedPref.getString (getString(R.string.preference_LastPlayer), "");
        EditText editTextTextAccount = (EditText)rootView.findViewById(R.id.editTextTextAccount);
        editTextTextAccount.setText(preference_LastPlayer);
        //
        listener.receiveResultSettings(rootView);
        //
        return rootView;
    }
}

