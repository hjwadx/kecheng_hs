package fm.jihua.kecheng.rest.service;

import java.io.File;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.http.Header;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.impl.auth.BasicScheme;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.Response.Listener;
import com.google.gson.Gson;

import fm.jihua.kecheng.App;
import fm.jihua.kecheng_hs.R;
import fm.jihua.kecheng.rest.contract.DataCallback;
import fm.jihua.kecheng.rest.entities.AccountResult;
import fm.jihua.kecheng.rest.entities.AddCoursesResult;
import fm.jihua.kecheng.rest.entities.Avatar;
import fm.jihua.kecheng.rest.entities.AvatarOrFile;
import fm.jihua.kecheng.rest.entities.BaseResult;
import fm.jihua.kecheng.rest.entities.ClassTimeResult;
import fm.jihua.kecheng.rest.entities.ConfigParamsResult;
import fm.jihua.kecheng.rest.entities.Course;
import fm.jihua.kecheng.rest.entities.CourseDetailsResult;
import fm.jihua.kecheng.rest.entities.CourseResult;
import fm.jihua.kecheng.rest.entities.CourseUnit;
import fm.jihua.kecheng.rest.entities.CoursesResult;
import fm.jihua.kecheng.rest.entities.DepartmentsResult;
import fm.jihua.kecheng.rest.entities.Event;
import fm.jihua.kecheng.rest.entities.EventResult;
import fm.jihua.kecheng.rest.entities.EventsResult;
import fm.jihua.kecheng.rest.entities.ImportCoursesResult;
import fm.jihua.kecheng.rest.entities.Note;
import fm.jihua.kecheng.rest.entities.RankingsResult;
import fm.jihua.kecheng.rest.entities.Rating;
import fm.jihua.kecheng.rest.entities.RatingResult;
import fm.jihua.kecheng.rest.entities.RegistResult;
import fm.jihua.kecheng.rest.entities.SNSUsersResult;
import fm.jihua.kecheng.rest.entities.SchoolsResult;
import fm.jihua.kecheng.rest.entities.SecretPost;
import fm.jihua.kecheng.rest.entities.SecretPostComment;
import fm.jihua.kecheng.rest.entities.SecretPostCommentResult;
import fm.jihua.kecheng.rest.entities.SecretPostCommentsResult;
import fm.jihua.kecheng.rest.entities.SecretPostResult;
import fm.jihua.kecheng.rest.entities.SecretPostsResult;
import fm.jihua.kecheng.rest.entities.Semester;
import fm.jihua.kecheng.rest.entities.SemestersResult;
import fm.jihua.kecheng.rest.entities.TagsResult;
import fm.jihua.kecheng.rest.entities.UpdateCourseResult;
import fm.jihua.kecheng.rest.entities.UploadAvatatsResult;
import fm.jihua.kecheng.rest.entities.User;
import fm.jihua.kecheng.rest.entities.UserDetailsResult;
import fm.jihua.kecheng.rest.entities.UserResult;
import fm.jihua.kecheng.rest.entities.UsersResult;
import fm.jihua.kecheng.rest.entities.WrappedResult;
import fm.jihua.kecheng.rest.entities.mall.MyProductsResult;
import fm.jihua.kecheng.rest.entities.mall.ProductsResult;
import fm.jihua.kecheng.rest.entities.sticker.PasteResult;
import fm.jihua.kecheng.rest.entities.sticker.UserSticker;
import fm.jihua.kecheng.utils.AppLogger;
import fm.jihua.kecheng.utils.DatabaseHelper;
import fm.jihua.kecheng.utils.EncryptionUtil;

@SuppressLint("CommitPrefEdits")
public class DataAdapter {

	public static final int MESSAGE_NOT_CARE = 0;
	// public static final int MESSAGE_SIGN_IN = 1;
	public static final int MESSAGE_GET_SEMESTER = 2;
	public static final int MESSAGE_GET_COURSES = 3;
	public static final int MESSAGE_DELETE_COURSE = 4;
	public static final int MESSAGE_IS_SUPPORT = 5;
	public static final int MESSAGE_GET_USER = 6;
	public static final int MESSAGE_REGISTER = 7;
	public static final int MESSAGE_LOGIN_BY_OAUTH = 8;
	public static final int MESSAGE_LOGIN = 9;
	public static final int MESSAGE_GET_STUDENTS = 10;
	public static final int MESSAGE_GET_NOTES = 11;
	public static final int MESSAGE_GET_FREE_TIME_FRIENDS = 12;
	public static final int MESSAGE_GET_FRIENDS = 14;
	public static final int MESSAGE_UPDATE_USER = 15;
	public static final int MESSAGE_SEND_MESSAGE = 16;
	public static final int MESSAGE_CREATE_COURSE = 17;
	public static final int MESSAGE_UPDATE_COURSE = 18;
	public static final int MESSAGE_UPLOAD_PHOTO = 20;
	public static final int MESSAGE_GET_ACTIVITIES = 30;
	public static final int MESSAGE_ADD_FRIEND = 31;
	public static final int MESSAGE_REMOVE_FRIEND = 32;
	public static final int MESSAGE_REMOVE_NOTE = 33;
	public static final int MESSAGE_GET_GRAPH = 34;
	public static final int MESSAGE_UPDATE_OAUTH = 35;
	public static final int MESSAGE_SEARCH_USERS = 36;
	public static final int MESSAGE_GET_CLASSMATES = 37;
	public static final int MESSAGE_VISIT = 38;
	public static final int MESSAGE_CHANGE_PASSWORD = 39;
	public static final int MESSAGE_GET_ACCOUNT_INFO = 40;
	public static final int MESSAGE_SEARCH_USER_BY_GEZI_ID = 41;
	public static final int MESSAGE_ADD_COURSE = 42;
	public static final int MESSAGE_ADD_FRIEND_COURSE = 43;
	public static final int MESSAGE_ADD_FRIEND_COURSE_BY_ID = 4311;
	public static final int MESSAGE_GET_RATING = 44;
	public static final int MESSAGE_ADD_RATING = 45;
	public static final int MESSAGE_GET_TAGS = 46;
	public static final int MESSAGE_GET_LEADERBOARDS = 47;
	public static final int MESSAGE_GET_LEADERBOARD = 48;
	public static final int MESSAGE_GET_DETAILS = 49;
	public static final int MESSAGE_GET_CONFIG_PARAMS = 50;
	public static final int MESSAGE_GET_FOLLOWERS = 51;
	public static final int MESSAGE_GET_SKINS = 52;

