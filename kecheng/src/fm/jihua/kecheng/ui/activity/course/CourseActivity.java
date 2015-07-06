package fm.jihua.kecheng.ui.activity.course;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewStub;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;

import fm.jihua.common.ui.helper.UIUtil;
import fm.jihua.kecheng.App;
import fm.jihua.kecheng.KechengAppWidgetProvider;
import fm.jihua.kecheng_hs.R;
import fm.jihua.kecheng.rest.contract.DataCallback;
import fm.jihua.kecheng.rest.entities.BaseResult;
import fm.jihua.kecheng.rest.entities.Course;
import fm.jihua.kecheng.rest.entities.CourseDetailsResult;
import fm.jihua.kecheng.rest.entities.CourseResult;
import fm.jihua.kecheng.rest.entities.Examination;
import fm.jihua.kecheng.rest.entities.Note;
import fm.jihua.kecheng.rest.entities.Rating;
import fm.jihua.kecheng.rest.entities.RatingResult;
import fm.jihua.kecheng.rest.entities.User;
import fm.jihua.kecheng.rest.service.DataAdapter;
import fm.jihua.kecheng.ui.activity.BaseActivity;
import fm.jihua.kecheng.ui.adapter.NoteLoadMoreAdapter;
import fm.jihua.kecheng.ui.adapter.StudentGridAdapter;
import fm.jihua.kecheng.ui.helper.Hint;
import fm.jihua.kecheng.ui.widget.HorizontialListView;
import fm.jihua.kecheng.utils.AndroidSystem;
import fm.jihua.kecheng.utils.CommonUtils;
import fm.jihua.kecheng.utils.Const;
import fm.jihua.kecheng.utils.ImageHlp;

@SuppressLint("DefaultLocale")
public class CourseActivity extends BaseActivity{

	private int courseId;
	private Course course;
	private Examination examination;
	private boolean needRefresh;
	RatingBar ratingbar;
	LinearLayout tagParent;

	private DataAdapter mDataAdapter;
	boolean saved;
	int page = 1;
	
	StudentGridAdapter studentGridAdapter;
	NoteLoadMoreAdapter  notesAdapter;
	List<User> students = new ArrayList<User>();
	List<Note> mNotes = new ArrayList<Note>();
	
	ListView notesListView;
	
