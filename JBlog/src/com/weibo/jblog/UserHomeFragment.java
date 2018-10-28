package com.weibo.jblog;

import static com.weibo.utils.ConstantUtil.*;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

import com.weibo.adapter.CircleImageView;
import com.weibo.adapter.User_HomeFragmentAdapter;
import com.weibo.connect.ConnectToServer;
import com.weibo.connect.LoginTask;
import com.weibo.connect.LoginTask.CallbackListener;
import com.weibo.connect.ConnectManager;
import com.weibo.listener.ListenerManager;
import com.weibo.listener.ListenerManager.onCountChangedListener;
import com.weibo.utils.LocalAccessUtil;
import com.weibo.utils.MemoryLruCache;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class UserHomeFragment extends Fragment implements onCountChangedListener{
	private boolean isFirstLoaded = true;
	private boolean isVisible = false;
	// 因为预加载时必须要判断oncreateview有没有被调用，否则不能getdata
	private boolean isCreated = false;
	Handler handler;
	ProgressDialog pd;
Button user_fans;
Button user_concern;
Button user_diary;
	
	
	public static final int REQUEST_ADDHEAD = 0;
	int[] itemsid = { R.string.myhome, R.string.myfile, R.string.mypicture };
	int[] imageid = { R.drawable.home, R.drawable.file, R.drawable.pic };
	int[] nextid = { R.drawable.next };

	CallbackListener listener = new CallbackListener() {

		@Override
		public void callback(JSONObject result) {
			try {
				pd.dismiss();
				if (result != null) {
					if (result.getString("action").equals(LOGIN_SUCCESS)) {

						Message message = handler.obtainMessage();
						message.obj = result;
						message.what = 1;
						handler.sendMessage(message);
					} else
						Toast.makeText(getActivity(), "登录失败",
								Toast.LENGTH_SHORT).show();
				} else
					Toast.makeText(getActivity(), "无法连接服务器", Toast.LENGTH_SHORT)
							.show();
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	};

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);
		if (isVisibleToUser) {
			isVisible = true;
			if (isFirstLoaded && isCreated)
				{getData();
				ListenerManager.getInstance().setOnCountChangedListener(this);
				}
		} else
			isVisible = false;
	}

	public void getData() {
		if (user_id != 0) {// 使用userid获取简易信息
			try {
				JSONObject data = LocalAccessUtil.getLastAccount(getActivity());
				if (data.length() > 0) {
					pd = ProgressDialog.show(getActivity(), "获取信息", "等待中。。。");
					pd.setCancelable(true);
					LoginTask task = new LoginTask(listener);
					task.execute(data.getString("user_phone"),
							data.getString("user_password"));
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			isFirstLoaded = false;
		}
	}

	// 不使用构造器传递listener
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view;
		view = inflater.inflate(R.layout.user_home_fragment, container, false);
		initHomeView(view);
		if (isVisible) {
			getData();
			ListenerManager.getInstance().setOnCountChangedListener(this);
		}
		return view;
	}

	public void initHomeView(View view) {
		handler = new MyHandler(view);
		user_fans = (Button) view.findViewById(R.id.user_fans);
		user_concern = (Button) view.findViewById(R.id.user_concern);
		user_diary = (Button) view.findViewById(R.id.user_diary);
		LinearLayout linear=(LinearLayout)view.findViewById(R.id.userhead_layout);
		linear.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				System.out.println("点击布局");
				
			}
		});
		user_fans.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent i = new Intent(getActivity(),
						FansActivity.class);
				i.putExtra("user_id", user_id);
				startActivity(i);
			}
		});
		user_concern.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent i = new Intent(getActivity(),
						ConcernActivity.class);
				i.putExtra("user_id", user_id);
				startActivity(i);
			}
		});
		//跳转到我的微博
		user_diary.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent i = new Intent(getActivity(),
						DiaryListActivity.class);
				i.putExtra("user_id", user_id);
				startActivity(i);
			}
		});

		ListView homelist = (ListView) view.findViewById(R.id.user_home_list);
		User_HomeFragmentAdapter adapter = new User_HomeFragmentAdapter(
				getActivity(), itemsid, imageid, nextid);
		homelist.setAdapter(adapter);
		homelist.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				switch (position) {
				case 0:
					Intent i0 = new Intent(getActivity(), UserActivity.class);
					i0.putExtra("user_id", user_id);
					startActivity(i0);
					break;
				case 1:
					Intent i1 = new Intent(getActivity(),
							CollectionActivity.class);
					startActivity(i1);
					break;
				case 2:
					// 先不经过AlbumListActivity，等以后需要将相册分类再说
					 Intent i2 = new Intent(getActivity(),
					 AlbumActivity.class);
					 startActivity(i2);

//					Intent i2 = new Intent(getActivity(),
//							AlbumListActivity.class);
//					i2.putExtra("user_id", user_id);
//					startActivity(i2);

					break;
				case 3:
					System.out.println(3);
					break;
				}
			}
		});

	}

	class MyHandler extends Handler {
		View view;

		MyHandler(View view) {
			this.view = view;
		}

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			CircleImageView user_head = (CircleImageView) view
					.findViewById(R.id.user_home_head);
			switch (msg.what) {
			case 1:
				JSONObject s = (JSONObject) msg.obj;
				TextView user_name = (TextView) view
						.findViewById(R.id.userfragment_name);
				TextView user_desc = (TextView) view
						.findViewById(R.id.userfragment_desc);
				user_head.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						// 嵌套fragment的onactivityresult的调用问题。
						// Intent intent = new
						// Intent(Intent.ACTION_GET_CONTENT);
						// intent.setType("image/*");
						// startActivityForResult(intent, REQUEST_ADDHEAD);
					}
				});
				try {

					if (s.has("head_data")) {
						ConnectManager.loadBitmap(null, null,
								s.getString("head_data"), user_head);
					}
					user_name.setText(s.getString("user_name"));
					if (s.has("user_desc")) {
						user_desc.setText(s.getString("user_desc"));
					} else
						user_desc.setText("空空如也");
					user_fans.setText("粉丝 "+s.getString("user_fans"));
					user_concern.setText("关注 "+s.getString("user_concern"));
					user_diary.setText("微博 "+s.getString("user_diary"));
				} catch (JSONException e) {
					e.printStackTrace();
				}
				break;
			case 2:
				Bitmap bp = (Bitmap) msg.obj;
				user_head.setImageBitmap(bp);
				break;
			case 3:user_concern.setText("关注 "+(Integer.parseInt(user_concern.getText().toString().substring(3))+1));
			break;
			case 4:user_concern.setText("关注 "+(Integer.parseInt(user_concern.getText().toString().substring(3))-1));
			break;
			case 5:
				user_fans.setText("粉丝 "+msg.arg1);
				break;
			case 6:
				user_diary.setText("微博 "+(Integer.parseInt(user_diary.getText().toString().substring(3))+1));
			    break;
			}
			
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Log.i("myinfo", "进入");
		if (requestCode == REQUEST_ADDHEAD && resultCode == Activity.RESULT_OK) {
			if (data != null) {
				Uri picuri = data.getData();
				// 最好异步加载

				ContentResolver cr = getActivity().getContentResolver();
				Bitmap bitmap = null;
				try {
					bitmap = BitmapFactory.decodeStream(cr
							.openInputStream(picuri));
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
				Message msg = new Message();
				msg.obj = bitmap;
				msg.what = 2;
				handler.sendMessage(msg);
				Log.i("myinfo", "拖出");
			}
		} else
			System.out.println("获取图片失败");

	}

	@Override
	public void onCountChanged(String action,int count) {
		if(action.equals(SETATTENTION_SUCCESS)){
			handler.sendEmptyMessage(3);
		}else if(action.equals(CANCELATTENTION_SUCCESS)){
			handler.sendEmptyMessage(4);
		}else if(action.equals(GETFANS_SUCCESS)){
			Message msg=new Message();
			msg.what=5;
			msg.arg1=count;
			handler.sendMessage(msg);
		}else if(action.equals(PUBLISH_SUCCESS)){
			Message msg=new Message();
			msg.what=6;
			msg.arg1=count;
			handler.sendMessage(msg);
		}
		
	}

}
