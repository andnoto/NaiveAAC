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
 * <h1>SettingsStoriesQuickRegistrationFragment</h1>
 *
 * **SettingsStoriesQuickRegistrationFragment** UI for game2
 *
 *
 * @version     4.0, 09/09/2023
 * @see GameFragmentAbstractClass
 * @see Game2FragmentAbstractClass
 *
 * @see Game2Activity
 */
class SettingsStoriesQuickRegistrationFragment : Game2FragmentAbstractClass() {
    var keywordStoryToAdd: String? = ""
    var phraseNumberToAdd: String? = ""
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
        rootView = inflater.inflate(
            R.layout.activity_settings_stories_quick_registration_recycler_view,
            container,
            false
        )
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
            keywordStoryToAdd = bundle.getString(getString(R.string.keywordstorytoadd))
            phraseNumberToAdd = bundle.getString(getString(R.string.phrasenumbertoadd))
            sentenceToAdd.setText(bundle.getString(getString(R.string.etext)))
            //
            val keywordstorytoadd = rootView.findViewById<View>(R.id.keywordstorytoadd) as EditText
            val phrasenumbertoadd = rootView.findViewById<View>(R.id.phrasenumbertoadd) as EditText
            keywordstorytoadd.setText(keywordStoryToAdd)
            phrasenumbertoadd.setText(phraseNumberToAdd)
            //
        }
        //
        if (sentenceToAdd.text.toString() != "") {
            val recyclerView1 = rootView.findViewById<View>(R.id.imagegallery1) as RecyclerView
            recyclerView1.setHasFixedSize(true)
            val layoutManager1: RecyclerView.LayoutManager =
                LinearLayoutManager(ctext, LinearLayoutManager.HORIZONTAL, false)
            recyclerView1.layoutManager = layoutManager1
            val createLists1 = prepareData(sharedLastPhraseNumber)
            val adapter1 = Game2RecyclerViewAdapter(ctext, createLists1)
            recyclerView1.adapter = adapter1
            //
        }
        //
        listener.receiveResultGameFragment(rootView)
        return rootView
    }
}