package com.sampietro.NaiveAAC.activities.WordPairs

/**
 * <h1>VoiceToBeRecordedInWordPairs</h1>
 *
 *
 * **VoiceToBeRecordedInWordPairs** utility class for registration in WordPairs class.
 *
 * @see ListOfNames
 *
 * @version     5.0, 01/04/2024
 */
class VoiceToBeRecordedInWordPairs {
    /**
     * word
     * in game1 , if it is not a verb, it represents the subject of the sentence.
     * (only noun-verb pairs are considered or vice versa verb-noun)
     */
    var word1: String? = null
    /**
     * word
     * in game1 , if it is not a verb, it represents the object complement of the sentence.
     * (only noun-verb pairs are considered or vice versa verb-noun)
     */
    var word2: String? = null
    /**
     * if `word1` is a verb, the articles and prepositions for the object complement and
     * the complements of motion to place and motion from place are automatically generated;
     * if necessary, for other types of complement, indicate the complement itself (example
     * andare - nonno insert "con il"
     */
    var complement: String? = null
    /**
     * game1 displays collections of word images that you can select to form simple sentences.
     * when the sentence is completed the app listens and
     * if spoken matches the sentence the player will receive a prize
     * awardType = V : premium video of the Game1
     * awardType = Y : premium Youtube video of the Game1.
     * if awardtype is empty the award is a simple balloon popping game
     */
    var awardType: String? = null
    /**
     * contains the video key in the videos table
     * or the Youtube url of the premium video (originally contained the uri).
     *
     * if awardType = V and uriPremiumVideo is empty,
     * the video prize is the one with the prize key in the videos table.
     */
    var uriPremiumVideo: String? = null
    /**
     * contains Y if the object was imported from assets.
     */
    var fromAssets: String? = null
}