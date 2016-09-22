package com.sifast.appsocle.tasks;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;
import com.firebase.client.core.Context;
import com.sifast.appsocle.R;
import com.sifast.appsocle.models.User;
import com.sifast.appsocle.views.Home;


/**
 * Created by Asus on 14/06/2016.
 */
public class ConnectionTask extends AsyncTask {

    private Context context;

    // TODO this attributes should be remouved
    private User user2;
    private String test;

    private User authentifcatedUser;
    private Activity loginActicity;
    private ProgressDialog progress;
    private SharedPreferences sharedPreferences;

    public ConnectionTask(User authentifcatedUser, Activity loginAct, SharedPreferences sharedPreferences) {
        this.sharedPreferences = sharedPreferences;
        this.authentifcatedUser = authentifcatedUser;
        this.loginActicity = loginAct;
    }

    @Override
    protected Object doInBackground(Object[] params) {

        connect();

        return test;
    }

    public String connect() {
        //setting connexion parameter
        // rename it "restWsUrl"
        String restWsUrl =loginActicity.getResources().getString(R.string.dbUsersUrl);
        Query query = new Firebase(restWsUrl).orderByChild("username").equalTo(authentifcatedUser.getUsername());

        //dialog to say to the user that he must wait
        loginActicity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progress = ProgressDialog.show(loginActicity, "Authentification",
                        "wait ...", true);
            }
        });

        //get the data from th DB
        query.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //checking if the user exist
                if (dataSnapshot.exists()) {
                    for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                        //get each user which has the target username
                        final User user = userSnapshot.getValue(User.class);
                        authentifcatedUser.setEmail(user.getEmail());
                        //checkinng if the password is not true
                        if (!user.getPassword().equals(String.valueOf(authentifcatedUser.getPassword().hashCode()))) {
                            loginActicity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    AlertDialog.Builder alert = new AlertDialog.Builder(loginActicity);
                                    alert.setTitle("Not auentificated  ");
                                    alert.setMessage("Check your password");
                                    alert.show();
                                }
                            });
                        }

                        //if the password is true , the data will be storaged in the sharedPreferences file and a Home activity will be launched

                        else {

                            //launching the Home activity
                            //submiting data in the sharedpreferences file
                            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(loginActicity.getApplicationContext());
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("username", authentifcatedUser.getUsername());
                            editor.putString("password", authentifcatedUser.getPassword());
                            editor.putString("email", authentifcatedUser.getEmail());
                            editor.commit();
                            //opening the home activity
                            Intent i = new Intent(loginActicity, Home.class);
                            loginActicity.startActivity(i);

                        }


                        //dismissing the dialog
                        loginActicity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                progress.dismiss();
                            }
                        });

                    }
                }

                //if the user do not exist
                else {

                    //dismissing the dialog
                    loginActicity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progress.dismiss();
                        }
                    });

                    //alert that username do not exist
                    loginActicity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            AlertDialog.Builder alert = new AlertDialog.Builder(loginActicity);
                            alert.setTitle("Sorry ");
                            alert.setMessage("The username do not exist");
                            alert.show();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.e("Can't connect ","Connexion problems");
            }
        });

        return test;
    }


    public User getAuthentifcatedUser() {
        return authentifcatedUser;
    }

    public void setAuthentifcatedUser(User authentifcatedUser) {
        this.authentifcatedUser = authentifcatedUser;
    }

    public String getTest() {
        return test;
    }

    public void setTest(String test) {
        this.test = test;
    }

    public User getUser() {
        return this.user2;
    }

    public Activity getLoginActicity() {
        return loginActicity;
    }

    public void setLoginActicity(Activity loginActicity) {
        this.loginActicity = loginActicity;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public User getUser2() {
        return user2;
    }

    public void setUser2(User user2) {
        this.user2 = user2;
    }

    public ProgressDialog getProgress() {
        return progress;
    }

    public void setProgress(ProgressDialog progress) {
        this.progress = progress;
    }

    public SharedPreferences getSharedPreferences() {
        return sharedPreferences;
    }

    public void setSharedPreferences(SharedPreferences sharedPreferences) {
        this.sharedPreferences = sharedPreferences;
    }
}
