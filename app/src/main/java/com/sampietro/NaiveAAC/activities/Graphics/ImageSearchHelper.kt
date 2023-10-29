package com.sampietro.NaiveAAC.activities.Graphics

import com.sampietro.NaiveAAC.activities.Grammar.GrammarHelper.searchVerb
import com.sampietro.NaiveAAC.activities.Arasaac.PictogramsAll
import io.realm.Realm

/**
 * <h1>ImageSearchHelper</h1>
 *
 *
 * **ImageSearchHelper** utility class for image search.
 *
 * @version     4.0, 09/09/2023
 */
object ImageSearchHelper {
    private const val REMOTE_ADDR_PICTOGRAM = "https://api.arasaac.org/api/pictograms/"

    /**
     * search for the image corresponding to a word in the following way :
     * 1) if it is conjugation search for infinity
     * 2) search the images of the words first in the internal menory
     * 3) if the image is not in the internal memory search the images on Arasaac
     *
     * @param realm realm
     * @param word string with corresponding word
     * @return ResponseImageSearch
     * @see com.sampietro.NaiveAAC.activities.Grammar.GrammarHelper.searchVerb
     *
     * @see searchUri
     * @see ResponseImageSearch
     */
    @JvmStatic
    fun imageSearch(realm: Realm, word: String?): ResponseImageSearch? {
        // IF IT IS CONJUGATION SEARCH FOR INFINITY, THEN
        // SEARCH THE IMAGES OF THE WORDS FIRST IN THE INTERNAL MEMORY,
        // IF THE IMAGE IS NOT IN THE INTERNAL MEMORY
        // SEARCH THE IMAGES ON ARASAAC
        val verbToSearch = searchVerb(word, realm)
        val wordToSearch: String?
        val idToSearch: String?
        wordToSearch = if (verbToSearch != "non trovato") {
            verbToSearch
        } else {
            word
        }
        // search the images of the words first in the internal memory
        val uriToSearch = searchUri(realm, wordToSearch)
        if (uriToSearch != "non trovata") {
            return ResponseImageSearch(wordToSearch!!, "S", uriToSearch)
        } else  // if the image is not in the internal memory search the images on Arasaac
        {
            idToSearch = searchId(realm, wordToSearch)
            if (idToSearch != "non trovata") {
                val url =
                    REMOTE_ADDR_PICTOGRAM + idToSearch + "?download=false"
                return ResponseImageSearch(wordToSearch!!, "A", url)
            }
        }
        return null
    }

    /**
     * search for the image file path corresponding to a word
     *
     * @param realm realm
     * @param k string with corresponding word
     * @return string with file path
     * @see Images
     */
    fun searchUri(realm: Realm, k: String?): String {
        var uriToSearchRealm = "non trovata"
        //
        val results = realm.where(
            Images::class.java
        ).equalTo("descrizione", k).findAll()
        val count = results.size
        if (count != 0) {
            val result = results[0]
            if (result != null) {
                uriToSearchRealm = result.uri!!
            }
        }
        return uriToSearchRealm
    }

    /**
     * search for the image url id corresponding to a word
     *
     * @param realm realm
     * @param k string with corresponding word
     * @return string with id url of arasaac
     * @see PictogramsAll
     */
    fun searchId(realm: Realm, k: String?): String {
        var idToSearchRealm = "non trovata"
        //
        val results = realm.where(PictogramsAll::class.java).equalTo("keyword", k).findAll()
        val count = results.size
        if (count != 0) {
            val result = results[0]
            if (result != null) {
                idToSearchRealm = result.get_id()!!
            }
        }
        return idToSearchRealm
    }
}