package com.sampietro.NaiveAAC.activities.Game.GameADA

import com.sampietro.NaiveAAC.activities.Game.Utils.GameFragmentAbstractClass
import io.realm.RealmResults
import com.sampietro.NaiveAAC.activities.Stories.Stories
import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import com.sampietro.NaiveAAC.activities.Game.Utils.CenterVideoView
import android.media.MediaPlayer
import com.sampietro.NaiveAAC.R
import android.view.LayoutInflater
import android.view.ViewGroup
import android.os.Bundle
import com.sampietro.NaiveAAC.activities.Grammar.GrammarHelper
import com.sampietro.NaiveAAC.activities.Graphics.ImageSearchHelper
import android.speech.tts.TextToSpeech
import com.sampietro.NaiveAAC.activities.Graphics.Sounds
import com.sampietro.NaiveAAC.activities.Graphics.Videos
import android.util.Log
import android.view.View
import android.widget.*
import com.sampietro.NaiveAAC.activities.Graphics.GraphicsHelper
import io.realm.Realm
import java.io.File
import java.io.IOException
import java.lang.Exception
import java.util.*

/**
 * <h1>GameADAViewPagerFragment</h1>
 *
 * **GameADAViewPagerFragment** UI for GameADAViewPager
 *
 *
 *
 * @version     4.0, 09/09/2023
 * @see GameFragmentAbstractClass
 *
 * @see GameADAViewPagerActivity
 */
class GameADAViewPagerFragment : GameFragmentAbstractClass() {
    var sharedStory: String? = null
    lateinit var resultsStories: RealmResults<Stories>
    var wordToDisplay: Stories? = null
//    var phraseToDisplayIndex = 0
    var wordToDisplayIndex = 0

    //
    var activity: Activity? = null

    //
    private var image: ImageView? = null
    private var frameLayout: FrameLayout? = null
    private var centerVideoView: CenterVideoView? = null
    private var isVideoViewAdded = false
    private var soundMediaPlayer: MediaPlayer? = null