	public static final int MESSAGE_GET_USER_MEDALS = 54;
	public static final int MESSAGE_GET_AVATARS = 53;
	public static final int MESSAGE_SEARCH_SCHOOLS = 55;
	public static final int MESSAGE_SEARCH_DEPARTMENTS = 56;
	public static final int MESSAGE_SEARCH_ALL_DEPARTMENTS = 55;
	

	public static final int MESSAGE_IMPORT_COURSES_FEEDBACK = 59;
	public static final int MESSAGE_IMPORT_COURSES = 60;
	public static final int MESSAGE_IMPORT_COURSES_BY_IDS = 601;
	
	public static final int MESSAGE_CREATE_SECRET_POST = 61;
	public static final int MESSAGE_CREATE_SECRET_POST_COMMENT = 62;
	public static final int MESSAGE_GET_SECRET_POSTS = 63;
	public static final int MESSAGE_GET_SECRET_POST_COMMENTS = 64;
	public static final int MESSAGE_ADD_EVENT = 65;
	public static final int MESSAGE_DELETE_EVENT = 66;
	public static final int MESSAGE_GET_EVENT = 67;
	public static final int MESSAGE_GET_JOINED_EVENT = 68;
	public static final int MESSAGE_GET_EVENT_USERS = 69;
	public static final int MESSAGE_SAVE_NOTE = 70;
	public static final int MESSAGE_CREATE_NOTE = 71;
	public static final int MESSAGE_GET_CAMPUSMAP_INFO = 72;
	public static final int MESSAGE_CHANGED_THEME = 73;
	public static final int MESSAGE_STICKER_PASTE = 74;
	public static final int MESSAGE_STICKER_MODIFY = 75;
	public static final int MESSAGE_STICKER_REMOVE = 76;
	public static final int MESSAGE_STICKER_SYNC = 77;
	public static final int MESSAGE_GET_ALL_PRODUCTS = 80;
	public static final int MESSAGE_GET_MY_PRODUCTS = 81;
	public static final int MESSAGE_GET_USER_CLASSTIME = 82;
	public static final int MESSAGE_POST_USER_CLASSTIME = 83;
	public static final int MESSAGE_UPDATE_SEMESTER_BEGIN_DATE = 84;
	
	public static final String MY_PRODUCTS_KEY = "MY_DATA_MY_PRODUCTS";
	
	public static final String SEARCH_API_USERNAME = "1029384756";
	public static final String SEARCH_API_PASSWORD = "qwertyuiop";
	
	public static final String TAG = "DataAdapter";

	private DataCallback mCallback;
	private android.app.Activity mContext;
	private DatabaseHelper mDBHelper;
	private SQLiteDatabase mUserDB;
	private UIHandler mHandler;
	private App mApp;
	private final RequestQueue queue;
	private Gson gson;
	private RequestBuilder requestBuilder;
	
	public DataAdapter(android.app.Activity context, DataCallback callback) {
		this.mCallback = callback;
		this.mContext = context;
		this.queue = App.getInstance().getHttpQueue();
		mApp = (App) context.getApplication();
		this.mDBHelper = mApp.getDBHelper();
		this.mUserDB = mApp.getUserDB();
		this.gson = GsonRequest.getDefaultGson();
		Looper looper = Looper.myLooper();
		if (looper != null && mHandler == null) {
			mHandler = new UIHandler(looper, context, callback);
		}
		this.requestBuilder = new RequestBuilder(queue, mHandler);
	}
	
	public <T>void request(RestEntity restEntity){
		request(restEntity, MESSAGE_NOT_CARE);
	}

	@SuppressWarnings("unchecked")
	public <T>void request(RestEntity restEntity, int category){
		if (restEntity.clientClass == null) {
			throw new IllegalArgumentException("restEntity should cantain client class");
		}
		requestBuilder.buildAndAddRequest(restEntity, restEntity.clientClass, category);
	}

	public void register(final User user, final String school,
			final String department, final int county_id) {
		RestEntity restEntity = RestService.get().register(user, school, department, county_id);
		requestBuilder.buildAndAddRequest(restEntity, RegistResult.class, MESSAGE_REGISTER, getAutoSaveUserResponseListener(MESSAGE_REGISTER));
	}

	public void loginByOauth(final String thirdPartId,
			final String thirdPartToken, final int category) {
		RestEntity restEntity = RestService.get().login_by_oauth(thirdPartId, thirdPartToken, category);
		requestBuilder.buildAndAddRequest(restEntity, RegistResult.class, MESSAGE_LOGIN_BY_OAUTH, getAutoSaveUserResponseListener(MESSAGE_LOGIN_BY_OAUTH));
	}
	
