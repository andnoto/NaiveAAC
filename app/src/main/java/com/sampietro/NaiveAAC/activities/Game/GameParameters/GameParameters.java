package com.sampietro.NaiveAAC.activities.Game.GameParameters;

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
 * <h1>GameParameters</h1>
 * <p><b>GameParameters</b>  contains games parameters.</p>
 */
public class GameParameters extends RealmObject {
    /**
     * represent the description displayed in the view for game choice.
     */
    private String gameName;
    /**
     * indicates whether the game is active or not.
     * <p>
     * gameActive = A then the game is active
     */
    private String gameActive;
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
     * gameJavaClass = NAVIGATORE
     * the app displays collections of word images that you can select to form simple sentences
     * <p>
     * gameJavaClass = COMUNICATORE
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
     * gameJavaClass = A/DA
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
    /**
     * indicates whether the game uses video and sound.
     */
    private String gameUseVideoAndSound;
    /**
     * contains Y if the object was imported from assets.
     */
    private String fromAssets;
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
     * get <code>getGameActive</code>.
     *
     * @return gameActive string data to get
     */
    public String getGameActive() {
        return gameActive;
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
    /**
     * get <code>gameUseVideoAndSound</code>.
     *
     * @return gameUseVideoAndSound string data to get
     */
    public String getGameUseVideoAndSound() {
        return gameUseVideoAndSound;
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
     * set <code>gameName</code>.
     *
     * @param gameName string data to set
     */
    public void setGameName(String gameName) {
        this.gameName = gameName;
    }
    /**
     * set <code>gameActive</code>.
     *
     * @param gameActive string data to set
     */
    public void setGameActive(String gameActive) {
        this.gameActive = gameActive;
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
    /**
     * set <code>gameUseVideoAndSound</code>.
     *
     * @param gameUseVideoAndSound string data to set
     */
    public void setGameUseVideoAndSound(String gameUseVideoAndSound)
    {
        this.gameUseVideoAndSound = gameUseVideoAndSound;
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
     * Export the game parameters of a story to CSV.
     * Refer to <a href="https://stackoverflow.com/questions/49468809/is-there-a-way-to-export-realm-database-to-csv-json">stackoverflow</a>
     * answer of <a href="https://stackoverflow.com/users/8989439/vrasheed">vrasheed</a>
     *
     * @param context context
     * @param realm realm obtained from the activity by Realm#getDefaultInstance
     * @param resultsDB RealmResults<GameParameters>
     * @param dir file output directory
     * @see AdvancedSettingsDataImportExportHelper#grabHeader(Realm, String)
     * @see AdvancedSettingsDataImportExportHelper#savBak(Context, String, String)
     * @see AdvancedSettingsDataImportExportHelper#dataProcess(String)
     */
    public static void exporttoCsv(Context context, Realm realm, RealmResults<GameParameters> resultsDB, File dir){
//
        String FILE_NAME = "gameparameters.csv";
        // Grab all data from the DB in question :
        //        RealmResults<GameParameters> resultsDB = realm.where(GameParameters.class).findAll();

        // Here we need to put in header fields
        String dataP = null;
        String header = AdvancedSettingsDataImportExportHelper.grabHeader(realm, "GameParameters");

        // We write the header to file
        savBak(context, header,FILE_NAME, dir );

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
    public static void importFromCsvFromInternalStorage(Context context, Realm realm, String mode)
    {
        if (Objects.equals(mode, "Replace"))
            {
                // clear the table
                RealmResults<GameParameters> daCancellare = realm.where(GameParameters.class).findAll();
                realm.beginTransaction();
                daCancellare.deleteAllFromRealm();
                realm.commitTransaction();
            }
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
                            && oneWord[4] != null
                            && oneWord[5] != null
                            && oneWord[6] != null
                            && oneWord[7] != null
                            && oneWord[8] != null) {

                        if ((oneWord[0].length() > 0
                                && oneWord[1].length() > 0
                                && oneWord[2].length() > 0
                                && oneWord[3].length() > 0
                                && oneWord[5].length() > 0 )
                                &&
                                ((!oneWord[2].equals(context.getString(R.string.character_s)))
                                        || (oneWord[2].equals(context.getString(R.string.character_s))  && f.exists())))  {
                            //
                            realm.beginTransaction();
                            GameParameters gameParameters = realm.createObject(GameParameters.class);
                            // set the fields here
                            gameParameters.setGameName(oneWord[0]);
                            gameParameters.setGameActive(oneWord[1]);
                            gameParameters.setGameIconType(oneWord[2]);

                            gameParameters.setGameIconPath(uri);
                            gameParameters.setGameInfo(oneWord[4]);
                            gameParameters.setGameJavaClass(oneWord[5]);
                            gameParameters.setGameParameter(oneWord[6]);
                            gameParameters.setGameUseVideoAndSound(oneWord[7]);
                            gameParameters.setFromAssets(oneWord[8]);
                            realm.commitTransaction();
                        }
                    }
                }
            }
        }
    }
    //
    /**
     * Import from CSV the game parameters of a story.
     * Refer to <a href="https://stackoverflow.com/questions/49468809/is-there-a-way-to-export-realm-database-to-csv-json">stackoverflow</a>
     * answer of <a href="https://stackoverflow.com/users/8989439/vrasheed">vrasheed</a>
     *
     * @param context context
     * @param realm realm obtained from the activity by Realm#getDefaultInstance
     *
     */
    public static void importFromCsvFromInternalStorage(Context context, Realm realm)
    {
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
                    // clear the table
                    if (oneWord[0] != null ) {
                        RealmResults<GameParameters> daCancellare =
                                realm.where(GameParameters.class).equalTo("gameName", oneWord[0]).findAll();
                        realm.beginTransaction();
                        daCancellare.deleteAllFromRealm();
                        realm.commitTransaction();
                    }
                    // checks
                    File f = null;
                    String uri = null;
                    if (oneWord[3] != null ) {
                        if (oneWord[2].equals(context.getString(R.string.character_s))) {
                            // replace with root of data directory
//                          uri = oneWord[3].replaceFirst(context.getString(R.string.rootdirectory), rootPath);
                            String fileName = oneWord[3].substring(oneWord[3].lastIndexOf("/")+1);
                            uri = rootPath + "/" + fileName;
                            //
                            f = new File(uri);
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
                            && oneWord[4] != null
                            && oneWord[5] != null
                            && oneWord[6] != null
                            && oneWord[7] != null
                            && oneWord[8] != null ) {

                        if (oneWord[0].length() > 0
                                && oneWord[1].length() > 0
                                && oneWord[2].length() > 0
                                && oneWord[3].length() > 0
                                && oneWord[5].length() > 0 )
//                                &&
//                                ((!oneWord[2].equals(context.getString(R.string.character_s)))
//                                        || (oneWord[2].equals(context.getString(R.string.character_s))  && f.exists())))
                            {
                            //
                            realm.beginTransaction();
                            GameParameters gameParameters = realm.createObject(GameParameters.class);
                            // set the fields here
                            gameParameters.setGameName(oneWord[0]);
                            gameParameters.setGameActive(oneWord[1]);
                            gameParameters.setGameIconType(oneWord[2]);
                            if (oneWord[2].equals(context.getString(R.string.character_s))  && !f.exists()) {
                                File puntoInterrogativoFile = context.getFileStreamPath("punto interrogativo.png");
                                uri = puntoInterrogativoFile.getAbsolutePath();
                            }
                            gameParameters.setGameIconPath(uri);
                            gameParameters.setGameInfo(oneWord[4]);
                            gameParameters.setGameJavaClass(oneWord[5]);
                            gameParameters.setGameParameter(oneWord[6]);
                            gameParameters.setGameUseVideoAndSound(oneWord[7]);
                                gameParameters.setFromAssets(oneWord[8]);
                            realm.commitTransaction();
                        }
                    }
                }
            }
        }
    }
}