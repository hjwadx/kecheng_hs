package fm.jihua.kecheng.ui.activity.course;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;
import com.weibo.sdk.android.sso.SsoHandler;

import fm.jihua.common.ui.helper.UIUtil;
import fm.jihua.kecheng.App;
import fm.jihua.kecheng_hs.R;
import fm.jihua.kecheng.interfaces.AuthHelper;
import fm.jihua.kecheng.interfaces.SNSCallback;
import fm.jihua.kecheng.rest.contract.DataCallback;
import fm.jihua.kecheng.rest.entities.Course;
import fm.jihua.kecheng.rest.entities.Rating;
import fm.jihua.kecheng.rest.entities.RatingResult;
import fm.jihua.kecheng.rest.entities.Tag;
import fm.jihua.kecheng.rest.entities.TagsResult;
import fm.jihua.kecheng.rest.service.DataAdapter;
import fm.jihua.kecheng.ui.activity.BaseActivity;
import fm.jihua.kecheng.ui.helper.Hint;
import fm.jihua.kecheng.utils.Const;
import fm.jihua.kecheng.utils.ImageHlp;
import fm.jihua.kecheng.utils.RenrenAuthHelper;
import fm.jihua.kecheng.utils.TencentAuthHelper;
import fm.jihua.kecheng.utils.WeiboAuthHelper;

public class RateActivity extends BaseActivity{

	RatingBar ratingBar;
	private DataAdapter mDataAdapter;
	LinearLayout tagParent;
	Tag[] tags;
	List<String> selectedTag = new ArrayList<String>();
	Course course;

	CheckBox renrenCheckBox;
	CheckBox weiboCheckBox;
	CheckBox tencentCheckBox;
	CheckBox tencentWeiboCheckBox;

	RenrenAuthHelper renrenAuthHelper;
	WeiboAuthHelper weiboAuthHelper;
	TencentAuthHelper tencentAuthHelper;
	TencentAuthHelper tencentWeiboAuthHelper;

	final int NO_NEED_UPLOAD = 0;
	final int READY_FOR_UPLOAD = 1;
	final int UPLOAD_SUCCESS = 2;
	final int UPLOAD_FAILED = 3;

