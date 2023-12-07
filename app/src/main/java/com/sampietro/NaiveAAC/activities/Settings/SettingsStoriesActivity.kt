package com.sampietro.NaiveAAC.activities.Settings

import com.sampietro.NaiveAAC.activities.Stories.StoriesHelper.renumberAPhraseOfAStory
import com.sampietro.NaiveAAC.activities.Stories.StoriesHelper.renumberAStory
import com.sampietro.NaiveAAC.activities.Settings.Utils.AccountActivityAbstractClass
import com.sampietro.NaiveAAC.activities.Settings.Utils.SettingsFragmentAbstractClass.onFragmentEventListenerSettings
import com.sampietro.NaiveAAC.activities.Stories.StoriesAdapter.StoriesAdapterInterface
import com.sampietro.NaiveAAC.activities.Settings.StoriesVideosSearchAdapter.StoriesVideosSearchAdapterInterface
import com.sampietro.NaiveAAC.activities.Settings.StoriesSoundsSearchAdapter.StoriesSoundsSearchAdapterInterface
import android.widget.TextView
import com.sampietro.NaiveAAC.activities.Stories.VoiceToBeRecordedInStories
import com.sampietro.NaiveAAC.activities.Stories.VoiceToBeRecordedInStoriesViewModel
import android.os.Bundle
import com.sampietro.NaiveAAC.R
import androidx.lifecycle.ViewModelProvider
import com.sampietro.NaiveAAC.activities.Game.Utils.ActionbarFragment
import android.content.Intent
import androidx.activity.result.ActivityResultCallback
import android.app.Activity
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.view.View
import android.widget.EditText
import android.widget.RadioButton
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.FragmentManager
import io.realm.RealmResults
import com.sampietro.NaiveAAC.activities.Graphics.Sounds
import com.sampietro.NaiveAAC.activities.Stories.Stories
import com.sampietro.NaiveAAC.activities.Stories.StoriesComparator
import io.realm.Realm
import io.realm.Sort
import java.io.ByteArrayOutputStream
import java.lang.Exception
import java.net.URISyntaxException
import java.util.*

/**
 * <h1>SettingsStoriesActivity</h1>
 *
 * **SettingsStoriesActivity** app settings.
 * Refer to [developer.android.com](https://developer.android.com/guide/fragments/communicate)
 *
 * @version     4.0, 09/09/2023
 * @see com.sampietro.NaiveAAC.activities.Settings.Utils.SettingsFragmentAbstractClass
 *
 * @see com.sampietro.NaiveAAC.activities.WordPairs.WordPairsAdapter
 *
 * @see com.sampietro.NaiveAAC.activities.Grammar.ListsOfNamesAdapter
 *
 * @see com.sampietro.NaiveAAC.activities.Stories.StoriesAdapter
 */
