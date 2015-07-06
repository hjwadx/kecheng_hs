package fm.jihua.kecheng.rest.entities.weekstyle;

import java.util.Arrays;

/**
 * @date 2013-7-26
 * @introduce
 */
public class TimeGridConfig {
	public String[] gridColors = new String[] { "250, 120, 134", "52, 206, 217", "56, 211, 169", "175, 146, 215", "252, 220, 54", "100, 186, 255", "255, 169, 65", "168, 210, 65", "248, 149, 215" };
	public String textColor = "255,255,255,100";
	public String eventColor = "192,191,188";

	@Override
	public String toString() {
		return "TimeGridConfig [gridColors=" + Arrays.toString(gridColors) + ", textColor=" + textColor + "]";
	}

}
