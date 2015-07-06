package fm.jihua.kecheng.ui.activity.message;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MessageTextUtils {
	public static final Pattern stickerPattern = Pattern.compile("^\\[([^\\[\\]]*)\\]$");
	
	public static String getChatCodeFromMessage(String message){
		String result = null;
		Matcher matcher = stickerPattern.matcher(message);
		if (matcher != null) {
			if (matcher.find()) {
				result = matcher.group(1);
			}
		}
		return result;
	}
	
	public static String unescape(String message){
		if (message != null) {
			message = message.replace("[[", "[").replace("]]", "]");
		}
		return message;
	}
	
	public static String escape(String message){
		if (message != null) {
			message = message.replace("[", "[[").replace("]", "]]");
		}
		return message;
	}
}
