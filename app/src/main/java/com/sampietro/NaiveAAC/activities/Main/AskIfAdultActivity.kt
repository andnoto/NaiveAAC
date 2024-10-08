package com.sampietro.NaiveAAC.activities.Main

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import com.sampietro.NaiveAAC.R
import com.sampietro.NaiveAAC.activities.Account.AccountActivity
import com.sampietro.NaiveAAC.activities.Info.RequestConsentToTheProcessingOfPersonalDataActivity
import java.util.Calendar

/**
 * <h1>AskIfAdultActivity</h1>
 *
 * **AskIfAdultActivity** ask if the user is an adult
 *
 * @version     5.0, 01/04/2024
 */
class AskIfAdultActivity : AppCompatActivity() {
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
     *
     * @see android.app.Activity.onCreate
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)
        //
        context = this
        //
        sharedPref = context.getSharedPreferences(
            getString(R.string.preference_file_key), MODE_PRIVATE
        )
        //
        if (savedInstanceState == null) {
            fragmentManager = supportFragmentManager
            fragmentManager!!.beginTransaction()
                .add(
                    R.id.game_container,
                    AskIfAdultFragment(),
                    "AskIfAdultFragment"
                )
                .commit()
        }
        //
    }
    /**
     * Called when the user taps "I'm a child" text.
     *
     *
     * @param view view of tapped text
     */
    fun onClickIAmAChild(view: View?) {
        val frag = AskAParentFragment()
        val ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.game_container, frag, "AskAParentFragment")
        ft.addToBackStack(null)
        ft.commit()
    }
    /**
     * Called when the user taps "I'm a parent" text.
     *
     *
     * @param view view of tapped text
     */
    fun onClickIAmAParent(view: View?) {
        val frag = AskIfAdultVerifyDateOfBirthFragment()
        val ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.game_container, frag, "VerifyDateOfBirthFragment")
        ft.addToBackStack(null)
        ft.commit()
    }
    /**
     * Called when the user taps "Ask a parent" button.
     *
     *
     * @param view view of tapped text
     */
    fun onClickAskAParent(view: View?) {
        val frag = AskIfAdultFragment()
        val ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.game_container, frag, "AskIfAdultFragment")
        ft.addToBackStack(null)
        ft.commit()
    }
    /**
     * Called when the user sends the date of birth.
     *
     *
     * @param view view of tapped text
     */
    fun submitVerificationDateOfBirth(view: View?) {
        val editText =
            findViewById<View>(R.id.dateofbirthToBeVerified) as EditText
        val value = editText.text.toString()
        var yearofbirthToBeVerified = 99
        try {
            yearofbirthToBeVerified = value.toInt()
        } catch (nfe: NumberFormatException) {
            println("Could not parse $nfe")
        }
        // controllo età
        val yearToday= Calendar.getInstance().get(Calendar.YEAR)
        if ((yearToday - yearofbirthToBeVerified) > 18) {
            //
            val d = Dialog(this)
            // Setting dialogview
            val window = d.window
            val wlp = window!!.attributes
            wlp.gravity = Gravity.BOTTOM
            wlp.flags = wlp.flags and WindowManager.LayoutParams.FLAG_DIM_BEHIND.inv()
            window.attributes = wlp
            //
            d.setCancelable(false)
            d.setContentView(R.layout.activity_checkifadult_write_dialog)
            //
            val dialogTitle =
                d.findViewById<TextView>(R.id.verifydateofbirthTV)
            dialogTitle.setText("CONFERMA L'ETA' DEL GENITORE PER CONTINUARE\n\nHai detto che il tuo anno di nascita è il " + value + ", è corretto?")
            val submitResponseOrRequestButton =
                d.findViewById<ImageButton>(R.id.submitDateOfBirthButton)
            val cancelTheAnswerOrRequestButton =
                d.findViewById<ImageButton>(R.id.cancelDateOfBirthButton)
            //
            submitResponseOrRequestButton.requestFocus()
            //
            submitResponseOrRequestButton.setOnClickListener { //
                //
                d.cancel()
                //
                val hasConsentToTheProcessingOfPersonalData = verifyConsentToTheProcessingOfPersonalData()
                //
                if (hasConsentToTheProcessingOfPersonalData) {
                    // go to the user registration activity
                    val intent = Intent(this, AccountActivity::class.java)
                    //
                    val message = "enter username"
                    //
                    intent.putExtra(MainActivity.EXTRA_MESSAGE, message)
                    startActivity(intent)
                }
            }
            cancelTheAnswerOrRequestButton.setOnClickListener { //
                d.cancel()
                //
            }
            //
            d.show()
        }

    }

    /**
     * if there is no consent to the processing of personal data already registered, go to the request consent to the processing of personal data activity.
     *
     * @return boolean whit true if the processing of personal data is granted
     * @see RequestConsentToTheProcessingOfPersonalDataActivity
     */
    fun verifyConsentToTheProcessingOfPersonalData(): Boolean {
        val hasConsentToTheProcessingOfPersonalData =
            sharedPref.contains(getString(R.string.preference_ConsentToTheProcessingOfPersonalData))
        var sharedConsentToTheProcessingOfPersonalData: String? = "N"
        if (hasConsentToTheProcessingOfPersonalData) {
            sharedConsentToTheProcessingOfPersonalData = sharedPref.getString(
                getString(R.string.preference_ConsentToTheProcessingOfPersonalData),
                "N"
            )
        }
        return if (sharedConsentToTheProcessingOfPersonalData == "N") {
            // go to the request consent to the processing of personal data activity
            val intent =
                Intent(this, RequestConsentToTheProcessingOfPersonalDataActivity::class.java)
            //
            startActivity(intent)
            false
        } else {
            true
        }
    }

    companion object {
    }
}