package com.zterry.library.photopicker.util;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.huoban.config.Config;
import com.huoban.model2.ChatAttachment;
import com.huoban.network.APIQueue;
import com.huoban.tools.BitmapUtils;
import com.huoban.tools.HBUtils;
import com.huoban.tools.LogUtil;

import java.util.ArrayList;

/**
 * Desc:处理图片
 * Author: Terry
 * Date:2016-03-07
 */
public class PhotoTaskHandler implements Runnable {

    private static final int MSG_OK = 333;
    private Context context;

    private ArrayList<ChatAttachment> attachaments = new ArrayList<>();

    private OnTaskCompleteListener onTaskCompleteListener;

    public static int TAKE_PHOTO_REQUEST_CODE = 111;

    private Handler handler;
    private ArrayList<PhotoModel> data;

    public PhotoTaskHandler(Context context) {
        this.context = context;
        this.handler = new Handler(context.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == MSG_OK) {
                    if (onTaskCompleteListener != null) {
                        onTaskCompleteListener.onComplete(attachaments);
                    }
                }
            }
        };
    }

    public void setOnTaskCompleteListener(OnTaskCompleteListener onTaskCompleteListener) {
        this.onTaskCompleteListener = onTaskCompleteListener;
    }

    public void setData(ArrayList<PhotoModel> data) {
        this.data = data;
    }

    public interface OnTaskCompleteListener {
        void onComplete(ArrayList<ChatAttachment> data);
    }

    public static class PhotoModel {

        public PhotoModel(String path, int from) {
            this.path = path;
            this.from = from;
        }

        String path;
        int from;

    }

    public void start() {
        APIQueue.getInstance().execute(this);
    }

    @Override
    public void run() {
        LogUtil.d("Tata","start photo task#");
        for (PhotoModel photoModel : data) {
            handlePhoto(photoModel.path, photoModel.from);
        }
        handler.sendEmptyMessage(MSG_OK);
    }

    private void handlePhoto(String photoPath, int from) {
        ChatAttachment attachment = new ChatAttachment();

        //1.set file id
        String fileId = HBUtils.mucJID();
        attachment.setFileId(fileId);

        //复制旧的图片到新的路径：缓存目录
        com.huoban.tools.FileUtils.copyFile(photoPath,
                Config.SDCARD_CACHE_FILE_PATH + HBUtils.stringToMD5(fileId));

        //复制旧的图片到新的路径：大图目录
        com.huoban.tools.FileUtils.copyFile(photoPath,
                Config.SDCARD_PICTURE_PATH + HBUtils.stringToMD5(fileId)
                        + ".jpg");

        //设置本地缓存链接
        attachment.setLocalLink(Config.SDCARD_CACHE_FILE_PATH
                + HBUtils.stringToMD5(fileId));

        /***
         * 如果是来自照相机的操作，删除原图片
         */
        if (from == TAKE_PHOTO_REQUEST_CODE) {
            com.huoban.tools.FileUtils.deleteFile(photoPath);
        }
        // 压缩质量到缩略图级别
        BitmapUtils.decodeBitmapFromFile(Config.SDCARD_CACHE_FILE_PATH
                        + HBUtils.stringToMD5(fileId), HBUtils.dipToPx(120),
                HBUtils.dipToPx(120), 40);
        // 压缩大图质量到70%
        BitmapUtils.decodeBitmapFromFile(
                Config.SDCARD_PICTURE_PATH + HBUtils.stringToMD5(fileId)
                        + ".jpg", 620, 3000, 70);
        // 获得图片附件宽高
        int[] WH = BitmapUtils.decodeBitmapSize(Config.SDCARD_PICTURE_PATH
                + HBUtils.stringToMD5(fileId) + ".jpg", 620, 3000);
        attachment.setWidth(WH[0]);
        attachment.setHeight(WH[1]);
        attachaments.add(attachment);
    }
}
