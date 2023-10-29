package com.sampietro.NaiveAAC.activities.Graphics

import android.widget.BaseAdapter
import android.content.Context
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import android.widget.ListView
import com.sampietro.NaiveAAC.R
import android.widget.TextView

/**
 * this adapter is the View "supplier" for the listview in the UI for images copyright.
 *
 * @version 4.0, 09/09/2023
 * @see Images
 *
 * @see com.sampietro.NaiveAAC.activities.Info.InfoImagesCopyrightListFragment
 *
 * @see com.sampietro.NaiveAAC.activities.Info.InfoActivity
 */
class ImagesCopyrightAdapter(private val context: Context, private val images: List<Images>?, listview: ListView?) :
    BaseAdapter() {
//    private val images: List<Images>? = null
//    private val context: Context? = null
//    private val listview: ListView? = null

    // Realm
//    private val realm: Realm? = null

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
//        val activity = context as Activity?
    }

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
            v1 = LayoutInflater.from(context).inflate(R.layout.activity_copyright_row, null)
            }
            else
            { v1 = v}
        val l = getItem(position) as Images
        val txt = v1.findViewById<View>(R.id.imageDescriptionRow) as TextView
        txt.text = l.copyright
        return v1
    }
}