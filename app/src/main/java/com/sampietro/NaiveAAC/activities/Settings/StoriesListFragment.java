package com.sampietro.NaiveAAC.activities.Settings;

import static io.realm.Sort.ASCENDING;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;

import com.sampietro.NaiveAAC.R;
import com.sampietro.NaiveAAC.activities.Settings.Utils.SettingsFragmentAbstractClass;
import com.sampietro.NaiveAAC.activities.Stories.Stories;
import com.sampietro.NaiveAAC.activities.Stories.StoriesAdapter;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 * <h1>StoriesListFragment</h1>
 * <p><b>StoriesListFragment</b> UI for stories list settings
 * </p>
 *
 * @version     1.1, 04/22/22
 * @see com.sampietro.NaiveAAC.activities.Settings.Utils.SettingsFragmentAbstractClass
 * @see com.sampietro.NaiveAAC.activities.Settings.SettingsActivity
 */
public class StoriesListFragment extends SettingsFragmentAbstractClass {
    private Realm realm;
    //
    private ListView listView=null;
    private StoriesAdapter adapter;
    //
    public EditText storyToSearch;
    public EditText phraseNumberToSearch;
    /**
     * prepares the ui also using a listview and makes the callback to the activity
     *
     * @see androidx.fragment.app.Fragment#onCreateView
     * @see com.sampietro.NaiveAAC.activities.Stories.Stories
     * @see com.sampietro.NaiveAAC.activities.Stories.StoriesAdapter
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        rootView = inflater.inflate(R.layout.activity_settings_stories_list, container, false);
        // logic of fragment
        realm= Realm.getDefaultInstance();
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
        RealmResults<Stories> results;
        storyToSearch = (EditText)rootView.findViewById(R.id.keywordstorytosearch);
        phraseNumberToSearch = (EditText)rootView.findViewById(R.id.phrasenumbertosearch);
        Bundle bundle = this.getArguments();
        if (bundle == null) {
            results = realm.where(Stories.class).findAll();
            }
            else
            {
                storyToSearch.setText(bundle.getString("keywordStoryToSearch"));
                phraseNumberToSearch.setText(Integer.toString(bundle.getInt("phraseNumberToSearch")));
                //
                if (bundle.getString("keywordStoryToSearch").equals(getString(R.string.nome_storia))) {
                    results = realm.where(Stories.class).findAll();
                    }
                    else
                    {
                    if (bundle.getInt("phraseNumberToSearch") == 0) {
                            results =
                                    realm.where(Stories.class)
                                            .equalTo("story", bundle.getString("keywordStoryToSearch").toLowerCase())
                                            .findAll();
                        }
                        else
                        {
                            results =
                                    realm.where(Stories.class)
                                            .equalTo("story", bundle.getString("keywordStoryToSearch").toLowerCase())
                                            .equalTo("phraseNumberInt", bundle.getInt("phraseNumberToSearch"))
                                            .findAll();
                        }
                    }
            }

        //
        String[] mStrings1 = {"story","phraseNumberInt", "wordNumberInt" };
        Sort[] mStrings2 = {ASCENDING,ASCENDING, ASCENDING };
        results = results.sort(mStrings1, mStrings2 );
        //
        listView=(ListView) rootView.findViewById(R.id.listview);
        //
        adapter=new StoriesAdapter(ctext, results, listView);
        //
        listView.setAdapter(adapter);
        //
        listener.receiveResultSettings(rootView);
        //
        return rootView;
    }
}

