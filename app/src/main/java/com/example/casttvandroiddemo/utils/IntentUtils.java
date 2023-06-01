package com.example.casttvandroiddemo.utils;

import android.content.Context;
import android.content.Intent;

public class IntentUtils {
    public static void goToActivity(Context context, Class<?> cls){
        context.startActivity(new Intent(context, cls));
    }
}
