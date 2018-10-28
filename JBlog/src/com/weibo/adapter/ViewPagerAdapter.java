package com.weibo.adapter;

import java.util.ArrayList;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

public class ViewPagerAdapter extends FragmentPagerAdapter{
    ArrayList<Fragment> list=null;
	public ViewPagerAdapter(FragmentManager fm,ArrayList<Fragment> list) {
		super(fm);
		this.list=list;
	}

	@Override
	public Fragment getItem(int index) {
		return list.get(index);
	}

	@Override
	public int getCount() {
		if(list!=null)return list.size();
		return 0;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		//这样就不会删除fragment
//		super.destroyItem(container, position, object);
	}

}
