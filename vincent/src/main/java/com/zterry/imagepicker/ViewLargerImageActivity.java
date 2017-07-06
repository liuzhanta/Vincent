package com.zterry.imagepicker;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.github.piasy.biv.BigImageViewer;
import com.github.piasy.biv.loader.glide.GlideImageLoader;
import com.zterry.imagepicker.adapter.CommonFragmentPagerAdapter;
import com.zterry.imagepicker.bean.ImageFile;

import java.util.ArrayList;
import java.util.List;

import static com.zterry.imagepicker.util.Constants.EXTRA_KEY_SELECTED_IMAGE_LIST;

/**
 * Description:  <br>
 * Author:Terry<br>
 * Date:2017/6/30 上午11:47
 */

public class ViewLargerImageActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener, View.OnClickListener {

    private static final String TAG = "ViewLargerImageActivity";

    private static final String EXTRA_KEY_IMAGE_URI = "EXTRA_KEY_IMAGE_URI";

    private static final String EXTRA_KEY_ALL_IMAGE_LIST = "EXTRA_KEY_ALL_IMAGE_LIST";
    private static final String EXTRA_KEY_PREVIEW_MODE = "EXTRA_KEY_PREVIEW_MODE";

    public static final int MODE_GALLERY = 0;
    public static final int MODE_PREVIEW = 1;
    public static final int REQ_CODE_VIEW_LARGE_IMAGE = 100;

    private List<ImageFile> allImageFileList = new ArrayList<>();
    private List<ImageFile> selectedImageFileList = new ArrayList<>();
    private List<ImageFile> actualImageFileList = new ArrayList<>();
    private ImageFile selectedImageFile;

    private ViewPager mViewPager;
    private Toolbar mToolbar;
    private AppCompatCheckBox mAppCompatCheckBox;
    private TextView mCompleteTextView;

    private int mCurrentMode;
    private int currentIndex = 0;
    private int mTotalCount = 0;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_complete) {
            onCompleteMenuClick();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_larger_image);
        Log.d(TAG, "onCreate: ");
        BigImageViewer.initialize(GlideImageLoader.with(getApplicationContext()));

        initView();
        parseIntent();
        initToolbar();
        initViewPager();
    }

    private void initView() {
        mCompleteTextView = (TextView) findViewById(R.id.tv_complete_chose);
        mCompleteTextView.setVisibility(View.GONE);
        mCompleteTextView.setOnClickListener(this);

        mAppCompatCheckBox = (AppCompatCheckBox) findViewById(R.id.checkbox);
        mAppCompatCheckBox.setOnCheckedChangeListener(this);

    }

    private void parseIntent() {
        final Intent intent = getIntent();
        mCurrentMode = intent.getIntExtra(EXTRA_KEY_PREVIEW_MODE, MODE_PREVIEW);
        Log.d(TAG, "parseIntent: mCurrentMode= " + mCurrentMode);
        selectedImageFileList = (List<ImageFile>) intent.getSerializableExtra(EXTRA_KEY_SELECTED_IMAGE_LIST);
        allImageFileList = (List<ImageFile>) intent.getSerializableExtra(EXTRA_KEY_ALL_IMAGE_LIST);
        selectedImageFile = (ImageFile) intent.getSerializableExtra(EXTRA_KEY_IMAGE_URI);

        if (mCurrentMode == MODE_GALLERY) {
            currentIndex = allImageFileList.indexOf(selectedImageFile);
            mTotalCount = allImageFileList.size();
            actualImageFileList.addAll(allImageFileList);
            mAppCompatCheckBox.setChecked(false);
        } else {
            currentIndex = 0;
            mTotalCount = selectedImageFileList.size();
            actualImageFileList.addAll(selectedImageFileList);
            mAppCompatCheckBox.setChecked(true);
        }

        Log.d(TAG, "parseIntent: actualImageFileList= " + actualImageFileList.size());
    }

    private void initToolbar() {

        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
        }
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        if (mCurrentMode == MODE_GALLERY) {
            updateTitle(currentIndex, mTotalCount);
        } else {
            updateTitle(currentIndex + 1, mTotalCount);
        }
        mToolbar.setTitleTextColor(ImageParams.titleColor);
        mToolbar.setNavigationIcon(R.drawable.ic_ab_back);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED);
        finish();
    }

    public void updateTitle(int currentIndex, int count) {
        setTitle(getString(R.string.preview_image_count, currentIndex, count));
    }

    private void initViewPager() {
        final List<Fragment> mfragments = new ArrayList<>(actualImageFileList.size());
        for (ImageFile imageFile : actualImageFileList) {
            mfragments.add(PreviewLargeImageFragment.createPreviewLargeImageFragment(imageFile));
        }
        final CommonFragmentPagerAdapter adapter = new CommonFragmentPagerAdapter(
                getSupportFragmentManager(), mfragments);
        mViewPager = (ViewPager) findViewById(R.id.mViewpager);
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                Log.d(TAG, "onPageSelected: position = " + position);
                currentIndex = position;
                updateTitle(position + 1, mTotalCount);

                //update checkbox state
                final ImageFile imageFile = actualImageFileList.get(position);
                if (mCurrentMode == MODE_PREVIEW) {
                    mAppCompatCheckBox.setChecked(selectedImageFileList.contains(imageFile));
                } else {
                    mAppCompatCheckBox.setChecked(selectedImageFileList.contains(imageFile));
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


        mViewPager.setAdapter(adapter);

        if (currentIndex != -1) {
            mViewPager.setCurrentItem(currentIndex);
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        final ImageFile imageFile = actualImageFileList.get(currentIndex);
        if (isChecked) {
            if (!selectedImageFileList.contains(imageFile)) {
                selectedImageFileList.add(imageFile);
            }
        } else {
            selectedImageFileList.remove(imageFile);
        }

        //update complete menu
        updateCompleteMenu();
    }

    private void updateCompleteMenu() {
        if (selectedImageFileList.size() == 0) {
            mCompleteTextView.setVisibility(View.GONE);
        } else {
            mCompleteTextView.setVisibility(View.VISIBLE);
            mCompleteTextView.setText(getString(R.string.complete_with_args,
                    selectedImageFileList.size(), mTotalCount));
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
        if (view != null) {
            ActivityOptionsCompat compat = ActivityOptionsCompat.makeSceneTransitionAnimation((Activity)
                    context, view, context.getString(R.string.transition_image));
            ActivityCompat.startActivityForResult((Activity) context, starter, REQ_CODE_VIEW_LARGE_IMAGE,
                    compat.toBundle());
        } else {
            ((Activity) context).startActivityForResult(starter, REQ_CODE_VIEW_LARGE_IMAGE);
        }

    }

    @Override
    public void onClick(View v) {
        if (v == mCompleteTextView) {
            onCompleteMenuClick();
        }
    }

    private void onCompleteMenuClick() {
        Intent data = new Intent();
        data.putExtra(EXTRA_KEY_SELECTED_IMAGE_LIST, new ArrayList<>(selectedImageFileList));
        setResult(RESULT_OK, data);
        finish();
    }
}
