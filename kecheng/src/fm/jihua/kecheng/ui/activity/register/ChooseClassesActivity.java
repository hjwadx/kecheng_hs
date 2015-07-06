package fm.jihua.kecheng.ui.activity.register;

import java.util.Arrays;
import java.util.regex.Pattern;

import kankan.wheel.widget.OnWheelChangedListener;
import kankan.wheel.widget.WheelView;
import kankan.wheel.widget.adapters.ArrayWheelAdapter;
import kankan.wheel.widget.adapters.NumericWheelAdapter;
import android.R.integer;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import fm.jihua.kecheng.ui.activity.BaseActivity;
import fm.jihua.kecheng_hs.R;

public class ChooseClassesActivity extends BaseActivity {
	EditText editView;
	TextView tv_prompt;
	
	View class_picker_parent;
	WheelView grade_picker_wheel;
	WheelView class_picker_wheel;
	WheelView prdfix_picker_wheel;

	String grade;
	String class_name;
	String prdfix;
	int class_number;

    String gradeArray[] = new String[] {"初一", "初二", "初三", "高一", "高二", "高三"};
    String prdfixArray[] = new String[] {"", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_choose_classes);
		findViews();
		initViewByType();
		initTitlebar();
		initTimePicker();
	}
	
	private void findViews(){
		editView = (EditText) findViewById(R.id.signature);
		tv_prompt = (TextView)findViewById(R.id.prompt);
		class_picker_parent = findViewById(R.id.classesPicker);
		class_picker_parent.setVisibility(View.VISIBLE);
		grade_picker_wheel = (WheelView) findViewById(R.id.grade_picker);
		class_picker_wheel = (WheelView) findViewById(R.id.class_picker);
		prdfix_picker_wheel = (WheelView) findViewById(R.id.prdfix_picker);
		MyOnWheelChangedListener listener = new MyOnWheelChangedListener();
		grade_picker_wheel.addChangingListener(listener);
		class_picker_wheel.addChangingListener(listener);
		prdfix_picker_wheel.addChangingListener(listener);
	}
	
	void initViewByType(){
		grade = getIntent().getStringExtra(RegisterFragment.INTENT_KEY_GRADE);
		class_name = getIntent().getStringExtra(RegisterFragment.INTENT_KEY_CLASSES);
		if(grade == null){
			grade = "初一";
		}
	    if(class_name == null){
	    	prdfix = "";
	    	class_number = 1;
	    	class_name = prdfix + class_number;
	    } else {
	    	if(isNumeric(class_name)){
		    	prdfix = "";
		    	class_number = Integer.valueOf(class_name);
	    	} else {
	    		prdfix = class_name.substring(0, 1);
	    		class_number = Integer.valueOf(class_name.substring(1, class_name.length()));
	    	}
	    }
		editView.setText(grade + class_name + "班");
		editView.setEnabled(false);
	}
	
	private void initTitlebar() {
		setTitle("班级");
		getKechengActionBar().setLeftImage(R.drawable.menubar_btn_icon_back);
		getKechengActionBar().setRightImage(R.drawable.menubar_btn_icon_save);
		getKechengActionBar().getActionButton().setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.putExtra(RegisterFragment.INTENT_KEY_CLASSES, class_name);
				intent.putExtra(RegisterFragment.INTENT_KEY_GRADE, grade);
				setResult(RESULT_OK, intent);
				finish();
			}
		});
	}
	
	public static boolean isNumeric(String str) {
		Pattern pattern = Pattern.compile("[0-9]*");
		return pattern.matcher(str).matches();
	}
	
	private void initTimePicker() {
        int cur_grade = 0;
        cur_grade = Arrays.asList(gradeArray).indexOf(grade);
        grade_picker_wheel.setViewAdapter(new GradeArrayAdapter(this, gradeArray, cur_grade));
        
        int cur_prdfix = 0;
        cur_prdfix = Arrays.asList(prdfixArray).indexOf(prdfix);
        prdfix_picker_wheel.setViewAdapter(new GradeArrayAdapter(this, prdfixArray, cur_prdfix));
        
        int cur_class = 0;
        cur_class = class_number - 1;
        class_picker_wheel.setViewAdapter(new ClassNumericAdapter(this, 1, 30, cur_class));

        grade_picker_wheel.setCurrentItem(cur_grade);
        prdfix_picker_wheel.setCurrentItem(cur_prdfix);
        class_picker_wheel.setCurrentItem(cur_class);
	}
	
	 private class ClassNumericAdapter extends NumericWheelAdapter {
	        // Index of current item
	        int currentItem;
	        // Index of item to be highlighted
	        int currentValue;
	        
	        /**
	         * Constructor
	         */
	        public ClassNumericAdapter(Context context, int minValue, int maxValue, int current) {
	            super(context, minValue, maxValue);
	            this.currentValue = current;
//	            setTextSize(16);
	        }
	        
	        @Override
	        protected void configureTextView(TextView view) {
	            super.configureTextView(view);
	            if (currentItem == currentValue) {
//	                view.setTextColor(0xFF0000F0);
	            }
	            view.setTypeface(Typeface.SANS_SERIF);
	        }
	        
	        @Override
	        public View getItem(int index, View cachedView, ViewGroup parent) {
	            currentItem = index;
	            return super.getItem(index, cachedView, parent);
	        }
	        
	        @Override
	        public CharSequence getItemText(int index) {
	        	CharSequence resultString = super.getItemText(index);
	            return resultString == null? null : resultString + "   班";
	        }
	    }
	
	private class GradeArrayAdapter extends ArrayWheelAdapter<String> {
        // Index of current item
        int currentItem;
        // Index of item to be highlighted
        int currentValue;
        
        /**
         * Constructor
         */
        public GradeArrayAdapter(Context context, String[] items, int current) {
            super(context, items);
            this.currentValue = current;
//            setTextSize(16);
        }
        
        @Override
        protected void configureTextView(TextView view) {
            super.configureTextView(view);
            if (currentItem == currentValue) {
//                view.setTextColor(0xFF0000F0);
            }
            view.setTypeface(Typeface.SANS_SERIF);
        }
        
        @Override
        public View getItem(int index, View cachedView, ViewGroup parent) {
            currentItem = index;
            return super.getItem(index, cachedView, parent);
        }
    }
	
	class MyOnWheelChangedListener implements OnWheelChangedListener {

		@Override
		public void onChanged(WheelView wheel, int oldValue, int newValue) {
			grade = gradeArray[grade_picker_wheel.getCurrentItem()];
			prdfix = prdfixArray[prdfix_picker_wheel.getCurrentItem()];
			class_number = class_picker_wheel.getCurrentItem() + 1;
			class_name = prdfix + class_number;
			editView.setText(grade + prdfix + class_number + "班");
		}
		
	}
}
