package com.sampietro.NaiveAAC.activities.Game.Balloon;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.sampietro.NaiveAAC.R;
import com.sampietro.NaiveAAC.activities.Game.Balloon.Utils.SoundHelper;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * <h1>GamePlayActivity</h1>
 *
 * <p><b>GamePlayActivity</b> represent gameplay screen and handle all game logic.</p>
 * Refer to <a href="https://github.com/Vukan-Markovic/Pop_Balloon">github.com</a>
 * By <a href="https://github.com/Vukan-Markovic">Vukan Marković</a>
 *
 * @version     1.3, 05/05/22
 * @see Balloon.BalloonListener
 */
public class BalloonGameplayActivity extends AppCompatActivity implements Balloon.BalloonListener {
    private static final int MIN_ANIMATION_DELAY = 500, MAX_ANIMATION_DELAY = 1500, MIN_ANIMATION_DURATION = 1000, MAX_ANIMATION_DURATION = 8000, NUMBER_OF_HEARTS = 5;
    private final Random mRandom = new Random();
    private final int[] mBalloonColors = {Color.YELLOW, Color.RED, Color.WHITE, Color.MAGENTA, Color.GREEN, Color.CYAN, Color.BLUE};
    private int mBalloonsPerLevel = 30, mBalloonsPopped, mScreenWidth, mScreenHeight, mLevel, mScore, mHeartsUsed;
    private boolean mPlaying, mSound, mMusic, mGame, mGameStopped = true;
    //
    private ViewGroup mContentView;
    private SoundHelper mSoundHelper, mMusicHelper;
    private final List<ImageView> mHeartImages = new ArrayList<>();
    private final List<Balloon> mBalloons = new ArrayList<>();
    private Animation mAnimation;
    //
    private int balloonsLaunched;
    /**
     * This method is responsible for configurations of gameplay screen.
     *
     * @param savedInstanceState Define potentially saved parameters due to configurations changes.
     * @see Activity#onCreate(Bundle)
     * @see #setToFullScreen
     * @see SoundHelper
     * @see #gameOver()
     * @see #startGame
     */
    @SuppressLint("FindViewByIdCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_balloon_gameplay);
        getWindow().setBackgroundDrawableResource(R.drawable.balloon_background);
        mContentView = findViewById(R.id.activity_game_balloon_gameplay_id);
        setToFullScreen();
        ViewTreeObserver viewTreeObserver = mContentView.getViewTreeObserver();
        mMusicHelper = new SoundHelper(this);
        mMusicHelper.prepareMusicPlayer(this);
//        Intent intent = getIntent();
        //
        if (viewTreeObserver.isAlive()) {
            viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    mContentView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    mScreenWidth = mContentView.getWidth();
                    mScreenHeight = mContentView.getHeight();
                }
            });
        }
        //
        mContentView.setOnClickListener(view -> setToFullScreen());
        mHeartImages.add(findViewById(R.id.heart1));
        mHeartImages.add(findViewById(R.id.heart2));
        mHeartImages.add(findViewById(R.id.heart3));
        mHeartImages.add(findViewById(R.id.heart4));
        mHeartImages.add(findViewById(R.id.heart5));
        //
        mSoundHelper = new SoundHelper(this);
        mSoundHelper.prepareMusicPlayer(this);
        mAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.balloon_fade);
        mAnimation.setDuration(100);
        //
        findViewById(R.id.btn_back_gameplay).setOnClickListener(view -> {
            view.startAnimation(mAnimation);
            gameOver();
        //
        });
        //
        int TIME_OUT = 500;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startGame();
            }
        }, TIME_OUT);
    }
    /**
     * This method is responsible to transfer MainActivity into fullscreen mode.
     */
    private void setToFullScreen() {
        findViewById(R.id.activity_game_balloon_gameplay_id).setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }
    /**
     * This method is responsible for calling method setToFullScreen().
     *
     * @see Activity#onResume()
     * @see BalloonGameplayActivity#setToFullScreen()
     */
    @Override
    protected void onResume() {
        super.onResume();
        setToFullScreen();
    }
    /**
     * This method is responsible to continue play music when user back to the game.
     *
     * @see Activity#onRestart()
     * @see SoundHelper#playMusic()
     */
    @Override
    protected void onRestart() {
        super.onRestart();
        if (mGame) {
            mMusicHelper.playMusic();
        }
    }
    /**
     * This method is responsible to start game and set beginning game parameters.
     *
     * @see BalloonGameplayActivity#setToFullScreen()
     * @see SoundHelper#playMusic()
     * @see BalloonGameplayActivity#startLevel()
     */
    private void startGame() {
        setToFullScreen();
        mScore = 0;
        mLevel = 0;
        mHeartsUsed = 0;
        mGameStopped = false;
        mGame = true;
        mMusicHelper.playMusic();
        for (ImageView pin : mHeartImages) pin.setImageResource(R.drawable.heart);
        startLevel();
    }
    /**
     * This method is responsible to start the level.
     *
     * @see BalloonLauncher
     */
    private void startLevel() {
        mLevel++;
//
        new BalloonLauncher().execute(mLevel);
        mPlaying = true;
        mBalloonsPopped = 0;
//
    }
    /**
     * This method is called when balloon is popped, either by tapping by user or by going away.
     *
     * @param balloon   represent balloon object which is popped.
     * @param userTouch indicate if user popped balloon or balloon is going away.
     * @see Balloon
     * @see #gameOver()
     */
    @Override
    public void popBalloon(Balloon balloon, boolean userTouch) {
        mBalloonsPopped++;
        if (mPlaying)
            mSoundHelper.playSound();
        mContentView.removeView(balloon);
        mBalloons.remove(balloon);
        if (userTouch) mScore++;
        else {
            mHeartsUsed++;
            if (mHeartsUsed <= mHeartImages.size())
                mHeartImages.get(mHeartsUsed - 1).setImageResource(R.drawable.broken_heart);
            if (mHeartsUsed == NUMBER_OF_HEARTS) {
                gameOver();
                return;
            }
        }
        if (balloonsLaunched == mBalloonsPerLevel) {
            gameOver();
            return;
        }
    }
    /**
     * This method finish the game.
     */
    private void gameOver() {
        mPlaying = false;
        finish();
    }
    /**
     * This method add new balloon to the screen.
     *
     * @param x represent x axis of the balloon.
     * @see Balloon
     * @see ViewGroup#addView(View)
     */
    private void launchBalloon(int x) {
        Balloon balloon = new Balloon(this, mBalloonColors[mRandom.nextInt(mBalloonColors.length)], 150);
        mBalloons.add(balloon);
        balloon.setX(x);
        balloon.setY(mScreenHeight + balloon.getHeight());
        mContentView.addView(balloon);
        balloon.releaseBalloon(mScreenHeight, Math.max(MIN_ANIMATION_DURATION, MAX_ANIMATION_DURATION - (mLevel * 5000)));
    }
    /**
     * This method is responsible to pause music if user leave game during gameplay.
     *
     * @see Activity#onPause()
     * @see SoundHelper#pauseMusic()
     */
    @Override
    protected void onPause() {
        super.onPause();
        if (mGame) {
            mMusicHelper.pauseMusic();
        }
    }
    /**
     * This class is responsible for calculating speed of balloons and x axis position of the balloon
     *
     * @see AsyncTask
     */
    @SuppressLint("StaticFieldLeak")
    private class BalloonLauncher extends AsyncTask<Integer, Integer, Void> {
        /**
         * This method is executing in background and calculate speed and position of balloons depends on game level.
         * With increasing level, speed of balloons is increasing too.
         *
         * @param params represent current level
         * @see AsyncTask#doInBackground(Object[])
         * @see AsyncTask#publishProgress(Object[])
         * @see Thread#sleep(long)
         */
        @Nullable
        @Override
        protected Void doInBackground(@NonNull Integer... params) {
            if (params.length != 1) throw new AssertionError(getString(R.string.assertion_message));
            int minDelay = Math.max(MIN_ANIMATION_DELAY, (MAX_ANIMATION_DELAY - ((params[0] - 1) * 500))) / 2;
            balloonsLaunched = 0;

            while (mPlaying && balloonsLaunched < mBalloonsPerLevel) {
                Random random = new Random(new Date().getTime());
                publishProgress(random.nextInt(mScreenWidth - 200));
                balloonsLaunched++;

                try {
                    Thread.sleep(random.nextInt(minDelay) + minDelay);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }
        /**
         * This method update UI, calling launchBalloon() method.
         *
         * @param values represent calculated x axis of balloon
         * @see BalloonGameplayActivity#launchBalloon(int)
         * @see AsyncTask#onProgressUpdate(Object[])
         */
        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            launchBalloon(values[0]);
        }
    }
}
