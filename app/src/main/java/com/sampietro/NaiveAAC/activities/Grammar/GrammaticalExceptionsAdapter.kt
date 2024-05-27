package com.sampietro.NaiveAAC.activities.Grammar

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageButton
import android.widget.ListView
import android.widget.TextView
import com.sampietro.NaiveAAC.R
import io.realm.Realm

/**
 * this adapter is the View "supplier" for the listview in the UI for grammatical exceptions settings.
 *
 * @version     4.0, 09/09/2023
 * @see com.sampietro.NaiveAAC.activities.Settings.GrammaticalExceptionsFragment
 *
 * @see com.sampietro.NaiveAAC.activities.Settings.SettingsActivity
 */
class GrammaticalExceptionsAdapter(
    private val context: Context,
    private val grammaticalExceptions: List<GrammaticalExceptions>?,
    listview: ListView
) : BaseAdapter() {
    /**
     * <h1>GrammaticalExceptionsAdapterInterface</h1>
     *
     * **GrammaticalExceptionsAdapterInterface**
     * interface used to refer to the Activity without having to explicitly use its class
     *
     *
     * this interface is initialized by the constructor.
     *
     * @see GrammaticalExceptionsAdapter
     */
    interface GrammaticalExceptionsAdapterInterface {
        fun reloadGrammaticalExceptionsFragment()
    }

    lateinit var listener: GrammaticalExceptionsAdapterInterface

    // Realm
    lateinit private var realm: Realm

    /**
     * return size of list<GrammaticalExceptions>
     *
     * @return int with size of list<GrammaticalExceptions>
    */
    override fun getCount(): Int {
        return grammaticalExceptions!!.size
    }

    /**
     * return the element of a specified index within the list<GrammaticalExceptions>
     *
     * @param position int index within the list<GrammaticalExceptions>
     * @return object with the element within the list<GrammaticalExceptions>
    </GrammaticalExceptions></GrammaticalExceptions></GrammaticalExceptions> */
    override fun getItem(position: Int): Any {
        return grammaticalExceptions!![position]
    }

    /**
     * return the hashCode of a specified index within the list<GrammaticalExceptions>
     *
     * @param position int index within the list<GrammaticalExceptions>
     * @return long with the the hashCode of the element within the list<GrammaticalExceptions>
    */
    override fun getItemId(position: Int): Long {
        return getItem(position).hashCode().toLong()
    }

    /**
     * return the view with the row corresponding to a specific index within the list<GrammaticalExceptions>
     *
     * @param position int index within the list<GrammaticalExceptions>
     * @param v view to inflate with the row corresponding to a specific index within the list<GrammaticalExceptions>
     * @param vg viewgroup the parent that this view will eventually be attached to
     * @return view with the row corresponding to a specific index within the list<GrammaticalExceptions>
     * @see .getItem
     *
     * @see .clickListenerDeleteGrammaticalExceptions
    */
    override fun getView(position: Int, v: View?, vg: ViewGroup): View {
        // 1) it is checked if the View passed in input is null and only in this
        // case is initialized with the LayoutInflater.
        // The LayoutInflater structures the View to be created as defined in the
        // XML layout indicated.
        // 2) The data will be taken from the object with the element within
        // the list <GrammaticalExceptions> of position position retrieved using getItem.
        // 3) the listener for the delete button of the element within
        // the list <GrammaticalExceptions> of position position is set
        val v1: View
        if (v == null) {
            v1 = LayoutInflater.from(context).inflate(R.layout.activity_settings_row, null)
            }
            else
            { v1 = v}
        val l = getItem(position) as GrammaticalExceptions
        val txt = v1.findViewById<View>(R.id.imageDescriptionRow) as TextView
        txt.text = l.keyword + " " + l.exceptionType
        //
        val imgbtn = v1.findViewById<View>(R.id.btn_delete_image) as ImageButton
        imgbtn.setOnClickListener(clickListenerDeleteGrammaticalExceptions)
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
     * @see GrammaticalExceptionsAdapter.GrammaticalExceptionsAdapterInterface.reloadGrammaticalExceptionsFragment
     */
    private val clickListenerDeleteGrammaticalExceptions = View.OnClickListener { v ->
        val position = listview.getPositionForView(v)
        // delete image
        realm = Realm.getDefaultInstance()
        val results = realm.where(
            GrammaticalExceptions::class.java
        ).findAll()
        realm.beginTransaction()
        val daCancellare = results[position]!!
        daCancellare.deleteFromRealm()
        realm.commitTransaction()
        //
        listener.reloadGrammaticalExceptionsFragment()
        //
    }

    /**
     * GrammaticalExceptionsAdapter constructor.
     * listener setting for settings activity callbacks ,  context annotation and other
     *
    */
    init {
        val activity = context as Activity
        listener = activity as GrammaticalExceptionsAdapterInterface
    }
}