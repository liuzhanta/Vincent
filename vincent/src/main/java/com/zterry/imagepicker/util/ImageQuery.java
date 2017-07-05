package com.zterry.imagepicker.util;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

/**
 * Desc:
 * Author: Terry
 * Date:2016-03-15
 */
public abstract class ImageQuery {

    public static final Uri IMAGE_URI = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

    private Context context;

    public ContentResolver getContentResolver() {
        return contentResolver;
    }

    private ContentResolver contentResolver;

    public ImageQuery(Context context) {
        this.context = context;
        this.contentResolver = context.getContentResolver();
    }

    public static String getString(Cursor cursor, String columnName) {
        return cursor.getString(cursor.getColumnIndex(columnName));
    }

    public static int getInt(Cursor cursor, String columnName) {
        return cursor.getInt(cursor.getColumnIndex(columnName));
    }

    public static boolean isCursorEmpty(Cursor cursor) {
        return cursor == null || cursor.getCount() == 0;
    }

    public abstract Cursor query();
}
