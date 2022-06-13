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
// import android.util.Log;
import android.view.View;
import android.widget.EditText;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentManager;

import com.example.NaiveAAC.R;
import com.example.NaiveAAC.activities.Arasaac.PictogramsAll;
import com.example.NaiveAAC.activities.Arasaac.PictogramsAllToModify;
import com.example.NaiveAAC.activities.Game.ChoiseOfGame.ChoiseOfGameActivity;
import com.example.NaiveAAC.activities.Game.GameParameters.GameParameters;
import com.example.NaiveAAC.activities.Game.Utils.ActionbarFragment;
import com.example.NaiveAAC.activities.Grammar.ComplementsOfTheName;
import com.example.NaiveAAC.activities.Grammar.GrammaticalExceptions;
import com.example.NaiveAAC.activities.Grammar.ListsOfNames;
import com.example.NaiveAAC.activities.Grammar.Verbs;
import com.example.NaiveAAC.activities.Graphics.Images;
import com.example.NaiveAAC.activities.Graphics.Videos;
import com.example.NaiveAAC.activities.Phrases.Phrases;
import com.example.NaiveAAC.activities.Settings.AccountFragment;
import com.example.NaiveAAC.activities.Settings.Utils.AccountActivityAbstractClass;
import com.example.NaiveAAC.activities.Settings.Utils.AdvancedSettingsDataImportExportHelper;
import com.example.NaiveAAC.activities.Settings.Utils.SettingsFragmentAbstractClass;
import com.example.NaiveAAC.activities.Stories.Stories;
import com.example.NaiveAAC.activities.WordPairs.WordPairs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * <h1>AccountActivityRealmCreation</h1>
 * <p><b>AccountActivityRealmCreation</b> create the initial realm database.</p>
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
public class AccountActivityRealmCreation extends AccountActivityAbstractClass implements
        SettingsFragmentAbstractClass.onFragmentEventListenerSettings {
    //
    private static final String TAG = "VERBO";
    private final String TAGPERMISSION = "Permission";
    //
    public static final String EXTRA_MESSAGE ="helloworldandroidMessage" ;

    //

    public String infinitive = "nessun verbo";
    //
    public FragmentManager fragmentManager;
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
    //
    /**
     * Called when the user taps the save account button.
     * </p>
     * if the case creates creates the initial realm database.</p>
     *
     * @param view view of tapped button
     * @see #isStoragePermissionGranted
     * @see AdvancedSettingsDataImportExportHelper#findExternalStorageRoot()
     * @see #prepareTheSimsimDirectory
     * @see #loadJSONFromAsset(String)
     * @see #registerPersonName(String)
     * @see #openFileOutput(String)
     * @see PictogramsAll
     * @see PictogramsAllToModify
     * @see Verbs
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
        //
        if (isStoragePermissionGranted()) {
            //
            root = findExternalStorageRoot();
            rootPath = root.getAbsolutePath();
            //
            // naiveaac dir registration and csv copy from assets to dir naiveaac
                prepareTheSimsimDirectory();
//
                realm= Realm.getDefaultInstance();
                if (realm.isEmpty()) {

                        //
                        PictogramsAllToModify.importFromCsv(context, realm);
                        // creo la collezione di pictogramsToModify (vedi java cap 39)
                        HashSet<String> pictogramsToModifySet =
                            new HashSet<String>();
                        RealmResults<PictogramsAllToModify> resultsDB =
                            realm.where(PictogramsAllToModify.class).findAll();
                        for (PictogramsAllToModify taskitems: resultsDB) {
                            pictogramsToModifySet.add(taskitems.get_id());
                            }
                    // RECOVER ALL PICTOGRAMS FROM ARASAAC
                    //
                    JSONArray response = null;
                    try {
                        response = new JSONArray(loadJSONFromAsset("it.json"));
                        } catch (JSONException e) {
                        e.printStackTrace();
                        }

                    for (int i = 0; i < response.length(); i++) {
                        //

                        try {
                            JSONObject j = response.getJSONObject(i);

                            String _id = j.getString(getString(R.string._id));
                            //
                            // PICTOGRAMSALLTOMODIFY ID EXCLUSION
                            if(!pictogramsToModifySet.contains(_id))
                            {
                            // 
                            JSONArray jcategories = j.getJSONArray(getString(R.string.categories));
                            //
                            JSONArray jKeywords = j.getJSONArray(getString(R.string.keywords));
                            for (int ik = 0; ik < jKeywords.length(); ik++) {
                                JSONObject jKeyword = jKeywords.getJSONObject(ik);
                                String type = jKeyword.getString(getString(R.string.type));
                                String keyword = jKeyword.getString(getString(R.string.keyword));
                                //
                                if ((type.equals(getString(R.string.number_2))) && (jKeyword.has(getString(R.string.plural))))
                                {
                                    String plural = jKeyword.getString(getString(R.string.plural));
                                    realm.beginTransaction();
                                    PictogramsAll p2 = realm.createObject(PictogramsAll.class);
                                    p2.set_id(_id);
                                    p2.setType(type);
                                    p2.setKeyword(plural);
                                    p2.setPlural("Y");
                                    p2.setKeywordPlural(" ");
                                    realm.commitTransaction();
                                }

                                if (type.equals(getString(R.string.number_1)) || type.equals(getString(R.string.number_2)) || type.equals(getString(R.string.number_3)) )
                                {
                                       realm.beginTransaction();
                                       PictogramsAll p = realm.createObject(PictogramsAll.class);
                                       p.set_id(_id);
                                       p.setType(type);
                                       p.setKeyword(keyword);
                                       p.setPlural(getString(R.string.character_n));
                                       p.setKeywordPlural(" ");
                                       if (jKeyword.has(getString(R.string.plural)))
                                        {
                                           String plural = jKeyword.getString(getString(R.string.plural));
                                           p.setKeywordPlural(plural);
                                        }

                                       realm.commitTransaction();
                                }
                                //
                                if (type.equals(getString(R.string.number_4)) || type.equals(getString(R.string.number_6)))
                                {

                                    for (int ic = 0; ic < jcategories.length(); ic++) {
                                        String jcategory = jcategories.getString(ic);
                                        if (jcategory.equals(getString(R.string.article))
                                                || jcategory.equals(getString(R.string.preposition))
                                                || jcategory.equals(getString(R.string.adverb_of_place))
                                                || jcategory.equals(getString(R.string.number))) {

                                            realm.beginTransaction();
                                            ComplementsOfTheName cOTN = realm.createObject(ComplementsOfTheName.class);
                                            cOTN.set_id(_id);
                                            cOTN.setType(type);
                                            cOTN.setKeyword(keyword);
                                            realm.commitTransaction();
                                            break;
                                        }
                                    }
                                }

                            }
                            //

                            }
                            //

                        } catch (JSONException e) {
                        }

                    }

                    //
                    // RECOVER VERB CONJUGATION FROM ASSETS
                    //
                    JSONArray m_jArry = null;
                    try {
                        m_jArry = new JSONArray(loadJSONFromAsset("verbs.json"));
                        } catch (JSONException e) {
                        e.printStackTrace();
                        }
//
                    String conjugation;
                    String form;
                    Verbs pv;
                    for (int iv = 0; iv < m_jArry.length(); iv++) {
                        try {
                            JSONObject joVerbs = m_jArry.getJSONObject(iv);
                            JSONArray jconjugations = joVerbs.getJSONArray("conjugations");
                            for (int ic = 0; ic < jconjugations.length(); ic++) {
                                JSONObject jconjugation = jconjugations.getJSONObject(ic);
                                String group = jconjugation.getString("group");
                                switch(group) {
                                    //
                                    case "infinitive":
                                        infinitive = jconjugation.getString(getString(R.string.value));
                                        // debug
                                        // Log.d(TAG, "Ultimo verbo = " + infinitive);
                                        break;
                                    case "auxiliaryverb":
                                        break;
                                    case "indicative/pasthistoric":
                                        break;
                                    case "conditional/present":
                                        break;
                                    case "subjunctive/present":
                                        break;
                                    case "subjunctive/imperfect":
                                        break;
                                    case "indicative/present":
                                    case "indicative/imperfect":
                                    case "indicative/future":
                                        conjugation = jconjugation.getString(getString(R.string.value));
                                        form = jconjugation.getString("form");
                                        //
                                        realm.beginTransaction();
                                        pv = realm.createObject(Verbs.class);
                                        pv.setConjugation(conjugation);
                                        pv.setForm(form);
                                        pv.setGroup(group);
                                        pv.setInfinitive(infinitive);
                                        realm.commitTransaction();
                                        break;
                                    default:
                                        conjugation = jconjugation.getString(getString(R.string.value));
                                        //
                                        realm.beginTransaction();
                                        pv = realm.createObject(Verbs.class);
                                        pv.setConjugation(conjugation);
                                        pv.setInfinitive(infinitive);
                                        realm.commitTransaction();
                                }
                            }

                        }
                        catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
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

        // register the user in the shared preferences
        // and move on to the welcome activity
        EditText editText = (EditText) rootViewFragment.findViewById(R.id.editTextTextAccount);
        String textPersonName = editText.getText().toString();
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
            File destination = openFileOutput("copiarealm");
            realm.writeCopyTo(destination);
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
    /**
     * load a local json file from assets.
     * <p>
     * Refer to <a href="https://stackoverflow.com/questions/19945411/how-can-i-parse-a-local-json-file-from-assets-folder-into-a-listview">stackoverflow</a>
     * answer of <a href="https://stackoverflow.com/users/1839336/grishu">grishu</a>
     *
     * @param file string with the name of the file to be loaded
     * @return json string json to parse
     */
    public String loadJSONFromAsset(String file) {
        String json = null;
        try {
            InputStream is = this.getAssets().open(file);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, StandardCharsets.UTF_8);
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
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
     * open the copy realm file
     *
     * @param fileName string name of the output file
     * @return file
     * @see AdvancedSettingsDataImportExportHelper#findExternalStorageRoot
     */
    public File openFileOutput(String fileName){
        File root = findExternalStorageRoot();
        //
        File dir = new File (root.getAbsolutePath() + getString(R.string.character_slash) + getString(R.string.app_name));
        File file = new File(dir, fileName);
        file.setReadable(true, true);
        return file;
    }
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
