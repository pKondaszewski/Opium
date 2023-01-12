package com.example.expertsystem;

import lombok.Getter;

public class ResourceNotFoundException extends Exception {
    @Getter
    private final String TAG;
    @Getter
    private final String message;

    public ResourceNotFoundException(String TAG, String message) {
        super();
        this.TAG = TAG;
        this.message = message;
    }
}
