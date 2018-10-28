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
				// 刷新评论
				manager.getComments(diary_id);
				comment.setText("评论 "
						+ (Integer.parseInt(comment.getText().toString()
								.substring(3)) + 1));
				Toast.makeText(DiaryActivity.this, "发送成功", Toast.LENGTH_SHORT)
						.show();
				// 回调以使主页的评论数更改
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
				Toast.makeText(DiaryActivity.this, "发送失败", Toast.LENGTH_SHORT)
						.show();

				break;
			case 4:
				laud.setText("点赞 "
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
				transmit.setText("转发 "
						+ (Integer.parseInt(transmit.getText().toString()
								.substring(3)) + 1));
				Toast.makeText(DiaryActivity.this, "转发成功", Toast.LENGTH_SHORT)
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
				Toast.makeText(DiaryActivity.this, "已赞", Toast.LENGTH_SHORT)
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
		// 更新底部的评论数量,点赞数量,转发数量
		updateCount(json);
		View headView = getHeadView(json);
		listView.addHeaderView(headView);
		// 第二个headView是就只有评论两个字
		View head_view = LayoutInflater.from(DiaryActivity.this).inflate(
				R.layout.diary_comment_head_item, null);
		listView.addHeaderView(head_view);
		// footView代表到底部了
		View foot_view = LayoutInflater.from(DiaryActivity.this).inflate(
				R.layout.diary_comment_foot_item, null);
		listView.addFooterView(foot_view);
		// boolean tocomment = getIntent().getBooleanExtra("to_comment", false);
		// if (tocomment) {
		// // 0为diary内容所在位置，1为第2个headView所在位置，2是第一个评论位置
		// listView.setSelection(1);
		// }
		// 获取评论列表
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
						// 0代表已经关注了此人，不要再添加关注数
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

	// 由于diary要改变，需要插入时replace

	private void updateCount(JSONObject json) {
		if (json.length() != 0) {
			try {
				String comment_count = json.getString("comment_count");
				String laud_count = json.getString("laud_count");
				String transmit_count = json.getString("transmit_count");
				comment.setText("评论 " + comment_count);
				laud.setText("点赞 " + laud_count);
				transmit.setText("转发 " + transmit_count);
			} catch (JSONException e) {
				e.printStackTrace();
			}

		}

	}

	public View getHeadView(final JSONObject json) {
		View headView = null;
		// 先从本地获取json，如果没有就联网获取
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

		// 初始化一个PopupWindow，第一个参数是加载的View，第二个参数是View的宽度，第三个参数是View的高度，第四个是是否获取焦点

		final PopupWindow popupWindow = new PopupWindow(popView,
				ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT, true);
		// 设置可以点击空白处让PopupWindow消失
		popupWindow.setOutsideTouchable(true);
		popupWindow.setTouchable(true);
		popupWindow.setTouchInterceptor(new OnTouchListener() {

			@Override
			public boolean onTouch(View view, MotionEvent event) {
				System.out.println("touch");
				// 这里如果返回true的话，touch事件将被拦截
				return false;

			}
		});
		// 设置背景颜色，否则点击外面空白处无效
		popupWindow.setBackgroundDrawable(new ColorDrawable(0x00ff0000));
		// 设置PopupWindow显示的位置，第一个参数是相对的View，第二个是X轴，第三个参数是Y轴
		// showAsDropDown(View anchor)：相对某个控件的位置（正左下方），无偏移
		// showAsDropDown(View anchor, int xoff, int yoff)：相对某个控件的位置，有偏移
		// showAtLocation(View parent, int gravity, int x, int y)：
		// 相对于父控件的位置（例如正中央Gravity.CENTER，下方Gravity.BOTTOM等），可以设置偏移或无偏移
		// PS:parent这个参数只要是activity中的view就可以了！
		popupWindow.showAsDropDown(view, 20, -900);
		// popupWindow
		// .setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
		// // 如果设为false，其中的控件无法获取焦点
		// popupWindow.setFocusable(true);
		// popupWindow.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);

		// 弹出输入法
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
			Toast.makeText(this, "内容为空", Toast.LENGTH_SHORT).show();
			ifBlank = false;
		} else if (input.length() > COMMENT_MAXLENGTH) {
			Toast.makeText(this,
					"超出" + (input.length() - COMMENT_MAXLENGTH) + "个字",
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
					concern_dialog_concern.setText("取消关注");
					concern_dialog_collection.setText("收藏");
					break;
				case 1:
					concern_dialog_concern.setText("关注");
					concern_dialog_collection.setText("收藏");
					break;
				case 2:
					concern_dialog_concern.setText("取消关注");
					concern_dialog_collection.setText("取消收藏");
					break;
				case 3:
					concern_dialog_concern.setText("关注");
					concern_dialog_collection.setText("取消收藏");
					break;
				}
				// dismiss不会删除dialog，还在后台运行
				dialog.show();
			} else {
				// 第一次创建，是初始状态，后来的状态变化不会影响这里

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
					concern_dialog_concern.setText("取消关注");
					concern_dialog_collection.setText("收藏");
					break;
				case 1:
					concern_dialog_concern.setText("关注");
					concern_dialog_collection.setText("收藏");
					break;
				case 2:
					concern_dialog_concern.setText("取消关注");
					concern_dialog_collection.setText("取消收藏");
					break;
				case 3:
					concern_dialog_concern.setText("关注");
					concern_dialog_collection.setText("取消收藏");
					break;
				}
				concern_dialog_concern
						.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								dialog.dismiss();
								if (concern_dialog_concern.getText().equals(
										"关注")) {
									concern_dialog_concern.setText("取消关注");
									clickState -= 1;
									home_concerned.setVisibility(View.VISIBLE);
									manager.setAttention(user_id,
											current_userid);

								} else {
									concern_dialog_concern.setText("关注");
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
										"收藏")) {
									concern_dialog_collection.setText("取消收藏");
									clickState += 2;
									home_collection.setVisibility(View.VISIBLE);
									manager.setCollection(user_id, diary_id);
								} else {
									concern_dialog_collection.setText("收藏");
									clickState -= 2;
									home_collection
											.setVisibility(View.INVISIBLE);
									manager.cancelCollection(user_id, diary_id);

								}
								// 回调改变原来的界面
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
