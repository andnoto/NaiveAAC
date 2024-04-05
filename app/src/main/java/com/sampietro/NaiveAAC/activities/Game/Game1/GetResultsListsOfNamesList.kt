package com.sampietro.NaiveAAC.activities.Game.Game1

import com.sampietro.NaiveAAC.activities.Grammar.ListsOfNames
import io.realm.RealmResults
import io.realm.Realm
import java.util.ArrayList

/**
 * <h1>GetResultsListsOfNamesList</h1>
 *
 * **GetResultsListsOfNamesList** convert RealmResults<Model> to ArrayList<Model>
</Model></Model> *
 * REFER to [stackoverflow](https://stackoverflow.com/questions/37250987/how-to-convert-realmresultsmodel-to-arraylistmodel)
 * answer of [DH28](https://stackoverflow.com/users/5152885/dh28)
 *
 * @version     5.0, 01/04/2024
 */
object GetResultsListsOfNamesList {
    /**
     * convert RealmResults<Model> to ArrayList<Model>
     *
     * @param realm realm
     * @param resultsListsOfNames RealmResults<ListOfNames> to convert
     * @return resultsListOfNamesList List<ListOfNames> converted
     * @see ListsOfNames
    */
    fun getResultsListsOfNamesList(
        realm: Realm,
        resultsListsOfNames: RealmResults<ListsOfNames>?
    ): MutableList<ListsOfNames> {
        val resultsListsOfNamesList: MutableList<ListsOfNames> = ArrayList()
        resultsListsOfNamesList.addAll(realm.copyFromRealm(resultsListsOfNames!!))
        return resultsListsOfNamesList
    }
}