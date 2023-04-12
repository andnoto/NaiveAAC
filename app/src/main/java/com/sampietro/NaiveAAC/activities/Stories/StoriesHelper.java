package com.sampietro.NaiveAAC.activities.Stories;

import java.util.Collections;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * <h1>StoriesHelper</h1>
 *
 * <p><b>StoriesHelper</b> utility class for stories.</p>
 *
 */
public class StoriesHelper {

    /**
     * renumber a sentence in a story
     *
     * @param realm realm
     * @param story string
     * @param phraseNumber int
     */
    public static void renumberAPhraseOfAStory(Realm realm, String story, int phraseNumber)
    {
        RealmResults<Stories> resultsStories =
                realm.where(Stories.class)
                        .equalTo("story", story)
                        .equalTo("phraseNumberInt", phraseNumber)
                        .findAll();
        int storiesSize = resultsStories.size();
        //
        // cancello frase vecchia
        // copio la frase vecchia rinumerando wordNumber
        //
        // clear the table
        // cancello frase vecchia dopo averne salvato copia
        List<Stories> resultsStoriesList = realm.copyFromRealm(resultsStories);
        //
        Collections.sort(resultsStoriesList,new StoriesComparator());
        //
        realm.beginTransaction();
        resultsStories.deleteAllFromRealm();
        realm.commitTransaction();
        //
        // copio la frase vecchia rinumerando wordNumber
        int irrh=0;
        while(irrh < storiesSize)   {
            Stories resultStories = resultsStoriesList.get(irrh);
            assert resultStories != null;
            int currentWordNumber = resultStories.getWordNumber();
            if (!(currentWordNumber >= 99))
            {
                resultStories.setWordNumber(irrh);
            }
            realm.beginTransaction();
            realm.copyToRealm(resultStories);
            realm.commitTransaction();
            //
            irrh++;
        }
    }
    /**
     * renumber a story
     *
     * @param realm realm
     * @param story string
     */
    public static void renumberAStory(Realm realm, String story)
    {
        RealmResults<Stories> resultsStories =
                realm.where(Stories.class)
                        .equalTo("story", story)
                        .findAll();
        int storiesSize = resultsStories.size();
        //
        List<Stories> resultsStoriesList = realm.copyFromRealm(resultsStories);
        //
        Collections.sort(resultsStoriesList,new StoriesComparator());
        //
        realm.beginTransaction();
        resultsStories.deleteAllFromRealm();
        realm.commitTransaction();
        //
        int irrh=0;
        int wordNumberIntInTheStory = 0;
        while(irrh < storiesSize)   {
            Stories resultStories = resultsStoriesList.get(irrh);
            assert resultStories != null;
            int currentWordNumber = resultStories.getWordNumber();
            if (!(currentWordNumber == 0) && !(currentWordNumber >= 99))
            {
                wordNumberIntInTheStory++;
                resultStories.setWordNumberIntInTheStory(wordNumberIntInTheStory);
            }
            realm.beginTransaction();
            realm.copyToRealm(resultStories);
            realm.commitTransaction();
            //
            irrh++;
        }
    }
    /**
     * renumber stories
     *
     * @param realm realm
     */
    public static void renumberStories(Realm realm)
    {
        RealmResults<Stories> resultsStories =
                realm.where(Stories.class)
                        .findAll();
        int storiesSize = resultsStories.size();
        //
        List<Stories> resultsStoriesList = realm.copyFromRealm(resultsStories);
        //
        Collections.sort(resultsStoriesList,new StoriesComparator());
        //
        realm.beginTransaction();
        resultsStories.deleteAllFromRealm();
        realm.commitTransaction();
        //
        int irrh=0;
        String story ="";
        int wordNumberIntInTheStory = 0;
        while(irrh < storiesSize)   {
            Stories resultStories = resultsStoriesList.get(irrh);
            assert resultStories != null;
            if (resultStories.getStory().compareTo(story) != 0)
                {
                story = resultStories.getStory();
                wordNumberIntInTheStory = 0;
                }
            int currentWordNumber = resultStories.getWordNumber();
            if (!(currentWordNumber == 0) && !(currentWordNumber >= 99))
            {
                wordNumberIntInTheStory++;
                resultStories.setWordNumberIntInTheStory(wordNumberIntInTheStory);
            }
            realm.beginTransaction();
            realm.copyToRealm(resultStories);
            realm.commitTransaction();
            //
            irrh++;
            }
    }
}
