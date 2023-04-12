package com.sampietro.NaiveAAC.activities.history;
/**
 * <h1>VoiceToBeRecordedInHistory</h1>
 *
 * <p><b>VoiceToBeRecordedInHistory</b> utility class for registration in history class.</p>
 *
 * @see History
 * @version     3.0, 03/12/23
 */
import java.util.Date;

public class VoiceToBeRecordedInHistory {
    /**
     * represents the progressive number of the app session that is incremented in main activity.
     */
    private Integer session;
    /**
     * represents the progressive number of the sentence.
     */
    private Integer phraseNumber;
    /**
     * represents the date of the sentence.
     */
    private Date date;
    /**
     * if wordNumber = 0 then word contains the entire sentence
     * if wordNumber > 0 then wordNumber contains the pass number of the word and
     * word contains the word itself
     */
    private Integer wordNumber;
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
     * uritype = C then , uri not registered because word is a complement of the name
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
    /**
     * VoiceToBeRecordedInHistory constructor.
     * set fields
     */
    public VoiceToBeRecordedInHistory(Integer session, Integer phraseNumber, Date date, Integer wordNumber,
                                      String type, String word, String plural, String uriType, String uri){
        this.session = session;
        this.phraseNumber = phraseNumber;
        this.date = date;
        this.wordNumber = wordNumber;
        this.type = type;
        this.word = word;
        this.plural = plural;
        this.uriType = uriType;
        this.uri = uri;
    }
    //
    public VoiceToBeRecordedInHistory(Integer session, Integer phraseNumber, Date date, Integer wordNumber,
                                      String type, String word, String plural, String uriType, String uri, String video, String sound, String soundReplacesTTS){
        this.session = session;
        this.phraseNumber = phraseNumber;
        this.date = date;
        this.wordNumber = wordNumber;
        this.type = type;
        this.word = word;
        this.plural = plural;
        this.uriType = uriType;
        this.uri = uri;
        this.video = video;
        this.sound = sound;
        this.soundReplacesTTS = soundReplacesTTS;
    }
    /*
     * getter, setter and other methods
     */
    public Integer getSession() {
        return session;
    }
    public Integer getPhraseNumber() {
        return phraseNumber;
    }
    public Date getDate() {
        return date;
    }
    public Integer getWordNumber() {
        return wordNumber;
    }
    public String getType() { return type; }
    public String getWord() { return word; }
    public String getPlural() { return plural; }
    public String getUriType() { return uriType; }
    public String getUri() { return uri; }
    public String getVideo() { return video; }
    public String getSound() { return sound; }
    public String getSoundReplacesTTS() { return soundReplacesTTS; }
    //
    public void setSession(Integer session) {
        this.session = session;
    }
    public void setPhraseNumber(Integer phraseNumber) {
        this.phraseNumber = phraseNumber;
    }
    public void setDate(Date date) {
        this.date = date;
    }
    public void setWordNumber(Integer wordNumber) {
        this.wordNumber = wordNumber;
    }
    public void setType(String type) {
        this.type = type;
    }
    public void setWord(String word) {
        this.word = word;
    }
    public void setPlural(String plural) {
        this.plural = plural;
    }
    public void setUriType(String uriType) {
        this.uriType = uriType;
    }
    public void setUri(String uri) {
        this.uri = uri;
    }
    public void setVideo(String video) {
        this.video = video;
    }
    public void setSound(String sound) {
        this.sound = sound;
    }
    public void setSoundReplacesTTS(String soundReplacesTTS) {
        this.soundReplacesTTS = soundReplacesTTS;
    }
}
