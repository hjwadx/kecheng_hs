package fm.jihua.kecheng.rest.entities.mall;

import fm.jihua.kecheng.rest.entities.BaseResult;

public class ProductsResult extends BaseResult{
	
	private static final long serialVersionUID = -5660981081247642441L;
	public Banner banner;
	public int[] purchased;
	public StickerSetProduct[] stickers;
	public ThemeProduct[] themes;
	public boolean more_sticker;
	public boolean more_themes;

}