	public void addFriendCoursesById(final int userId,final String courseString) {
		Listener<AddCoursesResult> listener = new Listener<AddCoursesResult>() {

			@Override
			public void onResponse(AddCoursesResult result) {
				if (result != null) {
					mDBHelper.saveCourses(mUserDB, result.courses);
					List<Course> courses = mDBHelper.getCourses(mUserDB);
					Course[] courseArray = new Course[courses.size()];
					result.courses = courses.toArray(courseArray);
				}
				mHandler.sendMessage(createMessage(MESSAGE_ADD_FRIEND_COURSE_BY_ID, result));
			}
		};
		RestEntity restEntity = RestService.get().addFriendCoursesById(userId,courseString);
		requestBuilder.buildAndAddRequest(restEntity, AddCoursesResult.class, MESSAGE_ADD_FRIEND_COURSE_BY_ID, listener);
	}
	
	public void importCourses(final String username, final String password, final String sessionId, final String captcha) {
		Listener<ImportCoursesResult> listener = new Listener<ImportCoursesResult>() {

			@Override
			public void onResponse(ImportCoursesResult result) {
//				if (result != null && result.success) {
//					if (result.courses != null) {
//						for (Course course : result.courses) {
//							mDBHelper.saveCourse(mUserDB, course);
//						}
//					}
//				}
				mHandler.sendMessage(createMessage(MESSAGE_IMPORT_COURSES, result));
			}
		};
		byte[] key = EncryptionUtil.xorWithKey(String.valueOf(mApp.getMyUserId()).getBytes(), mApp.getToken().getBytes());
		RestEntity restEntity = RestService.get().importCourses(EncryptionUtil.encode(username, key), EncryptionUtil.encode(password, key), sessionId, captcha);
		requestBuilder.buildAndAddRequest(restEntity, ImportCoursesResult.class, MESSAGE_IMPORT_COURSES, listener);
	}
	
	public void importCoursesByIds(final String courseString) {
		Listener<AddCoursesResult> listener = new Listener<AddCoursesResult>() {

			@Override
			public void onResponse(AddCoursesResult result) {
				if (result != null) {
					mDBHelper.saveCourses(mUserDB, result.courses);
					List<Course> courses = mDBHelper.getCourses(mUserDB);
					Course[] courseArray = new Course[courses.size()];
					result.courses = courses.toArray(courseArray);
				}
				mHandler.sendMessage(createMessage(MESSAGE_IMPORT_COURSES_BY_IDS, result));
			}
		};
		RestEntity restEntity = RestService.get().importCoursesByIds(courseString);
		requestBuilder.buildAndAddRequest(restEntity, AddCoursesResult.class, MESSAGE_IMPORT_COURSES_BY_IDS, listener);
	}

	public void getCourseDetails(final int courseId, final int page,
			final int limit) {
		RestEntity restEntity = RestService.get().getCourseDetails(courseId, page, limit);
		requestBuilder.buildAndAddRequest(restEntity, CourseDetailsResult.class, MESSAGE_GET_DETAILS, true);
		requestBuilder.buildAndAddRequest(restEntity, CourseDetailsResult.class, MESSAGE_GET_DETAILS);
	}

	public void getTags(boolean local) {
		RestEntity restEntity = RestService.get().getTags();
		if (local) {
			requestBuilder.buildAndAddRequest(restEntity, TagsResult.class, MESSAGE_GET_TAGS, true, true);
		}else {
			requestBuilder.buildAndAddRequest(restEntity, TagsResult.class, MESSAGE_GET_TAGS);
		}
	}

	public void getLeaderboards() {
		RestEntity restEntity = RestService.get().getLeaderboards();
		requestBuilder.buildAndAddRequest(restEntity, RankingsResult.class, MESSAGE_GET_LEADERBOARDS, true);
		requestBuilder.buildAndAddRequest(restEntity, RankingsResult.class, MESSAGE_GET_LEADERBOARDS);
	}

	public void getRankings(final int leaderboardId) {
		RestEntity restEntity = RestService.get().getRankings(leaderboardId);
		requestBuilder.buildAndAddRequest(restEntity, RankingsResult.class, MESSAGE_GET_LEADERBOARD, true);
		requestBuilder.buildAndAddRequest(restEntity, RankingsResult.class, MESSAGE_GET_LEADERBOARD);
	}

	public void Rate(final Rating rating) {
		Listener<RatingResult> listener = new Listener<RatingResult>() {

			@Override
			public void onResponse(RatingResult result) {
				if (result != null && result.success) {
					mDBHelper.saveRating(mUserDB, rating, rating.course_id,
							rating.user_id);
					Rating rat = new Rating(rating.course_id, result, 0, null);
					mDBHelper.saveRating(mUserDB, rat, rating.course_id);
				}
				mHandler.sendMessage(createMessage(MESSAGE_ADD_RATING, result));
			}
		};
		RestEntity restEntity = RestService.get().rate(rating);
		requestBuilder.buildAndAddRequest(restEntity, RatingResult.class, MESSAGE_ADD_RATING, listener);
	}
	
	public void createSecretPost(final SecretPost post){
		RestEntity restEntity = RestService.get().createSecretPost(post);
		requestBuilder.buildAndAddRequest(restEntity, SecretPostResult.class, MESSAGE_CREATE_SECRET_POST);
	}
	public void createSecretPostComment(final int postId, final SecretPostComment comment, int replyId){
		RestEntity restEntity = RestService.get().createSecretPostComment(postId, comment, replyId);
		requestBuilder.buildAndAddRequest(restEntity, SecretPostCommentResult.class, MESSAGE_CREATE_SECRET_POST_COMMENT);
	}
	
	public void getSecretPosts(boolean cache, final int page, final int limit){
		RestEntity restEntity = RestService.get().getSecretPosts(page, limit);
		if (page == 1) {
			requestBuilder.buildAndAddRequest(restEntity, SecretPostsResult.class, MESSAGE_GET_SECRET_POSTS, true);
		}
		requestBuilder.buildAndAddRequest(restEntity, SecretPostsResult.class, MESSAGE_GET_SECRET_POSTS);
	}
	
