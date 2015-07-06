package fm.jihua.kecheng.rest.entities;

import java.io.Serializable;

public class Medal implements Serializable{
	
	private static final long serialVersionUID = -3896170353378127001L;
	public int id;
	public String name;
	public String url;
	public String description;
	
	
	public Medal() {
	}


	public Medal(int id, String name, String url, String description) {
		super();
		this.id = id;
		this.name = name;
		this.url = url;
		this.description = description;
	}

}
