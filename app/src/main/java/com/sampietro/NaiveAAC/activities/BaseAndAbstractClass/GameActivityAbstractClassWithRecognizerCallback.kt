package com.sampietro.NaiveAAC.activities.BaseAndAbstractClass

import android.view.View
import com.sampietro.NaiveAAC.activities.VoiceRecognition.RecognizerCallback
import com.sampietro.NaiveAAC.activities.VoiceRecognition.SpeechRecognizerManagement

/**
 * <h1>GameActivityAbstractClass</h1>
 *
 * **GameActivityAbstractClass**
 * abstract class containing common methods that is extended by
 *
 * 1) ChoiseOfGameActivity
 * 2) Game1Activity
 * 3) Game2Activity
 * 4) GameADAActivity
 * implements voice recognizer common methods and others
 * Refer to [howtomakeavideogameusingvoicecommands.blogspot.com](https://howtomakeavideogameusingvoicecommands.blogspot.com/2021/08/presentation.html)
 * and [github.com](https://github.com/andnoto/MyApplicationLibrary)
 *
 *
 * @version     5.0, 01/04/2024
 * @see com.sampietro.NaiveAAC.activities.Game.ChoiseOfGame.ChoiseOfGameActivity
 *
 * @see com.sampietro.NaiveAAC.activities.Game.Game1.Game1Activity
 *
 * @see com.sampietro.NaiveAAC.activities.Game.Game2.Game2Activity
 *
 * @see RecognizerCallback
 */
abstract class GameActivityAbstractClassWithRecognizerCallback : GameActivityAbstractClass(), RecognizerCallback
    {
    //
    /**
     * Called when the user taps the start speech button.
     *
     * @param v view of tapped button
     * @see SpeechRecognizerManagement.startSpeech
     */
    fun startSpeech(v: View?) {
        SpeechRecognizerManagement.startSpeech()
    }

    /**
     * Called when the user taps the stop speech button.
     *
     * @param v view of tapped button
     * @see SpeechRecognizerManagement.stopSpeech
     */
    fun stopSpeech(v: View?) {
        SpeechRecognizerManagement.stopSpeech()
    }

    /**
     * Called on beginning of speech.
     * sets the color of the hearing button to green
     *
     * @param eText string message from SpeechRecognizerManagement
     * @see RecognizerCallback
     */
    override fun onBeginningOfSpeech(eText: String?) {}

    /**
     * Called on ready for speech.
     *
     * @param editText string message from SpeechRecognizerManagement
     * @see RecognizerCallback
     */
    override fun onReadyForSpeech(editText: String?) {}

    /**
     * Called on Rms changed.
     *
     * @param editText string message from SpeechRecognizerManagement
     * @see RecognizerCallback
     */
    override fun onRmsChanged(editText: String?) {}

    /**
     * Called on buffer received.
     *
     * @param editText string message from SpeechRecognizerManagement
     * @see RecognizerCallback
     */
    override fun onBufferReceived(editText: String?) {}

    /**
     * Called on end of speech.
     * sets the color of the hearing button to red
     *
     * @param editText string message from SpeechRecognizerManagement
     * @see RecognizerCallback
     */
    override fun onEndOfSpeech(editText: String?) {}

    /**
     * Called on partial results.
     *
     * @param editText string message from SpeechRecognizerManagement
     * @see RecognizerCallback
     */
    override fun onPartialResults(editText: String?) {}

    /**
     * Called on event.
     *
     * @param editText string message from SpeechRecognizerManagement
     * @see RecognizerCallback
     */
    override fun onEvent(editText: String?) {}

    /**
     * Called on error.
     *
     * @param errorCode int error code from SpeechRecognizerManagement
     * @see RecognizerCallback
     */
    override fun onError(errorCode: Int) {
        // Error 9 ERROR_INSUFFICIENT_PERMISSIONS is also thrown when google does not have record_audio permission.
        val errore = errorCode
    }

    override fun onResult(editText: String?) {}

}