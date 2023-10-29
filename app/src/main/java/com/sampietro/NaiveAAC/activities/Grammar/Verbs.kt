package com.sampietro.NaiveAAC.activities.Grammar

import io.realm.RealmObject
import io.realm.annotations.Index

/**
 * <h1>Verbs</h1>
 *
 * **Verbs** represents the used portion of the downloaded data from https://github.com/ian-hamlin/verb-data
 * a collection of verbs and conjugations.
 */
open class Verbs : RealmObject() {
    /**
     * conjugation.
     */
    @Index
    var conjugation: String? = null
    /**
     * represent the person of `conjugation`.
     *
     *
     * form=s1 first person singular
     *
     *
     * form=s2 second person singular
     *
     *
     * form=s3 third person singular
     *
     *
     * form=p1 first person plural
     *
     *
     * form=p2 second person plural
     *
     *
     * form=p3 third person plural
     */
    var form: String? = null
    /**
     * represent the group of `conjugation`.
     *
     *
     * group="indicative/present"
     *
     *
     * group="indicative/imperfect"
     *
     *
     * group="indicative/future"
     */
    var group: String? = null
    /**
     * represent the infinitive of `conjugation`.
     */
    var infinitive: String? = null
    //
}