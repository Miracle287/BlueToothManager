package com.lzy.bluetoothtest.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * Toast封装
 */
public class ToastUtil {

    public static void shortToast(Context context, String str){
        if(context != null)
            Toast.makeText(context, str, Toast.LENGTH_SHORT).show();
    }

    public static void longToast(Context context, String str){
        if(context != null)
            Toast.makeText(context, str, Toast.LENGTH_LONG).show();
    }

}
