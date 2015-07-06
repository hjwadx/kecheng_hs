package fm.jihua.kecheng.ui.activity.setting;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import kankan.wheel.widget.OnWheelChangedListener;
import kankan.wheel.widget.WheelView;
import kankan.wheel.widget.adapters.ArrayWheelAdapter;
import kankan.wheel.widget.adapters.NumericWheelAdapter;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import fm.jihua.kecheng.App;
import fm.jihua.kecheng_hs.R;
import fm.jihua.kecheng.rest.entities.Semester;
import fm.jihua.kecheng.ui.activity.BaseActivity;
import fm.jihua.kecheng.ui.helper.Hint;

public class SemesterActivity extends BaseActivity {
	View timerPickerView;
	Builder timerPickerDialog;
	
	EditText nameEditText;
	EditText startDateEditText;
	EditText endDateEditText;
//	ImageView saveButton;
	Semester semester;
	SimpleDateFormat dateFormat;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.semester);
		findViews();
		setListeners();
		setTitle("设置学期");
//		saveButton.setText("保存");
		initViews();
		initTitlebar();
	}
	
	void findViews(){
		nameEditText = (EditText) findViewById(R.id.name);
		startDateEditText = (EditText) findViewById(R.id.start_date);
		endDateEditText = (EditText) findViewById(R.id.end_date);
	}
	
	void setListeners(){
		startDateEditText.setOnClickListener(listener);
		endDateEditText.setOnClickListener(listener);
//		saveButton.setOnClickListener(listener);
	}
	
	void initViews(){
		App app = (App)getApplication();
		semester = app.getDBHelper().getActiveSemester(app.getUserDB());
		String pattern="yyyy-MM-dd";  
		dateFormat=new SimpleDateFormat(pattern);
		if (semester != null) {
			nameEditText.setText(semester.name);  
			startDateEditText.setText(dateFormat.format(new Date(semester.begin_time*1000)));
			endDateEditText.setText(dateFormat.format(new Date(semester.end_time*1000)));
		}else {
			semester = new Semester();
		}
	}
	
	View.OnClickListener listener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.action:
				if (saveSemester()) {
					Hint.showTipsShort(SemesterActivity.this, "学期保存成功");
				}else {
					Hint.showTipsLong(SemesterActivity.this, "学期保存失败，是不是时间填错了？");
				}
				break;
			case R.id.start_date:
			case R.id.end_date:
				showTimePicker((EditText) v);
				break;

			default:
				break;
			}
			
		}
	};
	
	boolean saveSemester(){
		boolean result = false;
		if (startDateEditText.getText() != null && endDateEditText.getText() != null) {
			try {
				long begin_time = dateFormat.parse(startDateEditText.getText().toString()).getTime()/1000;
				long end_time = dateFormat.parse(endDateEditText.getText().toString()).getTime()/1000;
				if (semester.end_time > semester.begin_time && (begin_time != semester.begin_time || end_time != semester.end_time)) {
					semester.modified = true;
					semester.begin_time = begin_time;
					semester.end_time = end_time;
					App app = (App)getApplication();
					app.getDBHelper().saveSemester(app.getUserDB(), semester);
					result = true;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return result;
	}

	void showTimePicker(final EditText editText){
		try {
			timerPickerView = getLayoutInflater().inflate(R.layout.date_picker, null);
			String value = editText.getText().toString();
			Date dt = (value == null || value.length() == 0) ? new Date() : dateFormat.parse(value);
			initTimePicker(timerPickerView, dt);
			new AlertDialog.Builder(this).setTitle("选择时间").setView(timerPickerView)
			.setPositiveButton("确定",
					new DialogInterface.OnClickListener() {
						public void onClick(
								DialogInterface dialoginterface,
								int i) {
							editText.setText(getTimeString());
							dialoginterface.cancel();
						}
					})
			.setNegativeButton("取消",
					new DialogInterface.OnClickListener() {
						public void onClick(
								DialogInterface dialoginterface,
								int i) {
							dialoginterface.cancel();
						}
					}).show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	String getTimeString(){
		if (timerPickerView != null) {
			final WheelView month = (WheelView) timerPickerView.findViewById(R.id.month_picker);
	        final WheelView year = (WheelView) timerPickerView.findViewById(R.id.year_picker);
	        final WheelView day = (WheelView) timerPickerView.findViewById(R.id.day_picker);
	        Calendar calendar = Calendar.getInstance();
	        calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR)-1+year.getCurrentItem());
	        calendar.set(Calendar.MONTH, month.getCurrentItem());
	        calendar.set(Calendar.DATE, day.getCurrentItem()+1);
	        return dateFormat.format(calendar.getTime());
		}
		return "";
	}
	
	private void initTimePicker(View view, Date dt){
		
		final WheelView month = (WheelView) view.findViewById(R.id.month_picker);
        final WheelView year = (WheelView) view.findViewById(R.id.year_picker);
        final WheelView day = (WheelView) view.findViewById(R.id.day_picker);
        
        OnWheelChangedListener listener = new OnWheelChangedListener() {
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                updateDays(year, month, day);
            }
        };
        
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dt);
        int curMonth = calendar.get(Calendar.MONTH);
        String months[] = new String[] {"一月", "二月", "三月", "四月", "五月", "六月",
        		"七月", "八月", "九月", "十月", "十一月", "十二月"};
        month.setViewAdapter(new DateArrayAdapter(this, months, curMonth));
        month.setCurrentItem(curMonth);
        month.addChangingListener(listener);
    
        // year
        int curYear = calendar.get(Calendar.YEAR);
        year.setViewAdapter(new DateNumericAdapter(this, curYear - 1, curYear + 1, 1));
        year.setCurrentItem(1);
        year.addChangingListener(listener);
        
        //day
        updateDays(year, month, day);
        day.setCurrentItem(calendar.get(Calendar.DAY_OF_MONTH) - 1);
	}
	
	void updateDays(WheelView year, WheelView month, WheelView day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR) + year.getCurrentItem());
        calendar.set(Calendar.MONTH, month.getCurrentItem());
        
        int maxDays = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        day.setViewAdapter(new DateNumericAdapter(this, 1, maxDays, calendar.get(Calendar.DAY_OF_MONTH) - 1));
        int curDay = Math.min(maxDays, day.getCurrentItem() + 1);
        day.setCurrentItem(curDay - 1, true);
    }
	
	/**
     * Adapter for numeric wheels. Highlights the current value.
     */
    private class DateNumericAdapter extends NumericWheelAdapter {
        // Index of current item
        int currentItem;
        // Index of item to be highlighted
        int currentValue;
        
        /**
         * Constructor
         */
        public DateNumericAdapter(Context context, int minValue, int maxValue, int current) {
            super(context, minValue, maxValue);
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
	
	/**
     * Adapter for string based wheel. Highlights the current value.
     */
    private class DateArrayAdapter extends ArrayWheelAdapter<String> {
        // Index of current item
        int currentItem;
        // Index of item to be highlighted
        int currentValue;
        
        /**
         * Constructor
         */
        public DateArrayAdapter(Context context, String[] items, int current) {
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
    
    void initTitlebar(){
    	getKechengActionBar().getRightButton().setVisibility(View.VISIBLE);
	}
}
