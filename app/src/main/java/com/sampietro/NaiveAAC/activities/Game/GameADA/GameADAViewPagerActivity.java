package com.sampietro.NaiveAAC.activities.Game.GameADA;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.widget.ViewPager2;
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback;

import com.sampietro.NaiveAAC.R;
import com.sampietro.NaiveAAC.activities.Game.ChoiseOfGame.ChoiseOfGameActivity;
import com.sampietro.NaiveAAC.activities.Game.Game1.Game1ViewPagerAdapter;
import com.sampietro.NaiveAAC.activities.Game.Utils.GameActivityAbstractClass;
import com.sampietro.NaiveAAC.activities.Game.Utils.PrizeFragment;
import com.sampietro.NaiveAAC.activities.Game.Utils.YoutubePrizeFragment;
import com.sampietro.NaiveAAC.activities.Settings.VerifyActivity;
import com.example.voicerecognitionlibrary.AndroidPermission;
import com.example.voicerecognitionlibrary.SpeechRecognizerManagement;

import io.realm.Realm;

/**
 * <h1>GameAdaViewPagerActivity</h1>
 * <p><b>GameAdaViewPagerActivity</b> displays images (uploaded by the user or Arasaac pictograms) of the
 *  * phrases of a story
 * </p>
 * Refer to <a href="https://www.raywenderlich.com/8192680-viewpager2-in-android-getting-started">raywenderlich.com</a>
 * By <a href="https://www.raywenderlich.com/u/rajdeep1008">Rajdeep Singh</a>
 *
 * @version     1.0, 06/13/22
 * @see GameActivityAbstractClass
 * @see GameADARecyclerViewAdapterInterface
 * @see PrizeFragment.onFragmentEventListenerPrize
 * @see YoutubePrizeFragment.onFragmentEventListenerYoutubePrize
 */
