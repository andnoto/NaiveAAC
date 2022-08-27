package com.example.NaiveAAC.activities.WordPairs;

import static com.example.NaiveAAC.activities.Settings.Utils.AdvancedSettingsDataImportExportHelper.dataProcess;
import static com.example.NaiveAAC.activities.Settings.Utils.AdvancedSettingsDataImportExportHelper.savBak;

import android.content.Context;

import com.example.NaiveAAC.R;
import com.example.NaiveAAC.activities.Graphics.ImageSearchHelper;
import com.example.NaiveAAC.activities.Graphics.ResponseImageSearch;
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
 * <h1>WordPairs</h1>
 * <p><b>WordPairs</b>  contains word pairs.</p>
 * in game1 they are not considered wordpairs with pairs of names or pairs of verbs
 * (only noun-verb pairs are considered or vice versa verb-noun)
 *
 * @version     1.1, 04/22/22
 */
public class WordPairs extends RealmObject {
    /**
     * word</p>
     * in game1 , if it is not a verb, it represents the subject of the sentence.
     * (only noun-verb pairs are considered or vice versa verb-noun)
     */
    private String word1;
    /**
     * word</p>
     * in game1 , if it is not a verb, it represents the object complement of the sentence.
     * (only noun-verb pairs are considered or vice versa verb-noun)
     */
    private String word2;
    /**
     * if <code>word1</code> is a verb, the articles and prepositions for the object complement and
     * the complements of motion to place and motion from place are automatically generated;
     * if necessary, for other types of complement, indicate the complement itself (example
     * andare - nonno insert "con il"
     */
    private String complement;
    /**
     * game1 is designed as a search engine where categories are represented by image menus.</p>
     * isMenuItem = TLM : <code>word1</code> is a top-level menu item of Game1
     * (in this case word1 must be equal to word2).</p>
     * isMenuItem = SLM : <code>word2</code> is a second level menu item in Game1
     * where <code>word1</code> is its top level menu.
     */
    private String isMenuItem;
    /**
     * game1 displays collections of word images that you can select to form simple sentences.
     * when the sentence is completed the app listens and
     * if spoken matches the sentence the player will receive a prize</p>
     * awardType = V : premium video of the Game1</p>
     * awardType = Y : premium Youtube video of the Game1.</p>
     * if awardtype is empty the award is a simple balloon popping game
     */
    private String awardType;
    /**
     * contains the video key in the videos table
     * or the Youtube url of the premium video (originally contained the uri).
     * </p>
     * if awardType = V and uriPremiumVideo is empty,
     * the video prize is the one with the prize key in the videos table.
     */
    private String uriPremiumVideo;
    /*
     * getter, setter and other methods
     */
    /**
     * get <code>word1</code>.
     *
     * @return word1 string data to get
     */
    public String getWord1() {
        return word1;
    }
    /**
     * get <code>word2</code>.
     *
     * @return word2 string data to get
     */
    public String getWord2() {
        return word2;
    }
    /**
     * get <code>complement</code>.
     *
     * @return complement string data to get
     */
    public String getComplement() {
        return complement;
    }
    /**
     * get <code>isMenuItem</code>.
     *
     * @return isMenuItem string data to get
     */
    public String getIsMenuItem() {
        return isMenuItem;
    }
    /**
     * get <code>awardType</code>.
     *
     * @return awardType string data to get
     */
    public String getAwardType() {
        return awardType;
    }
    /**
     * get <code>uriPremiumVideo</code>.
     *
     * @return uriPremiumVideo string data to get
     */
    public String getUriPremiumVideo() {
        return uriPremiumVideo;
    }
    //
    /**
     * set <code>word1</code>.
     *
     * @param word1 string data to set
     */
    public void setWord1(String word1) {
        this.word1 = word1;
    }
    /**
     * set <code>word2</code>.
     *
     * @param word2 string data to set
     */
    public void setWord2(String word2) {
        this.word2 = word2;
    }
    /**
     * set <code>complement</code>.
     *
     * @param complement string data to set
     */
    public void setComplement(String complement) { this.complement = complement; }
    /**
     * set <code>isMenuItem</code>.
     *
     * @param isMenuItem string data to set
     */
    public void setIsMenuItem(String isMenuItem) { this.isMenuItem = isMenuItem; }
    /**
     * set <code>awardType</code>.
     *
     * @param awardType string data to set
     */
    public void setAwardType(String awardType) { this.awardType = awardType; }
    /**
     * set <code>uriPremiumVideo</code>.
     *
     * @param uriPremiumVideo string data to set
     */
    public void setUriPremiumVideo(String uriPremiumVideo) { this.uriPremiumVideo = uriPremiumVideo; }
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
        String FILE_NAME = "wordpairs.csv";
        // Grab all data from the DB in question (WordPairs):

        RealmResults<WordPairs> resultsDB = realm.where(WordPairs.class).findAll();

        // Here we need to put in header fields
        String dataP = null;
        String header = AdvancedSettingsDataImportExportHelper.grabHeader(realm, "WordPairs");

        // We write the header to file
        savBak(context, header,FILE_NAME );

        // Now we write all the data corresponding to the fields grabbed above:
        //
        int count = resultsDB.size();
        if (count != 0) {
            int irrh=0;
            while((irrh < count  )) {
                WordPairs taskitems = resultsDB.get(irrh);
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
        RealmResults<WordPairs> daCancellare = realm.where(WordPairs.class).findAll();
        realm.beginTransaction();
        daCancellare.deleteAllFromRealm();
        realm.commitTransaction();
        //
        String rootPath = context.getFilesDir().getAbsolutePath();
        //
        String FILE_NAME = "wordpairs.csv";
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
                    // register excluding lines with words that do not correspond to images
                    if (oneWord[0] != null && oneWord[1] != null) {
                        ResponseImageSearch image1 = null;
                        ResponseImageSearch image2 = null;
                        image1 = ImageSearchHelper.imageSearch(realm, oneWord[0]);
                        image2 = ImageSearchHelper.imageSearch(realm, oneWord[1]);
                        if (image1 !=null && image2 !=null
                                && oneWord[2] != null
                                && oneWord[3] != null
                                && oneWord[4] != null
                                && oneWord[5] != null ) {
                            //
                            realm.beginTransaction();
                            WordPairs wordPairs = realm.createObject(WordPairs.class);
                            // set the fields here
                            wordPairs.setWord1(oneWord[0]);
                            wordPairs.setWord2(oneWord[1]);
                            wordPairs.setComplement(oneWord[2]);
                            wordPairs.setIsMenuItem(oneWord[3]);
                            wordPairs.setAwardType(oneWord[4]);
                            // replace with root of the external storage or data directory
                            String uri = oneWord[5].replaceFirst("rootdirectory", rootPath);
                            //
                            wordPairs.setUriPremiumVideo(uri);
                            realm.commitTransaction();
                        }
                    }
                }
            }
        }
    }
    //
}
