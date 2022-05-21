package com.example.NaiveAAC.activities.Settings;

import android.app.Activity;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.NaiveAAC.R;
import com.example.NaiveAAC.activities.Graphics.Videos;
import com.example.NaiveAAC.activities.Graphics.VideosAdapter;
import com.example.NaiveAAC.activities.Settings.Utils.SettingsFragmentAbstractClass;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * <h1>VideosFragment</h1>
 * <p><b>VideosFragment</b> UI for videos settings
 * </p>
 *
 * @version     1.1, 04/22/22
 * @see com.example.NaiveAAC.activities.Settings.Utils.SettingsFragmentAbstractClass
 * @see SurfaceHolder
 * @see com.example.NaiveAAC.activities.Settings.SettingsActivity
 */
public class VideosFragment extends SettingsFragmentAbstractClass implements SurfaceHolder.Callback {
    public Activity activity;
    //
    private Realm realm;
    //
    public Uri uri;
    //
    private ListView listView=null;
    private VideosAdapter adapter;
    // for videos
    // the fragment implements the SurfaceHolder.Callback interface,
    // which requires the everride of three methods: surfaceCreated, surfaceChanged, surfaceDestroyed.
    // Their invocation will occur, respectively, the first time the surface comes created,
    // whenever it undergoes changes and when it is destroyed.
    // Within the surfaceCreated method, once the surface has been created, we can start the video.
    // Within the onCreateView method we assign a layout to the UI and make the SurfaceHolder use the fragment
    // as a listener for its own callback events.
    private MediaPlayer mediaPlayer;
    private SurfaceHolder holder;
    private SurfaceView surface;
    /**
     * implements the SurfaceHolder.Callback interface.
     * start the video.
     *
     * @see SurfaceHolder.Callback
     */
    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        // 1) a reference to the MediaPlayer is retrieved via create (),passing the movie uri as an argument.
        // 2) the display is assigned to the MediaPlayer indicating which will be the container of the video.
        // 3) we invoke the MediaPlayer # start () method within an OnPreparedListener class listener.
        // 4) within an OnCompletionListener type listener, we invoke the method MediaPlayer # release ().
        mediaPlayer= MediaPlayer.create(ctext,uri);
        mediaPlayer.setDisplay(surfaceHolder);
        mediaPlayer.setOnPreparedListener(
                new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mediaPlayer) {
                        mediaPlayer.start();
                    }
                }
        );
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                mediaPlayer.release();
            }
        });
    }
    /**
     * implements the SurfaceHolder.Callback interface.
     *
     * @see SurfaceHolder.Callback
     */
    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i2, int i3) {
    }
    /**
     * implements the SurfaceHolder.Callback interface.
     *
     * @see SurfaceHolder.Callback
     */
    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
    }
    /**
     * prepares the ui also using a listview and a surfaceview and makes the callback to the activity
     *
     * @see androidx.fragment.app.Fragment#onCreateView
     * @see com.example.NaiveAAC.activities.Graphics.Videos
     * @see com.example.NaiveAAC.activities.Graphics.VideosAdapter
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        rootView = inflater.inflate(R.layout.activity_settings_videos, container, false);
        // logic of fragment
        Bundle bundle = this.getArguments();
        String stringUri = null;
        if (bundle != null) {
            stringUri = bundle.getString("URI");
            if (!stringUri.equals("none")) {
                uri = Uri.parse(stringUri);
                //
                surface = (SurfaceView) rootView.findViewById(R.id.surfView);
                holder = surface.getHolder();
                holder.addCallback(this);
            }
        }
        //
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
        RealmResults<Videos> results = realm.where(Videos.class).findAll();
        //
        listView=(ListView) rootView.findViewById(R.id.listview);
        //
        adapter=new VideosAdapter(ctext, results, listView);
        //
        listView.setAdapter(adapter);
        //
        listener.receiveResultSettings(rootView);
        //
        return rootView;
    }
}

