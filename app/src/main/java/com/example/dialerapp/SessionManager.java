package com.example.dialerapp;

import android.content.SharedPreferences;

class SessionManager {

    private static String LOGIN = "login";
    private static String OTP = "otp";
    private static String USERNAME = "username";
    private static String MOBILE = "mobile";
    private static String Customer_MOBILE = "customer_mobile";
    private static String DEVICE_TOKEN = "device_token";
    private static String BIDDER_ID = "bidder_id";
    private static String LMS_TYPE = "lms_type";
    private static String PRODUCT_TYPE = "product_type";
    private static String CALL_ID = "call_id";
    private static String ACCESS_TOKEN = "access_token";


    private static void savePreference(SharedPreferences prefs, String key, String value) {
        SharedPreferences.Editor e = prefs.edit();
        e.putString(key, value);
        e.apply();
    }

    static void dataclear(SharedPreferences prefs) {
        SharedPreferences.Editor e = prefs.edit();
        e.clear();
        e.apply();
    }

    static void save_login(SharedPreferences prefs, String value) {
        SessionManager.savePreference(prefs, LOGIN, value);
    }

    static String get_login(SharedPreferences prefs) {
        return prefs.getString(LOGIN, "");
    }

    static void save_username(SharedPreferences prefs, String value) {
        SessionManager.savePreference(prefs, USERNAME, value);
    }

    static String get_username(SharedPreferences prefs) {
        return prefs.getString(USERNAME, "");
    }


    static void save_mobile(SharedPreferences prefs, String value) {
        SessionManager.savePreference(prefs, MOBILE, value);
    }

    static String get_mobile(SharedPreferences prefs) {
        return prefs.getString(MOBILE, "");
    }

    static void save_customer_mobile(SharedPreferences prefs, String value) {
        SessionManager.savePreference(prefs, Customer_MOBILE, value);
    }

    static String get_customer_mobile(SharedPreferences prefs) {
        return prefs.getString(Customer_MOBILE, "");
    }

    static void save_device_token(SharedPreferences prefs, String value) {
        SessionManager.savePreference(prefs, DEVICE_TOKEN, value);
    }

    static String get_device_token(SharedPreferences prefs) {
        return prefs.getString(DEVICE_TOKEN, "");
    }

    static void save_otp(SharedPreferences prefs, String value) {
        SessionManager.savePreference(prefs, OTP, value);
    }

    static String get_otp(SharedPreferences prefs) {
        return prefs.getString(OTP, "");
    }

    static void save_bidder_id(SharedPreferences prefs, String value) {
        SessionManager.savePreference(prefs, BIDDER_ID, value);
    }

    static String get_bidder_id(SharedPreferences prefs) {
        return prefs.getString(BIDDER_ID, "");
    }

    static void save_lms_type(SharedPreferences prefs, String value) {
        SessionManager.savePreference(prefs, LMS_TYPE, value);
    }

    static String get_lms_type(SharedPreferences prefs) {
        return prefs.getString(LMS_TYPE, "");
    }

    static void save_product_type(SharedPreferences prefs, String value) {
        SessionManager.savePreference(prefs, PRODUCT_TYPE, value);
    }

    static String get_product_type(SharedPreferences prefs) {
        return prefs.getString(PRODUCT_TYPE, "");
    }


    static void save_call_id(SharedPreferences prefs, String value) {
        SessionManager.savePreference(prefs, CALL_ID, value);
    }

    static String get_call_id(SharedPreferences prefs) {
        return prefs.getString(CALL_ID, "");
    }

 static void save_access_token(SharedPreferences prefs, String value) {
        SessionManager.savePreference(prefs, ACCESS_TOKEN, value);
    }

    static String get_access_token(SharedPreferences prefs) {
        return prefs.getString(ACCESS_TOKEN, "");
    }

}
