package com.sifast.appsocle.tasks;

/**
 * Created by Asus on 09/07/2016.
 */

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

import com.sifast.appsocle.models.GMail;

import java.util.List;

public class MailSendingTask extends AsyncTask {

    private ProgressDialog statusDialog;

    @Override
    protected Object doInBackground(Object... args) {
        try {
            Log.i("MailSendingTask", "About to instantiate GMail...");
            publishProgress("Processing input....");
            GMail androidEmail = new GMail(args[0].toString(),
                    args[1].toString(), (List) args[2], args[3].toString(),
                    args[4].toString());
            publishProgress("Preparing mail message....");
            androidEmail.createEmailMessage();
            publishProgress("Sending email....");
            androidEmail.sendEmail();
            publishProgress("Email Sent.");
            Log.i("MailSendingTask", "Mail Sent.");
        } catch (Exception e) {
            publishProgress(e.getMessage());
            Log.e("MailSendingTask", e.getMessage(), e);
        }
        return null;
    }

    public ProgressDialog getStatusDialog() {
        return statusDialog;
    }

    public void setStatusDialog(ProgressDialog statusDialog) {
        this.statusDialog = statusDialog;
    }
}
