package com.sampietro.NaiveAAC.activities.Settings

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import com.sampietro.NaiveAAC.R
import com.sampietro.NaiveAAC.activities.BaseAndAbstractClass.ActivityAbstractClass
import com.sampietro.NaiveAAC.activities.BaseAndAbstractClass.FragmentAbstractClassWithListenerWithoutConstructor
import com.sampietro.NaiveAAC.activities.DataStorage.DataStorageHelper.copyFileFromSharedToInternalStorageAndGetPath
import com.sampietro.NaiveAAC.activities.Game.Game2.SettingsStoriesRegistrationActivity
import com.sampietro.NaiveAAC.activities.Game.GameADA.SettingsStoriesImprovementActivity
import com.sampietro.NaiveAAC.activities.Game.GameParameters.GameParameters
import com.sampietro.NaiveAAC.activities.Game.Utils.ActionbarFragment
import com.sampietro.NaiveAAC.activities.Settings.Utils.ImageSearchArasaacFragment
import com.sampietro.NaiveAAC.activities.Settings.Utils.ImageSearchArasaacRecyclerViewAdapterInterface
import com.sampietro.NaiveAAC.activities.Stories.Stories
import com.sampietro.NaiveAAC.activities.Stories.StoriesListAdapter
import com.sampietro.NaiveAAC.activities.Stories.VoiceToBeRecordedInStories
import com.sampietro.NaiveAAC.activities.Stories.VoiceToBeRecordedInStoriesViewModel
import io.realm.Realm
import io.realm.RealmResults
import io.realm.Sort
import java.io.ByteArrayOutputStream
import java.net.URISyntaxException
import java.util.*

/**
 * <h1>SettingsStoriesActivity</h1>
 *
 * **SettingsStoriesActivity** app settings.
 * Refer to [developer.android.com](https://developer.android.com/guide/fragments/communicate)
 *
 * @version     5.0, 01/04/2024
 * @see FragmentAbstractClassWithListenerWithoutConstructor
 * @see StoriesListAdapter
 */
