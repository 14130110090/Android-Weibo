package com.weibo.adapter;

import static com.weibo.utils.ConstantUtil.CANCELATTENTION_SUCCESS;
import static com.weibo.utils.ConstantUtil.CANCELCOLLECTION_SUCCESS;
import static com.weibo.utils.ConstantUtil.SETATTENTION_SUCCESS;
import static com.weibo.utils.ConstantUtil.SETCOLLECTION_SUCCESS;
import static com.weibo.utils.ConstantUtil.user_id;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import com.weibo.connect.ConnectManager;
import com.weibo.connect.ConnectManager.OnJsonReturnListener;

import com.weibo.jblog.R;
import com.weibo.listener.ListenerManager;

import com.weibo.utils.FileLruCache;
import com.weibo.utils.LocalAccessUtil;
import com.weibo.utils.MemoryLruCache;
import com.weibo.view.MyDialog;
import com.weibo.view.MyImageView;

import android.app.Activity;

import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;

import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class UserListAdapter extends BaseAdapter {
	Activity activity;
	ArrayList<JSONObject> list;
	MemoryLruCache mLruCache;
	FileLruCache fileCache;
	// 用来判断是谁创建该adapter,0代表关注页面，1代表粉丝页面
	//int flag = 0;
	ConnectManager manager;
	// 0为取消关注，1为关注
	SparseIntArray clickState = new SparseIntArray();
	// 用来保存当前点击的item的位置，传入按钮的点击事件中
	int current_position = -1;
	HolderView current_holderView = null;
	MyDialog dialog;

	public UserListAdapter(final Activity activity, ArrayList<JSONObject> list,
			MemoryLruCache mLruCache, FileLruCache fileCache) {
		this.activity = activity;
		this.list = list;
		this.mLruCache = mLruCache;
		this.fileCache = fileCache;
		manager = new ConnectManager();
		manager.setOnJsonReturnListener(new OnJsonReturnListener() {

			@Override
			public void onJsonReturn(JSONObject result) {
				if (result != null) {
					try {
						if (result.getString("action").equals(
								SETATTENTION_SUCCESS)) {
							int i = result.getInt("result");
							// 0代表已经关注了此人，不要再添加关注数
							if (i != 0) {
								ListenerManager.getInstance().onCountChanged(
										SETATTENTION_SUCCESS, -1);
								LocalAccessUtil.addConcernCount(activity,
										user_id);
							}

						} else if (result.getString("action").equals(
								CANCELATTENTION_SUCCESS)) {
							int i = result.getInt("result");
							// 为0代表没有删除任何记录
							if (i != 0) {
								ListenerManager.getInstance().onCountChanged(
										CANCELATTENTION_SUCCESS, -1);
								LocalAccessUtil.minusConcernCount(activity,
										user_id);
							}

						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}

			}
		});
	}
//每次数据集更新时清空标志状态
	public void clearState(){
		clickState.clear();
	}
	
	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int arg0) {
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		return 0;
	}

	@Override
	public View getView(final int position, View currentView, ViewGroup arg2) {
		HolderView holderView = null;
		MyOnClickListener mListener = null;
		if (currentView == null) {
			holderView = new HolderView();
			currentView = LayoutInflater.from(activity).inflate(
					R.layout.userlist_item, null);
			holderView.userlist_name = (TextView) currentView
					.findViewById(R.id.userlist_name);
			holderView.userlist_head = (ImageView) currentView
					.findViewById(R.id.userlist_head);
			holderView.userlist_desc = (TextView) currentView
					.findViewById(R.id.userlist_desc);
			holderView.userlist_more = (MyImageView) currentView
					.findViewById(R.id.userlist_more);
			holderView.userlist_concerned = (TextView) currentView
					.findViewById(R.id.userlist_concerned);
			mListener = new MyOnClickListener(holderView);
			holderView.userlist_more.setOnClickListener(mListener);
			currentView.setTag(holderView);

		} else {
			holderView = (HolderView) currentView.getTag();
		}
		try {
			JSONObject json = list.get(position);
			if (json.has("user_desc"))
				holderView.userlist_desc.setText(json.getString("user_desc"));
			else
				holderView.userlist_desc.setText("空空如也");
			holderView.userlist_name.setText(json.getString("user_name"));
			// 需要缓存了
			if (json.has("user_head")) {
				JSONObject head = json.getJSONObject("user_head");
				if (head.length() != 0) {
					ConnectManager.loadBitmap(mLruCache, fileCache,
							head.getString("head_data"),
							holderView.userlist_head);
				} else
					holderView.userlist_head.setImageDrawable(null);
			}
			// 必须设置每个按钮的位置，也可以用tag，但继承的button可以传入多个参数
			holderView.userlist_more.setPosition(position);
			if (clickState.indexOfKey(position) < 0) {
				if (json.getBoolean("hasAttention")) {
					clickState.put(position, 0);
				} else
					clickState.put(position, 1);
			}
			if (clickState.get(position) == 0)
				holderView.userlist_concerned.setVisibility(View.VISIBLE);
			else
				holderView.userlist_concerned.setVisibility(View.INVISIBLE);

		} catch (JSONException e) {
			e.printStackTrace();
		}

		return currentView;
	}

	public class HolderView {
		private ImageView userlist_head;
		private TextView userlist_name;
		private MyImageView userlist_more;
		private TextView userlist_desc;
		private TextView userlist_concerned;
	}

	private class MyOnClickListener implements OnClickListener {

		HolderView view;

		public MyOnClickListener(HolderView view) {

			this.view = view;
		}

		@Override
		public void onClick(View v) {
			current_position = ((MyImageView) v).getPosition();
			if (dialog != null) {
				// 绝对不能再重新加载一个布局，然后获取控件，因为重新加载的是新的布局
				Button dialog_concern = (Button) dialog
						.findViewById(R.id.dialog_userlist_concern);

				// 根据当前状态
				current_holderView = view;

				switch (clickState.get(current_position)) {
				case 0:
					dialog_concern.setText("取消关注");

					break;
				case 1:
					dialog_concern.setText("关注");
					break;
				}

				// dismiss不会删除dialog，还在后台运行
				dialog.show();
			} else {
				// 第一次创建，是初始状态，后来的状态变化不会影响这里

				View concern_dialog_view = LayoutInflater.from(activity)
						.inflate(R.layout.dialog_userlist, null);
				dialog = new MyDialog(activity, 600, 150, concern_dialog_view,
						R.style.MyDialog);
				dialog.show();
				final Button dialog_concern = (Button) concern_dialog_view
						.findViewById(R.id.dialog_userlist_concern);
				if (current_holderView == null)
					current_holderView = view;
				// 一开始状态应该是固定的，除非之后给每个微博加入收藏和关注的判断标志，

				switch (clickState.get(current_position)) {
				case 0:
					dialog_concern.setText("取消关注");
					break;
				case 1:
					dialog_concern.setText("关注");
					break;
				}

				dialog_concern.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						dialog.dismiss();
						try {
							if (dialog_concern.getText().equals("关注")) {
								dialog_concern.setText("取消关注");

								clickState.put(current_position,
										clickState.get(current_position) - 1);

								current_holderView.userlist_concerned
										.setVisibility(View.VISIBLE);
								manager.setAttention(
										user_id,
										list.get(current_position).getInt(
												"user_id"));

							} else {
								dialog_concern.setText("关注");

								clickState.put(current_position,
										clickState.get(current_position) + 1);

								current_holderView.userlist_concerned
										.setVisibility(View.INVISIBLE);
								manager.cancelAttention(
										user_id,
										list.get(current_position).getInt(
												"user_id"));

							}

						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				});
			}

		}
	}

}
