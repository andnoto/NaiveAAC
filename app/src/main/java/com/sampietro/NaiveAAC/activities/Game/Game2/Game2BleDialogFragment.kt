package com.sampietro.NaiveAAC.activities.Game.Game2

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sampietro.NaiveAAC.R
import com.sampietro.NaiveAAC.activities.Game.GameADA.GameADAArrayList
import com.sampietro.NaiveAAC.activities.Graphics.GraphicsAndPrintingHelper.addImage
import com.sampietro.NaiveAAC.activities.Graphics.GraphicsAndPrintingHelper.addImageUsingPicasso
import com.sampietro.NaiveAAC.activities.Graphics.ImageSearchHelper.imageSearch
import com.sampietro.NaiveAAC.activities.Graphics.ResponseImageSearch
import io.realm.Realm
import java.util.ArrayList
import java.util.Locale

class Game2BleDialogFragment: DialogFragment()  {

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
    val theimage = ArrayList<GameADAArrayList>()
    //
    lateinit var listenAgainButton: ImageButton
    lateinit var continueGameButton: ImageButton
    //
    lateinit var sendToBlueToothImage: ImageView
    //
    lateinit var img1: ImageView
    lateinit var img2: ImageView
    lateinit var img3: ImageView

    /**
     * listener setting for game activities callbacks , context annotation, realm get default instance
     *
     */
    private lateinit var listenerGame2BleDialogFragment: onFragmentEventListenerGame2BleDialogFragment
    /**
     * <h1>onFragmentEventListenerGame2BleDialogFragment</h1>
     *
     * **onFragmentEventListenerGame2BleDialogFragment**
     * interface used to refer to the Activity without having to explicitly use its class
     *
     * @see onAttach
     */
    interface onFragmentEventListenerGame2BleDialogFragment {
        // insert here any references to the Activity
        fun receiveResultOnClickFromGame2DialogFragment(v: View?)
        fun receiveResultFromGame2DialogFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        deviceEnabledUserName = arguments?.getString("DEVICE ENABLED USER NAME") ?: throw IllegalStateException("No DEVICE ENABLED USER NAME provided")
        messageFromGattServer = arguments?.getString("MESSAGE FROM GATT SERVER") ?: throw IllegalStateException("No MESSAGE FROM GATT SERVER provided")
        // use comma as separator
        val cvsSplitBy = ctext.getString(R.string.character_comma)
        val oneWord: Array<String?> =
            messageFromGattServer.split(cvsSplitBy.toRegex()).toTypedArray()
        val oneWordSize = oneWord.size
        //
        var irrh = 0
        while (irrh < oneWordSize) {
            if (oneWord[irrh] == " ")
                break
            val createList = GameADAArrayList()
            if (oneWord[irrh] == getString(R.string.io))
            { oneWord[irrh] = " " }
            if (oneWord[irrh] != getString(R.string.nessuno) && oneWord[irrh] != " " && preference_TitleWritingType == getString(R.string.uppercase))
            { createList.image_title = oneWord[irrh]!!.uppercase(Locale.getDefault()) }
            else { createList.image_title = oneWord[irrh]!!.lowercase(Locale.getDefault()) }
            createList.image_title = oneWord[irrh]
            createList.urlType = oneWord[irrh+1]
            createList.url = oneWord[irrh+2]
            theimage.add(createList)
            irrh = irrh+3
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val activity = context as Activity
        listenerGame2BleDialogFragment = activity as onFragmentEventListenerGame2BleDialogFragment
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
        rootView = inflater.inflate(R.layout.activity_game_2_ble_dialog, container, false)
        //
        listenAgainButton = rootView.findViewById<View>(R.id.dialog_listenagainbutton) as ImageButton
        listenAgainButton.setOnClickListener { view -> listenerGame2BleDialogFragment.receiveResultOnClickFromGame2DialogFragment(view) }
        continueGameButton = rootView.findViewById<View>(R.id.dialog_continuegamebutton) as ImageButton
        continueGameButton.setOnClickListener { view -> listenerGame2BleDialogFragment.receiveResultOnClickFromGame2DialogFragment(view) }
        //
        sendToBlueToothImage = rootView.findViewById<View>(R.id.dialog_sendtobluetoothimage) as ImageView
        sendToBlueToothImage.setOnClickListener { view -> listenerGame2BleDialogFragment.receiveResultOnClickFromGame2DialogFragment(view) }
        //
        //
        val recyclerView1 = rootView.findViewById<View>(R.id.dialog_imagegallery) as RecyclerView
//        recyclerView1.setHasFixedSize(true)
        val layoutManager1: RecyclerView.LayoutManager =
            LinearLayoutManager(ctext, LinearLayoutManager.HORIZONTAL, false)
        recyclerView1.layoutManager = layoutManager1
        val adapter1 = Game2BleRecyclerViewAdapter(ctext, theimage)
        recyclerView1.adapter = adapter1
        //
        if (deviceEnabledUserName != "non trovato")
        {
            val image: ResponseImageSearch?
            image = imageSearch(ctext, realm, deviceEnabledUserName)
            addImage(image!!.uriType, image.uriToSearch, sendToBlueToothImage, 150, 150)
        }
        else {
            val assetsUrl = "file:///android_asset/" + "images/puntointerrogativo.png"
            addImageUsingPicasso(assetsUrl, sendToBlueToothImage, 150, 150)
        }
        //
        listenerGame2BleDialogFragment.receiveResultFromGame2DialogFragment()
        //
        return rootView
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
        ): Game2BleDialogFragment = Game2BleDialogFragment().apply {
            arguments = Bundle().apply {
                putString("DEVICE ENABLED USER NAME", deviceEnabledUserName)
                putString("MESSAGE FROM GATT SERVER", messageFromGattServer)
            }
        }
    }

}