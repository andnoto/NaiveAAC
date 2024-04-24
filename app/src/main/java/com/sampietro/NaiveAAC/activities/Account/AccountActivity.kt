package com.sampietro.NaiveAAC.activities.Account

import android.Manifest
import android.app.Activity
import android.app.Dialog
import com.sampietro.NaiveAAC.activities.BaseAndAbstractClass.AccountActivityAbstractClass
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
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.ImageDecoder
import android.net.Uri
import android.view.ContextThemeWrapper
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.ImageButton
import android.widget.ImageView
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import com.google.android.material.snackbar.Snackbar
import com.sampietro.NaiveAAC.activities.Bluetooth.BluetoothDevices
//import com.sampietro.NaiveAAC.activities.DataStorage.DataStorageHelper
import com.sampietro.NaiveAAC.activities.DataStorage.DataStorageHelper.copyAssets
import com.sampietro.NaiveAAC.activities.DataStorage.DataStorageHelper.copyFileFromAssetsToInternalStorage
import com.sampietro.NaiveAAC.activities.DataStorage.DataStorageHelper.getFilePath
import com.sampietro.NaiveAAC.activities.Graphics.GraphicsAndPrintingHelper.showImage
//import com.sampietro.NaiveAAC.activities.Graphics.GraphicsAndPrintingHelper
import com.sampietro.NaiveAAC.activities.Graphics.Images
import com.sampietro.NaiveAAC.activities.history.History
import io.realm.Realm
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.lang.Exception
import java.net.URISyntaxException
import java.util.Objects

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
 * @version     5.0, 01/04/2024
 * @see AccountActivityAbstractClass
 *
 * @see com.sampietro.NaiveAAC.activities.Settings.Utils.SettingsFragmentAbstractClass
 */
class AccountActivity : AccountActivityAbstractClass() {
    //
    var fragmentManager: FragmentManager? = null
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
                    AccountFragment(R.layout.activity_settings_account),
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
     * Called when the user taps the enter password button.
     *
     * @param v view of tapped button
     */
    fun enterPassword(v: View?) {
        //
        val d = Dialog(this)
        // Setting dialogview
        val window = d.window
        val wlp = window!!.attributes
        wlp.gravity = Gravity.BOTTOM
        wlp.flags = wlp.flags and WindowManager.LayoutParams.FLAG_DIM_BEHIND.inv()
        window.attributes = wlp
        //
        d.setTitle("Inserisci la password per accedere alle impostazioni \\n(opzionale se omessa , per accedere alle impostazioni, \\nverrà richiesto il risultato di un calcolo aritmetico)")
        d.setCancelable(false)
        d.setContentView(R.layout.activity_account_write_dialog)
        //
        val submitPasswordButton =
            d.findViewById<ImageButton>(R.id.submitPasswordButton)
        val cancelPasswordButton =
            d.findViewById<ImageButton>(R.id.cancelPasswordButton)
        val editTextPasswordAccount = d.findViewById<View>(R.id.editTextPasswordAccount) as EditText
        //
        editTextPasswordAccount.requestFocus()
        val imm = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(d.findViewById<View>(R.id.editTextPasswordAccount),
            InputMethodManager.SHOW_IMPLICIT
        )
        imm.hideSoftInputFromWindow(d.findViewById<View>(R.id.editTextPasswordAccount).getWindowToken(),
            InputMethodManager.HIDE_IMPLICIT_ONLY
        )
        //
        submitPasswordButton.setOnClickListener {
            //
            textPassword = editTextPasswordAccount.text.toString()
            imm.hideSoftInputFromWindow(editTextPasswordAccount.windowToken, 0)
            //
            d.cancel()
        }
        cancelPasswordButton.setOnClickListener {
            //
            imm.hideSoftInputFromWindow(editTextPasswordAccount.windowToken, 0)
            //
            d.cancel()
        }
        //
        d.show()
    }

