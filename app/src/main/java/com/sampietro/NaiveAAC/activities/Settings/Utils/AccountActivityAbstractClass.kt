package com.sampietro.NaiveAAC.activities.Settings.Utils

import androidx.appcompat.app.AppCompatActivity
import android.content.SharedPreferences
import androidx.activity.result.ActivityResultLauncher
import android.content.Intent
import androidx.activity.result.ActivityResultCallback
import android.app.Activity
import com.sampietro.NaiveAAC.R
import android.graphics.Bitmap
import android.provider.MediaStore
import com.google.android.material.snackbar.Snackbar
import android.widget.EditText
import com.sampietro.NaiveAAC.activities.Settings.VideosFragment
import android.os.Bundle
import android.widget.TextView
import kotlin.Throws
import android.graphics.drawable.BitmapDrawable
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.documentfile.provider.DocumentFile
import android.annotation.SuppressLint
import android.content.Context
import android.database.Cursor
import android.graphics.Color
import android.graphics.ImageDecoder
import android.graphics.Matrix
import android.net.Uri
import android.provider.DocumentsContract
import android.os.Environment
import android.view.ContextThemeWrapper
import android.view.View
import android.widget.ImageView
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import io.realm.Realm
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import java.lang.Exception
import java.lang.NullPointerException
import java.net.URISyntaxException
import java.util.*

/**
 * <h1>AccountActivityAbstractClass</h1>
 *
 * **AccountActivityAbstractClass**
 * abstract class containing common methods that is extended by
 *
 * 1) AccountActivity
 * 2) AccountActivityRealmCreation
 * 3) SettingsActivity
 *
 * @version     4.0, 09/09/2023
 * @see com.sampietro.NaiveAAC.activities.Account.AccountActivity
 *
 * @see com.sampietro.NaiveAAC.activities.Account.AccountActivityRealmCreation
 *
 * @see com.sampietro.NaiveAAC.activities.Settings.SettingsActivity
 */
abstract class AccountActivityAbstractClass : AppCompatActivity() {
    //
    @JvmField
    var rootViewFragment: View? = null
//    @JvmField
    lateinit var context: Context
//    @JvmField
    lateinit var sharedPref: SharedPreferences

    // Realm
//    @JvmField
    lateinit var realm: Realm

    // ActivityResultLauncher
    var imageSearchActivityResultLauncher: ActivityResultLauncher<Intent>? = null
    var imageSearchGameParametersActivityResultLauncher: ActivityResultLauncher<Intent>? = null
    var imageSearchAccountActivityResultLauncher: ActivityResultLauncher<Intent>? = null
    @JvmField
    var imageSearchStoriesActivityResultLauncher: ActivityResultLauncher<Intent>? = null
    @JvmField
    var videoSearchActivityResultLauncher: ActivityResultLauncher<Intent>? = null
    @JvmField
    var videoSearchWordPairsActivityResultLauncher: ActivityResultLauncher<Intent>? = null
    @JvmField
    var soundSearchActivityResultLauncher: ActivityResultLauncher<Intent>? = null
    @JvmField
    var csvSearchActivityResultLauncher: ActivityResultLauncher<Intent>? = null
    @JvmField
    var exportCsvSearchActivityResultLauncher: ActivityResultLauncher<Intent>? = null
    @JvmField
    var csvTreeUri: Uri? = null

    //
    @JvmField
    var uri: Uri? = null
    @JvmField
    var stringUri: String? = null

    //
    @JvmField
    var gameIconType: String? = null

