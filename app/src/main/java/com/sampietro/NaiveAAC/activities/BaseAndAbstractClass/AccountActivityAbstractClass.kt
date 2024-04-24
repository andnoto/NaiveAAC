package com.sampietro.NaiveAAC.activities.BaseAndAbstractClass

import com.sampietro.NaiveAAC.R

/**
 * <h1>AccountActivityAbstractClass</h1>
 *
 * **AccountActivityAbstractClass**
 * abstract class containing common methods that is extended by
 *
 * 1) AccountActivity
 * 2) AccountActivityRealmCreation
 * 3) SettingsActivity
 *
 * @version     5.0, 01/04/2024
 * @see com.sampietro.NaiveAAC.activities.Account.AccountActivity
 *
 * @see com.sampietro.NaiveAAC.activities.Account.AccountActivityRealmCreation
 *
 * @see com.sampietro.NaiveAAC.activities.Settings.SettingsActivity
 */
abstract class AccountActivityAbstractClass : ActivityAbstractClass() {
    /**
     * register the last player name on shared preferences.
     *
     * @param textPersonName string containing the player's name
     */
    fun registerPersonName(textPersonName: String?) {
        //    context = this;
        sharedPref = getSharedPreferences(
            getString(R.string.preference_file_key), MODE_PRIVATE
        )
        val editor = sharedPref.edit()
        editor.putString(getString(R.string.preference_LastPlayer), textPersonName)
        editor.apply()
    }

    /**
     * register the last player password on shared preferences.
     *
     * @param textPassword string containing the player's password
     */
    fun registerPassword(textPassword: String?) {
        //    context = this;
        sharedPref = getSharedPreferences(
            getString(R.string.preference_file_key), MODE_PRIVATE
        )
        val editor = sharedPref.edit()
        editor.putString("password", textPassword)
        editor.apply()
    }
}