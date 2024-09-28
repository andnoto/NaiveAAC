package com.sampietro.NaiveAAC.activities.Settings

import android.app.Activity
import android.media.MediaPlayer
import android.media.MediaPlayer.OnCompletionListener
import android.media.MediaPlayer.OnPreparedListener
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.EditText
import com.sampietro.NaiveAAC.R
import com.sampietro.NaiveAAC.activities.BaseAndAbstractClass.FragmentAbstractClassWithoutConstructor

/**
 * <h1>VideosFragment</h1>
 *
 * **VideosFragment** UI for videos settings
 *
 *
 * @version     5.0, 01/04/2024
 * @see FragmentAbstractClassWithoutConstructor
 *
 * @see SurfaceHolder
 *
 * @see SettingsActivity
 */
class VideosFragment() : FragmentAbstractClassWithoutConstructor(),
    SurfaceHolder.Callback
    {
    var activity: Activity? = null
    //
    var uri: Uri? = null

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
     * prepares the ui also using a listview and a surfaceview
     *
     * @see androidx.fragment.app.Fragment.onViewCreated
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
//    override fun onViewCreated(
//        view: View,
//        savedInstanceState: Bundle?
//    ) {
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
        return rootView
    }
}