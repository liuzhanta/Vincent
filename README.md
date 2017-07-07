![](https://github.com/liuzhanta/Vincent/blob/master/screen_shot/market.png)

# Vincent
Vincent is a Material-Design local image selector for Android.  

### Feature:
- [x] Toolbar and BottomAction Layout behavior;
- [x] Standard Material Design Components.For example,*BottomSheetDialog*,*Toolbar*,*RecyclerView* and so on;
- [x] Material Design Transition Animations;
- [x] Image Auto Fit ScaleType;
- [x] The Zoomable Gesture used on preview large Image;
- [x] Preview Larger Image Supported.


![Material Design Toolbar](https://github.com/liuzhanta/Vincent/blob/master/screen_shot/device-2017-07-05-164616.png)
![Bottom Sheet Behavior](https://github.com/liuzhanta/Vincent/blob/master/screen_shot/device-2017-07-05-164812.png)
![Preview large Image](https://github.com/liuzhanta/Vincent/blob/master/screen_shot/device-2017-07-06-191318.png)
![Toolbar Scroll Flag](https://github.com/liuzhanta/Vincent/blob/master/screen_shot/image_scroll.gif)
## Download
Add the Vincent dependency in your build.gradle.
    
    dependencies {
       compile 'com.zterry.imagepicker:Vincent:1.0.0'
    }



## How do I use Vincent?
#### Permission
The library requires two permissions:
- `android.permission.READ_EXTERNAL_STORAGE`
- `android.permission.WRITE_EXTERNAL_STORAGE`

So if you are targeting Android 6.0+, you need to handle runtime permission request before next step.

#### Simple usage snippet
------
Start `ImagePickerActivity` from current `Activity`:

```java
 Vincent.from(this)
                .maxSelectCount(9)
                .colorPrimary(R.color.colorPrimary)
                .titleColor(R.color.colorAccent)
                .title(R.string.app_name)
                .layoutBehavior(true)
                .overMaxSelectCountMessage(R.string.over_max_limit)
                .placeHolder(R.drawable.bg_photo_place_holder)
                .toPicker();
```
 
#### Themes
You can define your own theme as you wish.

#### Receive Result
In `onActivityResult()` callback of the starting `Activity` or `Fragment`:

```java
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
```

## Developed by

Name: ZTerry Liu  
E-mail: tata1989y@gmail.com  
Subject: Vincent 
 
## License


    Copyright 2016 刘战塔
    
    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at
 
      http://www.apache.org/licenses/LICENSE-2.0
 
    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.  
