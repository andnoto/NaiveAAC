package com.example.NaiveAAC.activities.Graphics;

import static com.example.NaiveAAC.activities.Settings.Utils.AdvancedSettingsDataImportExportHelper.dataProcess;
import static com.example.NaiveAAC.activities.Settings.Utils.AdvancedSettingsDataImportExportHelper.savBak;

import android.content.Context;

import com.example.NaiveAAC.R;
import com.example.NaiveAAC.activities.Settings.Utils.AdvancedSettingsDataImportExportHelper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmResults;

/**
 * <h1>Images</h1>
 * <p><b>Images</b>  contains filepaths for images other than Arasaac pictograms.</p>
 */
public class Images extends RealmObject {
    /**
     * word or phrase.
     */
    private String descrizione;
    /**
     * contains the file path (originally contained the uri).
     */
    private String uri;
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
    String FILE_NAME = "images.csv";
    // Grab all data from the DB in question :

    RealmResults<Images> resultsDB = realm.where(Images.class).findAll();

    // Here we need to put in header fields
    String dataP = null;
    String header = AdvancedSettingsDataImportExportHelper.grabHeader(realm, "Images");

    // We write the header to file
    savBak(context, header,FILE_NAME );

    // Now we write all the data corresponding to the fields grabbed above:
    //
    int count = resultsDB.size();
    if (count != 0) {
        int irrh=0;
        while((irrh < count  )) {
            Images taskitems = resultsDB.get(irrh);
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
     */
    public static void importFromCsvFromInternalStorage(Context context, Realm realm)
    {
        // clear the table
        RealmResults<Images> daCancellare = realm.where(Images.class).findAll();
        realm.beginTransaction();
        daCancellare.deleteAllFromRealm();
        realm.commitTransaction();
        //
        String rootPath = context.getFilesDir().getAbsolutePath();
        //
        String FILE_NAME = "images.csv";
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
                     // checks
                    if (oneWord[1] != null ) {
                        // replace with root of the external storage or data directory
                        String uri = oneWord[1].replaceFirst("rootdirectory", rootPath);
                        //
                        File f = new File(uri);
                        if (oneWord[0] != null && oneWord[0].length() > 0 && f.exists() ) {
                            realm.beginTransaction();
                            Images images = realm.createObject(Images.class);
                            // set the fields here
                            images.setDescrizione(oneWord[0]);
                            images.setUri(uri);
                            realm.commitTransaction();
                        }
                    }
                }
            }
        }
    }
    //
}