	public void getSecretPostComments(final int postId, final int page, final int limit){
		RestEntity restEntity = RestService.get().getSecretPostComments(postId, page, limit);
		if (page == 1) {
			requestBuilder.buildAndAddRequest(restEntity, SecretPostCommentsResult.class, MESSAGE_GET_SECRET_POST_COMMENTS, true);
		}
		requestBuilder.buildAndAddRequest(restEntity, SecretPostCommentsResult.class, MESSAGE_GET_SECRET_POST_COMMENTS);
	}
	
	public void getAllProducts(){
		RestEntity restEntity = RestService.get().getAllProducts();
		requestBuilder.buildAndAddRequest(restEntity, ProductsResult.class, MESSAGE_GET_ALL_PRODUCTS, true);
		requestBuilder.buildAndAddRequest(restEntity, ProductsResult.class, MESSAGE_GET_ALL_PRODUCTS);
	}
	
	public void getRemoteMineProducts(){
		RestEntity restEntity = RestService.get().getMineProducts();
		Listener<MyProductsResult> listener = new Listener<MyProductsResult>() {

			@Override
			public void onResponse(MyProductsResult response) {
				if (response.success) {
					App.getInstance().putValue(MY_PRODUCTS_KEY, gson.toJson(response));
				}
				mHandler.sendMessage(createMessage(MESSAGE_GET_MY_PRODUCTS, response));
			}
		};
		requestBuilder.buildAndAddRequest(restEntity, MyProductsResult.class, MESSAGE_GET_MY_PRODUCTS, listener);
	}
	
	public void createCourse(final Course course, List<CourseUnit> course_units) {
		Listener<CourseResult> listener = new Listener<CourseResult>() {

			@Override
			public void onResponse(CourseResult response) {
				if (response.success) {
					mDBHelper.saveCourse(mUserDB, response.course);
				}
				mHandler.sendMessage(createMessage(MESSAGE_CREATE_COURSE, response));
			}
			
		};
		RestEntity restEntity = RestService.get().createCourse(course, course_units);
		requestBuilder.buildAndAddRequest(restEntity, CourseResult.class, MESSAGE_CREATE_COURSE, listener);
	}

	public void addCourse(final int courseId) {
		Listener<CourseResult> listener = new Listener<CourseResult>() {

			@Override
			public void onResponse(CourseResult response) {
				if (response.success) {
					mDBHelper.saveCourse(mUserDB, response.course);
				}
				mHandler.sendMessage(createMessage(MESSAGE_ADD_COURSE, response));
			}
			
		};
		RestEntity restEntity = RestService.get().addCourse(courseId);
		requestBuilder.buildAndAddRequest(restEntity, CourseResult.class, MESSAGE_ADD_COURSE, listener);
	}
	
	public void changeTheme(String theme_name) {
		RestEntity restEntity = RestService.get().changeTheme(theme_name);
		requestBuilder.buildAndAddRequest(restEntity, BaseResult.class, MESSAGE_CHANGED_THEME);
	}

	public void pasterSticker(UserSticker paster) {
		requestBuilder.buildAndAddRequest(RestService.get().pasteSticker(paster), PasteResult.class, MESSAGE_STICKER_PASTE);
	}

	public void modifySticker(UserSticker paster) {
		requestBuilder.buildAndAddRequest(RestService.get().modifySticker(paster), BaseResult.class, MESSAGE_STICKER_MODIFY);
	}

	public void removeSticker(UserSticker paster) {
		requestBuilder.buildAndAddRequest(RestService.get().removeSticker(paster), BaseResult.class, MESSAGE_STICKER_REMOVE);
	}

	public void updateCourse(final Course course, final List<CourseUnit> course_units) {
		Listener<UpdateCourseResult> listener = new Listener<UpdateCourseResult>() {

			@Override
			public void onResponse(UpdateCourseResult result) {
				if (result != null && result.success) {
					if (result.action.equals("create")
							|| result.action.equals("update")) {
						mDBHelper.deleteCourse(mUserDB, course.id);
						mDBHelper.saveCourse(mUserDB, result.updated_course);
					} else if (result.action.equals("patch")) {
						course.course_units = course_units;
						result.updated_course = course;
						mDBHelper.saveCourse(mUserDB, result.updated_course, true);
					}
				}
				mHandler.sendMessage(createMessage(MESSAGE_UPDATE_COURSE,
						result));
			}
			
		};
		RestEntity restEntity = RestService.get().updateCourse(course, course_units);
		requestBuilder.buildAndAddRequest(restEntity, UpdateCourseResult.class, MESSAGE_UPDATE_COURSE, listener);
	}
	
	private void saveUserInfo(RegistResult result) {
		if (result != null && result.success) {
			App app = App.getInstance();
			app.setToken(result.kecheng_token);
			app.setMyUserId(result.user.id);
			if (result.school_time_slot != 0) {
				app.setSlotLength(result.school_time_slot);
			}
			if (result.semesters != null) {
				initApplicationData(result.semesters, result.active_semester_id);
			}
			app.getDBHelper().saveUser(app.getUserDB(), result.user);
			if (result.config_params != null) {
				app.saveConfigParams(result.config_params);
			}
			SharedPreferences settings = PreferenceManager
					.getDefaultSharedPreferences(app);
			SharedPreferences.Editor edit = settings.edit();
			edit.putString(App.ACCOUNT_PASSWORD_KEY, result.kecheng_token);
			App.commitEditor(edit);
		}
	}
	
