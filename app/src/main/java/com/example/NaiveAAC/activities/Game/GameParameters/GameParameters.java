package com.example.NaiveAAC.activities.Game.GameParameters;

import static com.example.NaiveAAC.activities.Settings.Utils.AdvancedSettingsDataImportExportHelper.dataProcess;
import static com.example.NaiveAAC.activities.Settings.Utils.AdvancedSettingsDataImportExportHelper.savBak;

import android.content.Context;
import android.os.Environment;

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
 * <h1>GameParameters</h1>
 * <p><b>GameParameters</b>  contains games parameters.</p>
 */
public class GameParameters extends RealmObject {
    /**
     * represent the description displayed in the view for game choice.
     */
    private String gameName;
    /**
     * represent the type of icon displayed in the view for game choice.
     * <p>
     * gameIconType = S or space then , if it exists, gameIconPath refers to storage
     * <p>
     * gameIconType = A then , if it exists, gameIconPath refers to a uri of arasaac
     * <p>
     * gameIconType = AS then , if it exists, gameIconPath refers to assets
     */
    private String gameIconType;
    /**
     * represent the path of icon displayed in the view for game choice.
     */
    private String gameIconPath;
    /**
     * represent the info displayed in the view for game choice.
     */
    private String gameInfo;
    /**
     * represent the java class of the game.
     * <p>
     * gameJavaClass = PENSIERI E PAROLE
     * the app displays collections of word images that you can select to form simple sentences
     * <p>
     * gameJavaClass = PAROLE IN LIBERTA'
     * the app displays images (uploaded by the user or Arasaac pictograms) of the words spoken after pressing the listen button
     * <p>
     * gameJavaClass = ELENCHI (under construction)
     * the app displays the images in a list:
     * if the word spoken after pressing the listen button matches the image, you will receive a reward
     * <p>
     * gameJavaClass = PLURALE (under construction)
     * the app displays the images (in the singular and in the plural) of a list:
     * if the word pronounced after pressing the listen key corresponds to the plural, you will receive a reward
     * <p>
     * gameJavaClass = A/DA (under construction)
     * the app tells and displays the images of a story:
     * at certain points questions are asked by proposing images of the possible answers:
     * if the word pronounced after pressing the listen button is correct, you will receive a reward
     * <p>
     * gameJavaClass = E (under construction)
     * the app displays the images of a list of word pairs, one of which is hidden after a few seconds:
     * if the word spoken after pressing the listen button matches the hidden image, you will receive a reward
     */
    private String gameJavaClass;
    /**
     * represent the parameter of the game.
     */
    private String gameParameter;

    /*
     * getter, setter and other methods
     */
    /**
     * get <code>gameName</code>.
     *
     * @return gameName string data to get
     */
    public String getGameName() {
        return gameName;
    }
    /**
     * get <code>gameIconType</code>.
     *
     * @return gameIconType string data to get
     */
    public String getGameIconType() {
        return gameIconType;
    }
    /**
     * get <code>gameIconPath</code>.
     *
     * @return gameIconPath string data to get
     */
    public String getGameIconPath() {
        return gameIconPath;
    }
    /**
     * get <code>gameInfo</code>.
     *
     * @return gameInfo string data to get
     */
    public String getGameInfo() {
        return gameInfo;
    }
    /**
     * get <code>gameJavaClass</code>.
     *
     * @return gameJavaClass string data to get
     */
    public String getGameJavaClass() {
        return gameJavaClass;
    }
    /**
     * get <code>gameParameter</code>.
     *
     * @return gameParameter string data to get
     */
    public String getGameParameter() {
        return gameParameter;
    }
    //
    /**
     * set <code>gameName</code>.
     *
     * @param gameName string data to set
     */
    public void setGameName(String gameName) {
        this.gameName = gameName;
    }
    /**
     * set <code>gameIconType</code>.
     *
     * @param gameIconType string data to set
     */
    public void setGameIconType(String gameIconType)
    {
        this.gameIconType = gameIconType;
    }
    /**
     * set <code>gameIconPath</code>.
     *
     * @param gameIconPath string data to set
     */
    public void setGameIconPath(String gameIconPath)
    {
        this.gameIconPath = gameIconPath;
    }
    /**
     * set <code>gameInfo</code>.
     *
     * @param gameInfo string data to set
     */
    public void setGameInfo(String gameInfo) {
        this.gameInfo = gameInfo;
    }
    /**
     * set <code>gameJavaClass</code>.
     *
     * @param gameJavaClass string data to set
     */
    public void setGameJavaClass(String gameJavaClass)
    {
        this.gameJavaClass = gameJavaClass;
    }
    /**
     * set <code>gameParameter</code>.
     *
     * @param gameParameter string data to set
     */
    public void setGameParameter(String gameParameter)
    {
        this.gameParameter = gameParameter;
    }
//
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
    String FILE_NAME = "gameparameters.csv";
    // Grab all data from the DB in question :

    RealmResults<GameParameters> resultsDB = realm.where(GameParameters.class).findAll();

    // Here we need to put in header fields
    String dataP = null;
    String header = AdvancedSettingsDataImportExportHelper.grabHeader(realm, "GameParameters");

    // We write the header to file
    savBak(context, header,FILE_NAME );

    // Now we write all the data corresponding to the fields grabbed above:

    int count = resultsDB.size();
    if (count != 0) {
        int irrh=0;
        while((irrh < count  )) {
            GameParameters taskitems = resultsDB.get(irrh);
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
     * @see AdvancedSettingsDataImportExportHelper#findExternalStorageRoot
     * @see AdvancedSettingsDataImportExportHelper#openFileInput(Context, String)
     */
    public static void importFromCsv(Context context, Realm realm)
    {
        // clear the table
        RealmResults<GameParameters> daCancellare = realm.where(GameParameters.class).findAll();
        realm.beginTransaction();
        daCancellare.deleteAllFromRealm();
        realm.commitTransaction();
        //
        String rootPath = context.getFilesDir().getAbsolutePath();
        //
        String FILE_NAME = "gameparameters.csv";
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
                    File f = null;
                    String uri = null;
                    if (oneWord[2] != null ) {
                        if (oneWord[1].equals(context.getString(R.string.character_s))) {
                            // replace with root of the external storage or data directory
                            uri = oneWord[2].replaceFirst(context.getString(R.string.rootdirectory), rootPath);
                            //
                            f = new File(oneWord[2]);
                        }
                        else
                        {
                            uri = oneWord[2];
                        }
                    }
                    if (oneWord[0] != null
                            && oneWord[1] != null
                            && oneWord[2] != null
                            && oneWord[3] != null
                            && oneWord[4] != null
                            && oneWord[5] != null ) {

                        if ((oneWord[0].length() > 0
                                && oneWord[1].length() > 0
                                && oneWord[2].length() > 0
                                && oneWord[4].length() > 0 )
                                &&
                                ((!oneWord[1].equals(context.getString(R.string.character_s)))
                                        || (oneWord[1].equals(context.getString(R.string.character_s))  && f.exists())))  {
                            //
                            realm.beginTransaction();
                            GameParameters gameParameters = realm.createObject(GameParameters.class);
                            // set the fields here
                            gameParameters.setGameName(oneWord[0]);
                            gameParameters.setGameIconType(oneWord[1]);

                            gameParameters.setGameIconPath(uri);
                            gameParameters.setGameInfo(oneWord[3]);
                            gameParameters.setGameJavaClass(oneWord[4]);
                            gameParameters.setGameParameter(oneWord[5]);
                            realm.commitTransaction();
                        }
                    }
                }
            }
        }
    }
    //
}