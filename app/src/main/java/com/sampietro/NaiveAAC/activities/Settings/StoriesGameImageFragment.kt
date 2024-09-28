package com.sampietro.NaiveAAC.activities.Settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import com.sampietro.NaiveAAC.R
import com.sampietro.NaiveAAC.activities.BaseAndAbstractClass.FragmentAbstractClassWithoutConstructor
import com.sampietro.NaiveAAC.activities.Graphics.GraphicsAndPrintingHelper
import com.sampietro.NaiveAAC.activities.Stories.VoiceToBeRecordedInStories
import com.sampietro.NaiveAAC.activities.Stories.VoiceToBeRecordedInStoriesViewModel

/**
 * <h1>StoriesFragment</h1>
 *
 * **StoriesFragment** UI for stories settings
 *
 * Refer to [developer.android.com](https://developer.android.com/guide/fragments/communicate)
 *
 * @version     5.0, 01/04/2024
 * @see FragmentAbstractClassWithoutConstructor
 *
 * @see SettingsActivity
 */
class StoriesGameImageFragment() : FragmentAbstractClassWithoutConstructor() {
    //
    private lateinit var viewModel: VoiceToBeRecordedInStoriesViewModel
    /**
     * prepares the ui
     *
     * @see androidx.fragment.app.Fragment.onCreateView
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
        rootView = inflater.inflate(R.layout.activity_settings_stories_game_image, container, false)
        // logic of fragment
        val keywordstorytoadd = rootView.findViewById<View>(R.id.keywordstorytoadd) as TextView
        val gameimage = rootView.findViewById<View>(R.id.gameimage) as ImageView
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
                if (voiceToBeRecordedInStories.uriType != "")
                {
                    GraphicsAndPrintingHelper.addImage(
                        voiceToBeRecordedInStories.uriType,
                        voiceToBeRecordedInStories.uri, gameimage,
                        200, 200
                    )
                }
            }
        //
        return rootView
    }
}