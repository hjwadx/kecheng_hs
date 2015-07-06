package fm.jihua.kecheng.ui.view;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import fm.jihua.kecheng_hs.R;

public class EditCourseTimeWeekRow extends LinearLayout {

	public EditCourseTimeWeekRow(Context context) {
		super(context);
		initView();
	}

	public EditCourseTimeWeekRow(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView();
	}

	List<CheckBox> list;
	int rowNumber = 4;
	int columnNumber = 6;
	public static final int TYPE_SINGLE = 1;
	public static final int TYPE_DOUBLE = 2;
	public static final int TYPE_ALL = 3;
	public static final int TYPE_NORMAL = 0;
	int currentType = TYPE_NORMAL;

	void initView() {

		list = new ArrayList<CheckBox>();
		setOrientation(VERTICAL);
		LayoutInflater inflater = LayoutInflater.from(getContext());
		for (int rowindex = 0; rowindex < rowNumber; rowindex++) {
			LinearLayout layout = new LinearLayout(getContext());
			layout.setOrientation(HORIZONTAL);
			for (int columnindex = 0; columnindex < columnNumber; columnindex++) {

				View view = (View) inflater.inflate(R.layout.add_course_week_checkbox, null);
				CheckBox checkBox = (CheckBox) view.findViewById(R.id.checkBox1);
				initCheckBox(checkBox, rowindex, columnindex);
				checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
						if (isChecked)
							buttonView.setBackgroundResource(R.drawable.week_select_checkedback);
						else
							buttonView.setBackgroundColor((Integer) buttonView.getTag());

						checkTitleBarStatus();
					}
				});

