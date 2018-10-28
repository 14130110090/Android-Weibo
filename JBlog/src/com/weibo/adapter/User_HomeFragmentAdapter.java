package com.weibo.adapter;

import com.weibo.adapter.PhotoAdapter.HolderView;
import com.weibo.connect.ConnectManager;
import com.weibo.jblog.R;

import android.R.raw;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class User_HomeFragmentAdapter extends BaseAdapter {
	int[] itemsid;
	int[] imageid;
	int[] nextid;
	Context context;

	public User_HomeFragmentAdapter(Context context, int[] itemsid,
			int[] imageid, int[] nextid) {
		this.itemsid = itemsid;
		this.imageid = imageid;
		this.nextid = nextid;
		this.context = context;
	}

	@Override
	public int getCount() {
		if (itemsid == null)
			return 0;
		return itemsid.length;
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
					R.layout.user_homefragment_item, null);
			holderView.image = (ImageView) currentView
					.findViewById(R.id.user_homefragment_image);
			holderView.next = (ImageView) currentView
					.findViewById(R.id.user_homefragment_next);
			holderView.text = (TextView) currentView
					.findViewById(R.id.user_homefragment_text);
			currentView.setTag(holderView);
		} else {
			holderView = (HolderView) currentView.getTag();
		}

		if (itemsid != null && itemsid.length != 0) {

			holderView.text.setText(itemsid[position]);
		}
		if (imageid != null && imageid.length != 0) {
			// holderView.image.setImageResource(imageid[position]);
			// 因为此时控件还没有measure，不知道大小holderView.image.getWidth()会返回0
			holderView.image.setImageBitmap(ConnectManager
					.readBitmapFromResource(context.getResources(),
							imageid[position], 60, 60));
		}
		if (nextid != null && nextid.length != 0) {
			// 一个是向后箭头，一个是红点代表有消息
			holderView.next.setImageResource(nextid[0]);
		}
		return currentView;
	}

	public class HolderView {
		private ImageView image;
		private TextView text;
		private ImageView next;
	}
}
