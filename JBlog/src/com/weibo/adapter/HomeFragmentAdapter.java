package com.weibo.adapter;

import static com.weibo.utils.ConstantUtil.*;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;

import android.content.Intent;
import android.graphics.Bitmap;

import android.os.Handler;

import android.os.Message;

import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;

import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.weibo.connect.ConnectManager.OnJsonReturnListener;

import com.weibo.connect.ConnectManager;
import com.weibo.jblog.DiaryActivity;

import com.weibo.jblog.PhotoShowActivity;
import com.weibo.jblog.R;
import com.weibo.jblog.UserActivity;
import com.weibo.listener.ListenerManager;
import com.weibo.utils.FileLruCache;
import com.weibo.utils.LocalAccessUtil;
import com.weibo.utils.MemoryLruCache;

import com.weibo.view.MyDialog;
import com.weibo.view.MyGridView;
import com.weibo.view.MyGridView.onTouchBlankSpaceListener;
import com.weibo.view.PhotoView.OTHER;
import com.weibo.view.MyImageView;

public class HomeFragmentAdapter extends BaseAdapter {

	Activity activity;
	ArrayList<JSONObject> list;
	MemoryLruCache mLruCache;
	FileLruCache fileCache;
	// �����ж���˭������adapter,0�����עҳ�棬1��������ҳ��,2��������΢��
	int flag = 0;
	ConnectManager manager;
	MyDialog dialog;
	// �������浱ǰ�����item��λ�ã����밴ť�ĵ���¼���
	int current_position = -1;
	HolderView current_holderView = null;
	MyHandler handler;
	// ���������ע���ղصĵ��״̬
	// ��ʼ̬ 0:ȡ����ע���ղ�
	// 1:��ע���ղ�
	// 2:ȡ����ע��ȡ���ղ�
	// 3:��ע��ȡ���ղ�
	public SparseIntArray clickState = new SparseIntArray();

	public HomeFragmentAdapter(final Activity activity,
			final ArrayList<JSONObject> list, MemoryLruCache mLruCache,
			FileLruCache fileCache) {
		this.activity = activity;
		this.list = list;
		this.mLruCache = mLruCache;
		this.fileCache = fileCache;
		handler = new MyHandler();
		manager = new ConnectManager();
		manager.setOnJsonReturnListener(new OnJsonReturnListener() {

			@Override
			public void onJsonReturn(JSONObject result) {
				if (result != null) {
					try {
						if (result.getString("action").equals(
								SETCOLLECTION_SUCCESS)) {
							// �����ղسɹ�
							// ����Ҫ���ղسɹ�����ʾ��
						} else if (result.getString("action").equals(
								SETATTENTION_SUCCESS)) {

							int i = result.getInt("result");
							// 0�����Ѿ���ע�˴��ˣ���Ҫ����ӹ�ע��
							if (i != 0) {
								ListenerManager.getInstance().onCountChanged(
										SETATTENTION_SUCCESS, -1);
								LocalAccessUtil.addConcernCount(activity,
										user_id);
							}

						} else if (result.getString("action").equals(
								CANCELCOLLECTION_SUCCESS)) {

						} else if (result.getString("action").equals(
								CANCELATTENTION_SUCCESS)) {
							int i = result.getInt("result");
							// Ϊ0����û��ɾ���κμ�¼
							if (i != 0) {
								ListenerManager.getInstance().onCountChanged(
										CANCELATTENTION_SUCCESS, -1);
								LocalAccessUtil.minusConcernCount(activity,
										user_id);
							}
						} else if (result.getString("action").equals(
								ADDLAUD_SUCCESS)) {
							int i = result.getInt("result");
							if (i != 0) {
								handler.sendEmptyMessage(1);
							} else
								handler.sendEmptyMessage(0);
						} else if (result.getString("action").equals(
								ADDTRANSMIT_SUCCESS)) {
							handler.sendEmptyMessage(2);
						}

					} catch (JSONException e) {
						e.printStackTrace();
					}
				}

			}
		});
	}

