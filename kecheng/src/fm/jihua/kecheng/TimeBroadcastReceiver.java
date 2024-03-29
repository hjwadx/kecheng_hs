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

package fm.jihua.kecheng;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import fm.jihua.kecheng.utils.Const;

/**
 * A BroadcastReceiver that listens for updates for the ExampleAppWidgetProvider.  This
 * BroadcastReceiver starts off disabled, and we only enable it when there is a widget
 * instance created, in order to only receive notifications when we need them.
 */
public class TimeBroadcastReceiver extends fm.jihua.chat.TimeBroadcastReceiver{
	
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("TimeBroadcastReceiver", "intent=" + intent);
        
        String action = intent.getAction();
        if (action.equals(Intent.ACTION_TIMEZONE_CHANGED)
                || action.equals(Intent.ACTION_TIME_CHANGED)) {
        	context.sendBroadcast(new Intent(KechengAppWidgetProvider.UPDATE));
            Log.i(Const.TAG, ""+System.currentTimeMillis());
        }
    }
}
