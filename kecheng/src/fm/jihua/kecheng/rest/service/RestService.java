package fm.jihua.kecheng.rest.service;

import java.io.File;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.R.integer;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import fm.jihua.common.utils.HttpUtil;
import fm.jihua.kecheng.App;
import fm.jihua.kecheng.rest.entities.AvatarOrFile;
import fm.jihua.kecheng.rest.entities.BaseResult;
import fm.jihua.kecheng.rest.entities.CampusMap;
import fm.jihua.kecheng.rest.entities.CampusMapApplicants;
import fm.jihua.kecheng.rest.entities.ClassTimeResult;
import fm.jihua.kecheng.rest.entities.Course;
import fm.jihua.kecheng.rest.entities.CourseUnit;
import fm.jihua.kecheng.rest.entities.DepartmentsResult;
import fm.jihua.kecheng.rest.entities.EventResult;
import fm.jihua.kecheng.rest.entities.EventsResult;
import fm.jihua.kecheng.rest.entities.Note;
import fm.jihua.kecheng.rest.entities.NoteResult;
import fm.jihua.kecheng.rest.entities.Rating;
import fm.jihua.kecheng.rest.entities.RegistResult;
import fm.jihua.kecheng.rest.entities.SchoolsResult;
import fm.jihua.kecheng.rest.entities.SearchResult;
import fm.jihua.kecheng.rest.entities.SecretPost;
import fm.jihua.kecheng.rest.entities.SecretPostComment;
import fm.jihua.kecheng.rest.entities.UploadAvatatsResult;
import fm.jihua.kecheng.rest.entities.User;
import fm.jihua.kecheng.rest.entities.UsersResult;
import fm.jihua.kecheng.rest.entities.mall.MyProductsResult;
import fm.jihua.kecheng.rest.entities.mall.PaymentlResult;
import fm.jihua.kecheng.rest.entities.mall.ProductsResult;
import fm.jihua.kecheng.rest.entities.sticker.PasteResult;
import fm.jihua.kecheng.rest.entities.sticker.UserSticker;
import fm.jihua.kecheng.utils.AppLogger;
import fm.jihua.kecheng.utils.Const;

public final class RestService {

	private final Gson gson;
	private final String version;
	private final Locale phoneLocale;
	private static RestService _restService;
	
	public static RestService get(){
		if (_restService == null) {
			_restService = new RestService();
		}
		return _restService;
	}
	
	public RestService() {
		GsonBuilder gsonBuilder = new GsonBuilder();  
		gsonBuilder.setDateFormat("yyyy-MM-dd");
		gson = gsonBuilder.create();
		version = Const.getAppVersionName(App.getInstance());
		phoneLocale = Locale.getDefault();
	}
	
	public String getAuthUrl(String url){
		url += url.contains("?") ? "&" : "?";
		if (App.getInstance().getToken() != null) {
			try {
				url += "token=" + URLEncoder.encode(App.getInstance().getToken(), "UTF-8");
			} catch (UnsupportedEncodingException e) {
				AppLogger.printStackTrace(e);
			}
		}
		return url;
	}
	
	public String buildRequestUrl(String url){
		url = getAuthUrl(url);
		if (Locale.CHINA.equals(phoneLocale)) {
			url += "&locale=ch";
		}
		url += "&from_app=true";
		if (version != null) {
			url += "&version="+version;
		}
		return url;
	}
	
	public String getDownloadUrlFromId(int id){
		return buildRequestUrl(Const.REST_HOST + "/mall/download.json?id=" + id);
	}
	
