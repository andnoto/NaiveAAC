package com.sampietro.NaiveAAC.activities.Game.Game1

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.sampietro.NaiveAAC.R
import com.sampietro.NaiveAAC.activities.Graphics.GraphicsHelper
import com.sampietro.NaiveAAC.activities.Graphics.ImageSearchHelper
import com.sampietro.NaiveAAC.activities.Graphics.ResponseImageSearch
import io.realm.Realm
import java.io.File
import java.util.Locale

class Game1BleDialogFragment: DialogFragment()  {

    lateinit var realm: Realm
    lateinit var rootView: View
    lateinit var ctext: Context
    lateinit var sharedPref: SharedPreferences
    lateinit var preference_PrintPermissions: String
    lateinit var preference_TitleWritingType: String
    //
    var deviceEnabledUserName = "non trovato"
    var messageFromGattServer = "nessun messaggio"
    //
    var leftColumnContent: String? = null
    var leftColumnContentUrlType: String? = null
    var leftColumnContentUrl: String? = null
    var middleColumnContent: String? = null
    var middleColumnContentUrlType: String? = null
    var middleColumnContentUrl: String? = null
    var rightColumnContent: String? = null
    var rightColumnContentUrlType: String? = null
    var rightColumnContentUrl: String? = null
    //
    var leftColumnContentWord: String? = null
    var middleColumnContentWord: String? = null
    var rightColumnContentWord: String? = null
    //
    lateinit var listenAgainButton: ImageButton
    lateinit var continueGameButton: ImageButton
//    lateinit var messageToDisplay: TextView
    //
    lateinit var sendToBlueToothImage: ImageView
    //
    lateinit var img1: ImageView
    lateinit var img2: ImageView
    lateinit var img3: ImageView
    lateinit var title1: TextView
    lateinit var title2: TextView
    lateinit var title3: TextView

