package fm.jihua.kecheng.rest.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import fm.jihua.common.utils.CommonUtils;



public class Rating implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public int course_id;
	public float score ;
	public int user_id;
	public int num_ratings;
	public String comment;
	
	public String tagString;
	public String tagsCount;
	
	public Rating(int course_id, float score, String tagString, int user_id,
			String comment, String tagsCount, int num_ratings) {
		super();
		this.course_id = course_id;
		this.score = score;
		this.tagString = tagString;
		this.user_id = user_id;
		this.comment = comment;
		this.num_ratings = num_ratings;
		this.tagsCount = tagsCount;
	}
	
	public Rating(int course_id,RatingResult ratingResult,int user_id,String comment) {
		super();
		this.course_id = course_id;
		this.score = ratingResult.score;
		getStringTagAndCount(ratingResult.tags);
		this.user_id = user_id;
		this.comment = comment;
		this.num_ratings = ratingResult.num_ratings;
	}
	
	public Rating(int course_id, float score, int user_id,String comment, String[] tags) {
		super();
		this.course_id = course_id;
		this.score = score;
		this.user_id = user_id;
		this.comment = comment;
		this.setTags(Arrays.asList(tags));
	}
	
	public RatingResult toRatingResult(){
		RatingResult ratingResult = new RatingResult();
		ratingResult.success = true;
		ratingResult.num_ratings = this.num_ratings;
		ratingResult.score = this.score;
		ratingResult.tags = new LinkedHashMap<String, Integer>();
		List<String> tags = getTags();
		List<String> counts = getTagCount();
		for (int i=0; i<tags.size(); i++) {
			ratingResult.tags.put(tags.get(i), CommonUtils.parseInt(counts.get(i)));
		}
		return ratingResult;
	}
	
	public List<String> getTagCount() {
		List<String> list = new ArrayList<String>();
		String str[] = tagsCount.split(",");
		for(String string:str){
			list.add(string);
		}
		return list;
//		return Arrays.asList(tagsCount.split(","));
	}

	public void setTagCount(List<String> tagcount) {
		this.tagsCount = "";
		for(String str:tagcount){
			this.tagsCount += (str + ",");
		}	
	}

	public List<String> getTags() {
		List<String> list = new ArrayList<String>();
		if (!"".equals(tagString)) {
			String str[] = tagString.split(",");
			for(String string:str){
				list.add(string);
			}
		}
		return list;
//		return Arrays.asList(tagString.split(","));
	}

	public void setTags(List<String> tags) {
		this.tagString = "";
		for(String str:tags){
			this.tagString += (str + ",");
		}		
	}
	
	public void getStringTagAndCount(Map<String, Integer> map) {
		tagsCount = "";
		tagString = "";		
		Set<String> set = map.keySet();
		Iterator<String> iterator = set.iterator();
		if(iterator.hasNext()){
			String key = (String) iterator.next();
			tagsCount += map.get(key);
			tagString += key;
		}
		while (iterator.hasNext()) {
			String key1 = (String) iterator.next();
			tagsCount += (","+map.get(key1));
			tagString += (","+key1);
		}
	}
	
	
	public String[] getTagsCountArray(){
		if (tagsCount != null) {
			String []array = tagsCount.split(",");
			return array;
		}else {
			return new String[0];
		}
	}
}
