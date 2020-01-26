package com.martypants.silentpartner;

import android.app.Application;
import android.content.Context;

import com.martypants.silentpartner.modules.DaggerUserComponent;
import com.martypants.silentpartner.modules.NetModule;
import com.martypants.silentpartner.modules.UserComponent;
import com.martypants.silentpartner.modules.UserModule;


/**
 * Created by Martin Rehder on 2019-11-02.
 */
public class App extends Application {

    private static final String TAG = "App";

    private static App app;
    private UserComponent mUserComponent;

    public static App getApp() {
        return app;
    }
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (mUserComponent == null) {
            mUserComponent = DaggerUserComponent.builder()
                    .userModule(new UserModule())
                    .netModule(new NetModule(this, getNetworkEndpoint()))
                    .build();
        }
        app = this;

        mUserComponent.inject(this);
    }




    private String getNetworkEndpoint() {
        return  "https://api.giphy.com";
    }

    /**
     * Get the UserComponent to inject a class with
     *
     * @return a UserComponent
     */
    public UserComponent getUserComponent() {
        return mUserComponent;
    }

    /**
     * This is used to inject a component for testing if
     * required.
     *
     * @param component a usercomponent for testing with
     */
    public void setUserComponent(UserComponent component) {
        mUserComponent = component;
    }
}