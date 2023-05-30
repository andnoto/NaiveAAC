package com.sampietro.NaiveAAC.activities.Main;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
// import android.util.Log;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.sampietro.NaiveAAC.R;
import com.sampietro.NaiveAAC.activities.Account.AccountActivity;
import com.sampietro.NaiveAAC.activities.Game.ChoiseOfGame.ChoiseOfGameActivity;
import com.sampietro.NaiveAAC.activities.Info.RequestConsentToTheProcessingOfPersonalDataActivity;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

/**
 * <h1>MainActivity</h1>
 * <p><b>MainActivity</b> represent start screen for the app.</p>
 * 1) on first use copy defaultrealm from assets
 * 2) check if the permission is granted
 * 3) Store information with SharedPreferences
 * 4) check if there is a player already registered
 */
public class MainActivity extends AppCompatActivity {

    public static final String EXTRA_MESSAGE ="helloworldandroidMessage" ;
    //
    public static final int ID_RICHIESTA_PERMISSION = 1;
    public String permessi = "permission not granted";
    //
    public Context context;
    public SharedPreferences sharedPref;
    //
    private final String TAGPERMISSION = "permission";
    /**
     * configurations of main start screen. Furthermore:
     * </p>
     * 1) on first use copy defaultrealm from assets
     * 2) check if the permission is granted
     * 3) Store information with SharedPreferences
     * 4) check if there is a player already registered
     * 5) if there is a player already registered go to choise of game activity.
     * <p>
     * Refer to <a href="https://stackoverflow.com/questions/24745546/android-change-activity-after-few-seconds">stackoverflow</a>
     * answer of <a href="https://stackoverflow.com/users/3586222/chefes">Chefes</a>
     *
     * @param savedInstanceState Define potentially saved parameters due to configurations changes.
     * @see #displayFileInAssets
     * @see #verifyLastPlayer
     * @see ChoiseOfGameActivity
     * @see android.app.Activity#onCreate(Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // debug get the current schema version of Realm
        // DynamicRealm dynRealm = DynamicRealm.getInstance(realmConfiguration);
        // long version = dynRealm.getVersion();//this will return the existing schema version
        // dynRealm.close();
        //
        // we check if the permission is granted
        //        isStoragePermissionGranted();
        // Store information with SharedPreferences
        context = this;
        sharedPref = context.getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        boolean hasLastSession = sharedPref.contains(getString(R.string.preference_LastSession));
        if (!hasLastSession) {
            // is the first session and register LastSession on sharedpref with the number 1
            editor.putInt(getString(R.string.preference_LastSession), 1);
            }
            else
            {
            // it's not the first session and I add 1 to LastSession on sharedprefs
            Integer sharedLastSession =
                        sharedPref.getInt (getString(R.string.preference_LastSession), 1);
            editor.putInt(getString(R.string.preference_LastSession), sharedLastSession+1);
            }
        editor.apply();
        //
        displayFileInAssets();
        //
        boolean hasConsentToTheProcessingOfPersonalData = verifyConsentToTheProcessingOfPersonalData();
        //
        if (hasConsentToTheProcessingOfPersonalData) {
            boolean hasLastPlayer = verifyLastPlayer();
            if (hasLastPlayer) {
                //
//                if (isStoragePermissionGranted()) {
                    // I move on to the activity of choice
                    // Time to launch the another activity
                    int TIME_OUT = 4000;
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            Intent i = new Intent(MainActivity.this,
                                    ChoiseOfGameActivity.class);
                            //
                            String message = "Bentornato " ;
                            i.putExtra(EXTRA_MESSAGE, message);
                            startActivity(i);
                            finish();
                        }
                    }, TIME_OUT);
//                }
            }
        }
    }
    @Override
    protected void onNewIntent(Intent intent)
    {
        super.onNewIntent(intent);
        displayFileInAssets();
    }
    /**
     * start screen
     * <p>
     * Refer to <a href="https://xjaphx.wordpress.com/2011/10/02/store-and-use-files-in-assets/">Pete Houston</a>
     * answer of <a href="https://stackoverflow.com/users/801396/pete-houston">Pete Houston</a>
     *
     */
    public void displayFileInAssets() {
            try
            {
                // get input stream
                InputStream ims = context.getAssets().open("images/naiveaac.png");
                // load image as Drawable
                Drawable d = Drawable.createFromStream(ims, null);
                // set image to ImageView
                ImageView myImage = (ImageView) findViewById(R.id.imageviewTest);
                myImage.setImageDrawable(d);
                ims .close();
            }
            catch(IOException ex)
            {
            return;
            }
        }
    /**
     * if there is no consent to the processing of personal data already registered, go to the request consent to the processing of personal data activity.
     *
     * @return boolean whit true if the processing of personal data is granted
     * @see RequestConsentToTheProcessingOfPersonalDataActivity
     */
    public  boolean verifyConsentToTheProcessingOfPersonalData () {
        boolean hasConsentToTheProcessingOfPersonalData = sharedPref.contains(getString(R.string.preference_ConsentToTheProcessingOfPersonalData));
        String sharedConsentToTheProcessingOfPersonalData = "N";
        if (hasConsentToTheProcessingOfPersonalData) {
            sharedConsentToTheProcessingOfPersonalData =
                    sharedPref.getString (getString(R.string.preference_ConsentToTheProcessingOfPersonalData), "N");
        }
        if (Objects.equals(sharedConsentToTheProcessingOfPersonalData, "N")) {
            // go to the request consent to the processing of personal data activity
            Intent intent = new Intent(this, RequestConsentToTheProcessingOfPersonalDataActivity.class);
            //
            startActivity(intent);
            return false;
            }
            else
            {
            return true;
            }
    }
    //
    /**
     * if there is no player already registered, go to the user registration activity.
     *
     * @return boolean whit true if has last player
     * @see AccountActivity
     */
    public  boolean verifyLastPlayer () {
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