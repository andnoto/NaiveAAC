package com.example.NaiveAAC.activities.Account;

import static com.example.NaiveAAC.activities.Settings.Utils.AdvancedSettingsDataImportExportHelper.findExternalStorageRoot;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.EditText;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentManager;

import com.example.NaiveAAC.R;
import com.example.NaiveAAC.activities.Game.ChoiseOfGame.ChoiseOfGameActivity;
import com.example.NaiveAAC.activities.Game.GameParameters.GameParameters;
import com.example.NaiveAAC.activities.Game.Utils.ActionbarFragment;
import com.example.NaiveAAC.activities.Grammar.GrammaticalExceptions;
import com.example.NaiveAAC.activities.Grammar.ListsOfNames;
import com.example.NaiveAAC.activities.Graphics.Images;
import com.example.NaiveAAC.activities.Graphics.Videos;
import com.example.NaiveAAC.activities.Phrases.Phrases;
import com.example.NaiveAAC.activities.Settings.AccountFragment;
import com.example.NaiveAAC.activities.Settings.Utils.AccountActivityAbstractClass;
import com.example.NaiveAAC.activities.Settings.Utils.AdvancedSettingsDataImportExportHelper;
import com.example.NaiveAAC.activities.Settings.Utils.SettingsFragmentAbstractClass;
import com.example.NaiveAAC.activities.Stories.Stories;
import com.example.NaiveAAC.activities.WordPairs.WordPairs;
import com.example.NaiveAAC.activities.history.History;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * <h1>AccountActivity</h1>
 * <p><b>AccountActivity</b> create from assets the initial realm database , register the user
 * and move on to the welcome activity.</p>
 * the initial realm database consists of
 * </p>
 * 1) data from the Arasaac /pictograms/all API</p>
 * 2) data from https://github.com/ian-hamlin/verb-data a collection of verbs and conjugations </p>
 * 3) initial settings and content such as images, videos and others from assets</p>
 *
 * @version     1.1, 04/22/22
 * @see com.example.NaiveAAC.activities.Settings.Utils.AccountActivityAbstractClass
 * @see com.example.NaiveAAC.activities.Settings.Utils.SettingsFragmentAbstractClass
 */
