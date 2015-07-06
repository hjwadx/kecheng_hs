package fm.jihua.kecheng.utils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.SparseArray;
import fm.jihua.kecheng.App;
import fm.jihua.kecheng.rest.entities.Avatar;
import fm.jihua.kecheng.rest.entities.Course;
import fm.jihua.kecheng.rest.entities.CourseUnit;
import fm.jihua.kecheng.rest.entities.Event;
import fm.jihua.kecheng.rest.entities.Examination;
import fm.jihua.kecheng.rest.entities.OfflineData;
import fm.jihua.kecheng.rest.entities.OfflineData.Operation;
import fm.jihua.kecheng.rest.entities.Patch;
import fm.jihua.kecheng.rest.entities.Rating;
import fm.jihua.kecheng.rest.entities.Semester;
import fm.jihua.kecheng.rest.entities.User;

public class DatabaseHelper extends SQLiteOpenHelper {

	public static final String DATABASE_USER_FILE = "kechengbiao";
	public static final String DATABASE_TABLE_COURSES = "courses";
//	public static final String DATABASE_TABLE_COURSETIMES = "coursetimes";      //dumped
	public static final String DATABASE_TABLE_SEMESTER = "semester";
//	public static final String DATABASE_TABLE_COURSESTUDENTS = "courseusers";   //dumped
	public static final String DATABASE_TABLE_FRIENDS = "friends";
	public static final String DATABASE_TABLE_USERS = "users";
//	public static final String DATABASE_TABLE_NOTES = "notes";    //dumped
//	public static final String DATABASE_TABLE_PLANS = "plans";    //dumped
//	public static final String DATABASE_TABLE_NOTIFICATIONS = "notifications";   //dumped
	public static final String DATABASE_TABLE_PATCHES = "patches";
	public static final String DATABASE_TABLE_EXAMINATIONS = "examinations";
//	public static final String DATABASE_TABLE_ACTIVITIES = "activities";   //dumped
	public static final String DATABASE_TABLE_RATINGS = "ratings";
//	public static final String DATABASE_TABLE_TAGS = "tags";   //dumped
//	public static final String DATABASE_TABLE_CONFIG_PARAMS = "config_params";   //dumped
//	public static final String DATABASE_TABLE_MEDALS = "medals";      //dumped
//	public static final String DATABASE_TABLE_MEDALSUSERS = "medalsusers";    //dumped
	public static final String DATABASE_TABLE_AVATARS = "avatars";
	public static final String DATABASE_TABLE_OFFLINE = "offline";
	public static final String DATABASE_TABLE_EVENTS = "events";
	public static final String DATABASE_TABLE_COURSEUNITS = "courseunits";

	private final static String DATABASE_NAME = DATABASE_USER_FILE;
	private final static int DATABASE_VERSION = 1;
	private final String TAG = getClass().getSimpleName();

	private Object userLock = new Object();
	private Object courseLock = new Object();
	private Object semesterLock = new Object();
	private Object eventLock = new Object();

	App mApp;
	// private Object transactionLock = new Object();

	Context mContext;

