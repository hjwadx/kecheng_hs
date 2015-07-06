package fm.jihua.kecheng.ui.helper;

import java.io.File;
import java.util.Random;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;

import fm.jihua.common.utils.Compatibility;
import fm.jihua.kecheng.App;
import fm.jihua.kecheng_hs.R;
import fm.jihua.kecheng.ui.activity.common.ShareActivity;
import fm.jihua.kecheng.utils.AndroidSystem;
import fm.jihua.kecheng.utils.ImageHlp;

public class DialogUtils {
	public static void showShareWeekDialog(final Activity parent, final View weekView, String semesterName, LayoutParams layoutParams){
		Random random = new Random();
		showShareWeekDialog(parent, "", weekView, "我的课程表——分享自课程格子", parent.getResources().getStringArray(R.array.share_weekview)[random.nextInt(5)],
				"Hi，\n这是我在【课程格子】中生成的课程表 特别分享给你哦~给点建议吧 \n\n==============================\n课程格子-中学生课程表\n获得地址：http://hs.kechenggezi.com/download?s=azyj\n更多信息请访问我们的官方网站：http://hs.kechenggezi.com/\n", false, semesterName, "mycourse", layoutParams, true);
	}

	public static void showShareExaminationsDialog(final Activity parent, final View view, final String title){
		showShareWeekDialog(parent, "examinations", view, title+"——分享自课程格子", title + "@课程格子", "Hi，\n这是我在【课程格子】中生成的考试倒计时 特别分享给你哦~ \n\n==============================\n课程格子-中学生课程表\n获得地址：http://hs.kechenggezi.com/download?s=azyj\n更多信息请访问我们的官方网站：http://hs.kechenggezi.com/\n", true, null, "exam", null, true);
	}
	
	public static void showShareExaminationsDialogWithOutImageTitle(final Activity parent, final View view, final String title, String umeng_parameter){
		showShareWeekDialog(parent, "examinations", view, title+"——分享自课程格子", title + "@课程格子", "", true, null, umeng_parameter, null, false);
	}
	
