package pl.konradcygal.githubsearch;

import android.app.Application;
import android.content.Context;

import timber.log.BuildConfig;
import timber.log.Timber;

public class GithubSearchApp extends Application {

    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }
    }

    public static Context getContext(){
        return context;
    }
}
