package com.sampietro.NaiveAAC.activities.Game.Game2

import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.sampietro.NaiveAAC.R
import com.sampietro.NaiveAAC.activities.Game.GameADA.GameADAArrayList
import com.sampietro.NaiveAAC.activities.Game.GameADA.GameADAFragmentAbstractClass
import com.sampietro.NaiveAAC.activities.Game.GameADA.GameADARecyclerView
import com.sampietro.NaiveAAC.activities.Game.GameADA.SettingsStoriesImprovementRecyclerViewAdapter
import com.sampietro.NaiveAAC.activities.Stories.Stories
import com.sampietro.NaiveAAC.activities.history.History
import java.util.*

class Game2BleGameADAFragment() : GameADAFragmentAbstractClass() {
    /**
     * prepares the ui and makes the callback to the activity
     *
     *
     * Refer to [androidauthority](https://www.androidauthority.com/how-to-build-an-image-gallery-app-718976/)
     * by [Adam Sinicki](https://www.androidauthority.com/author/adamsinicki/)
     *
     * @see androidx.fragment.app.Fragment.onCreateView
     *
     * @see prepareData1
     *
     * @see SettingsStoriesImprovementRecyclerViewAdapter
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        rootView = inflater.inflate(R.layout.activity_game_2_ble_improvement, container, false)
        val bundle = this.arguments
        sharedLastPhraseNumber = 0
        if (bundle != null) {
            sharedLastPhraseNumber = bundle.getInt("LAST PHRASE NUMBER")
            wordToDisplayIndex = bundle.getInt("WORD TO DISPLAY INDEX")
            ttsEnabled = bundle.getBoolean("TTS ENABLED")
            gameUseVideoAndSound = bundle.getString("GAME USE VIDEO AND SOUND")
            //
            keywordStoryToAdd = bundle.getString(getString(R.string.keywordstorytoadd),"")
            phraseNumberToAdd = bundle.getString(getString(R.string.phrasenumbertoadd),"")
            //
        }
        //
        /*
        ADAPTED FOR VIDEO AND SOUND
         */
        recyclerView =
            rootView.findViewById<View>(R.id.game_ada_recycler_view) as GameADARecyclerView
        recyclerView!!.setHasFixedSize(true)
        val layoutManager = LinearLayoutManager(ctext, LinearLayoutManager.HORIZONTAL, false)
        recyclerView!!.layoutManager = layoutManager
        val createLists = prepareData1()
        recyclerView!!.setMediaObjectsGameADA(createLists)
        recyclerView!!.setGameUseVideoAndSound(gameUseVideoAndSound)
        val adapter = SettingsStoriesImprovementRecyclerViewAdapter(ctext, realm, createLists)
        recyclerView!!.adapter = adapter
        //
        recyclerView!!.smoothScrollBy(1, 0)
        //
        if (wordToDisplayIndex != 0) recyclerView!!.layoutManager!!.scrollToPosition(
            wordToDisplayIndex
        )
        /*

         */
        // TTS
        if (ttsEnabled) {
            tTS1 = TextToSpeech(ctext) { status ->
                if (status != TextToSpeech.ERROR) {
                    if (!tTS1!!.isSpeaking) {
                        tTS1!!.speak(sentence, TextToSpeech.QUEUE_FLUSH, null, null)
                    } else {
                        Toast.makeText(ctext, "is speaking", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(ctext, status, Toast.LENGTH_SHORT).show()
                }
            }
        }
        //
        listenerADA!!.receiveResultGameFragment(view, tTS1, createLists)
        //
        return rootView
    }
    /**
     * prepare data for the recyclerview using data from the history table
     *
     * @return theimage arraylist<GameADAArrayList> data for recyclerview
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