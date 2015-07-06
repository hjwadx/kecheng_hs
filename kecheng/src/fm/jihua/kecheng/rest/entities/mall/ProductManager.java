package fm.jihua.kecheng.rest.entities.mall;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import fm.jihua.kecheng.App;
import fm.jihua.kecheng.rest.service.DataAdapter;
import fm.jihua.kecheng.utils.AppLogger;
import fm.jihua.kecheng.utils.FileUtils;

public class ProductManager {
	
	Class classOfT;
	
	public ProductManager(Class classOfT) {
		this.classOfT = classOfT;
	}
	
	private String getOrderKey(){
		return "Product_" + classOfT.getSimpleName() + "_order";
	}
	
	public List<String> getOrder(){
		Gson gson = new Gson();
		String key = getOrderKey();
		String value = App.getInstance().getValue(key);
		if (TextUtils.isEmpty(value))
			return new ArrayList<String>();
		else {
			return gson.fromJson(value, new TypeToken<List<String>>() {
			}.getType());
		}
	}
	
	public void setOrder(List<String> order){
		String key = getOrderKey();
		Gson gson = new Gson();
		String json = gson.toJson(order);
		App.getInstance().putValue(key, json);
	}
	
	public void sort(List<? extends Product> products){
		final List<String> order = getOrder();
		if (order.size() == 0) {
			Collections.sort(products, new Comparator<Product>() {

				@Override
				public int compare(Product lhs, Product rhs) {
					int lIndex = lhs.exists() ? 1 : -1;
					int rIndex = rhs.exists() ? 1 : -1;
					return rIndex - lIndex;
				}
			});
		}else {
			Collections.sort(products, new Comparator<Product>() {

				@Override
				public int compare(Product lhs, Product rhs) {
					int indexLhs = order.indexOf(lhs.getOrderPropertyValue());
					int indexRhs = order.indexOf(rhs.getOrderPropertyValue());
					if (indexLhs != -1 && indexRhs != -1) {
						return indexLhs - indexRhs;
					} else if (indexLhs == -1 && indexRhs != -1) {
						return 1;
					}
					else if (indexRhs == -1 && indexLhs != -1) {
						return -1;
					}else
						return 0;
				}
			});
		}
	}
	
	public MyProductsResult getMyProductsResult(){
		//TODO using cache;
		MyProductsResult result = App.getInstance().getObjectValue(DataAdapter.MY_PRODUCTS_KEY, MyProductsResult.class);
		if (result != null && result.success) {
			List<StickerSetProduct> stickerProducts = Arrays.asList(result.stickers);
			sort(stickerProducts);
			List<ThemeProduct> themeProducts = Arrays.asList(result.themes);
			sort(themeProducts);
			result.stickers = stickerProducts.toArray(new StickerSetProduct[stickerProducts.size()]);
			result.themes = themeProducts.toArray(new ThemeProduct[themeProducts.size()]);
		}
		return result;
	}
	
	public MyProductsResult getMyLocalProductsResult(){
		MyProductsResult result = getMyProductsResult();
		if (result != null && result.success) {
			List<StickerSetProduct> stickerSetProducts = new ArrayList<StickerSetProduct>();
			for (StickerSetProduct stickerSetProduct : result.stickers) {
				if (stickerSetProduct.exists()) {
					stickerSetProducts.add(stickerSetProduct);
				}
			}
			List<ThemeProduct> themeProducts = new ArrayList<ThemeProduct>();
			for (ThemeProduct themeProduct : result.themes) {
				if (themeProduct.exists()) {
					themeProducts.add(themeProduct);
				}
			}
			result.stickers = stickerSetProducts.toArray(new StickerSetProduct[stickerSetProducts.size()]);
			result.themes = themeProducts.toArray(new ThemeProduct[themeProducts.size()]);
		}
		return result;
	}
	
	protected <ItemT>ItemT getItemFromSD(Class<ItemT> classOfT, String path) {
		ItemT item = null;
		File folderPath = new File(path);
		if (folderPath.exists()) {
			try {
				String pathString = path;
				List<File> files = FileUtils.getInstance().getAllFileBySuffix(pathString, ".json");
				for (File file : files) {
					String skinJson = FileUtils.getInstance().readStringFromFile(new FileInputStream(file));
					if (!TextUtils.isEmpty(skinJson)) {
						Gson gson = new Gson();
						item = (ItemT) gson.fromJson(skinJson, classOfT);
						break;
					}
				}
			} catch (Exception e) {
				AppLogger.printStackTrace(e);
			}
		}
		return item;
	}
}
