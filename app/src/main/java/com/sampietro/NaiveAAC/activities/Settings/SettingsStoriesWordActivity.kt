package com.sampietro.NaiveAAC.activities.Settings

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.RadioButton
import android.widget.TextView
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import com.sampietro.NaiveAAC.R
import com.sampietro.NaiveAAC.activities.BaseAndAbstractClass.ActivityAbstractClass
import com.sampietro.NaiveAAC.activities.BaseAndAbstractClass.FragmentAbstractClassWithListener
import com.sampietro.NaiveAAC.activities.DataStorage.DataStorageHelper.copyFileFromSharedToInternalStorageAndGetPath
import com.sampietro.NaiveAAC.activities.Game.GameADA.SettingsStoriesImprovementActivity
import com.sampietro.NaiveAAC.activities.Game.Utils.ActionbarFragment
import com.sampietro.NaiveAAC.activities.Graphics.Sounds
import com.sampietro.NaiveAAC.activities.Settings.StoriesSoundsSearchAdapter.StoriesSoundsSearchAdapterInterface
import com.sampietro.NaiveAAC.activities.Settings.StoriesVideosSearchAdapter.StoriesVideosSearchAdapterInterface
import com.sampietro.NaiveAAC.activities.Settings.Utils.ImageSearchArasaacFragment
import com.sampietro.NaiveAAC.activities.Settings.Utils.ImageSearchArasaacRecyclerViewAdapterInterface
import com.sampietro.NaiveAAC.activities.Stories.Stories
import com.sampietro.NaiveAAC.activities.Stories.StoriesComparator
import com.sampietro.NaiveAAC.activities.Stories.StoriesHelper.renumberAPhraseOfAStory
import com.sampietro.NaiveAAC.activities.Stories.StoriesHelper.renumberAStory
import com.sampietro.NaiveAAC.activities.Stories.VoiceToBeRecordedInStories
import com.sampietro.NaiveAAC.activities.Stories.VoiceToBeRecordedInStoriesViewModel
import io.realm.Realm
import java.io.ByteArrayOutputStream
import java.net.URISyntaxException
import java.util.*

/**
 * <h1>SettingsStoriesWordActivity</h1>
 *
 * **SettingsStoriesWordActivity** app settings.
 * Refer to [developer.android.com](https://developer.android.com/guide/fragments/communicate)
 *
 * @version     5.0, 01/04/2024
 */