	public String getDownloadStickerUrlFromChatCode(String chat_code){
		if(TextUtils.isEmpty(chat_code)){
			return "";
		}
		try {
			return buildRequestUrl(Const.REST_HOST + "/mall/chat_sticker.json?chat_code=" + URLEncoder.encode(chat_code,"UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return "";
		}
	}
	
	public String getDownloadThemeUrlFromName(String name){
		if(TextUtils.isEmpty(name)){
			return "";
		}
		try {
			return buildRequestUrl(Const.REST_HOST + "/mall/download_theme.json?name=" + URLEncoder.encode(name,"UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return "";
		}
	}
	
	public RestEntity getCourses() {
		String url = Const.REST_HOST + "/mobile/courses.json";
		return new RestEntity(url);
	}
	
	public RestEntity getCourses(int userId) {
		String url = Const.REST_HOST + "/mobile/courses.json?user_id="+userId;
		return new RestEntity(url);
	}
	
	public RestEntity getCourses(int userId, int semesterId) {
		String url = Const.REST_HOST + "/mobile/courses.json?user_id="+userId+"&semester_id="+semesterId;
		return new RestEntity(url);
	}
	
	public RestEntity pasteSticker(UserSticker userSticker) {
		String url = Const.REST_HOST + "/mobile/paste_sticker.json";
		JSONObject data = new JSONObject();
		try {
			data.put("name", userSticker.sticker.name);
			data.put("screen_x", userSticker.screen_x);
			data.put("screen_y", userSticker.screen_y);
		} catch (JSONException e) {
			AppLogger.printStackTrace(e);
		}
		return new RestEntity(RestEntity.POST, url, data, PasteResult.class);
	}

	public RestEntity modifySticker(UserSticker userSticker) {
		String url = Const.REST_HOST + "/mobile/modify_sticker.json";
		JSONObject data = new JSONObject();
		try {
			data.put("id", userSticker.id);
			data.put("screen_x", userSticker.screen_x);
			data.put("screen_y", userSticker.screen_y);
		} catch (JSONException e) {
			AppLogger.printStackTrace(e);
		}
		return new RestEntity(RestEntity.POST, url, data, BaseResult.class);
	}

	public RestEntity removeSticker(UserSticker userSticker) {
		String url = Const.REST_HOST + "/mobile/remove_sticker.json";
		JSONObject data = new JSONObject();
		try {
			data.put("id", userSticker.id);
		} catch (JSONException e) {
			AppLogger.printStackTrace(e);
		}
		return new RestEntity(RestEntity.POST, url, data, BaseResult.class);
	}

	public RestEntity changeTheme(String theme_name) {
		String url = Const.REST_HOST + "/mobile/change_theme.json";
		JSONObject data = new JSONObject();
		try {
			data.put("theme_name", theme_name);
		} catch (JSONException e) {
			AppLogger.printStackTrace(e);
		}
		return new RestEntity(RestEntity.POST, url, data);
	}
	
	
	
	public RestEntity getGraph(List<String> thirdPartIds, int category, int page, int limit) {
		String url = Const.REST_HOST + "/mobile/graph.json";
		JSONObject data = new JSONObject();
		try {
			data.put("third_part_ids", new JSONArray(thirdPartIds));
			data.put("category", category);
			data.put("page", page);
			data.put("per_page", limit);
		} catch (Exception e) {
			AppLogger.printStackTrace(e);
		}
		return new RestEntity(RestEntity.POST, url, data);
	}
	
	public RestEntity getStudents(int courseId){
		String url = Const.REST_HOST
				+ "/courses/" + courseId + "/students.json";
		return new RestEntity(url);
	}
	
	public RestEntity getNotes(int courseId){
		String url = Const.REST_HOST
				+ "/courses/" + courseId + "/notes.json";
		return new RestEntity(url);
	}
	
	public RestEntity getStudents(int courseId, int page, int limit){
		String url = Const.REST_HOST
				+ "/courses/" + courseId + "/students.json?page="+page+"&per_page="+limit;
		return new RestEntity(url);
	}
	
	public RestEntity getFreeTimeFriends(int day_of_week, int time_slot, int page, int limit){
//		String url = Const.REST_HOST
//				+ "/free_time_friends.json?day_of_week=" + day_of_week + "&time_slot=" + time_slot;
		String url = Const.REST_HOST + "/free_time_friends.json?day_of_week=" 
				+ day_of_week + "&time_slot=" + time_slot + "&page=" + page + "&per_page=" + limit;
		return new RestEntity(url);
	}
	
	public RestEntity getFriends(int page, int limit){
		String url = Const.REST_HOST
				+ "/friends.json?page="+page+"&per_page="+limit;
		return new RestEntity(url);
	}
	
	public RestEntity addFriend(int userId){
		JSONObject data = new JSONObject();
		String url = Const.REST_HOST+ "/add_friend.json";
		try {
			data.put("id", userId);
		} catch (JSONException e) {
			AppLogger.printStackTrace(e);
		}
		return new RestEntity(RestEntity.POST, url, data);
	}
	
	public RestEntity removeFriend(int userId){
		JSONObject data = new JSONObject();
		String url = Const.REST_HOST+ "/remove_friend.json";
		try {
			data.put("id", userId);
		} catch (JSONException e) {
			AppLogger.printStackTrace(e);
		}
		return new RestEntity(RestEntity.POST, url, data);
	}
	
	public RestEntity getNotesUnsafe(int courseId){
		String url = Const.REST_HOST
				+ "/courses/" + courseId + "/notes.json";
		return new RestEntity(url);
	}
	
	public RestEntity createSecretPost(SecretPost post){
		JSONObject data = new JSONObject();
		String url = Const.REST_HOST+ "/secret_posts.json";
		try {
			data.put("secret_post", new JSONObject(gson.toJson(post)));
		} catch (JSONException e) {
			AppLogger.printStackTrace(e);
		}
		return new RestEntity(RestEntity.POST, url, data);
	}
	public RestEntity createSecretPostComment(int postId, SecretPostComment comment, int replyId){
		JSONObject data = new JSONObject();
		String url = Const.REST_HOST+ "/secret_posts/"+ postId +"/add_comment.json";
		try {
			data.put("secret_post_comment", new JSONObject(gson.toJson(comment)));
			if(replyId != 0){
				data.put("reply", replyId);
			}
		} catch (JSONException e) {
			AppLogger.printStackTrace(e);
		}
		return new RestEntity(RestEntity.POST, url, data);
	}
	public RestEntity getSecretPosts(int page, int limit){
		String url = Const.REST_HOST
				+ "/secret_posts.json?page="+page+"&per_page="+limit;
		return new RestEntity(url);
	}
	
	public RestEntity getSecretPostComments(int postId, int page, int limit){
		String url = Const.REST_HOST
				+ "/secret_posts/"+ postId +"/comments.json?page="+page+"&per_page="+limit;
		return new RestEntity(url);
	}
	
	public RestEntity reportImportAddress(String address, boolean is_on_web) {
		JSONObject data = new JSONObject();
		String url = Const.REST_HOST+ "/courses/report_import_address.json";
		try {
			data.put("address", address);
			data.put("is_on_web", is_on_web);
		} catch (JSONException e) {
			AppLogger.printStackTrace(e);
		}
		return new RestEntity(RestEntity.POST, url, data, BaseResult.class);
	}
	
	public RestEntity reportCoursesErrorFeedback(String error_content, boolean courses_error, boolean lost_courses, boolean semester_error) {
		JSONObject data = new JSONObject();
		String url = Const.REST_HOST+ "/courses/report_courses_error_feedback.json";
		try {
			data.put("detail", error_content);
			data.put("courses_error", courses_error);
			data.put("lost_courses", lost_courses);
			data.put("semester_error", semester_error);
		} catch (JSONException e) {
			AppLogger.printStackTrace(e);
		}
		return new RestEntity(RestEntity.POST, url, data, BaseResult.class);
	}

	public RestEntity createNote(int courseId, String content) {
		JSONObject data = new JSONObject();
		String url = Const.REST_HOST+ "/notes.json";
		try {
			JSONObject noteData = new JSONObject();
			noteData.put("course_id", courseId);
			noteData.put("content", content);
			data.put("note", noteData);
		} catch (JSONException e) {
			AppLogger.printStackTrace(e);
		}
		return new RestEntity(RestEntity.POST, url, data, Note.class);
	}
	
	public RestEntity saveNote(Note note){
		JSONObject data = new JSONObject();
		String url = Const.REST_HOST+ "/notes/"+note.id+".json";
		try {
			data.put("id", note.id);
			JSONObject noteData = new JSONObject();
			noteData.put("content", note.content);
			data.put("note", noteData);
		} catch (JSONException e) {
			AppLogger.printStackTrace(e);
		}
		return new RestEntity(RestEntity.PUT, url, data, NoteResult.class);
	}
	
	public RestEntity removeNote(int noteId){
		JSONObject data = new JSONObject();
		String url = Const.REST_HOST+ "/remove_note/"+noteId+".json";
		try {
			JSONObject noteData = new JSONObject();
			noteData.put("id", noteId);
		} catch (JSONException e) {
			AppLogger.printStackTrace(e);
		}
		return new RestEntity(RestEntity.POST, url, data);
	}
	
	public RestEntity createCourse(Course course, List<CourseUnit> course_units){
		JSONObject data = new JSONObject();
		String url = Const.REST_HOST+ "/courses/create.json";
		try {
			data.put("course", new JSONObject(gson.toJson(course)));
			data.put("course_units", new JSONArray(gson.toJson(course_units)));
		} catch (JSONException e) {
			AppLogger.printStackTrace(e);
		}
		return new RestEntity(RestEntity.POST, url, data);
	}
	
	public RestEntity updateCourse(Course course, List<CourseUnit> course_units){
		JSONObject data = new JSONObject();
		String url = Const.REST_HOST+ "/courses/update.json";
		try {
			data.put("course", new JSONObject(gson.toJson(course)));
			data.put("course_units", new JSONArray(gson.toJson(course_units)));
		} catch (JSONException e) {
			AppLogger.printStackTrace(e);
		}
		return new RestEntity(RestEntity.POST, url, data);
	}
	
	public RestEntity addCourse(int courseId) {
		String url = Const.REST_HOST+ "/add_course/"+courseId+".json";
		return new RestEntity(url);
	}
	
	public RestEntity removeCourse(int courseId){
		String url = Const.REST_HOST+ "/remove_course/"+courseId+".json";
		return new RestEntity(url);
	}
	
	public RestEntity getSemesters() {
		String url = Const.REST_HOST + "/mobile/all_semesters.json";
		return new RestEntity(url);
	}
	
	public RestEntity getConfigParams(){
		JSONObject data = new JSONObject();
		String url = Const.REST_HOST + "/mobile/get_config_params.json";
		return new RestEntity(RestEntity.POST, url, data);
	}
	
	public RestEntity getSkins(final int page, final int limit){
		String url = Const.REST_HOST + "/mobile/skins.json?page="+page+"&per_page="+limit;
		return new RestEntity(url);
	}

	public RestEntity getClassmates(final int page, final int limit, final int type) {
		String url = Const.REST_HOST + "/mobile/classmates.json?page="+page+"&per_page="+limit+"&classmates_type="+type;;
		return new RestEntity(url);
	}
	
	public RestEntity getFollowers(final int page, final int limit) {
		String url = Const.REST_HOST + "/mobile/followers.json?page="+page+"&per_page="+limit;
		return new RestEntity(url);
	}
	
	public RestEntity visit(int userId){
		String url = Const.REST_HOST + "/statistics/profile.json?user_id=" + userId;
		return new RestEntity(url);
	}
	
	public RestEntity getAccountInfo(){
		String url = Const.REST_HOST + "/mobile/account_info.json";
		return new RestEntity(url);
	}
	
	public RestEntity changePassword(String oldPass, String password){
		JSONObject data = new JSONObject();
		String url = Const.REST_HOST+ "/mobile/change_password.json";
		try {
			data.put("old_password", oldPass);
			data.put("password", password);
		} catch (JSONException e) {
			AppLogger.printStackTrace(e);
		}
		return new RestEntity(RestEntity.POST, url, data);
	}
//	@Override
//	public Boolean isSupport() {
//		try {
//			String response = HttpUtil.getStringFromUrl(Const.REST_HOST
//					+ "/users/is_support");
//			BaseResult object= gson.fromJson(response, BaseResult.class);
//			return object.result;
//		} catch (Exception e) {
//			Log.w(RestService.class.toString(),
//					"connection failed " + e.getMessage());
//		}
//		return null;
//	}
	
	public RestEntity search(String term, int day_of_week, int time_slot){
		String url;
		try {
			term = URLEncoder.encode(term, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			AppLogger.printStackTrace(e);
		}
		if (day_of_week == Const.INVALID_INT_VALUE || time_slot == Const.INVALID_INT_VALUE) {
			url = Const.REST_HOST +  ((term == null || term.length() == 0) ? "/popular.json" : ("/search.json?term="+term));
		}else {
			String params = "day_of_week=" + day_of_week + "&time_slot=" + time_slot;
			String searchUrl = "/search.json?term="+term + "&" + params;
			String popularUrl = "/popular.json?" + params;
			url = Const.REST_HOST +  ((term == null || term.length() == 0) ? popularUrl : searchUrl);
		}
		return new RestEntity(url, SearchResult.class);
	}
	
	
	public RestEntity search(String term){
		return search(term, Const.INVALID_INT_VALUE, Const.INVALID_INT_VALUE);
	}
	
	public RestEntity searchUsers(String term, int page, int limit){
		try {
			term = URLEncoder.encode(term, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			AppLogger.printStackTrace(e);
		}
		String url = Const.REST_HOST + "/search/users.json?term="+term+"&page="+page+"&per_page="+limit;
		return new RestEntity(url);
	}
	
	public RestEntity searchSchools(String term, int page, int limit){
		try {
			term = URLEncoder.encode(term, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			AppLogger.printStackTrace(e);
		}
		String url = Const.REST_HOST + "/search_schools.json?term="+term+"&page="+page+"&per_page="+limit;
		return new RestEntity(url, SchoolsResult.class);
	}
	
	public RestEntity searchDepartments(String term, int school_id, int page, int limit){
		try {
			term = URLEncoder.encode(term, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			AppLogger.printStackTrace(e);
		}
		String url = Const.REST_HOST + "/search_department.json?term="+term+"&school_id="+school_id+"&page="+page+"&per_page="+limit;
		return new RestEntity(url, DepartmentsResult.class);
	}
	
	public RestEntity searchAllDepartments(int school_id){
		String url = Const.REST_HOST + "/search_department.json?school_id="+school_id;
		return new RestEntity(url, DepartmentsResult.class);
	}
	
	public RestEntity searchUserByGeziId(String term){
		try {
			term = URLEncoder.encode(term, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			AppLogger.printStackTrace(e);
		}
		String url = Const.REST_HOST + "/search/user_by_gezi_id.json?term="+term;
		return new RestEntity(url);
	}
	
	public RestEntity register(User user, String school, String department, int county_id){
		String url = Const.REST_HOST+ "/mobile/register.json";
		JSONObject data = new JSONObject();
		try {
			data.put("user", new JSONObject(gson.toJson(user)));
			data.put("school_name", school);
			data.put("department_name", department);
			data.put("renren_id", user.renren_id);
			data.put("renren_mobile_token", user.renren_token);

			data.put("tencent_token", user.tencent_token);
			data.put("tencent_id", user.tencent_id);
			data.put("weibo_token", user.weibo_token);
			data.put("weibo_id", user.weibo_id);
			
			data.put("county_id", county_id);
		} catch (JSONException e) {
			AppLogger.printStackTrace(e);
		}
		return new RestEntity(RestEntity.POST, url, data);
	}

	public RestEntity login_by_oauth(String third_part_id, String third_part_token, int category){
		String url = Const.REST_HOST+ "/mobile/login_by_oauth.json";
		JSONObject data = new JSONObject();
		try {
			data.put("third_part_id", third_part_id);
			data.put("third_part_token", third_part_token);
			data.put("category", category);
		} catch (JSONException e) {
			AppLogger.printStackTrace(e);
		}
		return new RestEntity(RestEntity.POST, url, data);
	}
	
	public RestEntity addFriendCoursesById(int userId,String course_ids){
		String url = Const.REST_HOST+ "/mobile/one_click_add_courses.json";
		JSONObject data = new JSONObject();
		try {
			data.put("course_ids", course_ids);
			data.put("user_id", userId);
		} catch (JSONException e) {
			AppLogger.printStackTrace(e);
		}
		return new RestEntity(RestEntity.POST, url, data);
	}
	
	public RestEntity importCourses(String username, String password, String sessionId, String captcha){
		String url = Const.REST_HOST+ "/courses/importable_courses.json";
		JSONObject data = new JSONObject();
		try {
			data.put("username", username);
			data.put("password", password);
			if (sessionId != null) {
				data.put("session_id", sessionId);
				data.put("captcha_value", captcha);
			}
		} catch (JSONException e) {
			AppLogger.printStackTrace(e);
		}
		return new RestEntity(RestEntity.POST, url, data);
	}
	
	public RestEntity importCoursesByIds(String course_ids){
		String url = Const.REST_HOST+ "/mobile/add_importable_courses.json";
		JSONObject data = new JSONObject();
		try {
			data.put("course_ids", course_ids);
		} catch (JSONException e) {
			AppLogger.printStackTrace(e);
		}
		return new RestEntity(RestEntity.POST, url, data);
	}
	
	public RestEntity getCourseDetailsResult (int courseId, int page, int limit) {
		String url = Const.REST_HOST + "/courses/"+courseId+"/details.json?page="+page+"&per_page="+limit;
		return new RestEntity(url);		
	}
	
	public RestEntity rate(Rating rating){
		String url = Const.REST_HOST+ "/ratings/rate.json";
		JSONObject data = new JSONObject();
		try {
			data.put("rating", new JSONObject(gson.toJson(rating)));
			data.put("tags", new JSONArray(rating.getTags()));
		} catch (JSONException e) {
			AppLogger.printStackTrace(e);
		}
		return new RestEntity(RestEntity.POST, url, data);
	}
	
	public RestEntity getTags (){
		String url = Const.REST_HOST + "/tags.json";
		return new RestEntity(url);			
	}
	
	public RestEntity getEvents(int page, int limit){
		String url = Const.REST_HOST + "/events.json?page="+page+"&per_page="+limit;
		return new RestEntity(url, EventsResult.class);		
	}
	
	public RestEntity getJoinedEvents(){
		String url = Const.REST_HOST + "/events/joined.json";
		return new RestEntity(url, EventsResult.class);		
	}
	
	public RestEntity deleteEvent(int eventId){
		String url = Const.REST_HOST+ "/events/"+eventId+"/unjoin.json";
		return new RestEntity(url, BaseResult.class);	
	}
	
	public RestEntity addEvent(int eventId){
		String url = Const.REST_HOST+ "/events/"+eventId+"/join.json";
		return new RestEntity(url, EventResult.class);
	}
	
	public RestEntity getEventUsers(int eventId, int page, int limit){
		String url = Const.REST_HOST
				+ "/events/"+eventId+"/users.json?page="+page+"&per_page="+limit;
		return new RestEntity(url, UsersResult.class);
	}
	
	public RestEntity getUserClassTime() {
		String url = Const.REST_HOST + "/users/class_time.json";
		return new RestEntity(url, ClassTimeResult.class);
	}
	
	public RestEntity saveUserClassTime(String userClassTimeString){
		String url = Const.REST_HOST+ "/users/save_class_time.json";
		JSONObject data = new JSONObject();
		try {
			data.put("time_json", userClassTimeString);
		} catch (JSONException e) {
			AppLogger.printStackTrace(e);
		}
		return new RestEntity(RestEntity.POST, url, data);
	}
	
	public RestEntity getLeaderboards (){
		String url = Const.REST_HOST + "/leaderboards.json"; 
		return new RestEntity(url);		
	}
	
	public RestEntity getRankings(int leaderboardId){
		String url = Const.REST_HOST + "/leaderboards/"+leaderboardId+".json"; 
		return new RestEntity(url);	
	}
	
	public RestEntity login(String account, String password){
		String url = Const.REST_HOST+ "/mobile/login.json";
		JSONObject data = new JSONObject();
		try {
			data.put("account", account);
			data.put("password", password);
		} catch (JSONException e) {
			AppLogger.printStackTrace(e);
		}
		return new RestEntity(RestEntity.POST, url, data);
	}
	
	public RestEntity getCourseDetails(final int courseId, final int page, final int limit){
		String url = Const.REST_HOST + "/courses/"+courseId+"/details.json?page="+page+"&per_page="+limit;
		return new RestEntity(url);
	}
	
//	@Override
//	public User getUser(int userId){
//		String url = Const.REST_HOST + "/users/info.json?id="+userId;
//		return getObject(url, User.class);
//	}
	
	public RestEntity getUser(int userId){
		String url = Const.REST_HOST + "/users/"+userId+"/details.json";
		return new RestEntity(url);
	}
	
	public RegistResult uploadPhoto(File photoFile) {
		RegistResult result= null;
		HttpClient client = new DefaultHttpClient();
		HttpPost post = new HttpPost(Const.REST_HOST + "/mobile/upload_avatar.json?token="+App.getInstance().getToken());

//		post.addHeader("User-Agent", Constants.CLIENT_USER_AGENT);
		post.addHeader("accept", "application/json");

		MultipartEntity reqEntity = new MultipartEntity();
		try {
			reqEntity.addPart("user_avatar[avatar]", new FileBody(photoFile));
			post.setEntity(reqEntity);
			HttpResponse response = client.execute(post);
			HttpEntity resEntity = response.getEntity();
			if (resEntity != null) {
				InputStream instream = null;
				String responseString;
				try {
					instream = resEntity.getContent();
					responseString = HttpUtil.convertStreamToString(instream);
				} finally {
					HttpUtil.closeQuietly(instream);
				}
				result = gson.fromJson(responseString, RegistResult.class);
			}
		} catch (Exception e) {
			Log.w(RestService.class.toString(),
					e.getMessage(), e);
		}
		return result;
	}
	
	public UploadAvatatsResult uploadPhotos(List<AvatarOrFile> avatarOrFiles) {
		UploadAvatatsResult result= null;
		JSONArray jsonArray = new JSONArray();
		try {
			for (AvatarOrFile avatarOrFile : avatarOrFiles) {
				JSONObject data = new JSONObject();
				if (avatarOrFile.isAvatar()) {
					data.put("id", avatarOrFile.avatar.id);
				} else {
					data.put("avatar", avatarOrFile.fileName);
				}
				jsonArray.put(data);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		String string = jsonArray.toString();
		HttpClient client = new DefaultHttpClient();
		HttpPost post = new HttpPost(Const.REST_HOST + "/mobile/upload_avatars.json?token="+App.getInstance().getToken());

//		post.addHeader("User-Agent", Constants.CLIENT_USER_AGENT);
		post.addHeader("accept", "application/json");

		MultipartEntity reqEntity = new MultipartEntity();
		try {
			StringBody body = new StringBody(string, Charset.forName("UTF-8"));
			reqEntity.addPart("avatars", body);
			for (AvatarOrFile avatarOrFile : avatarOrFiles) {
				if (!avatarOrFile.isAvatar()) {
					reqEntity.addPart(avatarOrFile.fileName, new FileBody(new File(avatarOrFile.path)));
				}
			}
			post.setEntity(reqEntity);
			HttpResponse response = client.execute(post);
			HttpEntity resEntity = response.getEntity();
			if (resEntity != null) {
				InputStream instream = null;
				String responseString;
				try {
					instream = resEntity.getContent();
					responseString = HttpUtil.convertStreamToString(instream);
				} finally {
					HttpUtil.closeQuietly(instream);
				}
				result = gson.fromJson(responseString, UploadAvatatsResult.class);
			}
		} catch (Exception e) {
			Log.w(RestService.class.toString(),
					e.getMessage(), e);
		}
		return result;
	}
	
	public RestEntity updateOauth(int category, String id, String token, String name, Date expirationDate){
		String url = Const.REST_HOST + "/mobile/update_oauth.json";
		JSONObject data = new JSONObject();
		try {
			data.put("third_part_id", id);
			data.put("third_part_token", token);
			data.put("expiration_date", expirationDate);
			data.put("name", name);
			data.put("category", category);
		}catch (JSONException e) {
			AppLogger.printStackTrace(e);
		}
		return new RestEntity(RestEntity.POST, url, data);
	}
	
	public RestEntity updateUser(Map<String, Object> map, String schoolName, String departmentName, String[] life_situations, int county_id){
		JSONObject data = new JSONObject();
		String url = Const.REST_HOST+ "/mobile/update.json";
		try {
			JSONObject user = new JSONObject();
			for (String key : map.keySet()) {
				user.put(key, map.get(key));
			}
			data.put("user", user);
			if (schoolName != null) {
				data.put("school_name", schoolName);
				data.put("county_id", county_id);
			}
			if (departmentName != null) {
				data.put("department_name", departmentName);
			}
			if (life_situations != null) {
				data.put("life_situations", new JSONArray(Arrays.asList(life_situations)));
			}
		}catch (JSONException e) {
			AppLogger.printStackTrace(e);
		}
		return new RestEntity(RestEntity.POST, url, data);
	}
	
	public RestEntity getCampusMapInfo(){
		String url = Const.REST_HOST + "/mobile/campus_map_info.json";
		return new RestEntity(url, CampusMap.class);
	}
	
	public RestEntity getNeedCampusMap(){
		String url = Const.REST_HOST + "/mobile/need_campus_map.json";
		return new RestEntity(url, CampusMapApplicants.class);
	}
	
	public RestEntity getNeedCoursesImporter(){
		String url = Const.REST_HOST + "/mobile/need_courses_importer.json";
		return new RestEntity(url, CampusMapApplicants.class);
	}
	
	public RestEntity getCoursesImporterApplicantsCount(){
		String url = Const.REST_HOST + "/mobile/courses_importer_applicants_count.json";
		return new RestEntity(url, CampusMapApplicants.class);
	}
	
	//所有的贴纸和主题
	public RestEntity getAllProducts(){
		String url = Const.REST_HOST + "/mall/products.json";
		return new RestEntity(url, ProductsResult.class);
	}
	
	//自己的贴纸和主题
	public RestEntity getMineProducts(){
		String url = Const.REST_HOST + "/mall/mines.json";
		return new RestEntity(url, MyProductsResult.class);
	}
	
	//付费接口
	public RestEntity payment(int id, String channel){
		String url = Const.REST_HOST + "/mall/payment.json?id="+id + "&channel=" + channel;
		return new RestEntity(url, PaymentlResult.class);
	}
	
	public RestEntity updateSemesterBeginDate(final String begin_date){
		String url = Const.REST_HOST + "/users/save_semester.json";
		JSONObject data = new JSONObject();
		try {
			data.put("begin_date", begin_date);
		} catch (JSONException e) {
			AppLogger.printStackTrace(e);
		}
		return new RestEntity(RestEntity.POST, url, data, BaseResult.class);
	}
}
