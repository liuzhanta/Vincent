package com.zterry.imagepicker.bean;

import java.util.List;

/**
 * Desc:相册实体类
 * Author: Terry
 * Date:2016-02-17
 */
public class PhotoAlbum {
    /**
     * 图片的文件夹路径
     */
    private String dir;

    /**
     * 里面包含的图片
     */
    private List<ImageFile> imageFiles;

    /**
     * 第一张图片的路径
     */
    private String firstImagePath;

    public PhotoAlbum() {
    }

    public PhotoAlbum(String name) {
        this.name = name;
    }

    public List<ImageFile> getImageFiles() {

        return imageFiles;
    }

    public void setImageFiles(List<ImageFile> imageFiles) {
//        if (!HBUtils.isEmpty(imageFiles)) {
//            for (ImageFile imageFile : imageFiles) {
//                imageFile.setNewUri( imageFile.getUri());
//            }
//        }
        this.imageFiles = imageFiles;
    }


    /**
     * 图片的数量
     */
    private int count;

    /**
     * 文件夹的名称
     */
    private String name;


    public void setName(String name) {
        this.name = name;
    }

    public String getDir() {
        return dir;
    }

    public void setDir(String dir) {
        this.dir = dir;
    }

    public String getFirstImagePath() {
        return firstImagePath;
    }

    public void setFirstImagePath(String firstImagePath) {
        this.firstImagePath = firstImagePath;
    }

    public String getName() {
        return name;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    @Override
    public String toString() {
        return "PhotoAlbum{" +
                "dir='" + dir + '\'' +
                ", firstImagePath='" + firstImagePath + '\'' +
                ", imageFiles=" + imageFiles +
                ", count=" + count +
                ", name='" + name + '\'' +
                '}';
    }

    public void setAllImages(List<ImageFile> imagesAll) {
        this.imageFiles = imagesAll;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;

        PhotoAlbum that = (PhotoAlbum) o;

        return this.name.equals(that.name);

    }

    @Override
    public int hashCode() {
        return this.name.hashCode();
    }
}
