package com.meiduohui.groupbuying.utils;

import android.app.Activity;

import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;


/**
 * 多图选择框架 PictureSelector的初始化配置
 * <p>
 * 作者： 周旭 on 2017年8月8日 0008.
 * 邮箱：374952705@qq.com
 * 博客：http://www.jianshu.com/u/56db5d78044d
 */

public class PictureSelectorConfig {

    /**
     * 初始化多图选择的配置
     *
     * @param activity
     * @param maxTotal
     */
    public static void initMultiConfig(Activity activity, int maxTotal) {
        // 进入相册 以下是例子：用不到的api可以不写
        PictureSelector.create(activity)
                .openGallery(PictureMimeType.ofImage())//全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()
                .maxSelectNum(maxTotal)// 最大图片选择数量 int
                .minSelectNum(0)// 最小选择数量 int
                .imageSpanCount(3)// 每行显示个数 int
                .selectionMode(PictureConfig.MULTIPLE)// 多选 or 单选 PictureConfig.MULTIPLE or PictureConfig.SINGLE
                .previewImage(true)// 是否可预览图片 true or false
                .isGif(false)// 是否显示gif图片 true or false
                .previewEggs(true)// 预览图片时 是否增强左右滑动图片体验(图片滑动一半即可看到上一张是否选中) true or false
                .previewVideo(false)// 是否可预览视频 true or false
                .enablePreviewAudio(false) // 是否可播放音频 true or false
                .isCamera(true)// 是否显示拍照按钮 true or false
//                .setOutputCameraPath("/CustomPath")// 自定义拍照保存路径,可不填
//                .isZoomAnim(true)// 图片列表点击 缩放效果 默认true
//                .sizeMultiplier(0.5f)// glide 加载图片大小 0~1之间 如设置 .glideOverride()无效
                .glideOverride(160, 160)// int glide 加载宽高，越小图片列表越流畅，但会影响列表图片浏览的清晰度
                .enableCrop(false)// 是否裁剪 true or false
                .compress(true)// 是否压缩 true or false
//                .cropCompressQuality()// 裁剪压缩质量 默认90 int
//                .selectionMedia(selectList)// 是否传入已选图片 List<LocalMedia> list
                .openClickSound(false)// 是否开启点击声音 true or false
                .forResult(PictureConfig.CHOOSE_REQUEST);//结果回调onActivityResult code


    }

    /**
     * 初始化视频选择的配置
     *
     * @param activity
     *
     */
    public static void initVideoConfig(Activity activity) {
        // 进入相册 以下是例子：用不到的api可以不写
        PictureSelector.create(activity)
                .openGallery(PictureMimeType.ofVideo())// 全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()、音频.ofAudio()
                .maxSelectNum(1)// 最大图片选择数量
                .minSelectNum(0)// 最小选择数量
                .imageSpanCount(3)// 每行显示个数
                .selectionMode(PictureConfig.SINGLE)// 多选 or 单选PictureConfig.MULTIPLE : PictureConfig.SINGLE
                .isCamera(true)// 是否显示拍照按钮
                .previewVideo(true)// 是否可预览视频 true or false
//                .setOutputCameraPath("/CustomPath")// 自定义拍照保存路径
                .videoQuality(1)// 视频录制质量 0 or 1 int
                .videoMaxSecond(15)// 显示多少秒以内的视频or音频也可适用 int
                .recordVideoSecond(15)//录制视频秒数 默认60s
                .forResult(PictureConfig.SINGLE);//结果回调onActivityResult code
    }

    /**
     * 初始化单张图片选择的配置(不裁剪)
     *
     * @param activity
     */
    public static void initEntirelySingleConfig(Activity activity) {
        // 进入相册 以下是例子：用不到的api可以不写
        PictureSelector.create(activity)
                .openGallery(PictureMimeType.ofImage())//全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()
                .maxSelectNum(1)// 最大图片选择数量 int
                .minSelectNum(0)// 最小选择数量 int
                .imageSpanCount(3)// 每行显示个数 int
                .selectionMode(PictureConfig.SINGLE)// 多选 or 单选 PictureConfig.MULTIPLE or PictureConfig.SINGLE
                .previewImage(true)// 是否可预览图片 true or false
                .isGif(false)// 是否显示gif图片 true or false
                .previewEggs(true)// 预览图片时 是否增强左右滑动图片体验(图片滑动一半即可看到上一张是否选中) true or false
                .previewVideo(false)// 是否可预览视频 true or false
                .enablePreviewAudio(false) // 是否可播放音频 true or false
                .isZoomAnim(true)// 图片列表点击 缩放效果 默认true
                .sizeMultiplier(0.5f)// glide 加载图片大小 0~1之间 如设置 .glideOverride()无效
                .glideOverride(130, 130)// int glide 加载宽高，越小图片列表越流畅，但会影响列表图片浏览的清晰度
                //                .setOutputCameraPath("/CustomPath")// 自定义拍照保存路径,可不填
                .hideBottomControls(true)// 是否显示uCrop工具栏，默认不显示 true or false
                .compress(false)// 是否压缩 true or false
                //                .cropCompressQuality()// 裁剪压缩质量 默认90 int
                //                .selectionMedia(selectList)// 是否传入已选图片 List<LocalMedia> list
                .openClickSound(false)// 是否开启点击声音 true or false
                .forResult(PictureConfig.CHOOSE_REQUEST);//结果回调onActivityResult code
    }