	private void initApplicationData(Semester[] semesters, int active_semester_id){
		mDBHelper.saveSemesters(mUserDB, semesters);
		if (mApp.getActiveSemesterId() != active_semester_id) {
			mApp.setActiveSemesterId(active_semester_id);
			mApp.setCurrentWeek(mApp.getCurrentWeek(true));
			SharedPreferences settings = PreferenceManager
					.getDefaultSharedPreferences(mApp);
			Editor editor = settings.edit();
			editor.putBoolean(mApp.getString(R.string.vacation),
					mApp.isVacation());
			App.commitEditor(editor);
			mApp.initAlarmService();
		}
	}
	
	public void getSemester() {
		Semester semester = this.mDBHelper.getActiveSemester(this.mUserDB);
		mCallback.callback(createMessage(MESSAGE_GET_SEMESTER, semester));

		Listener<SemestersResult> listener = new Listener<SemestersResult>() {

			@Override
			public void onResponse(SemestersResult result) {
				if (result != null && result.success) {
					initApplicationData(result.semesters, result.active_semester_id);
					Semester semester = mDBHelper.getActiveSemester(mUserDB);
					// Semester semester = getSemesterById(result.semesters,
					// result.active_semester_id);
					mHandler.sendMessage(createMessage(MESSAGE_GET_SEMESTER,
							semester));
				}
			}
			
		};
		RestEntity restEntity = RestService.get().getSemesters();
		requestBuilder.buildAndAddRequest(restEntity, SemestersResult.class, MESSAGE_GET_SEMESTER, listener);
	}

	public void getConfigParams() {
		RestEntity restEntity = RestService.get().getConfigParams();
		requestBuilder.buildAndAddRequest(restEntity, ConfigParamsResult.class, MESSAGE_GET_CONFIG_PARAMS, new Listener<ConfigParamsResult>() {

			@Override
			public void onResponse(ConfigParamsResult result) {
				if (result.success) {
					mApp.saveConfigParams(result.config_params);
				}
			}
			
		});
	}

	public void uploadPhoto(final File photoFile) {
		new Thread() {
			@Override
			public void run() {
				RegistResult result = RestService.get().uploadPhoto(
						photoFile);
				if (result != null && result.success) {
					mDBHelper.saveUser(mUserDB, result.user);
				}
				mHandler.sendMessage(createMessage(MESSAGE_UPLOAD_PHOTO, result));
			}
		}.start();
	}

	public void uploadPhotos(final List<AvatarOrFile> avatarOrFiles,
			final int userId) {
		new Thread() {
			@Override
			public void run() {
				UploadAvatatsResult result = RestService.get()
						.uploadPhotos(avatarOrFiles);
				if (result != null && result.success) {
					for (Avatar avatar : result.avatars) {
						avatarOrFiles.add(new AvatarOrFile(avatar));
					}
					mDBHelper.saveAvatars(mUserDB, result.avatars, userId);
					if (result.avatars != null && result.avatars.length > 0) {
						User myself = mApp.getMyself();
						myself.tiny_avatar_url = result.avatars[0].tiny_avatar_url;
						myself.origin_avatar_url = result.avatars[0].large_avatar_url;
						mDBHelper.saveUser(mUserDB, myself);
					}
				}
				mHandler.sendMessage(createMessage(MESSAGE_UPLOAD_PHOTO, result));
			}
		}.start();
	}

	public void getCourses(final int userId) {
		RestEntity restEntity = RestService.get().getCourses(userId);
		requestBuilder.buildAndAddRequest(restEntity, CoursesResult.class, MESSAGE_GET_COURSES, true);
		requestBuilder.buildAndAddRequest(restEntity, CoursesResult.class, MESSAGE_GET_COURSES);
	}

	public void getCourses() {
		getCourses(false);
	}

	public void getCourses(boolean local) {
		Log.i(getClass().getSimpleName(), "getCourses");
		List<Course> courses = this.mDBHelper.getCourses(this.mUserDB);
		List<Event> events = this.mDBHelper.getEvents(this.mUserDB);
		CoursesResult result = new CoursesResult();
		Course[] courseArray = new Course[courses.size()];
		result.courses = courses.toArray(courseArray);
		Event[] eventArray = new Event[events.size()];
		result.events = events.toArray(eventArray);
		List<UserSticker> savedStickerList = App.getInstance().getSavedStickerList();
		result.stickers = savedStickerList.toArray(new UserSticker[savedStickerList.size()]);
		if (local) {
			result.finished = true;
			mCallback.callback(createMessage(MESSAGE_GET_COURSES, result));
			return;
		}
		result.finished = false;
		mCallback.callback(createMessage(MESSAGE_GET_COURSES, result));
		Listener<CoursesResult> listener = new Listener<CoursesResult>() {

			@Override
			public void onResponse(CoursesResult result) {
				if (result != null && result.success) {
					result.finished = true;
					mDBHelper.saveCourses(mUserDB, result.courses);
					List<Course> courses = mDBHelper.getCourses(mUserDB);
					Course[] courseArray = new Course[courses.size()];
					result.courses = courses.toArray(courseArray);
					mDBHelper.saveEvents(mUserDB, result.events);
					App.getInstance().saveStickerList(Arrays.asList(result.stickers));
				}
				mHandler.sendMessage(createMessage(MESSAGE_GET_COURSES, result));
			}
		};
		RestEntity restEntity = RestService.get().getCourses();
		requestBuilder.buildAndAddRequest(restEntity, CoursesResult.class, MESSAGE_GET_COURSES, listener);
	}

