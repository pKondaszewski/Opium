package com.example.op.exception;

import android.util.Log;

public class UnauthorizedFitbitException extends RuntimeException {
    public UnauthorizedFitbitException(String TAG, String message) {
        super(message);
        Log.e(TAG, message);
    }
}
