package fm.jihua.chat.utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import fm.jihua.chat.service.Message;

public class DatabaseHelper extends SQLiteOpenHelper {

	private final static String DATABASE_NAME = "kechengbiao.chat";
	private final static int DATABASE_VERSION = 3;
	private final static String TABLE_CHAT = "chat";
	private final static String TAG = "DatabaseHelper";
	
	public DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		String chat_create = "CREATE TABLE IF NOT EXISTS "
				+ TABLE_CHAT
				+ "(ID INTEGER PRIMARY KEY,FROMJID TEXT,TOJID TEXT,TIME LONG,TEXT TEXT, ATTACHID TEXT, ATTACHTYPE INTEGER, STATE INTEGER, EXTRA_TYPE TEXT, EXTRA_JSON TEXT, SUBJECT TEXT);";
		db.beginTransaction();
		try {
			// Create tables & test data
			db.execSQL(chat_create);
			db.setTransactionSuccessful();
		} catch (SQLException e) {
			Log.e(TAG,
					"Error creating tables and debug data" + e.getMessage());
		} finally {
			db.endTransaction();
		}
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		try {
			if (oldVersion < 2) {
				db.beginTransaction();
				List<String> list = getDistinctJids(db);
				List<Integer> distinctIds = new ArrayList<Integer>();
				for (String jid : list) {
					int id = Integer.parseInt(jid.split("@")[0]);
					if (!distinctIds.contains(id)) {
						db.execSQL("update "+ TABLE_CHAT + " set FROMJID = ? where FROMJID = ?", new Object[]{id, jid});
						db.execSQL("update "+ TABLE_CHAT + " set TOJID = ? where TOJID = ?", new Object[]{id, jid});
						distinctIds.add(id);
					}
				}
				db.setTransactionSuccessful();
				db.endTransaction();
			}
			if (oldVersion < 3) {
				db.execSQL("ALTER TABLE " + TABLE_CHAT
						+ " ADD COLUMN EXTRA_TYPE TEXT");
				db.execSQL("ALTER TABLE " + TABLE_CHAT
						+ " ADD COLUMN EXTRA_JSON TEXT");
				db.execSQL("ALTER TABLE " + TABLE_CHAT
						+ " ADD COLUMN SUBJECT TEXT");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private List<String> getDistinctJids(SQLiteDatabase db){
		ArrayList<String> ids = new ArrayList<String>();
		Cursor cursor = db.rawQuery("select distinct FROMJID as JID from " + TABLE_CHAT +" union select distinct TOJID from " + TABLE_CHAT, null);
		int count = cursor.getCount();
		if (count > 0) {
			cursor.moveToFirst();
			while(!cursor.isAfterLast()){
//				int id = User.fromJID(cursor.getString(0)).id;
				String jid = cursor.getString(0);
				if (!ids.contains(jid)) {
					ids.add(jid);
				}
				cursor.moveToNext();
			}
			cursor.close();
		}
		return ids;
	}
	
	public void cleanData(SQLiteDatabase db){
		execSQL(db, "delete from "+TABLE_CHAT + ";");
	}
	
	public void execSQL(SQLiteDatabase db, String sql){
		db.beginTransaction();
		try {
			db.execSQL(sql);
			db.setTransactionSuccessful();
		} catch (SQLException e) {
			Log.e(TAG,
					"Error execSQL "+ sql + ";ErrorMessage:" + e.getMessage());
		} finally {
			db.endTransaction();
		}
	}
	
	public void execSQL(SQLiteDatabase db, String sql, Object[] params){
		db.beginTransaction();
		try {
			db.execSQL(sql, params);
			db.setTransactionSuccessful();
		} catch (SQLException e) {
			Log.e(TAG,
					"Error execSQL "+ sql + ";ErrorMessage:" + e.getMessage());
		} finally {
			db.endTransaction();
		}
	}
	
	public List<Message> getMessages(SQLiteDatabase db, String from, String to, boolean both, int limit, int offset) {
		List<Message> list = new ArrayList<Message>();
		try {
			String fromJid = from.split("@")[0];
			String toJid = to.split("@")[0];
			String sql = "SELECT * FROM " + TABLE_CHAT  + " where (FROMJID = '" + fromJid + "' and TOJID = '" + toJid + "')";
			if (both) {
				sql += " OR (FROMJID = '" + toJid + "' and TOJID = '" + fromJid + "')";
			}
			sql += " order by TIME DESC limit "+limit+" offset " + offset + ";";
			Cursor cursor = db.rawQuery(sql, null);
			int count = cursor.getCount();
			if (count > 0) {
				cursor.moveToFirst();
				while(!cursor.isAfterLast()){
					//"(ID INTEGER PRIMARY KEY,FROM TEXT,TO TEXT,TIME LONG,TEXT TEXT, ATTACHID TEXT, ATTACHTYPE INTEGER, STATE INTEGER);";
					Message msg = new Message(cursor.getString(2), Message.MSG_TYPE_CHAT);
					msg.setId(cursor.getInt(0));
					msg.setFrom(cursor.getString(1));
					msg.setBody(cursor.getString(4));
					msg.setTimestamp(new Date(cursor.getLong(3)));
					msg.setState(cursor.getInt(7));
					msg.setExtraType(cursor.getString(8));
					msg.setExtraJson(cursor.getString(9));
					msg.setSubject(cursor.getString(10));
					list.add(msg);
					cursor.moveToNext();
				}
				cursor.close();
				return list;
			} else {
				cursor.close();
			}
		} catch (SQLException e) {
			Log.e(TAG, "Error getRecords" + e.getMessage());
		}
		return list;
	}
	
	public List<Message> getThreads(SQLiteDatabase db, int myUserId, boolean both, int limit, int offset) {
		List<Message> list = new ArrayList<Message>();
		String myId = String.valueOf(myUserId);
		try {
			String sql = "(select max(id) as latest_id from "+TABLE_CHAT+" group by max(FROMJID, TOJID), " +
					"min(FROMJID, TOJID) having (FROMJID = '"+ myId +"' or TOJID = '"+myId+"' " +
					")order by latest_id desc limit "+ limit +" offset " + offset + ") AS threads";
			sql = "select * from " + TABLE_CHAT + ", "+ sql + " where " + TABLE_CHAT + ".id = threads.latest_id;";
			Cursor cursor = db.rawQuery(sql, null);
			int count = cursor.getCount();
			if (count > 0) {
				cursor.moveToFirst();
				while(!cursor.isAfterLast()){
					//"(ID INTEGER PRIMARY KEY,FROM TEXT,TO TEXT,TIME LONG,TEXT TEXT, ATTACHID TEXT, ATTACHTYPE INTEGER, STATE INTEGER);";
					Message msg = new Message(cursor.getString(2), Message.MSG_TYPE_CHAT);
					msg.setFrom(cursor.getString(1));
					msg.setBody(cursor.getString(4));
					msg.setTimestamp(new Date(cursor.getLong(3)));
					msg.setState(cursor.getInt(7));
					msg.setExtraType(cursor.getString(8));
					msg.setExtraJson(cursor.getString(9));
					msg.setSubject(cursor.getString(10));
					msg.setThread(msg.getFromUserId().equals(myId) ? msg.getToUserId() : msg.getFromUserId());
					list.add(msg);
					cursor.moveToNext();
				}
				cursor.close();
				return list;
			} else {
				cursor.close();
			}
		} catch (SQLException e) {
			Log.e(TAG, "Error getRecords" + e.getMessage());
		}
		return list;
	}
	
	public void deleteThread(SQLiteDatabase db, String thread){
		String jid = Message.getUserId(thread);
		execSQL(db, "delete from " + TABLE_CHAT + " where FROMJID = ? or TOJID = ?;", new Object[]{jid, jid});
	}
	
	public void addMessage(SQLiteDatabase db, Message message) {
		try {
//			ID INTEGER PRIMARY KEY,FROMJID TEXT,TOJID TEXT,TIME LONG,TEXT TEXT, ATTACHID TEXT, ATTACHTYPE INTEGER, STATE INTEGER, EXTRA_TYPE TEXT, EXTRA_JSON TEXT, SUBJECT TEXT
			String fromJid = message.getFrom().split("@")[0];
			String toJid = message.getTo().split("@")[0];
			if (message.getId() == 0) {
				db.execSQL("INSERT INTO " + TABLE_CHAT
						+ "(FROMJID, TOJID, TIME, TEXT, STATE, EXTRA_TYPE, EXTRA_JSON, SUBJECT) VALUES (?, ?, ?, ?, ?, ?, ?, ?)",
						new Object[]{fromJid, toJid, message.getTimestamp().getTime(), message.getBody(), message.getState(), message.getExtraType(), message.getExtraJson(), message.getSubject()});
			}else {
				db.execSQL("INSERT INTO " + TABLE_CHAT
						+ "(ID, FROMJID, TOJID, TIME, TEXT, STATE, EXTRA_TYPE, EXTRA_JSON, SUBJECT) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)",
						new Object[]{message.getId(), fromJid, toJid, message.getTimestamp().getTime(), message.getBody(), message.getState(), message.getExtraType(), message.getExtraJson(), message.getSubject()});
			}
		} catch (SQLException e) {
			Log.e(TAG, "Error adding new message:" + e.getMessage(), e);
		}
	}
	
	public void read(SQLiteDatabase db, String fromJid) {
		try {
			db.execSQL("UPDATE " + TABLE_CHAT
					+ " set STATE = ? where FROMJID = ? and STATE = ?", new Object[] {
					Message.READ, Message.getUserId(fromJid), Message.UNREAD });
		} catch (SQLException e) {
			Log.e(TAG, "Error adding new message:" + e.getMessage(), e);
		}
	}
	
	public int getUnreadCount(SQLiteDatabase db, String fromJid) {
		int unread = 0;
		try {
			Cursor cursor = db.rawQuery("SELECT count(ID) FROM " + TABLE_CHAT
					+ " where STATE = ? AND FROMJID = ?", new String[] {
					String.valueOf(Message.UNREAD), Message.getUserId(fromJid) });
			int count = cursor.getCount();
			if (count > 0 && cursor.moveToFirst()) {
				unread  = cursor.getInt(0);
			}
			cursor.close();
		} catch (SQLException e) {
			Log.e(TAG, "Error adding new message:" + e.getMessage(), e);
		}
		return unread;
	}
	
	public int getUnreadCount(SQLiteDatabase db) {
		int unread = 0;
		try {
			Cursor cursor = db.rawQuery("SELECT count(ID) FROM " + TABLE_CHAT
					+ " where STATE = ?", new String[] {
					String.valueOf(Message.UNREAD) });
			int count = cursor.getCount();
			if (count > 0 && cursor.moveToFirst()) {
				unread  = cursor.getInt(0);
			}
			cursor.close();
		} catch (SQLException e) {
			Log.e(TAG, "Error adding new message:" + e.getMessage(), e);
		}
		return unread;
	}
}
