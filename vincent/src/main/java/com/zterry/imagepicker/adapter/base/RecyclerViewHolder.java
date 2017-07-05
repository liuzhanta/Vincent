package com.zterry.imagepicker.adapter.base;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.TextView;


/**
 * Desc:RecyclerViewHolder
 * Author: Terry
 * Date:2016-04-08
 */
public class RecyclerViewHolder extends RecyclerView.ViewHolder {

    private static final String TAG = "RecyclerViewHolder";

    private final SparseArray<View> mViews;

    private View itemView;

    public RecyclerViewHolder(View itemView) {
        super(itemView);
        this.itemView = itemView;
        mViews = new SparseArray<>();
    }

    public View getItemView() {
        return itemView;
    }

    public static RecyclerViewHolder createViewHolder(Context context, ViewGroup parent, int layoutId, int viewType) {
        if (layoutId == 0)
            throw new IllegalStateException("The layout id must not be 0");

        return new RecyclerViewHolder(LayoutInflater.from(context).inflate(layoutId, parent, false));
    }


    public <T extends View> T getView(int viewId) {
        View view = mViews.get(viewId);
        if (view == null) {
            view = itemView.findViewById(viewId);
            mViews.put(viewId, view);
        }
        return (T) view;
    }


    //---------------------------以下为辅助方法---------------------------

    public RecyclerViewHolder setText(int viewId, String text) {
        TextView view = getView(viewId);
        view.setText(text);
        return this;
    }

    public RecyclerViewHolder setTextColor(int viewId, int textColor) {
        TextView view = getView(viewId);
        if (view != null) {
            view.setTextColor(textColor);
        }
        return this;
    }


    public RecyclerViewHolder setClickListener(int viewId, View.OnClickListener clickListener) {
        View view = getView(viewId);
        if (view != null) {
            view.setOnClickListener(clickListener);
        }
        return this;
    }


    public RecyclerViewHolder setImageResource(int viewId, int drawableId) {
        ImageView view = getView(viewId);
        if (view != null) {
            view.setImageResource(drawableId);
        }
        return this;
    }


    public RecyclerViewHolder setImageBitmap(int viewId, Bitmap bm) {
        ImageView view = getView(viewId);
        if (view == null) {
            view.setImageBitmap(bm);
        }
        return this;
    }

    public RecyclerViewHolder setImageDrawable(int viewId, Drawable drawable) {
        ImageView view = getView(viewId);
        if (view != null) {
            view.setImageDrawable(drawable);
        }
        return this;
    }

    public RecyclerViewHolder setBackgroundColor(int viewId, int color) {
        View view = getView(viewId);
        if (view != null) {
            view.setBackgroundColor(color);
        }
        return this;
    }

    public RecyclerViewHolder setBackgroundRes(int viewId, int backgroundRes) {
        View view = getView(viewId);
        if (view != null) {
            view.setBackgroundResource(backgroundRes);
        }
        return this;
    }

    @SuppressLint("NewApi")
    public RecyclerViewHolder setAlpha(int viewId, float value) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            getView(viewId).setAlpha(value);
        } else {
            // Pre-honeycomb hack to set Alpha value
            AlphaAnimation alpha = new AlphaAnimation(value, value);
            alpha.setDuration(0);
            alpha.setFillAfter(true);
            getView(viewId).startAnimation(alpha);
        }
        return this;
    }

    public RecyclerViewHolder setVisible(int viewId, boolean visible) {
        View view = getView(viewId);
        if (view != null) {
            view.setVisibility(visible ? View.VISIBLE : View.GONE);
        }
        return this;
    }

    //---------------------------关于事件的监听---------------------------

    public RecyclerViewHolder setOnClickListener(int viewId,
                                                 View.OnClickListener listener) {
        View view = getView(viewId);
        if (view != null) {
            view.setOnClickListener(listener);
        }
        return this;
    }

    public RecyclerViewHolder setOnTouchListener(int viewId,
                                                 View.OnTouchListener listener) {
        View view = getView(viewId);
        if (view != null) {
            view.setOnTouchListener(listener);
        }
        return this;
    }

    public RecyclerViewHolder setOnLongClickListener(int viewId,
                                                     View.OnLongClickListener listener) {
        View view = getView(viewId);
        if (view != null) {
            view.setOnLongClickListener(listener);
        }
        return this;
    }

}

