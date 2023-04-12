package com.sampietro.NaiveAAC.activities.Settings;

import static io.realm.Sort.ASCENDING;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;

import androidx.lifecycle.ViewModelProvider;

import com.sampietro.NaiveAAC.R;
import com.sampietro.NaiveAAC.activities.Settings.Utils.SettingsFragmentAbstractClass;
import com.sampietro.NaiveAAC.activities.Stories.Stories;
import com.sampietro.NaiveAAC.activities.Stories.StoriesAdapter;
import com.sampietro.NaiveAAC.activities.Stories.StoriesImportExportAdapter;
import com.sampietro.NaiveAAC.activities.Stories.VoiceToBeRecordedInStoriesViewModel;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 * <h1>StoriesImportExportFragment</h1>
 * <p><b>StoriesImportExportFragment</b> UI for stories import/export
 * </p>
 * Refer to <a href="https://developer.android.com/guide/fragments/communicate">developer.android.com</a>
 *
 * @version     3.0, 03/12/23
 * @see SettingsFragmentAbstractClass
 * @see SettingsStoriesImportExportActivity
 */
public class StoriesImportExportFragment extends SettingsFragmentAbstractClass {
    private Realm realm;
    //
    private ListView listView=null;
    private StoriesImportExportAdapter adapter;
    //
    public EditText storyToSearch;
    public EditText phraseNumberToSearch;
    //
    private VoiceToBeRecordedInStoriesViewModel viewModel;
    /**
     * prepares the ui also using a listview and makes the callback to the activity
     *
     * @see androidx.fragment.app.Fragment#onCreateView
     * @see Stories
     * @see StoriesImportExportAdapter
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        rootView = inflater.inflate(R.layout.activity_settings_stories_import_export, container, false);
        // logic of fragment
        // ListView
        // 1) we get a reference to the data structure through the RealmResults class which constitutes
        // the query result set and is an iterable collection accessible with Java constructs:
        // for loop, basic access to position and Iterator.
        // The approach for realm queries is object-oriented.
        // The where method will retrieve the objects from the specified class and the result will
        // be treated with filtrate by other specific methods.
        // At the end of the selection configuration, the findAll method will be invoked to retrieve
        // all the corresponding results.
        // 2) we instantiate an Adapter by assigning it, the collection and the layout related to
        // each single row
        // 3) we retrieve the ListView prepared in the layout and assign it the reference to the adapter
        // which will be your View "supplier".
        //
        storyToSearch = (EditText)rootView.findViewById(R.id.storytosearch);
        /*
        Both your fragment and its host activity can retrieve a shared instance of a ViewModel with activity scope by passing the activity into the ViewModelProvider
        constructor.
        The ViewModelProvider handles instantiating the ViewModel or retrieving it if it already exists. Both components can observe and modify this data
         */
        viewModel = new ViewModelProvider((requireActivity())).get(VoiceToBeRecordedInStoriesViewModel.class);
        viewModel.getSelectedItem().observe(getViewLifecycleOwner(), voiceToBeRecordedInStories -> {
            // Perform an action with the latest item data
            realm= Realm.getDefaultInstance();
            RealmResults<Stories> results;
            storyToSearch.setText(voiceToBeRecordedInStories.getStory());
            //
            if (voiceToBeRecordedInStories.getStory().equals(getString(R.string.nome_storia))) {
                results = realm.where(Stories.class).findAll();
            }
            else
            {
                results =
                        realm.where(Stories.class)
                                .equalTo("story", voiceToBeRecordedInStories.getStory().toLowerCase())
                                .findAll();
            }
            //
            String[] mStrings1 = {"story","phraseNumberInt", "wordNumberInt" };
            Sort[] mStrings2 = {ASCENDING,ASCENDING, ASCENDING };
            results = results.sort(mStrings1, mStrings2 );
            //
            listView=(ListView) rootView.findViewById(R.id.import_export_stories_listview);
            //
            adapter=new StoriesImportExportAdapter(ctext, results, listView);
            //
            listView.setAdapter(adapter);
        });
        //
        listener.receiveResultSettings(rootView);
        //
        return rootView;
    }
}