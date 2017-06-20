package com.zterry.library.photopicker.ui;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.view.View;

import com.huoban.model2.ChatAttachment;
import com.huoban.photopicker.ui.base.AbstractViewLargerImageActivity;
import com.huoban.photopicker.util.Constants;

import java.util.ArrayList;
import java.util.List;

/**
 * Desc:查看大图
 * Author: Terry
 * Date:2016-02-24
 */
public class ViewLargerImageActivity extends AbstractViewLargerImageActivity implements Constants {

    public static final String TAG = ViewLargerImageActivity.class.getSimpleName();

    private int currentImageIndex;

    private ArrayList<String> imageList;
    private ArrayList<String> selectedImageList;

    public static final String EXTRA_KEY_CURRENT_IMAGE_INDEX = "EXTRA_KEY_CURRENT_IMAGE_INDEX";
    public static final String EXTRA_KEY_IMAGE_LIST = "EXTRA_KEY_IMAGE_LIST";

    /**
     * 图片浏览模式
     *
     * @see AbstractViewLargerImageActivity
     */
    private int viewMode = VIEW_MODE_GALLERY;
    private int galleryType;
    private boolean mAllowUpdate;


    public static final String EXTRA_KEY_VIEW_MODE = "EXTRA_KEY_VIEW_MODE";
    public static final String EXTRA_KEY_GALLERY_TYPE = "EXTRA_KEY_GALLERY_TYPE";
    public static final String EXTRA_KEY_USER_ALLOW_UPDATE = "EXTRA_ALLOW_UPDATE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        initIntentData();
        super.onCreate(savedInstanceState);
        super.initData();
    }

    private void initIntentData() {
        galleryType = getIntent().getIntExtra(EXTRA_KEY_GALLERY_TYPE, GALLERY_TYPE_NORMAL);
        viewMode = getIntent().getIntExtra(EXTRA_KEY_VIEW_MODE, VIEW_MODE_GALLERY);
        imageList = (ArrayList<String>) getIntent().getSerializableExtra(EXTRA_KEY_IMAGE_LIST);
        selectedImageList = (ArrayList<String>) getIntent().getSerializableExtra(EXTRA_KEY_SELECTED_IMAGES);
        currentImageIndex = getIntent().getIntExtra(EXTRA_KEY_CURRENT_IMAGE_INDEX, 0);
        mAllowUpdate = getIntent().getBooleanExtra(EXTRA_KEY_USER_ALLOW_UPDATE, true);
        setAllowUpdate(mAllowUpdate);
    }

    @Override
    protected void onCompleteButtonClick(View v) {
        ArrayList<String> selectedPhotoPathList = getSelectedImageStringList();
        ArrayList<ChatAttachment> data = new ArrayList<>();
        for (String uri : selectedPhotoPathList) {
            ChatAttachment chatattachment = new ChatAttachment();
            chatattachment.setLocalLink(uri);
            data.add(chatattachment);
        }
        Intent intent = new Intent();
        intent.setAction(ItemGalleryActivity.ACTION_FROM_PICK_IMAGES);
        intent.putExtra(MultiSelectImageActivity.EXTRA_KEY_ATTACHMENTS, data);
        setResult(RESULT_CODE_COMPLETE_PICK, intent);
        finish();
    }

    public static final int RESULT_CODE_COMPLETE_PICK = 185;

    @Override
    public int getImageTotalCount() {
        if (viewMode == VIEW_MODE_SINGLE) {
            return selectedImageList == null ? 0 : selectedImageList.size();
        }
        return imageList == null ? 0 : imageList.size();
    }

    @Override
    public int getCurrentImageIndex() {
        return currentImageIndex;
    }

    @Override
    protected List<String> getAllImageList() {
        return imageList;
    }

    @Override
    protected ArrayList<String> getSelectedImageList() {
        return selectedImageList;
    }

    @Override
    protected int getCurrentViewMode() {
        return viewMode;
    }

    @Override
    protected int getMaxImageCount() {
        return (viewMode == VIEW_MODE_SINGLE) ? SINGLE_MODE_IMAGE_COUNT : Constants.MAX_IMAGE_UPLOAD_LIMIT_COUNT;
    }

    @Override
    protected int getCurrentGalleryType() {
        return galleryType;
    }

    public static void start(Context context, ArrayList<String> imageUriList, int viewMode,
                             int currentIndex, boolean allowUpdate, View targetView,
                             String transitionName) {
        Intent intent = new Intent(context, ViewLargerImageActivity.class);
        intent.putExtra(ViewLargerImageActivity.EXTRA_KEY_SELECTED_IMAGES, imageUriList);
        intent.putExtra(ViewLargerImageActivity.EXTRA_KEY_VIEW_MODE, viewMode);
        intent.putExtra(ViewLargerImageActivity.EXTRA_KEY_CURRENT_IMAGE_INDEX, currentIndex);
        intent.putExtra(ViewLargerImageActivity.EXTRA_KEY_USER_ALLOW_UPDATE, allowUpdate);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            ActivityOptions transitionActivityOptions = ActivityOptions.makeSceneTransitionAnimation((Activity) context,
                    targetView, transitionName);
            ActivityCompat.startActivity(context, intent, transitionActivityOptions.toBundle());
        } else {
            ActivityOptionsCompat compat = ActivityOptionsCompat.makeScaleUpAnimation(targetView,
                    targetView.getWidth() / 2, targetView.getHeight() / 2, 0, 0);
            ActivityCompat.startActivity(context, intent,
                    compat.toBundle());
        }
    }

    public static void startForViewSingleImage(Context context, String imageUrl, View targetView,
                                               String transitionName) {
        ArrayList<String> imageUrlList = new ArrayList<>(1);
        imageUrlList.add(imageUrl);
        start(context, imageUrlList, AbstractViewLargerImageActivity.VIEW_MODE_SINGLE, 0,
                false, targetView, transitionName);

    }
}
