package com.example.NaiveAAC.activities.Settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.NaiveAAC.R;
import com.example.NaiveAAC.activities.Arasaac.PictogramsAllToModify;
import com.example.NaiveAAC.activities.Arasaac.PictogramsAllToModifyAdapter;
import com.example.NaiveAAC.activities.Settings.Utils.SettingsFragmentAbstractClass;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * <h1>PictogramsToModifyFragment</h1>
 * <p><b>PictogramsToModifyFragment</b> UI for pictograms to modify settings
 * </p>
 *
 * @version     1.1, 04/22/22
 * @see com.example.NaiveAAC.activities.Settings.Utils.SettingsFragmentAbstractClass
 * @see com.example.NaiveAAC.activities.Settings.SettingsActivity
 */
public class PictogramsToModifyFragment extends SettingsFragmentAbstractClass {
    //
    private Realm realm;

    //
    private ListView listView=null;
    private PictogramsAllToModifyAdapter adapter;
    /**
     * prepares the ui also using a listview and makes the callback to the activity
     *
     * @see androidx.fragment.app.Fragment#onCreateView
     * @see com.example.NaiveAAC.activities.Arasaac.PictogramsAllToModify
     * @see com.example.NaiveAAC.activities.Arasaac.PictogramsAllToModifyAdapter
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        rootView = inflater.inflate(R.layout.activity_settings_pictograms_to_modify, container, false);
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
        RealmResults<PictogramsAllToModify> results = realm.where(PictogramsAllToModify.class).findAll();
        //
        listView=(ListView) rootView.findViewById(R.id.listview);
        //
        adapter=new PictogramsAllToModifyAdapter(ctext, results, listView);
        //
        listView.setAdapter(adapter);
        //
        listener.receiveResultSettings(rootView);
        //
        return rootView;
    }
}

