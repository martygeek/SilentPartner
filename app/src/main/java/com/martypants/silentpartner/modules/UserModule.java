package com.martypants.silentpartner.modules;

import com.martypants.silentpartner.managers.DataManager;
import com.martypants.silentpartner.network.DataManagerAPI;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit2.Retrofit;


@Module
public class UserModule {

    @Singleton
    @Provides
    DataManagerAPI getServerAPI(Retrofit retrofit) {
        return retrofit.create(DataManagerAPI.class);
    }


    @Singleton
    @Provides
    DataManager getDataManager(DataManagerAPI api) {
        return new DataManager(api);
    }

}
