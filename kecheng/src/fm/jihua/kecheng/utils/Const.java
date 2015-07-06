package fm.jihua.kecheng.utils;

import java.io.File;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.util.Log;
import fm.jihua.common.utils.Compatibility;

public class Const extends fm.jihua.common.utils.Const {
	public static final String API_KEY = "6154fd7f6b8544009431c42d9accc060";
	
	public static final String SECRET_KEY = "89eaa4da27ed40a09decf883c00579e7";
	
	public static final String WEIBO_CONSUMER_KEY = "3595477789";
	public static final String WEIBO_CONSUMER_SECRET = "9709d49f0f9835cac3e998c943feeff0";
	public static final String WEIBO_GEZI_ID = "2807417123";
	
	public static final String TENCENT_APP_ID = "100321004";
	public static final String TENCENT_CALLBACK = "auth://tauth.qq.com/";
	public static final String TENCENT_SCOPE = "get_user_info,get_simple_userinfo,get_user_profile,add_share,add_topic,list_album,upload_pic,add_album";
	public static final int TENCENT_AUTH_ACTIVITY_CODE = 5657;
	
	public static final String RENREN_SCOPE = "read_user_blog read_user_photo read_user_status read_user_album "
			+ "read_user_comment read_user_share publish_blog publish_share "
			+ "send_notification photo_upload status_update create_album "
			+ "publish_comment publish_feed";
	public static final int RENREN_COUNT_PER_REQUEST = 100;
	public static final int WEIBO_COUNT_PER_REQUEST = 200;
	
	public static final String APP_ID = "177210";	//172080, renren
	public static final String WEIXIN_APP_ID = "wx793c32cfe8309108";
	
	public static final String TAG = "KECHENGBIAO";
//	public static final String REST_HOST = "http://kechenggezi.com";
	public static final String REST_HOST = "http://hs.kechenggezi.com";
//	public static final String REST_HOST = "http://stage1.kechenggezi.com";
//	public static final String CHAT_HOST = "cb-stage-cloud.cloudapp.net";
//	public static final String REST_HOST = "http://" + Const.LOCAL_SERVER_HOST + ":3000";
	public static final String CHAT_HOST = "hs-chat.kechenggezi.com";
	public static final Boolean IS_TEST_ENV = !REST_HOST.equalsIgnoreCase("http://hs.kechenggezi.com");
	public static final Boolean IS_DEBUG_ENABLE = Const.IS_TEST_ENV;
	
	public static final String WEDGET_IMAGE_URL = "widget_image_url";
	
	public static final String BUNDLE_KEY_COURSETIME = "BUNDLE_KEY_COURSE_TIME";
	public static final String BUNDLE_KEY_COURSE = "BUNDLE_KEY_COURSE";
	public static final String BUNDLE_KEY_MY_COURSE = "BUNDLE_KEY_MY_COURSE";
	public static final String BUNDLE_KEY_NOTE = "BUNDLE_KEY_NOTE";
	public static final String BUNDLE_KEY_TO_CHAT = "TO_CHAT";
	
	public static final String USER_SERVER_HOST = "kechenggezi.com";	// 外网一直开着的Server，正式环境！！！,119.161.240.61
	public static final String LOCAL_SERVER_HOST = "192.168.1.125";	// 本机,10.0.2.2	
	public static final String PREFERENCE_TOKEN = "token";
	public static final String PREFERENCE_WEIBO_TOKEN = "weibo_token";
	public static final String PREFERENCE_WEIBO_ID = "weibo_id";
	public static final String PREFERENCE_NEWEST_VERSION_CODE = "newest_version_code";
	public static final String PREFERENCE_LAST_TIME_NOTIFY = "last_time_notify";
	public static final String PREFERENCE_WEIBO_EXPIRES = "weibo_expires";
	public static final String PREFERENCE_LAST_NOTIFICATION_VERSION = "last_notification_version";
	public static final String PREFERENCE_BACKGROUND = "background";
	public static final String PREFERENCE_BACKGROUND_STRING = "background_str";
	public static final String PREFERENCE_COLOR_STRING = "color_str";
	public static final String PREFERENCE_CURRENTSTYLE = "current_style";
	public static final String PREFERENCE_CURRENTSTYLE_STATUS = "current_style_status";
	public static final String PREFERENCE_SHOW_TUTORIAL = "show_tutorial";
	public static final String PREFERENCE_KNOW_IMPORT_COURSE = "know_import_course";
	public static final String PREFERENCE_KNOW_SECRET_POST = "know_secret_post";
	public static final String PREFERENCE_CREATED_SHORTCUT = "created_shortcut";
	public static final String PREFERENCE_SHARED = "shared";
	public static final String PREFERENCE_LAST_EDIT_EXAMINATION_TIME = "last_edit_examination_time";
	public static final String PREFERENCE_ACCOUNT_INFO = "account_info";
	public static final String PREFERENCE_CURRENT_WEEK = "current_week";
	public static final String PREFERENCE_SECRET_POST_DATE = "secret_post_date";
	
