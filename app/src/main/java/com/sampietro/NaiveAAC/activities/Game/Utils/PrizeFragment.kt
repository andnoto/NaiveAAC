package com.sampietro.NaiveAAC.activities.Game.Utils

import android.widget.TextView
import android.media.MediaPlayer
import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.os.Bundle
import com.sampietro.NaiveAAC.R
import com.sampietro.NaiveAAC.activities.Graphics.Videos
import android.media.MediaMetadataRetriever
import android.graphics.Bitmap
import android.net.Uri
import android.view.View
import androidx.fragment.app.Fragment
import io.realm.Realm
import java.io.IOException
import java.lang.RuntimeException

/**
 * <h1>PrizeFragment</h1>
 *
 * **PrizeFragment** UI for prize
 * upload a video as a reward
 *
 *
 * @version     4.0, 09/09/2023
 */
class PrizeFragment : Fragment() {
    private lateinit var realm: Realm

    //
    var uri: Uri? = null
    var stringUri: String? = null

    //
    lateinit var rootView: View
    lateinit var textView: TextView
    lateinit var ctext: Context

    //
//    private val mediaPlayer: MediaPlayer? = null

    //
    var prizeVideoView: CenterVideoView? = null
    var videoWidth = 0
    var videoHeight = 0

    /**
     * <h1>onFragmentEventListenerPrize</h1>
     *
     * **onFragmentEventListenerPrize**
     * interface used to refer to the Activity without having to explicitly use its class
     *
     *
     *
     * @see .onAttach
     */
    interface onFragmentEventListenerPrize {
        // insert here any references to the Activity
        fun receiveResultImagesFromPrizeFragment(v: View?)
        fun receiveResultOnCompletatioVideoFromPrizeFragment(v: View?)
    }

    /**
     * listener setting for game activities callbacks , context annotation, realm get default instance
     *
     * @see androidx.fragment.app.Fragment.onAttach
     */
    private lateinit var listener: onFragmentEventListenerPrize
    override fun onAttach(context: Context) {
        super.onAttach(context)
        val activity = context as Activity
        listener = activity as onFragmentEventListenerPrize
        //
        ctext = context
        //
        realm = Realm.getDefaultInstance()
    }

    /**
     * prepares the ui and makes the callback to the activity
     *
     *
     *
     * @see androidx.fragment.app.Fragment.onCreateView
     *
     * @see com.sampietro.NaiveAAC.activities.Graphics.ImageSearchHelper.imageSearch
     *
     * @see MediaPlayer
     *
     * @see Videos
     *
     * @see getVideoWidthOrHeight
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        rootView = inflater.inflate(R.layout.activity_game_prize, container, false)
        //
        textView = rootView.findViewById(R.id.copyright)
        // Find your VideoView in your video_main.xml layout
        prizeVideoView = rootView.findViewById<View>(R.id.prizeVideoView) as CenterVideoView
        //
        prizeVideoView!!.setOnCompletionListener { mediaPlayer -> //
            prizeVideoView!!.visibility = View.INVISIBLE
            //
            mediaPlayer.release()
            // remove the VideoView otherwise it exits the Activity
            val parent = prizeVideoView!!.parent as ViewGroup
            val index = parent.indexOfChild(prizeVideoView)
            if (index >= 0) {
                parent.removeViewAt(index)
            }
            //
            listener.receiveResultOnCompletatioVideoFromPrizeFragment(rootView)
        }
        //
        val bundle = this.arguments
        var uriPremiumVideo: String? = ""
        if (bundle != null) {
            uriPremiumVideo = bundle.getString(getString(R.string.uri_premium_video))
        }
        //
        if (uriPremiumVideo == "" || uriPremiumVideo == " ") {
            uriPremiumVideo = getString(R.string.prize)
        }
        // I create a random integer 0 or 1 to be used to dispense the prize 50% of the time
        // Random rn = new Random();
        // int rn01 = rn.nextInt(2);
        //
        // if (rn01 == 1) {
        val results = realm.where(Videos::class.java)
            .equalTo(getString(R.string.descrizione), uriPremiumVideo).findAll()
        val count = results.size
        if (count != 0) {
            val result = results[0]!!
            stringUri = result.uri
            textView.setText(result.copyright)
            //
            prizeVideoView!!.setVideoURI(Uri.parse(stringUri))
            //
            videoWidth = getVideoWidthOrHeight(stringUri, getString(R.string.width))
            videoHeight = getVideoWidthOrHeight(stringUri, getString(R.string.height))
            prizeVideoView!!.setVideoSize(videoWidth, videoHeight)
            // start a video
            prizeVideoView!!.start()
            //
        } else {
            // if the video award does not exist, it warns the activity that the award
            // has been completed and therefore you can continue
            prizeVideoView!!.visibility = View.INVISIBLE
            //
            listener.receiveResultOnCompletatioVideoFromPrizeFragment(rootView)
        }
        listener.receiveResultImagesFromPrizeFragment(rootView)
        return rootView
    }

    /**
     * determine video width and height
     *
     *
     * Refer to [stackoverflow](https://stackoverflow.com/questions/9077436/how-to-determine-video-width-and-height-on-android)
     * answer of  [user493244](https://stackoverflow.com/users/493244/user493244)
     *
     * @param file string with file pathname to use
     * @param widthOrHeight string with size required (width or height)
     * @return mWidthHeight int with video width or height
     */
    fun getVideoWidthOrHeight(file: String?, widthOrHeight: String): Int {
        var retriever: MediaMetadataRetriever? = null
        val bmp: Bitmap?
        //
        var mWidthHeight = 0
        try {
            retriever = MediaMetadataRetriever()
            //
            retriever.setDataSource(file)
            bmp = retriever.frameAtTime
            mWidthHeight = if (widthOrHeight == getString(R.string.width)) {
                bmp!!.width
            } else {
                bmp!!.height
            }
        } catch (e: RuntimeException) {
            e.printStackTrace()
        } finally {
            if (retriever != null) {
                try {
                    retriever.release()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
        return mWidthHeight
    }
}