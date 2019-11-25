package com.meiduohui.groupbuying.utils;

import android.content.Context;
import android.graphics.Bitmap;

import com.meiduohui.groupbuying.application.GlobalParameterApplication;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXImageObject;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject;
import com.tencent.mm.opensdk.openapi.IWXAPI;

import java.io.ByteArrayOutputStream;

public class WxShareUtils {

    /**
     * 分享网页类型至微信
     *
     * @param context 上下文
//     * @param appId   微信的appId
     * @param webUrl  网页的url
     * @param title   网页标题
     * @param content 网页描述
     * @param bitmap  位图
     */
//    public static void shareWeb(Context context, String appId, String webUrl, String title, String content, Bitmap bitmap) {
    public static void shareWeb(Context context, String webUrl, String title, String content, Bitmap bitmap) {
        // 通过appId得到IWXAPI这个对象
//        IWXAPI wxapi = WXAPIFactory.createWXAPI(context, appId);
        IWXAPI wxapi = GlobalParameterApplication.mWxApi;

        // 检查手机或者模拟器是否安装了微信
        if (!wxapi.isWXAppInstalled()) {
            ToastUtil.show(context,"您还没有安装微信");
            return;
        }

        // 初始化一个WXWebpageObject对象
        WXWebpageObject webpageObject = new WXWebpageObject();
        // 填写网页的url
        webpageObject.webpageUrl = webUrl;

        // 用WXWebpageObject对象初始化一个WXMediaMessage对象
        WXMediaMessage msg = new WXMediaMessage(webpageObject);
        // 填写网页标题、描述、位图
        msg.title = title;
        msg.description = content;
        // 如果没有位图，可以传null，会显示默认的图片
        msg.setThumbImage(bitmap);

        // 构造一个Req
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        // transaction用于唯一标识一个请求（可自定义）
        req.transaction = "webpage";
        // 上文的WXMediaMessage对象
        req.message = msg;
        // SendMessageToWX.Req.WXSceneSession是分享到好友会话
        // SendMessageToWX.Req.WXSceneTimeline是分享到朋友圈
        req.scene = SendMessageToWX.Req.WXSceneSession;

        // 向微信发送请求
        wxapi.sendReq(req);
    }

    private static final int THUMB_SIZE = 150;

    /**
     * 分享图片
     * @param bitmap    图片
     * @param shareType    0：分享到好友  1：分享到朋友圈
     */
    public static void sharePicture(Bitmap bitmap, int shareType) {

        IWXAPI wxapi = GlobalParameterApplication.mWxApi;

        WXImageObject imgObj = new WXImageObject(bitmap);

        WXMediaMessage msg = new WXMediaMessage();
        msg.mediaObject = imgObj;

        Bitmap thumbBitmap = Bitmap.createScaledBitmap(bitmap, THUMB_SIZE, THUMB_SIZE, true);
        bitmap.recycle();
        msg.thumbData = bmpToByteArray(thumbBitmap);  //设置缩略图

        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = buildTransaction("imgshareappdata");
        req.message = msg;
        req.scene = shareType;
        wxapi.sendReq(req);
    }

    public static byte[] bmpToByteArray(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }

    private static String buildTransaction(final String type) {
        return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
    }


}
