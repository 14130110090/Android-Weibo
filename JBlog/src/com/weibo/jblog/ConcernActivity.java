package com.weibo.jblog;

import static com.weibo.utils.ConstantUtil.*;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.weibo.adapter.HomeFragmentAdapter;
import com.weibo.adapter.UserListAdapter;
import com.weibo.connect.ConnectManager;
import com.weibo.connect.LoadHomeTask;
import com.weibo.connect.ConnectManager.OnJsonReturnListener;
import com.weibo.jblog.HomeHotFragment.MyHandler;
import com.weibo.listener.ListenerManager;
import com.weibo.utils.FileLruCache;
import com.weibo.utils.LocalAccessUtil;
import com.weibo.utils.MemoryLruCache;
import com.weibo.view.PullToRefreshLayout;
import com.weibo.view.PullableListView;
import com.weibo.view.PullToRefreshLayout.OnRefreshListener;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class ConcernActivity extends Activity {
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
		setContentView(R.layout.concern_activity);
		initView();
	}

	private void initView() {
		mLruCache = new MemoryLruCache();
		fileCache = new FileLruCache();
		adapter = new UserListAdapter(ConcernActivity.this, arrayList,
				mLruCache, fileCache);
		pullLayout = (PullToRefreshLayout) findViewById(R.id.concern_pulltorefresh);
		pullLayout.setOnRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {
				int other_id=getIntent().getIntExtra("other_id", 0);
				if(other_id!=0)
					manager.getAttention(other_id);
				else 
				manager.getAttention(user_id);
			}

			@Override
			public void onLoadMore() {
				// 先不要上拉加载，否则还要判断是哪个刷新方法
				// manager.getAttention(user_id);
			}
		});
		listView = (PullableListView) findViewById(R.id.concern_list);
		listView.setAdapter(adapter);
		listView.setDividerHeight(10);
		handler = new MyHandler(adapter);

		// 滑动时取消加载
		// listView.setOnScrollListener(adapter);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View view,
					int position, long arg3) {
				Intent i = new Intent(ConcernActivity.this, UserActivity.class);
				try {
					i.putExtra("other_id",
							arrayList.get(position).getInt("user_id"));
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				startActivity(i);

			}
		});
        Button concern_back=(Button)findViewById(R.id.concern_back);
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
						if (result.getString("action").equals(
								GETATTENTION_SUCCESS)) {
							arrayList.clear();
							JSONArray array = result.getJSONArray("JSONArray");
							for (int i = 0; i < array.length(); i++) {
								// 需要判断是否为新的日志
								arrayList.add(array.getJSONObject(i));
							}
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

	
	//必须等布局完了才能自动刷新，该activity可能多次获取焦点，必须要多加一个判断
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		if(hasFocus&&firstIn){
			pullLayout.autoRefresh();
			firstIn=false;
		}
	}

	
	
}
