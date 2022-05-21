package com.example.NaiveAAC.activities.Arasaac;

import io.realm.RealmObject;
import io.realm.annotations.Index;

/**
 * <h1>PictogramsAll</h1>
 * <p><b>PictogramsAll</b> represents the used portion of the downloaded data from the Arasaac /pictograms/all API.</p>
 */
public class PictogramsAll extends RealmObject {
    /**
     * represent the key of the pictogram associated with <code>keyword</code>.
     */
    private String _id;
    /**
     * represent the type of <code>keyword</code>.
     * <p>
     * type = 1 nouns without plural
     * <p>
     * type = 2 nouns (or phrases) with possible plural
     * <p>
     * type = 3 verbs
     * <p>
     * type = 4 numbers (by the way)
     * <p>
     * type = 6 articles, prepositions, adverbs of place (among others)
     */
    private String type;
    /**
     * word or phrase.
     */
    @Index
    private String keyword;
    /**
     * Y indicates if <code>keyword</code> is plural.
     */
    private String plural;
    /**
     * <code>keyword</code> in the plural.
     */
    private String keywordPlural;

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
    /**
     * get <code>plural</code>.
     *
     * @return plural string data to get
     */
    public String getPlural() {
        return plural;
    }
    /**
     * get <code>keywordPlural</code>.
     *
     * @return keywordPlural string data to get
     */
    public String getKeywordPlural() {
        return keywordPlural;
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
    /**
     * set <code>plural</code>.
     *
     * @param plural string data to set
     */
    public void setPlural(String plural)
    {
        this.plural = plural;
    }
    /**
     * set <code>keywordPlural</code>.
     *
     * @param keywordPlural string data to set
     */
    public void setKeywordPlural(String keywordPlural)
    {
        this.keywordPlural = keywordPlural;
    }

}
