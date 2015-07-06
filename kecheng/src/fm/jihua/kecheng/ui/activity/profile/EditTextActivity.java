package fm.jihua.kecheng.ui.activity.profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;
import fm.jihua.kecheng_hs.R;
import fm.jihua.kecheng.ui.activity.BaseActivity;
import fm.jihua.kecheng.ui.helper.Hint;

public class EditTextActivity extends BaseActivity{
	

	EditText editView;
	TextView tv_prompt;
	
	int type;
	String titleString;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.eidt_signature);
		findViews();
		initViewByType();
		initTitlebar();
	}
	
	private void findViews(){
		editView = (EditText) findViewById(R.id.signature);
		tv_prompt = (TextView)findViewById(R.id.prompt);
	}
	
	void initViewByType(){
		
		type=getIntent().getIntExtra(ConstProfile.INTENT_TO_EDITTEXT_TYPE_KEY, ConstProfile.EDITTEXT_TYPE_PROFILE_NAME);
		
		switch (type) {
		case ConstProfile.EDITTEXT_TYPE_PROFILE_NAME:
			editView.setSingleLine();
			tv_prompt.setVisibility(View.VISIBLE);
			tv_prompt.setText(R.string.edittext_prompt_use_true_name);
			titleString = getString(R.string.titlestring_name);
			break;
		case ConstProfile.EDITTEXT_TYPE_PROFILE_SIGNATURE:
			titleString = getString(R.string.titlestring_signature);
			break;
		case ConstProfile.EDITTEXT_TYPE_PROFILE_DORMITORY:
			tv_prompt.setVisibility(View.VISIBLE);
			tv_prompt.setText(R.string.edittext_prompt_dormitory);
			titleString = getString(R.string.titlestring_dormitory);
			break;
		default:
			break;
		}
		
		editView.setText(getIntent().getStringExtra(ConstProfile.INTENT_TO_EDITTEXT_KEY));
	}
	
	private void initTitlebar() {
		setTitle(titleString);
		getKechengActionBar().setLeftImage(R.drawable.menubar_btn_icon_back);
		getKechengActionBar().setRightImage(R.drawable.menubar_btn_icon_save);
		getKechengActionBar().getActionButton().setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(type == ConstProfile.EDITTEXT_TYPE_PROFILE_DORMITORY && editView.getText().toString().length() > 15){
					Hint.showTipsShort(EditTextActivity.this, "宿舍的名字不能超过15个字符");
					return;
				} else if(type == ConstProfile.EDITTEXT_TYPE_PROFILE_NAME && editView.getText().toString().length() > 15){
					Hint.showTipsShort(EditTextActivity.this, "名字不能超过15个字符");
					return;
				}
				Intent intent = new Intent();
				intent.putExtra(ConstProfile.INTENT_TO_EDITTEXT_KEY, editView.getText().toString());
				setResult(RESULT_OK, intent);
				finish();
			}
		});
	}

}