    /**
     * listener setting for game activities callbacks , context annotation, realm get default instance
     *
     */
    private lateinit var listenerGame1BleDialogFragment: onFragmentEventListenerGame1BleDialogFragment
    /**
     * <h1>onFragmentEventListenerGame1SecondLevelFragment</h1>
     *
     * **onFragmentEventListenerGame1SecondLevelFragment**
     * interface used to refer to the Activity without having to explicitly use its class
     *
     *
     *
     * @see .onAttach
     */
    interface onFragmentEventListenerGame1BleDialogFragment {
        // insert here any references to the Activity
        fun receiveResultOnClickFromGame1DialogFragment(v: View?)
        fun receiveResultFromGame1DialogFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        deviceEnabledUserName = arguments?.getString("DEVICE ENABLED USER NAME") ?: throw IllegalStateException("No DEVICE ENABLED USER NAME provided")
        messageFromGattServer = arguments?.getString("MESSAGE FROM GATT SERVER") ?: throw IllegalStateException("No MESSAGE FROM GATT SERVER provided")
        // use comma as separator
        val cvsSplitBy = ctext.getString(R.string.character_comma)
        val oneWord: Array<String?> =
            messageFromGattServer.split(cvsSplitBy.toRegex()).toTypedArray()
        if (oneWord[0] != getString(R.string.nessuno) && oneWord[0] != " " && preference_TitleWritingType == getString(R.string.uppercase))
        { leftColumnContent = oneWord[0]!!.uppercase(Locale.getDefault()) }
        else { leftColumnContent = oneWord[0]!!.lowercase(Locale.getDefault()) }
//        leftColumnContent = oneWord[0]
        leftColumnContentUrlType = oneWord[1]
        leftColumnContentUrl = oneWord[2]
        leftColumnContentWord = oneWord[3]
        if (oneWord[4] != getString(R.string.nessuno) && oneWord[4] != " " && preference_TitleWritingType == getString(R.string.uppercase))
        { middleColumnContent = oneWord[4]!!.uppercase(Locale.getDefault()) }
        else { middleColumnContent = oneWord[4]!!.lowercase(Locale.getDefault()) }
//        middleColumnContent = oneWord[4]
        middleColumnContentUrlType = oneWord[5]
        middleColumnContentUrl = oneWord[6]
        middleColumnContentWord = oneWord[7]
        if (oneWord[8] != getString(R.string.nessuno) && oneWord[8] != " " && preference_TitleWritingType == getString(R.string.uppercase))
        { rightColumnContent = oneWord[8]!!.uppercase(Locale.getDefault()) }
        else { rightColumnContent = oneWord[8]!!.lowercase(Locale.getDefault()) }
//        rightColumnContent = oneWord[8]
        rightColumnContentUrlType = oneWord[9]
        rightColumnContentUrl = oneWord[10]
        rightColumnContentWord = oneWord[11]
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val activity = context as Activity
        listenerGame1BleDialogFragment = activity as onFragmentEventListenerGame1BleDialogFragment
        //
        ctext = context
        //
        realm = Realm.getDefaultInstance()
        // if is print permitted then preference_PrintPermissions = Y
        sharedPref = ctext.getSharedPreferences(
            getString(R.string.preference_file_key), Context.MODE_PRIVATE
        )
        preference_PrintPermissions =
            sharedPref.getString(getString(R.string.preference_print_permissions), getString(R.string.default_string)).toString()
        preference_TitleWritingType =
            sharedPref.getString(getString(R.string.preference_title_writing_type), getString(R.string.uppercase)).toString()
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        rootView = inflater.inflate(R.layout.activity_game_1_ble_dialog, container, false)
        //
        listenAgainButton = rootView.findViewById<View>(R.id.dialog_listenagainbutton) as ImageButton
        listenAgainButton.setOnClickListener { view -> listenerGame1BleDialogFragment.receiveResultOnClickFromGame1DialogFragment(view) }
        continueGameButton = rootView.findViewById<View>(R.id.dialog_continuegamebutton) as ImageButton
        continueGameButton.setOnClickListener { view -> listenerGame1BleDialogFragment.receiveResultOnClickFromGame1DialogFragment(view) }
        //
//        messageToDisplay = rootView.findViewById<View>(R.id.dialog_message_to_display) as TextView
        sendToBlueToothImage = rootView.findViewById<View>(R.id.dialog_sendtobluetoothimage) as ImageView
        sendToBlueToothImage.setOnClickListener { view -> listenerGame1BleDialogFragment.receiveResultOnClickFromGame1DialogFragment(view) }
        //
        img1 = rootView.findViewById<View>(R.id.dialog_img1) as ImageView
        img1.scaleType = ImageView.ScaleType.CENTER_CROP
        img1.setOnClickListener { view -> listenerGame1BleDialogFragment.receiveResultOnClickFromGame1DialogFragment(view) }
        img2 = rootView.findViewById<View>(R.id.dialog_img2) as ImageView
        img2.scaleType = ImageView.ScaleType.CENTER_CROP
        img2.setOnClickListener { view -> listenerGame1BleDialogFragment.receiveResultOnClickFromGame1DialogFragment(view) }
        img3 = rootView.findViewById<View>(R.id.dialog_img3) as ImageView
        img3.scaleType = ImageView.ScaleType.CENTER_CROP
        img3.setOnClickListener { view -> listenerGame1BleDialogFragment.receiveResultOnClickFromGame1DialogFragment(view) }
        title1 = rootView.findViewById<View>(R.id.dialog_title1) as TextView
        title2 = rootView.findViewById<View>(R.id.dialog_title2) as TextView
        title3 = rootView.findViewById<View>(R.id.dialog_title3) as TextView

