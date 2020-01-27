package com.martypants.silentpartner.viewmodels

import android.util.Log
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


    private val gifData: MutableLiveData<GIF> by lazy {
        MutableLiveData<GIF>().also {
            loadGifData()
        }
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
            },
                { error ->
                    Log.d(
                        "GIPHY", "Error: " + error.localizedMessage
                    )
                })
    }


}