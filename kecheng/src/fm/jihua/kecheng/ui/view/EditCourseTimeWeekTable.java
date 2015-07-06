package fm.jihua.kecheng.ui.view;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import fm.jihua.kecheng_hs.R;
import fm.jihua.kecheng.rest.entities.CourseUnit;

public class EditCourseTimeWeekTable extends LinearLayout {

	private EditCourseTimeWeekRow weekRow;
	Button button1, button2, button3;
	TextView textView;

	public EditCourseTimeWeekTable(Context context, View view) {
		super(context);
		textView = (TextView) ((LinearLayout) view).findViewById(R.id.weeks_textview);
		initView();
	}

	public EditCourseTimeWeekTable(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView();
	}

	void initView() {
		LayoutInflater.from(getContext()).inflate(R.layout.add_course_week, this);
		weekRow = (EditCourseTimeWeekRow) findViewById(R.id.addCourseWeekRow);
		weekRow.setWeekString((String) textView.getTag());
		((CheckBox) findViewById(R.id.checkbox_single)).setOnClickListener(onClickListener);
		((CheckBox) findViewById(R.id.checkbox_double)).setOnClickListener(onClickListener);
		((CheckBox) findViewById(R.id.checkbox_all)).setOnClickListener(onClickListener);
		((ImageView) findViewById(R.id.btn_cancle)).setOnClickListener(onClickListener);
		((ImageView) findViewById(R.id.btn_save)).setOnClickListener(onClickListener);
	}

	OnClickListener onClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.checkbox_single:
				weekRow.setWeekType(EditCourseTimeWeekRow.TYPE_SINGLE);
				break;
			case R.id.checkbox_double:
				weekRow.setWeekType(EditCourseTimeWeekRow.TYPE_DOUBLE);
				break;
			case R.id.checkbox_all:
				weekRow.setWeekType(EditCourseTimeWeekRow.TYPE_ALL);
				break;
			case R.id.btn_save:
				String weekString = weekRow.getWeekString();
				textView.setTag(weekString);
				if (!TextUtils.isEmpty(weekString)) {
					switch (weekRow.currentType) {
					case EditCourseTimeWeekRow.TYPE_SINGLE:
						weekString = "1-23周(单周)";
						break;
					case EditCourseTimeWeekRow.TYPE_DOUBLE:
						weekString = "2-24周(双周)";
						break;
					default:
						weekString = CourseUnit.getWeekStringWithOccurance(weekString);
						break;
					}
					textView.setText(weekString);
				} else
					textView.setText(R.string.addCourseWeekTextView);
				if (dismissListener != null)
					dismissListener.dismiss();
				break;
			case R.id.btn_cancle:
				if (dismissListener != null)
					dismissListener.dismiss();
				break;

			default:
				break;
			}

		}
	};

	public interface onDismissListener {
		public void dismiss();
	}

	public void setOnDismissListener(onDismissListener dismissListener) {
		this.dismissListener = dismissListener;
	}

	onDismissListener dismissListener;

}
