package com.example.ngrfitness;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;

import java.util.Locale;

public class LenguageManager {

    private Context ct;
    private SharedPreferences sharedPreferences;
    public LenguageManager(Context ctx){
        ct=ctx;
        sharedPreferences = ct.getSharedPreferences("LANG",Context.MODE_PRIVATE);
    }
    public void setLang(String code){
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putString("lang",code);
        editor.commit();
    }

    public String getLang(){
        return sharedPreferences.getString("lang","en");
    }

    public void updateResource(String code){
        Locale locale = new Locale(code);
        Locale.setDefault(locale);
        Resources resources =ct.getResources();
        Configuration configuration = resources.getConfiguration();
        configuration.locale=locale;
        resources.updateConfiguration(configuration,resources.getDisplayMetrics());
        setLang(code);
    }
}