class SettingsStoriesActivity : ActivityAbstractClass(),
    FragmentAbstractClassWithListenerWithoutConstructor.onBaseFragmentEventListenerSettings,
    StoriesListAdapter.StoriesListAdapterInterface,
    ImageSearchArasaacRecyclerViewAdapterInterface {
    //
    var fragmentManager: FragmentManager? = null
    //
    private var voiceToBeRecordedInStories: VoiceToBeRecordedInStories? = null
    private lateinit var viewModel: VoiceToBeRecordedInStoriesViewModel
    /**
     * configurations of settings start screen.
     *
     * @param savedInstanceState Define potentially saved parameters due to configurations changes.
     * @see ActionbarFragment
     * @see Activity.onCreate
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        //
        context = this
        /*
        Both your fragment and its host activity can retrieve a shared instance of a ViewModel with activity scope by passing the activity into the ViewModelProvider
        constructor.
        The ViewModelProvider handles instantiating the ViewModel or retrieving it if it already exists. Both components can observe and modify this data
         */
        // In the Activity#onCreate make the only setItem
        voiceToBeRecordedInStories = VoiceToBeRecordedInStories()
        viewModel = ViewModelProvider(this).get(
            VoiceToBeRecordedInStoriesViewModel::class.java
        )
        viewModel.setItem(voiceToBeRecordedInStories!!)
        voiceToBeRecordedInStories!!.story = getString(R.string.nome_storia)
        voiceToBeRecordedInStories!!.phraseNumberInt = 0
        clearFieldsOfViewmodelDataClass()
        //
        setActivityResultLauncher()
        //
        if (savedInstanceState == null) {
            fragmentManager = supportFragmentManager
            fragmentManager!!.beginTransaction()
                .add(ActionbarFragment(), getString(R.string.actionbar_fragment))
                .add(R.id.settings_container, StoriesFirstFragment(), "StoriesFirstFragment")
                .commit()
        }
        // The MainActivity class provides an instance of Realm wherever needed in the application.
        // To use it in the Activity, a reference must be obtained to a Realm object in the onCreate method.
        // The Realm object must be closed in the onDestroy method.
        realm = Realm.getDefaultInstance()
    }
    /**
     * Called when the user taps the record new story button.
     *
     * @param v view of tapped button
     */
    fun searchNewStoryGameImage(v: View) {
        val frag = StoriesGameImageFragment()
        //
        val ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.settings_container, frag)
        ft.addToBackStack(null)
        ft.commit()
    }
    //
    @JvmField
    var imageSearchStoriesActivityResultLauncher: ActivityResultLauncher<Intent>? = null
    @JvmField
    var uri: Uri? = null
    @JvmField
    var filePath: String? = null
    lateinit var byteArray: ByteArray
    /**
     * setting callbacks to search for images and videos via ACTION_OPEN_DOCUMENT which is
     * the intent to choose a file via the system's file browser
     *
     *
     * REFER to [stackoverflow](https://stackoverflow.com/questions/62671106/onactivityresult-method-is-deprecated-what-is-the-alternative)
     * answer of [Muntashir Akon](https://stackoverflow.com/users/4147849/muntashir-akon)
     *
     *
     * and to [stackoverflow](https://stackoverflow.com/questions/7620401/how-to-convert-image-file-data-in-a-byte-array-to-a-bitmap)
     * answer of [Uttam](https://stackoverflow.com/users/840861/uttam)
     *
     * @see FragmentAbstractClassWithListener
     */
    fun setActivityResultLauncher() {
        //
        imageSearchStoriesActivityResultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult(),
            object : ActivityResultCallback<ActivityResult?> {
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
                                filePath = copyFileFromSharedToInternalStorageAndGetPath(context,
                                    uri!!
                                )
                            } catch (e: URISyntaxException) {
                                e.printStackTrace()
                            }
                            //
                            //
                            val takeFlags =
                                resultData.flags and Intent.FLAG_GRANT_READ_URI_PERMISSION
                            context.contentResolver.takePersistableUriPermission(
                                uri!!,
                                takeFlags
                            )
                            //
                            var bitmap: Bitmap? = null
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
                            val stream = ByteArrayOutputStream()
                            bitmap!!.compress(Bitmap.CompressFormat.PNG, 100, stream)
                            byteArray = stream.toByteArray()
                            //
//                                Viewmodel
//                                In the activity, sometimes it is called observe, other times it is limited to performing set directly
//                                (maybe it is not necessary to call observe)
                            voiceToBeRecordedInStories!!.uriType = "S"
                            voiceToBeRecordedInStories!!.uri = filePath
                            //
                            val frag = StoriesGameImageFragment()
                            //
                            val ft = supportFragmentManager.beginTransaction()
                            ft.replace(R.id.settings_container, frag)
                            ft.addToBackStack(null)
                            ft.commit()
                        }
                    }
                }
            })
    }
    /**
     * Called when the user taps the image search button.
     *
     * @param v view of tapped button
     */
    fun imageSearch(v: View) {
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
            R.id.buttongameimagesearchstories -> {
                /*  the instructions of the button */
                val textWord1 = findViewById<View>(R.id.keywordstorytoadd) as EditText
                // Viewmodel
                // In the activity, sometimes it is called observe, other times it is limited to performing set directly
                // (maybe it is not necessary to call observe)
                voiceToBeRecordedInStories!!.story =
                    textWord1.text.toString().lowercase(Locale.getDefault())
                //
                imageSearchStoriesActivityResultLauncher!!.launch(intent)
            }
        }
    }
    /**
     * Called when the user taps the image search Arasaac button from Stories Fragment.
     *
     * @param v view of tapped button
     */
    fun imageSearchArasaac(v: View?) {
        val textWord1 = findViewById<View>(R.id.keywordstorytoadd) as EditText
        //
        // view the image search Arasaac  initializing ImageSearchArasaacFragment (FragmentTransaction
        // switch between Fragments).
        // Viewmodel
        // In the activity, sometimes it is called observe, other times it is limited to performing set directly
        // (maybe it is not necessary to call observe)
        voiceToBeRecordedInStories!!.story =
            textWord1.text.toString().lowercase(Locale.getDefault())
        //
        val frag = ImageSearchArasaacFragment()
        //
        val bundle = Bundle()
        bundle.putString("keywordToSearchArasaac", textWord1.text.toString())
        frag.arguments = bundle
        //
        val ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.settings_container, frag)
        ft.addToBackStack(null)
        ft.commit()
    }
    /**
     * Called when the user taps the image search Arasaac button from StoriesImagesSearchArasaac Fragment.
     *
     * @param v view of tapped button
     */
    fun arasaacSearch(v: View?) {
        val textWord1 = findViewById<View>(R.id.keywordarasaactosearch) as EditText
        // view the image search Arasaac  initializing ImageSearchArasaacFragment (FragmentTransaction
        // switch between Fragments).
        val frag = ImageSearchArasaacFragment()
        //
        val bundle = Bundle()
        bundle.putString("keywordToSearchArasaac", textWord1.text.toString())
        frag.arguments = bundle
        //
        val ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.settings_container, frag)
        ft.addToBackStack(null)
        ft.commit()
    }
    /**
     * Called when the user taps the image Arasaac .
     *
     * @param view view of the image Arasaac
     * @param word string whit the keyword of the image Arasaac
     * @param url string whit the url of the image Arasaac
     */
    override fun onItemClick(view: View?, word: String?, url: String?) {
        // Viewmodel
        // In the activity, sometimes it is called observe, other times it is limited to performing set directly
        // (maybe it is not necessary to call observe)
        viewModel.getSelectedItem()
            .observe(this) { voiceToBeRecordedInStories: VoiceToBeRecordedInStories ->
                // Perform an action with the latest item data
                voiceToBeRecordedInStories.uriType = "A"
                voiceToBeRecordedInStories.uri = url
                //
                val frag = StoriesGameImageFragment()
                //
                val ft = supportFragmentManager.beginTransaction()
                ft.replace(R.id.settings_container, frag)
                ft.addToBackStack(null)
                ft.commit()
            }
    }
    /**
     * receives calls from fragment listeners.
     *
     * @param v view of calling fragment
     */
    override fun receiveResultSettings(v: View?) {
        when (v!!.id) {
            R.id.btn_search_arasaac -> {
                arasaacSearch(v)
            }
        }
    }
    /**
     * Called when the user taps the add button from stories settings.
     *
     * after the checks it adds the piece of story on realm
     *
     * and the activity is notified to view the stories settings.
     *
     * @param v view of tapped button
     * @see StoriesFragment
     *
     * @see Stories
     */
    fun storyToAdd(v: View?) {
        // Viewmodel
        // In the activity, sometimes it is called observe, other times it is limited to performing set directly
        // (maybe it is not necessary to call observe)
        viewModel.getSelectedItem()
            .observe(this) { voiceToBeRecordedInStories: VoiceToBeRecordedInStories ->
                // Perform an action with the latest item data
                voiceToBeRecordedInStories.story = getString(R.string.nome_storia)
                //
                val textWord1 = findViewById<View>(R.id.keywordstorytoadd) as EditText
                //
                if (textWord1.text.toString().lowercase(Locale.getDefault()) != "nome storia")
                {
                    val resultsStories = realm.where(Stories::class.java)
                        .equalTo(getString(R.string.story), textWord1.text.toString().lowercase(Locale.getDefault()))
                        .findAll()
                    val storiesSize = resultsStories.size
                    //
                    if (storiesSize == 0) {
                        // registro nuova storia su GameParameters
                        // Note that the realm object was generated with the createObject method
                        // and not with the new operator.
                        // The modification operations will be performed within a Transaction.
                        realm.beginTransaction()
                        val gp = realm.createObject(GameParameters::class.java)
                        gp.gameName = textWord1.text.toString().lowercase(Locale.getDefault())
                        gp.gameActive = "A"
                        gp.gameInfo = textWord1.text.toString().lowercase(Locale.getDefault())
                        gp.gameJavaClass = getString(R.string.a_da)
                        gp.gameParameter = textWord1.text.toString().lowercase(Locale.getDefault())
                        if (voiceToBeRecordedInStories.uriType != "")
                            {
                            gp.gameIconType = voiceToBeRecordedInStories.uriType
                            gp.gameIconPath = voiceToBeRecordedInStories.uri
                            }
                        else
                        {
                            gp.gameIconType = "AS"
                            gp.gameIconPath = "images/racconto.png"
                        }
                        gp.gameUseVideoAndSound = "N"
                        gp.fromAssets = "N"
                        realm.commitTransaction()

                        /*
                        navigate to settings stories registration activity
                        */
                        val intent = Intent(context, SettingsStoriesRegistrationActivity::class.java)
                        intent.putExtra(getString(R.string.story), textWord1.text.toString().lowercase(Locale.getDefault()))
                        startActivity(intent)
                    }
                }
            }
    }
    /**
     * clear fields of viewmodel data class
     *
     *
     * @see VoiceToBeRecordedInStories
     */
    fun clearFieldsOfViewmodelDataClass() {
        voiceToBeRecordedInStories!!.wordNumberInt = 0
        voiceToBeRecordedInStories!!.word = ""
        voiceToBeRecordedInStories!!.uriType = ""
        voiceToBeRecordedInStories!!.uri = ""
        voiceToBeRecordedInStories!!.answerActionType = ""
        voiceToBeRecordedInStories!!.answerAction = ""
        voiceToBeRecordedInStories!!.video = ""
        voiceToBeRecordedInStories!!.sound = ""
        voiceToBeRecordedInStories!!.soundReplacesTTS = ""
    }

    /**
     * Called when the user taps the show list button from the contents settings menu.
     *
     * the activity is notified to view the stories list.
     *
     *
     * @param view view of tapped button
     * @see StoriesListFragment
     */
    fun submitStoriesShowList(view: View?) {
        // Viewmodel
        // In the activity, sometimes it is called observe, other times it is limited to performing set directly
        // (maybe it is not necessary to call observe)
        voiceToBeRecordedInStories!!.story = getString(R.string.nome_storia)
        voiceToBeRecordedInStories!!.phraseNumberInt = 0
        // view the stories list  initializing StoriesListFragment (FragmentTransaction
        // switch between Fragments).
        val frag = StoriesListFragment()
        //
        val ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.settings_container, frag)
        ft.addToBackStack(null)
        ft.commit()
    }

    /**
     * Called when the user taps the story search button from the stories list.
     *
     * the activity is notified to view the stories list.
     *
     *
     * @param view view of tapped button
     * @see StoriesListFragment
     */
    fun storiesSearch(view: View?) {
        val textWord1 = findViewById<View>(R.id.keywordstorytosearch) as TextView
        // Viewmodel
        // In the activity, sometimes it is called observe, other times it is limited to performing set directly
        // (maybe it is not necessary to call observe)
        voiceToBeRecordedInStories!!.story =
            textWord1.text.toString().lowercase(Locale.getDefault())
        //
        val frag = StoriesListFragment()
        val ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.settings_container, frag)
        ft.addToBackStack(null)
        ft.commit()
    }

    override fun loadSettingsStoriesImprovementActivityForEditing(position: Int) {
        viewModel.getSelectedItem()
            .observe(this) { voiceToBeRecordedInStories: VoiceToBeRecordedInStories ->
                // Perform an action with the latest item data
                realm = Realm.getDefaultInstance()
                var results: RealmResults<Stories>
                val phraseNumberToSearch = 1
                val wordNumberToSearch = 0
                //
                results = if (voiceToBeRecordedInStories.story == getString(R.string.nome_storia)) {
                    realm.where(Stories::class.java)
                        .equalTo(getString(R.string.phrasenumberint), phraseNumberToSearch)
                        .equalTo(getString(R.string.wordnumberint), wordNumberToSearch)
                        .findAll()
                } else {
                    realm.where(Stories::class.java)
                        .equalTo(
                            getString(R.string.story),
                            voiceToBeRecordedInStories.story!!.lowercase(Locale.getDefault())
                        )
                        .equalTo(getString(R.string.phrasenumberint), phraseNumberToSearch)
                        .equalTo(getString(R.string.wordnumberint), wordNumberToSearch)
                        .findAll()
                }
                //
                val mStrings1 = arrayOf(getString(R.string.story))
                val mStrings2 = arrayOf(Sort.ASCENDING)
                results = results.sort(mStrings1, mStrings2)
                /*
                navigate to settings stories registration activity
                */
                val intent = Intent(context, SettingsStoriesImprovementActivity::class.java)
                intent.putExtra(getString(R.string.story), results[position]!!.story)
                startActivity(intent)
            }
    }
}