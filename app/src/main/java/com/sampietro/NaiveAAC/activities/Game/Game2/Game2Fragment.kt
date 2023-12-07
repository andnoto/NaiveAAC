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

/**
 * <h1>Game2Fragment</h1>
 *
 * **GameFragment** UI for game2
 *
 *
 * @version     4.0, 09/09/2023
 * @see GameFragmentAbstractClass
 * @see Game2FragmentAbstractClass
 *
 * @see Game2Activity
 */
class Game2Fragment : Game2FragmentAbstractClass() {
    /**
     * prepares the ui and makes the callback to the activity
     *
     *
     * Refer to [androidauthority](https://www.androidauthority.com/how-to-build-an-image-gallery-app-718976/)
     * by [Adam Sinicki](https://www.androidauthority.com/author/adamsinicki/)
     *
     * @see androidx.fragment.app.Fragment.onCreateView
     *
     * @see prepareData
     *
     * @see Game2RecyclerViewAdapter
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        rootView = inflater.inflate(R.layout.activity_game_2, container, false)
        //
        hearingImageButton = rootView.findViewById<View>(R.id.btn_start) as ImageButton
        hearingImageButton.setImageResource(R.drawable.ic_baseline_hearing_36_red) //set the image programmatically
        //
        sentenceToAdd = rootView.findViewById<View>(R.id.sentencetoadd) as EditText
        //
        val bundle = this.arguments
        sharedLastPhraseNumber = 0
        if (bundle != null) {
            sharedLastPhraseNumber = bundle.getInt(getString(R.string.last_phrase_number))
            sentenceToAdd.setText(bundle.getString(getString(R.string.etext)))
        }
        //
        val recyclerView1 = rootView.findViewById<View>(R.id.imagegallery1) as RecyclerView
        recyclerView1.setHasFixedSize(true)
        val layoutManager1: RecyclerView.LayoutManager =
            LinearLayoutManager(ctext, LinearLayoutManager.HORIZONTAL, false)
        recyclerView1.layoutManager = layoutManager1
        val createLists1 = prepareData(sharedLastPhraseNumber)
        val adapter1 = Game2RecyclerViewAdapter(ctext, createLists1)
        recyclerView1.adapter = adapter1
        //
        val recyclerView2 = rootView.findViewById<View>(R.id.imagegallery2) as RecyclerView
        recyclerView2.setHasFixedSize(true)
        val layoutManager2: RecyclerView.LayoutManager =
            LinearLayoutManager(ctext, LinearLayoutManager.HORIZONTAL, false)
        recyclerView2.layoutManager = layoutManager2
        val createLists2 = prepareData(sharedLastPhraseNumber - 1)
        val adapter2 = Game2RecyclerViewAdapter(ctext, createLists2)
        recyclerView2.adapter = adapter2
        //
        listener.receiveResultGameFragment(rootView)
        return rootView
    }
}