package com.sampietro.NaiveAAC.activities.Graphics;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.sampietro.NaiveAAC.R;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * this adapter is the View "supplier" for the listview in the UI for sounds settings.
 *
 * @version     3.0, 03/12/23
 * @see Sounds
 * @see com.sampietro.NaiveAAC.activities.Settings.SoundsFragment
 * @see com.sampietro.NaiveAAC.activities.Settings.SettingsActivity
 */
public class SoundsAdapter extends BaseAdapter
    {
        /**
         * <h1>SoundsAdapterAdapterInterface</h1>
         * <p><b>SoundsAdapterAdapterInterface</b>
         * interface used to refer to the Activity without having to explicitly use its class
         * <p>
         * this interface is initialized by the constructor.
         *
         * @see SoundsAdapter (Context, Sounds , ListView)
         */
        public interface SoundsAdapterInterface{
                public void reloadSoundsFragment();
            }
        public SoundsAdapterInterface listener;
        //
        private List<Sounds> sounds=null;
        private Context context=null;
        private ListView listview=null;
        // Realm
        private Realm realm;
        /**
         * SoundsAdapter constructor.
         * listener setting for settings activity callbacks ,  context annotation and other
         *
         * @param context context
         * @param sounds list<Sounds> reference to the data structure through the RealmResults class
         * @param listview listview reference to the listview in the UI for sounds settings
         */
        public SoundsAdapter(Context context, List<Sounds> sounds, ListView listview)
        {
            this.sounds=sounds;
            this.context=context;
            this.listview=listview;
            //
            Activity activity = (Activity) context;
            listener=(SoundsAdapterInterface) activity;
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
         * @see #clickListenerDeleteSounds
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
                v= LayoutInflater.from(context).inflate(R.layout.activity_settings_row, null);
            }
            Sounds l=(Sounds) getItem(position);
            TextView txt=(TextView) v.findViewById(R.id.imageDescriptionRow);
            txt.setText(l.getDescrizione());
            //
            ImageButton imgbtn=(ImageButton) v.findViewById(R.id.btn_delete_image);
            imgbtn.setOnClickListener(clickListenerDeleteSounds);
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
         * @see SoundsAdapterInterface#reloadSoundsFragment
         */
        private View.OnClickListener clickListenerDeleteSounds=new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                int position=listview.getPositionForView(v);
                // delete Sounds
                realm= Realm.getDefaultInstance();
                RealmResults<Sounds> results = realm.where(Sounds.class).findAll();
                realm.beginTransaction();
                Sounds daCancellare=results.get(position);
                assert daCancellare != null;
                daCancellare.deleteFromRealm();
                realm.commitTransaction();
                //
                listener.reloadSoundsFragment();
            }
        };
    }

