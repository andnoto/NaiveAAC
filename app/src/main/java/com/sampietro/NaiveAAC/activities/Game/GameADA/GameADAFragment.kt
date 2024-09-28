package com.sampietro.NaiveAAC.activities.Game.GameADA

import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.sampietro.NaiveAAC.R

class GameADAFragment() : GameADAFragmentAbstractClass() {
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
     * @see GameADARecyclerViewAdapter
     */
//    override fun onViewCreated(
//        view: View,
//        savedInstanceState: Bundle?
//    ) {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        rootView = inflater.inflate(R.layout.activity_game_ada_recycler_view, container, false)
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
        val adapter = GameADARecyclerViewAdapter(ctext, realm, createLists)
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
}