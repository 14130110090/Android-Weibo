package com.weibo.jblog;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class MessageFragment extends Fragment {
	private boolean isFirstLoaded = true;
	private boolean isVisible = false;
	private boolean isCreated = false;

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);
		if (isVisibleToUser) {
			isVisible = true;
			// 只有oncreateview调用之后并且是第一次调用才联网
			if (isCreated && isFirstLoaded) {
				getData();	
			}

		} else
			isVisible = false;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.message_fragment, container,
				false);
		initView(view);
		isCreated = true;
		if (isVisible) {
			getData();
		}
		return view;
	}

	public void initView(View view) {

	}

	public void getData() {
		Log.i("myinfo","Message联网获取数据");
		isFirstLoaded = false;
	}

}
