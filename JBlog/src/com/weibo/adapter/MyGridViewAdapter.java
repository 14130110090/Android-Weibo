package com.weibo.adapter;

import java.util.ArrayList;

import com.weibo.adapter.PhotoAdapter.HolderView;
import com.weibo.connect.ConnectManager;
import com.weibo.jblog.R;
import com.weibo.utils.MemoryLruCache;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MyGridViewAdapter extends BaseAdapter {
	private Activity activity;
MemoryLruCache mLruCache;
	ArrayList<String> picture;
	boolean isFirstItemLoaded = false;
	public MyGridViewAdapter(Activity activity, MemoryLruCache mLruCache,ArrayList<String> picture) {
		this.activity = activity;
		this.picture = picture;
		this.mLruCache=mLruCache;
	}

	@Override
	public int getCount() {
		if (picture == null)
			return 0;
		return picture.size();
	}

	@Override
	public Object getItem(int arg0) {
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		return 0;
	}

	@Override
	public View getView(int position, View currentView, ViewGroup arg2) {
		HolderView holderView = null;
		if (currentView == null) {
			holderView = new HolderView();
			currentView = LayoutInflater.from(activity).inflate(
					R.layout.mygridview_item, null);
			holderView.image = (ImageView) currentView
					.findViewById(R.id.gridview_image);
			currentView.setTag(holderView);
		} else {
			holderView = (HolderView) currentView.getTag();
		}
		if (picture != null && picture.get(position) != null) {
			
			if (isFirstItemLoaded == false || position != 0) {
				isFirstItemLoaded = true;
				ConnectManager.loadBitmap(mLruCache,null,
						picture.get(position), holderView.image);
			} else {
				// 为了避免多余的position为0的item的加载，这是由于
				// gridview不知道它里面到底能放多少item,因此多次加载position 0来适配
				return currentView;
			}
		}
		return currentView;
	}

	public class HolderView {
		private ImageView image;
	}

}
