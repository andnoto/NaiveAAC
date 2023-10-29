package com.sampietro.NaiveAAC.activities.Arasaac

import io.realm.RealmObject
import io.realm.annotations.Index

/**
 * <h1>PictogramsAll</h1>
 *
 * **PictogramsAll** represents the used portion of the downloaded data from the Arasaac /pictograms/all API.
 */
open class PictogramsAll : RealmObject() {
    /**
     * represent the key of the pictogram associated with `keyword`.
     */
    private var _id: String? = null
    /**
     * represent the type of `keyword`.
     *
     *
     * type = 1 nouns without plural
     *
     *
     * type = 2 nouns (or phrases) with possible plural
     *
     *
     * type = 3 verbs
     *
     *
     * type = 4 numbers , adjectives (by the way)
     *
     *
     * type = 5 Greetings (by the way)
     *
     *
     * type = 6 articles, prepositions, adverbs of place (among others)
     */
    var type: String? = null
    /**
     * word or phrase.
     */
    @Index
    var keyword: String? = null
    /**
     * Y indicates if `keyword` is plural.
     */
    var plural: String? = null
    /**
     * `keyword` in the plural.
     */
    var keywordPlural: String? = null
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