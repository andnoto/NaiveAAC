package com.sampietro.NaiveAAC.activities.VoiceRecognition

import android.app.Activity
import android.content.Context
import android.speech.SpeechRecognizer
import android.content.Intent
import android.speech.RecognizerIntent
import android.speech.RecognitionListener
import android.os.Bundle
import java.lang.RuntimeException
import java.util.*

object SpeechRecognizerManagement : Activity() {
    private lateinit var recognizer: SpeechRecognizer
    private var recognizerIntent: Intent? = null
    private var editText: String? = null

    //
    private var callback: RecognizerCallback? = null

    //
    //
    fun prepareSpeechRecognizer(context: Context) {
        recognizer = SpeechRecognizer.createSpeechRecognizer(context)
        recognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        recognizerIntent!!.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )
        recognizerIntent!!.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
        //
        recognizerIntent!!.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
        //
        recognizer.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(bundle: Bundle) {
                editText = "onReadyForSpeech.."
                callback!!.onReadyForSpeech(editText) // Invoke callback here
            }

            override fun onBeginningOfSpeech() {
                editText = ""
                editText = "Listening.."
                callback!!.onBeginningOfSpeech(editText) // Invoke callback here
            }

            override fun onRmsChanged(v: Float) {
                editText = "onRmsChanged.."
                callback!!.onRmsChanged(editText) // Invoke callback here
            }

            override fun onBufferReceived(bytes: ByteArray) {
                editText = "onBufferReceived.."
                callback!!.onBufferReceived(editText) // Invoke callback here
            }

            override fun onEndOfSpeech() {
                editText = "onEndOfSpeech.."
                callback!!.onEndOfSpeech(editText) // Invoke callback here
            }

            override fun onError(i: Int) {
                callback!!.onError(i) // Invoke callback here
            }

            override fun onResults(bundle: Bundle) {
                val givenByTheRecognizer =
                    bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                editText = givenByTheRecognizer!![0]
                callback!!.onResult(editText) // Invoke callback here
            }

            override fun onPartialResults(bundle: Bundle) {
                // When requesting results in onPartialResults(), the UNSTABLE_TEXT parameter to getSTtringArrayList() must be in quotes
                val data = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                val unstableData = bundle.getStringArrayList("android.speech.extra.UNSTABLE_TEXT")
                editText = data!![0] + unstableData!![0]
                callback!!.onPartialResults(editText) // Invoke callback here
            }

            override fun onEvent(i: Int, bundle: Bundle) {
                editText = "onEvent.."
                callback!!.onEvent(editText) // Invoke callback here
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

    fun startSpeech() {
        recognizer.startListening(recognizerIntent)
    }

    //
    fun stopSpeech() {
        recognizer.stopListening()
    }

    //
    fun destroyRecognizer() {
        recognizer.destroy()
    } //
}