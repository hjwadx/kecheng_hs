package fm.jihua.kecheng.rest.entities.mall;

import java.io.Serializable;

public class ProductItem implements Serializable{
	ProductItemStoreInterface productStoreInfo;
	
	public ProductItem(){
		
	}
	
	public void setProductStoreInfo(ProductItemStoreInterface productStoreInfo) {
		this.productStoreInfo = productStoreInfo;
	}
	
	public String getLocalStoreDir(){
		String dir = null;
		if (this.productStoreInfo != null) {
			dir = this.productStoreInfo.getLocalStoreDir();
		}
		return dir;
	}
}