	public void setFlag(int flag) {
		this.flag = flag;
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
					R.layout.home_fragment_item, null);
			holderView.home_time = (TextView) currentView
					.findViewById(R.id.home_time);
			holderView.home_name = (TextView) currentView
					.findViewById(R.id.home_name);
			holderView.home_head = (ImageView) currentView
					.findViewById(R.id.home_head);
			holderView.home_content = (TextView) currentView
					.findViewById(R.id.home_content);
			holderView.view = (MyGridView) currentView
					.findViewById(R.id.home_gridview);
			holderView.comment = (Button) currentView
					.findViewById(R.id.home_comment);
			holderView.transmit = (Button) currentView
					.findViewById(R.id.home_transmit);
			holderView.laud = (Button) currentView.findViewById(R.id.home_laud);
			holderView.home_more = (MyImageView) currentView
					.findViewById(R.id.home_more);
			holderView.home_collection = (TextView) currentView
					.findViewById(R.id.home_collection);
			holderView.home_concerned = (TextView) currentView
					.findViewById(R.id.home_concerned);
			holderView.other_content = (TextView) currentView
					.findViewById(R.id.other_content);
			mListener = new MyOnClickListener(holderView);
			holderView.home_more.setOnClickListener(mListener);
			currentView.setTag(holderView);
		} else {
			holderView = (HolderView) currentView.getTag();
		}
		try {
			JSONObject json = list.get(position);
			holderView.home_content.setText(json.getString("diary_content"));
			holderView.home_time.setText(json.getString("diary_date"));
			holderView.home_name.setText(json.getString("user_name"));
			// ��Ҫ������
			if (json.has("user_head")) {
				JSONObject head = json.getJSONObject("user_head");
				if (head.length() != 0) {

					ConnectManager.loadBitmap(mLruCache, fileCache,
							head.getString("head_data"), holderView.home_head);
				} else
					holderView.home_head.setImageDrawable(null);
			}
			holderView.home_head.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					try {
						Intent intent = new Intent(activity, UserActivity.class);
						intent.putExtra("other_id",
								list.get(position).getInt("user_id"));
						activity.startActivity(intent);
					} catch (JSONException e) {
						e.printStackTrace();
					}

				}
			});

			if (json.has("other_name")) {
                holderView.other_content.setText(json.getString("other_name")+" : "+json.getString("other_content"));
                holderView.other_content.setVisibility(View.VISIBLE);
			} else {
				holderView.other_content.setVisibility(View.GONE);
			}
			
			// ��������ÿ����ť��λ�ã�Ҳ������tag�����̳е�button���Դ���������
			holderView.home_more.setPosition(position);
			if (clickState.indexOfKey(position) < 0) {
				// ֤��clickState��positionλ��û�и���ֵ
				if (json.getBoolean("hasAttention")) {
					if (json.getBoolean("hasCollection")) {
						clickState.put(position, 2);
					} else
						clickState.put(position, 0);
				} else {
					if (json.getBoolean("hasCollection")) {
						clickState.put(position, 3);
					} else
						clickState.put(position, 1);
				}
			}
			// clickState��ÿ��position��һ������ֵ
			if (clickState.get(position) == 0 || clickState.get(position) == 2)
				holderView.home_concerned.setVisibility(View.VISIBLE);
			else
				holderView.home_concerned.setVisibility(View.INVISIBLE);
			if (clickState.get(position) == 2 || clickState.get(position) == 3)
				holderView.home_collection.setVisibility(View.VISIBLE);
			else
				holderView.home_collection.setVisibility(View.INVISIBLE);

			JSONArray pic = json.getJSONArray("pic");
			HomePictureGridViewAdapter homeadapter = new HomePictureGridViewAdapter(
					mLruCache, fileCache, activity, pic);
			holderView.view.setAdapter(homeadapter);
			holderView.view.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int item_position, long arg3) {
					Intent i = new Intent(activity, PhotoShowActivity.class);

					try {// ��·����ȡ����
						JSONArray jsonArray = list.get(position).getJSONArray(
								"pic");
						JSONArray array = new JSONArray();
						for (int j = 0; j < jsonArray.length(); j++) {
							array.put(jsonArray.getJSONObject(j).getString(
									"photo_data"));
						}
						i.putExtra("url", array.toString());
						i.putExtra("position", item_position);
						activity.startActivity(i);
					} catch (JSONException e) {
						e.printStackTrace();
					}

				}
			});
			holderView.view
					.setOnTouchBlankSpaceListener(new onTouchBlankSpaceListener() {

						@Override
						public boolean onTouchBlankSpace(int motionPosition) {
							int diary_id = 0;
							try {
								diary_id = list.get(position)
										.getInt("diary_id");
							} catch (JSONException e) {
								e.printStackTrace();
							}
							Intent i = new Intent(activity, DiaryActivity.class);
							i.putExtra("diary_id", diary_id);
							i.putExtra("flag", flag);
							i.putExtra("position", position);
							i.putExtra("clickState", clickState.get(position));
							activity.startActivity(i);
							return true;
						}
					});
			holderView.comment.setText("���� " + json.getString("comment_count"));
			holderView.comment.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					int diary_id = 0;
					try {
						diary_id = list.get(position).getInt("diary_id");
					} catch (JSONException e) {
						e.printStackTrace();
					}
					Intent i = new Intent(activity, DiaryActivity.class);
					i.putExtra("diary_id", diary_id);
					// ��ת��diary������ҳ��
					i.putExtra("to_comment", true);
					i.putExtra("flag", flag);
					i.putExtra("position", position);
					i.putExtra("clickState", clickState.get(position));
					activity.startActivity(i);
				}
			});
			holderView.transmit.setText("ת�� "
					+ json.getString("transmit_count"));
			holderView.transmit.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// ����Ҫ�޸�
					current_position = position;
					try {
						manager.addTransmit(user_id,
								list.get(position).getInt("diary_id"), "ת��");
						ListenerManager.getInstance().onCountChanged(PUBLISH_SUCCESS, 1);
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			});
			holderView.laud.setText("���� " + json.getString("laud_count"));
			holderView.laud.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					current_position = position;
					try {
						manager.addLaud(user_id,
								list.get(position).getInt("diary_id"));
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			});

		} catch (JSONException e) {
			e.printStackTrace();
		}

		return currentView;
	}

	class MyHandler extends Handler {

		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 0:
				Toast.makeText(activity, "����", Toast.LENGTH_SHORT).show();
				break;
			case 1:
				try {
					LocalAccessUtil.addLaudCount(activity,
							list.get(current_position).getInt("diary_id"));
				} catch (JSONException e) {
					e.printStackTrace();
				}
				if (flag == 0)
					ListenerManager.getInstance().onDiaryClickConcern(
							R.id.home_laud, flag, current_position);
				else if (flag == 1)
					ListenerManager.getInstance().onDiaryClickHot(
							R.id.home_laud, flag, current_position);
				else if (flag == 2)
					ListenerManager.getInstance().onDiaryClickCollection(
							R.id.home_laud, flag, current_position);
				else if (flag == 3)
					ListenerManager.getInstance().onDiaryClickUser(
							R.id.home_laud, flag, current_position);
				break;
			case 2:
				try {
					LocalAccessUtil.addTransmitCount(activity,
							list.get(current_position).getInt("diary_id"));
				} catch (JSONException e) {
					e.printStackTrace();
				}
				if (flag == 0)
					ListenerManager.getInstance().onDiaryClickConcern(
							R.id.home_transmit, flag, current_position);
				else if (flag == 1)
					ListenerManager.getInstance().onDiaryClickHot(
							R.id.home_transmit, flag, current_position);
				else if (flag == 2)
					ListenerManager.getInstance().onDiaryClickCollection(
							R.id.home_transmit, flag, current_position);
				else if (flag == 3)
					ListenerManager.getInstance().onDiaryClickUser(
							R.id.home_transmit, flag, current_position);
				break;

			}

		}
	}

	public class HolderView {
		private ImageView home_head;
		private TextView home_name;
		private MyGridView view;
		private MyImageView home_more;
		private TextView home_content;
		private TextView home_time;
		private Button comment;
		private Button laud;
		private Button transmit;
		private TextView home_concerned;
		private TextView home_collection;
		private TextView other_content;
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
				// ���Բ��������¼���һ�����֣�Ȼ���ȡ�ؼ�����Ϊ���¼��ص����µĲ���
				Button concern_dialog_concern = (Button) dialog
						.findViewById(R.id.concern_dialog_concern);
				Button concern_dialog_collection = (Button) dialog
						.findViewById(R.id.concern_dialog_collection);
				// ���ݵ�ǰ״̬
				current_holderView = view;
				switch (clickState.get(current_position)) {
				case 0:
					concern_dialog_concern.setText("ȡ����ע");
					concern_dialog_collection.setText("�ղ�");
					break;
				case 1:
					concern_dialog_concern.setText("��ע");
					concern_dialog_collection.setText("�ղ�");
					break;
				case 2:
					concern_dialog_concern.setText("ȡ����ע");
					concern_dialog_collection.setText("ȡ���ղ�");
					break;
				case 3:
					concern_dialog_concern.setText("��ע");
					concern_dialog_collection.setText("ȡ���ղ�");
					break;
				}

				// dismiss����ɾ��dialog�����ں�̨����
				dialog.show();
			} else {
				// ��һ�δ������ǳ�ʼ״̬��������״̬�仯����Ӱ������

				View concern_dialog_view = LayoutInflater.from(activity)
						.inflate(R.layout.concern_dialog, null);
				dialog = new MyDialog(activity, concern_dialog_view,
						R.style.MyDialog);
				dialog.show();
				final Button concern_dialog_concern = (Button) concern_dialog_view
						.findViewById(R.id.concern_dialog_concern);
				final Button concern_dialog_collection = (Button) concern_dialog_view
						.findViewById(R.id.concern_dialog_collection);
				if (current_holderView == null)
					current_holderView = view;
				switch (clickState.get(current_position)) {
				case 0:
					concern_dialog_concern.setText("ȡ����ע");
					concern_dialog_collection.setText("�ղ�");
					break;
				case 1:
					concern_dialog_concern.setText("��ע");
					concern_dialog_collection.setText("�ղ�");
					break;
				case 2:
					concern_dialog_concern.setText("ȡ����ע");
					concern_dialog_collection.setText("ȡ���ղ�");
					break;
				case 3:
					concern_dialog_concern.setText("��ע");
					concern_dialog_collection.setText("ȡ���ղ�");
					break;
				}

				concern_dialog_concern
						.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								dialog.dismiss();
								try {
									if (concern_dialog_concern.getText()
											.equals("��ע")) {
										concern_dialog_concern.setText("ȡ����ע");
										clickState
												.put(current_position,
														clickState
																.get(current_position) - 1);
										current_holderView.home_concerned
												.setVisibility(View.VISIBLE);
										manager.setAttention(user_id,
												list.get(current_position)
														.getInt("user_id"));

									} else {
										concern_dialog_concern.setText("��ע");
										current_holderView.home_concerned
												.setVisibility(View.INVISIBLE);

										clickState
												.put(current_position,
														clickState
																.get(current_position) + 1);

										manager.cancelAttention(user_id,
												list.get(current_position)
														.getInt("user_id"));

									}
								} catch (JSONException e) {
									e.printStackTrace();
								}
							}
						});

				concern_dialog_collection
						.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View arg0) {
								dialog.dismiss();
								try {
									if (concern_dialog_collection.getText()
											.equals("�ղ�")) {
										concern_dialog_collection
												.setText("ȡ���ղ�");

										clickState
												.put(current_position,
														clickState
																.get(current_position) + 2);

										current_holderView.home_collection
												.setVisibility(View.VISIBLE);
										manager.setCollection(user_id,
												list.get(current_position)
														.getInt("diary_id"));

									} else {
										concern_dialog_collection.setText("�ղ�");

										clickState
												.put(current_position,
														clickState
																.get(current_position) - 2);
										current_holderView.home_collection
												.setVisibility(View.INVISIBLE);
										manager.cancelCollection(user_id,
												list.get(current_position)
														.getInt("diary_id"));

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
