package com.weibo.jblog;

import static com.weibo.utils.ConstantUtil.*;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.weibo.adapter.UserListAdapter;
import com.weibo.connect.ConnectManager;
import com.weibo.connect.ConnectManager.OnJsonReturnListener;
import com.weibo.listener.ListenerManager;

import com.weibo.utils.FileLruCache;
import com.weibo.utils.MemoryLruCache;
import com.weibo.view.PullToRefreshLayout;
import com.weibo.view.PullableListView;
import com.weibo.view.PullToRefreshLayout.OnRefreshListener;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.AdapterView.OnItemClickListener;

public class FansActivity extends Activity {
	ConnectManager manager;
	MyHandler handler;
	UserListAdapter adapter;
	MemoryLruCache mLruCache;
	FileLruCache fileCache;
	ArrayList<JSONObject> arrayList = new ArrayList<JSONObject>();
	PullableListView listView;
	PullToRefreshLayout pullLayout;
    boolean firstIn=true;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.fans_activity);
		initView();
	}

	private void initView() {
		mLruCache = new MemoryLruCache();
		fileCache = new FileLruCache();
		adapter = new UserListAdapter(FansActivity.this, arrayList, mLruCache,
				fileCache);
		handler = new MyHandler(adapter);
		pullLayout = (PullToRefreshLayout) findViewById(R.id.fans_pulltorefresh);
		pullLayout.setOnRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {
				int other_id=getIntent().getIntExtra("other_id", 0);
				if(other_id!=0)
					manager.getFans(other_id);
				else 
				manager.getFans(user_id);
				
			}

			@Override
			public void onLoadMore() {
				// 先不要上拉加载，否则还要判断是哪个刷新方法
				// manager.getAttention(user_id);
			}
		});
		listView = (PullableListView) findViewById(R.id.fans_list);
		listView.setAdapter(adapter);
		listView.setDividerHeight(10);
		// 滑动时取消加载
		// listView.setOnScrollListener(adapter);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View view,
					int position, long arg3) {
				Intent i = new Intent(FansActivity.this, UserActivity.class);
				try {
					i.putExtra("other_id",
							arrayList.get(position).getInt("user_id"));
				} catch (JSONException e) {
					e.printStackTrace();
				}
				startActivity(i);

			}
		});
		Button concern_back = (Button) findViewById(R.id.fans_back);
		concern_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});
		manager = new ConnectManager();
		manager.setOnJsonReturnListener(new OnJsonReturnListener() {

			@Override
			public void onJsonReturn(JSONObject result) {
				try {
					if (result != null) {
						if (result.getString("action").equals(GETFANS_SUCCESS)) {
							arrayList.clear();
							JSONArray array = result.getJSONArray("JSONArray");
							for (int i = 0; i < array.length(); i++) {
								// 需要判断是否为新的日志
								arrayList.add(array.getJSONObject(i));
							}
							//当刷新该界面时，使用户界面的粉丝数改变
							ListenerManager.getInstance().onCountChanged(GETFANS_SUCCESS, arrayList.size());
							handler.sendEmptyMessage(5);
						} else {
							handler.sendEmptyMessage(4);
						}

					} else {

					}

				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		});

	}

	class MyHandler extends Handler {
		UserListAdapter fragmentAdapter;

		MyHandler(UserListAdapter adapter) {
			fragmentAdapter = adapter;

		}

		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 0:

				break;

			case 4:
				pullLayout.refreshFinish(0);

				break;
			case 5:
				fragmentAdapter.notifyDataSetChanged();
				pullLayout.refreshFinish(1);
				break;
			case 6:
				pullLayout.loadmoreFinish(0);

				break;
			case 7:
				pullLayout.loadmoreFinish(1);
				break;
			}

		}
	}
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		if(hasFocus&&firstIn){
			pullLayout.autoRefresh();
			firstIn=false;
		}
	}
}
