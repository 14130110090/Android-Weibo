package com.weibo.jblog;

import static com.weibo.utils.ConstantUtil.*;

import java.io.IOException;


import org.json.JSONException;
import org.json.JSONObject;

import com.weibo.adapter.CircleImageView;
import com.weibo.adapter.User_HomeFragmentAdapter;
import com.weibo.connect.ConnectManager;
import com.weibo.connect.ConnectManager.OnJsonReturnListener;
import com.weibo.connect.ConnectToServer;
import com.weibo.jblog.UserHomeFragment.MyHandler;


import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import android.support.v4.content.Loader;
import android.support.v4.view.PagerTitleStrip;

import android.support.v4.widget.DrawerLayout;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import android.view.Menu;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class UserActivity extends ActionBarActivity implements OnJsonReturnListener {
	
	ProgressDialog pd = null;
	Button user_fans;
	Button user_concern;
	Button user_diary;
	ConnectManager manager;
	//Ҫ���ʵ������û�
	int other_id;
	public static final int REQUEST_ADDHEAD = 0;
	int[] itemsid = { R.string.myhome, R.string.myfile, R.string.mypicture };
	int[] imageid = { R.drawable.home, R.drawable.file, R.drawable.pic };
	int[] nextid = { R.drawable.next };
	// handler��й¶����
	Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {

			CircleImageView user_head = (CircleImageView) 
					findViewById(R.id.user_activity_head);
			switch (msg.what) {
			case 1:
				JSONObject s = (JSONObject) msg.obj;
				TextView user_name = (TextView) 
						findViewById(R.id.user_activity_name);
				TextView user_desc = (TextView)
						findViewById(R.id.user_activity_desc);
				user_head.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						// Ƕ��fragment��onactivityresult�ĵ������⡣
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
						user_desc.setText("�տ���Ҳ");
					user_fans.setText("��˿ "+s.getString("user_fans"));
					user_concern.setText("��ע "+s.getString("user_concern"));
					user_diary.setText("΢�� "+s.getString("user_diary"));
				} catch (JSONException e) {
					e.printStackTrace();
				}
				break;
			case 2:
				Bitmap bp = (Bitmap) msg.obj;
				user_head.setImageBitmap(bp);
				break;
			case 3:user_concern.setText("��ע "+(Integer.parseInt(user_concern.getText().toString().substring(3))+1));
			break;
			case 4:user_concern.setText("��ע "+(Integer.parseInt(user_concern.getText().toString().substring(3))-1));
			break;
			case 5:
				user_fans.setText("��˿ "+msg.arg1);
				break;
			}
			super.handleMessage(msg);
		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.user_activity);
		initView();
		DrawerLayout f;
		PagerTitleStrip d;
		Loader<String> ds;
		PopupMenu fs;
		SearchView fds;

	}

	public void initView() {
		
		user_fans = (Button)findViewById(R.id.user_fans);
		user_concern = (Button)findViewById(R.id.user_concern);
		user_diary = (Button) findViewById(R.id.user_diary);
		LinearLayout linear=(LinearLayout)findViewById(R.id.user_activity_layout);
		linear.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				System.out.println("�������");
				
			}
		});
		user_fans.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent i = new Intent(UserActivity.this,
						FansActivity.class);
				i.putExtra("other_id", other_id);
				startActivity(i);
			}
		});
		user_concern.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent i = new Intent(UserActivity.this,
						ConcernActivity.class);
				i.putExtra("other_id", other_id);
				startActivity(i);
			}
		});
		//��ת���ҵ�΢��
		user_diary.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent i = new Intent(UserActivity.this,
						DiaryListActivity.class);
				i.putExtra("other_id", other_id);
				startActivity(i);
			}
		});

		ListView homelist = (ListView) findViewById(R.id.user_activity_list);
		User_HomeFragmentAdapter adapter = new User_HomeFragmentAdapter(
				UserActivity.this, itemsid, imageid, nextid);
		homelist.setAdapter(adapter);
		homelist.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				switch (position) {
				case 0:
					Intent i0 = new Intent(UserActivity.this, UserActivity.class);
					i0.putExtra("other_id", other_id);
					startActivity(i0);
					break;
				case 1:
					Intent i1 = new Intent(UserActivity.this,
							CollectionActivity.class);
					i1.putExtra("other_id", other_id);
					startActivity(i1);
					break;
				case 2:
					// �Ȳ�����AlbumListActivity�����Ժ���Ҫ����������˵
					 Intent i2 = new Intent(UserActivity.this,
					 AlbumActivity.class);
					 i2.putExtra("other_id", other_id);
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
		
		manager=new ConnectManager();
		manager.setOnJsonReturnListener(this);
		Intent intent=getIntent();
		other_id=intent.getIntExtra("other_id", 0);
		if(other_id!=0){
			manager.getUserInfo(other_id, user_id);
		}
		// pd = ProgressDialog.show(this, "��ȡ�û���Ϣ", "��ȴ�...");
		//
		// if (ConstantUtil.user_id != 0)
		// getUserInfo(ConstantUtil.user_id);
		// else {
		// Toast.makeText(this, "���¼", Toast.LENGTH_SHORT).show();
		// }

		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//		toolbar.setTitle("�û���ҳ");
		toolbar.setNavigationIcon(R.drawable.directional_left);
		/**
		 * ������Toolbar��inflateMenu()��������menu����ʱ�������ٵ���setSupportActionBar(),
		 * ������ص���onCreateOptionsMenu()�еĲ���,���ʱ����ص�onOptionsItemSelected()������
		 * ��Ҫ����Toolbar��setOnMenuItemClickListener()���������õ���ļ�����
		 * ���ݶ�Ӧ��id�����жϺʹ�����onOptionsItemSelected()�д���ͬ���ǣ���ӦnavigationIcon�ĵ���¼���
		 * ����Toolbar��setNavigationOnClickListener()�����н��д���
		 **/

//		toolbar.inflateMenu(R.menu.user_menu);
		
		toolbar.setNavigationOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
//		setSupportActionBar(toolbar);
//		// ���÷��ص�ͼ�꣬Ĭ��Ϊ��ͷ���������ļ�������meta-data���ݵͰ汾�����÷�����
//		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		
		
		
		
		
		
		
		
		
	}

	@Override
	public void onJsonReturn(JSONObject result) {
		try {
			
			if (result != null) {
				if (result.getString("action").equals(USERINFO_SUCCESS)) {

					Message message = handler.obtainMessage();
					message.obj = result;
					message.what = 1;
					handler.sendMessage(message);
				} else
					Toast.makeText(UserActivity.this, "��ȡ��Ϣʧ��",
							Toast.LENGTH_SHORT).show();
			} else
				Toast.makeText(UserActivity.this, "�޷����ӷ�����", Toast.LENGTH_SHORT)
						.show();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
	}

//	@Override
//	public boolean onOptionsItemSelected(MenuItem item) {
//		// android.R.id.home�ǵ�����ť��id
//		if (item.getItemId() == android.R.id.home) {
//			finish();
//			return true;
//		}
//
//		return super.onOptionsItemSelected(item);
//	}
//	@Override
//    
//    public boolean onCreateOptionsMenu(Menu menu) {
//    	return super.onCreateOptionsMenu(menu);
//    }
	// ʹOverflow����ʾͼ�꣬����Ĭ�ϲ���ʾ
	// @Override
	// protected boolean onPrepareOptionsPanel(View view, Menu menu) {
	// if (menu != null) {
	//
	// try {
	// Method m = menu.getClass().getDeclaredMethod(
	// "setOptionalIconsVisible", Boolean.TYPE);
	// m.setAccessible(true);
	// m.invoke(menu, true);
	// } catch (Exception e) {
	// }
	//
	// }
	// return super.onPrepareOptionsPanel(view, menu);
	// }


}
