package com.sampietro.NaiveAAC.activities.Game.ChoiseOfGame

import android.app.Activity
import android.content.Context
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.sampietro.NaiveAAC.R
import java.util.ArrayList

/**
 * <h1>ChoiseOfGameRecyclerViewAdapter</h1>
 *
 * **ChoiseOfGameRecyclerViewAdapter** adapter for game choice
 *
 * Refer to [androidauthority](https://www.androidauthority.com/how-to-build-an-image-gallery-app-718976/)
 * by [Adam Sinicki](https://www.androidauthority.com/author/adamsinicki/)
 * Refer to [codingwithmitch](https://codingwithmitch.com/blog/playing-video-recyclerview-exoplayer-android/)
 * by [Mitch Tabian](https://codingwithmitch.com/)
 *
 * @version     4.0, 09/09/2023
 * @see RecyclerView.Adapter<RecyclerView.ViewHolder>
</RecyclerView.ViewHolder> */
class ChoiseOfGameRecyclerViewAdapter(
    context: Context?, //
    private val gameArrayList: ArrayList<ChoiseOfGameArrayList>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var listener: ChoiseOfGameRecyclerViewAdapterInterface? = null

    /**
     * ChoiseOfGameRecyclerViewAdapter constructor.
     * set game list, listener setting for settings activity callbacks and context annotation
     *
    */
    init {
        //
        val activity = context as Activity?
        listener = activity as ChoiseOfGameRecyclerViewAdapterInterface?
    }

    /**
     * Called when RecyclerView needs a new RecyclerView.ViewHolder of the given type to represent an item.
     *
     * @param viewGroup viewGroup into which the new View will be added after it is bound to an adapter position
     * @param i int with the view type of the new View.
     * @return RecyclerView.ViewHolder new View created inflating it from an XML layout file
     * @see RecyclerView.Adapter.onCreateViewHolder
     */
    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): RecyclerView.ViewHolder {
        return ChoiseOfGameRecyclerViewViewHolder(
            LayoutInflater.from(viewGroup.context)
                .inflate(R.layout.activity_game_choise_of_game_cell_layout, viewGroup, false)
        )
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
     */
    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, i: Int) {
        (viewHolder as ChoiseOfGameRecyclerViewViewHolder).onBind(gameArrayList[i])
        //
        viewHolder.media_container.setOnClickListener(
            View.OnClickListener { view -> listener!!.onItemClick(view, i) })
        viewHolder.info.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View) {
                listener!!.onItemClick(view, i)
            }
        })
    }

    /**
     * returns number of item within the adapter's data set.
     *
     * @return int with number of item within the adapter's data set
     * @see RecyclerView.Adapter.getItemCount
     */
    override fun getItemCount(): Int {
        return gameArrayList.size
    }
}