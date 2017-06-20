package com.zterry.library.photopicker.bean;

import android.text.TextUtils;

import java.io.Serializable;

/**
 * Desc:图片文件
 * Author: Terry
 * Date:2016-03-01
 */
public class ImageFile implements Serializable {

    /**
     * 图片在数据库中的id
     */
    private int id;
    /**
     * 图片的缩略图
     */
    private String thumbnail;
    /**
     * 图片的原始图片
     */
    private String uri;
    /**
     * 图片宽度
     */
    private int width;
    /**
     * 图片高度
     */
    private int height;

    public ImageFile(int id, String thumbnail, String uri, String displayName) {
        this.id = id;
        this.thumbnail = thumbnail;
        this.uri = uri;
        this.displayName = displayName;
    }

    public ImageFile(int id) {
        this.id = id;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    /**
     * 所属相册名称
     */
    private String displayName;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }


    public String getUri() {
        return uri;
    }

    public ImageFile(int id, String thumbnail, String uri) {
        this.id = id;
        this.thumbnail = thumbnail;
        this.uri = uri;
    }

    public ImageFile(String uri) {
        this.uri = uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ImageFile imageFile = (ImageFile) o;

        if (TextUtils.isEmpty(uri)) {
            return false;
        }
        return uri.equals(imageFile.uri);

    }

    @Override
    public int hashCode() {
        return uri.hashCode();
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return "ImageFile{" +
                "id=" + id +
                ", thumbnail='" + thumbnail + '\'' +
                ", uri='" + uri + '\'' +
                ", displayName='" + displayName + '\'' +
                '}';
    }
}
