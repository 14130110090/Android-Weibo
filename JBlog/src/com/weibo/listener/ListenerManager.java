package com.weibo.listener;

import android.view.View;

public class ListenerManager {
	private onDiaryClickHotListener hotListener;
	private onDiaryClickUserListener userListener;
	private onDiaryClickConcernListener concernListener;
	private onDiaryClickCollectionListener collectionListener;
	private onCountChangedListener countListener;

	// 不允许创建该对象，只能通过getInstance方法获取该实例
	private ListenerManager() {
	}

	private static final ListenerManager manager = new ListenerManager();

	public static ListenerManager getInstance() {
		return manager;
	}

	public void setOnDiaryClickListener(onDiaryClickConcernListener mListener) {
		concernListener = mListener;
	}

	public void setOnDiaryClickListener(onDiaryClickHotListener mListener) {
		hotListener = mListener;
	}

	public void setOnDiaryClickListener(onDiaryClickCollectionListener mListener) {
		collectionListener = mListener;
	}
	public void setOnDiaryClickListener(onDiaryClickUserListener mListener) {
		userListener = mListener;
	}

	public void setOnCountChangedListener(onCountChangedListener mListener) {
		countListener = mListener;
	}

	public void onDiaryClickHot(int viewId, int flag, int position) {
		if (hotListener != null) {
			hotListener.onDiaryClick(viewId, flag, position);
		}
	}

	public void onDiaryClickConcern(int viewId, int flag, int position) {
		if (concernListener != null) {
			concernListener.onDiaryClick(viewId, flag, position);
		}
	}

	public void onDiaryClickCollection(int viewId, int flag, int position) {
		if (collectionListener != null) {
			collectionListener.onDiaryClick(viewId, flag, position);
		}
	}

	public void onCountChanged(String action,int count) {
		if (countListener != null) {
			countListener.onCountChanged(action,count);
		}
	}
	public void onDiaryClickUser(int viewId, int flag, int position) {
		if (userListener != null) {
			userListener.onDiaryClick(viewId, flag, position);
		}
	}
	
	// 必须要加入position，否则点击gridview的空白处就无法更新current_position
	public interface onDiaryClickConcernListener {
		public void onDiaryClick(int viewId, int flag, int position);
	}

	public interface onDiaryClickCollectionListener {
		public void onDiaryClick(int viewId, int flag, int position);
	}

	public interface onDiaryClickHotListener {
		public void onDiaryClick(int viewId, int flag, int position);
	}
	public interface onDiaryClickUserListener {
		public void onDiaryClick(int viewId, int flag, int position);
	}

	public interface onCountChangedListener {
		public void onCountChanged(String action,int count);
	}
}
