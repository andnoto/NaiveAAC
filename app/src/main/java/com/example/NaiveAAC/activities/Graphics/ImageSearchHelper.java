package com.example.NaiveAAC.activities.Graphics;

import static com.example.NaiveAAC.activities.Grammar.GrammarHelper.searchVerb;

import com.example.NaiveAAC.activities.Arasaac.PictogramsAll;
import com.example.NaiveAAC.activities.Grammar.GrammarHelper;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * <h1>ImageSearchHelper</h1>
 *
 * <p><b>ImageSearchHelper</b> utility class for image search.</p>
 *
 * @version     1.3, 05/05/22
 */
public class ImageSearchHelper {
    private static final String REMOTE_ADDR_PICTOGRAM=
            "https://api.arasaac.org/api/pictograms/";
    /**
     * search for the image corresponding to a word in the following way :
     * 1) if it is conjugation search for infinity
     * 2) search the images of the words first in the internal menory
     * 3) if the image is not in the internal memory search the images on Arasaac
     *
     * @param realm realm
     * @param word string with corresponding word
     * @return ResponseImageSearch
     * @see GrammarHelper#searchVerb
     * @see #searchUri(Realm, String)
     * @see ResponseImageSearch
     */
    public static ResponseImageSearch imageSearch(Realm realm, String word)
    {
        // IF IT IS CONJUGATION SEARCH FOR INFINITY, THEN
        // SEARCH THE IMAGES OF THE WORDS FIRST IN THE INTERNAL MEMORY,
        // IF THE IMAGE IS NOT IN THE INTERNAL MEMORY
        // SEARCH THE IMAGES ON ARASAAC
        String verbToSearch = searchVerb(word, realm);
        String wordToSearch;
        String idToSearch;
        if (!verbToSearch.equals("non trovato")) {
            wordToSearch = verbToSearch;
        }
        else
        {
            wordToSearch = word;
        };
        // search the images of the words first in the internal memory
        String uriToSearch = searchUri(realm, wordToSearch);
        if (!uriToSearch.equals("non trovata")) {
            ResponseImageSearch responseImageSearch = new ResponseImageSearch
                    (wordToSearch, "S", uriToSearch);
            return responseImageSearch;
        }
        //
        else
        // if the image is not in the internal memory search the images on Arasaac
        {
            idToSearch = searchId(realm, wordToSearch);
            if (!idToSearch.equals("non trovata")) {
                String url = REMOTE_ADDR_PICTOGRAM + idToSearch +"?download=false";
                ResponseImageSearch responseImageSearch = new ResponseImageSearch
                        (wordToSearch, "A", url);
                return responseImageSearch;
            }
        }
        return null;
    }
    /**
     * search for the image file path corresponding to a word
     *
     * @param realm realm
     * @param k string with corresponding word
     * @return string with file path
     * @see Images
     */
    public static String searchUri(Realm realm, String k)
    {
        String uriToSearchRealm = "non trovata";
        //
        RealmResults<Images> results =
                realm.where(Images.class).equalTo("descrizione", k).findAll();
        int count = results.size();
        if (count != 0) {
            Images result = results.get(0);
            if (result != null) {
                uriToSearchRealm = result.getUri();
            }
        }
        return uriToSearchRealm;
    }
    /**
     * search for the image url id corresponding to a word
     *
     * @param realm realm
     * @param k string with corresponding word
     * @return string with id url of arasaac
     * @see PictogramsAll
     */
    public static String searchId(Realm realm, String k)
    {
        String idToSearchRealm = "non trovata";
        //
        RealmResults<PictogramsAll> results =
                realm.where(PictogramsAll.class).equalTo("keyword", k).findAll();
        int count = results.size();
        if (count != 0) {
            PictogramsAll result = results.get(0);
            if (result != null) {
                idToSearchRealm = result.get_id();
            }
        }
        return idToSearchRealm;
    }
}
