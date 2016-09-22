package com.sifast.appsocle.tasks;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;

import com.firebase.client.core.Context;
import com.sifast.appsocle.models.User;
import com.sifast.appsocle.views.Home;

/**
 * Created by Asus on 05/09/2016.
 */
public class GoogleConnectionTask extends AsyncTask {
    private User authentifcatedUser;
    private Activity loginActicity;
    private SharedPreferences sharedPreferences;

    public GoogleConnectionTask(User authentifcatedUser,Activity loginActicity, SharedPreferences sharedPreferences) {
        this.loginActicity = loginActicity;
        this.authentifcatedUser= authentifcatedUser;
        this.sharedPreferences = sharedPreferences;
    }


    public void regiterUserSharePreferences(User authentifcatedUser){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(loginActicity.getApplicationContext());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("username", authentifcatedUser.getUsername());
        editor.putString("email", authentifcatedUser.getEmail());
        editor.commit();
        //opening the home activity
        Intent i = new Intent(loginActicity, Home.class);
        loginActicity.startActivity(i);
    }
    protected Object doInBackground(Object[] params) {
        regiterUserSharePreferences(this.authentifcatedUser);
        return null;
    }
}
