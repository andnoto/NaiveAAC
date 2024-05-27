package com.sampietro.NaiveAAC.activities.Game.Utils

import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.sampietro.NaiveAAC.R
import java.util.Locale

/**
 * <h1>YoutubePrizeFragment</h1>
 *
 * **YoutubePrizeFragment** UI for prize
 * upload a Youtube video as a reward
 *
 * Refer to [stackoverflow](https://stackoverflow.com/questions/4654878/how-to-play-youtube-video-in-my-android-application)
 * answer of [JanB](https://stackoverflow.com/users/957925/janb)
 *
 * @version     5.0, 01/04/2024
 */
class YoutubePrizeFragment : Fragment() {
    lateinit var rootView: View
    var textView: TextView? = null
    lateinit var ctext: Context

    //
    var mWebView: WebView? = null
    val videoStr1 = "<html><body>Promo video from <br>"
    val videoStr2Portrait =
        "<iframe width=\"426\" height=\"240\" src=\"https://www.youtube.com/embed/"
    val videoStr2Landscape =
        "<iframe width=\"213\" height=\"120\" src=\"https://www.youtube.com/embed/"
    val videoStr3 = "\" frameborder=\"0\" allowfullscreen></iframe></body></html>"
    val wwwUrlSplitBy = "v="
    val androidUrlSplitBy = "be/"

    //
    lateinit var listener: onFragmentEventListenerYoutubePrize

    /**
     * <h1>onFragmentEventListenerYoutubePrize</h1>
     *
     * **onFragmentEventListenerYoutubePrize**
     * interface used to refer to the Activity without having to explicitly use its class
     *
     *
     *
     * @see onAttach
     */
    interface onFragmentEventListenerYoutubePrize {
        // insert here any references to the Activity
        fun receiveResultImagesFromYoutubePrizeFragment(v: View?)
        fun receiveResultOnCompletatioVideoFromYoutubePrizeFragment(v: View?)
    }

    /**
     * listener setting for game activities callbacks , context annotation
     *
     * @see Fragment.onAttach
     */
    override fun onAttach(context: Context) {
        super.onAttach(context)
        val activity = context as Activity
        listener = activity as onFragmentEventListenerYoutubePrize
        //
        ctext = context
    }

    /**
     * prepares the ui and makes the callback to the activity
     *
     *
     *
     * @see Fragment.onCreateView
     *
     * @see WebViewClient
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        rootView = inflater.inflate(R.layout.activity_game_prize_youtube, container, false)
        //
        mWebView = rootView.findViewById<View>(R.id.youtubevideoview) as WebView
        //
        val bundle = this.arguments
        var uriPremiumVideo: String? = ""
        if (bundle != null) {
            uriPremiumVideo = bundle.getString(getString(R.string.uri_premium_video))
        }
        //
        if (!uriPremiumVideo!!.uppercase(Locale.getDefault()).contains(
                wwwUrlSplitBy.uppercase(
                    Locale.getDefault()
                )
            ) &&
            !uriPremiumVideo.uppercase(Locale.getDefault())
                .contains(androidUrlSplitBy.uppercase(Locale.getDefault()))
        ) {
            // if the video award does not exist, it warns the activity that the award
            // has been completed and therefore you can continue
            mWebView!!.visibility = View.INVISIBLE
            //
            listener.receiveResultOnCompletatioVideoFromYoutubePrizeFragment(rootView)
        } else {
            // build your own src link with your video ID
            val oneWord: Array<String?>
            oneWord = if (uriPremiumVideo.uppercase(Locale.getDefault()).contains(
                    wwwUrlSplitBy.uppercase(
                        Locale.getDefault()
                    )
                )
            ) {
                uriPremiumVideo.split(wwwUrlSplitBy).dropLastWhile { it.isEmpty() }.toTypedArray()
            } else {
                uriPremiumVideo.split(androidUrlSplitBy).dropLastWhile { it.isEmpty() }
                    .toTypedArray()
            }
            // builds the src link (including size and address)
            // The Right YouTube Dimensions = 426X240
            val orientation = this.resources.configuration.orientation
            val videoStr: String
            videoStr = if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                // code for portrait mode
                videoStr1 + uriPremiumVideo + videoStr2Portrait + oneWord[1] + videoStr3
            } else {
                // code for landscape mode
                videoStr1 + uriPremiumVideo + videoStr2Landscape + oneWord[1] + videoStr3
            }
            //
            mWebView!!.webViewClient = object : WebViewClient() {
                override fun shouldOverrideUrlLoading(
                    view: WebView,
                    request: WebResourceRequest
                ): Boolean {
                    return false
                }

                override fun onPageStarted(view: WebView, url: String, favicon: Bitmap?) {
                    val TIME_OUT = 180000
                    Handler(Looper.getMainLooper()).postDelayed({
                        listener.receiveResultOnCompletatioVideoFromYoutubePrizeFragment(
                            rootView
                        )
                                                                }, TIME_OUT.toLong())
                }
            }
            val ws = mWebView!!.settings
            ws.javaScriptEnabled = true
            mWebView!!.loadData(videoStr, "text/html", "utf-8")
        }
        listener.receiveResultImagesFromYoutubePrizeFragment(rootView)
        return rootView
    }

    /**
     * destroy WebView
     *
     *
     *
     * @see Fragment.onDestroyView
     */
    override fun onDestroyView() {
        mWebView!!.destroy()
        mWebView = null
        super.onDestroyView()
    }
}