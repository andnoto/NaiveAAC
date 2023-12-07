package com.sampietro.NaiveAAC.activities.Game.GameADA

import com.sampietro.NaiveAAC.activities.Game.Utils.CenterVideoView
import android.media.MediaPlayer
import com.sampietro.NaiveAAC.R
import android.content.Context
import android.util.AttributeSet
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sampietro.NaiveAAC.activities.Game.ChoiseOfGame.ChoiseOfGameRecyclerView
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
 * @see ChoiseOfGameRecyclerView
 * @see RecyclerView
 *
 * @see CenterVideoView
 *
 * @see GameADAArrayList
 */
class GameADARecyclerView : ChoiseOfGameRecyclerView {
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
    constructor(context: Context) : super(context)
    /**
     * GameADARecyclerView constructor.
     * init RecyclerView
     *
     * @param context context
     * @param attrs attributeSet
     */
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
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
    override fun playVideo(isEndOfList: Boolean) {
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
                centerVideoView.setBackgroundResource(R.drawable.white_background)
                centerVideoView.setVideoPath(videoPath)
                centerVideoView.seekTo(1)
                centerVideoView.start()
                centerVideoView.setOnPreparedListener { mp ->
                    mp.setVolume(0f, 0f)
                    mp.isLooping = true
                }
            }
        }
    }
    /**
     * Remove the old player and reset video view
     *
     */
    override fun resetVideoView() {
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
            centerVideoView.visibility = INVISIBLE
            image!!.visibility = VISIBLE
        }
    }
    /**
     * set game list
     *
     * @param gameArrayList arraylist<GameADAArrayList> to set
     * @see GameADAArrayList
    </GameADAArrayList> */
    fun setMediaObjectsGameADA(gameArrayList: ArrayList<GameADAArrayList>) {
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
}