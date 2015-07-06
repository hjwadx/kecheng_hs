package fm.jihua.kecheng.rest.entities.sticker;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.graphics.drawable.Drawable;
import fm.jihua.kecheng.rest.entities.mall.StickerSetProduct;
import fm.jihua.kecheng.utils.Const;
import fm.jihua.kecheng.utils.FileUtils;
import fm.jihua.kecheng.utils.ImageHlp;

/**
 * @date 2013-7-18
 * @introduce 贴纸
 */
public class Sticker implements Serializable, Cloneable {

	private static final long serialVersionUID = -3061251158792494341L;

	public static final int CATEGORY_CHAT = 1;
	public static final int CATEOGRY_COURSE = 2;
	public static final int CATEGORY_BOTH = 3;

	// public property only use for gson
	public int id = 0;
	
	public String name;
	public int width; // width
	public int height;
	public String chat_code = "";
	public int product_id;

	public int category;
	public boolean lock = false;
	
	//保留screen_x和screen_y，兼容3.0版本
	public int screen_x;
	public int screen_y;

	//local
	public static final int LOCK_ALPHA = 77;

	public Sticker(String imageName, int widthLength, int heightLength) {
		this.name = imageName;
		this.width = widthLength;
		this.height = heightLength;
	}
	
	public Sticker(int id, String name, int width, int height, String chat_code, int product_id, int category, boolean lock) {
		this.id = id;
		this.name = name;
		this.width = width;
		this.height = height;
		this.chat_code = chat_code;
		this.product_id = product_id;
		this.category = category;
		this.lock = lock;
	}
	
	public Drawable getDrawable(Context context) {
		return getDrawable(context, 1);
	}

	public Drawable getDrawable(Context context, int ratio) {
		Drawable drawable = null;
		StickerSetProduct localProduct = StickerSetProduct.getLocalProduct(StickerSetProduct.class, product_id);
		if (localProduct != null) {
			drawable =  ImageHlp.getImageFromFileShrinkedByWidthWithRatio(context, FileUtils.getInstance().addPngSuffix(localProduct.getLocalStoreDir() + name), Const.IPHONE_DISPLAY_WIDTH, ratio);
		}
		return drawable;
	}
	
	public boolean isValidForCategory(int category){
		return (this.category == category || this.category == CATEGORY_BOTH);
	}

//	@Override
//	public boolean equals(Object o) {
//		return this.getPoint().equals(((Paster) o).getPoint());
//	}
	
	public static Sticker findByName(String name) {

		List<StickerSetProduct> products = StickerSetProduct.getMyLocalProducts();
		for (StickerSetProduct product : products) {
			Sticker[] stickers = product.getItem().stickers;
			for (Sticker paster : stickers) {
				if (paster.name.equals(name))
					return paster;
			}
		}
		return null;
	}
	
	public static Map<String, Sticker> getPasterMap2KeyChatCode() {
		Map<String, Sticker> mapChatCode2ImageName = null;
		if (mapChatCode2ImageName == null) {
			mapChatCode2ImageName = new HashMap<String, Sticker>();
			
			List<StickerSet> stickerSets = StickerSetProduct.getMyLocalItems();
			for (StickerSet stickerSet : stickerSets) {
				Sticker[] stickers = stickerSet.stickers;
				for (Sticker paster : stickers) {
					mapChatCode2ImageName.put(paster.chat_code, paster);
				}
			}
		}
		return mapChatCode2ImageName;
	}
	
	public UserSticker clone2UserSticker(){
		return new UserSticker(id, screen_x, screen_y, new Sticker(0, name, width, height, chat_code, product_id, CATEGORY_BOTH, lock));
	}

}
