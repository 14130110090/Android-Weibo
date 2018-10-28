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
			// ֻ��oncreateview����֮�����ǵ�һ�ε��ò�����
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
		Log.i("myinfo","Message������ȡ����");
		isFirstLoaded = false;
	}

}
