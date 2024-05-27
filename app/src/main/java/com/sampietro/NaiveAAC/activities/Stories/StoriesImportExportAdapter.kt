package com.sampietro.NaiveAAC.activities.Stories

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ListView
import android.widget.TextView
import com.sampietro.NaiveAAC.R

/**
 * this adapter is the View "supplier" for the listview in the UI for stories import export.
 *
 * @version 4.0, 09/09/2023
 * @see Stories
 *
 * @see com.sampietro.NaiveAAC.activities.Settings.StoriesImportExportFragment
 *
 * @see com.sampietro.NaiveAAC.activities.Settings.SettingsStoriesImportExportActivity
 */
class StoriesImportExportAdapter(private val context: Context?, private val stories: List<Stories>?, listview: ListView?) :
    BaseAdapter() {
    /**
     * StoriesAdapter constructor.
     * listener setting for settings activity callbacks ,  context annotation and other
     *
    */
    init {
    }

    /**
     * return size of list<Stories>
     *
     * @return int with size of list<Stories>
    */
    override fun getCount(): Int {
        return stories!!.size
    }

    /**
     * return the element of a specified index within the list<Stories>
     *
     * @param position int index within the list<Stories>
     * @return object with the element within the list<Stories>
    */
    override fun getItem(position: Int): Any {
        return stories!![position]
    }

    /**
     * return the hashCode of a specified index within the list<Stories>
     *
     * @param position int index within the list<Stories>
     * @return long with the the hashCode of the element within the list<Stories>
    */
    override fun getItemId(position: Int): Long {
        return getItem(position).hashCode().toLong()
    }

    /**
     * return the view with the row corresponding to a specific index within the list<Stories>
     *
     * @param position int index within the list<Stories>
     * @param v view to inflate with the row corresponding to a specific index within the list<Stories>
     * @param vg viewgroup the parent that this view will eventually be attached to
     * @return view with the row corresponding to a specific index within the list<Stories>
     * @see getItem
    */
    override fun getView(position: Int, v: View?, vg: ViewGroup): View {
        // 1) it is checked if the View passed in input is null and only in this
        // case is initialized with the LayoutInflater.
        // The LayoutInflater structures the View to be created as defined in the
        // XML layout indicated.
        // 2) The data will be taken from the object with the element within
        // the list <Stories> of position position retrieved using getItem.
        // 3) the listener for the delete button of the element within
        // the list <Stories> of position position is set
        val v1: View
        if (v == null) {
            v1 = LayoutInflater.from(context)
                .inflate(R.layout.activity_settings_row_stories_import_export, null)
            }
            else
            { v1 = v}
        val l = getItem(position) as Stories
        val txt = v1.findViewById<View>(R.id.imageDescriptionRow) as TextView
        //
        txt.text = (l.story + "-" + l.phraseNumberInt
                + "-" + l.wordNumberInt + " " + l.word)
        //
        return v1
    }
}