package fm.jihua.kecheng.rest.entities;

import fm.jihua.chat.service.Message;

public class ContactItem extends User {
	
	private static final long serialVersionUID = -8928287560126185042L;
	public String lastMessage;
	public long lastChatTime;
	public boolean isMessage = false;
	public int unreadCount;
	
	public ContactItem(){
		
	}
	
	public ContactItem(User user){
		this.id = user.id;
		this.name = user.name;
		this.school = user.school;
		this.department = user.department;
		this.grade = user.grade;
		this.sex = user.sex;
		this.renren_id = user.renren_id;
		this.origin_avatar_url = user.origin_avatar_url;
		this.tiny_avatar_url = user.tiny_avatar_url;
	}
	
	public ContactItem (Message message, int unread, User user) {
		this(user);
		this.lastMessage = message.getBody();
		this.lastChatTime = message.getTimestamp().getTime();
		this.isMessage = true;
		this.unreadCount = unread;
	}
}