        //
//        messageToDisplay.setText(leftColumnContent + " " + middleColumnContent + " " + rightColumnContent)
        //
        if (deviceEnabledUserName != "non trovato")
        {
            val image: ResponseImageSearch?
            image = ImageSearchHelper.imageSearch(ctext, realm, deviceEnabledUserName)
            addImage(image!!.uriType, image.uriToSearch, sendToBlueToothImage, 150, 150)
        }
        else {
//            val uri = ctext.filesDir.absolutePath + "/images/puntointerrogativo.png"
//            addImage("S", uri, sendToBlueToothImage, 150, 150)
            val assetsUrl = "file:///android_asset/" + "images/puntointerrogativo.png"
            GraphicsHelper.addImageUsingPicasso(assetsUrl, sendToBlueToothImage, 150, 150)
        }
        //
        if (leftColumnContent != getString(R.string.nessuno) && leftColumnContent != " ")
        {
            title1.text = leftColumnContent
            if (leftColumnContentUrlType == "A")
            {
                GraphicsHelper.addImageUsingPicasso(leftColumnContentUrl, img1, 150, 150)
            }
            else
            {
                val image: ResponseImageSearch?
                image = ImageSearchHelper.imageSearch(ctext, realm, leftColumnContentWord)
                addImage(image!!.uriType, image.uriToSearch, img1, 150, 150)
            }
        }
        if (middleColumnContent != getString(R.string.nessuno) && middleColumnContent != " ")
        {
            title2.text = middleColumnContent
            if (middleColumnContentUrlType == "A")
            {
                GraphicsHelper.addImageUsingPicasso(middleColumnContentUrl, img2, 150, 150)
            }
            else
            {
                val image: ResponseImageSearch?
                image = ImageSearchHelper.imageSearch(ctext, realm, middleColumnContentWord)
                addImage(image!!.uriType, image.uriToSearch, img2, 150, 150)
            }
        }
        if (rightColumnContent != getString(R.string.nessuno) && rightColumnContent != " ")
        {
            title3.text = rightColumnContent
            if (rightColumnContentUrlType == "A")
            {
                GraphicsHelper.addImageUsingPicasso(rightColumnContentUrl, img3, 150, 150)
            }
            else
            {
                val image: ResponseImageSearch?
                image = ImageSearchHelper.imageSearch(ctext, realm, rightColumnContentWord)
                addImage(image!!.uriType, image.uriToSearch, img3, 150, 150)
            }

        }
        //
        listenerGame1BleDialogFragment.receiveResultFromGame1DialogFragment()
//
        return rootView
    }

    /**
     * load an image in imageview from a url or from a file
     *
     * @param urlType if string equal to "A" the image is loaded from a url otherwise it is loaded from a file
     * @param url string with url or file path of origin
     * @param img target imageview
     * @param width int with the width of the target imageview
     * @param height int with the height of the target imageview
     * @see GraphicsHelper.addImageUsingPicasso
     */
    fun addImage(urlType: String, url: String?, img: ImageView?, width: Int, height: Int) {
        if (urlType == "A") {
            GraphicsHelper.addImageUsingPicasso(url, img, width, height)
        } else {
            val f = File(url!!)
            GraphicsHelper.addFileImageUsingPicasso(f, img, width, height)
        }
    }
    companion object {

        //  apply
        //  The context object is available as a receiver (this).
        //
        //  The return value is the object itself.
        //
        //  As apply returns the context object itself, we recommend that you use it for code blocks
        //  that don't return a value and that mainly operate on the members of the receiver object.
        //  The most common use case for apply is for object configuration.
        //  Such calls can be read as "apply the following assignments to the object."
        //  example
        //  val adam = Person("Adam").apply {
        //    age = 32
        //    city = "London"
        //  }
        //  println(adam)
        //  Another use case for apply is to include apply in multiple call chains for more complex processing.

        fun newInstance(
            deviceEnabledUserName: String,
            messageFromGattServer: String
        ): Game1BleDialogFragment = Game1BleDialogFragment().apply {
            arguments = Bundle().apply {
                putString("DEVICE ENABLED USER NAME", deviceEnabledUserName)
                putString("MESSAGE FROM GATT SERVER", messageFromGattServer)
            }
        }
    }

}