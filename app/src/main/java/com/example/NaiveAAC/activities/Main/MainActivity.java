package com.example.NaiveAAC.activities.Main;

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

import com.example.NaiveAAC.R;
import com.example.NaiveAAC.activities.Account.AccountActivity;
import com.example.NaiveAAC.activities.Game.ChoiseOfGame.ChoiseOfGameActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import io.realm.Realm;
import io.realm.RealmConfiguration;

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
     * @see #copyFileRealm
     * @see #isStoragePermissionGranted
     * @see #displayFileInAssets
     * @see #verifyLastPlayer
     * @see ChoiseOfGameActivity
     * @see android.app.Activity#onCreate(Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //
        File fileRealm = new File(this.getFilesDir(), "default.realm");
        if (!fileRealm.exists()) {
            try {
                copyFileRealm();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //
        Realm.init(this);
        RealmConfiguration realmConfiguration =
                new RealmConfiguration.Builder()
                        .deleteRealmIfMigrationNeeded()
                        .build();
        Realm.setDefaultConfiguration(realmConfiguration);
        // we check if the permission is granted
        isStoragePermissionGranted();
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
        verifyLastPlayer();
        //
        if (isStoragePermissionGranted()) {
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
        }
    }
    /**
     * Callback for the result from requesting permissions.
     *
     * @param requestCode int which represents the request code passed in requestPermissions
     * @param permissions string which represents the request permissions
     * @param grantResults int which represents the grant results for the corresponding permissions
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        permessi = getString(R.string.permission_not_granted);
        if (requestCode == ID_RICHIESTA_PERMISSION) {
            // Request for camera permission.
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//
                permessi = getString(R.string.permission_is_granted);
            }
        }
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
     * if there is no player already registered, go to the user registration activity.
     *
     * @see AccountActivity
     */
    public void verifyLastPlayer () {
        boolean hasLastPlayer = sharedPref.contains(getString(R.string.preference_LastPlayer));
        if (!hasLastPlayer) {
            // go to the user registration activity
            Intent intent = new Intent(this, AccountActivity.class);
            //
            String message = "enter username";
            //
            intent.putExtra(EXTRA_MESSAGE, message);
            startActivity(intent);

            }
    }
    //
       /**
         * check permissions.
         *
         * @return boolean whit true if permission is granted
         */
        public  boolean isStoragePermissionGranted() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_GRANTED) {
                    // Log.v(TAGPERMISSION,getString(R.string.permission_is_granted));
                    return true;
                } else {

                    // Log.v(TAGPERMISSION,getString(R.string.permission_is_revoked));
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                    return false;
                }
            }
            else { //permission is automatically granted on sdk<23 upon installation
                // Log.v(TAGPERMISSION,getString(R.string.permission_is_granted));
                return true;
            }
        }
        //
        /**
         * copy defaultrealm from assets
         */
        public void copyFileRealm() throws IOException {
            InputStream sourceStream = getAssets().open("copiarealm");
            FileOutputStream destStream = openFileOutput("default.realm", Context.MODE_PRIVATE);
            byte[] buffer = new byte[1024];
            int read = sourceStream.read(buffer);
            while (read != -1) {
                destStream.write(buffer, 0, read);
                read = sourceStream.read(buffer);
            }
            destStream.close();
            sourceStream.close();
            //
        }

}