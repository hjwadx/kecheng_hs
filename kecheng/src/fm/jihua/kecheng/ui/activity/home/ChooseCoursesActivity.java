package fm.jihua.kecheng.ui.activity.home;

import java.util.Collections;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import fm.jihua.kecheng_hs.R;
import fm.jihua.kecheng.rest.entities.CourseBlock;
import fm.jihua.kecheng.ui.activity.BaseActivity;
import fm.jihua.kecheng.ui.activity.tutorial.ChooseCourseTutorialUtils;
import fm.jihua.kecheng.ui.adapter.ClassSelectAdapter;
import fm.jihua.kecheng.ui.widget.GalleryFlow;
import fm.jihua.kecheng.utils.ImageHlp;

public class ChooseCoursesActivity extends BaseActivity {
	
	public static final int CHOOSE_COURSES = 12345;
	int lastSelectedId = 0;
	public static final String INTENT_SHOW_TUTORIAL = "SHOW_TUTORIAL";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.class_select);
		@SuppressWarnings("unchecked")
		final List<CourseBlock> mCourses = (List<CourseBlock>) getIntent().getSerializableExtra("COURES");
		Collections.reverse(mCourses);

		ClassSelectAdapter adapter = new ClassSelectAdapter(this, mCourses);
		
//		findViewById(R.id.root).setBackgroundColor(Color.argb(0xff, 0x22, 0x22, 0x22));
		findViewById(R.id.root).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		final GalleryFlow galleryFlow = (GalleryFlow) findViewById(R.id.gallery_flow);
		galleryFlow.setAdapter(adapter);
		galleryFlow.setSpacing(ImageHlp.changeToSystemUnitFromDP(this, 1));
		galleryFlow.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				if(arg2 == lastSelectedId){
					Intent data = new Intent();
					data.putExtra("COURSE", mCourses.get(arg2));
					setResult(RESULT_OK, data);
					finish();
				}
			}
		});
		galleryFlow.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				lastSelectedId = position;
				
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				
			}
		});
		
//		galleryFlow.setSelection(mCourses.size() > 2 ? 1 : 0);
	}
	
	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		new ChooseCourseTutorialUtils(this);
	}
}
