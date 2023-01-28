package com.example.op.email;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.example.database.SecureSharedPreferences;
import com.example.op.R;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SendMailTask extends AsyncTask<String, Integer, Void> {
    private static final String TAG = SendMailTask.class.getName();
    private final Context context;
    private final String recipientEmailAddress, subject, message;
    private final boolean isEmergency;
    private Handler h;
    private MimeMessage mimeMessage;
    private String emailAddress, emailPassword;
    private Session session;

    @Override
    protected void onPreExecute() {
        try {
            initData();
            composeMimeMessage();
        } catch (MessagingException e) {
            Log.e(TAG, "There are unresolved error with preparing mail data.", e);
        }
    }

    private void initData() {
        h = new Handler(context.getMainLooper());
        String opiumPreferences = context.getString(com.example.database.R.string.opium_preferences);
        SharedPreferences sharPref = context.getSharedPreferences(opiumPreferences, Context.MODE_PRIVATE);
        SharedPreferences secureSharPref = SecureSharedPreferences.get(context, opiumPreferences);
        emailAddress = sharPref.getString(context.getString(com.example.database.R.string.gmail_address), null);
        if (emailAddress == null) {
            Log.i(TAG, "There are no email address");
        }
        emailPassword = secureSharPref.getString(context.getString(com.example.database.R.string.gmail_password), null);
        if (emailPassword == null) {
            Log.i(TAG, "There are no email password");
        }
        session = Session.getDefaultInstance(SendMailConfig.getPropertiesSetup(),
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(emailAddress, emailPassword);
                    }
                });
    }

    private void composeMimeMessage() throws MessagingException {
        mimeMessage = new MimeMessage(session);
        mimeMessage.setFrom(new InternetAddress(emailAddress));
        mimeMessage.addRecipients(
                Message.RecipientType.TO, String.valueOf(new InternetAddress(recipientEmailAddress)));
        mimeMessage.setSubject(subject);
        mimeMessage.setText(message);
    }

    @Override
    protected Void doInBackground(String... strings) {
        try {
            Transport.send(mimeMessage);
        } catch (MessagingException e) {
            h.post(() -> {
                if (!isEmergency) {
                    Toast.makeText(context, context.getString(R.string.email_report_failure), Toast.LENGTH_LONG).show();
                }
                Log.e(TAG, "Email report sent failure", e);
            });
            return null;
        }
        if (!isEmergency) {
            h.post(() -> {
                Toast.makeText(context, context.getString(R.string.email_report_success), Toast.LENGTH_LONG).show();
                Log.i(TAG, "Email report sent successfully");
            });
        }
        return null;
    }
}
