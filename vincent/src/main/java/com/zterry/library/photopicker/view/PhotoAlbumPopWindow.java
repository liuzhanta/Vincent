package com.zterry.library.photopicker.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;

import com.zterry.library.R;
import com.zterry.library.photopicker.adapter.AlbumAdapter;
import com.zterry.library.photopicker.bean.PhotoAlbum;

import java.util.List;


/**
 * Desc:相册选择适配popup window
 * Author: Terry
 * Date:2016-02-17
 */
public class PhotoAlbumPopWindow implements AdapterView.OnItemClickListener, PopupWindow.OnDismissListener {

    private Context context;
    private PopupWindow mPopupWindow;
    private View view;
    private AlbumAdapter mAdapter;
    private ListView mListView;

    private WindowManager.LayoutParams lp;
    private Window window;

    private List<PhotoAlbum> data;

    private OnPhotoAlbumItemClickListener onPhotoAlbumItemClickListener;

    public PhotoAlbumPopWindow(Context context) {
        this.context = context;
        view = LayoutInflater.from(context).inflate(getPopMenuLayoutId(), null);
        initPopSetting(context);
        initView(view);
    }

    public void setData(List<PhotoAlbum> data) {
        this.data = data;
        mAdapter.setData(data);
        mAdapter.setSelected(0);
    }

    private void initPopSetting(Context context) {
        int screenHeight = HBUtils.getScreenHeight(context);
        mPopupWindow = new PopupWindow(view, LinearLayout.LayoutParams.MATCH_PARENT,
                (int) (screenHeight * 0.8));
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.setOnDismissListener(this);
        mPopupWindow.setAnimationStyle(R.style.popwindow_show_from_bottom);
        mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
        mPopupWindow.getContentView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_MENU && event.getRepeatCount() == 0 && event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (mPopupWindow != null && mPopupWindow.isShowing()) {
                        dismiss();
                    }
                    return true;
                }
                return false;
            }
        });
    }

    public boolean isShowing() {
        if (mPopupWindow == null) {
            return false;
        }
        return mPopupWindow.isShowing();
    }

    private void initView(View view) {
        mAdapter = new AlbumAdapter(context);
        mListView = (ListView) view.findViewById(R.id.lv_album);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(this);
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                dismiss();
                return false;
            }
        });
    }

    protected int getPopMenuLayoutId() {
        return R.layout.pop_photo_album_layout;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (onPhotoAlbumItemClickListener != null) {
            if (data != null) {
                onPhotoAlbumItemClickListener.OnPhotoAlbumItemClick(data.get(position));
                mAdapter.setSelected(position);
            }
        }
        dismiss();
    }

    public void setOnPhotoAlbumItemClickListener(OnPhotoAlbumItemClickListener onPhotoAlbumItemClickListener) {
        this.onPhotoAlbumItemClickListener = onPhotoAlbumItemClickListener;
    }

    private void setWindowFake(boolean isTranslucent) {
        window = ((Activity) context).getWindow();
        lp = window.getAttributes();
        lp.alpha = isTranslucent ? 0.5f : 1.0f;
        window.setAttributes(lp);
    }

    @Override
    public void onDismiss() {
        setWindowFake(false);
    }

    public void dismiss() {
        if (mPopupWindow != null) {
            mPopupWindow.dismiss();
        }
    }

    public void show(View v) {
        if (((Activity) context).isFinishing()) {
            return;
        }
        int[] location = new int[2];
        v.getLocationOnScreen(location);
        if (mPopupWindow != null) {
            mPopupWindow.showAtLocation(v, Gravity.TOP, location[0], location[1]);
            setWindowFake(true);
        }
    }

    public interface OnPhotoAlbumItemClickListener {
        void OnPhotoAlbumItemClick(PhotoAlbum album);
    }
}
