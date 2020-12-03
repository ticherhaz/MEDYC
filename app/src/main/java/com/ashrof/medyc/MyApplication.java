package com.ashrof.medyc;

import android.app.Application;
import android.content.Context;

import androidx.multidex.MultiDex;

import com.ashrof.medyc.utils.Simpan;

import static net.ticherhaz.tarikhmasa.TarikhMasa.AndroidThreeTenBP;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        //Get the sharedPreference for the theme and apply to the apps.
        Simpan.init(getApplicationContext());
        //TarikhMasa
        AndroidThreeTenBP(this);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}
