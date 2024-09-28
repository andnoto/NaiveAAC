package com.sampietro.NaiveAAC.activities.Settings

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.OpenableColumns
import android.view.View
import android.widget.EditText
import android.widget.RadioButton
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.documentfile.provider.DocumentFile
import androidx.fragment.app.FragmentManager
import com.sampietro.NaiveAAC.R
import com.sampietro.NaiveAAC.activities.BaseAndAbstractClass.ActivityAbstractClass
import com.sampietro.NaiveAAC.activities.DataStorage.DataStorageHelper.copyFile
import com.sampietro.NaiveAAC.activities.DataStorage.DataStorageHelper.copyFileFromSharedToInternalStorage
import com.sampietro.NaiveAAC.activities.DataStorage.DataStorageHelper.copyFileFromSharedToInternalStorageAndGetPath
import com.sampietro.NaiveAAC.activities.DataStorage.DataStorageHelper.copyFileZipFromInternalToSharedStorage
import com.sampietro.NaiveAAC.activities.DataStorage.DataStorageHelper.copyFilesInFolderToRoot
import com.sampietro.NaiveAAC.activities.DataStorage.DataStorageHelper.extractFolder
import com.sampietro.NaiveAAC.activities.DataStorage.DataStorageHelper.zipFileAtPath
import com.sampietro.NaiveAAC.activities.Game.GameParameters.GameParameters
import com.sampietro.NaiveAAC.activities.Game.GameParameters.GameParameters.Companion.exporttoCsv
import com.sampietro.NaiveAAC.activities.Game.Utils.ActionbarFragment
import com.sampietro.NaiveAAC.activities.Graphics.Sounds
import com.sampietro.NaiveAAC.activities.Graphics.Videos
import com.sampietro.NaiveAAC.activities.Stories.Stories
import com.sampietro.NaiveAAC.activities.Stories.Stories.Companion.exporttoCsv
import com.sampietro.NaiveAAC.activities.Stories.Stories.Companion.importStoryFromCsvFromInternalStorage
import com.sampietro.NaiveAAC.activities.Stories.StoriesHelper.renumberAStory
import io.realm.Realm
import java.io.*
import java.net.URISyntaxException
import java.util.*

/**
 * <h1>SettingsStoriesImportExportActivity</h1>
 *
 * **SettingsStoriesImportExportActivity** app settings.
 * Refer to [developer.android.com](https://developer.android.com/guide/fragments/communicate)
 *
 * @version 5.0, 01/04/2024
 * @see ActivityAbstractClass
 */
