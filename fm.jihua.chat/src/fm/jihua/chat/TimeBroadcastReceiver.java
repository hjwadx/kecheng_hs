/*
 * Copyright (C) 2008 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package fm.jihua.chat;

import fm.jihua.chat.service.BeemService;
import fm.jihua.chat.utils.BeemConnectivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * A BroadcastReceiver that listens for updates for the ExampleAppWidgetProvider.  This
 * BroadcastReceiver starts off disabled, and we only enable it when there is a widget
 * instance created, in order to only receive notifications when we need them.
 */
public class TimeBroadcastReceiver extends BroadcastReceiver {
	String TAG = getClass().getSimpleName();
	
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("TimeBroadcastReceiver", "intent=" + intent);
        
        String action = intent.getAction();
        if(action.equals(Intent.ACTION_TIME_TICK)) {
        	if (context instanceof BeemService) {
        		Log.d(TAG, "is beemservice");
        		App app = (App)context.getApplicationContext();
        		Log.d(TAG, "isconnected = "+app.isConnected());
        		if (BeemConnectivity.isConnected(context)) {
        			((BeemService)context).initConnection();
				}
			}
//        	Calendar calendar = Calendar.getInstance();
//        	if (calendar.get(Calendar.MINUTE) % 10 == 0 && calendar.get(Calendar.SECOND) == 0) {
//        		context.sendBroadcast(new Intent(KechengAppWidgetProvider.UPDATE));
//			}
		}
    }
}
