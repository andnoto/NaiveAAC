package com.sampietro.NaiveAAC.activities.Graphics;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.sampietro.NaiveAAC.R;

import java.util.List;

import io.realm.Realm;

/**
 * this adapter is the View "supplier" for the listview in the UI for sounds copyright list.
 *
 * @version
 * @see Sounds
 * @see com.sampietro.NaiveAAC.activities.Info.InfoSoundsCopyrightListFragment
 * @see com.sampietro.NaiveAAC.activities.Info.InfoActivity
 */
public class SoundsCopyrightAdapter extends BaseAdapter
    {
        private List<Sounds> sounds=null;
        private Context context=null;
        private ListView listview=null;
        // Realm
        private Realm realm;
        /**
         * SoundsCopyrightAdapter constructor.
         * listener setting for settings activity callbacks ,  context annotation and other
         *
         * @param context context
         * @param sounds list<Sounds> reference to the data structure through the RealmResults class
         * @param listview listview reference to the listview in the UI for sounds copyright list
         */
        public SoundsCopyrightAdapter(Context context, List<Sounds> sounds, ListView listview)
        {
            this.sounds=sounds;
            this.context=context;
            this.listview=listview;
            //
            Activity activity = (Activity) context;
        }
        /**
         * return size of list<Sounds>
         *
         * @return int with size of list<Sounds>
         */
        @Override
        public int getCount()
        {
            return sounds.size();
        }
        /**
         * return the element of a specified index within the list<Sounds>
         *
         * @param position int index within the list<Sounds>
         * @return object with the element within the list<Sounds>
         */
        @Override
        public Object getItem(int position)
        {
            return sounds.get(position);
        }
        /**
         * return the hashCode of a specified index within the list<Sounds>
         *
         * @param position int index within the list<Sounds>
         * @return long with the the hashCode of the element within the list<Sounds>
         *
         */
        @Override
        public long getItemId(int position)
        {
            return getItem(position).hashCode();
        }
         /**
         * return the view with the row corresponding to a specific index within the list<Sounds>
         *
         * @param position int index within the list<Sounds>
         * @param v view to inflate with the row corresponding to a specific index within the list<Sounds>
         * @param vg viewgroup the parent that this view will eventually be attached to
         * @return view with the row corresponding to a specific index within the list<Sounds>
         * @see #getItem
         */
        @Override
        public View getView(int position, View v, ViewGroup vg)
        {
            // 1) it is checked if the View passed in input is null and only in this
            // case is initialized with the LayoutInflater.
            // The LayoutInflater structures the View to be created as defined in the
            // XML layout indicated.
            // 2) The data will be taken from the object with the element within
            // the list <Sounds> of position position retrieved using getItem.
            // 3) the listener for the delete button of the element within
            // the list <Sounds> of position position is set
            if (v==null)
            {
                v= LayoutInflater.from(context).inflate(R.layout.activity_copyright_row, null);
            }
            Sounds l=(Sounds) getItem(position);
            TextView txt=(TextView) v.findViewById(R.id.imageDescriptionRow);
            txt.setText(l.getCopyright());
            //
            return v;
        }
    }

