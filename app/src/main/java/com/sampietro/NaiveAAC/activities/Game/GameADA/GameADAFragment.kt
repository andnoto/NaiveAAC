package com.sampietro.NaiveAAC.activities.Game.GameADA

import android.app.Activity
import android.content.Context
import android.media.MediaPlayer
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.view.View
import android.widget.ImageButton
import android.widget.Toast
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.LinearLayoutManager
import com.sampietro.NaiveAAC.R
import com.sampietro.NaiveAAC.activities.BaseAndAbstractClass.GameFragmentAbstractClass
import com.sampietro.NaiveAAC.activities.Graphics.Sounds
import com.sampietro.NaiveAAC.activities.Graphics.Videos
import com.sampietro.NaiveAAC.activities.history.History
import io.realm.Realm
import java.io.IOException
import java.util.*

class GameADAFragment(@LayoutRes contentLayoutId : Int = 0) : GameFragmentAbstractClass(contentLayoutId) {
    //
    var sharedLastPhraseNumber = 0

    //
    var wordToDisplayIndex = 0

    //
    var row1debugWord = arrayOfNulls<String>(32)
    var row1debugUrlType = arrayOfNulls<String>(32)
    var row1debugUrl = arrayOfNulls<String>(32)

    //
    var row1debugVideo = arrayOfNulls<String>(32)
    var row1debugSound = arrayOfNulls<String>(32)
    var row1debugSoundReplacesTTS = arrayOfNulls<String>(32)

    //
    var sentence: String? = null

    //
    var listenerADA: GameADAOnFragmentEventListener? = null

    //
    var ttsEnabled = true

    //
    private var recyclerView: GameADARecyclerView? = null

