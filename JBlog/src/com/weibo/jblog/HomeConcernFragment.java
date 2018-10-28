package com.weibo.jblog;

import static com.weibo.utils.ConstantUtil.GETHOME_SUCCESS;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.weibo.adapter.HomeFragmentAdapter;
import com.weibo.connect.LoadHomeTask;

import com.weibo.connect.LoadHomeTask.LoadHomeListener;

import com.weibo.listener.ListenerManager;
import com.weibo.listener.ListenerManager.onDiaryClickConcernListener;
import com.weibo.utils.ConstantUtil;
import com.weibo.utils.FileLruCache;
import com.weibo.utils.LocalAccessUtil;
import com.weibo.utils.MemoryLruCache;
import com.weibo.utils.NetworkUtil;
import com.weibo.view.PullToRefreshLayout;
import com.weibo.view.PullToRefreshLayout.OnRefreshListener;
import com.weibo.view.PullableListView;

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
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;

import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;

import android.widget.TextView;

public class HomeConcernFragment extends Fragment implements LoadHomeListener,
		onDiaryClickConcernListener {
	MyHandler handler;
	HomeFragmentAdapter adapter;
	private boolean isFirstLoaded = true;
	private boolean isVisible = false;
	private boolean isCreated = false;
	MemoryLruCache mLruCache;
	FileLruCache fileCache;
	ArrayList<JSONObject> arrayList = new ArrayList<JSONObject>();
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
		View view = inflater.inflate(R.layout.home_concern_fragment, container,
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
		// ����DiaryActivity�еĵ���¼�
		ListenerManager.getInstance().setOnDiaryClickListener(
				HomeConcernFragment.this);
		mLruCache = new MemoryLruCache();
		fileCache = new FileLruCache();
		adapter = new HomeFragmentAdapter(getActivity(), arrayList, mLruCache,
				fileCache);

		pullLayout = (PullToRefreshLayout) view
				.findViewById(R.id.home_pulltorefresh);
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
		listView = (PullableListView) view.findViewById(R.id.home_concern_list);
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
					// Ϊ�˽��չ㲥ʱȷ�����ĸ�item�����
					i.putExtra("position", position);
					i.putExtra("clickState", adapter.clickState.get(position));
					startActivity(i);
				} catch (JSONException e) {
					e.printStackTrace();
				}

			}
		});

		listView.getViewTreeObserver().addOnGlobalLayoutListener(
				new OnGlobalLayoutListener() {

					@SuppressLint("NewApi")
					@Override
					public void onGlobalLayout() {

						listView.getViewTreeObserver()
								.removeOnGlobalLayoutListener(this);

						if (NetworkUtil.getNetworkState(getActivity()) == NetworkUtil.NONE) {
							JSONArray array = LocalAccessUtil
									.getAllDiary(getActivity());
							for (int i = 0; i < array.length(); i++) {
								// û����ȡ���ػ������־
								try {
									arrayList.add(array.getJSONObject(i));
								} catch (JSONException e) {
									e.printStackTrace();
								}
							}
							handler.sendEmptyMessage(0);
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
				} else {
					// ���������ˢ����Ҫ��յ��״̬��Ϣ
					fragmentAdapter.clickState.clear();
				}
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

				if (json.getString("action").equals(GETHOME_SUCCESS)) {
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
									// �������ͬid��diary�򲻲���
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
	public void onDiaryClick(int viewId, int flag, int position) {

		switch (viewId) {
		case R.id.home_comment:
			if (position != -1) {
				// ע��÷����ǻ�ȡ��Ļ�Ͽ���λ�õ�item������Ĳ����Ǵӵ�һ�����ӵ�item��λ������
				View view = listView.getChildAt(position
						- listView.getFirstVisiblePosition());
				Button comment = (Button) view.findViewById(R.id.home_comment);
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
				TextView laud = (TextView) view.findViewById(R.id.home_laud);
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
