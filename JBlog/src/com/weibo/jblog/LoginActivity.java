package com.weibo.jblog;

import static com.weibo.utils.ConstantUtil.LOGIN_SUCCESS;

import java.lang.ref.WeakReference;

import org.json.JSONException;
import org.json.JSONObject;

import com.weibo.connect.LoginTask;
import com.weibo.connect.LoginTask.CallbackListener;
import com.weibo.utils.ConstantUtil;
import com.weibo.utils.LocalAccessUtil;
import com.weibo.utils.StringUtil;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;

import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends Activity {
	ProgressDialog pd;
	OnClickListener listener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if (v.getId() == R.id.login_login) {
				EditText login_account = (EditText) findViewById(R.id.login_account);
				EditText login_password = (EditText) findViewById(R.id.login_password);
				String account = login_account.getText().toString().trim();
				String password = login_password.getText().toString();
				if (StringUtil.isBlank(account) || StringUtil.isBlank(password)) {
					// 可以用对话框代替

					Toast.makeText(LoginActivity.this, "请输入帐号或密码",
							Toast.LENGTH_SHORT).show();

				}

				else {
					LoginTask loginTask = new LoginTask(callbackListener);
					loginTask.execute(account, password);
				}

			} else if (v.getId() == R.id.login_register) {
				Intent intent = new Intent(LoginActivity.this,
						RegisterActivity.class);
				startActivity(intent);
			} else {

			}
		}
	};;

	CallbackListener callbackListener = new CallbackListener() {

		@Override
		public void callback(JSONObject result) {
			try {
				if (result != null) {
					if (result.getString("action").equals(LOGIN_SUCCESS)) {
						if(result.length()==1){
							Toast.makeText(LoginActivity.this, "登录失败",
									Toast.LENGTH_SHORT).show();
							return;
						}
						ConstantUtil.user_id=result.getInt("user_id");
						//在登录之前就要判断本地有没有信息，也就是checkinfo
						// 登录成功后将帐号信息存入本地数据库
						LocalAccessUtil.saveInfo(LoginActivity.this, result);
						// 登录成功跳转到主页
						Intent intent = new Intent(LoginActivity.this,
								MainFragmentActivity.class);
						startActivity(intent);
						finish();
					} else
						Toast.makeText(LoginActivity.this, "登录失败",
								Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(LoginActivity.this, "无法连接服务器",
							Toast.LENGTH_SHORT).show();
				}

			} catch (JSONException e) {
				e.printStackTrace();
			}

		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.login_activity);
		// 获取登录、注册和帮助按钮并设置监听器
		// 暂时注销，先考虑是一上来就注册还是后来需要时注册
		// checkInfo();
		Button login_login = (Button) findViewById(R.id.login_login);
		Button login_register = (Button) findViewById(R.id.login_register);
		Button login_help = (Button) findViewById(R.id.login_help);
		login_login.setOnClickListener(listener);
		login_register.setOnClickListener(listener);
		login_help.setOnClickListener(listener);

	}

	// 选项菜单
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