    //
    private var soundMediaPlayer: MediaPlayer? = null
    private var soundAssociatedWithThePhraseReplacesTheOtherSounds: String? = null

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
        val activity = context as Activity
        listenerADA = activity as GameADAOnFragmentEventListener
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
    }

    /**
     * prepares the ui and makes the callback to the activity
     *
     *
     * Refer to [androidauthority](https://www.androidauthority.com/how-to-build-an-image-gallery-app-718976/)
     * by [Adam Sinicki](https://www.androidauthority.com/author/adamsinicki/)
     *
     * @see androidx.fragment.app.Fragment.onViewCreated
     *
     * @see prepareData1
     *
     * @see GameADARecyclerViewAdapter
     */
    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        val bundle = this.arguments
        sharedLastPhraseNumber = 0
        if (bundle != null) {
            sharedLastPhraseNumber = bundle.getInt("LAST PHRASE NUMBER")
            wordToDisplayIndex = bundle.getInt("WORD TO DISPLAY INDEX")
            ttsEnabled = bundle.getBoolean("TTS ENABLED")
            gameUseVideoAndSound = bundle.getString("GAME USE VIDEO AND SOUND")
        }
        //
        /*
        ADAPTED FOR VIDEO AND SOUND
         */
        recyclerView =
            view.findViewById<View>(R.id.game_ada_recycler_view) as GameADARecyclerView
        recyclerView!!.setHasFixedSize(true)
        val layoutManager = LinearLayoutManager(ctext, LinearLayoutManager.HORIZONTAL, false)
        recyclerView!!.layoutManager = layoutManager
        val createLists = prepareData1()
        recyclerView!!.setMediaObjectsGameADA(createLists)
        recyclerView!!.setGameUseVideoAndSound(gameUseVideoAndSound)
        val adapter = GameADARecyclerViewAdapter(ctext, realm, createLists)
        recyclerView!!.adapter = adapter
        //
        recyclerView!!.smoothScrollBy(1, 0)
        //
        if (wordToDisplayIndex != 0) recyclerView!!.layoutManager!!.scrollToPosition(
            wordToDisplayIndex
        )
        /*

         */
        // TTS
        if (ttsEnabled) {
            tTS1 = TextToSpeech(ctext) { status ->
                if (status != TextToSpeech.ERROR) {
                    if (!tTS1!!.isSpeaking) {
                        tTS1!!.speak(sentence, TextToSpeech.QUEUE_FLUSH, null, null)
                    } else {
                        Toast.makeText(ctext, "is speaking", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(ctext, status, Toast.LENGTH_SHORT).show()
                }
            }
        }
        //
        listenerADA!!.receiveResultGameFragment(view, tTS1, createLists)
    }
    //
    /**
     * prepare data for the recyclerview using data from the history table
     *
     * @return theimage arraylist<Game2ArrayList> data for recyclerview
     * @see GameADAArrayList
     *
     * @see History
    */
    private fun prepareData1(): ArrayList<GameADAArrayList> {
        var row1debugUrlNumber = 0
        //
        val results = realm.where(
            History::class.java
        ).equalTo(getString(R.string.phrase_number), sharedLastPhraseNumber.toString()).findAll()
        val count = results.size
        if (count != 0) {
            var irrh = 0
            while (irrh < count && irrh < 33) {
                val result = results[irrh]!!
                val wordNumber = Objects.requireNonNull(result.wordNumber)!!.toInt()
                //
                if (wordNumber != 0 && wordNumber != 99
                    && wordNumber != 999 && wordNumber != 9999
                ) {
                    if (preference_TitleWritingType == "uppercase") row1debugWord[irrh - 1] =
                        result.word!!.uppercase(
                            Locale.getDefault()
                        ) else row1debugWord[irrh - 1] = result.word!!.lowercase(
                        Locale.getDefault()
                    )
                    //
                    row1debugUrlType[irrh - 1] = result.uriType
                    row1debugUrl[irrh - 1] = result.uri
                    //
                    val resultsVideos =
                        realm.where(Videos::class.java).equalTo("descrizione", result.video)
                            .findAll()
                    if (resultsVideos.size != 0) {
                        row1debugVideo[irrh - 1] = resultsVideos[0]!!.uri
                    }
                    val resultsSounds =
                        realm.where(Sounds::class.java).equalTo("descrizione", result.sound)
                            .findAll()
                    if (resultsSounds.size != 0) {
                        row1debugSound[irrh - 1] = resultsSounds[0]!!.uri
                        row1debugSoundReplacesTTS[irrh - 1] = result.soundReplacesTTS
                    }
                    //
                    row1debugUrlNumber++
                }
                if (wordNumber == 0) {
                    sentence = result.word
                    //
                    soundAssociatedWithThePhraseReplacesTheOtherSounds = "N"
                    if (soundMediaPlayer != null) {
                        if (soundMediaPlayer!!.isPlaying) {
                            soundMediaPlayer!!.stop()
                        }
                        soundMediaPlayer!!.release()
                        soundMediaPlayer = null
                    }
                    //
                    var soundPath = "non trovato"
                    val resultsSounds =
                        realm.where(Sounds::class.java).equalTo("descrizione", result.sound)
                            .findAll()
                    if (resultsSounds.size != 0) {
                        soundPath = resultsSounds[0]!!.uri!!
                    }
                    if (gameUseVideoAndSound == "Y" && soundPath != "non trovato"
                    ) {
                        soundAssociatedWithThePhraseReplacesTheOtherSounds = "Y"
                        // play audio file using MediaPlayer
                        try {
                            soundMediaPlayer = MediaPlayer()
                            soundMediaPlayer!!.setDataSource(soundPath)
                            soundMediaPlayer!!.prepare()
                            soundMediaPlayer!!.start()
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }
                // nel caso di domanda con attesa risposta
                // inibisco il bottone forward
                if (wordNumber == 99) {
                    val forwardImageButton =
                        requireActivity().findViewById<View>(R.id.continuegameadabutton) as ImageButton
                    //                    ImageButton forwardImageButton =
//                    (ImageButton)rootView.findViewById(R.id.continuegameadabutton);
                    forwardImageButton.visibility = View.INVISIBLE
                }
                irrh++
            }
        }
        //
        val theimage = ArrayList<GameADAArrayList>()
        for (i in 0 until row1debugUrlNumber) {
            val createList = GameADAArrayList()
            createList.image_title = row1debugWord[i]
            createList.urlType = row1debugUrlType[i]
            createList.url = row1debugUrl[i]
            createList.video = row1debugVideo[i]
            createList.sound = row1debugSound[i]
            createList.soundReplacesTTS = row1debugSoundReplacesTTS[i]
            createList.soundAssociatedWithThePhraseReplacesTheOtherSounds =
                soundAssociatedWithThePhraseReplacesTheOtherSounds
            theimage.add(createList)
        }
        return theimage
    }
    //
    /**
     * release the video player
     *
     * @see androidx.fragment.app.Fragment.onStop
     *
     * @see GameADARecyclerView.releasePlayer
     */
    override fun onStop() {
        /*
        ADAPTED FOR VIDEO AND SOUND
         */
        if (soundMediaPlayer != null) {
            if (soundMediaPlayer!!.isPlaying) {
                soundMediaPlayer!!.stop()
            }
            soundMediaPlayer!!.release()
            soundMediaPlayer = null
        }
        //
        if (recyclerView != null) recyclerView!!.releasePlayer()
        super.onStop()
        /*

         */
    } //
}