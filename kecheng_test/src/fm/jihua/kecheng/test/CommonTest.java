package fm.jihua.kecheng.test;

import junit.framework.TestCase;
import fm.jihua.kecheng.utils.EncryptionUtil;

public class CommonTest extends TestCase {
	public void testTrue(){
		String text = " hello\n ";
		assertEquals("hello", text.trim());
	}
	
//	public void testJson(){
//		String responseString = "{\"success\":true,\"skins\":[{\"id\":2,\"name\":\"\u6d4b\u8bd5\",\"category\":1,\"size\":19479,\"url\":\"/skins/iphone_0_2?1358403454\",\"thumbnail\":\"/skins/iphone_0_2?1358403454\"}]}";
//		Gson gson = new Gson();
//		SkinsResult result = gson.fromJson(responseString,
//				SkinsResult.class);
//		assertNotNull(result);
//	}
//	
//	public void testEncrypt(){
//		String string = EncryptionUtil.encode("lvdongdong", "asdfghjkl".getBytes());
//		assertEquals("313202", string);
//	}
	
	public void testParseByte2HexStr(){
		assertEquals("313233", EncryptionUtil.parseByte2HexStr("123".getBytes()));
	}
//	
//	public void testRegexp(){
//		String envolope = "POLYGON ((0.0 0.0, 120.0 0.0, 120.0 100.0, 0.0 100.0, 0.0 0.0))";
//		Pattern pattern = Pattern.compile("\\(([^\\(^\\)]*)\\)");
//		Matcher matcher = pattern.matcher(envolope);
//		matcher.find();
//		assertEquals("0.0 0.0, 120.0 0.0, 120.0 100.0, 0.0 100.0, 0.0 0.0", matcher.group(1));
//	}
//	
}