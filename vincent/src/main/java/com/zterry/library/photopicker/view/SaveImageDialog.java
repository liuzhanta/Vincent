package com.zterry.library.photopicker.view;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.huoban.R;
import com.huoban.tools.HBUtils;

/**
 * Desc:
 * Author: Terry
 * Date:2016-03-23
 */
public class SaveImageDialog {

    public static void showImageSaveDialog(final Context mContext, final OnImageSaveEventClickListener listener) {
        View mView = LayoutInflater.from(mContext).inflate(R.layout.save_image_dialog, null);
        final Dialog mDialog =new Dialog(mContext,R.style.SingleChoiceDialogStyle);

        DialogInterface.OnKeyListener keyListener = new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode,
                                 KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK
                        && event.getAction() == KeyEvent.ACTION_DOWN) {
                    mDialog.dismiss();
                }
                return false;
            }
        };
        mDialog.setOnKeyListener(keyListener);
        mDialog.setCanceledOnTouchOutside(true);
        mDialog.setCancelable(true);

//		mDialog.setContentView(R.layout.share_dialog_layout);

        TextView save = (TextView) mView.findViewById(R.id.save);
        TextView cancel = (TextView) mView.findViewById(R.id.cancel);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) listener.onCancel();
                mDialog.dismiss();
            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) listener.onSave();
                mDialog.dismiss();
            }
        });
        mDialog.show();


        final Window window = mDialog.getWindow();
        window.setContentView(mView);
        window.getDecorView().setPadding(0, 0, 0, 0);
        window.setGravity(Gravity.BOTTOM);
        window.setWindowAnimations(R.style.Animation_DialogWindow);

        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = HBUtils.getScreenWidth(mContext);
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        window.setAttributes(lp);
    }

    public interface OnImageSaveEventClickListener {
        void onCancel();

        boolean onSave();
    }
}
