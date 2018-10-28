package com.weibo.jblog;

import org.json.JSONException;
import org.json.JSONObject;

import com.weibo.connect.ConnectManager;

import com.weibo.connect.ConnectManager.OnJsonReturnListener;
import com.weibo.utils.ConstantUtil;
import com.weibo.utils.LocalAccessUtil;
import com.weibo.utils.StringUtil;

import static com.weibo.utils.ConstantUtil.*;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class RegisterActivity extends Activity implements OnJsonReturnListener {
	ProgressDialog pd;
	ConnectManager manager;
    MyHandler handler;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.register_activity);
		handler=new MyHandler();
		Button register = (Button) findViewById(R.id.register_register);
		register.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// pd=ProgressDialog.show(RegisterActivity.this, "等待",
				// "正在连接...",false);
				EditText account_et = (EditText) findViewById(R.id.register_account);
				EditText password_et = (EditText) findViewById(R.id.register_password);

				String account = account_et.getText().toString().trim();
				String password = password_et.getText().toString();
				if (StringUtil.isBlank(account) || StringUtil.isBlank(password)) {
					// 可以用对话框代替

					Toast.makeText(RegisterActivity.this, "请输入帐号或密码",
							Toast.LENGTH_SHORT).show();

				} else {
					manager.register(account, password);
				}
			}
		});
		manager = new ConnectManager();
		manager.setOnJsonReturnListener(this);
	}

	@Override
	protected void onDestroy() {

		super.onDestroy();
	}

	@Override
	public void onJsonReturn(JSONObject result) {
		try {
			if (result != null) {
				if (result.getString("action").equals(REGISTER_SUCCESS)) {
					if(result.length()==1){
						handler.sendEmptyMessage(0);
						return;
					}
					ConstantUtil.user_id = result.getInt("user_id");
					// 在登录之前就要判断本地有没有信息，也就是checkinfo
					// 登录成功后将帐号信息存入本地数据库
					
					LocalAccessUtil.saveInfo(RegisterActivity.this, result);
					// 登录成功跳转到主页
					Intent intent = new Intent(RegisterActivity.this,
							UserInfoActivity.class);
					startActivity(intent);
					finish();
				} 
//				else
//					Toast.makeText(RegisterActivity.this, "登录失败",
//							Toast.LENGTH_SHORT).show();
			} else {
				// Toast.makeText(LoginActivity.this, "无法连接服务器",
				// Toast.LENGTH_SHORT).show();
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	class MyHandler extends Handler {

		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 0:
				Toast.makeText(RegisterActivity.this, "帐号已存在", Toast.LENGTH_SHORT).show();
				break;
			}

		}
	}

}
