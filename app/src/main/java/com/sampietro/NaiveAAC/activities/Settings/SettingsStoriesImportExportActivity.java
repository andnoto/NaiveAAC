package com.sampietro.NaiveAAC.activities.Settings;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.app.ActivityCompat;
import androidx.documentfile.provider.DocumentFile;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;

import com.sampietro.NaiveAAC.R;
import com.sampietro.NaiveAAC.activities.Arasaac.PictogramsAllToModify;
import com.sampietro.NaiveAAC.activities.Game.GameParameters.GameParameters;
import com.sampietro.NaiveAAC.activities.Game.Utils.ActionbarFragment;
import com.sampietro.NaiveAAC.activities.Grammar.GrammaticalExceptions;
import com.sampietro.NaiveAAC.activities.Grammar.ListsOfNames;
import com.sampietro.NaiveAAC.activities.Graphics.Images;
import com.sampietro.NaiveAAC.activities.Graphics.Sounds;
import com.sampietro.NaiveAAC.activities.Graphics.Videos;
import com.sampietro.NaiveAAC.activities.Phrases.Phrases;
import com.sampietro.NaiveAAC.activities.Settings.Utils.AccountActivityAbstractClass;
import com.sampietro.NaiveAAC.activities.Settings.Utils.SettingsFragmentAbstractClass;
import com.sampietro.NaiveAAC.activities.Stories.Stories;
import com.sampietro.NaiveAAC.activities.Stories.StoriesHelper;
import com.sampietro.NaiveAAC.activities.Stories.VoiceToBeRecordedInStories;
import com.sampietro.NaiveAAC.activities.Stories.VoiceToBeRecordedInStoriesViewModel;
import com.sampietro.NaiveAAC.activities.WordPairs.WordPairs;
import com.sampietro.NaiveAAC.activities.history.History;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * <h1>SettingsStoriesImportExportActivity</h1>
 * <p><b>SettingsStoriesImportExportActivity</b> app settings.</p>
 * Refer to <a href="https://developer.android.com/guide/fragments/communicate">developer.android.com</a>
 *
 * @version 3.0 03/12/23
 * @see AccountActivityAbstractClass
 */
