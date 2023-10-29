package com.sampietro.NaiveAAC.activities.history

import java.util.*

/**
 * <h1>VoiceToBeRecordedInHistory</h1>
 *
 *
 * **VoiceToBeRecordedInHistory** utility class for registration in history class.
 *
 * @see History
 *
 * @version     4.0, 09/09/2023
 */
class VoiceToBeRecordedInHistory {
    /*
     * getter, setter and other methods
     *///
    /**
     * represents the progressive number of the app session that is incremented in main activity.
     */
    var session: Int

    /**
     * represents the progressive number of the sentence.
     */
    var phraseNumber: Int

    /**
     * represents the date of the sentence.
     */
    var date: Date

    /**
     * if wordNumber = 0 then word contains the entire sentence
     * if wordNumber > 0 then wordNumber contains the pass number of the word and
     * word contains the word itself
     */
    var wordNumber: Int

    /**
     * type = 1 word is a noun without plural
     * type = 2 word is a noun with plural
     * type = 3 word is a verb
     */
    var type: String

    /**
     * word or sentence
     */
    var word: String

    /**
     * plural = N then word is not a plural
     * plural = Y then word is a plural
     */
    var plural: String

    /**
     * uritype = A then , if it exists, image corresponding to the word refers to Arasaac
     * uritype = S then , if it exists, image corresponding to the word refers to storage
     * uritype = C then , uri not registered because word is a complement of the name
     */
    var uriType: String

    /**
     * contains the file path or the url of arasaac (originally contained the uri).
     */
    var uri: String

    /**
     * refers to a video key in the videos table .
     */
    var video: String? = null

    /**
     * refers to a sound key in the sounds table .
     */
    var sound: String? = null

    /**
     * indicates whether sound replaces TTS (Y or N).
     */
    var soundReplacesTTS: String? = null

    /**
     * VoiceToBeRecordedInHistory constructor.
     * set fields
     */
    constructor(
        session: Int, phraseNumber: Int, date: Date, wordNumber: Int,
        type: String, word: String, plural: String, uriType: String, uri: String
    ) {
        this.session = session
        this.phraseNumber = phraseNumber
        this.date = date
        this.wordNumber = wordNumber
        this.type = type
        this.word = word
        this.plural = plural
        this.uriType = uriType
        this.uri = uri
    }

    //
    constructor(
        session: Int,
        phraseNumber: Int,
        date: Date,
        wordNumber: Int,
        type: String,
        word: String,
        plural: String,
        uriType: String,
        uri: String,
        video: String?,
        sound: String?,
        soundReplacesTTS: String?
    ) {
        this.session = session
        this.phraseNumber = phraseNumber
        this.date = date
        this.wordNumber = wordNumber
        this.type = type
        this.word = word
        this.plural = plural
        this.uriType = uriType
        this.uri = uri
        this.video = video
        this.sound = sound
        this.soundReplacesTTS = soundReplacesTTS
    }
}