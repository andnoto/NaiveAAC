package com.sampietro.NaiveAAC.activities.Settings

import android.Manifest
import com.sampietro.NaiveAAC.activities.Stories.Stories.Companion.exporttoCsv
import com.sampietro.NaiveAAC.activities.Game.GameParameters.GameParameters.Companion.exporttoCsv
import com.sampietro.NaiveAAC.activities.Stories.Stories.Companion.importStoryFromCsvFromInternalStorage
import com.sampietro.NaiveAAC.activities.Stories.StoriesHelper.renumberAStory
import com.sampietro.NaiveAAC.activities.Settings.Utils.AccountActivityAbstractClass
import com.sampietro.NaiveAAC.activities.Settings.Utils.SettingsFragmentAbstractClass.onFragmentEventListenerSettings
import android.widget.TextView
import com.sampietro.NaiveAAC.activities.Stories.VoiceToBeRecordedInStories
import com.sampietro.NaiveAAC.activities.Stories.VoiceToBeRecordedInStoriesViewModel
import androidx.activity.result.ActivityResultLauncher
import android.content.Intent
import android.os.Bundle
import com.sampietro.NaiveAAC.R
import androidx.lifecycle.ViewModelProvider
import com.sampietro.NaiveAAC.activities.Game.Utils.ActionbarFragment
import android.provider.DocumentsContract
import android.os.Environment
import android.widget.RadioButton
import android.widget.EditText
import androidx.activity.result.ActivityResultCallback
import android.app.Activity
import androidx.documentfile.provider.DocumentFile
import androidx.lifecycle.LifecycleOwner
import com.sampietro.NaiveAAC.activities.Stories.Stories
import com.sampietro.NaiveAAC.activities.Game.GameParameters.GameParameters
import com.sampietro.NaiveAAC.activities.Graphics.Videos
import com.sampietro.NaiveAAC.activities.Graphics.Sounds
import android.content.pm.PackageManager
import android.net.Uri
import android.view.View
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentManager
import io.realm.Realm
import java.io.*
import java.lang.Exception
import java.util.*
import java.util.zip.ZipEntry
import java.util.zip.ZipException
import java.util.zip.ZipFile
import java.util.zip.ZipOutputStream
import kotlin.Throws

/**
 * <h1>SettingsStoriesImportExportActivity</h1>
 *
 * **SettingsStoriesImportExportActivity** app settings.
 * Refer to [developer.android.com](https://developer.android.com/guide/fragments/communicate)
 *
 * @version 4.0, 09/09/2023
 * @see AccountActivityAbstractClass
 */
