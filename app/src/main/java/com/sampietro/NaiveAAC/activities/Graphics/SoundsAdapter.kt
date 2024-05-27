package com.sampietro.NaiveAAC.activities.Graphics

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
 * this adapter is the View "supplier" for the listview in the UI for sounds settings.
 *
 * @version     4.0, 09/09/2023
 * @see Sounds
 *
 * @see com.sampietro.NaiveAAC.activities.Settings.SoundsFragment
 *
 * @see com.sampietro.NaiveAAC.activities.Settings.SettingsActivity
 */
class SoundsAdapter(private val context: Context, private val sounds: List<Sounds>?, listview: ListView) : BaseAdapter() {
    /**
     * <h1>SoundsAdapterAdapterInterface</h1>
     *
     * **SoundsAdapterAdapterInterface**
     * interface used to refer to the Activity without having to explicitly use its class
     *
     *
     * this interface is initialized by the constructor.
     *
     * @see SoundsAdapter
     */
    interface SoundsAdapterInterface {
        fun reloadSoundsFragment()
    }

    lateinit var listener: SoundsAdapterInterface

    //
//    private val sounds: List<Sounds>? = null
//    private val context: Context? = null
//    private val listview: ListView? = null

    // Realm
    lateinit private var realm: Realm

    /**
     * return size of list<Sounds>
     *
     * @return int with size of list<Sounds>
    </Sounds></Sounds> */
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
    </Sounds></Sounds></Sounds> */
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
     * @see .getItem
     *
     * @see .clickListenerDeleteSounds
    </Sounds></Sounds></Sounds></Sounds> */
    override fun getView(position: Int, v: View?, vg: ViewGroup): View {
        // 1) it is checked if the View passed in input is null and only in this
        // case is initialized with the LayoutInflater.
        // The LayoutInflater structures the View to be created as defined in the
        // XML layout indicated.
        // 2) The data will be taken from the object with the element within
        // the list <Sounds> of position position retrieved using getItem.
        // 3) the listener for the delete button of the element within
        // the list <Sounds> of position position is set
//        var v = v
        val v1: View
        if (v == null) {
            v1 = LayoutInflater.from(context).inflate(R.layout.activity_settings_row, null)
            }
            else
            { v1 = v}
        val l = getItem(position) as Sounds
        val txt = v1.findViewById<View>(R.id.imageDescriptionRow) as TextView
        txt.text = l.descrizione
        //
        val imgbtn = v1.findViewById<View>(R.id.btn_delete_image) as ImageButton
        imgbtn.setOnClickListener(clickListenerDeleteSounds)
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
     * @see SoundsAdapter.SoundsAdapterInterface.reloadSoundsFragment
     */
    private val clickListenerDeleteSounds = View.OnClickListener { v ->
        val position = listview.getPositionForView(v)
        // delete Sounds
        realm = Realm.getDefaultInstance()
        val results = realm.where(Sounds::class.java).findAll()
        realm.beginTransaction()
        val daCancellare = results[position]!!
        daCancellare.deleteFromRealm()
        realm.commitTransaction()
        //
        listener.reloadSoundsFragment()
    }

    /**
     * SoundsAdapter constructor.
     * listener setting for settings activity callbacks ,  context annotation and other
    */
    init {
//        this.sounds = sounds
//        this.context = context
//        this.listview = listview
        //
        val activity = context as Activity
        listener = activity as SoundsAdapterInterface
    }
}