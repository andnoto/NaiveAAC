package com.sampietro.NaiveAAC.activities.Grammar;

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
import java.util.Objects;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmResults;

/**
 * <h1>ListsOfNames</h1>
 * <p><b>ListsOfNames</b>  contains a list of names classified by a keyword.</p>
 * each name can be associated to an arasaac url or to a file path.
 */
public class ListsOfNames extends RealmObject {
    /**
     * key or class that identifies the list.
     */
    private String keyword;
    /**
     * name belonging to the list identified with the <code>keyword</code>.
     */
    private String word;
    /**
     * represent the type of image associated with the <code>word</code>.
     * <p>
     * uritype = S then , if it exists, uri refers to storage
     * <p>
     * uritype = A then , if it exists, uri refers to a url of arasaac
     * <p>
     * if neither the arasaac url nor the file path are indicated, the image is associated using PictogramsAll
     */
    private String uriType;
    /**
     * contains the file path or a url of arasaac  (originally contained the uri).
     */
    private String uri;
    /**
     * contains Y if the object was imported from assets.
     */
    private String fromAssets;
    /*
     * getter, setter and other methods
     */
    /**
     * get <code>keyword</code>.
     *
     * @return keyword string data to get
     */
    public String getKeyword() {
        return keyword;
    }
    /**
     * get <code>word</code>.
     *
     * @return word string data to get
     */
    public String getWord() {
        return word;
    }
    /**
     * get <code>uriType</code>.
     *
     * @return uriType string data to get
     */
    public String getUriType() {
        return uriType;
    }
    /**
     * get <code>uri</code>.
     *
     * @return uri string data to get
     */
    public String getUri() {
        return uri;
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
     * set <code>keyword</code>.
     *
     * @param keyword string data to set
     */
    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }
    /**
     * set <code>word</code>.
     *
     * @param word string data to set
     */
    public void setWord(String word) {
        this.word = word;
    }
    /**
     * set <code>uriType</code>.
     *
     * @param uriType string data to set
     */
    public void setUriType(String uriType) {
        this.uriType = uriType;
    }
    /**
     * set <code>uri</code>.
     *
     * @param uri string data to set
     */
    public void setUri(String uri) {
        this.uri = uri;
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
        String FILE_NAME = "listsofnames.csv";
        // Grab all data from the DB in question (ListsOfNames):

        RealmResults<ListsOfNames> resultsDB = realm.where(ListsOfNames.class).findAll();

        // Here we need to put in header fields
        String dataP = null;
        String header = AdvancedSettingsDataImportExportHelper.grabHeader(realm, "ListsOfNames");

        // We write the header to file
        savBak(context, header,FILE_NAME );

        // Now we write all the data corresponding to the fields grabbed above:
        //
        int count = resultsDB.size();
        if (count != 0) {
            int irrh=0;
            while((irrh < count  )) {
                ListsOfNames taskitems = resultsDB.get(irrh);
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
     * Import from CSV.
     * Refer to <a href="https://stackoverflow.com/questions/49468809/is-there-a-way-to-export-realm-database-to-csv-json">stackoverflow</a>
     * answer of <a href="https://stackoverflow.com/users/8989439/vrasheed">vrasheed</a>
     *
     * @param context context
     * @param realm realm obtained from the activity by Realm#getDefaultInstance
     * @param mode string import mode (Append or Replace)
     */
    public static void importFromCsvFromInternalStorage(Context context, Realm realm, String mode)
    {
        if (Objects.equals(mode, "Replace"))
            {
                // clear the table
                RealmResults<ListsOfNames> daCancellare = realm.where(ListsOfNames.class).findAll();
                realm.beginTransaction();
                daCancellare.deleteAllFromRealm();
                realm.commitTransaction();
            }
        //
        String rootPath = context.getFilesDir().getAbsolutePath();
        //
        String FILE_NAME = "listsofnames.csv";
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
                    final String[] oneWord= line.split(cvsSplitBy, -1);
                    // checks
                    File f = null;
                    String uri = null;
                    if (oneWord[3] != null ) {
                        if (oneWord[2].equals(context.getString(R.string.character_s))) {
                            // replace with root of the external storage or data directory
                            uri = oneWord[3].replaceFirst(context.getString(R.string.rootdirectory), rootPath);
                            //
                            f = new File(oneWord[3]);
                        }
                        else
                        {
                            uri = oneWord[3];
                        }
                    }
                    if (oneWord[0] != null
                            && oneWord[1] != null
                            && oneWord[2] != null
                            && oneWord[3] != null
                            && oneWord[4] != null ) {
                        if (oneWord[0].length() > 0
                                && oneWord[1].length() > 0
                                &&
                        ((!oneWord[2].equals(context.getString(R.string.character_s)))
                                || (oneWord[2].equals(context.getString(R.string.character_s))  && f.exists()))) {
                            //
                            realm.beginTransaction();
                            ListsOfNames listsOfNames = realm.createObject(ListsOfNames.class);
                            // set the fields here
                            listsOfNames.setKeyword(oneWord[0]);
                            listsOfNames.setWord(oneWord[1]);
                            listsOfNames.setUriType(oneWord[2]);
                            //
                            listsOfNames.setUri(uri);
                            listsOfNames.setFromAssets(oneWord[4]);
                            realm.commitTransaction();
                        }
                    }
                }
            }
        }
    }
    //
}
