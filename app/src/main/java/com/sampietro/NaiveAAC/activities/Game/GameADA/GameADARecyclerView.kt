package com.sampietro.NaiveAAC.activities.Game.GameADA

import android.widget.FrameLayout
import com.sampietro.NaiveAAC.activities.Game.Utils.CenterVideoView
import android.media.MediaPlayer
import android.view.WindowManager
import com.sampietro.NaiveAAC.R
import android.widget.VideoView
import android.view.ViewGroup
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.graphics.Point
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.io.IOException
import java.util.*

/**
 * <h1>GameADARecyclerView</h1>
 *
 * **GameADARecyclerView** Custom RecyclerView for game choice
 *
 * Refer to [codingwithmitch](https://codingwithmitch.com/blog/playing-video-recyclerview-exoplayer-android/)
 * by [Mitch Tabian](https://codingwithmitch.com/)
 * THIS CLASS WAS CREATED TO ASSOCIATE VIDEOS AND SOUNDS WITH WORDS (AS WELL AS IMAGES AND TTS)
 *
 * @version     4.0, 09/09/2023
 * @see RecyclerView
 *
 * @see CenterVideoView
 *
 * @see GameADAArrayList
 */
class GameADARecyclerView : RecyclerView {
    //
    private var image: ImageView? = null
    private var viewHolderItemView: View? = null
    private var frameLayout: FrameLayout? = null
    private var centerVideoView: CenterVideoView? = null

    //
    private var gameArrayList = ArrayList<GameADAArrayList>()
    private var videoSurfaceDefaultWidth = 0
//    private var context: Context? = null
    private var playPosition = -1
    private var isVideoViewAdded = false

    //
    private var soundMediaPlayer: MediaPlayer? = null

    //
    private var gameUseVideoAndSound: String? = null

    /**
     * GameADARecyclerView constructor.
     * init RecyclerView
     *
     * @param context context
     */
    constructor(context: Context) : super(context) {
        init(context)
    }

    /**
     * GameADARecyclerView constructor.
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
     * @see .playVideo
     *
     * @see .resetVideoView
     */
    private fun init(context: Context) {
//        this.context = activity
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
        centerVideoView!!.setVideoSize(200, 200)
        centerVideoView!!.setOnInfoListener { mediaPlayer, i, i1 ->
            if (i == MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START) {
                centerVideoView!!.setBackgroundResource(0)
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
     * @see .getVisibleVideoSurfaceWidth
     *
     * @see .removeVideoView
     *
     * @see .addVideoView
     */
    fun playVideo(isEndOfList: Boolean) {
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
        if (centerVideoView == null) {
            return
        }
        // remove old video views
        removeVideoView(centerVideoView)
        //
        val targetPositionAmongTheVisibleItems = (targetPosition
                - (Objects.requireNonNull(
            layoutManager
        ) as LinearLayoutManager).findFirstVisibleItemPosition())
        val child = getChildAt(targetPositionAmongTheVisibleItems) ?: return
        val holder = child.tag as GameADARecyclerViewViewHolder
        if (holder == null) {
            playPosition = -1
            return
        }
        image = holder.img
        viewHolderItemView = holder.itemView
        frameLayout = holder.itemView.findViewById(R.id.game_ada_media_container)
        //
        if (soundMediaPlayer != null
            && gameArrayList[targetPosition].soundAssociatedWithThePhraseReplacesTheOtherSounds != "Y"
        ) {
            if (soundMediaPlayer!!.isPlaying) {
                soundMediaPlayer!!.stop()
            }
            soundMediaPlayer!!.release()
            soundMediaPlayer = null
        }
        //
        val soundPath = gameArrayList[targetPosition].sound
        if (soundPath != null && gameUseVideoAndSound == "Y"
            && gameArrayList[targetPosition].soundReplacesTTS != "Y"
            && gameArrayList[targetPosition].soundAssociatedWithThePhraseReplacesTheOtherSounds != "Y"
        ) {
            if (soundPath != context!!.getString(R.string.non_trovato)) {
                // play audio file using MediaPlayer
                try {
                    soundMediaPlayer = MediaPlayer()
                    soundMediaPlayer!!.setDataSource(soundPath)
                    soundMediaPlayer!!.prepare()
                    soundMediaPlayer!!.start()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
        //
        val videoPath = gameArrayList[targetPosition].video
        if (videoPath != null && gameUseVideoAndSound == "Y") {
            if (videoPath != context!!.getString(R.string.non_trovato)) {
                //
                if (!isVideoViewAdded) {
                    addVideoView()
                }
                centerVideoView!!.setBackgroundResource(R.drawable.white_background)
                centerVideoView!!.setVideoPath(videoPath)
                centerVideoView!!.seekTo(1)
                centerVideoView!!.start()
                centerVideoView!!.setOnPreparedListener { mp ->
                    mp.setVolume(0f, 0f)
                    mp.isLooping = true
                }
            }
        }
    }

    /**
     * Returns the visible region of the video surface on the screen.
     * if some is cut off, it will return less than the @videoSurfaceDefaultHeight
     *
     * @param position int with position in game list
     * @return int visible video surface width for the video in the param position
     */
    private fun getVisibleVideoSurfaceWidth(position: Int): Int {
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
    private fun removeVideoView(videoView: VideoView?) {
        centerVideoView!!.setBackgroundResource(R.drawable.white_background)
//        val parent = videoView!!.parent as ViewGroup ?: return
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
    private fun addVideoView() {
        frameLayout!!.addView(centerVideoView)
        isVideoViewAdded = true
        centerVideoView!!.visibility = VISIBLE
        centerVideoView!!.alpha = 1f
    }

    /**
     * Remove the old player and reset video view
     *
     */
    private fun resetVideoView() {
        //
        if (soundMediaPlayer != null) {
            if (soundMediaPlayer!!.isPlaying) {
                soundMediaPlayer!!.stop()
            }
        }
        //
        if (isVideoViewAdded) {
            removeVideoView(centerVideoView)
            playPosition = -1
            centerVideoView!!.visibility = INVISIBLE
            image!!.visibility = VISIBLE
        }
    }

    /**
     * stop playback
     *
     */
    fun releasePlayer() {
        //
        centerVideoView!!.stopPlayback()
        viewHolderItemView = null
    }

    /**
     * set game list
     *
     * @param gameArrayList arraylist<GameADAArrayList> to set
     * @see GameADAArrayList
    </GameADAArrayList> */
    fun setMediaObjects(gameArrayList: ArrayList<GameADAArrayList>) {
        this.gameArrayList = gameArrayList
    }

    /**
     * set if game use video and sound
     *
     * @param gameUseVideoAndSound string to set
     */
    fun setGameUseVideoAndSound(gameUseVideoAndSound: String?) {
        this.gameUseVideoAndSound = gameUseVideoAndSound
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