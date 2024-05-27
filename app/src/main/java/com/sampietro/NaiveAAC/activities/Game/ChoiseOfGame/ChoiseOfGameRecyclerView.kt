package com.sampietro.NaiveAAC.activities.Game.ChoiseOfGame

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.graphics.Point
import android.media.MediaPlayer
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.VideoView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sampietro.NaiveAAC.R
import com.sampietro.NaiveAAC.activities.Game.Utils.CenterVideoView
import java.util.Objects

/**
 * <h1>ChoiseOfGameRecyclerView</h1>
 *
 * **ChoiseOfGameRecyclerView** Custom RecyclerView for game choice
 *
 * REFER to [codingwithmitch](https://codingwithmitch.com/blog/playing-video-recyclerview-exoplayer-android/)
 * by [Mitch Tabian](https://codingwithmitch.com/)
 *
 * @version     4.0, 09/09/2023
 * @see RecyclerView
 *
 * @see CenterVideoView
 *
 * @see ChoiseOfGameArrayList
 */
open class ChoiseOfGameRecyclerView : RecyclerView {
    //
    var image: ImageView? = null
    var viewHolderItemView: View? = null
    var frameLayout: FrameLayout? = null
    lateinit var centerVideoView: CenterVideoView

    //
    private var gameArrayList = ArrayList<ChoiseOfGameArrayList>()
    private var videoSurfaceDefaultWidth = 0
    private var playPosition = -1
    private var isVideoViewAdded = false

    /**
     * ChoiseOfGameRecyclerView constructor.
     * init RecyclerView
     *
     * @param context context
     */
    constructor(context: Context) : super(context) {
        init(context)
    }

