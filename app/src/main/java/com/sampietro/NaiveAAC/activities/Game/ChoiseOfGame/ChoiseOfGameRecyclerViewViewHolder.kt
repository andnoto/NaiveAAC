package com.sampietro.NaiveAAC.activities.Game.ChoiseOfGame

import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sampietro.NaiveAAC.R
import com.sampietro.NaiveAAC.activities.Graphics.GraphicsHelper
import java.io.File

/**
 * <h1>ChoiseOfGameRecyclerViewViewHolder</h1>
 *
 * **ChoiseOfGameRecyclerViewViewHolder** viewholder for game choice
 *
 * Refer to [codingwithmitch](https://codingwithmitch.com/blog/playing-video-recyclerview-exoplayer-android/)
 * by [Mitch Tabian](https://codingwithmitch.com/)
 *
 * @version     4.0, 09/09/2023
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
        addImage(gameArrayList.urlType, gameArrayList.url, imageView)
    }

    /**
     * add an image to a view as indicated in the parameters
     *
     * @param urlType string representing the type of icon displayed in the view for game choice.
     * @param url string representing the path of icon displayed in the view for game choice.
     * @param img imageview where the image will be added
     * @see com.sampietro.NaiveAAC.activities.Game.GameParameters.GameParameters
     *
     * @see GraphicsHelper.addImageUsingPicasso
     *
     * @see GraphicsHelper.addFileImageUsingPicasso
     */
    fun addImage(urlType: String?, url: String?, img: ImageView?) {
        if (urlType == "AS") {
            val assetsUrl = "file:///android_asset/$url"
            GraphicsHelper.addImageUsingPicasso(assetsUrl, img, 200, 200)
        } else {
            if (urlType == "A") {
                GraphicsHelper.addImageUsingPicasso(url, img, 200, 200)
            } else {
                val f = File(url!!)
                GraphicsHelper.addFileImageUsingPicasso(f, img, 200, 200)
            }
        }
    }
}