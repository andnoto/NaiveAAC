package com.sampietro.NaiveAAC.activities.Stories

import java.util.Comparator

class StoriesComparator : Comparator<Stories> {
    override fun compare(s1: Stories, s2: Stories): Int {
        val s1Story = s1.story
        val s1PhraseNumber = s1.phraseNumberInt
        val s1WordNumber = s1.wordNumberInt
        val s2Story = s2.story
        val s2PhraseNumber = s2.phraseNumberInt
        val s2WordNumber = s2.wordNumberInt
        var retVal = 0
        if (s1Story!!.compareTo(s2Story!!) > 0) {
            retVal = 1
        } else if (s1Story.compareTo(s2Story) < 0) {
            retVal = -1
        } else if (s1PhraseNumber > s2PhraseNumber) {
            retVal = 1
        } else if (s1PhraseNumber < s2PhraseNumber) {
            retVal = -1
        } else if (s1WordNumber > s2WordNumber) {
            retVal = 1
        } else if (s1WordNumber < s2WordNumber) {
            retVal = -1
        }
        return retVal
    }
}