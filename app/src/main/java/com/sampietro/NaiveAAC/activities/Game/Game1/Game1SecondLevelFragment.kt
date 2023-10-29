package com.sampietro.NaiveAAC.activities.Game.Game1

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sampietro.NaiveAAC.R
import com.sampietro.NaiveAAC.activities.Game.Utils.GameFragmentAbstractClass
import com.sampietro.NaiveAAC.activities.history.History
import java.util.*

/**
 * <h1>Game1SecondLevelFragment</h1>
 *
 * **Game1SecondLevelFragment** UI for game1
 * display second level menu
 * the search for words takes place via a two-level menu
 * the first level contains the main research classes such as games, food, family, animals
 * the second level contains the subclasses of the first level for example
 * for the game class subclasses ball, tablet, running, etc.
 *
 *
 * @version     4.0, 09/09/2023
 * @see GameFragmentAbstractClass
 *
 * @see Game1Activity
 */
class Game1SecondLevelFragment : GameFragmentAbstractClass() {
    var hearingImageButton: ImageButton? = null

    //
    var listenAgainButton: ImageButton? = null
    var continueGameButton: ImageButton? = null

    //
    var leftColumnMenuPhraseNumber = 0
    var middleColumnMenuPhraseNumber = 0
    var rightColumnMenuPhraseNumber = 0
    var leftColumnContent: String? = null
    var leftColumnContentUrlType: String? = null
    var leftColumnContentUrl: String? = null
    var middleColumnContent: String? = null
    var middleColumnContentUrlType: String? = null
    var middleColumnContentUrl: String? = null
    var rightColumnContent: String? = null
    var rightColumnContentUrlType: String? = null
    var rightColumnContentUrl: String? = null

    //
    var column1debugWord = arrayOfNulls<String>(32)
    var column1debugUrlType = arrayOfNulls<String>(32)
    var column1debugUrl = arrayOfNulls<String>(32)

    //
    var column2debugWord = arrayOfNulls<String>(32)
    var column2debugUrlType = arrayOfNulls<String>(32)
    var column2debugUrl = arrayOfNulls<String>(32)

    //
    var column3debugWord = arrayOfNulls<String>(32)
    var column3debugUrlType = arrayOfNulls<String>(32)
    var column3debugUrl = arrayOfNulls<String>(32)

