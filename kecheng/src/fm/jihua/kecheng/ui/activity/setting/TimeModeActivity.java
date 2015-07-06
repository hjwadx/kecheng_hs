package fm.jihua.kecheng.ui.activity.setting;

import java.util.List;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.umeng.analytics.MobclickAgent;

import fm.jihua.common.ui.helper.UIUtil;
import fm.jihua.common.ui.helper.WheelUtil;
import fm.jihua.kecheng.App;
import fm.jihua.kecheng_hs.R;
import fm.jihua.kecheng.rest.contract.DataCallback;
import fm.jihua.kecheng.rest.entities.BaseResult;
import fm.jihua.kecheng.rest.entities.EventsResult;
import fm.jihua.kecheng.rest.service.DataAdapter;
import fm.jihua.kecheng.ui.activity.BaseActivity;
import fm.jihua.kecheng.ui.adapter.TimeModeAdapter;
import fm.jihua.kecheng.utils.AlarmManagerUtil;
import fm.jihua.kecheng.utils.AppLogger;
import fm.jihua.kecheng.utils.Const;

/**
 * @date 2013-7-9
 * @introduce 时间模式页面
 */
public class TimeModeActivity extends BaseActivity {

	View timePicker;

	ListView listView;
	TimeModeAdapter timeModeAdapter;
	TimeModeParams timeModeParams;

	DataAdapter dataAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_time_mode);

		
		initTitle();
		findViews();
		initViews();
		
		//取消进入时间模式的状态，用来控制其他位置的Dialog
		App.getInstance().cancleFirstStatus(Const.FIRST_ENTER2TIMEMODE);
	}

	void initTitle() {
		setTitle(R.string.time_mode);
		View actionButton = getKechengActionBar().getActionButton();
		actionButton.setVisibility(View.VISIBLE);
		getKechengActionBar().setRightImage(R.drawable.menubar_btn_icon_save);
		actionButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (timeModeParams.checkListString()) {
					MobclickAgent.onEvent(TimeModeActivity.this, "event_set_time_succeed");
					App.getInstance().saveTimeModeList(timeModeParams.listString);
					AlarmManagerUtil.getInstance().resetAlarmByTimeMode();
					UIUtil.block(TimeModeActivity.this);
					dataAdapter.saveClassTime(App.getInstance().getTimeModeString());
				}
			}
		});
	}

	void findViews() {
		listView = (ListView) findViewById(R.id.time_mode_listview);
	}

	void initViews() {

		timeModeParams = new TimeModeParams(this);
		timeModeParams.initListItemData(App.getInstance().getTimeModeList());

		timeModeAdapter = new TimeModeAdapter(timeModeParams.listString, this);
		listView.setAdapter(timeModeAdapter);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				showTimePickerDialog(view, position);
			}
		});

		// init data if string is null
		if (App.getInstance().getTimeModeList().size() == 0) {
			App.getInstance().saveTimeModeList(timeModeParams.listString);
		}

		dataAdapter = new DataAdapter(this, callback);
	}

	private void showTimePickerDialog(View view, final int position) {
		try {
			timePicker = WheelUtil.getTimeWidenSpanPicker(this, timeModeParams.listString.get(position));
			DialogInterface.OnClickListener clickListener = new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					List<String> timeSpanWidenStringList = WheelUtil.getTimeSpanWidenStringList(timePicker);
					if (position == 0) {
						timeModeParams.initBaseInfoByFirstSetting(timeSpanWidenStringList);
						timeModeAdapter.setListString(timeModeParams.changeListItemData(true));
						timeModeAdapter.notifyDataSetChanged();
					} else {
						timeModeParams.setWheelUtilString(timeSpanWidenStringList, position);
						timeModeAdapter.notifyDataSetChanged();
					}
					dialog.dismiss();
				}
			};
			new AlertDialog.Builder(TimeModeActivity.this).setTitle(R.string.choise_start_end_time).setView(timePicker).setPositiveButton(R.string.certain, clickListener)
					.setNegativeButton(R.string.cancle, null).show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	DataCallback callback = new DataCallback() {

		@Override
		public void callback(Message msg) {
			UIUtil.unblock(TimeModeActivity.this);
			switch (msg.what) {
			case DataAdapter.MESSAGE_POST_USER_CLASSTIME:
				BaseResult baseResult = (BaseResult) msg.obj;
				if (baseResult != null && baseResult.success) {
					AppLogger.i("success:  " + baseResult.success);
					UIUtil.block(TimeModeActivity.this);
					dataAdapter.getJoinedEvents();
				} else {
					finish();
				}
				break;
			case DataAdapter.MESSAGE_GET_JOINED_EVENT:
				EventsResult result = (EventsResult) msg.obj;
				finish();
				break;
			}
		}
	};
}