public class SettingsStoriesImportExportActivity extends AccountActivityAbstractClass
        implements
        SettingsFragmentAbstractClass.onFragmentEventListenerSettings
{
    public String message = "messaggio non formato";
    public TextView textView;
    //
    public FragmentManager fragmentManager;
    //
    private VoiceToBeRecordedInStories voiceToBeRecordedInStories;
    private VoiceToBeRecordedInStoriesViewModel viewModel;
    //
    public boolean radiobuttonStoriesImportButtonClicked = false;
    public boolean radiobuttonStoriesExportButtonClicked = false;
    // ActivityResultLauncher
    public ActivityResultLauncher<Intent> exportStorySearchActivityResultLauncher;
    public Uri storyTreeUri;
    /**
     * configurations of settings start screen.
     *
     * @param savedInstanceState Define potentially saved parameters due to configurations changes.
     * @see #setActivityResultLauncher
     * @see ActionbarFragment
     * @see ContentsFragment
     * @see Activity#onCreate(Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        //
        context = this;
        //
        setExportStorySearchActivityResultLauncher();
         /*
        Both your fragment and its host activity can retrieve a shared instance of a ViewModel with activity scope by passing the activity into the ViewModelProvider
        constructor.
        The ViewModelProvider handles instantiating the ViewModel or retrieving it if it already exists. Both components can observe and modify this data
         */
        // In the Activity#onCreate make the only setItem
        voiceToBeRecordedInStories = new VoiceToBeRecordedInStories();
        viewModel = new ViewModelProvider(this).get(VoiceToBeRecordedInStoriesViewModel.class);
        viewModel.setItem(voiceToBeRecordedInStories);
        voiceToBeRecordedInStories.setStory(getString(R.string.nome_storia));
        //
        if (savedInstanceState == null)
        {
            fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .add(new ActionbarFragment(), getString(R.string.actionbar_fragment))
                    .add(R.id.settings_container, new StoriesImportExportFragment(), "StoriesImportExportFragment")
                    .commit();
        }
        // The MainActivity class provides an instance of Realm wherever needed in the application.
        // To use it in the Activity, a reference must be obtained to a Realm object in the onCreate method.
        // The Realm object must be closed in the onDestroy method.
        realm= Realm.getDefaultInstance();
    }
    //
    /**
     * receives calls from fragment listeners.
     *
     * @param v view of calling fragment
     * @see SettingsFragmentAbstractClass
     */
    @Override
    public void receiveResultSettings(View v) {
        rootViewFragment = v;
    }
    /**
     * Called when the user taps the add button from stories settings.
     * </p>
     * Refer to <a href="https://stackoverflow.com/questions/65203681/how-to-create-multiple-files-at-once-using-android-storage-access-framework">stackoverflow</a>
     * answer of <a href="https://stackoverflow.com/users/11355432/ismail-osunlana">Ismail Osunlana</a>
     *
     * @param v view of tapped button
     *
     */
    public void storiesImportExport(View v)
    {
        // Choose a directory using the system's file picker.
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
        // Optionally, specify a URI for the directory that should be opened in
        // the system file picker when it loads.
        intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, Environment.DIRECTORY_DOWNLOADS);
        exportStorySearchActivityResultLauncher.launch(intent);
    }
    /**
     * Called when the user click the radio button import / export from the stories import / export settings.
     * </p>
     * register which radio button was clicked
     *
     * @param view view of clicked radio button
     * @see StoriesImportExportFragment
     */
    public void onStoriesImportExportButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();
        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.radio_import:
                if (checked) {
                    //
                    radiobuttonStoriesImportButtonClicked = true;
                    radiobuttonStoriesExportButtonClicked = false;
                }
                break;
            case R.id.radio_export:
                if (checked) {
                    //
                    radiobuttonStoriesImportButtonClicked = false;
                    radiobuttonStoriesExportButtonClicked = true;
                }
                break;
        }
    }
    /**
     * Called when the user taps the story search button .
     * </p>
     * the activity is notified to view the stories list.
     * </p>
     *
     * @param view view of tapped button
     * @see StoriesImportExportFragment
     */
    public void storiesSearch(View view) {
        EditText textWord1=(EditText) rootViewFragment.findViewById(R.id.storytosearch);
        // Viewmodel
        // In the activity, sometimes it is called observe, other times it is limited to performing set directly
        // (maybe it is not necessary to call observe)
        voiceToBeRecordedInStories.setStory(textWord1.getText().toString().toLowerCase());
        //
        StoriesImportExportFragment frag= new StoriesImportExportFragment();
        FragmentTransaction ft=getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.settings_container, frag);
        ft.addToBackStack(null);
        ft.commit();
    }
    /**
     * Called when the user taps export tables button.
     * </p>
     * at the end the activity is notified to view the settings menu.
     *
     * @see #isStoragePermissionGranted
     * @see #copy
     * @see #zipFileAtPath
     * @see #copyFileFromInternalToSharedStorage
     * @see #copyFileFromSharedToInternalStorage
     * @see #extractFolder
     * @see #copyFilesInFolderToRoot
     * @see StoriesImportExportFragment
     * @see Images
     * @see Videos
     * @see Sounds
     * @see GameParameters
     * @see Stories
     */
    public void setExportStorySearchActivityResultLauncher() {
        //
        // You can do the assignment inside onAttach or onCreate, i.e, before the activity is displayed
        exportStorySearchActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult aResult) {
                        if (aResult.getResultCode() == Activity.RESULT_OK) {
                            // There are no request codes
                            Intent resultData = aResult.getData();
                            // doSomeOperations();
                            storyTreeUri = null;
//                            filePath = getString(R.string.non_trovato);
                            if (resultData != null) {
                                storyTreeUri = Objects.requireNonNull(resultData).getData();
                                DocumentFile zipFolder = DocumentFile.fromTreeUri(context, storyTreeUri);
//
                                if (isStoragePermissionGranted()) {
                                    //
                                    // Viewmodel
                                    // In the activity, sometimes it is called observe, other times it is limited to performing set directly
                                    // (maybe it is not necessary to call observe)
                                    viewModel.getSelectedItem().observe((LifecycleOwner) context, voiceToBeRecordedInStories -> {
                                        // Perform an action with the latest item data
                                        voiceToBeRecordedInStories.setStory(getString(R.string.nome_storia));
                                        //
                                        realm= Realm.getDefaultInstance();
                                        //
                                        EditText textWord1=(EditText) findViewById(R.id.storytosearch);
                                        if ((textWord1 != null) && (!textWord1.getText().toString().equals(""))
                                                && !(radiobuttonStoriesImportButtonClicked && radiobuttonStoriesExportButtonClicked)
                                                && !(!radiobuttonStoriesImportButtonClicked && !radiobuttonStoriesExportButtonClicked))
                                        {
                                            if (radiobuttonStoriesExportButtonClicked)
                                            {
                                                RealmResults<Stories> resultsStories =
                                                        realm.where(Stories.class)
                                                                .equalTo("story", textWord1.getText().toString().toLowerCase())
                                                                .findAll();
                                                //
                                                RealmResults<GameParameters> resultsGameParameters =
                                                        realm.where(GameParameters.class)
                                                                .equalTo("gameParameter", textWord1.getText().toString().toLowerCase())
                                                                .findAll();
                                                //
                                                // creo due set (in modo da non ammettere duplicati) con la descrizione dei video e dei suoni utilizzati dalla storia
                                                HashSet<String> videosDescriptionSet = new HashSet<String>();
                                                HashSet<String> soundsDescriptionSet = new HashSet<String>();
                                                // creo tre set (in modo da non ammettere duplicati) con i media file utilizzati dalla storia
                                                HashSet<String> imagesSet = new HashSet<String>();
                                                HashSet<String> videosSet = new HashSet<String>();
                                                HashSet<String> soundsSet = new HashSet<String>();
                                                //
                                                int count = resultsStories.size();
                                                if (count != 0) {
                                                    int irrh=0;
                                                    while(irrh < count) {
                                                        Stories result = resultsStories.get(irrh);
                                                        assert result != null;
                                                        int wordNumber = result.getWordNumber();
                                                        //
                                                        if ((wordNumber != 0) && (wordNumber != 99)
                                                                && (wordNumber != 999) && (wordNumber != 9999))
                                                        {
                                                            // immagini
                                                            if (!result.getUriType().equals("A"))
                                                                imagesSet.add(result.getUri());
                                                            // video
                                                            RealmResults<Videos> resultsVideos =
                                                                    realm.where(Videos.class).equalTo("descrizione", result.getVideo()).findAll();
                                                            if (resultsVideos.size() != 0) {
                                                                assert resultsVideos.get(0) != null;
                                                                videosDescriptionSet.add(resultsVideos.get(0).getDescrizione());
                                                                videosSet.add(resultsVideos.get(0).getUri());
                                                            }
                                                            // suoni
                                                            RealmResults<Sounds> resultsSounds =
                                                                    realm.where(Sounds.class).equalTo("descrizione", result.getSound()).findAll();
                                                            if (resultsSounds.size() != 0) {
                                                                assert resultsSounds.get(0) != null;
                                                                soundsDescriptionSet.add(resultsSounds.get(0).getDescrizione());
                                                                soundsSet.add(resultsSounds.get(0).getUri());
                                                            }
                                                        }
                                                        irrh++;
                                                    }
                                                }
                                                // trasformo i tre set ottenuti in tre list contenenti i path dei media file utilizzati dalla storia
                                                List<String> imagesList = new ArrayList<String>();
                                                imagesList.addAll(imagesSet);
                                                //
                                                List<String> videosList = new ArrayList<String>();
                                                videosList.addAll(videosSet);
                                                int videosSize = videosList.size();
                                                int irrh=0;
                                                while(irrh < videosSize)   {
                                                    RealmResults<Videos> resultsVideos =
                                                            realm.where(Videos.class).equalTo("descrizione", videosList.get(irrh)).findAll();
                                                    if (resultsVideos.size() != 0) {
                                                        videosList.set(irrh, resultsVideos.get(0).getUri());
                                                    }
                                                    irrh++;
                                                }
                                                //
                                                List<String> soundsList = new ArrayList<String>();
                                                soundsList.addAll(soundsSet);
                                                int soundsSize = soundsList.size();
                                                irrh=0;
                                                while(irrh < soundsSize)   {
                                                    RealmResults<Sounds> resultsSounds =
                                                            realm.where(Sounds.class).equalTo("descrizione", soundsList.get(irrh)).findAll();
                                                    if (resultsSounds.size() != 0) {
                                                        soundsList.set(irrh, resultsSounds.get(0).getUri());
                                                    }
                                                    irrh++;
                                                }
                                                // creo le dir della storia
                                                String rootPath = context.getFilesDir().getAbsolutePath();
                                                String dirName = textWord1.getText().toString().toLowerCase();
                                                File d = new File(rootPath + "/" + dirName);
                                                if (!d.exists())
                                                {
                                                    d.mkdirs();
                                                }
                                                else
                                                {
                                                    d.delete();
                                                    d.mkdirs();
                                                }
                                                File dcsv = new File(rootPath + "/" + dirName + "/csv" );
                                                dcsv.mkdirs();
                                                File dimages = new File(rootPath + "/" + dirName + "/images" );
                                                dimages.mkdirs();
                                                File dvideos = new File(rootPath + "/" + dirName + "/videos" );
                                                dvideos.mkdirs();
                                                File dsounds = new File(rootPath + "/" + dirName + "/sounds" );
                                                dsounds.mkdirs();
                                                // creo file csv
                                                Stories.exporttoCsv(context, realm, resultsStories, dcsv);
                                                GameParameters.exporttoCsv(context, realm, resultsGameParameters, dcsv);
                                                //
                                                Videos.exporttoCsv(context, realm, videosDescriptionSet, dcsv);
                                                //
                                                Sounds.exporttoCsv(context, realm, soundsDescriptionSet, dcsv);
                                                // creo copia dei file immagini
                                                //
                                                File finput;
                                                File foutput;
                                                int imagesSize = imagesList.size();
                                                irrh=0;
                                                while(irrh < imagesSize)   {
                                                    finput = new File(imagesList.get(irrh));
                                                    foutput = new File(rootPath + "/" + dirName + "/images" + "/" + imagesList.get(irrh).substring(imagesList.get(irrh).lastIndexOf("/")+1));
                                                    try {
                                                        copy (finput, foutput);
                                                    } catch (IOException e) {
                                                        e.printStackTrace();
                                                    }
                                                    irrh++;
                                                }
                                                // creo copia dei file video
                                                irrh=0;
                                                while(irrh < videosSize)   {
                                                    finput = new File(videosList.get(irrh));
                                                    foutput = new File(rootPath + "/" + dirName + "/videos" + "/" + videosList.get(irrh).substring(videosList.get(irrh).lastIndexOf("/")+1));
                                                    try {
                                                        copy (finput, foutput);
                                                    } catch (IOException e) {
                                                        e.printStackTrace();
                                                    }
                                                    irrh++;
                                                }
                                                // creo copia dei file suono
                                                irrh=0;
                                                while(irrh < soundsSize)   {
                                                    finput = new File(soundsList.get(irrh));
                                                    foutput = new File(rootPath + "/" + dirName + "/sounds" + "/" + soundsList.get(irrh).substring(soundsList.get(irrh).lastIndexOf("/")+1));
                                                    try {
                                                        copy (finput, foutput);
                                                    } catch (IOException e) {
                                                        e.printStackTrace();
                                                    }
                                                    irrh++;
                                                }
                                                // zippo la directory di output
                                                zipFileAtPath(rootPath + "/" + dirName, rootPath + "/" + dirName + ".zip");
                                                //
                                                assert zipFolder != null;
                                                // copio il file zip sulla shared storage
                                                try {
//                                                    copyFileZipFromInternalToSharedStorage(zipFolder,dirName + ".zip");
                                                    copyFileZipFromInternalToSharedStorage(zipFolder,dirName + ".zip");
                                                } catch (IOException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                            if (radiobuttonStoriesImportButtonClicked)
                                            {
                                                // creo le dir della storia
                                                String rootPath = context.getFilesDir().getAbsolutePath();
                                                String dirName = textWord1.getText().toString().toLowerCase();
                                                File d = new File(rootPath + "/" + dirName);
                                                if (!d.exists())
                                                {
                                                    d.mkdirs();
                                                }
                                                else
                                                {
                                                    d.delete();
                                                    d.mkdirs();
                                                }
                                                File dcsv = new File(rootPath + "/" + dirName + "/csv" );
                                                dcsv.mkdirs();
                                                File dimages = new File(rootPath + "/" + dirName + "/images" );
                                                dimages.mkdirs();
                                                File dvideos = new File(rootPath + "/" + dirName + "/videos" );
                                                dvideos.mkdirs();
                                                File dsounds = new File(rootPath + "/" + dirName + "/sounds" );
                                                dsounds.mkdirs();
                                                // copio il file zip nella internal storage
                                                assert zipFolder != null;
                                                DocumentFile documentFileNewFile = zipFolder.findFile(dirName + ".zip");
                                                assert documentFileNewFile != null;
                                                Uri zipFileUri = documentFileNewFile.getUri();
                                                try {
                                                    copyFileFromSharedToInternalStorage(zipFileUri,dirName + ".zip");
                                                } catch (IOException e) {
                                                    e.printStackTrace();
                                                }
                                                // unzippo la directory di input
                                                // errore qui
                                                try {
                                                    extractFolder(new File(rootPath), new File(rootPath + "/" + dirName + ".zip"));
                                                } catch (IOException e) {
                                                    e.printStackTrace();
                                                }
                                                // creo copia dei file suono
                                                copyFilesInFolderToRoot(rootPath + "/" + dirName + "/sounds");
                                                // creo copia dei file video
                                                copyFilesInFolderToRoot(rootPath + "/" + dirName + "/videos");
                                                // creo copia dei file immagini
                                                copyFilesInFolderToRoot(rootPath + "/" + dirName + "/images");
                                                // creo file csv
                                                copyFilesInFolderToRoot(rootPath + "/" + dirName + "/csv");
                                                // creo la storia
                                                Sounds.importFromCsvFromInternalStorage(context, realm);
                                                Videos.importFromCsvFromInternalStorage(context, realm);
                                                Stories.importStoryFromCsvFromInternalStorage(context, realm, dirName);
                                                StoriesHelper.renumberAStory(realm, dirName);
                                                GameParameters.importFromCsvFromInternalStorage(context, realm);
                                            }
                                            // clear fields of viewmodel data class
                                            voiceToBeRecordedInStories.setStory(textWord1.getText().toString().toLowerCase());
                                            voiceToBeRecordedInStories.setPhraseNumber(0);
                                            voiceToBeRecordedInStories.setWordNumber(0);
                                            voiceToBeRecordedInStories.setWord("");
                                            voiceToBeRecordedInStories.setUriType("");
                                            voiceToBeRecordedInStories.setUri("");
                                            voiceToBeRecordedInStories.setAnswerActionType("");
                                            voiceToBeRecordedInStories.setAnswerAction("");
                                            voiceToBeRecordedInStories.setVideo("");
                                            voiceToBeRecordedInStories.setSound("");
                                            voiceToBeRecordedInStories.setSoundReplacesTTS("");
                                        }
                                        // view the stories import / export settings fragment initializing StoriesImportExportFragment (FragmentTransaction
                                        // switch between Fragments).
                                        FragmentTransaction ft=getSupportFragmentManager().beginTransaction();
                                        //
                                        StoriesImportExportFragment frag= new StoriesImportExportFragment();
                                        //
                                        ft.replace(R.id.settings_container, frag);
                                        ft.addToBackStack(null);
                                        ft.commit();
                                    });
                                    //
                                }
                             }
                        }
                    }
                });
    }
    /**
     * check permissions.
     *
     * @return boolean whit true if permission is granted
     */
    public  boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                // Log.v(TAGPERMISSION,getString(R.string.permission_is_granted));
                return true;
            } else {

                // Log.v(TAGPERMISSION,getString(R.string.permission_is_revoked));
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            // Log.v(TAGPERMISSION,getString(R.string.permission_is_granted));
            return true;
        }
    }
    /**
     * copy file.
     * <p>
     * Refer to <a href="https://stackoverflow.com/questions/9292954/how-to-make-a-copy-of-a-file-in-android">stackoverflow</a>
     * answer of <a href="https://stackoverflow.com/users/979752/rakshi">Rakshi</a>
     *
     * @param src file source
     * @param dst file destination
     */
    public static void copy(File src, File dst) throws IOException {
        try (InputStream in = new FileInputStream(src)) {
            try (OutputStream out = new FileOutputStream(dst)) {
                // Transfer bytes from in to out
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
            }
        }
    }
    /**
     * Zips a file at a location and places the resulting zip file at the toLocation
     * Example: zipFileAtPath("downloads/myfolder", "downloads/myFolder.zip");
     * <p>
     * Refer to <a href="https://stackoverflow.com/questions/6683600/zip-compress-a-folder-full-of-files-on-android">stackoverflow</a>
     * answer of <a href="https://stackoverflow.com/users/2053024/hailzeon">HailZeon</a>
     *
     * @param sourcePath string
     * @param toLocation string
     * @return boolean
     */
    public boolean zipFileAtPath(String sourcePath, String toLocation) {
        final int BUFFER = 2048;

        File sourceFile = new File(sourcePath);
        try {
            BufferedInputStream origin = null;
            FileOutputStream dest = new FileOutputStream(toLocation);
            ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(
                    dest));
            if (sourceFile.isDirectory()) {
                zipSubFolder(out, sourceFile, sourceFile.getParent().length());
            } else {
                byte data[] = new byte[BUFFER];
                FileInputStream fi = new FileInputStream(sourcePath);
                origin = new BufferedInputStream(fi, BUFFER);
                ZipEntry entry = new ZipEntry(getLastPathComponent(sourcePath));
                entry.setTime(sourceFile.lastModified()); // to keep modification time after unzipping
                out.putNextEntry(entry);
                int count;
                while ((count = origin.read(data, 0, BUFFER)) != -1) {
                    out.write(data, 0, count);
                }
            }
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
    /*
     *
     * Zips a subfolder
     *
     */
    private void zipSubFolder(ZipOutputStream out, File folder,
                              int basePathLength) throws IOException {

        final int BUFFER = 2048;

        File[] fileList = folder.listFiles();
        BufferedInputStream origin = null;
        for (File file : fileList) {
            if (file.isDirectory()) {
                zipSubFolder(out, file, basePathLength);
            } else {
                byte data[] = new byte[BUFFER];
                String unmodifiedFilePath = file.getPath();
                String relativePath = unmodifiedFilePath
                        .substring(basePathLength);
                FileInputStream fi = new FileInputStream(unmodifiedFilePath);
                origin = new BufferedInputStream(fi, BUFFER);
                ZipEntry entry = new ZipEntry(relativePath);
                entry.setTime(file.lastModified()); // to keep modification time after unzipping
                out.putNextEntry(entry);
                int count;
                while ((count = origin.read(data, 0, BUFFER)) != -1) {
                    out.write(data, 0, count);
                }
                origin.close();
            }
        }
    }
    /*
     * gets the last path component
     *
     * Example: getLastPathComponent("downloads/example/fileToZip");
     * Result: "fileToZip"
     */
    public String getLastPathComponent(String filePath) {
        String[] segments = filePath.split("/");
        if (segments.length == 0)
            return "";
        String lastPathComponent = segments[segments.length - 1];
        return lastPathComponent;
    }
    /**
     * unzip file with subdirectories.
     * <p>
     * Refer to <a href="https://stackoverflow.com/questions/43672241/how-to-unzip-file-with-sub-directories-in-android">stackoverflow</a>
     * answer of <a href="https://stackoverflow.com/users/4914757/a-b">A.B.</a>
     *
     * @param destination file
     * @param zipFile file
     * @return boolean
     */
    private boolean extractFolder(File destination, File zipFile) throws ZipException, IOException
    {
        int BUFFER = 8192;
//      File file = zipFile;
        //This can throw ZipException if file is not valid zip archive
        ZipFile zip  = new ZipFile(zipFile);
//      String newPath = destination.getAbsolutePath() + File.separator + FilenameUtils.removeExtension(zipFile.getName());
        String newPath = destination.getAbsolutePath() + File.separator + stripExtension(zipFile.getName().substring(zipFile.getName().lastIndexOf("/")+1));
        //Create destination directory
        new File(newPath).mkdir();
        Enumeration zipFileEntries = zip.entries();

        //Iterate overall zip file entries
        while (zipFileEntries.hasMoreElements())
        {
            ZipEntry entry = (ZipEntry) zipFileEntries.nextElement();
            String currentEntry = entry.getName();
            File destFile = new File(destination.getAbsolutePath(), currentEntry);
            File destinationParent = destFile.getParentFile();
            //If entry is directory create sub directory on file system
            destinationParent.mkdirs();
            if (!entry.isDirectory())
            {
                //Copy over data into destination file
                BufferedInputStream is = new BufferedInputStream(zip
                        .getInputStream(entry));
                int currentByte;
                byte data[] = new byte[BUFFER];
                //orthodox way of copying file data using streams
                FileOutputStream fos = new FileOutputStream(destFile);
                BufferedOutputStream dest = new BufferedOutputStream(fos, BUFFER);
                while ((currentByte = is.read(data, 0, BUFFER)) != -1) {
                    dest.write(data, 0, currentByte);
                }
                dest.flush();
                dest.close();
                is.close();
            }
        }
        return true;//some error codes etc.
    }
    /**
     * strip file name extension.
     * <p>
     * Refer to <a href="https://stackoverflow.com/questions/7541550/remove-the-extension-of-a-file">stackoverflow</a>
     * answer of <a href="https://stackoverflow.com/users/843804/palacsint">palacsint</a>
     *
     * @param s string file name
     * @return string file name without extension
     */
    public static String stripExtension(final String s)
    {
        return s != null && s.lastIndexOf(".") > 0 ? s.substring(0, s.lastIndexOf(".")) : s;
    }
    //
    /**
     * copy sounds images and videos to root internal storage.
     * <p>
     * Refer to <a href="https://stackoverflow.com/questions/4447477/how-to-copy-files-from-assets-folder-to-sdcard">stackoverflow</a>
     * answer of <a href="https://stackoverflow.com/users/481239/rohith-nandakumar">Rohith Nandakumar</a>
     *
     * @param dirName string folder to copy
     * @see #copyFile(InputStream, OutputStream)
     */
    private void copyFilesInFolderToRoot(String dirName) {
//      AssetManager assetManager = getAssets();
        File fDirName = new File(dirName);
        String[] files = null;
        files = fDirName.list();
        if (files != null) {
            for (String filename : files) {
                InputStream in = null;
                OutputStream out = null;
                try {
                    in = new FileInputStream(dirName + "/" + filename);
//                  in = context.openFileInput(dirName + "/" + filename);
                    //
                    out = context.openFileOutput(filename, Context.MODE_PRIVATE);
                    copyFile(in, out);
                } catch(IOException e) {
                    // Log.e("tag", "Failed to copy asset file: " + filename, e);
                }
                finally {
                    if (in != null) {
                        try {
                            in.close();
                        } catch (IOException e) {
                            // NOOP
                        }
                    }
                    if (out != null) {
                        try {
                            out.close();
                        } catch (IOException e) {
                            // NOOP
                        }
                    }
                }
            }
        }
    }
    /**
     * copy file.
     * <p>
     * Refer to <a href="https://stackoverflow.com/questions/4447477/how-to-copy-files-from-assets-folder-to-sdcard">stackoverflow</a>
     * answer of <a href="https://stackoverflow.com/users/481239/rohith-nandakumar">Rohith Nandakumar</a>
     *
     * @param in inputstream
     * @param out outputstream
     */
    private void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while((read = in.read(buffer)) != -1){
            out.write(buffer, 0, read);
        }
    }
    /**
     * copy file.
     * <p>
     *
     * @param outputFolder documentfile
     * @param destFileName string
     */
    public void copyFileZipFromInternalToSharedStorage(DocumentFile outputFolder, String destFileName) throws IOException {
        InputStream sourceStream = context.openFileInput(destFileName);
        DocumentFile documentFileNewFile = outputFolder.createFile("application/zip", destFileName);
        assert documentFileNewFile != null;
        OutputStream destStream = context.getContentResolver().openOutputStream(documentFileNewFile.getUri());
        byte[] buffer = new byte[1024];
        int read;
        while((read = sourceStream.read(buffer)) != -1) {
            destStream.write(buffer, 0, read);
        }
        destStream.close();
        sourceStream.close();
    }
}