class SettingsStoriesImportExportActivity : AccountActivityAbstractClass(),
    onFragmentEventListenerSettings {
    var message = "messaggio non formato"
    var textView: TextView? = null

    //
    var fragmentManager: FragmentManager? = null

    //
    private var voiceToBeRecordedInStories: VoiceToBeRecordedInStories? = null
    private var viewModel: VoiceToBeRecordedInStoriesViewModel? = null

    //
    var radiobuttonStoriesImportButtonClicked = false
    var radiobuttonStoriesExportButtonClicked = false

    // ActivityResultLauncher
    var exportStorySearchActivityResultLauncher: ActivityResultLauncher<Intent>? = null
    var storyTreeUri: Uri? = null

    /**
     * configurations of settings start screen.
     *
     * @param savedInstanceState Define potentially saved parameters due to configurations changes.
     * @see .setActivityResultLauncher
     *
     * @see ActionbarFragment
     *
     * @see ContentsFragment
     *
     * @see Activity.onCreate
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        //
        context = this
        //
        setExportStorySearchActivityResultLauncher()
        /*
        Both your fragment and its host activity can retrieve a shared instance of a ViewModel with activity scope by passing the activity into the ViewModelProvider
        constructor.
        The ViewModelProvider handles instantiating the ViewModel or retrieving it if it already exists. Both components can observe and modify this data
         */
        // In the Activity#onCreate make the only setItem
        voiceToBeRecordedInStories = VoiceToBeRecordedInStories()
        viewModel = ViewModelProvider(this).get(
            VoiceToBeRecordedInStoriesViewModel::class.java
        )
        viewModel!!.setItem(voiceToBeRecordedInStories!!)
        voiceToBeRecordedInStories!!.story = getString(R.string.nome_storia)
        //
        if (savedInstanceState == null) {
            fragmentManager = supportFragmentManager
            fragmentManager!!.beginTransaction()
                .add(ActionbarFragment(), getString(R.string.actionbar_fragment))
                .add(
                    R.id.settings_container,
                    StoriesImportExportFragment(),
                    "StoriesImportExportFragment"
                )
                .commit()
        }
        // The MainActivity class provides an instance of Realm wherever needed in the application.
        // To use it in the Activity, a reference must be obtained to a Realm object in the onCreate method.
        // The Realm object must be closed in the onDestroy method.
        realm = Realm.getDefaultInstance()
    }
    //
    /**
     * receives calls from fragment listeners.
     *
     * @param v view of calling fragment
     * @see com.sampietro.NaiveAAC.activities.Settings.Utils.SettingsFragmentAbstractClass
     */
    override fun receiveResultSettings(v: View?) {
        rootViewFragment = v
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
        // Choose a directory using the system's file picker.
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)
        // Optionally, specify a URI for the directory that should be opened in
        // the system file picker when it loads.
        intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, Environment.DIRECTORY_DOWNLOADS)
        exportStorySearchActivityResultLauncher!!.launch(intent)
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
        val textWord1 = rootViewFragment!!.findViewById<View>(R.id.storytosearch) as EditText
        // Viewmodel
        // In the activity, sometimes it is called observe, other times it is limited to performing set directly
        // (maybe it is not necessary to call observe)
        voiceToBeRecordedInStories!!.story =
            textWord1.text.toString().lowercase(Locale.getDefault())
        //
        val frag = StoriesImportExportFragment()
        val ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.settings_container, frag)
        ft.addToBackStack(null)
        ft.commit()
    }

    /**
     * Called when the user taps export tables button.
     *
     * at the end the activity is notified to view the settings menu.
     *
     * @see isStoragePermissionGranted
     *
     * @see copy
     *
     * @see zipFileAtPath
     *
     * @see copyFileFromInternalToSharedStorage
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
                        storyTreeUri = null
                        //                            filePath = getString(R.string.non_trovato);
                        if (resultData != null) {
                            storyTreeUri = Objects.requireNonNull(resultData).data
                            val zipFolder = DocumentFile.fromTreeUri(context, storyTreeUri!!)
                            //
                            if (isStoragePermissionGranted) {
                                //
                                // Viewmodel
                                // In the activity, sometimes it is called observe, other times it is limited to performing set directly
                                // (maybe it is not necessary to call observe)
                                viewModel!!.getSelectedItem()
                                    .observe((context as LifecycleOwner)) { voiceToBeRecordedInStories: VoiceToBeRecordedInStories ->
                                        // Perform an action with the latest item data
                                        voiceToBeRecordedInStories.story =
                                            getString(R.string.nome_storia)
                                        //
                                        realm = Realm.getDefaultInstance()
                                        //
                                        val textWord1 =
                                            findViewById<View>(R.id.storytosearch) as EditText
                                        if (textWord1.text.toString() != "" && !(radiobuttonStoriesImportButtonClicked && radiobuttonStoriesExportButtonClicked) && !(!radiobuttonStoriesImportButtonClicked && !radiobuttonStoriesExportButtonClicked)
                                        ) {
                                            if (radiobuttonStoriesExportButtonClicked) {
                                                val resultsStories = realm.where(
                                                    Stories::class.java
                                                )
                                                    .equalTo(
                                                        "story",
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
                                                            ).equalTo("descrizione", resultStories.video)
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
                                                            ).equalTo("descrizione", resultStories.sound)
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
                                                                    "descrizione",
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
                                                    ).equalTo("descrizione", videosList[irrh])
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
                                                    ).equalTo("descrizione", soundsList[irrh])
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
                                                        copy(finput, foutput)
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
                                                        copy(finput, foutput)
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
                                                        copy(finput, foutput)
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
//                                                    copyFileZipFromInternalToSharedStorage(zipFolder,dirName + ".zip");
                                                    copyFileZipFromInternalToSharedStorage(
                                                        zipFolder,
                                                        "$dirName.zip"
                                                    )
                                                } catch (e: IOException) {
                                                    e.printStackTrace()
                                                }
                                            }
                                            if (radiobuttonStoriesImportButtonClicked) {
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
                                                assert(zipFolder != null)
                                                val documentFileNewFile =
                                                    zipFolder!!.findFile("$dirName.zip")!!
                                                val zipFileUri = documentFileNewFile.uri
                                                try {
                                                    copyFileFromSharedToInternalStorage(
                                                        zipFileUri,
                                                        "$dirName.zip"
                                                    )
                                                } catch (e: IOException) {
                                                    e.printStackTrace()
                                                }
                                                // unzippo la directory di input
                                                // errore qui
                                                try {
                                                    extractFolder(
                                                        File(rootPath),
                                                        File("$rootPath/$dirName.zip")
                                                    )
                                                } catch (e: IOException) {
                                                    e.printStackTrace()
                                                }
                                                // creo copia dei file suono
                                                copyFilesInFolderToRoot("$rootPath/$dirName/sounds")
                                                // creo copia dei file video
                                                copyFilesInFolderToRoot("$rootPath/$dirName/videos")
                                                // creo copia dei file immagini
                                                copyFilesInFolderToRoot("$rootPath/$dirName/images")
                                                // creo file csv
                                                copyFilesInFolderToRoot("$rootPath/$dirName/csv")
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
                                            }
                                            // clear fields of viewmodel data class
                                            voiceToBeRecordedInStories.story =
                                                textWord1.text.toString().lowercase(
                                                    Locale.getDefault()
                                                )
                                            voiceToBeRecordedInStories.phraseNumberInt = 0
                                            voiceToBeRecordedInStories.wordNumberInt = 0
                                            voiceToBeRecordedInStories.word = ""
                                            voiceToBeRecordedInStories.uriType = ""
                                            voiceToBeRecordedInStories.uri = ""
                                            voiceToBeRecordedInStories.answerActionType = ""
                                            voiceToBeRecordedInStories.answerAction = ""
                                            voiceToBeRecordedInStories.video = ""
                                            voiceToBeRecordedInStories.sound = ""
                                            voiceToBeRecordedInStories.soundReplacesTTS = ""
                                        }
                                        // view the stories import / export settings fragment initializing StoriesImportExportFragment (FragmentTransaction
                                        // switch between Fragments).
                                        val ft = supportFragmentManager.beginTransaction()
                                        //
                                        val frag = StoriesImportExportFragment()
                                        //
                                        ft.replace(R.id.settings_container, frag)
                                        ft.addToBackStack(null)
                                        ft.commit()
                                    }
                                //
                            }
                        }
                    }
                }
            })
    }//permission is automatically granted on sdk<23 upon installation
    // Log.v(TAGPERMISSION,getString(R.string.permission_is_granted));
// Log.v(TAGPERMISSION,getString(R.string.permission_is_revoked));// Log.v(TAGPERMISSION,getString(R.string.permission_is_granted));
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

    /**
     * Zips a file at a location and places the resulting zip file at the toLocation
     * Example: zipFileAtPath("downloads/myfolder", "downloads/myFolder.zip");
     *
     *
     * Refer to [stackoverflow](https://stackoverflow.com/questions/6683600/zip-compress-a-folder-full-of-files-on-android)
     * answer of [HailZeon](https://stackoverflow.com/users/2053024/hailzeon)
     *
     * @param sourcePath string
     * @param toLocation string
     * @return boolean
     */
    fun zipFileAtPath(sourcePath: String, toLocation: String?): Boolean {
        val BUFFER = 2048
        val sourceFile = File(sourcePath)
        try {
            val origin: BufferedInputStream?
            val dest = FileOutputStream(toLocation)
            val out = ZipOutputStream(
                BufferedOutputStream(
                    dest
                )
            )
            if (sourceFile.isDirectory) {
                zipSubFolder(out, sourceFile, sourceFile.parent!!.length)
            } else {
                val data = ByteArray(BUFFER)
                val fi = FileInputStream(sourcePath)
                origin = BufferedInputStream(fi, BUFFER)
                val entry = ZipEntry(getLastPathComponent(sourcePath))
                entry.time = sourceFile.lastModified() // to keep modification time after unzipping
                out.putNextEntry(entry)
                var count: Int
                while (origin.read(data, 0, BUFFER).also { count = it } != -1) {
                    out.write(data, 0, count)
                }
            }
            out.close()
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
        return true
    }

    /*
     *
     * Zips a subfolder
     *
     */
    @Throws(IOException::class)
    private fun zipSubFolder(
        out: ZipOutputStream, folder: File,
        basePathLength: Int
    ) {
        val BUFFER = 2048
        val fileList = folder.listFiles()
        var origin: BufferedInputStream?
        for (file in fileList!!) {
            if (file.isDirectory) {
                zipSubFolder(out, file, basePathLength)
            } else {
                val data = ByteArray(BUFFER)
                val unmodifiedFilePath = file.path
                val relativePath = unmodifiedFilePath
                    .substring(basePathLength)
                val fi = FileInputStream(unmodifiedFilePath)
                origin = BufferedInputStream(fi, BUFFER)
                val entry = ZipEntry(relativePath)
                entry.time = file.lastModified() // to keep modification time after unzipping
                out.putNextEntry(entry)
                var count: Int
                while (origin.read(data, 0, BUFFER).also { count = it } != -1) {
                    out.write(data, 0, count)
                }
                origin.close()
            }
        }
    }

    /*
     * gets the last path component
     *
     * Example: getLastPathComponent("downloads/example/fileToZip");
     * Result: "fileToZip"
     */
    fun getLastPathComponent(filePath: String): String {
        val segments = filePath.split("/".toRegex()).toTypedArray()
        return if (segments.size == 0) "" else segments[segments.size - 1]
    }

    /**
     * unzip file with subdirectories.
     *
     *
     * REFER to [stackoverflow](https://stackoverflow.com/questions/43672241/how-to-unzip-file-with-sub-directories-in-android)
     * answer of [A.B.](https://stackoverflow.com/users/4914757/a-b)
     * Refer to [support.google.com](https://support.google.com/faqs/answer/9294009)
     * Refer to [stackoverflow](https://stackoverflow.com/questions/56303842/fixing-a-zip-path-traversal-vulnerability-in-android)
     * answer of [Indra Kumar S](https://stackoverflow.com/users/3577946/indra-kumar-s)
     *
     * @param destination file
     * @param zipFile file
     * @return boolean
     */
    @Throws(ZipException::class, IOException::class)
    private fun extractFolder(destination: File, zipFile: File): Boolean {
        val BUFFER = 8192
        //      File file = zipFile;
        //This can throw ZipException if file is not valid zip archive
        val zip = ZipFile(zipFile)
        //      String newPath = destination.getAbsolutePath() + File.separator + FilenameUtils.removeExtension(zipFile.getName());
        val newPath = destination.absolutePath + File.separator + stripExtension(
            zipFile.name.substring(zipFile.name.lastIndexOf("/") + 1)
        )
        //Create destination directory
        File(newPath).mkdir()
        val zipFileEntries: Enumeration<*> = zip.entries()

        //Iterate overall zip file entries
        while (zipFileEntries.hasMoreElements()) {
            val entry = zipFileEntries.nextElement() as ZipEntry
            val currentEntry = entry.name
            val destFile = File(destination.absolutePath, currentEntry)
            //
            // String canonicalPath = destFile.getCanonicalPath();
            try {
                ensureZipPathSafety(destFile, destination)
            } catch (e: Exception) {
                // SecurityException
                e.printStackTrace()
                return false
            }
            //            if (!canonicalPath.startsWith(destination.getAbsolutePath())) {
            // SecurityException
//            }
            // Finish unzipping…
            //
            val destinationParent = destFile.parentFile
            //If entry is directory create sub directory on file system
            destinationParent!!.mkdirs()
            if (!entry.isDirectory) {
                //Copy over data into destination file
                val `is` = BufferedInputStream(
                    zip
                        .getInputStream(entry)
                )
                var currentByte: Int
                val data = ByteArray(BUFFER)
                //orthodox way of copying file data using streams
                val fos = FileOutputStream(destFile)
                val dest = BufferedOutputStream(fos, BUFFER)
                while (`is`.read(data, 0, BUFFER).also { currentByte = it } != -1) {
                    dest.write(data, 0, currentByte)
                }
                dest.flush()
                dest.close()
                `is`.close()
            }
        }
        return true //some error codes etc.
    }

    /**
     * ensure Zip Path Safety.
     *
     *
     * Refer to [stackoverflow](https://stackoverflow.com/questions/56303842/fixing-a-zip-path-traversal-vulnerability-in-android)
     * answer of [Indra Kumar S](https://stackoverflow.com/users/3577946/indra-kumar-s)
     *
     * @param destFile file
     * @param destination directory
     */
    @Throws(Exception::class)
    private fun ensureZipPathSafety(destFile: File, destination: File) {
        val destDirCanonicalPath = destination.canonicalPath
        val destFileCanonicalPath = destFile.canonicalPath
        if (!destFileCanonicalPath.startsWith(destDirCanonicalPath)) {
            throw Exception(
                String.format(
                    "Found Zip Path Traversal Vulnerability with %s",
                    destFileCanonicalPath
                )
            )
        }
    }
    //
    /**
     * copy sounds images and videos to root internal storage.
     *
     *
     * Refer to [stackoverflow](https://stackoverflow.com/questions/4447477/how-to-copy-files-from-assets-folder-to-sdcard)
     * answer of [Rohith Nandakumar](https://stackoverflow.com/users/481239/rohith-nandakumar)
     *
     * @param dirName string folder to copy
     * @see .copyFile
     */
    private fun copyFilesInFolderToRoot(dirName: String) {
//      AssetManager assetManager = getAssets();
        val fDirName = File(dirName)
        val files: Array<String>?
        files = fDirName.list()
        if (files != null) {
            for (filename in files) {
                var `in`: InputStream? = null
                var out: OutputStream? = null
                try {
                    `in` = FileInputStream("$dirName/$filename")
                    //                  in = context.openFileInput(dirName + "/" + filename);
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

    /**
     * copy file.
     *
     *
     *
     * @param outputFolder documentfile
     * @param destFileName string
     */
    @Throws(IOException::class)
    fun copyFileZipFromInternalToSharedStorage(outputFolder: DocumentFile?, destFileName: String?) {
        val sourceStream: InputStream = context.openFileInput(destFileName)
        val documentFileNewFile = outputFolder!!.createFile("application/zip", destFileName!!)!!
        val destStream = context.contentResolver.openOutputStream(
            documentFileNewFile.uri
        )
        val buffer = ByteArray(1024)
        var read: Int
        while (sourceStream.read(buffer).also { read = it } != -1) {
            destStream!!.write(buffer, 0, read)
        }
        destStream?.close()
        sourceStream.close()
    }

    companion object {
        /**
         * copy file.
         *
         *
         * Refer to [stackoverflow](https://stackoverflow.com/questions/9292954/how-to-make-a-copy-of-a-file-in-android)
         * answer of [Rakshi](https://stackoverflow.com/users/979752/rakshi)
         *
         * @param src file source
         * @param dst file destination
         */
        @Throws(IOException::class)
        fun copy(src: File?, dst: File?) {
            FileInputStream(src).use { `in` ->
                FileOutputStream(dst).use { out ->
                    // Transfer bytes from in to out
                    val buf = ByteArray(1024)
                    var len: Int
                    while (`in`.read(buf).also { len = it } > 0) {
                        out.write(buf, 0, len)
                    }
                }
            }
        }

        /**
         * strip file name extension.
         *
         *
         * REFER to [stackoverflow](https://stackoverflow.com/questions/7541550/remove-the-extension-of-a-file)
         * answer of [palacsint](https://stackoverflow.com/users/843804/palacsint)
         *
         * @param s string file name
         * @return string file name without extension
         */
        fun stripExtension(s: String?): String {
            return if (s != null && s.lastIndexOf(".") > 0) s.substring(
                0,
                s.lastIndexOf(".")
            ) else s!!
        }
    }
}