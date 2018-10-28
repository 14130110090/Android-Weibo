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
					// �����öԻ������

					Toast.makeText(LoginActivity.this, "�������ʺŻ�����",
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
							Toast.makeText(LoginActivity.this, "��¼ʧ��",
									Toast.LENGTH_SHORT).show();
							return;
						}
						ConstantUtil.user_id=result.getInt("user_id");
						//�ڵ�¼֮ǰ��Ҫ�жϱ�����û����Ϣ��Ҳ����checkinfo
						// ��¼�ɹ����ʺ���Ϣ���뱾�����ݿ�
						LocalAccessUtil.saveInfo(LoginActivity.this, result);
						// ��¼�ɹ���ת����ҳ
						Intent intent = new Intent(LoginActivity.this,
								MainFragmentActivity.class);
						startActivity(intent);
						finish();
					} else
						Toast.makeText(LoginActivity.this, "��¼ʧ��",
								Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(LoginActivity.this, "�޷����ӷ�����",
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
		// ��ȡ��¼��ע��Ͱ�����ť�����ü�����
		// ��ʱע�����ȿ�����һ������ע�ỹ�Ǻ�����Ҫʱע��
		// checkInfo();
		Button login_login = (Button) findViewById(R.id.login_login);
		Button login_register = (Button) findViewById(R.id.login_register);
		Button login_help = (Button) findViewById(R.id.login_help);
		login_login.setOnClickListener(listener);
		login_register.setOnClickListener(listener);
		login_help.setOnClickListener(listener);

	}

	// ѡ��˵�
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
