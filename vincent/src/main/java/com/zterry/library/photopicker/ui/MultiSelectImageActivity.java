package com.zterry.library.photopicker.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import com.huoban.R;
import com.huoban.model2.ChatAttachment;
import com.huoban.photopicker.adapter.MultiSelectPhotoAdapter;
import com.huoban.photopicker.bean.ImageFile;
import com.huoban.photopicker.bean.PhotoAlbum;
import com.huoban.photopicker.util.AsyncGetPhotoTask;
import com.huoban.photopicker.util.Constants;
import com.huoban.photopicker.view.PhotoAlbumPopWindow;
import com.huoban.tools.HBUtils;
import com.huoban.tools.LogUtil;
import com.huoban.tools.WeakHandler;
import com.huoban.ui.activity.ItemActivity;
import com.huoban.ui.activity.ItemRichTextNewActivity;
import com.huoban.ui.activity.TakePhotoActivity;
import com.podio.sdk.domain.field.ImageField;

import java.util.ArrayList;
import java.util.List;


/**
 * 多图选择 Created by Terry
 */
public class MultiSelectImageActivity extends TakePhotoActivity implements OnClickListener, AdapterView.OnItemClickListener {

    public static final String TAG = MultiSelectImageActivity.class.getSimpleName();
    public static final String EXTRA_KEY_TAKE_PHOTO = "EXTRA_KEY_TAKE_PHOTO";
    public static final String EXTRA_KEY_OPEN_GALLERY = "EXTRA_KEY_OPEN_GALLERY";
    public static final int POSITION_OPEN_CAMERA = 0;
    public static final int REQ_CODE_SELECT_IMAGES = 0X19;
    public static final String EXTRA_KEY_ATTACHMENTS = "EXTRA_KEY_ATTACHMENTS";
    private static final int MSG_DATA_OK = 666;
    private static final int MSG_DATA_FAILED = 777;
    private static final int REQ_CODE_SELECT_IMAGE = 0X18;
    private GridView mGridView;
    private MultiSelectPhotoAdapter mAdapter;
    private TextView mCompleteButton;
    private TextView mPhotoAlbumTextView;
    private TextView mPreviewTextView;
    private PhotoAlbumPopWindow mPhotoAlbumPopWindow;
    private List<PhotoAlbum> mPhotoAlbumList;
    private boolean intentFromRichEditor = false;

