package com.sampietro.NaiveAAC.activities.Settings

import android.app.Activity
import android.content.Intent
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
import com.sampietro.NaiveAAC.R
import com.sampietro.NaiveAAC.activities.BaseAndAbstractClass.ActivityAbstractClass
import com.sampietro.NaiveAAC.activities.BaseAndAbstractClass.FragmentAbstractClassWithListenerWithoutConstructor
import com.sampietro.NaiveAAC.activities.DataStorage.DataStorageHelper.copyFileFromSharedToInternalStorageAndGetPath
import com.sampietro.NaiveAAC.activities.Game.Game2.Game2ViewModelItem
import com.sampietro.NaiveAAC.activities.Game.Game2.SettingsStoriesRegistrationFragment
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
import io.realm.Realm
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
    FragmentAbstractClassWithListenerWithoutConstructor.onBaseFragmentEventListenerSettings,
    ImageSearchArasaacRecyclerViewAdapterInterface,
    StoriesVideosSearchAdapterInterface, StoriesSoundsSearchAdapterInterface {
    //
    var fragmentManager: FragmentManager? = null
    //
//    private var voiceToBeRecordedInStories: VoiceToBeRecordedInStories? = null
//    private lateinit var viewModel: Game2ViewModel
    private var game2ViewModelItem: Game2ViewModelItem? = null
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
        //
        setActivityResultLauncher()
        /*
        Both your fragment and its host activity can retrieve a shared instance of a ViewModel with activity scope by passing the activity into the ViewModelProvider
        constructor.
        The ViewModelProvider handles instantiating the ViewModel or retrieving it if it already exists. Both components can observe and modify this data
         */
        // In the Activity#onCreate make the only setItem
//        voiceToBeRecordedInStories = VoiceToBeRecordedInStories()
        game2ViewModelItem = Game2ViewModelItem()
//        viewModel = ViewModelProvider(this).get(
//            Game2ViewModel::class.java
//        )
//        viewModel.setItem(game2ViewModelItem!!)
        game2ViewModelItem!!.story = getString(R.string.nome_storia)
        game2ViewModelItem!!.phraseNumberInt = 0
        clearFieldsOfViewmodelDataClass()
        //
        if (savedInstanceState != null) {
            // The onSaveInstanceState method is called before an activity may be killed
            // (for Screen Rotation Handling) so that
            // when it comes back it can restore its state.
            filePath = savedInstanceState.getString("IMAGE VIDEO OR SOUND FILE PATH", getString(R.string.non_trovato))
            //
            game2ViewModelItem!!.story = savedInstanceState.getString("STORY", "")
            game2ViewModelItem!!.phraseNumberInt = savedInstanceState.getInt("PHRASE NUMBER", 0)
            game2ViewModelItem!!.wordNumberInt = savedInstanceState.getInt("WORD NUMBER", 0)
            game2ViewModelItem!!.word = savedInstanceState.getString("WORD", "")
            game2ViewModelItem!!.uriType = savedInstanceState.getString("URI TYPE", "")
            game2ViewModelItem!!.uri = savedInstanceState.getString("URI", "")
            game2ViewModelItem!!.answerActionType = savedInstanceState.getString("ANSWER ACTION TYPE", "")
            game2ViewModelItem!!.answerAction = savedInstanceState.getString("ANSWER ACTION", "")
            game2ViewModelItem!!.video = savedInstanceState.getString("VIDEO", "")
            game2ViewModelItem!!.sound = savedInstanceState.getString("SOUND", "")
            game2ViewModelItem!!.soundReplacesTTS = savedInstanceState.getString("SOUND REPLACE TTS", "")
            //
            updateMode = savedInstanceState.getString("UPDATE MODE", "")
            theNewWordsMustBeInsertedBeforeThese = savedInstanceState.getString("THE NEW WORDS MUST BE INSERTED BEFORE THESE", "")
        }
        else
        {
            val extras = intent.extras
            if (extras != null) {
                game2ViewModelItem!!.story = extras.getString(getString(R.string.story_to_display))
                game2ViewModelItem!!.phraseNumberInt = extras.getInt(getString(R.string.phrase_to_display))
                game2ViewModelItem!!.wordNumberInt = extras.getInt(getString(R.string.word_to_display))
                updateMode = extras.getString(getString(R.string.update_mode))
                if (updateMode == getString(R.string.modify))
                {
                    val daModificare = realm.where(Stories::class.java)
                        .equalTo(
                            getString(R.string.story),
                            game2ViewModelItem!!.story
                        )
                        .equalTo(getString(R.string.phrasenumberint), game2ViewModelItem!!.phraseNumberInt)
                        .equalTo(getString(R.string.wordnumberint), game2ViewModelItem!!.wordNumberInt)
                        .findAll()
                    val daModificareSize = daModificare.size
                    if (daModificareSize > 0) {
                        game2ViewModelItem!!.word = daModificare[0]!!.word
                        game2ViewModelItem!!.uriType = daModificare[0]!!.uriType
                        game2ViewModelItem!!.uri = daModificare[0]!!.uri
                        game2ViewModelItem!!.answerActionType = daModificare[0]!!.answerActionType
                        game2ViewModelItem!!.answerAction = daModificare[0]!!.answerAction
                        game2ViewModelItem!!.video = daModificare[0]!!.video
                        game2ViewModelItem!!.sound = daModificare[0]!!.sound
                        game2ViewModelItem!!.soundReplacesTTS = daModificare[0]!!.soundReplacesTTS
                        game2ViewModelItem!!.fromAssets = daModificare[0]!!.fromAssets
                        theNewWordsMustBeInsertedBeforeThese = game2ViewModelItem!!.word
                    }
                }
                else
                {
                    val daInserirePrimaDi = realm.where(Stories::class.java)
                        .equalTo(
                            getString(R.string.story),
                            game2ViewModelItem!!.story
                        )
                        .equalTo(getString(R.string.phrasenumberint), game2ViewModelItem!!.phraseNumberInt)
                        .equalTo(getString(R.string.wordnumberint), game2ViewModelItem!!.wordNumberInt)
                        .findAll()
                    val daInserirePrimaDiSize = daInserirePrimaDi.size
                    if (daInserirePrimaDiSize > 0) {
                        theNewWordsMustBeInsertedBeforeThese = daInserirePrimaDi[0]!!.word
                    }
                }
                fragmentManager = supportFragmentManager
                fragmentManager!!.beginTransaction()
                    .add(ActionbarFragment(), getString(R.string.actionbar_fragment))
//                    .add(R.id.settings_container, StoriesFragment(), "StoriesFragment")
                    .commit()
            }
            fragmentTransactionStart("add")
        }
    }
    /**
     * This method is called before an activity may be killed so that when it comes back some time in the future it can restore its state..
     *
     *
     * it stores the image video or sound file path
     *
     * @param savedInstanceState Define potentially saved parameters due to configurations changes.
     */
    public override fun onSaveInstanceState(savedInstanceState: Bundle) {
        savedInstanceState.putString("IMAGE VIDEO OR SOUND FILE PATH", filePath)
        //
        savedInstanceState.putString("STORY", game2ViewModelItem!!.story)
        savedInstanceState.putInt("PHRASE NUMBER", game2ViewModelItem!!.phraseNumberInt)
        savedInstanceState.putInt("WORD NUMBER", game2ViewModelItem!!.wordNumberInt)
        savedInstanceState.putString("WORD", game2ViewModelItem!!.word)
        savedInstanceState.putString("URI TYPE", game2ViewModelItem!!.uriType)
        savedInstanceState.putString("URI", game2ViewModelItem!!.uri)
        savedInstanceState.putString("ANSWER ACTION TYPE", game2ViewModelItem!!.answerActionType)
        savedInstanceState.putString("ANSWER ACTION", game2ViewModelItem!!.answerAction)
        savedInstanceState.putString("VIDEO", game2ViewModelItem!!.video)
        savedInstanceState.putString("SOUND", game2ViewModelItem!!.sound)
        savedInstanceState.putString("SOUND REPLACE TTS", game2ViewModelItem!!.soundReplacesTTS)
        //
        savedInstanceState.putString("UPDATE MODE", updateMode)
        savedInstanceState.putString("THE NEW WORDS MUST BE INSERTED BEFORE THESE", theNewWordsMustBeInsertedBeforeThese)
        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState)
    }
    /**
     * initiate Fragment transaction.
     *
     *
     *
     * @see SettingsStoriesRegistrationFragment
     */
    fun fragmentTransactionStart(fragmentMode: String) {
        val frag = StoriesFragment()
        val bundle = Bundle()
        bundle.putString("STORY", game2ViewModelItem!!.story)
        bundle.putInt("PHRASE NUMBER", game2ViewModelItem!!.phraseNumberInt)
        bundle.putInt("WORD NUMBER", game2ViewModelItem!!.wordNumberInt)
        bundle.putString("WORD", game2ViewModelItem!!.word)
        bundle.putString("URI TYPE", game2ViewModelItem!!.uriType)
        bundle.putString("URI", game2ViewModelItem!!.uri)
        frag.arguments = bundle
        val ft = supportFragmentManager.beginTransaction()
        if (fragmentMode != "add") {
            ft.replace(R.id.settings_container, frag, "StoriesFragment")
        } else {
            ft.add(R.id.settings_container, frag, "StoriesFragment")
        }
        ft.addToBackStack(null)
        ft.commitAllowingStateLoss()
    }
    //
    @JvmField
    var imageSearchStoriesActivityResultLauncher: ActivityResultLauncher<Intent>? = null
