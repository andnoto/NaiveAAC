package com.sampietro.NaiveAAC.activities.Settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.lifecycle.ViewModelProvider;

import com.sampietro.NaiveAAC.R;
import com.sampietro.NaiveAAC.activities.Settings.Utils.SettingsFragmentAbstractClass;
import com.sampietro.NaiveAAC.activities.Stories.VoiceToBeRecordedInStoriesViewModel;

/**
 * <h1>StoriesFragment</h1>
 * <p><b>StoriesFragment</b> UI for stories settings
 * </p>
 * Refer to <a href="https://developer.android.com/guide/fragments/communicate">developer.android.com</a>
 *
 * @version     3.0, 03/12/23
 * @see com.sampietro.NaiveAAC.activities.Settings.Utils.SettingsFragmentAbstractClass
 * @see com.sampietro.NaiveAAC.activities.Settings.SettingsActivity
 */
public class StoriesFragment extends SettingsFragmentAbstractClass {
    //
    private VoiceToBeRecordedInStoriesViewModel viewModel;
    /**
     * prepares the ui and makes the callback to the activity
     *
     * @see androidx.fragment.app.Fragment#onCreateView
     * @see com.sampietro.NaiveAAC.activities.Stories.Stories
     * @see com.sampietro.NaiveAAC.activities.Game.GameParameters.GameParametersAdapter
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        rootView = inflater.inflate(R.layout.activity_settings_stories, container, false);
        // logic of fragment
        EditText keywordstorytoadd=(EditText) rootView.findViewById(R.id.keywordstorytoadd);
        EditText phrasenumbertoadd=(EditText) rootView.findViewById(R.id.phrasenumbertoadd);
        EditText wordnumbertoadd=(EditText) rootView.findViewById(R.id.wordnumbertoadd);
        EditText wordtoadd=(EditText) rootView.findViewById(R.id.wordtoadd);
        /*
        Both your fragment and its host activity can retrieve a shared instance of a ViewModel with activity scope by passing the activity into the ViewModelProvider
        constructor.
        The ViewModelProvider handles instantiating the ViewModel or retrieving it if it already exists. Both components can observe and modify this data
         */
        viewModel = new ViewModelProvider((requireActivity())).get(VoiceToBeRecordedInStoriesViewModel.class);
        viewModel.getSelectedItem().observe(getViewLifecycleOwner(), voiceToBeRecordedInStories -> {
            // Perform an action with the latest item data
            keywordstorytoadd.setText(voiceToBeRecordedInStories.getStory());
            phrasenumbertoadd.setText(Integer.toString(voiceToBeRecordedInStories.getPhraseNumber()));
            wordnumbertoadd.setText(Integer.toString(voiceToBeRecordedInStories.getWordNumber()));
            wordtoadd.setText(voiceToBeRecordedInStories.getWord());
        });
        //
        listener.receiveResultSettings(rootView);
        //
        return rootView;
    }
}