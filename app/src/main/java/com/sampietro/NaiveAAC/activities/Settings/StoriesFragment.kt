package com.sampietro.NaiveAAC.activities.Settings

import com.sampietro.NaiveAAC.activities.Stories.VoiceToBeRecordedInStoriesViewModel
import android.os.Bundle
import android.view.View
import com.sampietro.NaiveAAC.R
import android.widget.EditText
import androidx.lifecycle.ViewModelProvider
import com.sampietro.NaiveAAC.activities.BaseAndAbstractClass.FragmentAbstractClass
import com.sampietro.NaiveAAC.activities.Stories.VoiceToBeRecordedInStories
import java.util.*

/**
 * <h1>StoriesFragment</h1>
 *
 * **StoriesFragment** UI for stories settings
 *
 * Refer to [developer.android.com](https://developer.android.com/guide/fragments/communicate)
 *
 * @version     5.0, 01/04/2024
 * @see com.sampietro.NaiveAAC.activities.Settings.Utils.SettingsFragmentAbstractClass
 *
 * @see com.sampietro.NaiveAAC.activities.Settings.SettingsActivity
 */
class StoriesFragment(contentLayoutId: Int) : FragmentAbstractClass(contentLayoutId) {
    //
    private lateinit var viewModel: VoiceToBeRecordedInStoriesViewModel

    /**
     * prepares the ui
     *
     * @see androidx.fragment.app.Fragment.onViewCreated
     *
     * @see com.sampietro.NaiveAAC.activities.Stories.Stories
     *
     * @see com.sampietro.NaiveAAC.activities.Game.GameParameters.GameParametersAdapter
     */
    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        // logic of fragment
        val keywordstorytoadd = view.findViewById<View>(R.id.keywordstorytoadd) as EditText
        val phrasenumbertoadd = view.findViewById<View>(R.id.phrasenumbertoadd) as EditText
        val wordnumbertoadd = view.findViewById<View>(R.id.wordnumbertoadd) as EditText
        val wordtoadd = view.findViewById<View>(R.id.wordtoadd) as EditText
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
            }
    }
}