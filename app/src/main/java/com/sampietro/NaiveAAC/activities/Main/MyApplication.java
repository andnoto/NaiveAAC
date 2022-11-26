package com.sampietro.NaiveAAC.activities.Main;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;

import com.sampietro.NaiveAAC.BuildConfig;
import com.sampietro.NaiveAAC.R;
import com.sampietro.NaiveAAC.activities.Game.GameParameters.GameParameters;
import com.sampietro.NaiveAAC.activities.Graphics.Videos;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

import io.realm.DynamicRealmObject;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmObjectSchema;
import io.realm.RealmResults;

/**
 * <h1>MyApplication</h1>
 * <p><b>MyApplication</b> When the application process is started,
 * this class is instantiated before any of the application's components.
 */
public class MyApplication extends Application {
    /**
     * configurations :
     * </p>
     * 1) on first use copy defaultrealm from assets
     * 2) Realm init
     * 3) create RealmConfiguration
     * <p>
     * Refer to <a href="https://github.com/realm/realm-java/tree/master/examples/migrationExample/src/main/java/io/realm/examples/realmmigrationexample">github</a>
     * commits by <a href="https://github.com/cmelchior">Christian Melchior</a>
     *
     * @see android.app.Application#onCreate
     * @see #copyFileRealm()
     * @see customRealmMigration
     */
    @Override
    public void onCreate() {
        super.onCreate();
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
                        .name("default.realm")
                        .schemaVersion(2)
                        .migration(new customRealmMigration())
                        //     .deleteRealmIfMigrationNeeded()
                        .build();
        Realm.setDefaultConfiguration(realmConfiguration);
        //
        appVersionManagement();
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
    //
    /**
     * app version management
     */
    public void appVersionManagement() {
        int oldVersionCode = 0;
        //
        int currentVersionCode = BuildConfig.VERSION_CODE;
        //
        Context context = this;
        SharedPreferences sharedPref = context.getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        boolean hasVersionCode = sharedPref.contains("preference_VersionCode");
        if (!hasVersionCode) {
            // is the first version downloaded and register current VersionCode on sharedpref
            editor.putInt("preference_VersionCode", currentVersionCode);
            editor.apply();
        }
        else
        {
            // it's not the first version downloaded  and I record the old version code
            oldVersionCode =
                    sharedPref.getInt ("preference_VersionCode", 0);
        }
        //
        if (oldVersionCode == 0) {
            oldVersionCode++;
        }
        //
        if (oldVersionCode == 1) {
            oldVersionCode++;
        }
        //
        if (oldVersionCode == 2) {
            oldVersionCode++;
        }
        //
        if (oldVersionCode == 3) {
            oldVersionCode++;
        }
        //
        if (oldVersionCode == 4) {
            oldVersionCode++;
        }
        //
        if (oldVersionCode == 5) {
            oldVersionCode++;
        }
        //
        if (oldVersionCode == 6) {
            // rename video
            Realm realm= Realm.getDefaultInstance();
            RealmResults<Videos> results =
                    realm.where(Videos.class).equalTo(getString(R.string.descrizione), "pensieri e parole").findAll();
            int resultsSize = results.size();
            if (resultsSize != 0) {
                realm.beginTransaction();
                // crash
                Videos daRinominare=results.get(0);
                assert daRinominare != null;
                daRinominare.setDescrizione("AUTISMO");
                realm.commitTransaction();
            }
            // rename game parameters
            RealmResults<GameParameters> resultsgp1 =
                    realm.where(GameParameters.class).equalTo("gameName", "pensieri e parole").findAll();
            int resultsgp1Size = resultsgp1.size();
            if (resultsgp1Size != 0) {
                realm.beginTransaction();
                GameParameters daRinominaregp1=resultsgp1.get(0);
                assert daRinominaregp1 != null;
                daRinominaregp1.setGameName("NAVIGATORE");
                daRinominaregp1.setGameJavaClass("NAVIGATORE");
                realm.commitTransaction();
            }
            RealmResults<GameParameters> resultsgp2 =
                    realm.where(GameParameters.class).equalTo("gameName", "parole in libertà").findAll();
            int resultsgp2Size = resultsgp2.size();
            if (resultsgp2Size != 0) {
                realm.beginTransaction();
                GameParameters daRinominaregp2=resultsgp2.get(0);
                assert daRinominaregp2 != null;
                daRinominaregp2.setGameName("COMUNICATORE");
                daRinominaregp2.setGameJavaClass("COMUNICATORE");
                realm.commitTransaction();
            }
            //
            oldVersionCode++;
        }

        if (oldVersionCode < currentVersionCode) {
            throw new IllegalStateException(String.format(Locale.US, "Migration missing from v%d to v%d", oldVersionCode, currentVersionCode));
        }

    }
}