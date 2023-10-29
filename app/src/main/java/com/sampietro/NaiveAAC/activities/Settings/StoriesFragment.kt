package com.sampietro.NaiveAAC.activities.Settings

import com.sampietro.NaiveAAC.activities.Settings.Utils.SettingsFragmentAbstractClass
import com.sampietro.NaiveAAC.activities.Stories.VoiceToBeRecordedInStoriesViewModel
import android.view.LayoutInflater
import android.view.ViewGroup
import android.os.Bundle
import android.view.View
import com.sampietro.NaiveAAC.R
import android.widget.EditText
import androidx.lifecycle.ViewModelProvider
import com.sampietro.NaiveAAC.activities.Stories.VoiceToBeRecordedInStories
import java.util.*

/**
 * <h1>StoriesFragment</h1>
 *
 * **StoriesFragment** UI for stories settings
 *
 * Refer to [developer.android.com](https://developer.android.com/guide/fragments/communicate)
 *
 * @version     4.0, 09/09/2023
 * @see com.sampietro.NaiveAAC.activities.Settings.Utils.SettingsFragmentAbstractClass
 *
 * @see com.sampietro.NaiveAAC.activities.Settings.SettingsActivity
 */
class StoriesFragment : SettingsFragmentAbstractClass() {
    //
    private var viewModel: VoiceToBeRecordedInStoriesViewModel? = null

    /**
     * prepares the ui and makes the callback to the activity
     *
     * @see androidx.fragment.app.Fragment.onCreateView
     *
     * @see com.sampietro.NaiveAAC.activities.Stories.Stories
     *
     * @see com.sampietro.NaiveAAC.activities.Game.GameParameters.GameParametersAdapter
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        rootView = inflater.inflate(R.layout.activity_settings_stories, container, false)
        // logic of fragment
        val keywordstorytoadd = rootView.findViewById<View>(R.id.keywordstorytoadd) as EditText
        val phrasenumbertoadd = rootView.findViewById<View>(R.id.phrasenumbertoadd) as EditText
        val wordnumbertoadd = rootView.findViewById<View>(R.id.wordnumbertoadd) as EditText
        val wordtoadd = rootView.findViewById<View>(R.id.wordtoadd) as EditText
        /*
        Both your fragment and its host activity can retrieve a shared instance of a ViewModel with activity scope by passing the activity into the ViewModelProvider
        constructor.
        The ViewModelProvider handles instantiating the ViewModel or retrieving it if it already exists. Both components can observe and modify this data
         */viewModel = ViewModelProvider(requireActivity()).get(
            VoiceToBeRecordedInStoriesViewModel::class.java
        )
        viewModel!!.getSelectedItem()
            .observe(viewLifecycleOwner) { voiceToBeRecordedInStories: VoiceToBeRecordedInStories ->
                // Perform an action with the latest item data
                keywordstorytoadd.setText(voiceToBeRecordedInStories.story)
//                phrasenumbertoadd.setText(Integer.toString(voiceToBeRecordedInStories.phraseNumberInt))
//                wordnumbertoadd.setText(Integer.toString(voiceToBeRecordedInStories.wordNumberInt))
                phrasenumbertoadd.setText(String.format(Locale.getDefault(), "%d",voiceToBeRecordedInStories.phraseNumberInt))
                wordnumbertoadd.setText(String.format(Locale.getDefault(), "%d",voiceToBeRecordedInStories.wordNumberInt))
                wordtoadd.setText(voiceToBeRecordedInStories.word)
            }
        //
        listener.receiveResultSettings(rootView)
        //
        return rootView
    }
}