package com.sampietro.NaiveAAC.activities.Game.ChoiseOfGame;

/**
 * <h1>ChoiseOfGameArrayList</h1>
 * <p><b>ChoiseOfGameArrayList</b> for game choice represents the arraylist object
 * to initialize the recyclerview using data from the gameparameters class
 * </p>
 * Refer to <a href="https://www.androidauthority.com/how-to-build-an-image-gallery-app-718976/">androidauthority</a>
 * by <a href="https://www.androidauthority.com/author/adamsinicki/">Adam Sinicki</a>
 *
 * @see ChoiseOfGameFragment
 */
public class ChoiseOfGameArrayList {
    /**
     * represent the description displayed in the view for game choice.
     */
    private String image_title;
    /**
     * represent the type of icon displayed in the view for game choice.
     * <p>
     * gameIconType = S or space then , if it exists, gameIconPath refers to storage
     * <p>
     * gameIconType = A then , if it exists, gameIconPath refers to a uri of arasaac
     * <p>
     * gameIconType = AS then , if it exists, gameIconPath refers to assets
     */
    private String urlType;
    /**
     * represent the path of icon displayed in the view for game choice.
     */
    private String url;
    /**
     * if applicable, it contains the video file path (originally contained the uri).
     */
    private String urlVideo;
    /**
     * if applicable, it contains the video copyright information.
     */
    //
    private String videoCopyright;
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
    //
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
    //
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
    //
    /**
     * get <code>urlVideo</code>.
     *
     * @return urlVideo string data to get
     */
    public String getUrlVideo() {
        return urlVideo;
    }
    /**
     * set <code>urlVideo</code>.
     *
     * @param urlVideo string data to set
     */
    public void setUrlVideo(String urlVideo) {
        this.urlVideo = urlVideo;
    }
    //
    /**
     * get <code>videoCopyright</code>.
     *
     * @return videoCopyright string data to get
     */
    public String getVideoCopyright() {
        return videoCopyright;
    }
    /**
     * set <code>videoCopyright</code>.
     *
     * @param videoCopyright string data to set
     */
    public void setVideoCopyright(String videoCopyright) {
        this.videoCopyright = videoCopyright;
    }
}
