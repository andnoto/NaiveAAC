package com.sampietro.NaiveAAC.activities.Graphics

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ImageDecoder
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.util.Base64
import android.view.View
import android.view.Window
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.print.PrintHelper
import com.sampietro.NaiveAAC.R
import com.sampietro.NaiveAAC.activities.Game.GameADA.GameADAArrayList
import com.sampietro.NaiveAAC.activities.Grammar.GrammarHelper
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import io.realm.Realm
import java.io.File
import java.io.IOException
import java.net.URL
import java.util.Locale

/**
 * <h1>GraphicsAndPrintingHelper</h1>
 *
 *
 * **GraphicsAndPrintingHelper** utility class for graphics.
 *
 * @version     5.0, 01/04/2024
 */
object GraphicsAndPrintingHelper {
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
    @JvmStatic
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
    @JvmStatic
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
     * used for printing load an image in a target bitmap from a file
     *
     * @param file file of origin
     * @param target target bitmap
     * @see Picasso
     */
    @Deprecated("replaced by {@link #getTargetBitmapFromFileUsingPicasso(File,Target,int,int)}")
    @JvmStatic
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
    @Deprecated("replaced by {@link #getTargetBitmapFromUrlUsingPicasso(String,Target,int,int)}")
    @JvmStatic
    fun getTargetBitmapFromUrlUsingPicasso(url: String?, target: Target?) {
        Picasso.get()
            .load(url)
            .resize(200, 200)
            .into(target!!)
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
    @JvmStatic
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
    //
    /**
     * used for printing create single bitmap image from multiple bitmap images placing them horizontally
     * Refer to [stackoverflow](https://stackoverflow.com/questions/34719987/merging-two-images-into-one-image#:~:text=Here%20is%20the%20code%3A%20Used%20to%20combine%20Images&text=getHeight()%2C%20firstImage.,(firstImage%2C%20SecondImage)%3B%20im.)
     * answer of [caulitomaz](https://stackoverflow.com/users/2026359/caulitomaz)
     *
     * @param firstImage bitmap of first image
     * @param secondImage bitmap of second image
     * @return bitmap single image created from multiple images
     */
    @JvmStatic
    private fun createSingleImageFromMultipleImagesPlacingThemHorizontally(
        firstImage: Bitmap,
        secondImage: Bitmap
    ): Bitmap {
        val result = Bitmap.createBitmap(
            firstImage.width + secondImage.width,
            firstImage.height,
            firstImage.config
        )
        val canvas = Canvas(result)
        canvas.drawBitmap(firstImage, 0f, 0f, null)
        canvas.drawBitmap(secondImage, firstImage.width.toFloat(), 0f, null)
        return result
    }
    /**
     * This method is responsible to transfer MainActivity into fullscreen mode.
     */
    @JvmStatic
    fun setToFullScreen(window: Window) {
        WindowCompat.setDecorFitsSystemWindows(window, false)

        val insetsController = WindowCompat.getInsetsController(window, window.decorView)
        insetsController.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        insetsController.hide(WindowInsetsCompat.Type.statusBars())
        insetsController.hide(WindowInsetsCompat.Type.navigationBars())
    }
    /**
     * add an image to a view as indicated in the parameters
     *
     * @param urlType string representing the type of image displayed in the view .
     * @param url string representing the path of image displayed in the view.
     * @param img imageview where the image will be added
     * @param imgWidth int width
     * @param imgHeight int height
     *
     * @see addImageUsingPicasso
     *
     * @see addFileImageUsingPicasso
     */
    @JvmStatic
    fun addImage(urlType: String?, url: String?, img: ImageView?, imgWidth: Int, imgHeight: Int ) {
        if (urlType == "AS") {
            val assetsUrl = "file:///android_asset/$url"
            addImageUsingPicasso(assetsUrl, img, imgWidth, imgHeight)
        } else {
            if (urlType == "A") {
                addImageUsingPicasso(url, img, imgWidth, imgHeight)
            } else {
                val f = File(url!!)
                addFileImageUsingPicasso(f, img, imgWidth, imgHeight)
            }
        }
    }
    /**
     * used for printing
     *
     * @param context Context
     * @param urlType string representing the type of image displayed in the view .
     * @param url string representing the path of image displayed in the view.
     * @param imgWidth int width
     * @param imgHeight int height
     *
     * @see getTargetBitmapFromUrlUsingPicasso
     *
     * @see getTargetBitmapFromFileUsingPicasso
     */
    @JvmStatic
    fun printImage(context : Context, urlType: String?, url: String?, imgWidth: Int, imgHeight: Int ) {
        var target1: Target = object : Target {
            override fun onBitmapLoaded(bitmap: Bitmap, from: Picasso.LoadedFrom) {
                //
                val photoPrinter = PrintHelper(context)
                photoPrinter.scaleMode = PrintHelper.SCALE_MODE_FIT
                photoPrinter.printBitmap(context.getString(R.string.stampa_immagine1), bitmap)
            }

            override fun onBitmapFailed(e: Exception, errorDrawable: Drawable) {}
            override fun onPrepareLoad(placeHolderDrawable: Drawable) {}
        }
        //
        if (urlType == "A") {
            getTargetBitmapFromUrlUsingPicasso(url, target1, imgWidth, imgHeight)
        } else {
            val f = File(url!!)
            getTargetBitmapFromFileUsingPicasso(f, target1, imgWidth, imgHeight)
        }
    }
    /**
     * used for printing load an image in a target bitmap from a file
     *
     * @param file file of origin
     * @param target target bitmap
     * @param imgWidth int width
     * @param imgHeight int height
     * @see Picasso
     */
    @JvmStatic
    fun getTargetBitmapFromFileUsingPicasso(file: File?, target: Target?, imgWidth: Int, imgHeight: Int) {
        Picasso.get()
            .load(file!!)
            .resize(imgWidth, imgHeight)
            .into(target!!)
    }

    /**
     * used for printing load an image in a target bitmap from a url
     *
     * @param url string with url of origin
     * @param target target bitmap
     * @param imgWidth int width
     * @param imgHeight int height
     * @see Picasso
     */
    @JvmStatic
    fun getTargetBitmapFromUrlUsingPicasso(url: String?, target: Target?, imgWidth: Int, imgHeight: Int) {
        Picasso.get()
            .load(url)
            .resize(imgWidth, imgHeight)
            .into(target!!)
    }
    /**
     * used for printing create single bitmap image superimposed from multiple bitmap images
     *
     * @param firstImage bitmap of first image
     * @param secondImage bitmap of second image
     * @return bitmap single image created from multiple images
     */
    @JvmStatic
    fun createSingleImageSuperimposedFromMultipleImages(
        firstImage: Bitmap?,
        secondImage: Bitmap?
    ): Bitmap {
        val result = Bitmap.createBitmap(firstImage!!.width, firstImage.height, firstImage.config)
        val canvas = Canvas(result)
        canvas.drawBitmap(firstImage, 0f, 0f, null)
        canvas.drawBitmap(secondImage!!, 0f, 0f, null)
        return result
    }
    /**
     * used for printing create single bitmap image from image and title
     * Refer to [stackoverflow](https://stackoverflow.com/questions/2655402/android-canvas-drawtext)
     * answer of [gaz](https://stackoverflow.com/users/119396/gaz)
     *
     * @param firstImage bitmap of image
     * @param title string image title
     * @return bitmap single image created from image and title
     */
    @JvmStatic
    fun createSingleImageFromImageAndTitlePlacingThemVertically(
        firstImage: Bitmap?,
        title: String
    ): Bitmap {
        val result =
            Bitmap.createBitmap(firstImage!!.width, firstImage.height + 30, firstImage.config)
        val canvas = Canvas(result)
        canvas.drawBitmap(firstImage, 0f, 0f, null)
        //
        val paint = Paint()
        paint.color = Color.BLACK
        paint.style = Paint.Style.FILL
        paint.textSize = 20f
        canvas.drawText(title, 0f, (firstImage.height + 10).toFloat(), paint)
        //
        return result
    }
    /**
     * print phrase.
     *
     *
     * @param context Context
     * @param realm Realm
     * @param galleryList ArrayList<GameADAArrayList> with images to print
     * @see printPhraseCacheImages
     *
     * @see printPhraseCreateMergedImages
     */
    @JvmStatic
    fun printPhrase(context: Context, realm: Realm, galleryList: ArrayList<GameADAArrayList>) {
        /**
         * used for printing
         */
        var mergedImages: Bitmap? = null
        //
        printPhraseCacheImages(context, realm, galleryList)
        val TIME_OUT = 2500
        Handler(Looper.getMainLooper()).postDelayed({
            mergedImages = printPhraseCreateMergedImages(context, realm,  galleryList)
            //
            val photoPrinter = PrintHelper(context)
            photoPrinter.scaleMode = PrintHelper.SCALE_MODE_FIT
            photoPrinter.printBitmap(context.getString(R.string.stampa_immagine1), mergedImages!!)
        }, TIME_OUT.toLong())
    }
    /**
     * Cache images for printing
     *
     * @param context Context
     * @param realm Realm
     * @param galleryList ArrayList<GameADAArrayList> with images to print
     * @see getTargetBitmapFromUrlUsingPicasso
     * @see getTargetBitmapFromFileUsingPicasso
     * @see GrammarHelper.searchNegationAdverb
     */
    @JvmStatic
    fun printPhraseCacheImages(context: Context,realm: Realm, galleryList: ArrayList<GameADAArrayList>) {
        /**
         * used for printing
         */
        var target1: Target = object : Target {
            override fun onBitmapLoaded(bitmap: Bitmap, from: Picasso.LoadedFrom) {}
            override fun onBitmapFailed(e: Exception, errorDrawable: Drawable) {}
            override fun onPrepareLoad(placeHolderDrawable: Drawable?) {}
        }
        /**
         * used for printing
         */
        var target2: Target = object : Target {
            override fun onBitmapLoaded(bitmap: Bitmap, from: Picasso.LoadedFrom) {}
            override fun onBitmapFailed(e: Exception, errorDrawable: Drawable) {}
            override fun onPrepareLoad(placeHolderDrawable: Drawable?) {}
        }
        //
        val count = galleryList.size
        if (count != 0) {
            var irrh = 0
            while (irrh < count) {
                if (galleryList[irrh].urlType == "A") {
                    getTargetBitmapFromUrlUsingPicasso(galleryList[irrh].url, target1,200,200)
                } else {
                    val f = File(galleryList[irrh].url!!)
                    getTargetBitmapFromFileUsingPicasso(f, target1,200,200)
                }
                // search for negation adverbs
                val negationAdverbImageToSearchFor = GrammarHelper.searchNegationAdverb(
                    context,
                    galleryList[irrh].image_title!!.lowercase(Locale.getDefault()), realm
                )
                if (negationAdverbImageToSearchFor != context.getString(R.string.non_trovato)) {
                    // INTERNAL MEMORY IMAGE SEARCH
                    val uriToSearch =
                        ImageSearchHelper.searchUri(context, realm, negationAdverbImageToSearchFor)
                    val f = File(uriToSearch)
                    getTargetBitmapFromFileUsingPicasso(f, target2,200,200)
                }
                irrh++
            }
        }
    }
    /**
     * it merge images for printing
     *
     * @param context Context
     * @param realm Realm
     * @param galleryList ArrayList<GameADAArrayList> with images to print
     * @return bitmap single image created from multiple images
     *
     * @see getTargetBitmapFromUrlUsingPicasso
     * @see getTargetBitmapFromFileUsingPicasso
     * @see GrammarHelper.searchNegationAdverb
     * @see ImageSearchHelper.searchUri
     * @see createSingleImageSuperimposedFromMultipleImages
     * @see createSingleImageFromImageAndTitlePlacingThemVertically
     * @see createSingleImageFromMultipleImages
     */
    @JvmStatic
    fun printPhraseCreateMergedImages(context: Context,realm: Realm, galleryList: ArrayList<GameADAArrayList>): Bitmap? {
        var mergedImages: Bitmap? = null
        var bitmap1: Bitmap? = null
        var bitmap2: Bitmap? = null
        /**
         * used for printing
         */
        var target1: Target = object : Target {
            override fun onBitmapLoaded(bitmap: Bitmap, from: Picasso.LoadedFrom) {
                bitmap1 = bitmap
            }

            override fun onBitmapFailed(e: Exception, errorDrawable: Drawable) {}
            override fun onPrepareLoad(placeHolderDrawable: Drawable?) {}
        }

        /**
         * used for printing
         */
        var target2: Target = object : Target {
            override fun onBitmapLoaded(bitmap: Bitmap, from: Picasso.LoadedFrom) {
                bitmap2 = bitmap
            }

            override fun onBitmapFailed(e: Exception, errorDrawable: Drawable) {}
            override fun onPrepareLoad(placeHolderDrawable: Drawable?) {}
        }
        //
        val count = galleryList.size
        if (count != 0) {
            //
            var imagesContainedInARow = 0
            var nrows = 1
            var ncolumns = 1
            //
            var irrh = 0
            while (irrh < count) {
                if (galleryList[irrh].urlType == "A") {
                    getTargetBitmapFromUrlUsingPicasso(galleryList[irrh].url, target1, 200,200)
                } else {
                    val f = File(galleryList[irrh].url!!)
                    getTargetBitmapFromFileUsingPicasso(f, target1, 200, 200)
                }
                // search for negation adverbs
                val negationAdverbImageToSearchFor = GrammarHelper.searchNegationAdverb(
                    context,
                    galleryList[irrh].image_title!!.lowercase(Locale.getDefault()), realm
                )
                if (negationAdverbImageToSearchFor != context.getString(R.string.non_trovato)) {
                    // INTERNAL MEMORY IMAGE SEARCH
                    val uriToSearch =
                        ImageSearchHelper.searchUri(context, realm, negationAdverbImageToSearchFor)
                    val f = File(uriToSearch)
                    getTargetBitmapFromFileUsingPicasso(f, target2, 200, 200)
                    // addImage("S", uriToSearch, viewHolder.img2);
                    bitmap1 = createSingleImageSuperimposedFromMultipleImages(bitmap1, bitmap2)
                }
                // adding title
                bitmap1 = createSingleImageFromImageAndTitlePlacingThemVertically(
                    bitmap1,
                    galleryList[irrh].image_title!!
                )
                //
                if (irrh == 0) {
                    if (count > 24) {
                        imagesContainedInARow = 7
                        mergedImages = Bitmap.createBitmap(
                            bitmap1!!.width * 7,
                            bitmap1!!.height * 5,
                            bitmap1!!.config
                        )
                    } else if (count > 15) {
                        imagesContainedInARow = 6
                        mergedImages = Bitmap.createBitmap(
                            bitmap1!!.width * 6,
                            bitmap1!!.height * 4,
                            bitmap1!!.config
                        )
                    } else if (count > 12) {
                        imagesContainedInARow = 5
                        mergedImages = Bitmap.createBitmap(
                            bitmap1!!.width * 5,
                            bitmap1!!.height * 3,
                            bitmap1!!.config
                        )
                    } else {
                        imagesContainedInARow = 4
                        mergedImages = Bitmap.createBitmap(
                            bitmap1!!.width * 4,
                            bitmap1!!.height * 3,
                            bitmap1!!.config
                        )
                    }
                } else {
                    ncolumns++
                    if (ncolumns > imagesContainedInARow) {
                        nrows++
                        ncolumns = 1
                    }
                }
                val firstImage = mergedImages
                mergedImages =
                    createSingleImageFromMultipleImages(firstImage, bitmap1, nrows, ncolumns)
                irrh++
            }
        }
        //
        return mergedImages
    }
    //
    /**
     * used for printing create single bitmap image from multiple bitmap images
     *
     * @param firstImage bitmap of first image
     * @param secondImage bitmap of second image
     * @param nrows int number of rows on the page
     * @param ncolumns int number of columns on the page
     * @return bitmap single image created from multiple images
     */
    @JvmStatic
    fun createSingleImageFromMultipleImages(
        firstImage: Bitmap?,
        secondImage: Bitmap?,
        nrows: Int,
        ncolumns: Int
    ): Bitmap {
        val result = Bitmap.createBitmap(firstImage!!.width, firstImage.height, firstImage.config)
        val canvas = Canvas(result)
        canvas.drawBitmap(firstImage, 0f, 0f, null)
        canvas.drawBitmap(
            secondImage!!,
            (secondImage.width * (ncolumns - 1)).toFloat(),
            (secondImage.height * (nrows - 1)).toFloat(),
            null
        )
        return result
    }
    /**
     * show the chosen image identified by uri.
     *
     *
     * A Uniform Resource Identifier (URI) is a compact sequence of characters that identifies an abstract or physical resource
     *
     * @param context Context
     * @param uri uri of the image
     * @param myImage ImageView where the image will be displayed
     * @see scaleImage
     */
    @JvmStatic
    fun showImage(context: Context, uri: Uri?, myImage: ImageView) {
        var bitmap: Bitmap? = null
        //
        try {
            val source = ImageDecoder.createSource(
                context.contentResolver,
                uri!!
            )
            bitmap = ImageDecoder.decodeBitmap(source) { decoder, _, _ ->
                decoder.setTargetSampleSize(1) // shrinking by
                decoder.isMutableRequired = true // this resolve the hardware type of bitmap problem
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        //
        // On devices running SDK < 24 (Android 7.0), this method will fail to apply
        // correct density scaling to images loaded from content and file schemes.
        // Applications running on devices with SDK >= 24 MUST specify the
        // targetSdkVersion in their manifest as 24 or above for density scaling
        // to be applied to images loaded from these schemes.
        myImage.setImageBitmap(bitmap)
        scaleImage(context, bitmap, myImage)
    }

    /**
     * scale the image represented in the bitmap.
     * REFER to [stackoverflow](https://stackoverflow.com/questions/8232608/fit-image-into-imageview-keep-aspect-ratio-and-then-resize-imageview-to-image-d)
     * answer of [Jarno Argillander](https://stackoverflow.com/users/1030049/jarno-argillander)
     *
     * @param context Context
     * @param bitmap bitmap of the image
     * @param view imageview where the image will be displayed
     * @see dpToPx
     */
    @Throws(NoSuchElementException::class)
    @JvmStatic
    private fun scaleImage(context: Context, bitmap: Bitmap?, view: ImageView) {
        // Get current dimensions AND the desired bounding box
        var width: Int
        width = try {
            bitmap!!.width
        } catch (e: NullPointerException) {
            throw NoSuchElementException("Can't find bitmap on given view/drawable")
        }
        var height = bitmap.height
        val bounding = dpToPx(context,150)
        // Log.i("Test", "original width = " + Integer.toString(width));
        // Log.i("Test", "original height = " + Integer.toString(height));
        // Log.i("Test", "bounding = " + Integer.toString(bounding));
        // Determine how much to scale: the dimension requiring less scaling is
        // closer to the its side. This way the image always stays inside your
        // bounding box AND either x/y axis touches it.
        val xScale = bounding.toFloat() / width
        val yScale = bounding.toFloat() / height
        val scale = if (xScale <= yScale) xScale else yScale
        // Log.i("Test", "xScale = " + Float.toString(xScale));
        // Log.i("Test", "yScale = " + Float.toString(yScale));
        // Log.i("Test", "scale = " + Float.toString(scale));
        // Create a matrix for the scaling and add the scaling data
        val matrix = Matrix()
        matrix.postScale(scale, scale)
        // Create a new bitmap and convert it to a format understood by the ImageView
        val scaledBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true)
        width = scaledBitmap.width // re-use
        height = scaledBitmap.height // re-use
        val result = BitmapDrawable(context.resources, scaledBitmap)
        // Log.i("Test", "scaled width = " + Integer.toString(width));
        // Log.i("Test", "scaled height = " + Integer.toString(height));
        // Apply the scaled bitmap
        view.setImageDrawable(result)
        // Now change ImageView's dimensions to match the scaled image
        val params = view.layoutParams as ConstraintLayout.LayoutParams
        params.width = width
        params.height = height
        view.layoutParams = params
        // Log.i("Test", "done");
    }

    /**
     * calculate the dimensions of desired bounding box.
     *
     *
     * @param dp int with dimensions of desired bounding box
     * @return int with dimensions of desired bounding box in px
     */
    @JvmStatic
    private fun dpToPx(context: Context, dp: Int): Int {
        val density = context.getApplicationContext().resources.displayMetrics.density
        return Math.round(dp.toFloat() * density)
    }
    /**
     * print phrase.
     *
     *
     * @param context Context
     * @param realm Realm
     * @param galleryList ArrayList<GameADAArrayList> with images to print
     * @see printPhraseCacheImages
     *
     * @see printPhraseCreateMergedImages
     */
    @JvmStatic
    fun printPhraseUsingReadBytes(context: Context, realm: Realm, galleryList: ArrayList<GameADAArrayList>) {
        /**
         * used for printing
         */
        var mergedImages: Bitmap? = null
        mergedImages = printPhraseCreateMergedImagesUsingReadBytes(context, realm,  galleryList)
        //
//        printPhraseCacheImages(context, realm, galleryList)
        val TIME_OUT = 2500
        Handler(Looper.getMainLooper()).postDelayed({
//            mergedImages = printPhraseCreateMergedImages(context, realm,  galleryList)
            //
            val photoPrinter = PrintHelper(context)
            photoPrinter.scaleMode = PrintHelper.SCALE_MODE_FIT
            photoPrinter.printBitmap(context.getString(R.string.stampa_immagine1), mergedImages!!)
        }, TIME_OUT.toLong())
    }
    /**
     * it merge images for printing
     *
     * @param context Context
     * @param realm Realm
     * @param galleryList ArrayList<GameADAArrayList> with images to print
     * @return bitmap single image created from multiple images
     *
     * @see getTargetBitmapFromUrlUsingPicasso
     * @see getTargetBitmapFromFileUsingPicasso
     * @see GrammarHelper.searchNegationAdverb
     * @see ImageSearchHelper.searchUri
     * @see createSingleImageSuperimposedFromMultipleImages
     * @see createSingleImageFromImageAndTitlePlacingThemVertically
     * @see createSingleImageFromMultipleImages
     */
    @JvmStatic
    fun printPhraseCreateMergedImagesUsingReadBytes(context: Context,realm: Realm, galleryList: ArrayList<GameADAArrayList>): Bitmap? {
        var mergedImages: Bitmap? = null
        var bitmap1: Bitmap? = null
        var bitmap2: Bitmap? = null
        /**
         * used for printing
         */
        var target1: Target = object : Target {
            override fun onBitmapLoaded(bitmap: Bitmap, from: Picasso.LoadedFrom) {
                bitmap1 = bitmap
            }

            override fun onBitmapFailed(e: Exception, errorDrawable: Drawable) {}
            override fun onPrepareLoad(placeHolderDrawable: Drawable?) {}
        }

        /**
         * used for printing
         */
        var target2: Target = object : Target {
            override fun onBitmapLoaded(bitmap: Bitmap, from: Picasso.LoadedFrom) {
                bitmap2 = bitmap
            }

            override fun onBitmapFailed(e: Exception, errorDrawable: Drawable) {}
            override fun onPrepareLoad(placeHolderDrawable: Drawable?) {}
        }
        //
        val count = galleryList.size
        if (count != 0) {
            //
            var imagesContainedInARow = 0
            var nrows = 1
            var ncolumns = 1
            //
            var irrh = 0
            while (irrh < count) {
                if (galleryList[irrh].urlType == "A") {
                    val url = URL(galleryList[irrh].url)
                    val imageData = url.readBytes()
                    val decodedBytes = Base64.decode(imageData, Base64.DEFAULT)
                    bitmap1 = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
//                    getTargetBitmapFromUrlUsingPicasso(galleryList[irrh].url, target1, 200,200)
                } else {
                    bitmap1 = BitmapFactory.decodeFile(galleryList[irrh].url!!)
//                    val f = File(galleryList[irrh].url!!)
//                    getTargetBitmapFromFileUsingPicasso(f, target1, 200, 200)
                }
                // search for negation adverbs
                val negationAdverbImageToSearchFor = GrammarHelper.searchNegationAdverb(
                    context,
                    galleryList[irrh].image_title!!.lowercase(Locale.getDefault()), realm
                )
                if (negationAdverbImageToSearchFor != context.getString(R.string.non_trovato)) {
                    // INTERNAL MEMORY IMAGE SEARCH
                    val uriToSearch =
                        ImageSearchHelper.searchUri(context, realm, negationAdverbImageToSearchFor)
                    val f = File(uriToSearch)
                    getTargetBitmapFromFileUsingPicasso(f, target2, 200, 200)
                    // addImage("S", uriToSearch, viewHolder.img2);
                    bitmap1 = createSingleImageSuperimposedFromMultipleImages(bitmap1, bitmap2)
                }
                // adding title
                bitmap1 = createSingleImageFromImageAndTitlePlacingThemVertically(
                    bitmap1,
                    galleryList[irrh].image_title!!
                )
                //
                if (irrh == 0) {
                    if (count > 24) {
                        imagesContainedInARow = 7
                        mergedImages = Bitmap.createBitmap(
                            bitmap1!!.width * 7,
                            bitmap1!!.height * 5,
                            bitmap1!!.config
                        )
                    } else if (count > 15) {
                        imagesContainedInARow = 6
                        mergedImages = Bitmap.createBitmap(
                            bitmap1!!.width * 6,
                            bitmap1!!.height * 4,
                            bitmap1!!.config
                        )
                    } else if (count > 12) {
                        imagesContainedInARow = 5
                        mergedImages = Bitmap.createBitmap(
                            bitmap1!!.width * 5,
                            bitmap1!!.height * 3,
                            bitmap1!!.config
                        )
                    } else {
                        imagesContainedInARow = 4
                        mergedImages = Bitmap.createBitmap(
                            bitmap1!!.width * 4,
                            bitmap1!!.height * 3,
                            bitmap1!!.config
                        )
                    }
                } else {
                    ncolumns++
                    if (ncolumns > imagesContainedInARow) {
                        nrows++
                        ncolumns = 1
                    }
                }
                val firstImage = mergedImages
                mergedImages =
                    createSingleImageFromMultipleImages(firstImage, bitmap1, nrows, ncolumns)
                irrh++
            }
        }
        //
        return mergedImages
    }
}