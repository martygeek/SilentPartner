package com.martypants.silentpartner

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.CountDownTimer
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.martypants.silentpartner.managers.DataManager
import com.martypants.silentpartner.models.GIF
import com.martypants.silentpartner.viewmodels.GifViewModel
import com.trello.rxlifecycle.components.support.RxAppCompatActivity
import javax.inject.Inject


class MainActivity : RxAppCompatActivity(), SpeechListener.OnSpeechListener {

     @Inject
    lateinit var dataManager: DataManager

    var mVoiceResultTV: TextView? = null
    var imageView: ImageView? = null
    lateinit var timer: CountDownTimer
    lateinit var recognizer: SpeechRecognizer
    lateinit var viewmodel: GifViewModel
     var speechIntent: Intent? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mVoiceResultTV = findViewById(R.id.voiceResultText) as TextView
        mVoiceResultTV?.setOnClickListener {
            timer.cancel()
            speak()
        }
        imageView = findViewById(R.id.gifView) as ImageView?
        viewmodel =   ViewModelProviders.of(this, MyViewModelFactory(application as App))
            .get(GifViewModel::class.java)


        (application as App).userComponent?.inject(this)

        if (checkSelfPermission(
                Manifest.permission.RECORD_AUDIO)
            != PERMISSION_GRANTED
        ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_CONTACTS)) {
            } else {
                ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.RECORD_AUDIO), 999)
            }
        } else {
            speak()
        }
        checkVoiceRecognition()

    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            999 -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    speak()
                } else {
                }
                return
            }
        }
    }

    fun checkVoiceRecognition() {
        // Check if voice recognition is present
        val pm = packageManager
        val activities = pm.queryIntentActivities(Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0)
        if (activities.size == 0) {
            Toast.makeText(this, "Voice recognizer not present", Toast.LENGTH_SHORT).show()
        } else {
            startListening()
        }
    }

    fun startListening() {
        mVoiceResultTV?.text = "Listening..."
        speak()
    }


    fun showGifData(data: GIF) {
        val url = data.data[0].embed_url

        val thumbnailRequest: RequestBuilder<Drawable> =
            GlideApp.with(this).load(data).decode(
                Bitmap::class.java
            )

        GlideApp.with(this)
            .load(data.data[0].images.original.url)
            .thumbnail(thumbnailRequest)
            .listener(
                object :
                    RequestListener<Drawable?> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any,
                        target: Target<Drawable?>,
                        isFirstResource: Boolean
                    ): Boolean { return false  }

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any,
                        target: Target<Drawable?>,
                        dataSource: DataSource,
                        isFirstResource: Boolean
                    ): Boolean { return false  }
                })
            .into(imageView!!)

        timer = object: CountDownTimer(20000, 20000) {
            override fun onFinish() {
                recognizer.startListening(speechIntent)
            }
            override fun onTick(p0: Long) { }
        }
        timer.start()
    }

    fun speak() {
        recognizer = SpeechRecognizer.createSpeechRecognizer(this)

        speechIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        speechIntent?.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, javaClass.getPackage()!!.name)
        speechIntent?.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
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
            showGifData(it)
        })
    }

    companion object {
        private val TAG = "SilentP"
        private val NUMBER_OF_RESULTS_TO_SHOW = 1
    }

}
