package com.sampietro.NaiveAAC.activities.Settings

import com.sampietro.NaiveAAC.activities.Settings.Utils.SettingsFragmentAbstractClass
import android.app.Activity
import com.sampietro.NaiveAAC.activities.Graphics.VideosAdapter
import android.media.MediaPlayer
import android.media.MediaPlayer.OnPreparedListener
import android.media.MediaPlayer.OnCompletionListener
import android.net.Uri
import android.os.Bundle
import android.view.*
import com.sampietro.NaiveAAC.R
import android.widget.EditText
import android.widget.ListView
import com.sampietro.NaiveAAC.activities.Graphics.Videos
import io.realm.Realm

/**
 * <h1>VideosFragment</h1>
 *
 * **VideosFragment** UI for videos settings
 *
 *
 * @version     4.0, 09/09/2023
 * @see SettingsFragmentAbstractClass
 *
 * @see SurfaceHolder
 *
 * @see SettingsActivity
 */
class VideosFragment : SettingsFragmentAbstractClass(), SurfaceHolder.Callback {
    var activity: Activity? = null

    //
    private lateinit var realm: Realm

    //
    var uri: Uri? = null

    //
    private lateinit var listView: ListView
    private var adapter: VideosAdapter? = null

    // for videos
    // the fragment implements the SurfaceHolder.Callback interface,
    // which requires the override of three methods: surfaceCreated, surfaceChanged, surfaceDestroyed.
    // Their invocation will occur, respectively, the first time the surface comes created,
    // whenever it undergoes changes and when it is destroyed.
    // Within the surfaceCreated method, once the surface has been created, we can start the video.
    // Within the onCreateView method we assign a layout to the UI and make the SurfaceHolder use the fragment
    // as a listener for its own callback events.
    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var holder: SurfaceHolder
    private var surface: SurfaceView? = null

    /**
     * implements the SurfaceHolder.Callback interface.
     * start the video.
     *
     * @see SurfaceHolder.Callback
     */
    override fun surfaceCreated(surfaceHolder: SurfaceHolder) {
        // 1) a reference to the MediaPlayer is retrieved via create (),passing the movie uri as an argument.
        // 2) the display is assigned to the MediaPlayer indicating which will be the container of the video.
        // 3) we invoke the MediaPlayer # start () method within an OnPreparedListener class listener.
        // 4) within an OnCompletionListener type listener, we invoke the method MediaPlayer # release ().
        mediaPlayer = MediaPlayer.create(ctext, uri)
        mediaPlayer.setDisplay(surfaceHolder)
        mediaPlayer.setOnPreparedListener(
            OnPreparedListener { mediaPlayer -> mediaPlayer.start() }
        )
        mediaPlayer.setOnCompletionListener(OnCompletionListener { mediaPlayer -> mediaPlayer.release() })
    }

    /**
     * implements the SurfaceHolder.Callback interface.
     *
     * @see SurfaceHolder.Callback
     */
    override fun surfaceChanged(surfaceHolder: SurfaceHolder, i: Int, i2: Int, i3: Int) {}

    /**
     * implements the SurfaceHolder.Callback interface.
     *
     * @see SurfaceHolder.Callback
     */
    override fun surfaceDestroyed(surfaceHolder: SurfaceHolder) {}

    /**
     * prepares the ui also using a listview and a surfaceview and makes the callback to the activity
     *
     * @see androidx.fragment.app.Fragment.onCreateView
     *
     * @see com.sampietro.NaiveAAC.activities.Graphics.Videos
     *
     * @see com.sampietro.NaiveAAC.activities.Graphics.VideosAdapter
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        rootView = inflater.inflate(R.layout.activity_settings_videos, container, false)
        // logic of fragment
        val bundle = this.arguments
        val stringUri: String?
        //
        val descrizione: String?
        //
        if (bundle != null) {
            //
            descrizione = bundle.getString("descrizione")
            val vidD = rootView.findViewById<View>(R.id.videoDescription) as EditText
            vidD.setText(descrizione)
            //
            stringUri = bundle.getString("URI")
            if (stringUri != "none") {
                uri = Uri.parse(stringUri)
                //
                surface = rootView.findViewById<View>(R.id.surfView) as SurfaceView
                holder = surface!!.holder
                holder.addCallback(this)
            }
        }
        //
        realm = Realm.getDefaultInstance()
        // ListView
        // 1) we get a reference to the data structure through the RealmResults class which constitutes
        // the query result set and is an iterable collection accessible with Java constructs:
        // for loop, basic access to position and Iterator.
        // The approach for realm queries is object-oriented.
        // The where method will retrieve the objects from the specified class and the result will
        // be treated with filtrate by other specific methods.
        // At the end of the selection configuration, the findAll method will be invoked to retrieve
        // all the corresponding results.
        // 2) we instantiate an Adapter by assigning it, the collection and the layout related to
        // each single row
        // 3) we retrieve the ListView prepared in the layout and assign it the reference to the adapter
        // which will be your View "supplier".
        val results = realm.where(Videos::class.java).findAll()
        //
        listView = rootView.findViewById<View>(R.id.listview) as ListView
        //
        adapter = VideosAdapter(ctext, results, listView)
        //
        listView.adapter = adapter
        //
        listener.receiveResultSettings(rootView)
        //
        return rootView
    }
}