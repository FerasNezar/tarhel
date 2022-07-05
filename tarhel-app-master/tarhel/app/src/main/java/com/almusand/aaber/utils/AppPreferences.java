package com.almusand.aaber.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.almusand.aaber.ui.login.LoginActivity;


/**
 * Developed by Anas Elshwaf
 * anaselshawaf357@gmail.com
 */
public class AppPreferences {

    public static void logout(Context context) {
        clearUserDetails(context);
        Intent i = new Intent(context.getApplicationContext(), LoginActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

        context.startActivity(i);
        ((Activity) context).finish();

    }

    private static void clearUserDetails(Context context) {
        context.getSharedPreferences(AppConstants.PREFERENCE_USER, Context.MODE_PRIVATE).edit().clear().apply();
    }

    public static String getIsUserInConversation(Context context) {
        return context.getSharedPreferences(AppConstants.PREFERENCE_USER, Context.MODE_PRIVATE).getString(AppConstants.USER_IN_CONVERSATION, "no");
    }

    public static void setIsUserInConversation(Context context, String id) {
        SharedPreferences.Editor editor = context.getSharedPreferences(AppConstants.PREFERENCE_USER, Context.MODE_PRIVATE).edit();
        editor.putString(AppConstants.USER_IN_CONVERSATION, id);
        editor.commit();
    }

    public static String getAppUrl(Context context) {
        return context.getSharedPreferences(AppConstants.PREFERENCE_USER, Context.MODE_PRIVATE).getString(AppConstants.AppUrl, null);
    }

    public static void setAppUrl(Context context, String appUrl) {
        SharedPreferences.Editor editor = context.getSharedPreferences(AppConstants.PREFERENCE_USER, Context.MODE_PRIVATE).edit();
        editor.putString(AppConstants.AppUrl, appUrl);
        editor.commit();
    }

    public static String getFCMToken(Context context) {
        return context.getSharedPreferences(AppConstants.PREFERENCE_FCM_TOKEN, Context.MODE_PRIVATE).getString(AppConstants.FCM_TOKEN, null);
    }

    public static void saveFcmToken(Context context, String s) {
        SharedPreferences.Editor editor = context.getSharedPreferences(AppConstants.PREFERENCE_FCM_TOKEN, Context.MODE_PRIVATE).edit();
        editor.putString(AppConstants.FCM_TOKEN, s);
        editor.commit();
    }

    public static String getUser(Context context) {
        return context.getSharedPreferences(AppConstants.PREFERENCE_USER, Context.MODE_PRIVATE).
                getString(AppConstants.USERDETAILS, null);
    }

    public static void setUser(Context context, String s) {
        SharedPreferences.Editor editor = context.getSharedPreferences(AppConstants.PREFERENCE_USER, Context.MODE_PRIVATE).edit();
        editor.putString(AppConstants.USERDETAILS, s);
        editor.commit();
    }

    public static String getSetting(Context context) {
        return context.getSharedPreferences(AppConstants.PREFERENCE_SETTING, Context.MODE_PRIVATE).
                getString(AppConstants.SETTING, null);
    }

    public static void setSetting(Context context, String s) {
        SharedPreferences.Editor editor = context.getSharedPreferences(AppConstants.PREFERENCE_SETTING, Context.MODE_PRIVATE).edit();
        editor.putString(AppConstants.SETTING, s);
        editor.commit();
    }

    public static void setServiceFlag(Context context, String flag) {
        SharedPreferences.Editor editor = context.getSharedPreferences(AppConstants.PREFERENCE_USER, Context.MODE_PRIVATE).edit();
        editor.putString(AppConstants.ServiceType, flag);
        editor.commit();
    }


    public static String getLatLng(Context context) {
        return context.getSharedPreferences(AppConstants.PREFERENCE_USER, Context.MODE_PRIVATE).getString(AppConstants.LATLNG, null);
    }

    public static void setLatLng(Context context, String latlng) {
        SharedPreferences.Editor editor = context.getSharedPreferences(AppConstants.PREFERENCE_USER, Context.MODE_PRIVATE).edit();
        editor.putString(AppConstants.LATLNG, latlng);
        editor.commit();
    }

    public static String getServiceFlag(Context context) {
        return context.getSharedPreferences(AppConstants.PREFERENCE_USER, Context.MODE_PRIVATE).getString(AppConstants.ServiceType, null);
    }


    public static String getUserToken(Context context) {
        return context.getSharedPreferences(AppConstants.PREFERENCE_USER, Context.MODE_PRIVATE)
                .getString(AppConstants.Token, null);
    }

    public static void saveUserToken(Context context, String s) {
        SharedPreferences.Editor editor = context.getSharedPreferences(AppConstants.PREFERENCE_USER, Context.MODE_PRIVATE).edit();
        editor.putString(AppConstants.Token, s);
        editor.commit();
    }

    public static String getUserPhone(Context context) {
        return context.getSharedPreferences(AppConstants.PREFERENCE_USER, Context.MODE_PRIVATE)
                .getString(AppConstants.Phone, "null");
    }

    public static void saveUserPhone(Context context, String s) {
        SharedPreferences.Editor editor = context.getSharedPreferences(AppConstants.PREFERENCE_USER, Context.MODE_PRIVATE).edit();
        editor.putString(AppConstants.Phone, s);
        editor.commit();
    }

}
