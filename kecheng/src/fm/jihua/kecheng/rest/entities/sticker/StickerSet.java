package fm.jihua.kecheng.rest.entities.sticker;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import android.content.Context;
import android.graphics.drawable.Drawable;
import fm.jihua.kecheng.rest.entities.mall.ProductItem;
import fm.jihua.kecheng.utils.Const;
import fm.jihua.kecheng.utils.FileUtils;
import fm.jihua.kecheng.utils.ImageHlp;

/**
 * @date 2013-7-31
 * @introduce
 */
public class StickerSet extends ProductItem implements Serializable {

	private static final long serialVersionUID = -878694084939664155L;
	
	public String stickerSetName = "";
	public String normalIconImage = "";
	public String category = "";
	public String selectedIconImage = "";
	public String listIcon="";

	public Sticker[] stickers;
	
	//used local
	private int stickerCategory = 0;
	
	public Drawable getNormalIconDrawable(Context context){
		return ImageHlp.getImageFromFileShrinkedByWidthWithRatio(context, FileUtils.getInstance().addPngSuffix(getLocalStoreDir() + this.normalIconImage), Const.IPHONE_DISPLAY_WIDTH, 1);
	}
	
	public Drawable getSelectedIconDrawable(Context context){
		return ImageHlp.getImageFromFileShrinkedByWidthWithRatio(context, FileUtils.getInstance().addPngSuffix(getLocalStoreDir() + this.selectedIconImage), Const.IPHONE_DISPLAY_WIDTH, 1);
	}
	
	public int getStickerCategory(){
		if (stickerCategory == 0) {
			if (stickers != null && stickers.length > 0) {
				Set<Integer> stickerSet = new HashSet<Integer>();
				for (Sticker sticker : stickers) {
					stickerSet.add(sticker.category);
				}
				if (stickerSet.size() > 1) {
					stickerCategory = Sticker.CATEGORY_BOTH;
				}else {
					stickerCategory = stickerSet.toArray(new Integer[1])[0];
				}
			}else {
				stickerCategory = Sticker.CATEGORY_BOTH;
			}
		}
		return stickerCategory;
	}

	@Override
	public String toString() {
		return "StickerSet [stickerSetName=" + stickerSetName + ", normalIconImage=" + normalIconImage + ", category=" + category + ", selectedIconImage=" + selectedIconImage + ", listIcon="
				+ listIcon + ", stickers=" + Arrays.toString(stickers) + ", stickerCategory=" + stickerCategory + "]";
	}
	
}
