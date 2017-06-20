package com.zterry.library.photopicker.adapter;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeController;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.huoban.R;
import com.huoban.adapter.base.CommonAdapter;
import com.huoban.adapter.base.ViewHolder;
import com.huoban.config.Config;
import com.huoban.model2.ChatAttachment;
import com.huoban.tools.HBUtils;
import com.huoban.view.htmltextview.widget.HtmlCheckBox;
import com.zterry.library.photopicker.adapter.base.CommonAdapter;

import java.util.ArrayList;

/**
 * Desc:Item中相册中的图片适配器
 * Author: Terry
 * Date:2016-03-02
 */
public class ItemGalleryAdapter extends CommonAdapter<ChatAttachment> {

    private int scaledPicWidth;
    private int whiteColor;
    private int greenColor;
    private ArrayList<ChatAttachment> mSelectedImageList = new ArrayList<>();
    private boolean isEditMode = false;

    public ItemGalleryAdapter(Context mContext) {
        super(mContext);
        scaledPicWidth = (HBUtils.getScreenWidth(mContext) - 4 * HBUtils.dipToPx(4)) / 3 / 2;
        whiteColor = getResources().getColor(R.color.white);
        greenColor = getResources().getColor(R.color.green_2DAF5A);
    }

    @Override
    public void convertView(ViewHolder helper, ChatAttachment item, final int position) {
        //1.set image uri
        GenericDraweeHierarchyBuilder builder = GenericDraweeHierarchyBuilder.newInstance(getResources());
//        final ProgressBarDrawable progressBarDrawable = new CircleProgressBarDrawable();
//        progressBarDrawable.setBarWidth(30);
//        progressBarDrawable.setColor(getResources().getColor(R.color.green_2DAF5A));

//        GenericDraweeHierarchy hierarchy = builder
//                .setProgressBarImage(progressBarDrawable)
//                .build();
        SimpleDraweeView photo = helper.getView(R.id.sdv_photo);
//        photo.setHierarchy(hierarchy);
        Uri uri = null;
        switch (item.getAttachStatus()) {
            case FINISH:
                if (!TextUtils.isEmpty(item.getPermalink())) {
                    uri = Uri.parse(item.getPermalink());
                }
                break;
            case FINISH_UNCOMMITED:
            case LOADING:
                uri = Uri.parse(Config.FrescoSupportedURIs.FILE + item.getLocalLink());
                break;
        }
        if (uri != null) {
            ImageRequest request = ImageRequestBuilder.newBuilderWithSource(uri)
                    .setResizeOptions(new ResizeOptions(scaledPicWidth, scaledPicWidth))
                    .setProgressiveRenderingEnabled(true)
                    .setLocalThumbnailPreviewsEnabled(true)
                    .build();
            PipelineDraweeController controller = (PipelineDraweeController) Fresco.newDraweeControllerBuilder()
                    .setOldController(photo.getController())
                    .setImageRequest(request)
                    .build();
            photo.setController(controller);
        }

        //2. set progressbar
        ProgressBar progressBar = helper.getView(R.id.progressBar);
        ImageView retryImage = helper.getView(R.id.common_tv_retry_icon);

        //3. update the layout by the upload status
        if (item.getAttachStatus() == ChatAttachment.AttachStatus.FINISH || item.getAttachStatus() == ChatAttachment.AttachStatus.FINISH_UNCOMMITED) {
            photo.setColorFilter(null);
            progressBar.setVisibility(View.GONE);
            retryImage.setVisibility(View.GONE);
        } else if (item.getAttachStatus() == ChatAttachment.AttachStatus.FAILED) {
//            photo.setColorFilter(Color.parseColor("#77000000"));
            retryImage.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
        } else if (item.getAttachStatus() == ChatAttachment.AttachStatus.LOADING) {
            photo.setColorFilter(Color.parseColor("#77000000"));
            retryImage.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
        }

        final HtmlCheckBox tvSelect = helper.getView(R.id.tv_select);
        tvSelect.setVisibility(isEditMode ? View.VISIBLE : View.GONE);

        if (!mSelectedImageList.contains(item)) {
            tvSelect.setChecked(false);
        } else {
            tvSelect.setChecked(true);
        }
        tvSelect.setOnClickListener(getL(item, photo, tvSelect));

    }

    public ArrayList<ChatAttachment> getSelectedImageList() {
        return mSelectedImageList;
    }

    @NonNull
    private View.OnClickListener getL(final ChatAttachment item, final SimpleDraweeView photo, final HtmlCheckBox tvSelect) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSelectedImageList.contains(item)) {
                    tvSelect.setChecked(false);
                    mSelectedImageList.remove(item);
                    tvSelect.setTextColor(whiteColor);
                } else {
                    tvSelect.setChecked(true);
                    mSelectedImageList.add(item);
                    tvSelect.setTextColor(greenColor);
                }
            }
        };
    }

    public void setEditMode(boolean isEditMode) {
        this.isEditMode = isEditMode;
        if (!isEditMode) {
            mSelectedImageList.clear();
        }
        notifyDataSetChanged();
    }

    @Override
    public int getItemLayoutId() {
        return R.layout.adapter_item_gallery;
    }
}
