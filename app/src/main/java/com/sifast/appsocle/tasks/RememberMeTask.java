package com.sifast.appsocle.tasks;

import android.content.SharedPreferences;
import android.os.AsyncTask;

import com.sifast.appsocle.models.User;

/**
 * Created by Asus on 23/06/2016.
 */
public class RememberMeTask extends AsyncTask {
    private SharedPreferences sharedPreferences;
    private User authentifcatedUser;

    public RememberMeTask(SharedPreferences sharedPreferences, User authentifcatedUser) {
        this.authentifcatedUser = authentifcatedUser;
        this.sharedPreferences = sharedPreferences;
    }

    @Override
    protected Object doInBackground(Object[] params) {
        rememberMe();
        return null;
    }

    public void rememberMe() {
        SharedPreferences.Editor editor = this.sharedPreferences.edit();
        editor.putBoolean(authentifcatedUser.getUsername(), true);
        editor.putString(authentifcatedUser.getUsername() + "password", authentifcatedUser.getPassword());
        editor.putString(authentifcatedUser.getUsername() + "email", authentifcatedUser.getEmail());
        editor.commit();
    }

    public User getAuthentifcatedUser() {
        return authentifcatedUser;
    }

    public void setAuthentifcatedUser(User authentifcatedUser) {
        this.authentifcatedUser = authentifcatedUser;
    }

    public SharedPreferences getSharedPreferences() {
        return sharedPreferences;
    }

    public void setSharedPreferences(SharedPreferences sharedPreferences) {
        this.sharedPreferences = sharedPreferences;
    }
}
