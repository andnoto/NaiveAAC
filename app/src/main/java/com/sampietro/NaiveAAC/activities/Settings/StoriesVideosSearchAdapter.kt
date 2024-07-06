package com.sampietro.NaiveAAC.activities.Settings

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ListView
import android.widget.TextView
import com.sampietro.NaiveAAC.R
import com.sampietro.NaiveAAC.activities.Graphics.Videos
import io.realm.Realm

/**
 * this adapter is the View "supplier" for the listview in the UI for videos settings.
 *
 * @version     4.0, 09/09/2023
 * @see Videos
 *
 * @see StoriesVideosSearchFragment
 *
 * @see SettingsStoriesActivity
 */
class StoriesVideosSearchAdapter(
    private val context: Context,
    private val videos: List<Videos>?,
    private val listview: ListView,
    private val startingFragment: String?
) : BaseAdapter() {
    /**
     * <h1>StoriesVideosSearchAdapter</h1>
     *
     * **StoriesVideosSearchAdapter**
     * interface used to refer to the Activity without having to explicitly use its class
     *
     *
     * this interface is initialized by the constructor.
     *
     * @see StoriesVideosSearchAdapter
     */
    interface StoriesVideosSearchAdapterInterface {
        fun reloadStoriesFragmentFromVideoSearch(videoKey: String?)
        fun reloadStoriesFragmentFromActionAfterResponse(videoKey: String?)
    }

    lateinit var listener: StoriesVideosSearchAdapterInterface

    //
//    private val videos: List<Videos>? = null
//    private val context: Context? = null
//    private val listview: ListView? = null
//    private val startingFragment: String? = null

    // Realm
    private lateinit var realm: Realm

    /**
     * return size of list<Videos>
     *
     * @return int with size of list<Videos>
    </Videos></Videos> */
    override fun getCount(): Int {
        return videos!!.size
    }

    /**
     * return the element of a specified index within the list<Videos>
     *
     * @param position int index within the list<Videos>
     * @return object with the element within the list<Videos>
    </Videos></Videos></Videos> */
    override fun getItem(position: Int): Any {
        return videos!![position]
    }

    /**
     * return the hashCode of a specified index within the list<Videos>
     *
     * @param position int index within the list<Videos>
     * @return long with the the hashCode of the element within the list<Videos>
    </Videos></Videos></Videos> */
    override fun getItemId(position: Int): Long {
        return getItem(position).hashCode().toLong()
    }

    /**
     * return the view with the row corresponding to a specific index within the list<Videos>
     *
     * @param position int index within the list<Videos>
     * @param v view to inflate with the row corresponding to a specific index within the list<Videos>
     * @param vg viewgroup the parent that this view will eventually be attached to
     * @return view with the row corresponding to a specific index within the list<Videos>
     * @see .getItem
     *
     * @see .clickListenerSelectVideo
    </Videos></Videos></Videos></Videos> */
    override fun getView(position: Int, v: View?, vg: ViewGroup): View {
        // 1) it is checked if the View passed in input is null and only in this
        // case is initialized with the LayoutInflater.
        // The LayoutInflater structures the View to be created as defined in the
        // XML layout indicated.
        // 2) The data will be taken from the object with the element within
        // the list <Videos> of position position retrieved using getItem.
        // 3) the listener for item selection within
        // the list <Videos> of position position is set
        var v = v
        if (v==null)
        {
            v= LayoutInflater.from(context).inflate(R.layout.activity_settings_row_stories_images_videos_and_sounds_search, null)
        }
        val l = getItem(position) as Videos
        //
        val vdr = v!!.findViewById<View>(R.id.media_description_row) as TextView
        vdr.text = l.descrizione
        vdr.setOnClickListener(clickListenerSelectVideo)
        //
        return v
    }

    /**
     * listener for item selection.
     *
     *
     * it makes the callback to the activity to reload the fragment with the updated data
     *
     * @see View.OnClickListener
     *
     * @see StoriesVideosSearchAdapterInterface.reloadStoriesFragmentFromVideoSearch
     */
    private val clickListenerSelectVideo = View.OnClickListener { v ->
        val position = listview.getPositionForView(v)
        //
        realm = Realm.getDefaultInstance()
        val results = realm.where(Videos::class.java).findAll()
        val selectedVideo = results[position]!!
        if (startingFragment == "VideoSearch") {
            listener.reloadStoriesFragmentFromVideoSearch(selectedVideo.descrizione)
        } else {
            listener.reloadStoriesFragmentFromActionAfterResponse(selectedVideo.descrizione)
        }
        //
    }

    /**
     * StoriesVideosSearchAdapter constructor.
     * listener setting for settings activity callbacks ,  context annotation and other
     *
    */
    init {
//        this.videos = videos
//        this.context = context
//        this.listview = listview
//        this.startingFragment = startingFragment
        //
        val activity = context as Activity
        listener = activity as StoriesVideosSearchAdapterInterface
        //
    }
}