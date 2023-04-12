package com.sampietro.NaiveAAC.activities.Stories;

/**
 * <h1>VoiceToBeRecordedInStories</h1>
 *
 * <p><b>VoiceToBeRecordedInStories</b> utility class for registration in Stories class.</p>
 *
 * @see Stories
 * @version     3.0, 2023
 */
public class VoiceToBeRecordedInStories {
    /**
     * key that identifies the story.
     */
    public String story;
    /**
     * order number of the sentence in a given story.
     */
    public int phraseNumberInt;
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
    public int wordNumberInt;
    /**
     * sentence, word, question , answer or topic belonging to the story identified with the <code>story</code>.
     */
    public String word;
    /**
     * represent the type of media associated with the <code>word</code>.
     * <p>
     * uritype = S then , if it exists, uri refers to an image from storage
     * <p>
     * uritype = A then , if it exists, uri refers to an image from a url of arasaac
     * <p>
     * if neither the arasaac url nor the file path are indicated, the image is associated using PictogramsAll
     */
    public String uriType;
    /**
     * contains the file path or a url of arasaac  (originally contained the uri).
     */
    public String uri;
    /**
     * contains the type of action associated with the <code>word</code> in case the word is an answer.
     * <p>
     * answerActionType = V then the action consists of playing a video
     * <p>
     * answerActionType = G then the action consists consists of executing a goto (phraseNumber) statement
     * corresponding to the topic of a sentence
     */
    public String answerActionType;
    /**
     * contains the action associated with the <code>word</code> in case the word is an answer:
     * <p>
     * contains the video key in the videos table or the topic of a sentence
     */
    public String answerAction;
    /**
     * refers to a video key in the videos table .
     */
    public String video;
    /**
     * refers to a sound key in the sounds table .
     */
    public String sound;
    /**
     * indicates whether sound replaces TTS (Y or N).
     */
    public String soundReplacesTTS;
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
     * get <code>phraseNumber</code>.
     *
     * @return phraseNumber string data to get
     */
    public int getPhraseNumber() { return phraseNumberInt; }
    /**
     * get <code>wordNumber</code>.
     *
     * @return wordNumber string data to get
     */
    public int getWordNumber() { return wordNumberInt; }
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
     * @param phraseNumber string data to set
     */
    public void setPhraseNumber(int phraseNumber) {
        this.phraseNumberInt = phraseNumber;
    }
    /**
     * set <code>wordNumber</code>.
     *
     * @param wordNumber string data to set
     */
    public void setWordNumber(int wordNumber) {
        this.wordNumberInt = wordNumber;
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
}
