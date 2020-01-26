package com.martypants.silentpartner.modules;

import com.google.gson.Gson;
import com.martypants.silentpartner.App;
import com.martypants.silentpartner.MainActivity;
import com.martypants.silentpartner.viewmodels.GifViewModel;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {UserModule.class, NetModule.class})
public interface UserComponent {

    // Pass on any provided objects to subclasses.
    // If you are writing tests and can't get things injected
    // add to this list.
    Gson gson();


    void inject(App app);

    void inject(MainActivity mainActivity);

    void inject(GifViewModel gifViewModel);

}
