package com.weibo.jblog;

import org.json.JSONArray;
import org.json.JSONException;

import com.weibo.utils.FileLruCache;
import com.weibo.utils.LocalAccessUtil;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

public class SettingActivity extends Activity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.setting_activity);
		initView();
		
	}

	public void initView(){
		Button login=(Button)findViewById(R.id.setting_login);
		login.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Intent i=new Intent(SettingActivity.this,LoginActivity.class);
				startActivity(i);
			}
		});
		
		Button manage=(Button)findViewById(R.id.setting_manage);
		manage.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Intent i=new Intent(SettingActivity.this,AccountActivity.class);
				startActivity(i);
			}
		});
		
		Button diary=(Button)findViewById(R.id.setting_diary);
		diary.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				JSONArray array=LocalAccessUtil.getAllDiary(SettingActivity.this);
				for(int i=0;i<array.length();i++){
					try {
						System.out.println(array.get(i));
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			}
		});
		
		Button storage=(Button)findViewById(R.id.setting_storage);
		storage.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				FileLruCache lrucache=new FileLruCache();
//				lrucache.deleteFile();
				System.out.println(LocalAccessUtil.getAllPhoto(SettingActivity.this, 9));;
				Toast.makeText(SettingActivity.this, ""+lrucache.getAllFileSize(), Toast.LENGTH_SHORT).show();
			}
		});
		
	}
	
	
}
