package com.weibo.jblog;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import com.weibo.adapter.MyViewPager;
import com.weibo.adapter.ViewPagerAdapter;
import com.weibo.utils.ConstantUtil;
import com.weibo.utils.LocalAccessUtil;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

public class MainFragmentActivity extends FragmentActivity implements
		OnCheckedChangeListener {
	FragmentManager fragmentManager = null;
	HomeFragment homeFragment = null;
	UserFragment userFragment = null;
	DiscoverFragment discoverFragment = null;
	MessageFragment messageFragment = null;
	ArrayList<Fragment> fragmentList = new ArrayList<Fragment>();
	MyViewPager viewPager;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.main_activity);
		initView();

	}

	public void initView() {
		// 查看本地是否有记录
		JSONObject json = LocalAccessUtil.getLastAccount(this);
		if (json.length() != 0) {
			try {
				ConstantUtil.user_id = json.getInt("user_id");
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		discoverFragment = new DiscoverFragment();
		homeFragment = new HomeFragment();
		messageFragment = new MessageFragment();
		userFragment = new UserFragment();

		fragmentList.add(homeFragment);
//		fragmentList.add(messageFragment);
//		fragmentList.add(discoverFragment);
		fragmentList.add(userFragment);

		viewPager = (MyViewPager) findViewById(R.id.fragment_layout);
		viewPager.setScrollable(false);
		// 默认为1，即缓存前后一个Fragment
		//viewPager.setOffscreenPageLimit(3);
		viewPager.setAdapter(new ViewPagerAdapter(getSupportFragmentManager(),
				fragmentList));

//		RadioButton radio=(RadioButton)findViewById(R.id.radio_discover);
		Drawable drawable_news = getResources().getDrawable(R.drawable.add);
	    //当这个图片被绘制时，给他绑定一个矩形 ltrb规定这个矩形
	    drawable_news.setBounds(-2, -2, 55, 55);
	    
	    //设置图片在文字的哪个方向
//	    radio.setCompoundDrawables(null, drawable_news, null, null);
		
		RadioGroup radio_group = (RadioGroup) findViewById(R.id.radiogroup);
		radio_group.setOnCheckedChangeListener(this);
		// 默认第一个显示的
		radio_group.check(R.id.radio_home);
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		return super.onMenuItemSelected(featureId, item);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		return super.onCreateOptionsMenu(menu);
	}

	//使点击返回键不
	@Override
	public void onBackPressed() {
		 Intent intent = new Intent(Intent.ACTION_MAIN);
		 intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		 intent.addCategory(Intent.CATEGORY_HOME);
		 startActivity(intent);
//		moveTaskToBack(true);
	}

	@Override
	public void onCheckedChanged(RadioGroup arg0, int checkedid) {
		switch (checkedid) {
		case R.id.radio_home:
			viewPager.setCurrentItem(0);
			break;
//		case R.id.radio_msg:
//			viewPager.setCurrentItem(1);
//			break;
//		case R.id.radio_discover:
//			viewPager.setCurrentItem(2);
//			break;
		case R.id.radio_selfInfo:
			viewPager.setCurrentItem(1);
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int responseCode,
			Intent data) {
		super.onActivityResult(requestCode, responseCode, data);
//		if (requestCode == 0) {
//			userFragment.user_homeFragment.onActivityResult(requestCode,
//					responseCode, data);
//		}

	}
}
