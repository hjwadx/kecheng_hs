package fm.jihua.kecheng.rest.entities;

import java.io.Serializable;

public class PushNotification implements Serializable {
	private static final long serialVersionUID = -1507314525756022733L;
	public int id;
	public int notification_id;
	public String message;
	public String action;
	public long created_at;

	
	public PushNotification() {
		// for gson usage
	}

	public PushNotification(int notification_id, String action, String message,
			long created_at) {
		super();
		this.notification_id = notification_id;
		this.message = message;
		this.action = action;
		this.created_at = created_at;
	}
	
	public PushNotification(int id, int notification_id, String action,
			String message, long created_at) {
		this(notification_id, action, message, created_at);
		this.id = id;
	}
	
}
