package com.sampietro.NaiveAAC.activities.Game.Game2

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sampietro.NaiveAAC.R

/**
 * <h1>SettingsStoriesQuickRegistrationFragment</h1>
 *
 * **SettingsStoriesQuickRegistrationFragment** UI for game2
 *
 *
 * @version     5.0, 01/04/2024
 * @see Game2FragmentAbstractClass
 *
 * @see Game2Activity
 */
class SettingsStoriesRegistrationFragment(@LayoutRes contentLayoutId : Int = 0) : Game2FragmentAbstractClass(contentLayoutId) {
    var keywordStoryToAdd: String? = ""
    var phraseNumberToAdd: String? = ""
   /**
     * prepares the ui
     *
     *
     * Refer to [androidauthority](https://www.androidauthority.com/how-to-build-an-image-gallery-app-718976/)
     * by [Adam Sinicki](https://www.androidauthority.com/author/adamsinicki/)
     *
     * @see androidx.fragment.app.Fragment.onViewCreated
     *
     * @see prepareData
     *
     * @see Game2RecyclerViewAdapter
     */
    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        hearingImageButton = view.findViewById<View>(R.id.btn_start) as ImageButton
        hearingImageButton.setImageResource(R.drawable.ic_baseline_hearing_36_red) //set the image programmatically
        //
        sentenceToAdd = view.findViewById<View>(R.id.sentencetoadd) as EditText
        //
        val bundle = this.arguments
        sharedLastPhraseNumber = 0
        if (bundle != null) {
            sharedLastPhraseNumber = bundle.getInt(getString(R.string.last_phrase_number))
            keywordStoryToAdd = bundle.getString(getString(R.string.keywordstorytoadd))
            phraseNumberToAdd = bundle.getString(getString(R.string.phrasenumbertoadd))
            sentenceToAdd.setText(bundle.getString(getString(R.string.etext)))
            //
            val keywordstorytoadd = view.findViewById<View>(R.id.keywordstorytoadd) as TextView?
            val phrasenumbertoadd = view.findViewById<View>(R.id.phrasenumbertoadd) as TextView?
            if (keywordstorytoadd != null) {
                keywordstorytoadd.setText(keywordStoryToAdd)
            }
            if (phrasenumbertoadd != null) {
                phrasenumbertoadd.setText(phraseNumberToAdd)
            }
            //
        }
        //
        if (sentenceToAdd.text.toString() != "") {
            val recyclerView1 = view.findViewById<View>(R.id.imagegallery1) as RecyclerView
            recyclerView1.setHasFixedSize(true)
            val layoutManager1: RecyclerView.LayoutManager =
                LinearLayoutManager(ctext, LinearLayoutManager.HORIZONTAL, false)
            recyclerView1.layoutManager = layoutManager1
            val createLists1 = prepareData(sharedLastPhraseNumber)
            val adapter1 = Game2RecyclerViewAdapter(ctext, createLists1)
            recyclerView1.adapter = adapter1
            //
        }
    }
}