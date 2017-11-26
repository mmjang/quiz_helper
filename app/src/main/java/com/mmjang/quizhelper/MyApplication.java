package com.mmjang.quizhelper;

import android.app.Activity;
import android.app.Application;
import android.content.Context;

import com.mmjang.quizhelper.anki.AnkiDroidHelper;
import com.tencent.bugly.crashreport.CrashReport;

import org.litepal.LitePalApplication;

/**
 * Created by liao on 2017/4/27.
 */

public class MyApplication extends Application {
    private static Context context;
    private static Application application;
    private static AnkiDroidHelper mAnkiDroid;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        application = this;
        LitePalApplication.initialize(context);
        CrashReport.initCrashReport(getApplicationContext(), "e408f695f4", false);
    }

    public static Context getContext() {
        return context;
    }

    public static Application getApplication(){
        return application;
    }

    public static AnkiDroidHelper getAnkiDroid() {
        if (mAnkiDroid == null) {
            mAnkiDroid = new AnkiDroidHelper(getApplication());
        }
        return mAnkiDroid;
    }

    private static void getAnkiDroidPermission(Activity activity) {
    }


}