	public static final String PREFERENCE_TENCENT_TOKEN = "tencent_token";
	public static final String PREFERENCE_TENCENT_OPENID = "tencent_openid";
	public static final String PREFERENCE_TENCENT_EXPIRES = "tencent_expires";
	public static final String PREFERENCE_IS_SKINS_MOVED = "is_skins_moved";
	
	public static final String PREFERENCE_STICKER_LIST = "preference_sticker_list";
	public static final String PREFERENCE_STICKER_DELETE_ID = "preference_sticker_delete_id";
	

	public static final int FILE_IMAGE_MAX_WIDTH = 800;
//	public static final int FILE_IMAGE_MAX_HEIGHT = 800;
	public static final int MENU_MARGIN_WIDTH = 40;
	
	public static final String AVATAR_CACHE_TINY_SUFFIX = "_tiny";
	public static final String AVATAR_CACHE_ORIGIN_SUFFIX = "_origin";
	public static final String AVATAR_CACHE_DRAWABLE_PREFIX= "drawable_";
	public static final String AVATAR_CACHE_EMOJI_PREFIX= "emoji_";
	public static final String AVATAR_CACHE_CUSTOM_BG = "CUSTOM_BACKGROUND";
	public static final String AVATAR_CACHE_CUSTOM_BG_WEEK = "CUSTOM_BACKGROUND_WEEK";
	
	public static final String LOCAL_ACTIVE_SEMESTER = "local_active_semester";
	
	public static final int WEB_VIEWER_WITH_RESULT = 3024;
	public static final int INTENT_DEFAULT = 10000;
	public static final int INTENT_ADD_COURSE = 10001;
	public static final int INTENT_FOR_COMMENT = 10002;
	
	public static final int PER_SLOT_TIME = 60;
	public static final String START_SLOT_TIME = "8:00";
	public static final int MAX_TIME_SLOT = 16;
	public static final int MAX_WEEK = 22;
	
	public static final int UPDATE_NOTIFY_INTERVAL = 7*24*3600*1000;  //7天	
	
	public static final int REGISTER_IMAGE_WIDTH = 450;			//此单位为像素，其他为DP
	public static final int REGISTER_IMAGE_HEIGHT = 450;		//此单位为像素，其他为DP
	
	public static final int IPHONE_DISPLAY_WIDTH = 640;    //iphone屏幕宽度
	
//	public static final int BACKGROUND_IMAGE_WIDTH = 700;			//此单位为像素，其他为DP
//	public static final int BACKGROUND_IMAGE_HEIGHT = 700;			//此单位为像素，其他为DP
	public static final String MY_AVATAR_ID = "my_avatar";
	public static final String ROOT_DIR = "/kecheng_hs/";
	public static final String IMAGE_DIR = "/kecheng_hs/images/";
//	public static final String SKIN_STYLE_FOLDER_PATH = "skin/styles";
//	public static final String SKIN_IMAGE_FOLDER_PATH = "skin/images";
	public static final String DOWNLOAD_FOLDER_PATH = "/kecheng_hs/download/";
	public static final String STR_COURSESRESULT = "CoursesResult";
	
	//用于同一个activity根据不同的intent携带的类型显示不同的内容
	public static final String INTENT_TEXT_CONTENT = "content_text";
	public static final String INTENT_TITLE_CONTENT = "content_title";
	
	//widget type
	public static final int WIDGET_1X4 = 1;
	public static final int WIDGET_3X4 = 2;
	public static final int WIDGET_4X4 = 3;
	
	/* 图片头像设置 */
	public static final int PHOTO_CROPED_WITH_DATA = 13025;
	
	public static final int INVALID_INT_VALUE = -1;
	public static final int FEMALE = 0;
	public static final int MALE = 1;
	
	public static final int ADD_COURSE = 1;
	public static final int FIND_FRIENDS = 2;
	public static final int EAT = 3;
	public static final int SLEEP = 4;
	public static final int GAME = 5;
	public static final int MOVIE = 6;
	public static final int SPORT = 7;
	public static final int MUSIC = 8;
	public static final int STUDY = 9;
	
	/* 离线数据类型 */
	public static final int COURSE_OFFLINE = 1;
	public static final int NOTE_OFFLINE = 2;
	
