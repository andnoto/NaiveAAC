package com.sampietro.NaiveAAC.activities.Stories;

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

/**
 * this adapter is the View "supplier" for the listview in the UI for stories list settings.
 *
 * @version     1.1, 04/22/22
 * @see com.sampietro.NaiveAAC.activities.Stories.Stories
 * @see com.sampietro.NaiveAAC.activities.Settings.StoriesFragment
 * @see com.sampietro.NaiveAAC.activities.Settings.SettingsActivity
 */
public class StoriesAdapter extends BaseAdapter
    {
        /**
         * <h1>StoriesAdapterInterface</h1>
         * <p><b>StoriesAdapterInterface</b>
         * interface used to refer to the Activity without having to explicitly use its class
         * <p>
         * this interface is initialized by the constructor.
         *
         * @see StoriesAdapter (Context, Stories , ListView)
         */
        public interface StoriesAdapterInterface{
            public void reloadStoriesFragmentDeleteStories(int position);
            public void reloadStoriesFragmentForInsertion(int position);
            public void reloadStoriesFragmentForEditing(int position);
        }
        public StoriesAdapterInterface listener;
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
        public StoriesAdapter(Context context, List<Stories> stories, ListView listview)
        {
            this.stories=stories;
            this.context=context;
            this.listview=listview;
            //
            Activity activity = (Activity) context;
            listener=(StoriesAdapterInterface) activity;
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
         * @see #clickListenerDeleteStories
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
                v= LayoutInflater.from(context).inflate(R.layout.activity_settings_row_stories, null);
            }
            Stories l=(Stories) getItem(position);
            TextView txt=(TextView) v.findViewById(R.id.imageDescriptionRow);
            //
            // if ((l.getStory().equals("prova")) && (l.getPhraseNumber().equals("4")) && (l.getWordNumber().equals("1")))
            // {
            //    Log.v("TAGSTORY","record finded");
            // }
            //
            txt.setText(l.getStory()+ "-" + l.getPhraseNumber()
                    + "-" + l.getWordNumber() + " " + l.getWord());
            //
            ImageButton imgbtn=(ImageButton) v.findViewById(R.id.btn_delete_image);
            imgbtn.setOnClickListener(clickListenerDeleteStories);
            //
            ImageButton imgbtnedit=(ImageButton) v.findViewById(R.id.btn_edit_image);
            imgbtnedit.setOnClickListener(clickListenerEditStories);
            //
            ImageButton imgbtninsert=(ImageButton) v.findViewById(R.id.btn_insert_image);
            imgbtninsert.setOnClickListener(clickListenerInsertStories);
            //
            return v;
        }
        /**
         * listener for the insert button.
         * <p>
         * enables inserting before the selected item and
         * makes the callback to the activity to load the fragment for insertion
         *
         * @see View.OnClickListener
         * @see StoriesAdapterInterface#reloadStoriesFragmentForInsertion
         */
        private View.OnClickListener clickListenerInsertStories=new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                int position=listview.getPositionForView(v);
                //
                listener.reloadStoriesFragmentForInsertion(position);
                //
            }
        };
        /**
         * listener for the edit button.
         * <p>
         * enable editing of the selected item and
         * makes the callback to the activity to reload the fragment for editing
         *
         * @see View.OnClickListener
         * @see StoriesAdapterInterface#reloadStoriesFragmentForEditing
         */
        private View.OnClickListener clickListenerEditStories=new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                int position=listview.getPositionForView(v);
                // delete
//              realm= Realm.getDefaultInstance();
//              RealmResults<Stories> results = realm.where(Stories.class).findAll();
//                realm.beginTransaction();
//              Stories daModificare=results.get(position);
//                assert daModificare != null;
//                daCancellare.deleteFromRealm();
//                realm.commitTransaction();
                //
                listener.reloadStoriesFragmentForEditing(position);
                //
            }
        };
        /**
         * listener for the delete button.
         * <p>
         * it deletes the selected element from the realm and
         * makes the callback to the activity to reload the fragment with the updated data
         *
         * @see View.OnClickListener
         * @see StoriesAdapterInterface#reloadStoriesFragmentDeleteStories
         */
        private View.OnClickListener clickListenerDeleteStories=new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                int position=listview.getPositionForView(v);
                // delete
//                realm= Realm.getDefaultInstance();
//                RealmResults<Stories> results = realm.where(Stories.class).findAll();
//                realm.beginTransaction();
//                Stories daCancellare=results.get(position);
//                assert daCancellare != null;
//                daCancellare.deleteFromRealm();
//                realm.commitTransaction();
                //
                listener.reloadStoriesFragmentDeleteStories(position);
                //
            }
        };
    }

