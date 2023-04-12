package com.sampietro.NaiveAAC.activities.Graphics;

import static com.sampietro.NaiveAAC.activities.Settings.Utils.AdvancedSettingsDataImportExportHelper.dataProcess;
import static com.sampietro.NaiveAAC.activities.Settings.Utils.AdvancedSettingsDataImportExportHelper.savBak;

import android.content.Context;

import com.sampietro.NaiveAAC.R;
import com.sampietro.NaiveAAC.activities.Settings.Utils.AdvancedSettingsDataImportExportHelper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Objects;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import io.realm.annotations.Index;

/**
 * <h1>Videos</h1>
 * <p><b>Videos</b>  contains filepaths for videos.</p>
 *
 * @version     1.1, 04/22/22
 */
public class Videos extends RealmObject {
    /**
     * search key for premium videos and thumbnail videos for the choice of the game.</p>
     * descrizione = prize for default premium video
     * descrizione = GameParameters#gameName for thumbnail videos for the choice of the game</p>
     */
    @Index
    private String descrizione;
    /**
     * contains the file path (originally contained the uri).
     */
    private String uri;
    /**
     * contains copyright information.
     */
    private String copyright;
    /**
     * contains Y if the object was imported from assets.
     */
    private String fromAssets;
    /*
     * getter, setter and other methods
     */
    /**
     * get <code>descrizione</code>.
     *
     * @return descrizione string data to get
     */
    public String getDescrizione() {
        return descrizione;
    }
    /**
     * get <code>uri</code>.
     *
     * @return uri string file path to get
     */
    public String getUri() {
        return uri;
    }
    /**
     * get <code>copyright</code>.
     *
     * @return copyright string file path to get
     */
    public String getCopyright() {
        return copyright;
    }
    /**
     * get <code>fromAssets</code>.
     *
     * @return fromAssets string Y if the object was imported from assets
     */
    public String getFromAssets() {
        return fromAssets;
    }
    //
    /**
     * set <code>descrizione</code>.
     *
     * @param descrizione string data to set
     */
    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }
    /**
     * set <code>uri</code>.
     *
     * @param uri string file path to set
     */
    public void setUri(String uri) {
        this.uri = uri;
    }
    /**
     * set <code>copyright</code>.
     *
     * @param copyright string copyright to set
     */
    public void setCopyright(String copyright) {
        this.copyright = copyright;
    }
    /**
     * set <code>fromAssets</code>.
     *
     * @param fromAssets string fromAssets to set
     */
    public void setFromAssets(String fromAssets) {
        this.fromAssets = fromAssets;
    }
    //
    /**
     * Export to CSV.
     * Refer to <a href="https://stackoverflow.com/questions/49468809/is-there-a-way-to-export-realm-database-to-csv-json">stackoverflow</a>
     * answer of <a href="https://stackoverflow.com/users/8989439/vrasheed">vrasheed</a>
     *
     * @param context context
     * @param realm realm obtained from the activity by Realm#getDefaultInstance
     * @see AdvancedSettingsDataImportExportHelper#grabHeader(Realm, String)
     * @see AdvancedSettingsDataImportExportHelper#savBak(Context, String, String)
     * @see AdvancedSettingsDataImportExportHelper#dataProcess(String)
     */
    public static void exporttoCsv(Context context, Realm realm){
//
        String FILE_NAME = "videos.csv";
        // Grab all data from the DB in question :

        RealmResults<Videos> resultsDB = realm.where(Videos.class).findAll();

        // Here we need to put in header fields
        String dataP = null;
        String header = AdvancedSettingsDataImportExportHelper.grabHeader(realm, "Videos");

        // We write the header to file
        savBak(context, header,FILE_NAME );

        // Now we write all the data corresponding to the fields grabbed above:
        //
        int count = resultsDB.size();
        if (count != 0) {
            int irrh=0;
            while((irrh < count  )) {
                Videos taskitems = resultsDB.get(irrh);
                assert taskitems != null;
                //
                dataP = taskitems.toString();
                // We process the data obtained and add commas and formatting:
                dataP = dataProcess(dataP);
                // Workaround to remove the last comma from final string
                int total = dataP.length() - 1;
                dataP =  dataP.substring(0,total);
                // Workaround to remove the last line feed from final row
                // We write the data to file
                if (irrh == count-1)
                {
                    savBak(context, dataP, FILE_NAME, true);
                }
                else {
                    savBak(context, dataP, FILE_NAME, false);
                }
                irrh++;
            }
        }
    }
    /**
     * Export to CSV with set.
     * Refer to <a href="https://stackoverflow.com/questions/49468809/is-there-a-way-to-export-realm-database-to-csv-json">stackoverflow</a>
     * answer of <a href="https://stackoverflow.com/users/8989439/vrasheed">vrasheed</a>
     * Refer to <a href="https://stackoverflow.com/questions/32066648/realm-query-with-list">stackoverflow</a>
     * answer of <a href="https://stackoverflow.com/users/3710341/sagar-chapagain">Sagar Chapagain</a>
     *
     * @param context context
     * @param realm realm obtained from the activity by Realm#getDefaultInstance
     * @param videosDescriptionSet HashSet<String> to export
     * @param dir File with the output folder
     * @see AdvancedSettingsDataImportExportHelper#grabHeader(Realm, String)
     * @see AdvancedSettingsDataImportExportHelper#savBak(Context, String, String)
     * @see AdvancedSettingsDataImportExportHelper#dataProcess(String)
     */
    public static void exporttoCsv(Context context, Realm realm, HashSet<String> videosDescriptionSet, File dir){
//
        String FILE_NAME = "videos.csv";
        // Grab all data from the DB in question :
        String[] videosDescriptionArray = new String[videosDescriptionSet.size()];
        videosDescriptionSet.toArray(videosDescriptionArray);
        RealmQuery<Videos> realmQuery  = realm.where(Videos.class)
                .in("descrizione",videosDescriptionArray);
        RealmResults<Videos> resultsDB = realmQuery.findAll();
        // Here we need to put in header fields
        String dataP = null;
        String header = AdvancedSettingsDataImportExportHelper.grabHeader(realm, "Videos");

        // We write the header to file
        savBak(context, header,FILE_NAME, dir );

        // Now we write all the data corresponding to the fields grabbed above:
        //
        int count = resultsDB.size();
        if (count != 0) {
            int irrh=0;
            while((irrh < count  )) {
                Videos taskitems = resultsDB.get(irrh);
                assert taskitems != null;
                //
                dataP = taskitems.toString();
                // We process the data obtained and add commas and formatting:
                dataP = dataProcess(dataP);
                // Workaround to remove the last comma from final string
                int total = dataP.length() - 1;
                dataP =  dataP.substring(0,total);
                // Workaround to remove the last line feed from final row
                // We write the data to file
                if (irrh == count-1)
                {
                    savBak(context, dataP, FILE_NAME, dir, true);
                }
                else {
                    savBak(context, dataP, FILE_NAME, dir, false);
                }
                irrh++;
            }
        }
    }
    /**
     * Import from CSV.
     * Refer to <a href="https://stackoverflow.com/questions/49468809/is-there-a-way-to-export-realm-database-to-csv-json">stackoverflow</a>
     * answer of <a href="https://stackoverflow.com/users/8989439/vrasheed">vrasheed</a>
     *
     * @param context context
     * @param realm realm obtained from the activity by Realm#getDefaultInstance
     * @param mode string import mode (Append or Replace)
     */
    public static void importFromCsvFromInternalStorage(Context context,Realm realm, String mode)
    {
        if (Objects.equals(mode, "Replace"))
            {
                // clear the table
                RealmResults<Videos> daCancellare = realm.where(Videos.class).findAll();
                realm.beginTransaction();
                daCancellare.deleteAllFromRealm();
                realm.commitTransaction();
            }
        //
        String rootPath = context.getFilesDir().getAbsolutePath();
        //
        String FILE_NAME = "videos.csv";
        //adding to db
        BufferedReader br = null;
        String line = "";
        String cvsSplitBy = context.getString(R.string.character_comma);
        //
        try {
            br = new BufferedReader(new InputStreamReader(context.openFileInput(FILE_NAME), StandardCharsets.UTF_8));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        if (br != null) {
            boolean isTheFirstLine = true;
            while (true) {
                try {
                    if (!((line = br.readLine()) != null)) break;
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
                // exclusion of the first line
                if (isTheFirstLine)
                {
                    isTheFirstLine = false;
                }
                else
                {
                    // added otherwise if the line ends with a comma it does not consider the last subdivision of the csv
                    String lineLastCharacter = line.substring(line.length() - 1);
                    if (lineLastCharacter.equals(cvsSplitBy)) {
                        line = line + " ";
                    }
                    // use comma as separator
                    final String[] oneWord= line.split(cvsSplitBy);
                    //
                    // checks
                    if (oneWord[1] != null ) {
                        // replace with root of the external storage or data directory
                        String uri = oneWord[1].replaceFirst("rootdirectory", rootPath);
                        //
                        File f = new File(uri);
                        if (oneWord[0] != null && oneWord[0].length() > 0
                                && oneWord[2] != null
                                && oneWord[3] != null
                                && f.exists() ) {
                            realm.beginTransaction();
                            Videos images = realm.createObject(Videos.class);
                            // set the fields here
                            images.setDescrizione(oneWord[0]);
                            images.setUri(uri);
                            images.setCopyright(oneWord[2]);
                            images.setFromAssets(oneWord[3]);
                            realm.commitTransaction();
                        }
                    }
                }
            }
        }
    }
    //
    /**
     * Import from CSV the videos of a story.
     * Refer to <a href="https://stackoverflow.com/questions/49468809/is-there-a-way-to-export-realm-database-to-csv-json">stackoverflow</a>
     * answer of <a href="https://stackoverflow.com/users/8989439/vrasheed">vrasheed</a>
     *
     * @param context context
     * @param realm realm obtained from the activity by Realm#getDefaultInstance
     *
     */
    public static void importFromCsvFromInternalStorage(Context context,Realm realm)
    {
        String rootPath = context.getFilesDir().getAbsolutePath();
        //
        String FILE_NAME = "videos.csv";
        //adding to db
        BufferedReader br = null;
        String line = "";
        String cvsSplitBy = context.getString(R.string.character_comma);
        //
        try {
            br = new BufferedReader(new InputStreamReader(context.openFileInput(FILE_NAME), StandardCharsets.UTF_8));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        if (br != null) {
            boolean isTheFirstLine = true;
            while (true) {
                try {
                    if (!((line = br.readLine()) != null)) break;
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
                // exclusion of the first line
                if (isTheFirstLine)
                {
                    isTheFirstLine = false;
                }
                else
                {
                    // added otherwise if the line ends with a comma it does not consider the last subdivision of the csv
                    String lineLastCharacter = line.substring(line.length() - 1);
                    if (lineLastCharacter.equals(cvsSplitBy)) {
                        line = line + " ";
                    }
                    // use comma as separator
                    final String[] oneWord= line.split(cvsSplitBy);
                    //
                    // checks
                    if (oneWord[1] != null ) {
                        // clear the table
                        RealmResults<Videos> daCancellare =
                                realm.where(Videos.class).equalTo("descrizione", oneWord[0]).findAll();
                        realm.beginTransaction();
                        daCancellare.deleteAllFromRealm();
                        realm.commitTransaction();
                        // replace with root of data directory
//                      String uri = oneWord[1].replaceFirst("rootdirectory", rootPath);
                        String fileName = oneWord[1].substring(oneWord[1].lastIndexOf("/")+1);
                        String uri = rootPath + "/" + fileName;
                        //
                        File f = new File(uri);
                        if (oneWord[0] != null && oneWord[0].length() > 0
                                && oneWord[2] != null
                                && oneWord[3] != null
                                && f.exists() ) {
                            realm.beginTransaction();
                            Videos images = realm.createObject(Videos.class);
                            // set the fields here
                            images.setDescrizione(oneWord[0]);
                            images.setUri(uri);
                            images.setCopyright(oneWord[2]);
                            images.setFromAssets(oneWord[3]);
                            realm.commitTransaction();
                        }
                    }
                }
            }
        }
    }
}
