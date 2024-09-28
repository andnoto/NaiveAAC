package com.sampietro.NaiveAAC.activities.Grammar

/**
 * <h1>VoiceToBeRecordedInListOfNames</h1>
 *
 *
 * **VoiceToBeRecordedInListOfNames** utility class for registration in ListOfNames class.
 *
 * @see ListsOfNames
 *
 * @version     5.0, 01/04/2024
 */
class VoiceToBeRecordedInListsOfNames {
    /**
     * key or class that identifies the list.
     */
    var keyword: String? = null
    /**
     * name belonging to the list identified with the `keyword`.
     */
    var word: String? = null
    /**
     * indicates whether the table element is active or not.
     *
     *
     * elementActive = A then the table element is active
     */
    var elementActive: String? = null
    /**
     * game1 is designed as a search engine where categories are represented by image menus.
     * isMenuItem = Y : `word1` is a top-level menu item of Game1
     * (in this case word1 must be equal to word2).
     */
    var isMenuItem: String? = null
    /**
     * represent the type of image associated with the `word`.
     *
     *
     * uritype = S then , if it exists, uri refers to storage
     *
     *
     * uritype = A then , if it exists, uri refers to a url of arasaac
     *
     *
     * if neither the arasaac url nor the file path are indicated, the image is associated using PictogramsAll
     */
//    var uriType: String? = null
    /**
     * contains the file path or a url of arasaac  (originally contained the uri).
     */
//    var uri: String? = null
    /**
     * contains Y if the object was imported from assets.
     */
    var fromAssets: String? = null
}