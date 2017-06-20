package com.zterry.library.photopicker.adapter.base;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

/**
 * an Enhanced adapter
 *
 * @param <T>
 */
public abstract class CommonAdapter<T> extends BaseAdapter {

    protected LayoutInflater mInflater;
    protected Context mContext;
    protected List<T> mDatas;
    protected Resources mResources = null;

    public CommonAdapter(Context mContext) {
        this(mContext, null);
    }

    public List<T> getData() {
        return mDatas;
    }

    public CommonAdapter(Context context, List<T> mDatas) {
        this.mContext = context;
        this.mDatas = mDatas;
        this.mInflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return mDatas == null ? 0 : (mDatas.isEmpty() ? 0 : mDatas.size());
    }

    @Override
    public T getItem(int position) {
        return mDatas == null ? null : mDatas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder = getViewHolder(position, convertView, parent);
        convertView(holder, getItem(position), position);
        return holder.getConvertView();
    }

    public abstract void convertView(ViewHolder helper, T item, int position);

    public abstract int getItemLayoutId();

    private ViewHolder getViewHolder(int position, View convertView, ViewGroup parent) {
        return ViewHolder.getViewHolder(mContext, convertView, parent, getItemLayoutId(), position);
    }


    public void setData(List<T> data) {
        mDatas = data;
        notifyDataSetChanged();
    }

    public void appendData(List<T> data) {
        if (mDatas != null) {
            mDatas.addAll(data);
        } else {
            mDatas = data;
        }
        notifyDataSetChanged();
    }

    public void appendData(T data) {
        if (mDatas != null) {
            mDatas.add(data);
        }
        notifyDataSetChanged();
    }

    public void clearData() {
        if (mDatas != null)
            mDatas.clear();
        notifyDataSetChanged();
    }

    public Resources getResources() {
        if (mResources == null) {
            mResources = mContext.getResources();
        }
        return mResources;
    }

    public String getString(int resId) {
        return getResources().getString(resId);
    }
}
