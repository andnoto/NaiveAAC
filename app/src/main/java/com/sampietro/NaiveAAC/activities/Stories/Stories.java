package com.sampietro.NaiveAAC.activities.Stories;

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
 * <h1>Stories</h1>
 * <p><b>Stories</b>  contains stories identified by a keyword.</p>
 */
public class Stories extends RealmObject {
    /**
     * key that identifies the story.
     */
    private String story;
    /**
     * order number of the sentence in a given story.
     */
    private int phraseNumberInt;
    /**
     * order number of the word in a given sentence or the entire sentence or a question / answer related to the phrase or
     * the topic of the sentence.
     * <p>
     * if wordNumber = 0 then word contains the entire sentence
     * <p>
     * if wordNumber > 0 and < 99 then wordNumber contains the pass number of the word and word contains the word itself
     * <p>
     * if wordNumber = 99 then word contain a question related to the phrase
     * <p>
     * if wordNumber = 999 the word contain the answer related to the phrase
     * <p>
     * if wordNumber = 9999 the word contain the topic of the sentence
     */
    private int wordNumberInt;
    /**
     * order number of the word in a given story
     * <p>
     */
    private int wordNumberIntInTheStory;
    /**
     * sentence, word, question , answer or topic belonging to the story identified with the <code>story</code>.
     */
    private String word;
    /**
     * represent the type of media associated with the <code>word</code>.
     * <p>
     * uritype = S then , if it exists, uri refers to an image from storage
     * <p>
     * uritype = A then , if it exists, uri refers to an image from a url of arasaac
     * <p>
     * if neither the arasaac url nor the file path are indicated, the image is associated using PictogramsAll
     */
    private String uriType;
    /**
     * contains the file path or a url of arasaac  (originally contained the uri).
     */
    private String uri;
    /**
     * contains the type of action associated with the <code>word</code> in case the word is an answer.
     * <p>
     * answerActionType = V then the action consists of playing a video
     * <p>
     * answerActionType = G then the action consists consists of executing a goto (phraseNumber) statement
     * corresponding to the topic of a sentence
     */
    private String answerActionType;
    /**
     * contains the action associated with the <code>word</code> in case the word is an answer:
     * <p>
     * contains the video key in the videos table or the topic of a sentence
     */
    private String answerAction;
    /**
     * refers to a video key in the videos table .
     */
    private String video;
    /**
     * refers to a sound key in the sounds table .
     */
    private String sound;
    /**
     * indicates whether sound replaces TTS (Y or N).
     */
    private String soundReplacesTTS;
    /**
     * contains Y if the object was imported from assets.
     */
    private String fromAssets;
    /*
     * getter, setter and other methods
     */
    /**
     * get <code>story</code>.
     *
     * @return story string data to get
     */
    public String getStory() { return story; }
    /**
     * get <code>phraseNumberInt</code>.
     *
     * @return phraseNumberInt int data to get
     */
    public int getPhraseNumber() { return phraseNumberInt; }
    /**
     * get <code>wordNumberInt</code>.
     *
     * @return wordNumberInt int data to get
     */
    public int getWordNumber() { return wordNumberInt; }
    /**
     * get <code>wordNumberIntInTheStory</code>.
     *
     * @return wordNumberIntInTheStory int data to get
     */
    public int getWordNumberIntInTheStory() { return wordNumberIntInTheStory; }
    /**
     * get <code>word</code>.
     *
     * @return word string data to get
     */
    public String getWord() { return word; }
    /**
     * get <code>uriType</code>.
     *
     * @return uriType string data to get
     */
    public String getUriType() { return uriType; }
    /**
     * get <code>uri</code>.
     *
     * @return uri string data to get
     */
    public String getUri() { return uri; }
    //
    /**
     * get <code>answerActionType</code>.
     *
     * @return answerActionType string data to get
     */
    public String getanswerActionType() { return answerActionType; }
    //
    /**
     * get <code>answerAction</code>.
     *
     * @return answerAction string data to get
     */
    public String getAnswerAction() { return answerAction; }
    //
    /**
     * get <code>video</code>.
     *
     * @return video string data to get
     */
    public String getVideo() { return video; }
    //
    /**
     * get <code>sound</code>.
     *
     * @return sound string data to get
     */
    public String getSound() { return sound; }
    //
    /**
     * get <code>soundReplacesTTS</code>.
     *
     * @return soundReplacesTTS string data to get
     */
    public String getSoundReplacesTTS() { return soundReplacesTTS; }
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
     * set <code>story</code>.
     *
     * @param story string data to set
     */
    public void setStory(String story) { this.story = story; }
    /**
     * set <code>phraseNumber</code>.
     *
     * @param phraseNumber int data to set
     */
    public void setPhraseNumber(int phraseNumber) {
        this.phraseNumberInt = phraseNumber;
    }
    /**
     * set <code>wordNumber</code>.
     *
     * @param wordNumberInt int data to set
     */
    public void setWordNumber(int wordNumberInt) {
        this.wordNumberInt = wordNumberInt;
    }
    /**
     * set <code>wordNumberIntInTheStory</code>.
     *
     * @param wordNumberIntInTheStory int data to set
     */
    public void setWordNumberIntInTheStory(int wordNumberIntInTheStory) {
        this.wordNumberIntInTheStory = wordNumberIntInTheStory;
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
     * set <code>answerActionType</code>.
     *
     * @param answerActionType string data to set
     */
    public void setAnswerActionType(String answerActionType) {
        this.answerActionType = answerActionType;
    }
    /**
     * set <code>answerAction</code>.
     *
     * @param answerAction string data to set
     */
    public void setAnswerAction(String answerAction) {
        this.answerAction = answerAction;
    }
    /**
     * set <code>video</code>.
     *
     * @param video string data to set
     */
    public void setVideo(String video) {
        this.video = video;
    }
    /**
     * set <code>sound</code>.
     *
     * @param sound string data to set
     */
    public void setSound(String sound) {
        this.sound = sound;
    }
    /**
     * set <code>setSoundReplacesTTS</code>.
     *
     * @param soundReplacesTTS string data to set
     */
    public void setSoundReplacesTTS(String soundReplacesTTS) {
        this.soundReplacesTTS = soundReplacesTTS;
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
        String FILE_NAME = "stories.csv";
        // Grab all data from the DB in question :

        RealmResults<Stories> resultsDB = realm.where(Stories.class).findAll();

        // Here we need to put in header fields
        String dataP = null;
        String header = AdvancedSettingsDataImportExportHelper.grabHeader(realm, "Stories");

        // We write the header to file
        savBak(context, header,FILE_NAME );

        // Now we write all the data corresponding to the fields grabbed above:
        //
        int count = resultsDB.size();
        if (count != 0) {
            int irrh=0;
            while((irrh < count  )) {
                Stories taskitems = resultsDB.get(irrh);
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
     * Export a story to CSV .
     * Refer to <a href="https://stackoverflow.com/questions/49468809/is-there-a-way-to-export-realm-database-to-csv-json">stackoverflow</a>
     * answer of <a href="https://stackoverflow.com/users/8989439/vrasheed">vrasheed</a>
     *
     * @param context context
     * @param realm realm obtained from the activity by Realm#getDefaultInstance
     * @param resultsDB RealmResults<Stories> story to export
     * @param dir File with output directory
     * @see AdvancedSettingsDataImportExportHelper#grabHeader(Realm, String)
     * @see AdvancedSettingsDataImportExportHelper#savBak(Context, String, String)
     * @see AdvancedSettingsDataImportExportHelper#dataProcess(String)
     */
    public static void exporttoCsv(Context context, Realm realm, RealmResults<Stories> resultsDB, File dir){
//
        String FILE_NAME = "stories.csv";
        // Grab all data from the DB in question :
        //        RealmResults<Stories> resultsDB = realm.where(Stories.class).findAll();

        // Here we need to put in header fields
        String dataP = null;
        String header = AdvancedSettingsDataImportExportHelper.grabHeader(realm, "Stories");

        // We write the header to file
        savBak(context, header,FILE_NAME, dir);

        // Now we write all the data corresponding to the fields grabbed above:
        //
        int count = resultsDB.size();
        if (count != 0) {
            int irrh=0;
            while((irrh < count  )) {
                Stories taskitems = resultsDB.get(irrh);
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
                    savBak(context, dataP, FILE_NAME, dir,true);
                }
                else {
                    savBak(context, dataP, FILE_NAME, dir, false);
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
     * @param mode string import mode (Append or Replace)
     */
    public static void importFromCsvFromInternalStorage(Context context, Realm realm, String mode)
    {
        if (Objects.equals(mode, "Replace"))
            {
                // clear the table
                RealmResults<Stories> daCancellare = realm.where(Stories.class).findAll();
                realm.beginTransaction();
                daCancellare.deleteAllFromRealm();
                realm.commitTransaction();
            }
        //
        String rootPath = context.getFilesDir().getAbsolutePath();
        //
        String FILE_NAME = "stories.csv";
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
                    // also including empty strings
                    final String[] oneWord= line.split(cvsSplitBy, -1);
                    // checks
                    File f = null;
                    String uri = null;
                    if (oneWord[6] != null ) {
                        if (oneWord[5].equals(context.getString(R.string.character_s))) {
                            // replace with root of the external storage or data directory
                            uri = oneWord[6].replaceFirst(context.getString(R.string.rootdirectory), rootPath);
                            //
                            f = new File(oneWord[6]);
                        }
                        else
                        {
                            uri = oneWord[6];
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
                            && oneWord[8] != null
                            && oneWord[9] != null
                            && oneWord[10] != null
                            && oneWord[11] != null
                            && oneWord[12] != null
                          )
                    {
                        if (oneWord[0].length() > 0
                                && oneWord[1].length() > 0
                                && oneWord[2].length() > 0
                                && oneWord[3].length() > 0
                                && oneWord[4].length() > 0
                                &&
                                ((!oneWord[5].equals(context.getString(R.string.character_s)))
                                || (oneWord[5].equals(context.getString(R.string.character_s))  && f.exists())))  {
                            //
                            realm.beginTransaction();
                            Stories history = realm.createObject(Stories.class);
                            // set the fields here
                            history.setStory(oneWord[0]);
                            history.setPhraseNumber(Integer.parseInt(oneWord[1]));
                            history.setWordNumber(Integer.parseInt(oneWord[2]));
                            history.setWordNumberIntInTheStory(Integer.parseInt(oneWord[3]));
                            history.setWord(oneWord[4]);
                            history.setUriType(oneWord[5]);
                            //
                            history.setUri(uri);
                            //
                            history.setAnswerActionType(oneWord[7]);
                            history.setAnswerAction(oneWord[8]);
                            //
                            history.setVideo(oneWord[9]);
                            history.setSound(oneWord[10]);
                            history.setSoundReplacesTTS(oneWord[11]);
                            history.setFromAssets(oneWord[12]);
                            realm.commitTransaction();
                        }
                    }
                }
            }
        }
    }
    //
    /**
     * Import from CSV of a story.
     * Refer to <a href="https://stackoverflow.com/questions/49468809/is-there-a-way-to-export-realm-database-to-csv-json">stackoverflow</a>
     * answer of <a href="https://stackoverflow.com/users/8989439/vrasheed">vrasheed</a>
     *
     * @param context context
     * @param realm realm obtained from the activity by Realm#getDefaultInstance
     * @param story string story to import
     */
    public static void importStoryFromCsvFromInternalStorage(Context context, Realm realm, String story)
    {
        // clear the table
        RealmResults<Stories> daCancellare =
                realm.where(Stories.class).equalTo("story", story).findAll();
        realm.beginTransaction();
        daCancellare.deleteAllFromRealm();
        realm.commitTransaction();
        //
        String rootPath = context.getFilesDir().getAbsolutePath();
        //
        String FILE_NAME = "stories.csv";
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
                    // also including empty strings
                    final String[] oneWord= line.split(cvsSplitBy, -1);
                    // checks
                    File f = null;
                    String uri = null;
                    if (oneWord[6] != null ) {
                        if (oneWord[5].equals(context.getString(R.string.character_s))) {
//                          uri = oneWord[5].replaceFirst(context.getString(R.string.rootdirectory), rootPath);
                            // replace with root of data directory
                            String fileName = oneWord[6].substring(oneWord[6].lastIndexOf("/")+1);
                            uri = rootPath + "/" + fileName;
                            //
                            f = new File(uri);
                        }
                        else
                        {
                            uri = oneWord[6];
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
                            && oneWord[8] != null
                            && oneWord[9] != null
                            && oneWord[10] != null
                            && oneWord[11] != null
                            && oneWord[12] != null
                    )
                    {
                        if (oneWord[0].length() > 0
                                && oneWord[1].length() > 0
                                && oneWord[2].length() > 0
                                && oneWord[3].length() > 0
                                && oneWord[4].length() > 0
                                &&
                                ((!oneWord[5].equals(context.getString(R.string.character_s)))
                                        || (oneWord[5].equals(context.getString(R.string.character_s))  && f.exists())))  {
                            //
                            realm.beginTransaction();
                            Stories history = realm.createObject(Stories.class);
                            // set the fields here
                            history.setStory(oneWord[0]);
                            history.setPhraseNumber(Integer.parseInt(oneWord[1]));
                            history.setWordNumber(Integer.parseInt(oneWord[2]));
                            history.setWordNumberIntInTheStory(Integer.parseInt(oneWord[3]));
                            history.setWord(oneWord[4]);
                            history.setUriType(oneWord[5]);
                            //
                            history.setUri(uri);
                            //
                            history.setAnswerActionType(oneWord[7]);
                            history.setAnswerAction(oneWord[8]);
                            //
                            history.setVideo(oneWord[9]);
                            history.setSound(oneWord[10]);
                            history.setSoundReplacesTTS(oneWord[11]);
                            history.setFromAssets(oneWord[12]);
                            realm.commitTransaction();
                        }
                    }
                }
            }
        }
    }
}

