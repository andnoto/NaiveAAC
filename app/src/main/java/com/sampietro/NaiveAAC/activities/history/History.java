package com.sampietro.NaiveAAC.activities.history;

import static com.sampietro.NaiveAAC.activities.Settings.Utils.AdvancedSettingsDataImportExportHelper.dataProcess;
import static com.sampietro.NaiveAAC.activities.Settings.Utils.AdvancedSettingsDataImportExportHelper.openFileInput;
import static com.sampietro.NaiveAAC.activities.Settings.Utils.AdvancedSettingsDataImportExportHelper.savBak;

import android.content.Context;

import com.sampietro.NaiveAAC.activities.Settings.Utils.AdvancedSettingsDataImportExportHelper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmResults;

/**
 * <h1>History</h1>
 * <p><b>History</b>  it is used for passing complex data between activities and fragments
 * it also contains the history of games for statistical use.</p>
 */
public class History extends RealmObject {
    /**
     * represents the progressive number of the app session that is incremented in main activity.
     */
    private String session;
    /**
     * represents the progressive number of the sentence.
     */
    private String phraseNumber;
    /**
     * represents the date of the sentence.
     */
    private String date;
    /**
     * if wordNumber = 0 then word contains the entire sentence
     * if wordNumber > 0 then wordNumber contains the pass number of the word and
     * word contains the word itself
     */
    private String wordNumber;
    /**
     * type = 1 word is a noun without plural
     * type = 2 word is a noun with plural
     * type = 3 word is a verb
     */
    private String type;
    /**
     * word or sentence
     */
    private String word;
    /**
     * plural = N then word is not a plural
     * plural = Y then word is a plural
     */
    private String plural;
    /**
     * uritype = A then , if it exists, image corresponding to the word refers to Arasaac
     * uritype = S then , if it exists, image corresponding to the word refers to storage
     */
    private String uriType;
    /**
     * contains the file path or the url of arasaac (originally contained the uri).
     */
    private String uri;
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
    /*
     * getter, setter and other methods
     */
    /**
     * get <code>session</code>.
     *
     * @return session integer data to get
     */
    public Integer getSession() { return Integer.parseInt(session); }
    /**
     * get <code>phraseNumber</code>.
     *
     * @return phraseNumber integer data to get
     */
    public Integer getPhraseNumber() {
        return Integer.parseInt(phraseNumber);
    }
    /**
     * get <code>date</code>.
     *
     * @return date date data to get
     */
    public Date getDate() {
        SimpleDateFormat simpleDate =  new SimpleDateFormat("dd/MM/yyyy");
        try {
            return simpleDate.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
    /**
     * get <code>wordNumber</code>.
     *
     * @return wordNumber integer data to get
     */
    public Integer getWordNumber() {
        return Integer.parseInt(wordNumber);
    }
    /**
     * get <code>type</code>.
     *
     * @return type string data to get
     */
    public String getType() { return type; }
    /**
     * get <code>word</code>.
     *
     * @return word string data to get
     */
    public String getWord() { return word; }
    /**
     * get <code>plural</code>.
     *
     * @return plural string data to get
     */
    public String getPlural() { return plural; }
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
     * set <code>session</code>.
     *
     * @param session integer data to set
     */
    public void setSession(Integer session) { this.session = String.valueOf(session); }
    /**
     * set <code>phraseNumber</code>.
     *
     * @param phraseNumber integer data to set
     */
    public void setPhraseNumber(Integer phraseNumber) {
        this.phraseNumber = String.valueOf(phraseNumber);
    }
    /**
     * set <code>date</code>.
     *
     * @param date date data to set
     */
    public void setDate(Date date) {
        SimpleDateFormat simpleDate =  new SimpleDateFormat("dd/MM/yyyy");
        this.date = simpleDate.format(date);
    }
    /**
     * set <code>wordNumber</code>.
     *
     * @param wordNumber integer data to set
     */
    public void setWordNumber(Integer wordNumber) {
        this.wordNumber = String.valueOf(wordNumber);
    }
    /**
     * set <code>type</code>.
     *
     * @param type string data to set
     */
    public void setType(String type) {
        this.type = type;
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
     * set <code>plural</code>.
     *
     * @param plural string data to set
     */
    public void setPlural(String plural) {
        this.plural = plural;
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
        String FILE_NAME = "history.csv";
        // Grab all data from the DB in question :

        RealmResults<History> resultsDB = realm.where(History.class).findAll();

        // Here we need to put in header fields
        String dataP = null;
        String header = AdvancedSettingsDataImportExportHelper.grabHeader(realm, "History");

        // We write the header to file
        savBak(context, header,FILE_NAME );

        // Now we write all the data corresponding to the fields grabbed above:
        //
        int count = resultsDB.size();
        if (count != 0) {
            int irrh=0;
            while((irrh < count  )) {
                History taskitems = resultsDB.get(irrh);
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
     * Import from CSV (not currently used).
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
        String FILE_NAME = "history.csv";
        File file = null;

        file = openFileInput(context, FILE_NAME);

        //adding to db
        BufferedReader br = null;
        String line = "";
        String cvsSplitBy = ",";
        //
        try {
            br = new BufferedReader(new FileReader(file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        while (true) {
            try {
                if (!((line = br.readLine()) != null)) break;
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
            // use comma as separator
            final String[] oneWord= line.split(cvsSplitBy, -1);
            //
            realm.beginTransaction();
            //
            History history = realm.createObject(History.class);
            // set the fields here
            history.setSession(Integer.parseInt(oneWord[0]));
            history.setPhraseNumber(Integer.parseInt(oneWord[1]));
            SimpleDateFormat simpleDate =  new SimpleDateFormat("dd/MM/yyyy");
            try {
                history.setDate(simpleDate.parse(oneWord[2]));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            history.setWordNumber(Integer.parseInt(oneWord[3]));
            history.setType(oneWord[4]);
            history.setWord(oneWord[5]);
            history.setPlural(oneWord[6]);
            history.setUriType(oneWord[7]);
            history.setUri(oneWord[8]);
            history.setVideo(oneWord[9]);
            history.setSound(oneWord[10]);
            history.setSoundReplacesTTS(oneWord[11]);
            //
            realm.commitTransaction();
        }
    }
}

