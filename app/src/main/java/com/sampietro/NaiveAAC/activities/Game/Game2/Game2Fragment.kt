package com.sampietro.NaiveAAC.activities.Game.Game2

import com.sampietro.NaiveAAC.activities.Game.Utils.GameFragmentAbstractClass
import android.widget.ImageButton
import android.widget.EditText
import android.view.LayoutInflater
import android.view.ViewGroup
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sampietro.NaiveAAC.R
import com.sampietro.NaiveAAC.activities.history.History
import java.util.*

/**
 * <h1>Game2Fragment</h1>
 *
 * **GameFragment** UI for game2
 *
 *
 * @version     4.0, 09/09/2023
 * @see GameFragmentAbstractClass
 *
 * @see Game2Activity
 */
class Game2Fragment : GameFragmentAbstractClass() {
    var hearingImageButton: ImageButton? = null
    var sentenceToAdd: EditText? = null

    //
    var sharedLastPhraseNumber = 0

    //
    var row1debugWord = arrayOfNulls<String>(32)
    var row1debugUrlType = arrayOfNulls<String>(32)
    var row1debugUrl = arrayOfNulls<String>(32)

    /**
     * prepares the ui and makes the callback to the activity
     *
     *
     * Refer to [androidauthority](https://www.androidauthority.com/how-to-build-an-image-gallery-app-718976/)
     * by [Adam Sinicki](https://www.androidauthority.com/author/adamsinicki/)
     *
     * @see androidx.fragment.app.Fragment.onCreateView
     *
     * @see Game2ArrayList
     *
     * @see prepareData1
     *
     * @see Game2RecyclerViewAdapter1
     *
     * @see prepareData2
     *
     * @see Game2RecyclerViewAdapter2
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        rootView = inflater.inflate(R.layout.activity_game_2, container, false)
        //
        hearingImageButton = rootView.findViewById<View>(R.id.btn_start) as ImageButton
        hearingImageButton!!.setImageResource(R.drawable.ic_baseline_hearing_36_red) //set the image programmatically
        //
        sentenceToAdd = rootView.findViewById<View>(R.id.sentencetoadd) as EditText
        //
        val bundle = this.arguments
        sharedLastPhraseNumber = 0
        if (bundle != null) {
            sharedLastPhraseNumber = bundle.getInt(getString(R.string.last_phrase_number))
            sentenceToAdd!!.setText(bundle.getString("eText"))
        }
        //
        val recyclerView1 = rootView.findViewById<View>(R.id.imagegallery1) as RecyclerView
        recyclerView1.setHasFixedSize(true)
        val layoutManager1: RecyclerView.LayoutManager =
            LinearLayoutManager(ctext, LinearLayoutManager.HORIZONTAL, false)
        recyclerView1.layoutManager = layoutManager1
        val createLists1 = prepareData1()
        val adapter1 = Game2RecyclerViewAdapter1(ctext, createLists1)
        recyclerView1.adapter = adapter1
        //
        val recyclerView2 = rootView.findViewById<View>(R.id.imagegallery2) as RecyclerView
        recyclerView2.setHasFixedSize(true)
        val layoutManager2: RecyclerView.LayoutManager =
            LinearLayoutManager(ctext, LinearLayoutManager.HORIZONTAL, false)
        recyclerView2.layoutManager = layoutManager2
        val createLists2 = prepareData2()
        val adapter2 = Game2RecyclerViewAdapter2(ctext, createLists2)
        recyclerView2.adapter = adapter2
        //
        listener.receiveResultGameFragment(rootView)
        return rootView
    }
    //
    /**
     * prepare data for the first recyclerview (first recyclerview from above) using data from the history table
     *
     * @return theimage arraylist<Game2ArrayList> data for recyclerview
     * @see Game2ArrayList
     *
     * @see History
    </Game2ArrayList> */
    private fun prepareData1(): ArrayList<Game2ArrayList> {
//        var row1debugUrlNumber = 0
        //
        val results = realm.where(
            History::class.java
        ).equalTo(getString(R.string.phrase_number), sharedLastPhraseNumber.toString()).findAll()
        val count = results.size
        val row1debugUrlNumber = count - 1
        if (count != 0) {
            var irrh = 0
            while (irrh < count && irrh < 33) {
                val result = results[irrh]!!
                val wordNumber = result.wordNumber!!.toInt()
                //
                if (wordNumber != 0) {
                    if (preference_TitleWritingType == "uppercase") row1debugWord[irrh - 1] =
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
        val theimage = ArrayList<Game2ArrayList>()
        for (i in 0 until row1debugUrlNumber) {
            val createList = Game2ArrayList()
            createList.image_title = row1debugWord[i]
            createList.urlType = row1debugUrlType[i]
            createList.url = row1debugUrl[i]
            theimage.add(createList)
        }
        return theimage
    }
    //
    /**
     * prepare data for the second recyclerview (last recyclerview from above) using data from the history table
     *
     * @return theimage arraylist<Game2ArrayList> data for recyclerview
     * @see Game2ArrayList
     *
     * @see History
    </Game2ArrayList> */
    private fun prepareData2(): ArrayList<Game2ArrayList> {
//        var row1debugUrlNumber = 0
        //
        val results = realm.where(
            History::class.java
        ).equalTo(getString(R.string.phrase_number), (sharedLastPhraseNumber - 1).toString())
            .findAll()
        val count = results.size
        val row1debugUrlNumber = count - 1
        if (count != 0) {
            var irrh = 0
            while (irrh < count && irrh < 33) {
                val result = results[irrh]!!
                val wordNumber = result.wordNumber!!.toInt()
                //
                if (wordNumber != 0) {
                    if (preference_TitleWritingType == "uppercase") row1debugWord[irrh - 1] =
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
        val theimage = ArrayList<Game2ArrayList>()
        for (i in 0 until row1debugUrlNumber) {
            val createList = Game2ArrayList()
            createList.image_title = row1debugWord[i]
            createList.urlType = row1debugUrlType[i]
            createList.url = row1debugUrl[i]
            theimage.add(createList)
        }
        return theimage
    } //
}