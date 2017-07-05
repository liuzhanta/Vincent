package com.zterry.imagepicker.adapter;

import android.content.Context;
import android.support.v7.widget.AppCompatRadioButton;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.zterry.imagepicker.R;
import com.zterry.imagepicker.adapter.base.BaseRecyclerViewAdapter;
import com.zterry.imagepicker.adapter.base.RecyclerViewHolder;
import com.zterry.imagepicker.bean.PhotoAlbum;

import java.io.File;


/**
 * Description:  <br>
 * Author:Terry<br>
 * Date:2017/6/20 下午5:14
 */

public class AlbumAdapter extends BaseRecyclerViewAdapter<PhotoAlbum> {


    private PhotoAlbum lastCheckedPhotoAlbum;

    private int lastCheckedPosition = -1;
    private int checkedPosition;

    public AlbumAdapter(Context mContext) {
        super(mContext);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.adapter_item_album;
    }

    @Override
    protected void onBindViewData(RecyclerViewHolder holder, final PhotoAlbum photoAlbum, final int position) {

        //set image
        ImageView imageCover = holder.getView(R.id.image_view_cover);
        Glide.with(mContext)
                .load(new File(photoAlbum.getFirstImagePath()))
                .into(imageCover);

        //set Album name
        holder.setText(R.id.tv_album_name, photoAlbum.getName());

        //set the album's images count
        holder.setText(R.id.tv_album_count, String.valueOf(photoAlbum.getCount()));

        //set checkbox
        AppCompatRadioButton radioButton = holder.getView(R.id.tv_check);
        radioButton.setChecked(lastCheckedPhotoAlbum == photoAlbum);
    }


    public void setCheckedAlbum(PhotoAlbum lastCheckedPhotoAlbum) {
        this.lastCheckedPhotoAlbum = lastCheckedPhotoAlbum;
        notifyDataSetChanged();
    }

    public interface OnAlbumSelectedListener {
        void onAlbumSelected(PhotoAlbum photoAlbum);
    }


}
