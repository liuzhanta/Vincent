package com.zterry.imagepicker;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.zterry.imagepicker.adapter.AlbumAdapter;
import com.zterry.imagepicker.adapter.ImagePickerAdapter;
import com.zterry.imagepicker.adapter.base.BaseRecyclerViewAdapter;
import com.zterry.imagepicker.bean.ImageFile;
import com.zterry.imagepicker.bean.PhotoAlbum;
import com.zterry.imagepicker.behavior.QuickHideBehavior;
import com.zterry.imagepicker.util.GetAlbumListAsyncTask;
import com.zterry.imagepicker.view.AlbumBottomSheetDialog;

import java.util.ArrayList;
import java.util.List;

import static com.zterry.imagepicker.util.Constants.EXTRA_KEY_SELECTED_IMAGES;

/**
 * Description: ImagePickerActivity <br>
 * Author:Terry<br>
 * Date:2017/6/29 上午10:46
 */

public class ImagePickerActivity extends AppCompatActivity implements View.OnClickListener,
        BaseRecyclerViewAdapter.OnItemClickListener {

    private static final String TAG = "ImagePickerActivity";

    private List<PhotoAlbum> photoAlbumList;

    private RecyclerView mRecyclerGridView;
    private ImagePickerAdapter mImagePickerAdapter;
    private Toolbar mToolbar;
    private MenuItem mCompleteMenu;
    private TextView mAlbumTextView;
    private TextView mPreviewTextView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_layout_image_picker);

        initBottomActionBarView();
        initToolbar();
        initRecyclerGridView();
        loadData();
        initScrollFlags();
    }

    private void initScrollFlags() {
        //bottom action bar behavior
        bottomActionLayout = findViewById(R.id.bottom_action_layout);
        final CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams)
                bottomActionLayout.getLayoutParams();
        if (ImageParams.layoutBehavior) {
            layoutParams.setBehavior(new QuickHideBehavior());
        } else {
            layoutParams.setBehavior(null);
        }
        bottomActionLayout.setLayoutParams(layoutParams);

        //Toolbar scroll flags
        AppBarLayout.LayoutParams mToolbarLayoutParams = (AppBarLayout.LayoutParams) mToolbar
                .getLayoutParams();
        if (ImageParams.layoutBehavior) {
            mToolbarLayoutParams.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS
                    | AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL);
        } else {
            mToolbarLayoutParams.setScrollFlags(0);
        }
        mToolbar.setLayoutParams(mToolbarLayoutParams);

    }

    private View bottomActionLayout;

    private void initBottomActionBarView() {
        mAlbumTextView = (TextView) findViewById(R.id.tv_album);
        mAlbumTextView.setOnClickListener(this);

        mPreviewTextView = (TextView) findViewById(R.id.tv_preview);
        mPreviewTextView.setOnClickListener(this);
    }

    private void initToolbar() {
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
        }
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        if (TextUtils.isEmpty(ImageParams.title)) {
            if (ImageParams.titleResId != 0) {
                mToolbar.setTitle(ImageParams.titleResId);
            }
        } else {
            mToolbar.setTitle(ImageParams.title);
        }
        mToolbar.setTitleTextColor(ImageParams.titleColor);
        mToolbar.setNavigationIcon(R.drawable.ic_ab_close);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_image_picker, menu);
        mCompleteMenu = menu.findItem(R.id.menu_complete);
        mCompleteMenu.setEnabled(false);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_complete) {
            onCompleteClick();
        }
        return super.onOptionsItemSelected(item);
    }

    private void onCompleteClick() {
        Intent data = new Intent();
        data.putExtra(EXTRA_KEY_SELECTED_IMAGES, new ArrayList<>(mImagePickerAdapter
                .getSelectedImageFiles()));
        setResult(RESULT_OK, data);
        finish();
    }

    private void loadData() {
        new GetAlbumListAsyncTask(this) {

            @Override
            protected void onPostExecute(List<PhotoAlbum> photoAlba) {
                photoAlbumList = photoAlba;
                final List<ImageFile> imageFiles = photoAlba.get(0).getImageFiles();
                for (ImageFile imageFile : imageFiles) {
                    Log.d(TAG, "onPhotoAlbumListGet: imageFile =" + imageFile.toString());
                }
                Log.d(TAG, "onPhotoAlbumListGet: data = " + photoAlba.size());
                mImagePickerAdapter.setData(imageFiles);
            }
        }.execute();

    }

    private void initRecyclerGridView() {
        mImagePickerAdapter = new ImagePickerAdapter(this);
        mImagePickerAdapter.setOnImageSelectedListener(getImageSelectedListener());
        mImagePickerAdapter.setOnItemClickListener(this);

        mRecyclerGridView = (RecyclerView) findViewById(R.id.recycler_view);
        mRecyclerGridView.setLayoutManager(new GridLayoutManager(this, 3));
        mRecyclerGridView.setHasFixedSize(true);
        mRecyclerGridView.setAdapter(mImagePickerAdapter);
    }

    private ImagePickerAdapter.OnImageSelectedListener getImageSelectedListener() {
        return new ImagePickerAdapter.OnImageSelectedListener() {
            @Override
            public void onImageSelected(ImageFile imageFile, int totalSelectedCount) {
                mToolbar.setTitle(getString(R.string.selected_image_count, totalSelectedCount,
                        ImageParams.maxSelectCount));
                mCompleteMenu.setEnabled(totalSelectedCount > 0);
                mPreviewTextView.setText(getString(R.string.preview_with_args, totalSelectedCount));
            }
        };
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.tv_album) {
            onAlbumViewClick();
        } else if (i == R.id.tv_preview) {
            onPreviewClick(v);
        }
    }

    /**
     * Called when the preview view has been clicked.
     *
     * @param v
     */
    private void onPreviewClick(View v) {
        ViewLargerImageActivity.startForPreview(this, mImagePickerAdapter.getSelectedImageFiles());
    }

    private PhotoAlbum lastCheckedPhotoAlbum;

    /**
     * Called when the album view has been clicked.
     */
    private void onAlbumViewClick() {
        AlbumBottomSheetDialog dialog = new AlbumBottomSheetDialog(this);
        dialog.setData(photoAlbumList);
        dialog.setDefaultCheckedAlbum(lastCheckedPhotoAlbum);
        dialog.setOnAlbumSelectedListener(new AlbumAdapter.OnAlbumSelectedListener() {

            @Override
            public void onAlbumSelected(PhotoAlbum photoAlbum) {
                lastCheckedPhotoAlbum = photoAlbum;
                mImagePickerAdapter.setData(photoAlbum.getImageFiles());
                mAlbumTextView.setText(photoAlbum.getName());
            }
        });
        dialog.show();
    }

    @Override
    public void onItemClick(View itemView, RecyclerView.ViewHolder holder, int position) {
        final ImageFile imageFile = mImagePickerAdapter.getItem(position);
        ViewLargerImageActivity.start(this, itemView.findViewById(R.id.image_view),
                imageFile, new ArrayList<>(mImagePickerAdapter.getSelectedImageFiles()),
                new ArrayList<>(mImagePickerAdapter.getmDatas()));

    }
}
