package fm.jihua.kecheng.rest.entities.mall;

import java.io.File;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.List;

import android.os.Environment;
import fm.jihua.common.utils.ObjectUtils;
import fm.jihua.kecheng.rest.service.RestService;
import fm.jihua.kecheng.ui.activity.mall.DownloadSpirit;
import fm.jihua.kecheng.utils.AppLogger;
import fm.jihua.kecheng.utils.FileUtils;

/**
 * @author jihua
 *
 * @param <T>
 */
public abstract class Product<T extends ProductItem> implements Serializable, ProductItemStoreInterface{
	
	private static final long serialVersionUID = -1276726205650302116L;
	
	public static final int STICKER_SET = 1;
    public static final int THEME = 2;
    
//	public static final int STATUS_FREE = 1;
//	public static final int STATUS_PAY = 2;
	public static final int STATUS_NEW = 4;
	public static final int STATUS_EVENT = 8;
	public static final int STATUS_PURCHASED = 16;
	
	
	public static final int EXIST = 1;
	public static final int PAY_AND_PAYMENT = 2;
	public static final int PAY_AND_UNPAYMENT = 3;
	public static final int FREE_AND_PAYMENT = 4;
	public static final int FREE_AND_UNPAYMENT = 5;
	
	public int id;
	public String banner_url;
	public String thumb_url;
	public String screen_shot_url;
	public int type;
	public float price;
	public String name;
	public int status;
	public String author;
	public String about_author;
	public String author_icon;
	public String get_way;
	public String summary;
	public String icon_url;
	public String press_icon_url;
	public String introduction;
	
	public static final String PRODUCTS_FOLDER_PATH = "/kecheng_hs/.products/";
	//local used
	public boolean isEditStatus;
	public void resetStatus(){
		this.isEditStatus = false;
	}
	
	public Product() {
		
	}
	
	public Class<T> getClassOfT() {
		return (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
	}
	
	// T must extends Product
	public static <T>T getLocalProduct(Class<T> classOfT, int productId){
		T product = null;
		try {
			product = (T) classOfT.newInstance();
			Field field = classOfT.getField("id");
			field.set(product, productId);
			Method method = classOfT.getMethod("existsInLocal", (Class[])null);
			boolean isExist = (Boolean) method.invoke(product, (Object[])null);
			if (!isExist) {
				product = null;
			}
		} catch (InstantiationException e) {
			AppLogger.printStackTrace(e);
		} catch (IllegalAccessException e) {
			AppLogger.printStackTrace(e);
		} catch (NoSuchFieldException e) {
			AppLogger.printStackTrace(e);
		} catch (IllegalArgumentException e) {
			AppLogger.printStackTrace(e);
		} catch (InvocationTargetException e) {
			AppLogger.printStackTrace(e);
		} catch (NoSuchMethodException e) {
			AppLogger.printStackTrace(e);
		}
		return product;
	}
	
	public boolean getStatus(int status){
		int result = this.status & status;
		return result != 0;
	}
	
	public void addStatus(int status){
		this.status = this.status | status;
	}
	
	public String getPriceString(){
		if(this.getStatus(STATUS_EVENT)){
			return this.summary;
		} else if(price != 0){
			return "￥ " + this.price;
		} else {
			return "免费";
		}
	}
	
	public int getStatusLocal(){
		if(exists()){
			return EXIST;
		} else if(this.getStatus(Product.STATUS_PURCHASED) && price != 0){
			return PAY_AND_PAYMENT;
		} else if(price != 0){
			return PAY_AND_UNPAYMENT;
		} else if(this.getStatus(Product.STATUS_PURCHASED)){
			return FREE_AND_PAYMENT;
		} else {
			return FREE_AND_UNPAYMENT;
		}
	}
	
	public String getLocalStoreDir(){
		return getLocalStoreDir(String.valueOf(id));
	}
	
	public static String getLocalStoreDir(String folder){
		return Environment.getExternalStorageDirectory() + PRODUCTS_FOLDER_PATH + folder + File.separator;
	}
	
	public boolean exists(){
		if (!existsInLocal()) {
			return false;
		}
		//hack because server return error status
		//TODO need be deleted
		if (ObjectUtils.containsElement(new Integer[]{1,2,3,4,5}, id)) {
			return true;
		}
		return getStatus(Product.STATUS_PURCHASED);
	}
	
	public boolean existsInLocal(){
		String pathString = getLocalStoreDir();
		List<File> files = FileUtils.getInstance().getAllFileBySuffix(pathString, ".json");
		File file = new File(pathString);
		return file.exists() && (files != null && files.size() > 0);
	}
	
	public void delete(){
		String pathString = getLocalStoreDir();
		FileUtils.getInstance().deleteFolder(pathString);
	}
	
	public String getOrderPropertyValue(){
		return String.valueOf(id);
	}
	
	
	/**
	 * get Class which extend Product
	 * @return
	 */
//	protected static Class getItemType(){
////		return new Object() { }.getClass().getEnclosingClass();
////		return Thread.currentThread().getStackTrace()[1].getClass();
//		return Product.class;
//	}
	
	public T getItem(){
		return getItemFromSD();
	}
	
	public ProductManager getManager(){
		return (new ProductManager(getClass()));
	}
	
	private T getItemFromSD(){
		T t = getManager().getItemFromSD(getClassOfT(), getLocalStoreDir());
		if(t != null){
			t.setProductStoreInfo(this);
		}
		return t;
	}
	
	public DownloadSpirit getDownloadSpirit(){
		return new DownloadSpirit(RestService.get().getDownloadUrlFromId(id), PRODUCTS_FOLDER_PATH, this);
	}
}
