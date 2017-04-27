package com.thanhduy.ophuot.base;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;

import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.FontAwesomeModule;
import com.thanhduy.ophuot.utils.SessionManagerForLanguage;

import java.util.Locale;

/**
 * Created by buivu on 07/02/2017.
 */

public class OPhuotApplication extends Application {
    private static OPhuotApplication instance;

    public static OPhuotApplication getInstance() {
        return instance;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        //MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        Iconify.with(new FontAwesomeModule());
        setLocale();
        // register to be informed of activities starting up
        registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle bundle) {
                activity.setRequestedOrientation(
                        ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            }

            @Override
            public void onActivityStarted(Activity activity) {

            }

            @Override
            public void onActivityResumed(Activity activity) {

            }

            @Override
            public void onActivityPaused(Activity activity) {

            }

            @Override
            public void onActivityStopped(Activity activity) {

            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {

            }

            @Override
            public void onActivityDestroyed(Activity activity) {

            }
        });
    }

    private void setLocale() {
        Resources resources = getResources();
        Configuration configuration = resources.getConfiguration();
        Locale locale = getLocale();
        if (!configuration.locale.equals(locale)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                configuration.setLocale(locale);
            }
            resources.updateConfiguration(configuration, null);
        }
    }

    private Locale getLocale() {
        SessionManagerForLanguage sessionManagerForLanguage = new SessionManagerForLanguage(this);
        String language = sessionManagerForLanguage.getLanguage();
        switch (language) {
            case "Tiếng Việt":
                language = "vi";
                break;

            case "English":
                language = "en";
                break;
        }
        return new Locale(language);
    }
}
