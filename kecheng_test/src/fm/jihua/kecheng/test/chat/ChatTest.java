package fm.jihua.kecheng.test.chat;

import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;

import android.test.ActivityTestCase;
import fm.jihua.kecheng.ui.activity.message.MessageTextUtils;

@RunWith(PowerMockRunner.class)   // Enable PowerMock's replayAll() and verifyAll() methods.
public class ChatTest extends ActivityTestCase {
	public void testGetChatCodeFromMessageSuccess(){
		String message = "[chat1]";
//		HashMap<String, Sticker> stickerMap = new HashMap<String, Sticker>();
//		Sticker sticker = new Sticker("chat1", 1, 1);
//		stickerMap.put(sticker.name, sticker);
//		EasyMock.expect(Sticker.getPasterMap2KeyChatCode()).andReturn(stickerMap);
//		PowerMock.replayAll();
		String chatCode = MessageTextUtils.getChatCodeFromMessage(message);
		assertEquals("chat1", chatCode);
//		PowerMock.verifyAll();
	}
	
	public void testGetChatCodeFromMessageDueToWrongFormat(){
		String message = "[[chat1]]";
		String chatCode = MessageTextUtils.getChatCodeFromMessage(message);
		assertEquals(null, chatCode);
	}
	
	public void testGetChatCodeFromMessageDueToWrongFormat2(){
		String message = "dfchat1]";
		String chatCode = MessageTextUtils.getChatCodeFromMessage(message);
		assertEquals(null, chatCode);
	}
	
	public void testEscapeNormal(){
		String message = "[chat]";
		assertEquals("[[chat]]", MessageTextUtils.escape(message));
	}
	
	public void testEscapeUsingSepcialCase(){
		String message = "[[chat]";
		assertEquals("[[[[chat]]", MessageTextUtils.escape(message));
	}
	
	public void testUnescapeNormal(){
		String message = "[[chat]]";
		assertEquals("[chat]", MessageTextUtils.unescape(message));
	}
	
	public void testUnescapeUsingSepcialCase(){
		String message = "[[[chat]";
		assertEquals("[[chat]", MessageTextUtils.unescape(message));
	}
}
