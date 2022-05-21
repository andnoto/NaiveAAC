package com.example.NaiveAAC.activities.Settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.NaiveAAC.R;
import com.example.NaiveAAC.activities.Graphics.Images;
import com.example.NaiveAAC.activities.Graphics.ImagesAdapter;
import com.example.NaiveAAC.activities.Settings.Utils.SettingsFragmentAbstractClass;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * <h1>ImagesFragment</h1>
 * <p><b>ImagesFragment</b> UI for images settings
 * </p>
 *
 * @version     1.1, 04/22/22
 * @see com.example.NaiveAAC.activities.Settings.Utils.SettingsFragmentAbstractClass
 * @see com.example.NaiveAAC.activities.Settings.SettingsActivity
 */
public class ImagesFragment extends SettingsFragmentAbstractClass {
    //
    private Realm realm;
    //
    private ListView listView=null;
    private ImagesAdapter adapter;
    /**
     * prepares the ui also using a listview and makes the callback to the activity
     *
     * @see androidx.fragment.app.Fragment#onCreateView
     * @see com.example.NaiveAAC.activities.Graphics.Images
     * @see com.example.NaiveAAC.activities.Graphics.ImagesAdapter
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        rootView = inflater.inflate(R.layout.activity_settings_images, container, false);
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
        RealmResults<Images> results = realm.where(Images.class).findAll();
        //
        listView=(ListView) rootView.findViewById(R.id.listview);
        //
        adapter=new ImagesAdapter(ctext, results, listView);
        //
        listView.setAdapter(adapter);
        //
        listener.receiveResultSettings(rootView);
        //
        return rootView;
    }
}

