package fm.jihua.kecheng.rest.entities;

import java.io.Serializable;

/**
 * @date 2013-7-23
 * @introduce 手绘地图申请人数
 */
public class CampusMapApplicants implements Serializable {

	private static final long serialVersionUID = 7879541523922686768L;

	public int applicants;
	public boolean success;

	public CampusMapApplicants(int applicants, boolean success) {
		this.applicants = applicants;
		this.success = success;
	}

	@Override
	public String toString() {
		return "CampusMapApplicants [applicants=" + applicants + ", success=" + success + "]";
	}

}
