package com.martypants.silentpartner.modules;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.internal.bind.util.ISO8601Utils;
import com.martypants.silentpartner.App;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.ParsePosition;
import java.util.Date;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Module for providing network related dependencies.
 *
 */
@Module
public class NetModule {
    private final Context mContext;
    private final String mNetworkEndpoint;

    private final App mApp;

    public NetModule(App context, String networkEndpoint) {
        mContext = context;
        mApp = context;
        mNetworkEndpoint = networkEndpoint;
    }

    @Singleton
    @Provides
    App provideApp() {
        return mApp;
    }

    @Provides
    @Singleton
    Gson provideGson() {

        return new GsonBuilder()
                .registerTypeAdapter(Date.class, new GsonUtcDateAdapter())
//                .excludeFieldsWithoutExposeAnnotation()
                .create();
    }

    @Singleton
    @Provides
    OkHttpClient provideOkHttpClient() {
        // Set up some caching
        int cacheSize = 10 * 1024 * 1024; // 10 MiB
        Cache cache = new Cache(mContext.getCacheDir(), cacheSize);

        // Set up some logging
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        return new OkHttpClient.Builder()
                .cache(cache)
                .addInterceptor(chain -> {

                    Request request = chain.request();

                    request = request
                            .newBuilder()
                            .addHeader("Content-Type", "application/json").build();

                    return chain.proceed(request);
                })
                .addInterceptor(interceptor)
                .build();
    }

    public class GsonUtcDateAdapter implements JsonSerializer<Date>, JsonDeserializer<Date> {

        GsonUtcDateAdapter() {
        }

        @Override
        public synchronized JsonElement serialize(Date date, Type type, JsonSerializationContext jsonSerializationContext) {
            return new JsonPrimitive(ISO8601Utils.format(date));
        }

        @Override
        public synchronized Date deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) {
            try {
                return ISO8601Utils.parse(jsonElement.getAsString(), new ParsePosition(0));
            } catch (ParseException e) {
                throw new JsonParseException(e);
            }
        }
    }

    @Singleton
    @Provides
    Retrofit provideRetrofit(Gson gson, OkHttpClient client) {

        return new Retrofit.Builder()
                .baseUrl(mNetworkEndpoint)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();

    }
}