	public void getGraph(final List<String> thirdPartIds, final int category,
			final int page, final int limit) {
		Listener<UsersResult> listener = new Listener<UsersResult>() {

			@Override
			public void onResponse(UsersResult result) {
				SNSUsersResult snsUsersResult = new SNSUsersResult();
				if (result.success) {
					List<User> users = new ArrayList<User>();
					users.addAll(Arrays.asList(result.users));
					mDBHelper.saveUsers(mUserDB, users);
					snsUsersResult = new SNSUsersResult();
					snsUsersResult.success = true;
					snsUsersResult.users = result.users;
					List<User> friends = mDBHelper.getFriends(mUserDB);
					List<User> listA = new ArrayList<User>(users);
					List<User> common_friends = new ArrayList<User>();
					List<User> not_friends = new ArrayList<User>();
					for (User user : listA) {
						if (friends.contains(user)) {
							common_friends.add(user);
						} else {
							not_friends.add(user);
						}
					}
					User[] friendUsers = new User[common_friends.size()];
					User[] notFriendUsers = new User[not_friends.size()];
					snsUsersResult.friends = common_friends.toArray(friendUsers);
					snsUsersResult.not_friends = not_friends
							.toArray(notFriendUsers);
					snsUsersResult.invite_third_part_ids = result.invite_third_part_ids;
				} 
				mHandler.sendMessage(createMessage(MESSAGE_GET_GRAPH,
						snsUsersResult));
			}
		};
		RestEntity restEntity = RestService.get().getGraph(thirdPartIds, category, page, limit);
		requestBuilder.buildAndAddRequest(restEntity, UsersResult.class, MESSAGE_GET_GRAPH, listener);
	}

	public void searchUsers(final String term, final int page, final int limit) {
		Listener<UsersResult> listener = new Listener<UsersResult>() {

			@Override
			public void onResponse(UsersResult result) {
				WrappedResult wrappedResult = new WrappedResult();
				wrappedResult.param = term;
				wrappedResult.result = result;
				mHandler.sendMessage(createMessage(MESSAGE_SEARCH_USERS,
						wrappedResult));
			}
		};
		RestEntity restEntity = RestService.get().searchUsers(term, page, limit);
		requestBuilder.buildAndAddRequest(restEntity, UsersResult.class, MESSAGE_SEARCH_USERS, listener);
	}
	
	public void searchSchools(final String term, final int page, final int limit) {
		RestEntity restEntity = RestService.get().searchSchools(term, page, limit);
		Header header = BasicScheme.authenticate(new UsernamePasswordCredentials(SEARCH_API_USERNAME, SEARCH_API_PASSWORD), "UTF-8", false);
		@SuppressWarnings("unchecked")
		GsonRequest<SchoolsResult> request = requestBuilder.buildRequest(restEntity, restEntity.clientClass, MESSAGE_SEARCH_SCHOOLS, false, null, false);
		request.addHeader(header.getName(), header.getValue());
		queue.add(request);
	}
	
	public void searchDepartments(final String term, final int school_id, final int page, final int limit) {
		RestEntity restEntity = RestService.get().searchDepartments(term, school_id, page, limit);
		Header header = BasicScheme.authenticate(new UsernamePasswordCredentials(SEARCH_API_USERNAME, SEARCH_API_PASSWORD), "UTF-8", false);
		@SuppressWarnings("unchecked")
		GsonRequest<DepartmentsResult> request = requestBuilder.buildRequest(restEntity, restEntity.clientClass, MESSAGE_SEARCH_DEPARTMENTS, false, null, false);
		request.addHeader(header.getName(), header.getValue());
		queue.add(request);
	}
	
	public void searchAllDepartments(final int school_id) {
		RestEntity restEntity = RestService.get().searchAllDepartments(school_id);
		Header header = BasicScheme.authenticate(new UsernamePasswordCredentials(SEARCH_API_USERNAME, SEARCH_API_PASSWORD), "UTF-8", false);
		@SuppressWarnings("unchecked")
		GsonRequest<DepartmentsResult> request = requestBuilder.buildRequest(restEntity, restEntity.clientClass, MESSAGE_SEARCH_ALL_DEPARTMENTS, false, null, false);
		request.addHeader(header.getName(), header.getValue());
		queue.add(request);
	}

	public void searchUserByGeziId(final String term) {
		RestEntity restEntity = RestService.get().searchUserByGeziId(term);
		requestBuilder.buildAndAddRequest(restEntity, UserResult.class, MESSAGE_SEARCH_USER_BY_GEZI_ID);
	}

	public void getClassmates(final int page, final int limit, final int type) {
		RestEntity restEntity = RestService.get().getClassmates(page, limit, type);
		requestBuilder.buildAndAddRequest(restEntity, UsersResult.class, MESSAGE_GET_CLASSMATES);
	}

	public void getFollowers(final int page, final int limit) {
		RestEntity restEntity = RestService.get().getFollowers(page, limit);
		requestBuilder.buildAndAddRequest(restEntity, UsersResult.class, MESSAGE_GET_FOLLOWERS);
	}

	public void visit(final int userId) {
		RestEntity restEntity = RestService.get().visit(userId);
		requestBuilder.buildAndAddRequest(restEntity, BaseResult.class, MESSAGE_VISIT);
	}

	public void getAccountInfo() {
		AccountResult result = mApp.getAccountInfo();
		if (result != null) {
			mHandler.sendMessage(createMessage(MESSAGE_GET_ACCOUNT_INFO, result));
			return;
		}
		Listener<AccountResult> listener = new Listener<AccountResult>() {

			@Override
			public void onResponse(AccountResult result) {
				if (result != null && result.success) {
					mApp.setAccountInfo(result);
				}
				mHandler.sendMessage(createMessage(MESSAGE_GET_ACCOUNT_INFO,
						result));
			}
		};
		RestEntity restEntity = RestService.get().getAccountInfo();
		requestBuilder.buildAndAddRequest(restEntity, AccountResult.class, MESSAGE_GET_ACCOUNT_INFO, listener);
	}

