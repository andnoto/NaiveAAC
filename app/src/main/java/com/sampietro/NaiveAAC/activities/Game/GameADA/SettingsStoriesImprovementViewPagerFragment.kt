package com.sampietro.NaiveAAC.activities.Game.GameADA

import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import android.media.MediaPlayer
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import android.widget.VideoView
import com.sampietro.NaiveAAC.R
import com.sampietro.NaiveAAC.activities.BaseAndAbstractClass.GameFragmentAbstractClass
import com.sampietro.NaiveAAC.activities.BaseAndAbstractClass.GameFragmentAbstractClassWithoutConstructor
import com.sampietro.NaiveAAC.activities.Game.Utils.CenterVideoView
import com.sampietro.NaiveAAC.activities.Grammar.GrammarHelper.searchNegationAdverb
import com.sampietro.NaiveAAC.activities.Graphics.GraphicsAndPrintingHelper.addImage
import com.sampietro.NaiveAAC.activities.Graphics.ImageSearchHelper.searchUri
import com.sampietro.NaiveAAC.activities.Graphics.Sounds
import com.sampietro.NaiveAAC.activities.Graphics.Videos
import com.sampietro.NaiveAAC.activities.Stories.Stories
import io.realm.Realm
import io.realm.RealmResults
import java.io.IOException
import java.util.Locale

/**
 * <h1>GameADAViewPagerFragment</h1>
 *
 * **GameADAViewPagerFragment** UI for GameADAViewPager
 *
 *
 *
 * @version     5.0, 01/04/2024
 * @see GameFragmentAbstractClass
 *
 * @see GameADAViewPagerActivity
 */
class SettingsStoriesImprovementViewPagerFragment() : GameFragmentAbstractClassWithoutConstructor() {
    lateinit var rootView: View
    //
    var sharedStory: String? = null
    lateinit var resultsStories: RealmResults<Stories>
    var wordToDisplay: Stories? = null
    var wordToDisplayIndex = 0

    //
    lateinit var activity: Activity

    //
    private var image: ImageView? = null
    private var frameLayout: FrameLayout? = null
    private lateinit var centerVideoView: CenterVideoView
    private var isVideoViewAdded = false
    private var soundMediaPlayer: MediaPlayer? = null

    //
    lateinit var gameADAViewPagerOnFragmentEventlistener: GameADAViewPagerOnFragmentEventListener
    lateinit var gameADAViewPagerOnFragmentSoundMediaPlayerlistener: GameADAViewPagerOnFragmentSoundMediaPlayerListener
    lateinit var media_containerOnClickListener: GameADAViewPagerMediaContainerOnClickListener
    var media_container: FrameLayout? = null

    //
    private var gameUseVideoAndSound: String? = null

