package com.example.NaiveAAC.activities.Game.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.example.NaiveAAC.R;
import com.example.NaiveAAC.activities.Game.ChoiseOfGame.ChoiseOfGameActivity;
import com.example.NaiveAAC.activities.Game.Game1.Game1Activity;
import com.example.NaiveAAC.activities.history.ToBeRecordedInHistory;
import com.example.NaiveAAC.activities.history.VoiceToBeRecordedInHistory;
import com.example.voicerecognitionlibrary.RecognizerCallback;
import com.example.voicerecognitionlibrary.SpeechRecognizerManagement;

import java.util.Date;

import io.realm.Realm;

/**
 * <h1>GameActivityAbstractClass</h1>
 * <p><b>GameActivityAbstractClass</b>
 * abstract class containing common methods that is extended by
 * </p>
 * 1) GameActivityChoiseOfGameMediaPlayer</p>
 * 2) GameActivityGame1ViewPager</p>
 * 3) GameActivityGame2</p>
 * implements voice recognizer common methods and others</p>
 * Refer to <a href="https://howtomakeavideogameusingvoicecommands.blogspot.com/2021/08/presentation.html">howtomakeavideogameusingvoicecommands.blogspot.com</a>
 * and <a href="https://github.com/andnoto/MyApplicationLibrary">github.com</a>
 *
 *
 * @version     1.2, 04/28/22
 * @see ChoiseOfGameActivity
 * @see Game1Activity
 * @see com.example.NaiveAAC.activities.Game.Game2.GameActivityGame2
 * @see RecognizerCallback
 * @see OnFragmentEventListenerGame
 */
public abstract class GameActivityAbstractClass extends AppCompatActivity implements RecognizerCallback,
        OnFragmentEventListenerGame {
    //
    public ImageButton hearingImageButton;
    //
    public Realm realm;
    //
    public String message = "messaggio non formato";
    public TextView textView;
    //
    public FragmentManager fragmentManager;
    //
    public View rootViewImageFragment;
    public View rootViewPrizeFragment;
    //
    public ToBeRecordedInHistory<VoiceToBeRecordedInHistory> toBeRecordedInHistory;
    public Context context;
    public SharedPreferences sharedPref;
    public Integer sharedLastSession;
    public Integer sharedLastPhraseNumber;
    public Date currentTime;
    public VoiceToBeRecordedInHistory voiceToBeRecordedInHistory;
    //
    /**
     * Called when the user taps the start speech button.
     *
     * @param v view of tapped button
     * @see SpeechRecognizerManagement#startSpeech()
     */
    public void startSpeech(View v)
    {
        SpeechRecognizerManagement.startSpeech();
    }
    /**
     * Called when the user taps the stop speech button.
     *
     * @param v view of tapped button
     * @see SpeechRecognizerManagement#stopSpeech()
     */
    public void stopSpeech(View v)
    {
        SpeechRecognizerManagement.stopSpeech();
    }
    /**
     * Called on beginning of speech.
     * sets the color of the hearing button to green
     *
     * @param eText string message from SpeechRecognizerManagement
     * @see RecognizerCallback
     */
    @Override
    public void onBeginningOfSpeech(String eText) {
        hearingImageButton.setImageResource(R.drawable.ic_baseline_hearing_36_green);
    }
    /**
     * Called on ready for speech.
     *
     * @param editText string message from SpeechRecognizerManagement
     * @see RecognizerCallback
     */
    @Override
    public void onReadyForSpeech(String editText) {
    }
    /**
     * Called on Rms changed.
     *
     * @param editText string message from SpeechRecognizerManagement
     * @see RecognizerCallback
     */
    @Override
    public void onRmsChanged(String editText) {
    }
    /**
     * Called on buffer received.
     *
     * @param editText string message from SpeechRecognizerManagement
     * @see RecognizerCallback
     */
    @Override
    public void onBufferReceived(String editText) {
    }
    /**
     * Called on end of speech.
     * sets the color of the hearing button to red
     *
     * @param editText string message from SpeechRecognizerManagement
     * @see RecognizerCallback
     */
    @Override
    public void onEndOfSpeech(String editText) {
        hearingImageButton.setImageResource(R.drawable.ic_baseline_hearing_36_red);
    }
    /**
     * Called on partial results.
     *
     * @param editText string message from SpeechRecognizerManagement
     * @see RecognizerCallback
     */
    @Override
    public void onPartialResults(String editText) {
    }
    /**
     * Called on event.
     *
     * @param editText string message from SpeechRecognizerManagement
     * @see RecognizerCallback
     */
    @Override
    public void onEvent(String editText) {
    }
    /**
     * Called on error.
     *
     * @param errorCode int error code from SpeechRecognizerManagement
     * @see RecognizerCallback
     */
    @Override
    public void onError(int errorCode) {
    }
    /**
     * on callback from GameFragment to this Activity
     *
     * @param v view root fragment view
     */
    @Override
    public void receiveResultGameFragment(View v) {
        rootViewImageFragment = v;
    }
//
}