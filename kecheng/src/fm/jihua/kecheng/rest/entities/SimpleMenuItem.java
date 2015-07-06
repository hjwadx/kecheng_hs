package fm.jihua.kecheng.rest.entities;


public class SimpleMenuItem {
	private int itemId;
	public String title;
	private String highlightText;
	private int iconId;
	private boolean showSetting;
	private boolean showSettingHint;
	
	public int getItemId() {
		return itemId;
	}



	public void setItemId(int itemId) {
		this.itemId = itemId;
	}



	public String getTitle() {
		return title;
	}



	public void setTitle(String title) {
		this.title = title;
	}
	
	public int getIcon() {
		return iconId;
	}

	public void setIcon(int iconId) {
		this.iconId = iconId;
	}

	public void showSettingIcon(){
		this.showSetting = true;
	}
	
	public boolean isShowSettingIcon(){
		return showSetting;
	}
	
	public void showSettingIconHint(boolean isShow){
		this.showSettingHint = isShow;
	}
	
	public boolean getSettingIconHint(){
		return this.showSettingHint;
	}
	
	public SimpleMenuItem(int itemId, String title) {
		super();
		this.itemId = itemId;
		this.title = title;
	}
	
	public SimpleMenuItem(int itemId, String title, String text) {
		this(itemId, title);
		this.highlightText = text;
	}
	
	public SimpleMenuItem(int itemId, String title, String text, int iconId) {
		this(itemId, title, text);
		this.iconId = iconId;
	}

	
	
	@Override
	public String toString() {
		return getTitle();
	}



	public String getHighlightText() {
		return highlightText;
	}



	public void setHighlightText(String text) {
		this.highlightText = text;
	}
	
	
}