	public void changePassword(final String oldPassword, final String password) {
		RestEntity restEntity = RestService.get().changePassword(oldPassword, password);
		requestBuilder.buildAndAddRequest(restEntity, BaseResult.class, MESSAGE_CHANGE_PASSWORD);
	}

	public void deleteCourse(final int courseId) {
		Listener<BaseResult> listener = new Listener<BaseResult>() {

			@Override
			public void onResponse(BaseResult result) {
				if (result != null && result.success) {
					mDBHelper.deleteCourse(mUserDB, courseId);
				}
				mHandler.sendMessage(createMessage(MESSAGE_DELETE_COURSE,
						result));
			}
		};
		RestEntity restEntity = RestService.get().removeCourse(courseId);
		requestBuilder.buildAndAddRequest(restEntity, BaseResult.class, MESSAGE_DELETE_COURSE, listener);
	}

	public void removeNote(final int noteId) {
		RestEntity restEntity = RestService.get().removeNote(noteId);
		requestBuilder.buildAndAddRequest(restEntity, BaseResult.class, MESSAGE_REMOVE_NOTE);
	}

	public void getStudents(final int courseId, final int page, final int limit) {
		RestEntity restEntity = RestService.get().getStudents(courseId, page, limit);
		requestBuilder.buildAndAddListRequest(restEntity, User.class, MESSAGE_GET_STUDENTS);
	}

	public void getFriends(final int page, final int limit, boolean isNeedLocalData) {
		Listener<UsersResult> listener = new Listener<UsersResult>() {

			@Override
			public void onResponse(UsersResult result) {
				if (result != null && result.success) {
					List<User> users = new ArrayList<User>();
					users.addAll(Arrays.asList(result.users));
					mDBHelper.saveFriends(mUserDB, users);
				}
				mHandler.sendMessage(createMessage(MESSAGE_GET_FRIENDS,
						result));
			}
		};
		RestEntity restEntity = RestService.get().getFriends(page, limit);
		if (page == 1 && isNeedLocalData) {
			List<User> users = this.mDBHelper.getFriends(this.mUserDB);
			UsersResult result = new UsersResult();
			User[] userArray = new User[users.size()];
			result.users = users.toArray(userArray);
			result.success = true;
			result.finished = false;
			mCallback.callback(createMessage(MESSAGE_GET_FRIENDS, result));
		}
		requestBuilder.buildAndAddRequest(restEntity, UsersResult.class, MESSAGE_GET_FRIENDS, listener);
	}

	public void getFriends(boolean isNeedLocalData) {
		getFriends(1, 1000, isNeedLocalData);
	}

	public void addFriend(final User user) {
		Listener<BaseResult> listener = new Listener<BaseResult>() {

			@Override
			public void onResponse(BaseResult result) {
				if (result != null && result.success) {
					mDBHelper.addFriend(mUserDB, user.id);
					mDBHelper.saveUser(mUserDB, user);
					mHandler.sendMessage(createMessage(MESSAGE_ADD_FRIEND, result));
				}
			}
		};
		RestEntity restEntity = RestService.get().addFriend(user.id);
		requestBuilder.buildAndAddRequest(restEntity, BaseResult.class, MESSAGE_ADD_FRIEND, listener);
	}

	public void removeFriend(final int userId) {
		Listener<BaseResult> listener = new Listener<BaseResult>() {

			@Override
			public void onResponse(BaseResult result) {
				if (result != null && result.success) {
					mDBHelper.removeFriend(mUserDB, userId);
					mDBHelper.deleteUser(mUserDB, userId);
					mHandler.sendMessage(createMessage(MESSAGE_REMOVE_FRIEND,
							result));
				}
			}
		};
		RestEntity restEntity = RestService.get().removeFriend(userId);
		requestBuilder.buildAndAddRequest(restEntity, BaseResult.class, MESSAGE_REMOVE_FRIEND, listener);
	}

	public void getFreeTimeFriends(final int day_of_week, final int time_slot,
			final int page, final int limit) {
		RestEntity restEntity = RestService.get().getFreeTimeFriends(day_of_week, time_slot, page, limit);
		requestBuilder.buildAndAddRequest(restEntity, UsersResult.class, MESSAGE_GET_FREE_TIME_FRIENDS);
	}

	public void updateOauth(final int category, final String id,
			final String token, final String name, final Date expirationDate) {
		RestEntity restEntity = RestService.get().updateOauth(category, id, token, name, expirationDate);
		requestBuilder.buildAndAddRequest(restEntity, BaseResult.class, MESSAGE_UPDATE_OAUTH);
	}

	public void updateUser(final Map<String, Object> map,
			final String schoolName, final String departmentName,
			final String[] life_situations, final int county_id) {
		Listener<RegistResult> listener = getAutoSaveUserResponseListener(MESSAGE_UPDATE_USER);
		RestEntity restEntity = RestService.get().updateUser(map, schoolName, departmentName, life_situations, county_id);
		requestBuilder.buildAndAddRequest(restEntity, RegistResult.class, MESSAGE_UPDATE_USER, listener);
	}

	public void getNotes(final int courseId) {
		RestEntity restEntity = RestService.get().getNotesUnsafe(courseId);
		requestBuilder.buildAndAddListRequest(restEntity, Note.class, MESSAGE_GET_NOTES, true, true);
		requestBuilder.buildAndAddListRequest(restEntity, Note.class, MESSAGE_GET_NOTES);
	}

	public void getUser(final int userId) {
		RestEntity restEntity = RestService.get().getUser(userId);
		requestBuilder.buildAndAddRequest(restEntity, UserDetailsResult.class, MESSAGE_GET_USER, true);
		requestBuilder.buildAndAddRequest(restEntity, UserDetailsResult.class, MESSAGE_GET_USER);
	}
	
