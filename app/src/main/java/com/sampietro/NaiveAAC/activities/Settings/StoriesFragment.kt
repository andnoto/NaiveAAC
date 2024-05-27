package com.sampietro.NaiveAAC.activities.Settings

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import com.sampietro.NaiveAAC.R
import com.sampietro.NaiveAAC.activities.BaseAndAbstractClass.FragmentAbstractClass
import com.sampietro.NaiveAAC.activities.Graphics.GraphicsAndPrintingHelper
import com.sampietro.NaiveAAC.activities.Stories.VoiceToBeRecordedInStories
import com.sampietro.NaiveAAC.activities.Stories.VoiceToBeRecordedInStoriesViewModel
import java.util.*

/**
 * <h1>StoriesFragment</h1>
 *
 * **StoriesFragment** UI for stories settings
 *
 * Refer to [developer.android.com](https://developer.android.com/guide/fragments/communicate)
 *
 * @version     5.0, 01/04/2024
 * @see FragmentAbstractClass
 *
 * @see SettingsActivity
 */
class StoriesFragment(contentLayoutId: Int) : FragmentAbstractClass(contentLayoutId) {
    //
    private lateinit var viewModel: VoiceToBeRecordedInStoriesViewModel

    /**
     * prepares the ui
     *
     * @see androidx.fragment.app.Fragment.onViewCreated
     */
    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        // logic of fragment
        val keywordstorytoadd = view.findViewById<View>(R.id.keywordstorytoadd) as TextView
        val phrasenumbertoadd = view.findViewById<View>(R.id.phrasenumbertoadd) as TextView
        val wordnumbertoadd = view.findViewById<View>(R.id.wordnumbertoadd) as TextView
        val wordtoadd = view.findViewById<View>(R.id.wordtoadd) as EditText
        val gameimage = view.findViewById<View>(R.id.gameimage) as ImageView
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
                keywordstorytoadd.setText(voiceToBeRecordedInStories.story)
                phrasenumbertoadd.setText(String.format(Locale.getDefault(), "%d",voiceToBeRecordedInStories.phraseNumberInt))
                wordnumbertoadd.setText(String.format(Locale.getDefault(), "%d",voiceToBeRecordedInStories.wordNumberInt))
                wordtoadd.setText(voiceToBeRecordedInStories.word)
                if (voiceToBeRecordedInStories.uriType != "")
                {
                    GraphicsAndPrintingHelper.addImage(
                        voiceToBeRecordedInStories.uriType,
                        voiceToBeRecordedInStories.uri, gameimage,
                        200, 200
                    )
                }
            }
    }
}