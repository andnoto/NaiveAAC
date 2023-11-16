package com.sampietro.NaiveAAC.activities.Main

import android.app.Application
import io.realm.Realm
import io.realm.RealmConfiguration
import java.io.IOException
import kotlin.Throws

/**
 * <h1>MyApplication</h1>
 *
 * **MyApplication** When the application process is started,
 * this class is instantiated before any of the application's components.
 */
class MyApplicationRealmCreation : Application() {
    /**
     * configurations :
     *
     * 1) on first use copy defaultrealm from assets
     * 2) Realm init
     * 3) create RealmConfiguration
     *
     *
     * Refer to [github](https://github.com/realm/realm-java/tree/master/examples/migrationExample/src/main/java/io/realm/examples/realmmigrationexample)
     * commits by [Christian Melchior](https://github.com/cmelchior)
     *
     * @see Application.onCreate
     *
     * @see .copyFileRealm
     * @see customRealmMigration
     */
    override fun onCreate() {
        super.onCreate()
        //
        // File fileRealm = new File(this.getFilesDir(), "default.realm");
        // if (!fileRealm.exists()) {
        //    try {
        //        copyFileRealm();
        //    } catch (IOException e) {
        //        e.printStackTrace();
        //    }
        // }
        //
        //
        Realm.init(this)
        val realmConfiguration = RealmConfiguration.Builder()
            .name("default.realm")
            .schemaVersion(2)
            .deleteRealmIfMigrationNeeded()
            .build()
        Realm.setDefaultConfiguration(realmConfiguration)
        //
        //
        // Realm.init(this);
        // RealmConfiguration realmConfiguration =
        //         new RealmConfiguration.Builder()
        //                .name("default.realm")
        //                .schemaVersion(2)
        //                .migration(new customRealmMigration())
        //     .deleteRealmIfMigrationNeeded()
        //                .build();
        // Realm.setDefaultConfiguration(realmConfiguration);
    }
    //
    /**
     * copy defaultrealm from assets
     */
    @Throws(IOException::class)
    fun copyFileRealm() {
        val sourceStream = assets.open("copiarealm")
        val destStream = openFileOutput("default.realm", MODE_PRIVATE)
        val buffer = ByteArray(1024)
        var read = sourceStream.read(buffer)
        while (read != -1) {
            destStream.write(buffer, 0, read)
            read = sourceStream.read(buffer)
        }
        destStream.close()
        sourceStream.close()
        //
    }
}