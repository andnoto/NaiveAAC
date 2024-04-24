package com.sampietro.NaiveAAC.activities.BaseAndAbstractClass

import androidx.appcompat.app.AppCompatActivity
import android.content.SharedPreferences
import android.app.Activity
import android.content.Context
import io.realm.Realm

/**
 * <h1>ActivityAbstractClass</h1>
 *
 * **ActivityAbstractClass**
 * class containing common methods that is extended by
 *
 *
 * @version     5.0, 01/04/2024
 * @see com.sampietro.NaiveAAC.activities.Account.AccountActivity
 *
 * @see com.sampietro.NaiveAAC.activities.Account.AccountActivityRealmCreation
 *
 * @see com.sampietro.NaiveAAC.activities.Settings.SettingsActivity
 */
abstract class ActivityAbstractClass : AppCompatActivity() {
    lateinit var context: Context
    lateinit var sharedPref: SharedPreferences
    // Realm
    lateinit var realm: Realm


    /**
     * closing realm to be performed on destroy activity
     *
     * @see Activity.onDestroy
     */
    override fun onDestroy() {
        super.onDestroy()
            realm.close()
    }

}