	//find friend type
	public static final int SEARCH = 0;
	public static final int WEIBO = 1;
	public static final int RENREN = 2;
	public static final int QQ = 3;
	public static final int QQWeibo = 4;
	public static final int CLASSMATES = 100;
	public static final int SEARCH_BY_GEZI_ID = 101;
	public static final int CLASSMATES_GRADE = 102;
	public static final int CLASSMATES_SCHOOL = 103;
	//show users
	public static final int FRIENDS = 110;
	public static final int FREETIME_FRIENDS = 111;
	public static final int FOLLOWER = 112;
	public static final int EVENT_FRIENDS = 113;
	
	public static final int DATA_COUNT_PER_REQUEST = 30;
	
	public static final int SMILES_COUNT_PER_GRID = 21;
	public static final String[] EMOJIS = new String[]{"😄", "😊", "😃", "😉", "😍","😘", "😚", "😳", "😌", "😁", "😜", "😡", "😏", "😓", "😔", "😖", "😥", "😰", "😨", "😣", "😢", "😭", "😂", "😲", "😱", "😠", "👻", "💜", "✌", "👙", "💤", "💏", "❤", "💗", "💘", "💔", "🎅", "🎄", "🎂", "🍰", "👽", "💋", "💎", "🍀", "🌙", "⛄", "📢", "🔒", "🔫", "💰", "🎶", "🏈", "🏀", "⚽", "⚾", "🎾", "🎱", "🍔", "🍟", "🍜", "🍎", "✈", "🚌", "🚙", "🚲", "🌟", "💤", "🎵", "🔥", "💩", "👍", "👎", "👌", "🎎", "🎒", "🎓", "🎆", "🎃", "🎁", "🔔", "📷", "💻", "🈶", "🈚", "🚾", "㊙"};
	public static final String[] CHENGZI_MMS = new String[]{"czmm你好","czmm占座","czmm别挂科","czmm上课中","czmm求真相","czmm奋斗","czmm欺负","czmm必过","czmm全过","czmm加油","czmm喜欢","czmm休息","czmm知道","czmm顶","czmm上课"};
	public static final String[] CHENGZI_MMS_FILES = new String[]{"c00","c01","c02","c03","c04","c05","c06","c07","c08","c09","c10","c11","c12","c13","c14"};
	public static final String[] YANWENZI = new String[]{"┗(＾0＾)┓", "┏(＾0＾)┛", "(×_×;）", "~旦_(^O^ )", "( ´･ω･`)_且~", "ヾ(＾∇＾)", "(⌒▽⌒)☆", "(*°∀°)=3", "(ღ˘⌣˘ღ)", "ლ(´ڡ`ლ)", "(;¬_¬)", "(︶︹︺)", "(┳◇┳)", "凸(｀0´)凸", "(シ_ _)シ", "└(=^‥^=)┐", "〜(￣▽￣〜)", "(〜￣▽￣)〜", 
		"(´ε｀ )♡", "/(*ι*)ヾ", "（ ＴДＴ）", "( p_q)", "ヽ(ﾟДﾟ)ﾉ", "(((( ;°Д°))))", "∑(O_O；)", "(*´∀`*)", "(´▽｀)ノ♪", "(′～`●)", "(′へ`、 )", "(〃′o`)", "( ；′⌒`)", "(⊙ˍ⊙)", "(￣.￣)+", "(￣▽￣)~*", "(→_→)", "<(￣︶￣)>", "(′▽`〃)", "( ′ｏ`)", "ﾍ(･_|", "⊙０⊙", "(=′ー`)", "（*/∇＼*）", 
		"♥（ﾉ´∀`）", "(=′∇`=）", "(*´∀`*)", "(￣へ￣)", "(￣～￣；)", "L(・o・)」", "(oﾟωﾟo)", "(｡・`ω´･)", "(￣0 ￣)y", "Σ(゜゜)", "(σ｀・д･)σ", "(●♡∀♡)", "[]~(￣▽￣)~*", "(￣ˇ￣)", "(￣﹏￣)", "(￣ε(#￣)", "╮(￣▽￣)╭", "∑(っ °Д °;)っ", "Σ( ° △ °|||)︴", "(￣▽￣)\"", "╮(╯▽╰)╭", "(☆▽☆)", 
		"(=￣ω￣=)", "(￣３￣)a", "(。_。)", "(＞﹏＜)", "(￣o￣) . z Z", "m( _ _ )m", "<(￣ ﹌ ￣)>", "<(￣ ﹌ ￣)@m"};
	public static final String[] WEEKS = new String[]{"周一", "周二", "周三", "周四", "周五", "周六", "周日"};
	public static final String[] WEEKS_XINGQI = new String[] { "星期一", "星期二", "星期三", "星期四", "星期五", "星期六", "星期日" };
	public static final String[] SERVER_WEEKS = new String[]{"周日", "周一", "周二", "周三", "周四", "周五", "周六"};
	public static final String[] OCCURANCES = new String[]{"每周", "单周", "双周"};
	public static final String CLASSTIME_DEFAULT = "[[\"08:00\",\"08:50\"],[\"09:00\",\"09:50\"],[\"10:00\",\"10:50\"],[\"11:00\",\"11:50\"],[\"13:00\",\"13:50\"],[\"14:00\",\"14:50\"],[\"15:00\",\"15:50\"],[\"16:00\",\"16:50\"],[\"17:00\",\"17:50\"],[\"19:00\",\"19:50\"],[\"20:00\",\"20:50\"],[\"21:00\",\"21:50\"]]";
	
