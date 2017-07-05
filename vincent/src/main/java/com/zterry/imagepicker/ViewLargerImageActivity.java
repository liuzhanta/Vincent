package com.zterry.imagepicker;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.github.piasy.biv.BigImageViewer;
import com.github.piasy.biv.loader.glide.GlideImageLoader;
import com.zterry.imagepicker.bean.ImageFile;

import java.util.ArrayList;
import java.util.List;

/**
 * Description:  <br>
 * Author:Terry<br>
 * Date:2017/6/30 上午11:47
 */

public class ViewLargerImageActivity extends AppCompatActivity {

    private static final String TAG = "ViewLargerImageActivity";

    private static final String EXTRA_KEY_IMAGE_URI = "EXTRA_KEY_IMAGE_URI";
    private static final String EXTRA_KEY_SELECTED_IMAGE_LIST = "EXTRA_KEY_SELECTED_IMAGE_LIST";
    private static final String EXTRA_KEY_ALL_IMAGE_LIST = "EXTRA_KEY_ALL_IMAGE_LIST";
    private static final String EXTRA_KEY_PREVIEW_MODE = "EXTRA_KEY_PREVIEW_MODE";

    public static final int MODE_GALLERY = 0;
    public static final int MODE_PREVIEW = 1;

    private List<ImageFile> allImageFileList;
    private List<ImageFile> selectedImageFileList;
    private ImageFile selectedImageFile;

    private ViewPager mViewPager;
    private Toolbar mToolbar;

    private int mCurrentMode;
    private int currentIndex = 0;
    int mTotalCount = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_larger_image);

        BigImageViewer.initialize(GlideImageLoader.with(getApplicationContext()));

        parseIntent();
        initToolbar();
        initViewPager();
    }

    private void initToolbar() {
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
        }
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        mToolbar.setTitleTextColor(ImageParams.titleColor);
        if (mCurrentMode == MODE_GALLERY) {
            updateTitle(currentIndex, mTotalCount);
        } else {
            updateTitle(currentIndex + 1, mTotalCount);

        }
        mToolbar.setNavigationIcon(R.drawable.ic_ab_back);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });
    }

    public void updateTitle(int currentIndex, int count) {
        mToolbar.setTitle(getString(R.string.preview_image_count, currentIndex, count));
    }

    private void parseIntent() {
        final Intent intent = getIntent();
        mCurrentMode = intent.getIntExtra(EXTRA_KEY_PREVIEW_MODE, MODE_PREVIEW);
        selectedImageFileList = (List<ImageFile>) intent.getSerializableExtra(EXTRA_KEY_SELECTED_IMAGE_LIST);
        allImageFileList = (List<ImageFile>) intent.getSerializableExtra(EXTRA_KEY_ALL_IMAGE_LIST);
        selectedImageFile = (ImageFile) intent.getSerializableExtra(EXTRA_KEY_IMAGE_URI);

        if (mCurrentMode == MODE_GALLERY) {
            currentIndex = allImageFileList.indexOf(selectedImageFile);
            mTotalCount = allImageFileList.size();
        } else {
            currentIndex = 0;
            mTotalCount = selectedImageFileList.size();
        }


        Log.d(TAG, "parseIntent: selectedImageFile=" + allImageFileList.size());
    }

    private void initViewPager() {
        mViewPager = (ViewPager) findViewById(R.id.mViewpager);
        mViewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return PreviewLargeImageFragment.createPreviewLargeImageFragment(
                        allImageFileList.get(position));
            }

            @Override
            public int getCount() {
                return allImageFileList == null ? 0 : allImageFileList.size();
            }
        });
        if (currentIndex != -1) {
            mViewPager.setCurrentItem(currentIndex);
        }
    }

    public static void startForPreview(Context context, List<ImageFile> selectedImageFiles) {
        start(context, null, null, new ArrayList<>(selectedImageFiles), null);
    }

    public static void start(Context context, View view, ImageFile curImageFile, ArrayList<ImageFile>
            selectedImageFiles, ArrayList<ImageFile> allImageFiles) {
        Intent starter = new Intent(context, ViewLargerImageActivity.class);
        starter.putExtra(EXTRA_KEY_PREVIEW_MODE, allImageFiles == null ? MODE_PREVIEW : MODE_GALLERY);
        starter.putExtra(EXTRA_KEY_IMAGE_URI, curImageFile);
        starter.putExtra(EXTRA_KEY_SELECTED_IMAGE_LIST, selectedImageFiles);
        if (allImageFiles != null) {
            starter.putExtra(EXTRA_KEY_ALL_IMAGE_LIST, allImageFiles);
        }
        ActivityOptionsCompat compat = ActivityOptionsCompat.makeSceneTransitionAnimation((Activity)
                context, view, context.getString(R.string.transition_image));
        ActivityCompat.startActivity(context, starter,
                compat.toBundle());
    }
}
