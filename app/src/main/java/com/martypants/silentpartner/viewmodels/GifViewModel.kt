package com.martypants.silentpartner.viewmodels

import android.util.Log
import android.view.View
import androidx.databinding.ObservableInt
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.martypants.silentpartner.App
import com.martypants.silentpartner.managers.DataManager
import com.martypants.silentpartner.models.GIF
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import javax.inject.Inject

/**
 * Created by Martin Rehder on 2020-01-05.
 */
class GifViewModel (app: App): AndroidViewModel(app) {

    init {
        app.userComponent?.inject(this)
    }

    @Inject
    lateinit var dataManager: DataManager
    var searchString: String? = null
    var shouldShowSplash = ObservableInt(View.VISIBLE)
    var shouldShowListening = ObservableInt(View.GONE)
    var shouldShowAttribution = ObservableInt(View.GONE)
    var hasLoadadGif = false

    val gifData: MutableLiveData<GIF> by lazy {
        MutableLiveData<GIF>().also {
            loadGifData()
        }
    }

    fun hasData(): Boolean {
        return hasLoadadGif
    }

    fun splashShown() {
        shouldShowSplash.set(View.GONE)
    }

    fun showListening(show: Boolean) {
        shouldShowListening.set(if (show) View.VISIBLE else View.GONE)
    }

    fun gifShown() {
        shouldShowAttribution.set(View.VISIBLE)
    }

    fun getGif(search: String): LiveData<GIF> {
        searchString = search
        loadGifData()
        return gifData
    }

    private fun loadGifData() {

        dataManager.getGifData(searchString!!)
            ?.subscribeOn(Schedulers.io())
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.subscribe({
                gifData.value = it
                hasLoadadGif = true
            },
                { error ->
                    Log.d(
                        "GIPHY", "Error: " + error.localizedMessage
                    )
                })
    }


}