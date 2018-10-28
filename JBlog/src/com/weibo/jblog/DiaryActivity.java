package com.weibo.jblog;

import java.io.IOException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.weibo.adapter.CommentAdapter;
import com.weibo.adapter.HomePictureGridViewAdapter;
import com.weibo.connect.ConnectManager.OnJsonReturnListener;

import com.weibo.connect.ConnectManager;
import com.weibo.listener.ListenerManager;
import com.weibo.utils.FileLruCache;
import com.weibo.utils.LocalAccessUtil;
import com.weibo.utils.MemoryLruCache;
import com.weibo.utils.StringUtil;
import com.weibo.view.MyDialog;
import com.weibo.view.MyGridView;
import com.weibo.view.PhotoView.OTHER;

import static com.weibo.utils.ConstantUtil.*;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

public class DiaryActivity extends Activity implements OnClickListener {
	private static final int COMMENT_MAXLENGTH = 140;

	ProgressDialog pd = null;
	MemoryLruCache mLruCache;
	FileLruCache fileCache;
	int diary_id = 0;
	int position = -1;
	ListView listView;
	ConnectManager manager;
	Button comment;
	Button laud;
	Button transmit;
	int flag = 0;
	MyDialog dialog;
	int clickState = -1;
	int current_userid = 0;
	TextView home_concerned = null;
	TextView home_collection = null;

	Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 1:
				JSONObject item = (JSONObject) msg.obj;
				try {
					JSONArray commentArray = item.getJSONArray("array");

					CommentAdapter adapter = new CommentAdapter(
							DiaryActivity.this, commentArray, mLruCache,
							fileCache);
					listView.setAdapter(adapter);
					listView.setDividerHeight(5);
				} catch (JSONException e) {
					e.printStackTrace();
				}
				break;
			case 2:
				LocalAccessUtil.addCommentCount(DiaryActivity.this, diary_id);
				// ˢ������
				manager.getComments(diary_id);
				comment.setText("���� "
						+ (Integer.parseInt(comment.getText().toString()
								.substring(3)) + 1));
				Toast.makeText(DiaryActivity.this, "���ͳɹ�", Toast.LENGTH_SHORT)
						.show();
				// �ص���ʹ��ҳ������������
				if (flag == 0) {
					ListenerManager.getInstance().onDiaryClickConcern(
							R.id.home_comment, flag, position);
				} else if (flag == 1) {
					ListenerManager.getInstance().onDiaryClickHot(
							R.id.home_comment, flag, position);
				} else if (flag == 2) {
					ListenerManager.getInstance().onDiaryClickCollection(
							R.id.home_comment, flag, position);
				}

				break;
			case 3:
				Toast.makeText(DiaryActivity.this, "����ʧ��", Toast.LENGTH_SHORT)
						.show();

