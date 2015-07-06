package fm.jihua.kecheng.ui.activity.tutorial;

import java.util.LinkedHashMap;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.RelativeLayout;
import fm.jihua.common.utils.Compatibility;
import fm.jihua.kecheng.ui.activity.BaseActivity;
import fm.jihua.kecheng.ui.activity.BaseMenuActivity;
import fm.jihua.kecheng.ui.activity.tutorial.TutorialView.TrigerCallback;
import fm.jihua.kecheng.ui.activity.tutorial.TutorialView.TutorialParams;

public abstract class TutorialManager implements TrigerCallback {
	public static final String TUTORIAL_FINISHED = "TUTORIAL_FINISHED"; 
	
	Map<String, TutorialSet> mapTutorialSets;
	TutorialView tutorialView;
	Activity activity;
	int screenWidth;
	int screenHeight;
	
	abstract void initTutorialsSets();
	
	public TutorialManager(final Context context) {
		activity = (Activity)context;
		screenWidth = Compatibility.getWidth(activity.getWindowManager().getDefaultDisplay());
		screenHeight = Compatibility.getHeight(activity.getWindowManager().getDefaultDisplay());
		tutorialView = new TutorialView(context);
		mapTutorialSets = new LinkedHashMap<String, TutorialSet>();
		if (gotoNextStep()) {
			if (activity instanceof BaseActivity) {
				((BaseActivity) activity).lockKeyInput(true);
			}else if (activity instanceof BaseMenuActivity) {
				((BaseMenuActivity) activity).lockKeyInput(true);
			}
		}
	}
	
	protected ViewGroup getParentView(){
		return (ViewGroup) activity.getWindow().getDecorView();
	}
	
	protected void startTutorial(){
		if (gotoNextStep()) {
			ViewGroup parent = getParentView();
			RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
			parent.addView(tutorialView, lp);
		}
	}
	
	protected TutorialParams transformParams(TutorialParams params){
		return params;
	}
	
	public boolean gotoNextStep(){
		TutorialParams params = null;
		for (TutorialSet tutorialSet : mapTutorialSets.values()) {
			if (tutorialSet.isEnable()) {
				params = tutorialSet.getNextTutorialParams();
				break;
			}
		}
		if (params != null) {
			params = transformParams(params);
			tutorialView.setNoticeVisibility(View.VISIBLE);
			tutorialView.setTutorialParams(params);
			return true;
		}
		return false;
	}
	
	public boolean isFinished(){
		TutorialParams params = null;
		for (TutorialSet tutorialSet : mapTutorialSets.values()) {
			if (tutorialSet.isEnable()) {
				params = tutorialSet.getNextTutorialParams();
				break;
			}
		}
		return params == null;
	}

	@Override
	public void onTriger(TutorialParams params) {
		mapTutorialSets.get(params.category).finishCurrentStep();
		if (!gotoNextStep()) {
			stopTutorials();
			if (activity instanceof BaseActivity) {
				((BaseActivity) activity).lockKeyInput(false);
			}else if (activity instanceof BaseMenuActivity) {
				((BaseMenuActivity) activity).lockKeyInput(false);
			}
		}
	}

	@Override
	public void onError(TutorialParams params) {
		
	}
	
	int getStatusBarHeight() {
		Rect rectgle = new Rect();
		Window window = activity.getWindow();
		window.getDecorView().getWindowVisibleDisplayFrame(rectgle);
		return rectgle.top;
	}
	
	public void finishTutorials(){
		for (TutorialSet tutorialSet : mapTutorialSets.values()) {
			tutorialSet.finishTutorial();
		}
		stopTutorials();
	}
	
	public void stopTutorials(){
		if (tutorialView != null && tutorialView.getParent() != null) {
			((ViewGroup)tutorialView.getParent()).removeView(tutorialView);
		}
	}
}