    //
    /**
     * prepares the ui and makes the callback to the activity
     *
     *
     * Refer to [androidauthority](https://www.androidauthority.com/how-to-build-an-image-gallery-app-718976/)
     * by [Adam Sinicki](https://www.androidauthority.com/author/adamsinicki/)
     *
     * @see androidx.fragment.app.Fragment.onCreateView
     *
     * @see Game1ArrayList
     *
     * @see prepareData1
     *
     * @see Game1RecyclerViewAdapter1
     *
     * @see prepareData2
     *
     * @see Game1RecyclerViewAdapter2
     *
     * @see prepareData3
     *
     * @see Game1RecyclerViewAdapter3
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        rootView = inflater.inflate(R.layout.activity_game_1, container, false)
        //
        hearingImageButton = rootView.findViewById<View>(R.id.btn_start) as ImageButton
        //
        listenAgainButton = rootView.findViewById<View>(R.id.listenagainbutton) as ImageButton
        continueGameButton = rootView.findViewById<View>(R.id.continuegamebutton) as ImageButton
        hearingImageButton!!.setImageResource(R.drawable.ic_baseline_hearing_36_red)
        //
        val bundle = this.arguments
        //
        if (bundle != null) {
            leftColumnMenuPhraseNumber =
                bundle.getInt(getString(R.string.left_column_menu_phrase_number))
            middleColumnMenuPhraseNumber =
                bundle.getInt(getString(R.string.middle_column_menu_phrase_number))
            rightColumnMenuPhraseNumber =
                bundle.getInt(getString(R.string.right_column_menu_phrase_number))
            leftColumnContent = bundle.getString(getString(R.string.left_column_content))
            leftColumnContentUrlType =
                bundle.getString(getString(R.string.left_column_content_url_type))
            leftColumnContentUrl = bundle.getString(getString(R.string.left_column_content_url))
            middleColumnContent = bundle.getString(getString(R.string.middle_column_content))
            middleColumnContentUrlType =
                bundle.getString(getString(R.string.middle_column_content_url_type))
            middleColumnContentUrl = bundle.getString(getString(R.string.middle_column_content_url))
            rightColumnContent = bundle.getString(getString(R.string.right_column_content))
            rightColumnContentUrlType =
                bundle.getString(getString(R.string.right_column_content_url_type))
            rightColumnContentUrl = bundle.getString(getString(R.string.right_column_content_url))
        }
        //
        if (leftColumnContent != getString(R.string.nessuno)
            && middleColumnContent != getString(R.string.nessuno)
            && rightColumnContent != getString(R.string.nessuno)
        ) {
            hearingImageButton!!.visibility = View.VISIBLE
            listenAgainButton!!.visibility = View.VISIBLE
            continueGameButton!!.visibility = View.VISIBLE
        } else {
            hearingImageButton!!.visibility = View.INVISIBLE
            listenAgainButton!!.visibility = View.INVISIBLE
            continueGameButton!!.visibility = View.INVISIBLE
        }
        //
        val recyclerView1 = rootView.findViewById<View>(R.id.imagegallery1) as RecyclerView
        recyclerView1.setHasFixedSize(true)
        val layoutManager1: RecyclerView.LayoutManager = LinearLayoutManager(ctext)
        recyclerView1.layoutManager = layoutManager1
        val createLists1 = prepareData1()
        val adapter1 = Game1RecyclerViewAdapter1(ctext, createLists1)
        recyclerView1.adapter = adapter1
        //
        val recyclerView2 = rootView.findViewById<View>(R.id.imagegallery2) as RecyclerView
        recyclerView2.setHasFixedSize(true)
        val layoutManager2: RecyclerView.LayoutManager = LinearLayoutManager(ctext)
        recyclerView2.layoutManager = layoutManager2
        val createLists2 = prepareData2()
        val adapter2 = Game1RecyclerViewAdapter2(ctext, createLists2)
        recyclerView2.adapter = adapter2
        //
        val recyclerView3 = rootView.findViewById<View>(R.id.imagegallery3) as RecyclerView
        recyclerView3.setHasFixedSize(true)
        val layoutManager3: RecyclerView.LayoutManager = LinearLayoutManager(ctext)
        recyclerView3.layoutManager = layoutManager3
        val createLists3 = prepareData3()
        val adapter3 = Game1RecyclerViewAdapter3(ctext, createLists3)
        recyclerView3.adapter = adapter3
        //
        listener.receiveResultGameFragment(rootView)
        return rootView
    }

    /**
     * prepare data for the first recyclerview (left recyclerview) using data from the history table
     *
     * @return theimage arraylist<GameFragmentChoiseOfGameArrayList> data for recyclerview
     * @see Game1ArrayList
     *
     * @see History
    </GameFragmentChoiseOfGameArrayList> */
    private fun prepareData1(): ArrayList<Game1ArrayList> {
        var column1debugUrlNumber = 0
        //
        if (leftColumnContent != getString(R.string.nessuno) && leftColumnContent != " ") {
            column1debugUrlNumber = 1
            if (preference_TitleWritingType == "uppercase") column1debugWord[0] =
                leftColumnContent!!.uppercase(
                    Locale.getDefault()
                ) else column1debugWord[0] = leftColumnContent!!.lowercase(Locale.getDefault())
            //
            column1debugUrlType[0] = leftColumnContentUrlType
            column1debugUrl[0] = leftColumnContentUrl
        } else {
            //
            if (leftColumnMenuPhraseNumber != 0) {
                val results = realm.where(
                    History::class.java
                )
                    .equalTo(
                        getString(R.string.phrase_number),
                        leftColumnMenuPhraseNumber.toString()
                    ).findAll()
                val count = results.size
                column1debugUrlNumber = count - 1
                if (count != 0) {
                    var irrh = 0
                    while (irrh < count) {
                        val result = results[irrh]!!
                        val wordNumber = result.wordNumber!!.toInt()
                        //
                        if (wordNumber != 0) {
                            if (preference_TitleWritingType == "uppercase") column1debugWord[irrh - 1] =
                                result.word!!.uppercase(
                                    Locale.getDefault()
                                ) else column1debugWord[irrh - 1] = result.word!!.lowercase(
                                Locale.getDefault()
                            )
                            //
                            column1debugUrlType[irrh - 1] = result.uriType
                            column1debugUrl[irrh - 1] = result.uri
                        }
                        irrh++
                    }
                }
            }
        }
        //
        val theimage = ArrayList<Game1ArrayList>()
        for (i in 0 until column1debugUrlNumber) {
            val createList = Game1ArrayList()
            createList.image_title = column1debugWord[i]
            createList.urlType = column1debugUrlType[i]
            createList.url = column1debugUrl[i]
            theimage.add(createList)
        }
        return theimage
    }

