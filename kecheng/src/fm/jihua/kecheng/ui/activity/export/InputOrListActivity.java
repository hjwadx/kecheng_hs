package fm.jihua.kecheng.ui.activity.export;

import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import fm.jihua.kecheng.App;
import fm.jihua.kecheng_hs.R;
import fm.jihua.kecheng.rest.entities.Course;
import fm.jihua.kecheng.ui.activity.BaseActivity;

/**
 * @date 2013-7-16
 * @introduce 输入或列表页面
 */
public class InputOrListActivity extends BaseActivity {

	public static final int CATEGORY_COURSEPICKER = 201;
	public static final String INTENT_KEY = "InputOrList";
	int category = -1;

	ListView mListView;
	InputOrListAdapter inputOrListAdapter;
	
	//------------------
	List<Course> courses;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_input);
		
		initIntent();
		initTitle();
		findViews();
		initView();
	}

	void initIntent() {
		category = getIntent().getIntExtra(INTENT_KEY, -1);
		if (-1 == category)
			finish();
	}

	void initTitle() {
		switch (category) {
		case CATEGORY_COURSEPICKER:
			setTitle("选择课程");
			break;

		default:
			break;
		}
	}

	void findViews() {
		mListView = (ListView) findViewById(R.id.input_listview);

		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				switch (category) {
				case CATEGORY_COURSEPICKER:
					Course course = null;
					if (position < courses.size()) {
						course = courses.get(position);
					}
					Intent intent = new Intent();
					intent.putExtra("COURSE", course);
					setResult(RESULT_OK, intent);
					finish();
					break;

				default:
					break;
				}
			}
		});
	}

	void initView() {
		switch (category) {
		case CATEGORY_COURSEPICKER:
			setCoursePicker();
			break;

		default:
			break;
		}
	}

	void setCoursePicker() {
		App app = (App) getApplication();
		courses = app.getDBHelper().getCourses(app.getUserDB());
		String[] courseNames = new String[courses.size() + 1];
		for (int i = 0; i < courses.size(); i++) {
			courseNames[i] = courses.get(i).name;
		}
		courseNames[courses.size()] = "其它课程";
		inputOrListAdapter = new InputOrListAdapter(this, courseNames);
		mListView.setAdapter(inputOrListAdapter);
	}
}
