package com.crypton.crypton_flutter_modules;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

public class AddressUtils {

    public static String getRegister(Context context, String url) {
        return joinUrl(context, url);
    }

    public static String getValidate(Context context, String url) {
        return joinUrl(context, url);
    }

    private static String joinUrl(Context context, String url) {
        return getPreferences(context).getString("settings_address_api1", url);
    }

    private static SharedPreferences getPreferences(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

}
