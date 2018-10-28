package com.weibo.adapter;

import static com.weibo.utils.ConstantUtil.ROOTDIR;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.weibo.adapter.HomePictureGridViewAdapter.HolderView;
import com.weibo.connect.ConnectManager;
import com.weibo.jblog.R;
import com.weibo.utils.FileLruCache;
import com.weibo.utils.MemoryLruCache;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CommentAdapter extends BaseAdapter {

	JSONArray comments;
	Context context;
	MemoryLruCache mLruCache;
	FileLruCache fileCache;

	public CommentAdapter(Context context, JSONArray comments,
			MemoryLruCache mLruCache, FileLruCache fileCache) {
		this.context = context;
		this.comments = comments;
		this.mLruCache = mLruCache;
		this.fileCache = fileCache;
	}

	@Override
	public int getCount() {
		if (comments == null)
			return 0;
		return comments.length();
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
					R.layout.comment_item, null);
			holderView.user_head = (ImageView) currentView
					.findViewById(R.id.comment_head);
			holderView.user_name = (TextView) currentView
					.findViewById(R.id.comment_username);
			holderView.comment_content = (TextView) currentView
					.findViewById(R.id.comment_content);
			holderView.comment_date = (TextView) currentView
					.findViewById(R.id.comment_date);
			currentView.setTag(holderView);
		} else {
			holderView = (HolderView) currentView.getTag();
		}
		
		try {
			if (comments != null && comments.length() != 0) {
				JSONObject comment;
				comment = comments.getJSONObject(position);
				if (comment.has("head_data")) {
					String path = comment.getString("head_data");
					ConnectManager.loadBitmap(mLruCache, fileCache, path,
							holderView.user_head);
				} else
					holderView.user_head.setImageDrawable(null);
				holderView.comment_content.setText(comment
						.getString("comments_content"));
				holderView.user_name.setText(comment.getString("user_name"));
				holderView.comment_date.setText(comment
						.getString("comments_date"));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return currentView;
	}

	public class HolderView {
		private ImageView user_head;
		private TextView user_name;
		private TextView comment_content;
		private TextView comment_date;
	}
}
