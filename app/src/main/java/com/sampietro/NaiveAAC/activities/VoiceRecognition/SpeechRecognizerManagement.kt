package com.sampietro.NaiveAAC.activities.VoiceRecognition

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import java.util.Locale

object SpeechRecognizerManagement {
    private lateinit var recognizer: SpeechRecognizer
    private lateinit var recognizerIntent: Intent
    private lateinit var editText: String

    //
    private lateinit var callback: RecognizerCallback

    //
    @JvmStatic
    fun prepareSpeechRecognizer(context: Context) {
        recognizer = SpeechRecognizer.createSpeechRecognizer(context)
        recognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        recognizerIntent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
        //
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
        //
        recognizer.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(bundle: Bundle) {
                editText = "onReadyForSpeech.."
                callback.onReadyForSpeech(editText) // Invoke callback here
            }

            override fun onBeginningOfSpeech() {
                editText = ""
                editText = "Listening.."
                callback.onBeginningOfSpeech(editText) // Invoke callback here
            }

            override fun onRmsChanged(v: Float) {
                editText = "onRmsChanged.."
                callback.onRmsChanged(editText) // Invoke callback here
            }

            override fun onBufferReceived(bytes: ByteArray) {
                editText = "onBufferReceived.."
                callback.onBufferReceived(editText) // Invoke callback here
            }

            override fun onEndOfSpeech() {
                editText = "onEndOfSpeech.."
                callback.onEndOfSpeech(editText) // Invoke callback here
            }

            override fun onError(i: Int) {
                callback.onError(i) // Invoke callback here
            }

            override fun onResults(bundle: Bundle) {
                val givenByTheRecognizer =
                    bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                editText = givenByTheRecognizer!![0]
                callback.onResult(editText) // Invoke callback here
            }

            override fun onPartialResults(bundle: Bundle) {
                // When requesting results in onPartialResults(), the UNSTABLE_TEXT parameter to getSTtringArrayList() must be in quotes
                val data = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                val unstableData = bundle.getStringArrayList("android.speech.extra.UNSTABLE_TEXT")
                editText = data!![0] + unstableData!![0]
                callback.onPartialResults(editText) // Invoke callback here
            }

            override fun onEvent(i: Int, bundle: Bundle) {
                editText = "onEvent.."
                callback.onEvent(editText) // Invoke callback here
            }
        })
        //
        if (context is RecognizerCallback) {
            callback = context
        } else {
            throw RuntimeException(
                context.toString()
                        + " RecognizerCallback not found"
            )
        }
    }
    @JvmStatic
    fun startSpeech() {
        recognizer.startListening(recognizerIntent)
    }

    //
    @JvmStatic
    fun stopSpeech() {
        recognizer.stopListening()
    }

    //
    @JvmStatic
    fun destroyRecognizer() {
        recognizer.destroy()
    } //
}