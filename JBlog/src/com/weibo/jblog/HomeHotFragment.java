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
			// ֻ��oncreateview����֮�����ǵ�һ�ε��ò�����
			if (isCreated && isFirstLoaded) {
				// ֻ�ڵ�һ��ʱ�Զ�ˢ��
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
			// ֻ�ڵ�һ��ʱ�Զ�ˢ��
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
		// �������ã������Ĭ��Ϊ0,Ҳ����Ĭ��Ϊ��עҳ�洴����adapter
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
				// �����û�м��ع���start_indexΪ-1
				int current = start_index == -1 ? 0 : start_index + pageSize;
				getData(current, current + pageSize);
			}
		});
		listView = (PullableListView) view.findViewById(R.id.home_hot_list);
		listView.setAdapter(adapter);
		listView.setDividerHeight(10);
		handler = new MyHandler(adapter);

		// ����ʱȡ������
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
		// Ҳ���ǵ���עҳ��ˢ��ʱ�����Ѿ��ڸ�������ҳ��
		listView.getViewTreeObserver().addOnGlobalLayoutListener(
				new OnGlobalLayoutListener() {

					@SuppressLint("NewApi")
					@Override
					public void onGlobalLayout() {
						listView.getViewTreeObserver()
								.removeOnGlobalLayoutListener(this);
						
						if (NetworkUtil.getNetworkState(getActivity()) == NetworkUtil.NONE) {
							
							// �Ͳ��������ŵ�΢���� ����û��ҳ��
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
					//���������ˢ����Ҫ��յ��״̬��Ϣ
					fragmentAdapter.clickState.clear();}
				break;
			case 4:
				pullLayout.refreshFinish(0);

				break;
			case 5:
				pullLayout.refreshFinish(1);
				// ˢ�³ɹ�������Ϊ��һҳ
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

		// ���û�оͼ�������΢����
		LoadHomeTask home = new LoadHomeTask(getActivity());
		home.setOnLoadHomeListener(this);
		home.setFlag(1);
		home.execute(ConstantUtil.user_id, start, end);
		isFirstLoaded = false;

	}

	@Override
	public void LoadJson(JSONObject json, int start_index) {
		// ��Ҫ�ж�json�Ƿ�Ϊ��
		try {
			if (json != null) {

				// ��Ҫ��鵱ǰ�б��Ƿ��Ѿ����˸���־��������ں�id��֪���Ƿ�Ϊ����־
				// ���ڼ����ԭ���б�
				if (start_index == 0) {
					handler.sendEmptyMessage(5);
					// ֻ������ˢ��ʱ������У���������ʱֱ���������
					arrayList.clear();
				} else {
					handler.sendEmptyMessage(7);
				}

				if (json.getString("action").equals(GETHOT_SUCCESS)) {
					JSONArray array = json.getJSONArray("JSONArray");
					for (int i = 0; i < array.length(); i++) {
						// ��Ҫ�ж��Ƿ�Ϊ�µ���־
						arrayList.add(array.getJSONObject(i));
					}
					// �첽���浽���ݿ�
					new Thread() {
						@Override
						public void run() {
							for (int i = 0; i < arrayList.size(); i++) {
								// �����Ӧ�ò�����
								// �ȼٶ���ౣ��25������
								if (LocalAccessUtil
										.getDiaryCount(getActivity()) < 25) {
									LocalAccessUtil.saveDiary(getActivity(),
											arrayList.get(i));
								} else {
									// ɾ������ʹ�õ�
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
			// ��Ϊposition����Ϊ0�������һ��item
			if (position != -1) {
				// ע��÷����ǻ�ȡ��Ļ�Ͽ���λ�õ�item������Ĳ����Ǵӵ�һ�����ӵ�item��λ������
				View view = listView.getChildAt(position
						- listView.getFirstVisiblePosition());
				TextView comment = (TextView) view
						.findViewById(R.id.home_comment);
				comment.setText("���� "
						+ (Integer.parseInt(comment.getText().toString()
								.substring(3)) + 1));
			}
			break;
		case R.id.home_laud:
			if (position != -1) {
				// ע��÷����ǻ�ȡ��Ļ�Ͽ���λ�õ�item������Ĳ����Ǵӵ�һ�����ӵ�item��λ������
				View view = listView.getChildAt(position
						- listView.getFirstVisiblePosition());
				TextView laud = (TextView) view
						.findViewById(R.id.home_laud);
				laud.setText("���� "
						+ (Integer.parseInt(laud.getText().toString()
								.substring(3)) + 1));
			}
			break;
		case R.id.home_transmit:
			if (position != -1) {
				// ע��÷����ǻ�ȡ��Ļ�Ͽ���λ�õ�item������Ĳ����Ǵӵ�һ�����ӵ�item��λ������
				View view = listView.getChildAt(position
						- listView.getFirstVisiblePosition());
				TextView transmit = (TextView) view
						.findViewById(R.id.home_transmit);
				transmit.setText("ת�� "
						+ (Integer.parseInt(transmit.getText().toString()
								.substring(3)) + 1));
			}
			break;
		case R.id.concern_dialog_concern:
		case R.id.concern_dialog_collection:
			// �����ĸ���ť�����������ͨ������ĸ��½���
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
