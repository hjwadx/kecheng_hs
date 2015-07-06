package fm.jihua.kecheng.ui.adapter;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.BaseAdapter;
import android.widget.TextView;
import fm.jihua.kecheng.App;
import fm.jihua.kecheng_hs.R;
import fm.jihua.kecheng.rest.entities.Examination;
import fm.jihua.kecheng.rest.entities.User;
import fm.jihua.kecheng.ui.activity.plugin.AddExaminationActivity;
import fm.jihua.kecheng.utils.Const;
import fm.jihua.kecheng.utils.ImageHlp;

public class ExaminatinAdapter extends BaseAdapter {

	// List<CourseTime> mFullCourseTimes;

	// List<CourseTime> mCourseTimes;
	final List<Examination> mExaminations;
	int mDayOfWeek;
	int mTimeSlot;
	boolean mCustomBackground = true;
	final int ONGOING = 0;
	final int ADD = 1;
	final int EXPIRED = 2;
	boolean showAddButton = true;

	public ExaminatinAdapter(List<Examination> examinations) {
		this.mExaminations = examinations;
	}
	
	public ExaminatinAdapter(List<Examination> examinations, boolean showAddButton) {
		this.mExaminations = examinations;
		this.showAddButton = showAddButton;
	}
	

	@Override
	public int getCount() {
		return this.mExaminations.size();
	}

	@Override
	public Object getItem(int position) {
		return this.mExaminations.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}
	
	@Override
	public int getItemViewType(int position) {
		Examination examination = mExaminations.get(position);
		return getViewType(examination);
	}
	
	@Override
	public int getViewTypeCount() {
		return 3;
	}

	@Override
	public View getView(final int position, View convertView,
			final ViewGroup parent) {
		int type = getItemViewType(position);
		Examination examination = mExaminations.get(position);
		if (convertView == null) {
			convertView =initViews(parent, type);
		}
		ViewHolder viewHolder = (ViewHolder) convertView.getTag();
		viewHolder.setView(convertView.getContext(), examination, mExaminations);
		return convertView;
	}
	
	View initViews(final ViewGroup parent, int type){
		View view = null;
		ViewHolder baseViewHolder = null;
		switch (type) {
		case ONGOING:{
			view = LayoutInflater.from(parent.getContext()).inflate(
					R.layout.examination_item, parent, false);
			OngoingViewHolder viewHolder = new OngoingViewHolder();
			viewHolder.name = ((TextView) view.findViewById(R.id.name));
			viewHolder.date = ((TextView) view.findViewById(R.id.date));
			viewHolder.time = ((TextView) view.findViewById(R.id.time));
			viewHolder.room = ((TextView) view.findViewById(R.id.room));
			viewHolder.remain = ((TextView) view.findViewById(R.id.remain));
			baseViewHolder = viewHolder;
		} 
			break;
		case ADD:{
			view = LayoutInflater.from(parent.getContext()).inflate(
					R.layout.examination_add, parent, false);
			AddViewHolder viewHolder = new AddViewHolder();
			viewHolder.add = view.findViewById(R.id.add);
			viewHolder.hint = (TextView)view.findViewById(R.id.add_hint);
			if (!showAddButton) {
				viewHolder.add.setVisibility(View.GONE);
			}
			baseViewHolder = viewHolder;
		}
			break;
		case EXPIRED:
			view = LayoutInflater.from(parent.getContext()).inflate(
					R.layout.examination_expired_item, parent, false);
			BaseViewHolder viewHolder = new BaseViewHolder();
			viewHolder.name = ((TextView) view.findViewById(R.id.name));
			viewHolder.date = ((TextView) view.findViewById(R.id.date));
			viewHolder.time = ((TextView) view.findViewById(R.id.time));
			viewHolder.room = ((TextView) view.findViewById(R.id.room));
			baseViewHolder = viewHolder;
			break;
		default:
			break;
		}
		view.setTag(baseViewHolder);
		return view;
	}
	
