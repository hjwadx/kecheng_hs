package fm.jihua.kecheng.rest.entities;

import java.io.Serializable;

public class Ranking implements Serializable, Cloneable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 876779455628599048L;
	public int school_id;
	public int leaderboard_id;
	public int rank;
	public int diff;
	public String desc;
	public float ranking_value;
	public Course course;
	
	
	public Ranking() {

	}
}
