package fm.jihua.kecheng.rest.entities;

import java.io.Serializable;

/**
 * @date 2013-7-23
 * @introduce 手绘地图
 */
public class CampusMap implements Serializable {

	private static final long serialVersionUID = 7879541523922686768L;

	public String map_url;
	public String thumb_url;
	public int applicants_count;
	public String size;
	public boolean success;

	@Override
	public String toString() {
		return "CampusMap [map_url=" + map_url + ", thumb_url=" + thumb_url + ", applicants_count=" + applicants_count + ", size=" + size + ", success=" + success + "]";
	}
}
