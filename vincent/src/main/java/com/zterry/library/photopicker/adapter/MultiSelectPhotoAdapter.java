package com.zterry.library.photopicker.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeController;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.huoban.R;
import com.huoban.adapter.base.CommonAdapter;
import com.huoban.adapter.base.ViewHolder;
import com.huoban.config.Config;
import com.huoban.photopicker.bean.ImageFile;
import com.huoban.photopicker.util.ConfigConstants;
import com.huoban.photopicker.util.Constants;
import com.huoban.tools.HBUtils;
import com.huoban.tools.LogUtil;
import com.huoban.view.htmltextview.widget.HtmlCheckBox;
import com.zterry.library.photopicker.adapter.base.CommonAdapter;

import java.util.ArrayList;

/**
 * Desc:图片多选适配器
 * Author: Terry
 * Date:2016-02-16
 */
public class MultiSelectPhotoAdapter extends CommonAdapter<ImageFile> {

    public static final String TAG = MultiSelectPhotoAdapter.class.getSimpleName();

    private ArrayList<ImageFile> mSelectedImageList = new ArrayList<>();

    private int scaledPicWidth = 0;
    private int whiteColor;
    private int greenColor;
    private Drawable palceHolderDrawable = null;

    private OnImageSelectedListener mOnImageSelectedListener;

    public static final int ITEM_VIEW_TYPE_CAMERA = 0;
    public static final int ITEM_VIEW_TYPE_PHOTO = 1;
    public static final String CAMERA_POS = "camera_position";


    public MultiSelectPhotoAdapter(Context mContext) {
        super(mContext);
        scaledPicWidth = (HBUtils.getScreenWidth(mContext) - 2 * HBUtils.dipToPx(10)) / 3;
        whiteColor = getResources().getColor(R.color.white);
        greenColor = getResources().getColor(R.color.green_2DAF5A);
        palceHolderDrawable = getResources().getDrawable(R.drawable.bg_photo_place_holder);

        GenericDraweeHierarchyBuilder builder = GenericDraweeHierarchyBuilder.newInstance(getResources());
        hierarchy = builder
                .setPlaceholderImage(palceHolderDrawable)
                .setFadeDuration(300)
                .build();
    }

    GenericDraweeHierarchy hierarchy;

    public ArrayList<ImageFile> getSelectedImageFileList() {
        return mSelectedImageList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (isCameraPosition(position)) {
            View cameraView = mInflater.inflate(R.layout.adapter_item_camera, null);
            return cameraView;
        }
        return super.getView(position, convertView, parent);
    }


    public boolean isCameraPosition(int position) {
        final ImageFile item = getItem(position);
        return !TextUtils.isEmpty(item.getUri()) && item.getUri().equals(CAMERA_POS);
    }

    @Override
    public void convertView(ViewHolder helper, final ImageFile item, final int position) {
        LogUtil.d(TAG, "convertView and position =" + position);
        //1.set image uri
        final SimpleDraweeView photo = helper.getView(R.id.sdv_photo);
        String imagePath = null;
        if (isImageThumbAvailable(item)) {
            imagePath = item.getThumbnail();
        } else {
            imagePath = item.getUri();
        }
        Uri uri = Uri.parse(Config.FrescoSupportedURIs.FILE + imagePath);
//        LogUtil.d(TAG, "uri = " + uri);
        photo.setTag(uri);

        if (photo.getTag() != null && photo.getTag().equals(uri)) {

            ImageRequest request = null;
            final ImageRequestBuilder imageRequestBuilder = ImageRequestBuilder.newBuilderWithSource(uri);

            imageRequestBuilder.setResizeOptions(new ResizeOptions(scaledPicWidth, scaledPicWidth));
            imageRequestBuilder.setProgressiveRenderingEnabled(true);
            imageRequestBuilder.setLocalThumbnailPreviewsEnabled(true);
            imageRequestBuilder.setImageDecodeOptions(ConfigConstants.getImageDecodeOptions());
            request = imageRequestBuilder.build();

            PipelineDraweeController controller = (PipelineDraweeController) Fresco.newDraweeControllerBuilder()
                    .setOldController(photo.getController())
                    .setImageRequest(request)
                    .build();

            photo.setController(controller);
        }

        //2. set checkbox state
        final HtmlCheckBox tvSelect = helper.getView(R.id.tv_select);

        if (!mSelectedImageList.contains(item)) {
            tvSelect.setChecked(false);
            photo.setColorFilter(null);
        } else {
            tvSelect.setChecked(true);
            photo.setColorFilter(Color.parseColor("#77000000"));
        }

        // set listener for check view
        tvSelect.setOnClickListener(getL(item, photo, tvSelect));
    }

    @NonNull
    private View.OnClickListener getL(final ImageFile item, final SimpleDraweeView photo, final HtmlCheckBox tvSelect) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSelectedImageList.contains(item)) {
                    tvSelect.setChecked(false);
                    mSelectedImageList.remove(item);
                    tvSelect.setTextColor(whiteColor);
                    photo.setColorFilter(null);
                } else {
                    if (mSelectedImageList.size() >= Constants.MAX_IMAGE_UPLOAD_LIMIT_COUNT) {
                        Toast.makeText(mContext, getResources().getString(R.string.tips_upload_image_max_limit), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    tvSelect.setChecked(true);
                    mSelectedImageList.add(item);
                    tvSelect.setTextColor(greenColor);
                    photo.setColorFilter(Color.parseColor("#77000000"));
                }
                if (mSelectedImageList != null) {
                    mOnImageSelectedListener.onImageSelected(mSelectedImageList.size());
                }
            }
        };
    }


    private boolean isImageThumbAvailable(ImageFile item) {
        return !TextUtils.isEmpty(item.getThumbnail());
    }

    public void setOnImageSelectedListener(OnImageSelectedListener mOnImageSelectedListener) {
        this.mOnImageSelectedListener = mOnImageSelectedListener;
    }

    public void setSelectedImageList(ArrayList<ImageFile> selectedImageList) {
        this.mSelectedImageList = selectedImageList;
        this.notifyDataSetChanged();
    }

    public interface OnImageSelectedListener {
        void onImageSelected(int selectedTotalCount);
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        return position == 0 ? ITEM_VIEW_TYPE_CAMERA : ITEM_VIEW_TYPE_PHOTO;
    }

    public boolean containsCameraItem() {
        return !HBUtils.isEmpty(getData()) && getData().contains(new ImageFile(CAMERA_POS));
    }

    @Override
    public int getItemLayoutId() {
        return R.layout.adapter_item_grid_multi_select;
    }
}