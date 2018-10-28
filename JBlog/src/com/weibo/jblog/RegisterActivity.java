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
				// pd=ProgressDialog.show(RegisterActivity.this, "�ȴ�",
				// "��������...",false);
				EditText account_et = (EditText) findViewById(R.id.register_account);
				EditText password_et = (EditText) findViewById(R.id.register_password);

				String account = account_et.getText().toString().trim();
				String password = password_et.getText().toString();
				if (StringUtil.isBlank(account) || StringUtil.isBlank(password)) {
					// �����öԻ������

					Toast.makeText(RegisterActivity.this, "�������ʺŻ�����",
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
					// �ڵ�¼֮ǰ��Ҫ�жϱ�����û����Ϣ��Ҳ����checkinfo
					// ��¼�ɹ����ʺ���Ϣ���뱾�����ݿ�
					
					LocalAccessUtil.saveInfo(RegisterActivity.this, result);
					// ��¼�ɹ���ת����ҳ
					Intent intent = new Intent(RegisterActivity.this,
							UserInfoActivity.class);
					startActivity(intent);
					finish();
				} 
//				else
//					Toast.makeText(RegisterActivity.this, "��¼ʧ��",
//							Toast.LENGTH_SHORT).show();
			} else {
				// Toast.makeText(LoginActivity.this, "�޷����ӷ�����",
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
				Toast.makeText(RegisterActivity.this, "�ʺ��Ѵ���", Toast.LENGTH_SHORT).show();
				break;
			}

		}
	}

}
