package com.example.NaiveAAC.activities.Graphics;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.example.NaiveAAC.R;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * this adapter is the View "supplier" for the listview in the UI for videos settings.
 *
 * @version     1.1, 04/22/22
 * @see com.example.NaiveAAC.activities.Graphics.Videos
 * @see com.example.NaiveAAC.activities.Settings.VideosFragment
 * @see com.example.NaiveAAC.activities.Settings.SettingsActivity
 */
public class VideosAdapter extends BaseAdapter
    {
        /**
         * <h1>VideosAdapterInterface</h1>
         * <p><b>VideosAdapterInterface</b>
         * interface used to refer to the Activity without having to explicitly use its class
         * <p>
         * this interface is initialized by the constructor.
         *
         * @see VideosAdapter (Context, Videos , ListView)
         */
        public interface VideosAdapterInterface{
            public void reloadVideosFragment();
        }
        public VideosAdapterInterface listener;
        //
        private List<Videos> videos=null;
        private Context context=null;
        private ListView listview=null;
        // Realm
        private Realm realm;
        /**
         * VideosAdapter constructor.
         * listener setting for settings activity callbacks ,  context annotation and other
         *
         * @param context context
         * @param videos list<Videos> reference to the data structure through the RealmResults class
         * @param listview listview reference to the listview in the UI for videos settings
         */
        public VideosAdapter(Context context, List<Videos> videos, ListView listview)
        {
            this.videos=videos;
            this.context=context;
            this.listview=listview;
            //
            Activity activity = (Activity) context;
            listener=(VideosAdapterInterface) activity;
            //
        }
        /**
         * return size of list<Videos>
         *
         * @return int with size of list<Videos>
         */
        @Override
        public int getCount()
        {
            return videos.size();
        }
        /**
         * return the element of a specified index within the list<Videos>
         *
         * @param position int index within the list<Videos>
         * @return object with the element within the list<Videos>
         */
        @Override
        public Object getItem(int position)
        {
            return videos.get(position);
        }
        /**
         * return the hashCode of a specified index within the list<Videos>
         *
         * @param position int index within the list<Videos>
         * @return long with the the hashCode of the element within the list<Videos>
         */
        @Override
        public long getItemId(int position)
        {
            return getItem(position).hashCode();
        }
        /**
         * return the view with the row corresponding to a specific index within the list<Videos>
         *
         * @param position int index within the list<Videos>
         * @param v view to inflate with the row corresponding to a specific index within the list<Videos>
         * @param vg viewgroup the parent that this view will eventually be attached to
         * @return view with the row corresponding to a specific index within the list<Videos>
         * @see #getItem
         * @see #clickListenerDeleteVideo
         */
        @Override
        public View getView(int position, View v, ViewGroup vg)
        {
            // 1) it is checked if the View passed in input is null and only in this
            // case is initialized with the LayoutInflater.
            // The LayoutInflater structures the View to be created as defined in the
            // XML layout indicated.
            // 2) The data will be taken from the object with the element within
            // the list <Videos> of position position retrieved using getItem.
            // 3) the listener for the delete button of the element within
            // the list <Videos> of position position is set
            if (v==null)
            {
                v= LayoutInflater.from(context).inflate(R.layout.activity_settings_row, null);
            }
            Videos l=(Videos) getItem(position);
            TextView txt=(TextView) v.findViewById(R.id.imageDescriptionRow);
            txt.setText(l.getDescrizione());
            //
            ImageButton imgbtn=(ImageButton) v.findViewById(R.id.btn_delete_image);
            imgbtn.setOnClickListener(clickListenerDeleteVideo);
            //
            return v;
        }
        /**
         * listener for the delete button.
         * <p>
         * it deletes the selected element from the realm and
         * makes the callback to the activity to reload the fragment with the updated data
         *
         * @see View.OnClickListener
         * @see VideosAdapterInterface#reloadVideosFragment
         */
        private View.OnClickListener clickListenerDeleteVideo=new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                int position=listview.getPositionForView(v);
                // delete image
                realm= Realm.getDefaultInstance();
                RealmResults<Videos> results = realm.where(Videos.class).findAll();
                realm.beginTransaction();
                Videos daCancellare=results.get(position);
                assert daCancellare != null;
                daCancellare.deleteFromRealm();
                realm.commitTransaction();
                //
                listener.reloadVideosFragment();
                //
            }
        };
    }

