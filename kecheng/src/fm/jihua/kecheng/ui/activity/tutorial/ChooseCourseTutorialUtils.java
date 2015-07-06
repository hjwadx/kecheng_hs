package fm.jihua.kecheng.ui.activity.tutorial;

import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import fm.jihua.kecheng_hs.R;
import fm.jihua.kecheng.ui.activity.tutorial.TutorialView.TutorialParams;

public class ChooseCourseTutorialUtils extends TutorialManager {
	
	class MyTutorialSet extends TutorialSet{

		MyTutorialSet(String tutorialSetName) {
			super(tutorialSetName);
		}
		
		@Override
		public boolean isEnable() {
			return getNextStep() == WeekViewTutorialUtils.MAX_CONFLICT_COURSES_TUTORIALS_STEPS - 1;
		}
		
		public TutorialParams getNextTutorialParams() {
			return data.get(0);
		}
	}
	
	public ChooseCourseTutorialUtils(Context context) {
		super(context);
		activity.getWindow().getDecorView().getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
			
			@Override
			public void onGlobalLayout() {
				if (tutorialView.getParent() == null) {
					initTutorialsSets();
					startTutorial();
				}
			}
		});
	}

	private final static String TUTORIAL_CATEGORY_NAME = "CONFLICT_COURSES_TUTORIAL";

	@Override
	void initTutorialsSets() {
		MyTutorialSet tutorialsSet = new MyTutorialSet(TUTORIAL_CATEGORY_NAME); tutorialsSet.setData(getTutorials());
		mapTutorialSets.put(tutorialsSet.tutorialSetName, tutorialsSet);
	}
	
	private List<TutorialParams> getTutorials(){
		List<TutorialParams> list = new LinkedList<TutorialParams>();
		TutorialParams tutorialParams = new TutorialParams(TUTORIAL_CATEGORY_NAME, "会看到时间重叠的所有课程", this);
		tutorialParams.noticeLayout = R.layout.view_tutorial_notice_image_right;
		list.add(tutorialParams);//1
		return list;
	}
	
	@Override
	public void onTriger(TutorialParams params) {
		super.onTriger(params);
		Intent intent = new Intent();
		intent.putExtra(TUTORIAL_FINISHED, true);
		activity.setResult(Activity.RESULT_OK, intent);
		activity.finish();
	}
}
