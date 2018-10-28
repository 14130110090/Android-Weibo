package com.weibo.jblog;

import java.io.DataOutputStream;
import java.io.IOException;

import org.json.JSONObject;

import com.weibo.connect.ConnectToServer;
import com.weibo.connect.ConnectManager;

import static com.weibo.utils.ConstantUtil.*;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Base64;
import android.view.Window;

public class UploadActivity extends Activity {
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.upload_activity);
		initView();
	}

	public void initView() {
		Intent i = getIntent();
		byte[] data = i.getByteArrayExtra("picture_data");
		//new ConnectManager().uploadHead(3, data, "ff", 2);
	}
}
