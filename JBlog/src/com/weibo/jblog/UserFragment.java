package com.weibo.jblog;

import static com.weibo.utils.ConstantUtil.*;

import java.io.IOException;
import java.util.ArrayList;

import com.weibo.adapter.MyViewPager;
import com.weibo.adapter.ViewPagerAdapter;
import com.weibo.connect.ConnectToServer;
import com.weibo.utils.LocalAccessUtil;
import com.weibo.utils.NetworkUtil;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 嵌套使用Fragment，调用getChildFragmentManager获取FragmentManager
 * 
 * @author JiHongHua
 * 
 */
public class UserFragment extends Fragment{
	private boolean isFirstLoaded = true;
	private boolean isVisible = false;
	// 因为预加载时必须要判断oncreateview有没有被调用，否则不能getdata
	private boolean isCreated = false;
	UserHomeFragment user_homeFragment = null;
	UserLoginFragment user_loginFragment = null;
	NoNetworkFragment nonetwork_Fragment = null;
	FragmentManager fragmentManager = null;
	ArrayList<Fragment> fragmentList = new ArrayList<Fragment>();
	MyViewPager viewPager;
	String account;
	String password;

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);
		if (isVisibleToUser) {
			isVisible = true;
			if (isFirstLoaded && isCreated)
				getData();
		} else
			isVisible = false;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.user_fragment, container, false);
		initView(view);
		isCreated = true;
		if (isVisible) {
			getData();
		}
		return view;
	}

	public void getData() {
		if (user_id!=0) {
			if (NetworkUtil.getNetworkState(getActivity()) != NetworkUtil.NONE) {
				//联网获取消息
				viewPager.setCurrentItem(0);
			} else {
				//直接获取本地的用户信息
				viewPager.setCurrentItem(1);
			}
		} else {
			//显示登录界面
			viewPager.setCurrentItem(2);
		}
		isFirstLoaded = false;

	}

	public void initView(View view) {
		viewPager = (MyViewPager) view.findViewById(R.id.user_childfragment);
		fragmentList.add(new UserHomeFragment());
		fragmentList.add(new NoNetworkFragment());
		fragmentList.add(new UserLoginFragment());
		viewPager.setScrollable(false);
		// 如果不设置初始显示，默认item为0号。
		viewPager.setAdapter(new ViewPagerAdapter(getChildFragmentManager(),
				fragmentList));
		Button setting = (Button) view.findViewById(R.id.user_setting);
		setting.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent i=new Intent(getActivity(),SettingActivity.class);
				startActivity(i);
			}
		});
	}
}
