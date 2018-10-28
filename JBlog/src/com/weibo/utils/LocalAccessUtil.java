package com.weibo.utils;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;

public final class LocalAccessUtil {
	static int version=12;
	public static JSONObject getLastAccount(Activity activity) {
        
		
		MyOpenHelper myHelper = new MyOpenHelper(activity,
				MyOpenHelper.DATABASE_NAME, null,version);
		JSONObject json = myHelper.getLastAccount();
		return json;

	}

	public static JSONArray getAllAccount(Activity activity) {

		MyOpenHelper myHelper = new MyOpenHelper(activity,
				MyOpenHelper.DATABASE_NAME, null, version);
		JSONArray json = myHelper.getAllAccount();
		return json;

	}

	public static JSONArray getAllDiary(Activity activity) {

		MyOpenHelper myHelper = new MyOpenHelper(activity,
				MyOpenHelper.DATABASE_NAME, null, version);
		JSONArray json = myHelper.getAllDiary();
		return json;

	}

	
	public static void saveDiary(Activity activity, JSONObject json) {
		MyOpenHelper myHelper = new MyOpenHelper(activity,
				MyOpenHelper.DATABASE_NAME, null, version);
		myHelper.insertDiary(json);
	}

	// ���ʺź����뱣�浽�ֻ���
	public static void saveInfo(Activity activity, JSONObject json) {
		/**
		 * Ӧ�ý��������ݿ�Ĳ�������һ����������,�����ظ�������ͬ���ֵı�����ݿ�
		 */

		MyOpenHelper myHelper = new MyOpenHelper(activity,
				MyOpenHelper.DATABASE_NAME, null, version);
		myHelper.insertUser(json);
	}
	public static JSONArray getAllPhoto(Activity activity,int diary_id) {
		MyOpenHelper myHelper = new MyOpenHelper(activity,
				MyOpenHelper.DATABASE_NAME, null, version);
		JSONArray json = myHelper.getAllPhoto(diary_id);
		return json;

	}

	public static JSONObject getDiary(Activity activity,int diary_id) {
		MyOpenHelper myHelper = new MyOpenHelper(activity,
				MyOpenHelper.DATABASE_NAME, null, version);
		JSONObject json = myHelper.getDiary(diary_id);
		myHelper.updateAccessCount(diary_id);
		return json;

	}
	
	public static void addCommentCount(Activity activity,int diary_id) {
		MyOpenHelper myHelper = new MyOpenHelper(activity,
				MyOpenHelper.DATABASE_NAME, null, version);
		myHelper.addCommentCount(diary_id);
	}
	
	public static void addTransmitCount(Activity activity,int diary_id) {
		MyOpenHelper myHelper = new MyOpenHelper(activity,
				MyOpenHelper.DATABASE_NAME, null, version);
		myHelper.addTransmitCount(diary_id);
	}
	
	public static void addLaudCount(Activity activity,int diary_id) {
		MyOpenHelper myHelper = new MyOpenHelper(activity,
				MyOpenHelper.DATABASE_NAME, null, version);
		myHelper.addLaudCount(diary_id);
	}
	
	public static boolean deleteDiary(Activity activity,int diary_id) {
		MyOpenHelper myHelper = new MyOpenHelper(activity,
				MyOpenHelper.DATABASE_NAME, null, version);
		return myHelper.deleteDiary(diary_id);

	}
	
	public static boolean deleteAllDiary(Activity activity) {
		MyOpenHelper myHelper = new MyOpenHelper(activity,
				MyOpenHelper.DATABASE_NAME, null, version);
		return myHelper.deleteAllDiary();

	}
	
	public static long getDiaryCount(Activity activity) {
		MyOpenHelper myHelper = new MyOpenHelper(activity,
				MyOpenHelper.DATABASE_NAME, null, version);
		return myHelper.getDiaryCount();

	}
	public static int getLeastUsedDiary(Activity activity) {
		MyOpenHelper myHelper = new MyOpenHelper(activity,
				MyOpenHelper.DATABASE_NAME, null, version);
		return myHelper.getLeastUsedDiary();

	}
	public static void addConcernCount(Activity activity,int user_id) {
		MyOpenHelper myHelper = new MyOpenHelper(activity,
				MyOpenHelper.DATABASE_NAME, null, version);
		myHelper.addConcernCount(user_id);
	}
	public static void minusConcernCount(Activity activity,int user_id) {
		MyOpenHelper myHelper = new MyOpenHelper(activity,
				MyOpenHelper.DATABASE_NAME, null, version);
		myHelper.minusConcernCount(user_id);
	}
}
