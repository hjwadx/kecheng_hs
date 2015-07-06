package fm.jihua.kecheng.rest.entities;

import java.io.Serializable;

public class SecretPost implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -918537756483446794L;
	
	public int id;
	public String content;
	public String avatar;
	public String name;
	public int comments_count;
	public long created_at;
	
	public SecretPost(){
		
	}

}
