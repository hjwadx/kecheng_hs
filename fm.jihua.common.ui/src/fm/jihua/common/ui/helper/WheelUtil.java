package fm.jihua.common.ui.helper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import kankan.wheel.widget.OnWheelChangedListener;
import kankan.wheel.widget.WheelView;
import kankan.wheel.widget.adapters.AbstractWheelTextAdapter;
import kankan.wheel.widget.adapters.ArrayWheelAdapter;
import kankan.wheel.widget.adapters.NumericWheelAdapter;
import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import fm.jihua.ui.R;

public class WheelUtil {
	static String months[] = new String[] {"一月", "二月", "三月", "四月", "五月", "六月",
    		"七月", "八月", "九月", "十月", "十一月", "十二月"};
	
	public static View getDatePicker(Activity activity, Date dt, final boolean afterToday){
		View view = activity.getLayoutInflater().inflate(R.layout.date_picker, null);
		final WheelView month = (WheelView) view.findViewById(R.id.month_picker);
        final WheelView year = (WheelView) view.findViewById(R.id.year_picker);
        final WheelView day = (WheelView) view.findViewById(R.id.day_picker);
        
        OnWheelChangedListener listener = new OnWheelChangedListener() {
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
            	if (afterToday) {
            		updateDaysAfterToday(year, month, day);
				}else {
					updateDays(year, month, day);
				}
            }
        };
        
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dt);
        int curMonth = calendar.get(Calendar.MONTH);
        
        month.setViewAdapter(new DateArrayAdapter(activity, months, curMonth));
        month.setCurrentItem(curMonth);
        month.addChangingListener(listener);
    
        // year
        int curYear = calendar.get(Calendar.YEAR);
        if (afterToday) {
        	year.setViewAdapter(new DateNumericAdapter(activity, curYear, curYear + 4, 0));
			year.setCurrentItem(0);
		}else {
			year.setViewAdapter(new DateNumericAdapter(activity, curYear - 1, curYear + 1, 1));
			year.setCurrentItem(1);
		}
        
        year.addChangingListener(listener);
        
        //day
        if (afterToday) {
    		updateDaysAfterToday(year, month, day);
		}else {
			updateDays(year, month, day);
		}
        day.setCurrentItem(calendar.get(Calendar.DAY_OF_MONTH) - 1);
        return view;
	}
	
	public static View getTimePicker(Activity activity, Date dt){
		View view = activity.getLayoutInflater().inflate(R.layout.time_layout, null);
		final WheelView hour = (WheelView) view.findViewById(R.id.hour_picker);
        final WheelView minute = (WheelView) view.findViewById(R.id.minute_picker);
        
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dt);
        int curHour = calendar.get(Calendar.HOUR_OF_DAY);
        hour.setViewAdapter(new DateNumericAdapter(activity, 0, 23, curHour));
    
        // year
        int curMinute = calendar.get(Calendar.MINUTE);
        minute.setViewAdapter(new DateNumericAdapter(activity, 0, 59, curMinute));
        
        return view;
	}
	
	public static View getTimeSpanPicker(Activity activity, String timeString, final OnWheelAnyChangedListener anyChangedListener) {
		View view = activity.getLayoutInflater().inflate(R.layout.layout_time_span, null);
		final WheelView start_hour = (WheelView) view.findViewById(R.id.start_hour_picker);
		final WheelView start_minute = (WheelView) view.findViewById(R.id.start_minute_picker);
		final WheelView end_hour = (WheelView) view.findViewById(R.id.end_hour_picker);
		final WheelView end_minute = (WheelView) view.findViewById(R.id.end_minute_picker);

		int curHourStart = 8;
		int curMinuteStart = 0;
		int curHourEnd = 8;
		int curMinuteEnd = 0;
		if (!TextUtils.isEmpty(timeString)) {
			String[] split = timeString.split("-");
			if (split.length > 1) {
				String startString = split[0];
				String[] splitStart = startString.split(":");
				curHourStart = Integer.parseInt(splitStart[0]);
				curMinuteStart = Integer.parseInt(splitStart[1]);

				String endString = split[1];
				String[] splitEnd = endString.split(":");
				curHourEnd = Integer.parseInt(splitEnd[0]);
				curMinuteEnd = Integer.parseInt(splitEnd[1]);
			}
		}
		// start
		start_hour.setViewAdapter(new DateNumericAdapter(activity, 0, 23, curHourStart));
		start_minute.setViewAdapter(new DateNumericAdapter(activity, 0, 59, curMinuteStart));

		// end
		end_hour.setViewAdapter(new DateNumericAdapter(activity, 0, 23, curHourEnd));
		end_minute.setViewAdapter(new DateNumericAdapter(activity, 0, 59, curMinuteEnd));

		start_hour.setCurrentItem(curHourStart);
		end_hour.setCurrentItem(curHourEnd);
		start_minute.setCurrentItem(curMinuteStart);
		end_minute.setCurrentItem(curMinuteEnd);

		OnWheelChangedListener onWheelChangedListener = new OnWheelChangedListener() {

			@Override
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				if (start_hour == wheel) {
					int currentItem = end_hour.getCurrentItem();
					if (newValue > currentItem) {
						end_hour.setCurrentItem(newValue, true);
					}
				} else if (end_hour == wheel) {
					int currentItem = start_hour.getCurrentItem();
					if (newValue < currentItem) {
						start_hour.setCurrentItem(newValue, true);
					}
				} else if (start_minute == wheel) {
					int currentItem_minute = end_minute.getCurrentItem();
					if (newValue > currentItem_minute && start_hour.getCurrentItem() == end_hour.getCurrentItem()) {
						end_minute.setCurrentItem(newValue, true);
					}
				} else if (end_minute == wheel) {
					int currentItem_minute = start_minute.getCurrentItem();
					if (newValue < currentItem_minute && start_hour.getCurrentItem() == end_hour.getCurrentItem()) {
						start_minute.setCurrentItem(newValue, true);
					}
				}
				if(anyChangedListener!=null){
					anyChangedListener.onChanged();
				}
			}
		};
		start_hour.addChangingListener(onWheelChangedListener);
		end_hour.addChangingListener(onWheelChangedListener);
		start_minute.addChangingListener(onWheelChangedListener);
		end_minute.addChangingListener(onWheelChangedListener);
		return view;
	}
	
	public static View getTimeWidenSpanPicker(Activity activity, List<String> itemList) {
		View view = activity.getLayoutInflater().inflate(R.layout.layout_time_span_widen, null);
		final WheelView start_hour = (WheelView) view.findViewById(R.id.start_hour_picker);
		final WheelView start_minute = (WheelView) view.findViewById(R.id.start_minute_picker);
		final WheelView time_duration = (WheelView) view.findViewById(R.id.time_duration);

		int curHourStart = 8;
		int curMinuteStart = 0;
		int curTimeDuration = 50;
		if (itemList != null && itemList.size() == 2) {
			String startString = itemList.get(0);
			String[] splitStart = startString.split(":");
			curHourStart = Integer.parseInt(splitStart[0]);
			curMinuteStart = Integer.parseInt(splitStart[1]);

			String endString = itemList.get(1);
			String[] splitEnd = endString.split(":");
			int curHourEnd = Integer.parseInt(splitEnd[0]);
			int curMinuteEnd = Integer.parseInt(splitEnd[1]);

			curTimeDuration = (curHourEnd * 60 + curMinuteEnd) - (curHourStart * 60 + curMinuteStart);
		}
		// start
		start_hour.setViewAdapter(new DateNumericAdapter(activity, 0, 23, curHourStart));
		start_minute.setViewAdapter(new DateNumericAdapter(activity, 0, 59, curMinuteStart));

		// end
		time_duration.setViewAdapter(new ArrayWheelAdapter<Integer>(activity, getIntegerArrayForWheel()));

		start_hour.setCurrentItem(curHourStart);
		start_minute.setCurrentItem(curMinuteStart);
		time_duration.setCurrentItem(getWidenPickerIndex(curTimeDuration));

		return view;
	}
	
	//5,10,15……120
	private static Integer[] getIntegerArrayForWheel(){
		Integer[] intArray = new Integer[24];
		for (int i = 1; i < 121; i++) {
			if (i % 5 == 0) {
				intArray[i / 5 - 1] = i;
			}
		}
		return intArray;
	}
	
	public static int getWidenPickerIndex(int number) {
		return number / 5 - 1;
	}

	public static int getWidenPickerNumber(int index) {
		return (index + 1) * 5;
	}
	
	public interface OnWheelAnyChangedListener {
		public void onChanged();
	}
	
	public static String getTimeSpanString(View timerPickerView) {
		if (timerPickerView != null) {
			final WheelView start_hour = (WheelView) timerPickerView.findViewById(R.id.start_hour_picker);
			final WheelView start_minute = (WheelView) timerPickerView.findViewById(R.id.start_minute_picker);
			final WheelView end_hour = (WheelView) timerPickerView.findViewById(R.id.end_hour_picker);
			final WheelView end_minute = (WheelView) timerPickerView.findViewById(R.id.end_minute_picker);

			int startHour = start_hour.getCurrentItem();
			int endHour = end_hour.getCurrentItem();
			int startMinute = start_minute.getCurrentItem();
			String startMinute_String = startMinute < 10 ? "0" + startMinute : String.valueOf(startMinute);
			int endMinute = end_minute.getCurrentItem();
			String endMinute_String = endMinute < 10 ? "0" + endMinute : String.valueOf(endMinute);
			return startHour + ":" + startMinute_String + "-" + endHour + ":" + endMinute_String;
		}
		return "";
	}
	
	public static List<String> getTimeSpanWidenStringList(View timerPickerView) {
		if (timerPickerView != null) {
			final WheelView start_hour = (WheelView) timerPickerView.findViewById(R.id.start_hour_picker);
			final WheelView start_minute = (WheelView) timerPickerView.findViewById(R.id.start_minute_picker);
			final WheelView time_duration = (WheelView) timerPickerView.findViewById(R.id.time_duration);

			int startHour = start_hour.getCurrentItem();
			int startMinute = start_minute.getCurrentItem();
			String startHour_String = startHour < 10 ? "0" + startHour : String.valueOf(startHour);
			String startMinute_String = startMinute < 10 ? "0" + startMinute : String.valueOf(startMinute);

			int timeDuration = getWidenPickerNumber(time_duration.getCurrentItem());
			int endTime = startHour * 60 + startMinute + timeDuration;
			int endHour = endTime / 60;
			String endString = "";
			if (endHour > 23) {
				endString = "23:59";
			} else {
				int endMinute = endTime % 60;
				String endHour_String = endHour < 10 ? "0" + endHour : String.valueOf(endHour);
				String endMinute_String = endMinute < 10 ? "0" + endMinute : String.valueOf(endMinute);
				endString = endHour_String + ":" + endMinute_String;
			}
			List<String> list = new ArrayList<String>();
			list.add(startHour_String + ":" + startMinute_String);
			list.add(endString);
			return list;
		}
		return null;
	}
	
	public static String getDateString(View timerPickerView, String pattern){
		if (timerPickerView != null) {
			final WheelView month = (WheelView) timerPickerView.findViewById(R.id.month_picker);
	        final WheelView year = (WheelView) timerPickerView.findViewById(R.id.year_picker);
	        final WheelView day = (WheelView) timerPickerView.findViewById(R.id.day_picker);
	        Calendar calendar = Calendar.getInstance();
	        Date dt = new Date(calendar.get(Calendar.YEAR)-1+year.getCurrentItem()-1900, month.getCurrentItem(), day.getCurrentItem()+1);
	        SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
	        return dateFormat.format(dt.getTime());
		}
		return "";
	}
	
	public static String getTimeString(View timerPickerView, String pattern){
		if (timerPickerView != null) {
			final WheelView hour = (WheelView) timerPickerView.findViewById(R.id.hour_picker);
	        final WheelView minute = (WheelView) timerPickerView.findViewById(R.id.minute_picker);
	        Calendar calendar = Calendar.getInstance();
	        calendar.set(Calendar.HOUR, hour.getCurrentItem());
	        calendar.set(Calendar.MINUTE, minute.getCurrentItem());
	        SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
	        return dateFormat.format(calendar.getTimeInMillis());
		}
		return "";
	}
	
	public static long getDateAndTime(View timerPickerView){
		if (timerPickerView != null) {
			int day = ((WheelView) timerPickerView.findViewById(R.id.day))
					.getCurrentItem();
			Calendar calendar = Calendar.getInstance(Locale.CHINA);
			calendar.add(Calendar.DAY_OF_YEAR, day);
			int hour = ((WheelView) timerPickerView
					.findViewById(R.id.hour)).getCurrentItem();
			int minute = ((WheelView) timerPickerView
					.findViewById(R.id.mins)).getCurrentItem();
			calendar.set(Calendar.HOUR_OF_DAY, hour);
			calendar.set(Calendar.MINUTE, minute);
			calendar.set(Calendar.SECOND, 0);
	        return calendar.getTimeInMillis();
		}
		return 0;
	}
	
	public static String getCourseRemindDateAndTime(View timerPickerView){
		if (timerPickerView != null) {
			String strTime;
			int day = ((WheelView) timerPickerView.findViewById(R.id.day)).getCurrentItem();
			Calendar calendar = Calendar.getInstance(Locale.CHINA);
			int hour = ((WheelView) timerPickerView
					.findViewById(R.id.hour)).getCurrentItem();
			int minute = ((WheelView) timerPickerView
					.findViewById(R.id.mins)).getCurrentItem();
			calendar.set(Calendar.HOUR_OF_DAY, hour);
			calendar.set(Calendar.MINUTE, minute);
			calendar.set(Calendar.SECOND, 0);
			Date dt = new Date(calendar.getTimeInMillis());
			String timePattern = "HH:mm";
			if(day == 0){
				SimpleDateFormat sdf = new SimpleDateFormat("-"+timePattern);
				strTime = sdf.format(dt);
			} else {
				SimpleDateFormat sdf = new SimpleDateFormat(""+timePattern);
				strTime = sdf.format(dt);
			}
	        return strTime;
		}
		return null;
	}
	
	public static View getDayAndTimePicker(Activity activity, Date dt) {
		View timerPickerView = activity.getLayoutInflater().inflate(
				R.layout.day_time_layout, null);

		final WheelView hours = (WheelView) timerPickerView
				.findViewById(R.id.hour);
		NumericWheelAdapter hourAdapter = new NumericWheelAdapter(activity, 0, 23, "%02d");
		hourAdapter.setItemResource(R.layout.wheel_text_item);
		hourAdapter.setItemTextResource(R.id.text);
		hours.setViewAdapter(hourAdapter);

		final WheelView mins = (WheelView) timerPickerView
				.findViewById(R.id.mins);
		NumericWheelAdapter minAdapter = new NumericWheelAdapter(activity, 0, 59,
				"%02d");
		minAdapter.setItemResource(R.layout.wheel_text_item);
		minAdapter.setItemTextResource(R.id.text);
		mins.setViewAdapter(minAdapter);
		mins.setCyclic(true);

		// set current time
		Calendar calendar = Calendar.getInstance(Locale.getDefault());
		calendar.setTime(dt);
		hours.setCurrentItem(calendar.get(Calendar.HOUR_OF_DAY));
		mins.setCurrentItem(calendar.get(Calendar.MINUTE));

		final WheelView day = (WheelView) timerPickerView
				.findViewById(R.id.day);
		day.setViewAdapter(new DayArrayAdapter(activity, Calendar.getInstance(Locale.getDefault())));
		int dayDiff = (int) (calendar.getTimeInMillis()/(24*3600*1000)-System.currentTimeMillis()/(24*3600*1000));
		if (dayDiff >= 0) {
			day.setCurrentItem(dayDiff);
		}
		return timerPickerView;
	}
	
	public static View getCourseRemindDatePicker(Activity activity, String str) {
		View timerPickerView = activity.getLayoutInflater().inflate(
				R.layout.day_time_layout, null);
		String timeString = str;
		SimpleDateFormat sdf = new SimpleDateFormat("hh:mm");
		long time = 0;
		if (timeString.startsWith("-")) {
			timeString = timeString.substring(1);
		}
		try {
			time = sdf.parse(timeString).getTime();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		Date dt = new Date(time);
		final WheelView hours = (WheelView) timerPickerView
				.findViewById(R.id.hour);
		NumericWheelAdapter hourAdapter = new NumericWheelAdapter(activity, 0, 23);
		hourAdapter.setItemResource(R.layout.wheel_text_item);
		hourAdapter.setItemTextResource(R.id.text);
		hours.setViewAdapter(hourAdapter);

		final WheelView mins = (WheelView) timerPickerView
				.findViewById(R.id.mins);
		NumericWheelAdapter minAdapter = new NumericWheelAdapter(activity, 0, 59,
				"%02d");
		minAdapter.setItemResource(R.layout.wheel_text_item);
		minAdapter.setItemTextResource(R.id.text);
		mins.setViewAdapter(minAdapter);
		mins.setCyclic(true);

		// set current time
		Calendar calendar = Calendar.getInstance(Locale.getDefault());
		calendar.setTime(dt);
		hours.setCurrentItem(calendar.get(Calendar.HOUR_OF_DAY));
		mins.setCurrentItem(calendar.get(Calendar.MINUTE));

		final WheelView day = (WheelView) timerPickerView
				.findViewById(R.id.day);
		ArrayWheelAdapter<String> dayAdapter =
	            new ArrayWheelAdapter<String>(activity, new String[]{"前一天", "当天"});
		dayAdapter.setItemResource(R.layout.wheel_text_item);
		dayAdapter.setItemTextResource(R.id.text);
		day.setViewAdapter(dayAdapter);
		if (str.startsWith("-")) {
			day.setCurrentItem(0);
		} else {
			day.setCurrentItem(1);
		}
		return timerPickerView;
	}

	/**
	 * Day adapter
	 * 
	 */
	private static class DayArrayAdapter extends AbstractWheelTextAdapter {
		// Count of days to be shown
		private final int daysCount = 1000;

		// Calendar
		Calendar calendar;

		/**
		 * Constructor
		 */
		protected DayArrayAdapter(Context context, Calendar calendar) {
			super(context, R.layout.time_day, NO_RESOURCE);
			this.calendar = calendar;

			setItemTextResource(R.id.time2_monthday);
		}

		@Override
		public View getItem(int index, View cachedView, ViewGroup parent) {
			int day = index;
			Calendar newCalendar = (Calendar) calendar.clone();
			newCalendar.add(Calendar.DAY_OF_YEAR, day);

			View view = super.getItem(index, cachedView, parent);
			TextView year = (TextView) view.findViewById(R.id.time2_year);
			if (day == 0) {
				year.setText("");
			} else {
				SimpleDateFormat format = new SimpleDateFormat("yyyy年");
				year.setText(format.format(newCalendar.getTime()));
			}

			TextView monthday = (TextView) view
					.findViewById(R.id.time2_monthday);
			if (day == 0) {
				monthday.setText("今天");
				monthday.setTextColor(0xFF0000F0);
			} else {
				SimpleDateFormat format = new SimpleDateFormat("MMM d");
				monthday.setText(format.format(newCalendar.getTime()));
				monthday.setTextColor(0xFF111111);
			}

			return view;
		}

		@Override
		public int getItemsCount() {
			return daysCount + 1;
		}

		@Override
		protected CharSequence getItemText(int index) {
			return "";
		}
	}

	private static void updateDays(WheelView year, WheelView month, WheelView day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR) + year.getCurrentItem()-1);
        calendar.set(Calendar.MONTH, month.getCurrentItem());
        
        int maxDays = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        day.setViewAdapter(new DateNumericAdapter(year.getContext(), 1, maxDays, calendar.get(Calendar.DAY_OF_MONTH) - 1));
        int curDay = Math.min(maxDays, day.getCurrentItem() + 1);
        day.setCurrentItem(curDay - 1, true);
    }
	
	private static void updateDaysAfterToday(WheelView year, WheelView month, WheelView day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR) + year.getCurrentItem());
        int oldMonth = month.getCurrentItem();
        String[] newMonths = new String[12-oldMonth];
        for (int i = 0; i < newMonths.length; i++) {
			newMonths[i] = months[oldMonth+i];
		}
        if (year.getCurrentItem() == 0) {
        	month.setViewAdapter(new DateArrayAdapter(year.getContext(), newMonths, oldMonth));
		}else {
			month.setViewAdapter(new DateArrayAdapter(year.getContext(), months, oldMonth));
		}
        calendar.set(Calendar.MONTH, month.getCurrentItem());
        
        int maxDays = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        day.setViewAdapter(new DateNumericAdapter(year.getContext(), 1, maxDays, calendar.get(Calendar.DAY_OF_MONTH) - 1));
        int curDay = Math.min(maxDays, day.getCurrentItem() + 1);
        day.setCurrentItem(curDay - 1, true);
    }
	
	/**
     * Adapter for numeric wheels. Highlights the current value.
     */
    private static class DateNumericAdapter extends NumericWheelAdapter {
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
    private static class DateArrayAdapter extends ArrayWheelAdapter<String> {
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
}