				break;
			case 4:
				laud.setText("���� "
						+ (Integer.parseInt(laud.getText().toString()
								.substring(3)) + 1));
				LocalAccessUtil.addLaudCount(DiaryActivity.this, diary_id);
				if (flag == 0) {
					ListenerManager.getInstance().onDiaryClickConcern(
							R.id.home_laud, flag, position);
				} else if (flag == 1) {
					ListenerManager.getInstance().onDiaryClickHot(
							R.id.home_laud, flag, position);
				} else if (flag == 2) {
					ListenerManager.getInstance().onDiaryClickCollection(
							R.id.home_laud, flag, position);
				}
				break;
			case 5:
				transmit.setText("ת�� "
						+ (Integer.parseInt(transmit.getText().toString()
								.substring(3)) + 1));
				Toast.makeText(DiaryActivity.this, "ת���ɹ�", Toast.LENGTH_SHORT)
						.show();
				ListenerManager.getInstance()
						.onCountChanged(PUBLISH_SUCCESS, 1);
				LocalAccessUtil.addTransmitCount(DiaryActivity.this, diary_id);
				if (flag == 0) {
					ListenerManager.getInstance().onDiaryClickConcern(
							R.id.home_transmit, flag, position);
				} else if (flag == 1) {
					ListenerManager.getInstance().onDiaryClickHot(
							R.id.home_transmit, flag, position);
				} else if (flag == 2) {
					ListenerManager.getInstance().onDiaryClickCollection(
							R.id.home_transmit, flag, position);
				}
				break;
			case 6:
				Toast.makeText(DiaryActivity.this, "����", Toast.LENGTH_SHORT)
						.show();
				break;
			}
		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.diary_activity);
		initView();
		getDiary(diary_id);
	}

	public void initView() {
		Intent i = getIntent();
		fileCache = new FileLruCache();
		mLruCache = new MemoryLruCache();
		diary_id = i.getIntExtra("diary_id", 0);
		flag = i.getIntExtra("flag", 0);
		position = i.getIntExtra("position", -1);
		clickState = i.getIntExtra("clickState", -1);
		listView = (ListView) findViewById(R.id.comment_list);
		comment = (Button) findViewById(R.id.home_comment);
		transmit = (Button) findViewById(R.id.home_transmit);
		laud = (Button) findViewById(R.id.home_laud);
		Button backButton = (Button) findViewById(R.id.diary_back);
		backButton.setOnClickListener(this);
		comment.setOnClickListener(this);
		transmit.setOnClickListener(this);
		laud.setOnClickListener(this);
	}

	public void getDiary(final int diary_id) {
		JSONObject json = LocalAccessUtil
				.getDiary(DiaryActivity.this, diary_id);
		try {
			current_userid = json.getInt("user_id");
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		// ���µײ�����������,��������,ת������
		updateCount(json);
		View headView = getHeadView(json);
		listView.addHeaderView(headView);
		// �ڶ���headView�Ǿ�ֻ������������
		View head_view = LayoutInflater.from(DiaryActivity.this).inflate(
				R.layout.diary_comment_head_item, null);
		listView.addHeaderView(head_view);
		// footView�����ײ���
		View foot_view = LayoutInflater.from(DiaryActivity.this).inflate(
				R.layout.diary_comment_foot_item, null);
		listView.addFooterView(foot_view);
		// boolean tocomment = getIntent().getBooleanExtra("to_comment", false);
		// if (tocomment) {
		// // 0Ϊdiary��������λ�ã�1Ϊ��2��headView����λ�ã�2�ǵ�һ������λ��
		// listView.setSelection(1);
		// }
		// ��ȡ�����б�
		manager = new ConnectManager(new OnJsonReturnListener() {

			@Override
			public void onJsonReturn(JSONObject result) {
				try {
					if (result.getString("action").equals(GETCOMMENT_SUCCESS)) {
						Message message = handler.obtainMessage();
						message.obj = result;
						message.what = 1;
						handler.sendMessage(message);
					} else if (result.getString("action").equals(
							SETATTENTION_SUCCESS)) {
						int i = result.getInt("result");
						// 0�����Ѿ���ע�˴��ˣ���Ҫ����ӹ�ע��
						if (i != 0) {
							LocalAccessUtil.addConcernCount(DiaryActivity.this,
									user_id);
							ListenerManager.getInstance().onCountChanged(
									SETATTENTION_SUCCESS, -1);
						}

					} else if (result.getString("action").equals(
							SETCOLLECTION_SUCCESS)) {

					} else if (result.getString("action").equals(
							CANCELATTENTION_SUCCESS)) {
						int i = result.getInt("result");
						if (i != 0) {
							LocalAccessUtil.minusConcernCount(
									DiaryActivity.this, user_id);
							ListenerManager.getInstance().onCountChanged(
									CANCELATTENTION_SUCCESS, -1);
						}

					} else if (result.getString("action").equals(
							CANCELCOLLECTION_SUCCESS)) {
					} else if (result.getString("action").equals(
							ADDLAUD_SUCCESS)) {
						int i = result.getInt("result");
						if (i != 0) {
							handler.sendEmptyMessage(4);
						} else
							handler.sendEmptyMessage(6);
					} else if (result.getString("action").equals(
							ADDTRANSMIT_SUCCESS)) {
						handler.sendEmptyMessage(5);
					}

				} catch (JSONException e) {

					e.printStackTrace();
				}

			}
		});
		manager.getComments(diary_id);
	}

	// ����diaryҪ�ı䣬��Ҫ����ʱreplace

	private void updateCount(JSONObject json) {
		if (json.length() != 0) {
			try {
				String comment_count = json.getString("comment_count");
				String laud_count = json.getString("laud_count");
				String transmit_count = json.getString("transmit_count");
				comment.setText("���� " + comment_count);
				laud.setText("���� " + laud_count);
				transmit.setText("ת�� " + transmit_count);
			} catch (JSONException e) {
				e.printStackTrace();
			}

		}

	}

	public View getHeadView(final JSONObject json) {
		View headView = null;
		// �ȴӱ��ػ�ȡjson�����û�о�������ȡ
		if (json.length() != 0) {
			try {
				headView = LayoutInflater.from(DiaryActivity.this).inflate(
						R.layout.diary_content_component, null);

				headView.setPadding(20, 20, 10, 2);
				TextView home_time = (TextView) headView
						.findViewById(R.id.home_time);
				TextView home_name = (TextView) headView
						.findViewById(R.id.home_name);
				ImageView home_head = (ImageView) headView
						.findViewById(R.id.home_head);
				TextView home_content = (TextView) headView
						.findViewById(R.id.home_content);
				MyGridView gridView = (MyGridView) headView
						.findViewById(R.id.home_gridview);
				ImageView home_more = (ImageView) headView
						.findViewById(R.id.home_more);
				home_concerned = (TextView) headView
						.findViewById(R.id.home_concerned);
				home_collection = (TextView) headView
						.findViewById(R.id.home_collection);
				home_head.setOnClickListener(this);
				home_more.setOnClickListener(this);
				home_name.setText(json.getString("user_name"));
				home_content.setText(json.getString("diary_content"));
				home_time.setText(json.getString("diary_date"));
				home_name.setText(json.getString("user_name"));
				if (clickState != -1) {
					if (clickState == 0 || clickState == 2)
						home_concerned.setVisibility(View.VISIBLE);
					else
						home_concerned.setVisibility(View.INVISIBLE);
					if (clickState == 2 || clickState == 3)
						home_collection.setVisibility(View.VISIBLE);
					else
						home_collection.setVisibility(View.INVISIBLE);
				}
				if (json.has("user_head")) {
					String head = json.getString("user_head");
					ConnectManager.loadBitmap(mLruCache, fileCache, head,
							home_head);
				} else
					home_head.setImageDrawable(null);
				JSONArray pic = json.getJSONArray("pic");
				HomePictureGridViewAdapter homeadapter = new HomePictureGridViewAdapter(
						mLruCache, fileCache, DiaryActivity.this, pic);
				gridView.setAdapter(homeadapter);
				gridView.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1,
							int item_position, long arg3) {

						Intent i = new Intent(DiaryActivity.this,
								PhotoShowActivity.class);
						try {
							JSONArray jsonArray = json.getJSONArray("pic");
							JSONArray array = new JSONArray();
							for (int j = 0; j < jsonArray.length(); j++) {
								array.put(jsonArray.getJSONObject(j).getString(
										"photo_data"));
							}
							i.putExtra("url", array.toString());
							i.putExtra("position", item_position);

							startActivity(i);
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				});
			} catch (JSONException e) {
				e.printStackTrace();
			}

		} else {
		}
		return headView;
	}

	private void initPop(View view, final int id) {
		View popView = LayoutInflater.from(DiaryActivity.this).inflate(
				R.layout.popupwindow_comment, null);

		Button send_comment = (Button) popView.findViewById(R.id.send_comment);
		final EditText write_comment = (EditText) popView
				.findViewById(R.id.write_comment);

		// ��ʼ��һ��PopupWindow����һ�������Ǽ��ص�View���ڶ���������View�Ŀ�ȣ�������������View�ĸ߶ȣ����ĸ����Ƿ��ȡ����

		final PopupWindow popupWindow = new PopupWindow(popView,
				ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT, true);
		// ���ÿ��Ե���հ״���PopupWindow��ʧ
		popupWindow.setOutsideTouchable(true);
		popupWindow.setTouchable(true);
		popupWindow.setTouchInterceptor(new OnTouchListener() {

			@Override
			public boolean onTouch(View view, MotionEvent event) {
				System.out.println("touch");
				// �����������true�Ļ���touch�¼���������
				return false;

			}
		});
		// ���ñ�����ɫ������������հ״���Ч
		popupWindow.setBackgroundDrawable(new ColorDrawable(0x00ff0000));
		// ����PopupWindow��ʾ��λ�ã���һ����������Ե�View���ڶ�����X�ᣬ������������Y��
		// showAsDropDown(View anchor)�����ĳ���ؼ���λ�ã������·�������ƫ��
		// showAsDropDown(View anchor, int xoff, int yoff)�����ĳ���ؼ���λ�ã���ƫ��
		// showAtLocation(View parent, int gravity, int x, int y)��
		// ����ڸ��ؼ���λ�ã�����������Gravity.CENTER���·�Gravity.BOTTOM�ȣ�����������ƫ�ƻ���ƫ��
		// PS:parent�������ֻҪ��activity�е�view�Ϳ����ˣ�
		popupWindow.showAsDropDown(view, 20, -900);
		// popupWindow
		// .setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
		// // �����Ϊfalse�����еĿؼ��޷���ȡ����
		// popupWindow.setFocusable(true);
		// popupWindow.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);

		// �������뷨
		final InputMethodManager imm = (InputMethodManager) getApplicationContext()
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);

		send_comment.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				String content = write_comment.getText().toString();
				if (checkString(content)) {
					if (id == 1) {
						ConnectManager manager = new ConnectManager();
						manager.uploadComment(user_id, diary_id, content);
						manager.setOnJsonReturnListener(new OnJsonReturnListener() {

							@Override
							public void onJsonReturn(JSONObject result) {
								try {
									if (result.getString("action").equals(
											SAVECOMMENT_SUCCESS)) {
										handler.sendEmptyMessage(2);

									} else {
										handler.sendEmptyMessage(3);
									}
								} catch (JSONException e) {
									e.printStackTrace();
								}

							}
						});
						imm.hideSoftInputFromWindow(
								write_comment.getWindowToken(), 0);
						popupWindow.dismiss();
					} else {
						manager.addTransmit(user_id, diary_id, content);
						imm.hideSoftInputFromWindow(
								write_comment.getWindowToken(), 0);
						popupWindow.dismiss();
					}
				}
			}
		});

	}

	public boolean checkString(String input) {
		boolean ifBlank = true;
		if (StringUtil.isBlank(input)) {
			Toast.makeText(this, "����Ϊ��", Toast.LENGTH_SHORT).show();
			ifBlank = false;
		} else if (input.length() > COMMENT_MAXLENGTH) {
			Toast.makeText(this,
					"����" + (input.length() - COMMENT_MAXLENGTH) + "����",
					Toast.LENGTH_SHORT).show();
			ifBlank = false;
		}
		return ifBlank;
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.home_comment:
			initPop(view, 1);
			break;
		case R.id.home_laud:
			manager.addLaud(user_id, diary_id);
			break;
		case R.id.home_transmit:
			initPop(view, 2);

			break;
		case R.id.diary_back:
			onBackPressed();
			break;
		case R.id.home_head:
			Intent i = new Intent(DiaryActivity.this, UserActivity.class);
			i.putExtra("other_id", current_userid);
			startActivity(i);
			break;
		case R.id.home_more:
			if (dialog != null) {
				Button concern_dialog_concern = (Button) dialog
						.findViewById(R.id.concern_dialog_concern);
				Button concern_dialog_collection = (Button) dialog
						.findViewById(R.id.concern_dialog_collection);
				switch (clickState) {
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

				View concern_dialog_view = LayoutInflater.from(
						DiaryActivity.this).inflate(R.layout.concern_dialog,
						null);
				dialog = new MyDialog(DiaryActivity.this, concern_dialog_view,
						R.style.MyDialog);
				dialog.show();
				final Button concern_dialog_concern = (Button) concern_dialog_view
						.findViewById(R.id.concern_dialog_concern);
				final Button concern_dialog_collection = (Button) concern_dialog_view
						.findViewById(R.id.concern_dialog_collection);
				switch (clickState) {
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
								if (concern_dialog_concern.getText().equals(
										"��ע")) {
									concern_dialog_concern.setText("ȡ����ע");
									clickState -= 1;
									home_concerned.setVisibility(View.VISIBLE);
									manager.setAttention(user_id,
											current_userid);

								} else {
									concern_dialog_concern.setText("��ע");
									home_concerned
											.setVisibility(View.INVISIBLE);
									clickState += 1;
									manager.cancelAttention(user_id,
											current_userid);
								}
								if (flag == 0) {
									ListenerManager
											.getInstance()
											.onDiaryClickConcern(
													R.id.concern_dialog_concern,
													clickState, position);
								} else if (flag == 1) {
									ListenerManager
											.getInstance()
											.onDiaryClickHot(
													R.id.concern_dialog_concern,
													clickState, position);
								} else if (flag == 2) {
									ListenerManager
											.getInstance()
											.onDiaryClickCollection(
													R.id.concern_dialog_concern,
													clickState, position);
								}
							}
						});

				concern_dialog_collection
						.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View arg0) {
								dialog.dismiss();
								if (concern_dialog_collection.getText().equals(
										"�ղ�")) {
									concern_dialog_collection.setText("ȡ���ղ�");
									clickState += 2;
									home_collection.setVisibility(View.VISIBLE);
									manager.setCollection(user_id, diary_id);
								} else {
									concern_dialog_collection.setText("�ղ�");
									clickState -= 2;
									home_collection
											.setVisibility(View.INVISIBLE);
									manager.cancelCollection(user_id, diary_id);

								}
								// �ص��ı�ԭ���Ľ���
								if (flag == 0) {
									ListenerManager
											.getInstance()
											.onDiaryClickConcern(
													R.id.concern_dialog_collection,
													clickState, position);
								} else if (flag == 1) {
									ListenerManager
											.getInstance()
											.onDiaryClickHot(
													R.id.concern_dialog_collection,
													clickState, position);
								} else if (flag == 2) {
									ListenerManager
											.getInstance()
											.onDiaryClickCollection(
													R.id.concern_dialog_collection,
													clickState, position);
								}

							}
						});
			}
			break;

		}

	}

}
