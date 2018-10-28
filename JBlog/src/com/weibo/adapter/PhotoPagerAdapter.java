package com.weibo.adapter;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;

import com.weibo.connect.ConnectManager;
import com.weibo.jblog.R;
import com.weibo.utils.FileLruCache;
import com.weibo.utils.MemoryLruCache;
import com.weibo.view.PhotoView;

import android.app.Activity;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class PhotoPagerAdapter extends PagerAdapter {
	JSONArray list;
	Activity activity;
	MemoryLruCache mLruCache;
	FileLruCache fLruCache;

	public PhotoPagerAdapter(Activity activity, MemoryLruCache mLruCache,
			FileLruCache fLruCache, JSONArray list) {
		this.list = list;
		this.activity = activity;
		this.mLruCache = mLruCache;
		this.fLruCache = fLruCache;
	}

	@Override
	public int getCount() {
		if (list == null)
			return 0;
		return list.length();
	}

	// 判断是否显示同一张图片
	@Override
	public boolean isViewFromObject(View view, Object object) {
		return view == object;
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		View view=LayoutInflater.from(activity).inflate(R.layout.photo_show_page,null);
		PhotoView photoView = (PhotoView)view.findViewById(R.id.photoshow);
		TextView tv_page = (TextView)view.findViewById(R.id.tv_page);
		tv_page.setText((position+1)+" / "+list.length());
		photoView.enable();
		photoView.setScaleType(ImageView.ScaleType.FIT_CENTER);
		try {
			String path = list.getString(position);
			ConnectManager.loadBitmap(mLruCache, fLruCache, path, photoView);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		photoView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				activity.finish();
			}
		});
		container.addView(view);
		return view;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		container.removeView((View) object);
	}
}
