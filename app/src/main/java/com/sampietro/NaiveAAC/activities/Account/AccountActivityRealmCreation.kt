package com.sampietro.NaiveAAC.activities.Account

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Bundle
import android.view.ContextThemeWrapper
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentManager
import com.google.android.material.snackbar.Snackbar
import com.sampietro.NaiveAAC.R
import com.sampietro.NaiveAAC.activities.Arasaac.PictogramsAll
import com.sampietro.NaiveAAC.activities.Arasaac.PictogramsAllToModify
import com.sampietro.NaiveAAC.activities.Game.ChoiseOfGame.ChoiseOfGameActivity
import com.sampietro.NaiveAAC.activities.Game.GameParameters.GameParameters
import com.sampietro.NaiveAAC.activities.Grammar.ComplementsOfTheName
import com.sampietro.NaiveAAC.activities.Grammar.GrammaticalExceptions
import com.sampietro.NaiveAAC.activities.Grammar.ListsOfNames
import com.sampietro.NaiveAAC.activities.Grammar.Verbs
import com.sampietro.NaiveAAC.activities.Graphics.Images
import com.sampietro.NaiveAAC.activities.Graphics.Videos
import com.sampietro.NaiveAAC.activities.Phrases.Phrases
import com.sampietro.NaiveAAC.activities.Settings.AccountFragment
import com.sampietro.NaiveAAC.activities.BaseAndAbstractClass.AccountActivityAbstractClass
import com.sampietro.NaiveAAC.activities.DataStorage.DataStorageHelper.getFilePath
import com.sampietro.NaiveAAC.activities.Graphics.GraphicsAndPrintingHelper.showImage
import com.sampietro.NaiveAAC.activities.Settings.Utils.AdvancedSettingsDataImportExportHelper.findExternalStorageRoot
import com.sampietro.NaiveAAC.activities.Stories.Stories
import com.sampietro.NaiveAAC.activities.WordPairs.WordPairs
import com.sampietro.NaiveAAC.activities.history.History
import io.realm.Realm
import org.json.JSONArray
import org.json.JSONException
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
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
 * @version     1.0, 06/13/22
 * @see AccountActivityAbstractClass
 *
 * @see com.sampietro.NaiveAAC.activities.Settings.Utils.SettingsFragmentAbstractClass
 */
class AccountActivityRealmCreation : AccountActivityAbstractClass() {
    //
    var fragmentManager: FragmentManager? = null

    //
    private val FOLDER_SIMSIM = "simsim"

    //
    var textPersonName: String? = null

