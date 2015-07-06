package fm.jihua.kecheng.rest.entities;

import java.io.Serializable;

/**
 * @date 2013-7-25
 * @introduce 课表复制次数
 */
public class Calendar implements Serializable {

	private static final long serialVersionUID = -6115952899554060598L;

	public int copy;

	@Override
	public String toString() {
		return "Calendar [copy=" + copy + "]";
	}

}
