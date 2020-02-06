package com.martypants.silentpartner

import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.SpeechRecognizer
import android.util.Log

/**
 * Created by Martin Rehder on 2020-01-26.
 */
public class SpeechListener : RecognitionListener {

    interface OnSpeechListener {
        fun onSpeechResults(words: String)
        fun onSpeechReady()
        fun onSpeechRestart()
    }

    private var speechRecognizer: SpeechRecognizer?  = null
    private var speechIntent: Intent? = null
    private var speechCallback: OnSpeechListener? = null

    fun setRecognizer(recognizer: SpeechRecognizer) {
        speechRecognizer = recognizer
    }

    fun setIntent(intent: Intent) {
        speechIntent = intent
    }

    fun setListener(listener: OnSpeechListener) {
        speechCallback = listener
    }

    override fun onReadyForSpeech(p0: Bundle?) {
        showToastMessage("OnReadyForSpeech")
    }

    override fun onRmsChanged(p0: Float) {
//        showToastMessage("onRmsCHanged "+p0)
    }

    override fun onBufferReceived(p0: ByteArray?) {
        showToastMessage("onBufferRecd")
    }

    override fun onPartialResults(p0: Bundle?) {
        showToastMessage("onPartialResults")
    }

    override fun onEvent(p0: Int, p1: Bundle?) {
        showToastMessage("onEvent")
    }

    override fun onBeginningOfSpeech() {
        showToastMessage("onBeginningofSpeech")
        speechCallback?.onSpeechReady()

    }

    override fun onEndOfSpeech() {
        showToastMessage("onEndofSpeech")
    }

    override fun onError(p0: Int) {
        when (p0) {
            SpeechRecognizer.ERROR_AUDIO -> showToastMessage("ERROR_AUDIO")
            SpeechRecognizer.ERROR_CLIENT -> showToastMessage("ERROR_CLIENT")
            SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> showToastMessage("ERROR_INSUFFICIENT_PERMISSIONS")
            SpeechRecognizer.ERROR_NETWORK -> showToastMessage("ERROR_NETWORK")
            SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> showToastMessage("ERROR_NETWORK_TIMEOUT")
            SpeechRecognizer.ERROR_NO_MATCH -> {
                showToastMessage("ERROR_NO_MATCH")
                speechRecognizer?.cancel()
                speechRecognizer?.startListening(speechIntent)

                showToastMessage("starting speech listener after no match")
            }
            SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> showToastMessage("ERROR_RECOGNIZER_BUSY")
            SpeechRecognizer.ERROR_SERVER -> showToastMessage("ERROR_SERVER")
            SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> {
                showToastMessage("ERROR_SPEECH_TIMEOUT")
                speechCallback?.onSpeechRestart()
            }

        }
    }

    override fun onResults(p0: Bundle?) {
        val words: ArrayList<String> = p0?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION) as ArrayList<String>
        val scores: FloatArray? = p0.getFloatArray(SpeechRecognizer.CONFIDENCE_SCORES)
        if (scores != null) {
            if (scores[0] > 0.5) {
                speechCallback?.onSpeechResults(words[0])
            }
        }
    }

    fun showToastMessage(message:String) {
        Log.d("SPL", message)
    }
}