package fm.jihua.kecheng.ui.activity.tutorial;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.SharedPreferences.Editor;
import fm.jihua.kecheng.App;
import fm.jihua.kecheng.ui.activity.tutorial.TutorialView.TutorialParams;

@SuppressLint("CommitPrefEdits")
public class TutorialSet {
	List<TutorialParams> data;
	String tutorialSetName;

	TutorialSet(String tutorialSetName) {
		this.tutorialSetName = tutorialSetName;
	}

	public boolean isEnable() {
		return getNextStep() < data.size();
	}

	protected int getNextStep() {
		return App.getInstance().getDefaultPreferences()
				.getInt(tutorialSetName, 0);
	}

	public void finishCurrentStep() {
		Editor editor = App.getInstance().getDefaultPreferences().edit();
		editor.putInt(tutorialSetName, getNextStep() + 1);
		App.commitEditor(editor);
	}
	
	public void finishTutorial(){
		while (isEnable()) {
			finishCurrentStep();
		}
	}

	public TutorialParams getNextTutorialParams() {
		int index = getNextStep();
		return data.get(index);
	}

	public void setData(List<TutorialParams> data) {
		this.data = data;
	}
}