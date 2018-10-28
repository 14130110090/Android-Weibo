package com.weibo.jblog;

import static com.weibo.utils.ConstantUtil.*;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.weibo.adapter.AsyncDrawable;
import com.weibo.adapter.BitmapLoadTask;
import com.weibo.adapter.HomeFragmentAdapter;
import com.weibo.adapter.MyViewPager;
import com.weibo.adapter.ViewPagerAdapter;
import com.weibo.connect.LoadHomeTask;
import com.weibo.connect.LoadHomeTask.LoadHomeListener;
import com.weibo.connect.ConnectManager.GetPhotoListListener;
import com.weibo.connect.ConnectManager;
import com.weibo.utils.LocalAccessUtil;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

public class HomeFragment extends Fragment implements OnCheckedChangeListener {
	MyViewPager viewPager;
	ArrayList<Fragment> fragmentList = new ArrayList<Fragment>();
	ConnectManager manager;
	HomeConcernFragment concernFragment;
	HomeHotFragment hotFragment;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.home_fragment, container, false);
		initView(view);
		return view;
	}

	public void initView(View view) {
		viewPager = (MyViewPager) view
				.findViewById(R.id.homefragment_viewpager);
		Button home_search = (Button) view.findViewById(R.id.home_search);
		Button home_publish = (Button) view.findViewById(R.id.home_publish);
		home_search.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent i = new Intent(getActivity(), SearchActivity.class);
				startActivity(i);
			}
		});
		home_publish.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent i = new Intent(getActivity(), PublishActivity.class);
				startActivity(i);
			}
		});
		viewPager.setScrollable(false);
		concernFragment = new HomeConcernFragment();
		hotFragment = new HomeHotFragment();
		fragmentList.add(concernFragment);
		fragmentList.add(hotFragment);
		// 默认为1，即缓存前后一个Fragment
		// viewPager.setOffscreenPageLimit(3);
		viewPager.setAdapter(new ViewPagerAdapter(getChildFragmentManager(),
				fragmentList));
		RadioGroup radio_group = (RadioGroup) view
				.findViewById(R.id.homefragment_radiogroup);
		radio_group.setOnCheckedChangeListener(this);

		JSONObject json = LocalAccessUtil.getLastAccount(getActivity());
		int concern = 0;
		if (json.length() != 0) {
			try {
				concern = json.getInt("user_concern");
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		if (concern != 0) {
			radio_group.check(R.id.home_concern);
		} else
			radio_group.check(R.id.home_hot);
	}

	@Override
	public void onCheckedChanged(RadioGroup arg0, int arg1) {
		switch (arg1) {
		case R.id.home_concern:
			viewPager.setCurrentItem(0);
			break;
		case R.id.home_hot:
			viewPager.setCurrentItem(1);
			break;
		}

	}

}
