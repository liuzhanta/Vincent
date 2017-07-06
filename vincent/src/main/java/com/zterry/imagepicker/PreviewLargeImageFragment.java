package com.zterry.imagepicker;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.piasy.biv.view.BigImageView;
import com.zterry.imagepicker.bean.ImageFile;

import static com.zterry.imagepicker.util.GetAlbumListAsyncTask.TAG;

/**
 * Class: PreviewLargeImageFragment <br>
 * Description: 预览大图 <br>
 * Author: Terry <br>
 * Date: 2017/7/5 下午3:47
 */
public class PreviewLargeImageFragment extends Fragment {

    private static final String TAG = "PreviewLargeImageFragment";
    private ImageFile imageFile;
    private BigImageView bigImageView;

    private PreviewLargeImageFragment(ImageFile imageFile) {
        this.imageFile = imageFile;
    }

    public PreviewLargeImageFragment() {
    }

    public static PreviewLargeImageFragment createPreviewLargeImageFragment(ImageFile imageFile) {
        return new PreviewLargeImageFragment(imageFile);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_preview_large_image, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        bigImageView = (BigImageView) getView().findViewById(R.id.mBigImage);
        if (imageFile != null && !TextUtils.isEmpty(imageFile.getThumbnail())) {
            bigImageView.showImage(Uri.parse("file://" + imageFile.getUri()));
        }
    }

}
