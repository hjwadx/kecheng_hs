package fm.jihua.kecheng.rest.entities;

import java.io.Serializable;

public class Leaderboard implements Serializable, Cloneable{
//	{"success":true,"leaderboards":[{"id":1,"title":"全校最给力的课程","scope":1},{"id":2,"title":"全国最难的课程","scope":10}]}
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 9056451270351920088L;
	public int id;
	public String title;
	public String tag_name;
	public Integer state;
	public String metric_name;
	public Integer scope;
	public String icon_name;
	public String icon_url;
	
	public Leaderboard() {

	}

	public Leaderboard(int id, String title, String tag_name, Integer state,
			String metric_name, Integer scope, String icon_name,
			String icon_urlString) {
		super();
		this.id = id;
		this.title = title;
		this.tag_name = tag_name;
		this.state = state;
		this.metric_name = metric_name;
		this.scope = scope;
		this.icon_name = icon_name;
		this.icon_url = icon_urlString;
	}
	
	
	
	

}
