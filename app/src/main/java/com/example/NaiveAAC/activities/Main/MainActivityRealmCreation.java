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
import androidx.core.content.ContextCompat;

import com.example.NaiveAAC.R;
import com.example.NaiveAAC.activities.Account.AccountActivityRealmCreation;
import com.example.NaiveAAC.activities.Game.ChoiseOfGame.ChoiseOfGameActivity;

import java.io.IOException;
import java.io.InputStream;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class MainActivityRealmCreation extends AppCompatActivity {

    public static final String EXTRA_MESSAGE ="helloworldandroidMessage" ;

    // SIMSIM
    //
    public static final int ID_RICHIESTA_PERMISSION = 1;
    public String permessi = "permessi non pervenuti";

    //
    public Context context;
    public SharedPreferences sharedPref;
    //

    private static final String TAGPERMISSION = "Permission";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        //
        Realm.init(this);
        RealmConfiguration realmConfiguration =
                new RealmConfiguration.Builder()
                        .deleteRealmIfMigrationNeeded()
                        .build();
        Realm.setDefaultConfiguration(realmConfiguration);
        //
        // SIM SIM
        // controlliamo se la permission è concessa
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // se arriviamo qui è perchè la permission non è stata ancora concessa
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                // mostriamo ulteriori informazioni all'utente riguardante l'uso della permission nell'app ed eventualmente richiediamo la permission
            } else {
                // se siamo qui è perchè non si è mostrata alcuna spiegazione all'utente, richiesta di permission
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, ID_RICHIESTA_PERMISSION);
            }
        }
        //
        //
        // vedi cap 29 Memorizzare informazioni con SharedPreferences
        context = this;
        sharedPref = context.getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        boolean hasLastSession = sharedPref.contains("preference_LastSession");
        if (!hasLastSession) {
            // è la prima sessione e registro LastSession sulle sharedpref con il numero 1
            editor.putInt("preference_LastSession", 1);
            }
            else
            {
            // non è la prima sessione e sommo 1 a LastSession sulle sharedpref
            Integer sharedLastSession =
                        sharedPref.getInt ("preference_LastSession", 1);
            editor.putInt("preference_LastSession", sharedLastSession+1);
            }
        editor.apply();
        //
        displayFileInAssets();
        verifyLastPlayer();
        //
        if (isStoragePermissionGranted()) {
            // passo all'activity di benvenuto
            //
            //Time to launch the another activity
            // fonte https://stackoverflow.com/questions/24745546/android-change-activity-after-few-seconds
            int TIME_OUT = 4000;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    Intent i = new Intent(MainActivityRealmCreation.this,
//                            GameActivityChoiseOfGame.class);
//                            GameActivityChoiseOfGameVideo.class);
//                            GameActivityChoiseOfGameExoplayer.class);
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
    // SIMSIM
    //
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        permessi = "permessi in verifica";
        if (requestCode == ID_RICHIESTA_PERMISSION) {
            // Request for camera permission.
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//        switch (requestCode) {
//            case ID_RICHIESTA_PERMISSION: {
//                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // permission concessa: eseguiamo il codice
                permessi = "permessi concessi";
            } else {
                // permission negata: provvediamo in qualche maniera
                permessi = "permessi negati";
            }
            return;
//           }
        }
    }
    //
    //
    public void displayFileInAssets() {

        // fonte https://xjaphx.wordpress.com/2011/10/02/store-and-use-files-in-assets/
        //
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
            // debug
            //    TextView myText = findViewById(R.id.textViewMain);
            //    myText.setText((CharSequence) ex);
            return;
            }
        }

    // QUI testare shared preference
    public void verifyLastPlayer () {
        boolean hasLastPlayer = sharedPref.contains("preference_LastPlayer");
        if (!hasLastPlayer) {
            // passo all'activity di registrazione utente
            Intent intent = new Intent(this, AccountActivityRealmCreation.class);
            //
            String message = "enter username";
            //
            intent.putExtra(EXTRA_MESSAGE, message);
            startActivity(intent);

            }
            else
            {
            String sharedLastPlayer =
                    sharedPref.getString ("preference_LastPlayer", "DEFAULT");
            String sharedGameLevel =
                    sharedPref.getString ("preference_GameLevel", "DEFAULT");
            }

    }
    //
        public  boolean isStoragePermissionGranted() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_GRANTED) {
                    // Log.v(TAGPERMISSION,"Permission is granted");
                    return true;
                } else {

                    // Log.v(TAGPERMISSION,"Permission is revoked");
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                    return false;
                }
            }
            else { //permission is automatically granted on sdk<23 upon installation
                // Log.v(TAGPERMISSION,"Permission is granted");
                return true;
            }
        }
        //


}