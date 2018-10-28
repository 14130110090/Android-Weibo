package com.weibo.connect;

import static com.weibo.utils.ConstantUtil.*;

import java.io.IOException;
import java.lang.ref.WeakReference;

import org.json.JSONException;
import org.json.JSONObject;

import com.weibo.jblog.MainFragmentActivity;
import com.weibo.utils.NetworkUtil;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

public class LoginTask extends AsyncTask<String, Integer, JSONObject> {
	ConnectToServer connect = null;
	CallbackListener listener = null;

	public LoginTask(CallbackListener listener) {
		this.listener = listener;
	}

	public interface CallbackListener {
		public void callback(JSONObject result);
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();

	}

	@Override
	protected JSONObject doInBackground(String... param) {
		if (connect == null) {
			connect = new ConnectToServer(ADDRESS, PORT);
			if (!connect.isConnected()) {
				connect = null;// 重设为null，否则下次点击就会跳过这里也就再也不会重新连接
				return null;
			}
		}
		JSONObject result = null;
		try {
			JSONObject json = new JSONObject();
			json.put("action", LOGIN);
			json.put("account", param[0]);
			json.put("password", param[1]);
			connect.getOutputStream().writeUTF(json.toString());
			String receivedMessage = connect.getInputStream().readUTF();
			result = new JSONObject(receivedMessage);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return result;
	}

	@Override
	protected void onPostExecute(JSONObject result) {
		super.onPostExecute(result);
		if (connect != null) {
			connect.closeConnection();
			connect = null;
		}
		if (listener != null) {
			listener.callback(result);
		}
	}

}