    /**
     * prepare data for the second recyclerview (middle recyclerview) using data from the history table
     *
     * @return theimage arraylist<GameFragmentChoiseOfGameArrayList> data for recyclerview
     * @see Game1ArrayList
     *
     * @see History
    </GameFragmentChoiseOfGameArrayList> */
    private fun prepareData2(): ArrayList<Game1ArrayList> {
        var column2debugUrlNumber = 0
        //
        if (middleColumnContent != getString(R.string.nessuno)) {
            column2debugUrlNumber = 1
            if (preference_TitleWritingType == "uppercase") column2debugWord[0] =
                middleColumnContent!!.uppercase(
                    Locale.getDefault()
                ) else column2debugWord[0] = middleColumnContent!!.lowercase(
                Locale.getDefault()
            )
            //
            column2debugUrlType[0] = middleColumnContentUrlType
            column2debugUrl[0] = middleColumnContentUrl
        } else {
            //
            if (middleColumnMenuPhraseNumber != 0) {
                val results = realm.where(
                    History::class.java
                )
                    .equalTo(
                        getString(R.string.phrase_number),
                        middleColumnMenuPhraseNumber.toString()
                    ).findAll()
                val count = results.size
                column2debugUrlNumber = count - 1
                if (count != 0) {
                    var irrh = 0
                    while (irrh < count) {
                        val result = results[irrh]!!
                        val wordNumber = result.wordNumber!!.toInt()
                        //
                        if (wordNumber != 0) {
                            if (preference_TitleWritingType == "uppercase") column2debugWord[irrh - 1] =
                                result.word!!.uppercase(
                                    Locale.getDefault()
                                ) else column2debugWord[irrh - 1] = result.word!!.lowercase(
                                Locale.getDefault()
                            )
                            //
                            column2debugUrlType[irrh - 1] = result.uriType
                            column2debugUrl[irrh - 1] = result.uri
                        }
                        irrh++
                    }
                }
            }
        }
        //
        val theimage = ArrayList<Game1ArrayList>()
        for (i in 0 until column2debugUrlNumber) {
            val createList = Game1ArrayList()
            createList.image_title = column2debugWord[i]
            createList.urlType = column2debugUrlType[i]
            createList.url = column2debugUrl[i]
            theimage.add(createList)
        }
        return theimage
    }

    /**
     * prepare data for the third recyclerview (right recyclerview) using data from the history table
     *
     * @return theimage arraylist<GameFragmentChoiseOfGameArrayList> data for recyclerview
     * @see Game1ArrayList
     *
     * @see History
    </GameFragmentChoiseOfGameArrayList> */
    private fun prepareData3(): ArrayList<Game1ArrayList> {
        var column3debugUrlNumber = 0
        //
        if (rightColumnContent != getString(R.string.nessuno) && rightColumnContent != " ") {
            column3debugUrlNumber = 1
            if (preference_TitleWritingType == "uppercase") column3debugWord[0] =
                rightColumnContent!!.uppercase(
                    Locale.getDefault()
                ) else column3debugWord[0] = rightColumnContent!!.lowercase(
                Locale.getDefault()
            )
            //
            column3debugUrlType[0] = rightColumnContentUrlType
            column3debugUrl[0] = rightColumnContentUrl
        } else {
            //
            if (rightColumnMenuPhraseNumber != 0) {
                val results = realm.where(
                    History::class.java
                )
                    .equalTo(
                        getString(R.string.phrase_number),
                        rightColumnMenuPhraseNumber.toString()
                    ).findAll()
                val count = results.size
                column3debugUrlNumber = count - 1
                if (count != 0) {
                    var irrh = 0
                    while (irrh < count) {
                        val result = results[irrh]!!
                        val wordNumber = result.wordNumber!!.toInt()
                        //
                        if (wordNumber != 0) {
                            if (preference_TitleWritingType == "uppercase") column3debugWord[irrh - 1] =
                                result.word!!.uppercase(
                                    Locale.getDefault()
                                ) else column3debugWord[irrh - 1] = result.word!!.lowercase(
                                Locale.getDefault()
                            )
                            //
                            column3debugUrlType[irrh - 1] = result.uriType
                            column3debugUrl[irrh - 1] = result.uri
                        }
                        irrh++
                    }
                }
            }
        }
        //
        val theimage = ArrayList<Game1ArrayList>()
        for (i in 0 until column3debugUrlNumber) {
            val createList = Game1ArrayList()
            createList.image_title = column3debugWord[i]
            createList.urlType = column3debugUrlType[i]
            createList.url = column3debugUrl[i]
            theimage.add(createList)
        }
        return theimage
    }
}