	public DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		this.mContext = context;
		mApp = (App) mContext.getApplicationContext();
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String course_create = "CREATE TABLE IF NOT EXISTS "
				+ DATABASE_TABLE_COURSES
				+ "(ID INTEGER PRIMARY KEY, NAME TEXT, TEACHER TEXT, DESCRIPTION TEXT, STARTWEEK INTEGER, ENDWEEK INTEGER, COURSETYPE STRING, SEMESTERID INTEGER, COURSE_TIME_STRING STRING, STUDENTS_COUNT INTEGER);";
		String semester_create = "CREATE TABLE IF NOT EXISTS "
				+ DATABASE_TABLE_SEMESTER
				+ "(ID INTEGER PRIMARY KEY, YEAR INTEGER, BEGINDATE LONG, ENDDATE LONG, SCHOOLID INTEGER, NAME TEXT, SEMESTER_ID INTEGER, MODIFIED INTEGER DEFAULT 0);";
		String user_create = "CREATE TABLE IF NOT EXISTS "
				+ DATABASE_TABLE_USERS
				+ "(ID INTEGER PRIMARY KEY,NAME TEXT,AVATAR TEXT,SCHOOL TEXT, MAJOR TEXT, GRADE INTEGER, RENREN_ID LONG, BIG_AVATAR TEXT, SEX INTEGER, GEZI_ID INTEGER, BACKGROUND TEXT, "
				+ "BIRTHDAY TEXT, ASTROLOGY TEXT, SIGNATURE TEXT, DEGREE TEXT, GOAL TEXT, SITUATIONS TEXT, RELATIONSHIP TEXT, SHOW_BIRTHDAY INTEGER, DORMITORY TEXT, CITY TEXT);";
		String friends_create = "CREATE TABLE IF NOT EXISTS "
				+ DATABASE_TABLE_FRIENDS
				+ "(ID INTEGER PRIMARY KEY, USERID INTEGER);";
		String patches_create = "CREATE TABLE IF NOT EXISTS "
				+ DATABASE_TABLE_PATCHES
				+ "(ID INTEGER PRIMARY KEY,COURSEID INTEGER, KEY TEXT, VALUE TEXT, DAY_AND_SLOTS TEXT);";
		String examination_create = "CREATE TABLE IF NOT EXISTS "
				+ DATABASE_TABLE_EXAMINATIONS
				+ "(ID INTEGER PRIMARY KEY, NAME TEXT, TIME LONG, ROOM TEXT, REMIND INTEGER DEFAULT 1, COURSEID INTEGER);";
		String ratings_create = "CREATE TABLE IF NOT EXISTS "
				+ DATABASE_TABLE_RATINGS
				+ "(ID INTEGER PRIMARY KEY, COURSEID INTEGER, SCORE FLOAT, TAGS TEXT, TAGSCOUNT TEXT ,USERID INTEGER, COMMENT TEXT, RATINGS_COUNT INTEGER);";
		String avatars_create = "CREATE TABLE IF NOT EXISTS "
				+ DATABASE_TABLE_AVATARS
				+ "(ID INTEGER PRIMARY KEY,TINY TEXT, LARGE TEXT, ORIGIN TEXT, AVATAR_ORDER INTEGER, USERID INTEGER);";
		String offline_create = "CREATE TABLE IF NOT EXISTS "
				+ DATABASE_TABLE_OFFLINE
				+ "(ID INTEGER PRIMARY KEY,CATEGORY TEXT, CONTENT TEXT, OPERATION INTEGER, SEMESTER_ID INTEGER);";
		String events_create = "CREATE TABLE IF NOT EXISTS "
				+ DATABASE_TABLE_EVENTS
				+ "(ID INTEGER PRIMARY KEY, NAME TEXT, START_TIME LONE, END_TIME LONE, CITY TEXT, PROVINCE TEXT, LOCATION TEXT, INITIATOR TEXT, CONTENT TEXT, POSTER_URL TEXT, TIME_SLOTS TEXT, SCHOOL TEXT);";
		String courseunit_create = "CREATE TABLE IF NOT EXISTS "
				+ DATABASE_TABLE_COURSEUNITS
				+ "(ID INTEGER PRIMARY KEY, DAYOFWEEK INTEGER, TIMESLOTS TEXT, ROOM TEXT, WEEKS TEXT, COURSEID INTEGER);";
		db.beginTransaction();
		try {
			// Create tables & test data
			db.execSQL(course_create);
			db.execSQL(semester_create);
			db.execSQL(user_create);
			db.execSQL(friends_create);
			db.execSQL(patches_create);
			db.execSQL(examination_create);
			db.execSQL(ratings_create);
			db.execSQL(avatars_create);
			db.execSQL(offline_create);
			db.execSQL(events_create);
			db.execSQL(courseunit_create);
			// db.execSQL(activities_create);
			db.setTransactionSuccessful();
		} catch (SQLException e) {
			AppLogger.e(TAG,
					"Error creating tables and debug data" + e.getMessage());
		} finally {
			db.endTransaction();
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		if (oldVersion >= newVersion) {
			return;
		}
//		db.beginTransaction();
//		try {
//			if (oldVersion < 2) {
//				String user_create = "CREATE TABLE IF NOT EXISTS "
//						+ DATABASE_TABLE_USERS
//						+ "(ID INTEGER PRIMARY KEY,NAME TEXT,AVATAR TEXT,SCHOOL TEXT, MAJOR TEXT, GRADE INTEGER);";
//
//				db.execSQL(user_create);
//			}
//			if (oldVersion < 3) {
//				db.execSQL("ALTER TABLE " + DATABASE_TABLE_USERS
//						+ " ADD COLUMN RENREN_ID LONG");
//				db.execSQL("ALTER TABLE " + DATABASE_TABLE_USERS
//						+ " ADD COLUMN BIG_AVATAR TEXT");
//				db.execSQL("ALTER TABLE " + DATABASE_TABLE_USERS
//						+ " ADD COLUMN SEX INTEGER");
//				String friends_create = "CREATE TABLE IF NOT EXISTS "
//						+ DATABASE_TABLE_FRIENDS
//						+ "(ID INTEGER PRIMARY KEY, USERID INTEGER);";
//				db.execSQL(friends_create);
//			}
//			if (oldVersion < 4) {
//				db.execSQL("ALTER TABLE " + DATABASE_TABLE_SEMESTER
//						+ " ADD COLUMN NAME TEXT");
//				db.execSQL("ALTER TABLE " + DATABASE_TABLE_SEMESTER
//						+ " ADD COLUMN SEMESTER_ID INTEGER");
//				db.execSQL("ALTER TABLE " + DATABASE_TABLE_COURSES
//						+ " ADD COLUMN SEMESTERID INTEGER");
//				db.execSQL("ALTER TABLE " + DATABASE_TABLE_COURSES
//						+ " ADD COLUMN COURSE_TIME_STRING STRING");
//			}
//			if (oldVersion < 5) {
//				db.execSQL("ALTER TABLE " + DATABASE_TABLE_SEMESTER
//						+ " ADD COLUMN MODIFIED INTEGER DEFAULT 0");
//			}
//			if (oldVersion < 6) {
//				String patches_create = "CREATE TABLE IF NOT EXISTS "
//						+ DATABASE_TABLE_PATCHES
//						+ "(ID INTEGER PRIMARY KEY,COURSEID INTEGER, KEY TEXT, VALUE TEXT);";
//				db.execSQL(patches_create);
//			}
//			if (oldVersion < 7) {
//				db.execSQL("ALTER TABLE " + DATABASE_TABLE_PATCHES
//						+ " ADD COLUMN COURSETIMEID INTEGER");
//			}
//			if (oldVersion < 8) {
//				db.execSQL("ALTER TABLE " + DATABASE_TABLE_USERS
//						+ " ADD COLUMN GEZI_ID INTEGER");
//				db.execSQL("ALTER TABLE " + DATABASE_TABLE_USERS
//						+ " ADD COLUMN BACKGROUND TEXT");
//			}
//			if (oldVersion < 9) {
//				String examination_create = "CREATE TABLE IF NOT EXISTS "
//						+ DATABASE_TABLE_EXAMINATIONS
//						+ "(ID INTEGER PRIMARY KEY, NAME TEXT, TIME LONG, ROOM TEXT, REMIND INTEGER DEFAULT 1, COURSEID INTEGER);";
//				db.execSQL(examination_create);
//			}
//			if (oldVersion < 10) {
//				String ratings_create = "CREATE TABLE IF NOT EXISTS "
//						+ DATABASE_TABLE_RATINGS
//						+ "(ID INTEGER PRIMARY KEY, COURSEID INTEGER, SCORE FLOAT, TAGS TEXT, TAGSCOUNT TEXT, USERID INTEGER, COMMENT TEXT, RATINGS_COUNT INTEGER);";
//				db.execSQL(ratings_create);
//			}
//			if (oldVersion < 11) {
//				db.execSQL("ALTER TABLE " + DATABASE_TABLE_COURSES
//						+ " ADD COLUMN STUDENTS_COUNT INTEGER");
//			}
//			if (oldVersion < 12) {
//				// "BIRTHDAY TEXT, ASTROAppLoggerY TEXT, SIGNATURE TEXT, DEGREE
//				// TEXT, GOAL TEXT, SITUATIONS TEXT, RELATIONSHIP TEXT,
//				// SHOW_BIRTHDAY INTEGER
//				db.execSQL("ALTER TABLE " + DATABASE_TABLE_USERS
//						+ " ADD COLUMN BIRTHDAY TEXT");
//				db.execSQL("ALTER TABLE " + DATABASE_TABLE_USERS
//						+ " ADD COLUMN ASTROAppLoggerY TEXT");
//				db.execSQL("ALTER TABLE " + DATABASE_TABLE_USERS
//						+ " ADD COLUMN SIGNATURE TEXT");
//				db.execSQL("ALTER TABLE " + DATABASE_TABLE_USERS
//						+ " ADD COLUMN DEGREE TEXT");
//				db.execSQL("ALTER TABLE " + DATABASE_TABLE_USERS
//						+ " ADD COLUMN GOAL TEXT");
//				db.execSQL("ALTER TABLE " + DATABASE_TABLE_USERS
//						+ " ADD COLUMN SITUATIONS TEXT");
//				db.execSQL("ALTER TABLE " + DATABASE_TABLE_USERS
//						+ " ADD COLUMN RELATIONSHIP TEXT");
//				db.execSQL("ALTER TABLE " + DATABASE_TABLE_USERS
//						+ " ADD COLUMN SHOW_BIRTHDAY INTEGER");
//				String avatars_create = "CREATE TABLE IF NOT EXISTS "
//						+ DATABASE_TABLE_AVATARS
//						+ "(ID INTEGER PRIMARY KEY,TINY TEXT, LARGE TEXT, ORIGIN TEXT, AVATAR_ORDER INTEGER, USERID INTEGER);";
//				db.execSQL(avatars_create);
//			}
//			if (oldVersion < 13) {
//				String offline_create = "CREATE TABLE IF NOT EXISTS "
//						+ DATABASE_TABLE_OFFLINE
//						+ "(ID INTEGER PRIMARY KEY,TYPE INTEGER, CONTENT TEXT);";
//				db.execSQL(offline_create);
//			}
//			if (oldVersion < 14) {
//				String events_create = "CREATE TABLE IF NOT EXISTS "
//						+ DATABASE_TABLE_EVENTS
//						+ "(ID INTEGER PRIMARY KEY, NAME TEXT, START_DATE TEXT, CITY TEXT, DISTRICT TEXT, LOCAL TEXT, INITIATOR TEXT, INTRODUCTION TEXT, POSTER TEXT, DURATION INTEGER);";
//				db.execSQL(events_create);
//				String courseunit_create = "CREATE TABLE IF NOT EXISTS "
//						+ DATABASE_TABLE_COURSEUNITS
//						+ "(ID INTEGER PRIMARY KEY, DAYOFWEEK INTEGER, TIMESLOTS TEXT, ROOM TEXT, WEEKS TEXT, COURSEID INTEGER);";
//				db.execSQL(courseunit_create);
//				db.execSQL("ALTER TABLE " + DATABASE_TABLE_USERS
//						+ " ADD COLUMN DORMITORY TEXT");
//				if (mApp.getMyUserId() != 0) {
//					execSQL(db, "delete from " + DATABASE_TABLE_COURSES
//							+ " where id not in (select COURSEID from "
//							+ DATABASE_TABLE_COURSESTUDENTS
//							+ " where USERID <> " + mApp.getMyUserId() + " );");
//				}
//				execSQL(db, "drop table if exists "
//						+ DATABASE_TABLE_COURSESTUDENTS + ";");
//				execSQL(db, "drop table if exists " + DATABASE_TABLE_NOTES
//						+ ";");
//				execSQL(db, "drop table if exists " + DATABASE_TABLE_TAGS + ";");
//				execSQL(db, "drop table if exists " + DATABASE_TABLE_PLANS
//						+ ";");
//				execSQL(db, "drop table if exists "
//						+ DATABASE_TABLE_NOTIFICATIONS + ";");
//				execSQL(db, "drop table if exists " + DATABASE_TABLE_MEDALS
//						+ ";");
//				execSQL(db, "drop table if exists "
//						+ DATABASE_TABLE_MEDALSUSERS + ";");
//				execSQL(db, "drop table if exists "
//						+ DATABASE_TABLE_COURSETIMES + ";");
//				execSQL(db, "drop table if exists "
//						+ DATABASE_TABLE_PATCHES + ";");
//				String patches_create = "CREATE TABLE IF NOT EXISTS "
//						+ DATABASE_TABLE_PATCHES
//						+ "(ID INTEGER PRIMARY KEY,COURSEID INTEGER, KEY TEXT, VALUE TEXT, DAY_AND_SLOTS TEXT);";
//				db.execSQL(patches_create);
//			}
//			if (oldVersion < 15) {
//				List<OfflineData<Course>> offlineDatas = getOldOfflineData(db, Const.COURSE_OFFLINE);
//				execSQL(db, "drop table if exists "
//						+ DATABASE_TABLE_OFFLINE + ";");
//				String offline_create = "CREATE TABLE IF NOT EXISTS "
//						+ DATABASE_TABLE_OFFLINE
//						+ "(ID INTEGER PRIMARY KEY,CATEGORY TEXT, CONTENT TEXT, OPERATION INTEGER, SEMESTER_ID INTEGER);";
//				db.execSQL(offline_create);
//				for (OfflineData<Course> offlineData : offlineDatas) {
//					saveOfflineData(db, offlineData);
//				}
//			}
//
//			if (oldVersion < 16) {
//				execSQL(db, "drop table if exists "
//						+ DATABASE_TABLE_EVENTS + ";");
//				String events_create = "CREATE TABLE IF NOT EXISTS "
//						+ DATABASE_TABLE_EVENTS
//						+ "(ID INTEGER PRIMARY KEY, NAME TEXT, START_TIME LONE, END_TIME LONE, CITY TEXT, PROVINCE TEXT, LOCATION TEXT, INITIATOR TEXT, CONTENT TEXT, POSTER_URL TEXT, TIME_SLOTS TEXT, SCHOOL TEXT);";
//				db.execSQL(events_create);
//			}
//			if (oldVersion < 17) {
//				db.execSQL("ALTER TABLE " + DATABASE_TABLE_USERS
//						+ " ADD COLUMN CITY TEXT");
//			};
//			db.setTransactionSuccessful();
//		} catch (SQLException e) {
//			AppLogger.e(TAG,
//					"Error creating tables and debug data" + e.getMessage());
//		} finally {
//			db.endTransaction();
//		}
	}

