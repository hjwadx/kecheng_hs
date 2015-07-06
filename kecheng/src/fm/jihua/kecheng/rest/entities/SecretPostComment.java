package fm.jihua.kecheng.rest.entities;

import java.io.Serializable;

public class SecretPostComment implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -918537756483446794L;
	
	public int id;
	public String content;
	public String avatar;
	public String name;
	public User user;
	public long created_at;
	public boolean anonymous = true;
	
	//楼层和恢复的评论
	public int floor_num;
	public SecretPostComment reply;
	
	public SecretPostComment(){
		
	}

}
