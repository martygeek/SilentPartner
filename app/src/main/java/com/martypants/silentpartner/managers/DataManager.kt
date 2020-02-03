package com.martypants.silentpartner.managers

import com.martypants.silentpartner.models.GIF
import com.martypants.silentpartner.network.DataManagerAPI
import com.martypants.silentpartner.network.DataManagerAPI.apiKey
import rx.Observable


/**
 * Created by Martin Rehder on 2019-11-02.
 */
class DataManager(private val mApi: DataManagerAPI) {

    fun getGifData(search: String) : Observable<GIF>? {
        return mApi.getGifData(search, apiKey, "R");
    }

}