package fm.jihua.kecheng.ui.fragment;

import java.io.File;

import uk.co.senab.photoview.PhotoViewAttacher;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.umeng.analytics.MobclickAgent;

import fm.jihua.common.ui.helper.Hint;
import fm.jihua.common.ui.helper.UIUtil;
import fm.jihua.kecheng.App;
import fm.jihua.kecheng_hs.R;
import fm.jihua.kecheng.rest.contract.DataCallback;
import fm.jihua.kecheng.rest.entities.CampusMap;
import fm.jihua.kecheng.rest.entities.CampusMapApplicants;
import fm.jihua.kecheng.rest.entities.User;
import fm.jihua.kecheng.rest.service.DataAdapter;
import fm.jihua.kecheng.rest.service.HandDrawnMapDownloadService;
import fm.jihua.kecheng.rest.service.HandDrawnMapDownloadService.MyIBinder;
import fm.jihua.kecheng.rest.service.HandDrawnMapDownloadService.OnProgressChangedListener;
import fm.jihua.kecheng.rest.service.RestService;
import fm.jihua.kecheng.ui.activity.BaseActivity;
import fm.jihua.kecheng.ui.helper.DialogUtils;
import fm.jihua.kecheng.ui.widget.CachedImageView;
import fm.jihua.kecheng.ui.widget.ShadowTextView;
import fm.jihua.kecheng.utils.FileUtils;

/**
 * @date 2013-7-20
 * @introduce 手绘地图
 */
public class HandDrawnMapFragment extends BaseFragment {

	PhotoViewAttacher mAttacher;
	BaseActivity activity;

	ImageView img_all;
	CachedImageView img_thumb;
	TextView tv_school, tv_mapsize,tv_hint;
	ProgressBar progressBar;
	ShadowTextView btn_download, btn_apply;
	LinearLayout layout_download, layout_apply;
	ScrollView scrollView;

	String URL;
	MyIBinder iBinder;
	ServiceConnection serviceConnection;

