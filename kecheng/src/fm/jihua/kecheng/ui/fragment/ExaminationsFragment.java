package fm.jihua.kecheng.ui.fragment;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.umeng.analytics.MobclickAgent;

import fm.jihua.common.utils.Compatibility;
import fm.jihua.kecheng.App;
import fm.jihua.kecheng_hs.R;
import fm.jihua.kecheng.rest.entities.Examination;
import fm.jihua.kecheng.ui.activity.BaseActivity;
import fm.jihua.kecheng.ui.activity.plugin.AddExaminationActivity;
import fm.jihua.kecheng.ui.adapter.ExaminatinAdapter;
import fm.jihua.kecheng.ui.helper.DialogUtils;
import fm.jihua.kecheng.utils.Const;

public class ExaminationsFragment extends BaseFragment {
	final List<Examination> examinations = new ArrayList<Examination>();
	long currentTime = System.currentTimeMillis();
	public static final int INTENT_EXCHANGE = 10001;
	ExaminatinAdapter adapter;
	ListView listView;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		MobclickAgent.onEvent(parent, "enter_exam_view");
		View view = null;
		view = inflater.inflate(R.layout.examination, container, false);
		initTitlebar();
		return view;
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		initList();
		ListView listView = (ListView) findViewById(R.id.list1);
		listView.post(new Runnable() {
			
			@Override
			public void run() {
				try {
					Intent intent = new Intent();
					intent.setClassName("fm.jihua.examination","fm.jihua.kecheng.ui.activity.export.ExchangeActivity");
					intent.putExtra("METHOD", "EXAMINATIONS");
					startActivityForResult(intent, INTENT_EXCHANGE);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	@Override
	public void onResume() {
		super.onResume();
		currentTime = System.currentTimeMillis();
		getExaminationsFromDatabase();
		adapter.notifyDataSetChanged();
	}
	
	void getExaminationsFromDatabase(){
		App app = (App)getApplication();
		examinations.clear();
		examinations.addAll(app.getDBHelper().getExaminations(app.getUserDB()));
		int index = examinations.size();
		for (int i=0; i< examinations.size(); i++) {
			Examination examination = examinations.get(i);
			if (examination.isExpired(currentTime)) {
				index = i;
				break;
			}
		}
		examinations.add(index, null);
	}
	
	void initList(){
		adapter = new ExaminatinAdapter(examinations);
		listView = (ListView) findViewById(R.id.list1);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new MyOnItemClickListener());
		listView.setOnItemLongClickListener(new MyOnItemLongClickListener());
		listView.setDivider(null);
	}
	
	class MyOnItemClickListener implements OnItemClickListener{

		@Override
		public void onItemClick(AdapterView<?> listView, View arg1, int position,
				long arg3) {
			try {
				Examination examination = examinations.get(position);
				if (examination != null && !examination.isExpired(currentTime)) {
					Intent intent = new Intent(parent, AddExaminationActivity.class);
					intent.putExtra("EXAMINATION", examination);
					startActivityForResult(intent, Const.INTENT_DEFAULT);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
//			Semester semester= (Semester) map.get("semester");
//			Intent intent = new Intent(parent, TotalCoursesActivity.class);
//			intent.putExtra("semester", semester);
//			startActivity(intent);
		}
	}
	
	class MyOnItemLongClickListener implements OnItemLongClickListener{

		@Override
		public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
				int position, long arg3) {
			Examination examination = examinations.get(position);
			if (examination != null) {
				showDeleteDialog(examination);
				return true;
			}
			return false;
		}
	}
	
	public void initTitlebar(){
		BaseActivity activity = (BaseActivity) getActivity();
		activity.setTitle("考试倒计时");
		activity.getKechengActionBar().setLeftImage(R.drawable.menubar_btn_icon_cancel);
		activity.getKechengActionBar().setRightText("分享");
		activity.getKechengActionBar().getRightTextButton().setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				ListView view = (ListView) parent.getLayoutInflater().inflate(R.layout.examination, null);
				view.setDivider(null);
				view.setAdapter(new ExaminatinAdapter(examinations, false));
				view.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT));
				int height = getListViewHeightBasedOnChildren(view);
				int widthMeasureSpec = MeasureSpec.makeMeasureSpec(Compatibility.getWidth(parent.getWindowManager().getDefaultDisplay()), MeasureSpec.EXACTLY);
				int heightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
				view.measure(widthMeasureSpec, heightMeasureSpec);
				view.layout(0, 0, view.getMeasuredWidth(), height);
				
				DialogUtils.showShareExaminationsDialog(parent, view, "期末急急逼近，考试滚滚而来。这是我悲催的考试安排，也来晒晒你的？");
			}
		});
	}
	
	public int getListViewHeightBasedOnChildren(ListView listView) {
		ListAdapter listAdapter = listView.getAdapter();
		if (listAdapter == null) {
			return 0;
		}

		int totalHeight = 0;
		for (int i = 0; i < listAdapter.getCount(); i++) {
			View listItem = listAdapter.getView(i, null, listView);int widthSpec = MeasureSpec.makeMeasureSpec(listView.getMeasuredWidth()-(listView.getPaddingLeft()+listView.getPaddingRight()), MeasureSpec.AT_MOST);
			int heightSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
			listItem.measure(widthSpec, heightSpec);
			totalHeight += listItem.getMeasuredHeight();
		}

		return totalHeight;
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != Activity.RESULT_OK) {
			return;
		}
		switch (requestCode) {
		case Const.INTENT_DEFAULT:
			initList();
			break;
		case INTENT_EXCHANGE:
			if (data != null) {
				@SuppressWarnings("unchecked")
				List<Examination> examinations = (List<Examination>) data.getSerializableExtra("DATA");
				long lastEditTime = data.getLongExtra("TIME", 0);
				App app = (App)getApplication();
				if (lastEditTime > app.getLastEditExaminationTime() && examinations != null) {
					app.getDBHelper().saveExaminations(app.getUserDB(), examinations);
					app.setLastEditExaminationTime(lastEditTime);
				}
			}
			break;
		default:
			break;
		}
	}
	
	private void showDeleteDialog(final Examination examination){
		new AlertDialog.Builder(parent)
		.setTitle("提示")
		.setMessage("是否确定删除")
		.setPositiveButton("删除",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialoginterface, int i) {
							App app = (App)parent.getApplication();
							app.getDBHelper().deleteExamination(app.getUserDB(), examination);
							examinations.remove(examination);
							adapter.notifyDataSetChanged();
					}
		})
		.setNegativeButton("取消",
				new DialogInterface.OnClickListener() {
					public void onClick(
							DialogInterface dialoginterface,
							int i) {
						// 按钮事件
						dialoginterface.dismiss();
					}
				}).show();
	}
}
