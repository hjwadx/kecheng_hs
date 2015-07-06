package fm.jihua.kecheng.rest.entities.weekstyle;

/**
 * @date 2013-7-26
 * @introduce
 */
public class GridBackgroundConfig {
	public String backgroundImage = "";
	public String backgroundColor = "245,244,241";
	public String backgroundType = "color";

	public void setBackgroundColor(String backgroundColor) {
		this.backgroundColor = backgroundColor;
	}

	@Override
	public String toString() {
		return "GridBackgroundConfig [backgroundImage=" + backgroundImage + ", backgroundColor=" + backgroundColor + ", backgroundType=" + backgroundType + "]";
	}

}
