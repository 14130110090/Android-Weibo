package com.weibo.jblog;

import com.weibo.utils.ConstantUtil;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

public class DiscoverFragment extends Fragment implements OnClickListener {
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

		View view = inflater.inflate(R.layout.discover_fragment, container,
				false);
		initView(view);
		isCreated = true;
		if (isVisible) {
			getData();
		}
		return view;
	}

	public void getData() {
		isFirstLoaded = false;
	}

	public void initView(View view) {
		// 如果mainactivity被销毁就会无法获取到该intent
		
		Button b1 = (Button) view.findViewById(R.id.b1);
		Button b2 = (Button) view.findViewById(R.id.b2);
		Button b5 = (Button) view.findViewById(R.id.b5);
		Button b6 = (Button) view.findViewById(R.id.b6);
		b1.setOnClickListener(this);
		b2.setOnClickListener(this);
		b5.setOnClickListener(this);
		b6.setOnClickListener(this);
		
	}

	
	@Override
	public void onClick(View v) {
		if(ConstantUtil.user_id==0){
			Intent i=new Intent(getActivity(),LoginActivity.class);
			getActivity().startActivity(i);
		}
		switch (v.getId()) {
		
		case R.id.b1:
			Intent i1 = new Intent(getActivity(), PublishActivity.class);
			i1.putExtra("user_id", ConstantUtil.user_id);
			startActivity(i1);
			break;
		case R.id.b2:
			Intent i2 = new Intent(getActivity(), ShootActivity.class);
			startActivity(i2);
			break;

		case R.id.b5:
			Intent i3 = new Intent(getActivity(), SearchActivity.class);
			startActivity(i3);
			break;
		case R.id.b6:
			Intent i6 = new Intent(getActivity(), VisitorActivity.class);
			i6.putExtra("user_id", ConstantUtil.user_id);
			startActivity(i6);
			break;
		}

	}
}
