package com.sampietro.NaiveAAC.activities.Game.GameADA

import android.app.Activity
import android.app.Dialog
import com.sampietro.NaiveAAC.activities.Game.Utils.GameHelper.historyAdd
import com.sampietro.NaiveAAC.activities.Game.Utils.GameActivityAbstractClass
import com.sampietro.NaiveAAC.activities.Game.Utils.PrizeFragment.onFragmentEventListenerPrize
import com.sampietro.NaiveAAC.activities.Game.Utils.YoutubePrizeFragment.onFragmentEventListenerYoutubePrize
import io.realm.RealmResults
import com.sampietro.NaiveAAC.activities.Stories.Stories
import android.graphics.Bitmap
import com.squareup.picasso.Picasso.LoadedFrom
import android.graphics.drawable.Drawable
import android.speech.tts.TextToSpeech
import android.os.Bundle
import com.sampietro.NaiveAAC.R
import android.content.Intent
import com.sampietro.NaiveAAC.activities.Game.ChoiseOfGame.ChoiseOfGameActivity
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import com.sampietro.NaiveAAC.activities.history.VoiceToBeRecordedInHistory
import androidx.print.PrintHelper
import com.sampietro.NaiveAAC.activities.Grammar.GrammarHelper
import com.sampietro.NaiveAAC.activities.Graphics.ImageSearchHelper
import com.sampietro.NaiveAAC.activities.Settings.VerifyActivity
import android.widget.ImageButton
import android.widget.EditText
import android.os.Handler
import android.os.Looper
import android.text.InputType
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.view.inputmethod.InputMethodManager.HIDE_IMPLICIT_ONLY
import android.view.inputmethod.InputMethodManager.SHOW_IMPLICIT
import com.sampietro.NaiveAAC.activities.Game.Utils.GameFragmentHear
import android.widget.Toast
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.sampietro.NaiveAAC.activities.Game.Utils.PrizeFragment
import com.sampietro.NaiveAAC.activities.Game.Utils.YoutubePrizeFragment
import com.sampietro.NaiveAAC.activities.Game.Balloon.BalloonGameplayActivity
import com.sampietro.NaiveAAC.activities.Graphics.GraphicsHelper.getTargetBitmapFromFileUsingPicasso
import com.sampietro.NaiveAAC.activities.Graphics.GraphicsHelper.getTargetBitmapFromUrlUsingPicasso
import com.sampietro.NaiveAAC.activities.history.ToBeRecordedInHistory
import com.sampietro.NaiveAAC.activities.history.ToBeRecordedInHistoryImpl
import com.sampietro.NaiveAAC.activities.Graphics.ResponseImageSearch
import com.sampietro.NaiveAAC.activities.VoiceRecognition.AndroidPermission
import com.sampietro.NaiveAAC.activities.VoiceRecognition.SpeechRecognizerManagement
import com.sampietro.NaiveAAC.activities.history.History
import com.squareup.picasso.Target
import io.realm.Realm
import java.io.File
import java.lang.Exception
import java.util.*

/**
 * <h1>GameADAActivity</h1>
 *
 * **GameADAActivity** displays images (uploaded by the user or Arasaac pictograms) of the
 * phrases of a story
 *
 *
 * @version     4.0, 09/09/2023
 * @see GameActivityAbstractClass
 */
