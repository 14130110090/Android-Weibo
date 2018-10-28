package com.weibo.jblog;

import org.json.JSONArray;

import com.weibo.utils.LocalAccessUtil;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;

public class AccountActivity extends Activity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.account_activity);
		initView();
		
		
		
		
	}

	public void initView(){
		Button getall=(Button)findViewById(R.id.account_getallaccount);
		getall.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				JSONArray array=LocalAccessUtil.getAllAccount(AccountActivity.this);
			    System.out.println(array);
			}
		});
		
		
		
	}
	
}
