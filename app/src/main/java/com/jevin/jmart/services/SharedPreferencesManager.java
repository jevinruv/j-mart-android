package com.jevin.jmart.services;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesManager {

    private static final String APP_SETTINGS = "J_MART";

    private static final String USER_ID = "USER_ID";
    private static final String CART_ID = "CART_ID";


    private SharedPreferencesManager() {
    }

    private static SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences(APP_SETTINGS, Context.MODE_PRIVATE);
    }

    public static int getUserId(Context context) {
        return getSharedPreferences(context).getInt(USER_ID, 0);
    }

    public static int getCartId(Context context) {
        return getSharedPreferences(context).getInt(CART_ID, 0);
    }

    public static void setUserId(Context context, int value) {
        final SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putInt(USER_ID, value);
        editor.commit();
    }

    public static void setCartId(Context context, int value) {
        final SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putInt(CART_ID, value);
        editor.commit();
    }


}