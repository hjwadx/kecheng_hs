package fm.jihua.kecheng.ui.activity.setting;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import fm.jihua.common.ui.helper.Hint;
import fm.jihua.kecheng.App;
import fm.jihua.kecheng_hs.R;
import fm.jihua.kecheng.utils.AppLogger;

/**
 * @date 2013-8-21
 * @introduce
 */
public class TimeModeParams {

	public int startTimeHour = 8;
	public int startTimeMinute = 0;
	public int classLength = 50;
	public int playTime = 10;
	public int morningSlotLength = 4;

	public List<List<String>> listString = new ArrayList<List<String>>();

	@SuppressLint("SimpleDateFormat")
	public SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");

	Context context;

	public TimeModeParams(Context context) {
		super();
		this.context = context;
	}

	public void initListItemData(List<List<String>> listItem) {
		this.listString.clear();
		if (listItem != null && listItem.size() > App.mTimeSlotLength) {
			this.listString = listItem.subList(0, App.mTimeSlotLength);
		} else {
			this.listString = listItem;
		}
		boolean isNew = false;
		isNew = listString == null || listString.size() < 2;
		int slotLength = App.mTimeSlotLength;
		for (int position = 0; position < slotLength; position++) {

			if (position < listString.size()) {
				if (position == 0) {
					initBaseInfoByFirstSetting(listString.get(position));
				}
				if (isNew) {
					listString.set(position, getTimeStringByPosition(position));
				}
			} else {
				listString.add(getTimeStringByPosition(position));
			}
		}

	}

	public void initBaseInfoByFirstSetting(List<String> itemListString) {
		try {
			Calendar cal_start = Calendar.getInstance();
			cal_start.setTime(dateFormat.parse(itemListString.get(0)));
			Calendar cal_end = Calendar.getInstance();
			cal_end.setTime(dateFormat.parse(itemListString.get(1)));

			startTimeHour = cal_start.get(Calendar.HOUR_OF_DAY);
			startTimeMinute = cal_start.get(Calendar.MINUTE);
			classLength = (cal_end.get(Calendar.HOUR_OF_DAY) * 60 + cal_end.get(Calendar.MINUTE)) - (startTimeHour * 60 + startTimeMinute);
			playTime = 10;
		} catch (ParseException e) {
			e.printStackTrace();
			AppLogger.i("dataFormat.parse throw exception");
		}
	}

	public String getStringFromTime(int totalTime) {
		int hour = totalTime / 60;
		int minute = totalTime % 60;
		String hourString = hour < 10 ? "0" + hour : String.valueOf(hour);
		String minString = minute < 10 ? "0" + minute : String.valueOf(minute);
		return hour > 23 ? "23:59" : hourString + ":" + minString;
	}

	public int getTimeFromString(List<String> itemTimeList, boolean isStart) {
		int totalTime = 0;
		try {
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(isStart ? dateFormat.parse(itemTimeList.get(0)) : dateFormat.parse(itemTimeList.get(1)));
			totalTime = calendar.get(Calendar.HOUR_OF_DAY) * 60 + calendar.get(Calendar.MINUTE);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return totalTime;
	}

	public List<String> getTimeStringByPosition(int position) {
		int offsetTime = (this.classLength + this.playTime) * position;
		int totalTime = offsetTime + startTimeHour * 60 + startTimeMinute;
		List<String> itemListString = new ArrayList<String>();
		itemListString.add(getStringFromTime(totalTime));
		itemListString.add(getStringFromTime(totalTime + classLength));
		return itemListString;
	}

	public void setWheelUtilString(List<String> itemListString, int position) {
		int size = listString.size();
		listString.set(position, itemListString);
		for (int i = position + 1; i < size; i++) {
			List<String> listCur = listString.get(i);
			List<String> listPrev = listString.get(i - 1);
			int timeNextStart = getTimeFromString(listCur, true);
			int timePrevEnd = getTimeFromString(listPrev, false);
			if ((timeNextStart - timePrevEnd) < playTime) {
				timeNextStart = timePrevEnd + playTime;
				listCur.set(0, getStringFromTime(timeNextStart));
				listCur.set(1, getStringFromTime(timeNextStart + classLength));
				listString.set(i, listCur);
			}
		}
	}

	List<List<String>> changeListItemData(boolean isNew) {
		int slotLength = App.mTimeSlotLength;
		for (int position = 0; position < slotLength; position++) {
			if (position > listString.size() - 1) {
				listString.add(getTimeStringByPosition(position));
			}else if(isNew){
				listString.set(position, getTimeStringByPosition(position));
			}
		}
		return listString;
	}

	// 检查课程时间是否冲突
	public boolean checkListString() {
		int size = listString.size();
		try {
			for (int i = 0; i < size; i++) {
				List<String> itemStringList = listString.get(i);
				Date startDate = (Date) dateFormat.parse(itemStringList.get(0));
				Date endDate = (Date) dateFormat.parse(itemStringList.get(1));
				if (startDate.getTime() > endDate.getTime()) {
					String hintString = context.getResources().getString(R.string.prompt_conflict_when_set_time_pattern_single);
					Hint.showTipsShort(context, String.format(hintString, String.valueOf(i + 1)));
					return false;
				} else {
					if (i != size - 1) {
						List<String> listNext = listString.get(i + 1);
						Date startDateNext = (Date) dateFormat.parse(listNext.get(0));
						if (endDate.getTime() > startDateNext.getTime()) {
							String hintString = context.getResources().getString(R.string.prompt_conflict_when_set_time_pattern);
							Hint.showTipsShort(context, String.format(hintString, String.valueOf(i + 1), String.valueOf(i + 2)));
							return false;
						}
					}
				}
			}
		} catch (ParseException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

}
