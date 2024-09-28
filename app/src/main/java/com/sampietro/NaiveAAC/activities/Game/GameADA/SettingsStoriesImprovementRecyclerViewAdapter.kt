package com.sampietro.NaiveAAC.activities.Game.GameADA

import android.app.Activity
import android.content.Context
import android.speech.tts.TextToSpeech
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.sampietro.NaiveAAC.R
import com.sampietro.NaiveAAC.activities.Grammar.GrammarHelper.searchNegationAdverb
import com.sampietro.NaiveAAC.activities.Graphics.GraphicsAndPrintingHelper.addImage
import com.sampietro.NaiveAAC.activities.Graphics.ImageSearchHelper.searchUri
import io.realm.Realm
import java.util.Locale

/**
 * <h1>GameADARecyclerViewAdapter</h1>
 *
 * **GameADARecyclerViewAdapter** adapter for games
 *
 * Refer to [androidauthority](https://www.androidauthority.com/how-to-build-an-image-gallery-app-718976/)
 * by [Adam Sinicki](https://www.androidauthority.com/author/adamsinicki/)
 *
 * @version     5.0, 01/04/2024
 * @see RecyclerView.Adapter<RecyclerView.ViewHolder>
    */
class SettingsStoriesImprovementRecyclerViewAdapter(
    private val context: Context, //
    private val realm: Realm, //
    private val galleryList: ArrayList<GameADAArrayList>
) : RecyclerView.Adapter<GameADARecyclerViewViewHolder>() {
    var listener: GameADARecyclerViewAdapterInterface

    /**
     * used for TTS
     */
    var tTS1: TextToSpeech? = null

    /**
     * GameRecyclerViewAdapter constructor.
     * set game list and context annotation, get print permissions
     *
    */
    init {
        //
        //
        val activity = context as Activity
        listener = activity as GameADARecyclerViewAdapterInterface
    }

    /**
     * Called when RecyclerView needs a new RecyclerView.ViewHolder of the given type to represent an item.
     *
     * @param viewGroup viewGroup into which the new View will be added after it is bound to an adapter position
     * @param i int with the view type of the new View.
     * @return RecyclerView.ViewHolder new View created inflating it from an XML layout file
     * @see RecyclerView.Adapter.onCreateViewHolder
     */
    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): GameADARecyclerViewViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.activity_settings_stories_improvement_cell_layout, viewGroup, false)
        return GameADARecyclerViewViewHolder(view)
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
     * @see com.sampietro.NaiveAAC.activities.Grammar.GrammarHelper.searchNegationAdverb
     *
     * @see addImage
     */
    override fun onBindViewHolder(viewHolder: GameADARecyclerViewViewHolder, i: Int) {
        //
        viewHolder.onBind(galleryList[i])
        //
        viewHolder.title.text = galleryList[i].image_title
        viewHolder.img.scaleType = ImageView.ScaleType.CENTER_CROP
        //
        if (galleryList[i].urlType == "I") {
            val imageUrl = searchUri(context,
                realm,
                galleryList[i].url)
            galleryList[i].urlType = "S"
            galleryList[i].url = imageUrl
        }
        addImage(
            galleryList[i].urlType,
            galleryList[i].url, viewHolder.img,
            100,100
        )
        // search for negation adverbs
        viewHolder.img2.visibility = View.INVISIBLE
        val negationAdverbImageToSearchFor = searchNegationAdverb(
            context,
            galleryList[i].image_title!!.lowercase(Locale.getDefault()), realm
        )
        if (negationAdverbImageToSearchFor != context.getString(R.string.non_trovato)) {
            // INTERNAL MEMORY IMAGE SEARCH
            val uriToSearch = searchUri(context, realm, negationAdverbImageToSearchFor)
            viewHolder.img2.scaleType = ImageView.ScaleType.CENTER_CROP
            addImage("S", uriToSearch, viewHolder.img2,
                100,100)
            viewHolder.img2.visibility = View.VISIBLE
        }
        //
        viewHolder.media_container.setOnClickListener { view ->
            listener.onItemClick(
                view,
                i,
                galleryList
            )
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
}