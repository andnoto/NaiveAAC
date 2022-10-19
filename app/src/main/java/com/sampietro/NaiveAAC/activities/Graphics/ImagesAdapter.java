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
import com.sampietro.NaiveAAC.activities.Settings.ImagesFragment;
import com.sampietro.NaiveAAC.activities.Settings.SettingsActivity;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * this adapter is the View "supplier" for the listview in the UI for images settings.
 *
 * @version     1.1, 04/22/22
 * @see Images
 * @see ImagesFragment
 * @see SettingsActivity
 */
public class ImagesAdapter extends BaseAdapter
    {
        /**
         * <h1>ImagesAdapterAdapterInterface</h1>
         * <p><b>ImagesAdapterAdapterInterface</b>
         * interface used to refer to the Activity without having to explicitly use its class
         * <p>
         * this interface is initialized by the constructor.
         *
         * @see ImagesAdapter (Context, Images , ListView)
         */
        public interface ImagesAdapterInterface{
                public void reloadImagesFragment();
            }
        public ImagesAdapterInterface listener;
        //
        private List<Images> images=null;
        private Context context=null;
        private ListView listview=null;
        // Realm
        private Realm realm;
        /**
         * ImagesAdapter constructor.
         * listener setting for settings activity callbacks ,  context annotation and other
         *
         * @param context context
         * @param images list<Images> reference to the data structure through the RealmResults class
         * @param listview listview reference to the listview in the UI for images settings
         */
        public ImagesAdapter(Context context, List<Images> images, ListView listview)
        {
            this.images=images;
            this.context=context;
            this.listview=listview;
            //
            Activity activity = (Activity) context;
            listener=(ImagesAdapterInterface) activity;
        }
        /**
         * return size of list<Images>
         *
         * @return int with size of list<Images>
         */
        @Override
        public int getCount()
        {
            return images.size();
        }
        /**
         * return the element of a specified index within the list<Images>
         *
         * @param position int index within the list<Images>
         * @return object with the element within the list<Images>
         */
        @Override
        public Object getItem(int position)
        {
            return images.get(position);
        }
        /**
         * return the hashCode of a specified index within the list<Images>
         *
         * @param position int index within the list<Images>
         * @return long with the the hashCode of the element within the list<Images>
         *
         */
        @Override
        public long getItemId(int position)
        {
            return getItem(position).hashCode();
        }
         /**
         * return the view with the row corresponding to a specific index within the list<Images>
         *
         * @param position int index within the list<Images>
         * @param v view to inflate with the row corresponding to a specific index within the list<Images>
         * @param vg viewgroup the parent that this view will eventually be attached to
         * @return view with the row corresponding to a specific index within the list<Images>
         * @see #getItem
         * @see #clickListenerDeleteImage
         */
        @Override
        public View getView(int position, View v, ViewGroup vg)
        {
            // 1) it is checked if the View passed in input is null and only in this
            // case is initialized with the LayoutInflater.
            // The LayoutInflater structures the View to be created as defined in the
            // XML layout indicated.
            // 2) The data will be taken from the object with the element within
            // the list <Images> of position position retrieved using getItem.
            // 3) the listener for the delete button of the element within
            // the list <Images> of position position is set
            if (v==null)
            {
                v= LayoutInflater.from(context).inflate(R.layout.activity_settings_row, null);
            }
            Images l=(Images) getItem(position);
            TextView txt=(TextView) v.findViewById(R.id.imageDescriptionRow);
            txt.setText(l.getDescrizione());
            //
            ImageButton imgbtn=(ImageButton) v.findViewById(R.id.btn_delete_image);
            imgbtn.setOnClickListener(clickListenerDeleteImage);
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
         * @see ImagesAdapterInterface#reloadImagesFragment
         */
        private View.OnClickListener clickListenerDeleteImage=new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                int position=listview.getPositionForView(v);
                // delete image
                realm= Realm.getDefaultInstance();
                RealmResults<Images> results = realm.where(Images.class).findAll();
                realm.beginTransaction();
                Images daCancellare=results.get(position);
                assert daCancellare != null;
                daCancellare.deleteFromRealm();
                realm.commitTransaction();
                //
                listener.reloadImagesFragment();
            }
        };
    }

