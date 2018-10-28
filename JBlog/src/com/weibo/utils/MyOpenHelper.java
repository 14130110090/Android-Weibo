package com.weibo.utils;

import static com.weibo.utils.ConstantUtil.ROOTDIR;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.weibo.connect.ConnectManager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

public class MyOpenHelper extends SQLiteOpenHelper {
	public static final String DATABASE_NAME = "JBlogDatabase";

	// 如果用default current_timestamp时间不准
	public static final String CREATE_USER = "create table user ("
			+ "user_id integer primary key unique,"
			+ "login_time timestamp not null DEFAULT(datetime('now', 'localtime')),"
			+ "user_name varchar(8)," + "user_email varchar(18), "
			+ "user_password varchar(16), " + "user_desc varchar(45),"
			+ "user_concern integer DEFAULT 0,"
			+ "user_diary integer DEFAULT 0," + "user_fans integer DEFAULT 0,"
			+ "user_phone varchar(11))";

	// 多添加一条访问次数，添加数就默认为1次，每次替换访问次数最小的diary
	public static final String CREATE_DIARY = "create table diary ("
			+ "diary_id integer primary key unique," + "diary_date text,"
			+ "user_id integer," + "user_name varchar(8),"
			+ "diary_content text, " + "user_head varchar(120),"
			+ "access_count integer default 1,"
			+ "comment_count integer default 0,"
			+ "laud_count integer default 0,"
			+ "transmit_count integer default 0)";

	public static final String CREATE_PHOTO = "create table photo ("
			+ "photo_id integer primary key unique,"
			+ "diary_id integer REFERENCES  diary(diary_id) ON DELETE cascade,"
			+ "photo_data varchar(120))";

