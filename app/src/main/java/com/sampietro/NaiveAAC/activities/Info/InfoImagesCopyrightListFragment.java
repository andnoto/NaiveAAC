package com.sampietro.NaiveAAC.activities.Info;

import static io.realm.Sort.ASCENDING;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.sampietro.NaiveAAC.R;
import com.sampietro.NaiveAAC.activities.Graphics.Images;
import com.sampietro.NaiveAAC.activities.Graphics.ImagesCopyrightAdapter;
import com.sampietro.NaiveAAC.activities.Info.Utils.InfoFragmentAbstractClass;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 * <h1>InfoImagesCopyrightListFragment</h1>
 * <p><b>InfoImagesCopyrightListFragment</b> UI for images copyright list
 * </p>
 *
 * @version
 * @see InfoFragmentAbstractClass
 * @see InfoActivity
 */
public class InfoImagesCopyrightListFragment extends InfoFragmentAbstractClass {
    private Realm realm;
    //
    private ListView listView=null;
    private ImagesCopyrightAdapter adapter;
    /**
     * prepares the ui also using a listview and makes the callback to the activity
     *
     * @see androidx.fragment.app.Fragment#onCreateView
     * @see Images
     * @see ImagesCopyrightAdapter
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        rootView = inflater.inflate(R.layout.activity_settings_copyright_list, container, false);
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
        realm= Realm.getDefaultInstance();
        RealmResults<Images> results;
        results = realm.where(Images.class).findAll();
        //
        String[] mStrings1 = {"copyright" };
        Sort[] mStrings2 = {ASCENDING };
        results = results.sort(mStrings1, mStrings2 );
        //
        listView=(ListView) rootView.findViewById(R.id.listview);
        //
        adapter=new ImagesCopyrightAdapter(ctext, results, listView);
        //
        listView.setAdapter(adapter);
        //
        return rootView;
    }
}