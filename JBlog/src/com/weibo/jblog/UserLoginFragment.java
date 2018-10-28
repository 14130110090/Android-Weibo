package com.weibo.jblog;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class UserLoginFragment extends Fragment{
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.login_fragment, container, false);
		initView(view);
		return view;
	}
	public void initView(View view){
		System.out.println("login≥ı ºªØ");
	}
}
