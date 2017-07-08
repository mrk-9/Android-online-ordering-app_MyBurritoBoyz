package com.winrestenterprise.ewallet.mbbz;

import android.app.Application;
import android.content.res.Configuration;

import com.parse.Parse;
import com.parse.ParseInstallation;

/**
 * Created by michael on 01/12/2015.
 */
public class MyApplication extends Application {

@Override
public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        }

@Override
public void onCreate() {
        super.onCreate();
        //Parse.initialize(this, "aegfMcodrjUtJPLMqLGj7rTeOfSPeDOdfdVp9MU6", "9jnuW8qJ9wZi6o8JhiQplOezfiJjXkJ8kuJTzzGJ");
        //ParseInstallation.getCurrentInstallation().saveInBackground();
        }

@Override
public void onLowMemory() {
        super.onLowMemory();
        }

@Override
public void onTerminate() {
        super.onTerminate();
        }

}
