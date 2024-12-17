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
 * this adapter is the View "supplier" for the listview in the UI for lists of names settings.
 *
 * @version     5.0, 01/04/2024
 * @see com.sampietro.NaiveAAC.activities.Grammar.ListsOfNames
 *
 * @see com.sampietro.NaiveAAC.activities.Settings.ListsOfNamesFragment
 *
 * @see com.sampietro.NaiveAAC.activities.Settings.SettingsActivity
 */
class ListsOfNamesAdapter(private val context: Context, private val listsOfNames: List<ListsOfNames>?, listview: ListView) :
    BaseAdapter() {
    /**
     * <h1>ListsOfNamesAdapterInterface</h1>
     *
     * **ListsOfNamesAdapterInterface**
     * interface used to refer to the Activity without having to explicitly use its class
     *
     *
     * this interface is initialized by the constructor.
     *
     * @see ListsOfNamesAdapter
     */
    interface ListsOfNamesAdapterInterface {
        fun reloadListOfNamesFragmentForDeletion(position: Int)
        fun reloadListOfNamesFragmentForInsertion(position: Int)
        fun reloadListOfNamesFragmentForEditing(position: Int)
    }

    lateinit var listener: ListsOfNamesAdapterInterface

    // Realm
    lateinit private var realm: Realm

    /**
     * return size of list<ListsOfNames>
     *
     * @return int with size of list<ListsOfNames>
     */
    override fun getCount(): Int {
        return listsOfNames!!.size
    }

    /**
     * return the element of a specified index within the list<ListsOfNames>
     *
     * @param position int index within the list<ListsOfNames>
     * @return object with the element within the list<ListsOfNames>
    */
    override fun getItem(position: Int): Any {
        return listsOfNames!![position]
    }

    /**
     * return the hashCode of a specified index within the list<ListsOfNames>
     *
     * @param position int index within the list<ListsOfNames>
     * @return long with the the hashCode of the element within the list<ListsOfNames>
    */
    override fun getItemId(position: Int): Long {
        return getItem(position).hashCode().toLong()
    }

    /**
     * return the view with the row corresponding to a specific index within the list<GameParameters>
     *
     * @param position int index within the list<ListsOfNames>
     * @param v view to inflate with the row corresponding to a specific index within the list<ListsOfNames>
     * @param vg viewgroup the parent that this view will eventually be attached to
     * @return view with the row corresponding to a specific index within the list<ListsOfNames>
     * @see getItem
     *
     * @see clickListenerDeleteListsOfNames
     */
    override fun getView(position: Int, v: View?, vg: ViewGroup): View {
        // 1) it is checked if the View passed in input is null and only in this
        // case is initialized with the LayoutInflater.
        // The LayoutInflater structures the View to be created as defined in the
        // XML layout indicated.
        // 2) The data will be taken from the object with the element within
        // the list <ListsOfNames> of position position retrieved using getItem.
        // 3) the listener for the delete button of the element within
        // the list <ListsOfNames> of position position is set
        val v1: View
        if (v == null) {
            v1 = LayoutInflater.from(context).inflate(R.layout.activity_settings_row_crud, null)
        }
        else
        { v1 = v}
        val l = getItem(position) as ListsOfNames
        val txt = v1.findViewById<View>(R.id.imageDescriptionRow) as TextView
        //
        txt.text = l.keyword + " " + l.word + " " + l.elementActive + "-" + l.isMenuItem
        //
        val imgbtn = v1.findViewById<View>(R.id.btn_delete_image) as ImageButton
        imgbtn.setOnClickListener(clickListenerDeleteListOfNames)
        //
        val imgbtnedit = v1.findViewById<View>(R.id.btn_edit_image) as ImageButton
        imgbtnedit.setOnClickListener(clickListenerEditListOfNames)
        //
        val imgbtninsert = v1.findViewById<View>(R.id.btn_insert_image) as ImageButton
        imgbtninsert.setOnClickListener(clickListenerInsertListOfNames)
        //
        return v1
    }

    /**
     * listener for the insert button.
     *
     *
     * enables inserting before the selected item and
     * makes the callback to the activity to load the fragment for insertion
     *
     * @see View.OnClickListener
     *
     * @see reloadListOfNamesFragmentForInsertion
     */
    private val clickListenerInsertListOfNames = View.OnClickListener { v ->
        val position = listview.getPositionForView(v)
        //
        listener.reloadListOfNamesFragmentForInsertion(position)
        //
    }

    /**
     * listener for the edit button.
     *
     *
     * enable editing of the selected item and
     * makes the callback to the activity to reload the fragment for editing
     *
     * @see View.OnClickListener
     *
     * @see reloadListOfNamesFragmentForEditing
     */
    private val clickListenerEditListOfNames = View.OnClickListener { v ->
        val position = listview.getPositionForView(v)
        //
        listener.reloadListOfNamesFragmentForEditing(position)
        //
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
     * @see reloadListsOfNamesFragmentDeleteStories
     */
    private val clickListenerDeleteListOfNames = View.OnClickListener { v ->
        val position = listview.getPositionForView(v)
        //
        listener.reloadListOfNamesFragmentForDeletion(position)
        //
    }

    /**
     * ListsOfNamesAdapter constructor.
     * listener setting for settings activity callbacks ,  context annotation and other
     *
    */
    init {
        val activity = context as Activity
        listener = activity as ListsOfNamesAdapterInterface
        //
    }
}