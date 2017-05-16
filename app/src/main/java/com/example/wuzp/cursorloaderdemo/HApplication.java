package com.example.wuzp.cursorloaderdemo;

import android.app.Application;
import android.content.Context;

/**
 * Created by wuzp on 2017/5/16.
 */
public class HApplication extends Application {
    public static Context gContext;

    @Override
    public void onCreate() {
        super.onCreate();
        gContext = this;
    }
}
