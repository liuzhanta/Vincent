package com.zterry.imagepicker.util;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;

/**
 * Desc:
 * Author: Terry
 * Date:2016-03-15
 */
public class GetPhotoListCursor extends ImageQuery {


    public GetPhotoListCursor(Context context) {
        super(context);
    }

    @Override
    public Cursor query() {
        final String projection[] = new String[]{MediaStore.Images.Media._ID,//
                MediaStore.Images.Media.DATA,//
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
        };
        final Cursor mCursor = getContentResolver().query(IMAGE_URI, projection,
                MediaStore.Images.Media.MIME_TYPE + "=? or "
                        + MediaStore.Images.Media.MIME_TYPE + "=?",
                new String[]{"image/jpeg", "image/png"},
                MediaStore.Images.Media.DATE_TAKEN);
        return mCursor;
    }

}