    private WeakHandler mHandler = new WeakHandler(this) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == MSG_DATA_OK) {
                setHidenEmptyView();
                mPhotoAlbumPopWindow.setData(mPhotoAlbumList);
                mPhotoAlbumTextView.setText(mPhotoAlbumList.get(0).getName());
                mAdapter.setData(mPhotoAlbumList.get(0).getImageFiles());
            } else {
                setEmptyView();
            }
        }
    };
    private ImageField mImageField;
    private int mFieldStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.initToolBarWithTitle(R.string.title_activity_select_photo);
        if (TextUtils.isEmpty(getIntent().getAction())) {
            mImageField = (ImageField) getIntent().getSerializableExtra(ItemActivity.PARAM_FIELD_UPDATE);
            mFieldStatus = getIntent().getIntExtra(ItemGalleryActivity.PARAM_KEY_FIELD_STATUS, 0);
        } else {
            if (ItemRichTextNewActivity.ACTION_FROM_RICH_EDITOR.equals(getIntent().getAction())) {
                intentFromRichEditor = true;
            }
        }
        initView();
        initGridView();
        initPopUpWindow();
        loadData();
    }

    public static void start(Context context) {
        Intent starter = new Intent(context, MultiSelectImageActivity.class);
        ((Activity) context).startActivityForResult(starter, REQ_CODE_SELECT_IMAGES);
    }

    private void initPopUpWindow() {
        mPhotoAlbumPopWindow = new PhotoAlbumPopWindow(this);
        mPhotoAlbumPopWindow.setOnPhotoAlbumItemClickListener(new PhotoAlbumPopWindow.OnPhotoAlbumItemClickListener() {
            @Override
            public void OnPhotoAlbumItemClick(PhotoAlbum album) {
                if (album == null) {
                    return;
                }
                mPhotoAlbumTextView.setText(album.getName());
                mAdapter.setData(album.getImageFiles());
                mGridView.smoothScrollToPosition(0);
            }
        });
    }

    private void initView() {
        mCompleteButton = (TextView) findViewById(R.id.tv_complete);

        updateCompleteButton(0);

        mPhotoAlbumTextView = (TextView) findViewById(R.id.tv_album_name);
        mPreviewTextView = (TextView) findViewById(R.id.tv_preview);
        updatePreviewButton(0);

        mPreviewTextView.setOnClickListener(this);
        mPhotoAlbumTextView.setOnClickListener(this);
        mCompleteButton.setOnClickListener(this);
    }

    private void loadData() {
        showLoadingView();
        new AsyncGetPhotoTask(this).start(new AsyncGetPhotoTask.IPhotoAlbumListGet() {

            @Override
            public void onPhotoAlbumListGet(List<PhotoAlbum> photoAlba) {
                if (!HBUtils.isEmpty(photoAlba)) {
                    List<ImageFile> allAlbumList = new ArrayList<>();
                    for (PhotoAlbum photoAlbum : photoAlba) {
                        allAlbumList.addAll(photoAlbum.getImageFiles());
                    }
                    PhotoAlbum pa = new PhotoAlbum();
                    pa.setName(getString(R.string.all_images));
                    pa.setCount(allAlbumList.size() - 1);
                    pa.setAllImages(allAlbumList);
                    pa.setFirstImagePath(allAlbumList.get(0).getUri());
                    photoAlba.add(0, pa);

                    allAlbumList.add(0, new ImageFile(MultiSelectPhotoAdapter.CAMERA_POS));
                    mPhotoAlbumList = photoAlba;
                    mHandler.sendEmptyMessage(MSG_DATA_OK);
                } else {
                    mHandler.sendEmptyMessage(MSG_DATA_FAILED);
                }
            }
        });
    }

    private void initGridView() {
        mAdapter = new MultiSelectPhotoAdapter(this);
        mGridView = (GridView) findViewById(R.id.gridView);
        mGridView.setAdapter(mAdapter);
        mGridView.setOnItemClickListener(this);
        mAdapter.setOnImageSelectedListener(new MultiSelectPhotoAdapter.OnImageSelectedListener() {
            @Override
            public void onImageSelected(int selectedImagesTotalCount) {
                updateCompleteButton(selectedImagesTotalCount);
                updatePreviewButton(selectedImagesTotalCount);
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mPhotoAlbumPopWindow.isShowing()) {
                mPhotoAlbumPopWindow.dismiss();
                return false;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected int getContentViewId() {
        return R.layout.activity_select_photo;
    }

    /**
     * 相册点击
     *
     * @param v
     */
    private void onPhotoAlbumClick(View v) {
        showAlbumDialog(v);
    }

    /**
     * 预览点击
     */
    private void onPreviewButtonClick() {
        jumpToViewLargerImageActivity(mAdapter.getSelectedImageFileList(),//
                mAdapter.getSelectedImageFileList(),//
                0, ViewLargerImageActivity.VIEW_MODE_GALLERY,//
                ViewLargerImageActivity.GALLERY_TYPE_NORMAL);
    }

    private void showAlbumDialog(View v) {
        mPhotoAlbumPopWindow.show(v);
    }

    /**
     * 完成按钮点击
     */
    private void onCompleteButtonClick() {
        ArrayList<ChatAttachment> data = getSelectedImages();
        Intent intent = new Intent();
        if (intentFromRichEditor) {
            intent.putExtra(EXTRA_KEY_ATTACHMENTS, data);
        } else {
            intent.setClass(MultiSelectImageActivity.this, ItemGalleryActivity.class);
            intent.setAction(ItemGalleryActivity.ACTION_FROM_PICK_IMAGES);
            intent.putExtra(EXTRA_KEY_ATTACHMENTS, data);
            intent.putExtra(ItemActivity.PARAM_FIELD_UPDATE, mImageField);
            intent.putExtra(ItemGalleryActivity.PARAM_KEY_FIELD_STATUS, mFieldStatus);
        }
        setResult(RESULT_OK, intent);
        finish();
    }

    @NonNull
    private ArrayList<ChatAttachment> getSelectedImages() {
        ArrayList<ImageFile> selectedPhotoPathList = mAdapter.getSelectedImageFileList();
        ArrayList<ChatAttachment> data = new ArrayList<>();
        for (ImageFile imageFile : selectedPhotoPathList) {
            ChatAttachment chatattachment = new ChatAttachment();
            chatattachment.setLocalLink(imageFile.getUri());
            data.add(chatattachment);
        }
        return data;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (mAdapter.containsCameraItem()) {
            if (position == POSITION_OPEN_CAMERA) {
                takePhoto();
            } else {
                ArrayList<ImageFile> data = new ArrayList<>(mAdapter.getData());
                data.remove(0);
                jumpToViewLargerImageActivity(data, mAdapter.getSelectedImageFileList(),//
                        position - 1, ViewLargerImageActivity.VIEW_MODE_GALLERY, ViewLargerImageActivity.GALLERY_TYPE_NORMAL);
            }
        } else {
            jumpToViewLargerImageActivity((ArrayList<ImageFile>) mAdapter.getData(),//
                    mAdapter.getSelectedImageFileList(), position, ViewLargerImageActivity.VIEW_MODE_GALLERY, ViewLargerImageActivity.GALLERY_TYPE_NORMAL);
        }
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED);
        finish();
        super.onBackPressed();

    }

    @Override
    public boolean isMultiSelectPhotoMode() {
        return true;
    }

    /**
     * jump to the activity of view larger image
     *
     * @param images
     * @param selectedImages
     * @param position
     * @param viewMode
     */
    private void jumpToViewLargerImageActivity(ArrayList<ImageFile> images, ArrayList<ImageFile> selectedImages, int position, int viewMode, int galleryType) {
        Intent intent = new Intent();
        intent.setClass(this, ViewLargerImageActivity.class);
        intent.putExtra(ViewLargerImageActivity.EXTRA_KEY_VIEW_MODE, viewMode);
        intent.putExtra(ViewLargerImageActivity.EXTRA_KEY_GALLERY_TYPE, galleryType);
        intent.putExtra(ViewLargerImageActivity.EXTRA_KEY_CURRENT_IMAGE_INDEX, position);

        intent.putExtra(ViewLargerImageActivity.EXTRA_KEY_SELECTED_IMAGES, convertToImagePathList(selectedImages));
        intent.putExtra(ViewLargerImageActivity.EXTRA_KEY_IMAGE_LIST, convertToImagePathList(images));
        startActivityForResult(intent, REQ_CODE_SELECT_IMAGE);
    }

    /**
     * Convert List<T> which Type parameters is ImageFile to String.
     *
     * @param images
     * @return
     */
    private ArrayList<String> convertToImagePathList(ArrayList<ImageFile> images) {
        ArrayList<String> imagelist = new ArrayList<>();
        if (HBUtils.isEmpty(images)) {
            return imagelist;
        }
        for (ImageFile image : images) {
            imagelist.add(image.getUri());
        }
        return imagelist;
    }

    /**
     * 当拍完照之后图片返回
     *
     * @param attachment
     */
    @Override
    protected void handlePhoto(ChatAttachment attachment) {
        super.handlePhoto(attachment);
        LogUtil.d(TAG, "handlePhoto--->");
        LogUtil.d(TAG, "ChatAttachment :" + attachment);

        Intent intent = new Intent();
        intent.setClass(this, ViewLargerImageActivity.class);
        ArrayList<String> data = new ArrayList<>();
        data.add(attachment.getLocalLink());
        intent.putExtra(Constants.EXTRA_KEY_SELECTED_IMAGES, data);
        intent.putExtra(ViewLargerImageActivity.EXTRA_KEY_VIEW_MODE, ViewLargerImageActivity.VIEW_MODE_SINGLE);
        intent.putExtra(EXTRA_KEY_TAKE_PHOTO, TAKE_PHOTO_REQUEST_CODE);
        startActivityForResult(intent, REQ_CODE_SELECT_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        LogUtil.d(TAG, "onActivityResult: ");
        if (requestCode == REQ_CODE_SELECT_IMAGE) {
            if (resultCode == ViewLargerImageActivity.RESULT_CODE_COMPLETE_PICK) {
                setResult(RESULT_OK, data);
                finish();
            } else if (resultCode == RESULT_OK) {
                ArrayList<String> selectedImageList = (ArrayList<String>) data.getSerializableExtra(Constants.EXTRA_KEY_SELECTED_IMAGES);
                LogUtil.d(TAG, "selectedImageList = " + selectedImageList);
                ArrayList<ImageFile> imageFilesList = new ArrayList<>();
                if (!HBUtils.isEmpty(selectedImageList)) {
                    for (String s : selectedImageList) {
                        imageFilesList.add(new ImageFile(s));
                    }
                }

                mAdapter.setSelectedImageList(imageFilesList);
                updateCompleteButton(selectedImageList.size());
                updatePreviewButton(selectedImageList.size());
            }
        }
    }

    /**
     * update complete button state with the number of selected images
     *
     * @param selectedImageCount
     */
    public void updateCompleteButton(int selectedImageCount) {
        if (selectedImageCount > 0) {
            mCompleteButton.setEnabled(true);
            mCompleteButton.setAlpha(1.0f);
            mCompleteButton.setText(getString(R.string.complete_num_with_args, (String.valueOf(selectedImageCount) + "/9")));
        } else {
            mCompleteButton.setEnabled(false);
            mCompleteButton.setAlpha(0.5f);
            mCompleteButton.setText(R.string.complete);
        }
        final boolean clickable = mCompleteButton.isClickable();
        LogUtil.d(TAG, "updateCompleteButton: clickable=" + clickable);
    }

    /**
     * update preview button state with the number of selected images
     *
     * @param selectedImageCount
     */
    public void updatePreviewButton(int selectedImageCount) {
        if (selectedImageCount > 0) {
            mPreviewTextView.setEnabled(true);
            mPreviewTextView.setAlpha(1f);
            mPreviewTextView.setText(getString(R.string.preview_num_with_args, String.valueOf(selectedImageCount)));
        } else {
            mPreviewTextView.setEnabled(false);
            mPreviewTextView.setAlpha(0.5f);
            mPreviewTextView.setText(R.string.preview);
        }
    }

    @Override
    public void onClick(View v) {
        if (v == mCompleteButton) {
            onCompleteButtonClick();
        } else if (v == mPhotoAlbumTextView) {
            onPhotoAlbumClick(v);
        } else if (v == mPreviewTextView) {
            onPreviewButtonClick();
        }
    }

}