    //
    @JvmField
    var filePath: String? = null
    var fileName: String? = null
//    @JvmField
    lateinit var byteArray: ByteArray
    //
    /**
     * setting callbacks to search for images and videos via ACTION_OPEN_DOCUMENT which is
     * the intent to choose a file via the system's file browser
     *
     *
     * Refer to [stackoverflow](https://stackoverflow.com/questions/62671106/onactivityresult-method-is-deprecated-what-is-the-alternative)
     * answer of [Muntashir Akon](https://stackoverflow.com/users/4147849/muntashir-akon)
     * and
     * Refer to [stackoverflow](https://stackoverflow.com/questions/56651444/deprecated-getbitmap-with-api-29-any-alternative-codes)
     * answer of [Ally](https://stackoverflow.com/users/6258197/ally)
     *
     * @see .getFilePath
     *
     * @see .showImage
     */
    open fun setActivityResultLauncher() {
        // You can do the assignment inside onAttach or onCreate, i.e, before the activity is displayed
        imageSearchActivityResultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult(),
            object : ActivityResultCallback<ActivityResult?> {
//                @RequiresApi(Build.VERSION_CODES.P)
                override fun onActivityResult(result: ActivityResult?) {
                    if (result!!.resultCode == RESULT_OK) {
                        // There are no request codes
                        val resultData = result.data
                        // doSomeOperations();
                        uri = null
                        filePath = getString(R.string.non_trovato)
                        if (resultData != null) {
                            uri = Objects.requireNonNull(resultData).data
                            //
                            try {
                                filePath = getFilePath(context, uri)
                            } catch (e: URISyntaxException) {
                                e.printStackTrace()
                            }
                            //
//                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                                val takeFlags =
                                    resultData.flags and Intent.FLAG_GRANT_READ_URI_PERMISSION
                                context.contentResolver.takePersistableUriPermission(
                                    uri!!,
                                    takeFlags
                                )
//                            }
                            //
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
//                            try {
//                                bitmap = MediaStore.Images.Media.getBitmap(
//                                    context!!.contentResolver,
//                                    uri
//                                )
//                            } catch (e: IOException) {
//                                e.printStackTrace()
//                            }
                            val stream = ByteArrayOutputStream()
                            bitmap!!.compress(Bitmap.CompressFormat.PNG, 100, stream)
                            byteArray = stream.toByteArray()
                            //
                            val myImage: ImageView
                            myImage = findViewById<View>(R.id.imageviewTest) as ImageView
                            showImage(uri, myImage)
                        }
                    }
                }
            })
        //
        imageSearchGameParametersActivityResultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult(),
            object : ActivityResultCallback<ActivityResult?> {
//                @RequiresApi(Build.VERSION_CODES.P)
                override fun onActivityResult(result: ActivityResult?) {
                    if (result!!.resultCode == RESULT_OK) {
                        // There are no request codes
                        val resultData = result.data
                        // doSomeOperations();
                        uri = null
                        filePath = getString(R.string.non_trovato)
                        //
                        if (resultData != null) {
                            uri = Objects.requireNonNull(resultData).data
                            //
                            try {
                                filePath = getFilePath(context, uri)
                                gameIconType = "S"
                            } catch (e: URISyntaxException) {
                                e.printStackTrace()
                            }
                            //
//                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                                val takeFlags =
                                    resultData.flags and Intent.FLAG_GRANT_READ_URI_PERMISSION
                                context.contentResolver.takePersistableUriPermission(
                                    uri!!,
                                    takeFlags
                                )
//                            }
                            //
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
                            //
                            val stream = ByteArrayOutputStream()
                            bitmap!!.compress(Bitmap.CompressFormat.PNG, 100, stream)
                            byteArray = stream.toByteArray()
                            //
                            val myImage: ImageView
                            myImage = findViewById<View>(R.id.imageviewgameicon) as ImageView
                            showImage(uri, myImage)
                        }
                    }
                }
            })
        //
        imageSearchAccountActivityResultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult(),
            object : ActivityResultCallback<ActivityResult?> {
//                @RequiresApi(Build.VERSION_CODES.P)
                override fun onActivityResult(result: ActivityResult?) {
                    if (result!!.resultCode == RESULT_OK) {
                        // There are no request codes
                        val resultData = result.data
                        // doSomeOperations();
                        uri = null
                        filePath = getString(R.string.non_trovato)
                        //
                        if (resultData != null) {
                            uri = Objects.requireNonNull(resultData).data
                            //
                            try {
                                filePath = getFilePath(context, uri)
                            } catch (e: URISyntaxException) {
                                e.printStackTrace()
                            }
                            //
                            if (filePath != getString(R.string.non_trovato))
                            {
                                if (filePath == "da download") {
                                    val ctw = ContextThemeWrapper(context, R.style.CustomSnackbarTheme)
                                    val snackbar = Snackbar.make(
                                        ctw,
                                        findViewById(R.id.imageviewaccounticon),
                                        "al momento l'app non è in grado di accedere ad immagini nella cartella download",
                                        10000
                                    )
                                    snackbar.setTextMaxLines(5)
                                    snackbar.setTextColor(Color.BLACK)
                                    snackbar.show()
                                }
//                                if (filePath != getString(R.string.non_trovato)
//                                    && filePath != "da download"
//                                ) {
                                    //
//                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                                else {
                                    val takeFlags =
                                        resultData.flags and Intent.FLAG_GRANT_READ_URI_PERMISSION
                                        context.contentResolver.takePersistableUriPermission(
                                        uri!!,
                                        takeFlags
                                    )
//                                }
                                    //
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
                                    //
                                    val stream = ByteArrayOutputStream()
                                    bitmap!!.compress(Bitmap.CompressFormat.PNG, 100, stream)
                                    byteArray = stream.toByteArray()
                                    //
                                    val myImage: ImageView
                                    myImage = findViewById<View>(R.id.imageviewaccounticon) as ImageView
                                    showImage(uri, myImage)
                                }
                            }
                        }
                    }
                }
            })
        //
        videoSearchActivityResultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult(),
            object : ActivityResultCallback<ActivityResult?> {
                override fun onActivityResult(result: ActivityResult?) {
                    if (result!!.resultCode == RESULT_OK) {
                        // There are no request codes
                        val resultData = result.data
                        // doSomeOperations();
                        uri = null
                        stringUri = null
                        //
                        val vidD = findViewById<View>(R.id.videoDescription) as EditText
                        //
                        if (resultData != null) {
                            uri = Objects.requireNonNull(resultData).data
                            //
                            //
//                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                                val takeFlags =
                                    resultData.flags and Intent.FLAG_GRANT_READ_URI_PERMISSION
                                context.contentResolver.takePersistableUriPermission(
                                    uri!!,
                                    takeFlags
                                )
//                            }
                            //
                            stringUri = uri.toString()
                            //
                            val frag = VideosFragment()
                            val bundle = Bundle()
                            //
                            if (vidD.length() > 0) {
                                bundle.putString(
                                    getString(R.string.descrizione),
                                    vidD.text.toString()
                                )
                            } else {
                                bundle.putString(
                                    getString(R.string.descrizione),
                                    getString(R.string.nessuna)
                                )
                            }
                            //
                            bundle.putString(getString(R.string.uri), stringUri)
                            frag.arguments = bundle
                            val ft = supportFragmentManager.beginTransaction()
                            ft.replace(R.id.settings_container, frag)
                            ft.addToBackStack(null)
                            ft.commit()
                            //
                        }
                    }
                }
            })
        videoSearchWordPairsActivityResultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult(),
            object : ActivityResultCallback<ActivityResult?> {
                override fun onActivityResult(result: ActivityResult?) {
                    if (result!!.resultCode == RESULT_OK) {
                        // There are no request codes
                        val resultData = result.data
                        // doSomeOperations();
                        uri = null
                        stringUri = null
                        //
                        if (resultData != null) {
                            uri = Objects.requireNonNull(resultData).data
                            //
                            //
//                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                                val takeFlags =
                                    resultData.flags and Intent.FLAG_GRANT_READ_URI_PERMISSION
                                context.contentResolver.takePersistableUriPermission(
                                    uri!!,
                                    takeFlags
                                )
//                            }
                            //
                            stringUri = uri.toString()
                            //
                            val awardType = findViewById<EditText>(R.id.awardtype)
                            val uriPremiumVideo = findViewById<TextView>(R.id.uripremiumvideo)
                            try {
                                filePath = getFilePath(context, uri)
                                assert(filePath != null)
                                val cut = filePath!!.lastIndexOf('/')
                                if (cut != -1) {
                                    fileName = filePath!!.substring(cut + 1)
                                }
                                //
                            } catch (e: URISyntaxException) {
                                e.printStackTrace()
                            }
                            awardType.setText(getString(R.string.character_v))
                            uriPremiumVideo.text = fileName
                        }
                    }
                }
            })
        soundSearchActivityResultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult(),
            object : ActivityResultCallback<ActivityResult?> {
                override fun onActivityResult(result: ActivityResult?) {
                    if (result!!.resultCode == RESULT_OK) {
                        // There are no request codes
                        val resultData = result.data
                        // doSomeOperations();
                        uri = null
                        stringUri = null
                        //
                        if (resultData != null) {
                            uri = Objects.requireNonNull(resultData).data
                            //
                            //
//                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                                val takeFlags =
                                    resultData.flags and Intent.FLAG_GRANT_READ_URI_PERMISSION
                                context.contentResolver.takePersistableUriPermission(
                                    uri!!,
                                    takeFlags
                                )
//                            }
                            //
                            stringUri = uri.toString()
                            //
                            try {
                                filePath = getFilePath(context, uri)
                                assert(filePath != null)
                                val cut = filePath!!.lastIndexOf('/')
                                if (cut != -1) {
                                    fileName = filePath!!.substring(cut + 1)
                                }
                                //
                            } catch (e: URISyntaxException) {
                                e.printStackTrace()
                            }
                        }
                    }
                }
            })
    }

    /**
     * closing realm to be performed on destroy activity
     *
     * @see Activity.onDestroy
     */
    override fun onDestroy() {
        super.onDestroy()
//        if (realm != null) {
            realm.close()
//        }
    }

    /**
     * register the last player name on shared preferences.
     *
     * @param textPersonName string containing the player's name
     */
    fun registerPersonName(textPersonName: String?) {
        //    context = this;
        sharedPref = getSharedPreferences(
            getString(R.string.preference_file_key), MODE_PRIVATE
        )
        val editor = sharedPref.edit()
        editor.putString(getString(R.string.preference_LastPlayer), textPersonName)
        editor.apply()
    }

    /**
     * register the last player password on shared preferences.
     *
     * @param textPassword string containing the player's password
     */
    fun registerPassword(textPassword: String?) {
        //    context = this;
        sharedPref = getSharedPreferences(
            getString(R.string.preference_file_key), MODE_PRIVATE
        )
        val editor = sharedPref.edit()
        editor.putString("password", textPassword)
        editor.apply()
    }

    /**
     * Called when the user taps the image search button.
     *
     * @param v view of tapped button
     */
    open fun imageSearch(v: View) {
        // ACTION_OPEN_DOCUMENT is the intent to choose a file via the system's file
        // browser.
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        // Filter to only show results that can be "opened", such as a
        // file (as opposed to a list of contacts or timezones)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        // Filter to show only images, using the image MIME data type.
        // If one wanted to search for ogg vorbis files, the type would be "audio/ogg".
        // To search for all documents available via installed storage providers,
        // it would be "*/*".
        intent.type = "image/*"
        when (v.id) {
            R.id.buttonimagesearch ->                 /*  the instructions of the button */imageSearchActivityResultLauncher!!.launch(
                intent
            )
            R.id.buttonimagesearchgameparameters ->                 /*  the instructions of the button */imageSearchGameParametersActivityResultLauncher!!.launch(
                intent
            )
            R.id.buttonimagesearchaccount ->                 /*  the instructions of the button */imageSearchAccountActivityResultLauncher!!.launch(
                intent
            )
            R.id.buttonimagesearchstories ->                 /*  the instructions of the button */imageSearchStoriesActivityResultLauncher!!.launch(
                intent
            )
        }
    }

    /**
     * show the chosen image identified by uri.
     *
     *
     * A Uniform Resource Identifier (URI) is a compact sequence of characters that identifies an abstract or physical resource
     *
     * @param uri uri of the image
     * @param myImage ImageView where the image will be displayed
     * @see .scaleImage
     */
