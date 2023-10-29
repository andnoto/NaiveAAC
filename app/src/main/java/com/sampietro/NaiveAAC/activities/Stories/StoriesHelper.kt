package com.sampietro.NaiveAAC.activities.Stories

import io.realm.Realm
import java.util.*

/**
 * <h1>StoriesHelper</h1>
 *
 *
 * **StoriesHelper** utility class for stories.
 *
 */
object StoriesHelper {
    /**
     * renumber a sentence in a story
     *
     * @param realm realm
     * @param story string
     * @param phraseNumber int
     */
    @JvmStatic
    fun renumberAPhraseOfAStory(realm: Realm, story: String?, phraseNumber: Int) {
        val resultsStories = realm.where(Stories::class.java)
            .equalTo("story", story)
            .equalTo("phraseNumberInt", phraseNumber)
            .findAll()
        val storiesSize = resultsStories.size
        //
        // cancello frase vecchia
        // copio la frase vecchia rinumerando wordNumber
        //
        // clear the table
        // cancello frase vecchia dopo averne salvato copia
        val resultsStoriesList = realm.copyFromRealm(resultsStories)
        //
        Collections.sort(resultsStoriesList, StoriesComparator())
        //
        realm.beginTransaction()
        resultsStories.deleteAllFromRealm()
        realm.commitTransaction()
        //
        // copio la frase vecchia rinumerando wordNumber
        var irrh = 0
        while (irrh < storiesSize) {
            val resultStories = resultsStoriesList[irrh]!!
            val currentWordNumber = resultStories.wordNumberInt
            if (currentWordNumber < 99) {
                resultStories.wordNumberInt = irrh
            }
            realm.beginTransaction()
            realm.copyToRealm(resultStories)
            realm.commitTransaction()
            //
            irrh++
        }
    }

    /**
     * renumber a story
     *
     * @param realm realm
     * @param story string
     */
    @JvmStatic
    fun renumberAStory(realm: Realm, story: String?) {
        val resultsStories = realm.where(Stories::class.java)
            .equalTo("story", story)
            .findAll()
        val storiesSize = resultsStories.size
        //
        val resultsStoriesList = realm.copyFromRealm(resultsStories)
        //
        Collections.sort(resultsStoriesList, StoriesComparator())
        //
        realm.beginTransaction()
        resultsStories.deleteAllFromRealm()
        realm.commitTransaction()
        //
        var irrh = 0
        var wordNumberIntInTheStory = 0
        while (irrh < storiesSize) {
            val resultStories = resultsStoriesList[irrh]!!
            val currentWordNumber = resultStories.wordNumberInt
            if (currentWordNumber != 0 && currentWordNumber < 99) {
                wordNumberIntInTheStory++
                resultStories.wordNumberIntInTheStory = wordNumberIntInTheStory
            }
            realm.beginTransaction()
            realm.copyToRealm(resultStories)
            realm.commitTransaction()
            //
            irrh++
        }
    }

    /**
     * renumber stories
     *
     * @param realm realm
     */
    @JvmStatic
    fun renumberStories(realm: Realm) {
        val resultsStories = realm.where(Stories::class.java)
            .findAll()
        val storiesSize = resultsStories.size
        //
        val resultsStoriesList = realm.copyFromRealm(resultsStories)
        //
        Collections.sort(resultsStoriesList, StoriesComparator())
        //
        realm.beginTransaction()
        resultsStories.deleteAllFromRealm()
        realm.commitTransaction()
        //
        var irrh = 0
        var story = ""
        var wordNumberIntInTheStory = 0
        while (irrh < storiesSize) {
            val resultStories = resultsStoriesList[irrh]!!
            if (resultStories.story!!.compareTo(story) != 0) {
                story = resultStories.story!!
                wordNumberIntInTheStory = 0
            }
            val currentWordNumber = resultStories.wordNumberInt
            if (currentWordNumber != 0 && currentWordNumber < 99) {
                wordNumberIntInTheStory++
                resultStories.wordNumberIntInTheStory = wordNumberIntInTheStory
            }
            realm.beginTransaction()
            realm.copyToRealm(resultStories)
            realm.commitTransaction()
            //
            irrh++
        }
    }
}