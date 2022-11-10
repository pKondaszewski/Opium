package com.example.op.email;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.example.op.R;

import java.util.Map;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SendMail implements Runnable {

    private static final String TAG = SendMail.class.getName();
    private final Context context;
    private final String recipientEmailAddress, subject, message;
    private String email, password;
    private Session session;

    @Override
    public void run() {
        try {
            initSession();
            composeMimeMessage(session);
        } catch (MessagingException e) {
            Log.e(TAG, String.format("EMail send to: %s ended with failure. Exception: " + e,
                    recipientEmailAddress));
        }
    }

    private void initSession() {
        SharedPreferences sharPref = context.getSharedPreferences(context.getString(R.string.opium_preferences), Context.MODE_PRIVATE);
        email = sharPref.getString("gmail_address", null);
        password = sharPref.getString("gmail_password", null);
        session = Session.getDefaultInstance(SendMailConfig.getPropertiesSetup(),
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(email, password);
                    }
                });
    }

    private void composeMimeMessage(Session session) throws MessagingException {
        MimeMessage mimeMessage = new MimeMessage(session);
        mimeMessage.setFrom(new InternetAddress(email));
        mimeMessage.addRecipients(
                Message.RecipientType.TO, String.valueOf(new InternetAddress(recipientEmailAddress)));
        mimeMessage.setSubject(subject);
        mimeMessage.setText(message);
        Transport.send(mimeMessage);
    }
}
