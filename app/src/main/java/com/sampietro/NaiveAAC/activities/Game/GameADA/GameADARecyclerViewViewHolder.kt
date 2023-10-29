package com.sampietro.NaiveAAC.activities.Game.GameADA

import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sampietro.NaiveAAC.R

/**
 * <h1>GameADARecyclerViewViewHolder</h1>
 *
 * **GameADARecyclerViewViewHolder** viewholder for game ada
 *
 * Refer to [codingwithmitch](https://codingwithmitch.com/blog/playing-video-recyclerview-exoplayer-android/)
 * by [Mitch Tabian](https://codingwithmitch.com/)
 * THIS CLASS WAS CREATED TO ASSOCIATE VIDEOS AND SOUNDS WITH WORDS (AS WELL AS IMAGES AND TTS)
 *
 * @version     4.0, 09/09/2023
 * @see RecyclerView.ViewHolder
 */
class GameADARecyclerViewViewHolder(  //
    var parent: View
) : RecyclerView.ViewHolder(parent) {
    var media_container: FrameLayout
    var title: TextView
    var img: ImageView
    var img2: ImageView

    /**
     * GameADARecyclerViewViewHolder constructor.
     * init RecyclerView.ViewHolder finding views
     *
     */
    init {
        media_container = itemView.findViewById(R.id.game_ada_media_container)
        title = itemView.findViewById<View>(R.id.title) as TextView
        img = itemView.findViewById<View>(R.id.img1) as ImageView
        img2 = itemView.findViewById<View>(R.id.img2) as ImageView
    }

    /**
     * set views
     *
     * @param gameArrayList GameADAArrayList with game list
     * @see GameADAArrayList
     */
    fun onBind(gameArrayList: GameADAArrayList?) {
        parent.tag = this
    }
}