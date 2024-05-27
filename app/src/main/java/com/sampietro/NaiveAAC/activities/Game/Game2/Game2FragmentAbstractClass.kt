package com.sampietro.NaiveAAC.activities.Game.Game2

import android.widget.EditText
import android.widget.ImageButton
import androidx.annotation.LayoutRes
import com.sampietro.NaiveAAC.R
import com.sampietro.NaiveAAC.activities.BaseAndAbstractClass.GameFragmentAbstractClass
import com.sampietro.NaiveAAC.activities.Game.Game1.Game1ArrayList
import com.sampietro.NaiveAAC.activities.history.History
import java.util.Locale

/**
 * <h1>Game2FragmentAbstractClass</h1>
 *
 * **GameFragment** UI for game2
 *
 *
 * @version     5.0, 01/04/2024
 * @see GameFragmentAbstractClass
 *
 * @see Game2Activity
 */
abstract class Game2FragmentAbstractClass(@LayoutRes contentLayoutId : Int = 0) : GameFragmentAbstractClass(contentLayoutId)  {
    lateinit var hearingImageButton: ImageButton
    lateinit var sentenceToAdd: EditText
    //
    var sharedLastPhraseNumber = 0
    //
    var row1debugWord = arrayOfNulls<String>(32)
    var row1debugUrlType = arrayOfNulls<String>(32)
    var row1debugUrl = arrayOfNulls<String>(32)
    /**
     * prepare data for the first recyclerview (first recyclerview from above) using data from the history table
     *
     * @return theimage arraylist<Game1ArrayList> data for recyclerview
     * @see Game1ArrayList
     *
     * @see History
    */
    internal fun prepareData(
        phraseNumber: Int
    ): ArrayList<Game1ArrayList>
    {
//        var row1debugUrlNumber = 0
        //
        val results = realm.where(
            History::class.java
        ).equalTo(getString(R.string.phrase_number), phraseNumber.toString()).findAll()
        val count = results.size
        val row1debugUrlNumber = count - 1
        if (count != 0) {
            var irrh = 0
            while (irrh < count && irrh < 33) {
                val result = results[irrh]!!
                val wordNumber = result.wordNumber!!.toInt()
                //
                if (wordNumber != 0) {
                    if (preference_TitleWritingType == getString(R.string.uppercase)) row1debugWord[irrh - 1] =
                        result.word!!.uppercase(
                            Locale.getDefault()
                        ) else row1debugWord[irrh - 1] = result.word!!.lowercase(
                        Locale.getDefault()
                    )
                    //
                    row1debugUrlType[irrh - 1] = result.uriType
                    row1debugUrl[irrh - 1] = result.uri
                }
                irrh++
            }
        }
        //
        val theimage = ArrayList<Game1ArrayList>()
        for (i in 0 until row1debugUrlNumber) {
            val createList = Game1ArrayList()
            createList.image_title = row1debugWord[i]
            createList.urlType = row1debugUrlType[i]
            createList.url = row1debugUrl[i]
            theimage.add(createList)
        }
        return theimage
    }
}