	public MyOpenHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);

	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_USER);
		db.execSQL(CREATE_DIARY);
		db.execSQL(CREATE_PHOTO);
	}

	/**
	 * 如果有2，3版本，则要考虑每种情况，1升2，2升3，1升3，因为每台手机上的版本可能不一样
	 */

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		if (oldVersion == 1 && newVersion == 2) {
			db.execSQL("drop table user");
			db.execSQL(CREATE_USER);
			System.out.println("旧版本：1");
			System.out.println("新版本：2");
		}
		if (oldVersion == 2 && newVersion == 3) {
			db.execSQL("drop table user");
			db.execSQL(CREATE_USER);
			System.out.println("旧版本：2");
			System.out.println("新版本：3");
		}
		if (oldVersion == 11 && newVersion == 12) {
			db.execSQL("PRAGMA foreign_keys = ON");
			db.execSQL("drop table diary");
			db.execSQL(CREATE_DIARY);
			System.out.println("旧版本：11");
			System.out.println("新版本：12");
		}
	}

	/**
	 * 保存用户数据
	 * 
	 * @param json
	 */
	public void insertUser(JSONObject json) {
		SQLiteDatabase db = getWritableDatabase();
		// 需要判断主键是否重复，否则会报错，如果已经有了数据，就不更改数据，没有就插入数据
		// 存在则更新（先删除原先导致冲突的记录，然后插入新的），不存在就插入
		try {
			db.execSQL(
					"insert or replace into user(user_id,user_name,"
							+ "user_email,user_password,user_desc,user_phone,"
							+ "user_concern,user_diary,user_fans) values(?,?,?,?,?,?,?,?,?)",
					new String[] {
							json.getInt("user_id") + "",
							json.has("user_name") ? json.getString("user_name")
									: null,
							json.has("user_email") ? json
									.getString("user_email") : null,
							json.getString("user_password"),
							json.has("user_desc") ? json.getString("user_desc")
									: null, json.getString("user_phone"),
							json.getInt("user_concern") + "",
							json.getInt("user_diary") + "",
							json.getInt("user_fans") + "" });
		} catch (JSONException e) {
			e.printStackTrace();
		}
		db.close();
	}

	/**
	 * 查询指定用户所有信息
	 * 
	 * @param user_id
	 * @return
	 */
	public JSONObject getUserInfo(int user_id) {
		SQLiteDatabase db = getReadableDatabase();
		JSONObject result = new JSONObject();
		Cursor c = db.rawQuery("select * from user where user_id=?",
				new String[] { user_id + "" });
		while (c.moveToNext()) {
			try {
				result.put("user_name",
						c.getString(c.getColumnIndex("user_name")));
				result.put("user_email",
						c.getString(c.getColumnIndex("user_email")));
				result.put("user_desc",
						c.getString(c.getColumnIndex("user_desc")));
				result.put("user_phone",
						c.getString(c.getColumnIndex("user_phone")));
				result.put("user_password",
						c.getString(c.getColumnIndex("user_password")));
				result.put("user_id", c.getInt(c.getColumnIndex("user_id")));
				result.put("login_time",
						c.getString(c.getColumnIndex("login_time")));
				result.put("user_concern",
						c.getInt(c.getColumnIndex("user_concern")));
				result.put("user_diary",
						c.getInt(c.getColumnIndex("user_diary")));
				result.put("user_fans", c.getInt(c.getColumnIndex("user_fans")));
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		c.close();
		db.close();

		return result;

	}

	/**
	 * 删除指定user_id的用户数据
	 * 
	 * @param user_id
	 */
	public boolean deleteUser(int user_id) {
		SQLiteDatabase db = getWritableDatabase();
		int i = db.delete("user", "user_id=?", new String[] { user_id + "" });
		if (i <= 0) {
			return false;
		} else
			return true;
	}

	/**
	 * limit 0,1 就是limit 1 offset 0;偏移为0行，一共获取一行
	 * 
	 * @return
	 */
	public JSONObject getLastAccount() {
		JSONObject json = new JSONObject();
		SQLiteDatabase db = getReadableDatabase();
		Cursor c = db.rawQuery(
				"select * from user order by login_time desc limit 0,1", null);
		while (c.moveToNext()) {
			try {

				json.put("user_name",
						c.getString(c.getColumnIndex("user_name")));
				json.put("user_email",
						c.getString(c.getColumnIndex("user_email")));
				json.put("user_desc",
						c.getString(c.getColumnIndex("user_desc")));
				json.put("user_phone",
						c.getString(c.getColumnIndex("user_phone")));
				json.put("user_password",
						c.getString(c.getColumnIndex("user_password")));
				json.put("user_id", c.getInt(c.getColumnIndex("user_id")));
				json.put("login_time",
						c.getString(c.getColumnIndex("login_time")));
				json.put("user_concern",
						c.getInt(c.getColumnIndex("user_concern")));
				json.put("user_diary", c.getInt(c.getColumnIndex("user_diary")));
				json.put("user_fans", c.getInt(c.getColumnIndex("user_fans")));
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		c.close();
		db.close();
		return json;
	}

	public JSONArray getAllAccount() {
		JSONArray array = new JSONArray();
		SQLiteDatabase db = getReadableDatabase();
		Cursor c = db.rawQuery("select * from user order by login_time desc",
				null);
		while (c.moveToNext()) {
			try {
				JSONObject result = new JSONObject();
				result.put("user_name",
						c.getString(c.getColumnIndex("user_name")));
				result.put("user_email",
						c.getString(c.getColumnIndex("user_email")));
				result.put("user_desc",
						c.getString(c.getColumnIndex("user_desc")));
				result.put("user_phone",
						c.getString(c.getColumnIndex("user_phone")));
				result.put("user_password",
						c.getString(c.getColumnIndex("user_password")));
				result.put("user_id", c.getInt(c.getColumnIndex("user_id")));
				result.put("login_time",
						c.getString(c.getColumnIndex("login_time")));
				result.put("user_concern",
						c.getInt(c.getColumnIndex("user_concern")));
				result.put("user_diary",
						c.getInt(c.getColumnIndex("user_diary")));
				result.put("user_fans", c.getInt(c.getColumnIndex("user_fans")));
				array.put(result);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		c.close();
		db.close();
		return array;
	}

	public void insertDiary(JSONObject json) {

		SQLiteDatabase db = getWritableDatabase();
		// 需要判断主键是否重复，否则会报错，如果已经有了数据，就不更改数据，没有就插入数据
		try {
			String user_head = null;
			if (json.has("user_head")) {
				JSONObject head = json.getJSONObject("user_head");
				if (head.length() != 0) {
					user_head = head.getString("head_data");
				}
			}

			db.execSQL(
					"insert or ignore into diary(diary_id,user_name,user_id,"
							+ "diary_date,diary_content,user_head,comment_count,laud_count,transmit_count) values(?,?,?,?,?,?,?,?,?)",
					new String[] {
							json.getInt("diary_id") + "",
							json.has("user_name") ? json.getString("user_name")
									: null, json.getInt("user_id") + "",
							json.getString("diary_date"),
							json.getString("diary_content"), user_head,
							json.getInt("comment_count") + "",
							json.getInt("laud_count") + "",
							json.getInt("transmit_count") + "" });
			JSONArray pic = json.getJSONArray("pic");

			int length = pic.length();
			for (int i = 0; i < length; i++) {
				db.execSQL(
						"insert or ignore into photo(photo_id,photo_data,diary_id) values(?,?,?)",
						new String[] {
								pic.getJSONObject(i).getInt("photo_id") + "",
								pic.getJSONObject(i).getString("photo_data"),
								json.getInt("diary_id") + ""

						});

			}
			
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		db.close();
	}

	public JSONArray getAllDiary() {
		JSONArray array = new JSONArray();
		SQLiteDatabase db = getReadableDatabase();
		Cursor c = db.rawQuery("select * from diary order by diary_id desc",
				null);
		while (c.moveToNext()) {
			try {
				JSONObject result = new JSONObject();
				result.put("user_name",
						c.getString(c.getColumnIndex("user_name")));
				result.put("diary_id", c.getInt(c.getColumnIndex("diary_id")));
				result.put("user_id", c.getInt(c.getColumnIndex("user_id")));
				result.put("diary_content",
						c.getString(c.getColumnIndex("diary_content")));
				result.put("user_head",
						c.getString(c.getColumnIndex("user_head")));
				result.put("diary_date",
						c.getString(c.getColumnIndex("diary_date")));
				result.put("comment_count",
						c.getInt(c.getColumnIndex("comment_count")));
				result.put("laud_count",
						c.getInt(c.getColumnIndex("laud_count")));
				result.put("transmit_count",
						c.getInt(c.getColumnIndex("transmit_count")));
				result.put("pic",
						getAllPhoto(c.getInt(c.getColumnIndex("diary_id"))));
				array.put(result);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		c.close();
		db.close();

		return array;
	}

	// 获取指定id的diary
	public JSONObject getDiary(int diary_id) {
		JSONObject result = new JSONObject();
		SQLiteDatabase db = getReadableDatabase();
		Cursor c = db.rawQuery("select * from diary where diary_id=? ",
				new String[] { diary_id + "" });
		while (c.moveToNext()) {
			try {
				result.put("user_name",
						c.getString(c.getColumnIndex("user_name")));
				result.put("diary_id", c.getInt(c.getColumnIndex("diary_id")));
				result.put("user_id", c.getInt(c.getColumnIndex("user_id")));
				result.put("diary_content",
						c.getString(c.getColumnIndex("diary_content")));
				result.put("user_head",
						c.getString(c.getColumnIndex("user_head")));
				result.put("diary_date",
						c.getString(c.getColumnIndex("diary_date")));
				result.put("comment_count",
						c.getInt(c.getColumnIndex("comment_count")));
				result.put("laud_count",
						c.getInt(c.getColumnIndex("laud_count")));
				result.put("transmit_count",
						c.getInt(c.getColumnIndex("transmit_count")));
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		c.close();
		db.close();
		// 如果result为空，证明没有该diary,则没必要再获取图片
		if (result.length() == 0)
			return result;
		try {
			result.put("pic", getAllPhoto(diary_id));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return result;
	}

	public JSONArray getAllPhoto(int diary_id) {
		JSONArray array = new JSONArray();
		SQLiteDatabase db = getReadableDatabase();
		Cursor c = db.rawQuery("select * from photo where diary_id=?",
				new String[] { diary_id + "" });
		while (c.moveToNext()) {
			try {
				JSONObject result = new JSONObject();
				result.put("photo_id", c.getInt(c.getColumnIndex("photo_id")));
				result.put("photo_data",
						c.getString(c.getColumnIndex("photo_data")));
				result.put("diary_id", c.getInt(c.getColumnIndex("diary_id")));
				array.put(result);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		c.close();
		db.close();
		return array;
	}

	public boolean deleteDiary(int diary_id) {
		SQLiteDatabase db = getWritableDatabase();
		// 一定要加，否则无法实现级联删除
		db.execSQL("PRAGMA foreign_keys = ON");
		int i = db
				.delete("diary", "diary_id=?", new String[] { diary_id + "" });
		if (i <= 0) {
			return false;
		} else
			return true;
	}

	public boolean deleteAllDiary() {
		SQLiteDatabase db = getWritableDatabase();
		// 一定要加，否则无法实现级联删除
		db.execSQL("PRAGMA foreign_keys = ON");
		int i = db.delete("diary", null, null);
		if (i <= 0) {
			return false;
		} else
			return true;
	}

	public Long getDiaryCount() {
		SQLiteDatabase db = getReadableDatabase();
		Cursor cursor = db.rawQuery("select count(*) from diary", null);
		cursor.moveToFirst();
		Long count = cursor.getLong(0);
		cursor.close();
		return count;
	}

	// 获取date最小的，也就是时间最久远的diary进行替换
	public int getLeastUsedDiary() {
		int diary_id = 0;
		SQLiteDatabase db = getReadableDatabase();
		Cursor c = db.rawQuery(
				"select * from diary order by access_count limit 1", null);
		while (c.moveToNext()) {
			diary_id = c.getInt(c.getColumnIndex("diary_id"));
		}
		c.close();
		db.close();
		return diary_id;
	}

	public void updateAccessCount(int diary_id) {
		SQLiteDatabase db = getWritableDatabase();
		db.execSQL(
				"update diary set access_count=access_count+1 where diary_id=?",
				new String[] { diary_id + "" });

		db.close();
	}
	
	public void addTransmitCount(int diary_id) {
		SQLiteDatabase db = getWritableDatabase();
		db.execSQL(
				"update diary set transmit_count=transmit_count+1 where diary_id=?",
				new String[] { diary_id + "" });

		db.close();
	}
	
	public void addLaudCount(int diary_id) {
		SQLiteDatabase db = getWritableDatabase();
		db.execSQL(
				"update diary set laud_count=laud_count+1 where diary_id=?",
				new String[] { diary_id + "" });

		db.close();
	}
	
	public void addCommentCount(int diary_id) {
		SQLiteDatabase db = getWritableDatabase();
		db.execSQL(
				"update diary set comment_count=comment_count+1 where diary_id=?",
				new String[] { diary_id + "" });

		db.close();
	}
	
	public void addConcernCount(int user_id) {
		SQLiteDatabase db = getWritableDatabase();
		db.execSQL(
				"update user set user_concern=user_concern+1 where user_id=?",
				new String[] { user_id + "" });
		db.close();
	}
	public void minusConcernCount(int user_id) {
		SQLiteDatabase db = getWritableDatabase();
		db.execSQL(
				"update user set user_concern=user_concern-1 where user_id=?",
				new String[] { user_id + "" });
		db.close();
	}
	
}
