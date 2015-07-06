package fm.jihua.kecheng.ui.helper;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;
import fm.jihua.kecheng.utils.Const;

public class Hint extends fm.jihua.common.ui.helper.Hint{
	
	public static void debugHint(final Context cx, final String msg) {
		Log.i(TAG, "Hint:" + msg);
		if(Const.IS_DEBUG_ENABLE){
			if (cx instanceof Activity) {
				((Activity) cx).runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						Toast.makeText(cx, msg, Toast.LENGTH_SHORT).show();
					}
				});
			}
		}
	}
}