    /**
     * ChoiseOfGameRecyclerView constructor.
     * init RecyclerView
     *
     * @param context context
     * @param attrs attributeSet
     */
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context)
    }

    /**
     * init RecyclerView , set listeners
     *
     * @param context context
     * @see playVideo
     *
     * @see resetVideoView
     */
    fun init(context: Context) {
        val contextA: Context = activity!!
        val display =
            (getContext().getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay
        val point = Point()
        display.getSize(point)
        // getSize Gets the size of the display, in pixels.
        // Value returned by this method does not necessarily represent the actual raw size
        // (native resolution) of the display.
        videoSurfaceDefaultWidth = point.x
        //
        centerVideoView = CenterVideoView(contextA)
        centerVideoView.setVideoSize(200, 200)
        centerVideoView.setOnInfoListener { mediaPlayer, i, i1 ->
            if (i == MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START) {
                centerVideoView.setBackgroundResource(0)
            }
            false
        }
        //
        addOnScrollListener(object : OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == SCROLL_STATE_IDLE) {
                    if (image != null) {
                        image!!.visibility = VISIBLE
                    }

                    // special case when the end of the list has been reached.
                    if (!recyclerView.canScrollHorizontally(1)) //                            || !recyclerView.canScrollHorizontally(-1))
                    {
                        playVideo(true)
                    } else {
                        playVideo(false)
                    }
                }
            }

            //
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
            }
        })
        //
        addOnChildAttachStateChangeListener(object : OnChildAttachStateChangeListener {
            override fun onChildViewAttachedToWindow(view: View) {
                //    all'apertura, inserire qui video primo item?
            }

            //
            override fun onChildViewDetachedFromWindow(view: View) {
                if (viewHolderItemView != null && viewHolderItemView == view) {
                    resetVideoView()
                }
            }
        })
    }

    /**
     * init RecyclerView , set listeners
     *
     * @param isEndOfList boolean true when position is at end of list
     * @see getVisibleVideoSurfaceWidth
     *
     * @see removeVideoView
     *
     * @see addVideoView
     */
    open fun playVideo(isEndOfList: Boolean) {
        val targetPosition: Int
        //
        if (!isEndOfList) {
            val startPosition = (Objects.requireNonNull(
                layoutManager
            ) as LinearLayoutManager).findFirstVisibleItemPosition()
            var endPosition =
                (layoutManager as LinearLayoutManager?)!!.findLastVisibleItemPosition()
            // if there is more than 2 list-items on the screen, set the difference to be 1
            if (endPosition - startPosition > 1) {
                endPosition = startPosition + 1
            }
            targetPosition = if (startPosition != endPosition) {
                // there is more than 1 list-item on the screen
                // he chooses between the first and second list-items
                // the one that occupies the screen the most
                val startPositionVideoWidth = getVisibleVideoSurfaceWidth(startPosition)
                val endPositionVideoWidth = getVisibleVideoSurfaceWidth(endPosition)
                if (startPositionVideoWidth > endPositionVideoWidth) startPosition else endPosition
            } else {
                // there is 1 list-item on the screen
                startPosition
            }
        } else {
            targetPosition = gameArrayList.size - 1
        }
        // video is already playing
        if (targetPosition == playPosition) {
            return
        }
        // set the position of the list-item that is to be played
        playPosition = targetPosition
        // remove old video views
        removeVideoView(centerVideoView)
        //
        val targetPositionAmongTheVisibleItems = (targetPosition
                - (Objects.requireNonNull(
            layoutManager
        ) as LinearLayoutManager).findFirstVisibleItemPosition())
        val child = getChildAt(targetPositionAmongTheVisibleItems) ?: return
        val holder = child.tag as ChoiseOfGameRecyclerViewViewHolder
        if (holder == null) {
            playPosition = -1
            return
        }
        image = holder.imageView
        viewHolderItemView = holder.itemView
        frameLayout = holder.itemView.findViewById(R.id.media_container)
        //
        val videoPath = gameArrayList[targetPosition].urlVideo
        if (videoPath != context!!.getString(R.string.non_trovato)) {
            //
            if (!isVideoViewAdded) {
                addVideoView()
            }
            centerVideoView.setBackgroundResource(R.drawable.white_background)
            centerVideoView.setVideoPath(videoPath)
            centerVideoView.seekTo(1)
            centerVideoView.start()
            centerVideoView.setOnPreparedListener { mp -> mp.isLooping = true }
        }
    }

    /**
     * Returns the visible region of the video surface on the screen.
     * if some is cut off, it will return less than the @videoSurfaceDefaultHeight
     *
     * @param position int with position in game list
     * @return int visible video surface width for the video in the param position
     */
    fun getVisibleVideoSurfaceWidth(position: Int): Int {
        val positionAmongTheVisibleItems = (position
                - (Objects.requireNonNull(
            layoutManager
        ) as LinearLayoutManager).findFirstVisibleItemPosition())
        val child = getChildAt(positionAmongTheVisibleItems) ?: return 0
        //
        val location = IntArray(2)
        // getLocationInWindow Computes the coordinates of this view in its window.
        // The argument must be an array of two integers.
        // After the method returns, the array contains the x and y location in that order.
        child.getLocationInWindow(location)
        //
        return if (location[0] < 0) {
            location[0] + videoSurfaceDefaultWidth
        } else {
            videoSurfaceDefaultWidth - location[0]
        }
    }

    /**
     * Remove the old player.
     *
     * @param videoView videoview to remove
     */
    fun removeVideoView(videoView: VideoView?) {
        centerVideoView.setBackgroundResource(R.drawable.white_background)
        val parentViewParent = videoView?.parent ?: return
        val parent: ViewGroup = parentViewParent as ViewGroup? ?: return
        val indexOfChild = parent.indexOfChild(videoView)
        if (indexOfChild >= 0) {
            parent.removeViewAt(indexOfChild)
            isVideoViewAdded = false
            viewHolderItemView!!.setOnClickListener(null)
        }
        //
        image!!.visibility = VISIBLE
    }

    /**
     * Add the new videoview.
     *
     */
    fun addVideoView() {
        frameLayout!!.addView(centerVideoView)
        isVideoViewAdded = true
        //        centerVideoView.requestFocus();
        centerVideoView.visibility = VISIBLE
        centerVideoView.alpha = 1f
    }

    /**
     * Remove the old player and reset video view
     *
     */
    open fun resetVideoView() {
        if (isVideoViewAdded) {
            removeVideoView(centerVideoView)
            playPosition = -1
            centerVideoView.visibility = INVISIBLE
            image!!.visibility = VISIBLE
        }
    }
    /**
     * stop playback
     *
     */
    fun releasePlayer() {
        centerVideoView.stopPlayback()
        viewHolderItemView = null
    }
    /**
     * set game list
     *
     * @param gameArrayList arraylist<ChoiseOfGameArrayList> to set
     * @see ChoiseOfGameArrayList
    */
    fun setMediaObjects(gameArrayList: ArrayList<ChoiseOfGameArrayList>) {
        this.gameArrayList = gameArrayList
    }

    //
    private val activity: Activity?
        private get() {
            var context = getContext()
            while (context is ContextWrapper) {
                if (context is Activity) {
                    return context
                }
                context = context.baseContext
            }
            return null
        }
}