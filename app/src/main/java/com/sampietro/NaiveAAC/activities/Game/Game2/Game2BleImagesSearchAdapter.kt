package com.sampietro.NaiveAAC.activities.Game.Game2

import android.widget.BaseAdapter
import android.view.ViewGroup
import android.view.LayoutInflater
import com.sampietro.NaiveAAC.R
import android.widget.TextView
import android.app.Activity
import android.content.Context
import android.view.View
import android.widget.ListView
import com.sampietro.NaiveAAC.activities.Graphics.Images
import io.realm.Realm

/**
 * this adapter is the View "supplier" for the listview in the UI for images settings.
 *
 * @version     5.0, 01/04/2024
 * @see com.sampietro.NaiveAAC.activities.Graphics.Images
 *
 * @see Game2BleImagesFragment
 *
 * @see Game2BleActivity
 */
class Game2BleImagesSearchAdapter(private val context: Context, private val images: List<Images>?, listview: ListView) : BaseAdapter() {
    /**
     * <h1>ImagesAdapterAdapterInterface</h1>
     *
     * **ImagesAdapterAdapterInterface**
     * interface used to refer to the Activity without having to explicitly use its class
     *
     *
     * this interface is initialized by the constructor.
     *
     * @see Game2BleImagesSearchAdapter
     */
    interface ImagesSearchAdapterInterface {
        fun reloadFragmentFromImageSearch(imageKey: String?)
    }

    lateinit var listener: ImagesSearchAdapterInterface

    // Realm
    lateinit private var realm: Realm

    /**
     * return size of list<Images>
     *
     * @return int with size of list<Images>
    */
    override fun getCount(): Int {
        return images!!.size
    }

    /**
     * return the element of a specified index within the list<Images>
     *
     * @param position int index within the list<Images>
     * @return object with the element within the list<Images>
    */
    override fun getItem(position: Int): Any {
        return images!![position]
    }

    /**
     * return the hashCode of a specified index within the list<Images>
     *
     * @param position int index within the list<Images>
     * @return long with the the hashCode of the element within the list<Images>
    */
    override fun getItemId(position: Int): Long {
        return getItem(position).hashCode().toLong()
    }

    /**
     * return the view with the row corresponding to a specific index within the list<Images>
     *
     * @param position int index within the list<Images>
     * @param v view to inflate with the row corresponding to a specific index within the list<Images>
     * @param vg viewgroup the parent that this view will eventually be attached to
     * @return view with the row corresponding to a specific index within the list<Images>
     * @see getItem
     *
     * @see clickListenerSelectImage
    */
    override fun getView(position: Int, v: View?, vg: ViewGroup): View {
        // 1) it is checked if the View passed in input is null and only in this
        // case is initialized with the LayoutInflater.
        // The LayoutInflater structures the View to be created as defined in the
        // XML layout indicated.
        // 2) The data will be taken from the object with the element within
        // the list <Images> of position position retrieved using getItem.
        // 3) the listener for the delete button of the element within
        // the list <Images> of position position is set
        val v1: View
        if (v == null) {
            v1 = LayoutInflater.from(context).inflate(R.layout.activity_settings_row_stories_images_videos_and_sounds_search, null)
            }
            else
            { v1 = v}
        val l = getItem(position) as Images
        val vdr = v1.findViewById<View>(R.id.media_description_row) as TextView
        vdr.text = l.descrizione
        vdr.setOnClickListener(clickListenerSelectImage)
        return v1
    }
    /**
     * listener for item selection.
     *
     *
     * it makes the callback to the activity to reload the fragment with the updated data
     *
     * @see View.OnClickListener
     */
    private val clickListenerSelectImage = View.OnClickListener { v ->
        val position = listview.getPositionForView(v)
        //
        realm = Realm.getDefaultInstance()
        val results = realm.where(
            Images::class.java
        ).findAll()
        val selectedImage = results[position]!!
        //
        listener.reloadFragmentFromImageSearch(selectedImage.descrizione)
    }


    /**
     * ImagesAdapter constructor.
     * listener setting for settings activity callbacks ,  context annotation and other
     *
    */
    init {
        val activity = context as Activity
        listener = activity as ImagesSearchAdapterInterface
    }
}