class GameADAActivity : GameActivityAbstractClass(), GameADARecyclerViewAdapterInterface,
    onFragmentEventListenerPrize, GameADAOnFragmentEventListener,
    onFragmentEventListenerYoutubePrize {
    //
    var sharedStory: String? = null
    var resultsStories: RealmResults<Stories>? = null
    var sharedPhraseSize = 0

    //
    var phraseToDisplayIndex = 0
    var phraseToDisplay: Stories? = null

    //
    var preference_PrintPermissions: String? = null
    var preference_AllowedMarginOfError = 0

    //
    var wordToDisplayIndex = 0
    var ttsEnabled = true

    //
    lateinit var galleryList: ArrayList<GameADAArrayList>
    //
    /**
     * used for printing
     */
    var bitmap1: Bitmap? = null
    var bitmap2: Bitmap? = null
    var mergedImages: Bitmap? = null

    /**
     * used for printing
     */
    var target1: Target = object : Target {
        override fun onBitmapLoaded(bitmap: Bitmap, from: LoadedFrom) {
            bitmap1 = bitmap
        }

        override fun onBitmapFailed(e: Exception, errorDrawable: Drawable) {}
        override fun onPrepareLoad(placeHolderDrawable: Drawable?) {}
    }

    /**
     * used for printing
     */
    var target2: Target = object : Target {
        override fun onBitmapLoaded(bitmap: Bitmap, from: LoadedFrom) {
            bitmap2 = bitmap
        }

        override fun onBitmapFailed(e: Exception, errorDrawable: Drawable) {}
        override fun onPrepareLoad(placeHolderDrawable: Drawable?) {}
    }

    /**
     * used for TTS
     */
    var tTS1: TextToSpeech? = null

    //
//    private var mContentView: ViewGroup? = null

    //
    private var gameUseVideoAndSound: String? = null

    /**
     * configurations of gameADA start screen.
     *
     *
     *
     * @param savedInstanceState Define potentially saved parameters due to configurations changes.
     * @see SpeechRecognizerManagement.prepareSpeechRecognizer
     *
     * @see android.app.Activity.onCreate
     * @see .fragmentTransactionStart
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //
        setContentView(R.layout.activity_game_ada)
        //
        if (supportActionBar != null) {
            supportActionBar!!.hide()
        }
        /*
        USED FOR FULL SCREEN
         */
//        val mContentView:ViewGroup = findViewById(R.id.activity_game_ada_id)
        setToFullScreen()
//        val viewTreeObserver = mContentView.getViewTreeObserver()
//        if (viewTreeObserver.isAlive) {
//            viewTreeObserver.addOnGlobalLayoutListener(object : OnGlobalLayoutListener {
//                override fun onGlobalLayout() {
//                    mContentView.getViewTreeObserver().removeOnGlobalLayoutListener(this)
//                }
//            })
//        }
//        mContentView.setOnClickListener(View.OnClickListener { view: View? -> setToFullScreen() })
        /*

         */
        AndroidPermission.checkPermission(this)
        //
        SpeechRecognizerManagement.prepareSpeechRecognizer(this)
        //
        realm = Realm.getDefaultInstance()
        // SEARCH HISTORY TO BE DISPLAYED
        val intent = intent
        if (intent.hasExtra(ChoiseOfGameActivity.EXTRA_MESSAGE_GAME_PARAMETER)) {
            sharedStory = intent.getStringExtra(ChoiseOfGameActivity.EXTRA_MESSAGE_GAME_PARAMETER)
            phraseToDisplayIndex = 1
        }
        // SEARCH IF GAME USE VIDEO AND SOUND
        if (intent.hasExtra(ChoiseOfGameActivity.EXTRA_MESSAGE_GAME_USE_VIDEO_AND_SOUND)) {
            gameUseVideoAndSound =
                intent.getStringExtra(ChoiseOfGameActivity.EXTRA_MESSAGE_GAME_USE_VIDEO_AND_SOUND)
        }
        // if intent is from GameADAViewPagerActivity
        if (intent.hasExtra(getString(R.string.story_to_display))) {
            sharedStory = intent.getStringExtra(getString(R.string.story_to_display))
            phraseToDisplayIndex = intent.getIntExtra(getString(R.string.phrase_to_display_index), 1)
            wordToDisplayIndex = intent.getIntExtra(getString(R.string.word_to_display_index), 0)
            gameUseVideoAndSound = intent.getStringExtra(getString(R.string.game_use_video_and_sound))
            ttsEnabled = false
        }
        //
        context = this
        //
        sharedPref = context.getSharedPreferences(
            getString(R.string.preference_file_key), MODE_PRIVATE
        )
        //
        if (sharedStory == null) {
            sharedStory = sharedPref.getString(getString(R.string.preference_story), getString(R.string.default_string))
        }
        // resumes the story from the last sentence displayed
        if (intent.hasExtra(ChoiseOfGameActivity.EXTRA_MESSAGE_GAME_PARAMETER)) {
            val hasPhraseToDisplayIndex =
                sharedPref.contains(sharedStory + getString(R.string.preference_phrasetodisplayindex))
            if (hasPhraseToDisplayIndex) {
                phraseToDisplayIndex =
                    sharedPref.getInt(sharedStory + getString(R.string.preference_phrasetodisplayindex), 1)
            }
        }
        //
        val hasLastPhraseNumber = sharedPref.contains(getString(R.string.preference_last_phrase_number))
        if (hasLastPhraseNumber) {
            sharedLastPhraseNumber = sharedPref.getInt(getString(R.string.preference_last_phrase_number), 1)
        }
        // if is print permitted then preference_PrintPermissions = Y
        preference_PrintPermissions = sharedPref.getString(
            context.getString(R.string.preference_print_permissions),
            getString(R.string.default_string)
        )
        //
        preference_AllowedMarginOfError =
            sharedPref.getInt(getString(R.string.preference_allowed_margin_of_error), 20)
        //
        // SEARCH FIRST PHRASE TO DISPLAY
        // Check whether we're recreating a previously destroyed instance
        if (savedInstanceState != null) {
            // The onSaveInstanceState method is called before an activity may be killed
            // (for Screen Rotation Handling) so that
            // when it comes back it can restore its state.
            // it display the last phrase
            // else display the first phrase
            phraseToDisplayIndex = savedInstanceState.getInt(getString(R.string.phrase_to_display_index))
            sharedLastPhraseNumber = savedInstanceState.getInt(getString(R.string.last_phrase_number))
            fragmentTransactionStart()
        } else {
            resultsStories = realm.where(Stories::class.java)
                .beginGroup()
                .equalTo(getString(R.string.story), sharedStory)
                .equalTo(getString(R.string.phrasenumberint), phraseToDisplayIndex)
                .endGroup()
                .findAll()
            sharedPhraseSize = resultsStories!!.size
            //
            resultsStories = resultsStories!!.sort(getString(R.string.wordnumberint))
            //
            if (sharedPhraseSize != 0) {
                val editor = sharedPref.edit()
                editor.putInt(sharedStory + getString(R.string.preference_phrasetodisplayindex), phraseToDisplayIndex)
                editor.apply()
                //
                val toBeRecordedInHistory = gettoBeRecordedInHistory()
                // REALM SESSION REGISTRATION
                val voicesToBeRecordedInHistory: MutableList<VoiceToBeRecordedInHistory?> =
                    toBeRecordedInHistory.getListVoicesToBeRecordedInHistory()
//                val voicesToBeRecordedInHistory: List<VoiceToBeRecordedInHistory?> =
//                    toBeRecordedInHistory.getVoicesToBeRecordedInHistory()
                //
                val debugUrlNumber =
                    toBeRecordedInHistory.getNumberOfVoicesToBeRecordedInHistory()
                //
                historyAdd(realm, debugUrlNumber, voicesToBeRecordedInHistory)
                //
                fragmentTransactionStart()
            }
        }
    }
    //
    /**
     * Hide the Navigation Bar and go to the sentence
     *
     * @see android.app.Activity.onResume
     */
    override fun onResume() {
        /*
        USED FOR FULL SCREEN
         */
        super.onResume()
        setToFullScreen()
        /*
        USED FOR FULL SCREEN
         */
        continueGameAda()
    }
    //
    /**
     * destroy SpeechRecognizer
     *
     * @see android.app.Activity.onDestroy
     */
    override fun onDestroy() {
        super.onDestroy()
        SpeechRecognizerManagement.destroyRecognizer()
    }
    //
    /**
     * This method is called before an activity may be killed so that when it comes back some time in the future it can restore its state..
     *
     *
     * it stores the last phrase number
     *
     * @param savedInstanceState Define potentially saved parameters due to configurations changes.
     */
    public override fun onSaveInstanceState(savedInstanceState: Bundle) {
        //
        savedInstanceState.putInt(getString(R.string.phrase_to_display_index), phraseToDisplayIndex)
        savedInstanceState.putInt(getString(R.string.word_to_display_index), wordToDisplayIndex)
        savedInstanceState.putInt(getString(R.string.last_phrase_number), sharedLastPhraseNumber)
        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState)
    }

    /**
     * This method is responsible to transfer MainActivity into fullscreen mode.
     */
    private fun setToFullScreen() {
        WindowCompat.setDecorFitsSystemWindows(window, false)

        val insetsController = WindowCompat.getInsetsController(window, window.decorView)
        insetsController.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        insetsController.hide(WindowInsetsCompat.Type.statusBars())
        insetsController.hide(WindowInsetsCompat.Type.navigationBars())
//        findViewById<View>(R.id.activity_game_ada_id).systemUiVisibility =
//            View.SYSTEM_UI_FLAG_LOW_PROFILE or View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
    }
    //
    /**
     * Called when the user taps the home button.
     *
     *
     * @param v view of tapped button
     * @see ChoiseOfGameActivity
     */
    fun returnHome(v: View?) {
        /*
                navigate to home screen (ChoiseOfGameActivity)
    */
        val intent = Intent(this, ChoiseOfGameActivity::class.java)
        startActivity(intent)
    }
    //
    /**
     * Called when the user taps the print button.
     *
     *
     * @param v view of tapped button
     * @see .printPhraseCacheImages
     *
     * @see .printPhraseCreateMergedImages
     */
    fun printPhrase(v: View?) {
        printPhraseCacheImages()
        val TIME_OUT = 2500
        Handler(Looper.getMainLooper()).postDelayed({
            printPhraseCreateMergedImages()
            //
            val photoPrinter = PrintHelper(context)
            photoPrinter.scaleMode = PrintHelper.SCALE_MODE_FIT
            photoPrinter.printBitmap(context.getString(R.string.stampa_immagine1), mergedImages!!)
        }, TIME_OUT.toLong())
//        Handler().postDelayed({
//            printPhraseCreateMergedImages()
            //
//            val photoPrinter = PrintHelper(context)
//            photoPrinter.scaleMode = PrintHelper.SCALE_MODE_FIT
//            photoPrinter.printBitmap(context.getString(R.string.stampa_immagine1), mergedImages!!)
//        }, TIME_OUT.toLong())
        //
    }
    //
    /**
     * Cache images for printing
     *
     *
     * @see .getTargetBitmapFromUrlUsingPicasso
     *
     * @see .getTargetBitmapFromFileUsingPicasso
     *
     * @see GrammarHelper.searchNegationAdverb
     */
    fun printPhraseCacheImages() {
        val count = galleryList.size
        if (count != 0) {
            mergedImages = null
            var irrh = 0
            while (irrh < count) {
                if (galleryList[irrh].urlType == "A") {
                    getTargetBitmapFromUrlUsingPicasso(galleryList[irrh].url, target1)
                } else {
                    val f = File(galleryList[irrh].url!!)
                    getTargetBitmapFromFileUsingPicasso(f, target1)
                }
                // search for negation adverbs
                val negationAdverbImageToSearchFor = GrammarHelper.searchNegationAdverb(
                    context,
                    galleryList[irrh].image_title!!.lowercase(Locale.getDefault()), realm
                )
                if (negationAdverbImageToSearchFor != getString(R.string.non_trovato)) {
                    // INTERNAL MEMORY IMAGE SEARCH
                    val uriToSearch =
                        ImageSearchHelper.searchUri(context, realm, negationAdverbImageToSearchFor)
                    val f = File(uriToSearch)
                    getTargetBitmapFromFileUsingPicasso(f, target2)
                }
                irrh++
            }
        }
        //
    }

    /**
     * it merge images for printing
     *
     *
     * @see .getTargetBitmapFromUrlUsingPicasso
     *
     * @see .getTargetBitmapFromFileUsingPicasso
     *
     * @see GrammarHelper.searchNegationAdverb
     * @see ImageSearchHelper.searchUri
     * @see .createSingleImageSuperimposedFromMultipleImages
     *
     * @see .createSingleImageFromImageAndTitlePlacingThemVertically
     *
     * @see .createSingleImageFromMultipleImages
     */
    fun printPhraseCreateMergedImages() {
        val count = galleryList.size
        if (count != 0) {
            mergedImages = null
            //
            var imagesContainedInARow = 0
            var nrows = 1
            var ncolumns = 1
            //
            var irrh = 0
            while (irrh < count) {
                if (galleryList[irrh].urlType == "A") {
                    getTargetBitmapFromUrlUsingPicasso(galleryList[irrh].url, target1)
                } else {
                    val f = File(galleryList[irrh].url!!)
                    getTargetBitmapFromFileUsingPicasso(f, target1)
                }
                // search for negation adverbs
                val negationAdverbImageToSearchFor = GrammarHelper.searchNegationAdverb(
                    context,
                    galleryList[irrh].image_title!!.lowercase(Locale.getDefault()), realm
                )
                if (negationAdverbImageToSearchFor != getString(R.string.non_trovato)) {
                    // INTERNAL MEMORY IMAGE SEARCH
                    val uriToSearch =
                        ImageSearchHelper.searchUri(context, realm, negationAdverbImageToSearchFor)
                    val f = File(uriToSearch)
                    getTargetBitmapFromFileUsingPicasso(f, target2)
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
    private fun createSingleImageFromMultipleImages(
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
    //
    /**
     * used for printing create single bitmap image from image and title
     * Refer to [stackoverflow](https://stackoverflow.com/questions/2655402/android-canvas-drawtext)
     * answer of [gaz](https://stackoverflow.com/users/119396/gaz)
     *
     * @param firstImage bitmap of image
     * @param title string image title
     * @return bitmap single image created from image and title
     */
    private fun createSingleImageFromImageAndTitlePlacingThemVertically(
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
    //
    /**
     * used for printing create single bitmap image superimposed from multiple bitmap images
     *
     * @param firstImage bitmap of first image
     * @param secondImage bitmap of second image
     * @return bitmap single image created from multiple images
     */
    private fun createSingleImageSuperimposedFromMultipleImages(
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
     * Called when the user taps the settings button.
     * replace with hear fragment
     *
     *
     * @param v view of tapped button
     * @see ChoiseOfGameActivity
     */
    fun returnSettings(v: View?) {
        /*
                navigate to settings screen
    */
        val intent = Intent(this, VerifyActivity::class.java)
        startActivity(intent)
    }

    /**
     * Called when the user taps the start write button.
     * Refer to [stackoverflow](https://stackoverflow.com/questions/9467026/changing-position-of-the-dialog-on-screen-android)
     * answer of [Aleks G](https://stackoverflow.com/users/717214/aleks-g)
     * Refer to [stackoverflow](https://stackoverflow.com/questions/8991522/how-can-i-set-the-focus-and-display-the-keyboard-on-my-edittext-programmatical)
     * answer of [ungalcrys](https://stackoverflow.com/users/443427/ungalcrys)
     *
     *
     * @param v view of tapped button
     */
//    @RequiresApi(Build.VERSION_CODES.N)
    fun startWriteGameADA(v: View?) {
        val correspondingWord = searchAnswer()
        val correspondingWordIsNumeric = isNumeric(correspondingWord)
        //
        val d = Dialog(this)
        // Setting dialogview
        val window = d.window
        val wlp = window!!.attributes
        wlp.gravity = Gravity.BOTTOM
        wlp.flags = wlp.flags and WindowManager.LayoutParams.FLAG_DIM_BEHIND.inv()
        window.attributes = wlp
        //
        d.setTitle("inserisci la risposta o la richiesta")
        d.setCancelable(false)
        d.setContentView(R.layout.activity_game_ada_write_dialog)
        //
        val submitResponseOrRequestButton =
            d.findViewById<ImageButton>(R.id.submitResponseOrRequestButton)
        val cancelTheAnswerOrRequestButton =
            d.findViewById<ImageButton>(R.id.cancelTheAnswerOrRequestButton)
        val responseOrRequest = d.findViewById<View>(R.id.responseOrRequest) as EditText
        if (correspondingWordIsNumeric) {
            responseOrRequest.inputType = InputType.TYPE_CLASS_NUMBER
        } else {
            responseOrRequest.inputType = InputType.TYPE_CLASS_TEXT
        }
        //
        responseOrRequest.requestFocus()
        val imm = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(d.findViewById<View>(R.id.responseOrRequest),SHOW_IMPLICIT)
        imm.hideSoftInputFromWindow(d.findViewById<View>(R.id.responseOrRequest).getWindowToken(),HIDE_IMPLICIT_ONLY)
//        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)
        //
        submitResponseOrRequestButton.setOnClickListener { //
            var eText = responseOrRequest.text.toString()
            eText = eText.lowercase(Locale.getDefault())
            //
//            val responseOrRequest = d.findViewById<View>(R.id.responseOrRequest) as EditText
//            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(responseOrRequest.windowToken, 0)
            //
            d.cancel()
            //
            setToFullScreen()
            //
            checkAnswer(eText)
        }
        cancelTheAnswerOrRequestButton.setOnClickListener { //
//            val responseOrRequest = d.findViewById<View>(R.id.responseOrRequest) as EditText
//            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(responseOrRequest.windowToken, 0)
            //
            d.cancel()
            //
            setToFullScreen()
        }
        //
        d.show()
    }

    /**
     * If exist return answer related to the phrase.
     *
     *
     * @return string answer related to the phrase
     */
    fun searchAnswer(): String {
        //
        context = this
        sharedPref = context.getSharedPreferences(
            getString(R.string.preference_file_key), MODE_PRIVATE
        )
        val hasLastPhraseNumber = sharedPref.contains(getString(R.string.preference_last_phrase_number))
        if (hasLastPhraseNumber) {
            sharedLastPhraseNumber = sharedPref.getInt(getString(R.string.preference_last_phrase_number), 1)
        }
        // if wordNumber = 999 the word contain the answer related to the phrase
        var correspondingWord = getString(R.string.non_trovata)
        val results = realm.where(
            History::class.java
        )
            .beginGroup()
            .equalTo(getString(R.string.phrase_number), sharedLastPhraseNumber.toString())
            .and()
            .equalTo(getString(R.string.wordnumber), 999.toString())
            .endGroup()
            .findAll()
        // hystory also contains the article that I have to exclude from the comparison
        val count = results.size
        if (count != 0) {
            val result = results[0]
            if (result != null) {
                val resultHistoryCorrespondingWord = result.word
                val arrResultHistoryCorrespondingWords =
                    GrammarHelper.splitString(resultHistoryCorrespondingWord!!)
                val arrCorrespondingWordsLength = arrResultHistoryCorrespondingWords.size
                correspondingWord = if (arrCorrespondingWordsLength == 2) {
                    arrResultHistoryCorrespondingWords[1]
                } else {
                    arrResultHistoryCorrespondingWords[0]
                }
            }
        }
        return correspondingWord
    }

    /**
     * Called when the user taps the start speech button.
     * replace with hear fragment
     *
     * Refer to [stackoverflow](https://stackoverflow.com/questions/43520688/findfragmentbyid-and-findfragmentbytag)
     * answer of [Ricardo](https://stackoverflow.com/users/4266957/ricardo)
     *
     * @param v view of tapped button
     * @see SpeechRecognizerManagement.startSpeech
     */
    fun startSpeechGameADA(v: View?) {
        SpeechRecognizerManagement.startSpeech()
        //
        val frag = GameFragmentHear()
        val ft = supportFragmentManager.beginTransaction()
        val fragmentgotinstance =
            supportFragmentManager.findFragmentByTag(getString(R.string.gamefragmentgameada)) as GameADAFragment?
        val hearfragmentgotinstance =
            supportFragmentManager.findFragmentByTag(getString(R.string.game_fragment_hear)) as GameFragmentHear?
        if (fragmentgotinstance != null || hearfragmentgotinstance != null) {
            ft.replace(R.id.game_container, frag, getString(R.string.game_fragment_hear))
            // ok, we got the fragment instance, but should we manipulate its view?
        } else {
            ft.add(R.id.game_container, frag, getString(R.string.game_fragment_hear))
        }
        ft.addToBackStack(null)
        ft.commit()
    }

    /**
     * Called when the user taps the first page button.
     * go to the first sentence
     * @param v view of tapped button
     * @see .continueGameAda
     */
    fun firstPageGameAdaButton(v: View?) {
        if (tTS1 != null) {
            if (!tTS1!!.isSpeaking) {
                phraseToDisplayIndex = 1
                wordToDisplayIndex = 0
                continueGameAda()
            } else {
                Toast.makeText(
                    context,
                    getString(R.string.tts_sta_parlando_riprovare_pi_tardi),
                    Toast.LENGTH_SHORT
                ).show()
            }
        } else {
            phraseToDisplayIndex = 1
            wordToDisplayIndex = 0
            continueGameAda()
        }
    }

    /**
     * Called when the user taps the last page button.
     * go to the last sentence
     * @param v view of tapped button
     * @see .continueGameAda
     */
    fun lastPageGameAdaButton(v: View?) {
        // VIEW THE LAST PHRASE
        resultsStories = realm.where(Stories::class.java)
            .beginGroup()
            .equalTo(getString(R.string.story), sharedStory)
            .endGroup()
            .findAll()
        val resultsStoriesSize = (resultsStories)!!.size
        //
        resultsStories = (resultsStories)!!.sort(getString(R.string.phrasenumberint))
        //
        val resultStories = (resultsStories)!!.get(resultsStoriesSize - 1)!!
        if (tTS1 != null) {
            if (!tTS1!!.isSpeaking) {
                phraseToDisplayIndex = resultStories.phraseNumberInt
                wordToDisplayIndex = 0
                continueGameAda()
            } else {
                Toast.makeText(
                    context,
                    getString(R.string.tts_sta_parlando_riprovare_pi_tardi),
                    Toast.LENGTH_SHORT
                ).show()
            }
        } else {
            phraseToDisplayIndex = resultStories.phraseNumberInt
            wordToDisplayIndex = 0
            continueGameAda()
        }
    }

    /**
     * Called when the user taps the return button.
     * go to the previous sentence
     * @param v view of tapped button
     * @see .continueGameAda
     */
    fun returnGameAdaButton(v: View?) {
        if (tTS1 != null) {
            if (!tTS1!!.isSpeaking) {
                if (phraseToDisplayIndex > 1) {
                    phraseToDisplayIndex--
                    wordToDisplayIndex = 0
                }
                continueGameAda()
            } else {
                Toast.makeText(
                    context,
                    getString(R.string.tts_sta_parlando_riprovare_pi_tardi),
                    Toast.LENGTH_SHORT
                ).show()
            }
        } else {
            if (phraseToDisplayIndex > 1) {
                phraseToDisplayIndex--
                wordToDisplayIndex = 0
            }
            continueGameAda()
        }
    }
    //
    /**
     * Called when the user taps the continue button.
     * go to the next sentence
     * Refer to [stackoverflow](https://stackoverflow.com/questions/9702216/get-the-latest-fragment-in-backstack)
     * answer of [Deepak Goel](https://stackoverflow.com/users/628291/deepak-goel)
     * @param v view of tapped button
     * @see .continueGameAda
     */
    fun continueGameAdaButton(v: View?) {
        val fragmentBackStackIndex = supportFragmentManager.backStackEntryCount - 1
        val fragmentBackEntry = supportFragmentManager.getBackStackEntryAt(fragmentBackStackIndex)
        val fragmenttag = fragmentBackEntry.name
        if (tTS1 != null) {
            if (!tTS1!!.isSpeaking) {
                // it is necessary to increase the phraseToDisplayIndex by 1 only if a prize video is not being viewed
                if (fragmenttag != getString(R.string.prize_fragment) && fragmenttag != getString(R.string.youtube_prize_fragment)) {
                    phraseToDisplayIndex++
                    wordToDisplayIndex = 0
                }
                continueGameAda()
            } else {
                Toast.makeText(
                    context,
                    getString(R.string.tts_sta_parlando_riprovare_pi_tardi),
                    Toast.LENGTH_SHORT
                ).show()
            }
        } else {
            // it is necessary to increase the phraseToDisplayIndex by 1 only if a prize video is not being viewed
            if (fragmenttag != getString(R.string.prize_fragment) && fragmenttag != getString(R.string.youtube_prize_fragment)) {
                phraseToDisplayIndex++
                wordToDisplayIndex = 0
            }
            continueGameAda()
        }
    }
    //
    /**
     * View the next phrase.
     *
     * @see ToBeRecordedInHistory
     *
     * @see VoiceToBeRecordedInHistory
     *
     * @see com.sampietro.NaiveAAC.activities.Game.Utils.GameHelper.historyAdd
     *
     * @see .gettoBeRecordedInHistory
     *
     * @see .fragmentTransactionStart
     */
    fun continueGameAda() {
        // VIEW THE NEXT PHRASE
        resultsStories = realm.where(Stories::class.java)
            .beginGroup()
            .equalTo(getString(R.string.story), sharedStory)
            .equalTo(
                getString(R.string.phrasenumberint),
                phraseToDisplayIndex
            )
            .endGroup()
            .findAll()
        sharedPhraseSize = (resultsStories)!!.size
        //
        resultsStories = (resultsStories)!!.sort(getString(R.string.wordnumberint))
        // if there are no subsequent sentences, I start again from the first sentence
        if (sharedPhraseSize == 0) {
            phraseToDisplayIndex = 1
            resultsStories = realm.where(Stories::class.java)
                .beginGroup()
                .equalTo(getString(R.string.story), sharedStory)
                .equalTo(
                    getString(R.string.phrasenumberint),
                    phraseToDisplayIndex
                )
                .endGroup()
                .findAll()
            sharedPhraseSize = (resultsStories)!!.size
            //
            resultsStories = (resultsStories)!!.sort(getString(R.string.wordnumberint))
        }
        //
        if (sharedPhraseSize != 0) {
            val editor = sharedPref.edit()
            editor.putInt(sharedStory + getString(R.string.preference_phrasetodisplayindex), phraseToDisplayIndex)
            editor.apply()
            //
            val toBeRecordedInHistory = gettoBeRecordedInHistory()
            // REALM SESSION REGISTRATION
//            val voicesToBeRecordedInHistory: List<VoiceToBeRecordedInHistory?> =
//                toBeRecordedInHistory.getVoicesToBeRecordedInHistory()
            val voicesToBeRecordedInHistory: MutableList<VoiceToBeRecordedInHistory?> =
                toBeRecordedInHistory.getListVoicesToBeRecordedInHistory()
            //
            val debugUrlNumber = toBeRecordedInHistory.getNumberOfVoicesToBeRecordedInHistory()
            //
            historyAdd(realm, debugUrlNumber, voicesToBeRecordedInHistory)
            //
            fragmentTransactionStart()
        }
    }
    //
    /**
     * initiate Fragment transaction.
     *
     *
     *
     * @see GameADAFragment
     */
    fun fragmentTransactionStart() {
        setToFullScreen()
        val frag = GameADAFragment()
        val bundle = Bundle()
        bundle.putInt(getString(R.string.last_phrase_number), sharedLastPhraseNumber)
        bundle.putInt(getString(R.string.word_to_display_index), wordToDisplayIndex)
        bundle.putBoolean(getString(R.string.tts_enabled), ttsEnabled)
        ttsEnabled = true
        bundle.putString(getString(R.string.game_use_video_and_sound), gameUseVideoAndSound)
        frag.arguments = bundle
        val ft = supportFragmentManager.beginTransaction()
        val fragmentgotinstance =
            supportFragmentManager.findFragmentByTag(getString(R.string.gamefragmentgameada)) as GameADAFragment?
        val prizefragmentgotinstance =
            supportFragmentManager.findFragmentByTag(getString(R.string.prize_fragment)) as PrizeFragment?
        val youtubeprizefragmentgotinstance =
            supportFragmentManager.findFragmentByTag(getString(R.string.youtube_prize_fragment)) as YoutubePrizeFragment?
        val hearfragmentgotinstance =
            supportFragmentManager.findFragmentByTag(getString(R.string.gamefragmentgame1hear)) as GameFragmentHear?
        if (fragmentgotinstance != null || prizefragmentgotinstance != null || youtubeprizefragmentgotinstance != null || hearfragmentgotinstance != null) {
            ft.replace(R.id.game_container, frag, getString(R.string.gamefragmentgameada))
        } else {
            ft.add(R.id.game_container, frag, getString(R.string.gamefragmentgameada))
        }
        ft.addToBackStack(null)
        ft.commit()
    }

    /**
     * Called when the user taps a picture of the story.
     * print or switch to viewpager
     * @param view view of tapped picture
     * @param i int index of tapped picture
     * @param galleryList ArrayList<GameADAArrayList> list for choice of words
     * @see GameADAArrayList
     *
     * @see .getTargetBitmapFromUrlUsingPicasso
     *
     * @see .getTargetBitmapFromFileUsingPicasso
     *
     * @see GameADAViewPagerActivity
    </GameADAArrayList> */
    override fun onItemClick(view: View?, i: Int, galleryList: ArrayList<GameADAArrayList>) {
        if (preference_PrintPermissions == "Y") {
            if (galleryList[i].urlType == "A") {
                getTargetBitmapFromUrlUsingPicasso(galleryList[i].url, target1)
            } else {
                val f = File(galleryList[i].url!!)
                getTargetBitmapFromFileUsingPicasso(f, target1)
            }
            //
            val photoPrinter = PrintHelper(context)
            photoPrinter.scaleMode = PrintHelper.SCALE_MODE_FIT
            photoPrinter.printBitmap(context.getString(R.string.stampa_immagine1), bitmap1!!)
        } else {
            val intent: Intent?
            intent = Intent(
                this,
                GameADAViewPagerActivity::class.java
            )
            intent.putExtra(getString(R.string.story_to_display), sharedStory)
            intent.putExtra(getString(R.string.phrase_to_display), phraseToDisplayIndex)
            intent.putExtra(getString(R.string.word_to_display), i + 1)
            intent.putExtra(getString(R.string.game_use_video_and_sound), gameUseVideoAndSound)
            startActivity(intent)
            //
        }
    }
    //
    /**
     * Called on result of speech.
     *
     * @param editText string result from SpeechRecognizerManagement
     * @see com.sampietro.NaiveAAC.activities.VoiceRecognition.RecognizerCallback
     *
     * @see .checkAnswer
     */
//    @RequiresApi(api = Build.VERSION_CODES.N)
    override fun onResult(editText: String?) {
        checkAnswer(editText!!)
    }
    //
    /**
     * Called on error from SpeechRecognizerManagement.
     *
     * @param errorCode int error code from SpeechRecognizerManagement
     * @see com.sampietro.NaiveAAC.activities.VoiceRecognition.RecognizerCallback
     *
     * @see .fragmentTransactionStart
     */
    override fun onError(errorCode: Int) {
        fragmentTransactionStart()
    }
    //
    /**
     * Called on beginning of speech.
     *
     * @param eText string message from SpeechRecognizerManagement
     * @see com.sampietro.NaiveAAC.activities.VoiceRecognition.RecognizerCallback
     */
    override fun onBeginningOfSpeech(eText: String?) {}
    //
    /**
     * Called on end of speech.
     * overrides the method on GameActivityAbstractClass
     *
     * @param editText string message from SpeechRecognizerManagement
     * @see GameActivityAbstractClass
     *
     * @see com.sampietro.NaiveAAC.activities.VoiceRecognition.RecognizerCallback
     */
    override fun onEndOfSpeech(editText: String?) {}
    //
    /**
     * on callback from PrizeFragment to this Activity
     *
     *
     * @param v view inflated inside PrizeFragment
     * @see PrizeFragment
     */
    override fun receiveResultImagesFromPrizeFragment(v: View?) {
        rootViewPrizeFragment = v
    }

    /**
     * callback from PrizeFragment to this Activity on completation video
     *
     * go to the next sentence
     *
     * @param v view inflated inside PrizeFragment
     * @see .continueGameAdaButton
     *
     * @see PrizeFragment
     */
    override fun receiveResultOnCompletatioVideoFromPrizeFragment(v: View?) {
        continueGameAdaButton(v)
    }
    //
    /**
     * on callback from YoutubePrizeFragment to this Activity
     *
     *
     * @param v view inflated inside YoutubePrizeFragment
     * @see YoutubePrizeFragment
     */
    override fun receiveResultImagesFromYoutubePrizeFragment(v: View?) {}

    /**
     * callback from YoutubePrizeFragment to this Activity on completatio video
     *
     * display second level menu
     *
     * @param v view inflated inside YoutubePrizeFragment
     * @see .continueGameAdaButton
     *
     * @see YoutubePrizeFragment
     */
    override fun receiveResultOnCompletatioVideoFromYoutubePrizeFragment(v: View?) {
        continueGameAdaButton(v)
    }
    //
    /**
     * Called on result of speech.
     * check the speech results
     *
     * @param editText string result from SpeechRecognizerManagement
     * @see com.sampietro.NaiveAAC.activities.VoiceRecognition.RecognizerCallback
     *
     * @see GrammarHelper.splitString
     *
     * @see GrammarHelper.searchVerb
     *
     * @see .extemporaneousInteractionWithVoice
     *
     * @see .continueGameAda
     */
//    @RequiresApi(api = Build.VERSION_CODES.N)
    fun checkAnswer(editText: String) {
        // convert uppercase letter to lowercase
        var eText = editText
        eText = eText.lowercase(Locale.getDefault())
        // break down EditText
        val arrWords = GrammarHelper.splitString(eText)
        val arrWordsLength = arrWords.size
        //
        context = this
        sharedPref = context.getSharedPreferences(
            getString(R.string.preference_file_key), MODE_PRIVATE
        )
        val hasLastPhraseNumber = sharedPref.contains(getString(R.string.preference_last_phrase_number))
        if (hasLastPhraseNumber) {
            sharedLastPhraseNumber = sharedPref.getInt(getString(R.string.preference_last_phrase_number), 1)
        }
        // if wordNumber = 999 the word contain the answer related to the phrase
        var correspondingWord = getString(R.string.non_trovata)
        val results = realm.where(
            History::class.java
        )
            .beginGroup()
            .equalTo(getString(R.string.phrase_number), sharedLastPhraseNumber.toString())
            .and()
            .equalTo(getString(R.string.wordnumber), 999.toString())
            .endGroup()
            .findAll()
        // hystory also contains the article that I have to exclude from the comparison
        val count = results.size
        if (count != 0) {
            val result = results[0]
            if (result != null) {
                val resultHistoryCorrespondingWord = result.word
                val arrResultHistoryCorrespondingWords =
                    GrammarHelper.splitString(resultHistoryCorrespondingWord!!)
                val arrCorrespondingWordsLength = arrResultHistoryCorrespondingWords.size
                correspondingWord = if (arrCorrespondingWordsLength == 2) {
                    arrResultHistoryCorrespondingWords[1]
                } else {
                    arrResultHistoryCorrespondingWords[0]
                }
            }
        }
        // I look for the presence of the corresponding word in eText
        var theWordMatches = false
        //
        var i = 0
        while (i < arrWordsLength) {
            if (GrammarHelper.thereIsACorrespondenceWithAnAllowedMarginOfError(
                    arrWords[i],
                    correspondingWord,
                    preference_AllowedMarginOfError
                )
            ) //            if (arrWords[i].equals(correspondingWord))
            {
                theWordMatches = true
                break
            }
            // check if the word in eText is a conjugation of the corresponding word
            val verbToSearch = GrammarHelper.searchVerb(context, arrWords[i], realm)
            if (verbToSearch == correspondingWord) {
                theWordMatches = true
                break
            }
            //
            i++
        }
        // if the corresponding word is present in eText, I give the prize,
        // otherwise I answer "no, try again"
        if (!theWordMatches) {
            // treatment of impromptu responses
            //(go to the next sentence, go back to the previous sentence, go to topic)
            extemporaneousInteractionWithVoice(eText)
            continueGameAda()
        } else {
            // riattivo il bottone forward
            val forwardImageButton = findViewById<View>(R.id.continuegameadabutton) as ImageButton
            forwardImageButton.visibility = View.VISIBLE
            //
            val value999 = 999
            resultsStories = realm.where(Stories::class.java)
                .beginGroup()
                .equalTo(getString(R.string.story), sharedStory)
                .equalTo(
                    getString(R.string.phrasenumberint),
                    phraseToDisplayIndex
                )
                .equalTo(getString(R.string.wordnumberint), value999)
                .endGroup()
                .findAll()
            val answerSize = resultsStories!!.size
            if (answerSize > 0) {
                val resultStories = resultsStories!![0]!!
                val answerActionType = resultStories.answerActionType
                if (answerActionType == null) {
                    phraseToDisplayIndex++
                    wordToDisplayIndex = 0
                    // charge the activity balloon as a reward
                    // a simple balloon game
                    val intent = Intent(applicationContext, BalloonGameplayActivity::class.java)
                    // ad uso futuro
                    intent.putExtra(EXTRA_MESSAGE_BALLOON, getString(R.string.a_da))
                    startActivity(intent)
                } else {
                    val answerAction: String?
                    when (answerActionType) {
                        "V" -> {
                            // upload a video as a reward
                            answerAction = resultStories.answerAction
                            uploadAVideoAsAReward(answerAction)
                            //
                            phraseToDisplayIndex++
                            wordToDisplayIndex = 0
                        }
                        "Y" -> {
                            // upload a Youtube video as a reward
                            answerAction = resultStories.answerAction
                            uploadAYoutubeVideoAsAReward(answerAction)
                            //
                            phraseToDisplayIndex++
                            wordToDisplayIndex = 0
                        }
                        else -> {
                            phraseToDisplayIndex++
                            wordToDisplayIndex = 0
                            // charge the activity balloon as a reward
                            // a simple balloon game
                            val intent =
                                Intent(applicationContext, BalloonGameplayActivity::class.java)
                            // ad uso futuro
                            intent.putExtra(EXTRA_MESSAGE_BALLOON, getString(R.string.a_da))
                            startActivity(intent)
                        }
                    }
                }
            }
        }
    }

    /**
     * upload a video as a reward
     *
     *
     *
     * @see PrizeFragment
     */
    fun uploadAVideoAsAReward(uriPremiumVideo: String?) {
        setToFullScreen()
        // upload the award video
        val frag = PrizeFragment()
        val bundle = Bundle()
        bundle.putString(getString(R.string.award_type), "V")
        bundle.putString(getString(R.string.uri_premium_video), uriPremiumVideo)
        frag.arguments = bundle
        val ft = supportFragmentManager.beginTransaction()
        val fragmentgotinstance =
            supportFragmentManager.findFragmentByTag(getString(R.string.gamefragmentgameada)) as GameADAFragment?
        if (fragmentgotinstance != null) {
            ft.replace(R.id.game_container, frag, getString(R.string.prize_fragment))
        } else {
            ft.add(R.id.game_container, frag, getString(R.string.prize_fragment))
        }
        ft.addToBackStack(getString(R.string.prize_fragment))
        ft.commit()
    }

    /**
     * upload a Youtube video as a reward
     *
     *
     *
     * @see YoutubePrizeFragment
     */
    fun uploadAYoutubeVideoAsAReward(uriPremiumVideo: String?) {
        setToFullScreen()
        // upload a Youtube video as a reward
        val yfrag = YoutubePrizeFragment()
        val ybundle = Bundle()
        ybundle.putString(getString(R.string.award_type), "Y")
        ybundle.putString(getString(R.string.uri_premium_video), uriPremiumVideo)
        yfrag.arguments = ybundle
        val yft = supportFragmentManager.beginTransaction()
        val yfragmentgotinstance =
            supportFragmentManager.findFragmentByTag(getString(R.string.gamefragmentgameada)) as GameADAFragment?
        if (yfragmentgotinstance != null) {
            yft.replace(R.id.game_container, yfrag, getString(R.string.youtube_prize_fragment))
        } else {
            yft.add(R.id.game_container, yfrag, getString(R.string.youtube_prize_fragment))
        }
        yft.addToBackStack(getString(R.string.youtube_prize_fragment))
        yft.commit()
    }
    //
    /**
     * Called on result of speech.
     * treatment of impromptu responses
     * (go to the next sentence, go back to the previous sentence, go to topic)
     *
     * @param eText string result from SpeechRecognizerManagement
     * @see com.sampietro.NaiveAAC.activities.VoiceRecognition.RecognizerCallback
     *
     * @see GrammarHelper.splitString
     */
    fun extemporaneousInteractionWithVoice(eText: String) {
        // break down EditText
        val arrWords = GrammarHelper.splitString(eText)
        val arrWordsLength = arrWords.size
        // search for the presence in eText of the words forward (to the next sentence),
        // backwards (to the previous sentence), (go to) topic
        var i = 0
        val value999 = 999
        while (i < arrWordsLength) {
            if (arrWords[i] == "avanti") {
                // go to the next sentence only if this sentence does not contain a question
                val results: RealmResults<Stories> = realm.where(Stories::class.java)
                    .beginGroup()
                    .equalTo(getString(R.string.story), sharedStory)
                    .equalTo(
                        getString(R.string.phrasenumberint),
                        phraseToDisplayIndex
                    )
                    .equalTo(
                        getString(R.string.wordnumberint),
                        value999
                    )
                    .endGroup()
                    .findAll()
                val count = results.size
                if (count == 0) {
                    phraseToDisplayIndex++
                }
                break
            }
            if (arrWords[i] == "indietro") {
                if (phraseToDisplayIndex > 1) {
                    phraseToDisplayIndex--
                }
                break
            }
            //
            i++
        }
        // check if a story topic is in eText
        // (if wordNumber = 9999 the word contain the topic of the sentence)
        val value9999 = 9999
        val results: RealmResults<Stories> = realm.where(Stories::class.java)
            .beginGroup()
            .equalTo(getString(R.string.story), sharedStory)
            .equalTo(getString(R.string.wordnumberint), value9999)
            .endGroup()
            .findAll()
        val count = results.size
        if (count != 0) {
            i = 0
            while (i < count) {
                val result = results[i]!!
                // if the argument is in eText then that is the sentence to display
                if (eText.lowercase(Locale.getDefault())
                        .contains(result.word!!.lowercase(Locale.getDefault()))
                ) {
                    phraseToDisplayIndex = result.phraseNumberInt
                    break
                }
                //
                i++
            }
        }
    }

    /**
     * prepares the list of items to be registered on History
     *
     * @return ToBeRecordedInHistory<VoiceToBeRecordedInHistory> list of items to be registered on History
     * @see VoiceToBeRecordedInHistory
     *
     * @see ToBeRecordedInHistory
     *
     * @see ImageSearchHelper.imageSearch
    </VoiceToBeRecordedInHistory> */
    fun gettoBeRecordedInHistory(): ToBeRecordedInHistoryImpl {
        // initializes the list of items to be registered on History
        val toBeRecordedInHistory: ToBeRecordedInHistoryImpl
        toBeRecordedInHistory = ToBeRecordedInHistoryImpl()
        // adds the first entry to be recorded on History to the list
        sharedPref = context.getSharedPreferences(
            getString(R.string.preference_file_key), MODE_PRIVATE
        )
        sharedLastSession = sharedPref.getInt(getString(R.string.preference_LastSession), 1)
        //
        val editor = sharedPref.edit()
        val hasLastPhraseNumber = sharedPref.contains(getString(R.string.preference_last_phrase_number))
        sharedLastPhraseNumber = if (!hasLastPhraseNumber) {
            // is the first recorded sentence and LastPhraseNumber log on sharedpref
            // with the number 1
            1
        } else {
            // it is not the first sentence recorded and I add 1 to LastPhraseNumber on sharedprefs
            sharedPref.getInt(getString(R.string.preference_last_phrase_number), 1) + 1
        }
        editor.putInt(getString(R.string.preference_last_phrase_number), sharedLastPhraseNumber)
        editor.apply()
        //
        val currentTime = Calendar.getInstance().time
        // SEARCH THE IMAGES OF THE WORDS
        var i = 0
        while (i < sharedPhraseSize) {
            phraseToDisplay = resultsStories!![i]
            assert(phraseToDisplay != null)
            val phraseToDisplayWordNumber = phraseToDisplay!!.wordNumberInt
            when (phraseToDisplayWordNumber) {
                0 -> {
                    voiceToBeRecordedInHistory = VoiceToBeRecordedInHistory(
                        sharedLastSession,
                        sharedLastPhraseNumber, currentTime,
                        0, " ", phraseToDisplay!!.word!!,
                        " ", " ", " ", " ", phraseToDisplay!!.sound,
                        " "
                    )
                    toBeRecordedInHistory.add(voiceToBeRecordedInHistory)
                }
                99 -> {
                    voiceToBeRecordedInHistory = VoiceToBeRecordedInHistory(
                        sharedLastSession,
                        sharedLastPhraseNumber, currentTime,
                        99, " ", phraseToDisplay!!.word!!,
                        " ", " ", " "
                    )
                    toBeRecordedInHistory.add(voiceToBeRecordedInHistory)
                }
                999 -> {
                    voiceToBeRecordedInHistory = VoiceToBeRecordedInHistory(
                        sharedLastSession,
                        sharedLastPhraseNumber, currentTime,
                        999, " ", phraseToDisplay!!.word!!,
                        " ", " ", " "
                    )
                    toBeRecordedInHistory.add(voiceToBeRecordedInHistory)
                }
                9999 -> {
                    voiceToBeRecordedInHistory = VoiceToBeRecordedInHistory(
                        sharedLastSession,
                        sharedLastPhraseNumber, currentTime,
                        9999, " ", phraseToDisplay!!.word!!,
                        " ", " ", " "
                    )
                    toBeRecordedInHistory.add(voiceToBeRecordedInHistory)
                }
                else -> {
                    // image search
                    var image: ResponseImageSearch?
                    val phraseToDisplayWord = phraseToDisplay!!.word
                    val phraseToDisplayUriType = phraseToDisplay!!.uriType
                    if (phraseToDisplayUriType != null) {
                        if (phraseToDisplayUriType == " " || phraseToDisplayUriType == "") {
                            image = ImageSearchHelper.imageSearch(context, realm, phraseToDisplayWord)
                            if (image != null) {
                                voiceToBeRecordedInHistory = VoiceToBeRecordedInHistory(
                                    sharedLastSession,
                                    sharedLastPhraseNumber, currentTime,
                                    1, " ", phraseToDisplayWord!!,
                                    " ", image.uriType, image.uriToSearch,
                                    phraseToDisplay!!.video, phraseToDisplay!!.sound,
                                    phraseToDisplay!!.soundReplacesTTS
                                )
                                toBeRecordedInHistory.add(voiceToBeRecordedInHistory)
                            }
                        } else {
                            voiceToBeRecordedInHistory = VoiceToBeRecordedInHistory(
                                sharedLastSession,
                                sharedLastPhraseNumber, currentTime,
                                1, " ", phraseToDisplayWord!!,
                                " ", phraseToDisplayUriType,
                                phraseToDisplay!!.uri!!,
                                phraseToDisplay!!.video, phraseToDisplay!!.sound,
                                phraseToDisplay!!.soundReplacesTTS
                            )
                            toBeRecordedInHistory.add(voiceToBeRecordedInHistory)
                        }
                    }
                }
            }
            //
            i++
        }
        return toBeRecordedInHistory
    }

    /**
     * on callback from GameFragment to this Activity
     *
     * @param v view root fragment view
     * @param t TextToSpeech
     * @param gL ArrayList<GameADAArrayList>
    </GameADAArrayList> */
    override fun receiveResultGameFragment(
        v: View?,
        t: TextToSpeech?,
        gL: ArrayList<GameADAArrayList>
    ) {
        rootViewImageFragment = v
        tTS1 = t
        galleryList = gL
    }
    //

    companion object {
        const val EXTRA_MESSAGE_BALLOON = "GameJavaClass"

        /**
         * return if a string is numeric.
         * Refer to [stackoverflow](https://stackoverflow.com/questions/1102891/how-to-check-if-a-string-is-numeric-in-java)
         * answer of [CraigTP](https://stackoverflow.com/users/57477/craigtp)
         * Refer to [Medium](https://betulnecanli.medium.com/regular-expressions-regex-in-kotlin-a2eaeb2cd113)
         * answer of [Betul Necanli](https://betulnecanli.medium.com/)
         *
         *
         * @param str string to verify
         * @return boolean true if the string is numeric
         */
        fun isNumeric(str: String): Boolean {
//            return str.matches("-?\\d+(\\.\\d+)?") //match a number with optional '-' and decimal.
            return Regex("-?\\d+(\\.\\d+)?").matches(str)
        }
    }
}