class SettingsStoriesImportExportActivity : ActivityAbstractClass()
    {
//    var message = "messaggio non formato"
//    var textView: TextView? = null

    //
    var fragmentManager: FragmentManager? = null

    //
    var story: String? = null
    //
    var radiobuttonStoriesImportButtonClicked = false
    var radiobuttonStoriesExportButtonClicked = false

    // ActivityResultLauncher
    var exportStorySearchActivityResultLauncher: ActivityResultLauncher<Intent>? = null
    var importStorySearchActivityResultLauncher: ActivityResultLauncher<Intent>? = null
//    var storyTreeUri: Uri? = null

    /**
     * configurations of settings start screen.
     *
     * @param savedInstanceState Define potentially saved parameters due to configurations changes.
     * @see setExportStorySearchActivityResultLauncher
     * @see ActionbarFragment
     * @see Activity.onCreate
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        //
        context = this
        //
        setExportStorySearchActivityResultLauncher()
        setImportStorySearchActivityResultLauncher()
        // The MainActivity class provides an instance of Realm wherever needed in the application.
        // To use it in the Activity, a reference must be obtained to a Realm object in the onCreate method.
        // The Realm object must be closed in the onDestroy method.
        realm = Realm.getDefaultInstance()
        //
        story = getString(R.string.nome_storia)
        //
        if (savedInstanceState != null) {
            // The onSaveInstanceState method is called before an activity may be killed
            // (for Screen Rotation Handling) so that
            // when it comes back it can restore its state.
            story = savedInstanceState.getString("STORY", "")
        }
        else
        {
            fragmentManager = supportFragmentManager
            fragmentManager!!.beginTransaction()
                .add(ActionbarFragment(), getString(R.string.actionbar_fragment))
                .commit()
            val frag = StoriesImportExportFragment()
            val bundle = Bundle()
            bundle.putString("STORY", story)
            frag.arguments = bundle
            val ft = supportFragmentManager.beginTransaction()
            ft.add(R.id.settings_container, frag, "StoriesImportExportFragment")
            ft.addToBackStack(null)
            ft.commitAllowingStateLoss()
        }
    }
    /**
     * This method is called before an activity may be killed so that when it comes back some time in the future it can restore its state..
     *
     *
     * it stores the image video or sound file path
     *
     * @param savedInstanceState Define potentially saved parameters due to configurations changes.
     */
    public override fun onSaveInstanceState(savedInstanceState: Bundle) {
        savedInstanceState.putString("STORY", story)
        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState)
    }
    /**
     * initiate Fragment transaction.
     *
     *
     *
     * @see StoriesImportExportFragment
     */
    fun fragmentTransactionStart() {
        val frag = StoriesImportExportFragment()
        val bundle = Bundle()
        bundle.putString("STORY", story)
        frag.arguments = bundle
        val ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.settings_container, frag, "StoriesImportExportFragment")
        ft.addToBackStack(null)
        ft.commitAllowingStateLoss()
    }
     /**
     * Called when the user taps the add button from stories settings.
     *
     * Refer to [stackoverflow](https://stackoverflow.com/questions/65203681/how-to-create-multiple-files-at-once-using-android-storage-access-framework)
     * answer of [Ismail Osunlana](https://stackoverflow.com/users/11355432/ismail-osunlana)
     *
     * @param v view of tapped button
     */
    fun storiesImportExport(v: View?) {
        val stieRBImport = findViewById<View>(R.id.radio_import) as RadioButton
        val stieRBExport = findViewById<View>(R.id.radio_export) as RadioButton
//        if (radiobuttonStoriesExportButtonClicked) {
        if (stieRBExport.isChecked()) {
            // Choose a directory using the system's file picker.
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)
            // Optionally, specify a URI for the directory that should be opened in
            // the system file picker when it loads.
            intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, Environment.DIRECTORY_DOWNLOADS)
            exportStorySearchActivityResultLauncher!!.launch(intent)
        }
