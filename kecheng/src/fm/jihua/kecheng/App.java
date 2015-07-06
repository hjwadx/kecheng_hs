package fm.jihua.kecheng;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.AssetManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.WindowManager;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.mozillaonline.providers.DownloadManager;
import com.renn.rennsdk.RennClient;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import fm.jihua.common.utils.CommonUtils;
import fm.jihua.common.utils.Compatibility;
import fm.jihua.kecheng.rest.entities.AccountResult;
import fm.jihua.kecheng.rest.entities.Semester;
import fm.jihua.kecheng.rest.entities.User;
import fm.jihua.kecheng.rest.entities.mall.Product;
import fm.jihua.kecheng.rest.entities.mall.ThemeProduct;
import fm.jihua.kecheng.rest.entities.sticker.Sticker;
import fm.jihua.kecheng.rest.entities.sticker.UserSticker;
import fm.jihua.kecheng.ui.activity.SplashActivity;
import fm.jihua.kecheng.ui.activity.message.ChatActivity;
import fm.jihua.kecheng.ui.activity.register.RegisterActivity;
import fm.jihua.kecheng.ui.helper.BackgroundEditor;
import fm.jihua.kecheng.utils.AlarmManagerUtil;
import fm.jihua.kecheng.utils.AppLogger;
import fm.jihua.kecheng.utils.BitmapCache;
import fm.jihua.kecheng.utils.Const;
import fm.jihua.kecheng.utils.CourseHelper;
import fm.jihua.kecheng.utils.DatabaseHelper;
import fm.jihua.kecheng.utils.ImageHlp;
import fm.jihua.kecheng.utils.SchoolDBHelper;
import fm.jihua.kecheng.utils.UserStatusUtils;
import fm.jihua.kecheng.utils.UserStatusUtils.UserStatus;
import fm.jihua.kecheng.utils.ZipUtils;
import fm.jihua.kecheng_hs.R;

@SuppressLint("CommitPrefEdits")
public class App extends fm.jihua.chat.App {
	
	private DatabaseHelper mDBHelper;
	private SchoolDBHelper mSchoolDBHelper;
	private SQLiteDatabase mUserDB;
	IWXAPI wxAPI;
	final String TAG = this.getClass().getSimpleName();
	private ImageLoader mImageLoader;
	private RequestQueue mHttpQueue;
	private RequestQueue mImageHttpQueue;
	public static int mDisplayWidth;
	public static int mDisplayHeight;
	boolean mIsNewUser;
	private DownloadManager mDownloadManager;
	private static Gson gson;
	
	public static int mTimeSlotLength; 
	
	public App() {
		
	}
	