	DataAdapter dataAdapter;
	App app;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.act_handdrawn_map, null);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		initTitlebar();
		findViews(view);
		initView();
		initData();
	}

	public void initTitlebar() {
		BaseActivity activity = (BaseActivity) getActivity();
		activity.getKechengActionBar().setLeftImage(R.drawable.menubar_btn_icon_cancel);
		activity.setTitle("手绘地图");
	}
	
	private void addShareBtn(){
		((BaseActivity) getActivity()).getKechengActionBar().setRightText("分享");
		((BaseActivity) getActivity()).getKechengActionBar().getRightTextButton().setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				View view = DialogUtils.getViewByImageViewResource(img_all, getActivity());
				DialogUtils.showShareExaminationsDialogWithOutImageTitle(parent, view, getString(R.string.sharetext_handmap), "map");
			}
		});
	}
	

	void findViews(View view) {
		btn_download = (ShadowTextView) view.findViewById(R.id.btn_download);
		btn_apply = (ShadowTextView) view.findViewById(R.id.btn_applyfor);
		img_thumb = (CachedImageView) view.findViewById(R.id.handmap_imgthumb);
		progressBar = (ProgressBar) view.findViewById(R.id.handmap_progressBar);
		img_all = (ImageView) view.findViewById(R.id.handmap_image);
		layout_download = (LinearLayout) view.findViewById(R.id.handmap_download_layout);
		layout_apply = (LinearLayout) view.findViewById(R.id.handmap_apply_layout);
		scrollView = (ScrollView) view.findViewById(R.id.handmap_other_layout);

		tv_school = (TextView) view.findViewById(R.id.handmap_text_school);
		tv_mapsize = (TextView) view.findViewById(R.id.handmap_text_mapsize);
		tv_hint=(TextView) view.findViewById(R.id.handmap_text_hint);
	}

	void initView() {
		activity = (BaseActivity) getActivity();
		btn_download.setImageIcon(R.drawable.menu_icon_widget_download);
		btn_download.setClickable(true);
		findViewById(R.id.btn_download_child).setOnClickListener(onClickListener);
		findViewById(R.id.btn_applyfor_child).setOnClickListener(onClickListener);
		btn_apply.setImageIcon(-1);
	}

	void initData() {
		app = App.getInstance();
		dataAdapter = new DataAdapter(getActivity(), dataCallback);
		String userHandMapString = FileUtils.getInstance().getUserHandMapString((App)getActivity().getApplication());
		File file = new File(FileUtils.getInstance().getHandDrawnMapDownloadPath(getActivity(), userHandMapString));
		if (file.exists()) {
			showImage(FileUtils.getInstance().getCacheBitmap(getActivity(), userHandMapString));
		} else {
			MobclickAgent.onEvent(parent, "enter_map_none_view");
			bindService();
			String schoolName = app.getMyself().school;
			String lastSchool = app.getLastSchool();
			if(!schoolName.equals(lastSchool)){
				app.clearInfoByChangeSchool();
			}
			String campusMapInfo = app.getCampusMapInfo();
			if (TextUtils.isEmpty(campusMapInfo)) {
				UIUtil.block(getActivity());
			} else {
				CampusMap campusInfo = getCampusInfo(campusMapInfo);
				if (campusInfo != null) {
					String map_url = campusInfo.map_url;
					if (TextUtils.isEmpty(map_url)) {
						if (app.getCampusMapHaveApply()) {
							btn_apply.setText(getUseableString(campusInfo.applicants_count));
							btn_apply.setOnClickListener(null);
							findViewById(R.id.btn_applyfor_child).setOnClickListener(null);
							btn_apply.setBackgroundColor(getResources().getColor(R.color.textcolor_b2));
						}
					} else {
						layout_download.setVisibility(View.VISIBLE);
						layout_apply.setVisibility(View.GONE);
						initDownloadView(campusInfo);
					}
				}
			}
			dataAdapter.request(RestService.get().getCampusMapInfo(), DataAdapter.MESSAGE_GET_CAMPUSMAP_INFO);
		}
	}

	CampusMap getCampusInfo(String jsonString) {
		return new Gson().fromJson(jsonString, CampusMap.class);
	}

	String getCampusInfoString(CampusMap campusMap) {
		return new Gson().toJson(campusMap);
	}

	void initDownloadView(CampusMap campusMap) {
		User user = app.getMyself();
		tv_school.setText(user.school);
		tv_mapsize.setText(campusMap.size);
		img_thumb.setImageURI(Uri.parse(campusMap.thumb_url));
		this.URL = campusMap.map_url;
	}

	DataCallback dataCallback = new DataCallback() {

		@Override
		public void callback(Message msg) {
			UIUtil.unblock(getActivity());
			switch (msg.what) {
			case DataAdapter.MESSAGE_GET_CAMPUSMAP_INFO:
				CampusMap campusMap = (CampusMap) (msg.obj);
				if (campusMap == null) {
					Hint.showTipsShort(getActivity(), R.string.network_error_string);
				} else {
					if (campusMap.success) {
						layout_download.setVisibility(View.VISIBLE);
						layout_apply.setVisibility(View.GONE);
						initDownloadView(campusMap);
					}
					app.setCampusMapInfo(getCampusInfoString(campusMap));
					app.setLastSchool(app.getMyself().school);
				}
				break;
			case DataAdapter.MESSAGE_NOT_CARE:
				CampusMapApplicants campusMapApp = (CampusMapApplicants) (msg.obj);
				if (campusMapApp == null) {
					Hint.showTipsShort(getActivity(), R.string.network_error_string);
					btn_apply.setText(getApplication().getString(R.string.empty_handmap_apply_for_btn));
				} else {
					if (campusMapApp.success) {
						app.setCampusMapHaveApply();
						btn_apply.setOnClickListener(null);
						findViewById(R.id.btn_applyfor_child).setOnClickListener(null);
						// update class
						CampusMap campusMap_update = getCampusInfo(app.getCampusMapInfo());
						campusMap_update.applicants_count = campusMapApp.applicants;
						app.setCampusMapInfo(getCampusInfoString(campusMap_update));

						btn_apply.setText(getUseableString(campusMapApp.applicants));
						btn_apply.setBackgroundColor(getResources().getColor(R.color.textcolor_b2));
					} else {
						Hint.showTipsShort(getActivity(), R.string.apply_error_string);
						btn_apply.setText(getApplication().getString(R.string.empty_handmap_apply_for_btn));
					}
				}
				break;
			default:
				break;
			}
		}
	};

	OnClickListener onClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			if (v.getId() == R.id.btn_download_child) {
				MobclickAgent.onEvent(parent, "action_download_map");
				if (iBinder != null) {
					if (iBinder.getService().checkDownload()) {
						setDownLoadBtnStyle(STYLE_NORMAL);
						iBinder.getService().cancleAsync();
					} else {
						setDownLoadBtnStyle(STYLE_DOWNLOADING);
						Intent intent = new Intent(getActivity(), HandDrawnMapDownloadService.class);
						intent.putExtra(HandDrawnMapDownloadService.INTENT_URL_STRING, URL);
						getActivity().startService(intent);
					}
				}
			} else if (v.getId() == R.id.btn_applyfor_child) {
				UIUtil.block(getActivity());
				btn_apply.setText("申请中...");
				dataAdapter.request(RestService.get().getNeedCampusMap());
			}
		}
	};

	@Override
	public void onDestroy() {
		if (serviceConnection != null)
			getActivity().unbindService(serviceConnection);
		super.onDestroy();
	}

	void bindService() {
		Intent intent = new Intent(getActivity(), HandDrawnMapDownloadService.class);
		serviceConnection = new ServiceConnection() {

			@Override
			public void onServiceDisconnected(ComponentName name) {
			}

			@Override
			public void onServiceConnected(ComponentName name, IBinder service) {
				iBinder = (MyIBinder) service;
				HandDrawnMapDownloadService downloadService = iBinder.getService();
				if (downloadService.checkDownload()) {
					setDownLoadBtnStyle(STYLE_DOWNLOADING);
				}
				downloadService.setOnProgressChangedListener(changedListener);
			}
		};
		getActivity().bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
	}

	OnProgressChangedListener changedListener = new OnProgressChangedListener() {

		@Override
		public void onProgressChanged(int currentSize) {
			progressBar.setProgress(currentSize);
			progressBar.setVisibility(View.VISIBLE);
			tv_hint.setVisibility(View.GONE);
		}

		@Override
		public void onDownloadCompleted(Bitmap bitmap) {
			showImage(bitmap);
		}

		@Override
		public void onCancaled() {
			progressBar.setProgress(0);
			progressBar.setVisibility(View.GONE);
			tv_hint.setVisibility(View.VISIBLE);
		}

	};

	private void showImage(Bitmap bitmap) {
		MobclickAgent.onEvent(parent, "enter_map_view");
		if(bitmap!=null){
			scrollView.setVisibility(View.GONE);
			img_all.setVisibility(View.VISIBLE);
			img_all.setImageBitmap(bitmap);
			mAttacher = new PhotoViewAttacher(img_all);
			addShareBtn();
		}else{
			setDownLoadBtnStyle(STYLE_NORMAL);
		}
	}

	private String getUseableString(int number) {
		return "已申请,现已有" + number + "人申请";
	}
	
	final int STYLE_NORMAL = 1;
	final int STYLE_DOWNLOADING = 2;
	private void setDownLoadBtnStyle(int style){
		switch (style) {
		case STYLE_NORMAL:
			btn_download.setBackgroundResource(R.color.blue_selector_linearlayout_bg);
			btn_download.setText(getApplication().getString(R.string.download));
			btn_download.setImageIcon(R.drawable.menu_icon_widget_download);
			break;
		case STYLE_DOWNLOADING:
			btn_download.setBackgroundResource(R.color.red_selector_linearlayout_bg);
			btn_download.setText(getApplication().getString(R.string.download_cancle));
			btn_download.setImageIcon(R.drawable.activity_icon_cancel);
			break;

		default:
			break;
		}
	}

}
