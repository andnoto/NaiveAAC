package com.sampietro.NaiveAAC.activities.Game.Game2

import androidx.annotation.LayoutRes
import com.sampietro.NaiveAAC.R
import com.sampietro.NaiveAAC.activities.Game.GameADA.GameADAArrayList
import com.sampietro.NaiveAAC.activities.Game.GameADA.GameADAFragment
import com.sampietro.NaiveAAC.activities.Stories.Stories
import com.sampietro.NaiveAAC.activities.history.History
import java.util.*

class Game2BleGameADAFragment(@LayoutRes contentLayoutId : Int = 0) : GameADAFragment(contentLayoutId) {
    /**
     * prepare data for the recyclerview using data from the history table
     *
     * @return theimage arraylist<Game2ArrayList> data for recyclerview
     * @see GameADAArrayList
     *
     * @see History
    */
    override fun prepareData1(): ArrayList<GameADAArrayList> {
        var row1debugUrlNumber = 0
        //
        val results =
            realm.where(Stories::class.java)
                .equalTo(getString(R.string.story), keywordStoryToAdd)
                .findAll()
        //
        val count = results.size
        if (count != 0) {
            var irrh = 0
            while (irrh < count && irrh < 33) {
                val result = results[irrh]!!
                val wordNumber = Objects.requireNonNull(result.wordNumberInt)
                //
                if (wordNumber != 0 && wordNumber != 99
                    && wordNumber != 999 && wordNumber != 9999
                ) {
                    if (preference_TitleWritingType == "uppercase") row1debugWord[irrh - 1] =
                        result.word!!.uppercase(
                            Locale.getDefault()
                        ) else row1debugWord[irrh - 1] = result.word!!.lowercase(
                        Locale.getDefault()
                    )
                    //
                    row1debugUrlType[irrh - 1] = result.uriType
                    row1debugUrl[irrh - 1] = result.uri
                    //
                    row1debugUrlNumber++
                }
                if (wordNumber == 0) {
                    sentence = result.word
                }
                irrh++
            }
        }
        //
        val theimage = ArrayList<GameADAArrayList>()
        for (i in 0 until row1debugUrlNumber) {
            val createList = GameADAArrayList()
            createList.image_title = row1debugWord[i]
            createList.urlType = row1debugUrlType[i]
            createList.url = row1debugUrl[i]
            theimage.add(createList)
        }
        return theimage
    }
}