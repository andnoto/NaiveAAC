package com.sampietro.NaiveAAC.activities.Game.Balloon

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.sampietro.NaiveAAC.R
import com.sampietro.NaiveAAC.activities.Game.Balloon.Balloon.BalloonListener
import com.sampietro.NaiveAAC.activities.Game.Balloon.Utils.SoundHelper
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 * <h1>GamePlayActivity</h1>
 *
 *
 * **GamePlayActivity** represent gameplay screen and handle all game logic.
 * Refer to [github.com](https://github.com/Vukan-Markovic/Pop_Balloon)
 * By [Vukan Marković](https://github.com/Vukan-Markovic)
 *
 * @version     4.0, 09/09/2023
 * @see Balloon.BalloonListener
 */
class BalloonGameplayActivity : AppCompatActivity(), BalloonListener {
    private val mRandom = Random()
    private val mBalloonColors = intArrayOf(
        Color.YELLOW,
        Color.RED,
        Color.WHITE,
        Color.MAGENTA,
        Color.GREEN,
        Color.CYAN,
        Color.BLUE
    )
    private val mBalloonsPerLevel = 30
    private var mBalloonsPopped = 0
    private var mScreenWidth = 0
    private var mScreenHeight = 0
    private var mLevel = 0
    private var mScore = 0
    private var mHeartsUsed = 0
    private var mPlaying = false
    private var mGame = false
    private var mGameStopped = true

    //
    lateinit var mContentView: ViewGroup
    private lateinit var mSoundHelper: SoundHelper
    private lateinit var mMusicHelper: SoundHelper
    private val mHeartImages: MutableList<ImageView> = ArrayList()
    private val mBalloons: MutableList<Balloon> = ArrayList()
    lateinit private var mAnimation: Animation

    //
    private var balloonsLaunched = 0

    /**
     * This method is responsible for configurations of gameplay screen.
     *
     * @param savedInstanceState Define potentially saved parameters due to configurations changes.
     * @see setToFullScreen
     *
     * @see SoundHelper
     *
     * @see gameOver
     * @see startGame
     */
    @SuppressLint("FindViewByIdCast")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_balloon_gameplay)
        //
        if (supportActionBar != null) {
            supportActionBar!!.hide()
        }
//        window.setBackgroundDrawableResource(R.drawable.balloon_background)
        /*
        USED FOR FULL SCREEN
        */
        mContentView = findViewById(R.id.activity_game_balloon_gameplay_id)
        setToFullScreen()
        val viewTreeObserver = mContentView.getViewTreeObserver()
        //
        if (viewTreeObserver.isAlive) {
            viewTreeObserver.addOnGlobalLayoutListener(object : OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    mContentView.getViewTreeObserver().removeOnGlobalLayoutListener(this)
                    mScreenWidth = mContentView.getWidth()
                    mScreenHeight = mContentView.getHeight()
                }
            })
        }
        //
        mContentView.setOnClickListener({ view: View? -> setToFullScreen() })
        /*

        */
        window.setBackgroundDrawableResource(R.drawable.balloon_background)
        //
        mMusicHelper = SoundHelper(this)
        mMusicHelper.prepareMusicPlayer(this)
        //        Intent intent = getIntent();
        mHeartImages.add(findViewById(R.id.heart1))
        mHeartImages.add(findViewById(R.id.heart2))
        mHeartImages.add(findViewById(R.id.heart3))
        mHeartImages.add(findViewById(R.id.heart4))
        mHeartImages.add(findViewById(R.id.heart5))
        //
        mSoundHelper = SoundHelper(this)
        mSoundHelper.prepareMusicPlayer(this)
        mAnimation = AnimationUtils.loadAnimation(applicationContext, R.anim.balloon_fade)
        mAnimation.setDuration(100)
        //
        findViewById<View>(R.id.btn_back_gameplay).setOnClickListener { view: View ->
            view.startAnimation(mAnimation)
            gameOver()
        }
        //
        val TIME_OUT = 1000
        Handler(Looper.getMainLooper()).postDelayed({ startGame()  }, TIME_OUT.toLong())
