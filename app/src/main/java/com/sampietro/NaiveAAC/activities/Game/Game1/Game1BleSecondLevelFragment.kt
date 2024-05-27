package com.sampietro.NaiveAAC.activities.Game.Game1

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sampietro.NaiveAAC.R
import com.sampietro.NaiveAAC.activities.BaseAndAbstractClass.GameFragmentAbstractClass
import com.sampietro.NaiveAAC.activities.Grammar.ListsOfNames
import com.sampietro.NaiveAAC.activities.Graphics.GraphicsAndPrintingHelper.addImage
import com.sampietro.NaiveAAC.activities.Graphics.GraphicsAndPrintingHelper.addImageUsingPicasso
import com.sampietro.NaiveAAC.activities.Graphics.ImageSearchHelper.imageSearch
import com.sampietro.NaiveAAC.activities.Graphics.ResponseImageSearch
import com.sampietro.NaiveAAC.activities.history.History
import io.realm.Realm
import java.util.Locale

/**
 * <h1>Game1BleSecondLevelFragment</h1>
 *
 * **Game1BleSecondLevelFragment** UI for game1
 * display second level menu
 * the search for words takes place via a two-level menu
 * the first level contains the main research classes such as games, food, family, animals
 * the second level contains the subclasses of the first level for example
 * for the game class subclasses ball, tablet, running, etc.
 *
 *
 * @version     5.0, 01/04/2024
 * @see GameFragmentAbstractClass
 *
 * @see Game1BleActivity
 */
class Game1BleSecondLevelFragment(@LayoutRes contentLayoutId : Int = 0) : GameFragmentAbstractClass(contentLayoutId) {
    lateinit var listenAgainButton: ImageButton
    lateinit var continueGameButton: ImageButton
//
    lateinit var sendToBlueToothImage: ImageView
    //
    var leftColumnMenuPhraseNumber = 0
    var middleColumnMenuPhraseNumber = 0
    var rightColumnMenuPhraseNumber = 0
    var leftColumnContent: String? = null
    var middleColumnContent: String? = null
    var rightColumnContent: String? = null
    var theSentenceIsCompleted: Boolean = false
    //
    var deviceEnabledUserName = "non trovato"
    var deviceEnabledName = "non trovato"
    //
    var column1debugWord = arrayOfNulls<String>(32)
    var column1debugUrlType = arrayOfNulls<String>(32)
    var column1debugUrl = arrayOfNulls<String>(32)

    /**
     * <h1>onFragmentEventListenerGame1BleSecondLevelFragment</h1>
     *
     * **onFragmentEventListenerGame1BleSecondLevelFragment**
     * interface used to refer to the Activity without having to explicitly use its class
     *
     *
     *
     * @see onAttach
     */
    interface onFragmentEventListenerGame1BleSecondLevelFragment {
        // insert here any references to the Activity
        fun scanRequestBluetoothDevicesFromGame1BleSecondLevelFragment()
    }

