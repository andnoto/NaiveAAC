package com.example.NaiveAAC.activities.Grammar;

import io.realm.RealmObject;

/**
 * <h1>ComplementsOfTheName</h1>
 * <p><b>ComplementsOfTheName</b> represents the complements of the name downloaded from the Arasaac /pictograms/all API.</p>
 */
public class ComplementsOfTheName extends RealmObject {
    /**
     * represent the key of the pictogram associated with <code>keyword</code>.
     */
    private String _id;
    /**
     * represent the type of <code>keyword</code>.
     * <p>
     * type = 4 numbers (by the way)
     * <p>
     * type = 6 articles, prepositions, adverbs of place (among others)
     */
    private String type;
    /**
     * word.
     */
    private String keyword;

    /*
     * getter, setter and other methods
     */
    /**
     * get <code>_id</code>.
     *
     * @return _id string data to get
     */
    public String get_id() {
        return _id;
    }
    /**
     * get <code>type</code>.
     *
     * @return type string data to get
     */
    public String getType() {
        return type;
    }
    /**
     * get <code>keyword</code>.
     *
     * @return keyword string data to get
     */
    public String getKeyword() {
        return keyword;
    }
    //
    /**
     * set <code>_id</code>.
     *
     * @param _id string data to set
     */
    public void set_id(String _id)
    {
        this._id = _id;
    }
    /**
     * set <code>type</code>.
     *
     * @param type string data to set
     */
    public void setType(String type)
    {
        this.type = type;
    }
    /**
     * set <code>keyword</code>.
     *
     * @param keyword string data to set
     */
    public void setKeyword(String keyword)
    {
        this.keyword = keyword;
    }
}