//        if (radiobuttonStoriesImportButtonClicked) {
        if (stieRBImport.isChecked()) {
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
            intent.type = "application/zip"
            importStorySearchActivityResultLauncher!!.launch(intent)
        }
    }

    /**
     * Called when the user click the radio button import / export from the stories import / export settings.
     *
     * register which radio button was clicked
     *
     * @param view view of clicked radio button
     * @see StoriesImportExportFragment
     */
    fun onStoriesImportExportButtonClicked(view: View) {
        // Is the button now checked?
        val checked = (view as RadioButton).isChecked
        when (view.getId()) {
            R.id.radio_import -> if (checked) {
                //
                radiobuttonStoriesImportButtonClicked = true
                radiobuttonStoriesExportButtonClicked = false
            }
            R.id.radio_export -> if (checked) {
                //
                radiobuttonStoriesImportButtonClicked = false
                radiobuttonStoriesExportButtonClicked = true
            }
        }
    }

    /**
     * Called when the user taps the story search button .
     *
     * the activity is notified to view the stories list.
     *
     *
     * @param view view of tapped button
     * @see StoriesImportExportFragment
     */
    fun storiesSearch(view: View?) {
        val textWord1 = findViewById<View>(R.id.storytosearch) as EditText
        story =
            textWord1.text.toString().lowercase(Locale.getDefault())
        //
        fragmentTransactionStart()
    }

    /**
     * Called when the user taps export tables button.
     *
     * at the end the activity is notified to view the settings menu.
     *
     * @see isStoragePermissionGranted
     *
     * @see copyFile
     *
     * @see zipFileAtPath
     *
     * @see copyFileFromSharedToInternalStorage
     *
     * @see extractFolder
     *
     * @see copyFilesInFolderToRoot
     *
     * @see StoriesImportExportFragment
     *
     * @see com.sampietro.NaiveAAC.activities.Graphics.Images
     *
     * @see Videos
     *
     * @see Sounds
     *
     * @see GameParameters
     *
     * @see Stories
     */
    fun setExportStorySearchActivityResultLauncher() {
        //
        // You can do the assignment inside onAttach or onCreate, i.e, before the activity is displayed
        exportStorySearchActivityResultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult(),
            object : ActivityResultCallback<ActivityResult?> {
                override fun onActivityResult(result: ActivityResult?) {
                    if (result!!.resultCode == RESULT_OK) {
                        // There are no request codes
                        val resultData = result.data
                        // doSomeOperations();
                        var storyTreeUri: Uri? = null
                        //                            filePath = getString(R.string.non_trovato);
                        if (resultData != null) {
                            storyTreeUri = Objects.requireNonNull(resultData).data
                            val zipFolder = DocumentFile.fromTreeUri(context, storyTreeUri!!)
                            //
//                            if (isStoragePermissionGranted)
                            if ((isStoragePermissionGranted) || (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU))
                            // if the version code >= TIRAMISU (version 33 = Android 13)
                            // you don't need to ask for StoragePermission
                            {
                                        story =
                                            getString(R.string.nome_storia)
                                        //
                                        realm = Realm.getDefaultInstance()
                                        //
                                        val textWord1 =
                                            findViewById<View>(R.id.storytosearch) as EditText
                                        val stieRBImport = findViewById<View>(R.id.radio_import) as RadioButton
                                        val stieRBExport = findViewById<View>(R.id.radio_export) as RadioButton
                                        if (textWord1.text.toString() != "" &&
//                                            !(radiobuttonStoriesImportButtonClicked && radiobuttonStoriesExportButtonClicked) &&
//                                            !(!radiobuttonStoriesImportButtonClicked && !radiobuttonStoriesExportButtonClicked)
                                            !(stieRBImport.isChecked() && stieRBExport.isChecked()) &&
                                            !(!stieRBImport.isChecked() && !stieRBExport.isChecked())
                                        ) {
                                                val resultsStories = realm.where(
                                                    Stories::class.java
                                                )
                                                    .equalTo(
                                                        getString(R.string.story),
                                                        textWord1.text.toString()
                                                            .lowercase(Locale.getDefault())
                                                    )
                                                    .findAll()
                                                //
                                                val resultsGameParameters = realm.where(
                                                    GameParameters::class.java
                                                )
                                                    .equalTo(
                                                        "gameParameter",
                                                        textWord1.text.toString().lowercase(
                                                            Locale.getDefault()
                                                        )
                                                    )
                                                    .findAll()
                                                //
                                                // creo due set (in modo da non ammettere duplicati) con la descrizione dei video e dei suoni utilizzati dalla storia
                                                val videosDescriptionSet = HashSet<String?>()
                                                val soundsDescriptionSet = HashSet<String?>()
                                                // creo tre set (in modo da non ammettere duplicati) con i media file utilizzati dalla storia
                                                val imagesSet = HashSet<String?>()
                                                val videosSet = HashSet<String?>()
                                                val soundsSet = HashSet<String?>()
                                                //
                                                val count = resultsStories.size
                                                if (count != 0) {
                                                    var irrh = 0
                                                    while (irrh < count) {
                                                        val resultStories = resultsStories[irrh]!!
                                                        val wordNumber = resultStories.wordNumberInt
                                                        //
                                                        if (wordNumber != 0 && wordNumber != 99
                                                            && wordNumber != 999 && wordNumber != 9999
                                                        ) {
                                                            // immagini
                                                            if (resultStories.uriType != "A") imagesSet.add(
                                                                resultStories.uri
                                                            )
                                                            // video
                                                            val resultsVideos = realm.where(
                                                                Videos::class.java
                                                            ).equalTo(getString(R.string.descrizione), resultStories.video)
                                                                .findAll()
                                                            if (resultsVideos.size != 0) {
                                                                assert(resultsVideos[0] != null)
                                                                videosDescriptionSet.add(
                                                                    resultsVideos[0]!!.descrizione
                                                                )
                                                                videosSet.add(resultsVideos[0]!!.uri)
                                                            }
                                                            // suoni
                                                            val resultsSounds = realm.where(
                                                                Sounds::class.java
                                                            ).equalTo(getString(R.string.descrizione), resultStories.sound)
                                                                .findAll()
                                                            if (resultsSounds.size != 0) {
                                                                assert(resultsSounds[0] != null)
                                                                soundsDescriptionSet.add(
                                                                    resultsSounds[0]!!.descrizione
                                                                )
                                                                soundsSet.add(resultsSounds[0]!!.uri)
                                                            }
                                                        }
                                                        if (wordNumber == 999) {
                                                            // video
                                                            if (resultStories.answerActionType == "V") {
                                                                val resultsVideos = realm.where(
                                                                    Videos::class.java
                                                                ).equalTo(
                                                                    getString(R.string.descrizione),
                                                                    resultStories.answerAction
                                                                ).findAll()
                                                                if (resultsVideos.size != 0) {
                                                                    assert(resultsVideos[0] != null)
                                                                    videosDescriptionSet.add(
                                                                        resultsVideos[0]!!.descrizione
                                                                    )
                                                                    videosSet.add(resultsVideos[0]!!.uri)
                                                                }
                                                            }
                                                        }
                                                        irrh++
                                                    }
                                                }
                                                // trasformo i tre set ottenuti in tre list contenenti i path dei media file utilizzati dalla storia
                                                val imagesList: MutableList<String?> = ArrayList()
                                                imagesList.addAll(imagesSet)
                                                //
                                                val videosList: MutableList<String?> = ArrayList()
                                                videosList.addAll(videosSet)
                                                val videosSize = videosList.size
                                                var irrh = 0
                                                while (irrh < videosSize) {
                                                    val resultsVideos = realm.where(
                                                        Videos::class.java
                                                    ).equalTo(getString(R.string.descrizione), videosList[irrh])
                                                        .findAll()
                                                    if (resultsVideos.size != 0) {
                                                        videosList[irrh] = resultsVideos[0]!!.uri
                                                    }
                                                    irrh++
                                                }
                                                //
                                                val soundsList: MutableList<String?> = ArrayList()
                                                soundsList.addAll(soundsSet)
                                                val soundsSize = soundsList.size
                                                irrh = 0
                                                while (irrh < soundsSize) {
                                                    val resultsSounds = realm.where(
                                                        Sounds::class.java
                                                    ).equalTo(getString(R.string.descrizione), soundsList[irrh])
                                                        .findAll()
                                                    if (resultsSounds.size != 0) {
                                                        soundsList[irrh] = resultsSounds[0]!!.uri
                                                    }
                                                    irrh++
                                                }
                                                // creo le dir della storia
                                                val rootPath = context.filesDir.absolutePath
                                                val dirName = textWord1.text.toString()
                                                    .lowercase(Locale.getDefault())
                                                val d = File("$rootPath/$dirName")
                                                if (!d.exists()) {
                                                    d.mkdirs()
                                                } else {
                                                    d.delete()
                                                    d.mkdirs()
                                                }
                                                val dcsv = File("$rootPath/$dirName/csv")
                                                dcsv.mkdirs()
                                                val dimages = File("$rootPath/$dirName/images")
                                                dimages.mkdirs()
                                                val dvideos = File("$rootPath/$dirName/videos")
                                                dvideos.mkdirs()
                                                val dsounds = File("$rootPath/$dirName/sounds")
                                                dsounds.mkdirs()
                                                // creo file csv
                                                exporttoCsv(context, realm, resultsStories, dcsv)
                                                exporttoCsv(
                                                    context,
                                                    realm,
                                                    resultsGameParameters,
                                                    dcsv
                                                )
                                                //
                                                Videos.exporttoCsv(
                                                    context,
                                                    realm,
                                                    videosDescriptionSet,
                                                    dcsv
                                                )
                                                //
                                                Sounds.exporttoCsv(
                                                    context,
                                                    realm,
                                                    soundsDescriptionSet,
                                                    dcsv
                                                )
                                                // creo copia dei file immagini
                                                //
                                                var finput: File
                                                var foutput: File
                                                val imagesSize = imagesList.size
                                                irrh = 0
                                                while (irrh < imagesSize) {
                                                    finput = File(imagesList[irrh]!!)
                                                    foutput = File(
                                                        "$rootPath/$dirName/images/" + imagesList[irrh]!!
                                                            .substring(
                                                                imagesList[irrh]!!.lastIndexOf(
                                                                    "/"
                                                                ) + 1
                                                            )
                                                    )
                                                    try {
                                                        copyFile(finput, foutput)
                                                    } catch (e: IOException) {
                                                        e.printStackTrace()
                                                    }
                                                    irrh++
                                                }
                                                // creo copia dei file video
                                                irrh = 0
                                                while (irrh < videosSize) {
                                                    finput = File(videosList[irrh]!!)
                                                    foutput = File(
                                                        "$rootPath/$dirName/videos/" + videosList[irrh]!!
                                                            .substring(
                                                                videosList[irrh]!!.lastIndexOf(
                                                                    "/"
                                                                ) + 1
                                                            )
                                                    )
                                                    try {
                                                        copyFile(finput, foutput)
                                                    } catch (e: IOException) {
                                                        e.printStackTrace()
                                                    }
                                                    irrh++
                                                }
                                                // creo copia dei file suono
                                                irrh = 0
                                                while (irrh < soundsSize) {
                                                    finput = File(soundsList[irrh]!!)
                                                    foutput = File(
                                                        "$rootPath/$dirName/sounds/" + soundsList[irrh]!!
                                                            .substring(
                                                                soundsList[irrh]!!.lastIndexOf(
                                                                    "/"
                                                                ) + 1
                                                            )
                                                    )
                                                    try {
                                                        copyFile(finput, foutput)
                                                    } catch (e: IOException) {
                                                        e.printStackTrace()
                                                    }
                                                    irrh++
                                                }
                                                // zippo la directory di output
                                                zipFileAtPath(
                                                    "$rootPath/$dirName",
                                                    "$rootPath/$dirName.zip"
                                                )
                                                assert(zipFolder != null)
                                                // copio il file zip sulla shared storage
                                                try {
                                                    copyFileZipFromInternalToSharedStorage(
                                                        context,
                                                        zipFolder,
                                                        "$dirName.zip"
                                                    )
                                                } catch (e: IOException) {
                                                    e.printStackTrace()
                                                }
                                            //
                                            story =
                                                textWord1.text.toString().lowercase(
                                                    Locale.getDefault()
                                                )
                                        }
                                        fragmentTransactionStart()
                            }
                        }
                    }
                }
            })
    }
    /**
     * Called when the user taps export tables button.
     *
     * at the end the activity is notified to view the settings menu.
     *
     * @see isStoragePermissionGranted
     *
     * @see copyFile
     *
     * @see zipFileAtPath
     *
     * @see copyFileFromSharedToInternalStorage
     *
     * @see extractFolder
     *
     * @see copyFilesInFolderToRoot
     *
     * @see StoriesImportExportFragment
     *
     * @see com.sampietro.NaiveAAC.activities.Graphics.Images
     *
     * @see Videos
     *
     * @see Sounds
     *
     * @see GameParameters
     *
     * @see Stories
     */
    fun setImportStorySearchActivityResultLauncher() {
        //
        // You can do the assignment inside onAttach or onCreate, i.e, before the activity is displayed
        importStorySearchActivityResultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult(),
            object : ActivityResultCallback<ActivityResult?> {
                override fun onActivityResult(result: ActivityResult?) {
                    if (result!!.resultCode == RESULT_OK) {
                        val resultData = result.data
                        // doSomeOperations();
                        if (resultData != null) {
                            val uri = Objects.requireNonNull(resultData).data
                            //
                            try {
                                val filePath =
                                    copyFileFromSharedToInternalStorageAndGetPath(
                                        context,
                                        uri!!
                                    )
                            } catch (e: URISyntaxException) {
                                e.printStackTrace()
                            }
                            // The query, because it only applies to a single document, returns only
                            // one row. There's no need to filter, sort, or select fields,
                            // because we want all fields for one document.
                            val cursor: Cursor? = context.contentResolver.query(
                                uri!!, null, null, null, null, null)
                            var displayName: String? = null
                            cursor?.use {
                                // moveToFirst() returns false if the cursor has 0 rows. Very handy for
                                // "if there's anything to look at, look at it" conditionals.
                                if (it.moveToFirst()) {
                                    // Note it's called "Display Name". This is
                                    // provider-specific, and might not necessarily be the file name.
                                    val myCursorColumnIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                                    displayName =
                                        it.getString(myCursorColumnIndex)
                                }
                            }
                            //
                            story =
                                getString(R.string.nome_storia)
                            //
                            realm = Realm.getDefaultInstance()
                            //
                            // creo le dir della storia
                            val rootPath = context.filesDir.absolutePath
                            val dirName = displayName!!.substring(0,
                                displayName!!.indexOf("."))
                            val d = File("$rootPath/$dirName")
                            if (!d.exists()) {
                                d.mkdirs()
                            } else {
                                d.delete()
                                d.mkdirs()
                            }
                            val dcsv = File("$rootPath/$dirName/csv")
                            dcsv.mkdirs()
                            val dimages = File("$rootPath/$dirName/images")
                            dimages.mkdirs()
                            val dvideos = File("$rootPath/$dirName/videos")
                            dvideos.mkdirs()
                            val dsounds = File("$rootPath/$dirName/sounds")
                            dsounds.mkdirs()
                            // unzippo la directory di input
                            try {
                                extractFolder(
                                    File(rootPath),
                                    File("$rootPath/$dirName.zip")
                                )
                            } catch (e: IOException) {
                                e.printStackTrace()
                            }
                            // creo copia dei file suono
                            copyFilesInFolderToRoot(context,"$rootPath/$dirName/sounds")
                            // creo copia dei file video
                            copyFilesInFolderToRoot(context,"$rootPath/$dirName/videos")
                            // creo copia dei file immagini
                            copyFilesInFolderToRoot(context,"$rootPath/$dirName/images")
                            // creo file csv
                            copyFilesInFolderToRoot(context,"$rootPath/$dirName/csv")
                            // creo la storia
                            Sounds.importFromCsvFromInternalStorage(
                                context,
                                realm
                            )
                            Videos.importFromCsvFromInternalStorage(
                                context,
                                realm
                            )
                            importStoryFromCsvFromInternalStorage(
                                context,
                                realm,
                                dirName
                            )
                            renumberAStory(realm, dirName)
                            GameParameters.importFromCsvFromInternalStorage(
                                context,
                                realm
                            )
                            story =
                                dirName
                            fragmentTransactionStart()
                        }
                    }
                }
            })
    }
    /**
     * check permissions.
     *
     * @return boolean whit true if permission is granted
     */
    val isStoragePermissionGranted: Boolean
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
    companion object {
    }
}