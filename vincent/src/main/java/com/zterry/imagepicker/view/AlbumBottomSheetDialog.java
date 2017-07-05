package com.zterry.imagepicker.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;

import com.zterry.imagepicker.R;
import com.zterry.imagepicker.adapter.AlbumAdapter;
import com.zterry.imagepicker.adapter.base.BaseRecyclerViewAdapter;
import com.zterry.imagepicker.bean.PhotoAlbum;

import java.util.List;

/**
 * Description:  <br>
 * Author:Terry<br>
 * Date:2017/6/29 下午3:47
 */

public class AlbumBottomSheetDialog extends BottomSheetDialog implements
        BaseRecyclerViewAdapter.OnItemClickListener {

    private final View view;

    private RecyclerView mRecyclerView;
    private AlbumAdapter mAdapter;
    private List<PhotoAlbum> photoAlbumList;
    private AlbumAdapter.OnAlbumSelectedListener onAlbumSelectedListener;

    public AlbumBottomSheetDialog(@NonNull Context context) {
        super(context);

        view = LayoutInflater.from(context).inflate(R.layout.bottom_sheet_album_list, null);

        initView(view);
        setContentView(view);
    }

    private void initView(View view) {
        mAdapter = new AlbumAdapter(getContext());
        mAdapter.setOnItemClickListener(this);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(mAdapter);
    }

    public void setData(List<PhotoAlbum> photoAlbumList) {
        this.photoAlbumList = photoAlbumList;
        mAdapter.setData(photoAlbumList);
    }

    public void setDefaultCheckedAlbum(PhotoAlbum lastCheckedPhotoAlbum) {
        mAdapter.setCheckedAlbum(lastCheckedPhotoAlbum);
    }

    public void setOnAlbumSelectedListener(final AlbumAdapter.OnAlbumSelectedListener
                                                   onAlbumSelectedListener) {
        this.onAlbumSelectedListener = onAlbumSelectedListener;
    }

    @Override
    public void onItemClick(View itemView, RecyclerView.ViewHolder holder, int position) {
        dismiss();
        final PhotoAlbum item = mAdapter.getItem(position);
        mAdapter.setCheckedAlbum(item);
        if (onAlbumSelectedListener != null) {
            onAlbumSelectedListener.onAlbumSelected(item);
        }
    }
}
