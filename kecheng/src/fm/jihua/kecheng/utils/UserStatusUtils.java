package fm.jihua.kecheng.utils;

public class UserStatusUtils {
	
	public enum UserStatus{
		NEW_USER,RELOGIN_USER,COMMON_USER
	}
	
	private UserStatus userStatus = UserStatus.COMMON_USER;
	
	static UserStatusUtils userStatusUtils;
	public static UserStatusUtils get(){
		if (userStatusUtils == null) {
			userStatusUtils = new UserStatusUtils();
		}
		return userStatusUtils;
	}
	
	public boolean isNewUser() {
		return this.userStatus == UserStatus.NEW_USER;
	}
	
	public boolean isReloginUser(){
		return this.userStatus == UserStatus.RELOGIN_USER;
	}

	public void setNewUser(UserStatus userStatus) {
		this.userStatus = userStatus;
	}
}
