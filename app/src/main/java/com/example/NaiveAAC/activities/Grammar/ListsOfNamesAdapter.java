package com.example.NaiveAAC.activities.Grammar;

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
 * this adapter is the View "supplier" for the listview in the UI for lists of names settings.
 *
 * @version     1.1, 04/22/22
 * @see com.example.NaiveAAC.activities.Grammar.ListsOfNames
 * @see com.example.NaiveAAC.activities.Settings.ListsOfNamesFragment
 * @see com.example.NaiveAAC.activities.Settings.SettingsActivity
 */
public class ListsOfNamesAdapter extends BaseAdapter
    {
        /**
         * <h1>ListsOfNamesAdapterInterface</h1>
         * <p><b>ListsOfNamesAdapterInterface</b>
         * interface used to refer to the Activity without having to explicitly use its class
         * <p>
         * this interface is initialized by the constructor.
         *
         * @see ListsOfNamesAdapter (Context, ListsOfNames , ListView)
         */
        public interface ListsOfNamesAdapterInterface{
            public void reloadListOfNamesFragment();
        }
        public ListsOfNamesAdapterInterface listener;
        //
        private List<ListsOfNames> listsOfNames=null;
        private Context context=null;
        private ListView listview=null;
        // Realm
        private Realm realm;
        /**
         * ListsOfNamesAdapter constructor.
         * listener setting for settings activity callbacks ,  context annotation and other
         *
         * @param context context
         * @param listsOfNames list<ListsOfNames> reference to the data structure through the RealmResults class
         * @param listview listview reference to the listview in the UI for lists of names settings
         */
        public ListsOfNamesAdapter(Context context, List<ListsOfNames> listsOfNames, ListView listview)
        {
            this.listsOfNames=listsOfNames;
            this.context=context;
            this.listview=listview;
            //
            Activity activity = (Activity) context;
            listener=(ListsOfNamesAdapterInterface) activity;
            //
        }
        /**
         * return size of list<ListsOfNames>
         *
         * @return int with size of list<ListsOfNames>
         */
        @Override
        public int getCount()
        {
            return listsOfNames.size();
        }
        /**
         * return the element of a specified index within the list<ListsOfNames>
         *
         * @param position int index within the list<ListsOfNames>
         * @return object with the element within the list<ListsOfNames>
         */
        @Override
        public Object getItem(int position)
        {
            return listsOfNames.get(position);
        }
        /**
         * return the hashCode of a specified index within the list<ListsOfNames>
         *
         * @param position int index within the list<ListsOfNames>
         * @return long with the the hashCode of the element within the list<ListsOfNames>
         *
         */
        @Override
        public long getItemId(int position)
        {
            return getItem(position).hashCode();
        }
        /**
         * return the view with the row corresponding to a specific index within the list<GameParameters>
         *
         * @param position int index within the list<ListsOfNames>
         * @param v view to inflate with the row corresponding to a specific index within the list<ListsOfNames>
         * @param vg viewgroup the parent that this view will eventually be attached to
         * @return view with the row corresponding to a specific index within the list<ListsOfNames>
         * @see #getItem
         * @see #clickListenerDeleteListsOfNames
         */
        @Override
        public View getView(int position, View v, ViewGroup vg)
        {
            // 1) it is checked if the View passed in input is null and only in this
            // case is initialized with the LayoutInflater.
            // The LayoutInflater structures the View to be created as defined in the
            // XML layout indicated.
            // 2) The data will be taken from the object with the element within
            // the list <ListsOfNames> of position position retrieved using getItem.
            // 3) the listener for the delete button of the element within
            // the list <ListsOfNames> of position position is set
            if (v==null)
            {
                v= LayoutInflater.from(context).inflate(R.layout.activity_settings_row, null);
            }
            ListsOfNames l=(ListsOfNames) getItem(position);
            TextView txt=(TextView) v.findViewById(R.id.imageDescriptionRow);
            txt.setText(l.getKeyword()+ " " + l.getWord());
            //
            ImageButton imgbtn=(ImageButton) v.findViewById(R.id.btn_delete_image);
            imgbtn.setOnClickListener(clickListenerDeleteListsOfNames);
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
         * @see ListsOfNamesAdapterInterface#reloadListOfNamesFragment
         */
        private View.OnClickListener clickListenerDeleteListsOfNames=new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                int position=listview.getPositionForView(v);
                // delete image
                realm= Realm.getDefaultInstance();
                RealmResults<ListsOfNames> results = realm.where(ListsOfNames.class).findAll();
                realm.beginTransaction();
                ListsOfNames daCancellare=results.get(position);
                daCancellare.deleteFromRealm();
                realm.commitTransaction();
                //
                listener.reloadListOfNamesFragment();
                //
            }
        };
    }

