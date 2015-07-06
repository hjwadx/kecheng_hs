package fm.jihua.kecheng;



import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class StartupReceiver extends BroadcastReceiver
{
	
	@Override
	public void onReceive(Context context, Intent intent)
	{
		Intent serviceIntent = new Intent(context, BeemService.class);		
		context.startService(serviceIntent);
		AlarmReceiver.schedule(context);
	}
}
