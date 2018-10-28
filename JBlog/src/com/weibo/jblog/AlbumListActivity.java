package com.weibo.jblog;

import static com.weibo.utils.ConstantUtil.*;

import java.io.IOException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.weibo.connect.ConnectToServer;
import com.weibo.utils.ConstantUtil;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class AlbumListActivity extends Activity {
	ConnectToServer connect = null;
	ProgressDialog pd = null;
	JSONArray albumItem;
	// handler��й¶����
	Handler handle = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			pd.dismiss();
			albumItem = (JSONArray) msg.obj;
			ListView albumList = (ListView) findViewById(R.id.albumlist);
			albumList.setAdapter(adapter);
			albumList.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {

					try {
						Intent i = new Intent(AlbumListActivity.this,
								AlbumActivity.class);
						JSONObject item = albumItem.getJSONObject(arg2);
						i.putExtra("albumid", item.getInt("album_id"));
						startActivity(i);
					} catch (JSONException e) {
						e.printStackTrace();
					}

				}
			});
			albumList.setOnItemLongClickListener(new OnItemLongClickListener() {

				@Override
				public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
						final int arg2, long arg3) {
					AlertDialog.Builder builder = new AlertDialog.Builder(
							AlbumListActivity.this);
					builder.setTitle("hello");
					builder.setItems(R.array.array,
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface arg0,
										int arg1) {
									switch (arg1) {
									case 0:

										// deleteAlbum(Integer.parseInt(item[0]));
										break;
									case 1:
										break;
									case 2:
										break;
									}

								}
							});
					builder.create().show();
					return true;
				}
			});
			super.handleMessage(msg);
		}

	};
	BaseAdapter adapter = new BaseAdapter() {
		@Override
		public View getView(int arg0, View arg1, ViewGroup arg2) {
			View v = LayoutInflater.from(getApplicationContext()).inflate(
					R.layout.album_item, null);

			TextView album_name = (TextView) v.findViewById(R.id.album_name);
			TextView album_date = (TextView) v.findViewById(R.id.album_date);
			TextView album_permission = (TextView) v
					.findViewById(R.id.album_permisssion);
			if (albumItem != null) {
				JSONObject item;
				try {
					item = albumItem.getJSONObject(arg0);

					album_name.setText(item.getString("album_name"));
					album_date.setText(item.getString("album_date"));
					album_permission
							.setText(item.getString("album_permission"));
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			return v;
		}

		@Override
		public long getItemId(int arg0) {
			return 0;
		}

		@Override
		public Object getItem(int arg0) {
			return null;
		}

		@Override
		public int getCount() {
			// �ܹ���item����
			return albumItem.length();
		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.albumlist_activity);
		initView();
	}

	public void initView() {
		pd = ProgressDialog.show(this, "��ȡ���", "��ȴ�...");
		pd.setCancelable(true);
		if (ConstantUtil.user_id != 0) {
			getAlbumList(ConstantUtil.user_id);
		}else {
			//��ת����¼ҳ��
			Intent i=new Intent(AlbumListActivity.this,LoginActivity.class);
			startActivity(i);
		}

	}

	// ��ȡ��־�б�
	public void getAlbumList(final int userid) {
		Thread t = new Thread() {
			public void run() {
				// ��Ҫʹ��Toast
				// Looper.prepare();
				if (connect == null) {
					connect = new ConnectToServer(ADDRESS, PORT);
					if (!connect.isConnected()) {
						System.out.println("weilianjie");
						connect = null;
						return;
					}
				}

				try {
					JSONObject json = new JSONObject();
					json.put("action", ALBUMLIST);
					json.put("userid", userid);
					connect.getOutputStream().writeUTF(json.toString());
					String receivedMessage = connect.getInputStream().readUTF();
					JSONObject result = new JSONObject(receivedMessage);
					if (result.getString("action").equals(ALBUMLIST_SUCCESS)) {
						Message msg = new Message();
						msg.obj = result.getJSONArray("array");
						handle.sendMessage(msg);
					} else {
						// �������ڷ����߳�ֱ��ʹ��Toast
						// Toast.makeText(LoginActivity.this, "��¼ʧ��",
						// Toast.LENGTH_SHORT).show();
						System.out.println("fail");
					}
				} catch (IOException e) {
					e.printStackTrace();
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		};
		t.start();
	}

	@Override
	protected void onDestroy() {
		if (connect != null) {
			connect.closeConnection();
			connect = null;
		}
		super.onDestroy();
	}

}