				LayoutParams layoutParams = new LinearLayout.LayoutParams(0, LayoutParams.WRAP_CONTENT, 1);
				view.setLayoutParams(layoutParams);
				layout.addView(view);
				list.add(checkBox);
				// add vertical line
				if (columnindex != columnNumber - 1) {
					View lineView = new View(getContext());
					lineView.setBackgroundColor(getContext().getResources().getColor(R.color.divider_bg));
					lineView.setLayoutParams(new LayoutParams((int) (getContext().getResources().getDimension(R.dimen.dimen_divider_thick_line)), LayoutParams.MATCH_PARENT));
					layout.addView(lineView);
				}
			}
			addView(layout);

			// add horizontal line
			View lineView = new View(getContext());
			lineView.setBackgroundColor(getContext().getResources().getColor(R.color.divider_bg));
			lineView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, (int) (getContext().getResources().getDimension(R.dimen.dimen_divider_thick_line))));
			addView(lineView);
		}
	}

	void initCheckBox(CheckBox checkBox, int rowindex, int columnindex) {
		// 背景颜色 要根据行数和列数来判断
		int position = columnNumber * rowindex + (columnindex + 1);
		int viewColor = 0;
		if (rowindex % 2 == 0) {
			if (columnindex % 2 == 0)
				viewColor = 0xFFF4F4F4;
			else
				viewColor = 0xFFFFFFFF;
		} else {
			if (columnindex % 2 == 0)
				viewColor = 0xFFFFFFFF;
			else
				viewColor = 0xFFF4F4F4;
		}
		checkBox.setBackgroundColor(viewColor);
		checkBox.setTag(viewColor);
		checkBox.setText(String.valueOf(position));
	}

	void checkTitleBarStatus() {
		currentType = checkCurrentStatus();
		switch (currentType) {
		case TYPE_NORMAL:
			setTitlbarStatus(false, false, false);
			break;
		case TYPE_SINGLE:
			setTitlbarStatus(true, false, false);
			break;
		case TYPE_DOUBLE:
			setTitlbarStatus(false, true, false);
			break;
		case TYPE_ALL:
			setTitlbarStatus(false, false, true);
			break;

		default:
			break;
		}
	}
	
	void setTitlbarStatus(boolean keySingle, boolean keyDouble, boolean keyAll) {
		((CheckBox) ((View) EditCourseTimeWeekRow.this.getParent()).findViewById(R.id.checkbox_single)).setChecked(keySingle);
		((CheckBox) ((View) EditCourseTimeWeekRow.this.getParent()).findViewById(R.id.checkbox_double)).setChecked(keyDouble);
		((CheckBox) ((View) EditCourseTimeWeekRow.this.getParent()).findViewById(R.id.checkbox_all)).setChecked(keyAll);
	}

	int checkCurrentStatus() {
		boolean isAll = true;
		boolean isSingle = true;
		boolean isDouble = true;
		for (int i = 0; i < list.size() && (isAll || isSingle || isDouble); i++) {
			if (!list.get(i).isChecked()) {
				isAll = false;
				if (i % 2 == 0)
					isSingle = false;
				else {
					isDouble = false;
				}
			} else {
				if (i % 2 == 0)
					isDouble = false;
				else {
					isSingle = false;
				}
			}
		}
		if (isAll) {
			return TYPE_ALL;
		}
		if (isSingle) {
			return TYPE_SINGLE;
		}
		if (isDouble) {
			return TYPE_DOUBLE;
		}
		return TYPE_NORMAL;
	}

	public void setWeekType(int type) {
		switch (type) {
		case TYPE_SINGLE:
			for (int i = 0; i < list.size(); i++) {
				if (i % 2 == 0)
					list.get(i).setChecked(true);
				else
					list.get(i).setChecked(false);
			}
			break;
		case TYPE_DOUBLE:
			for (int i = 0; i < list.size(); i++) {
				if (i % 2 == 1)
					list.get(i).setChecked(true);
				else
					list.get(i).setChecked(false);
			}
			break;
		case TYPE_ALL:
			for (int i = 0; i < list.size(); i++) {
				list.get(i).setChecked(true);
			}
			break;

		default:
			break;
		}
	}

	String getWeekString() {
		StringBuffer buffer = new StringBuffer();
		String currentPosition = "";
		for (int position = 0; position < list.size(); position++) {

			currentPosition = String.valueOf(position + 1);
			if (position == 0) {
				if (list.get(position).isChecked()) {
					if (list.get(position + 1).isChecked())
						buffer.append(currentPosition + "-");
					else
						buffer.append(String.valueOf(position + 1));
				}
			} else if (position == list.size() - 1) {
				if (list.get(position).isChecked()) {
					if (list.get(position - 1).isChecked())
						buffer.append(currentPosition);
					else
						buffer.append("," + currentPosition);
				}
			} else {
				if (list.get(position).isChecked()) {
					// 前true,后false
					if (list.get(position - 1).isChecked() && !list.get(position + 1).isChecked())
						buffer.append(currentPosition);
					// 前false,后false
					else if (!list.get(position - 1).isChecked() && !list.get(position + 1).isChecked()) {
						if (buffer.toString().length() != 0)
							buffer.append(",");
						buffer.append(currentPosition);
					}
					// 前false,后true
					else if (!list.get(position - 1).isChecked() && list.get(position + 1).isChecked()) {
						if (buffer.toString().length() != 0)
							buffer.append(",");
						buffer.append(currentPosition + "-");
					}
				}
			}
		}
		return buffer.toString();
	}

	void setWeekString(String weekString) {
		if (!TextUtils.isEmpty(weekString) && !getContext().getString(R.string.addCourseWeekTextView).equals(weekString)) {
			String[] splitWith_Douhao = weekString.split(",");
			for (int i = 0; i < splitWith_Douhao.length; i++) {
				String[] splitWith_Hengxian = splitWith_Douhao[i].split("-");
				if (splitWith_Hengxian.length == 1)
					list.get(Integer.parseInt(splitWith_Hengxian[0]) - 1).setChecked(true);
				else {
					for (int j = Integer.parseInt(splitWith_Hengxian[0]) - 1; j < Integer.parseInt(splitWith_Hengxian[1]); j++) {
						list.get(j).setChecked(true);
					}
				}
			}
		}
	}
}
