package com.sampietro.NaiveAAC.activities.Graphics

import android.widget.BaseAdapter
import android.view.ViewGroup
import android.view.LayoutInflater
import com.sampietro.NaiveAAC.R
import android.widget.TextView
import android.widget.ImageButton
import android.app.Activity
import android.content.Context
import android.view.View
import android.widget.ListView
import io.realm.Realm

/**
 * this adapter is the View "supplier" for the listview in the UI for images settings.
 *
 * @version     4.0, 09/09/2023
 * @see com.sampietro.NaiveAAC.activities.Graphics.Images
 *
 * @see com.sampietro.NaiveAAC.activities.Settings.ImagesFragment
 *
 * @see com.sampietro.NaiveAAC.activities.Settings.SettingsActivity
 */
class ImagesAdapter(private val context: Context, private val images: List<Images>?, listview: ListView) : BaseAdapter() {
    /**
     * <h1>ImagesAdapterAdapterInterface</h1>
     *
     * **ImagesAdapterAdapterInterface**
     * interface used to refer to the Activity without having to explicitly use its class
     *
     *
     * this interface is initialized by the constructor.
     *
     * @see ImagesAdapter
     */
    interface ImagesAdapterInterface {
        fun reloadImagesFragment()
    }

    lateinit var listener: ImagesAdapterInterface

    //
//    private val images: List<Images>? = null
//    private val context: Context? = null
//    private val listview: ListView? = null

    // Realm
    lateinit private var realm: Realm

    /**
     * return size of list<Images>
     *
     * @return int with size of list<Images>
    </Images></Images> */
    override fun getCount(): Int {
        return images!!.size
    }

    /**
     * return the element of a specified index within the list<Images>
     *
     * @param position int index within the list<Images>
     * @return object with the element within the list<Images>
    </Images></Images></Images> */
    override fun getItem(position: Int): Any {
        return images!![position]
    }

    /**
     * return the hashCode of a specified index within the list<Images>
     *
     * @param position int index within the list<Images>
     * @return long with the the hashCode of the element within the list<Images>
    </Images></Images></Images> */
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
     * @see .getItem
     *
     * @see .clickListenerDeleteImage
    </Images></Images></Images></Images> */
    override fun getView(position: Int, v: View?, vg: ViewGroup): View {
        // 1) it is checked if the View passed in input is null and only in this
        // case is initialized with the LayoutInflater.
        // The LayoutInflater structures the View to be created as defined in the
        // XML layout indicated.
        // 2) The data will be taken from the object with the element within
        // the list <Images> of position position retrieved using getItem.
        // 3) the listener for the delete button of the element within
        // the list <Images> of position position is set
//        var v = v
        val v1: View
        if (v == null) {
            v1 = LayoutInflater.from(context).inflate(R.layout.activity_settings_row, null)
            }
            else
            { v1 = v}
        val l = getItem(position) as Images
        val txt = v1.findViewById<View>(R.id.imageDescriptionRow) as TextView
        txt.text = l.descrizione
        //
        val imgbtn = v1.findViewById<View>(R.id.btn_delete_image) as ImageButton
        imgbtn.setOnClickListener(clickListenerDeleteImage)
        //
        return v1
    }

    /**
     * listener for the delete button.
     *
     *
     * it deletes the selected element from the realm and
     * makes the callback to the activity to reload the fragment with the updated data
     *
     * @see View.OnClickListener
     *
     * @see ImagesAdapter.ImagesAdapterInterface.reloadImagesFragment
     */
    private val clickListenerDeleteImage = View.OnClickListener { v ->
        val position = listview.getPositionForView(v)
        // delete image
        realm = Realm.getDefaultInstance()
        var results = realm.where(
            Images::class.java
        ).findAll()
        //
        results = results.sort("descrizione")
        //
        realm.beginTransaction()
        val daCancellare = results[position]!!
        daCancellare.deleteFromRealm()
        realm.commitTransaction()
        //
        listener.reloadImagesFragment()
    }

    /**
     * ImagesAdapter constructor.
     * listener setting for settings activity callbacks ,  context annotation and other
     *
    */
    init {
//        this.images = images
//        this.context = context
//        this.listview = listview
        //
        val activity = context as Activity
        listener = activity as ImagesAdapterInterface
    }
}