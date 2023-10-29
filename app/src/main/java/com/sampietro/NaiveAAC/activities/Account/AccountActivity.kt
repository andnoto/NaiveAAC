package com.sampietro.NaiveAAC.activities.Account

import android.Manifest
import com.sampietro.NaiveAAC.activities.Settings.Utils.AccountActivityAbstractClass
import com.sampietro.NaiveAAC.activities.Settings.Utils.SettingsFragmentAbstractClass.onFragmentEventListenerSettings
import android.os.Bundle
import com.sampietro.NaiveAAC.R
import com.sampietro.NaiveAAC.activities.Settings.AccountFragment
import com.sampietro.NaiveAAC.activities.Graphics.Sounds
import com.sampietro.NaiveAAC.activities.Graphics.Videos
import com.sampietro.NaiveAAC.activities.Game.GameParameters.GameParameters
import com.sampietro.NaiveAAC.activities.Grammar.GrammaticalExceptions
import com.sampietro.NaiveAAC.activities.Grammar.ListsOfNames
import com.sampietro.NaiveAAC.activities.Phrases.Phrases
import com.sampietro.NaiveAAC.activities.Stories.Stories
import com.sampietro.NaiveAAC.activities.WordPairs.WordPairs
import android.widget.EditText
import android.content.Intent
import com.sampietro.NaiveAAC.activities.Game.ChoiseOfGame.ChoiseOfGameActivity
import kotlin.Throws
import android.content.pm.PackageManager
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import com.sampietro.NaiveAAC.activities.Graphics.Images
import com.sampietro.NaiveAAC.activities.history.History
import io.realm.Realm
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

/**
 * <h1>AccountActivity</h1>
 *
 * **AccountActivity** create from assets the initial realm database , register the user
 * and move on to the welcome activity.
 * the initial realm database consists of
 *
 * 1) data from the Arasaac /pictograms/all API
 * 2) data from https://github.com/ian-hamlin/verb-data a collection of verbs and conjugations
 * 3) initial settings and content such as images, videos and others from assets
 *
 * @version     4.0, 09/09/2023
 * @see AccountActivityAbstractClass
 *
 * @see com.sampietro.NaiveAAC.activities.Settings.Utils.SettingsFragmentAbstractClass
 */
class AccountActivity : AccountActivityAbstractClass(), onFragmentEventListenerSettings {
    //
    var fragmentManager: FragmentManager? = null

    //
//    private val FOLDER_SIMSIM = "simsim"

