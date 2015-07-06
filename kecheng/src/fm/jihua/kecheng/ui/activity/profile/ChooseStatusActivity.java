package fm.jihua.kecheng.ui.activity.profile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import fm.jihua.kecheng_hs.R;
import fm.jihua.kecheng.ui.activity.BaseActivity;

public class ChooseStatusActivity extends BaseActivity{
	
	EditText searchBox;
	ListView searchListView;
	
	boolean firstLoad = true;
	String selectedStatus;
	List<String> myStatusList;
	
	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.profile_edit_list);
		myStatusList = (List<String>) getIntent().getSerializableExtra("STATUS");
		myStatusList = myStatusList == null ? new ArrayList<String>() : myStatusList;
		initTitlebar();
		findViews();
		initList();
	}
	
	private void initTitlebar() {
		setTitle(R.string.name_my_status);
		getKechengActionBar().setLeftImage(R.drawable.menubar_btn_icon_back);
		getKechengActionBar().getMenuButton().setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		getKechengActionBar().setRightImage(R.drawable.menubar_btn_icon_save);
		getKechengActionBar().getActionButton().setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.putExtra("STATUS",  new ArrayList<String>(myStatusList));
				setResult(RESULT_OK, intent);
				finish();
			}
		});
	}
	
	private void findViews(){
		searchBox = (EditText) findViewById(R.id.add_course_search);
		((View)searchBox.getParent()).setVisibility(View.GONE);
		
		searchListView = (ListView) findViewById(R.id.add_course_courses);
		// searchListView.setBackgroundResource(R.drawable.course_detail_whole_bg);
		findViewById(R.id.prompt).setVisibility(View.VISIBLE);
		((TextView)findViewById(R.id.prompt)).setText(R.string.write_my_status);
	}
	
	private void initList(){
		MyAdpter myAdpter = new MyAdpter(this, Arrays.asList(status));
		searchListView.setAdapter(myAdpter);
	}
	
	class MyOnItemClickListener implements OnItemClickListener{
		@Override
		public void onItemClick(AdapterView<?> listView, View arg1, int position,
				long arg3) {
			Object object = listView.getAdapter().getItem(position);
			if (object != null && object instanceof HashMap) {
				@SuppressWarnings("unchecked")
				HashMap<String, Object> year = (HashMap<String, Object>) object;
				Intent intent = new Intent();
				intent.putExtra("STATUS", (String) year.get("name"));
				setResult(RESULT_OK, intent);
				finish();
			}
		}
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != RESULT_OK){
			return;
		}
		try {
			data.putExtra("STATUS", selectedStatus);
			setResult(RESULT_OK, data);
			finish();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	String[] status = new String[] { "宅一枚", "社团活动", "学生会", "好好学习", "考研",
			"GRE/TOEFL/IELTS备考", "出国申请中", "找实习", "找工作", "实习中", "工作中", "无" };
	
	class MyAdpter extends BaseAdapter{
		
		List<String> mStatus;
		Context context;
		
		public MyAdpter(Context context, List<String> status) {
			this.context = context;
			setData(status);
		}
		
		public void setData(List<String> medals) {
			this.mStatus = medals;
			this.notifyDataSetChanged();
		}

		@Override
		public int getCount() {
			return mStatus.size();
		}

		@Override
		public Object getItem(int position) {
			return mStatus.get(position);
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			final ViewHolder holder;
			if (convertView == null) {
				convertView = LayoutInflater.from(parent.getContext()).inflate(
						R.layout.current_status_item, parent, false);
				holder = new ViewHolder();
				holder.name = (TextView) convertView.findViewById(R.id.name);
				holder.imageView = (ImageView) convertView.findViewById(R.id.imageview);
				convertView.setTag(holder);
			}else {
				holder = (ViewHolder) convertView.getTag();
			}
			try {
				
				final String status = mStatus.get(position);
				holder.name.setText(status);
				if (myStatusList.contains(status)) {
					holder.imageView.setVisibility(View.VISIBLE);
				} else {
					holder.imageView.setVisibility(View.GONE);
				}
                convertView.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						if (getString(R.string.null_string).equals(status)) {
							myStatusList.clear();
							myStatusList.add(status);
							notifyDataSetChanged();
							return;
						} else if (myStatusList.contains(getString(R.string.null_string))){
							myStatusList.clear();
							notifyDataSetChanged();
						}
						if (myStatusList.contains(status)){
							myStatusList.remove(status);
							holder.imageView.setVisibility(View.GONE);
						} else if(myStatusList.size() < 3) {
							myStatusList.add(status);
							holder.imageView.setVisibility(View.VISIBLE);
						}					
					}
				});
			} catch (Exception e) {
				e.printStackTrace();
			}

			return convertView;
		}
	}
	
	static class ViewHolder{
		TextView name;
		ImageView imageView;
	}
}
