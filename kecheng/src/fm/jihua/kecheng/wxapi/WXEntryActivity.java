package fm.jihua.kecheng.wxapi;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.tencent.mm.sdk.openapi.BaseReq;
import com.tencent.mm.sdk.openapi.BaseResp;
import com.tencent.mm.sdk.openapi.ConstantsAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.ShowMessageFromWX;
import com.tencent.mm.sdk.openapi.WXAppExtendObject;
import com.tencent.mm.sdk.openapi.WXMediaMessage;

import fm.jihua.common.utils.CommonUtils;
import fm.jihua.kecheng.App;
import fm.jihua.kecheng.rest.entities.Course;
import fm.jihua.kecheng.ui.activity.course.CourseActivity;
import fm.jihua.kecheng.ui.activity.home.MenuActivity;
import fm.jihua.kecheng.ui.activity.home.WeekActivity;
import fm.jihua.kecheng.utils.Const;

public class WXEntryActivity extends Activity  implements IWXAPIEventHandler{
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		App app = (App)getApplication();
		app.getWXAPI().handleIntent(getIntent(), this);
		finish();
	}
	
	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		
		setIntent(intent);
		App app = (App)getApplication();
		app.getWXAPI().handleIntent(intent, this);
	}

	// 微信发送请求到第三方应用时，会回调到该方法
	@Override
	public void onReq(BaseReq req) {
		switch (req.getType()) {
		case ConstantsAPI.COMMAND_GETMESSAGE_FROM_WX:{
//			goToGetMsg();
			Intent intent = new Intent(this, MenuActivity.class);
			startActivity(intent);
		}
			break;
		case ConstantsAPI.COMMAND_SHOWMESSAGE_FROM_WX:
			ShowMessageFromWX.Req showReq = (ShowMessageFromWX.Req) req;
			WXMediaMessage wxMsg = showReq.message;		
			WXAppExtendObject obj = (WXAppExtendObject) wxMsg.mediaObject;
			try {
				JSONObject json = new JSONObject(obj.extInfo);
				if (json != null && json.has("userId")) {
					Intent intent = new Intent(this, WeekActivity.class);
					intent.putExtra("USER_ID", json.get("userId").toString());
					startActivity(intent);
				}
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
			try { 
				JSONObject json = new JSONObject(obj.extInfo);
				if (json != null && json.has("courseId")) {
					int courseId = CommonUtils.parseInt(json.get("courseId").toString());
					App app = (App)getApplication();
					Course course = app.getDBHelper().getCourse(app.getUserDB(), courseId);
					if (course != null) {
						Intent intent = new Intent(this, CourseActivity.class);
						intent.putExtra(Const.BUNDLE_KEY_COURSE, course);
						startActivity(intent);
						break;
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			Intent intent = new Intent(this, MenuActivity.class);
			startActivity(intent);
//			goToShowMsg((ShowMessageFromWX.Req) req);
			break;
		default:
			break;
		}
//		Toast.makeText(this, "request", Toast.LENGTH_LONG).show();
	}

	// 第三方应用发送到微信的请求处理后的响应结果，会回调到该方法
	@Override
	public void onResp(BaseResp resp) {
//		int result = 0;
//		
//		switch (resp.errCode) {
//		case BaseResp.ErrCode.ERR_OK:
//			result = R.string.errcode_success;
//			break;
//		case BaseResp.ErrCode.ERR_USER_CANCEL:
//			result = R.string.errcode_cancel;
//			break;
//		case BaseResp.ErrCode.ERR_AUTH_DENIED:
//			result = R.string.errcode_deny;
//			break;
//		default:
//			result = R.string.errcode_unknown;
//			break;
//		}
//		
//		Toast.makeText(this, "response", Toast.LENGTH_LONG).show();
//		IWXAPI api = WXAPIFactory.createWXAPI(this, Const.WEIXIN_APP_ID, false);
//		api.h
	}

}
