package com.sampietro.NaiveAAC.activities.Stories

/**
 * <h1>VoiceToBeRecordedInStories</h1>
 *
 *
 * **VoiceToBeRecordedInStories** utility class for registration in Stories class.
 *
 * @see Stories
 *
 * @version     4.0, 09/09/2023
 */
open class VoiceToBeRecordedInStories {
    /**
     * key that identifies the story.
     */
    var story: String? = null
    /**
     * order number of the sentence in a given story.
     */
    var phraseNumberInt = 0
    /**
     * order number of the word in a given sentence or the entire sentence or a question / answer related to the phrase or
     * the topic of the sentence.
     *
     *
     * if wordNumber = 0 then word contains the entire sentence
     *
     *
     * if wordNumber > 0 and < 99 then wordNumber contains the pass number of the word and word contains the word itself
     *
     *
     * if wordNumber = 99 then word contain a question related to the phrase
     *
     *
     * if wordNumber = 999 the word contain the answer related to the phrase
     *
     *
     * if wordNumber = 9999 the word contain the topic of the sentence
     */
    var wordNumberInt = 0
    /**
     * sentence, word, question , answer or topic belonging to the story identified with the `story`.
     */
    var word: String? = null
    /**
     * represent the type of media associated with the `word`.
     *
     *
     * uritype = S then , if it exists, uri refers to an image from storage
     *
     *
     * uritype = A then , if it exists, uri refers to an image from a url of arasaac
     *
     *
     * if neither the arasaac url nor the file path are indicated, the image is associated using PictogramsAll
     */
    var uriType: String? = null
    /**
     * contains the file path or a url of arasaac  (originally contained the uri).
     */
    var uri: String? = null
    /**
     * contains the type of action associated with the `word` in case the word is an answer.
     *
     *
     * answerActionType = V then the action consists of playing a video
     *
     *
     * answerActionType = G then the action consists consists of executing a goto (phraseNumber) statement
     * corresponding to the topic of a sentence
     */
    var answerActionType: String? = null
    /**
     * contains the action associated with the `word` in case the word is an answer:
     *
     *
     * contains the video key in the videos table or the topic of a sentence
     */
    var answerAction: String? = null
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
     * contains Y if the object was imported from assets.
     */
    var fromAssets: String? = null
}