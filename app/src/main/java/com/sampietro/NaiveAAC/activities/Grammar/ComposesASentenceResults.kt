package com.sampietro.NaiveAAC.activities.Grammar

/**
 * <h1>ComposesASentenceResults</h1>
 *
 *
 * **ComposesASentenceResults** utility class for composes a sentence consisting of subject, verb, complement.
 *
 * @see GrammarHelper.composesASentence
 *
 * @version     5.0, 01/04/2024
 */
class ComposesASentenceResults(
    var outcomeOfSentenceComposition: Int,
    var numberOfWordsChosen: Int,
    var leftColumnContent: String,
    var middleColumnContent: String,
    var rightColumnContent: String,
    var listOfWordsLeft: ArrayList<String>,
    var listOfWordsCenter: ArrayList<String>,
    var listOfWordsRight: ArrayList<String>
)
    {
}