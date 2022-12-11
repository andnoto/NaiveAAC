package com.sampietro.NaiveAAC.activities.Stories;

import java.util.Comparator;

public class StoriesComparator implements Comparator<Stories> {
    @Override
    public int compare(Stories s1, Stories s2) {
        int s1PhraseNumber = s1.getPhraseNumber();
        int s1WordNumber = s1.getWordNumber();
        int s2PhraseNumber = s2.getPhraseNumber();
        int s2WordNumber = s2.getWordNumber();
        int retVal=0;
        if(s1PhraseNumber>s2PhraseNumber)
        {
            retVal=1;
        }
        else if(s1PhraseNumber<s2PhraseNumber)
             {
             retVal=-1;
             }
            else
            if(s1WordNumber>s2WordNumber)
            {
            retVal=1;
            }
            else if(s1WordNumber<s2WordNumber)
            {
                retVal=-1;
            }

        return retVal;
    }
}
