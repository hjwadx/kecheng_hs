package fm.jihua.kecheng.rest.entities;


public class PluginBean {
	
	public static final int CATEGORY_LOCAL = 901;
	public static final int CATEGORY_WEB = 902;
//	public static final int CATEGORY_APPSTORE = 903;
	
	public String text;
	public int icon;
	public int icon_uninstall;
	
	//local
	public Class<?> activityClass;
	//web
	public String pakageName;
	public boolean installed = false;
	
	public int category = CATEGORY_LOCAL;
			
	public PluginBean(Class<?> cls, int icon, String text) {
		this.activityClass = cls;
		this.icon = icon;
		this.text = text;
		this.category = CATEGORY_LOCAL;
	}
	
	public PluginBean(String pakageName, int icon, int icon_null, String text) {
		this.pakageName = pakageName;
		this.icon = icon;
		this.icon_uninstall = icon_null;
		this.text = text;
		this.category = CATEGORY_WEB;
	}
	
	
	
}
