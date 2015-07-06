package fm.jihua.common.ui.helper;

import java.util.ArrayList;

import android.graphics.drawable.Drawable;

/**
 * 该类模拟了功能菜单的数据部分
 */
public class SlideableGridData {
	/** 该常量代表每一屏能够容纳的数目 */
	public static final int NUMBER_IN_ONE_SCREEN = 32;
	public static final int CATEGORY_EMOJI = 101;
	public static final int CATEGORY_HIDDEN = 102;
	public static final int CATEGORY_DELETE = 103;
	public int number = 0;
	public boolean haveDeleteBtn = false;

	/** 该类代表每个应用程序的数据部分 */
	public static class DataItem {
		public String dataName; 
		public Drawable drawable; 
		public int category = CATEGORY_EMOJI;
		public int emojiIndex = -1;
	}
	
	public void setNumber(int number){
		this.number = number;
	}
	
	public int getNumber(){
		if (haveDeleteBtn) {
			int corretNumber = number == 0 ? NUMBER_IN_ONE_SCREEN : number;
			return corretNumber - 1;
		} else {
			return number == 0 ? NUMBER_IN_ONE_SCREEN : number;
		}
	}
	
	public void setDeleteBtn(boolean haveDeleteBtn) {
		this.haveDeleteBtn = haveDeleteBtn;
	}

	/** 该类代表了一个屏的所有应用程序 */
	public static class MenuDataOneScreen {
		public ArrayList<DataItem> mDataItems = new ArrayList<DataItem>();
	}

	/** 该数据时该类的主要部分，所有屏的列表，实际上该类就是代表了所有的屏 */
	ArrayList<MenuDataOneScreen> mScreens = new ArrayList<MenuDataOneScreen>();

	/** 对该类进行赋予数据 */
	public void setMenuItems(ArrayList<DataItem> dataItems) {
		int screenNum = dataItems.size() / getNumber();
		int remain = dataItems.size() % getNumber();
		screenNum += remain == 0 ? 0 : 1;
		int pos = 0;
		if (haveDeleteBtn) {
			for (int i = 0; i < screenNum; i++) {
				MenuDataOneScreen screen = new MenuDataOneScreen();
				for (int j = 0; j < getNumber(); j++) {
					if (pos <= dataItems.size() - 1) {
						DataItem dataItem = dataItems.get(pos);
						dataItem.emojiIndex = pos;
						screen.mDataItems.add(dataItem);
						pos++;
					}else{
						//for hidden button
						DataItem dataItem = new DataItem();
						dataItem.category = CATEGORY_HIDDEN;
						dataItem.drawable = screen.mDataItems.get(screen.mDataItems.size() - 1).drawable;
						screen.mDataItems.add(dataItem);
					}
				}
				//for delete button
				DataItem dataItem = new DataItem();
				dataItem.category = CATEGORY_DELETE;
				screen.mDataItems.add(dataItem);
				mScreens.add(screen);
			}
		} else {
			for (int i = 0; i < screenNum; i++) {
				MenuDataOneScreen screen = new MenuDataOneScreen();
				for (int j = 0; j < getNumber(); j++) {
					if (pos <= dataItems.size() - 1) {
						screen.mDataItems.add(dataItems.get(pos));
						pos++;
					}
				}
				mScreens.add(screen);
			}
		}
		
	}

	/** 获取屏的数目 */
	public int getScreenNumber() {
		return mScreens.size();
	}

	/** 根据屏的索引，获取某个屏的数据 */
	public MenuDataOneScreen getScreen(int screenIndex) {
		return mScreens.get(screenIndex);
	}
}