	public void cleanData(SQLiteDatabase db) {
		execSQL(db, "delete from " + DATABASE_TABLE_COURSES + ";");
		execSQL(db, "delete from " + DATABASE_TABLE_COURSEUNITS + ";");
		execSQL(db, "delete from " + DATABASE_TABLE_SEMESTER + ";");
		execSQL(db, "delete from " + DATABASE_TABLE_FRIENDS + ";");
		execSQL(db, "delete from " + DATABASE_TABLE_EVENTS + ";");
		execSQL(db, "delete from " + DATABASE_TABLE_OFFLINE + ";");
		execSQL(db, "delete from " + DATABASE_TABLE_USERS + ";");
		execSQL(db, "delete from " + DATABASE_TABLE_PATCHES + ";");
		execSQL(db, "delete from " + DATABASE_TABLE_RATINGS + ";");
		execSQL(db, "delete from " + DATABASE_TABLE_AVATARS + ";");
		// execSQL(db, "delete from " + DATABASE_TABLE_ACTIVITIES + ";");
	}

	public void execSQL(SQLiteDatabase db, String sql) {
		db.beginTransaction();
		try {
			// Create tables & test data
			db.execSQL(sql);
			db.setTransactionSuccessful();
		} catch (SQLException e) {
			AppLogger.e(TAG,
					"Error execSQL " + sql + ";ErrorMessage:" + e.getMessage());
		} finally {
			db.endTransaction();
		}
	}

	public void execSQL(SQLiteDatabase db, String sql, Object[] params) {
		db.beginTransaction();
		try {
			// Create tables & test data
			db.execSQL(sql, params);
			db.setTransactionSuccessful();
		} catch (SQLException e) {
			AppLogger.e(TAG,
					"Error execSQL " + sql + ";ErrorMessage:" + e.getMessage());
		} finally {
			db.endTransaction();
		}
	}

	public List<Course> getCourses(SQLiteDatabase db) {
		synchronized (courseLock) {
			List<Course> list = new ArrayList<Course>();
			try {
				Cursor cursor = db.rawQuery("SELECT * FROM "
						+ DATABASE_TABLE_COURSES, null);
				int count = cursor.getCount();
				if (count > 0) {
					cursor.moveToFirst();
					Map<String, Course> courseIds = new HashMap<String, Course>();
					while (!cursor.isAfterLast()) {
						Course course = new Course(cursor);
						course.course_units = new ArrayList<CourseUnit>();
						courseIds.put(String.valueOf(cursor.getInt(0)), course);
						list.add(course);
						cursor.moveToNext();
					}
					bindCourseUnitsToCourses(db, courseIds);
					applyPatchesToCourses(db, courseIds);
				}
				cursor.close();
			} catch (SQLException e) {
				AppLogger.e(TAG, "Error getRecords" + e.getMessage());
			}
			AppLogger.d(TAG, "after sync getCourses");
			return list;
		}
	}
	
