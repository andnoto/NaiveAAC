package com.sampietro.NaiveAAC.activities.Grammar

import io.realm.RealmObject

/**
 * <h1>ComplementsOfTheName</h1>
 *
 * **ComplementsOfTheName** represents the complements of the name downloaded from the Arasaac /pictograms/all API.
 */
open class ComplementsOfTheName : RealmObject() {
    /**
     * represent the key of the pictogram associated with `keyword`.
     */
    private var _id: String? = null
    /**
     * represent the type of `keyword`.
     *
     *
     * type = 4 numbers (by the way)
     *
     *
     * type = 6 articles, prepositions, adverbs of place (among others)
     */
    var type: String? = null
    /**
     * word.
     */
    var keyword: String? = null
    /*
     * getter, setter and other methods
     */
    /**
     * get `_id`.
     *
     * @return _id string data to get
     */
    fun get_id(): String? {
        return _id
    }
    //
    /**
     * set `_id`.
     *
     * @param _id string data to set
     */
    fun set_id(_id: String?) {
        this._id = _id
    }
}