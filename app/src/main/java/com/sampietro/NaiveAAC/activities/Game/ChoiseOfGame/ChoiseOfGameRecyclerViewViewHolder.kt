package com.sampietro.NaiveAAC.activities.Game.ChoiseOfGame

import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sampietro.NaiveAAC.R
import com.sampietro.NaiveAAC.activities.Graphics.GraphicsAndPrintingHelper.addImage

/**
 * <h1>ChoiseOfGameRecyclerViewViewHolder</h1>
 *
 * **ChoiseOfGameRecyclerViewViewHolder** viewholder for game choice
 *
 * Refer to [codingwithmitch](https://codingwithmitch.com/blog/playing-video-recyclerview-exoplayer-android/)
 * by [Mitch Tabian](https://codingwithmitch.com/)
 *
 * @version     5.0, 01/04/2024
 * @see RecyclerView.ViewHolder
 */
class ChoiseOfGameRecyclerViewViewHolder(  // public ProgressBar progressBar;
    var parent: View
) : RecyclerView.ViewHolder(parent) {
    var media_container: FrameLayout
    var title: TextView
    var info: TextView
    var copyright: TextView
    var imageView: ImageView

    /**
     * ChoiseOfGameRecyclerViewViewHolder constructor.
     * init RecyclerView.ViewHolder finding views
     */
    init {
        media_container = itemView.findViewById(R.id.media_container)
        imageView = itemView.findViewById(R.id.imageView)
        title = itemView.findViewById(R.id.title)
        info = itemView.findViewById(R.id.infomediaplayer)
        copyright = itemView.findViewById(R.id.copyright)
    }

    /**
     * set views
     *
     * @param gameArrayList ChoiseOfGameArrayList with game list
     * @see ChoiseOfGameArrayList
     *
     * @see addImage
     */
    fun onBind(gameArrayList: ChoiseOfGameArrayList) {
        parent.tag = this
        title.text = gameArrayList.image_title
        info.setText(R.string.info)
        copyright.text = gameArrayList.videoCopyright
        imageView.contentDescription = gameArrayList.image_title
        addImage(gameArrayList.urlType, gameArrayList.url, imageView, 200, 200)
    }
}