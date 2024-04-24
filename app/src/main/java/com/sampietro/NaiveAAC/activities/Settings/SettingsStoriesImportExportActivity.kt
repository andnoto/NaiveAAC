package com.sampietro.NaiveAAC.activities.Settings

import android.Manifest
import com.sampietro.NaiveAAC.activities.Stories.Stories.Companion.exporttoCsv
import com.sampietro.NaiveAAC.activities.Game.GameParameters.GameParameters.Companion.exporttoCsv
import com.sampietro.NaiveAAC.activities.Stories.Stories.Companion.importStoryFromCsvFromInternalStorage
import com.sampietro.NaiveAAC.activities.Stories.StoriesHelper.renumberAStory
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
import com.sampietro.NaiveAAC.activities.BaseAndAbstractClass.ActivityAbstractClass
import com.sampietro.NaiveAAC.activities.DataStorage.DataStorageHelper.copyFile
import com.sampietro.NaiveAAC.activities.DataStorage.DataStorageHelper.copyFileFromSharedToInternalStorage
import com.sampietro.NaiveAAC.activities.DataStorage.DataStorageHelper.copyFileZipFromInternalToSharedStorage
import com.sampietro.NaiveAAC.activities.DataStorage.DataStorageHelper.copyFilesInFolderToRoot
import com.sampietro.NaiveAAC.activities.DataStorage.DataStorageHelper.extractFolder
import com.sampietro.NaiveAAC.activities.DataStorage.DataStorageHelper.zipFileAtPath
import io.realm.Realm
import java.io.*
import java.util.*

/**
 * <h1>SettingsStoriesImportExportActivity</h1>
 *
 * **SettingsStoriesImportExportActivity** app settings.
 * Refer to [developer.android.com](https://developer.android.com/guide/fragments/communicate)
 *
 * @version 5.0, 01/04/2024
 * @see AccountBaseActivity
 */
class SettingsStoriesImportExportActivity : ActivityAbstractClass()
    {
    var message = "messaggio non formato"
    var textView: TextView? = null

    //
    var fragmentManager: FragmentManager? = null

    //
    private var voiceToBeRecordedInStories: VoiceToBeRecordedInStories? = null
    private lateinit var viewModel: VoiceToBeRecordedInStoriesViewModel

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
     * @see setActivityResultLauncher
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
        viewModel.setItem(voiceToBeRecordedInStories!!)
        voiceToBeRecordedInStories!!.story = getString(R.string.nome_storia)
        //
        if (savedInstanceState == null) {
            fragmentManager = supportFragmentManager
            fragmentManager!!.beginTransaction()
                .add(ActionbarFragment(), getString(R.string.actionbar_fragment))
                .add(
                    R.id.settings_container,
                    StoriesImportExportFragment(R.layout.activity_settings_stories_import_export),
                    "StoriesImportExportFragment"
                )
                .commit()
        }
        // The MainActivity class provides an instance of Realm wherever needed in the application.
        // To use it in the Activity, a reference must be obtained to a Realm object in the onCreate method.
        // The Realm object must be closed in the onDestroy method.
        realm = Realm.getDefaultInstance()
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
        val textWord1 = findViewById<View>(R.id.storytosearch) as EditText
        // Viewmodel
        // In the activity, sometimes it is called observe, other times it is limited to performing set directly
        // (maybe it is not necessary to call observe)
        voiceToBeRecordedInStories!!.story =
            textWord1.text.toString().lowercase(Locale.getDefault())
        //
        val frag = StoriesImportExportFragment(R.layout.activity_settings_stories_import_export)
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
                                viewModel.getSelectedItem()
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
                                                        context,
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
                                        val frag = StoriesImportExportFragment(R.layout.activity_settings_stories_import_export)
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