	public static final String[] NO_SMS_CHANNEL = new String[]{"gfan"};
	public static final String[] NO_RECOMMEND_CHANNEL = new String[]{"sanxing", "anzhi", "wo", "dangle", "leshi", "91"};
	public static final String[] NO_UPDATE_CHANNEL = new String[]{"wo"};
	
	public static final String[] SYSTEM_ACCOUNTS = new String[]{"26", "27"};
	
	public static final int NOTIFICATION_REQUEST_ALARM = 0;
	public static final int NOTIFICATION_REQUEST_EXAMINATION_ALARM = 1000;
	public static final int NOTIFICATION_REQUEST_WAKEUP = 10001;
	
	public static final String CONFIG_PARAM_HIGHLIGHT_RANKING = "leaderboard_highlight";
	public static final String CONFIG_PARAM_ENABLE_IMPORT = "import_enabled";
	public static final String CONFIG_PARAM_ENABLE_EVENT = "is_event_enable";
	public static final String CONFIG_PARAM_HIGHLIGHT_SETTING = "setting_highlight";
	
	public static final String CONFIG_PARAM_HIGHLIGHT_SETTING_PASSWORD = "password";
	public static final String[] CONFIG_PARAMS = new String[]{Const.CONFIG_PARAM_HIGHLIGHT_RANKING, Const.CONFIG_PARAM_ENABLE_IMPORT};
	
	public static final String RECEIVER_ACTION_SHAKE = "stopshake";
	
	public static final String FIRST_EVENTDIALOG = "event_dialog";
	public static final String FIRST_ENTER2TIMEMODE = "enter2timemode";
	public static final String FIRST_SETTING_TIMEMODE_DIALOG = "setting_timemode_dialog";
	
	public static String getChannelName(Context context) {
		String name = "";
		try {
			// ---get the package info---
			PackageManager pm = context.getPackageManager();
			ApplicationInfo appInfo = pm.getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
			
			name = appInfo.metaData.get("UMENG_CHANNEL").toString();
		} catch (Exception e) {
			Log.e(Const.TAG, "Exception", e);
		}
		return name;
	}
	
	/**
     * Get a usable cache directory (external if available, internal otherwise).
     *
     * @param context The context to use
     * @param uniqueName A unique directory name to append to the cache dir
     * @return The cache dir
     */
    public static File getDiskCacheDir(Context context, String uniqueName) {
        // Check if media is mounted or storage is built-in, if so, try and use external cache dir
        // otherwise use internal cache dir
    	try {
    		final String cachePath =
                    Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) ||
                            !isExternalStorageRemovable() ? getExternalCacheDir(context).getPath() :
                                    context.getCacheDir().getPath();

            return new File(cachePath + File.separator + uniqueName);
		} catch (Exception e) {
			e.printStackTrace();
		}
    	return null;
    }
    
    /**
     * Check if external storage is built-in or removable.
     *
     * @return True if external storage is removable (like an SD card), false
     *         otherwise.
     */
    @TargetApi(9)
    public static boolean isExternalStorageRemovable() {
        if (Compatibility.isCompatible(9)) {
            return Environment.isExternalStorageRemovable();
        }
        return true;
    }
    
    /**
     * Get the external app cache directory.
     *
     * @param context The context to use
     * @return The external cache dir
     */
    @TargetApi(8)
    public static File getExternalCacheDir(Context context) {
        if (Compatibility.isCompatible(8)) {
            return context.getExternalCacheDir();
        }

        // Before Froyo we need to construct the external cache dir ourselves
        final String cacheDir = "/Android/data/" + context.getPackageName() + "/cache/";
        return new File(Environment.getExternalStorageDirectory().getPath() + cacheDir);
    }
	
	public static String getSDPath() {
		File sdDir = null;
		boolean sdCardExist = Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED); // 判断sd卡是否存在
		if (sdCardExist) {
			sdDir = Environment.getExternalStorageDirectory();// 获取跟目录
		}
		return sdDir.toString();
	}
	
}
