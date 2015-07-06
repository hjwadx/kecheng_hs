package fm.jihua.kecheng.ui.view;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import fm.jihua.kecheng_hs.R;
import fm.jihua.kecheng.ui.activity.friend.AddFriendsActivity;


public class NoFriendsView extends LinearLayout {
	BiasHintView emptyView;

	public NoFriendsView(Context context) {
		super(context);
		initViews();
	}

	public NoFriendsView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initViews();
	}
	
	void initViews(){
		setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		inflate(getContext(), R.layout.no_friends, this);
		emptyView = (BiasHintView) findViewById(R.id.empty);
		emptyView.setText(getResources().getString(R.string.empty_my_friends_list));
	}
	
	public void startInviteActivity(){
		getContext().startActivity(new Intent(getContext(), AddFriendsActivity.class));
	}
	
}
