package fm.jihua.kecheng.utils;

import android.graphics.Bitmap;

import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.SendMessageToWX;
import com.tencent.mm.sdk.openapi.WXAppExtendObject;
import com.tencent.mm.sdk.openapi.WXMediaMessage;
import com.tencent.mm.sdk.openapi.WXWebpageObject;

import fm.jihua.common.ui.helper.Util;
import fm.jihua.kecheng.rest.entities.Course;

public class AndroidSystem extends fm.jihua.common.utils.AndroidSystem{

	public static void shareCourseToWeixin(IWXAPI api, Course course){

		final WXAppExtendObject appdata = new WXAppExtendObject();
		appdata.extInfo = String.format("{courseId:%d}", course.id);
		// 用WXTextObject对象初始化一个WXMediaMessage对象
		WXMediaMessage msg = new WXMediaMessage();
		msg.mediaObject = appdata;
		// 发送文本类型的消息时，title字段不起作用
		// msg.title = "Will be ignored";
		msg.description = course.getCourseTimeString();
		msg.title = course.name;

		// 构造一个Req
		SendMessageToWX.Req req = new SendMessageToWX.Req();
		req.transaction = buildTransaction("text"); // transaction字段用于唯一标识一个请求
		req.message = msg;
		req.scene = SendMessageToWX.Req.WXSceneSession;

		// 调用api接口发送数据到微信
		api.sendReq(req);

	}

	public static void shareImageToWeixin(IWXAPI api, Bitmap bmp, String title, String description,int user_id){
		shareImageToWeixin(api, bmp, false, title, description, user_id);
	}

//	final WXAppExtendObject appdata = new WXAppExtendObject();
//	final String path = CameraUtil.getResultPhotoPath(this, data, SDCARD_ROOT + "/tencent/");
//	appdata.filePath = path;
//	appdata.extInfo = "this is ext info";
//
//	final WXMediaMessage msg = new WXMediaMessage();
//	msg.setThumbImage(Util.extractThumbNail(path, 150, 150, true));
//	msg.title = "this is title";
//	msg.description = "this is description";
//	msg.mediaObject = appdata;
//
//	SendMessageToWX.Req req = new SendMessageToWX.Req();
//	req.transaction = buildTransaction("appdata");
//	req.message = msg;
//	req.scene = isTimelineCb.isChecked() ? SendMessageToWX.Req.WXSceneTimeline : SendMessageToWX.Req.WXSceneSession;
//	api.sendReq(req);


	public static void shareImageToWeixin(IWXAPI api, Bitmap bmp, boolean timeLine, String title, String description,int user_id){
		WXMediaMessage msg;
		if (timeLine) {
			WXWebpageObject webpage = new WXWebpageObject();
			webpage.webpageUrl = "http://kechenggezi.com";
			msg = new WXMediaMessage(webpage);
			msg.title = title;
			msg.description = description;
			if(bmp != null) {
				Bitmap thumbBmp = Bitmap.createScaledBitmap(bmp, 80, 120, true);
				bmp.recycle();
				msg.thumbData = Util.bmpToByteArray(thumbBmp, true);
			}
		} else {
			final WXAppExtendObject appdata = new WXAppExtendObject();
			appdata.extInfo = String.format("{userId:%d}", user_id);
//			WXImageObject imgObj = new WXImageObject(bmp);
			msg = new WXMediaMessage();
			msg.mediaObject = appdata;
			msg.title = title;
			msg.description = description;
			if(bmp != null) {
				Bitmap thumbBmp = Bitmap.createScaledBitmap(bmp, 80, 120, true);
				bmp.recycle();
				msg.thumbData = Util.bmpToByteArray(thumbBmp, true);  // 设置缩略图
			}
		}
		SendMessageToWX.Req req = new SendMessageToWX.Req();
		req.transaction = buildTransaction("img");
		req.message = msg;
		req.scene = timeLine ? SendMessageToWX.Req.WXSceneTimeline : SendMessageToWX.Req.WXSceneSession;
		api.sendReq(req);
	}

	private static String buildTransaction(final String type) {
		return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
	}
}