    //
    var gameADAViewPagerOnFragmentEventlistener: GameADAViewPagerOnFragmentEventListener? = null
    var gameADAViewPagerOnFragmentSoundMediaPlayerlistener: GameADAViewPagerOnFragmentSoundMediaPlayerListener? =
        null
    var media_containerOnClickListener: GameADAViewPagerMediaContainerOnClickListener? = null
    lateinit var media_container: FrameLayout

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
            activity as GameADAViewPagerOnFragmentEventListener?
        gameADAViewPagerOnFragmentSoundMediaPlayerlistener =
            activity as GameADAViewPagerOnFragmentSoundMediaPlayerListener?
        media_containerOnClickListener = activity as GameADAViewPagerMediaContainerOnClickListener?
        //
        ctext = context
        // REALM
        realm = Realm.getDefaultInstance()
        // if is print permitted then preference_PrintPermissions = Y
        sharedPref = ctext.getSharedPreferences(
            getString(R.string.preference_file_key), Context.MODE_PRIVATE
        )
        preference_PrintPermissions =
            sharedPref.getString("preference_PrintPermissions", "DEFAULT")!!
        preference_TitleWritingType =
            sharedPref.getString("preference_title_writing_type", "uppercase")!!
        //
        /*
        ADAPTED FOR VIDEO AND SOUND
         */
//        soundMediaPlayer = new MediaPlayer();
        //
        centerVideoView = CenterVideoView(context)
        centerVideoView!!.setOnInfoListener { mediaPlayer, i, i1 ->
            if (i == MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START) {
                centerVideoView!!.setBackgroundResource(0)
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
        rootView = inflater.inflate(R.layout.activity_game_ada_viewpager_content, container, false)
        return rootView
    }
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
            sharedStory = bundle.getString("STORY TO DISPLAY")
            //            phraseToDisplayIndex = bundle.getInt("PHRASE TO DISPLAY INDEX");
            wordToDisplayIndex = bundle.getInt("WORD TO DISPLAY INDEX")
            gameUseVideoAndSound = bundle.getString("GAME USE VIDEO AND SOUND")
            //
            resultsStories = realm.where(Stories::class.java)
                .beginGroup()
                .equalTo("story", sharedStory)
                .equalTo(
                    "wordNumberIntInTheStory",
                    wordToDisplayIndex + 1
                ) //                            .equalTo("phraseNumberInt", phraseToDisplayIndex)
                //                            .notEqualTo("wordNumberInt", 0)
                //                            .lessThan("wordNumberInt", 99)
                .endGroup()
                .findAll()
            //
            //
//            resultsStories = resultsStories.sort("wordNumberInt");
            //
            wordToDisplay = resultsStories.get(0)
            //
            val textTitleImage = rootView.findViewById<View>(R.id.titleimage) as TextView
            textTitleImage.text = wordToDisplay!!.word!!.uppercase(Locale.getDefault())
            Log.e("TAGWORDTODISPLAY", wordToDisplay!!.word!!)
            // ricerca immagine
            val imageGameImage = rootView.findViewById<ImageView>(R.id.gameimage)
            imageGameImage.contentDescription =
                wordToDisplay!!.word!!.uppercase(Locale.getDefault())
            addImage(wordToDisplay!!.uriType!!, wordToDisplay!!.uri, imageGameImage, 200, 200)
            // search for negation adverbs
            val imageGameImage2 = rootView.findViewById<ImageView>(R.id.gameimage2)
            imageGameImage2.visibility = View.INVISIBLE
            val negationAdverbImageToSearchFor = GrammarHelper.searchNegationAdverb(
                wordToDisplay!!.word!!.lowercase(Locale.getDefault()), realm
            )
            if (negationAdverbImageToSearchFor != "non trovato") {
                // INTERNAL MEMORY IMAGE SEARCH
                val uriToSearch = ImageSearchHelper.searchUri(realm, negationAdverbImageToSearchFor)
                // imageGameImage2.setScaleType(ImageView.ScaleType.CENTER_CROP);
                addImage("S", uriToSearch, imageGameImage2, 200, 200)
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
                            "prova tts"
                        )
                    } else {
                        Toast.makeText(ctext, status, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
        /*
        ADAPTED FOR VIDEO AND SOUND
         */if (centerVideoView == null) {
            return
        }
        // remove old video views
        removeVideoView(centerVideoView!!)
        //
        val resultsSounds =
            realm.where(Sounds::class.java).equalTo("descrizione", wordToDisplay!!.sound).findAll()
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
                    gameADAViewPagerOnFragmentSoundMediaPlayerlistener!!.receiveResultGameFragment(
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
            realm.where(Videos::class.java).equalTo("descrizione", wordToDisplay!!.video).findAll()
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
                centerVideoView!!.layoutParams.height = imageSize
                centerVideoView!!.layoutParams.width = imageSize
                //
                centerVideoView!!.setBackgroundResource(R.drawable.white_background)
                centerVideoView!!.setVideoPath(videoPath)
                centerVideoView!!.seekTo(1)
                centerVideoView!!.start()
                centerVideoView!!.setOnPreparedListener { mp ->
                    mp.setVolume(0f, 0f)
                    mp.isLooping = true
                }
                //
                media_container = rootView.findViewById(R.id.gameimageFL)
                media_container.setOnClickListener(View.OnClickListener { view ->
                    media_containerOnClickListener!!.receiveOnClickGameImage(
                        view
                    )
                })
            }
        }
        /*

         */gameADAViewPagerOnFragmentEventlistener!!.receiveWordToDisplayIndexGameFragment(
            rootView,
            wordToDisplayIndex + 1
        )
        //
        listener.receiveResultGameFragment(rootView)
    }
    //
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
    override fun addImage(urlType: String, url: String?, img: ImageView?, width: Int, height: Int) {
        val imageSize = calculateImageSize()
        if (urlType == "A") {
            GraphicsHelper.addImageUsingPicasso(url, img, imageSize, imageSize)
        } else {
            val f = File(url!!)
            GraphicsHelper.addFileImageUsingPicasso(f, img, imageSize, imageSize)
        }
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
//        var imageSize = 0
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
        centerVideoView!!.setBackgroundResource(R.drawable.white_background)
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
        centerVideoView!!.visibility = View.VISIBLE
        centerVideoView!!.alpha = 1f
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
        centerVideoView!!.stopPlayback()
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
    } /*

     */
}