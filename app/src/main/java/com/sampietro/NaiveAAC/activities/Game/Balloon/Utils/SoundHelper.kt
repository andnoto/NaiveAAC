package com.sampietro.NaiveAAC.activities.Game.Balloon.Utils

import android.content.Context
import android.media.AudioAttributes
import androidx.appcompat.app.AppCompatActivity
import android.media.MediaPlayer
import android.media.SoundPool
import android.media.AudioManager
import com.sampietro.NaiveAAC.R

/**
 * <h1>SoundHelper</h1>
 * *
 *
 ***SoundHelper** class is responsible for reproducing game sound and music.
 *
 * @version     4.0, 09/09/2023
 */
class SoundHelper(activity: AppCompatActivity) {
    lateinit private var mMusicPlayer: MediaPlayer
    private var mSoundPool: SoundPool? = null
    private val mSoundID: Int
    private val mVolume: Float
    private var mLoaded = false

    /**
     * SoundHelper constructor.
     * init RecyclerView
     */
    init {
        val audioManager = activity.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        mVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC).toFloat() / audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
            .toFloat()
        activity.volumeControlStream = AudioManager.STREAM_MUSIC
        //
//        mSoundPool = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        val audioAttributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()
        mSoundPool = SoundPool.Builder().setAudioAttributes(audioAttributes).setMaxStreams(6).build()
//        } else {
            //SoundPool(int maxStreams, int streamType, int srcQuality Currently has no effect )
//            SoundPool(6, AudioManager.STREAM_MUSIC, 0)
//        }
        //
        mSoundPool!!.setOnLoadCompleteListener { soundPool: SoundPool?, sampleId: Int, status: Int ->
            mLoaded = true
        }
        mSoundID = mSoundPool!!.load(activity, R.raw.balloon_pop, 1)
    }

    /**
     * This method is responsible to play game sound.
     *
     * @see SoundPool.play
     */
    fun playSound() {
        if (mLoaded) mSoundPool!!.play(mSoundID, mVolume, mVolume, 1, 0, 1f)
    }

    /**
     * This method is responsible for preparation of music player which is instance of MediaPlayer class.
     *
     * @param context Context of activity which call this method.
     * @see MediaPlayer
     */
    fun prepareMusicPlayer(context: Context) {
        mMusicPlayer = MediaPlayer.create(context.applicationContext, R.raw.balloon_game_music)
        mMusicPlayer.setVolume(.5f, .5f)
        mMusicPlayer.setLooping(true)
    }

    /**
     * This method is responsible for start playing game music.
     *
     * @see MediaPlayer.start
     */
    fun playMusic() {
        mMusicPlayer.start()
    }

    /**
     * This method is responsible for pausing game music.
     *
     * @see MediaPlayer.isPlaying
     * @see MediaPlayer.pause
     */
    fun pauseMusic() {
        if (mMusicPlayer.isPlaying) mMusicPlayer.pause()
    }
}