    //
    var textPersonName: String? = null
    var textPassword: String? = null
    //
    /**
     * configurations of account start screen.
     *
     * @param savedInstanceState Define potentially saved parameters due to configurations changes.
     * @see setActivityResultLauncher
     *
     * @see AccountActionbarFragment
     *
     * @see AccountFragment
     *
     * @see android.app.Activity.onCreate
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account)
        //
        realm = Realm.getDefaultInstance()
        //
        context = this
        //
        filePath = getString(R.string.non_trovato)
        //
        setActivityResultLauncher()
        //
        if (savedInstanceState == null) {
            fragmentManager = supportFragmentManager
            fragmentManager!!.beginTransaction()
                .add(AccountActionbarFragment(), "AccountActionbarFragment")
                .add(
                    R.id.settings_container,
                    AccountFragment(),
                    getString(R.string.account_fragment)
                )
                .commit()
        }
// Register the permissions callback, which handles the user's response to the
// system permissions dialog. Save the return value, an instance of
// ActivityResultLauncher. You can use either a val, as shown in this snippet,
// or a lateinit var in your onAttach() or onCreate() method.
        val requestPermissionLauncher =
            registerForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) { isGranted: Boolean ->
                if (isGranted) {
                    // Permission is granted. Continue the action or workflow in your
                    // app.
                } else {
                    // Explain to the user that the feature is unavailable because the
                    // feature requires a permission that the user has denied. At the
                    // same time, respect the user's decision. Don't link to system
                    // settings in an effort to convince the user to change their
                    // decision.
                    // da COMPLETARE
                }
            }
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED -> {
                // You can use the API that requires the permission.
            }
//            shouldShowRequestPermissionRationale(...) -> {
            // In an educational UI, explain to the user why your app requires this
            // permission for a specific feature to behave as expected, and what
            // features are disabled if it's declined. In this UI, include a
            // "cancel" or "no thanks" button that lets the user continue
            // using your app without granting the permission.
//            showInContextUI(...)
//        }
            else -> {
                // You can directly ask for the permission.
                // The registered ActivityResultCallback gets the result of this request.
                requestPermissionLauncher.launch(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }
        }
        //
//        if (isStoragePermissionGranted) {
            //    Log.v(TAGPERMISSION,getString(R.string.permission_is_granted));
//        }
        //
    }

    /**
     * Called when the user taps the save account button.
     *
     * if the case creates creates the initial realm database.
     *
     * @param view view of tapped button
//     * @see isStoragePermissionGranted
     *
     * @see com.sampietro.NaiveAAC.activities.Settings.Utils.AdvancedSettingsDataImportExportHelper.findExternalStorageRoot
     * @see prepareTheSimsimDirectory
     *
     * @see registerPersonName
     * @see com.sampietro.NaiveAAC.activities.Settings.Utils.AdvancedSettingsDataImportExportHelper.openFileInput
     * @see Images
     *
     * @see Videos
     *
     * @see GameParameters
     *
     * @see GrammaticalExceptions
     *
     * @see ListsOfNames
     *
     * @see Phrases
     *
     * @see Stories
     *
     * @see WordPairs
     */
    fun saveAccount(view: View?) {
//        if (isStoragePermissionGranted) {
            //
            // naiveaac dir registration and csv copy from assets to dir naiveaac
            prepareTheSimsimDirectory()
            //
            // try {
            //     copyManualFromAssetsToInternalStorage();
            // } catch (IOException e) {
            //     e.printStackTrace();
            // }
            //
//            realm = Realm.getDefaultInstance()
            //
            val daCancellare = realm.where(
                History::class.java
            ).findAll()
            realm.beginTransaction()
            daCancellare.deleteAllFromRealm()
            realm.commitTransaction()
            //
            Images.importFromCsvFromInternalStorage(context, realm, "Replace")
            Sounds.importFromCsvFromInternalStorage(context, realm, "Replace")
            Videos.importFromCsvFromInternalStorage(context, realm, "Replace")
            //
            GameParameters.importFromCsvFromInternalStorage(context, realm, "Replace")
            GrammaticalExceptions.importFromCsvFromInternalStorage(context, realm, "Replace")
            ListsOfNames.importFromCsvFromInternalStorage(context, realm, "Replace")
            Phrases.importFromCsvFromInternalStorage(context, realm, "Replace")
            Stories.importFromCsvFromInternalStorage(context, realm, "Replace")
            WordPairs.importFromCsvFromInternalStorage(context, realm, "Replace")
            //
//        }
        //
        // record default preferences
        sharedPref = getSharedPreferences(
            getString(R.string.preference_file_key), MODE_PRIVATE
        )
        val editor = sharedPref.edit()
        editor.putString(
            getString(R.string.preference_print_permissions),
            getString(R.string.character_n)
        )
        editor.apply()
        editor.putString("preference_title_writing_type", "uppercase")
        editor.apply()
        editor.putString(getString(R.string.preference_list_mode), getString(R.string.sorted))
        editor.apply()
        editor.putInt("preference_AllowedMarginOfError", 20)
        editor.apply()
        // register the user in the shared preferences
        // and move on to the welcome activity
        val editText = rootViewFragment!!.findViewById<View>(R.id.editTextTextAccount) as EditText
        textPersonName = editText.text.toString()
        //
        val editTextPassword =
            rootViewFragment!!.findViewById<View>(R.id.editTextPasswordAccount) as EditText
        textPassword = editTextPassword.text.toString()
        // default
        if (textPersonName!!.length <= 0) textPersonName = "utente"
        if (filePath == getString(R.string.non_trovato) || filePath == "da download") {
            val utenteFile = getFileStreamPath("utente.png")
            filePath = utenteFile.absolutePath
        }
        if (textPassword!!.length <= 0) textPassword = "nessuna password"
        //
        if (textPersonName!!.length > 0 && filePath != getString(R.string.non_trovato)
            && filePath != "da download" && textPassword!!.length > 0
        ) {
            // register the user
            registerPersonName(textPersonName)
            // register the linked image
            realm.beginTransaction()
            val i = realm.createObject(
                Images::class.java
            )
            i.descrizione = textPersonName
            i.uri = filePath
            realm.commitTransaction()
            realm.beginTransaction()
            val iIo = realm.createObject(
                Images::class.java
            )
            iIo.descrizione = getString(R.string.io)
            iIo.uri = filePath
            realm.commitTransaction()
            // register the linked word pairs
            realm.beginTransaction()
            val wordPairs = realm.createObject(WordPairs::class.java)
            wordPairs.word1 = getString(R.string.famiglia)
            wordPairs.word2 = textPersonName
            wordPairs.complement = ""
            wordPairs.isMenuItem = getString(R.string.slm)
            wordPairs.awardType = ""
            wordPairs.uriPremiumVideo = ""
            realm.commitTransaction()
            // register the password
            registerPassword(textPassword)
            //
            val intent = Intent(
                this,
                ChoiseOfGameActivity::class.java
            )
            val message = getString(R.string.puoi_accedere)
            intent.putExtra(EXTRA_MESSAGE, message)
            startActivity(intent)
        }
    }
    //
    /**
     * copy the csv files with initial settings and content such as images, videos and others from assets.
     *
     * @see .copyFileCsvFromAssetsToInternalStorage
     *
     * @see .copyAssets
     */
    fun prepareTheSimsimDirectory() {
        try {
            copyFileCsvFromAssetsToInternalStorage("gameparameters.csv")
            copyFileCsvFromAssetsToInternalStorage("grammaticalexceptions.csv")
            copyFileCsvFromAssetsToInternalStorage("images.csv")
            copyFileCsvFromAssetsToInternalStorage("listsofnames.csv")
            copyFileCsvFromAssetsToInternalStorage("phrases.csv")
            copyFileCsvFromAssetsToInternalStorage("pictogramsalltomodify.csv")
            copyFileCsvFromAssetsToInternalStorage("sounds.csv")
            copyFileCsvFromAssetsToInternalStorage("stories.csv")
            copyFileCsvFromAssetsToInternalStorage("videos.csv")
            copyFileCsvFromAssetsToInternalStorage("wordpairs.csv")
        } catch (e: IOException) {
            e.printStackTrace()
        }
        //
        copyAssets("images")
        copyAssets("sounds")
        copyAssets("videos")
        copyAssets("pdf")
        //
    }
    //
    /**
     * copy the single csv file with initial settings and content from assets
     *
     * @param fileName string with the name of the file to be copied
     */
    @Throws(IOException::class)
    fun copyFileCsvFromAssetsToInternalStorage(fileName: String) {
        val sourceStream =
            assets.open(getString(R.string.csv) + getString(R.string.character_slash) + fileName)
        val destStream = openFileOutput(fileName, MODE_PRIVATE)
        val buffer = ByteArray(100)
        var bytesRead: Int
        while (sourceStream.read(buffer).also { bytesRead = it } != -1) {
            destStream.write(buffer, 0, bytesRead)
        }
        destStream.close()
        sourceStream.close()
    }
    /**
     * on callback from SettingsFragment to this Activity
     *
     * @param v view root fragment view
     */
    override fun receiveResultSettings(v: View?) {
        rootViewFragment = v
    }
    //
    /**
     * copy assets images and videos to storage.
     *
     *
     * Refer to [stackoverflow](https://stackoverflow.com/questions/4447477/how-to-copy-files-from-assets-folder-to-sdcard)
     * answer of [Rohith Nandakumar](https://stackoverflow.com/users/481239/rohith-nandakumar)
     *
     * @param path string assets folder to copy
     * @see .copyFile
     */
    private fun copyAssets(path: String) {
        val assetManager = assets
        var files: Array<String>? = null
        try {
            files = assetManager.list(path)
        } catch (e: IOException) {
            // Log.e("tag", "Failed to get asset file list.", e);
        }
        if (files != null) {
            for (filename in files) {
                var `in`: InputStream? = null
                var out: OutputStream? = null
                try {
                    `in` = assetManager.open("$path/$filename")
                    //
                    out = context.openFileOutput(filename, MODE_PRIVATE)
                    copyFile(`in`, out)
                } catch (e: IOException) {
                    // Log.e("tag", "Failed to copy asset file: " + filename, e);
                } finally {
                    if (`in` != null) {
                        try {
                            `in`.close()
                        } catch (e: IOException) {
                            // NOOP
                        }
                    }
                    if (out != null) {
                        try {
                            out.close()
                        } catch (e: IOException) {
                            // NOOP
                        }
                    }
                }
            }
        }
    }

    /**
     * copy file.
     *
     *
     * Refer to [stackoverflow](https://stackoverflow.com/questions/4447477/how-to-copy-files-from-assets-folder-to-sdcard)
     * answer of [Rohith Nandakumar](https://stackoverflow.com/users/481239/rohith-nandakumar)
     *
     * @param in inputstream
     * @param out outputstream
     */
    @Throws(IOException::class)
    private fun copyFile(`in`: InputStream, out: OutputStream?) {
        val buffer = ByteArray(1024)
        var read: Int
        while (`in`.read(buffer).also { read = it } != -1) {
            out!!.write(buffer, 0, read)
        }
    }

    companion object {
        //
//        private const val TAG = "VERBO"
//        private const val TAGPERMISSION = "Permission"

        //
        const val EXTRA_MESSAGE = "helloworldandroidMessage"
    }
}