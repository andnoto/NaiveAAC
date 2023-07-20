package com.sampietro.NaiveAAC.activities.Game.GameADA;

/**
 * <h1>GameADAArrayList</h1>
 * <p><b>GameADAArrayList</b> for choice of words represents the arraylist object
 * to initialize the recyclerview
 * </p>
 * Refer to <a href="https://www.androidauthority.com/how-to-build-an-image-gallery-app-718976/">androidauthority</a>
 * by <a href="https://www.androidauthority.com/author/adamsinicki/">Adam Sinicki</a>
 */
public class GameADAArrayList {
    /**
     * represent the description displayed in the recycler view for game2.
     */
    private String image_title;
    /**
     * represent the type of image displayed in the recycler view for game2.
     * <p>
     * gameIconType = S or space then , if it exists, gameIconPath refers to storage
     * <p>
     * gameIconType = A then , if it exists, gameIconPath refers to a uri of arasaac
     */
    private String urlType;
    /**
     * represent the path of image displayed in the recycler view for game2.
     */
    private String url;
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
     * indicates whether the sound associated with the phrase replaces the other sounds (Y or N).
     */
    private String soundAssociatedWithThePhraseReplacesTheOtherSounds;
    /**
     * get <code>image_title</code>.
     *
     * @return image_title string data to get
     */
    public String getImage_title() {
        return image_title;
    }
    /**
     * set <code>image_title</code>.
     *
     * @param image_title string data to set
     */
    public void setImage_title(String image_title) {
        this.image_title = image_title;
    }
    /**
     * get <code>urlType</code>.
     *
     * @return urlType string data to get
     */
    public String getUrlType() {
        return urlType;
    }
    /**
     * set <code>urlType</code>.
     *
     * @param urlType string data to set
     */
    public void setUrlType(String urlType) {
        this.urlType = urlType;
    }
    /**
     * get <code>url</code>.
     *
     * @return url string data to get
     */
    public String getUrl() {
        return url;
    }
    /**
     * set <code>url</code>.
     *
     * @param url string data to set
     */
    public void setUrl(String url) {
        this.url = url;
    }
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
    public String getsoundReplacesTTS() { return soundReplacesTTS; }
    /**
     * get <code>soundAssociatedWithThePhraseReplacesTheOtherSounds</code>.
     *
     * @return soundAssociatedWithThePhraseReplacesTheOtherSounds string data to get
     */
    public String getSoundAssociatedWithThePhraseReplacesTheOtherSounds() { return soundAssociatedWithThePhraseReplacesTheOtherSounds; }
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
     * set <code>setSoundAssociatedWithThePhraseReplacesTheOtherSounds</code>.
     *
     * @param soundAssociatedWithThePhraseReplacesTheOtherSounds string data to set
     */
    public void setSoundAssociatedWithThePhraseReplacesTheOtherSounds(String soundAssociatedWithThePhraseReplacesTheOtherSounds) {
        this.soundAssociatedWithThePhraseReplacesTheOtherSounds = soundAssociatedWithThePhraseReplacesTheOtherSounds;
    }
}
