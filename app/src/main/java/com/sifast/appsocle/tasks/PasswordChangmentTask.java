package com.sifast.appsocle.tasks;

import android.app.Activity;
import android.os.AsyncTask;

import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;
import com.sifast.appsocle.R;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Asus on 20/07/2016.
 */
public class PasswordChangmentTask extends AsyncTask {
    Activity loginActicity;
    String mail;
    private SecureRandom random = new SecureRandom();

    public PasswordChangmentTask(Activity loginActicity, String mail) {
        this.loginActicity = loginActicity;
        this.mail = mail;
    }

    // function to generate passwords
    int numBits=20;
    public String generatePassword() {
        return new BigInteger(numBits, random).toString(numBits);
    }

    public void resetPassword() {
        String dbUsersUrl  =loginActicity.getResources().getString(R.string.dbUsersUrl);;
        //setting connexion parameter
        final Firebase ref = new Firebase(dbUsersUrl);
        Query query = ref.orderByChild("email").equalTo(mail);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            String newPass = generatePassword();

            //set the new password in the db
            @Override
            public void onDataChange(com.firebase.client.DataSnapshot snapshot) {
                for (com.firebase.client.DataSnapshot user : snapshot.getChildren()) {

                    Firebase statusRef = user.child("password").getRef();
                    statusRef.setValue(String.valueOf(newPass.hashCode()));
                    //sending mail in which we find the new password
                    String sendTO = mail;
                    List<String> toEmailList = Arrays.asList(sendTO
                            .split("\\s*,\\s*"));
                    //set mail's data
                    String emailResetPassswordtext1 = loginActicity.getResources().getString(R.string.emailResetPassswordtext1);
                    String emailResetPassswordtext2 = loginActicity.getResources().getString(R.string.emailResetPassswordtext2);
                    String email = loginActicity.getResources().getString(R.string.email);
                    String passwordMail = loginActicity.getResources().getString(R.string.passwordMail);
                    String subject = loginActicity.getResources().getString(R.string.resetPasswordMailSubjet);
                    new MailSendingTask().execute(email, passwordMail, toEmailList, subject, emailResetPassswordtext1 + newPass + emailResetPassswordtext2);
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
            }
        });
    }

    @Override
    protected Object doInBackground(Object[] params) {
        resetPassword();
        return null;
    }
}
