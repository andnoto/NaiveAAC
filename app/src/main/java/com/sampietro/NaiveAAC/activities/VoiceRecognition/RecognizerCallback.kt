package com.sampietro.NaiveAAC.activities.VoiceRecognition

interface RecognizerCallback {
    //
    fun onResult(editText: String?)

    //
    fun onBeginningOfSpeech(editText: String?)

    //
    fun onReadyForSpeech(editText: String?)

    //
    fun onRmsChanged(editText: String?)

    //
    fun onBufferReceived(editText: String?)

    //
    fun onEndOfSpeech(editText: String?)

    //
    fun onPartialResults(editText: String?)

    //
    fun onEvent(editText: String?)

    //
    fun onError(errorCode: Int)
}