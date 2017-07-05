package com.zterry.imagepicker.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.AppCompatCheckBox;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.zterry.imagepicker.ImageParams;
import com.zterry.imagepicker.R;
import com.zterry.imagepicker.adapter.base.BaseRecyclerViewAdapter;
import com.zterry.imagepicker.adapter.base.RecyclerViewHolder;
import com.zterry.imagepicker.bean.ImageFile;
import com.zterry.imagepicker.util.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


/**
 * Description:  <br>
 * Author:Terry<br>
 * Date:2017/6/20 下午5:14
 */

public class ImagePickerAdapter extends BaseRecyclerViewAdapter<ImageFile> {

    private int imageWidth = 0;

    public List<ImageFile> getSelectedImageFiles() {
        return this.selectedImageFiles;
    }

    private ImagePickerAdapter.OnImageSelectedListener onImageSelectedListener;

    private List<ImageFile> selectedImageFiles = new ArrayList<>();

    public ImagePickerAdapter(Context mContext) {
        super(mContext);
        imageWidth = (int) ((Utils.getScreenWidth(mContext) - getResources().getDimension(R.dimen.grid_list_padding) * 4) / 3);
    }

    public void setSelectedImageFiles(List<ImageFile> selectedImageFiles) {
        this.selectedImageFiles = selectedImageFiles;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.adapter_item_grid_multi_select;
    }

    @Override
    protected void onBindViewData(RecyclerViewHolder holder, final ImageFile imageFile, int position) {
        //set image
        final ImageView imageView = holder.getView(R.id.image_view);
        if (ImageParams.placeHolder != 0) {
            imageView.setBackgroundResource(ImageParams.placeHolder);
        }
        final ViewGroup.LayoutParams layoutParams = imageView.getLayoutParams();
        layoutParams.height = layoutParams.width = imageWidth;
        imageView.setLayoutParams(layoutParams);

        Glide.with(mContext)
                .load(new File(imageFile.getUri()))
                .into(imageView);

        //set checkbox listener
        AppCompatCheckBox checkbox = holder.getView(R.id.checkbox);
        checkbox.setChecked(selectedImageFiles.contains(imageFile));
        checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    if (selectedImageFiles.size() >= ImageParams.maxSelectCount) {
                        Toast.makeText(mContext, ImageParams.overMaxSelectCountMessage,
                                Toast.LENGTH_SHORT).show();
                        buttonView.setChecked(false);
                        return;
                    }
                    selectedImageFiles.add(imageFile);
                    imageView.setColorFilter(Color.parseColor("#77000000"));
                } else {
                    selectedImageFiles.remove(imageFile);
                    imageView.setColorFilter(null);

                }
                if (onImageSelectedListener != null) {
                    onImageSelectedListener.onImageSelected(imageFile, selectedImageFiles.size());
                }
            }
        });
    }

    public void setOnImageSelectedListener(ImagePickerAdapter.OnImageSelectedListener
                                                   onImageSelectedListener) {
        this.onImageSelectedListener = onImageSelectedListener;
    }


    public interface OnImageSelectedListener {
        void onImageSelected(ImageFile imageFile, int totalSelectedCount);
    }
}
