package com.sampietro.NaiveAAC.activities.Arasaac

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
 * this adapter is the View "supplier" for the listview in the UI for pictograms to modify settings.
 *
 * @version     4.0, 09/09/2023
 * @see com.sampietro.NaiveAAC.activities.Arasaac.PictogramsAllToModify
 *
 * @see com.sampietro.NaiveAAC.activities.Settings.PictogramsToModifyFragment
 *
 * @see com.sampietro.NaiveAAC.activities.Settings.SettingsActivity
 */
class PictogramsAllToModifyAdapter(
    private val context: Context,
    private val pictogramsAllToModify: List<PictogramsAllToModify>?,
    listview: ListView
) : BaseAdapter() {
    /**
     * <h1>PictogramsAllToModifyAdapterInterface</h1>
     *
     * **PictogramsAllToModifyAdapterInterface**
     * interface used to refer to the Activity without having to explicitly use its class
     *
     *
     * this interface is initialized by the constructor.
     *
     * @see PictogramsAllToModifyAdapter
     */
    interface PictogramsAllToModifyAdapterInterface {
        fun reloadPictogramsToModifyFragment()
    }

    lateinit var listener: PictogramsAllToModifyAdapterInterface

    //
//    private val pictogramsAllToModify: List<PictogramsAllToModify>? = null
//    private val context: Context? = null
//    private val listview: ListView? = null

    // Realm
    lateinit private var realm: Realm

    /**
     * return size of list<PictogramsAllToModify>
     *
     * @return int with size of list<PictogramsAllToModify>
    </PictogramsAllToModify></PictogramsAllToModify> */
    override fun getCount(): Int {
        return pictogramsAllToModify!!.size
    }

    /**
     * return the element of a specified index within the list<PictogramsAllToModify>
     *
     * @param position int index within the list<PictogramsAllToModify>
     * @return object with the element within the list<PictogramsAllToModify>
    </PictogramsAllToModify></PictogramsAllToModify></PictogramsAllToModify> */
    override fun getItem(position: Int): Any {
        return pictogramsAllToModify!![position]
    }

    /**
     * return the hashCode of a specified index within the list<PictogramsAllToModify>
     *
     * @param position int index within the list<PictogramsAllToModify>
     * @return long with the the hashCode of the element within the list<PictogramsAllToModify>
    </PictogramsAllToModify></PictogramsAllToModify></PictogramsAllToModify> */
    override fun getItemId(position: Int): Long {
        return getItem(position).hashCode().toLong()
    }

    /**
     * return the view with the row corresponding to a specific index within the list<PictogramsAllToModify>
     *
     * @param position int index within the list<PictogramsAllToModify>
     * @param v view to inflate with the row corresponding to a specific index within the list<PictogramsAllToModify>
     * @param vg viewgroup the parent that this view will eventually be attached to
     * @return view with the row corresponding to a specific index within the list<PictogramsAllToModify>
     * @see .getItem
     *
     * @see .clickListenerDeletePictogramsAllToModify
    </PictogramsAllToModify></PictogramsAllToModify></PictogramsAllToModify></PictogramsAllToModify> */
    override fun getView(position: Int, v: View?, vg: ViewGroup): View {
//        var v = v
        val v1: View
        if (v == null) {
            v1 = LayoutInflater.from(context).inflate(R.layout.activity_settings_row, null)
            }
            else
            { v1 = v}
        val l = getItem(position) as PictogramsAllToModify
        val txt = v1.findViewById<View>(R.id.imageDescriptionRow) as TextView
        txt.text = l.get_id() + " " + l.modificationType
        //
        val imgbtn = v1.findViewById<View>(R.id.btn_delete_image) as ImageButton
        imgbtn.setOnClickListener(clickListenerDeletePictogramsAllToModify)
        //
        return v1
    }

    /**
     * listener for the delete button.
     *
     *
     * it deletes the selected element from the realm
     *
     * @see View.OnClickListener
     */
    private val clickListenerDeletePictogramsAllToModify = View.OnClickListener { v ->
        val position = listview.getPositionForView(v)
        // delete
        realm = Realm.getDefaultInstance()
        val results = realm.where(
            PictogramsAllToModify::class.java
        ).findAll()
        realm.beginTransaction()
        val daCancellare = results[position]!!
        daCancellare.deleteFromRealm()
        realm.commitTransaction()
        //
        listener.reloadPictogramsToModifyFragment()
        //
    }

    /**
     * PictogramsAllToModifyAdapter constructor.
     * listener setting for settings activity callbacks , context annotation and other
     */
    init {
//        this.pictogramsAllToModify = pictogramsAllToModify
//        this.context = context
//        this.listview = listview
        //
        val activity = context as Activity
        listener = activity as PictogramsAllToModifyAdapterInterface
        //
    }
}