package fm.jihua.kecheng.ui.activity.register;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;

import com.ngohung.widget.ContactListAdapter;

import fm.jihua.kecheng.App;
import fm.jihua.kecheng.rest.entities.City;
import fm.jihua.kecheng.ui.activity.BaseActivity;
import fm.jihua.kecheng.ui.activity.profile.ChooseSchoolActivity;
import fm.jihua.kecheng_hs.R;

public class ChooseCityActivity extends BaseActivity implements TextWatcher {

	private ExampleContactListView listview;
	
	private EditText searchBox;
	private String searchString;
	
	private Object searchLock = new Object();
	boolean inSearchMode = false;
	
	private final static String TAG = "com.ngohung.view.ContactListActivity";
	
	City selectedCity;
	
	List<City> contactList;
	List<City> filterList;
	private SearchListTask curSearchTask = null;
	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.choose_city);
        setTitle("选择城市");
        
//        final ActionBar actionBar = (ActionBar) findViewById(R.id.actionbar);

//		actionBar.setTitle("All Contacts");
//		actionBar.setHomeLogo(R.drawable.ic_launcher);
        
		
		filterList = new ArrayList<City>();
		contactList = App.getInstance().getSchoolDBHelper().getAllCities();
		
		ContactListAdapter adapter = new ContactListAdapter(this, R.layout.choose_city_item, contactList);
		
		listview = (ExampleContactListView) this.findViewById(R.id.listview);
		listview.setFastScrollEnabled(true);
		listview.setAdapter(adapter);
		
		// use this to process individual clicks
		// cannot use OnClickListener as the touch event is overrided by IndexScroller
		// use last touch X and Y if want to handle click for an individual item within the row
		listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView parent, View v, int position,
					long id) {
				List<City> searchList = inSearchMode ? filterList : contactList ;
				City city = selectedCity = searchList.get(position);
				startChooseSchoolActivity(city);
//				float lastTouchX = listview.getScroller().getLastTouchDownEventX();
//				if(lastTouchX < 45 && lastTouchX > -1){
//					Toast.makeText(ChooseCityActivity.this, "User image is clicked ( " + searchList.get(position).getItemForIndex()  + ")", Toast.LENGTH_SHORT).show();
//				}
//				else
//					Toast.makeText(ChooseCityActivity.this, "Nickname: " + searchList.get(position).getItemForIndex() , Toast.LENGTH_SHORT).show();
			}
		});
		
		
		searchBox = (EditText) findViewById(R.id.input_search_query);
		searchBox.addTextChangedListener(this);
    }
    
    void startChooseSchoolActivity(City city){
		Intent intent = new Intent(this, ChooseSchoolActivity.class);
		intent.putExtra(RegisterFragment.INTENT_KEY_CITY_ID, city.id);
		intent.putExtra(RegisterFragment.INTENT_KEY_CLASSES, getIntent().getStringExtra(RegisterFragment.INTENT_KEY_CLASSES));
		intent.putExtra(RegisterFragment.INTENT_KEY_GRADE, getIntent().getStringExtra(RegisterFragment.INTENT_KEY_GRADE));
		startActivityForResult(intent, 0);
	}
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	super.onActivityResult(requestCode, resultCode, data);
    	if (resultCode != RESULT_OK){
			return;
		}
		try {
			data.putExtra(RegisterFragment.INTENT_KEY_CITY_ID, selectedCity.id);
			data.putExtra(RegisterFragment.INTENT_KEY_CITY, selectedCity.name);
			setResult(RESULT_OK, data);
			finish();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
    }

    @Override
	public void afterTextChanged(Editable s) {
		

		searchString = searchBox.getText().toString().trim().toUpperCase();

		if(curSearchTask!=null && curSearchTask.getStatus() != AsyncTask.Status.FINISHED)
		{
			try{
				curSearchTask.cancel(true);
			}
			catch(Exception e){
				Log.i(TAG, "Fail to cancel running search task");
			}
			
		}
		curSearchTask = new SearchListTask();
		curSearchTask.execute(searchString); // put it in a task so that ui is not freeze
    }
    
    
    @Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
    	// do nothing
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		// do nothing
	}
    
	private class SearchListTask extends AsyncTask<String, Void, String> {

		
		
		@Override
		protected String doInBackground(String... params) {
			filterList.clear();
			
			String keyword = params[0];
			
			inSearchMode = (keyword.length() > 0);

			if (inSearchMode) {
				// get all the items matching this
				for (City item : contactList) {
					
					if ((item.name.toUpperCase().indexOf(keyword) > -1) || item.pinyin.toUpperCase().indexOf(keyword) > -1 || item.py.toUpperCase().indexOf(keyword) > -1) {
						filterList.add(item);
					}

				}


			} 
			return null;
		}
		
		protected void onPostExecute(String result) {
			
			synchronized(searchLock)
			{
			
				if(inSearchMode){
					
					ContactListAdapter adapter = new ContactListAdapter(ChooseCityActivity.this, R.layout.choose_city_item, filterList);
					adapter.setInSearchMode(true);
					listview.setInSearchMode(true);
					listview.setAdapter(adapter);
				} else {
					ContactListAdapter adapter = new ContactListAdapter(ChooseCityActivity.this, R.layout.choose_city_item, contactList);
					adapter.setInSearchMode(false);
					listview.setInSearchMode(false);
					listview.setAdapter(adapter);
				}
			}
			
		}
	}
	
    
}
