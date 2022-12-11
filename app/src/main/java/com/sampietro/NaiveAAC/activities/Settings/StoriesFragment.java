package com.sampietro.NaiveAAC.activities.Settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.sampietro.NaiveAAC.R;
import com.sampietro.NaiveAAC.activities.Settings.Utils.SettingsFragmentAbstractClass;

/**
 * <h1>StoriesFragment</h1>
 * <p><b>StoriesFragment</b> UI for stories settings
 * </p>
 *
 * @version     1.1, 04/22/22
 * @see com.sampietro.NaiveAAC.activities.Settings.Utils.SettingsFragmentAbstractClass
 * @see com.sampietro.NaiveAAC.activities.Settings.SettingsActivity
 */
public class StoriesFragment extends SettingsFragmentAbstractClass {
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
        //
        Bundle bundle = this.getArguments();
        //
        if (bundle != null) {
            EditText keywordstorytoadd=(EditText) rootView.findViewById(R.id.keywordstorytoadd);
            EditText phrasenumbertoadd=(EditText) rootView.findViewById(R.id.phrasenumbertoadd);
            EditText wordnumbertoadd=(EditText) rootView.findViewById(R.id.wordnumbertoadd);
            EditText wordtoadd=(EditText) rootView.findViewById(R.id.wordtoadd);
            EditText uritypetoadd=(EditText) rootView.findViewById(R.id.uritypetoadd);
            EditText uritoadd=(EditText) rootView.findViewById(R.id.uritoadd);
            keywordstorytoadd.setText(bundle.getString("Story"));
            phrasenumbertoadd.setText(Integer.toString(bundle.getInt("PhraseNumber")));
            wordnumbertoadd.setText(Integer.toString(bundle.getInt("WordNumber")));
            if (bundle.containsKey("Word"))
                wordtoadd.setText(bundle.getString("Word"));
            if (bundle.containsKey("UriType"))
                uritypetoadd.setText(bundle.getString("UriType"));
            if (bundle.containsKey("Uri"))
                uritoadd.setText(bundle.getString("Uri"));
        }
        //
        listener.receiveResultSettings(rootView);
        //
        return rootView;
    }
}

