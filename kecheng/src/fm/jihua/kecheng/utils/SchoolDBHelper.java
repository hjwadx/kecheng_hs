package fm.jihua.kecheng.utils;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import fm.jihua.kecheng.rest.entities.City;
import fm.jihua.kecheng.rest.entities.Department;
import fm.jihua.kecheng.rest.entities.School;

public class SchoolDBHelper extends SQLiteAssetHelper {
//	public static final String DB_PATH = "/data/data/fm.jihua.kecheng/databases/";
//	public static final String DB_NAME = "kecheng.db";
	Context context;

	
	public static final int MAX_SCHOOL_LENGTH =  50;
	private static final String DATABASE_NAME = "kecheng";
    private static final int DATABASE_VERSION = 2;
    private static Map<String, Integer> allEmojiMap;
    private Map<String, Integer> emojiImageMap;

	public SchoolDBHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		this.setForcedUpgradeVersion(2);
		this.context = context;
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		super.onCreate(db);
	}
	
	public List<City> getAllCities() {
		List<City> list = new ArrayList<City>();
		try {
			Cursor cursor = getReadableDatabase().rawQuery("SELECT id, name, pinyin, py FROM cities order by py", null);
			int count = cursor.getCount();
			if (count > 0) {
				cursor.moveToFirst();
				while (!cursor.isAfterLast()) {
					// ID INTEGER PRIMARY KEY, NAME TEXT, TEACHER TEXT,
					// DESCRIPTION TEXT, STARTWEEK INTEGER, ENDWEEK INTEGER,
					// COURSETYPE INTEGER
					City item = new City(cursor.getInt(0), cursor.getString(1),
							cursor.getString(2), cursor.getString(3));
					list.add(item);
					cursor.moveToNext();
				}
				cursor.close();
				return list;
			} else {
				cursor.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}

	public List<School> getSchools(int cityId, String name) {
		List<School> list = new ArrayList<School>();
		try {
			String search = "%"+name+"%";
			Cursor cursor = getReadableDatabase().rawQuery("SELECT * FROM schools where county_id in (select id from counties where city_id = ? ) and (name like ? or pinyin like ? or py like ?) order by rank desc limit ?;", new String[]{String.valueOf(cityId), search, search, search, String.valueOf(MAX_SCHOOL_LENGTH)});
			int count = cursor.getCount();
			if (count > 0) {
				cursor.moveToFirst();
				while (!cursor.isAfterLast()) {
					// ID INTEGER PRIMARY KEY, NAME TEXT, TEACHER TEXT,
					// DESCRIPTION TEXT, STARTWEEK INTEGER, ENDWEEK INTEGER,
					// COURSETYPE INTEGER
					School item = new School(cursor.getInt(0),
							cursor.getString(1), cursor.getInt(5));
					list.add(item);
					cursor.moveToNext();
				}
				cursor.close();
			} else {
				cursor.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		List<School> aliases = getSchoolsFromAlias(name);
		for (School school : aliases) {
			if (!list.contains(school)) {
				list.add(school);
			}
		}
		return list;
	}
	
	public School getSchool(String name) {
		School school = null;
		try {
			String search = name;
			Cursor cursor = getReadableDatabase().rawQuery("SELECT * FROM schools where name = ?", new String[]{search});
			int count = cursor.getCount();
			if (count > 0) {
				cursor.moveToFirst();
				if (!cursor.isAfterLast()) {
					// ID INTEGER PRIMARY KEY, NAME TEXT, TEACHER TEXT,
					// DESCRIPTION TEXT, STARTWEEK INTEGER, ENDWEEK INTEGER,
					// COURSETYPE INTEGER
					school = new School(cursor.getInt(0),
							cursor.getString(1), cursor.getInt(5));
				}
				cursor.close();
			} else {
				cursor.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return school;
	}
	
	private List<School> getSchoolsFromAlias(String name) {
		List<School> list = new ArrayList<School>();
		try {
			String search = "%"+name+"%";
			String pinyinSearch = name+"%";
			Cursor cursor = getReadableDatabase().rawQuery("SELECT school_id, master_name FROM school_aliases where name like ? or pinyin like ? or py like ? limit ?;", new String[]{search, pinyinSearch, pinyinSearch, String.valueOf(MAX_SCHOOL_LENGTH)});
			int count = cursor.getCount();
			if (count > 0) {
				cursor.moveToFirst();
				Cursor cursor1 = getReadableDatabase().rawQuery("SELECT * FROM schools where id = ?", new String[]{String.valueOf(cursor.getInt(0))});
				cursor1.moveToFirst();
				while (!cursor.isAfterLast()) {
					// ID INTEGER PRIMARY KEY, NAME TEXT, TEACHER TEXT,
					// DESCRIPTION TEXT, STARTWEEK INTEGER, ENDWEEK INTEGER,
					// COURSETYPE INTEGER
					School item = new School(cursor.getInt(0),
							cursor.getString(1), cursor1.getInt(5));
					list.add(item);
					cursor.moveToNext();
				}
				cursor.close();
			} else {
				cursor.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}
	
	public List<Department> getDepartments(int schoolId, String name) {
		List<Department> list = new ArrayList<Department>();
		try {
			Cursor cursor = getReadableDatabase().rawQuery("SELECT * FROM departments where school_id = ? and name like ?;", new String[]{String.valueOf(schoolId), "%"+name+"%"});
			int count = cursor.getCount();
			if (count > 0) {
				cursor.moveToFirst();
				while (!cursor.isAfterLast()) {
					// ID INTEGER PRIMARY KEY, NAME TEXT, TEACHER TEXT,
					// DESCRIPTION TEXT, STARTWEEK INTEGER, ENDWEEK INTEGER,
					// COURSETYPE INTEGER
					Department item = new Department(cursor.getInt(0),
							cursor.getString(1));
					list.add(item);
					cursor.moveToNext();
				}
				cursor.close();
				return list;
			} else {
				cursor.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}
	
	public String getSBUnicode(String utf8){
		String sbUnicode = null;
		try {
			Cursor cursor = getReadableDatabase().rawQuery("SELECT sbunicode FROM emoji where utf8 = ?;", new String[]{utf8});
			if (cursor.getCount() > 0) {
				cursor.moveToFirst();
				sbUnicode = cursor.getString(0);
			}
			cursor.close();
		}catch (SQLException e) {
			e.printStackTrace();
		}
		return sbUnicode;
	}
	
	public Map<String, Integer> getAllUtf8Emoji(){
		if (allEmojiMap != null) {
			return allEmojiMap;
		}
		Map<String, Integer> list = new HashMap<String, Integer>();
		Map<String, Integer> emojiMap = getEmojiImageMap();
		try {
			Cursor cursor = getReadableDatabase().rawQuery("SELECT utf8,sbunicode FROM emoji", null);
			int count = cursor.getCount();
			if (count > 0) {
				cursor.moveToFirst();
				while (!cursor.isAfterLast()) {
					// ID INTEGER PRIMARY KEY, NAME TEXT, TEACHER TEXT,
					// DESCRIPTION TEXT, STARTWEEK INTEGER, ENDWEEK INTEGER,
					// COURSETYPE INTEGER
					String key = null;
					Integer value = null;
					try {
						key = new String(cursor.getBlob(0), "UTF-8");
						value = emojiMap.get(cursor.getString(1).toLowerCase(Locale.getDefault()));
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
					list.put(key, value);
					cursor.moveToNext();
				}
				cursor.close();
				allEmojiMap = list;
				return list;
			} else {
				cursor.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}
	
	private Map<String, Integer> getEmojiImageMap(){
		if (emojiImageMap != null) {
			return emojiImageMap;
		}
		emojiImageMap = new HashMap<String, Integer>();
		int[] emoji_array = new int[]{0xe001, 0xe05a, 0xe101, 0xe15a, 0xe201, 0xe253, 0xe301, 0xe34d, 0xe401, 0xe44c, 0xe501, 0xe537};
		int count = 0;
		for(int i=0; i<emoji_array.length; i=i+2){
			int start = emoji_array[i];
			int end = emoji_array[i+1];
			for(int j=start; j<=end; j++){
				emojiImageMap.put(Integer.toHexString(j), count++); 
			}
		}
		return emojiImageMap;
	}
}
