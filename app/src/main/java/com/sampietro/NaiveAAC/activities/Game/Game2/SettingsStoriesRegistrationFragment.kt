package com.sampietro.NaiveAAC.activities.Game.Game2

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sampietro.NaiveAAC.R
import com.sampietro.NaiveAAC.activities.Stories.VoiceToBeRecordedInStories
import com.sampietro.NaiveAAC.activities.Stories.VoiceToBeRecordedInStoriesViewModel
import java.util.Locale

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
class SettingsStoriesRegistrationFragment() : Game2FragmentAbstractClass() {
    //
    private lateinit var viewModel: VoiceToBeRecordedInStoriesViewModel
    //
//    var keywordStoryToAdd: String? = ""
//    var phraseNumberToAdd: String? = ""
   /**
     * prepares the ui
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
//    override fun onViewCreated(
//        view: View,
//        savedInstanceState: Bundle?
//    ) {
   override fun onCreateView(
       inflater: LayoutInflater,
       container: ViewGroup?,
       savedInstanceState: Bundle?
   ): View {
       rootView = inflater.inflate(R.layout.activity_settings_stories_registration_recycler_view, container, false)
       //
        hearingImageButton = rootView.findViewById<View>(R.id.btn_start) as ImageButton
        hearingImageButton.setImageResource(R.drawable.ic_baseline_hearing_36_red) //set the image programmatically
        //
        sentenceToAdd = rootView.findViewById<View>(R.id.sentencetoadd) as EditText
        //
       /*
        Both your fragment and its host activity can retrieve a shared instance of a ViewModel with activity scope by passing the activity into the ViewModelProvider
        constructor.
        The ViewModelProvider handles instantiating the ViewModel or retrieving it if it already exists. Both components can observe and modify this data
        */
       viewModel = ViewModelProvider(requireActivity()).get(
           VoiceToBeRecordedInStoriesViewModel::class.java
       )
       viewModel.getSelectedItem()
           .observe(viewLifecycleOwner) { voiceToBeRecordedInStories: VoiceToBeRecordedInStories ->
               // Perform an action with the latest item data
               val keywordstorytoadd = rootView.findViewById<View>(R.id.keywordstorytoadd) as TextView?
               val phrasenumbertoadd = rootView.findViewById<View>(R.id.phrasenumbertoadd) as TextView?
               if (keywordstorytoadd != null) {
                   keywordstorytoadd.setText(voiceToBeRecordedInStories.story)
               }
               if (phrasenumbertoadd != null) {
                   phrasenumbertoadd.setText(String.format(Locale.getDefault(), "%d",voiceToBeRecordedInStories.phraseNumberInt))
               }
           }
        val bundle = this.arguments
        sharedLastPhraseNumber = 0
        if (bundle != null) {
            sharedLastPhraseNumber = bundle.getInt(getString(R.string.last_phrase_number))
//            keywordStoryToAdd = bundle.getString(getString(R.string.keywordstorytoadd))
//            phraseNumberToAdd = bundle.getString(getString(R.string.phrasenumbertoadd))
            sentenceToAdd.setText(bundle.getString(getString(R.string.etext)))
            //
        }
        //
        if (sentenceToAdd.text.toString() != "") {
            val recyclerView1 = rootView.findViewById<View>(R.id.imagegallery1) as RecyclerView
//            recyclerView1.setHasFixedSize(true)
            val layoutManager1: RecyclerView.LayoutManager =
                LinearLayoutManager(ctext, LinearLayoutManager.HORIZONTAL, false)
            recyclerView1.layoutManager = layoutManager1
            val createLists1 = prepareData(sharedLastPhraseNumber)
            val adapter1 = Game2RecyclerViewAdapter(ctext, createLists1)
            recyclerView1.adapter = adapter1
            //
        }
       //
       return rootView
    }
}