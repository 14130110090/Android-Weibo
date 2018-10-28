package com.weibo.jblog;

import static com.weibo.utils.ConstantUtil.REGISTER_SUCCESS;

import org.json.JSONException;
import org.json.JSONObject;

import com.weibo.connect.ConnectManager;
import com.weibo.connect.ConnectManager.OnJsonReturnListener;

import com.weibo.utils.ConstantUtil;
import com.weibo.utils.LocalAccessUtil;
import com.weibo.utils.StringUtil;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class UserInfoActivity extends Activity implements OnJsonReturnListener {
	ProgressDialog pd;
	ConnectManager manager;
	MyHandler handler;
	EditText et_name;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.userinfo_activity);
		et_name = (EditText) findViewById(R.id.userinfo_name);
		Button change = (Button) findViewById(R.id.userinfo_change);
		handler = new MyHandler();
		manager = new ConnectManager();
		manager.setOnJsonReturnListener(this);
		change.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (StringUtil.isBlank(et_name.getText().toString())) {
					Toast.makeText(UserInfoActivity.this, "êÇ³ÆÎª¿Õ",
							Toast.LENGTH_SHORT).show();
				} else
					manager.alterUserInfo(ConstantUtil.user_id, et_name
							.getText().toString(), null, null, null);
			}
		});

	}

	@Override
	protected void onDestroy() {

		super.onDestroy();
	}

	@Override
	public void onJsonReturn(JSONObject result) {

		if (result != null) {
			Intent i = new Intent(UserInfoActivity.this,
					MainFragmentActivity.class);
			startActivity(i);

		}
	}

	class MyHandler extends Handler {

		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 0:

				break;
			}

		}
	}
}
