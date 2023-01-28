package com.example.op.utils;

import android.content.Context;
import android.content.Intent;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Start {
    public static void activity(Context context, Class activityClass) {
        Intent intent = new Intent(context, activityClass);
        context.startActivity(intent);
    }

    public static void service(Context context, Class serviceClass) {
        Intent intent = new Intent(context, serviceClass);
        context.startService(intent);
    }
}
