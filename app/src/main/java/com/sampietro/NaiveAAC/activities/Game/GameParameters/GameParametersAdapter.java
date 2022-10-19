package com.sampietro.NaiveAAC.activities.Game.GameParameters;

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
import com.sampietro.NaiveAAC.activities.Settings.GameParametersSettingsFragment;
import com.sampietro.NaiveAAC.activities.Settings.SettingsActivity;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * this adapter is the View "supplier" for the listview in the UI for game parameters settings.
 *
 * @version     1.1, 04/22/22
 * @see GameParameters
 * @see GameParametersSettingsFragment
 * @see SettingsActivity
 */
public class GameParametersAdapter extends BaseAdapter
    {
        /**
         * <h1>GameParametersAdapterInterface</h1>
         * <p><b>GameParametersAdapterInterface</b>
         * interface used to refer to the Activity without having to explicitly use its class
         * <p>
         * this interface is initialized by the constructor.
         *
         * @see GameParametersAdapter(Context, GameParameters, ListView)
         */
        public interface GameParametersAdapterInterface{
                public void reloadGameParametersFragment();
            }
        public GameParametersAdapterInterface listener;
        //
        private List<GameParameters> gameParameters=null;
        private Context context=null;
        private ListView listview=null;
        // Realm
        private Realm realm;
        /**
         * GameParametersAdapter constructor.
         * listener setting for settings activity callbacks ,  context annotation and other
         *
         * @param context context
         * @param gameParameters list<GameParameters> reference to the data structure through the RealmResults class
         * @param listview listview reference to the listview in the UI for game parameters settings
         */
        public GameParametersAdapter(Context context, List<GameParameters> gameParameters, ListView listview)
        {
            this.gameParameters=gameParameters;
            this.context=context;
            this.listview=listview;
            //
            Activity activity = (Activity) context;
            listener=(GameParametersAdapterInterface) activity;
        }
        /**
         * return size of list<GameParameters>
         *
         * @return int with size of list<GameParameters>
         */
        @Override
        public int getCount()
        {
            return gameParameters.size();
        }
        /**
         * return the element of a specified index within the list<GameParameters>
         *
         * @param position int index within the list<GameParameters>
         * @return object with the element within the list<GameParameters>
         */
        @Override
        public Object getItem(int position)
        {
            return gameParameters.get(position);
        }
        /**
         * return the hashCode of a specified index within the list<GameParameters>
         *
         * @param position int index within the list<GameParameters>
         * @return long with the the hashCode of the element within the list<GameParameters>
         */
        @Override
        public long getItemId(int position)
        {
            return getItem(position).hashCode();
        }
        /**
         * return the view with the row corresponding to a specific index within the list<GameParameters>
         *
         * @param position int index within the list<GameParameters>
         * @param v view to inflate with the row corresponding to a specific index within the list<GameParameters>
         * @param vg viewgroup the parent that this view will eventually be attached to
         * @return view with the row corresponding to a specific index within the list<GameParameters>
         * @see #getItem
         * @see #clickListenerDeleteGameParameters
         */
        @Override
        public View getView(int position, View v, ViewGroup vg)
        {
            // 1) it is checked if the View passed in input is null and only in this
            // case is initialized with the LayoutInflater.
            // The LayoutInflater structures the View to be created as defined in the
            // XML layout indicated.
            // 2) The data will be taken from the object with the element within
            // the list <GameParameters> of position position retrieved using getItem.
            // 3) the listener for the delete button of the element within
            // the list <GameParameters> of position position is set
            if (v==null)
            {
                v= LayoutInflater.from(context).inflate(R.layout.activity_settings_row, null);
            }
            GameParameters l=(GameParameters) getItem(position);
            TextView txt=(TextView) v.findViewById(R.id.imageDescriptionRow);
            txt.setText(l.getGameName() + "-" + l.getGameParameter());
            //
            ImageButton imgbtn=(ImageButton) v.findViewById(R.id.btn_delete_image);
            imgbtn.setOnClickListener(clickListenerDeleteGameParameters);
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
         * @see GameParametersAdapterInterface#reloadGameParametersFragment
         */
        private View.OnClickListener clickListenerDeleteGameParameters=new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                int position=listview.getPositionForView(v);
                // delete
                realm= Realm.getDefaultInstance();
                RealmResults<GameParameters> results = realm.where(GameParameters.class).findAll();
                realm.beginTransaction();
                GameParameters daCancellare=results.get(position);
                assert daCancellare != null;
                daCancellare.deleteFromRealm();
                realm.commitTransaction();
                //
                listener.reloadGameParametersFragment();
                //
            }
        };
    }

