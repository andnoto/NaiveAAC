package com.example.NaiveAAC.activities.Grammar;

import static com.example.NaiveAAC.activities.Settings.Utils.AdvancedSettingsDataImportExportHelper.dataProcess;
import static com.example.NaiveAAC.activities.Settings.Utils.AdvancedSettingsDataImportExportHelper.findExternalStorageRoot;
import static com.example.NaiveAAC.activities.Settings.Utils.AdvancedSettingsDataImportExportHelper.openFileInput;
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
 * <h1>GrammaticalExceptions</h1>
 * <p><b>GrammaticalExceptions</b>  contains grammatical exceptions.</p>
 * in normal cases given the keyword Arasaac the masculine or feminine gender comes identified approximately as follows:
 * if the plural ends in "i" -> masculine gender;
 * if the singular ends in "a" and the plural in "e" -> feminine gender.</p>
 * in normal cases given the keyword Arasaac, the gender, the singular / plural category and the type of the movement verb, if any
 * after checking for any exceptions the article or the complement of motion come identified approximately as follows:
 * if singular and the keyword starts with a vowel -> l'
 * if masculine singular, the keyword starts with consonant s followed by consonant or starts with consonant z -> lo
 * if masculine singular and the keyword begins with a consonant in the other cases -> il
 * if feminine singular and the keyword starts with a consonant -> la
 * if masculine plural and the keyword starts with a vowel -> gli
 * if masculine plural and the keyword starts with a consonant -> i
 * if plural feminine -> le
  */
public class GrammaticalExceptions extends RealmObject {
    /**
     * word or phrase from the Arasaac /pictograms/all API.
     */
    private String keyword;
    /**
     * represent the type of exception.
     * <p>
     * exceptionType    = Is a verb of movement to place
     * <p>
     * exceptionType    = Is a verb of movement from place
     * <p>
     * exceptionType    = Article
     */
    private String exceptionType;
    /**
     * if exceptionType    = Article then exception1 = article
     */
    private String exception1;
    /**
     * if exceptionType    = Article then exception2 = complement of motion from place
     */
    private String exception2;
    /**
     * if exceptionType    = Article then exception2 = complement of motion to place
     */
    private String exception3;

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
     * get <code>exceptionType</code>.
     *
     * @return exceptionType string data to get
     */
    public String getExceptionType() {
        return exceptionType;
    }
    /**
     * get <code>exception1</code>.
     *
     * @return exception1 string data to get
     */
    public String getException1() {
        return exception1;
    }
    /**
     * get <code>exception2</code>.
     *
     * @return exception2 string data to get
     */
    public String getException2() {
        return exception2;
    }
    /**
     * get <code>exception3</code>.
     *
     * @return exception3 string data to get
     */
    public String getException3() {
        return exception3;
    }
    //
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
     * set <code>exceptionType</code>.
     *
     * @param exceptionType string data to set
     */
    public void setExceptionType(String exceptionType)  { this.exceptionType = exceptionType; }
    /**
     * set <code>exception1</code>.
     *
     * @param exception1 string data to set
     */
    public void setException1(String exception1)
    {
        this.exception1 = exception1;
    }
    /**
     * set <code>exception2</code>.
     *
     * @param exception2 string data to set
     */
    public void setException2(String exception2)
    {
        this.exception2 = exception2;
    }
    /**
     * set <code>exception3</code>.
     *
     * @param exception3 string data to set
     */
    public void setException3(String exception3)
    {
        this.exception3 = exception3;
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
        String FILE_NAME = "grammaticalexceptions.csv";
        // Grab all data from the DB in question (ListsOfNames):

        RealmResults<GrammaticalExceptions> resultsDB = realm.where(GrammaticalExceptions.class).findAll();

        // Here we need to put in header fields
        String dataP = null;
        String header = AdvancedSettingsDataImportExportHelper.grabHeader(realm, "GrammaticalExceptions");

        // We write the header to file
        savBak(context, header,FILE_NAME );

        // Now we write all the data corresponding to the fields grabbed above:
        //
        int count = resultsDB.size();
        if (count != 0) {
            int irrh=0;
            while((irrh < count  )) {
                GrammaticalExceptions taskitems = resultsDB.get(irrh);
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
    //
    /**
     * Import from CSV.
     * Refer to <a href="https://stackoverflow.com/questions/49468809/is-there-a-way-to-export-realm-database-to-csv-json">stackoverflow</a>
     * answer of <a href="https://stackoverflow.com/users/8989439/vrasheed">vrasheed</a>
     *
     * @param context context
     * @param realm realm obtained from the activity by Realm#getDefaultInstance
     * @see AdvancedSettingsDataImportExportHelper#openFileInput(Context, String)
     */
    public static void importFromCsv(Context context, Realm realm)
    {
        // clear the table
        RealmResults<GrammaticalExceptions> daCancellare = realm.where(GrammaticalExceptions.class).findAll();
        realm.beginTransaction();
        daCancellare.deleteAllFromRealm();
        realm.commitTransaction();
        //
        File root = findExternalStorageRoot();
        String rootPath = root.getAbsolutePath();
        //
        String FILE_NAME = "grammaticalexceptions.csv";
        File file = null;

        file = openFileInput(context, FILE_NAME);

        //adding to db
        BufferedReader br = null;
        String line = "";
        String cvsSplitBy = context.getString(R.string.character_comma);
        //
        try {
            br = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8));
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
                    if (oneWord[0] != null && oneWord[0].length() > 0
                            && oneWord[1] != null && oneWord[1].length() > 0
                            && oneWord[2] != null
                            && oneWord[3] != null
                            && oneWord[4] != null ) {
                        realm.beginTransaction();
                        GrammaticalExceptions grammaticalExceptions = realm.createObject(GrammaticalExceptions.class);
                        // set the fields here
                        grammaticalExceptions.setKeyword(oneWord[0]);
                        grammaticalExceptions.setExceptionType(oneWord[1]);
                        grammaticalExceptions.setException1(oneWord[2]);
                        grammaticalExceptions.setException2(oneWord[3]);
                        grammaticalExceptions.setException3(oneWord[4]);
                        realm.commitTransaction();
                    }
                }
            }
        }
    }
    //
}
