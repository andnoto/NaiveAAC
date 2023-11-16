package com.sampietro.NaiveAAC.activities.Graphics

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Context
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import com.sampietro.NaiveAAC.R
import com.sampietro.NaiveAAC.activities.VoiceRecognition.AndroidPermission.findViewById
import com.squareup.picasso.Picasso
import io.realm.Realm
import java.io.File
import java.io.IOException

/**
 * <h1>GraphicsHelper</h1>
 *
 *
 * **GraphicsHelper** utility class for graphics.
 *
 * @version     4.0, 09/09/2023
 */
object GraphicsHelper {
    /**
     * create a crossfade animation
     * Refer to [developer.android.com](https://developer.android.com/training/animation/reveal-or-hide-view)
     *
     * @param fadingInView view that is fading in
     * @param loadingView view that was fading out
     */
    @JvmStatic
    fun crossfade(fadingInView: View, loadingView: View) {
        // Set the content view to 0% opacity but visible, so that it is visible
        // (but fully transparent) during the animation.
        fadingInView.alpha = 0f
        fadingInView.visibility = View.VISIBLE
        // Animate the content view to 100% opacity, and clear any animation
        // listener set on the view.
        fadingInView.animate()
            .alpha(1f) // duration in millis
            .setDuration(5000)
            .setListener(null)
        // Animate the loading view to 0% opacity. After the animation ends,
        // set its visibility to GONE as an optimization step (it won't
        // participate in layout passes, etc.)
        loadingView.animate()
            .alpha(0f)
            .setDuration(5000)
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    loadingView.visibility = View.GONE
                }
            })
    }

    /**
     * inserts an image from url into a view
     * Refer to [picasso](https://square.github.io/picasso/#license)
     *
     * @param url string with url of the image
     * @param img view with target view
     * @see com.squareup.picasso.Picasso
     *
     */
    @Deprecated("replaced by {@link #addImageUsingPicasso(String,ImageView,int,int)}")
    fun addImageUsingPicasso(url: String?, img: ImageView?) {
        Picasso.get()
            .load(url)
            .resize(200, 200)
            .into(img)
    }

    /**
     * inserts an image from file into a view
     * Refer to [picasso](https://square.github.io/picasso/#license)
     *
     * @param file file containing the image
     * @param img view with target view
     * @see com.squareup.picasso.Picasso
     *
     */
    @Deprecated("replaced by {@link #addFileImageUsingPicasso(File,ImageView,int,int)}")
    fun addFileImageUsingPicasso(file: File?, img: ImageView?) {
        Picasso.get()
            .load(file!!)
            .resize(200, 200)
            .into(img)
    }

    /**
     * inserts an image from url into a view
     * Refer to [picasso](https://square.github.io/picasso/#license)
     *
     * @param url string with url of the image
     * @param img view with target view
     * @param width int with the image width for resizing
     * @param height int with the image height for resizing
     * @see com.squareup.picasso.Picasso
     */
    @JvmStatic
    fun addImageUsingPicasso(url: String?, img: ImageView?, width: Int, height: Int) {
        Picasso.get()
            .load(url)
            .resize(width, height)
            .into(img)
    }

    /**
     * inserts an image from file into a view
     * Refer to [picasso](https://square.github.io/picasso/#license)
     *
     * @param file file containing the image
     * @param img view with target view
     * @param width int with the image width for resizing
     * @param height int with the image height for resizing
     * @see com.squareup.picasso.Picasso
     */
    @JvmStatic
    fun addFileImageUsingPicasso(file: File?, img: ImageView?, width: Int, height: Int) {
        Picasso.get()
            .load(file!!)
            .resize(width, height)
            .into(img)
    }

    /**
     * inserts an image whose address is found via realm into a view
     * Refer to [picasso](https://square.github.io/picasso/#license)
     *
     * @param realm realm
     * @param word string with word for which you need to find the image
     * @param img view with target view
     * @param width int with the image width for resizing
     * @param height int with the image height for resizing
     * @see com.squareup.picasso.Picasso
     *
     * @see Images
     *
     * @see .addFileImageUsingPicasso
     */
    @JvmStatic
    fun addImageUsingRealm(
        realm: Realm,
        word: String?, img: ImageView?, width: Int, height: Int
    ) {
        val results = realm.where(
            Images::class.java
        ).equalTo("descrizione", word).findAll()
        val count = results.size
        if (count != 0) {
            val result = results[0]
            if (result != null) {
                val filePath = result.uri
                val f = File(filePath!!)
                addFileImageUsingPicasso(f, img, width, height)
            }
        }
    }
    /**
     * display file in Assets
     *
     *
     * Refer to [Pete Houston](https://xjaphx.wordpress.com/2011/10/02/store-and-use-files-in-assets/)
     * answer of [Pete Houston](https://stackoverflow.com/users/801396/pete-houston)
     *
     */
    fun displayFileInAssets(
        context: Context,
        fileName: String,
        myImage: ImageView
    ) {
        try {
            // get input stream
            val ims = context.assets.open(fileName)
            // load image as Drawable
            val d = Drawable.createFromStream(ims, null)
            // set image to ImageView
            myImage.setImageDrawable(d)
            ims.close()
        } catch (ex: IOException) {
            return
        }
    }
}