	public Course getCourseByName(SQLiteDatabase db, String name) {
		synchronized (courseLock) {
			// ID INTEGER PRIMARY KEY, NAME TEXT, TEACHER TEXT,
			// DESCRIPTION TEXT, STARTWEEK INTEGER, ENDWEEK INTEGER,
			// COURSETYPE INTEGER
			Cursor cursor = db.rawQuery("SELECT * FROM "
					+ DATABASE_TABLE_COURSES + " where NAME = ?",
					new String[] { name });
			Course course = null;
			try {
				if (!cursor.isAfterLast()) {
					cursor.moveToLast();
					// ID INTEGER PRIMARY KEY,NAME TEXT,AVATAR TEXT,SCHOOL TEXT,
					// MAJOR TEXT, GRADE INTEGER
					course = new Course(cursor);
					List<CourseUnit> courseUnits = getCourseUnits(db, course);
					course.course_units = courseUnits;
					List<Patch> patches = getPatches(db, course.id);
					applyPatches(db, course, patches);
				}
			} catch (SQLException e) {
				AppLogger.e(TAG, "Exception", e);
			}
			cursor.close();
			return course;
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void bindCourseUnitsToCourses(SQLiteDatabase db, Map<String, Course> courseIds) {
		String courseIdString = fm.jihua.kecheng.utils.CommonUtils.join(new ArrayList(courseIds.keySet()), ",");
		synchronized (courseLock) {
			Cursor cursor = db.rawQuery("SELECT * FROM "
					+ DATABASE_TABLE_COURSEUNITS + " where COURSEID in("
					+ courseIdString + ");", null);
			int count = cursor.getCount();
			if (count > 0) {
				cursor.moveToFirst();
				while (!cursor.isAfterLast()) {
					// ID INTEGER PRIMARY KEY, DAYOFWEEK INTEGER, TIMESLOTS TEXT, ROOM TEXT, WEEKS TEXT, COURSEID INTEGER
					CourseUnit item = new CourseUnit(cursor.getInt(1),
							cursor.getString(2), cursor.getString(3),
							cursor.getString(4), cursor.getInt(5));
					int courseId = cursor.getInt(5);
					item.course = courseIds.get(String.valueOf(courseId));
					item.course.course_units.add(item);
					cursor.moveToNext();
				}
				cursor.close();
			} else {
				cursor.close();
			}
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void applyPatchesToCourses(SQLiteDatabase db,
			Map<String, Course> courseIds) {
		String courseIdString = fm.jihua.kecheng.utils.CommonUtils.join(
				new ArrayList(courseIds.keySet()), ",");
		SparseArray<List<Patch>> coursePatches = new SparseArray<List<Patch>>();
		synchronized (courseLock) {
			Cursor cursor = db.rawQuery("SELECT * FROM "
					+ DATABASE_TABLE_PATCHES + " where COURSEID in("
					+ courseIdString + ");", null);
			int count = cursor.getCount();
			if (count > 0) {
				cursor.moveToFirst();
				while (!cursor.isAfterLast()) {
//					ID INTEGER PRIMARY KEY,COURSEID INTEGER, KEY TEXT, VALUE TEXT, DAY_AND_SLOTS TEXT
					Patch patch = new Patch(cursor);
					int courseId = cursor.getInt(1);
					if (coursePatches.get(courseId) != null) {
						coursePatches.get(courseId).add(patch);
					} else {
						List<Patch> patches = new ArrayList<Patch>();
						patches.add(patch);
						coursePatches.put(courseId, patches);
					}
					cursor.moveToNext();
				}
				cursor.close();
			} else {
				cursor.close();
			}
			for (int i = 0; i < coursePatches.size(); i++) {
				Course course = courseIds.get(String.valueOf(coursePatches.keyAt(i)));
				applyPatches(db, course, coursePatches.get(coursePatches.keyAt(i)));
			}
		}
	}

	public Course getCourse(SQLiteDatabase db, int courseId) {
		synchronized (courseLock) {
			// ID INTEGER PRIMARY KEY, NAME TEXT, TEACHER TEXT,
			// DESCRIPTION TEXT, STARTWEEK INTEGER, ENDWEEK INTEGER,
			// COURSETYPE INTEGER
			Cursor cursor = db.rawQuery("SELECT * FROM "
					+ DATABASE_TABLE_COURSES + " where ID = ?",
					new String[] { String.valueOf(courseId) });
			Course course = null;
			try {
				if (!cursor.isAfterLast()) {
					cursor.moveToLast();
					// ID INTEGER PRIMARY KEY,NAME TEXT,AVATAR TEXT,SCHOOL TEXT,
					// MAJOR TEXT, GRADE INTEGER
					course = new Course(cursor);
					List<CourseUnit> courseUnits = getCourseUnits(db, course);
					course.course_units = courseUnits;
					List<Patch> patches = getPatches(db, course.id);
					applyPatches(db, course, patches);
				}
			} catch (SQLException e) {
				AppLogger.e(TAG, "Exception", e);
			}
			cursor.close();
			return course;
		}
	}
	
	public boolean existCourse(SQLiteDatabase db, int courseId){
		return existRecord(db, DATABASE_TABLE_COURSES, courseId);
	}

	public Rating getRating(SQLiteDatabase db, int courseId) {
		return getRating(db, courseId, 0);
	}

	public Rating getRating(SQLiteDatabase db, int courseId, int userId) {
		// +
		// "(ID INTEGER PRIMARY KEY, COURSEID INTEGER, SCORE FLOAT, TAGS TEXT, TAGSCOUNT TEXT, USERID INTEGER, COMMENT TEXT, RATINGS_COUNT INTEGER);";
		Rating rating = null;
		Cursor cursor = db
				.rawQuery("SELECT * FROM " + DATABASE_TABLE_RATINGS
						+ " where COURSEID = ? and USERID = ? ", new String[] {
						String.valueOf(courseId), String.valueOf(userId) });
		try {
			if (cursor.getCount() > 0) {
				cursor.moveToLast();
				rating = new Rating(cursor.getInt(1), cursor.getFloat(2),
						cursor.getString(3), cursor.getInt(5),
						cursor.getString(6), cursor.getString(4),
						cursor.getInt(7));
			}
		} catch (SQLException e) {
			AppLogger.e(TAG, "Exception", e);
		} finally {
			cursor.close();
		}
		return rating;
	}

	public void deleteRating(SQLiteDatabase db, int courseId) {
		deleteRating(db, courseId, 0);
	}

	public void deleteRating(SQLiteDatabase db, int courseId, int userId) {
		try {
			db.execSQL("delete from " + DATABASE_TABLE_RATINGS
					+ " where COURSEID = ? and USERID = ? ", new String[] {
					String.valueOf(courseId), String.valueOf(userId) });
		} catch (SQLException e) {
			AppLogger.e(TAG, "Error execSQL deleteComment:" + courseId
					+ ";ErrorMessage:" + e.getMessage());
		} finally {

		}
	}

	public void saveRating(SQLiteDatabase db, Rating rating, int courseId) {
		saveRating(db, rating, courseId, 0);
	}

	public void saveRating(SQLiteDatabase db, Rating rating, int courseId,
			int userId) {
		deleteRating(db, courseId, userId);
		addRating(db, rating, courseId, userId);
	}

	private void addRating(SQLiteDatabase db, Rating rating, int courseId,
			int userId) {
		try {
			db.execSQL(
					"INSERT INTO "
							+ DATABASE_TABLE_RATINGS
							+ "(COURSEID, SCORE, TAGS, TAGSCOUNT, USERID, COMMENT, RATINGS_COUNT) "
							+ "VALUES(?, ?, ?, ?, ?, ?, ?);", new Object[] {
							rating.course_id, rating.score, rating.tagString,
							rating.tagsCount, userId, rating.comment,
							rating.num_ratings });
		} catch (SQLException e) {
			AppLogger.e(TAG, e.getMessage(), e);
		}
	}

	private int existOfflineData(SQLiteDatabase db, OfflineData offlineData) {
		int id = 0;
		try {
			Cursor cursor = db.rawQuery("SELECT id FROM "
					+ DATABASE_TABLE_OFFLINE + " where CONTENT = ? ",
					new String[] { offlineData.content });
			if (cursor.getCount() > 0) {
				cursor.moveToFirst();
				id = cursor.getInt(0);
			}
			cursor.close();
		} catch (SQLException e) {
			AppLogger.e(TAG, e.getMessage(), e);
		}
		return id;
	}

	public void deleteOfflineData(SQLiteDatabase db, int id) {
		try {
			db.execSQL("delete from " + DATABASE_TABLE_OFFLINE
					+ " where ID = ? ", new String[] { String.valueOf(id) });
		} catch (SQLException e) {
			AppLogger.e(TAG, "Error execSQL deleteOfflineData:" + id
					+ ";ErrorMessage:" + e.getMessage());
		} finally {

		}
	}

	//only used once
	private List<OfflineData<Course>> getOldOfflineData(SQLiteDatabase db, int type) {
		// ID INTEGER PRIMARY KEY,TYPE INTEGER, CONTENT TEXT
		List<OfflineData<Course>> list = new ArrayList<OfflineData<Course>>();
		Cursor cursor = db.rawQuery("SELECT * FROM " + DATABASE_TABLE_OFFLINE
				+ " where TYPE = ? ", new String[] { String.valueOf(type) });
		try {
			if (cursor.getCount() > 0) {
				cursor.moveToFirst();
				do {
					OfflineData<Course> offlineData = new OfflineData<Course>(cursor.getInt(0),
							cursor.getInt(1), cursor.getString(2));
					offlineData.category = OfflineData.getCategory(Course.class);
					JSONObject jsonObject = new JSONObject(offlineData.content);
					offlineData.content = jsonObject.get("course").toString();
					list.add(offlineData);
					offlineData.operation = Operation.ADD;
				} while (cursor.moveToNext());
			}
		} catch (Exception e) {
			AppLogger.e(TAG, "Exception", e);
		} finally {
			cursor.close();
		}
		return list;
	}
	
	public <T>List<OfflineData<T>> getOfflineData(SQLiteDatabase db, Class<T> classOfT) {
		// ID INTEGER PRIMARY KEY,TYPE INTEGER, CONTENT TEXT
		List<OfflineData<T>> list = new ArrayList<OfflineData<T>>();
		Cursor cursor = db.rawQuery("SELECT * FROM " + DATABASE_TABLE_OFFLINE
				+ " where CATEGORY = ? ", new String[] { OfflineData.getCategory(classOfT) });
		try {
			if (cursor.getCount() > 0) {
				cursor.moveToFirst();
				do {
					OfflineData<T> offlineData = new OfflineData<T>(cursor.getInt(0),
							cursor.getString(1), cursor.getString(2), Operation.values()[cursor.getInt(3)], cursor.getInt(4));
					list.add(offlineData);
				} while (cursor.moveToNext());
			}
		} catch (SQLException e) {
			AppLogger.e(TAG, "Exception", e);
		} finally {
			cursor.close();
		}
		return list;
	}

	public int saveOfflineData(SQLiteDatabase db, OfflineData offlineData) {
		int id = existOfflineData(db, offlineData);
		if (id == 0) {
			return addOfflineData(db, offlineData);
		}else{
			return id;
		}
	}

	private int addOfflineData(SQLiteDatabase db, OfflineData offlineData) {
		int id = 0;
		try {
			// + "(ID INTEGER PRIMARY KEY,CATEGORY TEXT, CONTENT TEXT, OPERATION INTEGER, RESTENTITY TEXT, SEMESTER_ID INTEGER);";
			db.execSQL("INSERT INTO " + DATABASE_TABLE_OFFLINE
					+ "(CATEGORY, CONTENT, OPERATION, SEMESTER_ID) " + "VALUES(?, ?, ?, ?);", new Object[] {
					offlineData.category, offlineData.content, offlineData.operation.ordinal(), offlineData.semesterId});
			Cursor cursor = db.rawQuery("SELECT id FROM " + DATABASE_TABLE_OFFLINE
					+ " order by id desc limit 1 ", null);
			if (cursor.getCount() > 0) {
				cursor.moveToFirst();
				id = cursor.getInt(0);
			}
			cursor.close();
		} catch (SQLException e) {
			AppLogger.e(TAG, e.getMessage(), e);
		}
		return id;
	}

	public Avatar[] getAvatars(SQLiteDatabase db, int userId) {
		// ID INTEGER PRIMARY KEY,TINY TEXT, LARGE TEXT, ORIGIN TEXT, ORDER
		// INTEGER, USERID INTEGER
		Avatar[] avatars = new Avatar[0];
		Cursor cursor = db.rawQuery("SELECT * FROM " + DATABASE_TABLE_AVATARS
				+ " where USERID = ? order by AVATAR_ORDER",
				new String[] { String.valueOf(userId) });
		try {
			if (cursor.getCount() > 0) {
				avatars = new Avatar[cursor.getCount()];
				cursor.moveToFirst();
				int i = 0;
				do {
					Avatar avatar = new Avatar(cursor.getInt(0),
							cursor.getString(1), cursor.getString(2),
							cursor.getString(3));
					avatars[i] = avatar;
					i++;
				} while (cursor.moveToNext());
			}
		} catch (SQLException e) {
			AppLogger.e(TAG, "Exception", e);
		} finally {
			cursor.close();
		}
		return avatars;
	}

	public void saveAvatars(SQLiteDatabase db, Avatar[] avatars, int userId) {
		deleteAvatars(db, userId);
		addAvatars(db, avatars, userId);
	}

	public void deleteAvatars(SQLiteDatabase db, int userId) {
		try {
			db.execSQL("delete from " + DATABASE_TABLE_AVATARS
					+ " where USERID = ? ",
					new String[] { String.valueOf(userId) });
		} catch (SQLException e) {
			AppLogger.e(TAG, "Error execSQL deleteAvatars:" + userId
					+ ";ErrorMessage:" + e.getMessage());
		} finally {

		}
	}

	private void addAvatars(SQLiteDatabase db, Avatar[] avatars, int userId) {
		try {
			// ID INTEGER PRIMARY KEY,TINY TEXT, LARGE TEXT, ORIGIN TEXT, ORDER
			// INTEGER, USERID INTEGER
			int order = 0;
			for (Avatar avatar : avatars) {
				db.execSQL("INSERT INTO " + DATABASE_TABLE_AVATARS
						+ "(ID, TINY, LARGE, ORIGIN, AVATAR_ORDER, USERID) "
						+ "VALUES(?, ?, ?, ?, ?, ?);", new Object[] {
						avatar.id, avatar.tiny_avatar_url,
						avatar.large_avatar_url, avatar.origin_avatar_url,
						order, userId });
				order++;
			}
		} catch (SQLException e) {
			AppLogger.e(TAG, e.getMessage(), e);
		}
	}

	public void saveCourses(SQLiteDatabase db, Course[] courses) {
		synchronized (courseLock) {
			db.beginTransaction();
			List<Course> oldCourses = getCourses(db);
			for (Course course : oldCourses) {
				deleteCourse(db, course.id, false);
			}
			for (Course course : courses) {
				saveCourse(db, course);
			}

			db.setTransactionSuccessful();
			db.endTransaction();
			AppLogger.d(TAG, "after sync saveCourses");
		}
	}

	private void addCourse(SQLiteDatabase db, Course course) {
		try {
			if (!existRecord(db, DATABASE_TABLE_COURSES, course.id)) {
				db.execSQL(
						"INSERT INTO "
								+ DATABASE_TABLE_COURSES
								+ "(ID, NAME, TEACHER, DESCRIPTION, STARTWEEK, ENDWEEK, COURSETYPE, SEMESTERID, COURSE_TIME_STRING, STUDENTS_COUNT) "
								+ "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?);",
						new Object[] { course.id, course.name, course.teacher,
								course.description, course.start_week,
								course.end_week, course.course_type,
								course.semester_id, course.course_times_string,
								course.students_count });
			}
		} catch (SQLException e) {
			AppLogger.e(TAG, e.getMessage(), e);
		}
	}

	public void saveCourse(SQLiteDatabase db, Course course) {
		synchronized (courseLock) {
			for (CourseUnit courseUnit : course.course_units) {
				addCourseUnit(db, courseUnit, course.id);
			}
			addCourse(db, course);
		}
	}

	public void saveCourse(SQLiteDatabase db, Course course, boolean patch) {
		synchronized (courseLock) {
			if (patch) {
				Course oldCourse = getCourse(db, course.id);
				if (oldCourse != null) {
					if (!CommonUtils.notStrictEquals(oldCourse.teacher,
							course.teacher)) {
						addPatch(db, course.id, "teacher", course.teacher);
					}
					//暂时没有周数和教室
//					for (int i = 0; i < oldCourse.course_units.size(); i++) {
//						String oldRoom = oldCourse.course_units.get(i).room;
//						String newRoom = course.course_units.get(i).room;
//						if (!CommonUtils.notStrictEquals(oldRoom, newRoom)) {
//							addPatch(db, course.id, "room", newRoom, oldCourse.course_units.get(i).day_of_week + "&" + oldCourse.course_units.get(i).time_slots);
//						}
//						String oldWeeks = oldCourse.course_units.get(i).weeks;
//						String newWeeks = course.course_units.get(i).weeks;
//						if (!CommonUtils.notStrictEquals(oldWeeks, newWeeks)) {
//							addPatch(db, course.id, "weeks", newWeeks, oldCourse.course_units.get(i).day_of_week + "&" + oldCourse.course_units.get(i).time_slots);
//						}
//					}
				}
			} else {
				saveCourse(db, course);
			}
		}
	}

	private List<Patch> getPatches(SQLiteDatabase db, int courseId) {
		synchronized (courseLock) {
			List<Patch> list = new ArrayList<Patch>();
			try {
				Cursor cursor = db.rawQuery("SELECT * FROM "
						+ DATABASE_TABLE_PATCHES + " where courseId = ? ",
						new String[] { String.valueOf(courseId) });
				int count = cursor.getCount();
				if (count > 0) {
					cursor.moveToFirst();
					while (!cursor.isAfterLast()) {
						Patch patch = new Patch(cursor);
						list.add(patch);
						cursor.moveToNext();
					}
					cursor.close();
				} else {
					cursor.close();
				}
			} catch (SQLException e) {
				AppLogger.e(TAG, "Error getRecords" + e.getMessage());
			}
			return list;
		}
	}

	private void applyPatches(SQLiteDatabase db, Course course,
			List<Patch> patches) {
		synchronized (courseLock) {
			for (Patch patch : patches) {
				if (patch.key.equals("teacher")) {
					course.teacher = patch.value;
				} 
				//暂时没有周数和教室
//				else if(patch.key.equals("room")){
//					if (patch.day_and_slots != null) {
//						CourseUnit courseUnit = CourseUnit
//								.findCourseUnitByDayAndSlots(course.course_units,
//										patch.day_and_slots);
//						if (courseUnit != null) {
//							courseUnit.room = patch.value;
//						}
//					} else {
//						for (CourseUnit courseUnit : course.course_units) {
//							courseUnit.room = patch.value;
//						}
//					}
//				} else if (patch.key.equals("weeks")){
//					if (patch.day_and_slots != null) {
//						CourseUnit courseUnit = CourseUnit
//								.findCourseUnitByDayAndSlots(course.course_units,
//										patch.day_and_slots);
//						if (courseUnit != null) {
//							courseUnit.weeks = patch.value;
//						}
//					} else {
//						for (CourseUnit courseUnit : course.course_units) {
//							courseUnit.weeks = patch.value;
//						}
//					}
//				}
			}
		}
	}

	private void addPatch(SQLiteDatabase db, int courseId, String key,
			String value) {
		addPatch(db, courseId, key, value, null);
	}

	private void addPatch(SQLiteDatabase db, int courseId, String key,
			String value, String day_and_slots) {
		synchronized (courseLock) {
			try {
				db.execSQL(
						"INSERT INTO "
								+ DATABASE_TABLE_PATCHES
								+ " (COURSEID, KEY, VALUE, DAY_AND_SLOTS) VALUES(?, ?, ?, ?)",
						new Object[] { courseId, key, value, day_and_slots });
			} catch (SQLException e) {
				AppLogger.e(TAG, e.getMessage(), e);
			}
		}
	}

	public void deleteCourse(SQLiteDatabase db, int courseId) {
		deleteCourse(db, courseId, true);
	}

	public void deleteCourse(SQLiteDatabase db, int courseId,
			boolean transaction) {
		synchronized (courseLock) {
			if (transaction) {
				db.beginTransaction();
			}
			try {
				// Create tables & test data
				db.execSQL("delete from " + DATABASE_TABLE_COURSES
						+ " where id = ? ",
						new String[] { String.valueOf(courseId) });
				db.execSQL("delete from " + DATABASE_TABLE_COURSEUNITS
						+ " where COURSEID = ? ",
						new String[] { String.valueOf(courseId) });
				if (transaction) {
					db.setTransactionSuccessful();
				}
			} catch (SQLException e) {
				AppLogger.e(TAG, "Error execSQL deleteCourse:" + courseId
						+ ";ErrorMessage:" + e.getMessage());
			} finally {
				if (transaction) {
					db.endTransaction();
				}
			}
		}
	}
	
	private void addCourseUnit(SQLiteDatabase db, CourseUnit courseUnit,
			int courseId) {
		synchronized (courseLock) {
			try {
//				ID INTEGER PRIMARY KEY, DAYOFWEEK INTEGER, TIMESLOTS TEXT, ROOM TEXT, WEEKS TEXT, COURSEID INTEGER
				//暂时没有周数和教室
//				if (courseUnit.weeks.matches("[\\d\\-,]*") && 
				if(courseUnit.time_slots.matches("[\\d\\-,]*")) {
					db.execSQL(
							"INSERT INTO "
									+ DATABASE_TABLE_COURSEUNITS
									+ "(DAYOFWEEK, TIMESLOTS, ROOM, WEEKS, COURSEID) VALUES(?, ?, ?, ?, ?)",
							new Object[] { courseUnit.day_of_week, courseUnit.time_slots, courseUnit.room, courseUnit.weeks, courseId });
					List<Integer> list = CourseUnit.changeStringToList(courseUnit.time_slots);
					int max = list.get(list.size()-1);
					if (max > mApp.getSlotLength()) {
						mApp.setSlotLength(Math.min(max,
								Const.MAX_TIME_SLOT));
					}
				}
			} catch (SQLException e) {
				AppLogger.e(TAG, e.getMessage(), e);
			}
		}
	}
	
	private List<CourseUnit> getCourseUnits(SQLiteDatabase db, Course course) {
		// synchronized (courseLock) {
		List<CourseUnit> list = new ArrayList<CourseUnit>();
		try {
			Cursor cursor = db.rawQuery("SELECT * FROM "
					+ DATABASE_TABLE_COURSEUNITS + " where COURSEID = ? ;",
					new String[] { String.valueOf(course.id) });
			int count = cursor.getCount();
			if (count > 0) {
				cursor.moveToFirst();
				while (!cursor.isAfterLast()) {
//					ID INTEGER PRIMARY KEY, DAYOFWEEK INTEGER, TIMESLOTS TEXT, ROOM TEXT, WEEKS TEXT, COURSEID INTEGER
					CourseUnit item = new CourseUnit(cursor.getInt(1),
							cursor.getString(2), cursor.getString(3),
							cursor.getString(4), cursor.getInt(5));
					list.add(item);
					item.course = course;
					cursor.moveToNext();
				}
				cursor.close();
				return list;
			} else {
				cursor.close();
			}
		} catch (SQLException e) {
			AppLogger.e(TAG, "Error getRecords" + e.getMessage());
		}
		return list;
		// }
	}

	public List<Semester> getSemesters(SQLiteDatabase db) {
		synchronized (semesterLock) {
			List<Semester> list = new ArrayList<Semester>();
			try {
				Cursor cursor = db.rawQuery("SELECT * FROM "
						+ DATABASE_TABLE_SEMESTER, null);
				int count = cursor.getCount();
				if (count > 0) {
					cursor.moveToFirst();
					while (!cursor.isAfterLast()) {
						Semester semester = new Semester(cursor.getInt(0),
								cursor.getInt(6), cursor.getLong(2) / 1000,
								cursor.getLong(3) / 1000, cursor.getString(5));
						list.add(semester);
						cursor.moveToNext();
					}
					cursor.close();
				} else {
					cursor.close();
				}
			} catch (SQLException e) {
				AppLogger.e(TAG, "Error getRecords" + e.getMessage());
			}
			return list;
		}
	}

	public Semester getActiveSemester(SQLiteDatabase db) {
		int semesterId = mApp.getActiveSemesterId();
		synchronized (semesterLock) {
			Semester semester = null;

			try {

				Cursor cursor = db.rawQuery("SELECT * FROM "
						+ DATABASE_TABLE_SEMESTER + " WHERE id=" + semesterId,
						null);
				int count = cursor.getCount();
				if (count > 0) {
					cursor.moveToFirst();
					semester = new Semester(cursor.getInt(0), cursor.getInt(6),
							cursor.getLong(2) / 1000, cursor.getLong(3) / 1000,
							cursor.getString(5));
				}
				cursor.close();
			} catch (SQLException e) {
				AppLogger.e(TAG, "Exception", e);
			}
			return semester;
		}
	}

	public void saveSemesters(SQLiteDatabase db, Semester[] semesters) {
		synchronized (semesterLock) {
			execSQL(db, "delete from " + DATABASE_TABLE_SEMESTER
					+ " where modified <> 1;");
			for (Semester semester : semesters) {
				saveSemester(db, semester);
			}
		}
	}

	public void saveSemester(SQLiteDatabase db, Semester semester) {
		synchronized (semesterLock) {
			Semester exiSemester = existSemester(db, semester);
			if (exiSemester != null) {
				if (semester.modified) {
					db.execSQL(
							"UPDATE "
									+ DATABASE_TABLE_SEMESTER
									+ " set SEMESTER_ID = ?, BEGINDATE=?, ENDDATE = ?, NAME = ?, ID = ?, MODIFIED = ? where ID = ?;",
							new Object[] { semester.id,
									semester.begin_time * 1000,
									semester.end_time * 1000, semester.name,
									semester.id, semester.modified,
									exiSemester.id });
				}
				// else alreay deleted
			} else {
				db.execSQL("INSERT INTO " + DATABASE_TABLE_SEMESTER
						+ "(ID, SEMESTER_ID, BEGINDATE, ENDDATE, NAME) "
						+ " VALUES(?, ?, ?, ?, ?);", new Object[] {
						semester.id, semester.id, semester.begin_time * 1000,
						semester.end_time * 1000, semester.name });
			}
		}
	}

	public Semester existSemester(SQLiteDatabase db, Semester semester) {
		synchronized (semesterLock) {
			Semester result = null;
			try {
				// ID INTEGER PRIMARY KEY, COURSEID INTEGER, CONTENT TEXT,
				// CRATORID
				// INTEGER, CREATED_AT LONG
				if (existRecord(db, DATABASE_TABLE_SEMESTER, semester.id)) {
					return semester;
				}
				String whereClause = " where SEMESTER_ID = ? ";
				Cursor cursor = db.rawQuery("SELECT * FROM "
						+ DATABASE_TABLE_SEMESTER + whereClause,
						new String[] { String.valueOf(semester.id) });
				int count = cursor.getCount();
				if (count > 0) {
					cursor.moveToLast();
					result = new Semester(cursor.getInt(0), cursor.getInt(6),
							cursor.getLong(2) / 1000, cursor.getLong(3) / 1000,
							cursor.getString(5));
				}
				cursor.close();
			} catch (SQLException e) {
				AppLogger.e(TAG, e.getMessage(), e);
			}
			return result;
		}
	}

	public User getUser(SQLiteDatabase db, int userId) {
		synchronized (userLock) {
			User user = null;
			Cursor cursor = db
					.rawQuery("SELECT * FROM " + DATABASE_TABLE_USERS
							+ " where ID = ?;",
							new String[] { String.valueOf(userId) });
			try {
				if (cursor.getCount() > 0) {
					cursor.moveToLast();
					// ID INTEGER PRIMARY KEY,NAME TEXT,AVATAR TEXT,SCHOOL TEXT,
					// MAJOR TEXT, GRADE INTEGER
					// "BIRTHDAY TEXT, ASTROAppLoggerY TEXT, SIGNATURE TEXT,
					// DEGREE TEXT, GOAL TEXT, SITUATIONS TEXT, RELATIONSHIP
					// TEXT, SHOW_BIRTHDAY INTEGER
					// String birthday, String astroAppLoggery_sign, String
					// signature, String pursuing_degree,
					// String career_goal, String life_situationsString, String
					// relationship_status, int birthday_visibility)
					user = new User(cursor.getInt(0), cursor.getString(1),
							cursor.getString(2), cursor.getString(3),
							cursor.getString(4), cursor.getInt(5),
							cursor.getLong(6), cursor.getString(7),
							cursor.getInt(8), cursor.getInt(9),
							cursor.getString(10), cursor.getString(11),
							cursor.getString(12), cursor.getString(13),
							cursor.getString(14), cursor.getString(15),
							cursor.getString(16), cursor.getString(17),
							cursor.getInt(18), cursor.getString(19), cursor.getString(20));
				}
			} catch (SQLException e) {
				AppLogger.e(TAG, e.getMessage(), e);
			} finally {
				cursor.close();
			}
			return user;
		}
	}

	public void saveUser(SQLiteDatabase db, User user) {
		synchronized (userLock) {
			try {
				if (existRecord(db, DATABASE_TABLE_USERS, user.id)) {
					db.execSQL(
							"UPDATE "
									+ DATABASE_TABLE_USERS
									+ " set NAME =? , AVATAR= ?, SCHOOL = ?, MAJOR = ?, GRADE = ?, RENREN_ID = ?, BIG_AVATAR = ?, SEX = ?, GEZI_ID = ?, BACKGROUND = ?, BIRTHDAY = ?, ASTROLOGY = ?, SIGNATURE = ?, DEGREE = ?, GOAL = ?, SITUATIONS = ?, RELATIONSHIP = ?, SHOW_BIRTHDAY= ? , DORMITORY= ? , CITY= ? where id = ?;",
							// new Object[] { user.name, user.tiny_avatar_url,
							// user.school, user.department, user.grade,
							// user.renren_id, user.origin_avatar_url,
							// user.sex, user.gezi_id, user.background,
							// user.birthday, user.astroAppLoggery_sign,
							// user.signature, user.pursuing_degree,
							// user.career_goal, user.life_situations,
							// user.relationship_status,
							// user.birthday_visibility, user.id}
							user.getObjectsExist());
				} else {
					db.execSQL(
							"INSERT INTO "
									+ DATABASE_TABLE_USERS
									+ "(ID, NAME, AVATAR, SCHOOL, MAJOR, GRADE, RENREN_ID, BIG_AVATAR, SEX, GEZI_ID, BACKGROUND, BIRTHDAY, ASTROLOGY, SIGNATURE, DEGREE, GOAL, SITUATIONS, RELATIONSHIP, SHOW_BIRTHDAY, DORMITORY, CITY) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ",
							user.getObjectsf());
				}
			} catch (SQLException e) {
				AppLogger.e(TAG, e.getMessage(), e);
			}
		}
	}
	
	public void deleteUser(SQLiteDatabase db, int userId) {
		try {
			db.execSQL("delete from " + DATABASE_TABLE_USERS
					+ " where ID = ? ", new String[] {
					String.valueOf(userId) });
		} catch (SQLException e) {
			AppLogger.e(TAG, "Error execSQL deleteUser:" + userId
					+ ";ErrorMessage:" + e.getMessage());
		} finally {

		}
	}

	public void addFriend(SQLiteDatabase db, int userId) {
		synchronized (userLock) {
			try {
				db.execSQL("INSERT INTO " + DATABASE_TABLE_FRIENDS
						+ "(USERID) VALUES(?)", new Object[] { userId });
			} catch (SQLException e) {
				AppLogger.e(TAG, e.getMessage(), e);
			}
		}
	}

	public void removeFriend(SQLiteDatabase db, int userId) {
		synchronized (userLock) {
			try {
				db.execSQL("delete from " + DATABASE_TABLE_FRIENDS
						+ " where userid = ?;", new Object[] { userId });
			} catch (SQLException e) {
				AppLogger.e(TAG, e.getMessage(), e);
			}
		}
	}

	public void saveFriends(SQLiteDatabase db, List<User> users) {

		synchronized (userLock) {
			db.beginTransaction();
			try {
				db.execSQL("delete from " + DATABASE_TABLE_FRIENDS);
				for (User user : users) {
					saveUser(db, user);
					addFriend(db, user.id);
				}
				db.setTransactionSuccessful();
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				db.endTransaction();
			}
		}
	}

	public void saveUsers(SQLiteDatabase db, List<User> users) {

		synchronized (userLock) {
			db.beginTransaction();
			try {
				for (User user : users) {
					saveUser(db, user);
				}
				db.setTransactionSuccessful();
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				db.endTransaction();
			}
		}
	}

	public List<User> getFriends(SQLiteDatabase db) {
		synchronized (userLock) {
			List<User> list = new ArrayList<User>();
			Cursor cursor = db.rawQuery("SELECT USERID FROM "
					+ DATABASE_TABLE_FRIENDS, null);
			try {
				int count = cursor.getCount();
				if (count > 0) {
					cursor.moveToFirst();
					while (!cursor.isAfterLast()) {
						list.add(getUser(db, cursor.getInt(0)));
						cursor.moveToNext();
					}
				}
			} catch (SQLException e) {
				AppLogger.e(TAG, "Error getRecords" + e.getMessage());
			} finally {
				cursor.close();
			}
			return list;
		}
	}

	public boolean isFriend(SQLiteDatabase db, int userId) {
		synchronized (userLock) {
			boolean result = false;
			try {
				Cursor cursor = db.rawQuery("SELECT 1 FROM "
						+ DATABASE_TABLE_FRIENDS + " where USERID = ?;",
						new String[] { String.valueOf(userId) });
				result = cursor.getCount() > 0;
				cursor.close();
			} catch (SQLException e) {
				AppLogger.e(TAG, e.getMessage(), e);
			}
			return result;
		}
	}

	public boolean existRecord(SQLiteDatabase db, String table, int id) {
		boolean result = false;
		try {
			// ID INTEGER PRIMARY KEY, COURSEID INTEGER, CONTENT TEXT, CRATORID
			// INTEGER, CREATED_AT LONG
			Cursor cursor = db.rawQuery("SELECT 1 FROM " + table
					+ " where id = ?;", new String[] { String.valueOf(id) });
			result = cursor.getCount() > 0;
			cursor.close();
		} catch (SQLException e) {
			AppLogger.e(TAG, e.getMessage(), e);
		}
		return result;
	}

	public List<Examination> getExaminations(SQLiteDatabase db) {
		long time = System.currentTimeMillis();
		List<Examination> list = new ArrayList<Examination>();
		Cursor cursor = db.rawQuery("SELECT * FROM "
				+ DATABASE_TABLE_EXAMINATIONS
				+ " where time >= ? order by time ASC ",
				new String[] { String.valueOf(time) });
		try {
			int count = cursor.getCount();
			if (count > 0) {
				cursor.moveToFirst();
				while (!cursor.isAfterLast()) {
					// "(ID INTEGER PRIMARY KEY, NAME TEXT, TIME LONG, ROOM TEXT, REMIND INTEGER DEFAULT 1, COURSEID INTEGER);";
					Examination examination = new Examination(cursor.getInt(0),
							cursor.getString(1), cursor.getLong(2),
							cursor.getString(3));
					examination.course = getCourse(db, cursor.getInt(5));
					list.add(examination);
					cursor.moveToNext();
				}
			}
		} catch (SQLException e) {
			AppLogger.e(TAG, "Error getRecords" + e.getMessage());
		} finally {
			cursor.close();
		}
		cursor = db.rawQuery("SELECT * FROM " + DATABASE_TABLE_EXAMINATIONS
				+ " where time < ? order by time DESC ",
				new String[] { String.valueOf(time) });
		try {
			int count = cursor.getCount();
			if (count > 0) {
				cursor.moveToFirst();
				while (!cursor.isAfterLast()) {
					// "(ID INTEGER PRIMARY KEY, NAME TEXT, TIME LONG, ROOM TEXT, REMIND INTEGER DEFAULT 1, COURSEID INTEGER);";
					Examination examination = new Examination(cursor.getInt(0),
							cursor.getString(1), cursor.getLong(2),
							cursor.getString(3));
					examination.course = getCourse(db, cursor.getInt(5));
					list.add(examination);
					cursor.moveToNext();
				}
			}
		} catch (SQLException e) {
			AppLogger.e(TAG, "Error getRecords" + e.getMessage());
		} finally {
			cursor.close();
		}
		return list;
	}

	public Examination getExamination(SQLiteDatabase db, int courseId) {
		Examination examination = null;
		Cursor cursor = db
				.rawQuery(
						"SELECT * FROM "
								+ DATABASE_TABLE_EXAMINATIONS
								+ " where COURSEID = ? and time >= ? order by time ASC;",
						new String[] { String.valueOf(courseId),
								String.valueOf(System.currentTimeMillis()) });
		try {
			if (cursor.getCount() > 0) {
				cursor.moveToFirst();
				// ID INTEGER PRIMARY KEY,NAME TEXT,AVATAR TEXT,SCHOOL TEXT,
				// MAJOR TEXT, GRADE INTEGER
				examination = new Examination(cursor.getInt(0),
						cursor.getString(1), cursor.getLong(2),
						cursor.getString(3));
				examination.course = getCourse(db, cursor.getInt(5));
			}
		} catch (SQLException e) {
			AppLogger.e(TAG, e.getMessage(), e);
		} finally {
			cursor.close();
		}
		return examination;
	}

	public void saveExaminations(SQLiteDatabase db,
			List<Examination> examinations) {
		db.execSQL("delete from " + DATABASE_TABLE_EXAMINATIONS);
		for (Examination examination : examinations) {
			saveExamination(db, examination, false);
		}
	}

	public void saveExamination(SQLiteDatabase db, Examination examination) {
		saveExamination(db, examination, false);
	}

	public void saveExamination(SQLiteDatabase db, Examination examination,
			boolean saveLastEditTime) {
		try {
			// "(ID INTEGER PRIMARY KEY, NAME TEXT, TIME LONG, ROOM TEXT, REMIND INTEGER DEFAULT 1, COURSEID INTEGER);";
			if (existRecord(db, DATABASE_TABLE_EXAMINATIONS, examination.id)) {
				db.execSQL(
						"UPDATE "
								+ DATABASE_TABLE_EXAMINATIONS
								+ " set NAME =? , TIME= ?, ROOM = ?, COURSEID = ? where id = ?;",
						new Object[] { examination.name, examination.time,
								examination.room, examination.courseId(),
								examination.id });
			} else {
				if (examination.id == 0) {
					db.execSQL(
							"INSERT INTO "
									+ DATABASE_TABLE_EXAMINATIONS
									+ "(NAME, TIME, ROOM, COURSEID) VALUES (?, ?, ?, ?) ",
							new Object[] { examination.name, examination.time,
									examination.room, examination.courseId() });
				} else {
					db.execSQL(
							"INSERT INTO "
									+ DATABASE_TABLE_EXAMINATIONS
									+ "(ID, NAME, TIME, ROOM, COURSEID) VALUES (?, ?, ?, ?, ?) ",
							new Object[] { examination.id, examination.name,
									examination.time, examination.room,
									examination.courseId() });
				}
			}
			if (saveLastEditTime) {
				mApp.setLastEditExaminationTime(System.currentTimeMillis());
			}
		} catch (SQLException e) {
			AppLogger.e(TAG, e.getMessage(), e);
		}
	}

	public void deleteExamination(SQLiteDatabase db, Examination examination) {
		try {
			// Create tables & test data
			db.execSQL("delete from " + DATABASE_TABLE_EXAMINATIONS
					+ " where ID = ? ",
					new String[] { String.valueOf(examination.id) });
			mApp.setLastEditExaminationTime(System.currentTimeMillis());
		} catch (SQLException e) {
			AppLogger.e(TAG, e.getMessage(), e);
		} finally {
		}
	}

	public void addEvent(SQLiteDatabase db, Event event) {
		try {
			if (!existRecord(db, DATABASE_TABLE_EVENTS, event.id)) {
				// ID INTEGER PRIMARY KEY, NAME TEXT, START_TIME LONE, END_TIME LONE, CITY TEXT, PROVINCE TEXT, LOCATION TEXT, INITIATOR TEXT, CONTENT TEXT, POSTER_URL TEXT, TIME_SLOTS TEXT
				db.execSQL(
						"INSERT INTO "
								+ DATABASE_TABLE_EVENTS
								+ "(ID, NAME, START_TIME, END_TIME, CITY, PROVINCE, LOCATION, INITIATOR, CONTENT, POSTER_URL, TIME_SLOTS, SCHOOL) "
								+ "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);",
						new Object[] { event.id, event.name, event.start_time, event.end_time,
								event.city, event.province, event.location,
								event.organizer_name, event.content,
								event.poster_url, event.time_slots , event.school});
			}
		} catch (SQLException e) {
			AppLogger.e(TAG, e.getMessage(), e);
		}
	}

	public void deleteEvent(SQLiteDatabase db, int eventId) {
		deleteEvent(db, eventId, true);
	}

	public void deleteEvent(SQLiteDatabase db, int eventId, boolean transaction) {
		synchronized (eventLock) {
			if (transaction) {
				db.beginTransaction();
			}
			try {
				// Create tables & test data
				db.execSQL("delete from " + DATABASE_TABLE_EVENTS
						+ " where id = ? ",
						new String[] { String.valueOf(eventId) });
				if (transaction) {
					db.setTransactionSuccessful();
				}
			} catch (SQLException e) {
				AppLogger.e(TAG, "Error execSQL deleteEvent:" + eventId
						+ ";ErrorMessage:" + e.getMessage());
			} finally {
				if (transaction) {
					db.endTransaction();
				}
			}
		}
	}

	public List<Event> getEvents(SQLiteDatabase db) {
		synchronized (eventLock) {
			List<Event> list = new ArrayList<Event>();
			// ID INTEGER PRIMARY KEY, NAME TEXT, START_TIME LONE, END_TIME LONE, CITY TEXT, PROVINCE TEXT, LOCATION TEXT, INITIATOR TEXT, CONTENT TEXT, POSTER_URL TEXT, TIME_SLOTS TEXT
			Cursor cursor = db.rawQuery("SELECT * FROM "
					+ DATABASE_TABLE_EVENTS, null);
			try {
				int count = cursor.getCount();
				if (count > 0) {
					cursor.moveToFirst();
					while (!cursor.isAfterLast()) {
						Event event = new Event(cursor.getInt(0), cursor.getString(1),
								cursor.getLong(2), cursor.getLong(3),
								cursor.getString(4), cursor.getString(5),
								cursor.getString(6), cursor.getString(7),
								cursor.getString(8), cursor.getString(9), cursor.getString(10), cursor.getString(11));
						list.add(event);
						cursor.moveToNext();
					}
				}
			} catch (SQLException e) {
				AppLogger.e(TAG, "Error getRecords" + e.getMessage());
			}finally{
				cursor.close();
			}
			AppLogger.d(TAG, "after sync getCourses");
			return list;
		}
	}

	public void saveEvents(SQLiteDatabase db, Event[] events) {
		synchronized (eventLock) {
			String sql = "DELETE FROM " + DATABASE_TABLE_EVENTS + ";";
			db.execSQL(sql);
			db.beginTransaction();
			if (events != null) {
				for (Event event : events) {
					addEvent(db, event);
				}
			}
			db.setTransactionSuccessful();
			db.endTransaction();
			AppLogger.d(TAG, "after sync saveCourses");
		}
	}

	public Event getEvent(SQLiteDatabase db, int eventId) {
		synchronized (eventLock) {
			// ID INTEGER PRIMARY KEY, NAME TEXT, START_TIME LONE, END_TIME LONE, CITY TEXT, PROVINCE TEXT, LOCATION TEXT, INITIATOR TEXT, CONTENT TEXT, POSTER_URL TEXT, TIME_SLOTS TEXT
//			int id, String name, long start_time, long end_time,
//			String city, String province, String location,
//			String organizer_name, String content, String poster_url,
//			String created_at, String time_slots
			Event event = null;
			Cursor cursor = db.rawQuery("SELECT * FROM "
					+ DATABASE_TABLE_EVENTS + " where ID = ?",
					new String[] { String.valueOf(eventId) });
			try {
				if (cursor.getCount() > 0) {
					cursor.moveToLast();
					event = new Event(cursor.getInt(0), cursor.getString(1),
							cursor.getLong(2), cursor.getLong(3),
							cursor.getString(4), cursor.getString(5),
							cursor.getString(6), cursor.getString(7),
							cursor.getString(8), cursor.getString(9), cursor.getString(10), cursor.getString(11));
				}
			} catch (SQLException e) {
				AppLogger.e(TAG, "Exception", e);
			} finally {
				cursor.close();
			}
			return event;
		}
	}
	
	public List<Event> getEventListByTime(SQLiteDatabase db, long searchTime){
		synchronized (eventLock) {
			
			List<Event> list = new ArrayList<Event>();
			
			Calendar calendar =Calendar.getInstance();
			calendar.setTimeInMillis(searchTime);
			calendar.set(Calendar.HOUR_OF_DAY, 0);
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.SECOND, 0);
			
			long startTime = calendar.getTimeInMillis() / 1000;
			calendar.add(Calendar.DATE, 1);
			long endTime = calendar.getTimeInMillis() / 1000;
			Event event = null;
			Cursor cursor = db.rawQuery("SELECT * FROM "
					+ DATABASE_TABLE_EVENTS + " WHERE START_TIME BETWEEN ? AND ?",
					new String[] { String.valueOf(startTime), String.valueOf(endTime) });
			try {
				cursor.moveToFirst();
				while (!cursor.isAfterLast()) {
					event = new Event(cursor.getInt(0), cursor.getString(1), cursor.getLong(2), cursor.getLong(3), cursor.getString(4), cursor.getString(5), cursor.getString(6), cursor.getString(7),
							cursor.getString(8), cursor.getString(9), cursor.getString(10), cursor.getString(11));
					list.add(event);
					cursor.moveToNext();
				}
			} catch (SQLException e) {
				AppLogger.e(TAG, "Exception", e);
			} finally {
				cursor.close();
			}
			return list;
		}
	}

}