public class AccountActivity extends AccountActivityAbstractClass implements
        SettingsFragmentAbstractClass.onFragmentEventListenerSettings         {
    //
    private static final String TAG = "VERBO";
    private static final String TAGPERMISSION = "Permission";
    //
    public static final String EXTRA_MESSAGE ="helloworldandroidMessage" ;
    //
    public FragmentManager fragmentManager;
    //
    private final String FOLDER_SIMSIM = "simsim";
    //
    public String textPersonName;
    //
    public File root;
    public String rootPath;
    //
    /**
     * configurations of account start screen.
     *
     * @param savedInstanceState Define potentially saved parameters due to configurations changes.
     * @see #setActivityResultLauncher
     * @see ActionbarFragment
     * @see AccountFragment
     * @see android.app.Activity#onCreate(Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        //
        context = this;
        //
        setActivityResultLauncher();
        //
        if (savedInstanceState == null)
            {
            fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .add(new ActionbarFragment(), getString(R.string.actionbar_fragment))
                    .add(R.id.settings_container, new AccountFragment(), getString(R.string.account_fragment))
                    .commit();
            }
        //
        if (isStoragePermissionGranted()) {
        //    Log.v(TAGPERMISSION,getString(R.string.permission_is_granted));
        }
        //
    }
    /**
     * Called when the user taps the save account button.
     * </p>
     * if the case creates creates the initial realm database.</p>
     *
     * @param view view of tapped button
     * @see #isStoragePermissionGranted
     * @see AdvancedSettingsDataImportExportHelper#findExternalStorageRoot()
     * @see #prepareTheSimsimDirectory
     * @see #registerPersonName(String)
     * @see AdvancedSettingsDataImportExportHelper#openFileInput(Context, String)
     * @see Images
     * @see Videos
     * @see GameParameters
     * @see GrammaticalExceptions
     * @see ListsOfNames
     * @see Phrases
     * @see Stories
     * @see WordPairs
     */
    public void saveAccount(View view) {

            if (isStoragePermissionGranted()) {
                //
                root = findExternalStorageRoot();
                rootPath = root.getAbsolutePath();
                //
                // naiveaac dir registration and csv copy from assets to dir naiveaac
                prepareTheSimsimDirectory();
                //
                realm= Realm.getDefaultInstance();
                //
                RealmResults<History> daCancellare = realm.where(History.class).findAll();
                realm.beginTransaction();
                daCancellare.deleteAllFromRealm();
                realm.commitTransaction();
                //
                File fI = AdvancedSettingsDataImportExportHelper.openFileInput (context,"images.csv");
                if (fI.exists()) {
                    Images.importFromCsv(context, realm);
                }
                File fV = AdvancedSettingsDataImportExportHelper.openFileInput (context,"videos.csv");
                if (fV.exists()) {
                    Videos.importFromCsv(context, realm);
                }
                //
                GameParameters.importFromCsv(context, realm);
                GrammaticalExceptions.importFromCsv(context, realm);
                ListsOfNames.importFromCsv(context, realm);
                Phrases.importFromCsv(context, realm);
                Stories.importFromCsv(context, realm);
                WordPairs.importFromCsv(context, realm);
                //
            }
            //
            // record default preferences
        sharedPref = this.getSharedPreferences(
                    getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(getString(R.string.preference_print_permissions), getString(R.string.character_n));
        editor.apply();
        editor.putString(getString(R.string.preference_list_mode), getString(R.string.sorted));
        editor.apply();
        editor.putInt("preference_AllowedMarginOfError", 20);
        editor.apply();
        // register the user in the shared preferences
        // and move on to the welcome activity
        EditText editText = (EditText) rootViewFragment.findViewById(R.id.editTextTextAccount);
        textPersonName = editText.getText().toString();
        //
        if (textPersonName.length()>0 && !filePath.equals(getString(R.string.non_trovato)))
        {
            // register the user
            registerPersonName(textPersonName);
            // register the linked image
            realm.beginTransaction();
            Images i = realm.createObject(Images.class);
            i.setDescrizione(textPersonName);
            i.setUri(filePath);
            realm.commitTransaction();
            realm.beginTransaction();
            Images iIo = realm.createObject(Images.class);
            iIo.setDescrizione(getString(R.string.io));
            iIo.setUri(filePath);
            realm.commitTransaction();
//
            Intent intent = new Intent(this,
                    ChoiseOfGameActivity.class);
            String message = (getString(R.string.puoi_accedere));
            intent.putExtra(EXTRA_MESSAGE, message);
            startActivity(intent);
        }
    }

    //
    /**
     * copy the csv files with initial settings and content such as images, videos and others from assets.
     *
     * @see #copyFileCsv
     * @see #copyAssets
     */
    public void prepareTheSimsimDirectory () {
        File f = new File(Environment.getExternalStorageDirectory(), getString(R.string.app_name));
        if (!f.exists()) {
            f.mkdirs();
            try {
                copyFileCsv("gameparameters.csv");
                copyFileCsv("grammaticalexceptions.csv");
                copyFileCsv("images.csv");
                copyFileCsv("listsofnames.csv");
                copyFileCsv("phrases.csv");
                copyFileCsv("pictogramsalltomodify.csv");
                copyFileCsv("stories.csv");
                copyFileCsv("videos.csv");
                copyFileCsv("wordpairs.csv");
            } catch (IOException e) {
                e.printStackTrace();
            }
            //
            copyAssets("images");
            copyAssets("videos");
            copyAssets("pdf");
        }
        //
    }
    //
    /**
     * copy the single csv file with initial settings and content from assets
     *
     * @param fileName string with the name of the file to be copied
     */
    public void copyFileCsv(String fileName) throws IOException {

        InputStream sourceStream = getAssets().open(getString(R.string.csv) + getString(R.string.character_slash) + fileName);
        InputStreamReader charSourceStream = new InputStreamReader(sourceStream, StandardCharsets.UTF_8);
        String destFileName = Environment.getExternalStorageDirectory()
                + getString(R.string.character_slash) + getString(R.string.app_name)
                + getString(R.string.character_slash) + fileName;
        FileOutputStream destStream = new FileOutputStream(destFileName);
        OutputStreamWriter charDestStream = new OutputStreamWriter(destStream, StandardCharsets.UTF_8);
        char[] buffer = new char[100];
        int charactersRead=0;
        while ( (charactersRead=charSourceStream.read(buffer))!=-1) {
            charDestStream.write(buffer,0,charactersRead);
        }
        charSourceStream.close();
        charDestStream.close();
        destStream.close();
        sourceStream.close();

    }
    //
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
     * on callback from SettingsFragment to this Activity
     *
     * @param v view root fragment view
     */
    @Override
    public void receiveResultSettings(View v) { rootViewFragment = v;  }
    //
    /**
     * copy assets images and videos to storage.
     * <p>
     * Refer to <a href="https://stackoverflow.com/questions/4447477/how-to-copy-files-from-assets-folder-to-sdcard">stackoverflow</a>
     * answer of <a href="https://stackoverflow.com/users/481239/rohith-nandakumar">Rohith Nandakumar</a>
     *
     * @param path string assets folder to copy
     * @see #copyFile(InputStream, OutputStream)
     */
    private void copyAssets(String path) {
        AssetManager assetManager = getAssets();
        String[] files = null;
        try {
            files = assetManager.list(path);
        } catch (IOException e) {
            // Log.e("tag", "Failed to get asset file list.", e);
        }
        if (files != null) {
            for (String filename : files) {
                InputStream in = null;
                OutputStream out = null;
                try {
                    in = assetManager.open(path + "/" + filename);
                    // qui
                    File outFile = new File(rootPath + "/" + getString(R.string.app_name), filename);
                    out = new FileOutputStream(outFile);
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



}
