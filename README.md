# PictureSelector-Plus
# 注：本代码修改自[PictureSelector 2.0](https://github.com/LuckSiege/PictureSelector)
   一款针对android平台下的图片选择器，支持从相册或拍照选择图片或视频、音频，支持动态权限获取、裁剪(单图or多图裁剪)、压缩、图片编辑、主题自定义配置等功能、适配android 6.0+系统的开源图片选择框架。<br>  
  
## 目录
-[功能特点](#功能特点)<br>
-[常见错误](#常见错误)<br>
-[功能配置](#功能配置)<br>
-[缓存清除](#缓存清除)<br>
-[主题配置](#主题配置)<br>
-[常用功能](#常用功能)<br>
-[结果回调](#结果回调)<br>
-[混淆配置](#混淆配置)<br>
-[兼容性测试](#兼容性测试)<br>
-[演示效果](#演示效果)<br>


# 功能特点

* 1.适配android6.0+系统
* 2.解决部分机型裁剪闪退问题
* 3.解决图片过大oom闪退问题
* 4.动态获取系统权限，避免闪退
* 5.支持相片or视频的单选和多选
* 6.支持裁剪比例设置，如常用的 1:1、3：4、3:2、16:9 默认为图片大小
* 7.支持视频预览
* 8.支持gif图片
* 9.支持.webp格式图片 
* 10.支持一些常用场景设置：如:是否裁剪、是否预览图片、是否显示相机等
* 11.新增自定义主题设置
* 12.新增图片勾选样式设置
* 13.新增图片裁剪宽高设置
* 14.新增图片压缩处理
* 15.新增录视频最大时间设置
* 16.新增视频清晰度设置
* 17.新增QQ选择风格，带数字效果 
* 18.新增自定义 文字颜色 背景色让风格和项目更搭配
* 19.新增多图裁剪功能
* 20.新增LuBan多图压缩
* 21.新增单独拍照功能
* 22.新增压缩大小设置
* 23.新增Luban压缩档次设置
* 24.新增圆形头像裁剪
* 25.新增音频功能查询


重要的事情说三遍记得添加权限

```
  <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
  <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
  <uses-permission android:name="android.permission.CAMERA" />
    
```


## 常见错误
```
 重要：PictureSelector.create()；调用此方法时，在activity中传activity.this，在fragment中请传fragment.this,
 影响回调到哪个地方的onActivityResult()。
 
 问题一：
 rxjava冲突：在app build.gradle下添加
 packagingOptions {
   exclude 'META-INF/rxjava.properties'
 }  
 
 问题二：
 java.lang.NullPointerException: 
 Attempt to invoke virtual method 'android.content.res.XmlResourceParser 
 android.content.pm.ProviderInfo.loadXmlMetaData(android.content.pm.PackageManager, java.lang.String)'
 on a null object reference
 
 * 注意 从v2.1.3版本中，将不需要配制以下内容
 
 application下添加如下节点:
 
 <provider
      android:name="android.support.v4.content.FileProvider"
      android:authorities="${applicationId}.provider"
      android:exported="false"
      android:grantUriPermissions="true">
       <meta-data
         android:name="android.support.FILE_PROVIDER_PATHS"
         android:resource="@xml/file_paths" />
</provider>

注意：如已添加其他sdk或项目中已使用过provider节点，
[请参考我的博客](http://blog.csdn.net/luck_mw/article/details/54970105)的解决方案

问题三：
经测试在小米部分低端机中，Fragment调用PictureSelector 2.0 拍照有时内存不足会暂时回收activity,
导致其fragment会重新创建 建议在fragment所依赖的activity加上如下代码:
if (savedInstanceState == null) {
      // 添加显示第一个fragment
      	fragment = new PhotoFragment();
      		getSupportFragmentManager().beginTransaction().add(R.id.tab_content, fragment,
                    PictureConfig.FC_TAG).show(fragment)
                    .commit();
     } else { 
      	fragment = (PhotoFragment) getSupportFragmentManager()
          .findFragmentByTag(PictureConfig.FC_TAG);
}
这里就是如果是被回收时，则不重新创建 通过tag取出fragment的实例。

问题四：
glide冲突
由于PictureSelector 2.0引入的是最新的glide 4.5.0,所以将项目中老版本的glide删除,并且将报错代码换成如下写法：
RequestOptions options = new RequestOptions();
options.placeholder(R.drawable.image);
Glide.with(context).load(url).apply(options).into(imageView);

```

## 功能配置
```
// 进入相册 以下是例子：用不到的api可以不写
 PictureSelector.create(MainActivity.this)
 	.openGallery()//全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()、音频.ofAudio()
 	.theme()//主题样式(不设置为默认样式) 也可参考demo values/styles下 例如：R.style.picture.white.style
 	.maxSelectNum()// 最大图片选择数量 int
 	.minSelectNum()// 最小选择数量 int
	.imageSpanCount(4)// 每行显示个数 int
 	.selectionMode()// 多选 or 单选 PictureConfig.MULTIPLE or PictureConfig.SINGLE
 	.previewImage()// 是否可预览图片 true or false
 	.previewVideo()// 是否可预览视频 true or false
	.enablePreviewAudio() // 是否可播放音频 true or false
 	.isCamera()// 是否显示拍照按钮 true or false
	.imageFormat(PictureMimeType.PNG)// 拍照保存图片格式后缀,默认jpeg
	.isZoomAnim(true)// 图片列表点击 缩放效果 默认true
	.sizeMultiplier(0.5f)// glide 加载图片大小 0~1之间 如设置 .glideOverride()无效
	.setOutputCameraPath("/CustomPath")// 自定义拍照保存路径,可不填
 	.enableCrop()// 是否裁剪 true or false
 	.compress()// 是否压缩 true or false
 	.glideOverride()// int glide 加载宽高，越小图片列表越流畅，但会影响列表图片浏览的清晰度
 	.withAspectRatio()// int 裁剪比例 如16:9 3:2 3:4 1:1 可自定义
 	.hideBottomControls()// 是否显示uCrop工具栏，默认不显示 true or false
 	.isGif()// 是否显示gif图片 true or false
	.compressSavePath(getPath())//压缩图片保存地址
 	.freeStyleCropEnabled()// 裁剪框是否可拖拽 true or false
 	.circleDimmedLayer()// 是否圆形裁剪 true or false
 	.showCropFrame()// 是否显示裁剪矩形边框 圆形裁剪时建议设为false   true or false
 	.showCropGrid()// 是否显示裁剪矩形网格 圆形裁剪时建议设为false    true or false
 	.openClickSound()// 是否开启点击声音 true or false
 	.selectionMedia()// 是否传入已选图片 List<LocalMedia> list
 	.previewEggs()// 预览图片时 是否增强左右滑动图片体验(图片滑动一半即可看到上一张是否选中) true or false
 	.cropCompressQuality()// 裁剪压缩质量 默认90 int
 	.minimumCompressSize(100)// 小于100kb的图片不压缩 
 	.synOrAsy(true)//同步true或异步false 压缩 默认同步
 	.cropWH()// 裁剪宽高比，设置如果大于图片本身宽高则无效 int 
 	.rotateEnabled() // 裁剪是否可旋转图片 true or false
 	.scaleEnabled()// 裁剪是否可放大缩小图片 true or false
 	.videoQuality()// 视频录制质量 0 or 1 int
	.videoMaxSecond(15)// 显示多少秒以内的视频or音频也可适用 int 
        .videoMinSecond(10)// 显示多少秒以内的视频or音频也可适用 int 
	.recordVideoSecond()//视频秒数录制 默认60s int
	.isDragFrame(false)// 是否可拖动裁剪框(固定)
 	.forResult(PictureConfig.CHOOSE_REQUEST);//结果回调onActivityResult code     
```

## 缓存清除
```
 //包括裁剪和压缩后的缓存，要在上传成功后调用，注意：需要系统sd卡权限 
 PictureFileUtils.deleteCacheDirFile(MainActivity.this);
 
```
## 主题配置

```
<!--默认样式 注意* 样式只可修改，不能删除任何一项 否则报错-->
    <style name="picture.default.style" parent="Theme.AppCompat.Light.DarkActionBar">
        <!-- Customize your theme here. -->
        <!--标题栏背景色-->
        <item name="colorPrimary">@color/bar_grey</item>
        <!--状态栏背景色-->
        <item name="colorPrimaryDark">@color/bar_grey</item>
        <!--是否改变图片列表界面状态栏字体颜色为黑色-->
        <item name="picture.statusFontColor">false</item>
        <!--返回键图标-->
        <item name="picture.leftBack.icon">@drawable/picture_back</item>
        <!--标题下拉箭头-->
        <item name="picture.arrow_down.icon">@drawable/arrow_down</item>
        <!--标题上拉箭头-->
        <item name="picture.arrow_up.icon">@drawable/arrow_up</item>
        <!--标题文字颜色-->
        <item name="picture.title.textColor">@color/white</item>
        <!--标题栏右边文字-->
        <item name="picture.right.textColor">@color/white</item>
        <!--图片列表勾选样式-->
        <item name="picture.checked.style">@drawable/checkbox_selector</item>
        <!--开启图片列表勾选数字模式-->
        <item name="picture.style.checkNumMode">false</item>
        <!--选择图片样式0/9-->
        <item name="picture.style.numComplete">false</item>
        <!--图片列表底部背景色-->
        <item name="picture.bottom.bg">@color/color_fa</item>
        <!--图片列表预览文字颜色-->
        <item name="picture.preview.textColor">@color/tab_color_true</item>
        <!--图片列表已完成文字颜色-->
        <item name="picture.complete.textColor">@color/tab_color_true</item>
        <!--图片已选数量圆点背景色-->
        <item name="picture.num.style">@drawable/num_oval</item>
        <!--预览界面标题文字颜色-->
        <item name="picture.ac_preview.title.textColor">@color/white</item>
        <!--预览界面已完成文字颜色-->
        <item name="picture.ac_preview.complete.textColor">@color/tab_color_true</item>
        <!--预览界面标题栏背景色-->
        <item name="picture.ac_preview.title.bg">@color/bar_grey</item>
        <!--预览界面底部背景色-->
        <item name="picture.ac_preview.bottom.bg">@color/bar_grey_90</item>
        <!--预览界面返回箭头-->
        <item name="picture.preview.leftBack.icon">@drawable/picture_back</item>
        <!--是否改变预览界面状态栏字体颜色为黑色-->
        <item name="picture.preview.statusFontColor">false</item>
        <!--裁剪页面标题背景色-->
        <item name="picture.crop.toolbar.bg">@color/bar_grey</item>
        <!--裁剪页面状态栏颜色-->
        <item name="picture.crop.status.color">@color/bar_grey</item>
        <!--裁剪页面标题文字颜色-->
        <item name="picture.crop.title.color">@color/white</item>
        <!--相册文件夹列表选中图标-->
        <item name="picture.folder_checked_dot">@drawable/orange_oval</item>
    </style>

```

## 常用功能

******启动相册并拍照******       
```
 PictureSelector.create(MainActivity.this)
       .openGallery(PictureMimeType.ofImage())
       .forResult(PictureConfig.CHOOSE_REQUEST);
       
```
******单独启动拍照或视频 根据PictureMimeType自动识别******       
```
  PictureSelector.create(MainActivity.this)
       .openCamera(PictureMimeType.ofImage())
       .forResult(PictureConfig.CHOOSE_REQUEST);
```
******预览图片******       
```
// 预览图片 可自定长按保存路径
*注意 .themeStyle(themeId)；不可少，否则闪退...

PictureSelector.create(MainActivity.this).themeStyle(themeId).openExternalPreview(position, "/custom_file", selectList);
PictureSelector.create(MainActivity.this).themeStyle(themeId).openExternalPreview(position, selectList);

```
******预览视频****** 
```
PictureSelector.create(MainActivity.this).externalPictureVideo(video_path);

```
## 结果回调
```
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PictureConfig.CHOOSE_REQUEST:
                    // 图片、视频、音频选择结果回调
                    List<LocalMedia> selectList = PictureSelector.obtainMultipleResult(data);
                    // 例如 LocalMedia 里面返回三种path
                    // 1.media.getPath(); 为原图path
                    // 2.media.getCutPath();为裁剪后path，需判断media.isCut();是否为true  注意：音视频除外
                    // 3.media.getCompressPath();为压缩后path，需判断media.isCompressed();是否为true  注意：音视频除外
                    // 如果裁剪并压缩了，以取压缩路径为准，因为是先裁剪后压缩的
                    adapter.setList(selectList);
                    adapter.notifyDataSetChanged();
                    break;
            }
        }
    }
    
```

# 项目使用第三方库：
* PictureSelector 2.0
* glide:4.5.0	
* rxjava:2.0.5
* rxandroid:2.0.1
* PhotoView:2.1.3
* luban
* 裁剪使用ucrop

## 混淆配置 
```
#PictureSelector 2.0
-keep class com.luck.picture.lib.** { *; }

-dontwarn com.yalantis.ucrop**
-keep class com.yalantis.ucrop** { *; }
-keep interface com.yalantis.ucrop** { *; }
   
 #rxjava
-dontwarn sun.misc.**
-keepclassmembers class rx.internal.util.unsafe.*ArrayQueue*Field* {
 long producerIndex;
 long consumerIndex;
}
-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueProducerNodeRef {
 rx.internal.util.atomic.LinkedQueueNode producerNode;
}
-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueConsumerNodeRef {
 rx.internal.util.atomic.LinkedQueueNode consumerNode;
}

#rxandroid
-dontwarn sun.misc.**
-keepclassmembers class rx.internal.util.unsafe.*ArrayQueue*Field* {
   long producerIndex;
   long consumerIndex;
}
-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueProducerNodeRef {
    rx.internal.util.atomic.LinkedQueueNode producerNode;
}
-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueConsumerNodeRef {
    rx.internal.util.atomic.LinkedQueueNode consumerNode;
}

#glide
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public class * extends com.bumptech.glide.AppGlideModule
-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}

# for DexGuard only
-keepresourcexmlelements manifest/application/meta-data@value=GlideModule

```

## 兼容性测试
******腾讯优测-深度测试-通过率达到100%******

![image](https://github.com/LuckSiege/PictureSelector-Plus/blob/master/image/test.png)

## 演示效果

![image](https://github.com/LuckSiege/PictureSelector-Plus/blob/master/image/1.jpg)
![image](https://github.com/LuckSiege/PictureSelector-Plus/blob/master/image/2.jpg)
![image](https://github.com/LuckSiege/PictureSelector-Plus/blob/master/image/3.jpg)
![image](https://github.com/LuckSiege/PictureSelector-Plus/blob/master/image/4.jpg)
![image](https://github.com/LuckSiege/PictureSelector-Plus/blob/master/image/white.jpg)
![image](https://github.com/LuckSiege/PictureSelector-Plus/blob/master/image/blue.jpg)
![image](https://github.com/LuckSiege/PictureSelector-Plus/blob/master/image/11.jpg)
![image](https://github.com/LuckSiege/PictureSelector-Plus/blob/master/image/5.jpg)
![image](https://github.com/LuckSiege/PictureSelector-Plus/blob/master/image/6.jpg)
![image](https://github.com/LuckSiege/PictureSelector-Plus/blob/master/image/7.jpg)
![image](https://github.com/LuckSiege/PictureSelector-Plus/blob/master/image/8.jpg)
![image](https://github.com/LuckSiege/PictureSelector-Plus/blob/master/image/audio.jpg)
![image](https://github.com/LuckSiege/PictureSelector-Plus/blob/master/image/9.jpg)
![image](https://github.com/LuckSiege/PictureSelector-Plus/blob/master/image/10.jpg)
![image](https://github.com/pangli/PictureSelector-Plus/blob/fbd8ac1cdc14d0e3c1f1319bebf29a80d964fe56/image/13.jpg)


