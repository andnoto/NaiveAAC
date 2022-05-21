package com.example.NaiveAAC.activities.Arasaac;

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
 * this adapter is the View "supplier" for the listview in the UI for pictograms to modify settings.
 *
 * @version     1.1, 04/22/22
 * @see com.example.NaiveAAC.activities.Arasaac.PictogramsAllToModify
 * @see com.example.NaiveAAC.activities.Settings.PictogramsToModifyFragment
 * @see com.example.NaiveAAC.activities.Settings.SettingsActivity
 */
public class PictogramsAllToModifyAdapter extends BaseAdapter
    {
        /**
         * <h1>PictogramsAllToModifyAdapterInterface</h1>
         * <p><b>PictogramsAllToModifyAdapterInterface</b>
         * interface used to refer to the Activity without having to explicitly use its class
         * <p>
         * this interface is initialized by the constructor.
         *
         * @see PictogramsAllToModifyAdapter (Context, PictogramsAllToModify , ListView)
         */
        public interface PictogramsAllToModifyAdapterInterface{
            public void reloadPictogramsToModifyFragment();
        }
        public PictogramsAllToModifyAdapterInterface listener;
        //
        private List<PictogramsAllToModify> pictogramsAllToModify=null;
        private Context context=null;
        private ListView listview=null;
        // Realm
        private Realm realm;
        /**
         * PictogramsAllToModifyAdapter constructor.
         * listener setting for settings activity callbacks , context annotation and other
         *
         * @param context context
         * @param pictogramsAllToModify list<PictogramsAllToModify> reference to the data structure through the RealmResults class
         * @param listview listview reference to the listview in the UI for pictograms to modify settings
         */
        public PictogramsAllToModifyAdapter(Context context, List<PictogramsAllToModify> pictogramsAllToModify, ListView listview)
        {
            this.pictogramsAllToModify=pictogramsAllToModify;
            this.context=context;
            this.listview=listview;
            //
            Activity activity = (Activity) context;
            listener=(PictogramsAllToModifyAdapterInterface) activity;
            //
        }
        /**
         * return size of list<PictogramsAllToModify>
         *
         * @return int with size of list<PictogramsAllToModify>
         */
        @Override
        public int getCount()
        {
            return pictogramsAllToModify.size();
        }
        /**
         * return the element of a specified index within the list<PictogramsAllToModify>
         *
         * @param position int index within the list<PictogramsAllToModify>
         * @return object with the element within the list<PictogramsAllToModify>
         */
        @Override
        public Object getItem(int position)
        {
            return pictogramsAllToModify.get(position);
        }
        /**
         * return the hashCode of a specified index within the list<PictogramsAllToModify>
         *
         * @param position int index within the list<PictogramsAllToModify>
         * @return long with the the hashCode of the element within the list<PictogramsAllToModify>
         */
        @Override
        public long getItemId(int position)
        {
            return getItem(position).hashCode();
        }
        /**
         * return the view with the row corresponding to a specific index within the list<PictogramsAllToModify>
         *
         * @param position int index within the list<PictogramsAllToModify>
         * @param v view to inflate with the row corresponding to a specific index within the list<PictogramsAllToModify>
         * @param vg viewgroup the parent that this view will eventually be attached to
         * @return view with the row corresponding to a specific index within the list<PictogramsAllToModify>
         * @see #getItem
         * @see #clickListenerDeletePictogramsAllToModify
         */
        @Override
        public View getView(int position, View v, ViewGroup vg)
        {
            if (v==null)
            {
                v= LayoutInflater.from(context).inflate(R.layout.activity_settings_row, null);
            }
            PictogramsAllToModify l=(PictogramsAllToModify) getItem(position);
            TextView txt=(TextView) v.findViewById(R.id.imageDescriptionRow);
            txt.setText(l.get_id()+ " " + l.getModificationType());
            //
            ImageButton imgbtn=(ImageButton) v.findViewById(R.id.btn_delete_image);
            imgbtn.setOnClickListener(clickListenerDeletePictogramsAllToModify);
            //
            return v;
        }
        /**
         * listener for the delete button.
         * <p>
         * it deletes the selected element from the realm
         *
         * @see View.OnClickListener
         */
        private View.OnClickListener clickListenerDeletePictogramsAllToModify=new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                int position=listview.getPositionForView(v);
                // delete
                realm= Realm.getDefaultInstance();
                RealmResults<PictogramsAllToModify> results =
                        realm.where(PictogramsAllToModify.class).findAll();
                realm.beginTransaction();
                PictogramsAllToModify daCancellare=results.get(position);
                assert daCancellare != null;
                daCancellare.deleteFromRealm();
                realm.commitTransaction();
                //
                listener.reloadPictogramsToModifyFragment();
                //
            }
        };
    }

