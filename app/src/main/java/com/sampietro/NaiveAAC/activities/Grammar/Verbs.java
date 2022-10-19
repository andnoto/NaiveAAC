package com.sampietro.NaiveAAC.activities.Grammar;

import io.realm.RealmObject;
import io.realm.annotations.Index;

/**
 * <h1>Verbs</h1>
 * <p><b>Verbs</b> represents the used portion of the downloaded data from https://github.com/ian-hamlin/verb-data
 * a collection of verbs and conjugations.</p>
 */
public class Verbs extends RealmObject {
    /**
     * conjugation.
     */
    @Index
    private String conjugation;
    /**
     * represent the person of <code>conjugation</code>.
     * <p>
     * form=s1 first person singular
     * <p>
     * form=s2 second person singular
     * <p>
     * form=s3 third person singular
     * <p>
     * form=p1 first person plural
     * <p>
     * form=p2 second person plural
     * <p>
     * form=p3 third person plural
     */
    private String form;
    /**
     * represent the group of <code>conjugation</code>.
     * <p>
     * group="indicative/present"
     * <p>
     * group="indicative/imperfect"
     * <p>
     * group="indicative/future"
     */
    private String group;
    /**
     * represent the infinitive of <code>conjugation</code>.
     */
    private String infinitive;
    /*
     * getter, setter and other methods
     */
    /**
     * get <code>conjugation</code>.
     *
     * @return conjugation string data to get
     */
    public String getConjugation() {
        return conjugation;
    }
    /**
     * get <code>form</code>.
     *
     * @return form string data to get
     */
    public String getForm() {
        return form;
    }
    /**
     * get <code>group</code>.
     *
     * @return group string data to get
     */
    public String getGroup() {
        return group;
    }
    /**
     * get <code>infinitive</code>.
     *
     * @return infinitive string data to get
     */
    public String getInfinitive() {
        return infinitive;
    }
    //
    /**
     * set <code>conjugation</code>.
     *
     * @param conjugation string data to set
     */
    public void setConjugation(String conjugation)
    {
        this.conjugation = conjugation;
    }
    /**
     * set <code>form</code>.
     *
     * @param form string data to set
     */
    public void setForm(String form)
    {
        this.form = form;
    }
    /**
     * set <code>group</code>.
     *
     * @param group string data to set
     */
    public void setGroup(String group)
    {
        this.group = group;
    }
    /**
     * set <code>infinitive</code>.
     *
     * @param infinitive string data to set
     */
    public void setInfinitive(String infinitive)
    {
        this.infinitive = infinitive;
    }

}
