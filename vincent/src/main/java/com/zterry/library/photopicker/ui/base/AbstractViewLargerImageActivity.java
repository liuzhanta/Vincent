package com.zterry.library.photopicker.ui.base;

import android.content.Intent;
import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeController;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.imagepipeline.image.ImageInfo;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.huoban.R;
import com.huoban.base.BaseActivity;
import com.huoban.config.Config;
import com.huoban.photo.ui.HackyViewPager;
import com.huoban.photopicker.bean.ImageFile;
import com.huoban.photopicker.util.Constants;
import com.huoban.photopicker.view.SaveImageDialog;
import com.huoban.tools.AlbumStorageDirFactory;
import com.huoban.tools.BaseAlbumDirFactory;
import com.huoban.tools.FileUtils;
import com.huoban.tools.FroyoAlbumDirFactory;
import com.huoban.tools.HBUtils;
import com.huoban.tools.LogUtil;
import com.huoban.tools.WeakHandler;
import com.huoban.view.htmltextview.widget.HtmlCheckBox;
import com.huoban.view.photodraweeview.OnPhotoTapListener;
import com.huoban.view.photodraweeview.PhotoDraweeView;
import com.nineoldandroids.animation.ObjectAnimator;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import huoban.api.network.HBDownloadHttpClient;

import static com.huoban.photopicker.util.Constants.EXTRA_KEY_SELECTED_IMAGES;


/**
 * Desc:查看大图的抽象类
 * Author: Terry
 * Date:2016-02-24
 */
public abstract class AbstractViewLargerImageActivity extends BaseActivity implements View.OnClickListener {

    public static final int ANIM_DURATION = 300;
    public static final int SINGLE_MODE_IMAGE_COUNT = 1;
    /**
     * 单图浏览
     */
    public static final int VIEW_MODE_SINGLE = 0;
    /**
     * 多图浏览
     */
    public static final int VIEW_MODE_GALLERY = 1;
    /**
     * 无操作的多图浏览
     */
    public static final int VIEW_MODE_GALLERY_PURE = 2;
    /**
     * 正常模式
     */
    public static final int GALLERY_TYPE_NORMAL = 0;
    /**
     * 预览模式
     */
    public static final int GALLERY_TYPE_PREVIEW = 1;
    private final ArrayList<ImageFile> selectedImagesList = new ArrayList<>();
    private View mTopBar;
    private View mBottomBar;
    private TextView mCompleteTextView;
    private HtmlCheckBox mChoseIconTextView;
    private LinearLayout mChoseView;
    private HackyViewPager mViewPager;
    private int currentSelectedIndex;
    private boolean mTopBarShow = true;
    private boolean mAllowUpdate = true;
    private String mCurrentSelectedImageUri;
    private ObjectAnimator exitAnim;
    private ObjectAnimator enterAnim;
    private AlbumStorageDirFactory mAlbumStorageDirFactory;
    private ViewPager.OnPageChangeListener onPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            currentSelectedIndex = position;
            if (VIEW_MODE_SINGLE == getCurrentViewMode()) {
                mCurrentSelectedImageUri = getSelectedImageList().get(position);
            } else {
                mCurrentSelectedImageUri = getAllImageList().get(position);
            }