	public static final int REQUESTCODE_ADD_NOTE = 301;
	public static final int REQUESTCODE_EDIT_COURSE = 302;
	public static final int REQUESTCODE_RATE = 303;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(Const.TAG, "enter course");
		MobclickAgent.onEvent(this, "enter_course_view");
		try {
			course = (Course)getIntent().getExtras().get(Const.BUNDLE_KEY_COURSE);
			App app = (App)getApplication();
			courseId = course.id;
			examination = app.getDBHelper().getExamination(app.getUserDB(), courseId);
			setContentView(R.layout.course);
			findViews();
			mDataAdapter = new DataAdapter(this, new MyDataCallback());
			mDataAdapter.getCourseDetails(courseId,page,Const.DATA_COUNT_PER_REQUEST);
			studentGridAdapter = new StudentGridAdapter(CourseActivity.this, students);
			HorizontialListView gridView = (HorizontialListView) findViewById(R.id.gridStudents);
			gridView.setAdapter(studentGridAdapter);
			
			notesListView = (ListView) findViewById(R.id.listNotes);
			initNotesListView();
		} catch (Exception e) {
			e.printStackTrace();
			Log.e(Const.TAG, "Entry onCreate Exception:" + e.getMessage());
		}
	}
	
	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		((ScrollView)findViewById(R.id.main_view)).fullScroll(ScrollView.FOCUS_UP);
		((ScrollView)findViewById(R.id.main_view)).smoothScrollTo(0,0);
	}

	@Override
	protected void onStart() {
		super.onStart();
		initTitlebar();
		initViews();
		initRating();
		initCourseToolbar();
	}

	@Override
	public void finish() {
		Log.d(Const.TAG, "exit course");
		if (needRefresh) {
			setResult(RESULT_OK);
		}
		if (saved) {
			Intent data = new Intent();
			data.putExtra("SAVED", saved);
			setResult(RESULT_OK, data);
		}
		super.finish();
	}

	void findViews(){
		tagParent = (LinearLayout) findViewById(R.id.tag_parent);
		ratingbar = (RatingBar)findViewById(R.id.ratingbar);
	}
	
	void initNotesListView(){
		notesListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> listView, View arg1,
					int position, long id) {
				Note note = (Note) listView.getAdapter().getItem(
						position);
				App app = (App)getApplication();
				Intent intent = new Intent();
				intent.putExtra(Const.BUNDLE_KEY_NOTE, note);
				if (note.creator != null && note.creator.id == app.getMyUserId()) {
					intent.setClass(CourseActivity.this, NewNoteActivity.class);
					startActivityForResult(intent, REQUESTCODE_ADD_NOTE);
				}else {
					intent.setClass(CourseActivity.this, NoteActivity.class);
					startActivity(intent);
				}
			}
		});
		notesListView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> adapterView, View arg1,
					int position, long arg3) {
				ListView listView = (ListView)adapterView;
				int firstPosition = listView.getFirstVisiblePosition() - listView.getHeaderViewsCount(); // This is the same as child #0
				int wantedChild = position - firstPosition;
				View v = listView.getChildAt(wantedChild);
				if (v != null) {
					final Note note = (Note)listView
							.getAdapter().getItem(position);
					App app = (App)getApplication();
					if (note != null && note.creator.id == app.getMyUserId()) {
						showDeleteDialog(note);
						return true;
					}
				}
				return false;
			}
		});
	}

	void initRating(){
		App app = (App)getApplication();
		Rating rating = app.getDBHelper().getRating(app.getUserDB(), courseId, app.getMyUserId());
		if (rating == null) {
			((Button)findViewById(R.id.rate)).setText("评价");
		}else {
			((Button)findViewById(R.id.rate)).setText("编辑");
		}
		if (!app.getDBHelper().existCourse(app.getUserDB(), courseId)) {
			findViewById(R.id.rate).setVisibility(View.GONE);
		}else {
			findViewById(R.id.rate).setVisibility(View.VISIBLE);
		}
	}

	void initTitlebar(){
		setTitle(R.string.act_course_title);
		getKechengActionBar().getActionButton().setVisibility(View.VISIBLE);
		getKechengActionBar().setRightImage(R.drawable.activity_btn_icon_share);
		getKechengActionBar().getActionButton().setOnClickListener(clickListener);
		getKechengActionBar().getMenuButton().setOnClickListener(clickListener);
	}

	void initCourseToolbar(){
		App app = (App)getApplication();
		findViewById(R.id.add_note).setOnClickListener(clickListener);
		findViewById(R.id.edit_course).setOnClickListener(clickListener);
		findViewById(R.id.delete_course).setOnClickListener(clickListener);
		if (app.getDBHelper().existCourse(app.getUserDB(), courseId)) {
			if (findViewById(R.id.view_stub_add_course) == null) {
				findViewById(R.id.btn_add_course_parent).setVisibility(View.GONE);
			}
			refreshButtonVisibility(true);
		}else {
			refreshButtonVisibility(false);
			if (findViewById(R.id.view_stub_add_course) != null) {
				((ViewStub)findViewById(R.id.view_stub_add_course)).inflate();
			}else {
				findViewById(R.id.btn_add_course_parent).setVisibility(View.VISIBLE);
			}
			findViewById(R.id.btn_add_course).setOnClickListener(clickListener);
			if (course.school_name != null && course.school_name.length() > 0) {
				if (!course.school_name.equals(app.getMyself().school)) {
					findViewById(R.id.btn_add_course_parent).setVisibility(View.GONE);
				}
			}
		}
	}

	void initViews(){
		findViewById(R.id.rate).setOnClickListener(clickListener);
		setTitle(course.name);
		((TextView) findViewById(R.id.name))
		.setText(course.name);
		((TextView) findViewById(R.id.course_times)).setText(course.getUnitString(true));
//		((TextView) findViewById(R.id.times)).setText(course.getTimeString());
		String teacher = course.teacher;
		if (teacher == null || "".equals(teacher)) {
			teacher = "教师未知";
		}
		((TextView) findViewById(R.id.teacher)).setText(teacher);
		ListView list = (ListView) findViewById(R.id.listNotes);
		list.setDivider(null);
		final HorizontialListView gridView = (HorizontialListView) findViewById(R.id.gridStudents);
		gridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> grid, View arg1,
					int position, long arg3) {
				startUserActivity(position);
			}
		});
		ImageView imageView = (ImageView) findViewById(R.id.studentsImage);
		imageView.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				startUserActivity(0);
			}
		});
		initExamination();
	}

	void startUserActivity(int position) {
		MobclickAgent.onEvent(CourseActivity.this, "action_course_users_item");
		Intent intent = new Intent();
		intent.putExtra("students", (ArrayList<User>)students);
		intent.putExtra("course", course);
		intent.putExtra("position", position);
		intent.putExtra("getFriendsBy", Const.FRIENDS);
		intent.setClass(CourseActivity.this,
				UsersActivity.class);
		startActivity(intent);
	}

	void initExamination(){
		if (examination != null) {
			View view = findViewById(R.id.examination);
			SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy年MM月dd日", Locale.getDefault());
			((TextView) view.findViewById(R.id.date)).setText(sdf1.format(examination.time));
			SimpleDateFormat sdf2 = new SimpleDateFormat("a hh:mm", Locale.getDefault());
			((TextView) view.findViewById(R.id.time)).setText(sdf2.format(examination.time));
			((TextView) view.findViewById(R.id.room)).setText(TextUtils.isEmpty(examination.room) ? "教室未知" : examination.room);
			int day = 0;
			if (examination.time > System.currentTimeMillis()) {
				day = (int)(examination.time/3600/1000/24 - System.currentTimeMillis()/3600/1000/24);
			}
			((TextView) view.findViewById(R.id.remain)).setText(String.valueOf(day));
		}else {
			findViewById(R.id.examination_parent).setVisibility(View.GONE);
		}
	}

	private OnClickListener clickListener = new OnClickListener() {

		@SuppressWarnings("unchecked")
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.menu:{
				finish();
				break;
			}
			case R.id.action:{
				showShareDialog();
				break;
			}
			case R.id.add_note: {
				Intent intent = new Intent();
				intent.setClass(CourseActivity.this, NewNoteActivity.class);
				intent.putExtra("course_id", courseId);
				startActivityForResult(intent, REQUESTCODE_ADD_NOTE);
				overridePendingTransition(R.anim.slide_bottom_in, 0);
				break;
			}
			case R.id.edit_course:{
				MobclickAgent.onEvent(CourseActivity.this, "action_edit_course");
				Intent intent = new Intent(CourseActivity.this, NewCourseActivity.class);
				intent.putExtra("COURSE", course);
				intent.putExtra("IS_EDIT", true);
				startActivityForResult(intent, REQUESTCODE_EDIT_COURSE);
				overridePendingTransition(R.anim.slide_bottom_in, 0);
				break;
			}
			case R.id.gridStudents: {
				MobclickAgent.onEvent(CourseActivity.this, "action_course_users_item");
				Intent intent = new Intent();
				Object tag = v.getTag();
				if (tag != null) {
					intent.putExtra("students", (ArrayList<User>) tag);
					intent.putExtra("course", course);
					intent.putExtra("getFriendsBy", Const.FRIENDS);
					intent.setClass(CourseActivity.this, UsersActivity.class);
					startActivity(intent);
				}
			}
			case R.id.delete_course:
				showDeleteDialog(course);
				break;
			case R.id.btn_add_course:
				Course exist = getCourseByName(course.name);
				if(exist != null){
					Hint.showTipsShort(CourseActivity.this, "已存在同名课程");
				} else {
					UIUtil.block(CourseActivity.this);
					mDataAdapter.addCourse(courseId);
				}
				break;
			case R.id.rate:
				Intent intent = new Intent();
				intent.setClass(CourseActivity.this, RateActivity.class);
				intent.putExtra("course", course);
				startActivityForResult(intent, REQUESTCODE_RATE);
				overridePendingTransition(R.anim.slide_bottom_in, 0);
				break;
			default:
				break;
			}
		}
	};
	
	public Course getCourseByName(String name) {
		return App.getInstance().getDBHelper().getCourseByName(App.getInstance().getUserDB(), name);
	}

	private void showDeleteDialog(final Course course){
		new AlertDialog.Builder(CourseActivity.this)
		.setTitle("提示")
		.setMessage("是否删除" + course.name)
		.setPositiveButton("确定",
				new DialogInterface.OnClickListener() {
					public void onClick(
							DialogInterface dialoginterface,
							int i) {
						dialoginterface.dismiss();
						UIUtil.block(CourseActivity.this);
						mDataAdapter.deleteCourse(courseId);
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

	public void setListViewHeightBasedOnChildren(ListView listView) {
		ListAdapter listAdapter = listView.getAdapter();
		if (listAdapter == null) {
			return;
		}

		int totalHeight = 0;
		int count = listAdapter.getCount();
		for (int i = 0; i < count; i++) {
			View listItem = listAdapter.getView(i, null, listView);
			int widthSpec = MeasureSpec.makeMeasureSpec(listView.getMeasuredWidth()-(listView.getPaddingLeft()+listView.getPaddingRight()), MeasureSpec.AT_MOST);
			int heightSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
			listItem.measure(widthSpec, heightSpec);
			totalHeight += listItem.getMeasuredHeight();
		}

		ViewGroup.LayoutParams params = listView.getLayoutParams();
		params.height = totalHeight
				+ (listView.getDividerHeight() * (listAdapter.getCount() - 1));
		params.height += ImageHlp.changeToSystemUnitFromDP(this, 5);// if
																	// without
																	// this
																	// statement,the
																	// listview
																	// will be a
																	// little
																	// short
		listView.setLayoutParams(params);
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != RESULT_OK) {
			return;
		}
		try {
			switch (requestCode) {
			case REQUESTCODE_ADD_NOTE:
				Note note = (Note) data.getSerializableExtra("NOTE");
				boolean isDelete = data.getBooleanExtra("ISDELETE", false);
				if (note != null) {

					int index = CommonUtils.indexOfById(mNotes, note.id);
					if (!isDelete) {
						if (index != -1) {
							mNotes.remove(index);
						} else {
							index = 0;
						}
						mNotes.add(index, note);
					} else
						mNotes.remove(index);
					
					refreshNotes(mNotes);
				}

				break;
			case REQUESTCODE_EDIT_COURSE:
				if (data.getSerializableExtra("COURSE") != null) {
					course = (Course) data.getSerializableExtra("COURSE");
					courseId = course.id;
					initViews();
					needRefresh = true;
				}
				break;
			case REQUESTCODE_RATE:
				RatingResult ratingResult = (RatingResult) data.getSerializableExtra("ratingResult");
				if (ratingResult != null) {
					refreshRate(ratingResult);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			Log.e(Const.TAG,
					"Main onActivityResult Exception:" + e.getMessage());
		}
	}

	private void refreshStudents(List<User> students){
		this.students.clear();
		this.students.addAll(students);
		studentGridAdapter.notifyDataSetChanged();
		if(course.students_count != null){
			((TextView) findViewById(R.id.students_count)).setText("同学("
					+ course.students_count + ")");
		}
	}

	private void refreshRate(RatingResult ratingResult){
		findViewById(R.id.rating_parent).setVisibility(View.VISIBLE);
		findViewById(R.id.rating_empty).setVisibility(View.GONE);
		String score = String.format(Locale.getDefault(), "%.1f", ratingResult.score);
		((TextView)findViewById(R.id.score)).setText(score);
		if (ratingResult.num_ratings >= 10) {
			((TextView)findViewById(R.id.ratecount)).setText("("+ratingResult.num_ratings+"人评价)");
		} 
		final Rating rating = new Rating(0, ratingResult, 0, null);
		ratingbar.setRating(ratingResult.score);
		tagParent.post(new Runnable() {

			@Override
			public void run() {
				refreshTags(rating.getTags(), rating.getTagCount());
			}
		});
	}

	private void refreshTags(List<String> tags ,List<String> tagCounts) {
		tagParent.removeAllViews();
		if (tags.size() == 0) {
			return;
		}
		int numTag = tags.size();
		LinearLayout linearLayout = new LinearLayout(CourseActivity.this);
		int width = tagParent.getWidth();
		int childwidth = 0;
		for(int i = 0;i < (numTag < 3?numTag:3); i++){
			Log.d(this.getClass().getSimpleName(), tags.get(i));
			View view = createTagView(tags.get(i),tagCounts.get(i));
			int w = view.getMeasuredWidth();
			childwidth += w + 2*ImageHlp.changeToSystemUnitFromDP(this, 4);
			if(childwidth > width){
				tagParent.addView(linearLayout);
				linearLayout = new LinearLayout(CourseActivity.this);
				childwidth = w+ 2*ImageHlp.changeToSystemUnitFromDP(this, 4);
			}
			linearLayout.addView(view);
		}
		tagParent.addView(linearLayout);
	}

	private View createTagView(String text, String count){
		TextView view = new TextView(CourseActivity.this);
		view.setText(text + " ("+count+")");
		view.setTextSize(11);
		view.setSingleLine();

		setTagView(view);

		LinearLayout.LayoutParams mlp = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		mlp.setMargins(ImageHlp.changeToSystemUnitFromDP(this, 4), ImageHlp.changeToSystemUnitFromDP(this, 0),
				ImageHlp.changeToSystemUnitFromDP(this, 4), ImageHlp.changeToSystemUnitFromDP(this, 8));
		view.setLayoutParams(mlp);

		int widthSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
		int heightSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
		view.measure(widthSpec, heightSpec);
		return view;
	}

	private void setTagView(TextView view){
		view.setBackgroundResource(R.drawable.course_detail_rate_tag);
		view.setShadowLayer(1, 0, 1, getResources().getColor(R.color.tag_text_shadow_selected));
		view.setTextColor(Color.WHITE);
		view.setGravity(Gravity.CENTER_VERTICAL);
	}

	private void refreshNotes(List<Note> notes){

		

		if(this.mNotes != notes){
			this.mNotes.clear();
			this.mNotes.addAll(notes);
		}
		notesAdapter = new NoteLoadMoreAdapter(this, mNotes, notesListView);
		notesListView.setAdapter(notesAdapter);
//		notesAdapter.notifyDataSetChanged();
//		notesAdapter.onDataReady();
		notesListView.post(new Runnable() {

			@Override
			public void run() {
				setListViewHeightBasedOnChildren(notesListView);
			}
		});

		if (notes.size() == 0) {
			findViewById(R.id.notes_empty_hint).setVisibility(View.VISIBLE);
		}else {
			findViewById(R.id.notes_empty_hint).setVisibility(View.GONE);
		}
		UIUtil.unblock(CourseActivity.this);
	}

	private void refreshDetails(CourseDetailsResult result) {
		if (result.num_ratings > 0) {
			RatingResult ratingResult = new RatingResult();
			ratingResult.score = result.average_score;
			ratingResult.num_ratings = result.num_ratings;
			ratingResult.tags = result.tags;
			refreshRate(ratingResult);
		}
		List<Note> notes = new ArrayList<Note>(Arrays.asList(result.notes));
		refreshNotes(notes);
		refreshStudents(new ArrayList<User>(Arrays.asList(result.students)));
	}

	private void showDeleteDialog(final Note note){
		String noteContent = "";
		if (note.content.length() > 10) {
			noteContent = note.content.substring(0, 10) + "...";
		}else {
			noteContent = note.content;
		}
		new AlertDialog.Builder(this)
		.setTitle("提示")
		.setMessage("是否删除笔记：" + noteContent)
		.setPositiveButton("确定",
				new DialogInterface.OnClickListener() {
					public void onClick(
							DialogInterface dialoginterface,
							int i) {
						// 按钮事件
						int index = CommonUtils.indexOfById(mNotes, note.id);
						if (index != -1) {
							mNotes.remove(index);
						}
						refreshNotes(mNotes);
						mDataAdapter.removeNote(note.id);
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

	private class MyDataCallback implements DataCallback {

		@SuppressLint("NewApi")
		@SuppressWarnings("unchecked")
		@Override
		public void callback(Message msg) {
			switch (msg.what) {
			case DataAdapter.MESSAGE_GET_STUDENTS: {
				refreshStudents((List<User>) msg.obj);
			}
				break;
			case DataAdapter.MESSAGE_GET_NOTES:
				refreshNotes((List<Note>) msg.obj);
				break;
			case DataAdapter.MESSAGE_REMOVE_NOTE: {
				BaseResult result = (BaseResult) msg.obj;
				if (result == null || !result.success) {
					Hint.showTipsShort(CourseActivity.this, "删除笔记失败");
					mDataAdapter.getNotes(courseId);
				}
				break;
			}
			case DataAdapter.MESSAGE_DELETE_COURSE: {
				UIUtil.unblock(CourseActivity.this);
				BaseResult result = (BaseResult) msg.obj;
				if (result == null || !result.success) {
					Hint.showTipsShort(CourseActivity.this, "删除课程失败");
				}else {
					saved = false;
					needRefresh = true;
					sendBroadcast(new Intent(KechengAppWidgetProvider.UPDATE));
					finish();
				}
			}
				break;
			case DataAdapter.MESSAGE_ADD_COURSE:{
				UIUtil.unblock(CourseActivity.this);
				CourseResult result = (CourseResult) msg.obj;
				if (result != null) {
					if (result.success) {
						saved = true;
//						CommonUtils.playSound(CourseActivity.this, R.raw.add_course);
						findViewById(R.id.btn_add_course_parent).setVisibility(View.GONE);
						refreshButtonVisibility(true);
						initRating();
						sendBroadcast(new Intent(KechengAppWidgetProvider.UPDATE));
						Hint.showTipsShort(CourseActivity.this, "添加课程成功");
					}else {
						Hint.showTipsShort(CourseActivity.this, result.notice);
					}
				}else {
					Hint.showTipsShort(CourseActivity.this, "添加课程失败");
				}
				break;
			}
			case DataAdapter.MESSAGE_GET_RATING:
				RatingResult ratingResult = ((RatingResult)msg.obj);
				if(ratingResult != null && ratingResult.success == true && ratingResult.num_ratings > 0){
					refreshRate(ratingResult);
				}
				break;
			case DataAdapter.MESSAGE_GET_DETAILS:
				CourseDetailsResult result = (CourseDetailsResult) msg.obj;
				if (result != null && result.success == true) {
					refreshDetails(result);
				}
			default:
				break;
			}
		}
	}

	void refreshButtonVisibility(boolean isVisible) {
		if (isVisible) {
			findViewById(R.id.add_note).setVisibility(View.VISIBLE);
			findViewById(R.id.edit_course).setVisibility(View.VISIBLE);
			findViewById(R.id.delete_course).setVisibility(View.VISIBLE);
		} else {
			findViewById(R.id.add_note).setVisibility(View.GONE);
			findViewById(R.id.edit_course).setVisibility(View.GONE);
			findViewById(R.id.delete_course).setVisibility(View.GONE);
		}
	}

	void showShareDialog(){
		ListView list = new ListView(this);
		list.setBackgroundColor(Color.WHITE);
		final AlertDialog.Builder builder = new AlertDialog.Builder(this);

		android.content.DialogInterface.OnClickListener clickListener = new android.content.DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				String title = "我的课程——"+course.name;
				String smsText = "【"+course.name+"】教师: "+course.teacher+", "+course.getCourseTimeString()+"。课程格子获取地址：http://hs.kechenggezi.com/download?s=adms ";
				switch (which) {
				case 0:
					MobclickAgent.onEvent(CourseActivity.this, "event_share_weixin_succeed", "course");
					App app = (App)getApplication();
					AndroidSystem.shareCourseToWeixin(app.getWXAPI(), course);
					break;
				case 1:
					MobclickAgent.onEvent(CourseActivity.this, "event_share_mail_succeed", "course");
					String emailText = String.format("Hi，\n这是我上的一门课：%s，你有兴趣吗\n\n%s\n教师: %s\n%s\n\n==============================\n课程格子-中学生课程表\n获得地址：http://hs.kechenggezi.com/download?s=adyj\n更多信息请访问我们的官方网站：http://hs.kechenggezi.com/", course.name, course.name, course.teacher, course.getCourseTimeString());
					AndroidSystem.shareTextToEmail(CourseActivity.this, title, emailText);
					break;
				case 2:
					MobclickAgent.onEvent(CourseActivity.this, "event_share_sms_succeed", "course");
					AndroidSystem.shareTextToSMS(CourseActivity.this, smsText);
					break;
				case 3:
					MobclickAgent.onEvent(CourseActivity.this, "event_share_sys_succeed", "course");
					AndroidSystem.shareText(CourseActivity.this, title, smsText);
					break;
				default:
					break;
				}
			}
		};
		builder.setItems(new String[]{"分享给微信联系人", "邮件分享", "短信分享", "系统分享"}, clickListener);
		// 添加点击
        final AlertDialog alertDialogs = builder.create();
		alertDialogs.setCanceledOnTouchOutside(true);
		alertDialogs.setTitle("分享课程信息");
		alertDialogs.show();
	}
}
