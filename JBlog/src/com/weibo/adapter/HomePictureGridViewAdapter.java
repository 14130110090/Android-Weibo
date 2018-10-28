package com.weibo.adapter;

import static com.weibo.utils.ConstantUtil.*;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.weibo.connect.ConnectManager;
import com.weibo.jblog.R;
import com.weibo.utils.FileLruCache;
import com.weibo.utils.MemoryLruCache;

public class HomePictureGridViewAdapter extends BaseAdapter {
	private Context context;
	boolean isFirstItemLoaded = false;
	JSONArray picture;
	MemoryLruCache mLruCache;
	FileLruCache fileCache;
	public HomePictureGridViewAdapter(MemoryLruCache mLruCache,FileLruCache fileCache,Context context, JSONArray picture) {
		this.context = context;
		this.picture = picture;
		this.mLruCache=mLruCache;
		this.fileCache=fileCache;
	}

	@Override
	public int getCount() {
		if (picture == null)
			return 0;
		return picture.length();
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
			currentView = LayoutInflater.from(context).inflate(
					R.layout.mygridview_item, null);
			holderView.image = (ImageView) currentView
					.findViewById(R.id.gridview_image);
			currentView.setTag(holderView);
		} else {
			holderView = (HolderView) currentView.getTag();
		}
		try {
			if (picture != null && picture.length() != 0) {
				if (isFirstItemLoaded == false || position != 0) {
					isFirstItemLoaded = true;
					String path = picture.getJSONObject(position).getString(
							"photo_data");
					
					ConnectManager.loadBitmap(mLruCache,fileCache,path, holderView.image);
				} else {
					// 为了避免多余的position为0的item的加载，这是由于
					// gridview不知道它里面到底能放多少item,因此多次加载position 0来适配
					return currentView;
				}

			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return currentView;
	}

	public class HolderView {
		private ImageView image;
	}

}