	public static void showShareAppDialog(final Activity parent, final String title, final String content, final String umeng_parameter){
		final AlertDialog.Builder builder = new AlertDialog.Builder(parent);
		final int userId = App.getInstance().getMyUserId();
		android.content.DialogInterface.OnClickListener clickListener = new android.content.DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				App app = (App)parent.getApplication();
				app.setShared(true);
				switch (which) {
				case 0:
					Intent intent = new Intent(parent, ShareActivity.class);
					intent.putExtra("CONTENT", content);
					intent.putExtra("ONLYTEXT", true);
					intent.putExtra("UMENG_PARAMETER", umeng_parameter);
					(parent).startActivity(intent);
					break;
				case 1: {
					MobclickAgent.onEvent(parent, "event_share_weixin_succeed", umeng_parameter);
					String titleString = "";
					String description = "";
					titleString = parent.getResources().getString(R.string.share_title_normal);
					description = content;
					AndroidSystem.shareImageToWeixin(app.getWXAPI(), null, titleString, description,userId);
					break;
				}
				case 2:
					String titleString = "";
					String description = "";
					titleString = parent.getResources().getString(R.string.share_title_normal);
					description = content;
					MobclickAgent.onEvent(parent, "event_share_weixin_timeline_succeed", umeng_parameter);
					AndroidSystem.shareImageToWeixin(app.getWXAPI(), null, true, titleString, description,userId);
					break;
				case 3:
					MobclickAgent.onEvent(parent, "event_share_mail_succeed", umeng_parameter);
					String emailText = content;
					AndroidSystem.shareTextToEmail(parent, title, emailText);
					break;
				case 4:
					MobclickAgent.onEvent(parent, "event_share_sys_succeed", umeng_parameter);
					AndroidSystem.shareText(parent, title, content);
					break;
				default:
					break;
				}
			}
		};
		builder.setItems(new String[]{"新浪微博/人人/腾讯", "微信联系人", "微信朋友圈", "邮件分享", "系统分享"}, clickListener);
		// 添加点击
        final AlertDialog alertDialogs = builder.create();
		alertDialogs.setCanceledOnTouchOutside(true);
		alertDialogs.setTitle("分享方式");
		alertDialogs.show();
	}

	public static void showShareRankingDialog(final Activity parent, final View view, final String title, final String school, final int type){
		final String emailtitle = title + "——分享自课程格子";
		final String emailContent = "Hi，\n这是"+title+" 特别分享给你哦~ \n\n==============================\n课程格子-中学生课程表\n获得地址：http://kechenggezi.com/download?s=azyj\n更多信息请访问我们的官方网站：http://kechenggezi.com/\n";
		final AlertDialog.Builder builder = new AlertDialog.Builder(parent);
		final int userId = App.getInstance().getMyUserId();
		android.content.DialogInterface.OnClickListener clickListener = new android.content.DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				App app = (App)parent.getApplication();
				app.setShared(true);
				switch (which) {
				case 0:{
					Bitmap bmp = combineRanking(parent, view, title, school);
					ImageHlp.saveBitmap(bmp, App.getShareImageFilename());
					bmp.recycle();
					Intent intent = new Intent(parent, ShareActivity.class);
					intent.putExtra("CONTENT", title);
					(parent).startActivity(intent);
					break;
				}
				case 1: {
					String titleString = "";
					if (type == 1) {
						titleString = school + title;
					} else {
						titleString = "全国" + title;
					}
					Bitmap bmp = combineRanking(parent, view, school+title, school);
					AndroidSystem.shareImageToWeixin(app.getWXAPI(), bmp, titleString, "数据来自百万中学生投票，赶紧来评价！",userId);
					break;
				}
				case 2:{
					String titleString = "";
					if (type == 1) {
						titleString = school + title;
					} else {
						titleString = "全国" + title;
					}
					Bitmap bmp = combineRanking(parent, view, title, school);
					AndroidSystem.shareImageToWeixin(app.getWXAPI(), bmp, true, titleString, "数据来自百万中学生投票，赶紧来评价！",userId);
					break;
				}
				case 3:{
					Bitmap bmp = combineRanking(parent, view, title, school);
					ImageHlp.saveBitmap(bmp, App.getShareImageFilename());
					bmp.recycle();
					String emailText = emailContent;
					AndroidSystem.shareToEmail(parent, emailtitle, emailText, Uri.fromFile(new File(App.getShareImageFilename())));
					break;
				}
				case 4:{
					Bitmap bmp = combineRanking(parent, view, title, school);
					ImageHlp.saveBitmap(bmp, App.getShareImageFilename());
					bmp.recycle();
					AndroidSystem.shareImage(parent, emailtitle, Uri.fromFile(new File(App.getShareImageFilename())));
					break;
				}
				default:
					break;
				}
			}
		};
		builder.setItems(new String[]{"新浪微博/人人/腾讯", "微信联系人", "微信朋友圈", "邮件分享", "系统分享"}, clickListener);
		// 添加点击
        final AlertDialog alertDialogs = builder.create();
		alertDialogs.setCanceledOnTouchOutside(true);
		alertDialogs.setTitle("分享方式");
		alertDialogs.show();
	}

	public static void showShareWeekDialog(final Activity parent, final String tag, final View view, final String title, 
			final String content, final String emailContent, final boolean isShareExaminations, final String semesterName, 
			final String umeng_parameter, final LayoutParams layoutParams, final boolean withImageTitle){
		final AlertDialog.Builder builder = new AlertDialog.Builder(parent);
		final int userId = App.getInstance().getMyUserId();
		android.content.DialogInterface.OnClickListener clickListener = new android.content.DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				App app = (App)parent.getApplication();
				app.setShared(true);
				if (layoutParams != null) {
					int widthMeasureSpec = MeasureSpec.makeMeasureSpec(layoutParams.width,
							MeasureSpec.EXACTLY);
					int heightMeasureSpec = MeasureSpec.makeMeasureSpec(layoutParams.height,
							MeasureSpec.EXACTLY);
					view.measure(widthMeasureSpec, heightMeasureSpec);
					view.layout(0, 0, layoutParams.width, layoutParams.height);
					view.setLayoutParams(layoutParams);
				}
				switch (which) {
				case 0:
					saveToFile(parent, view, withImageTitle);
					Intent intent = new Intent(parent, ShareActivity.class);
					intent.putExtra("CONTENT", content);
					intent.putExtra("UMENG_PARAMETER", umeng_parameter);
					(parent).startActivity(intent);
					break;
				case 1: {
					MobclickAgent.onEvent(parent, "event_share_weixin_succeed", umeng_parameter);
					Bitmap bmp = combine(parent, view, withImageTitle);
					String titleString = "";
					String description = "";
					if (isShareExaminations) {
						titleString = "这是我近期的考试计划";
						description = "来自课程格子的考试倒计时";
					} else {
						titleString = "我的" + semesterName + "课表";
						description = "我刚在“课程格子”里做好了课表，供你参考哦！";
					}
					AndroidSystem.shareImageToWeixin(app.getWXAPI(), bmp, titleString, description,userId);
					break;
				}
				case 2:
					String titleString = "";
					String description = "";
					if (isShareExaminations) {
						titleString = "这是我近期的考试计划";
						description = "来自课程格子的考试倒计时";
					} else {
						titleString = "我的" + semesterName + "课表";
						description = "我刚在“课程格子”里做好了课表，供你参考哦！";
					}
					MobclickAgent.onEvent(parent, "event_share_weixin_timeline_succeed", umeng_parameter);
					Bitmap bmp = combine(parent, view, withImageTitle);
					AndroidSystem.shareImageToWeixin(app.getWXAPI(), bmp, true, titleString, description,userId);
					break;
				case 3:
					MobclickAgent.onEvent(parent, "event_share_mail_succeed", umeng_parameter);
					saveToFile(parent, view, withImageTitle);
					String emailText = emailContent;
					AndroidSystem.shareToEmail(parent, title, emailText, Uri.fromFile(new File(App.getShareImageFilename())));
					break;
				case 4:
					MobclickAgent.onEvent(parent, "event_share_sys_succeed", umeng_parameter);
					saveToFile(parent, view, withImageTitle);
					AndroidSystem.shareImage(parent, title, Uri.fromFile(new File(App.getShareImageFilename())));
					break;
				default:
					break;
				}
				LayoutParams oldLayoutParams = (LayoutParams) view.getTag();
				if (oldLayoutParams != null)
					view.setLayoutParams((LayoutParams) view.getTag());
			}
		};
		builder.setItems(new String[]{"新浪微博/人人/腾讯", "微信联系人", "微信朋友圈", "邮件分享", "系统分享"}, clickListener);
		builder.setOnCancelListener(new OnCancelListener() {
			
			@Override
			public void onCancel(DialogInterface dialog) {
//				LayoutParams layoutParams = (LayoutParams) view.getTag();
//				if (layoutParams != null)
//					view.setLayoutParams((LayoutParams) view.getTag());
			}
		});
		// 添加点击
        final AlertDialog alertDialogs = builder.create();
		alertDialogs.setCanceledOnTouchOutside(true);
		alertDialogs.setTitle("分享方式");
		alertDialogs.show();
	}

	public static void saveToFile(Activity parent, View weekView, boolean withImageTitle){
		Bitmap bmp = combine(parent, weekView, withImageTitle);
		ImageHlp.saveBitmap(bmp, App.getShareImageFilename());
		bmp.recycle();
	}

	private static Bitmap combine(Activity parent, View view, boolean withImageTitle){
		
		int bmpHeight = 0;
		BitmapDrawable drawable = null;
		if(withImageTitle){
			drawable = (BitmapDrawable) parent.getResources().getDrawable(R.drawable.classbox_share_title);
			Bitmap bmp = drawable.getBitmap();
			bmpHeight = bmp.getHeight() * view.getWidth()/bmp.getWidth();
		}
		Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight()+bmpHeight, Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);
		canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG|Paint.FILTER_BITMAP_FLAG));
