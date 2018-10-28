package com.weibo.adapter;

import static com.weibo.utils.ConstantUtil.ROOTDIR;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.weibo.connect.ConnectManager;
import com.weibo.jblog.R;
import com.weibo.utils.MemoryLruCache;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class PhotoAdapter extends BaseAdapter {
	private Context context;
	private JSONArray data;
	MemoryLruCache mLruCache=new MemoryLruCache();
	
	public PhotoAdapter(Context context, JSONArray data
			) {
		this.context = context;
		this.data = data;
		
	}

	@Override
	public int getCount() {
		if (data == null)
			return 0;
		return data.length();
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
					R.layout.photo_item, null);
			holderView.photo_show = (ImageView) currentView
					.findViewById(R.id.photo_show);
			holderView.photo_id = (TextView) currentView
					.findViewById(R.id.photo_id);
			holderView.photo_data = (TextView) currentView
					.findViewById(R.id.photo_data);
			holderView.photo_name = (TextView) currentView
					.findViewById(R.id.photo_name);
			currentView.setTag(holderView);
		} else {
			holderView = (HolderView) currentView.getTag();
		}

		if (data != null) {
			try {
//				holderView.photo_id.setText(data.getJSONObject(position)
//						.getString("photo_id"));
//				holderView.photo_data.setText(data.getJSONObject(position)
//						.getString("photo_data"));
				String path = data.getJSONObject(position)
						.getString("photo_data");
				
				ConnectManager.loadBitmap(mLruCache,null,path, holderView.photo_show);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return currentView;
	}

	public class HolderView {
		private ImageView photo_show;
		private TextView photo_id;
		private TextView photo_name;
		private TextView photo_data;
	}

}
