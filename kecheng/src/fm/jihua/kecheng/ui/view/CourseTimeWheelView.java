package fm.jihua.kecheng.ui.view;

import kankan.wheel.widget.OnWheelChangedListener;
import kankan.wheel.widget.WheelView;
import kankan.wheel.widget.adapters.ArrayWheelAdapter;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import fm.jihua.kecheng_hs.R;
import fm.jihua.kecheng.rest.entities.CourseBlock;
import fm.jihua.kecheng.utils.Const;
import fm.jihua.kecheng.utils.CourseHelper;

public class CourseTimeWheelView extends LinearLayout {

	EditCourseTimeRow host;
	
	WheelView wv_week;
	WheelView wv_start_slot;
	WheelView wv_end_slot;
	
//	int correctEndSlotItem;
	
	public CourseTimeWheelView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initViews();
	}

	public CourseTimeWheelView(Context context) {
		super(context);
		initViews();
	}

	void initViews(){
		inflate(getContext(), R.layout.course_time_picker, this);
		findWheels(this);
		initTimePicker(this);
	}
	
	private void findWheels(View view){
		wv_week = (WheelView) view.findViewById(R.id.week_picker);
		wv_start_slot = (WheelView) view.findViewById(R.id.start_slot_picker);
		wv_end_slot = (WheelView) view.findViewById(R.id.slot_count_picker);
	}
	
	private void initTimePicker(View view){
		MyOnWheelChangedListener listener = new MyOnWheelChangedListener();
		
		
		ArrayWheelAdapter<String> occuranceAdapter =
	            new ArrayWheelAdapter<String>(getContext(), getResources().getStringArray(R.array.occurances_name));
		
		
		ArrayWheelAdapter<String> weekDayAdapter =
	            new ArrayWheelAdapter<String>(getContext(), Const.WEEKS);
		wv_week.setViewAdapter(weekDayAdapter);
		wv_week.addChangingListener(listener);
		int slotLength = Const.MAX_TIME_SLOT;
		String[] startSlots = new String[slotLength];
		for(int i=0; i<slotLength; i++){
			startSlots[i] = ("第"+(i+1)+"节");
		}
		
		String[] endSlots = new String[slotLength];
		for(int i=0; i<slotLength; i++){
			endSlots[i] = ("到"+(i+1)+"节");
		}

		
		ArrayWheelAdapter<String> startSlotAdapter =
	            new ArrayWheelAdapter<String>(getContext(), startSlots);
		wv_start_slot.setViewAdapter(startSlotAdapter);
		wv_start_slot.addChangingListener(listener);

		
		ArrayWheelAdapter<String> slotCountAdapter =
	            new ArrayWheelAdapter<String>(getContext(), endSlots);
		wv_end_slot.setViewAdapter(slotCountAdapter);
		wv_end_slot.setCurrentItem(1);
		wv_end_slot.addChangingListener(listener);
	}
	
	public void setHost(EditCourseTimeRow host){
		this.host = host;
	}
	
	public void setData(CourseBlock block){
		if(block.start_slot == 0 && block.end_slot == 0){
			block.start_slot = wv_start_slot.getCurrentItem() + 1;
			block.end_slot = wv_end_slot.getCurrentItem() + 1;
			block.day_of_week = CourseHelper.getIndexFromDayOfWeek(wv_week.getCurrentItem() + 1);
			host.setData(block, -1);
		}
		wv_week.setCurrentItem(block.day_of_week-1);
		wv_start_slot.setCurrentItem(block.start_slot-1);
		wv_end_slot.setCurrentItem(block.end_slot-1);
//		wv_end_slot.addScrollingListener(new OnWheelScrollListener() {
//			
//			@Override
//			public void onScrollingStarted(WheelView wheel) {
//				
//			}
//			
//			@Override
//			public void onScrollingFinished(WheelView wheel) {
//				wv_end_slot.setCurrentItem(correctEndSlotItem, true);
//			}
//		});
	}
	
	class MyOnWheelChangedListener implements OnWheelChangedListener{

		@Override
		public void onChanged(WheelView wheel, int oldValue, int newValue) {
//			int startSlot = (wv_start_slot.getCurrentItem()+1);
//			int endSlot = (wv_end_slot.getCurrentItem()+1);
//			correctEndSlotItem = wv_end_slot.getCurrentItem();
//			if (endSlot < startSlot) {
//				wheel.stopScrolling();
//				correctEndSlotItem = wv_start_slot.getCurrentItem();
//				wv_end_slot.setCurrentItem(correctEndSlotItem, true);
//				if (wheel == wv_start_slot) {
//					wheel.setCurrentItem(slotLength-wv_slot_count.getCurrentItem()-2);
//				}else if (wheel == wv_slot_count) {
//					wheel.setCurrentItem(slotLength-startSlot);
//				}
//			}
			CourseBlock block = host.getData();
//			block.occurance = wv_occurance.getCurrentItem()+1;
//			block.day_of_week = wv_week.getCurrentItem() + 1;
//			block.start_slot = wv_start_slot.getCurrentItem() + 1;
//			block.end_slot = wv_end_slot.getCurrentItem() + 1;
			switch (wheel.getId()) {
			case R.id.week_picker:
				block.day_of_week = CourseHelper.getIndexFromDayOfWeek(newValue + 1);
				if(block.start_slot * block.end_slot == 0){
					block.start_slot = wv_start_slot.getCurrentItem() + 1;
					block.end_slot = wv_end_slot.getCurrentItem() + 1;
				}
				break;
			case R.id.start_slot_picker:
				block.start_slot = newValue + 1;
				if (block.end_slot <= block.start_slot) {
					wv_end_slot.setCurrentItem(Math.min(block.start_slot, 15), false);
					block.end_slot = Math.min(block.start_slot + 1, 16);
				}
				block.day_of_week = CourseHelper.getIndexFromDayOfWeek(wv_week.getCurrentItem() + 1);
				break;
			case R.id.slot_count_picker:
				int endSlot = newValue + 1;
				if (endSlot < block.start_slot) {
					wv_end_slot.setCurrentItem(block.start_slot - 1, false);
					endSlot = block.start_slot;
				}
				block.end_slot = endSlot;
				block.day_of_week = CourseHelper.getIndexFromDayOfWeek(wv_week.getCurrentItem() + 1);
			default:
				break;
			}
			host.setData(block, -1);
		}
	}
}
