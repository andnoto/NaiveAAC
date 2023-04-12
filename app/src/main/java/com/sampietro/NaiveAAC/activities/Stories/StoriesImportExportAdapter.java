package com.sampietro.NaiveAAC.activities.Stories;

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
 * this adapter is the View "supplier" for the listview in the UI for stories import export.
 *
 * @version
 * @see Stories
 * @see com.sampietro.NaiveAAC.activities.Settings.StoriesImportExportFragment
 * @see com.sampietro.NaiveAAC.activities.Settings.SettingsStoriesImportExportActivity
 */
public class StoriesImportExportAdapter extends BaseAdapter
    {
        //
        private List<Stories> stories=null;
        private Context context=null;
        private ListView listview=null;
        // Realm
        private Realm realm;
        /**
         * StoriesAdapter constructor.
         * listener setting for settings activity callbacks ,  context annotation and other
         *
         * @param context context
         * @param stories list<Stories> reference to the data structure through the RealmResults class
         * @param listview listview reference to the listview in the UI for stories settings
         */
        public StoriesImportExportAdapter(Context context, List<Stories> stories, ListView listview)
        {
            this.stories=stories;
            this.context=context;
            this.listview=listview;
            //
            Activity activity = (Activity) context;
        }
        /**
         * return size of list<Stories>
         *
         * @return int with size of list<Stories>
         */
        @Override
        public int getCount()
        {
            return stories.size();
        }
        /**
         * return the element of a specified index within the list<Stories>
         *
         * @param position int index within the list<Stories>
         * @return object with the element within the list<Stories>
         */
        @Override
        public Object getItem(int position)
        {
            return stories.get(position);
        }
        /**
         * return the hashCode of a specified index within the list<Stories>
         *
         * @param position int index within the list<Stories>
         * @return long with the the hashCode of the element within the list<Stories>
         */
        @Override
        public long getItemId(int position)
        {
            return getItem(position).hashCode();
        }
         /**
         * return the view with the row corresponding to a specific index within the list<Stories>
         *
         * @param position int index within the list<Stories>
         * @param v view to inflate with the row corresponding to a specific index within the list<Stories>
         * @param vg viewgroup the parent that this view will eventually be attached to
         * @return view with the row corresponding to a specific index within the list<Stories>
         * @see #getItem
         *
         */
        @Override
        public View getView(int position, View v, ViewGroup vg)
        {
            // 1) it is checked if the View passed in input is null and only in this
            // case is initialized with the LayoutInflater.
            // The LayoutInflater structures the View to be created as defined in the
            // XML layout indicated.
            // 2) The data will be taken from the object with the element within
            // the list <Stories> of position position retrieved using getItem.
            // 3) the listener for the delete button of the element within
            // the list <Stories> of position position is set
            if (v==null)
            {
                v= LayoutInflater.from(context).inflate(R.layout.activity_settings_row_stories_import_export, null);
            }
            Stories l=(Stories) getItem(position);
            TextView txt=(TextView) v.findViewById(R.id.imageDescriptionRow);
            //
            txt.setText(l.getStory()+ "-" + l.getPhraseNumber()
                    + "-" + l.getWordNumber() + " " + l.getWord());
            //
            return v;
        }
    }