public class GameADAViewPagerActivity extends GameActivityAbstractClass
{
    public String sharedStory;
    public int phraseToDisplayIndex;
    public int wordToDisplayIndex;
    // TTS
    public TextToSpeech tTS1;
    public String toSpeak;
    //
    private ViewGroup mContentView;
    //
    //  ViewPager2
    // - 1) Create the views (activity_game_ada_viewpager_content.xml)
    // - 2) Create the fragment (GameADAViewPagerFragment)
    // - 3) Add a ViewPager2
    // - 4) Create a FragmentStateAdapter who will use the fragments in step 2
    // - 5) Create a layout that contains a ViewPager2 object: (activity_game_ada_viewpager.xml)
    // - 6) This activity must do the following things:
    //   - a) Sets the content view to be the layout with the ViewPager2.
    //   - b) Hooks up the FragmentStateAdapter to the ViewPager2 objects.
    //
    private ViewPager2 mViewPager;
    private GameADAViewPagerAdapter mAdapter;
    private Object lifecycle;
    OnPageChangeCallback callbackViewPager2 = new OnPageChangeCallback() {
        /**
         * Define page change callback
         * </p>
         *
         * @param position int with position index of the new selected page
         * @see OnPageChangeCallback#onPageSelected
         */
        @Override
        public void onPageSelected(int position) {
            super.onPageSelected(position);
            //  update activity variables
            wordToDisplayIndex = position;
        }
    };
    /**
     * configurations of game1 start screen.
     * <p>
     *
     * @param savedInstanceState Define potentially saved parameters due to configurations changes.
     * @see SpeechRecognizerManagement#prepareSpeechRecognizer
     // * @see ActionbarFragment
     * @see Game1ViewPagerAdapter
     * @see android.app.Activity#onCreate(Bundle)
     * @see OnPageChangeCallback#onPageSelected
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Check whether we're recreating a previously destroyed instance
        if (savedInstanceState != null) {
            // The onSaveInstanceState method is called before an activity may be killed
            // (for Screen Rotation Handling) so that
            // when it comes back it can restore its state.
            // it display the last word
            // else display the word passed by GameADAActivity
            sharedStory = savedInstanceState.getString("STORY TO DISPLAY" );
            phraseToDisplayIndex = savedInstanceState.getInt("PHRASE TO DISPLAY INDEX" );
            wordToDisplayIndex =
                    savedInstanceState.getInt("WORD TO DISPLAY INDEX" );
        }
        //
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras != null) {
                sharedStory= extras.getString("STORY TO DISPLAY");
                phraseToDisplayIndex= extras.getInt("PHRASE TO DISPLAY INDEX");
                wordToDisplayIndex= extras.getInt("WORD TO DISPLAY INDEX");
            }
        }
        // viewpager
        setContentView(R.layout.activity_game_ada_viewpager);
        //
        mContentView = findViewById(R.id.activity_game_ada_viewpager_id);
        setToFullScreen();
        ViewTreeObserver viewTreeObserver = mContentView.getViewTreeObserver();
        //
        if (viewTreeObserver.isAlive()) {
            viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    mContentView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
            });
        }
        //
        mContentView.setOnClickListener(view -> setToFullScreen());
        //
        AndroidPermission.checkPermission(this);
        //
        realm= Realm.getDefaultInstance();
        //
        context = this;
        // viewpager
        // Wire Adapter with ViewPager2
        mViewPager = (ViewPager2) findViewById(R.id.pager);
        Lifecycle lifecycle = getLifecycle();
        mAdapter = new GameADAViewPagerAdapter (getSupportFragmentManager(), lifecycle, context, realm, sharedStory, phraseToDisplayIndex);
        mViewPager.setAdapter(mAdapter);
        // Register page change callback
        mViewPager.registerOnPageChangeCallback(callbackViewPager2);
        // Set the currently selected page
        mViewPager.setCurrentItem(wordToDisplayIndex, false);
     }
    /**
     * Hide the Navigation Bar
     *
     * @see android.app.Activity#onResume
     */
    @Override
    protected void onResume() {
        super.onResume();
        setToFullScreen();
    }
    //
    /**
     * destroy SpeechRecognizer, TTS shutdown and more
     *
     * @see androidx.fragment.app.Fragment#onDestroy
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // TTS
        if (tTS1 != null) {
            tTS1.stop();
            tTS1.shutdown();
        }
        // Unregister page change callback
        mViewPager.unregisterOnPageChangeCallback(callbackViewPager2);
    }
    /**
     * This method is called before an activity may be killed so that when it comes back some time in the future it can restore its state..
     * <p>
     *
     * @param savedInstanceState Define potentially saved parameters due to configurations changes.
     */
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        //
        savedInstanceState.putString("STORY TO DISPLAY", sharedStory);
        savedInstanceState.putInt("PHRASE TO DISPLAY INDEX", phraseToDisplayIndex);
        savedInstanceState.putInt("WORD TO DISPLAY INDEX", wordToDisplayIndex);
        //
        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }
    /**
     * This method is responsible to transfer into fullscreen mode.
     */
    private void setToFullScreen() {
        findViewById(R.id.activity_game_ada_viewpager_id).setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }
    //
    /**
     * Called when the user taps the home button.
     * </p>
     *
     * @param v view of tapped button
     * @see ChoiseOfGameActivity
     */
    public void returnHome(View v)
    {
    /*
                navigate to home screen (ChoiseOfGameActivity)
    */
        Intent intent = new Intent(this, ChoiseOfGameActivity.class);
        startActivity(intent);
    }
    /**
     * Called when the user taps the settings button.
     * replace with hear fragment
     * </p>
     *
     * @param v view of tapped button
     * @see VerifyActivity
     */
    public void returnSettings(View v)
    {
    /*
                navigate to settings screen (ChoiseOfGameActivity)
    */
        Intent intent = new Intent(this, VerifyActivity.class);
        startActivity(intent);
    }

    /**
     * Called when the user taps the image.
     * return to GameADAActivity
     * @param v view of tapped button
     * @see GameADAActivity
     */
    public void onClickGameImage(View v) {
        Intent intent = null;
        intent = new Intent(this,
                GameADAActivity.class);
        intent.putExtra("STORY TO DISPLAY", sharedStory);
        intent.putExtra("PHRASE TO DISPLAY INDEX", phraseToDisplayIndex);
        intent.putExtra("WORD TO DISPLAY INDEX", wordToDisplayIndex);
        startActivity(intent);
    }
    /**
     * Called on result of speech.
     *
     * @param eText string result from SpeechRecognizerManagement
     * @see com.example.voicerecognitionlibrary.RecognizerCallback
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onResult(String eText) {
    }
}