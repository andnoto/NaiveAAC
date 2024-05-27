package com.sampietro.NaiveAAC.activities.Graphics

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ListView
import android.widget.TextView
import com.sampietro.NaiveAAC.R

/**
 * this adapter is the View "supplier" for the listview in the UI for sounds copyright list.
 *
 * @version
 * @see Sounds
 *
 * @see com.sampietro.NaiveAAC.activities.Info.InfoSoundsCopyrightListFragment
 *
 * @see com.sampietro.NaiveAAC.activities.Info.InfoActivity
 */
class SoundsCopyrightAdapter(private val context: Context, private val sounds: List<Sounds>?, listview: ListView?) :
    BaseAdapter() {

    /**
     * SoundsCopyrightAdapter constructor.
     * listener setting for settings activity callbacks ,  context annotation and other
    */
    init {
    }

    /**
     * return size of list<Sounds>
     *
     * @return int with size of list<Sounds>
    */
    override fun getCount(): Int {
        return sounds!!.size
    }

    /**
     * return the element of a specified index within the list<Sounds>
     *
     * @param position int index within the list<Sounds>
     * @return object with the element within the list<Sounds>
    </Sounds></Sounds></Sounds> */
    override fun getItem(position: Int): Any {
        return sounds!![position]
    }

    /**
     * return the hashCode of a specified index within the list<Sounds>
     *
     * @param position int index within the list<Sounds>
     * @return long with the the hashCode of the element within the list<Sounds>
    */
    override fun getItemId(position: Int): Long {
        return getItem(position).hashCode().toLong()
    }

    /**
     * return the view with the row corresponding to a specific index within the list<Sounds>
     *
     * @param position int index within the list<Sounds>
     * @param v view to inflate with the row corresponding to a specific index within the list<Sounds>
     * @param vg viewgroup the parent that this view will eventually be attached to
     * @return view with the row corresponding to a specific index within the list<Sounds>
     * @see getItem
    */
    override fun getView(position: Int, v: View?, vg: ViewGroup): View {
        // 1) it is checked if the View passed in input is null and only in this
        // case is initialized with the LayoutInflater.
        // The LayoutInflater structures the View to be created as defined in the
        // XML layout indicated.
        // 2) The data will be taken from the object with the element within
        // the list <Sounds> of position position retrieved using getItem.
        // 3) the listener for the delete button of the element within
        // the list <Sounds> of position position is set
        val v1: View
        if (v == null) {
            v1 = LayoutInflater.from(context).inflate(R.layout.activity_copyright_row, null)
            }
            else
            { v1 = v}
        val l = getItem(position) as Sounds
        val txt = v1.findViewById<View>(R.id.imageDescriptionRow) as TextView
        txt.text = l.copyright
        //
        return v1
    }
}