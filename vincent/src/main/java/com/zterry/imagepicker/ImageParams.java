package com.zterry.imagepicker;

import android.graphics.Color;

/**
 * Description:  <br>
 * Author:Terry<br>
 * Date:2017/6/29 上午11:52
 */

public class ImageParams {
    public static final int MAX_SELECT_ITEM_COUNT = 9;
    public static final int IMAGE_GRID_LIST_PADDING = 4;

    public static int maxSelectCount = MAX_SELECT_ITEM_COUNT;
    public static int placeHolder = R.drawable.bg_photo_place_holder;
    public static int overMaxSelectCountMessage;
    public static boolean layoutBehavior = false;
    public static int colorPrimary = Color.parseColor("#F5F5F5");
    public static int titleColor = Color.BLACK;
    public static CharSequence title;
    public static int titleResId;
}