	int getViewType(Examination examination){
		if (examination == null) {
			return ADD;
		}else {
			if (examination.time > System.currentTimeMillis()) {
				return ONGOING;
			}else {
				return EXPIRED;
			}
		}
	}
	
	private static String getHint(Context context, List<Examination> examinations){
		String hint;
		int index = examinations.indexOf(null);
		int count = examinations.size()-1;
		App app = (App)context.getApplicationContext();
		User myself = app.getMyself();
		if (count == 0) {
			hint = "神马？没有考试？这不科学！";
		}else if (index == 0) {
			hint = "祝贺你！考完试了，尽情放松吧！";
		}else if (index == examinations.size()-1 && count >= 5) {
			hint = "真正的学霸，敢于直面悲催的考试安排！";
		}else if (count >= 2 && index == 1) {
			hint = "考试的乌云即将远去，假期的曙光就在前方！";
		}else if (count >= 2 && index == count - 1) {
			hint = "考完一门了，小秘书继续给你加油哦！";
		}else {
			if (myself.sex == Const.FEMALE) {
				hint = "加油妹纸！爱格子，不挂科！";
			}else {
				hint = "加油骚年！爱格子，不挂科！";
			}
		}
		return hint;
	}
	
	static abstract class ViewHolder{
		abstract void setView(Context context, Examination examination, List<Examination> examinations);
	}
	
	static class BaseViewHolder extends ViewHolder{
		public TextView name;
		public TextView date;
		public TextView time;
		public TextView room;
		void setView(Context context, Examination examination, List<Examination> examinations){
			name.setText(examination.name);
			SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy年MM月dd日 E", Locale.getDefault());
			date.setText(sdf1.format(examination.time));
			SimpleDateFormat sdf2 = new SimpleDateFormat("a hh:mm", Locale.getDefault());
			time.setText(sdf2.format(examination.time));
			room.setText(examination.room);
			if (examination.room == null || "".equals(examination.room)) {
				room.setText("地点未知");
			}
		}
	}
	
	public static class OngoingViewHolder extends BaseViewHolder{
		public TextView remain;
		public void setView(Context context, Examination examination, List<Examination> examinations){
			super.setView(context, examination, examinations);
			int day = 0;
			Calendar calendar = Calendar.getInstance();
			if (examination.time > System.currentTimeMillis()) {
				day = (int)((examination.time+calendar.get(Calendar.ZONE_OFFSET))/3600/1000/24 - (System.currentTimeMillis()+calendar.get(Calendar.ZONE_OFFSET))/3600/1000/24);
			}
			remain.setText(String.valueOf(day));
			MarginLayoutParams lp = (MarginLayoutParams) remain.getLayoutParams();
			if (day >= 100 && day < 1000) {
				remain.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30);
				lp.setMargins(0, 0, ImageHlp.changeToSystemUnitFromDP(context, 3), ImageHlp.changeToSystemUnitFromDP(context, -7));
			}else if(day >= 1000){
				remain.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
				lp.setMargins(0, 0, ImageHlp.changeToSystemUnitFromDP(context, 3), ImageHlp.changeToSystemUnitFromDP(context, -3));
			}else {
				remain.setTextSize(TypedValue.COMPLEX_UNIT_SP, 40);
				lp.setMargins(0, 0, ImageHlp.changeToSystemUnitFromDP(context, 3), ImageHlp.changeToSystemUnitFromDP(context, -10));
			}
			remain.setLayoutParams(lp);
		}
	}
	
	static class AddViewHolder extends ViewHolder{
		View add;
		TextView hint;
		void setView(final Context context, Examination examination, List<Examination> examinations){
			add.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(context, AddExaminationActivity.class);
					((Activity)context).startActivityForResult(intent, Const.INTENT_DEFAULT);
				}
			});
			hint.setText(getHint(context, examinations));
		}
	}
}