    /**
     * 初始化单张图片选择的配置
     *
     * @param activity
     */
    public static void initSingleConfig(Activity activity) {
        // 进入相册 以下是例子：用不到的api可以不写
        PictureSelector.create(activity)
                .openGallery(PictureMimeType.ofImage())//全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()
                .maxSelectNum(1)// 最大图片选择数量 int
                .minSelectNum(0)// 最小选择数量 int
                .imageSpanCount(3)// 每行显示个数 int
                .selectionMode(PictureConfig.SINGLE)// 多选 or 单选 PictureConfig.MULTIPLE or PictureConfig.SINGLE
                .previewImage(true)// 是否可预览图片 true or false
                .isGif(false)// 是否显示gif图片 true or false
                .previewEggs(true)// 预览图片时 是否增强左右滑动图片体验(图片滑动一半即可看到上一张是否选中) true or false
                .previewVideo(false)// 是否可预览视频 true or false
                .enablePreviewAudio(false) // 是否可播放音频 true or false
                .isZoomAnim(true)// 图片列表点击 缩放效果 默认true
                .sizeMultiplier(0.5f)// glide 加载图片大小 0~1之间 如设置 .glideOverride()无效
                .glideOverride(130, 130)// int glide 加载宽高，越小图片列表越流畅，但会影响列表图片浏览的清晰度
//                .setOutputCameraPath("/CustomPath")// 自定义拍照保存路径,可不填
                .hideBottomControls(false)// 是否显示uCrop工具栏，默认不显示 true or false
                .enableCrop(true)// 是否裁剪 true or false
                .circleDimmedLayer(false)// 是否圆形裁剪 true or false
                .showCropFrame(true)// 是否显示裁剪矩形边框 圆形裁剪时建议设为false   true or false
                .showCropGrid(false)// 是否显示裁剪矩形网格 圆形裁剪时建议设为false    true or false
                .freeStyleCropEnabled(true)// 裁剪框是否可拖拽 true or false
                .withAspectRatio(1,1)// int 裁剪比例 如16:9 3:2 3:4 1:1 可自定义
                .cropWH(200,200)// 裁剪宽高比，设置如果大于图片本身宽高则无效 int
                .rotateEnabled(true) // 裁剪是否可旋转图片 true or false
                .scaleEnabled(true)// 裁剪是否可放大缩小图片 true or false
                .compress(true)// 是否压缩 true or false
                .cropCompressQuality(50)// 裁剪压缩质量 默认90 int
//                .selectionMedia(selectList)// 是否传入已选图片 List<LocalMedia> list
                .openClickSound(false)// 是否开启点击声音 true or false

                .forResult(PictureConfig.CHOOSE_REQUEST);//结果回调onActivityResult code
    }

    /**
     * 初始化单张拍照选择的配置(不裁剪)
     *
     * @param activity
     */
    public static void initEntirelySingleCameraConfig(Activity activity) {
        //拍照
        PictureSelector.create(activity)
                .openCamera(PictureMimeType.ofImage())
                .hideBottomControls(true)// 是否显示uCrop工具栏，默认不显示 true or false
                .compress(false)// 是否压缩 true or false
//                .cropCompressQuality()// 裁剪压缩质量 默认90 int
                .openClickSound(false)// 是否开启点击声音 true or false
                .forResult(PictureConfig.CHOOSE_REQUEST);
    }

    /**
     * 初始化单张拍照选择的配置
     *
     * @param activity
     */
    public static void initSingleCameraConfig(Activity activity) {
        //拍照
        PictureSelector.create(activity)
                .openCamera(PictureMimeType.ofImage())
                .hideBottomControls(false)// 是否显示uCrop工具栏，默认不显示 true or false
                .enableCrop(true)// 是否裁剪 true or false
                .circleDimmedLayer(false)// 是否圆形裁剪 true or false
                .showCropFrame(true)// 是否显示裁剪矩形边框 圆形裁剪时建议设为false   true or false
                .showCropGrid(false)// 是否显示裁剪矩形网格 圆形裁剪时建议设为false    true or false
                .freeStyleCropEnabled(true)// 裁剪框是否可拖拽 true or false
                .withAspectRatio(1,1)// int 裁剪比例 如16:9 3:2 3:4 1:1 可自定义
                .cropWH(200,200)// 裁剪宽高比，设置如果大于图片本身宽高则无效 int
                .rotateEnabled(true) // 裁剪是否可旋转图片 true or false
                .scaleEnabled(true)// 裁剪是否可放大缩小图片 true or false
                .compress(true)// 是否压缩 true or false
//                .cropCompressQuality()// 裁剪压缩质量 默认90 int
                .openClickSound(false)// 是否开启点击声音 true or false
                .forResult(PictureConfig.CHOOSE_REQUEST);
    }

}
