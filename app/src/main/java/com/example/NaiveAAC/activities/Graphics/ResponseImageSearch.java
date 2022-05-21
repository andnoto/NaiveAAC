package com.example.NaiveAAC.activities.Graphics;
/**
 * <h1>ResponseImageSearch</h1>
 *
 * <p><b>ResponseImageSearch</b> utility class for image search responses.</p>
 * Refer to <a href="https://stackoverflow.com/questions/8498997/android-return-multiple-values-from-a-method">stackoverflow.com</a>
 * answer of <a href="https://stackoverflow.com/users/479180/luxurymode">LuxuryMode</a>
 *
 * @see ImageSearchHelper
 * @version     1.3, 05/05/22
 */
public class ResponseImageSearch {
    /**
     * word or phrase.
     */
    private String word;
    /**
     * represent the type of image
     * <p>
     * uriType = S, if it exists, uriToSearch refers to storage file path
     * <p>
     * uriType = A then , if it exists, uriToSearch refers to a url of arasaac
     */
    private String uriType;
    /**
     * contains the file path or the url of arasaac (originally contained the uri).
     */
    private String uriToSearch;
    /**
     * ResponseImageSearch constructor.
     * set fields
     *
     * @param word string with word or phrase
     * @param uriType string with type of image
     * @param uriToSearch string with the file path or the url of arasaac
     */
    public ResponseImageSearch(String word, String uriType, String uriToSearch){
        this.word = word;
        this.uriType = uriType;
        this.uriToSearch = uriToSearch;
     }
    /*
     * getter, setter and other methods
     */
    /**
     * get <code>word</code>.
     *
     * @return word string word to get
     */
    public String getWord() {
        return word;
    }
    /**
     * get <code>uriType</code>.
     *
     * @return uriType string type of image to get
     */
    public String getUriType() {
        return uriType;
    }
    /**
     * get <code>uriToSearch</code>.
     *
     * @return uriToSearch string with the file path or the url of arasaac to get
     */
    public String getUriToSearch() {
        return uriToSearch;
    }
    //
    /**
     * set <code>word</code>.
     *
     * @param word string word to set
     */
    public void setWord(String word) {
        this.word = word;
    }
    /**
     * set <code>uriType</code>.
     *
     * @param uriType string type of image to set
     */
    public void setUriType(String uriType) {
        this.uriType = uriType;
    }
    /**
     * set <code>uriToSearch</code>.
     *
     * @param uriToSearch string with the file path or the url of arasaac to set
     */
    public void setUriToSearch(String uriToSearch) {
        this.uriToSearch = uriToSearch;
    }

}
