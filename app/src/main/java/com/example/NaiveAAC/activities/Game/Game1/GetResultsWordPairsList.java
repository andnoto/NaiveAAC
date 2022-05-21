package com.example.NaiveAAC.activities.Game.Game1;

import com.example.NaiveAAC.activities.WordPairs.WordPairs;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * <h1>GetResultsWordPairsList</h1>
 * <p><b>GetResultsWordPairsList</b> convert RealmResults<Model> to ArrayList<Model>
 * </p>
 * Refer to <a href="https://stackoverflow.com/questions/37250987/how-to-convert-realmresultsmodel-to-arraylistmodel">stackoverflow</a>
 * answer of <a href="https://stackoverflow.com/users/5152885/dh28">DH28</a>
 *
 * @version     1.3, 05/05/22
 */
public class GetResultsWordPairsList {
    /**
     * convert RealmResults<Model> to ArrayList<Model>
     *
     * @param realm realm
     * @param resultsWordPairs RealmResults<WordPairs> to convert
     * @return resultsWordPairsList List<WordPairs> converted
     * @see WordPairs
     */
    public static List<WordPairs>
    getResultsWordPairsList(Realm realm, RealmResults<WordPairs> resultsWordPairs) {
        List<WordPairs> resultsWordPairsList = new ArrayList<>();
        resultsWordPairsList.addAll(realm.copyFromRealm(resultsWordPairs));
        return resultsWordPairsList;
    }
}
