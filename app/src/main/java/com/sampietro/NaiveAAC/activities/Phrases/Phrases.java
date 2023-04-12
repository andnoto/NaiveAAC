package com.sampietro.NaiveAAC.activities.Phrases;

import static com.sampietro.NaiveAAC.activities.Settings.Utils.AdvancedSettingsDataImportExportHelper.dataProcess;
import static com.sampietro.NaiveAAC.activities.Settings.Utils.AdvancedSettingsDataImportExportHelper.savBak;

import android.content.Context;

import com.sampietro.NaiveAAC.R;
import com.sampietro.NaiveAAC.activities.Settings.Utils.AdvancedSettingsDataImportExportHelper;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmResults;

/**
 * <h1>Phrases</h1>
 * <p><b>Phrases</b>  contains phrases used by game2 and other games similarly eliza software.</p>
 * Refer to <a href="https://en.wikipedia.org/wiki/ELIZA">wikipedia</a>
 */
public class Phrases extends RealmObject {
    /**
     * represent the type of phrases.
     * <p>
     * tipo = welcome phrase first part which is pronounced before the player's name
     * <p>
     * tipo = welcome phrase second part which is pronounced after the player's name
     * <p>
     * tipo = reminder phrase which is pronounced if nothing is said several times after pressing the listen button
     * <p>
     * tipo = reminder phrase plural which is pronounced if nothing is said several times after pressing the listen button
     * (if the last noun of the sentence is plural)
     * <p>
     * the last noun of the sentence is added to the reminder phrase and proceeding backwards are added
     * also all related complements to the name
     */
    private String tipo;
    /**
     * phrase.
     */
    private String descrizione;
    /**
     * contains Y if the object was imported from assets.
     */
    private String fromAssets;
    /*
     * getter, setter and other methods
     */
    /**
     * get <code>tipo</code>.
     *
     * @return tipo string data to get
     */
    public String getTipo() {
        return tipo;
    }
    /**
     * get <code>descrizione</code>.
     *
     * @return descrizione string data to get
     */
    public String getDescrizione() {
        return descrizione;
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
     * set <code>tipo</code>.
     *
     * @param tipo string data to set
     */
    public void setTipo(String tipo) {
        this.tipo = tipo;
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
        String FILE_NAME = "phrases.csv";
        // Grab all data from the DB in question :

        RealmResults<Phrases> resultsDB = realm.where(Phrases.class).findAll();

        // Here we need to put in header fields
        String dataP = null;
        String header = AdvancedSettingsDataImportExportHelper.grabHeader(realm, "Phrases");

        // We write the header to file
        savBak(context, header,FILE_NAME );

        // Now we write all the data corresponding to the fields grabbed above:
        //
        int count = resultsDB.size();
        if (count != 0) {
            int irrh=0;
            while((irrh < count  )) {
                Phrases taskitems = resultsDB.get(irrh);
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
                RealmResults<Phrases> daCancellare = realm.where(Phrases.class).findAll();
                realm.beginTransaction();
                daCancellare.deleteAllFromRealm();
                realm.commitTransaction();
            }
        //
        String FILE_NAME = "phrases.csv";
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
                    if (oneWord[0] != null
                            && oneWord[0].length() > 0
                            && oneWord[1] != null
                            && oneWord[1].length() > 0
                            && oneWord[2] != null ) {
                        realm.beginTransaction();
                        Phrases phrases = realm.createObject(Phrases.class);
                        // set the fields here
                        phrases.setTipo(oneWord[0]);
                        phrases.setDescrizione(oneWord[1]);
                        phrases.setFromAssets(oneWord[2]);
                        realm.commitTransaction();
                    }
                }
            }
        }
    }
//
}