    /**
     * listener setting for game activities callbacks , context annotation, realm get default instance
     * and get print permissions
     *
     * @see androidx.fragment.app.Fragment.onAttach
     */
    override fun onAttach(context: Context) {
        super.onAttach(context)
        activity = context as Activity
        gameADAViewPagerOnFragmentEventlistener =
            activity as GameADAViewPagerOnFragmentEventListener
        gameADAViewPagerOnFragmentSoundMediaPlayerlistener =
            activity as GameADAViewPagerOnFragmentSoundMediaPlayerListener
        media_containerOnClickListener = activity as GameADAViewPagerMediaContainerOnClickListener
        //
        ctext = context
        // REALM
        realm = Realm.getDefaultInstance()
        // if is print permitted then preference_PrintPermissions = Y
        sharedPref = ctext.getSharedPreferences(
            getString(R.string.preference_file_key), Context.MODE_PRIVATE
        )
        preference_PrintPermissions =
            sharedPref.getString( getString(R.string.preference_print_permissions), getString(R.string.default_string))!!
        preference_TitleWritingType =
            sharedPref.getString(getString(R.string.preference_title_writing_type), getString(R.string.uppercase))!!
        //
        /*
        ADAPTED FOR VIDEO AND SOUND
         */
        centerVideoView = CenterVideoView(context)
        centerVideoView.setOnInfoListener { mediaPlayer, i, i1 ->
            if (i == MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START) {
                centerVideoView.setBackgroundResource(0)
            }
            false
        }
    }
    /**
     * prepares the ui and makes the callback to the activity
     *
     *
     * Refer to [raywenderlich.com](https://www.raywenderlich.com/8192680-viewpager2-in-android-getting-started)
     * By [Rajdeep Singh](https://www.raywenderlich.com/u/rajdeep1008)
     *
     * @see androidx.fragment.app.Fragment.onCreateView
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        rootView = inflater.inflate(R.layout.activity_settings_stories_improvement_viewpager_content, container, false)
        return rootView
    }
    /**
     * prepares the ui and makes the callback to the activity
     *
     *
     * Refer to [raywenderlich.com](https://www.raywenderlich.com/8192680-viewpager2-in-android-getting-started)
     * By [Rajdeep Singh](https://www.raywenderlich.com/u/rajdeep1008)
     *
     * @see androidx.fragment.app.Fragment.onViewCreated
     */
//    override fun onViewCreated(
//        view: View,
//        savedInstanceState: Bundle?
//    ) {
//        rootView = view
//
//    }
    //
    /**
     * prepares the ui and makes the callback to the activity
     *
     *
     * Refer to [raywenderlich.com](https://www.raywenderlich.com/8192680-viewpager2-in-android-getting-started)
     * By [Rajdeep Singh](https://www.raywenderlich.com/u/rajdeep1008)
     * Refer to [stackoverflow.com](https://stackoverflow.com/questions/70486297/how-to-recreate-or-refresh-fragment-while-swiping-viewpager2-tabs)
     * By [NimaAzhd](https://stackoverflow.com/users/11950155/nimaazhd)
     * @see androidx.fragment.app.Fragment.onResume
     *
     * @see addImage
     */
    override fun onResume() {
        super.onResume()
        //
        val bundle = this.arguments
        if (bundle != null) {
            sharedStory = bundle.getString(ctext.getString(R.string.story_to_display))
            //            phraseToDisplayIndex = bundle.getInt("PHRASE TO DISPLAY INDEX");
            wordToDisplayIndex = bundle.getInt(ctext.getString(R.string.word_to_display_index))
            gameUseVideoAndSound = bundle.getString(ctext.getString(R.string.game_use_video_and_sound))
            //
            resultsStories = realm.where(Stories::class.java)
                .beginGroup()
                .equalTo(ctext.getString(R.string.story), sharedStory)
                .equalTo(
                    ctext.getString(R.string.wordnumberintinthestory),
                    wordToDisplayIndex + 1
                )
                .endGroup()
                .findAll()
            //
            wordToDisplay = resultsStories.get(0)
            //
            val textTitleImage = rootView.findViewById<View>(R.id.titleimage) as TextView
            textTitleImage.text = wordToDisplay!!.word!!.uppercase(Locale.getDefault())
            Log.e("TAGWORDTODISPLAY", wordToDisplay!!.word!!)
            //
            val imageSize = calculateImageSize()
            // ricerca immagine
            val imageGameImage = rootView.findViewById<ImageView>(R.id.gameimage)
            imageGameImage.contentDescription =
                wordToDisplay!!.word!!.uppercase(Locale.getDefault())
            if (wordToDisplay!!.uriType!! == "I") {
                val imageUrl = searchUri(ctext,
                    realm,
                    wordToDisplay!!.uri)
                val uriType = "S"
                val uri = imageUrl
                addImage(uriType, uri, imageGameImage, imageSize, imageSize)
                } else
                {
                addImage(wordToDisplay!!.uriType!!, wordToDisplay!!.uri, imageGameImage, imageSize, imageSize)
                }
            // search for negation adverbs
            val imageGameImage2 = rootView.findViewById<ImageView>(R.id.gameimage2)
            imageGameImage2.visibility = View.INVISIBLE
            val negationAdverbImageToSearchFor = searchNegationAdverb(
                ctext,
                wordToDisplay!!.word!!.lowercase(Locale.getDefault()), realm
            )
            if (negationAdverbImageToSearchFor != ctext.getString(R.string.non_trovato)) {
                // INTERNAL MEMORY IMAGE SEARCH
                val uriToSearch = searchUri(ctext, realm, negationAdverbImageToSearchFor)
                addImage("S", uriToSearch, imageGameImage2, imageSize, imageSize)
                imageGameImage2.visibility = View.VISIBLE
            }
            // TTS
            if (wordToDisplay!!.soundReplacesTTS != "Y") {
                tTS1 = TextToSpeech(ctext) { status ->
                    if (status != TextToSpeech.ERROR) {
                        tTS1!!.speak(
                            wordToDisplay!!.word,
                            TextToSpeech.QUEUE_FLUSH,
                            null,
                            ctext.getString(R.string.prova_tts)
                        )
                    } else {
                        Toast.makeText(ctext, status, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
        /*
        ADAPTED FOR VIDEO AND SOUND
         */
        if (centerVideoView == null) {
            return
        }
        // remove old video views
        removeVideoView(centerVideoView)
        //
        val resultsSounds =
            realm.where(Sounds::class.java).equalTo(ctext.getString(R.string.descrizione), wordToDisplay!!.sound).findAll()
        var soundPath: String? = null
        if (resultsSounds.size != 0) {
            soundPath = resultsSounds[0]!!.uri
        }
        //
        if (soundPath != null && gameUseVideoAndSound == "Y") {
            if (soundPath != ctext.getString(R.string.non_trovato)) {
                // play audio file using MediaPlayer
                try {
                    soundMediaPlayer = MediaPlayer()
                    soundMediaPlayer!!.setDataSource(soundPath)
                    soundMediaPlayer!!.prepare()
                    soundMediaPlayer!!.start()
                    gameADAViewPagerOnFragmentSoundMediaPlayerlistener.receiveResultGameFragment(
                        rootView,
                        soundMediaPlayer
                    )
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
        //
        image = rootView.findViewById(R.id.gameimage)
        frameLayout = rootView.findViewById(R.id.gameimageFL)
        //
        val resultsVideos =
            realm.where(Videos::class.java).equalTo(ctext.getString(R.string.descrizione), wordToDisplay!!.video).findAll()
        var videoPath: String? = null
        if (resultsVideos.size != 0) {
            videoPath = resultsVideos[0]!!.uri
        }
        if (videoPath != null && gameUseVideoAndSound == "Y") {
            if (videoPath != ctext.getString(R.string.non_trovato)) {
                //
                if (!isVideoViewAdded) {
                    addVideoView()
                }
                //
                val imageSize = calculateImageSize()
                centerVideoView.layoutParams.height = imageSize
                centerVideoView.layoutParams.width = imageSize
                //
                centerVideoView.setBackgroundResource(R.drawable.white_background)
                centerVideoView.setVideoPath(videoPath)
                centerVideoView.seekTo(1)
                centerVideoView.start()
                centerVideoView.setOnPreparedListener { mp ->
                    mp.setVolume(0f, 0f)
                    mp.isLooping = true
                }
                //
                media_container = frameLayout
                media_container!!.setOnClickListener(View.OnClickListener { view ->
                    media_containerOnClickListener.receiveOnClickGameImage(
                        view
                    )
                })
            }
        }
        /*

         */gameADAViewPagerOnFragmentEventlistener.receiveWordToDisplayIndexGameFragment(
            rootView,
            wordToDisplayIndex + 1
        )
        //
//        listener.receiveResultGameFragment(rootView)
    }
    /*
        ADAPTED FOR VIDEO AND SOUND
    */
    /**
     * calculate video size.
     *
     * @return imageSize int image size
     */
    fun calculateImageSize(): Int {
        val configuration = requireActivity().resources.configuration
        val screenWidthDp =
            configuration.screenWidthDp //The current width of the available screen space, in dp units, corresponding to screen width resource qualifier.
        val screenHeightDp = configuration.screenHeightDp
        val orientation = requireActivity().resources.configuration.orientation
        val imageSize = if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            // code for portrait mode
            screenWidthDp
        } else {
            // code for landscape mode
            screenHeightDp * 80 / 100
        }
        return imageSize
    }

    /**
     * Remove the old player.
     *
     * @param videoView videoview to remove
     */
    private fun removeVideoView(videoView: VideoView?) {
        centerVideoView.setBackgroundResource(R.drawable.white_background)
        val parentViewParent = videoView?.parent ?: return
        val parent: ViewGroup = parentViewParent as ViewGroup? ?: return
        val indexOfChild = parent.indexOfChild(videoView)
        if (indexOfChild >= 0) {
            parent.removeViewAt(indexOfChild)
            isVideoViewAdded = false
        }
        //
        image!!.visibility = View.VISIBLE
    }

    /**
     * Add the new videoview.
     *
     */
    private fun addVideoView() {
        frameLayout!!.addView(centerVideoView)
        isVideoViewAdded = true
        centerVideoView.visibility = View.VISIBLE
        centerVideoView.alpha = 1f
    }

    /**
     * stop playback
     *
     */
    fun releasePlayer() {
        // release sound mediaplayer
        try {
            if (soundMediaPlayer != null) {
                if (soundMediaPlayer!!.isPlaying) {
                    soundMediaPlayer!!.stop()
                }
                soundMediaPlayer!!.release()
                soundMediaPlayer = null
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        //
        centerVideoView.stopPlayback()
    }
    //
    /**
     * release the video player
     *
     * @see androidx.fragment.app.Fragment.onStop
     *
     * @see releasePlayer
     */
    override fun onStop() {
        releasePlayer()
        super.onStop()
    }
    /*

     */
}