	public void login(String account, String password){
		Listener<RegistResult> listener = getAutoSaveUserResponseListener(MESSAGE_LOGIN);
		RestEntity restEntity = RestService.get().login(account, password);
		requestBuilder.buildAndAddRequest(restEntity, RegistResult.class, MESSAGE_LOGIN, listener);
	}
	
	public void addEvent(final int eventId) {
		Listener<EventResult> listener = new Listener<EventResult>() {

			@Override
			public void onResponse(EventResult result) {
				if (result != null && result.success) {
					mDBHelper.addEvent(mUserDB, result.event);
				}
				mHandler.sendMessage(createMessage(MESSAGE_ADD_EVENT, result));
			}
			
		};
		RestEntity restEntity = RestService.get().addEvent(eventId);
		requestBuilder.buildAndAddRequest(restEntity, EventResult.class, MESSAGE_ADD_EVENT, listener);
	}
	
	public void deleteEvent(final int eventId) {
		Listener<BaseResult> listener = new Listener<BaseResult>() {

			@Override
			public void onResponse(BaseResult result) {
				if (result != null && result.success) {
					mDBHelper.deleteEvent(mUserDB, eventId);
				}
				mHandler.sendMessage(createMessage(MESSAGE_DELETE_EVENT, result));
			}
			
		};
		RestEntity restEntity = RestService.get().deleteEvent(eventId);
		requestBuilder.buildAndAddRequest(restEntity, BaseResult.class, MESSAGE_DELETE_EVENT, listener);
	}
	
	@SuppressWarnings("unchecked")
	public void getEvents(final int page, final int limit) {
		Listener<EventsResult> listener = new Listener<EventsResult>() {

			@Override
			public void onResponse(EventsResult arg0) {
				mHandler.sendMessage(createMessage(MESSAGE_GET_EVENT, arg0));
			}
			
		};
		RestEntity restEntity = RestService.get().getEvents(page, limit);

		if (page == 1) {
			requestBuilder.buildAndAddRequest(restEntity, restEntity.clientClass, MESSAGE_GET_EVENT, true);
		}
		requestBuilder.buildAndAddRequest(restEntity, restEntity.clientClass, MESSAGE_GET_EVENT, listener);
	}
	
	@SuppressWarnings("unchecked")
	public void getJoinedEvents(){
		Listener<EventsResult> listener = new Listener<EventsResult>() {

			@Override
			public void onResponse(EventsResult result) {
				if (result != null && result.success) {
					mDBHelper.saveEvents(mUserDB, result.events);
				}
				mHandler.sendMessage(createMessage(MESSAGE_GET_JOINED_EVENT, result));
			}
			
		};
		RestEntity restEntity = RestService.get().getJoinedEvents();
		requestBuilder.buildAndAddRequest(restEntity, restEntity.clientClass, MESSAGE_GET_JOINED_EVENT, listener);
	}
	
	public void getEventUsers(final int eventId, final int page, final int limit){
		RestEntity restEntity = RestService.get().getEventUsers(eventId, page, limit);
		if (page == 1) {
			requestBuilder.buildAndAddRequest(restEntity, UsersResult.class, MESSAGE_GET_EVENT_USERS, true);
		}
		requestBuilder.buildAndAddRequest(restEntity, UsersResult.class, MESSAGE_GET_EVENT_USERS);
	}
	
	public void updateSemesterBeginDate(final String begin_date) {
		RestEntity restEntity = RestService.get().updateSemesterBeginDate(begin_date);
		requestBuilder.buildAndAddRequest(restEntity, BaseResult.class, MESSAGE_UPDATE_SEMESTER_BEGIN_DATE);
	}
	
	public void getClassTime(){
		RestEntity restEntity = RestService.get().getUserClassTime();
		requestBuilder.buildAndAddRequest(restEntity, ClassTimeResult.class, MESSAGE_GET_USER_CLASSTIME);
	}
	
	public void saveClassTime(String classTimeString){
		RestEntity restEntity = RestService.get().saveUserClassTime(classTimeString);
		requestBuilder.buildAndAddRequest(restEntity, BaseResult.class, MESSAGE_POST_USER_CLASSTIME);
	}

	private Message createMessage(int what, Object obj) {
		return Message.obtain(null, what, obj);
	}

	private static class UIHandler extends Handler {
		WeakReference<Activity> mContext;
		WeakReference<DataCallback> mWeakReferenceCallback;
		DataCallback mCallback;
		UIHandler(Looper looper, Activity activity, DataCallback dataCallback) {
			super(looper);
			this.mContext = new WeakReference<Activity>(activity);
			if (activity == dataCallback) {
				mWeakReferenceCallback = new WeakReference<DataCallback>(dataCallback);
			}else {
				this.mCallback = dataCallback;
			}
		}

		public void handleMessage(Message msg) {
			if (msg != null) {
				if (mWeakReferenceCallback != null) {
					mCallback = mWeakReferenceCallback.get();
				}
				if (mCallback != null && mContext.get() != null && !mContext.get().isFinishing()) {
					mCallback.callback(msg);
				}
				super.handleMessage(msg);
			}
		}
	}
	
	class AutoSaveResponseListener implements Listener<RegistResult>{
		
		Listener<RegistResult> listener;
		AutoSaveResponseListener(Listener<RegistResult> listener){
			this.listener = listener;
		}
		
		@Override
		public void onResponse(RegistResult result) {
			if (result != null && result.success) {
				saveUserInfo(result);
			}
			listener.onResponse(result);
		}
	}
	
	private Listener<RegistResult> getAutoSaveUserResponseListener(int category){
		return new AutoSaveResponseListener(requestBuilder.buildRequestListener(RegistResult.class, category));
	}
}
