package com.sampietro.NaiveAAC.activities.Main

import android.app.Application
import android.content.Context
import io.realm.RealmConfiguration
import com.sampietro.NaiveAAC.BuildConfig
import com.sampietro.NaiveAAC.R
import com.sampietro.NaiveAAC.activities.Bluetooth.BluetoothDevices
import com.sampietro.NaiveAAC.activities.DataStorage.DataStorageHelper.copyFileFromAssetsToInternalStorage
import kotlin.Throws
import com.sampietro.NaiveAAC.activities.Stories.Stories
import com.sampietro.NaiveAAC.activities.Grammar.GrammaticalExceptions
import com.sampietro.NaiveAAC.activities.Graphics.Sounds
import com.sampietro.NaiveAAC.activities.Graphics.Videos
import com.sampietro.NaiveAAC.activities.Game.GameParameters.GameParameters
import com.sampietro.NaiveAAC.activities.Graphics.Images
import io.realm.Realm
import java.io.File
import java.io.IOException
import java.util.*

/**
 * <h1>MyApplication</h1>
 *
 * **MyApplication** When the application process is started,
 * this class is instantiated before any of the application's components.
 */
class MyApplication : Application() {
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
        var itSAFreshInstall = false
        val fileRealm = File(this.filesDir, getString(R.string.default_realm))
        if (!fileRealm.exists()) {
            // it's a fresh install
            itSAFreshInstall = true
            try {
                copyFileRealm()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        //
        Realm.init(this)
        val realmConfiguration = RealmConfiguration.Builder()
            .name(getString(R.string.default_realm))
            .schemaVersion(7)
            .migration(customRealmMigration()) //     .deleteRealmIfMigrationNeeded()
            .build()
        Realm.setDefaultConfiguration(realmConfiguration)
        //
        if (itSAFreshInstall) {
            val context: Context = this
            val sharedPref = context.getSharedPreferences(
                getString(R.string.preference_file_key), MODE_PRIVATE
            )
            val editor = sharedPref.edit()
            // register current VersionCode on sharedpref
            editor.putInt("preference_VersionCode", BuildConfig.VERSION_CODE)
            editor.apply()
        } else {
            appVersionManagement()
        }
    }
    //
    /**
     * copy defaultrealm from assets
     */
    @Throws(IOException::class)
    fun copyFileRealm() {
        val sourceStream = assets.open("copiarealm")
        val destStream = openFileOutput(getString(R.string.default_realm), MODE_PRIVATE)
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
    //
    /**
     * app version management
     */
    fun appVersionManagement() {
        var oldVersionCode = 0
        //
        val currentVersionCode = BuildConfig.VERSION_CODE
        //
        val context: Context = this
        val sharedPref = context.getSharedPreferences(
            getString(R.string.preference_file_key), MODE_PRIVATE
        )
        val editor = sharedPref.edit()
        val hasVersionCode = sharedPref.contains(getString(R.string.preference_versioncode))
        if (hasVersionCode) {
            // I record the old version code (the version code is present from version 7)
            oldVersionCode = sharedPref.getInt(getString(R.string.preference_versioncode), 0)
        }
        //
        if (oldVersionCode == 0) {
            oldVersionCode++
        }
        //
        if (oldVersionCode == 1) {
            oldVersionCode++
        }
        //
        if (oldVersionCode == 2) {
            oldVersionCode++
        }
        //
        if (oldVersionCode == 3) {
            oldVersionCode++
        }
        //
        if (oldVersionCode == 4) {
            oldVersionCode++
        }
        //
        if (oldVersionCode == 5) {
            oldVersionCode++
        }
        //
        if (oldVersionCode == 6) {
            oldVersionCode++
        }
        //
        if (oldVersionCode == 7) {
            try {
                copyFileFromAssetsToInternalStorage(context, getString(R.string.images), "racconto.png", "racconto.png")
                copyFileFromAssetsToInternalStorage(context, getString(R.string.images), "non.png", "non.png")
                //
                copyFileFromAssetsToInternalStorage(
                     context,
                    "csv",
                    "toaddversion8-stories.csv",
                    "stories.csv"
                )
            } catch (e: IOException) {
                e.printStackTrace()
            }
            val realm = Realm.getDefaultInstance()
            Stories.importFromCsvFromInternalStorage(context, realm, getString(R.string.append))
            //
            oldVersionCode++
        }
        //
        if (oldVersionCode == 8) {
            // fix error in the transition from version 7 to 8 and update game parameters
            //
            // delete image
            val realm = Realm.getDefaultInstance()
            //
            try {
                copyFileFromAssetsToInternalStorage(
                     context,
                    "csv",
                    "grammaticalexceptions.csv",
                    "grammaticalexceptions.csv"
                )
            } catch (e: IOException) {
                e.printStackTrace()
            }
            GrammaticalExceptions.importFromCsvFromInternalStorage(context, realm, "Replace")
            //
            oldVersionCode++
        }
        //
        if (oldVersionCode == 9) {
            //
            oldVersionCode++
        }
        //
        if (oldVersionCode == 10) {
            //
            val realm = Realm.getDefaultInstance()
            // delete story
            val results =
                realm.where(Stories::class.java).equalTo(getString(R.string.story), "il gatto con gli stivali")
                    .findAll()
            realm.beginTransaction()
            results.deleteAllFromRealm()
            realm.commitTransaction()
            // replace story from csv in assets
            try {
                copyFileFromAssetsToInternalStorage(
                    context,
                    getString(R.string.sounds),
                    "cat-meow-14536.mp3",
                    "cat-meow-14536.mp3"
                )
                copyFileFromAssetsToInternalStorage(
                    context,
                    getString(R.string.sounds),
                    "fiaker-73119.mp3",
                    "fiaker-73119.mp3"
                )
                copyFileFromAssetsToInternalStorage(context,
                    getString(R.string.sounds),
                    "Hallelujah.mp3",
                    "Hallelujah.mp3")
                copyFileFromAssetsToInternalStorage(
                    context,
                    getString(R.string.sounds),
                    "jumento-101690.mp3",
                    "jumento-101690.mp3"
                )
                copyFileFromAssetsToInternalStorage(
                    context,
                    getString(R.string.sounds),
                    "lion-roar-6011.mp3",
                    "lion-roar-6011.mp3"
                )
                copyFileFromAssetsToInternalStorage(
                    context,
                    getString(R.string.sounds),
                    "river-stream-moderate-flow-2-24370.mp3",
                    "river-stream-moderate-flow-2-24370.mp3"
                )
                copyFileFromAssetsToInternalStorage(
                    context,
                    getString(R.string.sounds),
                    "scary-laugh-123862.mp3",
                    "scary-laugh-123862.mp3"
                )
                copyFileFromAssetsToInternalStorage(
                    context,
                    getString(R.string.sounds),
                    "Wedding March.mp3",
                    "Wedding March.mp3"
                )
                copyFileFromAssetsToInternalStorage(
                    context,
                    getString(R.string.images),
                    "puntointerrogativo.png",
                    "puntointerrogativo.png"
                )
                copyFileFromAssetsToInternalStorage(context,getString(R.string.videos), "cat-4916.mp4", "cat-4916.mp4")
                copyFileFromAssetsToInternalStorage(context,getString(R.string.videos), "cat-92641.mp4", "cat-92641.mp4")
                copyFileFromAssetsToInternalStorage(context,getString(R.string.videos), "man-131605.mp4", "man-131605.mp4")
                copyFileFromAssetsToInternalStorage(
                    context,
                    getString(R.string.videos),
                    "mouse-117573.mp4",
                    "mouse-117573.mp4"
                )
                copyFileFromAssetsToInternalStorage(
                    context,
                    getString(R.string.videos),
                    "rabbit-141719.mp4",
                    "rabbit-141719.mp4"
                )
                copyFileFromAssetsToInternalStorage(
                    context,
                    getString(R.string.videos),
                    "tags-bird-9267.mp4",
                    "tags-bird-9267.mp4"
                )
                copyFileFromAssetsToInternalStorage(context,"csv", "sounds.csv", "sounds.csv")
                copyFileFromAssetsToInternalStorage(context,"csv", "images.csv", "images.csv")
                copyFileFromAssetsToInternalStorage(context,"csv", "videos.csv", "videos.csv")
                copyFileFromAssetsToInternalStorage(
                    context,
                    "csv",
                    "toaddversion10-stories.csv",
                    "stories.csv"
                )
                copyFileFromAssetsToInternalStorage(
                    context,
                    "csv",
                    "gameparameters.csv",
                    "gameparameters.csv"
                )
            } catch (e: IOException) {
                e.printStackTrace()
            }
            // eliminazione di tutte le immagini tranne quelle relative ad "io" e a nome utente
            val sharedLastPlayer =
                sharedPref.getString(getString(R.string.preference_LastPlayer), getString(R.string.default_string))
            val resultsImages = realm.where(
                Images::class.java
            )
                .equalTo(getString(R.string.descrizione), "io")
                .or()
                .equalTo(getString(R.string.descrizione), sharedLastPlayer)
                .findAll()
            val imagesSize = resultsImages.size
            val resultsImagesList = realm.copyFromRealm(resultsImages)
            // clear the table
            val daCancellare = realm.where(
                Images::class.java
            ).findAll()
            realm.beginTransaction()
            daCancellare.deleteAllFromRealm()
            realm.commitTransaction()
            //
            var irrh = 0
            while (irrh < imagesSize) {
                val resultImages = resultsImagesList[irrh]!!
                realm.beginTransaction()
                realm.copyToRealm(resultImages)
                realm.commitTransaction()
                irrh++
            }
            //
            Sounds.importFromCsvFromInternalStorage(context, realm, getString(R.string.replace))
            Images.importFromCsvFromInternalStorage(context, realm, getString(R.string.append))
            Videos.importFromCsvFromInternalStorage(context, realm, getString(R.string.replace))
            Stories.importFromCsvFromInternalStorage(context, realm, getString(R.string.append))
            GameParameters.importFromCsvFromInternalStorage(context, realm, getString(R.string.replace))
            //
            oldVersionCode++
        }
        //
        if (oldVersionCode == 11) {
            oldVersionCode++
        }
        //
        if (oldVersionCode == 12) {
            oldVersionCode++
        }
        //
        if (oldVersionCode == 13) {
            oldVersionCode++
        }
        //
        if (oldVersionCode == 14) {
            oldVersionCode++
        }
        //
        if (oldVersionCode == 15) {
            oldVersionCode++
        }
        //
        if (oldVersionCode == 16) {
            //
            val realm = Realm.getDefaultInstance()
            // update game parameter
            val resultsgp = realm.where(
                GameParameters::class.java
            ).equalTo(getString(R.string.gamename), getString(R.string.comunicatore)).findAll()
            val resultsgpSize = resultsgp.size
            if (resultsgpSize != 0) {
                realm.beginTransaction()
                val daModificaregp = resultsgp[0]!!
                daModificaregp.gameActive = "A"
                realm.commitTransaction()
            }
            //
            oldVersionCode++
        }
        //
        if (oldVersionCode == 17) {
            //
            val realm = Realm.getDefaultInstance()
            try {
                copyFileFromAssetsToInternalStorage(
                    context,
                    getString(R.string.images),
                    "pecs.png",
                    "pecs.png"
                )
                copyFileFromAssetsToInternalStorage(context,"csv", "toaddversion17-images.csv", "images.csv")
                copyFileFromAssetsToInternalStorage(context,"csv", "toaddversion17-gameparameters.csv", "gameparameters.csv")
                copyFileFromAssetsToInternalStorage(context,"csv", "bluetoothdevices.csv", "bluetoothdevices.csv")
            } catch (e: IOException) {
                e.printStackTrace()
            }
            //
            Images.importFromCsvFromInternalStorage(context, realm, "Append")
            GameParameters.importFromCsvFromInternalStorage(context, realm, "Append")
            BluetoothDevices.importFromCsvFromInternalStorage(context, realm, getString(R.string.replace))
            //
            oldVersionCode++
        }
        //
        check(oldVersionCode >= currentVersionCode) {
            String.format(
                Locale.US,
                "Migration missing from v%d to v%d",
                oldVersionCode,
                currentVersionCode
            )
        }


        // register current VersionCode on sharedpref
        editor.putInt(getString(R.string.preference_versioncode), currentVersionCode)
        editor.apply()
    }
}