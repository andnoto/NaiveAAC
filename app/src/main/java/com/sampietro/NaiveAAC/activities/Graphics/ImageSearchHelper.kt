package com.sampietro.NaiveAAC.activities.Graphics

import android.content.Context
import com.sampietro.NaiveAAC.R
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
     * @param context context
     * @param realm realm
     * @param word string with corresponding word
     * @return ResponseImageSearch
     * @see com.sampietro.NaiveAAC.activities.Grammar.GrammarHelper.searchVerb
     *
     * @see searchUri
     * @see ResponseImageSearch
     */
    @JvmStatic
    fun imageSearch(context: Context,
                    realm: Realm,
                    word: String?): ResponseImageSearch? {
        // IF IT IS CONJUGATION SEARCH FOR INFINITY, THEN
        // SEARCH THE IMAGES OF THE WORDS FIRST IN THE INTERNAL MEMORY,
        // IF THE IMAGE IS NOT IN THE INTERNAL MEMORY
        // SEARCH THE IMAGES ON ARASAAC
        val verbToSearch = searchVerb(context,word, realm)
        val wordToSearch: String?
        val idToSearch: String?
        wordToSearch = if (verbToSearch != context.getString(R.string.non_trovato)) {
            verbToSearch
        } else {
            word
        }
        // search the images of the words first in the internal memory
        val uriToSearch = searchUri(context, realm, wordToSearch)
        if (uriToSearch != context.getString(R.string.non_trovata)) {
            return ResponseImageSearch(wordToSearch!!, "S", uriToSearch)
        } else  // if the image is not in the internal memory search the images on Arasaac
        {
            idToSearch = searchId(context, realm, wordToSearch)
            if (idToSearch != context.getString(R.string.non_trovata)) {
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
     * @param context context
     * @param realm realm
     * @param k string with corresponding word
     * @return string with file path
     * @see Images
     */
    fun searchUri(context: Context,
                  realm: Realm,
                  k: String?): String {
        var uriToSearchRealm = context.getString(R.string.non_trovata)
        //
        val results = realm.where(
            Images::class.java
        ).equalTo(context.getString(R.string.descrizione), k).findAll()
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
     * @param context context
     * @param realm realm
     * @param k string with corresponding word
     * @return string with id url of arasaac
     * @see PictogramsAll
     */
    fun searchId(context: Context,
                 realm: Realm,
                 k: String?): String {
        var idToSearchRealm = context.getString(R.string.non_trovata)
        //
        val results = realm.where(PictogramsAll::class.java).equalTo(context.getString(R.string.keyword), k).findAll()
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