package com.sampietro.NaiveAAC.activities.Game.Game1

import com.sampietro.NaiveAAC.activities.WordPairs.WordPairs
import io.realm.Realm
import io.realm.RealmResults

/**
 * <h1>GetResultsWordPairsList</h1>
 *
 * **GetResultsWordPairsList** convert RealmResults<Model> to ArrayList<Model>
</Model></Model> *
 * REFER to [stackoverflow](https://stackoverflow.com/questions/37250987/how-to-convert-realmresultsmodel-to-arraylistmodel)
 * answer of [DH28](https://stackoverflow.com/users/5152885/dh28)
 *
 * @version     4.0, 09/09/2023
 */
object GetResultsWordPairsList {
    /**
     * convert RealmResults<Model> to ArrayList<Model>
     *
     * @param realm realm
     * @param resultsWordPairs RealmResults<WordPairs> to convert
     * @return resultsWordPairsList List<WordPairs> converted
     * @see WordPairs
    </WordPairs></WordPairs></Model></Model> */
    fun getResultsWordPairsList(
        realm: Realm,
        resultsWordPairs: RealmResults<WordPairs>?
    ): MutableList<WordPairs> {
        val resultsWordPairsList: MutableList<WordPairs> = ArrayList()
        resultsWordPairsList.addAll(realm.copyFromRealm(resultsWordPairs!!))
        return resultsWordPairsList
    }
}