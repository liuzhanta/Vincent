package com.zterry.imagepicker.util;

import android.content.Context;

/**
 * Description:  <br>
 * Author:Terry<br>
 * Date:2017/6/29 下午12:00
 */

public class Utils {
    private Utils() {
        //no instance
    }

    public static int getScreenWidth(Context context) {
        return context.getResources().getDisplayMetrics().widthPixels;
    }

    public static int getScreenHeight(Context context) {
        return context.getResources().getDisplayMetrics().heightPixels;
    }
}