	int renrenShareState;
	int weiboShareState;
	int tencentShareState;



	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		MobclickAgent.onEvent(this, "enter_course_rate_view");
		setContentView(R.layout.rate);
		setTitle("我的课程评价");
		initTitlebar();
		initView();
		setListeners();
		init();
	}

	void init() {
		renrenAuthHelper = new RenrenAuthHelper(this);
		tencentAuthHelper = new TencentAuthHelper(this);
		tencentWeiboAuthHelper = new TencentAuthHelper(this);
		weiboAuthHelper = new WeiboAuthHelper(this);
		renrenCheckBox.setChecked(renrenAuthHelper.isAuthed());
		weiboCheckBox.setChecked(weiboAuthHelper.isAuthed());
		tencentCheckBox.setChecked(tencentAuthHelper.isAuthed());
		tencentWeiboCheckBox.setChecked(tencentWeiboAuthHelper.isAuthed());
	}

	void checkForFinish() {

		if (renrenShareState != READY_FOR_UPLOAD
				&& weiboShareState != READY_FOR_UPLOAD && tencentShareState != READY_FOR_UPLOAD) {
			UIUtil.unblock(RateActivity.this);
		}
	}

	private void initView() {
		renrenCheckBox = (CheckBox) findViewById(R.id.renren);
		weiboCheckBox = (CheckBox) findViewById(R.id.weibo);
		tencentCheckBox = (CheckBox) findViewById(R.id.tencent);
		tencentWeiboCheckBox = (CheckBox) findViewById(R.id.tencentWeibo);
		tagParent = (LinearLayout) findViewById(R.id.tag_parent);
		ratingBar = (RatingBar) findViewById(R.id.ratingbar);
		mDataAdapter = new DataAdapter(this, new MyDataCallback());
		App app = (App)getApplication();
		Rating rating = app.getDBHelper().getRating(app.getUserDB(), course.id, app.getMyUserId());
		if (rating != null) {
			ratingBar.setRating(rating.score);
			selectedTag = new ArrayList<String>(rating.getTags());
		}
		mDataAdapter.getTags(true);
	}

	void setListeners() {
		renrenCheckBox.setOnCheckedChangeListener(checkedChangedListener);
		weiboCheckBox.setOnCheckedChangeListener(checkedChangedListener);
		tencentCheckBox.setOnCheckedChangeListener(checkedChangedListener);
		tencentWeiboCheckBox.setOnCheckedChangeListener(checkedChangedListener);
	}

	OnCheckedChangeListener checkedChangedListener = new OnCheckedChangeListener() {

		@Override
		public void onCheckedChanged(CompoundButton buttonView,
				boolean isChecked) {
			switch (buttonView.getId()) {
			case R.id.renren:
				if (isChecked) {
					if (!renrenAuthHelper.isAuthed()) {
						renrenCheckBox.setChecked(false);
						renrenAuthHelper.auth(new MySNSCallback(SNSCallback.AUTH));
					}
				}
				break;
			case R.id.weibo:
				if (isChecked) {
					if (!weiboAuthHelper.isAuthed()) {
						weiboCheckBox.setChecked(false);
						weiboAuthHelper.auth(new MySNSCallback(SNSCallback.AUTH));
					}
				}
				break;
			case R.id.tencent:
				if (isChecked) {
					if (!tencentAuthHelper.isAuthed()) {
						tencentCheckBox.setChecked(false);
						tencentAuthHelper.setCurrentType(Const.QQ);
						tencentAuthHelper.auth(new MySNSCallback(SNSCallback.AUTH));
					}
				}
				break;
			case R.id.tencentWeibo:
				if (isChecked) {
					if (!tencentWeiboAuthHelper.isAuthed()) {
						tencentWeiboCheckBox.setChecked(false);
						tencentWeiboAuthHelper.setCurrentType(Const.QQWeibo);
						tencentWeiboAuthHelper.auth(new MySNSCallback(SNSCallback.AUTH));
					}
				}
				break;
			default:
				break;
			}
		}
	};

	void initTitlebar(){
		course = (Course)getIntent().getSerializableExtra("course");
		getKechengActionBar().setLeftImage(R.drawable.menubar_btn_icon_cancel);
		getKechengActionBar().setRightImage(R.drawable.menubar_btn_icon_save);
		getKechengActionBar().getRightButton().setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				float score = ratingBar.getRating();
				App app = (App) getApplicationContext();
				int userId = app.getMyUserId();
				Rating rating = new Rating(course.id, score, userId, null,selectedTag.toArray(new String[selectedTag.size()]));

				UIUtil.block(RateActivity.this);
				mDataAdapter.Rate(rating);

				String content = "我在课程格子上给" + course.name + "打了分，快去看看吧。";
				if (renrenCheckBox.isChecked()) {
					renrenShareState = READY_FOR_UPLOAD;
					String renrenContent = content + "#课程格子#http://t.cn/zRJEpPW ";
					renrenAuthHelper.update(renrenContent, new MySNSCallback(SNSCallback.UPLOAD));
				}
				if (weiboCheckBox.isChecked()) {
					weiboShareState = READY_FOR_UPLOAD;
					String weiboContent = content + "#课程格子#http://t.cn/zRJEpPW ";
					weiboAuthHelper.update(weiboContent, new MySNSCallback(SNSCallback.UPLOAD));
				}
				if (tencentCheckBox.isChecked()) {
					tencentShareState = READY_FOR_UPLOAD;
					String tencentContent = content + "#课程格子#http://t.cn/zRJEpPW ";
					tencentAuthHelper.setCurrentType(Const.QQ);
					tencentAuthHelper.update(tencentContent, new MySNSCallback(SNSCallback.UPLOAD));
				}
				if (tencentWeiboCheckBox.isChecked()) {
					tencentShareState = READY_FOR_UPLOAD;
					String tencentContent = content + "#课程格子#http://t.cn/zRJEpPW ";
					tencentWeiboAuthHelper.setCurrentType(Const.QQWeibo);
					tencentWeiboAuthHelper.update(tencentContent, new MySNSCallback(SNSCallback.UPLOAD));
//					tencentAuthHelper.share("课程格子，打分游戏，该我们上场了！", null, tencentContent, "http://kechenggezi.com/rate_image.jpg");
				}
			}

		});
	}

	private void setTagViewDefault(TextView view){
		view.setTextColor(getResources().getColor(R.color.tag_text_unselected));
		view.setCompoundDrawablesWithIntrinsicBounds(R.drawable.rating_tag_icon_add, 0, 0, 0);
		view.setCompoundDrawablePadding(ImageHlp.changeToSystemUnitFromDP(this, 3));
		view.setBackgroundResource(R.drawable.rating_tag_bg_default);
		view.setGravity(Gravity.CENTER_VERTICAL);
		view.setShadowLayer(1, 0, 1, Color.WHITE);
	}

	private void setTagViewSelected(TextView view){
		view.setBackgroundResource(R.drawable.rating_tag_bg_selected);
		view.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.rating_tag_icon_delete, 0);
		view.setCompoundDrawablePadding(ImageHlp.changeToSystemUnitFromDP(this, 3));
		view.setShadowLayer(1, 0, 1, getResources().getColor(R.color.tag_text_shadow_selected));
		view.setTextColor(Color.WHITE);
		view.setGravity(Gravity.CENTER_VERTICAL);
	}

	private OnClickListener tagClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			String string = ((TextView)v).getText().toString();
			if(selectedTag == null){
				selectedTag = new ArrayList<String>();
			}
			if(selectedTag.contains(string)){
				selectedTag.remove(string);
				setTagViewDefault((TextView)v);
			} else {
				selectedTag.add(string);
				setTagViewSelected((TextView)v);
			}
		}
	};

	private View createTagView(String text){
		TextView view = new TextView(RateActivity.this);
		view.setText(text);
		view.setTextSize(13);
		view.setSingleLine();
		if(selectedTag.contains(text)){
			setTagViewSelected(view);
		}else {
			setTagViewDefault(view);
		}
		LinearLayout.LayoutParams mlp = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		mlp.setMargins(ImageHlp.changeToSystemUnitFromDP(this, 5), ImageHlp.changeToSystemUnitFromDP(this, 8),
				ImageHlp.changeToSystemUnitFromDP(this, 5), ImageHlp.changeToSystemUnitFromDP(this, 8));
		view.setLayoutParams(mlp);
		view.setOnClickListener(tagClickListener);

		int widthSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
		int heightSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
		view.measure(widthSpec, heightSpec);
		return view;
	}

	private void refreshTags(Tag[] tags) {
		tagParent.removeAllViews();
		int numTag = tags.length;
		LinearLayout linearLayout = new LinearLayout(RateActivity.this);
		int width = tagParent.getWidth();
		int childwidth = 0;
		for(int i = 0;i < numTag; i++){
//			Log.d(this.getClass().getSimpleName(), tags[i].name);
			View view = createTagView(tags[i].name);
			int w = view.getMeasuredWidth();
			childwidth += w + 2*ImageHlp.changeToSystemUnitFromDP(this, 5);
			if(childwidth > width){
				tagParent.addView(linearLayout);
				linearLayout = new LinearLayout(RateActivity.this);
				childwidth = w+ 2*ImageHlp.changeToSystemUnitFromDP(this, 5);
			}
			linearLayout.addView(view);
		}
		tagParent.addView(linearLayout);
		TextView view = new TextView(RateActivity.this);
		view.setText("点击添加或取消标签");
		view.setTextColor(getResources().getColor(R.color.textcolor_80));
		view.setTextSize(10);
		view.setPadding(10, 10, 10, 10);
		linearLayout = new LinearLayout(RateActivity.this);
		linearLayout.setGravity(Gravity.CENTER);
		linearLayout.addView(view);
		tagParent.addView(linearLayout);
	}

	private class MyDataCallback implements DataCallback {

		@Override
		public void callback(Message msg) {
			UIUtil.unblock(RateActivity.this);
			switch (msg.what) {
			case DataAdapter.MESSAGE_ADD_RATING:
				RatingResult ratingResult = (RatingResult) msg.obj;
				//需要对得到的数据进行保存什么的
				if(ratingResult != null && ratingResult.success){
					MobclickAgent.onEvent(RateActivity.this, "event_course_rate_succeed");
				}
				Intent intent = new Intent();
				intent.putExtra("ratingResult", ratingResult);
				setResult(RESULT_OK, intent);
				finish();
				break;
			case DataAdapter.MESSAGE_GET_TAGS:
				TagsResult result = (TagsResult)msg.obj;
				if (result != null) {
					if (result.tags == null || result.tags.length == 0) {
						UIUtil.block(getThis());
						mDataAdapter.getTags(false);
					}else {
						UIUtil.unblock(getThis());
						tags = result.tags;
						refreshTags(result.tags);
					}
				}else {
					Hint.showTipsShort(getThis(), R.string.act_error_get_tags);
				}
				break;
			default:
				break;
			}
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != Activity.RESULT_OK){
			return;
		}
		try {
			UIUtil.block(this);
			switch (requestCode) {
			case SsoHandler.DEFAULT_AUTH_ACTIVITY_CODE:
				weiboAuthHelper.onActivityResult(requestCode, resultCode, data);
				break;
			default:
				if(requestCode != SsoHandler.DEFAULT_AUTH_ACTIVITY_CODE){
					tencentAuthHelper.onActivityResult(requestCode, resultCode, data);
				}
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
			UIUtil.unblock(this);
		}
	}

	private class MySNSCallback implements SNSCallback{

		private int mScope = 0;

		public MySNSCallback(int scope) {
            mScope = scope;
        }

		@Override
		public void onComplete(final AuthHelper authHelper, Object data) {
            int helperCategory = authHelper.getType();
			switch (mScope) {
			case SNSCallback.AUTH:
				authHelper.bind(new MySNSCallback(SNSCallback.BIND));
				break;
			case SNSCallback.BIND:
				if (helperCategory == Const.RENREN) {
					Hint.showTipsShort(RateActivity.this, "人人绑定成功");
					renrenCheckBox.setChecked(true);
				} else if (helperCategory == Const.WEIBO) {
					Hint.showTipsShort(RateActivity.this, "新浪微博绑定成功");
					weiboCheckBox.setChecked(true);
				} else if (helperCategory == Const.QQ) {
					Hint.showTipsShort(RateActivity.this, "QQ空间绑定成功");
					tencentCheckBox.setChecked(true);
				} else if (helperCategory == Const.QQWeibo) {
					Hint.showTipsShort(RateActivity.this, "腾讯微博绑定成功");
					tencentWeiboCheckBox.setChecked(true);
				}
				break;
			case SNSCallback.UPLOAD:
				((App) getApplication()).setShared(true);
				if (helperCategory == Const.RENREN) {
					MobclickAgent.onEvent(RateActivity.this, "event_share_renren_succeed", "rate");
					renrenShareState = UPLOAD_SUCCESS;
					Hint.showTipsShort(RateActivity.this, "人人分享成功");
				} else if (helperCategory == Const.WEIBO) {
					MobclickAgent.onEvent(RateActivity.this, "event_share_weibo_succeed", "rate");
					weiboShareState = UPLOAD_SUCCESS;
					Hint.showTipsShort(RateActivity.this, "新浪微博分享成功");
				} else if (helperCategory == Const.QQ) {
					MobclickAgent.onEvent(RateActivity.this, "event_share_qq_succeed", "rate");
					tencentShareState = UPLOAD_SUCCESS;
					Hint.showTipsShort(RateActivity.this, "QQ空间分享成功");
				} else if (helperCategory == Const.QQWeibo) {
					MobclickAgent.onEvent(RateActivity.this, "event_share_qq_succeed", "rate");
					tencentShareState = UPLOAD_SUCCESS;
					Hint.showTipsShort(RateActivity.this, "腾讯微博分享成功");
				}
				checkForFinish();
				break;
			}
		}

		@Override
		public void onError(final AuthHelper authHelper) {
			int helperCategory = authHelper.getType();
			switch (mScope) {
			case SNSCallback.AUTH:
			case SNSCallback.BIND:
				if (helperCategory == Const.RENREN) {
					renrenCheckBox.setChecked(false);
				} else if (helperCategory == Const.WEIBO) {
					weiboCheckBox.setChecked(false);
				} else if (helperCategory == Const.QQ) {
					tencentCheckBox.setChecked(false);
				} else if (helperCategory == Const.QQWeibo) {
					tencentWeiboCheckBox.setChecked(false);
				}
				break;
			case SNSCallback.UPLOAD:
				if (helperCategory == Const.RENREN) {
					renrenShareState = UPLOAD_FAILED;
					Hint.showTipsShort(RateActivity.this, "人人分享失败");
				} else if (helperCategory == Const.WEIBO) {
					weiboShareState = UPLOAD_FAILED;
					Hint.showTipsShort(RateActivity.this, "新浪微博分享失败");
				} else if (helperCategory == Const.QQ) {
					tencentShareState = UPLOAD_FAILED;
					Hint.showTipsShort(RateActivity.this, "QQ空间分享失败");
				} else if (helperCategory == Const.QQ) {
					tencentShareState = UPLOAD_FAILED;
					Hint.showTipsShort(RateActivity.this, "腾讯微博分享失败");
				}
				checkForFinish();
				break;
			}
		}

		@Override
		public boolean getNeedAuthHelperProcessMessage() {
			return true;
		}
	}
}
