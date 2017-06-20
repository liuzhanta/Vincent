package com.zterry.library.photopicker.adapter;


import android.content.Context;
import android.net.Uri;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.huoban.R;
import com.huoban.adapter.base.CommonAdapter;
import com.huoban.adapter.base.ViewHolder;
import com.huoban.config.Config;
import com.huoban.config.TTFConfig;
import com.huoban.photopicker.bean.PhotoAlbum;
import com.huoban.tools.HBUtils;
import com.huoban.tools.LogUtil;
import com.huoban.view.htmltextview.CommonIconTextView;
import com.zterry.library.photopicker.adapter.base.CommonAdapter;

import java.util.LinkedList;
import java.util.List;

/**
 * Desc:相册适配器
 * Author: Terry
 * Date:2016-02-17
 */
public class AlbumAdapter extends CommonAdapter<PhotoAlbum> {

    private static final String TAG = AlbumAdapter.class.getSimpleName();
    private static List<Integer> selectedPosList = new LinkedList<>();

    private int picWidth = 0;
    public AlbumAdapter(Context mContext) {
        super(mContext);
        picWidth = HBUtils.dipToPx(67);
    }

    @Override
    public void convertView(ViewHolder helper, final PhotoAlbum item, final int position) {

        //init view
        final SimpleDraweeView photo = helper.getView(R.id.sdv_photo);
        final TextView albumName = helper.getView(R.id.tv_album_name);
        final TextView albumPhotoCount = helper.getView(R.id.tv_album_count);

        //1. set image uri
        Uri uri = Uri.parse(Config.FrescoSupportedURIs.FILE + item.getFirstImagePath());
        LogUtil.d(TAG, "uri = " + uri);
        photo.setTag(item);
        if (photo.getTag() != null && photo.getTag().equals(item)) {
            int width = picWidth, height = picWidth;
            ImageRequest request = ImageRequestBuilder.newBuilderWithSource(uri)
                    .setResizeOptions(new ResizeOptions(width, height))
                    .setLocalThumbnailPreviewsEnabled(true)
                    .build();

            PipelineDraweeController controller = (PipelineDraweeController) Fresco.newDraweeControllerBuilder()
                    .setOldController(photo.getController())
                    .setImageRequest(request)
                    .build();

            photo.setController(controller);
        }
        //2. set album name
        albumName.setText(item.getName());

        //3. set album photo count
        albumPhotoCount.setText(getResources().getString(R.string.album_photo_number, item.getCount()));

        //4. set checkbox state
        CommonIconTextView checkView = helper.getView(R.id.tv_check);
        checkView.setIcon(selectedPosList.contains(position) ? TTFConfig.RADIO_SELECTED : TTFConfig.RADIO_UN_SELECTED);

    }

    public void setSelected(int position) {
        selectedPosList.clear();
        selectedPosList.add(position);
        notifyDataSetChanged();
    }

    @Override
    public int getItemLayoutId() {
        return R.layout.adapter_item_photo_album_list;
    }

}
