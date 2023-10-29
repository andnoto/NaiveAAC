package com.sampietro.NaiveAAC.activities.Main

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.content.SharedPreferences
import android.os.Bundle
import com.sampietro.NaiveAAC.R
import android.content.Intent
import com.sampietro.NaiveAAC.activities.Game.ChoiseOfGame.ChoiseOfGameActivity
import android.graphics.drawable.Drawable
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ImageView
import com.sampietro.NaiveAAC.activities.Info.RequestConsentToTheProcessingOfPersonalDataActivity
import com.sampietro.NaiveAAC.activities.Account.AccountActivity
import java.io.IOException

// import android.util.Log;
/**
 * <h1>MainActivity</h1>
 *
 * **MainActivity** represent start screen for the app.
 * 1) on first use copy defaultrealm from assets
 * 2) check if the permission is granted
 * 3) Store information with SharedPreferences
 * 4) check if there is a player already registered
 */
class MainActivity : AppCompatActivity() {
//    var permessi = "permission not granted"

    //
    lateinit var context: Context
    lateinit var sharedPref: SharedPreferences

    //
//    private val TAGPERMISSION = "permission"

    /**
     * configurations of main start screen. Furthermore:
     *
     * 1) on first use copy defaultrealm from assets
     * 2) check if the permission is granted
     * 3) Store information with SharedPreferences
     * 4) check if there is a player already registered
     * 5) if there is a player already registered go to choise of game activity.
     *
     *
     * Refer to [stackoverflow](https://stackoverflow.com/questions/24745546/android-change-activity-after-few-seconds)
     * answer of [Chefes](https://stackoverflow.com/users/3586222/chefes)
     *
     * @param savedInstanceState Define potentially saved parameters due to configurations changes.
     * @see displayFileInAssets
     *
     * @see verifyLastPlayer
     *
     * @see ChoiseOfGameActivity
     *
     * @see android.app.Activity.onCreate
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // debug get the current schema version of Realm
        // DynamicRealm dynRealm = DynamicRealm.getInstance(realmConfiguration);
        // long version = dynRealm.getVersion();//this will return the existing schema version
        // dynRealm.close();
        //
        // we check if the permission is granted
        //        isStoragePermissionGranted();
        // Store information with SharedPreferences
        context = this
        sharedPref = context.getSharedPreferences(
            getString(R.string.preference_file_key), MODE_PRIVATE
        )
        val editor = sharedPref.edit()
        val hasLastSession = sharedPref.contains(getString(R.string.preference_LastSession))
        if (!hasLastSession) {
            // is the first session and register LastSession on sharedpref with the number 1
            editor.putInt(getString(R.string.preference_LastSession), 1)
        } else {
            // it's not the first session and I add 1 to LastSession on sharedprefs
            val sharedLastSession = sharedPref.getInt(getString(R.string.preference_LastSession), 1)
            editor.putInt(getString(R.string.preference_LastSession), sharedLastSession + 1)
        }
        editor.apply()
        //
        displayFileInAssets()
        //
        val hasConsentToTheProcessingOfPersonalData = verifyConsentToTheProcessingOfPersonalData()
        //
        if (hasConsentToTheProcessingOfPersonalData) {
            val hasLastPlayer = verifyLastPlayer()
            if (hasLastPlayer) {
                //
//                if (isStoragePermissionGranted()) {
                // I move on to the activity of choice
                // Time to launch the another activity
                val TIME_OUT = 4000
//                Handler().postDelayed({
                Handler(Looper.getMainLooper()).postDelayed({
                    val i = Intent(
                        this@MainActivity,
                        ChoiseOfGameActivity::class.java
                    )
                    //
                    val message = "Bentornato "
                    i.putExtra(EXTRA_MESSAGE, message)
                    startActivity(i)
                    finish()
                }, TIME_OUT.toLong())
                //                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        displayFileInAssets()
    }

    /**
     * start screen
     *
     *
     * Refer to [Pete Houston](https://xjaphx.wordpress.com/2011/10/02/store-and-use-files-in-assets/)
     * answer of [Pete Houston](https://stackoverflow.com/users/801396/pete-houston)
     *
     */
    fun displayFileInAssets() {
        try {
            // get input stream
            val ims = context.assets.open("images/naiveaac.png")
            // load image as Drawable
            val d = Drawable.createFromStream(ims, null)
            // set image to ImageView
            val myImage = findViewById<View>(R.id.imageviewTest) as ImageView
            myImage.setImageDrawable(d)
            ims.close()
        } catch (ex: IOException) {
            return
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

        //
//        const val ID_RICHIESTA_PERMISSION = 1
    }
}