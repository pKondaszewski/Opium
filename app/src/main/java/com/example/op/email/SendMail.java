package com.example.op.email;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.expertsystem.ResourceNotFoundException;

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
        } catch (MessagingException | ResourceNotFoundException e) {
            Log.e(TAG, String.format("EMail send to: %s ended with failure. Exception: " + e,
                    recipientEmailAddress));
        }
    }

    private void initSession() throws ResourceNotFoundException {
        SharedPreferences sharPref = context.getSharedPreferences(context.getString(com.example.database.R.string.opium_preferences), Context.MODE_PRIVATE);
        email = sharPref.getString("gmail_address", null);
        if (email == null) {
            throw new ResourceNotFoundException(TAG, "There are no email address");
        }
        password = sharPref.getString("gmail_password", null);
        if (password == null) {
            throw new ResourceNotFoundException(TAG, "There are no email password");
        }
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