//    @JvmField
//    var uri: Uri? = null
    @JvmField
    var filePath: String? = null
//    lateinit var byteArray: ByteArray
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
                        var uri: Uri? = null
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
//                            var bitmap: Bitmap? = null
//                            try {
//                                val source = ImageDecoder.createSource(
//                                    context.contentResolver,
//                                    uri!!
//                                )
//                                bitmap = ImageDecoder.decodeBitmap(source) { decoder, _, _ ->
//                                    decoder.setTargetSampleSize(1) // shrinking by
//                                    decoder.isMutableRequired = true // this resolve the hardware type of bitmap problem
//                                }
//                            } catch (e: Exception) {
//                                e.printStackTrace()
//                            }
                            //
//                            val stream = ByteArrayOutputStream()
//                            bitmap!!.compress(Bitmap.CompressFormat.PNG, 100, stream)
//                            byteArray = stream.toByteArray()
                            //
//                                Viewmodel
//                                In the activity, sometimes it is called observe, other times it is limited to performing set directly
//                                (maybe it is not necessary to call observe)
                            game2ViewModelItem!!.uriType = "S"
                            game2ViewModelItem!!.uri = filePath
                            //
                            val wordtoadd = findViewById<View>(R.id.wordtoadd) as EditText
                            game2ViewModelItem!!.word = wordtoadd.text.toString()
                            //
                            fragmentTransactionStart("replace")
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
        intent.putExtra(getString(R.string.story), game2ViewModelItem!!.story)
        intent.putExtra(getString(R.string.phrase_number), game2ViewModelItem!!.phraseNumberInt)
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
                game2ViewModelItem!!.story =
                    textWord1.text.toString().lowercase(Locale.getDefault())
                if (textWord2.text.toString() != "") game2ViewModelItem!!.phraseNumberInt =
                    textWord2.text.toString().toInt()
                if (textWord3.text.toString() != "") game2ViewModelItem!!.wordNumberInt =
                    textWord3.text.toString().toInt()
                game2ViewModelItem!!.word = textWord4.text.toString()
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
        game2ViewModelItem!!.story =
            textWord1.text.toString().lowercase(Locale.getDefault())
        if (textWord2.text.toString() != "") game2ViewModelItem!!.phraseNumberInt =
            textWord2.text.toString().toInt()
        if (textWord3.text.toString() != "") game2ViewModelItem!!.wordNumberInt =
            textWord3.text.toString().toInt()
        game2ViewModelItem!!.word = textWord4.text.toString()
        //
        val frag = StoriesVideosSearchFragment()
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
        game2ViewModelItem!!.story =
            textWord1.text.toString().lowercase(Locale.getDefault())
        if (textWord2.text.toString() != "") game2ViewModelItem!!.phraseNumberInt =
            textWord2.text.toString().toInt()
        if (textWord3.text.toString() != "") game2ViewModelItem!!.wordNumberInt =
            textWord3.text.toString().toInt()
        game2ViewModelItem!!.word = textWord4.text.toString()
        //
        val frag = StoriesSoundsSearchFragment()
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
        game2ViewModelItem!!.story =
            textWord1.text.toString().lowercase(Locale.getDefault())
        if (textWord2.text.toString() != "") game2ViewModelItem!!.phraseNumberInt =
            textWord2.text.toString().toInt()
        if (textWord3.text.toString() != "") game2ViewModelItem!!.wordNumberInt =
            textWord3.text.toString().toInt()
        game2ViewModelItem!!.word = textWord4.text.toString()
        //
        val frag = ImageSearchArasaacFragment()
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
//        viewModel.getSelectedItem()
//            .observe(this) { game2ViewModelItem: Game2ViewModelItem ->
                // Perform an action with the latest item data
                game2ViewModelItem!!.uriType = "A"
                game2ViewModelItem!!.uri = url
                //
                fragmentTransactionStart("replace")
