package com.sampietro.NaiveAAC.activities.WordPairs;

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
import com.sampietro.NaiveAAC.activities.Settings.SettingsActivity;
import com.sampietro.NaiveAAC.activities.Settings.WordPairsFragment;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * this adapter is the View "supplier" for the listview in the UI for word pairs list settings.
 *
 * @version     1.0, 06/13/22
 * @see WordPairs
 * @see WordPairsFragment
 * @see SettingsActivity
 */
public class WordPairsAdapter extends BaseAdapter
    {
        /**
         * <h1>WordPairsAdapterInterface</h1>
         * <p><b>WordPairsAdapterInterface</b>
         * interface used to refer to the Activity without having to explicitly use its class
         * <p>
         * this interface is initialized by the constructor.
         *
         * @see WordPairsAdapter (Context, WordPairs , ListView)
         */
        public interface WordPairsAdapterInterface{
            public void reloadWordPairsFragment();
        }
        public WordPairsAdapterInterface listener;
        //
        private List<WordPairs> wordPairs=null;
        private Context context=null;
        private ListView listview=null;
        // Realm
        private Realm realm;
        /**
         * WordPairsAdapter constructor.
         * listener setting for settings activity callbacks ,  context annotation and other
         *
         * @param context context
         * @param wordPairs list<WordPairs> reference to the data structure through the RealmResults class
         * @param listview listview reference to the listview in the UI for word pairs settings
         */
        public WordPairsAdapter(Context context, List<WordPairs> wordPairs, ListView listview)
        {
            this.wordPairs=wordPairs;
            this.context=context;
            this.listview=listview;
            //
            Activity activity = (Activity) context;
            listener=(WordPairsAdapterInterface) activity;
        }
        /**
         * return size of list<WordPairs>
         *
         * @return int with size of list<WordPairs>
         */
        @Override
        public int getCount()
        {
            return wordPairs.size();
        }
        /**
         * return the element of a specified index within the list<WordPairs>
         *
         * @param position int index within the list<WordPairs>
         * @return object with the element within the list<WordPairs>
         */
        @Override
        public Object getItem(int position)
        {
            return wordPairs.get(position);
        }
        /**
         * return the hashCode of a specified index within the list<WordPairs>
         *
         * @param position int index within the list<WordPairs>
         * @return long with the the hashCode of the element within the list<WordPairs>
         */
        @Override
        public long getItemId(int position)
        {
            return getItem(position).hashCode();
        }
        // per prima cosa, viene controllato se la View passata in input è nulla e solo in questo
        // caso viene inizializzata con il LayoutInflater.
        // Questo aspetto è molto importante ai fini della salvaguardia delle risorse infatti
        // Android riciclerà quanto possibile le View già create.
        // Il LayoutInflater attua per i layout quello che abbiamo già visto fare per i menu
        // con il MenuInflater.
        // In pratica la View da creare verrà strutturata in base al “progetto” definito nel
        // layout XML indicatogli.
        // Dopo il blocco if, la View non sarà sicuramente nulla perciò procederemo al completamento
        // dei suoi campi.
        // I dati verranno prelevati dall’oggetto ArticleInfo di posizione position recuperato
        // mediante getItem, già implementato.
        // Al termine, getView restituirà la View realizzata.
        /**
         * return the view with the row corresponding to a specific index within the list<WordPairs>
         *
         * @param position int index within the list<WordPairs>
         * @param v view to inflate with the row corresponding to a specific index within the list<WordPairs>
         * @param vg viewgroup the parent that this view will eventually be attached to
         * @return view with the row corresponding to a specific index within the list<WordPairs>
         * @see #getItem
         * @see #clickListenerDeleteWordPairs
         */
        @Override
        public View getView(int position, View v, ViewGroup vg)
        {
            // 1) it is checked if the View passed in input is null and only in this
            // case is initialized with the LayoutInflater.
            // The LayoutInflater structures the View to be created as defined in the
            // XML layout indicated.
            // 2) The data will be taken from the object with the element within
            // the list <WordPairs> of position position retrieved using getItem.
            // 3) the listener for the delete button of the element within
            // the list <WordPairs> of position position is set
            if (v==null)
            {
                v= LayoutInflater.from(context).inflate(R.layout.activity_settings_row, null);
            }
            WordPairs l=(WordPairs) getItem(position);
            TextView txt=(TextView) v.findViewById(R.id.imageDescriptionRow);
            txt.setText(l.getWord1()+ " " + l.getWord2());
            //
            ImageButton imgbtn=(ImageButton) v.findViewById(R.id.btn_delete_image);
            imgbtn.setOnClickListener(clickListenerDeleteWordPairs);
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
         * @see WordPairsAdapterInterface#reloadWordPairsFragment
         */
        private View.OnClickListener clickListenerDeleteWordPairs=new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                int position=listview.getPositionForView(v);
                // delete
                realm= Realm.getDefaultInstance();
                RealmResults<WordPairs> results = realm.where(WordPairs.class).findAll();
                //
                results = results.sort("word1");
                //
                realm.beginTransaction();
                WordPairs daCancellare=results.get(position);
                daCancellare.deleteFromRealm();
                realm.commitTransaction();
                //
                listener.reloadWordPairsFragment();
                //
            }
        };
    }