	public static App getInstance(){
		if(gson == null){
			gson = new Gson();
		}
		return (App) _app;
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@Override
	public void onCreate() {
//		if (BuildConfig.DEBUG && Compatibility.isCompatible(9)) {
//			// when you create a new application you can set the Thread and VM Policy
//			StrictMode.ThreadPolicy.Builder builder = new StrictMode.ThreadPolicy.Builder();
//			builder.detectAll().
//				detectDiskReads()
//			   .detectDiskWrites()
//			   .detectNetwork()
//			   .penaltyLog();
//			if (Compatibility.isCompatible(11)) {
//				builder.detectCustomSlowCalls().penaltyFlashScreen();  // API level 11, to use with StrictMode.noteSlowCode
//			}
//			StrictMode.setThreadPolicy(builder.build());
//			StrictMode.VmPolicy.Builder vmBuilder = new StrictMode.VmPolicy.Builder();
//			vmBuilder.detectAll().detectLeakedSqlLiteObjects().penaltyLog();
//			if (Compatibility.isCompatible(11)) {
//				vmBuilder.detectLeakedClosableObjects(); // API level 11
//			}
//			StrictMode.setVmPolicy(vmBuilder.build());
//	     }
		super.onCreate();
		initAtFirst();
//		Parse.initialize(this, "OGSHk5FCnMU3AuGxs9mc5SN7kio6IEUY2iITHpap", "SBLHvg8uAhxpMeroT0wbrD7aiJKlhzS71YSCmFOQ");
//		PushService.subscribe(this, "", RenrenAuthActivity.class);
	}
	
	@Override
	public void initAtFirst(){
		super.initAtFirst();
		mDisplayWidth = Compatibility.getWidth(((WindowManager) getSystemService(WINDOW_SERVICE)).getDefaultDisplay());
		mDisplayHeight = Compatibility.getHeight(((WindowManager) getSystemService(WINDOW_SERVICE)).getDefaultDisplay());
		readDatabase();
		initConfig();
		initAlarmService();
		initFolders();
		initDefaultProducts();
		//没有必要分开不同的文件夹存储cache
		mHttpQueue = Volley.newRequestQueue(this);
		mImageHttpQueue = Volley.newRequestQueue(this);
		mImageLoader = new ImageLoader(mImageHttpQueue, new BitmapCache());
		mTimeSlotLength = getSlotLength();
		wxAPI = WXAPIFactory.createWXAPI(this, Const.WEIXIN_APP_ID, true);
        wxAPI.registerApp(Const.WEIXIN_APP_ID);
        
        mDownloadManager = new DownloadManager(getContentResolver(), getPackageName());
	}
	
	private void initDefaultProducts() {
		File path = new File(Environment.getExternalStorageDirectory() + Product.PRODUCTS_FOLDER_PATH);
		if (path == null || !path.exists()) {
			AssetManager assets = getAssets();
			String mDestPath = path.getPath();
			String string = "products.zip";
			try {
				ZipUtils.unZip(assets.open(string), mDestPath);
			} catch (IOException e) {
				AppLogger.printStackTrace(e);
			}
		}
	}

	public void clearCache(){
		mHttpQueue.getCache().clear();
		mImageHttpQueue.getCache().clear();
	}
	
	public RequestQueue getHttpQueue(){
		return mHttpQueue;
	}
	
	public ImageLoader getImageLoader(){
		return mImageLoader;
	}
	 
	public DownloadManager getDownloadManager(){
		return mDownloadManager;
	}
	
	@Override
	public Class<?> getStartupActivity() {
		return SplashActivity.class;
	}

	@Override
	public Class<?> getChatActivity() {
		return ChatActivity.class;
	}

	@Override
	public int getIcon() {
		return R.drawable.icon_48;
	}
	
	@Override
	public String getChatHost(){
		return Const.CHAT_HOST;
	}
	
	public IWXAPI getWXAPI(){
		return wxAPI;
	}
	
	public long getLastTimeUpdateNotify() {
		SharedPreferences share = this.getSharedPreferences(CONFIG_BASE, MODE_PRIVATE);
		return share.getLong(Const.PREFERENCE_LAST_TIME_NOTIFY, 0);
	}

	public void setLastTimeUpdateNotify(long time) {
		SharedPreferences share = this.getSharedPreferences(CONFIG_BASE,
				MODE_PRIVATE);
		Editor editor = share.edit();
		editor.putLong(Const.PREFERENCE_LAST_TIME_NOTIFY, time);
		commitEditor(editor);
	}
	
	public String getNewestVersion() {
		SharedPreferences share = this.getSharedPreferences(CONFIG_BASE, MODE_PRIVATE);
		return share.getString(Const.PREFERENCE_NEWEST_VERSION_CODE, "");
	}

	public void setNewestVersion(String version) {
		SharedPreferences share = this.getSharedPreferences(CONFIG_BASE,
				MODE_PRIVATE);
		Editor editor = share.edit();
		editor.putString(Const.PREFERENCE_NEWEST_VERSION_CODE, version);
		commitEditor(editor);
	}

	public String getWeiboToken() {
		SharedPreferences share = this.getSharedPreferences(CONFIG_BASE, MODE_PRIVATE);
		return share.getString(Const.PREFERENCE_WEIBO_TOKEN, null);
	}

	public void setWeiboToken(String token) {
		SharedPreferences share = this.getSharedPreferences(CONFIG_BASE,
				MODE_PRIVATE);
		Editor editor = share.edit();
		editor.putString(Const.PREFERENCE_WEIBO_TOKEN, token);
		commitEditor(editor);
	}
	
	public String getWeiboExpires() {
		SharedPreferences share = this.getSharedPreferences(CONFIG_BASE, MODE_PRIVATE);
		return share.getString(Const.PREFERENCE_WEIBO_EXPIRES, null);
	}
	
	public void setWeiboExpires(String expires) {
		SharedPreferences share = this.getSharedPreferences(CONFIG_BASE,
				MODE_PRIVATE);
		Editor editor = share.edit();
		editor.putString(Const.PREFERENCE_WEIBO_EXPIRES, expires);
		commitEditor(editor);
	}
	
	public void logoutWeibo(){
		SharedPreferences share = this.getSharedPreferences(CONFIG_BASE,
				MODE_PRIVATE);
		Editor editor = share.edit();
		editor.remove(Const.PREFERENCE_WEIBO_TOKEN);
		editor.remove(Const.PREFERENCE_WEIBO_ID);
		editor.remove(Const.PREFERENCE_WEIBO_EXPIRES);
		commitEditor(editor);
	}
	
	public String getWeiboId() {
		SharedPreferences share = this.getSharedPreferences(CONFIG_BASE, MODE_PRIVATE);
		return share.getString(Const.PREFERENCE_WEIBO_ID, null);
	}

	public void setWeiboId(String weiboId) {
		SharedPreferences share = this.getSharedPreferences(CONFIG_BASE,
				MODE_PRIVATE);
		Editor editor = share.edit();
		editor.putString(Const.PREFERENCE_WEIBO_ID, weiboId);
		commitEditor(editor);
	}

	public String getTencentToken() {
		SharedPreferences share = this.getSharedPreferences(CONFIG_BASE, MODE_PRIVATE);
		return share.getString(Const.PREFERENCE_TENCENT_TOKEN, null);
	}
	
	public void setTencentToken(String token) {
		SharedPreferences share = this.getSharedPreferences(CONFIG_BASE,
				MODE_PRIVATE);
		Editor editor = share.edit();
		editor.putString(Const.PREFERENCE_TENCENT_TOKEN, token);
		commitEditor(editor);
	}
	
	public Long getTencentExpires() {
		SharedPreferences share = this.getSharedPreferences(CONFIG_BASE, MODE_PRIVATE);
		return share.getLong(Const.PREFERENCE_TENCENT_EXPIRES, 0);
	}
	
	public void setTencentExpires(Long expires_in) {
		SharedPreferences share = this.getSharedPreferences(CONFIG_BASE,
				MODE_PRIVATE);
		Editor editor = share.edit();
		editor.putLong(Const.PREFERENCE_TENCENT_EXPIRES, expires_in);
		commitEditor(editor);
	}
	
	public String getTencentOpenid() {
		SharedPreferences share = this.getSharedPreferences(CONFIG_BASE, MODE_PRIVATE);
		return share.getString(Const.PREFERENCE_TENCENT_OPENID, null);
	}
	
	public void setTencentOpenid(String openid) {
		SharedPreferences share = this.getSharedPreferences(CONFIG_BASE,
				MODE_PRIVATE);
		Editor editor = share.edit();
		editor.putString(Const.PREFERENCE_TENCENT_OPENID, openid);
		commitEditor(editor);
	}
	
	public Boolean getIsInitSkins() {
		SharedPreferences share = this.getSharedPreferences(CONFIG_BASE, MODE_PRIVATE);
		return share.getBoolean(Const.PREFERENCE_IS_SKINS_MOVED, false);
	}
	
	public void setIsInitSkins(Boolean is_moved) {
		SharedPreferences share = this.getSharedPreferences(CONFIG_BASE,
				MODE_PRIVATE);
		Editor editor = share.edit();
		editor.putBoolean(Const.PREFERENCE_IS_SKINS_MOVED, is_moved);
		commitEditor(editor);
	}
	
	public void setWidgetFileUrl(String url) {
		SharedPreferences share = this.getSharedPreferences(CONFIG_BASE,
				MODE_PRIVATE);
		Editor editor = share.edit();
		editor.putString(Const.WEDGET_IMAGE_URL, url);
		commitEditor(editor);
	}
	
	public String getWidgetFileUrl() {
		SharedPreferences share = this.getSharedPreferences(CONFIG_BASE, MODE_PRIVATE);
		return share.getString(Const.WEDGET_IMAGE_URL, null);
	}
	
	public void logoutTencent(){
		SharedPreferences share = this.getSharedPreferences(CONFIG_BASE,
				MODE_PRIVATE);
		Editor editor = share.edit();
		editor.remove(Const.PREFERENCE_TENCENT_TOKEN);
		editor.remove(Const.PREFERENCE_TENCENT_OPENID);
		commitEditor(editor);
	}
	
	public void setActiveSemesterId(int semesterId) {
		SharedPreferences share = this.getSharedPreferences(CONFIG_BASE,
				MODE_PRIVATE);
		Editor editor = share.edit();
		editor.putInt(Const.LOCAL_ACTIVE_SEMESTER, semesterId);
		commitEditor(editor);
	}
	
	public int getActiveSemesterId() {
		SharedPreferences share = this.getSharedPreferences(CONFIG_BASE,
				MODE_PRIVATE);
		return share.getInt(Const.LOCAL_ACTIVE_SEMESTER, 0);
	}
	
	public String getLastNotificationVersion() {
		SharedPreferences share = this.getSharedPreferences(CONFIG_BASE, MODE_PRIVATE);
		return share.getString(Const.PREFERENCE_LAST_NOTIFICATION_VERSION, "0");
	}

	public void setLastNotificationVersion() {
		SharedPreferences share = this.getSharedPreferences(CONFIG_BASE,
				MODE_PRIVATE);
		Editor editor = share.edit();
		editor.putString(Const.PREFERENCE_LAST_NOTIFICATION_VERSION, Const.getAppVersionName(this));
		commitEditor(editor);
	}
	
	public String getToken() {
		SharedPreferences share = this.getSharedPreferences(CONFIG_BASE, MODE_PRIVATE);
		return share.getString(Const.PREFERENCE_TOKEN, null);
	}

	public void setToken(String token) {
		SharedPreferences share = this.getSharedPreferences(CONFIG_BASE,
				MODE_PRIVATE);
		Editor editor = share.edit();
		editor.putString(Const.PREFERENCE_TOKEN, token);
		commitEditor(editor);
	}
	
	public int getSlotLength() {
		String slotLenString = mSettings.getString(getString(R.string.time_slot_length), "12");
		return Integer.parseInt(slotLenString);
	}

	public void setSlotLength(int slotLength) {
		Editor editor = mSettings.edit();
		editor.putString(getString(R.string.time_slot_length), String.valueOf(slotLength));
		commitEditor(editor);
		mTimeSlotLength = slotLength;
	}
	
	public int getBeforeClassTime() {
		return mSettings.getInt(getString(R.string.time_mode_befroe_class_time), 10);
	}

	public void setBeforeClassTime(int time) {
		Editor editor = mSettings.edit();
		editor.putInt(getString(R.string.time_mode_befroe_class_time), time);
		editor.commit();
	}
	
	public int getTimeMuteCategory() {
		return mSettings.getInt(getString(R.string.turn_to_slient), 0);
	}

	public void setTimeMuteCategory(int category) {
		Editor editor = mSettings.edit();
		editor.putInt(getString(R.string.turn_to_slient), category);
		editor.commit();
	}
	
	public List<List<String>> getTimeModeList() {
		String slotLenString = getTimeModeString();
		try {
			@SuppressWarnings("unchecked")
			List<List<String>> list = (List<List<String>>) new Gson().fromJson(slotLenString, new TypeToken<List<List<String>>>() {
			}.getType());
			if (list == null) {
				return new ArrayList<List<String>>();
			} else {
				return list;
			}
		} catch (JsonSyntaxException e) {
			e.printStackTrace();
		}
		return new ArrayList<List<String>>();
	}

	public String getTimeModeString() {
		return mSettings.getString(getString(R.string.time_mode), "");
	}
	
	public void saveTimeModeString(String timeModeString) {
		Editor editor = mSettings.edit();
		editor.putString(getString(R.string.time_mode), timeModeString);
		commitEditor(editor);
	}
	
	public void saveTimeModeList(List<List<String>> list) {
		String json = new Gson().toJson(list);
		saveTimeModeString(json);
	}
	
	public int getPhoneRingerMode() {
		return mSettings.getInt(getString(R.string.ringer_mode), AudioManager.RINGER_MODE_SILENT);
	}
	
	public void setPhoneRingerMode(int ringer_mode) {
		Editor editor = mSettings.edit();
		editor.putInt(getString(R.string.ringer_mode), ringer_mode);
		commitEditor(editor);
	}
	
	public int getPostLastReadId(){
		SharedPreferences share = this.getSharedPreferences(SECRET_POST_READ_DATE, MODE_PRIVATE);
		return share.getInt(Const.PREFERENCE_SECRET_POST_DATE, -1);
	}
	
	public void setPostLastReadId(int postId){
		SharedPreferences share = this.getSharedPreferences(SECRET_POST_READ_DATE,
				MODE_PRIVATE);
		Editor editor = share.edit();
		editor.putInt(Const.PREFERENCE_SECRET_POST_DATE, postId);
		commitEditor(editor);
	}
	
	public int getEventLastLocalId() {
		return mSettings.getInt(getString(R.string.last_local_event_id), -1);
	}

	public void saveEventLastLocalId(int id) {
		Editor editor = mSettings.edit();
		editor.putInt(getString(R.string.last_local_event_id), id);
		commitEditor(editor);
	}
	
	public boolean isVacation(){
//		if (mSettings.contains(getString(R.string.vacation))) {
//			return mSettings.getBoolean(getString(R.string.vacation), false);
//		}else {
//			Semester semester = getDBHelper().getActiveSemester(getUserDB());
//			if (Calendar.getInstance().getTimeInMillis() > semester.begin_time*1000 && Calendar.getInstance().getTimeInMillis() < semester.end_time*1000) {
//				return false;
//			}
//			return true;
//		}
		return false;
	}
	
	public void saveConfigParams(Map<String, String> configParams){
		Editor editor = mSettings.edit();
		for (Entry<String,String> entry : configParams.entrySet()) {
			editor.putString(entry.getKey(), entry.getValue());
		}
		commitEditor(editor);
	}
	
	public boolean isSupportImport(){
		return "true".equals(getConfigParamValue(Const.CONFIG_PARAM_ENABLE_IMPORT));
	}
	
	public boolean isSupportEvent(){
		return "true".equals(getConfigParamValue(Const.CONFIG_PARAM_ENABLE_EVENT));
	}
	
	public String getConfigParamValue(String configParam){
		return mSettings.getString(configParam, "");
	}
	
	public List<String> getUnHandledParams(String configParam){
		String params = mSettings.getString(configParam, "");
		String[] params_array = params.split(",");
		ArrayList<String> unHandled = new ArrayList<String>();
		for (String param : params_array) {
			if (!TextUtils.isEmpty(param) && !mSettings.getBoolean(configParam+ "_" + param, false)) {
				unHandled.add(param);
			}
		}
		return unHandled;
	}
	
	public void handleConfigParam(String parent, String configParam){
		Editor editor = mSettings.edit();
		editor.putBoolean(parent+"_"+configParam, true);
		commitEditor(editor);
	}
	
	public boolean getBooleanSharedValue(String name){
		return mSettings.getBoolean(name, false);
	}
	
	public void putBooleanSharedValue(String name, boolean value){
		Editor editor = mSettings.edit();
		editor.putBoolean(name, value);
		commitEditor(editor);
	}
	
	public void putValue(String name, String value){
		Editor editor = mSettings.edit();
		editor.putString(name, value);;
		commitEditor(editor);
	}
	
	public String getValue(String name){
		return mSettings.getString(name, "");
	}
	
	public <T> T getObjectValue(String name, Class<T> classOfT){
		String json = getValue(name);
		return gson.fromJson(json, classOfT);
	}
	
	//用来检查各种"第一次"  如果是第一次返回true
	public boolean checkIsFirstStatus(String constKey){
		return mSettings.getBoolean(constKey, true);
	}
	
	public void cancleFirstStatus(String constKey){
		Editor editor = mSettings.edit();
		editor.putBoolean(constKey, false);
		commitEditor(editor);
	}
	
	/**
	 * @param truelyWeek
	 * @return currentWeek
	 * 根据起始周和起始周开始时间计算当前周
	 */
	public int getCurrentWeek(boolean truelyWeek) {
		int start_week = mSettings.getInt(getString(R.string.week_past), 0);
		long nextWeekTime = mSettings.getLong(getString(R.string.wakeup_time), 0);
		if (nextWeekTime == 0) {
			Semester semester = getDBHelper().getActiveSemester(getUserDB());
			if(semester == null){
				return 1;
			}
			nextWeekTime = semester.begin_time*1000;
		}
		//开学前有些地方显示第一周的课程
		if (nextWeekTime > Calendar.getInstance().getTimeInMillis() && !truelyWeek && start_week == 0) {
			return 1;
		}else {
			if (nextWeekTime > Calendar.getInstance().getTimeInMillis()) {
				return start_week+1;
			}
			return CourseHelper.getTrueWeek(start_week, nextWeekTime);
		}
	}
	
	public String getCurrentWeekName(){
		if (isVacation()) {
			return "放假中";
		}else {
//			int week = getCurrentWeek(false);
//			return "第"+String.valueOf(week)+"周";
			return CourseHelper.getDay();
		}
	}
	
	/**
	 * @param time
	 * 只在用户自己设置的时候可用
	 */
	public void setWakeupTime(long time){
		Editor editor = mSettings.edit();
		int week = getCurrentWeek(true);
		if (week > 0) {
			editor.putInt(getString(R.string.week_past), week-1);
		}
		editor.putLong(getString(R.string.wakeup_time), time);
		commitEditor(editor);
	}
	
	public void setCurrentWeek(int week){
		if (week > 0) {
			Editor editor = mSettings.edit();
			editor.putInt(getString(R.string.week_past), week-1);
			editor.putLong(getString(R.string.wakeup_time), Calendar.getInstance().getTimeInMillis());
			commitEditor(editor);
		}
	}
	
	public String getBeginDate(){
		int current_week = getCurrentWeek(true);
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(calendar.getTimeInMillis() - 7*24*60*60*1000*(current_week - 1));
		long begin_date = CourseHelper.getFirstDayOfWeek(calendar.getTimeInMillis());
		SimpleDateFormat dateFormat;
		String pattern="yyyy-MM-dd";
		dateFormat=new SimpleDateFormat(pattern);
		return dateFormat.format(new Date(begin_date));
	}
	
	public AccountResult getAccountInfo() {
		SharedPreferences share = this.getSharedPreferences(CONFIG_BASE, MODE_PRIVATE);
		String json = share.getString(Const.PREFERENCE_ACCOUNT_INFO, null);
		if (json != null) {
			Gson gson = new Gson();
			return gson.fromJson(json, AccountResult.class);
		}
		return null;
	}

	public void setAccountInfo(AccountResult account) {
		SharedPreferences share = this.getSharedPreferences(CONFIG_BASE,
				MODE_PRIVATE);
		Gson gson = new Gson();
		Editor editor = share.edit();
		editor.putString(Const.PREFERENCE_ACCOUNT_INFO, gson.toJson(account));
		commitEditor(editor);
	}
	
	public String getDefaultCourseView() {
		//return "day" or "week"
		Object object = mSettings.getAll().get(getString(R.string.default_main_view));
		if (object != null && object instanceof Integer) {
			int value = CommonUtils.parseInt(object.toString());
			Editor editor = mSettings.edit();
			editor.putString(getString(R.string.default_main_view), value == R.layout.week_view ? "week" : "day");
			commitEditor(editor);
		}
		return mSettings.getString(getString(R.string.default_main_view), "week");
	}

	public void setDefaultCourseView(String param) {
		Editor editor = mSettings.edit();
		editor.putString(getString(R.string.default_main_view), param);
		commitEditor(editor);
	}
	
	public boolean isShowAddCourseTutorial() {
		SharedPreferences share = this.getSharedPreferences(CONFIG_BASE, MODE_PRIVATE);
		return share.getBoolean(Const.PREFERENCE_SHOW_TUTORIAL, false);
	}

	public void setShowAddCourseTutorial(boolean shown) {
		SharedPreferences share = this.getSharedPreferences(CONFIG_BASE,
				MODE_PRIVATE);
		Editor editor = share.edit();
		editor.putBoolean(Const.PREFERENCE_SHOW_TUTORIAL, shown);
		commitEditor(editor);
	}

	public boolean isKnowImportCourse() {
		SharedPreferences share = this.getSharedPreferences(CONFIG_BASE, MODE_PRIVATE);
		return share.getBoolean(Const.PREFERENCE_KNOW_IMPORT_COURSE, false);
	}

	public void setKnowImportCourse(boolean known) {
		SharedPreferences share = this.getSharedPreferences(CONFIG_BASE,
				MODE_PRIVATE);
		Editor editor = share.edit();
		editor.putBoolean(Const.PREFERENCE_KNOW_IMPORT_COURSE, known);
		commitEditor(editor);
	}
	
	
	
	public boolean isCreatedShortcut() {
		SharedPreferences share = this.getSharedPreferences(CONFIG_BASE, MODE_PRIVATE);
		return share.getBoolean(Const.PREFERENCE_CREATED_SHORTCUT, false);
	}

	public void setCreatedShortcut(boolean shown) {
		SharedPreferences share = this.getSharedPreferences(CONFIG_BASE,
				MODE_PRIVATE);
		Editor editor = share.edit();
		editor.putBoolean(Const.PREFERENCE_CREATED_SHORTCUT, shown);
		commitEditor(editor);
	}
	
	public boolean isKnonwnSecretPost() {
		SharedPreferences share = this.getSharedPreferences(CONFIG_BASE, MODE_PRIVATE);
		return share.getBoolean(Const.PREFERENCE_KNOW_SECRET_POST, false);
	}

	public void setKnonwnSecretPost(boolean known) {
		SharedPreferences share = this.getSharedPreferences(CONFIG_BASE,
				MODE_PRIVATE);
		Editor editor = share.edit();
		editor.putBoolean(Const.PREFERENCE_KNOW_SECRET_POST, known);
		commitEditor(editor);
	}
	
	public boolean isShared() {
		SharedPreferences share = this.getSharedPreferences(CONFIG_BASE, MODE_PRIVATE);
		return share.getBoolean(Const.PREFERENCE_SHARED, false);
	}

	public void setShared(boolean shared) {
		SharedPreferences share = this.getSharedPreferences(CONFIG_BASE,
				MODE_PRIVATE);
		Editor editor = share.edit();
		editor.putBoolean(Const.PREFERENCE_SHARED, shared);
		commitEditor(editor);
	}
	
	public void saveStickerList(List<UserSticker> pasters) {
		SharedPreferences share = this.getSharedPreferences(CONFIG_BASE, MODE_PRIVATE);
		Editor editor = share.edit();
		Gson gson = new Gson();
		editor.putString(Const.PREFERENCE_STICKER_LIST, gson.toJson(pasters));
		commitEditor(editor);
	}
	
	public void saveStickerList(Set<UserSticker> pasters) {
		saveStickerList(new ArrayList<UserSticker>(pasters));
	}

	public List<UserSticker> getSavedStickerList() {
		SharedPreferences share = this.getSharedPreferences(CONFIG_BASE, MODE_PRIVATE);
		String string = share.getString(Const.PREFERENCE_STICKER_LIST, "");
		List<UserSticker> list = null;
		if (TextUtils.isEmpty(string))
			list = new ArrayList<UserSticker>();
		else {
			try {
				Gson gson = new Gson();
				list = gson.fromJson(string, new TypeToken<ArrayList<UserSticker>>() {
				}.getType());
			} catch (JsonSyntaxException e) {
				e.printStackTrace();
				list = new ArrayList<UserSticker>();
			}
		}

		if (list.size() > 0 && list.get(0).sticker == null) {
			list = getSaveStickerListOld();
		}
		
		return list;
	}
	
	//for 3.0 version
	private List<UserSticker> getSaveStickerListOld() {
		SharedPreferences share = this.getSharedPreferences(CONFIG_BASE, MODE_PRIVATE);
		String string = share.getString(Const.PREFERENCE_STICKER_LIST, "");
		List<Sticker> list = gson.fromJson(string, new TypeToken<ArrayList<Sticker>>() {
		}.getType());
		List<UserSticker> listUserSticker = new ArrayList<UserSticker>();
		for (Sticker sticker : list) {
			UserSticker clone2UserSticker = sticker.clone2UserSticker();
			listUserSticker.add(clone2UserSticker);
		}
		return listUserSticker;
		
	}
	
	public long getLastEditExaminationTime() {
		SharedPreferences share = this.getSharedPreferences(CONFIG_BASE, MODE_PRIVATE);
		return share.getLong(Const.PREFERENCE_LAST_EDIT_EXAMINATION_TIME, 0);
	}

	public void setLastEditExaminationTime(long time) {
		SharedPreferences share = this.getSharedPreferences(CONFIG_BASE,
				MODE_PRIVATE);
		Editor editor = share.edit();
		editor.putLong(Const.PREFERENCE_LAST_EDIT_EXAMINATION_TIME, time);
		commitEditor(editor);
	}
	
	public String getCampusMapInfo() {
		SharedPreferences share = this.getSharedPreferences(CONFIG_BASE, MODE_PRIVATE);
		return share.getString("CAMPUSMAPINFO", "");
	}

	public void setCampusMapInfo(String string) {
		SharedPreferences share = this.getSharedPreferences(CONFIG_BASE, MODE_PRIVATE);
		Editor editor = share.edit();
		editor.putString("CAMPUSMAPINFO", string);
		commitEditor(editor);
	}
	
	public String getLastSchool() {
		SharedPreferences share = this.getSharedPreferences(CONFIG_BASE, MODE_PRIVATE);
		return share.getString("LASTSCHOOL", "");
	}

	public void setLastSchool(String string) {
		SharedPreferences share = this.getSharedPreferences(CONFIG_BASE, MODE_PRIVATE);
		Editor editor = share.edit();
		editor.putString("LASTSCHOOL", string);
		commitEditor(editor);
	}
	
	public boolean getCampusMapHaveApply() {
		SharedPreferences share = this.getSharedPreferences(CONFIG_BASE, MODE_PRIVATE);
		return share.getBoolean("CAMPUSMAPINFOAPPLY", false);
	}

	public void setCampusMapHaveApply() {
		SharedPreferences share = this.getSharedPreferences(CONFIG_BASE, MODE_PRIVATE);
		Editor editor = share.edit();
		editor.putBoolean("CAMPUSMAPINFOAPPLY", true);
		commitEditor(editor);
	}
	
	public int getImportCourseNumber() {
		SharedPreferences share = this.getSharedPreferences(CONFIG_BASE, MODE_PRIVATE);
		return share.getInt("IMPORTCOURSEINFO", 0);
	}

	public void setImportCourseNumber(int number) {
		SharedPreferences share = this.getSharedPreferences(CONFIG_BASE, MODE_PRIVATE);
		Editor editor = share.edit();
		editor.putInt("IMPORTCOURSEINFO", number);
		commitEditor(editor);
	}
	
	public boolean getImportCourseHaveApply() {
		SharedPreferences share = this.getSharedPreferences(CONFIG_BASE, MODE_PRIVATE);
		return share.getBoolean("IMPORTCOURSEAPPLY", false);
	}
	
	public void setImportCourseHaveApply() {
		SharedPreferences share = this.getSharedPreferences(CONFIG_BASE, MODE_PRIVATE);
		Editor editor = share.edit();
		editor.putBoolean("IMPORTCOURSEAPPLY", true);
		commitEditor(editor);
	}
	
	public User getMyself(){
		User myself = new User();
		myself.id = getMyUserId();
		myself.token = getToken();
		myself = getDBHelper().getUser(getUserDB(), myself.id);
		return myself;
	}
	
	public User getUser(String jid){
		return mDBHelper.getUser(mUserDB, User.fromJID(jid).id);
	}

	public void setMyUserId(int id) {
		super.setMyUserId(id);
	}
	
	public void setCourseBlockColors(int[] colors){
		SharedPreferences share = this.getSharedPreferences(CONFIG_BASE, MODE_PRIVATE);
		Gson gson = new Gson();
		Editor editor = share.edit();
		editor.putString(Const.PREFERENCE_COLOR_STRING, gson.toJson(colors));
		commitEditor(editor);
	}
	
	public String getCurrentStyleName() {
		SharedPreferences share = this.getSharedPreferences(CONFIG_BASE, MODE_PRIVATE);
		return share.getString(Const.PREFERENCE_CURRENTSTYLE, ThemeProduct.STR_NORMAL_STRING);
	}
	
	public void setCurrentStyleName(String styleName){
		SharedPreferences share = this.getSharedPreferences(CONFIG_BASE, MODE_PRIVATE);
		Editor editor = share.edit();
		editor.putString(Const.PREFERENCE_CURRENTSTYLE, styleName);
		commitEditor(editor);
	}
	
	public Bitmap getSpecialBgBitmap(int width, int height) {
		Bitmap bmp = null;
		try {
			// if (SkinFragment.CUSTOM_BG.equals(image)) {
			if (BackgroundEditor.CUSTOM_BACKGROUND.exists()) {
				Bitmap bitmap = ImageHlp.decodeFile(BackgroundEditor.CUSTOM_BACKGROUND.getAbsolutePath());
				if (bitmap.getWidth() < width || bitmap.getHeight() < height) {
					bmp = Bitmap.createScaledBitmap(bitmap, width, height, true);
					bitmap.recycle();
				} else {
					bmp = bitmap;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bmp;
	}
	/**
	 * @param activity
	 * @param drawable
	 * @return drawable for week view
	 * 仅用于宽度大于屏幕宽度的图片 
	 */
	public Bitmap changeBitmapForWeekView(final Activity activity, Bitmap fullBmp){
		try {
			int width = Compatibility.getWidth(activity.getWindow()
					.getWindowManager().getDefaultDisplay());
			if (fullBmp == null) {
				// MobclickAgent.reportError(activity,
				// "getSpecialWeekBgDrawable warning:bitmap is null");
			}
			if (fullBmp.getWidth() < width) {
				return fullBmp;
			}
			return Bitmap.createBitmap(fullBmp, fullBmp.getWidth() - width, 0,
					width, fullBmp.getHeight());
		} catch (Exception e) {
			// MobclickAgent.reportError(activity,
			// "getSpecialWeekBgDrawable exception:"+message);
			e.printStackTrace();
			return null;
		}
	}

	public void setSpecialBackground(String image) {
		SharedPreferences share = this.getSharedPreferences(CONFIG_BASE,
				MODE_PRIVATE);
		Editor editor = share.edit();
		editor.putString(Const.PREFERENCE_BACKGROUND_STRING, image);
		commitEditor(editor);
	}
	
	public void logOut(){
		stopService(RegisterActivity.SERVICE_INTENT);
		clearSharedPreference();
		mDBHelper.cleanData(mUserDB);
		UserStatusUtils.get().setNewUser(UserStatus.COMMON_USER);
		logoutWeibo();
		RennClient rennClient = RennClient.getInstance(this);
		rennClient.init(Const.APP_ID, Const.API_KEY, Const.SECRET_KEY);
		rennClient.logout();
	}
	
	private void clearSharedPreference() {
		
		SharedPreferences share = this.getSharedPreferences(CONFIG_BASE, MODE_PRIVATE);
		Editor editor = share.edit();
		Set<String> keySet = share.getAll().keySet();
		for (String string : keySet) {
			editor.remove(string);
		}
		commitEditor(editor);
		
		Set<String> keySetSettings = mSettings.getAll().keySet();
		Editor editSettings = mSettings.edit();
		for (String string : keySetSettings) {
			editSettings.remove(string);
		}
		commitEditor(editSettings);
	}
	
	public void clearInfoByChangeSchool(){
		SharedPreferences share = this.getSharedPreferences(CONFIG_BASE, MODE_PRIVATE);
		Editor editor = share.edit();
		editor.remove("CAMPUSMAPINFO");
		editor.remove("CAMPUSMAPINFOAPPLY");
		commitEditor(editor);
	}
	
	private void readDatabase() {
		try {
			mDBHelper = new DatabaseHelper(this);
			mUserDB = mDBHelper.getWritableDatabase();
			mSchoolDBHelper = new SchoolDBHelper(this);
			mSchoolDBHelper.getReadableDatabase();	//For copy database first time;
		} catch (Exception e) {
			e.printStackTrace();
			Log.e(Const.TAG, "App readDatabse Exception:" + e.getMessage());
		}
	}
	
	private void initConfig(){
		if (!mSettings.contains(getString(R.string.notification))) {
			Editor editor = mSettings.edit();
			editor.putBoolean(getString(R.string.notification), true);
			editor.putString(getString(R.string.notification_time), getNotificationTime());
			editor.putString(getString(R.string.time_slot_length), "12");
			commitEditor(editor);
		} else {
			 String text = mSettings.getString(getString(R.string.notification_time), "");
			 if (!text.contains(":")) {
				int hour = CommonUtils.parseInt(text);
				if (hour < 0) {
					hour += 24;
					text = "-" + hour + ":00";
				}else {
					text = "" + hour + ":00";
				}
				Editor editor = mSettings.edit();
				 editor.putString(getString(R.string.notification_time), text);
				 commitEditor(editor);
			}
		}
		if (getAccountInfo() == null || !getAccountInfo().has_password) {
			Map<String, String> map = new HashMap<String, String>();
			map.put(Const.CONFIG_PARAM_HIGHLIGHT_SETTING, Const.CONFIG_PARAM_HIGHLIGHT_SETTING_PASSWORD);
			saveConfigParams(map);
		}
		Editor editor = mSettings.edit();
		editor.putString(App.NOTIFICATION_SOUND_KEY, Settings.System.DEFAULT_NOTIFICATION_URI.toString());
		editor.putString(App.NOTIFICATION_SOUND_COURSE_WARN_KEY, Settings.System.DEFAULT_NOTIFICATION_URI.toString());
//		editor.putString(App.NOTIFICATION_SOUND_COURSE_WARN_KEY, "android.resource://" + getPackageName() + "/" +R.raw.course_warn);	
		commitEditor(editor);
	}
	
	private String getNotificationTime(){
		String time = "-19:30";
		int i = ((int) (Math.random() * 100))%5;
		switch(i){
		case 0:
			time = "-19:00";
			break;
		case 1:
			time = "-19:15";
			break;
		case 2:
			time = "-19:30";
			break;
		case 3:
			time = "-19:45";
			break;
		case 4:
			time = "-20:00";
			break;
		}
		return time;
	}
	
	public void initAlarmService(){
		if (getActiveSemesterId() != 0) {
			if (isVacation()) {
				WakeupReceiver.schedule(App.this);
				AlarmReceiver.cancel(App.this);
			}else {
				WakeupReceiver.cancel(App.this);
				AlarmReceiver.schedule(App.this);
			}
		}
		
		AlarmManagerUtil.getInstance().resetAlarmByTimeMode();
		AlarmManagerUtil.getInstance().resetAlarmByEvent();
	}
	
	private void initFolders(){
		String path = Environment.getExternalStorageDirectory() + Const.IMAGE_DIR;
		File dir = new File(path);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		String nomedia = Environment.getExternalStorageDirectory() + Const.ROOT_DIR + ".nomedia/";
		File nomedia_dir = new File(nomedia);
		if (!nomedia_dir.exists()) {
			nomedia_dir.mkdirs();
		}
	}
	
	public SQLiteDatabase getUserDB() {
		return mUserDB;
	}
	
	public DatabaseHelper getDBHelper() {
		return mDBHelper;
	}
	
    public SchoolDBHelper getSchoolDBHelper() {
		return mSchoolDBHelper;
	}
	
	public long getPhoneID() {
		long phoneID;
		try {
			/* 获得15位手机IMEI */
			TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
			String strIMEI = tm.getDeviceId();
			phoneID = Long.parseLong(strIMEI);
			if (phoneID == 0) {
				/* 模拟器没有IMEI，随机生成一个99999开头的，辨识是模拟器 */
				Random rand = new Random();
				phoneID = rand.nextInt() + 999990000000000L;
			}
		} catch (Exception e) {
			e.printStackTrace();
			Log.e(Const.TAG, "App generatePhoneID Exception:" + e.getMessage());
			Random rand = new Random();
			phoneID = rand.nextInt() + 888880000000000L;
		}
		return phoneID;
	}
	
	public static String getShareImageFilename(){
		String path = Environment.getExternalStorageDirectory() + Const.IMAGE_DIR;
		File dir = new File(path);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		return path+"share.jpg";
	}
	
	@TargetApi(Build.VERSION_CODES.GINGERBREAD)
	public static void commitEditor(Editor editor){
		if (Compatibility.isCompatible(9)) {
			editor.apply();
		}else {
			editor.commit();
		}
	}

	@Override
	protected PreferenceListener getPreferenceListener(){
    	return new PreferenceListener();
    }
	
	/**
     * A listener for all the change in the preference file. It is used to maintain the global state of the application.
     */
	class PreferenceListener extends fm.jihua.chat.App.PreferenceListener {

		public PreferenceListener() {
		}

		public void onSharedPreferenceChanged(
				SharedPreferences sharedPreferences, String key) {
			super.onSharedPreferenceChanged(sharedPreferences, key);
			if (getString(R.string.notification).equals(key)) {
				if (mSettings.getBoolean(getString(R.string.notification),
						false)) {
					AlarmReceiver.schedule(App.this);
				} else {
					AlarmReceiver.cancel(App.this);
				}
			} else if (getString(R.string.vacation).equals(key)
					|| getString(R.string.wakeup_time).equals(key)) {
				initAlarmService();
			} else if (getString(R.string.notification_time).equals(key)) {
				AlarmReceiver.cancel(App.this);
				AlarmReceiver.schedule(App.this);
			} else if (getString(R.string.time_slot_length).equals(key)) {
				mTimeSlotLength = getSlotLength();
			}
		}
	}
}
