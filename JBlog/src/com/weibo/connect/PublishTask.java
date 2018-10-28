package com.weibo.connect;

import static com.weibo.utils.ConstantUtil.*;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import com.weibo.utils.PictureUtil;

import android.app.Activity;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Base64;


public class PublishTask extends AsyncTask<Integer, Integer, JSONObject> {
	ConnectToServer connect = null;
	ArrayList<String> uriList;
	WeakReference<Activity> activityRef;
	int MAXLENGTH = 20000;
	String diary;
    PublishListener listener;
	public PublishTask(Activity activity,PublishListener listener, String diary, ArrayList<String> uriList) {
		this.activityRef = new WeakReference<Activity>(activity);
		this.diary = diary;
		this.uriList = uriList;
		this.listener=listener;
		
	}

	public interface PublishListener{
		public void publish(JSONObject result);
	}

	@Override
	protected JSONObject doInBackground(Integer... arg0) {
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
			json.put("action", PUBLISH);
			json.put("title", "");
			json.put("diary", diary);
			json.put("uid", arg0[0]);
			json.put("pic_count", uriList.size());
			DataOutputStream out = connect.getOutputStream();
			out.writeUTF(json.toString());
			for (int i = 0; i < uriList.size(); i++) {
				// 原本需要判断get()的结果是否为null,太麻烦了
				byte[] data = PictureUtil.getBytes(uriList.get(i));
				String encoder = Base64.encodeToString(data, Base64.NO_WRAP);
				// 代表一共传输几次
				int count = encoder.length() / MAXLENGTH + 1;
				// 判断是否相等，决定要不要加一
				if (count * MAXLENGTH == encoder.length())
					count--;
				out.writeInt(count);
				for (int m = 0; m < count; m++) {
					if (m == count - 1) {
						out.writeUTF(encoder.substring(m * (MAXLENGTH)));
						break;
					}
					out.writeUTF(encoder.substring(m * MAXLENGTH, (m + 1)
							* MAXLENGTH));
				}
			}
			// 是因为在还没有读取完时关闭连接，导致连接重置
			out.flush();
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
		if(listener!=null){
			listener.publish(result);
		}
	}
}