//                val frag = StoriesFragment()
                //
//                val ft = supportFragmentManager.beginTransaction()
//                ft.replace(R.id.settings_container, frag)
//                ft.addToBackStack(null)
//                ft.commit()
//            }
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
//        viewModel.getSelectedItem()
//            .observe(this) { game2ViewModelItem: Game2ViewModelItem ->
                game2ViewModelItem!!.video = videoKey
                //
                fragmentTransactionStart("replace")
//                val frag = StoriesFragment()
                //
//                val ft = supportFragmentManager.beginTransaction()
//                ft.replace(R.id.settings_container, frag)
//                ft.addToBackStack(null)
//                ft.commit()
//            }
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
                game2ViewModelItem!!.soundReplacesTTS = "Y"
            }
            R.id.radio_sound_does_not_replace_TTS -> if (checked) {
                //
                game2ViewModelItem!!.soundReplacesTTS = "N" // default
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
        game2ViewModelItem!!.sound = selectedSound.descrizione
        //
        fragmentTransactionStart("replace")
//        val frag = StoriesFragment()
        //
//        val ft = supportFragmentManager.beginTransaction()
//        ft.replace(R.id.settings_container, frag)
//        ft.addToBackStack(null)
//        ft.commit()
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
        game2ViewModelItem!!.story =
            textWord1.text.toString().lowercase(Locale.getDefault())
        if (textWord2.text.toString() != "") game2ViewModelItem!!.phraseNumberInt =
            textWord2.text.toString().toInt()
        if (textWord3.text.toString() != "") game2ViewModelItem!!.wordNumberInt =
            textWord3.text.toString().toInt()
        game2ViewModelItem!!.word = textWord4.text.toString()
        //
        val frag = StoriesActionAfterResponseFragment()
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
//        viewModel.getSelectedItem()
//            .observe(this) { game2ViewModelItem: Game2ViewModelItem ->
                game2ViewModelItem!!.answerActionType = "V"
                game2ViewModelItem!!.answerAction = videoKey
                //
                fragmentTransactionStart("replace")
//                val frag = StoriesFragment()
                //
//                val ft = supportFragmentManager.beginTransaction()
//                ft.replace(R.id.settings_container, frag)
//                ft.addToBackStack(null)
//                ft.commit()
//            }
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
//        viewModel.getSelectedItem()
//            .observe(this) { game2ViewModelItem: Game2ViewModelItem ->
                val textWord1 = findViewById<View>(R.id.link_video_youtube) as EditText
                if (textWord1.text.toString() != "") {
                    game2ViewModelItem!!.answerActionType = "Y"
                    game2ViewModelItem!!.answerAction = textWord1.text.toString()
                    //
                    fragmentTransactionStart("replace")
//                    val frag = StoriesFragment()
                    //
//                    val ft = supportFragmentManager.beginTransaction()
//                    ft.replace(R.id.settings_container, frag)
//                    ft.addToBackStack(null)
//                    ft.commit()
                }
//            }
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
//        viewModel.getSelectedItem()
//            .observe(this) { game2ViewModelItem: Game2ViewModelItem ->
                // Perform an action with the latest item data
                game2ViewModelItem!!.story = getString(R.string.nome_storia)
                game2ViewModelItem!!.phraseNumberInt = 0
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
                                && game2ViewModelItem!!.uriType != null && game2ViewModelItem!!.uri != null)
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
                            stories.uriType = game2ViewModelItem!!.uriType
                            stories.uri = game2ViewModelItem!!.uri
                            stories.answerActionType = game2ViewModelItem!!.answerActionType
                            stories.answerAction = game2ViewModelItem!!.answerAction
                            stories.video = game2ViewModelItem!!.video
                            stories.sound = game2ViewModelItem!!.sound
                            stories.soundReplacesTTS = game2ViewModelItem!!.soundReplacesTTS
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
                            stories.uriType = game2ViewModelItem!!.uriType
                            stories.uri = game2ViewModelItem!!.uri
                            stories.answerActionType = game2ViewModelItem!!.answerActionType
                            stories.answerAction = game2ViewModelItem!!.answerAction
                            stories.video = game2ViewModelItem!!.video
                            stories.sound = game2ViewModelItem!!.sound
                            stories.soundReplacesTTS = game2ViewModelItem!!.soundReplacesTTS
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
                        game2ViewModelItem!!.story =
                            textWord1.text.toString().lowercase(Locale.getDefault())
                        game2ViewModelItem!!.phraseNumberInt =
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
//            }
    }

    /**
     * clear fields of viewmodel data class
     *
     *
     * @see Game2ViewModelItem
     */
    fun clearFieldsOfViewmodelDataClass() {
        game2ViewModelItem!!.wordNumberInt = 0
        game2ViewModelItem!!.word = ""
        game2ViewModelItem!!.uriType = ""
        game2ViewModelItem!!.uri = ""
        game2ViewModelItem!!.answerActionType = ""
        game2ViewModelItem!!.answerAction = ""
        game2ViewModelItem!!.video = ""
        game2ViewModelItem!!.sound = ""
        game2ViewModelItem!!.soundReplacesTTS = ""
    }
}