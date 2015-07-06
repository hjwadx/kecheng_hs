package fm.jihua.kecheng.rest.entities;

import java.io.Serializable;

public class Note implements Serializable {

	private static final long serialVersionUID = -8177487805948253631L;

	public int id;
	public String content;
	public User creator;
	public long created_at;
	
	public Note() {
	}

	public Note(int id, String content, long created_at) {
		super();
		this.id = id;
		this.content = content;
		this.created_at = created_at;
	}
}
