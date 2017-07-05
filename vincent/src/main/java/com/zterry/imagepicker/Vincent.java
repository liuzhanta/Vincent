package com.zterry.imagepicker;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.Nullable;

import com.zterry.imagepicker.bean.ImageFile;

import java.lang.ref.WeakReference;
import java.util.List;

import static com.zterry.imagepicker.util.Constants.EXTRA_KEY_SELECTED_IMAGES;

/**
 * Description:  <br>
 * Author:Terry<br>
 * Date:2017/6/20 下午4:43
 */

public class Vincent {

    public static final int REQ_CODE_IMAGE_PICK = 0x18;

    private final WeakReference<Activity> mContext;

    public Vincent(Activity activity) {
        mContext = new WeakReference<>(activity);
    }

    public static Vincent from(Activity a) {
        return new Vincent(a);
    }

    public Vincent toPicker() {
        Intent intent = new Intent(mContext.get(), ImagePickerActivity.class);
        if (mContext.get() != null) {
            mContext.get().startActivityForResult(intent, REQ_CODE_IMAGE_PICK);
        }
        return this;
    }

    public Vincent placeHolder(int drawableId) {
        ImageParams.placeHolder = drawableId;
        return this;
    }

    public Vincent maxSelectCount(int maxSelectCount) {
        ImageParams.maxSelectCount = maxSelectCount;
        return this;
    }

    public Vincent overMaxSelectCountMessage(int resId) {
        ImageParams.overMaxSelectCountMessage = resId;
        return this;
    }

    public Vincent colorPrimary(int color) {
        ImageParams.colorPrimary = color;
        return this;
    }

    public Vincent layoutBehavior(boolean behavior) {
        ImageParams.layoutBehavior = behavior;
        return this;
    }

    public static boolean isFromPicker(int requestCode) {
        return requestCode == REQ_CODE_IMAGE_PICK;
    }

    @Nullable
    public static List<ImageFile> handleActivityResult(Intent data) {
        if (data != null) {
            List<ImageFile> imageFiles = (List<ImageFile>) data.getSerializableExtra(
                    EXTRA_KEY_SELECTED_IMAGES);
            return imageFiles;
        }
        return null;
    }

    public static boolean hasResult(int requestCode, int resultCode) {
        return requestCode == REQ_CODE_IMAGE_PICK && resultCode == Activity.RESULT_OK;
    }

    public Vincent titleColor(int titleColor) {
        ImageParams.titleColor = titleColor;
        return this;
    }

    public Vincent title(int titleResId) {
        ImageParams.titleResId = titleResId;
        return this;
    }

    public Vincent title(CharSequence title) {
        ImageParams.title = title;
        return this;
    }
}
