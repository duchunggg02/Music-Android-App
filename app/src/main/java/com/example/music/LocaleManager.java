package com.example.music;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;

import java.util.Locale;

public class LocaleManager {
    private static final String PREF_LANG = "lang";

    public static Context setLocale(Context context) {
        return updateResources(context, getLanguage(context));
    }

    public static Context setNewLocale(Context context, String lang) {
        persistLanguage(context, lang);
        return updateResources(context, lang);
    }

    private static String getLanguage(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_LANG, Context.MODE_PRIVATE);
        return prefs.getString(PREF_LANG, "en");
    }

    private static void persistLanguage(Context context, String lang) {
        SharedPreferences.Editor editor = context.getSharedPreferences(PREF_LANG, Context.MODE_PRIVATE).edit();
        editor.putString(PREF_LANG, lang);
        editor.apply();
    }

    private static Context updateResources(Context context, String lang) {
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);

        Resources resources = context.getResources();
        Configuration configuration = resources.getConfiguration();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            configuration.setLocale(locale);
            context = context.createConfigurationContext(configuration);
        } else {
            configuration.locale = locale;
            resources.updateConfiguration(configuration, resources.getDisplayMetrics());
        }

        return context;
    }
}
