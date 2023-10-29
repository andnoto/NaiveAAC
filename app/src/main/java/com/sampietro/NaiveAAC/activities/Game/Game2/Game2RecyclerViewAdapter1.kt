package com.sampietro.NaiveAAC.activities.Game.Game2

import android.content.Context
import android.graphics.Bitmap
import com.squareup.picasso.Picasso.LoadedFrom
import android.graphics.drawable.Drawable
import android.content.SharedPreferences
import android.speech.tts.TextToSpeech
import com.sampietro.NaiveAAC.R
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.print.PrintHelper
import com.sampietro.NaiveAAC.activities.Graphics.GraphicsHelper
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import java.io.File
import java.lang.Exception
import java.util.ArrayList

/**
 * <h1>Game2RecyclerViewAdapter1</h1>
 *
 * **Game2RecyclerViewAdapter1** adapter for game2
 *
 * Refer to [androidauthority](https://www.androidauthority.com/how-to-build-an-image-gallery-app-718976/)
 * by [Adam Sinicki](https://www.androidauthority.com/author/adamsinicki/)
 *
 * @version     4.0, 09/09/2023
 * @see RecyclerView.Adapter<RecyclerView.ViewHolder>
</RecyclerView.ViewHolder> */
class Game2RecyclerViewAdapter1(
    private val context: Context,
    private val galleryList: ArrayList<Game2ArrayList>
) : RecyclerView.Adapter<Game2RecyclerViewAdapter1.ViewHolder>() {
    /**
     * used for printing
     */
    var bitmap1: Bitmap? = null

    /**
     * used for printing
     */
    var target1: Target = object : Target {
        override fun onBitmapLoaded(bitmap: Bitmap, from: LoadedFrom) {
            bitmap1 = bitmap
        }

        override fun onBitmapFailed(e: Exception, errorDrawable: Drawable) {}
        override fun onPrepareLoad(placeHolderDrawable: Drawable) {}
    }

    /**
     * used for printing
     */
    var sharedPref: SharedPreferences
    var preference_PrintPermissions: String?

    /**
     * used for TTS
     */
    var tTS1: TextToSpeech? = null
//    var toSpeak: String? = null

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
     * @see getTargetBitmapFromUrlUsingPicasso
     *
     * @see getTargetBitmapFromFileUsingPicasso
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
                if (galleryList[i].urlType == "A") {
                    getTargetBitmapFromUrlUsingPicasso(galleryList[i].url, target1)
                } else {
                    val f = File(galleryList[i].url!!)
                    getTargetBitmapFromFileUsingPicasso(f, target1)
                }
                //
                val photoPrinter = PrintHelper(context)
                photoPrinter.scaleMode = PrintHelper.SCALE_MODE_FIT
                photoPrinter.printBitmap(context.getString(R.string.stampa_immagine1), bitmap1!!)
            }
        }
    }

    /**
     * add an image to a view as indicated in the parameters
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

    /**
     * used for printing load an image in a target bitmap from a file
     *
     * @param file file of origin
     * @param target target bitmap
     * @see Picasso
     */
    fun getTargetBitmapFromFileUsingPicasso(file: File?, target: Target?) {
        Picasso.get()
            .load(file!!)
            .resize(200, 200)
            .into(target!!)
    }

    /**
     * used for printing load an image in a target bitmap from a url
     *
     * @param url string with url of origin
     * @param target target bitmap
     * @see Picasso
     */
    fun getTargetBitmapFromUrlUsingPicasso(url: String?, target: Target?) {
        Picasso.get()
            .load(url)
            .resize(200, 200)
            .into(target!!)
    }
}