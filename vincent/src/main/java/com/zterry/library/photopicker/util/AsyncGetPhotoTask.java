package com.zterry.library.photopicker.util;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import android.text.TextUtils;


import com.zterry.library.photopicker.bean.ImageFile;
import com.zterry.library.photopicker.bean.PhotoAlbum;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Desc:获取相册图片异步任务
 * Author: Terry
 * Date:2016-02-18
 */
public class AsyncGetPhotoTask implements Runnable {

    public static final String TAG = "AsyncGetPhotoTask";

    private Context context;
    private List<PhotoAlbum> mPhotoAlbums = new ArrayList<>();
    private IPhotoAlbumListGet onPhotoAlbumListGet;

    public AsyncGetPhotoTask(Context context) {
        this.context = context;
    }

    /**
     * Called when the photo album list data call back.
     */
    public interface IPhotoAlbumListGet {
        void onPhotoAlbumListGet(List<PhotoAlbum> data);
    }

    /**
     * 1.从相册的数据库中查出有所图
     * 2.从本地缩略图数据库查出所有缩略图
     * 3.1。2进行ID映射，如果没有映射到缩略图，则使用原图，进行压缩
     */
    public void doGetAlbumList() {
        //1.-----------------------> 查询缩略图 <-----------------------
        final Cursor thumbnailCursor = new GetImageThumbnailCursor(context).query();
        final Map<Integer, String> map = new LinkedHashMap<>();
        if (!ImageQuery.isCursorEmpty(thumbnailCursor)) {
            thumbnailCursor.moveToFirst();
            if (!ImageQuery.isCursorEmpty(thumbnailCursor)) {
                while (thumbnailCursor.moveToNext()) {
                    final String data = ImageQuery.getString(thumbnailCursor,
                            MediaStore.Images.Thumbnails.DATA);//
                    final int imageId = ImageQuery.getInt(thumbnailCursor, //
                            MediaStore.Images.Thumbnails.IMAGE_ID);
                    map.put(imageId, data);
                }
            }
            thumbnailCursor.close();
        }

        //2.-----------------------> 查询相册名称列表 <-----------------------
        final Cursor albumCursor = new GetPhotoAlbumCursor(context).query();
        final List<String> albumNameList = new ArrayList<>();
        albumCursor.moveToFirst();
        while (albumCursor.moveToNext()) {
            final String displayName = ImageQuery.getString(albumCursor,
                    MediaStore.Images.Media.BUCKET_DISPLAY_NAME);//
            if (!albumNameList.contains(displayName)) {
                albumNameList.add(displayName);
            } else {
                continue;
            }
        }
        albumCursor.close();
        Collections.sort(albumNameList, String.CASE_INSENSITIVE_ORDER);

        //3.-----------------------> 查询所有图片 <-----------------------
        GetPhotoListCursor getPhotoListCursor = new GetPhotoListCursor(context);
        final Cursor imageCursor = getPhotoListCursor.query();
        imageCursor.moveToFirst();

        List<ImageFile> imageFiles = new ArrayList<>();
        while (imageCursor.moveToNext()) {
            String uri = ImageQuery.getString(imageCursor, MediaStore.Images.Media.DATA);
            String displayName = ImageQuery.getString(imageCursor,
                    MediaStore.Images.Media.BUCKET_DISPLAY_NAME);//
            int id = ImageQuery.getInt(imageCursor, MediaStore.Images.Media._ID);

            String thumbnail = map.get(id);//根据 IMAGE_ID 从缩略图的缓存中查出缩略图路径
            imageFiles.add(new ImageFile(id, thumbnail, uri, displayName));
        }
        imageCursor.close();

        //4.-----------------------> 对图片进行归类 <-----------------------
        for (String albumName : albumNameList) {
            PhotoAlbum photoAlbum = new PhotoAlbum();

            List<ImageFile> imageFileList = new ArrayList<>();
            for (ImageFile imageFile : imageFiles) {
                if (albumName.equals(imageFile.getDisplayName())) {
                    imageFileList.add(imageFile);
                } else {
                    continue;
                }
            }

            //set dir first
            if (TextUtils.isEmpty(photoAlbum.getDir())) {
                photoAlbum.setDir(new File(imageFileList.get(0).//
                        getUri()).getParentFile().getAbsolutePath());
            }
//            Collections.reverse(imageFiles);//按创建时间排序
            Collections.sort(imageFileList, new MyImageFileComparator());
            photoAlbum.setImageFiles(imageFileList);

            //set photo album image's count
            photoAlbum.setCount(photoAlbum.getImageFiles().size());

            //set photo album name
            photoAlbum.setName(albumName);

            //set first image path
            photoAlbum.setFirstImagePath(photoAlbum.getImageFiles().get(0).getUri());

            mPhotoAlbums.add(photoAlbum);
        }

        if (onPhotoAlbumListGet != null) {
            onPhotoAlbumListGet.onPhotoAlbumListGet(mPhotoAlbums);
        }
    }

    private class MyImageFileComparator implements Comparator<ImageFile> {
        @Override
        public int compare(ImageFile lhs, ImageFile rhs) {
            if (new File(lhs.getUri()).lastModified() < new File(rhs.getUri()).lastModified()) {
                return 1;
            } else if (new File(lhs.getUri()).lastModified() > new File(rhs.getUri()).lastModified()) {
                return -1;
            } else {
                return 0;
            }
        }
    }

    @Override
    public void run() {
        doGetAlbumList();
    }

    public void start(IPhotoAlbumListGet onPhototaskget) {
        mPhotoAlbums.clear();
        this.onPhotoAlbumListGet = onPhototaskget;
        APIQueue.getInstance().execute(this);
    }
}

