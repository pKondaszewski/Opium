package com.example.op.exception;

import android.util.Log;

public class TooManyFitbitRequestsException extends RuntimeException {
    public TooManyFitbitRequestsException(String TAG, String message) {
        super(message);
        Log.e(TAG, message);
    }
}
