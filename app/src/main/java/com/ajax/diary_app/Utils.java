package com.ajax.diary_app;

import android.app.Activity;
import android.widget.Toast;

class Utils {
    static void showToast(Activity activity, String msg) {
        Toast error = Toast.makeText(activity, msg, Toast.LENGTH_LONG);
        error.show();
    }

}
