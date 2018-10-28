package com.weibo.connect;

import static com.weibo.utils.ConstantUtil.*;

import java.io.DataInputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Looper;
import android.os.Message;
import android.widget.ProgressBar;
import android.widget.Toast;

public class LoadHomeTask extends AsyncTask<Integer, Integer, JSONObject> {
	ConnectToServer connect = null;
	ProgressBar pb;
	LoadHomeListener listener;
	WeakReference<Activity> activityRef;
	MyHandler handler;
	int start_index;
	int flag = 0;

	static class MyHandler extends Handler {
		WeakReference<Activity> activityReference;

		MyHandler(WeakReference<Activity> ref) {
			activityReference = ref;
		}

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			final Activity activity = activityReference.get();
			if (activity != null) {
				switch (msg.what) {
				case 0:
					Toast.makeText(activity, "无法连接服务器", Toast.LENGTH_SHORT)
							.show();
					break;
				case 1:
					Toast.makeText(activity, "", Toast.LENGTH_SHORT).show();
					break;
				}
			}

		}

	}

	/**
	 * 回调函数
	 * 
	 * @author JiHongHua
	 * 
	 */
	public interface LoadHomeListener {
		public void LoadJson(JSONObject json, int start_index);
	}

	public LoadHomeTask(Activity activity) {
		this.activityRef = new WeakReference<Activity>(activity);
		handler = new MyHandler(activityRef);
	}

	public void setOnLoadHomeListener(LoadHomeListener listener) {
		this.listener = listener;
	}

	@Override
	protected JSONObject doInBackground(Integer... param) {
		if (connect == null) {
			// 应该先判断有没有网然后连接服务器
			connect = new ConnectToServer(ADDRESS, PORT);
			if (!connect.isConnected()) {
				handler.sendEmptyMessage(0);
				connect = null;
				return null;
			}
		}
		JSONObject result = null;
		try {
			JSONObject json = new JSONObject();
			if (flag == 1)
				json.put("action", GETHOT);
			else if (flag == 0)
				json.put("action", GETHOME);
			else if (flag == 2)
				json.put("action", GETCOLLECTION);
			else if(flag==3)
				json.put("action", DIARYLIST);
			json.put("user_id", param[0]);
			json.put("start_index", param[1]);
			json.put("end_index", param[2]);
			start_index = param[1];
			connect.getOutputStream().writeUTF(json.toString());
			DataInputStream din = connect.getInputStream();
			String receivedMessage = din.readUTF();
			result = new JSONObject(receivedMessage);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return result;

	}

	@Override
	protected void onCancelled() {
		super.onCancelled();

	}

	public void setFlag(int flag) {
		this.flag = flag;
	}

	@Override
	protected void onPostExecute(JSONObject result) {
		super.onPostExecute(result);
		if (connect != null) {
			connect.closeConnection();
			connect = null;
		}
		if (listener != null) {
			listener.LoadJson(result, start_index);
		}
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();

	}

}
