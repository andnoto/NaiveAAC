package com.sampietro.NaiveAAC.activities.Grammar

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
 * this adapter is the View "supplier" for the listview in the UI for lists of names settings.
 *
 * @version     4.0, 09/09/2023
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
        fun reloadListOfNamesFragment()
    }

    lateinit var listener: ListsOfNamesAdapterInterface

    //
//    private val listsOfNames: List<ListsOfNames>? = null
//    private val context: Context? = null
//    private val listview: ListView? = null

    // Realm
    lateinit private var realm: Realm

    /**
     * return size of list<ListsOfNames>
     *
     * @return int with size of list<ListsOfNames>
    </ListsOfNames></ListsOfNames> */
    override fun getCount(): Int {
        return listsOfNames!!.size
    }

    /**
     * return the element of a specified index within the list<ListsOfNames>
     *
     * @param position int index within the list<ListsOfNames>
     * @return object with the element within the list<ListsOfNames>
    </ListsOfNames></ListsOfNames></ListsOfNames> */
    override fun getItem(position: Int): Any {
        return listsOfNames!![position]
    }

    /**
     * return the hashCode of a specified index within the list<ListsOfNames>
     *
     * @param position int index within the list<ListsOfNames>
     * @return long with the the hashCode of the element within the list<ListsOfNames>
    </ListsOfNames></ListsOfNames></ListsOfNames> */
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
     * @see .getItem
     *
     * @see .clickListenerDeleteListsOfNames
    </ListsOfNames></ListsOfNames></ListsOfNames></GameParameters> */
    override fun getView(position: Int, v: View?, vg: ViewGroup): View {
        // 1) it is checked if the View passed in input is null and only in this
        // case is initialized with the LayoutInflater.
        // The LayoutInflater structures the View to be created as defined in the
        // XML layout indicated.
        // 2) The data will be taken from the object with the element within
        // the list <ListsOfNames> of position position retrieved using getItem.
        // 3) the listener for the delete button of the element within
        // the list <ListsOfNames> of position position is set
//        var v = v
        val v1: View
        if (v == null) {
            v1 = LayoutInflater.from(context).inflate(R.layout.activity_settings_row, null)
            }
            else
            { v1 = v}
        val l = getItem(position) as ListsOfNames
        val txt = v1.findViewById<View>(R.id.imageDescriptionRow) as TextView
        txt.text = l.keyword + " " + l.word
        //
        val imgbtn = v1.findViewById<View>(R.id.btn_delete_image) as ImageButton
        imgbtn.setOnClickListener(clickListenerDeleteListsOfNames)
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
     * @see ListsOfNamesAdapterInterface.reloadListOfNamesFragment
     */
    private val clickListenerDeleteListsOfNames = View.OnClickListener { v ->
        val position = listview.getPositionForView(v)
        // delete image
        realm = Realm.getDefaultInstance()
        val results = realm.where(ListsOfNames::class.java).findAll()
        realm.beginTransaction()
        val daCancellare = results[position]
        daCancellare!!.deleteFromRealm()
        realm.commitTransaction()
        //
        listener.reloadListOfNamesFragment()
        //
    }

    /**
     * ListsOfNamesAdapter constructor.
     * listener setting for settings activity callbacks ,  context annotation and other
     *
    */
    init {
//        this.listsOfNames = listsOfNames
//        this.context = context
//        this.listview = listview
        //
        val activity = context as Activity
        listener = activity as ListsOfNamesAdapterInterface
        //
    }
}