class SettingsStoriesActivity : AccountActivityAbstractClass(), onFragmentEventListenerSettings,
    StoriesAdapterInterface, StoriesImageSearchArasaacRecyclerViewAdapterInterface,
    StoriesVideosSearchAdapterInterface, StoriesSoundsSearchAdapterInterface {
    var message = "messaggio non formato"
    var textView: TextView? = null

    //
    var fragmentManager: FragmentManager? = null

    //
    private var voiceToBeRecordedInStories: VoiceToBeRecordedInStories? = null
    private lateinit var viewModel: VoiceToBeRecordedInStoriesViewModel

    /**
     * configurations of settings start screen.
     *
     * @param savedInstanceState Define potentially saved parameters due to configurations changes.
     * @see .setActivityResultLauncher
     *
     * @see ActionbarFragment
     *
     * @see ContentsFragment
     *
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
                .add(R.id.settings_container, StoriesFragment(), "StoriesFragment")
                .commit()
        }
        // The MainActivity class provides an instance of Realm wherever needed in the application.
        // To use it in the Activity, a reference must be obtained to a Realm object in the onCreate method.
        // The Realm object must be closed in the onDestroy method.
        realm = Realm.getDefaultInstance()
    }
    //
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
     * @see AccountActivityAbstractClass.getFilePath
     */
    override fun setActivityResultLauncher() {
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
                                filePath = getFilePath(context, uri)
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
//                            try {
//                                bitmap =
//                                    MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
//                            } catch (e: IOException) {
//                                e.printStackTrace()
//                            }
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
    override fun imageSearch(v: View) {
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
                val textWord1 = findViewById<View>(R.id.keywordstorytoadd) as EditText
                val textWord2 = findViewById<View>(R.id.phrasenumbertoadd) as EditText
                val textWord3 = findViewById<View>(R.id.wordnumbertoadd) as EditText
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
    //
    /**
     * Called when the user taps the video search button.
     *
     * @param v view of tapped button
     */
    fun videoSearch(v: View?) {
        val textWord1 = findViewById<View>(R.id.keywordstorytoadd) as EditText
        val textWord2 = findViewById<View>(R.id.phrasenumbertoadd) as EditText
        val textWord3 = findViewById<View>(R.id.wordnumbertoadd) as EditText
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
        val textWord1 = findViewById<View>(R.id.keywordstorytoadd) as EditText
        val textWord2 = findViewById<View>(R.id.phrasenumbertoadd) as EditText
        val textWord3 = findViewById<View>(R.id.wordnumbertoadd) as EditText
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
     * @see com.sampietro.NaiveAAC.activities.Settings.Utils.SettingsFragmentAbstractClass
     */
    override fun receiveResultSettings(v: View?) {
        rootViewFragment = v
    }

    /**
     * Called when the user taps the image search Arasaac button from Stories Fragment.
     *
     * @param v view of tapped button
     */
    fun imageSearchArasaac(v: View?) {
        val textWord1 = findViewById<View>(R.id.keywordstorytoadd) as EditText
        val textWord2 = findViewById<View>(R.id.phrasenumbertoadd) as EditText
        val textWord3 = findViewById<View>(R.id.wordnumbertoadd) as EditText
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
        val frag = StoriesImageSearchArasaacFragment()
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
        val frag = StoriesImageSearchArasaacFragment()
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
                val frag = StoriesFragment()
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
                val frag = StoriesFragment()
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
        val frag = StoriesFragment()
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
        val textWord1 = findViewById<View>(R.id.keywordstorytoadd) as EditText
        val textWord2 = findViewById<View>(R.id.phrasenumbertoadd) as EditText
        val textWord3 = findViewById<View>(R.id.wordnumbertoadd) as EditText
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
        viewModel.getSelectedItem()
            .observe(this) { voiceToBeRecordedInStories: VoiceToBeRecordedInStories ->
                voiceToBeRecordedInStories.answerActionType = "V"
                voiceToBeRecordedInStories.answerAction = videoKey
                //
                val frag = StoriesFragment()
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
                    val frag = StoriesFragment()
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
                val textWord1 = findViewById<View>(R.id.keywordstorytoadd) as EditText
                val textWord2 = findViewById<View>(R.id.phrasenumbertoadd) as EditText
                val textWord3 = findViewById<View>(R.id.wordnumbertoadd) as EditText
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
                        // clear fields of viewmodel data class
                        voiceToBeRecordedInStories.story =
                            textWord1.text.toString().lowercase(Locale.getDefault())
                        voiceToBeRecordedInStories.phraseNumberInt =
                            textWord2.text.toString().toInt()
                        clearFieldsOfViewmodelDataClass()
                    }
                    // view the stories settings fragment initializing StoriesListFragment (FragmentTransaction
                    // switch between Fragments).
                    val ft = supportFragmentManager.beginTransaction()
                    //
                    val frag = StoriesListFragment()
                    //
                    ft.replace(R.id.settings_container, frag)
                    ft.addToBackStack(null)
                    ft.commit()
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
     * @see ContentsFragment
     *
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
        val textWord1 = rootViewFragment!!.findViewById<View>(R.id.keywordstorytosearch) as EditText
        val textWord2 = rootViewFragment!!.findViewById<View>(R.id.phrasenumbertosearch) as EditText
        // Viewmodel
        // In the activity, sometimes it is called observe, other times it is limited to performing set directly
        // (maybe it is not necessary to call observe)
        voiceToBeRecordedInStories!!.story =
            textWord1.text.toString().lowercase(Locale.getDefault())
        voiceToBeRecordedInStories!!.phraseNumberInt = textWord2.text.toString().toInt()
        //
        val frag = StoriesListFragment()
        val ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.settings_container, frag)
        ft.addToBackStack(null)
        ft.commit()
    }

    /**
     * on callback from StoriesAdapter to this Activity
     *
     * after deleting a piece of story the activity is notified to view the word pairs settings
     *
     *
     * @see com.sampietro.NaiveAAC.activities.Stories.StoriesAdapter
     *
     * @see StoriesFragment
     */
    override fun reloadStoriesFragmentDeleteStories(position: Int) {
        // Viewmodel
        // In the activity, sometimes it is called observe, other times it is limited to performing set directly
        // (maybe it is not necessary to call observe)
        viewModel.getSelectedItem()
            .observe(this) { voiceToBeRecordedInStories: VoiceToBeRecordedInStories ->
                // Perform an action with the latest item data
                realm = Realm.getDefaultInstance()
                var results: RealmResults<Stories>?
                results =
                    if (voiceToBeRecordedInStories.story == null || voiceToBeRecordedInStories.story == getString(
                            R.string.nome_storia
                        )
                    ) {
                        realm.where(Stories::class.java).findAll()
                    } else if (voiceToBeRecordedInStories.phraseNumberInt == 0) {
                        realm.where(Stories::class.java)
                            .equalTo(getString(R.string.story), voiceToBeRecordedInStories.story)
                            .findAll()
                    } else {
                        realm.where(Stories::class.java)
                            .equalTo(getString(R.string.story), voiceToBeRecordedInStories.story)
                            .equalTo(getString(R.string.phrasenumberint), voiceToBeRecordedInStories.phraseNumberInt)
                            .findAll()
                    }
                //
                val mStrings1 = arrayOf(getString(R.string.story), getString(R.string.phrasenumberint), getString(R.string.wordnumberint))
                val mStrings2 = arrayOf(Sort.ASCENDING, Sort.ASCENDING, Sort.ASCENDING)
                results = results.sort(mStrings1, mStrings2)
                //
                val daCancellare = results[position]!!
                val daCancellareStory = daCancellare.story
                val daCancellarePhraseNumber = daCancellare.phraseNumberInt
//                val daCancellareWordNumber = daCancellare.wordNumberInt
                //
                voiceToBeRecordedInStories.story = daCancellareStory
                voiceToBeRecordedInStories.phraseNumberInt = daCancellarePhraseNumber
                // delete
                realm.beginTransaction()
                daCancellare.deleteFromRealm()
                realm.commitTransaction()
                //
                renumberAPhraseOfAStory(realm, daCancellareStory, daCancellarePhraseNumber)
                renumberAStory(realm, daCancellareStory)
                //
                val frag = StoriesListFragment()
                val ft = supportFragmentManager.beginTransaction()
                ft.replace(R.id.settings_container, frag)
                ft.addToBackStack(null)
                ft.commit()
            }
    }

    /**
     * on callback from StoriesAdapter to this Activity
     *
     * for editing a piece of story the activity is notified to view the word pairs settings
     *
     *
     * @see com.sampietro.NaiveAAC.activities.Stories.StoriesAdapter
     *
     * @see StoriesFragment
     */
    override fun reloadStoriesFragmentForEditing(position: Int) {
        // view the stories settings fragment initializing StoriesFragment (FragmentTransaction
        // switch between Fragments).
        // Viewmodel
        // In the activity, sometimes it is called observe, other times it is limited to performing set directly
        // (maybe it is not necessary to call observe)
        viewModel.getSelectedItem()
            .observe(this) { voiceToBeRecordedInStories: VoiceToBeRecordedInStories ->
                // Perform an action with the latest item data
                realm = Realm.getDefaultInstance()
                var results: RealmResults<Stories>?
                results =
                    if (voiceToBeRecordedInStories.story == null || voiceToBeRecordedInStories.story == getString(
                            R.string.nome_storia
                        )
                    ) {
                        realm.where(Stories::class.java).findAll()
                    } else if (voiceToBeRecordedInStories.phraseNumberInt == 0) {
                        realm.where(Stories::class.java)
                            .equalTo(getString(R.string.story), voiceToBeRecordedInStories.story)
                            .findAll()
                    } else {
                        realm.where(Stories::class.java)
                            .equalTo(getString(R.string.story), voiceToBeRecordedInStories.story)
                            .equalTo(getString(R.string.phrasenumberint), voiceToBeRecordedInStories.phraseNumberInt)
                            .findAll()
                    }
                //
                val mStrings1 = arrayOf(getString(R.string.story), getString(R.string.phrasenumberint), getString(R.string.wordnumberint))
                val mStrings2 = arrayOf(Sort.ASCENDING, Sort.ASCENDING, Sort.ASCENDING)
                results = results.sort(mStrings1, mStrings2)
                //
                val frag = StoriesFragment()
                //
                val daModificare = results[position]!!
                //
                val daModificareStory = daModificare.story
                val daModificarePhraseNumber = daModificare.phraseNumberInt
//                val daModificareWordNumber = daModificare.wordNumberInt
                //
                voiceToBeRecordedInStories.story = daModificare.story
                voiceToBeRecordedInStories.phraseNumberInt = daModificare.phraseNumberInt
                voiceToBeRecordedInStories.wordNumberInt = daModificare.wordNumberInt
                voiceToBeRecordedInStories.word = daModificare.word
                voiceToBeRecordedInStories.uriType = daModificare.uriType
                voiceToBeRecordedInStories.uri = daModificare.uri
                voiceToBeRecordedInStories.answerActionType = daModificare.answerActionType
                voiceToBeRecordedInStories.answerAction = daModificare.answerAction
                voiceToBeRecordedInStories.video = daModificare.video
                voiceToBeRecordedInStories.sound = daModificare.sound
                voiceToBeRecordedInStories.soundReplacesTTS = daModificare.soundReplacesTTS
                voiceToBeRecordedInStories.fromAssets = daModificare.fromAssets
                // delete
                realm.beginTransaction()
                daModificare.deleteFromRealm()
                realm.commitTransaction()
                //
                renumberAPhraseOfAStory(realm, daModificareStory, daModificarePhraseNumber)
                renumberAStory(realm, daModificareStory)
                //
                val ft = supportFragmentManager.beginTransaction()
                ft.replace(R.id.settings_container, frag)
                ft.addToBackStack(null)
                ft.commit()
            }
    }

    /**
     * on callback from StoriesAdapter to this Activity
     *
     * for insertion a piece of story the activity is notified to view the word pairs settings
     *
     *
     * @see com.sampietro.NaiveAAC.activities.Stories.StoriesAdapter
     *
     * @see StoriesFragment
     */
    override fun reloadStoriesFragmentForInsertion(position: Int) {
        // view the stories settings fragment initializing StoriesFragment (FragmentTransaction
        // switch between Fragments).
        // Viewmodel
        // In the activity, sometimes it is called observe, other times it is limited to performing set directly
        // (maybe it is not necessary to call observe)
        viewModel.getSelectedItem()
            .observe(this) { voiceToBeRecordedInStories: VoiceToBeRecordedInStories ->
                // Perform an action with the latest item data
                realm = Realm.getDefaultInstance()
                var results: RealmResults<Stories>?
                results =
                    if (voiceToBeRecordedInStories.story == null || voiceToBeRecordedInStories.story == getString(
                            R.string.nome_storia
                        )
                    ) {
                        realm.where(Stories::class.java).findAll()
                    } else if (voiceToBeRecordedInStories.phraseNumberInt == 0) {
                        realm.where(Stories::class.java)
                            .equalTo(getString(R.string.story), voiceToBeRecordedInStories.story)
                            .findAll()
                    } else {
                        realm.where(Stories::class.java)
                            .equalTo(getString(R.string.story), voiceToBeRecordedInStories.story)
                            .equalTo(getString(R.string.phrasenumberint), voiceToBeRecordedInStories.phraseNumberInt)
                            .findAll()
                    }
                //
                val mStrings1 = arrayOf(getString(R.string.story), getString(R.string.phrasenumberint), getString(R.string.wordnumberint))
                val mStrings2 = arrayOf(Sort.ASCENDING, Sort.ASCENDING, Sort.ASCENDING)
                results = results.sort(mStrings1, mStrings2)
                //
                val frag = StoriesFragment()
                //
                val daInserirePrimaDi = results[position]!!
                //
                voiceToBeRecordedInStories.story = daInserirePrimaDi.story
                voiceToBeRecordedInStories.phraseNumberInt = daInserirePrimaDi.phraseNumberInt
                voiceToBeRecordedInStories.wordNumberInt = daInserirePrimaDi.wordNumberInt
                //
                val ft = supportFragmentManager.beginTransaction()
                ft.replace(R.id.settings_container, frag)
                ft.addToBackStack(null)
                ft.commit()
            }
    }
}