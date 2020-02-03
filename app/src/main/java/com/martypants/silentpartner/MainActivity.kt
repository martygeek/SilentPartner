package com.martypants.silentpartner

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.media.AudioManager
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.martypants.silentpartner.Gfx.DisplayGif
import com.martypants.silentpartner.databinding.ActivityMainBinding
import com.martypants.silentpartner.managers.DataManager
import com.martypants.silentpartner.models.GIF
import com.martypants.silentpartner.viewmodels.GifViewModel
import com.martypants.silentpartner.viewmodels.MyViewModelFactory
import com.trello.rxlifecycle.components.support.RxAppCompatActivity
import javax.inject.Inject


class MainActivity : RxAppCompatActivity(), SpeechListener.OnSpeechListener {

    val TAG = "SilentP"

    @Inject
    lateinit var dataManager: DataManager
    lateinit var recognizer: SpeechRecognizer
    lateinit var viewmodel: GifViewModel
    lateinit var binding: ActivityMainBinding

    var speechIntent: Intent? = null
    var hasSeenSplash = false
    var hasSeenHint = false
    lateinit var disappear: Animation
    lateinit var appear: Animation


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (application as App).userComponent?.inject(this)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        viewmodel = ViewModelProviders.of(this, MyViewModelFactory(application as App))
            .get(GifViewModel::class.java)
        binding.viewmodel = viewmodel
        loadAnimations()

        if (viewmodel.hasData()) {
            DisplayGif(this, binding.gifview!!).showGifData(viewmodel.gifData.value!!)
        }

        // check that we have permission to record audio
        if (checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.RECORD_AUDIO), 999)
        } else {
            // make sure this device has speech recognition
            checkVoiceRecognition()
        }
    }

    private fun loadAnimations() {
        disappear = AnimationUtils.loadAnimation(this, R.anim.disappear)
        appear = AnimationUtils.loadAnimation(this, R.anim.appear)
    }

    fun checkVoiceRecognition() {
        // Check if voice recognition is present
        val pm = packageManager
        val activities =
            pm.queryIntentActivities(Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0)
        if (activities.size == 0) {
            Toast.makeText(this, "Voice recognizer not present", Toast.LENGTH_SHORT).show()
        } else {
            AudioUtils.muteAudio(true, this)
            createSpeechObjects()
            speak()
        }
    }

    private fun createSpeechObjects() {
        recognizer = SpeechRecognizer.createSpeechRecognizer(this)
        speechIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        speechIntent?.putExtra(
            RecognizerIntent.EXTRA_CALLING_PACKAGE,
            javaClass.getPackage()!!.name
        )
        speechIntent?.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )
        speechIntent?.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, NUMBER_OF_RESULTS_TO_SHOW)
        val speechListener = SpeechListener()
        speechListener.setRecognizer(recognizer)
        speechListener.setListener(this)
        speechListener.setIntent(speechIntent!!)
        recognizer.setRecognitionListener(speechListener)
    }

    private fun speak() {
        Log.d(TAG, "Listening")
        recognizer.startListening(speechIntent)
    }

    override fun onSpeechResults(words: String) {
        viewmodel.getGif(words).observe(this, Observer<GIF> {

            if (viewmodel.shouldShowListening.get().equals(View.VISIBLE)) {
                binding.status.visibility = View.GONE
                binding.status.startAnimation(disappear)
                viewmodel.showListening(false)
            }
            DisplayGif(this, binding.gifview!!).showGifData(it)
            viewmodel.gifShown()
            recognizer.startListening(speechIntent)
        })
    }

    override fun onSpeechReady() {
        // splash screen goes away
        if (viewmodel.shouldShowSplash.get().equals(View.VISIBLE)) {
            binding.splash?.visibility = View.GONE
            binding.splash?.startAnimation(disappear)
            viewmodel.splashShown()
            viewmodel.showListening(true)
        }
    }

    override fun onSpeechRestart() {
        Log.d(TAG, "Recreating speech objects")
//        createSpeechObjects()
        speak()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray
    ) {
        when (requestCode) {
            999 -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PERMISSION_GRANTED)) {
                    checkVoiceRecognition()
                }
                return
            }
        }
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (recognizer != null) {
            recognizer.destroy()
        }
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onStop() {
        super.onStop()
        AudioUtils.muteAudio(false, this)
    }

    companion object {
        private val NUMBER_OF_RESULTS_TO_SHOW = 1
    }

    object AudioUtils {

        @JvmStatic
        fun muteAudio(shouldMute: Boolean, context: Context) {
            val audioManager: AudioManager =
                context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
            val muteValue = if (shouldMute) AudioManager.ADJUST_MUTE else AudioManager.ADJUST_UNMUTE
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, muteValue, 0)
        }

    }
}
