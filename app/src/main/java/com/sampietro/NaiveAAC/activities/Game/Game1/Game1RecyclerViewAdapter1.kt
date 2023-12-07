package com.sampietro.NaiveAAC.activities.Game.Game1

import android.app.Activity
import android.content.Context
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import com.sampietro.NaiveAAC.R
import com.sampietro.NaiveAAC.activities.Graphics.GraphicsHelper
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.io.File
import java.util.ArrayList

/**
 * <h1>Game1RecyclerViewAdapter1</h1>
 *
 * **Game1RecyclerViewAdapter1** adapter for game1
 *
 * Refer to [androidauthority](https://www.androidauthority.com/how-to-build-an-image-gallery-app-718976/)
 * by [Adam Sinicki](https://www.androidauthority.com/author/adamsinicki/)
 *
 * @version     4.0, 09/09/2023
 * @see RecyclerView.Adapter<RecyclerView.ViewHolder>
</RecyclerView.ViewHolder> */
class Game1RecyclerViewAdapter1(
    private val context: Context, //
    private val galleryList: ArrayList<Game1ArrayList>
) : RecyclerView.Adapter<Game1RecyclerViewAdapter1.ViewHolder>() {
    var listener: Game1RecyclerViewAdapterInterface? = null

    /**
     * Game1RecyclerViewAdapter1 constructor.
     * set game1 list, listener setting for settings activity callbacks and context annotation
     *
     * @see Game1RecyclerViewAdapterInterface
    */
    init {
        //
        val activity = context as Activity
        listener = activity as Game1RecyclerViewAdapterInterface
    }

    /**
     * Called when RecyclerView needs a new RecyclerView.ViewHolder of the given type to represent an item.
     *
     * @param viewGroup viewGroup into which the new View will be added after it is bound to an adapter position
     * @param i int with the view type of the new View.
     * @return RecyclerView.ViewHolder new View created inflating it from an XML layout file
     * @see RecyclerView.Adapter.onCreateViewHolder
     */
    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.activity_game_1_cell_layout_1, viewGroup, false)
        return ViewHolder(view)
    }

    /**
     * Called by RecyclerView to display the data at the specified position.
     * set on click listeners
     *
     *
     * Refer to [stackoverflow](https://stackoverflow.com/questions/49969278/recyclerview-item-click-listener-the-right-way)
     * answer of [Reaz Murshed](https://stackoverflow.com/users/3145960/reaz-murshed)
     *
     * @param viewHolder RecyclerView.ViewHolder ViewHolder which should be updated to represent the contents of the item at the given position in the data set
     * @param i int with the position of the item within the adapter's data set.
     * @see RecyclerView.Adapter.onBindViewHolder
     *
     * @see addImage
     */
    override fun onBindViewHolder(viewHolder: ViewHolder, i: Int) {
        viewHolder.title.text = galleryList[i].image_title
        viewHolder.img.scaleType = ImageView.ScaleType.CENTER_CROP
        viewHolder.img.contentDescription = galleryList[i].image_title
        //
        addImage(
            galleryList[i].urlType,
            galleryList[i].url, viewHolder.img
        )
        //
        viewHolder.img.setOnClickListener { view -> listener!!.onItemClick(view, i) }
    }

    /**
     * add an image to a left view as indicated in the parameters
     *
     * @param urlType string representing the type of icon displayed in the left view.
     * @param url string representing the path of icon displayed in the left view.
     * @param img imageview where the image will be added
     * @see GraphicsHelper.addImageUsingPicasso
     *
     * @see GraphicsHelper.addFileImageUsingPicasso
     */
    fun addImage(urlType: String?, url: String?, img: ImageView?) {
        if (urlType == "A") {
            GraphicsHelper.addImageUsingPicasso(url, img, 200, 200)
        } else {
            val f = File(url!!)
            GraphicsHelper.addFileImageUsingPicasso(f, img, 200, 200)
        }
    }

    /**
     * returns number of item within the adapter's data set.
     *
     * @return int with number of item within the adapter's data set
     * @see RecyclerView.Adapter.getItemCount
     */
    override fun getItemCount(): Int {
        return galleryList.size
    }

    /**
     * viewholder for game1 left recyclerview.
     *
     *
     * @see RecyclerView.ViewHolder
     */
    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        internal val title: TextView
        internal val img: ImageView

        init {
            //
            title = view.findViewById<View>(R.id.title) as TextView
            img = view.findViewById<View>(R.id.img1) as ImageView
        }
    }
}