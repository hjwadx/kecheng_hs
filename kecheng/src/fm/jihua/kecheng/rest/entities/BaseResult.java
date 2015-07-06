package fm.jihua.kecheng.rest.entities;

import java.io.Serializable;

public class BaseResult implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 9206409026767910002L;
	public boolean success ;
	public String notice = "";
	
	
	/**
	 * 作为区别是否是从服务器返回的。
	 */
	public boolean finished = true;
	public BaseResult() {
	}
	@Override
	public String toString() {
		return "BaseResult [success=" + success + ", notice=" + notice + ", finished=" + finished + "]";
	}
	
	
}
