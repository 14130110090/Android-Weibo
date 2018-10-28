package com.weibo.jblog;

import static com.weibo.utils.ConstantUtil.*;

import java.io.IOException;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.weibo.adapter.HomeFragmentAdapter;
import com.weibo.connect.ConnectToServer;
import com.weibo.connect.LoadHomeTask;
import com.weibo.connect.LoadHomeTask.LoadHomeListener;
import com.weibo.jblog.CollectionActivity.MyHandler;
import com.weibo.listener.ListenerManager;
import com.weibo.listener.ListenerManager.onDiaryClickUserListener;

import com.weibo.utils.ConstantUtil;
import com.weibo.utils.FileLruCache;
import com.weibo.utils.LocalAccessUtil;
import com.weibo.utils.MemoryLruCache;
import com.weibo.view.PullToRefreshLayout;
import com.weibo.view.PullableListView;
import com.weibo.view.PullToRefreshLayout.OnRefreshListener;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class DiaryListActivity extends Activity implements onDiaryClickUserListener, LoadHomeListener {
	
	HomeFragmentAdapter adapter;
	MyHandler handler;
	MemoryLruCache mLruCache;
	FileLruCache fileCache;
	ArrayList<JSONObject> arrayList = new ArrayList<JSONObject>();
	IntentFilter filter;
	PullableListView listView;
	PullToRefreshLayout pullLayout;
	int start_index = -1;
	int pageSize = 4;
    boolean firstIn=true;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.diarylist_activity);
		initView();
	}

	private void initView() {
		//在跳转到该activity之前判断有没有user_id,也就是有没有登录
		ListenerManager.getInstance().setOnDiaryClickListener(this);
		mLruCache = new MemoryLruCache();
		fileCache = new FileLruCache();
		adapter = new HomeFragmentAdapter(DiaryListActivity.this, arrayList,
				mLruCache, fileCache);
		// 必须设置，否则就默认为0,也就是默认为关注页面创建的adapter
		adapter.setFlag(3);
		Button userdiary_back = (Button) findViewById(R.id.userdiary_back);
		pullLayout = (PullToRefreshLayout) findViewById(R.id.userdiary_pulltorefresh);
		listView = (PullableListView) findViewById(R.id.userdiary_list);
		userdiary_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				onBackPressed();
			}
		});

		pullLayout.setOnRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {
				getData(0, pageSize);
			}

			@Override
			public void onLoadMore() {
				// 如果还没有加载过，start_index为-1
				int current = start_index == -1 ? 0 : start_index + pageSize;
				getData(current, current + pageSize);
			}
		});

		listView.setAdapter(adapter);
		listView.setDividerHeight(10);
		handler = new MyHandler(adapter);

		// 滑动时取消加载
		// listView.setOnScrollListener(adapter);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View view,
					int position, long arg3) {
				try {
					int diary_id = arrayList.get(position).getInt("diary_id");
					Intent i = new Intent(DiaryListActivity.this,
							DiaryActivity.class);
					i.putExtra("diary_id", diary_id);
					i.putExtra("flag", 3);
					i.putExtra("position", position);
					i.putExtra("clickState",adapter.clickState.get(position,3));
					// 为了接收广播时确定是哪个item被点击
					startActivity(i);
				} catch (JSONException e) {
					e.printStackTrace();
				}

			}
		});

	}

	
	public void getData(int start, int end) {
		LoadHomeTask home = new LoadHomeTask(DiaryListActivity.this);
		home.setOnLoadHomeListener(this);
		home.setFlag(3);
		int other_id=getIntent().getIntExtra("other_id", 0);
		if(other_id!=0)
		home.execute(other_id, start, end);
		else 
		home.execute(user_id, start, end);
	}

	class MyHandler extends Handler {
		HomeFragmentAdapter fragmentAdapter;

		MyHandler(HomeFragmentAdapter adapter) {
			fragmentAdapter = adapter;

		}

		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 0:
				fragmentAdapter.notifyDataSetChanged();
				if (start_index != 0) {
					listView.smoothScrollBy(300, 500);
				}else {
					//如果是下拉刷新需要清空点击状态信息
					fragmentAdapter.clickState.clear();}
				break;
			case 4:
				pullLayout.refreshFinish(0);

				break;
			case 5:
				pullLayout.refreshFinish(1);
				// 刷新成功，重置为第一页
				start_index = 0;
				break;
			case 6:
				pullLayout.loadmoreFinish(0);

				break;
			case 7:
				pullLayout.loadmoreFinish(1);
				start_index += start_index + pageSize;
				break;
			}

		}
	}

	@Override
	public void LoadJson(JSONObject json, int start_index) {
		try {
			if (json != null) {
				// 需要检查当前列表是否已经有了该日志，检查日期和id就知道是否为新日志
				// 现在简单清除原先列表
				if (start_index == 0) {
					handler.sendEmptyMessage(5);
					// 只有下拉刷新时清除所有，上拉加载时直接往后添加
					arrayList.clear();
				} else {
					handler.sendEmptyMessage(7);
				}

				if (json.getString("action").equals(DIARYLIST_SUCCESS)) {
					JSONArray array = json.getJSONArray("JSONArray");
					for (int i = 0; i < array.length(); i++) {
						// 需要判断是否为新的日志
						arrayList.add(array.getJSONObject(i));
					}
					if(json.has("OtherArray")){
					JSONArray otherarray = json.getJSONArray("OtherArray");
					for (int i = 0; i < otherarray.length(); i++) {
						// 需要判断是否为新的日志
						arrayList.add(otherarray.getJSONObject(i));
					}}
//					// 异步保存到数据库
//					new Thread() {
//						@Override
//						public void run() {
//							for (int i = 0; i < arrayList.size(); i++) {
//								// 如果有应该不保存
//								// 先假定最多保存25条数据
//								if (LocalAccessUtil
//										.getDiaryCount(DiaryListActivity.this) < 25) {
//									LocalAccessUtil.saveDiary(
//											DiaryListActivity.this,
//											arrayList.get(i));
//								} else {
//									// 删除最少使用的
//									int diary_id = LocalAccessUtil
//											.getLeastUsedDiary(DiaryListActivity.this);
//									LocalAccessUtil.deleteDiary(
//											DiaryListActivity.this, diary_id);
//									LocalAccessUtil.saveDiary(
//											DiaryListActivity.this,
//											arrayList.get(i));
//								}
//							}
//						}
//
//					}.start();

					Message message = new Message();
					message.what = 0;
					handler.sendMessageAtFrontOfQueue(message);
				}

			} else {

				if (start_index == 0)
					handler.sendEmptyMessage(4);
				else
					handler.sendEmptyMessage(6);

			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void onDiaryClick(int viewId, int flag,int position) {
		switch (viewId) {
		case R.id.home_comment:
			// 因为position可以为0，代表第一个item
			if (position != -1) {
				// 注意该方法是获取屏幕上可视位置的item，传入的参数是从第一个可视的item的位置算起
				View view = listView.getChildAt(position
						- listView.getFirstVisiblePosition());
				TextView comment = (TextView) view
						.findViewById(R.id.home_comment);
				comment.setText("评论 "
						+ (Integer.parseInt(comment.getText().toString()
								.substring(3)) + 1));
			}
			break;
		case R.id.home_laud:
			if (position != -1) {
				// 注意该方法是获取屏幕上可视位置的item，传入的参数是从第一个可视的item的位置算起
				View view = listView.getChildAt(position
						- listView.getFirstVisiblePosition());
				TextView laud = (TextView) view
						.findViewById(R.id.home_laud);
				laud.setText("点赞 "
						+ (Integer.parseInt(laud.getText().toString()
								.substring(3)) + 1));
			}
			break;
		case R.id.home_transmit:
			if (position != -1) {
				// 注意该方法是获取屏幕上可视位置的item，传入的参数是从第一个可视的item的位置算起
				View view = listView.getChildAt(position
						- listView.getFirstVisiblePosition());
				TextView transmit = (TextView) view
						.findViewById(R.id.home_transmit);
				transmit.setText("转发 "
						+ (Integer.parseInt(transmit.getText().toString()
								.substring(3)) + 1));
			}
			break;
		case R.id.concern_dialog_concern:
		case R.id.concern_dialog_collection:
			// 不管哪个按钮点击，都可以通过下面的更新界面
			View view2 = listView.getChildAt(position
					- listView.getFirstVisiblePosition());
			TextView home_collection = (TextView) view2
					.findViewById(R.id.home_collection);
			TextView home_concerned = (TextView) view2
					.findViewById(R.id.home_concerned);
			if (flag == 0 || flag == 2)
				home_concerned.setVisibility(View.VISIBLE);
			else
				home_concerned.setVisibility(View.INVISIBLE);
			if (flag == 2 || flag == 3)
				home_collection.setVisibility(View.VISIBLE);
			else
				home_collection.setVisibility(View.INVISIBLE);
			adapter.clickState.put(position, flag);
			break;
			
		default:
			break;
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
	
	
	
	


//	
//				diaryList.setOnItemClickListener(new OnItemClickListener() {
//
//					@Override
//					public void onItemClick(AdapterView<?> arg0, View arg1,
//							int arg2, long arg3) {
//						try {
//							if (diaryItem != null) {
//								JSONObject item;
//								item = diaryItem.getJSONObject(arg2);
//								Intent i = new Intent(DiaryListActivity.this,
//										DiaryActivity.class);
//								i.putExtra("diary_id", item.getInt("diary_id"));
//								startActivity(i);
//							}
//						} catch (JSONException e) {
//							e.printStackTrace();
//						}
//					}
//				});
//				diaryList
//						.setOnItemLongClickListener(new OnItemLongClickListener() {
//
//							@Override
//							public boolean onItemLongClick(AdapterView<?> arg0,
//									View arg1, final int arg2, long arg3) {
//
//								AlertDialog.Builder builder = new AlertDialog.Builder(
//										DiaryListActivity.this);
//								builder.setTitle("hello");
//								builder.setItems(R.array.array,
//										new DialogInterface.OnClickListener() {
//
//											@Override
//											public void onClick(
//													DialogInterface arg0,
//													int arg1) {
//												switch (arg1) {
//												case 0:
//													JSONObject item;
//													try {
//														item = diaryItem
//																.getJSONObject(arg2);
//														deleteDiary(item
//																.getInt("diary_id"));
//													} catch (JSONException e) {
//
//														e.printStackTrace();
//													}
//
//													break;
//												case 1:
//													break;
//												case 2:
//													break;
//												}
//
//											}
//										});
//								builder.create().show();
//								return true;
//							}
//						});
//			
//
//		
//	
//		if (ConstantUtil.user_id != 0)
//			getDiaryList(ConstantUtil.user_id);



}