    /**
     * listener setting for game activities callbacks , context annotation, realm get default instance
     *
     * @see androidx.fragment.app.Fragment.onAttach
     */
    private lateinit var listenerGame1SecondLevelFragment: onFragmentEventListenerGame1BleSecondLevelFragment
    override fun onAttach(context: Context) {
        super.onAttach(context)
        val activity = context as Activity
        listenerGame1SecondLevelFragment = activity as onFragmentEventListenerGame1BleSecondLevelFragment
        //
        ctext = context
        //
        realm = Realm.getDefaultInstance()
    }
    //
    /**
     * prepares the ui
     *
     *
     * Refer to [androidauthority](https://www.androidauthority.com/how-to-build-an-image-gallery-app-718976/)
     * by [Adam Sinicki](https://www.androidauthority.com/author/adamsinicki/)
     *
     * @see androidx.fragment.app.Fragment.onViewCreated
     *
     * @see Game1ArrayList
     *
     * @see prepareData
     *
     * @see Game1RecyclerViewAdapter1
     *
     * @see Game1RecyclerViewAdapter2
     *
     * @see Game1RecyclerViewAdapter3
     */
    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
//        rootView = inflater.inflate(R.layout.activity_game_1_ble, container, false)
        //
        listenAgainButton = view.findViewById<View>(R.id.listenagainbutton) as ImageButton
        continueGameButton = view.findViewById<View>(R.id.continuegamebutton) as ImageButton
        //
        sendToBlueToothImage = view.findViewById<View>(R.id.sendtobluetoothimage) as ImageView
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
            middleColumnContent = bundle.getString(getString(R.string.middle_column_content))
            rightColumnContent = bundle.getString(getString(R.string.right_column_content))
            //
            theSentenceIsCompleted = bundle.getBoolean("THE SENTENCE IS COMPLETED")
            //
            deviceEnabledName = bundle.getString("DEVICE ENABLED NAME")!!
            deviceEnabledUserName = bundle.getString("DEVICE ENABLED USER NAME")!!
        }
        //
        if (theSentenceIsCompleted) {
            listenAgainButton.visibility = View.VISIBLE
            continueGameButton.visibility = View.VISIBLE
        } else {
            listenAgainButton.visibility = View.INVISIBLE
            continueGameButton.visibility = View.INVISIBLE
        }
        //
        if (theSentenceIsCompleted
            && deviceEnabledName == "non trovato")
            {
            listenerGame1SecondLevelFragment.scanRequestBluetoothDevicesFromGame1BleSecondLevelFragment()
            }
        //
        if (deviceEnabledName != "non trovato")
        {
            sendToBlueToothImage.visibility = View.VISIBLE
            //
            if (deviceEnabledUserName != "non trovato")
            {
                val image: ResponseImageSearch?
                image = imageSearch(ctext, realm, deviceEnabledUserName)
                addImage(image!!.uriType, image.uriToSearch, sendToBlueToothImage, 150, 150)
            }
            else {
                val assetsUrl = "file:///android_asset/" + "images/puntointerrogativo.png"
                addImageUsingPicasso(assetsUrl, sendToBlueToothImage, 150, 150)
            }
        }
         else {
            sendToBlueToothImage.visibility = View.INVISIBLE
        }
        //
        val recyclerView1 = view.findViewById<View>(R.id.imagegallery1) as RecyclerView
        recyclerView1.setHasFixedSize(true)
        val layoutManager1: RecyclerView.LayoutManager = LinearLayoutManager(ctext)
        recyclerView1.layoutManager = layoutManager1
        val createLists1 = prepareData(leftColumnMenuPhraseNumber )
        val adapter1 = Game1RecyclerViewAdapter1(ctext, createLists1)
        recyclerView1.adapter = adapter1
        //
        val recyclerView2 = view.findViewById<View>(R.id.imagegallery2) as RecyclerView
        recyclerView2.setHasFixedSize(true)
        val layoutManager2: RecyclerView.LayoutManager = LinearLayoutManager(ctext)
        recyclerView2.layoutManager = layoutManager2
        val createLists2 = prepareData(middleColumnMenuPhraseNumber )
        val adapter2 = Game1RecyclerViewAdapter2(ctext, createLists2)
        recyclerView2.adapter = adapter2
        //
        val recyclerView3 = view.findViewById<View>(R.id.imagegallery3) as RecyclerView
        recyclerView3.setHasFixedSize(true)
        val layoutManager3: RecyclerView.LayoutManager = LinearLayoutManager(ctext)
        recyclerView3.layoutManager = layoutManager3
        val createLists3 = prepareData(rightColumnMenuPhraseNumber )
        val adapter3 = Game1RecyclerViewAdapter3(ctext, createLists3)
        recyclerView3.adapter = adapter3
        //
//        listener.receiveResultGameFragment(rootView)
//        return rootView
    }

    /**
     * prepare data for the recyclerview using data from the history table
     *
     * @return theimage arraylist<GameFragmentChoiseOfGameArrayList> data for recyclerview
     * @see Game1ArrayList
     *
     * @see History
    */
    private fun prepareData(
        columnMenuPhraseNumber: Int
    ): ArrayList<Game1ArrayList> {
        var column1debugUrlNumber = 0
        //
        if (columnMenuPhraseNumber != 0) {
            val results = realm.where(
                History::class.java
            )
                .equalTo(
                    getString(R.string.phrase_number),
                    columnMenuPhraseNumber.toString()
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
                        if (preference_TitleWritingType == getString(R.string.uppercase)) column1debugWord[irrh - 1] =
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
        //
        val theimage = ArrayList<Game1ArrayList>()
        for (i in 0 until column1debugUrlNumber) {
            val createList = Game1ArrayList()
            createList.image_title = column1debugWord[i]
            createList.urlType = column1debugUrlType[i]
            createList.url = column1debugUrl[i]
            createList.theTitleRepresentsAClass = false
            if (column1debugWord.size > 1) {
                //
                val wordToSearch = column1debugWord[i]!!.lowercase(Locale.getDefault())
                if (ListsOfNames.checksWhetherWordRepresentsAClass(ctext,
                        wordToSearch,
                        realm ))
                    {
                        createList.theTitleRepresentsAClass = true
                    }
            }
            theimage.add(createList)
        }
        return theimage
    }
}