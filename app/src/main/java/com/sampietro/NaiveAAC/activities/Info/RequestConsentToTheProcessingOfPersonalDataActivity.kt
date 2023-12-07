package com.sampietro.NaiveAAC.activities.Info

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.content.SharedPreferences
import android.os.Bundle
import com.sampietro.NaiveAAC.R
import android.content.Intent
import android.view.View
import androidx.fragment.app.FragmentManager

/**
 * <h1>RequestConsentToTheProcessingOfPersonalDataActivity</h1>
 *
 * **RequestConsentToTheProcessingOfPersonalDataActivity** requires consent to the processing of personal data
 *
 * @version     4.0, 09/09/2023
 */
class RequestConsentToTheProcessingOfPersonalDataActivity : AppCompatActivity() {
    //
    var rootViewFragment: View? = null
    lateinit var context: Context
    lateinit var sharedPref: SharedPreferences

    //
    var fragmentManager: FragmentManager? = null
    //
    /**
     * configurations of account start screen.
     *
     * @param savedInstanceState Define potentially saved parameters due to configurations changes.
     * @see RequestConsentToTheProcessingOfPersonalDataFragment
     *
     * @see android.app.Activity.onCreate
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)
        //
        context = this
        //
        if (savedInstanceState == null) {
            fragmentManager = supportFragmentManager
            fragmentManager!!.beginTransaction()
                .add(
                    R.id.game_container,
                    RequestConsentToTheProcessingOfPersonalDataFragment(),
                    "RequestConsentToTheProcessingOfPersonalDataFragment"
                )
                .commit()
        }
        //
    }

    /**
     * Called when the user taps "Information on the processing of personal data" text.
     *
     *
     * @param view view of tapped text
     */
    fun onClickInformativa(view: View?) {
        val frag = InformativaFragment()
        val ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.game_container, frag, "InformativaFragment")
        ft.addToBackStack(null)
        ft.commit()
    }

    /**
     * Called when the user taps "Information on the processing of personal data" text.
     *
     *
     * @param view view of tapped text
     */
    fun onClickTermini(view: View?) {
        val frag = TerminiFragment()
        val ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.game_container, frag, "TerminiFragment")
        ft.addToBackStack(null)
        ft.commit()
    }

    /**
     * Called when the user taps the consent to the processing of personal data button.
     *
     *
     * @param view view of tapped button
     */
    fun iAccept(view: View?) {
        //
        // registers consent to the processing of personal data
        sharedPref = getSharedPreferences(
            getString(R.string.preference_file_key), MODE_PRIVATE
        )
        val editor = sharedPref.edit()
        editor.putString(
            getString(R.string.preference_ConsentToTheProcessingOfPersonalData),
            getString(R.string.character_y)
        )
        editor.apply()
        //
        val i = Intent(
            context,
            RequestConsentFirebaseActivity::class.java
        )
        startActivity(i)
    }

    /**
     * Called when the user taps the i do not accept the processing of personal data button.
     *
     *
     * @param view view of tapped button
     */
    fun iDoNotAccept(view: View?) {
        //
        // does not allow access to the app
        finishAndRemoveTask()
    }

    companion object {
//        const val EXTRA_MESSAGE = "helloworldandroidMessage"
    }
}