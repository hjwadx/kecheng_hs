package fm.jihua.kecheng.ui.activity.export;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import fm.jihua.kecheng.App;
import fm.jihua.kecheng.rest.entities.Examination;
import fm.jihua.kecheng.ui.activity.BaseActivity;

public class ExchangeActivity extends BaseActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent result = getData();
		setResult(RESULT_OK, result);
		finish();
	}
	
	Intent getData(){
		Intent intent = getIntent();
		Intent result = new Intent();
		String method = intent.getStringExtra("METHOD");
		App app = (App)getApplication();
		if ("EXAMINATIONS".equals(method)) {
			List<Examination> examinations = app.getDBHelper().getExaminations(app.getUserDB());
			result.putExtra("DATA", (ArrayList<Examination>)examinations);
			result.putExtra("TIME", app.getLastEditExaminationTime());
		}
		return result;
	}
}