    //
    var infinitive = "nessun verbo"
    //
    /**
     * configurations of account start screen.
     *
     * @param savedInstanceState Define potentially saved parameters due to configurations changes.
     * @see .setActivityResultLauncher
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
        //
        if (isStoragePermissionGranted) {
            //    Log.v(TAGPERMISSION,getString(R.string.permission_is_granted));
        }
        //
    }

    /**
     * Called when the user taps the save account button.
     *
     * if the case creates creates the initial realm database.
     *
     * @param view view of tapped button
     * @see isStoragePermissionGranted
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
        if (isStoragePermissionGranted) {
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
            realm = Realm.getDefaultInstance()
            //
            if (realm.isEmpty) {

                //
                PictogramsAllToModify.importFromCsvFromInternalStorage(context, realm)
                // creo la collezione di pictogramsToModify (vedi java cap 39)
                val pictogramsToModifySet = HashSet<String?>()
                val resultsDB = realm.where(
                    PictogramsAllToModify::class.java
                ).findAll()
                for (taskitems in resultsDB) {
                    pictogramsToModifySet.add(taskitems.get_id())
                }
                // RECOVER ALL PICTOGRAMS FROM ARASAAC
                //
                var response: JSONArray? = null
                try {
                    response = JSONArray(loadJSONFromAsset("it.json"))
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
                for (i in 0 until response!!.length()) {
                    //
                    try {
                        val j = response.getJSONObject(i)
                        val _id = j.getString(getString(R.string._id))
                        //
                        // PICTOGRAMSALLTOMODIFY ID EXCLUSION
                        if (!pictogramsToModifySet.contains(_id)) {
                            //
                            val jcategories = j.getJSONArray(getString(R.string.categories))
                            //
                            val jKeywords = j.getJSONArray(getString(R.string.keywords))
                            for (ik in 0 until jKeywords.length()) {
                                val jKeyword = jKeywords.getJSONObject(ik)
                                val type = jKeyword.getString(getString(R.string.type))
                                val keyword = jKeyword.getString(getString(R.string.keyword))
                                //
                                if (type == getString(R.string.number_2) && jKeyword.has(getString(R.string.plural))) {
                                    val plural = jKeyword.getString(getString(R.string.plural))
                                    realm.beginTransaction()
                                    val p2 = realm.createObject(
                                        PictogramsAll::class.java
                                    )
                                    p2.set_id(_id)
                                    p2.type = type
                                    p2.keyword = plural
                                    p2.plural = "Y"
                                    p2.keywordPlural = " "
                                    realm.commitTransaction()
                                }
                                if (type == getString(R.string.number_1) || type == getString(R.string.number_2) || type == getString(
                                        R.string.number_3
                                    )
                                ) {
                                    realm.beginTransaction()
                                    val p = realm.createObject(
                                        PictogramsAll::class.java
                                    )
                                    p.set_id(_id)
                                    p.type = type
                                    p.keyword = keyword
                                    p.plural = getString(R.string.character_n)
                                    p.keywordPlural = " "
                                    if (jKeyword.has(getString(R.string.plural))) {
                                        val plural = jKeyword.getString(getString(R.string.plural))
                                        p.keywordPlural = plural
                                    }
                                    realm.commitTransaction()
                                }
                                //
                                if (type == getString(R.string.number_4) || type == getString(R.string.number_6)) {
                                    for (ic in 0 until jcategories.length()) {
                                        val jcategory = jcategories.getString(ic)
                                        if (jcategory == getString(R.string.article) || jcategory == getString(
                                                R.string.preposition
                                            ) || jcategory == getString(R.string.adverb_of_place) || jcategory == getString(
                                                R.string.number
                                            )
                                        ) {
                                            realm.beginTransaction()
                                            val cOTN = realm.createObject(
                                                ComplementsOfTheName::class.java
                                            )
                                            cOTN.set_id(_id)
                                            cOTN.type = type
                                            cOTN.keyword = keyword
                                            realm.commitTransaction()
                                            break
                                        }
                                    }
                                }
                            }
                            //
                        }
                        //
                    } catch (e: JSONException) {
                    }
                }

                //
                // RECOVER VERB CONJUGATION FROM ASSETS
                //
                var m_jArry: JSONArray? = null
                try {
                    m_jArry = JSONArray(loadJSONFromAsset("verbs.json"))
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
                //
                var conjugation: String
                var form: String
                var pv: Verbs
                for (iv in 0 until m_jArry!!.length()) {
                    try {
                        val joVerbs = m_jArry.getJSONObject(iv)
                        val jconjugations = joVerbs.getJSONArray("conjugations")
                        for (ic in 0 until jconjugations.length()) {
                            val jconjugation = jconjugations.getJSONObject(ic)
                            val group = jconjugation.getString("group")
                            when (group) {
                                "infinitive" -> infinitive =
                                    jconjugation.getString(getString(R.string.value))

                                "auxiliaryverb" -> {}
                                "indicative/pasthistoric" -> {}
                                "conditional/present" -> {}
                                "subjunctive/present" -> {}
                                "subjunctive/imperfect" -> {}
                                "indicative/present", "indicative/imperfect", "indicative/future" -> {
                                    conjugation = jconjugation.getString(getString(R.string.value))
                                    form = jconjugation.getString("form")
                                    //
                                    realm.beginTransaction()
                                    pv = realm.createObject(Verbs::class.java)
                                    pv.conjugation = conjugation
                                    pv.form = form
                                    pv.group = group
                                    pv.infinitive = infinitive
                                    realm.commitTransaction()
                                }

                                else -> {
                                    conjugation = jconjugation.getString(getString(R.string.value))
                                    //
                                    realm.beginTransaction()
                                    pv = realm.createObject(Verbs::class.java)
                                    pv.conjugation = conjugation
                                    pv.infinitive = infinitive
                                    realm.commitTransaction()
                                }
                            }
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }
                //
                //
                val daCancellare = realm.where(
                    History::class.java
                ).findAll()
                realm.beginTransaction()
                daCancellare.deleteAllFromRealm()
                realm.commitTransaction()
                //
                Images.importFromCsvFromInternalStorage(context, realm, "Replace")
                Videos.importFromCsvFromInternalStorage(context, realm, "Replace")
                //
                GameParameters.importFromCsvFromInternalStorage(context, realm, "Replace")
                GrammaticalExceptions.importFromCsvFromInternalStorage(context, realm, "Replace")
                ListsOfNames.importFromCsvFromInternalStorage(context, realm, "Replace")
                Phrases.importFromCsvFromInternalStorage(context, realm, "Replace")
                Stories.importFromCsvFromInternalStorage(context, realm, "Replace")
                WordPairs.importFromCsvFromInternalStorage(context, realm, "Replace")
                //
            }
        }
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
        editor.putString(getString(R.string.preference_list_mode), getString(R.string.sorted))
        editor.apply()
        editor.putInt("preference_AllowedMarginOfError", 20)
        editor.apply()
        // register the user in the shared preferences
        // and move on to the welcome activity
        val editText = findViewById<View>(R.id.editTextTextAccount) as EditText
        textPersonName = editText.text.toString()
        // default
        if (textPersonName!!.length <= 0) textPersonName = "utente"
        if (filePath == getString(R.string.non_trovato)) {
            val utenteFile = getFileStreamPath("utente.png")
            filePath = utenteFile.absolutePath
        }
        //
        if (textPersonName!!.length > 0 && filePath != getString(R.string.non_trovato)) {
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
//            wordPairs.isMenuItem = getString(R.string.slm)
            wordPairs.awardType = ""
            wordPairs.uriPremiumVideo = ""
            realm.commitTransaction()
            //
            val destination = openFileOutput("copiarealm")
            realm.writeCopyTo(destination)
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
            copyFileCsvFromAssetsToInternalStorage("stories.csv")
            copyFileCsvFromAssetsToInternalStorage("videos.csv")
            copyFileCsvFromAssetsToInternalStorage("wordpairs.csv")
        } catch (e: IOException) {
            e.printStackTrace()
        }
        //
        copyAssets("images")
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
        var bytesRead = 0
        while (sourceStream.read(buffer).also { bytesRead = it } != -1) {
            destStream.write(buffer, 0, bytesRead)
        }
        destStream.close()
        sourceStream.close()
    }
    //
    /**
     * load a local json file from assets.
     *
     *
     * Refer to [stackoverflow](https://stackoverflow.com/questions/19945411/how-can-i-parse-a-local-json-file-from-assets-folder-into-a-listview)
     * answer of [grishu](https://stackoverflow.com/users/1839336/grishu)
     *
     * @param file string with the name of the file to be loaded
     * @return json string json to parse
     */
    fun loadJSONFromAsset(file: String?): String? {
        var json: String? = null
        json = try {
            val `is` = this.assets.open(file!!)
            val size = `is`.available()
            val buffer = ByteArray(size)
            `is`.read(buffer)
            `is`.close()
            buffer.toString(Charsets.UTF_8)
//            kotlin.String(buffer, StandardCharsets.UTF_8)
        } catch (ex: IOException) {
            ex.printStackTrace()
            return null
        }
        return json
    }

