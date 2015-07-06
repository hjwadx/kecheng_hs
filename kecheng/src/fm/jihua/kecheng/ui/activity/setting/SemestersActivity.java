package fm.jihua.kecheng.ui.activity.setting;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import fm.jihua.common.ui.helper.UIUtil;
import fm.jihua.kecheng.App;
import fm.jihua.kecheng.KechengAppWidgetProvider;
import fm.jihua.kecheng_hs.R;
import fm.jihua.kecheng.rest.contract.DataCallback;
import fm.jihua.kecheng.rest.entities.CoursesResult;
import fm.jihua.kecheng.rest.entities.RegistResult;
import fm.jihua.kecheng.rest.entities.Semester;
import fm.jihua.kecheng.rest.service.DataAdapter;
import fm.jihua.kecheng.ui.activity.BaseActivity;
import fm.jihua.kecheng.utils.ImageHlp;

public class SemestersActivity extends BaseActivity {
	
	DataAdapter mDataAdapter;
	final ArrayList<HashMap<String, Object>> mData = new ArrayList<HashMap<String,Object>>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_list);
		initList();
		setTitle("学期列表");
		initTitlebar();
		mDataAdapter = new DataAdapter(this, new MyDataCallback());
	}
	
	void initList(){
		App app = (App)getApplication();
		List<Semester> semesters = app.getDBHelper().getSemesters(app.getUserDB());
		mData.clear();
		for (int i = 0; i < semesters.size(); i++) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			String name = semesters.get(i).name;
			name = (name == null || name.length() == 0) ? "2012年秋季学期" : name;
			map.put("name", name);
			map.put("semester", semesters.get(i));
			mData.add(map);
		}
		SimpleAdapter adapter = new MySimpleAdapter(this, mData, R.layout.simple_list_item, new String[]{"name"}, new int[]{R.id.title});
//		SimpleAdapter adapter = new SimpleAdapter(this, array, R.layout.simple_list_item, new String[]{"name"}, new int[]{R.id.title});
		ListView listView = (ListView) findViewById(R.id.user_list);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new MyOnItemClickListener());
		listView.setDivider(null);
		
		int dimension = (int) getResources().getDimension(R.dimen.home_menu_left_padding);
		listView.setPadding(0, dimension, 0, dimension);
	}
	
	private class MySimpleAdapter extends SimpleAdapter {
		int localSemesterId;
		

		public MySimpleAdapter(Context context,
				List<? extends Map<String, ?>> data, int resource,
				String[] from, int[] to) {
			super(context, data, resource, from, to);
			App app = (App)getApplication();
			localSemesterId = app.getActiveSemesterId();
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			final int mPosition = position;
			Semester semester = (Semester) mData.get(mPosition).get("semester");
			convertView = super.getView(position, convertView, parent);
			final ImageView img = (ImageView) convertView.findViewById(R.id.select);
			if(localSemesterId == semester.id){
				img.setImageResource(R.drawable.check);
				img.setPadding(img.getPaddingLeft(), img.getPaddingTop(), 
						img.getPaddingRight()+ImageHlp.changeToSystemUnitFromDP(convertView.getContext(), 3), img.getPaddingBottom());
			}
			return convertView; 
		}
		
	}
	
	class MyOnItemClickListener implements OnItemClickListener{

		@Override
		public void onItemClick(AdapterView<?> listView, View arg1, final int position,
				long arg3) {
			final Semester semester = (Semester) mData.get(position).get("semester");
			final App app = (App)getApplication();
			String semesterName = semester.name;
			if (app.getActiveSemesterId() != semester.id) {
				new AlertDialog.Builder(SemestersActivity.this)
				.setTitle("提示")
				.setMessage("确定将"+semesterName+"设为当前学期？")
				.setPositiveButton("确定",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialoginterface,int i) {
								app.setActiveSemesterId(semester.id);									
								Map<String, Object> map = new HashMap<String, Object>();
								map.put("active_semester_id", semester.id);
								mDataAdapter.updateUser(map, null, null, null, 0);
								UIUtil.block(SemestersActivity.this);
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
	}
	
	void initTitlebar(){
		getKechengActionBar().getRightButton().setVisibility(View.GONE);
	}
	
	class MyDataCallback implements DataCallback{

		@Override
		public void callback(Message msg) {
			RegistResult result = (RegistResult) msg.obj;
			if(result != null && result.success) {
				new DataAdapter(SemestersActivity.this, new CoursesDataCallback()).getCourses(false);
			}
		}
	}
	
	class CoursesDataCallback implements DataCallback{

		@Override
		public void callback(Message msg) {
			UIUtil.unblock(SemestersActivity.this);
			CoursesResult result = (CoursesResult) msg.obj;
			if(result != null && result.finished) {
				sendBroadcast(new Intent(KechengAppWidgetProvider.UPDATE));
			}
			finish();
		}
	}
}
