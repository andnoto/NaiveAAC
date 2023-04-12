package com.sampietro.NaiveAAC.activities.Settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.sampietro.NaiveAAC.R;
import com.sampietro.NaiveAAC.activities.Graphics.Sounds;
import com.sampietro.NaiveAAC.activities.Settings.Utils.SettingsFragmentAbstractClass;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * <h1>StoriesSoundsSearchFragment</h1>
 * <p><b>StoriesSoundsSearchFragment</b> UI for sounds settings
 * </p>
 *
 * @version     3.0, 03/12/23
 * @see SettingsFragmentAbstractClass
 * @see SettingsStoriesActivity
 */
public class StoriesSoundsSearchFragment extends SettingsFragmentAbstractClass {
    private Realm realm;
    //
    private ListView listView=null;
    private StoriesSoundsSearchAdapter adapter;
    /**
     * prepares the ui also using a listview and makes the callback to the activity
     *
     * @see androidx.fragment.app.Fragment#onCreateView
     * @see Sounds
     * @see StoriesSoundsSearchAdapter
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        rootView = inflater.inflate(R.layout.activity_settings_stories_sounds, container, false);
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
        RealmResults<Sounds> results = realm.where(Sounds.class).findAll();
        //
        listView=(ListView) rootView.findViewById(R.id.listview);
        //
        adapter=new StoriesSoundsSearchAdapter(ctext, results, listView);
        //
        listView.setAdapter(adapter);
        //
        listener.receiveResultSettings(rootView);
        //
        return rootView;
    }
}