    /**
     * Called when the user taps the save account button.
     *
     * if the case creates creates the initial realm database.
     *
     * @param view view of tapped button
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
            // naiveaac dir registration and csv copy from assets to dir naiveaac
            prepareTheSimsimDirectory()
            //
            val daCancellare = realm.where(
                History::class.java
            ).findAll()
            realm.beginTransaction()
            daCancellare.deleteAllFromRealm()
            realm.commitTransaction()
            //
            Images.importFromCsvFromInternalStorage(context, realm, getString(R.string.replace))
            Sounds.importFromCsvFromInternalStorage(context, realm, getString(R.string.replace))
            Videos.importFromCsvFromInternalStorage(context, realm, getString(R.string.replace))
            //
            BluetoothDevices.importFromCsvFromInternalStorage(context, realm, getString(R.string.replace))
            GameParameters.importFromCsvFromInternalStorage(context, realm, getString(R.string.replace))
            GrammaticalExceptions.importFromCsvFromInternalStorage(context, realm, getString(R.string.replace))
            ListsOfNames.importFromCsvFromInternalStorage(context, realm, getString(R.string.replace))
            Phrases.importFromCsvFromInternalStorage(context, realm, getString(R.string.replace))
            Stories.importFromCsvFromInternalStorage(context, realm, getString(R.string.replace))
            WordPairs.importFromCsvFromInternalStorage(context, realm, getString(R.string.replace))
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
        editor.putString(getString(R.string.preference_title_writing_type), getString(R.string.uppercase))
        editor.apply()
        editor.putString(getString(R.string.preference_list_mode), getString(R.string.sorted))
        editor.apply()
        editor.putInt(getString(R.string.preference_allowed_margin_of_error), 20)
        editor.apply()
        // register the user in the shared preferences
        // and move on to the welcome activity
        val editText = findViewById<View>(R.id.editTextTextAccount) as EditText
        textPersonName = editText.text.toString()
        // default
        if (textPersonName!!.length <= 0) textPersonName = "utente"
        if (filePath == getString(R.string.non_trovato) || filePath == "da download") {
            val utenteFile = getFileStreamPath("utente.png")
            filePath = utenteFile.absolutePath
        }
        if (textPassword == null)   { textPassword = "nessuna password" }
                                    else
                                    { if (textPassword!!.length <= 0) textPassword = "nessuna password" }
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
            // register the linked lists of names
            realm.beginTransaction()
            val listsOfNames = realm.createObject(ListsOfNames::class.java)
            // set the fields here
            listsOfNames.keyword = getString(R.string.famiglia)
            listsOfNames.word = textPersonName
            listsOfNames.elementActive = "A"
            listsOfNames.isMenuItem = "N"
            listsOfNames.fromAssets = ""
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
     * @see copyFileFromAssetsToInternalStorage
     *
     * @see copyAssets
     */
    fun prepareTheSimsimDirectory() {
        try {
            copyFileFromAssetsToInternalStorage(context, getString(R.string.csv),"bluetoothdevices.csv","bluetoothdevices.csv" )
            copyFileFromAssetsToInternalStorage(context, getString(R.string.csv),"gameparameters.csv","gameparameters.csv")
            copyFileFromAssetsToInternalStorage(context, getString(R.string.csv),"grammaticalexceptions.csv","grammaticalexceptions.csv")
            copyFileFromAssetsToInternalStorage(context, getString(R.string.csv),"images.csv","images.csv")
            copyFileFromAssetsToInternalStorage(context, getString(R.string.csv),"listsofnames.csv","listsofnames.csv")
            copyFileFromAssetsToInternalStorage(context, getString(R.string.csv),"phrases.csv","phrases.csv")
            copyFileFromAssetsToInternalStorage(context, getString(R.string.csv),"pictogramsalltomodify.csv","pictogramsalltomodify.csv")
            copyFileFromAssetsToInternalStorage(context, getString(R.string.csv),"sounds.csv","sounds.csv")
            copyFileFromAssetsToInternalStorage(context, getString(R.string.csv),"stories.csv","stories.csv")
            copyFileFromAssetsToInternalStorage(context, getString(R.string.csv),"videos.csv","videos.csv")
            copyFileFromAssetsToInternalStorage(context, getString(R.string.csv),"wordpairs.csv","wordpairs.csv")
        } catch (e: IOException) {
            e.printStackTrace()
        }
        //
        copyAssets(context,"images")
        copyAssets(context,"sounds")
        copyAssets(context,"videos")
        copyAssets(context,"pdf")
        //
    }
    // ActivityResultLauncher
    var imageSearchAccountActivityResultLauncher: ActivityResultLauncher<Intent>? = null
    //
    @JvmField
    var uri: Uri? = null
    //
    @JvmField
    var filePath: String? = null
    lateinit var byteArray: ByteArray
    //
    /**
     * setting callbacks to search for images and videos via ACTION_OPEN_DOCUMENT which is
     * the intent to choose a file via the system's file browser
     *
     *
     * Refer to [stackoverflow](https://stackoverflow.com/questions/62671106/onactivityresult-method-is-deprecated-what-is-the-alternative)
     * answer of [Muntashir Akon](https://stackoverflow.com/users/4147849/muntashir-akon)
     * and
     * Refer to [stackoverflow](https://stackoverflow.com/questions/56651444/deprecated-getbitmap-with-api-29-any-alternative-codes)
     * answer of [Ally](https://stackoverflow.com/users/6258197/ally)
     *
     * @see .getFilePath
     *
     * @see .showImage
     */
    fun setActivityResultLauncher() {
        imageSearchAccountActivityResultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult(),
            object : ActivityResultCallback<ActivityResult?> {
                //                @RequiresApi(Build.VERSION_CODES.P)
                override fun onActivityResult(result: ActivityResult?) {
                    if (result!!.resultCode == RESULT_OK) {
                        // There are no request codes
                        val resultData = result.data
                        // doSomeOperations();
                        uri = null
                        filePath = getString(R.string.non_trovato)
                        //
                        if (resultData != null) {
                            uri = Objects.requireNonNull(resultData).data
                            //
                            try {
                                filePath = getFilePath(context, uri)
                            } catch (e: URISyntaxException) {
                                e.printStackTrace()
                            }
                            //
                            if (filePath != getString(R.string.non_trovato))
                            {
                                if (filePath == "da download") {
                                    val ctw = ContextThemeWrapper(context, R.style.CustomSnackbarTheme)
                                    val snackbar = Snackbar.make(
                                        ctw,
                                        findViewById(R.id.imageviewaccounticon),
                                        "al momento l'app non è in grado di accedere ad immagini nella cartella download",
                                        10000
                                    )
                                    snackbar.setTextMaxLines(5)
                                    snackbar.setTextColor(Color.BLACK)
                                    snackbar.show()
                                }
                                else {
                                    val takeFlags =
                                        resultData.flags and Intent.FLAG_GRANT_READ_URI_PERMISSION
                                    context.contentResolver.takePersistableUriPermission(
                                        uri!!,
                                        takeFlags
                                    )
                                    //
                                    var bitmap: Bitmap? = null
                                    //
                                    try {
                                        val source = ImageDecoder.createSource(
                                            context.contentResolver,
                                            uri!!
                                        )
                                        bitmap = ImageDecoder.decodeBitmap(source) { decoder, _, _ ->
                                            decoder.setTargetSampleSize(1) // shrinking by
                                            decoder.isMutableRequired = true // this resolve the hardware type of bitmap problem
                                        }
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                    }
                                    //
                                    //
                                    val stream = ByteArrayOutputStream()
                                    bitmap!!.compress(Bitmap.CompressFormat.PNG, 100, stream)
                                    byteArray = stream.toByteArray()
                                    //
                                    val myImage: ImageView
                                    myImage = findViewById<View>(R.id.imageviewaccounticon) as ImageView
                                    showImage(context, uri, myImage)
                                }
                            }
                        }
                    }
                }
            })
        //
    }
    /**
     * Called when the user taps the image search button.
     *
     * @param v view of tapped button
     */
    fun imageSearch(v: View) {
        // ACTION_OPEN_DOCUMENT is the intent to choose a file via the system's file
        // browser.
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        // Filter to only show results that can be "opened", such as a
        // file (as opposed to a list of contacts or timezones)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        // Filter to show only images, using the image MIME data type.
        // If one wanted to search for ogg vorbis files, the type would be "audio/ogg".
        // To search for all documents available via installed storage providers,
        // it would be "*/*".
        intent.type = "image/*"
        /*  the instructions of the button */
        imageSearchAccountActivityResultLauncher!!.launch(intent)
    }
    companion object {
        //
        const val EXTRA_MESSAGE = "helloworldandroidMessage"
    }
}