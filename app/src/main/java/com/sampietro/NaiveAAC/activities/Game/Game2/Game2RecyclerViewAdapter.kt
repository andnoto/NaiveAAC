package com.sampietro.NaiveAAC.activities.Game.Game2

import android.content.Context
import android.content.SharedPreferences
import android.speech.tts.TextToSpeech
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.sampietro.NaiveAAC.R
import com.sampietro.NaiveAAC.activities.Game.Game1.Game1ArrayList
import com.sampietro.NaiveAAC.activities.Graphics.GraphicsAndPrintingHelper.addImage
import com.sampietro.NaiveAAC.activities.Graphics.GraphicsAndPrintingHelper.printImage

/**
 * <h1>Game2RecyclerViewAdapter</h1>
 *
 * **Game2RecyclerViewAdapter** adapter for game2
 *
 * Refer to [androidauthority](https://www.androidauthority.com/how-to-build-an-image-gallery-app-718976/)
 * by [Adam Sinicki](https://www.androidauthority.com/author/adamsinicki/)
 *
 * @version     5.0, 01/04/2024
 * @see RecyclerView.Adapter<RecyclerView.ViewHolder>
 */
class Game2RecyclerViewAdapter(
    private val context: Context,
    private val galleryList: ArrayList<Game1ArrayList>
) : RecyclerView.Adapter<Game2RecyclerViewAdapter.ViewHolder>() {
    var sharedPref: SharedPreferences
    var preference_PrintPermissions: String?

    /**
     * used for TTS
     */
    var tTS1: TextToSpeech? = null

    /**
     * Game2RecyclerViewAdapter1 constructor.
     * set game2 list and context annotation, get print permissions
     *
     */
    init {
        // if is print permitted then preference_PrintPermissions = Y
        sharedPref = context.getSharedPreferences(
            context.getString(R.string.preference_file_key), Context.MODE_PRIVATE
        )
        preference_PrintPermissions = sharedPref.getString(
            context.getString(R.string.preference_print_permissions),
            "DEFAULT"
        )
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
            .inflate(R.layout.activity_game_2_cell_layout_1, viewGroup, false)
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
     *
     */
    override fun onBindViewHolder(viewHolder: ViewHolder, i: Int) {
        viewHolder.title.text = galleryList[i].image_title
        viewHolder.img.scaleType = ImageView.ScaleType.CENTER_CROP
        viewHolder.img.contentDescription = galleryList[i].image_title
        //
        addImage(
            galleryList[i].urlType,
            galleryList[i].url, viewHolder.img,
            200,200
        )
        //
        viewHolder.img.setOnClickListener {
            // TTS
            tTS1 = TextToSpeech(context) { status ->
                if (status != TextToSpeech.ERROR) {
                    tTS1!!.speak(
                        galleryList[i].image_title,
                        TextToSpeech.QUEUE_FLUSH,
                        null,
                        "prova tts"
                    )
                } else {
                    Toast.makeText(context, status, Toast.LENGTH_SHORT).show()
                }
            }
            //
            if (preference_PrintPermissions == "Y") {
                printImage(context, galleryList[i].urlType, galleryList[i].url, 200, 200 )
            }
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
     * viewholder for game2 recyclerview.
     *
     *
     * @see RecyclerView.ViewHolder
     */
    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView
        val img: ImageView

        init {
            //
            title = view.findViewById<View>(R.id.title) as TextView
            img = view.findViewById<View>(R.id.img1) as ImageView
        }
    }

}