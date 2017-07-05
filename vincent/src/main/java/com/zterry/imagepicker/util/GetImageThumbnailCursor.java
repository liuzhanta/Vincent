package com.zterry.imagepicker.util;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

/**
 * Desc:
 * Author: Terry
 * Date:2016-03-15
 */
public class GetImageThumbnailCursor extends ImageQuery {

    public GetImageThumbnailCursor(Context context) {
        super(context);
    }

    @Override
    public Cursor query() {
        final Uri mImageUri = MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI;
        final String[] projection = new String[]{MediaStore.Images.Thumbnails.DATA, MediaStore.Images.Thumbnails.IMAGE_ID};
        final Cursor thumbCursor = MediaStore.Images.Thumbnails.query(getContentResolver(), mImageUri, projection);
        return thumbCursor;
    }
}
