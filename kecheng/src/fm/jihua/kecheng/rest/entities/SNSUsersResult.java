package fm.jihua.kecheng.rest.entities;

import java.util.List;

import fm.jihua.kecheng.interfaces.SimpleUser;

public class SNSUsersResult {
	public User[] users;
	public User[] friends;
	public User[] not_friends;
	public boolean success;
	public String[] invite_third_part_ids;
	public List<SimpleUser> sns_friends;
	
	public SNSUsersResult() {
		
	}
}
