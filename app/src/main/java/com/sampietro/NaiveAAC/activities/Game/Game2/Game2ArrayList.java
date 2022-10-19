package com.sampietro.NaiveAAC.activities.Game.Game2;

/**
 * <h1>Game2ArrayList</h1>
 * <p><b>Game2ArrayList</b> for choice of words represents the arraylist object
 * to initialize the recyclerview
 * </p>
 * Refer to <a href="https://www.androidauthority.com/how-to-build-an-image-gallery-app-718976/">androidauthority</a>
 * by <a href="https://www.androidauthority.com/author/adamsinicki/">Adam Sinicki</a>
 */
public class Game2ArrayList {
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
}
