package fm.jihua.kecheng.ui.activity.friend;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

import fm.jihua.common.ui.helper.Hint;
import fm.jihua.common.ui.helper.UIUtil;
import fm.jihua.kecheng_hs.R;
import fm.jihua.kecheng.rest.entities.User;
import fm.jihua.kecheng.ui.activity.BaseActivity;
import fm.jihua.kecheng.ui.view.AddFriendsListView;
import fm.jihua.kecheng.ui.view.AddFriendsListView.ResultCallback;
import fm.jihua.kecheng.ui.view.BiasHintView;
import fm.jihua.kecheng.utils.Const;

public class AddFriendsBySearchActivity extends BaseActivity {
	
	final List<User> users = new ArrayList<User>();
	int category;
	AddFriendsListView listView;
	EditText searchBox;
	BiasHintView emptyView;
	
	@SuppressWarnings("serial")
	Map<Integer, String> hash = new HashMap<Integer, String>(){{
		put(Const.RENREN, "人人网");
		put(Const.WEIBO, "新浪微博");
		put(Const.CLASSMATES, "同班的同学");
		put(Const.SEARCH, "查找好友");
		put(Const.SEARCH_BY_GEZI_ID, "查找好友");
	}};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		category = getIntent().getIntExtra("category", Const.RENREN);
		setContentView(R.layout.add_friends_list);
		initTitlebar();
		init();
	}
	
	void initTitlebar(){
		String title = getIntent().getStringExtra("title");
		title = title == null ? hash.get(category) : title;
		setTitle(title);
	}
	
	void init(){
		emptyView = (BiasHintView) findViewById(R.id.empty);
		findViewById(R.id.search_container).setVisibility(View.VISIBLE);
		searchBox = (EditText)findViewById(R.id.search_field);
		if (category == Const.SEARCH_BY_GEZI_ID) {
			emptyView.setText(R.string.empty_search_by_gezi_id);
			searchBox.setInputType(InputType.TYPE_CLASS_NUMBER);
			searchBox.setHint("请输入格子号查找");
			findViewById(R.id.search).setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					if(TextUtils.isEmpty(searchBox.getText().toString())){
						Hint.showTipsShort(AddFriendsBySearchActivity.this, "请输入格子号");
					}else{
						UIUtil.block(AddFriendsBySearchActivity.this);
						listView.showUsers(searchBox.getText().toString().trim());
					}
				}
			});
		}else {
			emptyView.setText(R.string.empty_search_by_name);
			findViewById(R.id.search).setVisibility(View.GONE);
		}
		searchBox.addTextChangedListener(new MyTextWatcher());
		listView = ((AddFriendsListView)findViewById(R.id.user_list));
		listView.setResultCallback(new MyResultCallback());
		listView.init(category);
	}
	
	class MyTextWatcher implements TextWatcher {
		
		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			
		}
		
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
			
		}
		
		@Override
		public void afterTextChanged(Editable s) {
//			UIUtil.block(AddFriendsBySearchActivity.this);
			if (category != Const.SEARCH_BY_GEZI_ID) {
				listView.showUsers(s.toString(), true);
			}
			listView.setVisibility(View.VISIBLE);
			emptyView.setVisibility(View.GONE);
		}
	}
	
	@Override
	public void finish() {
		setResult(RESULT_OK);
		super.finish();
	}
	
	class MyResultCallback implements ResultCallback{

		@Override
		public void onComplete() {
			listView.setVisibility(View.VISIBLE);
			emptyView.setVisibility(View.GONE);
		}

		@Override
		public void onEmpty() {
			listView.setVisibility(View.GONE);
			emptyView.setVisibility(View.VISIBLE);
		}
	}
}
