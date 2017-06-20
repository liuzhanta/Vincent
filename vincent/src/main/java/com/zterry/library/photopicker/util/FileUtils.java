package com.zterry.library.photopicker.util;

import com.huoban.tools.HBUtils;

import java.io.File;
import java.text.DecimalFormat;
import java.util.List;

/**
 * Desc:
 * Author: Terry
 * Date:2016-02-29
 */
public class FileUtils {
    private FileUtils() {
    }

    public static int calculateFileSize(List<String> pathList) {
        int size = 0;
        if (HBUtils.isEmpty(pathList)) {
            return 0;
        }
        for (String s : pathList) {
            size += new File(s).length();
        }
        return size;
    }

    /**
     * 得到格式化的文件大小
     *
     * @param pathList
     * @return
     */
    public static String getFormatFileSize(List<String> pathList) {
        float size = calculateFileSize(pathList);//in bytes
        String formatSize = null;
        final DecimalFormat decimalFormat = new DecimalFormat("0.00");
        if (size / 1024 > 1024) {
            formatSize = decimalFormat.format(size / 1024 / 1024) + "M";
        } else {
            formatSize = decimalFormat.format(size / 1024) + "K";
        }
        return formatSize;
    }
}
