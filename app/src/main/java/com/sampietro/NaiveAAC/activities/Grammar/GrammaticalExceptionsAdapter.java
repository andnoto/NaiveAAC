package com.sampietro.NaiveAAC.activities.Grammar;

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
import com.sampietro.NaiveAAC.activities.Settings.GrammaticalExceptionsFragment;
import com.sampietro.NaiveAAC.activities.Settings.SettingsActivity;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * this adapter is the View "supplier" for the listview in the UI for grammatical exceptions settings.
 *
 * @version     1.1, 04/22/22
 * @see GrammaticalExceptionsFragment
 * @see SettingsActivity
 */
public class GrammaticalExceptionsAdapter extends BaseAdapter
    {
        /**
         * <h1>GrammaticalExceptionsAdapterInterface</h1>
         * <p><b>GrammaticalExceptionsAdapterInterface</b>
         * interface used to refer to the Activity without having to explicitly use its class
         * <p>
         * this interface is initialized by the constructor.
         *
         * @see GrammaticalExceptionsAdapter (Context, GrammaticalExceptions , ListView)
         */
        public interface GrammaticalExceptionsAdapterInterface{
            public void reloadGrammaticalExceptionsFragment();
        }
        public GrammaticalExceptionsAdapterInterface listener;
        //
        private List<GrammaticalExceptions> grammaticalExceptions=null;
        private Context context=null;
        private ListView listview=null;
        // Realm
        private Realm realm;
        /**
         * GrammaticalExceptionsAdapter constructor.
         * listener setting for settings activity callbacks ,  context annotation and other
         *
         * @param context context
         * @param grammaticalExceptions list<GrammaticalExceptions> reference to the data structure through the RealmResults class
         * @param listview listview reference to the listview in the UI for grammatical exceptions settings
         */
        public GrammaticalExceptionsAdapter(Context context, List<GrammaticalExceptions> grammaticalExceptions, ListView listview)
        {
            this.grammaticalExceptions=grammaticalExceptions;
            this.context=context;
            this.listview=listview;
            //
            Activity activity = (Activity) context;
            listener=(GrammaticalExceptionsAdapterInterface) activity;
        }
        /**
         * return size of list<GrammaticalExceptions>
         *
         * @return int with size of list<GrammaticalExceptions>
         */
        @Override
        public int getCount()
        {
            return grammaticalExceptions.size();
        }
        /**
         * return the element of a specified index within the list<GrammaticalExceptions>
         *
         * @param position int index within the list<GrammaticalExceptions>
         * @return object with the element within the list<GrammaticalExceptions>
         */
        @Override
        public Object getItem(int position)
        {
            return grammaticalExceptions.get(position);
        }
        /**
         * return the hashCode of a specified index within the list<GrammaticalExceptions>
         *
         * @param position int index within the list<GrammaticalExceptions>
         * @return long with the the hashCode of the element within the list<GrammaticalExceptions>
         *
         */
        @Override
        public long getItemId(int position)
        {
            return getItem(position).hashCode();
        }
        /**
         * return the view with the row corresponding to a specific index within the list<GrammaticalExceptions>
         *
         * @param position int index within the list<GrammaticalExceptions>
         * @param v view to inflate with the row corresponding to a specific index within the list<GrammaticalExceptions>
         * @param vg viewgroup the parent that this view will eventually be attached to
         * @return view with the row corresponding to a specific index within the list<GrammaticalExceptions>
         * @see #getItem
         * @see #clickListenerDeleteGrammaticalExceptions
         */
        @Override
        public View getView(int position, View v, ViewGroup vg)
        {
            // 1) it is checked if the View passed in input is null and only in this
            // case is initialized with the LayoutInflater.
            // The LayoutInflater structures the View to be created as defined in the
            // XML layout indicated.
            // 2) The data will be taken from the object with the element within
            // the list <GrammaticalExceptions> of position position retrieved using getItem.
            // 3) the listener for the delete button of the element within
            // the list <GrammaticalExceptions> of position position is set
            if (v==null)
            {
                v= LayoutInflater.from(context).inflate(R.layout.activity_settings_row, null);
            }
            GrammaticalExceptions l=(GrammaticalExceptions) getItem(position);
            TextView txt=(TextView) v.findViewById(R.id.imageDescriptionRow);
            txt.setText(l.getKeyword()+ " " + l.getExceptionType());
            //
            ImageButton imgbtn=(ImageButton) v.findViewById(R.id.btn_delete_image);
            imgbtn.setOnClickListener(clickListenerDeleteGrammaticalExceptions);
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
         * @see GrammaticalExceptionsAdapterInterface#reloadGrammaticalExceptionsFragment
         */
        private View.OnClickListener clickListenerDeleteGrammaticalExceptions=new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                int position=listview.getPositionForView(v);
                // delete image
                realm= Realm.getDefaultInstance();
                RealmResults<GrammaticalExceptions> results = realm.where(GrammaticalExceptions.class).findAll();
                realm.beginTransaction();
                GrammaticalExceptions daCancellare=results.get(position);
                assert daCancellare != null;
                daCancellare.deleteFromRealm();
                realm.commitTransaction();
                //
                listener.reloadGrammaticalExceptionsFragment();
                //
            }
        };
    }

