package com.martypants.silentpartner

import com.martypants.silentpartner.viewmodels.GifViewModel


/**
 * Created by Martin Rehder on 2019-11-02.
 */
class MyViewModelFactory(private val mApplication: App) :
    androidx.lifecycle.ViewModelProvider.Factory {


    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
        return GifViewModel(mApplication) as T
    }
}