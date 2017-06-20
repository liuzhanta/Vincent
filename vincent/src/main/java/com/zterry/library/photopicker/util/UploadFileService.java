package com.zterry.library.photopicker.util;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;

import com.huoban.config.Config;
import com.huoban.manager.ChatAttachmentManager;
import com.huoban.model2.ChatAttachment;
import com.huoban.network.APIQueue;
import com.huoban.tools.HBUtils;
import com.huoban.tools.LogUtil;

import java.util.ArrayList;

import huoban.api.file.FileResult;
import huoban.api.file.UploadFile;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class UploadFileService extends IntentService {

    private static final String ACTION_UPLOAD_FILE = "com.huoban.photopicker.util.action.ACTION_UPLOAD_FILE";

    public static final String EXTRA_PARAM_ATTACHMENT = "EXTRA_PARAM_ATTACHMENT";

    public static UploadFile.IFileResultCallBack uploadResultListener = null;

    public UploadFileService() {
        super("UploadFileService");
    }

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    public static void startUploadService(Context context, ArrayList<ChatAttachment> attachments) {
        Intent intent = new Intent(context, UploadFileService.class);
        intent.setAction(ACTION_UPLOAD_FILE);
        intent.putExtra(EXTRA_PARAM_ATTACHMENT, attachments);
        context.startService(intent);
    }

    public static void startUploadService(Context context, ChatAttachment attachment) {
        ArrayList<ChatAttachment> attachments = new ArrayList<>();
        attachments.add(attachment);
        startUploadService(context, attachments);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_UPLOAD_FILE.equals(action)) {
                final ArrayList<ChatAttachment> attachments = (ArrayList<ChatAttachment>) intent.getSerializableExtra(EXTRA_PARAM_ATTACHMENT);
                handleActionUploadFile(attachments);
            }
        }
    }

    private static final String TAG = "UploadFileService";

    private void handleActionUploadFile(ArrayList<ChatAttachment> attachments) {
        LogUtil.d(TAG, "handleActionUploadFile#");
        //start upload image
        for (ChatAttachment attachment : attachments) {
            LogUtil.d(TAG,"上传附件——>"+attachment);

            final String imagePath = attachment.getLocalLink();//原始图片
            final String fileId = attachment.getFileId();

            //复制旧的图片到新的路径：缓存目录
            com.huoban.tools.FileUtils.copyFile(imagePath,
                    Config.SDCARD_CACHE_FILE_PATH + HBUtils.stringToMD5(fileId));

            //复制旧的图片到新的路径：大图目录
            com.huoban.tools.FileUtils.copyFile(imagePath,
                    Config.SDCARD_PICTURE_PATH + HBUtils.stringToMD5(fileId)
                            + ".jpg");
            //设置新的本地路径
            attachment.setLocalLink(Config.SDCARD_CACHE_FILE_PATH
                    + HBUtils.stringToMD5(fileId));

            ChatAttachmentManager.getInstance().saveAttacht(attachment);
            final String filePath = Config.SDCARD_PICTURE_PATH + HBUtils.stringToMD5(fileId) + ".jpg";

            LogUtil.d(TAG, "filename = " + fileId + " , filePath = " + filePath);
            APIQueue.getInstance().execute(new UploadFile(filePath, fileId, new UploadFile.IFileResultCallBack() {
                @Override
                public synchronized void onResult(FileResult fileResult) {
                    if (uploadResultListener != null) {
                        uploadResultListener.onResult(fileResult);
                    }
                }

                @Override
                public synchronized void onError(Exception ex, String path) {
                    if (uploadResultListener != null) {
                        uploadResultListener.onError(ex, path);
                    }
                }
            }, UploadFile.ATTACHMENT_TYPE));
        }
    }

    public static void setOnUploadResultListener(UploadFile.IFileResultCallBack result) {
        uploadResultListener = result;
    }

}