    //
    val isStoragePermissionGranted: Boolean
        /**
         * check permissions.
         *
         * @return boolean whit true if permission is granted
         */
        get() =
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED
            ) {
                // Log.v(TAGPERMISSION,getString(R.string.permission_is_granted));
                true
            } else {

                // Log.v(TAGPERMISSION,getString(R.string.permission_is_revoked));
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    1
                )
                false
            }

    /**
     * open the copy realm file
     *
     * @param fileName string name of the output file
     * @return file
     * @see com.sampietro.NaiveAAC.activities.Settings.Utils.AdvancedSettingsDataImportExportHelper.findExternalStorageRoot
     */
    fun openFileOutput(fileName: String?): File {
        val root = findExternalStorageRoot()
        //
        val dir =
            File(root!!.absolutePath + getString(R.string.character_slash) + getString(R.string.app_name))
        val file = File(dir, fileName!!)
        file.setReadable(true, true)
        return file
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
    } //    /**

    // ActivityResultLauncher
    var imageSearchAccountActivityResultLauncher: ActivityResultLauncher<Intent>? = null
    @JvmField
    var uri: Uri? = null
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
     * @see getFilePath
     *
     * @see showImage
     */
    fun setActivityResultLauncher() {
        // You can do the assignment inside onAttach or onCreate, i.e, before the activity is displayed
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
        private const val TAG = "VERBO"
        private const val TAGPERMISSION = "Permission"

        //
        const val EXTRA_MESSAGE = "helloworldandroidMessage"
    }
}