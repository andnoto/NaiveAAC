package com.sampietro.NaiveAAC.activities.Stories

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

/**
 * this adapter is the View "supplier" for the listview in the UI for stories list settings.
 *
 * @version     4.0, 09/09/2023
 * @see com.sampietro.NaiveAAC.activities.Stories.Stories
 *
 * @see com.sampietro.NaiveAAC.activities.Settings.StoriesFragment
 *
 * @see com.sampietro.NaiveAAC.activities.Settings.SettingsActivity
 */
class StoriesAdapter(private val context: Context, private val stories: List<Stories>?, listview: ListView) :
    BaseAdapter() {
    /**
     * <h1>StoriesAdapterInterface</h1>
     *
     * **StoriesAdapterInterface**
     * interface used to refer to the Activity without having to explicitly use its class
     *
     *
     * this interface is initialized by the constructor.
     *
     * @see StoriesAdapter
     */
    interface StoriesAdapterInterface {
        fun reloadStoriesFragmentDeleteStories(position: Int)
        fun reloadStoriesFragmentForInsertion(position: Int)
        fun reloadStoriesFragmentForEditing(position: Int)
    }

    lateinit var listener: StoriesAdapterInterface

    //
//    private val stories: List<Stories>? = null
//    private val context: Context? = null
//    private val listview: ListView? = null

    // Realm
//    private val realm: Realm? = null

    /**
     * return size of list<Stories>
     *
     * @return int with size of list<Stories>
    </Stories></Stories> */
    override fun getCount(): Int {
        return stories!!.size
    }

    /**
     * return the element of a specified index within the list<Stories>
     *
     * @param position int index within the list<Stories>
     * @return object with the element within the list<Stories>
    </Stories></Stories></Stories> */
    override fun getItem(position: Int): Any {
        return stories!![position]
    }

    /**
     * return the hashCode of a specified index within the list<Stories>
     *
     * @param position int index within the list<Stories>
     * @return long with the the hashCode of the element within the list<Stories>
    </Stories></Stories></Stories> */
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
     * @see .getItem
     *
     * @see .clickListenerDeleteStories
    </Stories></Stories></Stories></Stories> */
    override fun getView(position: Int, v: View?, vg: ViewGroup): View {
        // 1) it is checked if the View passed in input is null and only in this
        // case is initialized with the LayoutInflater.
        // The LayoutInflater structures the View to be created as defined in the
        // XML layout indicated.
        // 2) The data will be taken from the object with the element within
        // the list <Stories> of position position retrieved using getItem.
        // 3) the listener for the delete button of the element within
        // the list <Stories> of position position is set
//        var v = v
        val v1: View
        if (v == null) {
            v1 = LayoutInflater.from(context).inflate(R.layout.activity_settings_row_stories, null)
            }
            else
            { v1 = v}
        val l = getItem(position) as Stories
        val txt = v1.findViewById<View>(R.id.imageDescriptionRow) as TextView
        //
        txt.text = (l.story + "-" + l.phraseNumberInt
                + "-" + l.wordNumberInt + " " + l.word)
        //
        val imgbtn = v1.findViewById<View>(R.id.btn_delete_image) as ImageButton
        imgbtn.setOnClickListener(clickListenerDeleteStories)
        //
        val imgbtnedit = v1.findViewById<View>(R.id.btn_edit_image) as ImageButton
        imgbtnedit.setOnClickListener(clickListenerEditStories)
        //
        val imgbtninsert = v1.findViewById<View>(R.id.btn_insert_image) as ImageButton
        imgbtninsert.setOnClickListener(clickListenerInsertStories)
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
     * @see StoriesAdapterInterface.reloadStoriesFragmentForInsertion
     */
    private val clickListenerInsertStories = View.OnClickListener { v ->
        val position = listview.getPositionForView(v)
        //
        listener.reloadStoriesFragmentForInsertion(position)
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
     * @see StoriesAdapterInterface.reloadStoriesFragmentForEditing
     */
    private val clickListenerEditStories = View.OnClickListener { v ->
        val position = listview.getPositionForView(v)
        //
        listener.reloadStoriesFragmentForEditing(position)
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
     * @see StoriesAdapterInterface.reloadStoriesFragmentDeleteStories
     */
    private val clickListenerDeleteStories = View.OnClickListener { v ->
        val position = listview.getPositionForView(v)
        //
        listener.reloadStoriesFragmentDeleteStories(position)
        //
    }

    /**
     * StoriesAdapter constructor.
     * listener setting for settings activity callbacks ,  context annotation and other
    */
    init {
//        this.stories = stories
//        this.context = context
//        this.listview = listview
        //
        val activity = context as Activity
        listener = activity as StoriesAdapterInterface
    }
}