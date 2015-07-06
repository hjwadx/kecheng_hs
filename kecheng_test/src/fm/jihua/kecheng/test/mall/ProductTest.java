package fm.jihua.kecheng.test.mall;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.test.ActivityTestCase;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import fm.jihua.kecheng.rest.entities.mall.MyProductsResult;
import fm.jihua.kecheng.rest.entities.mall.Product;
import fm.jihua.kecheng.rest.entities.mall.ProductAdapter;
import fm.jihua.kecheng.rest.entities.mall.ProductManager;
import fm.jihua.kecheng.rest.entities.mall.StickerSetProduct;
import fm.jihua.kecheng.rest.entities.mall.ThemeProduct;

public class ProductTest extends ActivityTestCase {
	
	String mineProductsJson;
	Gson gson;
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		//type = 1 : 2, type = 2 :2, type = 3 : 1
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("success", true);
		jsonObject.put("stickers", new JSONArray("[{type:1},{type:1}]"));
		jsonObject.put("themes", new JSONArray("[{type:2},{type:2}]"));
		mineProductsJson = jsonObject.toString();
		GsonBuilder gsonBilder = new GsonBuilder();
		gsonBilder.registerTypeAdapter(Product.class, new ProductAdapter());
		gson = gsonBilder.create();
	}
	
	public void testGetMyProductsFromJson(){
		MyProductsResult myProductsResult = gson.fromJson(mineProductsJson, MyProductsResult.class);
		assertEquals(true, myProductsResult.success);
		assertEquals(2, myProductsResult.stickers.length);
		assertEquals(2, myProductsResult.themes.length);
		assertEquals(StickerSetProduct.class.getSimpleName(), myProductsResult.stickers[0].getClass().getSimpleName());
	}
	
	public void testMyProductsToJson() throws JSONException{
		MyProductsResult myProductsResult = new MyProductsResult();
		myProductsResult.success = true;
		myProductsResult.stickers = new StickerSetProduct[2];
		myProductsResult.stickers[0] = new StickerSetProduct();
		myProductsResult.stickers[1] = new StickerSetProduct();
		myProductsResult.themes = new ThemeProduct[1];
		myProductsResult.themes[0] = new ThemeProduct();
		String json = gson.toJson(myProductsResult);
		assertNotNull(json);
		JSONObject object = new JSONObject(json);
		assertEquals(true, object.getBoolean("success"));
		assertEquals(2, object.getJSONArray("stickers").length());
		assertEquals(1, object.getJSONArray("themes").length());
	}
	
	public void testNormalSortProducts(){
		ProductManager manager = new ProductManager(StickerSetProduct.class);
		List<String> order = new ArrayList<String>();
		order.add("1");
		order.add("3");
		order.add("2");
		order.add("4");
		manager.setOrder(order);
		List<StickerSetProduct> products = new ArrayList<StickerSetProduct>();
		StickerSetProduct product = new StickerSetProduct();
		product.id = 1;
		products.add(product);
		product = new StickerSetProduct();
		product.id = 2;
		products.add(product);
		product = new StickerSetProduct();
		product.id = 3;
		products.add(product);
		product = new StickerSetProduct();
		product.id = 4;
		products.add(product);
		manager.sort(products);
		assertEquals(4, products.size());
		assertEquals(1, products.get(0).id);
		assertEquals(3, products.get(1).id);
		assertEquals(2, products.get(2).id);
		assertEquals(4, products.get(3).id);
	}
	
	public void testsortProducts(){
		ProductManager manager = new ProductManager(StickerSetProduct.class);
		List<String> order = new ArrayList<String>();
		order.add("1");
		order.add("3");
		order.add("6");
		order.add("4");
		manager.setOrder(order);
		List<StickerSetProduct> products = new ArrayList<StickerSetProduct>();
		StickerSetProduct product = new StickerSetProduct();
		product.id = 1;
		products.add(product);
		product = new StickerSetProduct();
		product.id = 2;
		products.add(product);
		product = new StickerSetProduct();
		product.id = 3;
		products.add(product);
		product = new StickerSetProduct();
		product.id = 4;
		products.add(product);
		manager.sort(products);
		assertEquals(4, products.size());
		assertEquals(1, products.get(0).id);
		assertEquals(3, products.get(1).id);
		assertEquals(4, products.get(2).id);
		assertEquals(2, products.get(3).id);
	}
	
}
