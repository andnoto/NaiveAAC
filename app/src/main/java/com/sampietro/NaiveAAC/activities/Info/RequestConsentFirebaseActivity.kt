package com.sampietro.NaiveAAC.activities.Info

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.content.SharedPreferences
import android.os.Bundle
import com.sampietro.NaiveAAC.R
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.sampietro.NaiveAAC.activities.Game.ChoiseOfGame.ChoiseOfGameActivity
import com.sampietro.NaiveAAC.activities.Account.AccountActivity

/**
 * <h1>RequestConsentFirebaseActivity</h1>
 *
 * **RequestConsentFirebaseActivity** requires consent Firebase Analytics and Crashlytics
 *
 * @version     5.0, 01/04/2024
 */
class RequestConsentFirebaseActivity : AppCompatActivity() {
    //
//    var rootViewFragment: View? = null
    lateinit var context: Context
    lateinit var sharedPref: SharedPreferences

    //
    lateinit var fragmentManager: FragmentManager
    //
    /**
     * configurations of account start screen.
     *
     * @param savedInstanceState Define potentially saved parameters due to configurations changes.
     * @see RequestConsentFirebaseFragment
     *
     * @see android.app.Activity.onCreate
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)
        //
        context = this
        sharedPref = getSharedPreferences(
            getString(R.string.preference_file_key), MODE_PRIVATE
        )
        //
        if (savedInstanceState == null) {
            fragmentManager = supportFragmentManager
            fragmentManager.beginTransaction()
                .add(
                    R.id.game_container,
                    Fragment(R.layout.activity_requestconsentfirebase),
                    "RequestConsentFirebaseFragment"
                )
                .commit()
        }
        //
    }

    /**
     * Called when the user taps the consent Firebase button.
     *
     *
     * @param view view of tapped button
     */
    fun iAccept(view: View?) {
        //
        // consent firebase
        FirebaseAnalytics.getInstance(this).setAnalyticsCollectionEnabled(true)
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true)
        //
        val hasLastPlayer = verifyLastPlayer()
        if (hasLastPlayer) {
            // I move on to the activity of choice
            // Time to launch the another activity
            val TIME_OUT = 4000
            Handler(Looper.getMainLooper()).postDelayed({
                val i = Intent(
                    context,
                    ChoiseOfGameActivity::class.java
                )
                //
                val message = getString(R.string.bentornato)
                i.putExtra(EXTRA_MESSAGE, message)
                startActivity(i)
                finish()
            }, TIME_OUT.toLong())
        }
    }

    /**
     * Called when the user taps the i do not consent Firebase button.
     *
     *
     * @param view view of tapped button
     */
    fun iDoNotAccept(view: View?) {
        //
        val hasLastPlayer = verifyLastPlayer()
        if (hasLastPlayer) {
            // I move on to the activity of choice
            // Time to launch the another activity
            val TIME_OUT = 4000
            Handler(Looper.getMainLooper()).postDelayed({
                val i = Intent(
                    context,
                    ChoiseOfGameActivity::class.java
                )
                //
                val message = getString(R.string.bentornato)
                i.putExtra(EXTRA_MESSAGE, message)
                startActivity(i)
                finish()
            }, TIME_OUT.toLong())
        }
    }
    //
    /**
     * if there is no player already registered, go to the user registration activity.
     *
     * @return boolean whit true if has last player
     * @see AccountActivity
     */
    fun verifyLastPlayer(): Boolean {
        val hasLastPlayer = sharedPref.contains(getString(R.string.preference_LastPlayer))
        return if (!hasLastPlayer) {
            // go to the user registration activity
            val intent = Intent(this, AccountActivity::class.java)
            //
            val message = "enter username"
            //
            intent.putExtra(EXTRA_MESSAGE, message)
            startActivity(intent)
            false
        } else {
            true
        }
    }

    companion object {
        const val EXTRA_MESSAGE = "helloworldandroidMessage"
    }
}