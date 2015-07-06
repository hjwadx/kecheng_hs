package fm.jihua.kecheng.ui.activity.common;

import java.util.ArrayList;

import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import fm.jihua.kecheng_hs.R;
import fm.jihua.kecheng.rest.entities.Avatar;
import fm.jihua.kecheng.ui.activity.BaseActivity;
import fm.jihua.kecheng.ui.adapter.ImageAdapter;
import fm.jihua.kecheng.ui.widget.DetialGallery;

public class ImageViewerActivity extends BaseActivity{
	
	DetialGallery gallery;
	ArrayList<Avatar> avatars = new ArrayList<Avatar>();
	
	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.imageviewer);
		avatars = (ArrayList<Avatar>) getIntent().getSerializableExtra("AVATARS");
		initTitlebar();
		findViews();
		initView();
	}
	
	private void initTitlebar() {
		actionbar.setTitle("");
		actionbar.setShowBackBtn(true);
		getKechengActionBar().setLeftImage(R.drawable.menubar_btn_icon_cancel);
	}

	private void initView() {
		ScaleAnimation animation = new ScaleAnimation(0.0f, 1f, 0.0f, 1f,   
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		animation.setDuration(500); // 设置持续时间2秒
		ImageAdapter imageAdapter = new ImageAdapter(this, avatars); 
		gallery.setAdapter(imageAdapter);
		gallery.setSelection(getIntent().getIntExtra("position", 0));
		gallery.startAnimation(animation);
		gallery.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				setTitle(arg2+1 + "/" + avatars.size());
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
			}
		});
	}

	private void findViews(){
		gallery = (DetialGallery) findViewById(R.id.gallery);
	}
}
