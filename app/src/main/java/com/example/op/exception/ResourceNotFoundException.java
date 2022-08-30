package com.example.op.exception;

import android.util.Log;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String TAG, String message) {
        super(message);
        Log.e(TAG, message);
    }
}
