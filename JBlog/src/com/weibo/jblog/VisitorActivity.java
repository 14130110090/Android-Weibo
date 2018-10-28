package com.weibo.jblog;

import static com.weibo.utils.ConstantUtil.*;

import java.io.IOException;

import com.weibo.connect.ConnectToServer;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class VisitorActivity extends Activity{
	ConnectToServer connect = null;
	ProgressDialog pd = null;
	String[] visitorItem;
	//handler的泄露问题
	Handler handle = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			pd.dismiss();
			ListView visitorList = (ListView) findViewById(R.id.visitorlist);
			visitorList.setAdapter(adapter);
			super.handleMessage(msg);
		}

	};
	BaseAdapter adapter = new BaseAdapter() {
		@Override
		public View getView(int arg0, View arg1, ViewGroup arg2) {
			View v = LayoutInflater.from(getApplicationContext()).inflate(
					R.layout.visitor_item, null);

			TextView  visitor_name= (TextView) v
					.findViewById(R.id.visitor_name);
			TextView visit_date = (TextView) v.findViewById(R.id.visit_date);
			ImageView visitor_head = (ImageView) v.findViewById(R.id.visitor_head);
			if (visitorItem != null) {

				String[] item = visitorItem[arg0].split("@");
				visitor_name.setText(item[0]);
				visit_date.setText(item[2]);
				//设置图片
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
			// 总共的item个数
			return visitorItem.length;
		}

	};
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.visitor_activity);
		initView();
		
	}

	public void initView(){
		 pd=ProgressDialog.show(this, "获取访问者列表", "请等待...");
		 getVisitorList(1);
		 
	}
	// 获取日志列表
		public void getVisitorList(final int uid) {
			Thread t = new Thread() {
				public void run() {
					// 若要使用Toast
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
						String msg = VISITORLIST + uid;
						connect.getOutputStream().writeUTF(msg);
						String receivedMessage = connect.getInputStream().readUTF();
						if (receivedMessage.startsWith(VISITORLIST_SUCCESS)) {
							String info = receivedMessage
									.substring(VISITORLIST_SUCCESS.length());
							String[] items = info.split("#");
							visitorItem = items;
							
							handle.sendEmptyMessage(0);
						} else if (receivedMessage.startsWith(VISITORLIST_FAIL)) {
							// 不可以在非主线程直接使用Toast
							// Toast.makeText(LoginActivity.this, "登录失败",
							// Toast.LENGTH_SHORT).show();
							System.out.println("fail");
						}
					} catch (IOException e) {
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
