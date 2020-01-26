package com.martypants.silentpartner.network;

import com.martypants.silentpartner.models.GIF;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by marty on 7/28/16.
 */



public interface DataManagerAPI {

    public static final String apiKey = "ykw9axdZn8EBob4bNyIvRJSkAkFGMWG0";

    @GET("/v1/gifs/search")
    Observable<GIF> getGifData(@Query("q") String search, @Query("api_key") String apiKey);


}