//		Rect src = new Rect(0, 0, bmp.getWidth(), bmp.getHeight());
//		Rect dst = new Rect(0, 0, view.getWidth(), bmpHeight);

//		canvas.drawBitmap(bmp, src, dst, new Paint());
		canvas.translate(0, bmpHeight);
		App app = (App)parent.getApplicationContext();
		Bitmap bmpBg = app.getSpecialBgBitmap(view.getWidth(), view.getHeight());
		if (bmpBg != null) {
			canvas.drawBitmap(bmpBg, 0, 0, new Paint());
		}
		view.draw(canvas);
		canvas.translate(0, -bmpHeight);
		if(drawable != null){
			drawable.setBounds(0, 0, view.getWidth(), bmpHeight);
			drawable.draw(canvas);
		}
		return bitmap;
	}

	private static Bitmap combineRanking(Activity parent, View view, String tag, String school){
		View titleView = parent.getLayoutInflater().inflate(R.layout.share_rating_title, null);
		((TextView)titleView.findViewById(R.id.title)).setText(tag);
		((TextView)titleView.findViewById(R.id.school)).setText(school);
		int widthMeasureSpec = MeasureSpec.makeMeasureSpec(view.getWidth(), MeasureSpec.UNSPECIFIED);
		int heightMeasureSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
		titleView.measure(widthMeasureSpec, heightMeasureSpec);
		titleView.layout(0, 0, view.getWidth(), titleView.getMeasuredHeight());
		Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight()+titleView.getMeasuredHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);
		canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG|Paint.FILTER_BITMAP_FLAG));
//		Rect src = new Rect(0, 0, bmp.getWidth(), bmp.getHeight());
//		Rect dst = new Rect(0, 0, view.getWidth(), bmpHeight);

//		canvas.drawBitmap(bmp, src, dst, new Paint());
		canvas.translate(0, titleView.getMeasuredHeight());
		// App app = (App)parent.getApplicationContext();
		canvas.translate(0, -titleView.getMeasuredHeight());
		titleView.draw(canvas);
		return bitmap;
	}
	
	//从一个ImageView中获得Drawable，根据屏幕的尺寸大小去分享这张图
	public static View getViewByImageViewResource(ImageView imageView, Activity activity) {
		ImageView view = new ImageView(activity);
		Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
		view.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		int screenWidth = Compatibility.getWidth(activity.getWindowManager().getDefaultDisplay());
		int trulyHeight = (int) ((float) bitmap.getHeight() / bitmap.getWidth() * screenWidth);
		int widthMeasureSpec = MeasureSpec.makeMeasureSpec(screenWidth, MeasureSpec.EXACTLY);
		int heightMeasureSpec = MeasureSpec.makeMeasureSpec(trulyHeight, MeasureSpec.EXACTLY);
		view.measure(widthMeasureSpec, heightMeasureSpec);
		view.layout(0, 0, screenWidth, trulyHeight);
		view.setImageBitmap(bitmap);
		return view;
	}
}