//    @RequiresApi(Build.VERSION_CODES.P)
    fun showImage(uri: Uri?, myImage: ImageView) {
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
//        try {
//            bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, uri)
//        } catch (e: IOException) {
//            e.printStackTrace()
//        }
        // On devices running SDK < 24 (Android 7.0), this method will fail to apply
        // correct density scaling to images loaded from content and file schemes.
        // Applications running on devices with SDK >= 24 MUST specify the
        // targetSdkVersion in their manifest as 24 or above for density scaling
        // to be applied to images loaded from these schemes.
        myImage.setImageBitmap(bitmap)
        scaleImage(bitmap, myImage)
    }

    /**
     * scale the image represented in the bitmap.
     * REFER to [stackoverflow](https://stackoverflow.com/questions/8232608/fit-image-into-imageview-keep-aspect-ratio-and-then-resize-imageview-to-image-d)
     * answer of [Jarno Argillander](https://stackoverflow.com/users/1030049/jarno-argillander)
     *
     * @param bitmap bitmap of the image
     * @param view imageview where the image will be displayed
     * @see .dpToPx
     */
    @Throws(NoSuchElementException::class)
    private fun scaleImage(bitmap: Bitmap?, view: ImageView) {
        // Get current dimensions AND the desired bounding box
        var width: Int
        width = try {
            bitmap!!.width
        } catch (e: NullPointerException) {
            throw NoSuchElementException("Can't find bitmap on given view/drawable")
        }
        var height = bitmap.height
        val bounding = dpToPx(150)
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
    private fun dpToPx(dp: Int): Int {
        val density = applicationContext.resources.displayMetrics.density
        return Math.round(dp.toFloat() * density)
    }
    //
    /**
     * copy file.
     *
     *
     *
     * @param sourceUri uri
     * @param destFileName string
     */
    @Throws(IOException::class)
    fun copyFileFromSharedToInternalStorage(sourceUri: Uri?, destFileName: String?) {
        val sourceStream = context.contentResolver.openInputStream(sourceUri!!)
        val destStream = openFileOutput(destFileName, MODE_PRIVATE)
        val buffer = ByteArray(1024)
        var read: Int
        while (sourceStream!!.read(buffer).also { read = it } != -1) {
            destStream.write(buffer, 0, read)
        }
        destStream.close()
        sourceStream.close()
    }
    //
    /**
     * copy file.
     *
     *
     *
     * @param outputFolder documentfile
     * @param destFileName string
     */
    @Throws(IOException::class)
    fun copyFileFromInternalToSharedStorage(outputFolder: DocumentFile, destFileName: String?) {
        val sourceStream: InputStream = context.openFileInput(destFileName)
        val documentFileNewFile = outputFolder.createFile("text/csv", destFileName!!)!!
        val destStream = context.contentResolver.openOutputStream(
            documentFileNewFile.uri
        )
        val buffer = ByteArray(1024)
        var read: Int
        while (sourceStream.read(buffer).also { read = it } != -1) {
            destStream!!.write(buffer, 0, read)
        }
        destStream?.close()
        sourceStream.close()
    }

    companion object {
        /**
         * get the FilePath from the uri.
         *
         * Refer to [stackoverflow](https://stackoverflow.com/questions/3401579/get-filename-and-path-from-uri-from-mediastore)
         * answer of [Jitendra Ramoliya](https://stackoverflow.com/users/4118297/jitendra-ramoliya)
         *
         *
         *
         * @param context context
         * @param uri uri
         * @return string with the filepath identified by uri from mediastore
         */
        @JvmStatic
        @SuppressLint("NewApi")
        @Throws(URISyntaxException::class)
        fun getFilePath(context: Context?, uri: Uri?): String? {
            var uri = uri
            var selection: String? = null
            var selectionArgs: Array<String>? = null
            // Uri is different in versions after KITKAT (Android 4.4), we need to
            if (
//                Build.VERSION.SDK_INT >= 19 &&
                DocumentsContract.isDocumentUri(
                    context!!.applicationContext,
                    uri
                )
            ) {
                if (isExternalStorageDocument(uri)) {
                    val docId = DocumentsContract.getDocumentId(uri)
                    val split = docId.split(":".toRegex()).toTypedArray()
                    return Environment.getExternalStorageDirectory().toString() + "/" + split[1]
                } else if (isDownloadsDocument(uri)) {
//            final String id = DocumentsContract.getDocumentId(uri);
//            uri = ContentUris.withAppendedId(
//                    Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
                    return "da download"
                } else if (isMediaDocument(uri)) {
                    val docId = DocumentsContract.getDocumentId(uri)
                    val split = docId.split(":".toRegex()).toTypedArray()
                    val type = split[0]
                    if ("image" == type) {
                        uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                    } else if ("video" == type) {
                        uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                    } else if ("audio" == type) {
                        uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                    }
                    selection = "_id=?"
                    selectionArgs = arrayOf(
                        split[1]
                    )
                }
            }
            if ("content".equals(uri!!.scheme, ignoreCase = true)) {
                if (isGooglePhotosUri(uri)) {
                    return uri.lastPathSegment
                }
                val projection = arrayOf(
                    MediaStore.Images.Media.DATA
                )
                val cursor: Cursor?
                try {
                    cursor = context.contentResolver
                        .query(uri, projection, selection, selectionArgs, null)
                    val column_index = cursor!!.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                    if (cursor.moveToFirst()) {
                        val filePathC = cursor.getString(column_index)
//                        return cursor.getString(column_index)
                        cursor.close()
                        return filePathC
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            } else if ("file".equals(uri.scheme, ignoreCase = true)) {
                return uri.path
            }
            return null
        }

        /**
         * gets document authority.
         *
         * @param uri uri
         * @return boolean true if uri refers to External Storage Document
         */
        fun isExternalStorageDocument(uri: Uri?): Boolean {
            return "com.android.externalstorage.documents" == uri!!.authority
        }

        /**
         * gets document authority.
         *
         * @param uri uri
         * @return boolean true if uri refers to downloaded documents
         */
        fun isDownloadsDocument(uri: Uri?): Boolean {
            return "com.android.providers.downloads.documents" == uri!!.authority
        }

        /**
         * gets document authority.
         *
         * @param uri uri
         * @return boolean true if uri refers to media documents
         */
        fun isMediaDocument(uri: Uri?): Boolean {
            return "com.android.providers.media.documents" == uri!!.authority
        }

        /**
         * gets document authority.
         *
         * @param uri uri
         * @return boolean true if uri refers to photos
         */
        fun isGooglePhotosUri(uri: Uri?): Boolean {
            return "com.google.android.apps.photos.content" == uri!!.authority
        }
    }

}