package com.weibo.jblog;

import static com.weibo.utils.ConstantUtil.GETHOT_SUCCESS;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.weibo.adapter.HomeFragmentAdapter;
import com.weibo.connect.LoadHomeTask;

import com.weibo.connect.LoadHomeTask.LoadHomeListener;

import com.weibo.listener.ListenerManager;
import com.weibo.listener.ListenerManager.onDiaryClickHotListener;
import com.weibo.utils.ConstantUtil;
import com.weibo.utils.FileLruCache;
import com.weibo.utils.LocalAccessUtil;
import com.weibo.utils.MemoryLruCache;
import com.weibo.utils.NetworkUtil;
import com.weibo.view.PullToRefreshLayout;
import com.weibo.view.PullableListView;
import com.weibo.view.PullToRefreshLayout.OnRefreshListener;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.IntentFilter;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.AdapterView;

import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class HomeHotFragment extends Fragment implements LoadHomeListener,
		onDiaryClickHotListener {

	MyHandler handler;
	HomeFragmentAdapter adapter;
	private boolean isFirstLoaded = true;
	private boolean isVisible = false;
	private boolean isCreated = false;
	MemoryLruCache mLruCache;
	FileLruCache fileCache;
	ArrayList<JSONObject> arrayList = new ArrayList<JSONObject>();
	IntentFilter filter;
	PullableListView listView;
	PullToRefreshLayout pullLayout;
	int start_index = -1;
	int pageSize = 4;

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);
		if (isVisibleToUser) {
			isVisible = true;
			// 只有oncreateview调用之后并且是第一次调用才联网
			if (isCreated && isFirstLoaded) {
				// 只在第一次时自动刷新
				// pullLayout.autoRefresh();

			}
		} else
			isVisible = false;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.home_hot_fragment, container,
				false);
		initView(view);
		isCreated = true;
		if (isVisible) {
			// 只在第一次时自动刷新
			// pullLayout.autoRefresh();

		}
		return view;
	}

	public void initView(View view) {
		ListenerManager.getInstance().setOnDiaryClickListener(
				HomeHotFragment.this);
		mLruCache = new MemoryLruCache();
		fileCache = new FileLruCache();
		adapter = new HomeFragmentAdapter(getActivity(), arrayList, mLruCache,
				fileCache);
		// 必须设置，否则就默认为0,也就是默认为关注页面创建的adapter
		adapter.setFlag(1);
		pullLayout = (PullToRefreshLayout) view
				.findViewById(R.id.hot_pulltorefresh);
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
		listView = (PullableListView) view.findViewById(R.id.home_hot_list);
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
					Intent i = new Intent(getActivity(), DiaryActivity.class);
					i.putExtra("diary_id", diary_id);
					i.putExtra("flag", 1);
					i.putExtra("position", position);
					i.putExtra("clickState",adapter.clickState.get(position,1));
					startActivity(i);
				} catch (JSONException e) {
					e.printStackTrace();
				}

			}
		});
		// 也就是当关注页面刷新时，就已经在更新热门页面
		listView.getViewTreeObserver().addOnGlobalLayoutListener(
				new OnGlobalLayoutListener() {

					@SuppressLint("NewApi")
					@Override
					public void onGlobalLayout() {
						listView.getViewTreeObserver()
								.removeOnGlobalLayoutListener(this);
						
						if (NetworkUtil.getNetworkState(getActivity()) == NetworkUtil.NONE) {
							
							// 就不缓存热门的微博， 跳到没网页面
						} else
							pullLayout.autoRefresh();
					}

				});
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

	public void getData(int start, int end) {

		// 如果没有就加载热门微博。
		LoadHomeTask home = new LoadHomeTask(getActivity());
		home.setOnLoadHomeListener(this);
		home.setFlag(1);
		home.execute(ConstantUtil.user_id, start, end);
		isFirstLoaded = false;

	}

	@Override
	public void LoadJson(JSONObject json, int start_index) {
		// 需要判断json是否为空
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

				if (json.getString("action").equals(GETHOT_SUCCESS)) {
					JSONArray array = json.getJSONArray("JSONArray");
					for (int i = 0; i < array.length(); i++) {
						// 需要判断是否为新的日志
						arrayList.add(array.getJSONObject(i));
					}
					// 异步保存到数据库
					new Thread() {
						@Override
						public void run() {
							for (int i = 0; i < arrayList.size(); i++) {
								// 如果有应该不保存
								// 先假定最多保存25条数据
								if (LocalAccessUtil
										.getDiaryCount(getActivity()) < 25) {
									LocalAccessUtil.saveDiary(getActivity(),
											arrayList.get(i));
								} else {
									// 删除最少使用的
									int diary_id = LocalAccessUtil
											.getLeastUsedDiary(getActivity());
									LocalAccessUtil.deleteDiary(getActivity(),
											diary_id);
									LocalAccessUtil.saveDiary(getActivity(),
											arrayList.get(i));
								}
							}
						}

					}.start();

					handler.sendEmptyMessage(0);
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
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onDiaryClick(int viewId, int flag, int position) {
		
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

}
