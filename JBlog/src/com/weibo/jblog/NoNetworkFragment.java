package com.weibo.jblog;

import com.weibo.utils.NetworkUtil;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class NoNetworkFragment extends Fragment {
	FragmentManager fragmentManager;
	UserFragment user_Fragment = null;

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view;
		view = inflater.inflate(R.layout.nonetwork_fragment, container, false);
		initNoNetworkView(view);
		return view;

	}

	public void initNoNetworkView(View view) {
		Button refresh = (Button) view.findViewById(R.id.refresh);
		refresh.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				
				if(NetworkUtil.getNetworkState(getActivity())!=NetworkUtil.NONE){
					user_Fragment = (UserFragment) getParentFragment();
					user_Fragment.viewPager.setCurrentItem(0);
				}else {
					Toast.makeText(getActivity(), "请连接网络并重试", Toast.LENGTH_SHORT).show();
				}
				
			}
		});
	}

}
