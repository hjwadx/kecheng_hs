package fm.jihua.kecheng.rest.entities;

import java.io.Serializable;

public class Avatar implements Serializable {
	
	private static final long serialVersionUID = 2077594363359354560L;
	public int id;
	public String tiny_avatar_url;
	public String large_avatar_url;
	public String origin_avatar_url;
	
	public Avatar() {
		
	}
	
	public Avatar(int id, String tiny_avatar_url, String large_avatar_url,
			String origin_avatar_url) {
		super();
		this.id = id;
		this.tiny_avatar_url = tiny_avatar_url;
		this.large_avatar_url = large_avatar_url;
		this.origin_avatar_url = origin_avatar_url;
	}
	
	public boolean equal (Avatar avatar) {	
		return this.id == avatar.id && tiny_avatar_url.equals(avatar.tiny_avatar_url) && large_avatar_url.equals(avatar.large_avatar_url) && origin_avatar_url.equals(avatar.origin_avatar_url);	
	}
	

}
