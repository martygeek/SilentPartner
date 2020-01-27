package com.martypants.silentpartner

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.martypants.silentpartner.Gfx.DisplayGif
import com.martypants.silentpartner.managers.DataManager
import com.martypants.silentpartner.models.GIF
import com.martypants.silentpartner.viewmodels.GifViewModel
import com.martypants.silentpartner.viewmodels.MyViewModelFactory
import com.trello.rxlifecycle.components.support.RxAppCompatActivity
import javax.inject.Inject


class MainActivity : RxAppCompatActivity(), SpeechListener.OnSpeechListener {

    @Inject
    lateinit var dataManager: DataManager
    lateinit var recognizer: SpeechRecognizer
    lateinit var viewmodel: GifViewModel

    var mVoiceResultTV: TextView? = null
    var imageView: ImageView? = null
    var speechIntent: Intent? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        imageView = findViewById(R.id.gifView) as ImageView?
        viewmodel = ViewModelProviders.of(this,
            MyViewModelFactory(
                application as App
            )
        )
            .get(GifViewModel::class.java)
        (application as App).userComponent?.inject(this)

        // first, check that we have permission to record audio
        if (checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.RECORD_AUDIO), 999)
        } else {
            // make sure this device has speech recognition
            checkVoiceRecognition()
        }
    }

    fun checkVoiceRecognition() {
        // Check if voice recognition is present
        val pm = packageManager
        val activities =
            pm.queryIntentActivities(Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0)
        if (activities.size == 0) {
            Toast.makeText(this, "Voice recognizer not present", Toast.LENGTH_SHORT).show()
        } else {
            speak()
        }
    }

    fun speak() {
        recognizer = SpeechRecognizer.createSpeechRecognizer(this)

        speechIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        speechIntent?.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, javaClass.getPackage()!!.name)
        speechIntent?.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        speechIntent?.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, NUMBER_OF_RESULTS_TO_SHOW)
        speechIntent?.putExtra(RecognizerIntent.EXTRA_PROMPT, "Silent Partner is Listening")
        val speechListener = SpeechListener()
        speechListener.setRecognizer(recognizer)
        speechListener.setListener(this)
        speechListener.setIntent(speechIntent!!)
        recognizer.setRecognitionListener(speechListener)
        recognizer.startListening(speechIntent)
    }


    override fun onSpeechResults(words: String) {
        viewmodel.getGif(words).observe(this, Observer<GIF> {

            val displayGif = DisplayGif(this, imageView!!)
            displayGif.showGifData(it)
            recognizer.startListening(speechIntent)
        })
    }

    override fun onSpeechReady() {
        // splash screen goes away
    }

    override fun onSpeechRestart() {
        recognizer.destroy()
        speak()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray
    ) {
        when (requestCode) {
            999 -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    checkVoiceRecognition()
                }
                return
            }
        }
    }

    companion object {
        private val TAG = "SilentP"
        private val NUMBER_OF_RESULTS_TO_SHOW = 1
    }

}
