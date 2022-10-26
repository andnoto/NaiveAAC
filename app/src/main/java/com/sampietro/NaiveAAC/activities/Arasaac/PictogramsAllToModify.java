package com.sampietro.NaiveAAC.activities.Arasaac;

import static com.sampietro.NaiveAAC.activities.Settings.Utils.AdvancedSettingsDataImportExportHelper.dataProcess;
import static com.sampietro.NaiveAAC.activities.Settings.Utils.AdvancedSettingsDataImportExportHelper.grabHeader;
import static com.sampietro.NaiveAAC.activities.Settings.Utils.AdvancedSettingsDataImportExportHelper.savBak;

import android.content.Context;

import com.sampietro.NaiveAAC.R;
import com.sampietro.NaiveAAC.activities.Settings.Utils.AdvancedSettingsDataImportExportHelper;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmResults;

/**
 * <h1>PictogramsAllToModify</h1>
 * <p><b>PictogramsAllToModify</b>  contains the list of data from the Arasaac which have been corrected because deemed inappropriate.</p>
 */
public class PictogramsAllToModify extends RealmObject {
    /**
     * represent the key of the pictogram relating to the data to be modified.
     */
    private String _id;
    /**
     * represent the type of modification.
     * <p>
     * modificationType = E id pictogram to exclude
     * <p>
     * modificationType = I id pictogram to include (to implement)
     * <p>
     * modificationType = C id pictogram to change (to implement)
     */
    private String modificationType;
    /**
     * represent the PictogramAll#keyword to be included or changed.
     */
    private String keyword;
    /**
     * represent the PictogramAll#plural to be included or changed.
     */
    private String plural;

    /*
     * getter, setter and other methods
     */
    /**
     * get <code>_id</code>.
     *
     * @return _id string data to get
     */
    public String get_id() {
        return _id;
    }
    /**
     * get <code>modificationType</code>.
     *
     * @return modificationType string data to get
     */
    public String getModificationType() {
        return modificationType;
    }
    /**
     * get <code>keyword</code>.
     *
     * @return keyword string data to get
     */
    public String getKeyword() {
        return keyword;
    }
    /**
     * get <code>plural</code>.
     *
     * @return plural string data to get
     */
    public String getPlural() {
        return plural;
    }

    //
    /**
     * set <code>_id</code>.
     *
     * @param _id string data to set
     */
    public void set_id(String _id)
    {
        this._id = _id;
    }
    /**
     * set <code>modificationType</code>.
     *
     * @param modificationType string data to set
     */
    public void setModificationType(String modificationType)
    {
        this.modificationType = modificationType;
    }
    /**
     * set <code>keyword</code>.
     *
     * @param keyword string data to set
     */
    public void setKeyword(String keyword)
    {
        this.keyword = keyword;
    }
    /**
     * set <code>plural</code>.
     *
     * @param plural string data to set
     */
    public void setPlural(String plural)
    {
        this.plural = plural;
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
        String FILE_NAME = "pictogramsalltomodify.csv";
        // Grab all data from the DB in question (PictogramsAllToModify):

        RealmResults<PictogramsAllToModify> resultsDB =
                realm.where(PictogramsAllToModify.class).findAll();

        // Here we need to put in header fields
        String dataP = null;
        String header = grabHeader(realm, "PictogramsAllToModify");

        // We write the header to file
        savBak(context, header,FILE_NAME );

        // Now we write all the data corresponding to the fields grabbed above:
        //
        int count = resultsDB.size();
        if (count != 0) {
            int irrh=0;
            while((irrh < count  )) {
                PictogramsAllToModify taskitems = resultsDB.get(irrh);
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
        //
    }
    //
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
        RealmResults<PictogramsAllToModify> daCancellare = realm.where(PictogramsAllToModify.class).findAll();
        realm.beginTransaction();
        daCancellare.deleteAllFromRealm();
        realm.commitTransaction();
        //
        String FILE_NAME = "pictogramsalltomodify.csv";
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
                    // use comma as separator
                    final String[] oneWord= line.split(cvsSplitBy, -1);
                    //
                    realm.beginTransaction();
                    PictogramsAllToModify pictogramsAllToModify = realm.createObject(PictogramsAllToModify.class);
                    // set the fields here
                    pictogramsAllToModify.set_id(oneWord[0]);
                    pictogramsAllToModify.setModificationType(oneWord[1]);
                    pictogramsAllToModify.setKeyword(oneWord[2]);
                    pictogramsAllToModify.setPlural(oneWord[3]);
                    realm.commitTransaction();
                }
            }
        }
    }

}
