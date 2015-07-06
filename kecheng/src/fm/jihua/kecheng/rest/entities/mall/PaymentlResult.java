package fm.jihua.kecheng.rest.entities.mall;

import fm.jihua.kecheng.rest.entities.BaseResult;

public class PaymentlResult extends BaseResult{
	
	public class Result{
		public String order_id;
		public String notify_url;
	}
	
	public Result result;
	
	public PaymentlResult() {

	}

}
