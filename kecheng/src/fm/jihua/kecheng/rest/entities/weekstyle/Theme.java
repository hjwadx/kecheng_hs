package fm.jihua.kecheng.rest.entities.weekstyle;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;

import com.google.gson.Gson;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.drawable.Drawable;

import fm.jihua.kecheng.App;
import fm.jihua.kecheng.rest.entities.mall.Product;
import fm.jihua.kecheng.rest.entities.mall.ProductItem;
import fm.jihua.kecheng.rest.entities.mall.ThemeProduct;
import fm.jihua.kecheng.utils.Const;
import fm.jihua.kecheng.utils.FileUtils;
import fm.jihua.kecheng.utils.ImageHlp;

/**
 * @date 2013-7-25
 * @introduce WeekView的绘图控制实体类
 */
public class Theme extends ProductItem implements Serializable {

	private static final long serialVersionUID = -4721817606142154571L;

	public String name = "";
	public String iconImage = "";

	public DayIndicatorConfig dayIndicatorConfig;
	public CourseIndicatorConfig courseIndicatorConfig;
	public GridViewConfig gridViewConfig;
	public GridBackgroundConfig gridBackgroundConfig;
	public TimeGridConfig timeGridConfig;
	public LeftTopCornerConfig leftTopCornerConfig;
	
	public int category = CATEGORY_FILE;
	
	public static final int CATEGORY_CUSTOM = 1;
	public static final int CATEGORY_FILE = 2;
	
	//local
	public String folderName;

	public Theme() {
		super();
		init();
	}

	void init() {
		dayIndicatorConfig = new DayIndicatorConfig();
		courseIndicatorConfig = new CourseIndicatorConfig();
		gridViewConfig = new GridViewConfig();
		gridBackgroundConfig = new GridBackgroundConfig();
		timeGridConfig = new TimeGridConfig();
		leftTopCornerConfig = new LeftTopCornerConfig();
	}
	
	public Drawable getIconDrawable(Context context){
		return ImageHlp.getImageFromFileShrinkedByWidthWithRatio(context, FileUtils.getInstance().addPngSuffix(getLocalStoreDir() + iconImage), Const.IPHONE_DISPLAY_WIDTH, 1);
	}
	
	public static Theme getThemeByName(String name) {
		List<Theme> weekStyleListFromAssets = ThemeProduct.getMyLocalItems();
		for (Theme weekStyleBean : weekStyleListFromAssets) {
			if (weekStyleBean.name.equals(name)){
				return weekStyleBean;
			}
		}
		return null;
	}
	
	public static Theme getThemeByNameForAllFolder(int productId){
		return ThemeProduct.getThemeObjectFromFolder(String.valueOf(productId));
	}
	
	public static Theme getDefaultTheme(){
		Theme weekStyleBean = getThemeByName(ThemeProduct.STR_NORMAL_STRING);
		if (weekStyleBean == null) {
			AssetManager assets = App.getInstance().getAssets();
			try {
				String skinJson = FileUtils.getInstance().readStringFromFile(assets.open("skin" + File.separator + "default_skin.json"));
				Gson gson = new Gson();
				weekStyleBean = gson.fromJson(skinJson, Theme.class);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return weekStyleBean;
	}
	
	public static boolean isDefaultTheme(String styleName) {
		return "默认".equals(styleName) || "默认皮肤".equals(styleName);
	}

	@Override
	public String toString() {
		return "WeekStyleBean [name=" + name + ", iconImage=" + iconImage + ", dayIndicatorConfig=" + dayIndicatorConfig + ", courseIndicatorConfig=" + courseIndicatorConfig + ", gridViewConfig="
				+ gridViewConfig + ", gridBackgroundConfig=" + gridBackgroundConfig + ", timeGridConfig=" + timeGridConfig + ", leftTopCornerConfig=" + leftTopCornerConfig + ", category=" + category
				+ "]";
	}
	
	
}