//        Handler().postDelayed({ startGame() }, TIME_OUT.toLong())
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
    }

    /**
     * This method is responsible for calling method setToFullScreen().
     *
     * @see androidx.appcompat.app.AppCompatActivity.onResume
     * @see BalloonGameplayActivity.setToFullScreen
     */
    override fun onResume() {
        super.onResume()
        setToFullScreen()
    }

    /**
     * This method is responsible to continue play music when user back to the game.
     *
     * @see androidx.appcompat.app.AppCompatActivity.onRestart
     * @see SoundHelper.playMusic
     */
    override fun onRestart() {
        super.onRestart()
        if (mGame) {
            mMusicHelper.playMusic()
        }
    }

    /**
     * This method is responsible to start game and set beginning game parameters.
     *
     * @see BalloonGameplayActivity.setToFullScreen
     * @see SoundHelper.playMusic
     * @see BalloonGameplayActivity.startLevel
     */
    private fun startGame() {
        setToFullScreen()
        mScore = 0
        mLevel = 0
        mHeartsUsed = 0
        mGameStopped = false
        mGame = true
        mMusicHelper.playMusic()
        for (pin in mHeartImages) pin.setImageResource(R.drawable.heart)
        startLevel()
    }

    /**
     * This method is responsible to start the level.
     * Refer to [stackoverflow](https://stackoverflow.com/questions/58767733/the-asynctask-api-is-deprecated-in-android-11-what-are-the-alternatives)
     * answer of [Kashfa Khan](https://stackoverflow.com/users/6277444/kashfa-khan)
     *
     * @see ExecuteBalloonLauncher
     */
    private fun startLevel() {
        mLevel++
        //
//        BalloonLauncher().execute(mLevel)
        //
        val executor: ExecutorService = Executors.newSingleThreadExecutor()
        executor.execute({
            //Background work here
            ExecuteBalloonLauncher(mLevel)
        })
        //
        mPlaying = true
        mBalloonsPopped = 0
        //
    }
//
    /**
     * This method is executing in background and calculate speed and position of balloons depends on game level.
     * With increasing level, speed of balloons is increasing too.
     *
     * @param mLevel represent current level
     * @see launchBalloon
     * @see Thread.sleep
     *
     */
    private fun ExecuteBalloonLauncher(mLevel: Int?) {
        val minDelay =
            Math.max(MIN_ANIMATION_DELAY, MAX_ANIMATION_DELAY - (mLevel!! - 1) * 500) / 2
        balloonsLaunched = 0
        while (mPlaying && balloonsLaunched < mBalloonsPerLevel) {
            val random = Random(Date().time)
            val handler = Handler(Looper.getMainLooper())
            handler.post {
                //UI Thread work here
                launchBalloon(random.nextInt(mScreenWidth - 200))
            }
            balloonsLaunched++
            try {
                Thread.sleep((random.nextInt(minDelay) + minDelay).toLong())
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }
    }
//
    /**
     * This method is called when balloon is popped, either by tapping by user or by going away.
     *
     * @param balloon   represent balloon object which is popped.
     * @param userTouch indicate if user popped balloon or balloon is going away.
     * @see Balloon
     *
     * @see gameOver
     */
    override fun popBalloon(balloon: Balloon?, userTouch: Boolean) {
        mBalloonsPopped++
        if (mPlaying) mSoundHelper.playSound()
        mContentView.removeView(balloon)
        mBalloons.remove(balloon)
        if (userTouch) mScore++ else {
            mHeartsUsed++
            if (mHeartsUsed <= mHeartImages.size) mHeartImages[mHeartsUsed - 1].setImageResource(R.drawable.broken_heart)
            if (mHeartsUsed == NUMBER_OF_HEARTS) {
                gameOver()
                return
            }
        }
        if (balloonsLaunched == mBalloonsPerLevel) {
            gameOver()
            return
        }
    }

    /**
     * This method finish the game.
     */
    private fun gameOver() {
        mPlaying = false
        finish()
    }

    /**
     * This method add new balloon to the screen.
     *
     * @param x represent x axis of the balloon.
     * @see Balloon
     *
     * @see ViewGroup.addView
     */
    private fun launchBalloon(x: Int) {
        val balloon = Balloon(this, mBalloonColors[mRandom.nextInt(mBalloonColors.size)], 150)
        mBalloons.add(balloon)
        balloon.x = x.toFloat()
        balloon.y = (mScreenHeight + balloon.height).toFloat()
        mContentView.addView(balloon)
        balloon.releaseBalloon(
            mScreenHeight,
            Math.max(MIN_ANIMATION_DURATION, MAX_ANIMATION_DURATION - mLevel * 5000)
        )
    }

    /**
     * This method is responsible to pause music if user leave game during gameplay.
     *
     * @see androidx.appcompat.app.AppCompatActivity.onPause
     * @see SoundHelper.pauseMusic
     */
    override fun onPause() {
        super.onPause()
        if (mGame) {
            mMusicHelper.pauseMusic()
        }
    }

    companion object {
        private const val MIN_ANIMATION_DELAY = 500
        private const val MAX_ANIMATION_DELAY = 1500
        private const val MIN_ANIMATION_DURATION = 1000
        private const val MAX_ANIMATION_DURATION = 8000
        private const val NUMBER_OF_HEARTS = 5
    }
}