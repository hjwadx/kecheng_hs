package fm.jihua.kecheng.rest.entities;

import java.io.Serializable;
import java.util.LinkedHashMap;

public class RatingResult extends BaseResult implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6140534993877679268L;
	public float score;
	public int num_ratings;
	public LinkedHashMap<String, Integer> tags;
//	JsonObject

	public RatingResult() {

	}
}
