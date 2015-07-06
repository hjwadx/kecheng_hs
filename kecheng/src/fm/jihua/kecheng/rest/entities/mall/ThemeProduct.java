package fm.jihua.kecheng.rest.entities.mall;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import fm.jihua.kecheng.rest.entities.weekstyle.Theme;


public class ThemeProduct extends Product<Theme> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5499311799588175688L;
	public static final String STR_CUSTOM_STRING = "自定义背景";
	public static final String STR_NORMAL_STRING = "默认皮肤";
	static String[] embedThemeFolders = new String[]{"custom", "default"};
	
	public static List<Theme> getMyLocalItems(){
		//TODO using cache;
		List<ThemeProduct> products = getMyLocalProducts();
		List<Theme> list = new ArrayList<Theme>();
		for (final String folder : embedThemeFolders) {
			Theme theme = getThemeObjectFromFolder(folder);
			if (theme != null) {
				if(STR_CUSTOM_STRING.equals(theme.name)){
					theme.category = Theme.CATEGORY_CUSTOM;
				}
				list.add(theme);
			}
		}
		for (ThemeProduct product : products) {
			list.add(product.getItem());
		}
		return list;
	}
	
	public static Theme getThemeObjectFromFolder(final String folder){
		Theme theme = new ProductManager(ThemeProduct.class).<Theme>getItemFromSD(Theme.class, ThemeProduct.getLocalStoreDir(folder));
		if (theme != null) {
			theme.setProductStoreInfo(new ProductItemStoreInterface() {
				
				@Override
				public String getLocalStoreDir() {
					return ThemeProduct.getLocalStoreDir(folder);
				}
			});
		}
		return theme;
	}
	
	public static List<ThemeProduct> getMyProducts(){
		//TODO using cache;
		MyProductsResult result = new ProductManager(ThemeProduct.class).getMyProductsResult();
		List<ThemeProduct> list = new ArrayList<ThemeProduct>();
		if (result != null && result.success) {
			list.addAll(Arrays.asList(result.themes));
		}
		return list;
	}
	
	public static List<ThemeProduct> getMyLocalProducts(){
		//TODO using cache;
		MyProductsResult result = new ProductManager(ThemeProduct.class).getMyLocalProductsResult();
		List<ThemeProduct> list = new ArrayList<ThemeProduct>();
		if (result != null && result.success) {
			list.addAll(Arrays.asList(result.themes));
		}
		return list;
	}

	public String getOrderPropertyValue(){
		return String.valueOf(name);
	}
}