class SettingsStoriesWordActivity : ActivityAbstractClass(),
    FragmentAbstractClassWithListener.onBaseFragmentEventListenerSettings,
    ImageSearchArasaacRecyclerViewAdapterInterface,
    StoriesVideosSearchAdapterInterface, StoriesSoundsSearchAdapterInterface {
    //
    var fragmentManager: FragmentManager? = null
    //
    private var voiceToBeRecordedInStories: VoiceToBeRecordedInStories? = null
    private lateinit var viewModel: VoiceToBeRecordedInStoriesViewModel
    //
    var updateMode:String? = null
    var theNewWordsMustBeInsertedBeforeThese: String? = ""
    /**
     * configurations of settings start screen.
     *
     * @param savedInstanceState Define potentially saved parameters due to configurations changes.
     * @see .setActivityResultLauncher
     *
     * @see ActionbarFragment
     *
     * @see Activity.onCreate
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        //
        context = this
        // The MainActivity class provides an instance of Realm wherever needed in the application.
        // To use it in the Activity, a reference must be obtained to a Realm object in the onCreate method.
        // The Realm object must be closed in the onDestroy method.
        realm = Realm.getDefaultInstance()
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
        if (savedInstanceState == null) {
            val extras = intent.extras
            if (extras != null) {
                voiceToBeRecordedInStories!!.story = extras.getString(getString(R.string.story_to_display))
                voiceToBeRecordedInStories!!.phraseNumberInt = extras.getInt(getString(R.string.phrase_to_display))
                voiceToBeRecordedInStories!!.wordNumberInt = extras.getInt(getString(R.string.word_to_display))
                updateMode = extras.getString(getString(R.string.update_mode))
                if (updateMode == getString(R.string.modify))
                {
                    val daModificare = realm.where(Stories::class.java)
                        .equalTo(
                            getString(R.string.story),
                            voiceToBeRecordedInStories!!.story
                        )
                        .equalTo(getString(R.string.phrasenumberint), voiceToBeRecordedInStories!!.phraseNumberInt)
                        .equalTo(getString(R.string.wordnumberint), voiceToBeRecordedInStories!!.wordNumberInt)
                        .findAll()
                    val daModificareSize = daModificare.size
                    if (daModificareSize > 0) {
                        voiceToBeRecordedInStories!!.word = daModificare[0]!!.word
                        voiceToBeRecordedInStories!!.uriType = daModificare[0]!!.uriType
                        voiceToBeRecordedInStories!!.uri = daModificare[0]!!.uri
                        voiceToBeRecordedInStories!!.answerActionType = daModificare[0]!!.answerActionType
                        voiceToBeRecordedInStories!!.answerAction = daModificare[0]!!.answerAction
                        voiceToBeRecordedInStories!!.video = daModificare[0]!!.video
                        voiceToBeRecordedInStories!!.sound = daModificare[0]!!.sound
                        voiceToBeRecordedInStories!!.soundReplacesTTS = daModificare[0]!!.soundReplacesTTS
                        voiceToBeRecordedInStories!!.fromAssets = daModificare[0]!!.fromAssets
                        theNewWordsMustBeInsertedBeforeThese = voiceToBeRecordedInStories!!.word
                    }
                }
                else
                {
                    val daInserirePrimaDi = realm.where(Stories::class.java)
                        .equalTo(
                            getString(R.string.story),
                            voiceToBeRecordedInStories!!.story
                        )
                        .equalTo(getString(R.string.phrasenumberint), voiceToBeRecordedInStories!!.phraseNumberInt)
                        .equalTo(getString(R.string.wordnumberint), voiceToBeRecordedInStories!!.wordNumberInt)
                        .findAll()
                    val daInserirePrimaDiSize = daInserirePrimaDi.size
                    if (daInserirePrimaDiSize > 0) {
                        theNewWordsMustBeInsertedBeforeThese = daInserirePrimaDi[0]!!.word
                    }
                }
            }
        }
        //
        setActivityResultLauncher()
        //
        if (savedInstanceState == null) {
            fragmentManager = supportFragmentManager
            fragmentManager!!.beginTransaction()
                .add(ActionbarFragment(), getString(R.string.actionbar_fragment))
                .add(R.id.settings_container, StoriesFragment(R.layout.activity_settings_stories_word), "StoriesFragment")
                .commit()
        }
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
     * @see copyFileFromSharedToInternalStorageAndGetPath
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
                            val frag = StoriesFragment(R.layout.activity_settings_stories_word)
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
     * Called when the user taps the go back button.
     * return to SettingsStoriesImprovementActivity
     * @param v view of tapped button
     * @see SettingsStoriesImprovementActivity
     */
    fun onClickGoBackFromWord(v: View?) {
        intent = Intent(
            this,
            SettingsStoriesImprovementActivity::class.java
        )
        intent.putExtra(getString(R.string.story), voiceToBeRecordedInStories!!.story)
        intent.putExtra(getString(R.string.phrase_number), voiceToBeRecordedInStories!!.phraseNumberInt)
        startActivity(intent)
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
            R.id.buttonimagesearchstories -> {
                /*  the instructions of the button */
                val textWord1 = findViewById<View>(R.id.keywordstorytoadd) as TextView
                val textWord2 = findViewById<View>(R.id.phrasenumbertoadd) as TextView
                val textWord3 = findViewById<View>(R.id.wordnumbertoadd) as TextView
                val textWord4 = findViewById<View>(R.id.wordtoadd) as EditText
                // Viewmodel
                // In the activity, sometimes it is called observe, other times it is limited to performing set directly
                // (maybe it is not necessary to call observe)
                voiceToBeRecordedInStories!!.story =
                    textWord1.text.toString().lowercase(Locale.getDefault())
                if (textWord2.text.toString() != "") voiceToBeRecordedInStories!!.phraseNumberInt =
                    textWord2.text.toString().toInt()
                if (textWord3.text.toString() != "") voiceToBeRecordedInStories!!.wordNumberInt =
                    textWord3.text.toString().toInt()
                voiceToBeRecordedInStories!!.word = textWord4.text.toString()
                //
                imageSearchStoriesActivityResultLauncher!!.launch(intent)
            }
        }
    }
    /**
     * Called when the user taps the video search button.
     *
     * @param v view of tapped button
     */
    fun videoSearch(v: View?) {
        val textWord1 = findViewById<View>(R.id.keywordstorytoadd) as TextView
        val textWord2 = findViewById<View>(R.id.phrasenumbertoadd) as TextView
        val textWord3 = findViewById<View>(R.id.wordnumbertoadd) as TextView
        val textWord4 = findViewById<View>(R.id.wordtoadd) as EditText
        // Viewmodel
        // In the activity, sometimes it is called observe, other times it is limited to performing set directly
        // (maybe it is not necessary to call observe)
        voiceToBeRecordedInStories!!.story =
            textWord1.text.toString().lowercase(Locale.getDefault())
        if (textWord2.text.toString() != "") voiceToBeRecordedInStories!!.phraseNumberInt =
            textWord2.text.toString().toInt()
        if (textWord3.text.toString() != "") voiceToBeRecordedInStories!!.wordNumberInt =
            textWord3.text.toString().toInt()
        voiceToBeRecordedInStories!!.word = textWord4.text.toString()
        //
        val frag = StoriesVideosSearchFragment(R.layout.activity_settings_stories_images_videos)
        //
        val ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.settings_container, frag)
        ft.addToBackStack(null)
        ft.commit()
    }
    //
    /**
     * Called when the user taps the sounds search button.
     *
     * @param v view of tapped button
     */
    fun soundsSearch(v: View?) {
        val textWord1 = findViewById<View>(R.id.keywordstorytoadd) as TextView
        val textWord2 = findViewById<View>(R.id.phrasenumbertoadd) as TextView
        val textWord3 = findViewById<View>(R.id.wordnumbertoadd) as TextView
        val textWord4 = findViewById<View>(R.id.wordtoadd) as EditText
        // Viewmodel
        // In the activity, sometimes it is called observe, other times it is limited to performing set directly
        // (maybe it is not necessary to call observe)
        voiceToBeRecordedInStories!!.story =
            textWord1.text.toString().lowercase(Locale.getDefault())
        if (textWord2.text.toString() != "") voiceToBeRecordedInStories!!.phraseNumberInt =
            textWord2.text.toString().toInt()
        if (textWord3.text.toString() != "") voiceToBeRecordedInStories!!.wordNumberInt =
            textWord3.text.toString().toInt()
        voiceToBeRecordedInStories!!.word = textWord4.text.toString()
        //
        val frag = StoriesSoundsSearchFragment(R.layout.activity_settings_stories_sounds)
        //
        val ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.settings_container, frag)
        ft.addToBackStack(null)
        ft.commit()
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
     * Called when the user taps the image search Arasaac button from Stories Fragment.
     *
     * @param v view of tapped button
     */
    fun imageSearchArasaac(v: View?) {
        val textWord1 = findViewById<View>(R.id.keywordstorytoadd) as TextView
        val textWord2 = findViewById<View>(R.id.phrasenumbertoadd) as TextView
        val textWord3 = findViewById<View>(R.id.wordnumbertoadd) as TextView
        val textWord4 = findViewById<View>(R.id.wordtoadd) as EditText
        //
        // view the image search Arasaac  initializing ImageSearchArasaacFragment (FragmentTransaction
        // switch between Fragments).
        // Viewmodel
        // In the activity, sometimes it is called observe, other times it is limited to performing set directly
        // (maybe it is not necessary to call observe)
        voiceToBeRecordedInStories!!.story =
            textWord1.text.toString().lowercase(Locale.getDefault())
        if (textWord2.text.toString() != "") voiceToBeRecordedInStories!!.phraseNumberInt =
            textWord2.text.toString().toInt()
        if (textWord3.text.toString() != "") voiceToBeRecordedInStories!!.wordNumberInt =
            textWord3.text.toString().toInt()
        voiceToBeRecordedInStories!!.word = textWord4.text.toString()
        //
        val frag = ImageSearchArasaacFragment(R.layout.activity_settings_stories_arasaac)
        //
        val bundle = Bundle()
        bundle.putString("keywordToSearchArasaac", textWord4.text.toString())
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
        val frag = ImageSearchArasaacFragment(R.layout.activity_settings_stories_arasaac)
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
                val frag = StoriesFragment(R.layout.activity_settings_stories_word)
                //
                val ft = supportFragmentManager.beginTransaction()
                ft.replace(R.id.settings_container, frag)
                ft.addToBackStack(null)
                ft.commit()
            }
    }

    /**
     * Called when the user taps the video .
     *
     * @param videoKey string whit video key in the videos table
     */
    override fun reloadStoriesFragmentFromVideoSearch(videoKey: String?) {
        // Viewmodel
        // In the activity, sometimes it is called observe, other times it is limited to performing set directly
        // (maybe it is not necessary to call observe)
        viewModel.getSelectedItem()
            .observe(this) { voiceToBeRecordedInStories: VoiceToBeRecordedInStories ->
                voiceToBeRecordedInStories.video = videoKey
                //
                val frag = StoriesFragment(R.layout.activity_settings_stories_word)
                //
                val ft = supportFragmentManager.beginTransaction()
                ft.replace(R.id.settings_container, frag)
                ft.addToBackStack(null)
                ft.commit()
            }
    }

    /**
     * Called when the user click the radio button sound_replace_TTS.
     *
     * register which radio button was clicked
     *
     * @param view view of clicked radio button
     * @see StoriesSoundsSearchFragment
     */
    fun onStoriesSoundsRadioButtonClicked(view: View) {
        // Is the button now checked?
        val checked = (view as RadioButton).isChecked
        when (view.getId()) {
            R.id.radio_sound_replace_TTS -> if (checked) {
                //
                voiceToBeRecordedInStories!!.soundReplacesTTS = "Y"
            }
            R.id.radio_sound_does_not_replace_TTS -> if (checked) {
                //
                voiceToBeRecordedInStories!!.soundReplacesTTS = "N" // default
            }
        }
    }

    /**
     * Called when the user taps the sound.
     *
     * @param position int whit position of sound key in the sounds table
     */
    override fun reloadStoriesFragmentFromSoundsSearch(position: Int) {
        val results = realm.where(Sounds::class.java).findAll()
        val selectedSound = results[position]!!
        voiceToBeRecordedInStories!!.sound = selectedSound.descrizione
        //
        val frag = StoriesFragment(R.layout.activity_settings_stories_word)
        //
        val ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.settings_container, frag)
        ft.addToBackStack(null)
        ft.commit()
    }
    //
    /**
     * Called when the user taps the action after response button.
     *
     * @param v view of tapped button
     */
    fun actionAfterResponse(v: View?) {
        val textWord1 = findViewById<View>(R.id.keywordstorytoadd) as TextView
        val textWord2 = findViewById<View>(R.id.phrasenumbertoadd) as TextView
        val textWord3 = findViewById<View>(R.id.wordnumbertoadd) as TextView
        val textWord4 = findViewById<View>(R.id.wordtoadd) as EditText
        // Viewmodel
        // In the activity, sometimes it is called observe, other times it is limited to performing set directly
        // (maybe it is not necessary to call observe)
        voiceToBeRecordedInStories!!.story =
            textWord1.text.toString().lowercase(Locale.getDefault())
        if (textWord2.text.toString() != "") voiceToBeRecordedInStories!!.phraseNumberInt =
            textWord2.text.toString().toInt()
        if (textWord3.text.toString() != "") voiceToBeRecordedInStories!!.wordNumberInt =
            textWord3.text.toString().toInt()
        voiceToBeRecordedInStories!!.word = textWord4.text.toString()
        //
        val frag = StoriesActionAfterResponseFragment(R.layout.activity_settings_stories_action_after_response)
        //
        val ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.settings_container, frag)
        ft.addToBackStack(null)
        ft.commit()
    }

    /**
     * Called when the user taps the video.
     *
     * @param videoKey string whit video key in the videos table
     */
    override fun reloadStoriesFragmentFromActionAfterResponse(videoKey: String?) {
        // Viewmodel
        // In the activity, sometimes it is called observe, other times it is limited to performing set directly
        // (maybe it is not necessary to call observe)
        viewModel.getSelectedItem()
            .observe(this) { voiceToBeRecordedInStories: VoiceToBeRecordedInStories ->
                voiceToBeRecordedInStories.answerActionType = "V"
                voiceToBeRecordedInStories.answerAction = videoKey
                //
                val frag = StoriesFragment(R.layout.activity_settings_stories_word)
                //
                val ft = supportFragmentManager.beginTransaction()
                ft.replace(R.id.settings_container, frag)
                ft.addToBackStack(null)
                ft.commit()
            }
    }
    //
    /**
     * Called when the user taps the save link video youtube button.
     *
     * @param v view of tapped button
     */
    fun actionAfterResponseVideoYoutubeToAdd(v: View?) {
        // Viewmodel
        // In the activity, sometimes it is called observe, other times it is limited to performing set directly
        // (maybe it is not necessary to call observe)
        viewModel.getSelectedItem()
            .observe(this) { voiceToBeRecordedInStories: VoiceToBeRecordedInStories ->
                val textWord1 = findViewById<View>(R.id.link_video_youtube) as EditText
                if (textWord1.text.toString() != "") {
                    voiceToBeRecordedInStories.answerActionType = "Y"
                    voiceToBeRecordedInStories.answerAction = textWord1.text.toString()
                    //
                    val frag = StoriesFragment(R.layout.activity_settings_stories_word)
                    //
                    val ft = supportFragmentManager.beginTransaction()
                    ft.replace(R.id.settings_container, frag)
                    ft.addToBackStack(null)
                    ft.commit()
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
                voiceToBeRecordedInStories.phraseNumberInt = 0
                //
                realm = Realm.getDefaultInstance()
                //
                val textWord1 = findViewById<View>(R.id.keywordstorytoadd) as TextView
                val textWord2 = findViewById<View>(R.id.phrasenumbertoadd) as TextView
                val textWord3 = findViewById<View>(R.id.wordnumbertoadd) as TextView
                val textWord4 = findViewById<View>(R.id.wordtoadd) as EditText
                if (textWord1.text.toString() != "" && textWord2.text.toString() != ""
                ) //                  && (voiceToBeRecordedInStories.getUriType() != null) && (voiceToBeRecordedInStories.getUri() != null))
                {
                    if (textWord3.text.toString() == "0"
                        || (textWord3.text.toString() != "0"
                                && voiceToBeRecordedInStories.uriType != null && voiceToBeRecordedInStories.uri != null)
                    ) {
                        // cancello frase vecchia
                        // se textWord3 non è vuoto
                        // copio la frase vecchia fino a textWord3
                        // inserisco la nuova parola
                        // copio il resto della frase vecchia e riordino
                        // se textWord3 è vuoto
                        // copio la frase vecchia
                        // inserisco la nuova parola alla fine e riordino
                        //
                        // clear the table
                        // cancello frase vecchia dopo averne salvato copia
                        val resultsStories = realm.where(Stories::class.java)
                            .equalTo(
                                getString(R.string.story),
                                textWord1.text.toString().lowercase(Locale.getDefault())
                            )
                            .equalTo(getString(R.string.phrasenumberint), textWord2.text.toString().toInt())
                            .findAll()
                        val storiesSize = resultsStories.size
                        //
                        val resultsStoriesList = realm.copyFromRealm(resultsStories)
                        //
                        Collections.sort(resultsStoriesList, StoriesComparator())
                        //
                        realm.beginTransaction()
                        resultsStories.deleteAllFromRealm()
                        realm.commitTransaction()
                        //
                        var currentWordNumber = 0
                        if (textWord3.text.toString() != "") {
                            // copio la frase vecchia fino a textWord3
                            var irrh = 0
                            while (irrh < storiesSize) {
                                val resultStories = resultsStoriesList[irrh]!!
                                currentWordNumber = resultStories.wordNumberInt
                                if (currentWordNumber < textWord3.text.toString().toInt()) {
                                    realm.beginTransaction()
                                    realm.copyToRealm(resultStories)
                                    realm.commitTransaction()
                                } else {
                                    break
                                }
                                irrh++
                            }
                            if (updateMode == getString(R.string.modify))
                            {
                                irrh++
                            }
                            // inserisco la nuova parola
                            // registro nuova parola
                            // Note that the realm object was generated with the createObject method
                            // and not with the new operator.
                            // The modification operations will be performed within a Transaction.
                            //
                            realm.beginTransaction()
                            val stories = realm.createObject(Stories::class.java)
                            // set the fields here
                            stories.story = textWord1.text.toString().lowercase(Locale.getDefault())
                            stories.phraseNumberInt = textWord2.text.toString().toInt()
                            stories.wordNumberInt = textWord3.text.toString().toInt()
                            stories.word = textWord4.text.toString()
                            stories.uriType = voiceToBeRecordedInStories.uriType
                            stories.uri = voiceToBeRecordedInStories.uri
                            stories.answerActionType = voiceToBeRecordedInStories.answerActionType
                            stories.answerAction = voiceToBeRecordedInStories.answerAction
                            stories.video = voiceToBeRecordedInStories.video
                            stories.sound = voiceToBeRecordedInStories.sound
                            stories.soundReplacesTTS = voiceToBeRecordedInStories.soundReplacesTTS
                            stories.fromAssets = ""
                            realm.commitTransaction()
                            // copio la frase vecchia
                            while (irrh < storiesSize) {
                                val resultStories = resultsStoriesList[irrh]!!
                                realm.beginTransaction()
                                realm.copyToRealm(resultStories)
                                realm.commitTransaction()
                                irrh++
                            }
                        } else {
                            // copio la frase vecchia
                            var irrh = 0
                            while (irrh < storiesSize) {
                                val resultStories = resultsStoriesList[irrh]!!
                                currentWordNumber = resultStories.wordNumberInt
                                //
                                realm.beginTransaction()
                                realm.copyToRealm(resultStories)
                                realm.commitTransaction()
                                //
                                irrh++
                            }
                            // inserisco la nuova parola
                            // registro nuova parola
                            // Note that the realm object was generated with the createObject method
                            // and not with the new operator.
                            // The modification operations will be performed within a Transaction.
                            //
                            realm.beginTransaction()
                            val stories = realm.createObject(Stories::class.java)
                            // set the fields here
                            stories.story = textWord1.text.toString().lowercase(Locale.getDefault())
                            stories.phraseNumberInt = textWord2.text.toString().toInt()
                            stories.wordNumberInt = ++currentWordNumber
                            stories.word = textWord4.text.toString()
                            stories.uriType = voiceToBeRecordedInStories.uriType
                            stories.uri = voiceToBeRecordedInStories.uri
                            stories.answerActionType = voiceToBeRecordedInStories.answerActionType
                            stories.answerAction = voiceToBeRecordedInStories.answerAction
                            stories.video = voiceToBeRecordedInStories.video
                            stories.sound = voiceToBeRecordedInStories.sound
                            stories.soundReplacesTTS = voiceToBeRecordedInStories.soundReplacesTTS
                            stories.fromAssets = ""
                            realm.commitTransaction()
                        }
                        // riordino
                        renumberAPhraseOfAStory(
                            realm,
                            textWord1.text.toString().lowercase(Locale.getDefault()),
                            textWord2.text.toString().toInt()
                        )
                        renumberAStory(
                            realm,
                            textWord1.text.toString().lowercase(Locale.getDefault())
                        )
                        //
                        val value0 = 0
                        val daModificare =
                            realm.where(Stories::class.java)
                                .equalTo(getString(R.string.story), textWord1.text.toString().lowercase(Locale.getDefault()))
                                .equalTo(getString(R.string.phrasenumberint), textWord2.text.toString().toInt())
                                .equalTo(getString(R.string.wordnumberint), value0)
                                .findFirst()
                        if(daModificare != null) {
                            val wordsToChange = daModificare.word
                            val wordsToBeInserted = textWord4.text.toString()
                            if (theNewWordsMustBeInsertedBeforeThese!! == "")
                            {
                                // the new words are at the end of the sentence
                                realm.beginTransaction()
                                daModificare.word = "$wordsToChange $wordsToBeInserted"
                                realm.insertOrUpdate(daModificare)
                                realm.commitTransaction()
                            }
                            else
                            {
                                val beforeTheWordsToBeInserted = wordsToChange!!.substringBefore(
                                    theNewWordsMustBeInsertedBeforeThese!!
                                )

                                val afterTheWordsToBeInserted =  wordsToChange.substringAfter(
                                    theNewWordsMustBeInsertedBeforeThese!!
                                )
                                realm.beginTransaction()
                                if (updateMode == getString(R.string.modify))
                                {
                                    daModificare.word = "$beforeTheWordsToBeInserted $wordsToBeInserted $afterTheWordsToBeInserted"
                                }
                                else
                                {
                                    daModificare.word = "$beforeTheWordsToBeInserted $wordsToBeInserted $theNewWordsMustBeInsertedBeforeThese $afterTheWordsToBeInserted"
                                }
                                realm.insertOrUpdate(daModificare)
                                realm.commitTransaction()
                            }
                        }
                        // clear fields of viewmodel data class
                        voiceToBeRecordedInStories.story =
                            textWord1.text.toString().lowercase(Locale.getDefault())
                        voiceToBeRecordedInStories.phraseNumberInt =
                            textWord2.text.toString().toInt()
                        clearFieldsOfViewmodelDataClass()
                    }
                    //
                    val gameUseVideoAndSound = "N"
                    val intent: Intent?
                    intent = Intent(
                        this,
                        SettingsStoriesImprovementActivity::class.java
                    )
                    intent.putExtra(getString(R.string.story), textWord1.text.toString().lowercase(Locale.getDefault()))
                    intent.putExtra(getString(R.string.phrase_number), textWord2.text.toString().toInt())
                    startActivity(intent)
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
}