            updateTitle(position, getImageTotalCount());
            updateChoseView();
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };
    private WeakHandler downloadHandler = new WeakHandler(this) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                String message = (String) msg.obj;
                show("图片已保存到相册file:" + message);

            } else {
                show("图片下载失败");
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_larger_image);

        setStatusBarColor(getResources().getColor(R.color.gray_2D2D2D));
        super.setupToolBar(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        initView();
        initViewPager();

        if (HBUtils.hasFroyo() && FileUtils.isExternalStorageWritable()) {
            mAlbumStorageDirFactory = new FroyoAlbumDirFactory();
        } else {
            mAlbumStorageDirFactory = new BaseAlbumDirFactory();
        }
    }

    public void initData() {
        initSelectedImageList();

        if (getCurrentViewMode() == VIEW_MODE_SINGLE) {
            mViewPager.setAdapter(new MyPagerAdapter(getSelectedImageList()));
        } else {
            mViewPager.setAdapter(new MyPagerAdapter(getAllImageList()));
        }
        updateTitle(getCurrentImageIndex(), getImageTotalCount());
        mViewPager.setCurrentItem(getCurrentImageIndex());
        onPageChangeListener.onPageSelected(getCurrentImageIndex());
        mViewPager.setOffscreenPageLimit(3);
    }

    private void initSelectedImageList() {
        if (!HBUtils.isEmpty(getSelectedImageList())) {
            for (String s : getSelectedImageList()) {
                selectedImagesList.add(new ImageFile(s));
            }
        }
    }

    public void setAllowUpdate(boolean allowUpdate) {
        this.mAllowUpdate = allowUpdate;
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra(EXTRA_KEY_SELECTED_IMAGES, getSelectedImageStringList());
        setResult(RESULT_OK, intent);
        finish();
    }

    protected void initView() {
        mCompleteTextView = (TextView) findViewById(R.id.tv_complete);
        mCompleteTextView.setOnClickListener(this);
        mTopBar = findViewById(R.id.fl_top_actionbar);
        mBottomBar = findViewById(R.id.mBottomBar);


        mChoseView = (LinearLayout) findViewById(R.id.ll_chose_view);
        mChoseIconTextView = (HtmlCheckBox) findViewById(R.id.tv_icon_image_chose);
        mChoseIconTextView.setChecked(false);
        mChoseView.setOnClickListener(this);


        if (getCurrentViewMode() == VIEW_MODE_GALLERY_PURE || !mAllowUpdate) {
            mCompleteTextView.setVisibility(View.GONE);
            mChoseIconTextView.setVisibility(View.GONE);
            findViewById(R.id.tv_image_chose).setVisibility(View.GONE);
            mBottomBar.setVisibility(View.GONE);
        } else {
            mBottomBar.setVisibility(View.VISIBLE);
            findViewById(R.id.tv_image_chose).setVisibility(View.VISIBLE);
            mChoseIconTextView.setVisibility(View.VISIBLE);
            mCompleteTextView.setVisibility(View.VISIBLE);
            mCompleteTextView.setOnClickListener(this);
            updateCompleteButton(0);
            if (!HBUtils.isEmpty(getSelectedImageList())) {
                updateCompleteButton(getSelectedImageList().size());
            }
        }
    }

    public void updateTitle(int currentImageIndex, int imageTotal) {
        setTitle(getString(R.string.title_activity_view_large_image_with_args, currentImageIndex + 1, imageTotal));
    }

    private void initViewPager() {
        mViewPager = (HackyViewPager) findViewById(R.id.mViewpager);
        mViewPager.addOnPageChangeListener(onPageChangeListener);

    }

    private void updateChoseView() {
        if (selectedImagesList.contains(new ImageFile(mCurrentSelectedImageUri))) {
            mChoseIconTextView.setChecked(true);
            mChoseIconTextView.setTextColor(getResources().getColor(R.color.green_2DAF5A));
        } else {
            mChoseIconTextView.setChecked(false);
            mChoseIconTextView.setTextColor(getResources().getColor(R.color.white));
        }
    }

    private void startExitAnim() {
        exitAnim = ObjectAnimator
                .ofFloat(mTopBar, "translationY", 0.0F, -mTopBar.getHeight())//
                .setDuration(ANIM_DURATION);
        exitAnim.start();

        mTopBarShow = false;
    }

    private void startEnterAnim() {
        enterAnim = ObjectAnimator//
                .ofFloat(mTopBar, "translationY", -mTopBar.getHeight(), 0.0F)//
                .setDuration(ANIM_DURATION);
        enterAnim.start();
        mTopBarShow = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (enterAnim != null) {
            enterAnim.cancel();
        }
        if (exitAnim != null) {
            exitAnim.cancel();
        }
    }

    private void updateCompleteButton(int selectedImageCount) {
        if (selectedImageCount > 0) {
            mCompleteTextView.setEnabled(true);
            mCompleteTextView.setAlpha(1.0f);
            mCompleteTextView.setText(getString(R.string.complete_num_with_args, (String.valueOf(selectedImageCount) + "/" + getMaxImageCount())));
        } else {
            mCompleteTextView.setEnabled(false);
            mCompleteTextView.setAlpha(0.5f);
            mCompleteTextView.setText(R.string.complete);
        }
    }

    @Override
    public void onClick(View v) {
        if (v == mChoseView) {
            //update chose IconTextView
            if (selectedImagesList.contains(new ImageFile(mCurrentSelectedImageUri))) {
                mChoseIconTextView.setTextColor(getResources().getColor(R.color.white));
                mChoseIconTextView.setChecked(false);
                selectedImagesList.remove(new ImageFile(mCurrentSelectedImageUri));
            } else {
                //check current selected image count
                if (selectedImagesList.size() >= Constants.MAX_IMAGE_UPLOAD_LIMIT_COUNT) {
                    show(getString(R.string.tips_upload_image_max_limit));
                    return;
                }
                if (getCurrentGalleryType() == GALLERY_TYPE_PREVIEW) {
                    selectedImagesList.add(new ImageFile(getSelectedImageList().get(currentSelectedIndex)));
                } else {
                    if (!HBUtils.isEmpty(getAllImageList()) && getAllImageList().get(currentSelectedIndex) != null) {
                        selectedImagesList.add(new ImageFile(getAllImageList().get(currentSelectedIndex)));
                    }
                }
                mChoseIconTextView.setChecked(true);
                mChoseIconTextView.setTextColor(getResources().getColor(R.color.green_2DAF5A));
            }

            //update complete TextView
            updateCompleteButton(selectedImagesList.size());
        } else if (v == mCompleteTextView) {
            onCompleteButtonClick(v);
        }
    }

    /**
     * Called when the complete button has been clicked
     *
     * @param v
     */
    protected abstract void onCompleteButtonClick(View v);

    /**
     * @return
     */
    public ArrayList<String> getSelectedImageStringList() {
        ArrayList<String> imageList = new ArrayList<>();
        if (HBUtils.isEmpty(selectedImagesList)) {
            return getSelectedImageList();
        }
        for (ImageFile imageFile : selectedImagesList) {
            imageList.add(imageFile.getUri());
        }
        return imageList;
    }

    /**
     * 插入到相册中
     *
     * @param filePath
     */
    private void insertIntoGallery(String filePath) {
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri uri = Uri.fromFile(new File(filePath));
        intent.setData(uri);
        sendBroadcast(intent);
    }

    /**
     * Returns Image total count
     *
     * @return
     */
    public abstract int getImageTotalCount();

    /**
     * Returns current image index which starts from 0
     *
     * @return
     */
    public abstract int getCurrentImageIndex();

    /**
     * Returns a list of all images
     *
     * @return
     */
    protected abstract List<String> getAllImageList();

    /**
     * Returns a list of selected images
     *
     * @return
     */
    protected abstract ArrayList<String> getSelectedImageList();

    /**
     * Returns current mode {@link #VIEW_MODE_GALLERY}, {@link #VIEW_MODE_SINGLE}, {@link #VIEW_MODE_GALLERY_PURE}
     *
     * @return
     */
    protected abstract int getCurrentViewMode();

    /**
     * Returns the max image count , {@link Constants#MAX_IMAGE_UPLOAD_LIMIT_COUNT} by default
     *
     * @return
     */
    protected abstract int getMaxImageCount();

    /**
     * Get current gallery type {@link #GALLERY_TYPE_NORMAL} , {@link #GALLERY_TYPE_PREVIEW}
     *
     * @return
     */
    protected abstract int getCurrentGalleryType();

    @Override
    protected void onDestroy() {
        super.onDestroy();
        downloadHandler.removeMessages(0, null);
    }

    private class MyPagerAdapter extends PagerAdapter {

        private final String SAVE_PIC_PATH = Environment.getExternalStorageState().equalsIgnoreCase(Environment.MEDIA_MOUNTED) ? Environment.getExternalStorageDirectory().getAbsolutePath() : "/mnt/sdcard";//保存到SD卡
        private final String SAVE_REAL_PATH = SAVE_PIC_PATH + "/huoban/";//保存的确切位置
        private List<String> images;
        private SparseArray<View> mViews;
        private int screenWidth = 0;
        private int screenHeight = 0;

        public MyPagerAdapter(List<String> images) {
            this.images = images;
            mViews = new SparseArray(images == null ? 0 : images.size());
            screenWidth = HBUtils.getScreenWidth(AbstractViewLargerImageActivity.this);
            screenHeight = HBUtils.getScreenWidth(AbstractViewLargerImageActivity.this);
        }

        @Override
        public int getItemPosition(Object object) {
            return super.getItemPosition(object);
        }

        @Override
        public int getCount() {
            return images == null ? 0 : images.size();
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {

            final View view = getLayoutInflater().inflate(R.layout.adapter_item_view_larger_image, null);
            final PhotoDraweeView photoDraweeView = (PhotoDraweeView) view.findViewById(R.id.photoDraweeView);
            final ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.progressbar_view_large_image);
            progressBar.setVisibility(View.VISIBLE);

//            GenericDraweeHierarchyBuilder builder = GenericDraweeHierarchyBuilder.newInstance(getResources());
//            GenericDraweeHierarchy hierarchy = builder
//                    .build();
//            photoDraweeView.setHierarchy(hierarchy);

            photoDraweeView.setOnPhotoTapListener(new OnPhotoTapListener() {

                @Override
                public void onPhotoTap(View view, float x, float y) {
                    if (mTopBarShow) {
                        startExitAnim();
                    } else {
                        startEnterAnim();
                    }
                    HBUtils.setActivityFullScreen(AbstractViewLargerImageActivity.this, mTopBarShow);
                }
            });
            Uri uri = null;
            if (getCurrentViewMode() == VIEW_MODE_GALLERY_PURE) {
                uri = Uri.parse(images.get(position));
                final Uri finalUri = uri;
                photoDraweeView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        showSavePictureDialog(finalUri);
                        return false;
                    }
                });
            } else {
                uri = Uri.parse(Config.FrescoSupportedURIs.FILE + images.get(position));
            }

            ImageRequest request = ImageRequestBuilder.newBuilderWithSource(uri)
                    .setLocalThumbnailPreviewsEnabled(true)
                    .build();

            PipelineDraweeController controller = (PipelineDraweeController) Fresco.newDraweeControllerBuilder()
                    .setOldController(photoDraweeView.getController())
                    .setControllerListener(new BaseControllerListener<ImageInfo>() {
                        @Override
                        public void onFinalImageSet(String id, ImageInfo imageInfo, Animatable animatable) {
                            super.onFinalImageSet(id, imageInfo, animatable);
                            LogUtil.d("tata", "onFinalImageSet--->");
                            progressBar.setVisibility(View.GONE);
                            photoDraweeView.update(imageInfo.getWidth(), imageInfo.getHeight());
                            if (imageInfo == null || photoDraweeView == null) {
                                return;
                            }
                        }
                    })
                    .setImageRequest(request)
                    .build();

            photoDraweeView.setController(controller);
            mViews.put(position, view);
            container.addView(view);
            return mViews.get(position);
        }

        /**
         * 下载图片对话框
         *
         * @param finalUri
         */
        private void showSavePictureDialog(final Uri finalUri) {
            SaveImageDialog.showImageSaveDialog(AbstractViewLargerImageActivity.this, new SaveImageDialog.OnImageSaveEventClickListener() {
                @Override
                public void onCancel() {

                }

                private String getAlbumName() {
                    return "hbpic";
                }


                @Override
                public boolean onSave() {

                    final String fileName = "huoban_" + System.currentTimeMillis() + ".jpg";

                    final String filePath = SAVE_REAL_PATH + fileName;
                    final File file = new File(SAVE_REAL_PATH);
                    if (!file.exists()) {
                        file.mkdirs();
                    }
                    huoban.api.network.DownloadManager.sharedLoadTaskManager().addTask(finalUri.toString(),
                            filePath,//
                            new HBDownloadHttpClient.OnDownloadListener() {
                                @Override
                                public void downloadSucceed(String url, String path) {
                                    //插入到相册中
                                    insertIntoGallery(filePath);

                                    final Message msg = downloadHandler.obtainMessage();
                                    msg.obj = filePath;
                                    msg.what = 1;
                                    downloadHandler.sendMessage(msg);
                                }

                                @Override
                                public void onProgress(int downloadSize, int totalSize) {

                                }

                                @Override
                                public void downloadFileError(Exception e, String path) {
                                    downloadHandler.sendEmptyMessage(-1);
                                }
                            });
                    return false;
                }

            });
        }


        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(mViews.get(position));
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

    }
}

