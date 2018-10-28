package com.weibo.guide;

import com.weibo.jblog.MainFragmentActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;

public class GuideActivity extends Activity{
    
	private final int SPLASH_TIME=3000;
	Handler handler=new Handler();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		handler.postDelayed(new Runnable(){

			@Override
			public void run() {
				Intent  i=new Intent(GuideActivity.this,MainFragmentActivity.class);
				startActivity(i);
				finish();
			}
			
		}, SPLASH_TIME);
	}
   
}
