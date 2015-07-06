package fm.jihua.kecheng.rest.entities.mall;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import fm.jihua.kecheng.rest.entities.sticker.Sticker;
import fm.jihua.kecheng.rest.entities.sticker.StickerSet;
import fm.jihua.kecheng.rest.entities.sticker.UserSticker;

public class StickerSetProduct extends Product<StickerSet> {
	
	public Sticker[] stickers;
	
	public static List<StickerSet> getMyLocalItems(){
		//TODO using cache;
		List<StickerSetProduct> products = getMyLocalProducts();
		List<StickerSet> list = new ArrayList<StickerSet>();
		for (StickerSetProduct product : products) {
			list.add(product.getItem());
		}
		return list;
	}
	
	public static List<StickerSet> getMyCourseItems(){
		return getMyLocalItemsByCategory(Sticker.CATEOGRY_COURSE);
	}
	
	public static List<StickerSet> getMyChatItems(){
		return getMyLocalItemsByCategory(Sticker.CATEGORY_CHAT);
	}
	
	private static List<StickerSet> getMyLocalItemsByCategory(int category){
		List<StickerSet> all = getMyLocalItems();
		List<StickerSet> list = new ArrayList<StickerSet>();
		for (StickerSet stickerSet : all) {
			if (stickerSet.getStickerCategory() == Sticker.CATEGORY_BOTH || stickerSet.getStickerCategory() == category) {
				list.add(stickerSet);
			}
		}
		return list;
	}
	
	public static List<StickerSetProduct> getMyProducts(){
		//TODO using cache;
		MyProductsResult result = new ProductManager(StickerSetProduct.class).getMyProductsResult();
		List<StickerSetProduct> list = new ArrayList<StickerSetProduct>();
		if (result != null && result.success) {
			list.addAll(Arrays.asList(result.stickers));
		}
		return list;
	}
	
	public static List<StickerSetProduct> getMyLocalProducts(){
		//TODO using cache;
		MyProductsResult result = new ProductManager(StickerSetProduct.class).getMyLocalProductsResult();
		List<StickerSetProduct> list = new ArrayList<StickerSetProduct>();
		if (result != null && result.success) {
			list.addAll(Arrays.asList(result.stickers));
		}
		return list;
	}
	
	@Override
	public StickerSet getItem() {
		StickerSet stickerSet = super.getItem();
		if (this.stickers != null && stickerSet != null) {
			stickerSet.stickers = this.stickers;
		}
		return stickerSet;
	}
}
