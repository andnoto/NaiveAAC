package com.sampietro.NaiveAAC.activities.Info;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.sampietro.NaiveAAC.R;
import com.sampietro.NaiveAAC.activities.Account.AccountActivity;
import com.sampietro.NaiveAAC.activities.Game.ChoiseOfGame.ChoiseOfGameActivity;
import com.sampietro.NaiveAAC.activities.Settings.Utils.AccountActivityAbstractClass;
import com.sampietro.NaiveAAC.activities.Settings.Utils.SettingsFragmentAbstractClass;

/**
 * <h1>RequestConsentFirebaseActivity</h1>
 * <p><b>RequestConsentFirebaseActivity</b> requires consent Firebase Analytics and Crashlytics</p>
 *
 * @version     3.0, 03/12/23
 * @see AccountActivityAbstractClass
 * @see SettingsFragmentAbstractClass
 */
public class RequestConsentFirebaseActivity extends AppCompatActivity {
    public static final String EXTRA_MESSAGE ="helloworldandroidMessage" ;
    //
    public View rootViewFragment;
    public Context context;
    public SharedPreferences sharedPref;
    //
    public FragmentManager fragmentManager;
    //
    /**
     * configurations of account start screen.
     *
     * @param savedInstanceState Define potentially saved parameters due to configurations changes.
     * @see RequestConsentFirebaseFragment
     * @see android.app.Activity#onCreate(Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        //
        context = this;
        sharedPref = context.getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        //
        if (savedInstanceState == null)
        {
            fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .add(R.id.game_container, new RequestConsentFirebaseFragment(), "RequestConsentFirebaseFragment")
                    .commit();
        }
        //
    }
    /**
     * Called when the user taps the consent Firebase button.
     * </p>
     *
     * @param view view of tapped button
     */
    public void iAccept(View view) {
        //
        // consent firebase
        FirebaseAnalytics.getInstance(this).setAnalyticsCollectionEnabled(true);
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true);
        //
        boolean hasLastPlayer = verifyLastPlayer();
        if (hasLastPlayer) {
            // I move on to the activity of choice
            // Time to launch the another activity
            int TIME_OUT = 4000;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    Intent i = new Intent(context,
                            ChoiseOfGameActivity.class);
                    //
                    String message = "Bentornato " ;
                    i.putExtra(EXTRA_MESSAGE, message);
                    startActivity(i);
                    finish();
                }
            }, TIME_OUT);
        }
    }
    /**
     * Called when the user taps the i do not consent Firebase button.
     * </p>
     *
     * @param view view of tapped button
     */
    public void iDoNotAccept(View view) {
        //
        boolean hasLastPlayer = verifyLastPlayer();
        if (hasLastPlayer) {
            // I move on to the activity of choice
            // Time to launch the another activity
            int TIME_OUT = 4000;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    Intent i = new Intent(context,
                            ChoiseOfGameActivity.class);
                    //
                    String message = "Bentornato " ;
                    i.putExtra(EXTRA_MESSAGE, message);
                    startActivity(i);
                    finish();
                }
            }, TIME_OUT);
        }
    }
    //
    /**
     * if there is no player already registered, go to the user registration activity.
     *
     * @return boolean whit true if has last player
     * @see AccountActivity
     */
    public  boolean verifyLastPlayer() {
        boolean hasLastPlayer = sharedPref.contains(getString(R.string.preference_LastPlayer));
        if (!hasLastPlayer) {
            // go to the user registration activity
            Intent intent = new Intent(this, AccountActivity.class);
            //
            String message = "enter username";
            //
            intent.putExtra(EXTRA_MESSAGE, message);
            startActivity(intent);
            return false;
        }
        else
        {
            return true;
        }
    }
}
