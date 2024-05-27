package com.sampietro.NaiveAAC.activities.Game.GameParameters

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

/**
 * this adapter is the View "supplier" for the listview in the UI for game parameters settings.
 *
 * @version     4.0, 09/09/2023
 * @see com.sampietro.NaiveAAC.activities.Game.GameParameters.GameParameters
 *
 * @see com.sampietro.NaiveAAC.activities.Settings.GameParametersSettingsFragment
 *
 * @see com.sampietro.NaiveAAC.activities.Settings.SettingsActivity
 */
class GameParametersAdapter(
    private val context: Context,
    private val gameParameters: List<GameParameters>?,
    listview: ListView
) : BaseAdapter() {
    /**
     * <h1>GameParametersAdapterInterface</h1>
     *
     * **GameParametersAdapterInterface**
     * interface used to refer to the Activity without having to explicitly use its class
     *
     *
     * this interface is initialized by the constructor.
     *
     * @see GameParametersAdapter
     */
    interface GameParametersAdapterInterface {
        fun reloadGameParametersFragmentDeleteGameParameters(position: Int)
        fun reloadGameParametersFragmentForEditing(position: Int)
    }

    lateinit var listener: GameParametersAdapterInterface

    /**
     * return size of list<GameParameters>
     *
     * @return int with size of list<GameParameters>
    */
    override fun getCount(): Int {
        return gameParameters!!.size
    }

    /**
     * return the element of a specified index within the list<GameParameters>
     *
     * @param position int index within the list<GameParameters>
     * @return object with the element within the list<GameParameters>
    */
    override fun getItem(position: Int): Any {
        return gameParameters!![position]
    }

    /**
     * return the hashCode of a specified index within the list<GameParameters>
     *
     * @param position int index within the list<GameParameters>
     * @return long with the the hashCode of the element within the list<GameParameters>
    */
    override fun getItemId(position: Int): Long {
        return getItem(position).hashCode().toLong()
    }

    /**
     * return the view with the row corresponding to a specific index within the list<GameParameters>
     *
     * @param position int index within the list<GameParameters>
     * @param v view to inflate with the row corresponding to a specific index within the list<GameParameters>
     * @param vg viewgroup the parent that this view will eventually be attached to
     * @return view with the row corresponding to a specific index within the list<GameParameters>
     * @see .getItem
     *
     * @see .clickListenerDeleteGameParameters
    */
    override fun getView(position: Int, v: View?, vg: ViewGroup): View {
        // 1) it is checked if the View passed in input is null and only in this
        // case is initialized with the LayoutInflater.
        // The LayoutInflater structures the View to be created as defined in the
        // XML layout indicated.
        // 2) The data will be taken from the object with the element within
        // the list <GameParameters> of position position retrieved using getItem.
        // 3) the listener for the delete button of the element within
        // the list <GameParameters> of position position is set
        val v1: View
        if (v == null) {
            v1 = LayoutInflater.from(context)
                .inflate(R.layout.activity_settings_row_game_parameters, null)
            }
            else
            { v1 = v}
        val l = getItem(position) as GameParameters
        val txt = v1.findViewById<View>(R.id.imageDescriptionRow) as TextView
        txt.text = l.gameName + "-" + l.gameParameter
        //
        val edtimgbtn = v1.findViewById<View>(R.id.btn_edit_image) as ImageButton
        edtimgbtn.setOnClickListener(clickListenerEditGameParameters)
        //
        val delimgbtn = v1.findViewById<View>(R.id.btn_delete_image) as ImageButton
        delimgbtn.setOnClickListener(clickListenerDeleteGameParameters)
        //
        return v1
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
     * @see GameParametersAdapter.GameParametersAdapterInterface.reloadGameParametersFragmentForEditing
     */
    private val clickListenerEditGameParameters = View.OnClickListener { v ->
        val position = listview.getPositionForView(v)
        listener.reloadGameParametersFragmentForEditing(position)
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
     * @see GameParametersAdapterInterface.reloadGameParametersFragmentDeleteGameParameters
     */
    private val clickListenerDeleteGameParameters = View.OnClickListener { v ->
        val position = listview.getPositionForView(v)
        // delete
        listener.reloadGameParametersFragmentDeleteGameParameters(position)
        //
    }

    /**
     * GameParametersAdapter constructor.
     * listener setting for settings activity callbacks ,  context annotation and other
     *
    */
    init {
        val activity = context as Activity
        listener = activity as GameParametersAdapterInterface
    }
}