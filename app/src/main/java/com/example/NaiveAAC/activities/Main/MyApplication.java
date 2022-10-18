package com.example.NaiveAAC.activities.Main;

import android.app.Application;
import android.content.Context;
import android.os.Bundle;

import com.example.NaiveAAC.activities.Game.ChoiseOfGame.ChoiseOfGameActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import io.realm.Realm;
import io.realm.RealmConfiguration;
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