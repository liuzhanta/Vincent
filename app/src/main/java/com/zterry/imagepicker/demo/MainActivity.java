package com.zterry.imagepicker.demo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.zterry.imagepicker.Vincent;
import com.zterry.imagepicker.bean.ImageFile;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onClick(View v) {
        Vincent.from(this)
                .maxSelectCount(9)
                .colorPrimary(R.color.colorPrimary)
                .titleColor(R.color.colorAccent)
                .title(R.string.app_name)
                .layoutBehavior(true)
                .overMaxSelectCountMessage(R.string.over_max_limit)
                .placeHolder(R.drawable.bg_photo_place_holder)
                .toPicker();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (Vincent.hasResult(requestCode, resultCode)) {
            List<ImageFile> imageFiles = Vincent.handleActivityResult(data);
            for (ImageFile imageFile : imageFiles) {
                Log.d(TAG, "onActivityResult: imageFile =" + imageFile);
            }
        }
    }
}
