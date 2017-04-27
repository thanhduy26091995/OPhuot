package com.thanhduy.ophuot.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.thanhduy.ophuot.model.User;

import java.util.HashMap;

import static android.accounts.AccountManager.KEY_PASSWORD;

/**
 * Created by DOBYNT on 18/02/2017.
 */

public class SessionManagerUser {
    // Shared Preferences
    SharedPreferences pref;

    // Editor for Shared preferences
    SharedPreferences.Editor editor;

    // Context
    Context _context;

    // Shared pref mode
    int PRIVATE_MODE = 0;

    // Sharedpref file name
    private static final String PREF_NAME = "User";

    // All Shared Preferences Keys
    private static final String IS_LOGIN = "IsLoggedIn";

    public static final String KEY_ID = "id";
    public static final String KEY_NAME = "name";
    public static final String KEY_DEVICE_TOKEN = "deviceToken";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_AVATAR = "avatar";

    // Constructor
    public SessionManagerUser(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }


    /**
     * Create login session
     */
    public void createLoginSession(User user) {
        // Storing login value as TRUE
        editor.putBoolean(IS_LOGIN, true);
        // Storing in pref
        editor.putString(KEY_ID, user.getUid());
        editor.putString(KEY_NAME, user.getName());
        editor.putString(KEY_DEVICE_TOKEN, user.getDeviceToken());
        editor.putString(KEY_EMAIL, user.getEmail());
        editor.putString(KEY_AVATAR, user.getAvatar());
        // commit changes
        editor.commit();
    }

    /**
     * Get stored session data
     */
    public HashMap<String, String> getUserDetails() {
        HashMap<String, String> user = new HashMap<String, String>();
        // user name
        user.put(KEY_ID, pref.getString(KEY_ID, null));
        user.put(KEY_NAME, pref.getString(KEY_NAME, null));
        user.put(KEY_PASSWORD, pref.getString(KEY_PASSWORD, null));
        user.put(KEY_DEVICE_TOKEN, pref.getString(KEY_DEVICE_TOKEN, null));
        user.put(KEY_AVATAR, pref.getString(KEY_AVATAR, null));
        user.put(KEY_EMAIL, pref.getString(KEY_EMAIL, null));
        // return user
        return user;
    }

    /**
     * Clear session details
     */
    public void logoutUser() {
        // Clearing all data from Shared Preferences
        editor.clear();
        editor.commit();
    }

    /**
     * Quick check for login
     **/
    // Get Login State
    public boolean isLoggedIn() {
        return pref.getBoolean(IS_LOGIN, false);
    }
}
