package com.sampietro.NaiveAAC.activities.Graphics

/**
 * <h1>ResponseImageSearch</h1>
 *
 *
 * **ResponseImageSearch** utility class for image search responses.
 * Refer to [stackoverflow.com](https://stackoverflow.com/questions/8498997/android-return-multiple-values-from-a-method)
 * answer of [LuxuryMode](https://stackoverflow.com/users/479180/luxurymode)
 *
 * @see ImageSearchHelper
 *
 * @version     4.0, 09/09/2023
 */
class ResponseImageSearch
/**
 * ResponseImageSearch constructor.
 * set fields
 *
 * @param word string with word or phrase
 * @param uriType string with type of image
 * @param uriToSearch string with the file path or the url of arasaac
 */(
    /**
     * word or phrase.
     */
    var word: String,
    /**
     * represent the type of image
     *
     *
     * uriType = S, if it exists, uriToSearch refers to storage file path
     *
     *
     * uriType = A then , if it exists, uriToSearch refers to a url of arasaac
     */
    var uriType: String,
    /**
     * contains the file path or the url of arasaac (originally contained the uri).
     */
    var uriToSearch: String
) {
}