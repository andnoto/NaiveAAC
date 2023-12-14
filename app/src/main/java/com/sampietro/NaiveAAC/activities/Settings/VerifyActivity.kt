package com.sampietro.NaiveAAC.activities.Settings

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import com.sampietro.NaiveAAC.activities.Settings.VerifyFragment.onFragmentEventListenerVerify
import android.content.SharedPreferences
import android.view.ViewGroup
import android.os.Bundle
import com.sampietro.NaiveAAC.R
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.content.Intent
import android.view.View
import com.sampietro.NaiveAAC.activities.Game.ChoiseOfGame.ChoiseOfGameActivity
import android.widget.EditText
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.FragmentManager
import java.lang.NumberFormatException

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
 * @version     4.0, 09/09/2023
 * @see com.sampietro.NaiveAAC.activities.Account.AccountActivity
 *
 * @see com.sampietro.NaiveAAC.activities.Account.AccountActivityRealmCreation
 *
 * @see SettingsActivity
 */
class VerifyActivity : AppCompatActivity(), onFragmentEventListenerVerify {
    //
    var rootViewVerifyFragment: View? = null
    var resultToVerify = 0

    //
    lateinit var context: Context
    lateinit var sharedPref: SharedPreferences
    var sharedPassword: String? = null

    //
    var fragmentManager: FragmentManager? = null

    //
//    private var mContentView: ViewGroup? = null

    /**
     * configurations of settings start screen.
     *
     * @param savedInstanceState Define potentially saved parameters due to configurations changes.
     * // * @see ActionbarFragment
     * @see VerifyFragment
     *
     * @see androidx.appcompat.app.AppCompatActivity.onCreate
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        //
        if (supportActionBar != null) {
            supportActionBar!!.hide()
        }
        /*
        USED FOR FULL SCREEN
        */
//        val mContentView:ViewGroup = findViewById(R.id.activity_settings_id)
        setToFullScreen()
//        val viewTreeObserver = mContentView.getViewTreeObserver()
        //
//        if (viewTreeObserver.isAlive) {
//            viewTreeObserver.addOnGlobalLayoutListener(object : OnGlobalLayoutListener {
//                override fun onGlobalLayout() {
//                    mContentView.getViewTreeObserver().removeOnGlobalLayoutListener(this)
//                }
//            })
//        }
        //
//        mContentView.setOnClickListener(View.OnClickListener { view: View? -> setToFullScreen() })
        /*

         */
        context = this
        //
        if (savedInstanceState == null) {
            sharedPref = getSharedPreferences(
                getString(R.string.preference_file_key), MODE_PRIVATE
            )
            sharedPassword = sharedPref.getString("password", "nessuna password")
            if (sharedPassword == "nessuna password") {
                fragmentManager = supportFragmentManager
                fragmentManager!!.beginTransaction()
                    .add(R.id.settings_container, VerifyFragment(), "VerifyFragment")
                    .commit()
            } else {
                fragmentManager = supportFragmentManager
                fragmentManager!!.beginTransaction()
                    .add(
                        R.id.settings_container,
                        VerifyPasswordFragment(),
                        "VerifyPasswordFragment"
                    )
                    .commit()
            }
        }
    }

    /**
     * Hide the Navigation Bar
     *
     * @see androidx.appcompat.app.AppCompatActivity.onResume
     */
    override fun onResume() {
        super.onResume()
        setToFullScreen()
    }

    /**
     * This method is responsible to transfer MainActivity into fullscreen mode.
     */
    private fun setToFullScreen() {
        WindowCompat.setDecorFitsSystemWindows(window, false)

        val insetsController = WindowCompat.getInsetsController(window, window.decorView)
        insetsController.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        insetsController.hide(WindowInsetsCompat.Type.statusBars())
        insetsController.hide(WindowInsetsCompat.Type.navigationBars())
//        findViewById<View>(R.id.activity_settings_id).systemUiVisibility =
//            View.SYSTEM_UI_FLAG_LOW_PROFILE or View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
    }
    //
    /**
     * Called when the user taps the home button.
     *
     *
     * @param v view of tapped button
     * @see ChoiseOfGameActivity
     */
    fun returnHome(v: View?) {
        /*
                navigate to home screen (ChoiseOfGameActivity)
    */
        val intent = Intent(this, ChoiseOfGameActivity::class.java)
        startActivity(intent)
    }
    //
    /**
     * receive result to verify for check the right to access the settings.
     *
     * @param v view of calling fragment
     * @param r int with result to verify
     * @see VerifyFragment
     */
    override fun receiveResultToVerify(v: View?, r: Int) {
        rootViewVerifyFragment = v
        resultToVerify = r
    }

    /**
     * Called when the user taps the submit verification button.
     *
     * check and if the answer is correct the activity is notified to view the fragment settings.
     *
     *
     * @param view view of tapped button
     * @see VerifyFragment
     *
     * @see .receiveResultToVerify
     *
     * @see MenuSettingsFragment
     */
    fun submitVerification(view: View?) {
        val editText =
            rootViewVerifyFragment!!.findViewById<View>(R.id.calculationToBeVerified) as EditText
        val value = editText.text.toString()
        var calculationToBeVerified = 99
        try {
            calculationToBeVerified = value.toInt()
        } catch (nfe: NumberFormatException) {
            println("Could not parse $nfe")
        }
        if (calculationToBeVerified == resultToVerify) {
            /*
                navigate to settings screen
                */
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }
    }

    /**
     * Called when the user taps the submit verification password button.
     *
     * check and if the answer is correct the activity is notified to view the fragment settings.
     *
     *
     * @param view view of tapped button
     * @see VerifyPasswordFragment
     *
     * @see MenuSettingsFragment
     */
    fun submitVerificationPassword(view: View?) {
        val passwordToBeVerifiedET = findViewById<View>(R.id.passwordToBeVerified) as EditText
        val passwordToBeVerified = passwordToBeVerifiedET.text.toString()
        if (passwordToBeVerified == sharedPassword) {
            /*
                navigate to settings screen
                */
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }
    }
}