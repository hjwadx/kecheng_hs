/*
    BEEM is a videoconference application on the Android Platform.

    Copyright (C) 2009 by Frederic-Charles Barthelery,
                          Jean-Manuel Da Silva,
                          Nikita Kozlov,
                          Philippe Lago,
                          Jean Baptiste Vergely,
                          Vincent Veronis.

    This file is part of BEEM.

    BEEM is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    BEEM is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with BEEM.  If not, see <http://www.gnu.org/licenses/>.

    Please send bug reports with examples or suggestions to
    contact@beem-project.com or http://dev.beem-project.com/

    Epitech, hereby disclaims all copyright interest in the program "Beem"
    written by Frederic-Charles Barthelery,
               Jean-Manuel Da Silva,
               Nikita Kozlov,
               Philippe Lago,
               Jean Baptiste Vergely,
               Vincent Veronis.

    Nicolas Sadirac, November 26, 2009
    President of Epitech.

    Flavien Astraud, November 26, 2009
    Head of the EIP Laboratory.

 */
package fm.jihua.chat.utils;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import fm.jihua.chat.App;
import fm.jihua.chat.service.BeemService;
import fm.jihua.common.utils.NetCheck;
/**
 * Manage broadcast disconnect intent.
 * 
 * @author nikita
 */
public class BeemBroadcastReceiver extends BroadcastReceiver {

	/** Broadcast intent type. */
	public static final String BEEM_CONNECTION_CLOSED = "BeemConnectionClosed";
	public static final String BEEM_CONNECTION_CREATED = "BeemConnectionCreated";

	/**
	 * constructor.
	 */
	public BeemBroadcastReceiver() {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onReceive(final Context context, final Intent intent) {
		String intentAction = intent.getAction();
		Log.i(getClass().getSimpleName(), intentAction + " received");
		if (intentAction.equals(BEEM_CONNECTION_CLOSED)) {
//			CharSequence message = intent.getCharSequenceExtra("message");
//			Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
			if (context instanceof Activity) {
//				Activity act = (Activity) context;
//				act.finish();
				// The service will be unbinded in the destroy of the activity.
			}
		} else if (intentAction.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
			changeToNetAPN(context);
			if (intent.getBooleanExtra(
					ConnectivityManager.EXTRA_NO_CONNECTIVITY, false)) {
				Log.i(this.getClass().getSimpleName(), "disconnected");
//				Toast.makeText(
//						context,
//						context.getString(R.string.BeemBroadcastReceiverDisconnect),
//						Toast.LENGTH_SHORT).show();
				// context.stopService(new Intent(context, BeemService.class));
			}
			if (BeemConnectivity.isConnected(context)) {
				Log.i(this.getClass().getSimpleName(), "connected");
				App app = (App)context.getApplicationContext();
				if (!app.isConnecting() && !app.isConnected()) {
					if (context instanceof BeemService) {
						Log.i(this.getClass().getSimpleName(), "reconnect");
						((BeemService)context).initConnection();
					}
				}
			}
		}
	}
	
	void changeToNetAPN(Context context){
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetInfo = connectivityManager
				.getActiveNetworkInfo();
		if (activeNetInfo != null) {
			String extra =  activeNetInfo.getExtraInfo() == null ?
					"" : activeNetInfo.getExtraInfo();
			if (extra.toLowerCase().contains("wap")) {
				try {
					NetCheck.checkNetworkInfo(context);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
}
