package com.sifast.appsocle.tasks;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;
import com.sifast.appsocle.R;

/**
 * Created by Asus on 29/07/2016.
 */
public class UserDataSettingsTask extends AsyncTask {
    String mail, password, username;
    Activity activity;

    public UserDataSettingsTask(String mail, String password, String username,Activity activity) {
        this.mail = mail;
        this.password = password;
        this.username = username;
        this.activity=activity;
    }

    public void resetData() {
        //setting connexion parameter
        String dbUsersUrl =activity.getResources().getString(R.string.dbUsersUrl);
        final Firebase ref = new Firebase(dbUsersUrl);
        Query query = ref.orderByChild("username").equalTo(username);

        query.addListenerForSingleValueEvent(new ValueEventListener() {

            //set the new password in the db
            @Override
            public void onDataChange(com.firebase.client.DataSnapshot snapshot) {
                for (com.firebase.client.DataSnapshot user : snapshot.getChildren()) {
                    Firebase statusRefPassword = user.child("password").getRef();
                    statusRefPassword.setValue(String.valueOf(password.hashCode()));
                    Firebase statusRefMail = user.child("email").getRef();
                    statusRefMail.setValue(mail);

                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.e("can't register","database problem");
            }


        });
    }

    @Override
    protected Object doInBackground(Object[] params) {
        resetData();
        return null;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
